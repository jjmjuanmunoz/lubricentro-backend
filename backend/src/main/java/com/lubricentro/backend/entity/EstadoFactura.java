package com.lubricentro.backend.entity;

/**
 * Estados posibles de una factura.
 */
public enum EstadoFactura {
    /**
     * Factura en borrador, aun no emitida.
     * Se pueden agregar/quitar items.
     */
    BORRADOR("Borrador"),

    /**
     * Factura emitida y aprobada por AFIP.
     * Ya tiene CAE asignado.
     */
    EMITIDA("Emitida"),

    /**
     * Factura anulada.
     * Requiere nota de credito para anulacion fiscal.
     */
    ANULADA("Anulada"),

    /**
     * Factura rechazada por AFIP.
     */
    RECHAZADA("Rechazada"),

    /**
     * Factura pendiente de autorizacion AFIP.
     */
    PENDIENTE("Pendiente");

    private final String descripcion;

    EstadoFactura(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
