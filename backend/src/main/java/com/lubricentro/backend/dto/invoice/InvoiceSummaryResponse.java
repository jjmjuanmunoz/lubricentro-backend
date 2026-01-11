package com.lubricentro.backend.dto.invoice;

import com.lubricentro.backend.entity.EstadoFactura;
import com.lubricentro.backend.entity.TipoComprobante;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO resumido para listados de facturas.
 */
public record InvoiceSummaryResponse(
    Long id,
    Integer posNumber,
    TipoComprobante tipoComprobante,
    Integer numeroComprobante,
    String nombreCliente,
    BigDecimal total,
    EstadoFactura estado,
    String cae,
    LocalDate caeVencimiento,
    LocalDateTime createdAt
) {}
