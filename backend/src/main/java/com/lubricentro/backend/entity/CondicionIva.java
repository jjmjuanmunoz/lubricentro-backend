package com.lubricentro.backend.entity;

/**
 * Condición frente al IVA según AFIP.
 * Códigos alineados con tabla de parámetros de AFIP para receptor.
 */
public enum CondicionIva {
    RESPONSABLE_INSCRIPTO(1, "IVA Responsable Inscripto"),
    RESPONSABLE_NO_INSCRIPTO(2, "IVA Responsable no Inscripto"),
    NO_RESPONSABLE(3, "IVA no Responsable"),
    SUJETO_EXENTO(4, "IVA Sujeto Exento"),
    CONSUMIDOR_FINAL(5, "Consumidor Final"),
    MONOTRIBUTISTA(6, "Responsable Monotributo"),
    SUJETO_NO_CATEGORIZADO(7, "Sujeto no Categorizado"),
    PROVEEDOR_EXTERIOR(8, "Proveedor del Exterior"),
    CLIENTE_EXTERIOR(9, "Cliente del Exterior"),
    IVA_LIBERADO(10, "IVA Liberado - Ley Nº 19.640"),
    MONOTRIBUTISTA_SOCIAL(13, "Monotributista Social"),
    PEQUENO_CONTRIBUYENTE_EVENTUAL(14, "Pequeño Contribuyente Eventual"),
    MONOTRIBUTO_EVENTUAL_SOCIAL(15, "Monotributo Eventual Social");

    private final int codigoAfip;
    private final String descripcion;

    CondicionIva(int codigoAfip, String descripcion) {
        this.codigoAfip = codigoAfip;
        this.descripcion = descripcion;
    }

    public int getCodigoAfip() {
        return codigoAfip;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static CondicionIva fromCodigoAfip(int codigo) {
        for (CondicionIva condicion : values()) {
            if (condicion.codigoAfip == codigo) {
                return condicion;
            }
        }
        throw new IllegalArgumentException("Código AFIP no válido: " + codigo);
    }
}
