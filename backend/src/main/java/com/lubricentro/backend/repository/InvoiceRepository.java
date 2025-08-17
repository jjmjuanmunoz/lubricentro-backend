package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    int countByPos_IdAndCbteTypeCode(Long posId, Integer cbteTypeCode);

    Invoice findTopByPos_IdAndCbteTypeCodeOrderByCbteNumberDesc(Long posId, Integer cbteTypeCode);
}