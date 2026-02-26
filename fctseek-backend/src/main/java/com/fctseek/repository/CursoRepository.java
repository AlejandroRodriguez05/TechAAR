package com.fctseek.repository;

import com.fctseek.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Curso.
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    /**
     * Busca un curso por su codigo.
     */
    Optional<Curso> findByCodigo(String codigo);

    /**
     * Busca un curso por sus siglas.
     */
    Optional<Curso> findBySiglas(String siglas);

    /**
     * Busca cursos por departamento.
     */
    List<Curso> findByDepartamentoId(Long departamentoId);

    /**
     * Busca cursos activos por departamento.
     */
    List<Curso> findByDepartamentoIdAndActivoTrue(Long departamentoId);

    /**
     * Busca cursos por grado.
     */
    List<Curso> findByGrado(String grado);

    /**
     * Busca cursos activos por grado.
     */
    List<Curso> findByGradoAndActivoTrue(String grado);

    /**
     * Busca todos los cursos activos.
     */
    List<Curso> findByActivoTrue();

    /**
     * Verifica si existe un curso con ese codigo.
     */
    boolean existsByCodigo(String codigo);

    /**
     * Busca cursos que estan asociados a una empresa especifica.
     */
    @Query("SELECT c FROM Curso c JOIN EmpresaCurso ec ON c.id = ec.curso.id WHERE ec.empresa.id = :empresaId")
    List<Curso> findByEmpresaId(@Param("empresaId") Long empresaId);
}
