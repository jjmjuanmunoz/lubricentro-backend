package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameIgnoreCaseContainingOrDescriptionIgnoreCaseContaining(String name, String description);

}