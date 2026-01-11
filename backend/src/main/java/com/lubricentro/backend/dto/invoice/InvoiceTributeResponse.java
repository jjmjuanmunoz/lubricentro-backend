package com.lubricentro.backend.dto.invoice;

import java.math.BigDecimal;

/**
 * DTO de respuesta para un tributo de factura.
 */
public record InvoiceTributeResponse(
    Long id,
    Integer codigo,
    String descripcion,
    BigDecimal baseImponible,
    BigDecimal alicuota,
    BigDecimal importe
) {}
