package com.lubricentro.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_numero_documento", columnList = "numero_documento"),
    @Index(name = "idx_users_tipo_numero_doc", columnList = "tipo_documento, numero_documento")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "full_name")
    private String fullName;

    // --- Campos fiscales AFIP ---

    @Size(max = 100)
    @Column(name = "nombre", length = 100)
    private String nombre;

    @Size(max = 100)
    @Column(name = "apellido", length = 100)
    private String apellido;

    @Size(max = 200)
    @Column(name = "razon_social", length = 200)
    private String razonSocial;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", length = 20)
    private TipoDocumento tipoDocumento;

    @Size(max = 20)
    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicion_iva", length = 40)
    private CondicionIva condicionIva;

    @Size(max = 30)
    @Column(name = "telefono", length = 30)
    private String telefono;

    @Embedded
    private Domicilio domicilio;

    // --- Fin campos fiscales ---

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Retorna el nombre fiscal para facturación.
     * Prioriza razonSocial, luego nombre+apellido, finalmente fullName.
     */
    public String getNombreFiscal() {
        if (razonSocial != null && !razonSocial.isBlank()) {
            return razonSocial;
        }
        if (nombre != null || apellido != null) {
            return ((apellido != null ? apellido : "") + " " + (nombre != null ? nombre : "")).trim();
        }
        return fullName;
    }
}