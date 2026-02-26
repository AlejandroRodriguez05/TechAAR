package com.fctseek.repository;

import com.fctseek.model.EmpresaContactada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de EmpresaContactada.
 */
@Repository
public interface EmpresaContactadaRepository extends JpaRepository<EmpresaContactada, Long> {

    /**
     * Busca contactos de una empresa.
     */
    List<EmpresaContactada> findByEmpresaId(Long empresaId);

    /**
     * Busca contactos de un departamento.
     */
    List<EmpresaContactada> findByDepartamentoId(Long departamentoId);

    /**
     * Busca contactos realizados por un profesor.
     */
    List<EmpresaContactada> findByProfesorId(Long profesorId);

    /**
     * Busca un contacto especifico entre empresa y departamento.
     */
    Optional<EmpresaContactada> findByEmpresaIdAndDepartamentoId(Long empresaId, Long departamentoId);

    /**
     * Verifica si una empresa ha sido contactada por un departamento.
     */
    boolean existsByEmpresaIdAndDepartamentoId(Long empresaId, Long departamentoId);

    /**
     * Busca contactos de un departamento ordenados por fecha descendente.
     */
    @Query("SELECT ec FROM EmpresaContactada ec WHERE ec.departamento.id = :deptoId ORDER BY ec.fecha DESC")
    List<EmpresaContactada> findByDepartamentoIdOrderByFechaDesc(@Param("deptoId") Long departamentoId);
}
