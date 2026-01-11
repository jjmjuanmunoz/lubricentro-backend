package com.lubricentro.backend.dto.invoice;

import java.math.BigDecimal;

/**
 * DTO de respuesta para un item de factura.
 */
public record InvoiceItemResponse(
    Long id,
    Long productId,
    String descripcion,
    BigDecimal cantidad,
    BigDecimal precioUnitario,
    Integer codigoIva,
    String descripcionIva,
    BigDecimal importeNeto,
    BigDecimal importeIva,
    BigDecimal importeTotal
) {}
