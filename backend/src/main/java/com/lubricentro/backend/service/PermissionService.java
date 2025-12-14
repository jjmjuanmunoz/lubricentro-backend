package com.lubricentro.backend.service;

import com.lubricentro.backend.dto.PermissionDto;
import com.lubricentro.backend.entity.Permission;
import com.lubricentro.backend.entity.User;
import com.lubricentro.backend.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PermissionService {

    private final PermissionRepository repository;

    public PermissionService(PermissionRepository repository) {
        this.repository = repository;
    }

    public List<PermissionDto> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public Optional<PermissionDto> getById(Long id) {
        return repository.findById(id).map(this::mapToDto);
    }

    public Optional<PermissionDto> getByCode(String code) {
        return repository.findByCode(code).map(this::mapToDto);
    }

    @Transactional
    public PermissionDto create(PermissionDto dto) {
        Permission permission = mapToEntity(dto);
        permission.setId(null);
        return mapToDto(repository.save(permission));
    }

    /**
     * Get all permission codes for a user by aggregating from all their roles
     */
    public Set<String> getUserPermissions(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    /**
     * Check if user has a specific permission
     */
    public boolean hasPermission(User user, String permissionCode) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission -> permission.getCode().equals(permissionCode));
    }

    private PermissionDto mapToDto(Permission entity) {
        return new PermissionDto(
                entity.getId(),
                entity.getCode(),
                entity.getDescription()
        );
    }

    private Permission mapToEntity(PermissionDto dto) {
        return Permission.builder()
                .id(dto.id())
                .code(dto.code())
                .description(dto.description())
                .build();
    }
}
