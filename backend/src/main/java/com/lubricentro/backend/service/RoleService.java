package com.lubricentro.backend.service;

import com.lubricentro.backend.dto.RoleCreateRequest;
import com.lubricentro.backend.dto.RoleDto;
import com.lubricentro.backend.entity.Permission;
import com.lubricentro.backend.entity.Role;
import com.lubricentro.backend.repository.PermissionRepository;
import com.lubricentro.backend.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public List<RoleDto> getAll() {
        return roleRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    public Optional<RoleDto> getById(Long id) {
        return roleRepository.findByIdWithPermissions(id).map(this::mapToDto);
    }

    @Transactional
    public RoleDto create(RoleCreateRequest request) {
        Set<Permission> permissions = new HashSet<>(
                permissionRepository.findAllById(request.permissionIds())
        );

        Role role = Role.builder()
                .name(request.name())
                .description(request.description())
                .permissions(permissions)
                .build();

        return mapToDto(roleRepository.save(role));
    }

    @Transactional
    public Optional<RoleDto> update(Long id, RoleCreateRequest request) {
        return roleRepository.findById(id).map(existing -> {
            existing.setName(request.name());
            existing.setDescription(request.description());

            Set<Permission> permissions = new HashSet<>(
                    permissionRepository.findAllById(request.permissionIds())
            );
            existing.setPermissions(permissions);

            return mapToDto(roleRepository.save(existing));
        });
    }

    @Transactional
    public boolean delete(Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private RoleDto mapToDto(Role entity) {
        Set<String> permissionCodes = entity.getPermissions().stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());

        return new RoleDto(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                permissionCodes
        );
    }
}
