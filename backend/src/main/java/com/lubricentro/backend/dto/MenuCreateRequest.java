package com.lubricentro.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuCreateRequest(
    @NotBlank(message = "Menu title is required")
    String title,
    String url,
    @NotBlank(message = "Menu type is required")
    String type,
    String icon,
    String permissionCode,
    Long parentId,
    @NotNull(message = "Display order is required")
    Integer displayOrder
) {}
