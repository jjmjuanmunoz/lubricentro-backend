package com.lubricentro.backend.dto;

public record TokenResponse(String accessToken, String refreshToken, String tokenType) {}