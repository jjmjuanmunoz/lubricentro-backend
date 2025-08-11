package com.lubricentro.backend.service;

import com.lubricentro.backend.dto.ProductDto;
import com.lubricentro.backend.entity.Product;
import com.lubricentro.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<ProductDto> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public Optional<ProductDto> getById(Long id) {
        return repository.findById(id).map(this::mapToDto);
    }

    public ProductDto create(ProductDto dto) {
        Product product = mapToEntity(dto);
        product.setId(null); // asegurar que es nuevo
        return mapToDto(repository.save(product));
    }

    public Optional<ProductDto> update(Long id, ProductDto dto) {
        return repository.findById(id).map(existing -> {
            Product updated = mapToEntity(dto);
            updated.setId(id);
            return mapToDto(repository.save(updated));
        });
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private ProductDto mapToDto(Product entity) {
        return new ProductDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getType(),
                entity.getBrand(),
                entity.getViscosity(),
                entity.getUnitOfMeasure(),
                entity.getStockQuantity(),
                entity.getMinimumStock(),
                entity.getUnitPrice(),
                entity.getActive()
        );
    }

    private Product mapToEntity(ProductDto dto) {
        return Product.builder()
                .id(dto.id())
                .name(dto.name())
                .description(dto.description())
                .type(dto.type())
                .brand(dto.brand())
                .viscosity(dto.viscosity())
                .unitOfMeasure(dto.unitOfMeasure())
                .stockQuantity(dto.stockQuantity())
                .minimumStock(dto.minimumStock())
                .unitPrice(dto.unitPrice())
                .active(dto.active())
                .build();
    }
}