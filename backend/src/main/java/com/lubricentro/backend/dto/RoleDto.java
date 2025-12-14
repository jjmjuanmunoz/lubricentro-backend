package com.lubricentro.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Set;

public record RoleDto(
    Long id,
    @NotBlank(message = "Role name is required")
    @Pattern(regexp = "^ROLE_[A-Z_]+$", message = "Role name must start with ROLE_")
    String name,
    String description,
    Set<String> permissionCodes
) {}
