package com.lubricentro.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoices", indexes = {
    @Index(name = "idx_invoices_user_id", columnList = "user_id"),
    @Index(name = "idx_invoices_customer_id", columnList = "customer_id"),
    @Index(name = "idx_invoices_cbte_number", columnList = "cbte_number")
})
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

    /**
     * Usuario del sistema como cliente.
     * Alternativa a customer para clientes registrados en el sistema.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private EstadoFactura status = EstadoFactura.BORRADOR;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InvoiceTribute> tributes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Obtiene el nombre del cliente (User o Customer).
     */
    public String getNombreCliente() {
        if (user != null) {
            if (user.getRazonSocial() != null && !user.getRazonSocial().isEmpty()) {
                return user.getRazonSocial();
            }
            return (user.getApellido() != null ? user.getApellido() : "") + " " +
                   (user.getNombre() != null ? user.getNombre() : "");
        }
        if (customer != null) {
            return customer.getName();
        }
        return "Consumidor Final";
    }

    /**
     * Obtiene el documento del cliente.
     */
    public String getDocumentoCliente() {
        if (user != null && user.getNumeroDocumento() != null) {
            return (user.getTipoDocumento() != null ? user.getTipoDocumento().name() : "DNI") +
                   ": " + user.getNumeroDocumento();
        }
        if (customer != null && customer.getDocNumber() != null) {
            return customer.getDocNumber();
        }
        return null;
    }

    /**
     * Agrega un item a la factura.
     */
    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
    }

    /**
     * Remueve un item de la factura.
     */
    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
    }

    /**
     * Agrega un tributo a la factura.
     */
    public void addTribute(InvoiceTribute tribute) {
        tributes.add(tribute);
        tribute.setInvoice(this);
    }

    /**
     * Verifica si la factura puede ser modificada.
     */
    public boolean isEditable() {
        return status == EstadoFactura.BORRADOR;
    }

    /**
     * Verifica si la factura puede ser emitida.
     */
    public boolean canBeIssued() {
        return status == EstadoFactura.BORRADOR && !items.isEmpty();
    }


}