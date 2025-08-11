package com.lubricentro.backend.controller;

import com.lubricentro.backend.dto.LoginRequest;
import com.lubricentro.backend.dto.MeResponse;
import com.lubricentro.backend.dto.RefreshRequest;
import com.lubricentro.backend.dto.TokenResponse;
import com.lubricentro.backend.entity.RefreshToken;
import com.lubricentro.backend.entity.User;
import com.lubricentro.backend.repository.UserRepository;
import com.lubricentro.backend.security.JwtService;
import com.lubricentro.backend.security.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authManager,
                          UserDetailsService userDetailsService,
                          UserRepository userRepository,
                          JwtService jwtService,
                          RefreshTokenService refreshTokenService) {
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Operation(summary = "Login", description = "Authenticate with username/password and receive access+refresh tokens.")
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserDetails principal = userDetailsService.loadUserByUsername(request.username());

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", principal.getAuthorities().iterator().next().getAuthority());

        String access = jwtService.generateAccessToken(principal, claims);
        User user = userRepository.findByUsernameAndIsActiveTrue(request.username()).orElseThrow();
        RefreshToken rt = refreshTokenService.issue(user);

        return ResponseEntity.ok(new TokenResponse(access, rt.getToken(), "Bearer"));
    }

    @Operation(summary = "Refresh", description = "Use a valid refresh token to obtain a new access+refresh pair (rotation).")
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        Optional<RefreshToken> rotated = refreshTokenService.rotateIfValid(request.refreshToken());
        if (rotated.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        User user = rotated.get().getUser();
        UserDetails principal = userDetailsService.loadUserByUsername(user.getUsername());

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", principal.getAuthorities().iterator().next().getAuthority());

        String access = jwtService.generateAccessToken(principal, claims);
        return ResponseEntity.ok(new TokenResponse(access, rotated.get().getToken(), "Bearer"));
    }

    @Operation(summary = "Logout", description = "Revoke a refresh token (cannot be used again).")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequest request) {
        refreshTokenService.revoke(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Me", description = "Return information about the authenticated user.")
    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@RequestHeader("Authorization") String authorization) {
        String token = authorization != null && authorization.startsWith("Bearer ") ? authorization.substring(7) : null;
        if (token == null) return ResponseEntity.status(401).build();

        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsernameAndIsActiveTrue(username).orElseThrow();
        return ResponseEntity.ok(new MeResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(), user.getRole()));
    }
}