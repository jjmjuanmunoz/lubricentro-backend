package com.lubricentro.backend.dto;

import java.math.BigDecimal;

public record BudgetItemDTO(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal finalPrice,
    Boolean included
) {}
