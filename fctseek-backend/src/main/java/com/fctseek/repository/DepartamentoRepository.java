package com.fctseek.repository;

import com.fctseek.model.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Departamento.
 */
@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

    /**
     * Busca un departamento por su codigo.
     */
    Optional<Departamento> findByCodigo(String codigo);

    /**
     * Verifica si existe un departamento con ese codigo.
     */
    boolean existsByCodigo(String codigo);

    /**
     * Busca un departamento por nombre ignorando mayusculas y minusculas.
     */
    Optional<Departamento> findByNombreIgnoreCase(String nombre);
}
