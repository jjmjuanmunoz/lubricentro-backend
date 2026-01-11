package com.lubricentro.backend.service;

import com.lubricentro.backend.dto.invoice.*;
import com.lubricentro.backend.entity.*;
import com.lubricentro.backend.repository.*;
import com.lubricentro.backend.service.afip.AfipInvoiceGateway;
import com.lubricentro.backend.service.afip.AfipInvoiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de facturacion.
 *
 * Maneja la creacion, edicion y emision de facturas.
 * Delega la emision AFIP al AfipInvoiceGateway (mock o real).
 */
@Service
public class InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository itemRepository;
    private final AfipPosRepository posRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final AfipInvoiceGateway afipGateway;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          InvoiceItemRepository itemRepository,
                          AfipPosRepository posRepository,
                          UserRepository userRepository,
                          CustomerRepository customerRepository,
                          ProductRepository productRepository,
                          AfipInvoiceGateway afipGateway) {
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
        this.posRepository = posRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.afipGateway = afipGateway;
    }

    // ================== CREAR FACTURA ==================

    /**
     * Crea una nueva factura en estado BORRADOR.
     */
    @Transactional
    public InvoiceDetailResponse createInvoice(CreateInvoiceRequest request) {
        log.info("Creando factura - POS: {}, Tipo: {}", request.posId(), request.tipoComprobante());

        // Validar punto de venta
        AfipPos pos = posRepository.findById(request.posId())
                .orElseThrow(() -> new IllegalArgumentException("Punto de venta no encontrado: " + request.posId()));

        if (pos.getActive() == null || !pos.getActive()) {
            throw new IllegalArgumentException("El punto de venta no esta activo");
        }

        // Validar cliente (user o customer)
        User user = null;
        Customer customer = null;

        if (request.userId() != null) {
            user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + request.userId()));
            // Validar que el usuario este activo
            if (user.getIsActive() != null && !user.getIsActive()) {
                throw new IllegalArgumentException("El cliente (usuario) no esta activo");
            }
        } else if (request.customerId() != null) {
            customer = customerRepository.findById(request.customerId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + request.customerId()));
            if (customer.getIsActive() != null && !customer.getIsActive()) {
                throw new IllegalArgumentException("El cliente no esta activo");
            }
        } else {
            throw new IllegalArgumentException("Debe especificar un cliente (userId o customerId)");
        }

        // Crear factura
        Invoice invoice = Invoice.builder()
                .pos(pos)
                .cbteTypeCode(request.tipoComprobante().getCodigoAfip())
                .user(user)
                .customer(customer)
                .conceptCode(request.conceptCode())
                .currencyCode(request.currencyCode())
                .currencyRate(BigDecimal.ONE)
                .netAmount(BigDecimal.ZERO)
                .ivaAmount(BigDecimal.ZERO)
                .exemptAmount(BigDecimal.ZERO)
                .tributesAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .status(EstadoFactura.BORRADOR)
                .build();

        invoice = invoiceRepository.save(invoice);
        log.info("Factura creada en borrador - ID: {}", invoice.getId());

        return mapToDetailResponse(invoice);
    }

    // ================== AGREGAR ITEM ==================

    /**
     * Agrega un item a una factura en borrador.
     */
    @Transactional
    public InvoiceDetailResponse addItem(Long invoiceId, AddInvoiceItemRequest request) {
        Invoice invoice = getEditableInvoice(invoiceId);

        // Si tiene productId, obtener datos del producto
        String descripcion = request.descripcion();
        BigDecimal precioUnitario = request.precioUnitario();

        if (request.productId() != null) {
            Product product = productRepository.findById(request.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + request.productId()));
            if (descripcion == null || descripcion.isBlank()) {
                descripcion = product.getName();
            }
            if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) == 0) {
                precioUnitario = product.getUnitPrice();
            }
        }

        // Calcular importes
        BigDecimal importeNeto = request.cantidad().multiply(precioUnitario).setScale(2, RoundingMode.HALF_UP);
        BigDecimal importeIva = calcularIva(importeNeto, request.codigoIva());

        // Crear item
        InvoiceItem item = InvoiceItem.builder()
                .invoice(invoice)
                .productId(request.productId())
                .description(descripcion)
                .quantity(request.cantidad())
                .unitPrice(precioUnitario)
                .ivaAliquotCode(request.codigoIva())
                .netAmount(importeNeto)
                .ivaAmount(importeIva)
                .build();

        invoice.addItem(item);
        recalcularTotales(invoice);

        invoice = invoiceRepository.save(invoice);
        log.info("Item agregado a factura {} - Producto: {}, Cantidad: {}", invoiceId, descripcion, request.cantidad());

        return mapToDetailResponse(invoice);
    }

    // ================== ELIMINAR ITEM ==================

    /**
     * Elimina un item de una factura en borrador.
     */
    @Transactional
    public InvoiceDetailResponse removeItem(Long invoiceId, Long itemId) {
        Invoice invoice = getEditableInvoice(invoiceId);

        InvoiceItem itemToRemove = invoice.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado: " + itemId));

        invoice.removeItem(itemToRemove);
        recalcularTotales(invoice);

        invoice = invoiceRepository.save(invoice);
        log.info("Item {} eliminado de factura {}", itemId, invoiceId);

        return mapToDetailResponse(invoice);
    }

    // ================== EMITIR FACTURA ==================

    /**
     * Emite una factura (mock AFIP o real).
     */
    @Transactional
    public EmitirInvoiceResponse emitirFactura(Long invoiceId) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada: " + invoiceId));

        // Validaciones
        if (!invoice.canBeIssued()) {
            if (invoice.getStatus() != EstadoFactura.BORRADOR) {
                throw new IllegalStateException("La factura no esta en estado BORRADOR");
            }
            if (invoice.getItems().isEmpty()) {
                throw new IllegalStateException("La factura debe tener al menos un item");
            }
        }

        if (invoice.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("El total de la factura debe ser mayor a cero");
        }

        log.info("Emitiendo factura {} - Total: {}", invoiceId, invoice.getTotalAmount());

        // Llamar al gateway AFIP (mock o real)
        AfipInvoiceResult result = afipGateway.emitirFactura(invoice);

        if (result.isApproved()) {
            // Actualizar factura con datos de AFIP
            invoice.setCbteNumber(result.numeroComprobante());
            invoice.setCae(result.cae());
            invoice.setCaeExpiration(result.caeVencimiento());
            invoice.setIssuedOn(OffsetDateTime.now());
            invoice.setStatus(EstadoFactura.EMITIDA);

            invoiceRepository.save(invoice);
            log.info("Factura {} emitida - CAE: {}, Numero: {}", invoiceId, result.cae(), result.numeroComprobante());

            return EmitirInvoiceResponse.success(invoiceId, result.numeroComprobante(), result.cae(), result.caeVencimiento());
        } else {
            invoice.setStatus(EstadoFactura.RECHAZADA);
            invoiceRepository.save(invoice);
            log.warn("Factura {} rechazada: {}", invoiceId, result.errores());

            return EmitirInvoiceResponse.error(invoiceId, result.errores());
        }
    }

    // ================== CONSULTAS ==================

    /**
     * Obtiene una factura por ID con todos sus detalles.
     */
    @Transactional(readOnly = true)
    public InvoiceDetailResponse getById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada: " + invoiceId));
        return mapToDetailResponse(invoice);
    }

    /**
     * Lista facturas con filtros y paginacion.
     */
    @Transactional(readOnly = true)
    public Page<InvoiceSummaryResponse> list(Long userId, Long customerId, EstadoFactura estado, Long posId, Pageable pageable) {
        Page<Invoice> page = invoiceRepository.findWithFilters(userId, customerId, estado, posId, pageable);
        return page.map(this::mapToSummaryResponse);
    }

    /**
     * Lista facturas de un usuario.
     */
    @Transactional(readOnly = true)
    public List<InvoiceSummaryResponse> listByUser(Long userId) {
        return invoiceRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToSummaryResponse)
                .collect(Collectors.toList());
    }

    // ================== HELPERS ==================

    private Invoice getEditableInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findByIdWithDetails(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada: " + invoiceId));

        if (!invoice.isEditable()) {
            throw new IllegalStateException("La factura no puede ser modificada en estado: " + invoice.getStatus());
        }
        return invoice;
    }

    private void recalcularTotales(Invoice invoice) {
        BigDecimal netAmount = BigDecimal.ZERO;
        BigDecimal ivaAmount = BigDecimal.ZERO;

        for (InvoiceItem item : invoice.getItems()) {
            netAmount = netAmount.add(item.getNetAmount());
            ivaAmount = ivaAmount.add(item.getIvaAmount());
        }

        BigDecimal tributesAmount = BigDecimal.ZERO;
        for (InvoiceTribute tribute : invoice.getTributes()) {
            tributesAmount = tributesAmount.add(tribute.getAmount());
        }

        invoice.setNetAmount(netAmount.setScale(2, RoundingMode.HALF_UP));
        invoice.setIvaAmount(ivaAmount.setScale(2, RoundingMode.HALF_UP));
        invoice.setTributesAmount(tributesAmount.setScale(2, RoundingMode.HALF_UP));
        invoice.setTotalAmount(netAmount.add(ivaAmount).add(invoice.getExemptAmount()).add(tributesAmount)
                .setScale(2, RoundingMode.HALF_UP));
    }

    private BigDecimal calcularIva(BigDecimal baseImponible, Integer codigoIva) {
        BigDecimal alicuota = getAlicuotaIva(codigoIva);
        return baseImponible.multiply(alicuota).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal getAlicuotaIva(Integer codigo) {
        if (codigo == null) return BigDecimal.ZERO;
        return switch (codigo) {
            case 3 -> BigDecimal.ZERO;              // 0%
            case 4 -> new BigDecimal("10.50");      // 10.5%
            case 5 -> new BigDecimal("21.00");      // 21%
            case 6 -> new BigDecimal("27.00");      // 27%
            case 8 -> new BigDecimal("5.00");       // 5%
            case 9 -> new BigDecimal("2.50");       // 2.5%
            default -> new BigDecimal("21.00");     // Default 21%
        };
    }

    private String getDescripcionIva(Integer codigo) {
        if (codigo == null) return "21%";
        return switch (codigo) {
            case 3 -> "0%";
            case 4 -> "10.5%";
            case 5 -> "21%";
            case 6 -> "27%";
            case 8 -> "5%";
            case 9 -> "2.5%";
            default -> "21%";
        };
    }

    // ================== MAPPERS ==================

    private InvoiceDetailResponse mapToDetailResponse(Invoice invoice) {
        TipoComprobante tipo = TipoComprobante.fromCodigoAfip(invoice.getCbteTypeCode());

        String condicionIva = null;
        if (invoice.getUser() != null && invoice.getUser().getCondicionIva() != null) {
            condicionIva = invoice.getUser().getCondicionIva().getDescripcion();
        }

        List<InvoiceItemResponse> items = invoice.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        List<InvoiceTributeResponse> tributos = invoice.getTributes().stream()
                .map(this::mapTributeToResponse)
                .collect(Collectors.toList());

        return new InvoiceDetailResponse(
                invoice.getId(),
                invoice.getPos().getId(),
                invoice.getPos().getPosNumber(),
                invoice.getPos().getDescription(),
                tipo,
                invoice.getCbteTypeCode(),
                invoice.getCbteNumber(),
                invoice.getUser() != null ? invoice.getUser().getId() : null,
                invoice.getCustomer() != null ? invoice.getCustomer().getId() : null,
                invoice.getNombreCliente(),
                invoice.getDocumentoCliente(),
                condicionIva,
                invoice.getCurrencyCode(),
                invoice.getCurrencyRate(),
                invoice.getNetAmount(),
                invoice.getIvaAmount(),
                invoice.getExemptAmount(),
                invoice.getTributesAmount(),
                invoice.getTotalAmount(),
                invoice.getCae(),
                invoice.getCaeExpiration(),
                invoice.getIssuedOn() != null ? invoice.getIssuedOn().toLocalDateTime() : null,
                invoice.getStatus(),
                invoice.isEditable(),
                items,
                tributos,
                invoice.getCreatedAt(),
                invoice.getUpdatedAt()
        );
    }

    private InvoiceSummaryResponse mapToSummaryResponse(Invoice invoice) {
        TipoComprobante tipo = TipoComprobante.fromCodigoAfip(invoice.getCbteTypeCode());

        return new InvoiceSummaryResponse(
                invoice.getId(),
                invoice.getPos().getPosNumber(),
                tipo,
                invoice.getCbteNumber(),
                invoice.getNombreCliente(),
                invoice.getTotalAmount(),
                invoice.getStatus(),
                invoice.getCae(),
                invoice.getCaeExpiration(),
                invoice.getCreatedAt()
        );
    }

    private InvoiceItemResponse mapItemToResponse(InvoiceItem item) {
        BigDecimal importeTotal = item.getNetAmount().add(item.getIvaAmount());
        return new InvoiceItemResponse(
                item.getId(),
                item.getProductId(),
                item.getDescription(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getIvaAliquotCode(),
                getDescripcionIva(item.getIvaAliquotCode()),
                item.getNetAmount(),
                item.getIvaAmount(),
                importeTotal
        );
    }

    private InvoiceTributeResponse mapTributeToResponse(InvoiceTribute tribute) {
        return new InvoiceTributeResponse(
                tribute.getId(),
                tribute.getCode(),
                tribute.getDescription(),
                tribute.getBaseAmount(),
                tribute.getRate(),
                tribute.getAmount()
        );
    }
}
