package com.lubricentro.backend.dto.invoice;

import com.lubricentro.backend.entity.EstadoFactura;
import com.lubricentro.backend.entity.TipoComprobante;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO completo de respuesta para una factura con todos sus detalles.
 */
public record InvoiceDetailResponse(
    Long id,

    // Punto de venta
    Long posId,
    Integer posNumber,
    String posDescription,

    // Tipo y numero
    TipoComprobante tipoComprobante,
    Integer codigoAfip,
    Integer numeroComprobante,

    // Cliente
    Long userId,
    Long customerId,
    String nombreCliente,
    String documentoCliente,
    String condicionIva,

    // Moneda
    String currencyCode,
    BigDecimal currencyRate,

    // Totales
    BigDecimal subtotal,        // neto gravado
    BigDecimal ivaAmount,
    BigDecimal exemptAmount,    // exento
    BigDecimal tributesAmount,  // otros tributos
    BigDecimal total,

    // AFIP
    String cae,
    LocalDate caeVencimiento,
    LocalDateTime fechaEmision,

    // Estado
    EstadoFactura estado,
    boolean editable,

    // Items
    List<InvoiceItemResponse> items,

    // Tributos
    List<InvoiceTributeResponse> tributos,

    // Auditoria
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
