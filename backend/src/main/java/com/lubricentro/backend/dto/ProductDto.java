package com.lubricentro.backend.dto;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String name,
        String description,
        String type,
        String brand,
        String viscosity,
        String unitOfMeasure,
        Integer stockQuantity,
        Integer minimumStock,
        BigDecimal unitPrice,
        Boolean active
) {}