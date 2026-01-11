package com.lubricentro.backend.service;

import com.lubricentro.backend.dto.DomicilioDto;
import com.lubricentro.backend.dto.UserCreateRequest;
import com.lubricentro.backend.dto.UserResponse;
import com.lubricentro.backend.dto.UserUpdateRequest;
import com.lubricentro.backend.entity.Domicilio;
import com.lubricentro.backend.entity.Role;
import com.lubricentro.backend.entity.User;
import com.lubricentro.backend.repository.RoleRepository;
import com.lubricentro.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Optional<UserResponse> findById(Long id) {
        return userRepository.findById(id).map(this::mapToResponse);
    }

    public List<UserResponse> search(String nombre, String apellido, String numeroDocumento) {
        return userRepository.searchUsers(nombre, apellido, numeroDocumento).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        validateUniqueConstraints(request.username(), request.email(), request.numeroDocumento(), null);

        Set<Role> roles = new HashSet<>();
        if (request.roleIds() != null && !request.roleIds().isEmpty()) {
            roles = new HashSet<>(roleRepository.findAllById(request.roleIds()));
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .nombre(request.nombre())
                .apellido(request.apellido())
                .razonSocial(request.razonSocial())
                .tipoDocumento(request.tipoDocumento())
                .numeroDocumento(request.numeroDocumento())
                .condicionIva(request.condicionIva())
                .telefono(request.telefono())
                .domicilio(mapToDomicilio(request.domicilio()))
                .roles(roles)
                .isActive(request.isActive() != null ? request.isActive() : true)
                .build();

        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public Optional<UserResponse> update(Long id, UserUpdateRequest request) {
        return userRepository.findById(id).map(existing -> {
            if (request.username() != null) {
                validateUniqueConstraints(request.username(), null, null, id);
                existing.setUsername(request.username());
            }
            if (request.email() != null) {
                validateUniqueConstraints(null, request.email(), null, id);
                existing.setEmail(request.email());
            }
            if (request.password() != null && !request.password().isBlank()) {
                existing.setPasswordHash(passwordEncoder.encode(request.password()));
            }
            if (request.fullName() != null) {
                existing.setFullName(request.fullName());
            }
            if (request.nombre() != null) {
                existing.setNombre(request.nombre());
            }
            if (request.apellido() != null) {
                existing.setApellido(request.apellido());
            }
            if (request.razonSocial() != null) {
                existing.setRazonSocial(request.razonSocial());
            }
            if (request.tipoDocumento() != null) {
                existing.setTipoDocumento(request.tipoDocumento());
            }
            if (request.numeroDocumento() != null) {
                validateUniqueConstraints(null, null, request.numeroDocumento(), id);
                existing.setNumeroDocumento(request.numeroDocumento());
            }
            if (request.condicionIva() != null) {
                existing.setCondicionIva(request.condicionIva());
            }
            if (request.telefono() != null) {
                existing.setTelefono(request.telefono());
            }
            if (request.domicilio() != null) {
                existing.setDomicilio(mapToDomicilio(request.domicilio()));
            }
            if (request.roleIds() != null) {
                Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.roleIds()));
                existing.setRoles(roles);
            }
            if (request.isActive() != null) {
                existing.setIsActive(request.isActive());
            }

            return mapToResponse(userRepository.save(existing));
        });
    }

    @Transactional
    public boolean delete(Long id) {
        return userRepository.findById(id).map(user -> {
            user.setIsActive(false);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean hardDelete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validateUniqueConstraints(String username, String email, String numeroDocumento, Long excludeId) {
        if (username != null) {
            userRepository.findByUsernameAndIsActiveTrue(username)
                    .filter(u -> !u.getId().equals(excludeId))
                    .ifPresent(u -> {
                        throw new IllegalArgumentException("Username ya existe: " + username);
                    });
        }
        if (email != null) {
            userRepository.findByEmailAndIsActiveTrue(email)
                    .filter(u -> !u.getId().equals(excludeId))
                    .ifPresent(u -> {
                        throw new IllegalArgumentException("Email ya existe: " + email);
                    });
        }
        if (numeroDocumento != null) {
            userRepository.findByNumeroDocumentoAndIsActiveTrue(numeroDocumento)
                    .filter(u -> !u.getId().equals(excludeId))
                    .ifPresent(u -> {
                        throw new IllegalArgumentException("Número de documento ya existe: " + numeroDocumento);
                    });
        }
    }

    private UserResponse mapToResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getNombre(),
                user.getApellido(),
                user.getRazonSocial(),
                user.getNombreFiscal(),
                user.getTipoDocumento(),
                user.getNumeroDocumento(),
                user.getCondicionIva(),
                user.getTelefono(),
                mapToDomicilioDto(user.getDomicilio()),
                roleNames,
                user.getIsActive()
        );
    }

    private Domicilio mapToDomicilio(DomicilioDto dto) {
        if (dto == null) return null;
        return Domicilio.builder()
                .calle(dto.calle())
                .numero(dto.numero())
                .piso(dto.piso())
                .departamento(dto.departamento())
                .localidad(dto.localidad())
                .provincia(dto.provincia())
                .codigoPostal(dto.codigoPostal())
                .pais(dto.pais() != null ? dto.pais() : "Argentina")
                .build();
    }

    private DomicilioDto mapToDomicilioDto(Domicilio domicilio) {
        if (domicilio == null) return null;
        return new DomicilioDto(
                domicilio.getCalle(),
                domicilio.getNumero(),
                domicilio.getPiso(),
                domicilio.getDepartamento(),
                domicilio.getLocalidad(),
                domicilio.getProvincia(),
                domicilio.getCodigoPostal(),
                domicilio.getPais()
        );
    }
}
