package com.arcana.afip.api;

public interface AfipAuthService {
    record Ta(String token, String sign, long cuit, java.time.OffsetDateTime expiration) {}

    Ta getValidTa();
}