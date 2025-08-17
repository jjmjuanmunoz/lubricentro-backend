package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.AfipPos;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AfipPosRepository extends JpaRepository<AfipPos, Long> {
    Optional<AfipPos> findByPosNumber(Integer posNumber);
}