package com.lubricentro.backend.dto;

public record MenuFlatDto(
    Long id,
    String title,
    String url,
    String type,
    String icon,
    String translateKey,
    Long parentId,
    Integer displayOrder
) {}
