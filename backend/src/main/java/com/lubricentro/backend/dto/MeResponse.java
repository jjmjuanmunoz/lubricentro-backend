package com.lubricentro.backend.dto;

import java.util.Set;

public record MeResponse(Long id, String username, String email, String fullName, Set<String> roles) {}