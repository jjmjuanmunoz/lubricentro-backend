package com.lubricentro.backend.security;

import com.lubricentro.backend.entity.RefreshToken;
import com.lubricentro.backend.entity.User;
import com.lubricentro.backend.repository.RefreshTokenRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final long refreshDays;

    public RefreshTokenService(RefreshTokenRepository repo,
                               @Value("${app.security.jwt.refresh-days}") long refreshDays) {
        this.repo = repo;
        this.refreshDays = refreshDays;
    }

    @Transactional
    public RefreshToken issue(User user) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plus(refreshDays, ChronoUnit.DAYS))
                .revoked(false)
                .createdAt(Instant.now())
                .build();
        return repo.save(token);
    }

    @Transactional
    public Optional<RefreshToken> rotateIfValid(String refreshToken) {
        return repo.findByToken(refreshToken)
                .filter(rt -> !rt.getRevoked())
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .map(rt -> {
                    rt.setRevoked(true);
                    repo.save(rt);
                    return issue(rt.getUser());
                });
    }

    @Transactional
    public void revoke(String refreshToken) {
        repo.findByToken(refreshToken).ifPresent(rt -> {
            rt.setRevoked(true);
            repo.save(rt);
        });
    }
}