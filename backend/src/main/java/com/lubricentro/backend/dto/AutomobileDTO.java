package com.lubricentro.backend.dto;

public record AutomobileDTO(
    Long id,
    String plate,
    String brand,
    String model,
    String ownerName
) {}
