package com.lubricentro.backend.controller;

import com.lubricentro.backend.dto.invoice.*;
import com.lubricentro.backend.entity.EstadoFactura;
import com.lubricentro.backend.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
@Tag(name = "Facturas", description = "Gestion de facturacion electronica")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // ================== CREAR FACTURA ==================

    @PostMapping
    @Operation(summary = "Crear factura", description = "Crea una nueva factura en estado BORRADOR")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Factura creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Punto de venta o cliente no encontrado")
    })
    public ResponseEntity<InvoiceDetailResponse> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceDetailResponse response = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ================== AGREGAR ITEM ==================

    @PostMapping("/{id}/items")
    @Operation(summary = "Agregar item", description = "Agrega un item a una factura en borrador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item agregado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o factura no editable"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    public ResponseEntity<InvoiceDetailResponse> addItem(
            @PathVariable Long id,
            @Valid @RequestBody AddInvoiceItemRequest request) {
        InvoiceDetailResponse response = invoiceService.addItem(id, request);
        return ResponseEntity.ok(response);
    }

    // ================== ELIMINAR ITEM ==================

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "Eliminar item", description = "Elimina un item de una factura en borrador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Factura no editable"),
            @ApiResponse(responseCode = "404", description = "Factura o item no encontrado")
    })
    public ResponseEntity<InvoiceDetailResponse> removeItem(
            @PathVariable Long id,
            @PathVariable Long itemId) {
        InvoiceDetailResponse response = invoiceService.removeItem(id, itemId);
        return ResponseEntity.ok(response);
    }

    // ================== EMITIR FACTURA ==================

    @PostMapping("/{id}/emitir")
    @Operation(summary = "Emitir factura", description = "Emite la factura (genera CAE mock o real)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Factura emitida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Factura no puede ser emitida"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    public ResponseEntity<EmitirInvoiceResponse> emitirFactura(@PathVariable Long id) {
        EmitirInvoiceResponse response = invoiceService.emitirFactura(id);
        if (response.success()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ================== OBTENER FACTURA ==================

    @GetMapping("/{id}")
    @Operation(summary = "Obtener factura", description = "Obtiene una factura con todos sus detalles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Factura encontrada"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    public ResponseEntity<InvoiceDetailResponse> getById(@PathVariable Long id) {
        InvoiceDetailResponse response = invoiceService.getById(id);
        return ResponseEntity.ok(response);
    }

    // ================== LISTAR FACTURAS ==================

    @GetMapping
    @Operation(summary = "Listar facturas", description = "Lista facturas con filtros opcionales y paginacion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de facturas")
    })
    public ResponseEntity<Page<InvoiceSummaryResponse>> list(
            @Parameter(description = "ID del usuario (cliente)")
            @RequestParam(required = false) Long userId,

            @Parameter(description = "ID del customer (cliente externo)")
            @RequestParam(required = false) Long customerId,

            @Parameter(description = "Estado de la factura")
            @RequestParam(required = false) EstadoFactura estado,

            @Parameter(description = "ID del punto de venta")
            @RequestParam(required = false) Long posId,

            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        Page<InvoiceSummaryResponse> page = invoiceService.list(userId, customerId, estado, posId, pageable);
        return ResponseEntity.ok(page);
    }

    // ================== FACTURAS POR USUARIO ==================

    @GetMapping("/user/{userId}")
    @Operation(summary = "Facturas por usuario", description = "Lista todas las facturas de un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de facturas del usuario")
    })
    public ResponseEntity<List<InvoiceSummaryResponse>> listByUser(@PathVariable Long userId) {
        List<InvoiceSummaryResponse> invoices = invoiceService.listByUser(userId);
        return ResponseEntity.ok(invoices);
    }

    // ================== EXCEPTION HANDLERS ==================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_STATE", ex.getMessage()));
    }

    public record ErrorResponse(String code, String message) {}
}
