package com.lubricentro.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final Key key;
    private final long accessSeconds;

    public JwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.access-minutes}") long accessMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessSeconds = accessMinutes * 60L;
    }

    public String generateAccessToken(UserDetails principal, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(accessSeconds);
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(principal.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String jwt) {
        return parseAllClaims(jwt).getSubject();
    }

    public boolean isValid(String jwt, UserDetails principal) {
        final String username = extractUsername(jwt);
        return username.equals(principal.getUsername()) && !isExpired(jwt);
    }

    private boolean isExpired(String jwt) {
        Date exp = parseAllClaims(jwt).getExpiration();
        return exp.before(new Date());
    }

    private Claims parseAllClaims(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }
}