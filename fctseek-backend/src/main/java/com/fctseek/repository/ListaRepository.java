package com.fctseek.repository;

import com.fctseek.model.Lista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Lista.
 */
@Repository
public interface ListaRepository extends JpaRepository<Lista, Long> {

    /**
     * Busca listas de un usuario.
     */
    List<Lista> findByUsuarioId(Long usuarioId);

    /**
     * Busca listas de un usuario ordenadas por fecha de creación.
     */
    List<Lista> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);

    /**
     * Busca la lista de favoritos de un usuario.
     */
    Optional<Lista> findByUsuarioIdAndEsFavoritosTrue(Long usuarioId);

    /**
     * Busca listas normales (no favoritos) de un usuario.
     */
    List<Lista> findByUsuarioIdAndEsFavoritosFalse(Long usuarioId);

    /**
     * Busca una lista por usuario y nombre.
     */
    Optional<Lista> findByUsuarioIdAndNombre(Long usuarioId, String nombre);

    /**
     * Verifica si existe una lista con ese nombre para el usuario.
     */
    boolean existsByUsuarioIdAndNombre(Long usuarioId, String nombre);

    /**
     * Cuenta listas de un usuario.
     */
    long countByUsuarioId(Long usuarioId);

    /**
     * Busca listas que contienen una empresa específica.
     */
    @Query("SELECT l FROM Lista l JOIN l.listaEmpresas le WHERE le.empresa.id = :empresaId AND l.usuario.id = :usuarioId")
    List<Lista> findListasConEmpresa(@Param("usuarioId") Long usuarioId, @Param("empresaId") Long empresaId);
}
