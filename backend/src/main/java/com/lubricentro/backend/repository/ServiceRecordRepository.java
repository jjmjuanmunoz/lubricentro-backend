package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {

}
