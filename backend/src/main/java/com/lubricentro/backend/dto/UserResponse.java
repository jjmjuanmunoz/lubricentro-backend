package com.lubricentro.backend.dto;

import com.lubricentro.backend.entity.CondicionIva;
import com.lubricentro.backend.entity.TipoDocumento;

import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String email,
        String fullName,
        String nombre,
        String apellido,
        String razonSocial,
        String nombreFiscal,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        CondicionIva condicionIva,
        String telefono,
        DomicilioDto domicilio,
        Set<String> roles,
        Boolean isActive
) {
}
