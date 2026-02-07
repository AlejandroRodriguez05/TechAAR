package com.fctseek.repository;

import com.fctseek.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para operaciones CRUD de Comentario.
 */
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    /**
     * Busca comentarios de una empresa.
     */
    List<Comentario> findByEmpresaId(Long empresaId);

    /**
     * Busca comentarios de un usuario.
     */
    List<Comentario> findByUsuarioId(Long usuarioId);

    /**
     * Busca comentarios públicos de una empresa.
     */
    List<Comentario> findByEmpresaIdAndEsPrivadoFalse(Long empresaId);

    /**
     * Busca comentarios de una empresa ordenados por fecha descendente.
     */
    List<Comentario> findByEmpresaIdOrderByCreatedAtDesc(Long empresaId);

    /**
     * Busca comentarios públicos de una empresa ordenados por fecha.
     */
    @Query("SELECT c FROM Comentario c WHERE c.empresa.id = :empresaId AND c.esPrivado = false ORDER BY c.createdAt DESC")
    List<Comentario> findComentariosPublicosByEmpresa(@Param("empresaId") Long empresaId);

    /**
     * Busca todos los comentarios de una empresa (públicos y privados) ordenados por fecha.
     * Para profesores que pueden ver los privados.
     */
    @Query("SELECT c FROM Comentario c WHERE c.empresa.id = :empresaId ORDER BY c.createdAt DESC")
    List<Comentario> findAllComentariosByEmpresa(@Param("empresaId") Long empresaId);

    /**
     * Cuenta comentarios de una empresa.
     */
    long countByEmpresaId(Long empresaId);

    /**
     * Cuenta comentarios públicos de una empresa.
     */
    long countByEmpresaIdAndEsPrivadoFalse(Long empresaId);
}
