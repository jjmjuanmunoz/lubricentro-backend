package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("SELECT m FROM Menu m WHERE m.parent IS NULL ORDER BY m.displayOrder ASC")
    List<Menu> findRootMenus();

    List<Menu> findByParentIdOrderByDisplayOrder(Long parentId);

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.children WHERE m.parent IS NULL ORDER BY m.displayOrder ASC")
    List<Menu> findRootMenusWithChildren();

    @Query("SELECT m FROM Menu m ORDER BY m.displayOrder ASC")
    List<Menu> findAllOrdered();
}
