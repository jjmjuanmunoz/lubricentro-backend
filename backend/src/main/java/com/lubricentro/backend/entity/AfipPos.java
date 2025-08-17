package com.lubricentro.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "afip_pos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AfipPos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pos_number", nullable = false, unique = true)
    private Integer posNumber;

    @Column(name = "description")
    private String description;

    @Column(name = "cbte_type_scope", nullable = false)
    private String cbteTypeScope; // e.g. "A_B", "A_B_C"

    @Column(name = "homologation", nullable = false)
    private Boolean homologation;
}