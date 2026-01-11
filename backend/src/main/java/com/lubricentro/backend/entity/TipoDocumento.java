package com.lubricentro.backend.entity;

/**
 * Tipos de documento según AFIP.
 * Códigos alineados con tabla de parámetros de AFIP (FEParamGetTiposDoc).
 */
public enum TipoDocumento {
    CUIT(80, "CUIT"),
    CUIL(86, "CUIL"),
    DNI(96, "DNI"),
    PASAPORTE(94, "Pasaporte"),
    CI_EXTRANJERA(91, "CI Extranjera"),
    SIN_IDENTIFICAR(99, "Doc. (Otro)");

    private final int codigoAfip;
    private final String descripcion;

    TipoDocumento(int codigoAfip, String descripcion) {
        this.codigoAfip = codigoAfip;
        this.descripcion = descripcion;
    }

    public int getCodigoAfip() {
        return codigoAfip;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoDocumento fromCodigoAfip(int codigo) {
        for (TipoDocumento tipo : values()) {
            if (tipo.codigoAfip == codigo) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Código AFIP no válido: " + codigo);
    }
}
