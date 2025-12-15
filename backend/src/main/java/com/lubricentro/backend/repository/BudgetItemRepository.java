package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.BudgetItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {

    List<BudgetItem> findByTurnId(Long turnId);
}
