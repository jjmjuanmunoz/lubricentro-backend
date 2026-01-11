package com.lubricentro.backend.dto.invoice;

import java.time.LocalDate;

/**
 * DTO de respuesta para la emision de una factura.
 */
public record EmitirInvoiceResponse(
    Long invoiceId,
    boolean success,
    Integer numeroComprobante,
    String cae,
    LocalDate caeVencimiento,
    String mensaje,
    String errores
) {
    public static EmitirInvoiceResponse success(Long invoiceId, Integer numero, String cae, LocalDate vencimiento) {
        return new EmitirInvoiceResponse(invoiceId, true, numero, cae, vencimiento,
                "Factura emitida correctamente", null);
    }

    public static EmitirInvoiceResponse error(Long invoiceId, String errores) {
        return new EmitirInvoiceResponse(invoiceId, false, null, null, null,
                "Error al emitir factura", errores);
    }
}
