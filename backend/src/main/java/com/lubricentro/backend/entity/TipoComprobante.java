package com.lubricentro.backend.entity;

/**
 * Tipos de comprobante AFIP.
 * Los codigos corresponden a los codigos oficiales de AFIP.
 */
public enum TipoComprobante {
    FACTURA_A(1, "Factura A"),
    FACTURA_B(6, "Factura B"),
    FACTURA_C(11, "Factura C"),
    NOTA_DEBITO_A(2, "Nota de Debito A"),
    NOTA_DEBITO_B(7, "Nota de Debito B"),
    NOTA_DEBITO_C(12, "Nota de Debito C"),
    NOTA_CREDITO_A(3, "Nota de Credito A"),
    NOTA_CREDITO_B(8, "Nota de Credito B"),
    NOTA_CREDITO_C(13, "Nota de Credito C"),
    RECIBO_A(4, "Recibo A"),
    RECIBO_B(9, "Recibo B"),
    RECIBO_C(15, "Recibo C");

    private final int codigoAfip;
    private final String descripcion;

    TipoComprobante(int codigoAfip, String descripcion) {
        this.codigoAfip = codigoAfip;
        this.descripcion = descripcion;
    }

    public int getCodigoAfip() {
        return codigoAfip;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene el TipoComprobante a partir del codigo AFIP.
     */
    public static TipoComprobante fromCodigoAfip(int codigo) {
        for (TipoComprobante tipo : values()) {
            if (tipo.codigoAfip == codigo) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Codigo AFIP no reconocido: " + codigo);
    }

    /**
     * Determina el tipo de factura segun la condicion IVA del cliente.
     */
    public static TipoComprobante determinarTipoFactura(CondicionIva condicionCliente, CondicionIva condicionEmisor) {
        if (condicionEmisor == CondicionIva.RESPONSABLE_INSCRIPTO) {
            return switch (condicionCliente) {
                case RESPONSABLE_INSCRIPTO -> FACTURA_A;
                case MONOTRIBUTISTA, CONSUMIDOR_FINAL, SUJETO_EXENTO -> FACTURA_B;
                default -> FACTURA_B;
            };
        } else if (condicionEmisor == CondicionIva.MONOTRIBUTISTA) {
            return FACTURA_C;
        }
        return FACTURA_B;
    }
}
