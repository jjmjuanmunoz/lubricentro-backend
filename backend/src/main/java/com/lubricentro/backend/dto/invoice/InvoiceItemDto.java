package com.lubricentro.backend.dto.invoice;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record InvoiceItemDto(
        Long productId,
        @NotBlank String description,
        @NotNull @Min(1) BigDecimal quantity,
        @NotNull BigDecimal unitPrice,
        @NotNull Integer ivaCode // AFIP IVA aliquot code, e.g., 5 = 21%
) {}