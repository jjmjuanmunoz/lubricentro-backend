package com.lubricentro.backend.dto.pos;

/**
 * DTO de respuesta para un punto de venta.
 */
public record PointOfSaleResponse(
    Long id,
    Integer numero,
    String descripcion,
    String alcanceTipos,     // A_B, A_B_C, etc.
    boolean homologacion,
    boolean activo
) {}
