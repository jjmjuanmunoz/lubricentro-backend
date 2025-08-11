package com.lubricentro.backend.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;

class BcryptGeneratorTest {

    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Test
    @DisplayName("Print BCrypt hash for provided password (use -Dpassword=yourPwd)")
    void printBcryptHash() {
        String raw = "admin123";
        if (raw == null || raw.isBlank()) {
            System.out.println(">> No password provided. Pass it with -Dpassword=yourPwd");
            return;
        }
        String hash = passwordEncoder().encode(raw);
        System.out.println("==== BCrypt hash (copy into your Flyway seed) ====");
        System.out.println(hash);
        System.out.println("=================================================");
    }

    @Test
    @DisplayName("Verify BCrypt hash matches (use -Dpassword=raw -Dhash=$2a...)")
    void verifyBcryptHash() {
        String raw = "admin123";
        String hash = "$2a$10$aMa9LkA5AgqWjkOvfL6cFeZMJ.mZ/x8DbzOYQfJBBrrLxzeTFkcMe";
        if (raw == null || raw.isBlank() || hash == null || hash.isBlank()) {
            System.out.println(">> Provide both -Dpassword=raw and -Dhash=$2a...");
            return;
        }
        boolean ok = passwordEncoder().matches(raw, hash);
        System.out.println("BCrypt matches? " + ok);
        assert ok;
    }

    @Test
    @DisplayName("Generate a secure random JWT secret (Base64-encoded, 256 bits)")
    void generateJwtSecret() {
        // 256 bits = 32 bytes
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);

        String base64Key = Base64.getEncoder().encodeToString(key);

        System.out.println("==== JWT Secret (Base64) ====");
        System.out.println(base64Key);
        System.out.println("=============================");
    }
}