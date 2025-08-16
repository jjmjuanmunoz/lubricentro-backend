package com.lubricentro.backend.dto.invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record InvoiceCreateDto(
        @NotNull Long posId,
        @NotNull Integer cbteTypeCode,   // AFIP comprobante type (e.g., 6 = Factura B)
        @NotNull Integer conceptCode,    // 1 = Products, 2 = Services, 3 = Both
        @NotNull Long customerId,
        String currencyCode,             // default ARS
        @Valid @Size(min = 1) List<InvoiceItemDto> items,
        @Valid List<InvoiceTributeDto> tributes
) {}