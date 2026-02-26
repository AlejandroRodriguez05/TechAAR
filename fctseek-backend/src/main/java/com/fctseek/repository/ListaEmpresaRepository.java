package com.fctseek.repository;

import com.fctseek.model.ListaEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de ListaEmpresa.
 */
@Repository
public interface ListaEmpresaRepository extends JpaRepository<ListaEmpresa, Long> {

    /**
     * Busca empresas de una lista.
     */
    List<ListaEmpresa> findByListaId(Long listaId);

    /**
     * Busca empresas de una lista ordenadas por fecha de creación.
     */
    List<ListaEmpresa> findByListaIdOrderByCreatedAtDesc(Long listaId);

    /**
     * Busca una relación específica lista-empresa.
     */
    Optional<ListaEmpresa> findByListaIdAndEmpresaId(Long listaId, Long empresaId);

    /**
     * Verifica si una empresa está en una lista.
     */
    boolean existsByListaIdAndEmpresaId(Long listaId, Long empresaId);

    /**
     * Elimina una empresa de una lista.
     */
    @Modifying
    void deleteByListaIdAndEmpresaId(Long listaId, Long empresaId);

    /**
     * Elimina todas las empresas de una lista.
     */
    @Modifying
    void deleteByListaId(Long listaId);

    /**
     * Cuenta empresas en una lista.
     */
    long countByListaId(Long listaId);
}
