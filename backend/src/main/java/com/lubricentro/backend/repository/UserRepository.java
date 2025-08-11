package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndIsActiveTrue(String username);
    Optional<User> findByEmailAndIsActiveTrue(String email);
}