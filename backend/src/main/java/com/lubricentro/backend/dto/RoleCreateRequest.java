package com.lubricentro.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RoleCreateRequest(
    @NotBlank(message = "Role name is required")
    String name,
    String description,
    @NotNull(message = "Permission IDs are required")
    Set<Long> permissionIds
) {}
