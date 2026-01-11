package com.lubricentro.backend.dto.pos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear un nuevo punto de venta.
 */
public record CreatePointOfSaleRequest(
    @NotNull(message = "El numero de punto de venta es requerido")
    @Min(value = 1, message = "El numero debe ser al menos 1")
    @Max(value = 99999, message = "El numero no puede exceder 99999")
    Integer numero,

    String descripcion,

    String alcanceTipos,     // A_B, A_B_C, etc. Default: A_B

    Boolean homologacion     // Default: true (ambiente de pruebas)
) {
    public CreatePointOfSaleRequest {
        if (alcanceTipos == null || alcanceTipos.isBlank()) {
            alcanceTipos = "A_B";
        }
        if (homologacion == null) {
            homologacion = true;
        }
    }
}
