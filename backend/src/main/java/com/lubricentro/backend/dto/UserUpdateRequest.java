package com.lubricentro.backend.dto;

import com.lubricentro.backend.entity.CondicionIva;
import com.lubricentro.backend.entity.TipoDocumento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UserUpdateRequest(
        @Size(max = 100)
        String username,

        @Email(message = "Email inválido")
        @Size(max = 255)
        String email,

        @Size(min = 6, message = "Password debe tener al menos 6 caracteres")
        String password,

        @Size(max = 255)
        String fullName,

        @Size(max = 100)
        String nombre,

        @Size(max = 100)
        String apellido,

        @Size(max = 200)
        String razonSocial,

        TipoDocumento tipoDocumento,

        @Size(max = 20)
        String numeroDocumento,

        CondicionIva condicionIva,

        @Size(max = 30)
        String telefono,

        @Valid
        DomicilioDto domicilio,

        Set<Long> roleIds,

        Boolean isActive
) {
}
