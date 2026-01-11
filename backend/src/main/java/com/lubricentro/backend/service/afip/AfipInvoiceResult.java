package com.lubricentro.backend.service.afip;

import java.time.LocalDate;

/**
 * Resultado de la emision de una factura en AFIP.
 */
public record AfipInvoiceResult(
    boolean success,
    String cae,
    LocalDate caeVencimiento,
    Integer numeroComprobante,
    String resultado,      // A = Aprobado, R = Rechazado, P = Parcial
    String observaciones,
    String errores
) {
    /**
     * Crea un resultado exitoso.
     */
    public static AfipInvoiceResult success(String cae, LocalDate caeVencimiento, Integer numeroComprobante) {
        return new AfipInvoiceResult(true, cae, caeVencimiento, numeroComprobante, "A", null, null);
    }

    /**
     * Crea un resultado de rechazo.
     */
    public static AfipInvoiceResult rejected(String errores) {
        return new AfipInvoiceResult(false, null, null, null, "R", null, errores);
    }

    /**
     * Verifica si el resultado fue aprobado.
     */
    public boolean isApproved() {
        return success && "A".equals(resultado);
    }
}
