package com.fctseek.repository;

import com.fctseek.model.EmpresaCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de EmpresaCurso.
 */
@Repository
public interface EmpresaCursoRepository extends JpaRepository<EmpresaCurso, Long> {

    /**
     * Busca relaciones por id de empresa.
     */
    List<EmpresaCurso> findByEmpresaId(Long empresaId);

    /**
     * Busca relaciones por id de curso.
     */
    List<EmpresaCurso> findByCursoId(Long cursoId);

    /**
     * Busca una relacion especifica entre empresa y curso.
     */
    Optional<EmpresaCurso> findByEmpresaIdAndCursoId(Long empresaId, Long cursoId);

    /**
     * Verifica si existe una relacion entre empresa y curso.
     */
    boolean existsByEmpresaIdAndCursoId(Long empresaId, Long cursoId);

    /**
     * Elimina todas las relaciones de una empresa.
     */
    @Modifying
    @Query("DELETE FROM EmpresaCurso ec WHERE ec.empresa.id = :empresaId")
    void deleteByEmpresaId(@Param("empresaId") Long empresaId);

    /**
     * Elimina una relacion especifica entre una empresa y un curso.
     */
    @Modifying
    void deleteByEmpresaIdAndCursoId(Long empresaId, Long cursoId);
}
