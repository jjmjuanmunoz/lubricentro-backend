package com.lubricentro.backend.controller;

import com.lubricentro.backend.dto.pos.CreatePointOfSaleRequest;
import com.lubricentro.backend.dto.pos.PointOfSaleResponse;
import com.lubricentro.backend.service.PointOfSaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pos")
@Tag(name = "Puntos de Venta", description = "Gestion de puntos de venta AFIP")
public class PointOfSaleController {

    private final PointOfSaleService posService;

    public PointOfSaleController(PointOfSaleService posService) {
        this.posService = posService;
    }

    @GetMapping
    @Operation(summary = "Listar puntos de venta", description = "Lista todos los puntos de venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de puntos de venta")
    })
    public ResponseEntity<List<PointOfSaleResponse>> listAll() {
        return ResponseEntity.ok(posService.listAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Listar puntos de venta activos", description = "Lista solo los puntos de venta activos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de puntos de venta activos")
    })
    public ResponseEntity<List<PointOfSaleResponse>> listActive() {
        return ResponseEntity.ok(posService.listActive());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener punto de venta", description = "Obtiene un punto de venta por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Punto de venta encontrado"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado")
    })
    public ResponseEntity<PointOfSaleResponse> getById(@PathVariable Long id) {
        return posService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{number}")
    @Operation(summary = "Obtener por numero", description = "Obtiene un punto de venta por su numero")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Punto de venta encontrado"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado")
    })
    public ResponseEntity<PointOfSaleResponse> getByNumber(@PathVariable Integer number) {
        return posService.getByNumber(number)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear punto de venta", description = "Crea un nuevo punto de venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Punto de venta creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o numero duplicado")
    })
    public ResponseEntity<PointOfSaleResponse> create(@Valid @RequestBody CreatePointOfSaleRequest request) {
        PointOfSaleResponse response = posService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activar punto de venta", description = "Activa un punto de venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Punto de venta activado"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado")
    })
    public ResponseEntity<PointOfSaleResponse> activate(@PathVariable Long id) {
        return ResponseEntity.ok(posService.activate(id));
    }

    @PostMapping("/{id}/deactivate")
    @Operation(summary = "Desactivar punto de venta", description = "Desactiva un punto de venta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Punto de venta desactivado"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado")
    })
    public ResponseEntity<PointOfSaleResponse> deactivate(@PathVariable Long id) {
        return ResponseEntity.ok(posService.deactivate(id));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("BAD_REQUEST", ex.getMessage()));
    }

    public record ErrorResponse(String code, String message) {}
}
