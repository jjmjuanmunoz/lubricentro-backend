package com.lubricentro.backend.dto.invoice;

import com.lubricentro.backend.entity.TipoComprobante;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear una nueva factura en estado BORRADOR.
 */
public record CreateInvoiceRequest(
    @NotNull(message = "El punto de venta es requerido")
    Long posId,

    @NotNull(message = "El tipo de comprobante es requerido")
    TipoComprobante tipoComprobante,

    Long userId,       // Cliente como User del sistema (opcional)
    Long customerId,   // Cliente externo (opcional)

    Integer conceptCode,    // 1=Productos, 2=Servicios, 3=Ambos (default: 1)
    String currencyCode     // default: ARS
) {
    public CreateInvoiceRequest {
        // Al menos uno de userId o customerId debe estar presente
        // La validacion se hace en el service para dar mensajes claros
        if (conceptCode == null) {
            conceptCode = 1; // Productos por defecto
        }
        if (currencyCode == null || currencyCode.isBlank()) {
            currencyCode = "ARS";
        }
    }
}
