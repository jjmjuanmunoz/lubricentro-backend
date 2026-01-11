package com.lubricentro.backend.controller;

import com.lubricentro.backend.dto.UserCreateRequest;
import com.lubricentro.backend.dto.UserResponse;
import com.lubricentro.backend.dto.UserUpdateRequest;
import com.lubricentro.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "ABM de usuarios con datos fiscales")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Listar o Buscar usuarios", description = "Obtiene todos los usuarios o busca por criterios")
    @GetMapping
    public ResponseEntity<List<UserResponse>> listOrSearch(
            @Parameter(description = "Filtrar por nombre (parcial, case-insensitive)")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtrar por apellido (parcial, case-insensitive)")
            @RequestParam(required = false) String apellido,
            @Parameter(description = "Filtrar por número de documento (DNI/CUIT/CUIL)")
            @RequestParam(required = false) String numeroDocumento) {

        List<UserResponse> users;
        if (nombre != null || apellido != null || numeroDocumento != null) {
            users = userService.search(nombre, apellido, numeroDocumento);
        } else {
            users = userService.findAll();
        }
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario con datos fiscales")
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        try {
            UserResponse created = userService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza datos de un usuario existente")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        try {
            return userService.update(id, request)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Desactivar usuario", description = "Soft delete: marca el usuario como inactivo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
