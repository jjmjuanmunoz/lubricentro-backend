package com.lubricentro.backend.dto.invoice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record InvoiceTributeDto(
        @NotNull Integer code,
        @NotBlank String description,
        @NotNull BigDecimal baseAmount,
        @NotNull BigDecimal rate,
        @NotNull BigDecimal amount
) {}