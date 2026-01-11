package com.lubricentro.backend.dto;

import jakarta.validation.constraints.Size;

public record DomicilioDto(
        @Size(max = 200) String calle,
        @Size(max = 20) String numero,
        @Size(max = 50) String piso,
        @Size(max = 50) String departamento,
        @Size(max = 100) String localidad,
        @Size(max = 100) String provincia,
        @Size(max = 10) String codigoPostal,
        @Size(max = 100) String pais
) {
}
