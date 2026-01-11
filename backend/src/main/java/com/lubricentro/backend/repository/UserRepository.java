package com.lubricentro.backend.repository;

import com.lubricentro.backend.entity.TipoDocumento;
import com.lubricentro.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsernameAndIsActiveTrue(String username);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    Optional<User> findByNumeroDocumentoAndIsActiveTrue(String numeroDocumento);

    Optional<User> findByTipoDocumentoAndNumeroDocumentoAndIsActiveTrue(
            TipoDocumento tipoDocumento, String numeroDocumento);

    @Query("SELECT u FROM User u WHERE u.isActive = true " +
           "AND (:nombre IS NULL OR LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:apellido IS NULL OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :apellido, '%'))) " +
           "AND (:numeroDocumento IS NULL OR u.numeroDocumento LIKE CONCAT('%', :numeroDocumento, '%'))")
    List<User> searchUsers(
            @Param("nombre") String nombre,
            @Param("apellido") String apellido,
            @Param("numeroDocumento") String numeroDocumento);

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}