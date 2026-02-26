package com.fctseek.repository;

import com.fctseek.model.Plaza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Plaza.
 */
@Repository
public interface PlazaRepository extends JpaRepository<Plaza, Long> {

    /**
     * Busca plazas de una empresa.
     */
    List<Plaza> findByEmpresaId(Long empresaId);

    /**
     * Busca plazas de un departamento.
     */
    List<Plaza> findByDepartamentoId(Long departamentoId);

    /**
     * Busca plazas de un curso académico.
     */
    List<Plaza> findByCursoAcademico(String cursoAcademico);

    /**
     * Busca plazas de una empresa para un departamento.
     */
    List<Plaza> findByEmpresaIdAndDepartamentoId(Long empresaId, Long departamentoId);

    /**
     * Busca plazas de una empresa para un curso académico.
     */
    List<Plaza> findByEmpresaIdAndCursoAcademico(Long empresaId, String cursoAcademico);

    /**
     * Busca plazas de un departamento en un curso académico.
     */
    List<Plaza> findByDepartamentoIdAndCursoAcademico(Long departamentoId, String cursoAcademico);

    /**
     * Busca una plaza específica.
     */
    Optional<Plaza> findByEmpresaIdAndDepartamentoIdAndCursoIdAndCursoAcademico(
            Long empresaId, Long departamentoId, Long cursoId, String cursoAcademico);

    /**
     * Busca plazas generales (sin curso específico) de una empresa para un departamento.
     */
    @Query("SELECT p FROM Plaza p WHERE p.empresa.id = :empresaId AND p.departamento.id = :deptoId " +
           "AND p.esGeneral = true AND p.cursoAcademico = :cursoAcademico")
    Optional<Plaza> findPlazaGeneral(@Param("empresaId") Long empresaId,
                                     @Param("deptoId") Long departamentoId,
                                     @Param("cursoAcademico") String cursoAcademico);

    /**
     * Cuenta total de plazas por departamento y curso académico.
     */
    @Query("SELECT COALESCE(SUM(p.cantidad), 0) FROM Plaza p WHERE p.departamento.id = :deptoId " +
           "AND p.cursoAcademico = :cursoAcademico")
    Integer countTotalPlazasByDepartamento(@Param("deptoId") Long departamentoId,
                                           @Param("cursoAcademico") String cursoAcademico);

    /**
     * Busca plazas con disponibilidad (cantidad > reservadas).
     */
    @Query("SELECT p FROM Plaza p WHERE p.departamento.id = :deptoId AND p.cursoAcademico = :cursoAcademico " +
           "AND p.cantidad > (SELECT COALESCE(SUM(r.cantidad), 0) FROM Reserva r WHERE r.plaza = p AND r.estado != 'CANCELADA')")
    List<Plaza> findPlazasDisponibles(@Param("deptoId") Long departamentoId,
                                      @Param("cursoAcademico") String cursoAcademico);
}
