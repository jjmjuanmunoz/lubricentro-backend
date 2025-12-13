package com.lubricentro.backend.service;

import com.lubricentro.backend.dto.MenuCreateRequest;
import com.lubricentro.backend.dto.MenuItemDto;
import com.lubricentro.backend.entity.Menu;
import com.lubricentro.backend.entity.User;
import com.lubricentro.backend.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final PermissionService permissionService;

    public MenuService(MenuRepository menuRepository, PermissionService permissionService) {
        this.menuRepository = menuRepository;
        this.permissionService = permissionService;
    }

    /**
     * Get filtered menu tree for authenticated user based on permissions
     * Recursive filtering: exclude collapsibles/sections with no visible children
     */
    public List<MenuItemDto> getMenuForUser(User user) {
        Set<String> userPermissions = permissionService.getUserPermissions(user);
        List<Menu> rootMenus = menuRepository.findRootMenusWithChildren();

        return rootMenus.stream()
                .map(menu -> buildMenuItemDto(menu, userPermissions))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Recursively build MenuItemDto with permission filtering
     * Returns Optional.empty() if menu should be hidden
     */
    private Optional<MenuItemDto> buildMenuItemDto(Menu menu, Set<String> userPermissions) {
        // Check if user has permission for this menu item (null permission = visible to all)
        boolean hasPermission = menu.getPermissionCode() == null
                || userPermissions.contains(menu.getPermissionCode());

        if (!hasPermission) {
            return Optional.empty();
        }

        // Recursively filter children
        List<MenuItemDto> visibleChildren = menu.getChildren().stream()
                .map(child -> buildMenuItemDto(child, userPermissions))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        // If this is a collapsible or section with no visible children, hide it
        if (("collapsible".equals(menu.getType()) || "section".equals(menu.getType()))
                && visibleChildren.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new MenuItemDto(
                menu.getId(),
                menu.getTitle(),
                menu.getUrl(),
                menu.getType(),
                menu.getIcon(),
                menu.getPermissionCode(),
                visibleChildren
        ));
    }

    public List<MenuItemDto> getAllMenus() {
        List<Menu> rootMenus = menuRepository.findRootMenusWithChildren();
        return rootMenus.stream()
                .map(this::mapToDto)
                .toList();
    }

    public Optional<MenuItemDto> getById(Long id) {
        return menuRepository.findById(id).map(this::mapToDto);
    }

    @Transactional
    public MenuItemDto create(MenuCreateRequest request) {
        Menu menu = Menu.builder()
                .title(request.title())
                .url(request.url())
                .type(request.type())
                .icon(request.icon())
                .permissionCode(request.permissionCode())
                .displayOrder(request.displayOrder())
                .build();

        if (request.parentId() != null) {
            menuRepository.findById(request.parentId())
                    .ifPresent(menu::setParent);
        }

        return mapToDto(menuRepository.save(menu));
    }

    @Transactional
    public boolean delete(Long id) {
        if (menuRepository.existsById(id)) {
            menuRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private MenuItemDto mapToDto(Menu entity) {
        List<MenuItemDto> children = entity.getChildren().stream()
                .map(this::mapToDto)
                .toList();

        return new MenuItemDto(
                entity.getId(),
                entity.getTitle(),
                entity.getUrl(),
                entity.getType(),
                entity.getIcon(),
                entity.getPermissionCode(),
                children
        );
    }
}
