package com.lubricentro.backend.service;

import com.lubricentro.backend.dto.invoice.InvoiceCreateDto;
import com.lubricentro.backend.dto.invoice.InvoiceResponseDto;

public interface BillingService {
    InvoiceResponseDto createAndAuthorize(InvoiceCreateDto dto);
    InvoiceResponseDto getById(Long id);
}