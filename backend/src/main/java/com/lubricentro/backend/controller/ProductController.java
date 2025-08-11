package com.lubricentro.backend.controller;

import com.lubricentro.backend.dto.ProductDto;
import com.lubricentro.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @Operation(summary = "Get all products", description = "Returns a list of all products in stock.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products successfully retrieved")
    })
    @GetMapping
    public List<ProductDto> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Get product by ID", description = "Returns a single product by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new product", description = "Creates a new product in the stock. ID is auto-generated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product created successfully")
    })
    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary = "Update existing product", description = "Updates an existing product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody ProductDto dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete product", description = "Deletes a product from the stock by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }
}