package com.lubricentro.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "service_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "automobile_id", nullable = false)
    private Automobile automobile;

    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "current_km", nullable = false)
    private Integer currentKm;

    // Aceite
    @Column(name = "oil_brand")
    private String oilBrand;

    @Column(name = "oil_name")
    private String oilName;

    // Filtros
    @Column(name = "oil_filter")
    private Boolean oilFilter;

    @Column(name = "air_filter")
    private String airFilter; // valores: "NO", "SI", "LIMPIEZA"

    @Column(name = "fuel_filter")
    private Boolean fuelFilter;

    // Otros
    @Column(name = "gearbox_oil")
    private Boolean gearboxOil;

    @Column(name = "differential_oil")
    private Boolean differentialOil;

    @Column(name = "next_service_km")
    private Integer nextServiceKm;
}