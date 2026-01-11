package com.lubricentro.backend.dto.invoice;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para agregar un item a una factura existente.
 */
public record AddInvoiceItemRequest(
    Long productId,                    // Opcional: referencia al producto

    @NotNull(message = "La descripcion es requerida")
    String descripcion,

    @NotNull(message = "La cantidad es requerida")
    @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor a 0")
    BigDecimal cantidad,

    @NotNull(message = "El precio unitario es requerido")
    @DecimalMin(value = "0", message = "El precio unitario no puede ser negativo")
    BigDecimal precioUnitario,

    Integer codigoIva                  // Codigo alicuota IVA (default: 5 = 21%)
) {
    public AddInvoiceItemRequest {
        if (codigoIva == null) {
            codigoIva = 5; // 21% por defecto
        }
    }
}
