package com.lubricentro.backend.security;

import com.lubricentro.backend.entity.User;
import com.lubricentro.backend.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public CustomUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = users.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Build authorities from all roles and their permissions
        Collection<GrantedAuthority> authorities = buildAuthorities(u);

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .authorities(authorities)
                .accountLocked(false)
                .disabled(Boolean.FALSE.equals(u.getIsActive()))
                .build();
    }

    /**
     * Build Spring Security authorities from user's roles and permissions
     * Returns both ROLE_ authorities and permission authorities
     */
    private Collection<GrantedAuthority> buildAuthorities(User user) {
        return user.getRoles().stream()
                .flatMap(role -> {
                    // Add role as authority (e.g., "ROLE_ADMIN")
                    var roleAuthority = new SimpleGrantedAuthority(role.getName());

                    // Add all permissions as authorities (e.g., "invoice:create")
                    var permissionAuthorities = role.getPermissions().stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
                            .toList();

                    return Stream.concat(
                            Stream.of(roleAuthority),
                            permissionAuthorities.stream()
                    );
                })
                .collect(Collectors.toSet());
    }
}