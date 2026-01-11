package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.EstadoFactura;
import com.lubricentro.backend.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    int countByPos_IdAndCbteTypeCode(Long posId, Integer cbteTypeCode);

    Invoice findTopByPos_IdAndCbteTypeCodeOrderByCbteNumberDesc(Long posId, Integer cbteTypeCode);

    // Buscar por usuario (cliente del sistema)
    List<Invoice> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Invoice> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Buscar por customer (cliente externo)
    List<Invoice> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    Page<Invoice> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    // Buscar por estado
    List<Invoice> findByStatusOrderByCreatedAtDesc(EstadoFactura status);

    Page<Invoice> findByStatusOrderByCreatedAtDesc(EstadoFactura status, Pageable pageable);

    // Buscar por punto de venta
    List<Invoice> findByPosIdOrderByCreatedAtDesc(Long posId);

    // Buscar por numero de comprobante (para verificar unicidad)
    Optional<Invoice> findByPosIdAndCbteTypeCodeAndCbteNumber(Long posId, Integer cbteTypeCode, Integer cbteNumber);

    // Buscar facturas con fetch de items para evitar N+1
    @Query("SELECT DISTINCT i FROM Invoice i " +
           "LEFT JOIN FETCH i.items " +
           "LEFT JOIN FETCH i.user " +
           "LEFT JOIN FETCH i.customer " +
           "LEFT JOIN FETCH i.pos " +
           "WHERE i.id = :id")
    Optional<Invoice> findByIdWithDetails(@Param("id") Long id);

    // Listar con paginacion y filtros
    @Query("SELECT i FROM Invoice i " +
           "WHERE (:userId IS NULL OR i.user.id = :userId) " +
           "AND (:customerId IS NULL OR i.customer.id = :customerId) " +
           "AND (:status IS NULL OR i.status = :status) " +
           "AND (:posId IS NULL OR i.pos.id = :posId) " +
           "ORDER BY i.createdAt DESC")
    Page<Invoice> findWithFilters(
            @Param("userId") Long userId,
            @Param("customerId") Long customerId,
            @Param("status") EstadoFactura status,
            @Param("posId") Long posId,
            Pageable pageable);
}