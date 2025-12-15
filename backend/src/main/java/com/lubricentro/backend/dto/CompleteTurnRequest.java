package com.lubricentro.backend.dto;

import java.time.LocalDate;

public record CompleteTurnRequest(
    LocalDate serviceDate,
    Integer currentKm,
    String oilBrand,
    String oilName,
    Boolean oilFilter,
    String airFilter,
    Boolean fuelFilter,
    Boolean gearboxOil,
    Boolean differentialOil,
    Integer nextServiceKm
) {}
