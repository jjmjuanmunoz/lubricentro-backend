package com.lubricentro.backend.controller;

import com.lubricentro.backend.dto.MenuCreateRequest;
import com.lubricentro.backend.dto.MenuFlatDto;
import com.lubricentro.backend.dto.MenuItemDto;
import com.lubricentro.backend.entity.User;
import com.lubricentro.backend.repository.UserRepository;
import com.lubricentro.backend.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final MenuService menuService;
    private final UserRepository userRepository;

    public MenuController(MenuService menuService, UserRepository userRepository) {
        this.menuService = menuService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Get menu for authenticated user",
               description = "Returns filtered menu tree based on user permissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<MenuItemDto>> getMenuForUser(Authentication authentication) {
        User user = userRepository.findByUsernameAndIsActiveTrue(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(menuService.getMenuForUser(user));
    }

    @Operation(summary = "Get all menus (admin)", description = "Returns all menus as a flat list with all fields except created_at and updated_at")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All menus retrieved successfully")
    })
    @GetMapping("/all")
    public ResponseEntity<List<MenuFlatDto>> getAllMenus() {
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    @Operation(summary = "Get menu by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu found"),
            @ApiResponse(responseCode = "404", description = "Menu not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDto> getById(@PathVariable Long id) {
        return menuService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new menu item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu created successfully")
    })
    @PostMapping
    public ResponseEntity<MenuItemDto> create(@Valid @RequestBody MenuCreateRequest request) {
        return ResponseEntity.ok(menuService.create(request));
    }

    @Operation(summary = "Update menu item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu updated successfully"),
            @ApiResponse(responseCode = "404", description = "Menu not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemDto> update(@PathVariable Long id, @Valid @RequestBody MenuCreateRequest request) {
        return menuService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete menu item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Menu deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Menu not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return menuService.delete(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }
}
