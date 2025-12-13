package com.lubricentro.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PermissionDto(
    Long id,
    @NotBlank(message = "Permission code is required")
    @Pattern(regexp = "^[a-z]+:[a-z]+$", message = "Code must be in format 'resource:action'")
    String code,
    @NotBlank(message = "Description is required")
    String description
) {}
