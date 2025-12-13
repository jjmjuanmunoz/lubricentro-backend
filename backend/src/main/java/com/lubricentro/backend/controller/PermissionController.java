package com.lubricentro.backend.controller;

import com.lubricentro.backend.dto.PermissionDto;
import com.lubricentro.backend.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final PermissionService service;

    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @Operation(summary = "Get all permissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permissions retrieved successfully")
    })
    @GetMapping
    public List<PermissionDto> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Get permission by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permission found"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PermissionDto> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new permission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permission created successfully")
    })
    @PostMapping
    public ResponseEntity<PermissionDto> create(@Valid @RequestBody PermissionDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }
}
