package com.lubricentro.backend.dto.invoice;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceResponseDto(
        Long id,
        Integer cbteNumber,
        String cae,
        LocalDate caeExpiration,
        String status,
        BigDecimal netAmount,
        BigDecimal ivaAmount,
        BigDecimal exemptAmount,
        BigDecimal tributesAmount,
        BigDecimal totalAmount
) {}