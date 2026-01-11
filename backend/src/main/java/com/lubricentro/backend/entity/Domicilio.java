package com.lubricentro.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domicilio fiscal embebido.
 * Se almacena en la misma tabla que la entidad padre.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Domicilio {

    @Size(max = 200)
    @Column(name = "domicilio_calle", length = 200)
    private String calle;

    @Size(max = 20)
    @Column(name = "domicilio_numero", length = 20)
    private String numero;

    @Size(max = 50)
    @Column(name = "domicilio_piso", length = 50)
    private String piso;

    @Size(max = 50)
    @Column(name = "domicilio_depto", length = 50)
    private String departamento;

    @Size(max = 100)
    @Column(name = "domicilio_localidad", length = 100)
    private String localidad;

    @Size(max = 100)
    @Column(name = "domicilio_provincia", length = 100)
    private String provincia;

    @Size(max = 10)
    @Column(name = "domicilio_codigo_postal", length = 10)
    private String codigoPostal;

    @Size(max = 100)
    @Column(name = "domicilio_pais", length = 100)
    @Builder.Default
    private String pais = "Argentina";

    /**
     * Retorna el domicilio formateado como una línea.
     */
    public String getDomicilioCompleto() {
        StringBuilder sb = new StringBuilder();
        if (calle != null) sb.append(calle);
        if (numero != null) sb.append(" ").append(numero);
        if (piso != null) sb.append(", Piso ").append(piso);
        if (departamento != null) sb.append(" ").append(departamento);
        if (localidad != null) sb.append(", ").append(localidad);
        if (provincia != null) sb.append(", ").append(provincia);
        if (codigoPostal != null) sb.append(" (").append(codigoPostal).append(")");
        return sb.toString().trim();
    }
}
