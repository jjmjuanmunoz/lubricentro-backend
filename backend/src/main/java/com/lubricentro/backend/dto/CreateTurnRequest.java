package com.lubricentro.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateTurnRequest(
    String plate,
    LocalDate scheduledDate,
    LocalTime scheduledTime,
    String notes,
    List<BudgetItemRequest> items
) {}
