package com.lubricentro.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TurnDTO(
    Long id,
    AutomobileDTO automobile,
    String status,
    Integer turnNumber,
    LocalDateTime arrivalTime,
    List<BudgetItemDTO> items,
    BigDecimal totalEstimated,
    String notes
) {}
