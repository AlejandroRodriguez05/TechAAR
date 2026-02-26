package com.fctseek.repository;

import com.fctseek.model.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Favorito.
 */
@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    /**
     * Busca favoritos de un usuario.
     */
    List<Favorito> findByUsuarioId(Long usuarioId);

    /**
     * Busca un favorito específico usuario-empresa.
     */
    Optional<Favorito> findByUsuarioIdAndEmpresaId(Long usuarioId, Long empresaId);

    /**
     * Verifica si una empresa es favorita de un usuario.
     */
    boolean existsByUsuarioIdAndEmpresaId(Long usuarioId, Long empresaId);

    /**
     * Elimina un favorito específico.
     */
    @Modifying
    void deleteByUsuarioIdAndEmpresaId(Long usuarioId, Long empresaId);

    /**
     * Cuenta favoritos de un usuario.
     */
    long countByUsuarioId(Long usuarioId);

    /**
     * Cuenta cuántos usuarios tienen una empresa como favorita.
     */
    long countByEmpresaId(Long empresaId);

    /**
     * Busca favoritos de un usuario ordenados por fecha de creación.
     */
    @Query("SELECT f FROM Favorito f WHERE f.usuario.id = :usuarioId ORDER BY f.createdAt DESC")
    List<Favorito> findByUsuarioIdOrderByCreatedAtDesc(@Param("usuarioId") Long usuarioId);
}
