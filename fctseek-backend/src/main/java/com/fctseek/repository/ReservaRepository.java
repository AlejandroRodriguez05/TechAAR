package com.fctseek.repository;

import com.fctseek.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para operaciones CRUD de Reserva.
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    /**
     * Busca reservas de una plaza.
     */
    List<Reserva> findByPlazaId(Long plazaId);

    /**
     * Busca reservas de un profesor.
     */
    List<Reserva> findByProfesorId(Long profesorId);

    /**
     * Busca reservas de un curso.
     */
    List<Reserva> findByCursoId(Long cursoId);

    /**
     * Busca reservas por estado.
     */
    List<Reserva> findByEstado(String estado);

    /**
     * Busca reservas de un profesor por estado.
     */
    List<Reserva> findByProfesorIdAndEstado(Long profesorId, String estado);

    /**
     * Busca reservas activas (no canceladas) de una plaza.
     */
    @Query("SELECT r FROM Reserva r WHERE r.plaza.id = :plazaId AND r.estado != 'CANCELADA'")
    List<Reserva> findReservasActivasByPlaza(@Param("plazaId") Long plazaId);

    /**
     * Cuenta plazas reservadas (no canceladas) de una plaza.
     */
    @Query("SELECT COALESCE(SUM(r.cantidad), 0) FROM Reserva r WHERE r.plaza.id = :plazaId AND r.estado != 'CANCELADA'")
    Integer countPlazasReservadas(@Param("plazaId") Long plazaId);

    /**
     * Busca reservas de una empresa.
     */
    @Query("SELECT r FROM Reserva r WHERE r.plaza.empresa.id = :empresaId")
    List<Reserva> findByEmpresaId(@Param("empresaId") Long empresaId);

    /**
     * Busca reservas de una empresa para un departamento.
     */
    @Query("SELECT r FROM Reserva r WHERE r.plaza.empresa.id = :empresaId AND r.plaza.departamento.id = :deptoId")
    List<Reserva> findByEmpresaIdAndDepartamentoId(@Param("empresaId") Long empresaId,
                                                    @Param("deptoId") Long departamentoId);

    /**
     * Busca reservas de un departamento en un curso académico.
     */
    @Query("SELECT r FROM Reserva r WHERE r.plaza.departamento.id = :deptoId " +
           "AND r.plaza.cursoAcademico = :cursoAcademico ORDER BY r.createdAt DESC")
    List<Reserva> findByDepartamentoAndCursoAcademico(@Param("deptoId") Long departamentoId,
                                                      @Param("cursoAcademico") String cursoAcademico);
}
