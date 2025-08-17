package com.lubricentro.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cbte_type_code", nullable = false)
    private Integer cbteTypeCode;

    @Column(name = "cbte_number")
    private Integer cbteNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pos_id", nullable = false)
    private AfipPos pos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "concept_code", nullable = false)
    private Integer conceptCode;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "currency_rate", nullable = false)
    private BigDecimal currencyRate;

    @Column(name = "net_amount", nullable = false)
    private BigDecimal netAmount;

    @Column(name = "iva_amount", nullable = false)
    private BigDecimal ivaAmount;

    @Column(name = "exempt_amount", nullable = false)
    private BigDecimal exemptAmount;

    @Column(name = "tributes_amount", nullable = false)
    private BigDecimal tributesAmount;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    private String cae;

    @Column(name = "cae_expiration")
    private LocalDate caeExpiration;

    @Column(name = "issued_on")
    private OffsetDateTime issuedOn;

    private String status;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceTribute> tributes = new ArrayList<>();
}