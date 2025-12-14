package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    Set<Permission> findByCodeIn(Set<String> codes);

    boolean existsByCode(String code);
}
