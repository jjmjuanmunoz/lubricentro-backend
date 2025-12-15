package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.Automobile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AutomobileRepository extends JpaRepository<Automobile, Long> {

    Optional<Automobile> findByPlate(String plate);
}
