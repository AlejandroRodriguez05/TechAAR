package com.fctseek.repository;

import com.fctseek.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Valoracion.
 */
@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    /**
     * Busca valoraciones de una empresa.
     */
    List<Valoracion> findByEmpresaId(Long empresaId);

    /**
     * Busca valoraciones de un usuario.
     */
    List<Valoracion> findByUsuarioId(Long usuarioId);

    /**
     * Busca la valoración de un usuario a una empresa.
     */
    Optional<Valoracion> findByEmpresaIdAndUsuarioId(Long empresaId, Long usuarioId);

    /**
     * Verifica si un usuario ya valoró una empresa.
     */
    boolean existsByEmpresaIdAndUsuarioId(Long empresaId, Long usuarioId);

    /**
     * Calcula la valoración media de una empresa.
     */
    @Query("SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.empresa.id = :empresaId")
    Double getValoracionMedia(@Param("empresaId") Long empresaId);

    /**
     * Cuenta valoraciones de una empresa.
     */
    long countByEmpresaId(Long empresaId);

    /**
     * Obtiene estadísticas de valoraciones de una empresa.
     */
    @Query("SELECT v.puntuacion, COUNT(v) FROM Valoracion v WHERE v.empresa.id = :empresaId GROUP BY v.puntuacion ORDER BY v.puntuacion")
    List<Object[]> getEstadisticasValoraciones(@Param("empresaId") Long empresaId);
}
