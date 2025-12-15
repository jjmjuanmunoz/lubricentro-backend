package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.Turn;
import com.lubricentro.backend.entity.TurnStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TurnRepository extends JpaRepository<Turn, Long> {

    List<Turn> findByStatusOrderByArrivalTimeAsc(TurnStatus status);

    @Query("SELECT t FROM Turn t WHERE DATE(t.arrivalTime) = :date ORDER BY t.arrivalTime ASC")
    List<Turn> findByArrivalDate(@Param("date") LocalDate date);

    @Query("SELECT COALESCE(MAX(t.turnNumber), 0) FROM Turn t WHERE DATE(t.arrivalTime) = :date")
    Integer findMaxTurnNumberByDate(@Param("date") LocalDate date);
}
