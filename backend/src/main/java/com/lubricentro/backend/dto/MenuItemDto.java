package com.lubricentro.backend.dto;

import java.util.List;

public record MenuItemDto(
    Long id,
    String title,
    String url,
    String type,
    String icon,
    String permission,
    List<MenuItemDto> children
) {}
