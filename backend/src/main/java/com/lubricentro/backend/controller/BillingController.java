package com.lubricentro.backend.controller;

import com.lubricentro.backend.dto.invoice.InvoiceCreateDto;
import com.lubricentro.backend.dto.invoice.InvoiceResponseDto;
import com.lubricentro.backend.entity.AfipPos;
import com.lubricentro.backend.repository.AfipPosRepository;
import com.lubricentro.backend.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billingService;
    private final AfipPosRepository posRepository;

    public BillingController(BillingService billingService, AfipPosRepository posRepository) {
        this.billingService = billingService;
        this.posRepository = posRepository;
    }

    @Operation(summary = "List AFIP Points of Sale")
    @GetMapping("/pos")
    public List<AfipPos> listPos() {
        return posRepository.findAll();
    }

    @Operation(summary = "Create AFIP Point of Sale")
    @PostMapping("/pos")
    public AfipPos createPos(@RequestBody AfipPos pos) {
        return posRepository.save(pos);
    }

    @Operation(summary = "Create and authorize an invoice (mock in homologation)")
    @PostMapping("/invoices")
    public ResponseEntity<InvoiceResponseDto> create(@Valid @RequestBody InvoiceCreateDto dto) {
        return ResponseEntity.ok(billingService.createAndAuthorize(dto));
    }

    @Operation(summary = "Get invoice by ID")
    @GetMapping("/invoices/{id}")
    public ResponseEntity<InvoiceResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getById(id));
    }
}