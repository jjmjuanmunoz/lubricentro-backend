package com.lubricentro.backend.service;

import com.lubricentro.backend.dto.invoice.InvoiceCreateDto;
import com.lubricentro.backend.dto.invoice.InvoiceItemDto;
import com.lubricentro.backend.dto.invoice.InvoiceResponseDto;
import com.lubricentro.backend.dto.invoice.InvoiceTributeDto;
import com.lubricentro.backend.entity.AfipPos;
import com.lubricentro.backend.entity.Customer;
import com.lubricentro.backend.entity.Invoice;
import com.lubricentro.backend.entity.InvoiceItem;
import com.lubricentro.backend.entity.InvoiceTribute;
import com.lubricentro.backend.repository.AfipPosRepository;
import com.lubricentro.backend.repository.CustomerRepository;
import com.lubricentro.backend.repository.InvoiceRepository;
import com.lubricentro.backend.service.BillingService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class BillingServiceImpl implements BillingService {

    private final AfipPosRepository posRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;

    public BillingServiceImpl(AfipPosRepository posRepository,
                              CustomerRepository customerRepository,
                              InvoiceRepository invoiceRepository) {
        this.posRepository = posRepository;
        this.customerRepository = customerRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional
    public InvoiceResponseDto createAndAuthorize(InvoiceCreateDto dto) {
        AfipPos pos = posRepository.findById(dto.posId())
                .orElseThrow(() -> new IllegalArgumentException("POS not found: " + dto.posId()));

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + dto.customerId()));

        String currency = StringUtils.hasText(dto.currencyCode()) ? dto.currencyCode() : "ARS";
        BigDecimal currencyRate = BigDecimal.ONE;

        Calculation calc = calculateTotals(dto.items(), dto.tributes());

        // Asignamos número local (en real: FECompUltimoAutorizado + 1)
        Integer nextNumber = nextLocalNumber(pos.getId(), dto.cbteTypeCode());

        Invoice invoice = Invoice.builder()
                .pos(pos)
                .cbteTypeCode(dto.cbteTypeCode())
                .cbteNumber(nextNumber)
                .customer(customer)
                .conceptCode(dto.conceptCode())
                .currencyCode(currency)
                .currencyRate(currencyRate)
                .netAmount(calc.net)
                .ivaAmount(calc.iva)
                .exemptAmount(calc.exempt)
                .tributesAmount(calc.tributes)
                .totalAmount(calc.total)
                .status("APPROVED") // mock: aprobamos
                .issuedOn(OffsetDateTime.now())
                .cae(generateMockCae())
                .caeExpiration(LocalDate.now().plusDays(10))
                .build();

        // Items
        for (InvoiceItemDto it : dto.items()) {
            InvoiceItem item = InvoiceItem.builder()
                    .invoice(invoice)
                    .productId(it.productId())
                    .description(it.description())
                    .quantity(it.quantity())
                    .unitPrice(it.unitPrice())
                    .ivaAliquotCode(it.ivaCode())
                    .netAmount(it.quantity().multiply(it.unitPrice()).setScale(2, RoundingMode.HALF_UP))
                    .ivaAmount(calcIva(it.quantity(), it.unitPrice(), ivaRate(it.ivaCode())))
                    .build();
            invoice.getItems().add(item);
        }

        // Tributos
        if (dto.tributes() != null) {
            for (InvoiceTributeDto t : dto.tributes()) {
                InvoiceTribute tr = InvoiceTribute.builder()
                        .invoice(invoice)
                        .code(t.code())
                        .description(t.description())
                        .baseAmount(t.baseAmount())
                        .rate(t.rate())
                        .amount(t.amount())
                        .build();
                invoice.getTributes().add(tr);
            }
        }

        invoice = invoiceRepository.save(invoice);

        return new InvoiceResponseDto(
                invoice.getId(),
                invoice.getCbteNumber(),
                invoice.getCae(),
                invoice.getCaeExpiration(),
                invoice.getStatus(),
                invoice.getNetAmount(),
                invoice.getIvaAmount(),
                invoice.getExemptAmount(),
                invoice.getTributesAmount(),
                invoice.getTotalAmount()
        );
    }

    @Override
    public InvoiceResponseDto getById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found: " + id));
        return new InvoiceResponseDto(
                invoice.getId(),
                invoice.getCbteNumber(),
                invoice.getCae(),
                invoice.getCaeExpiration(),
                invoice.getStatus(),
                invoice.getNetAmount(),
                invoice.getIvaAmount(),
                invoice.getExemptAmount(),
                invoice.getTributesAmount(),
                invoice.getTotalAmount()
        );
    }

    // ==== helpers ====

    private static class Calculation {
        BigDecimal net = BigDecimal.ZERO;
        BigDecimal iva = BigDecimal.ZERO;
        BigDecimal exempt = BigDecimal.ZERO;
        BigDecimal tributes = BigDecimal.ZERO;
        BigDecimal total = BigDecimal.ZERO;
    }

    private Calculation calculateTotals(List<InvoiceItemDto> items, List<InvoiceTributeDto> tributes) {
        Calculation c = new Calculation();
        if (items != null) {
            for (InvoiceItemDto it : items) {
                BigDecimal lineNet = it.quantity().multiply(it.unitPrice());
                BigDecimal lineIva = calcIva(it.quantity(), it.unitPrice(), ivaRate(it.ivaCode()));
                c.net = c.net.add(lineNet);
                c.iva = c.iva.add(lineIva);
            }
        }
        if (tributes != null) {
            for (InvoiceTributeDto t : tributes) {
                c.tributes = c.tributes.add(Objects.requireNonNullElse(t.amount(), BigDecimal.ZERO));
            }
        }
        c.net = c.net.setScale(2, RoundingMode.HALF_UP);
        c.iva = c.iva.setScale(2, RoundingMode.HALF_UP);
        c.exempt = c.exempt.setScale(2, RoundingMode.HALF_UP);
        c.tributes = c.tributes.setScale(2, RoundingMode.HALF_UP);
        c.total = c.net.add(c.iva).add(c.exempt).add(c.tributes).setScale(2, RoundingMode.HALF_UP);
        return c;
    }

    private BigDecimal calcIva(BigDecimal qty, BigDecimal price, BigDecimal rate) {
        BigDecimal base = qty.multiply(price);
        return base.multiply(rate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    // Mock: mapa mínimo de alícuotas. Más adelante, leer de tabla afip_iva_aliquots.
    private BigDecimal ivaRate(Integer code) {
        if (code == null) return BigDecimal.ZERO;
        return switch (code) {
            case 5 -> new BigDecimal("21.00");   // 21%
            case 4 -> new BigDecimal("10.50");   // 10.5%
            case 3 -> BigDecimal.ZERO;           // 0%
            default -> new BigDecimal("21.00");
        };
    }

    private Integer nextLocalNumber(Long posId, Integer cbteType) {
        Invoice last = invoiceRepository.findTopByPos_IdAndCbteTypeCodeOrderByCbteNumberDesc(posId, cbteType);
        return last != null && last.getCbteNumber() != null ? last.getCbteNumber() + 1 : 1;
    }

    private String generateMockCae() {
        String nano = String.valueOf(System.nanoTime());
        String rand = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        String base = nano.substring(Math.max(0, nano.length() - 8)) + rand;
        return base.substring(0, Math.min(14, base.length()));
    }
}