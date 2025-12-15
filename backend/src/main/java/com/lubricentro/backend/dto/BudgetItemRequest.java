package com.lubricentro.backend.dto;

public record BudgetItemRequest(
    Long productId,
    Integer quantity,
    String notes
) {}
