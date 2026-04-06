package com.fctseek.repository;

import com.fctseek.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Empresa.
 */
@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    /**
     * Busca una empresa por su CIF.
     */
    Optional<Empresa> findByCif(String cif);

    /**
     * Verifica si existe una empresa con el CIF dado.
     */
    boolean existsByCif(String cif);

    /**
     * Busca empresas activas.
     */
    List<Empresa> findByActivaTrue();

    /**
     * Busca empresas por ciudad.
     */
    List<Empresa> findByCiudadIgnoreCase(String ciudad);

    /**
     * Busca empresas activas por ciudad.
     */
    List<Empresa> findByCiudadIgnoreCaseAndActivaTrue(String ciudad);

    /**
     * Busca empresas cuyo nombre contenga el texto dado.
     */
    List<Empresa> findByNombreContainingIgnoreCaseAndActivaTrue(String nombre);

    /**
     * Busqueda por nombre o ciudad.
     */
    @Query("SELECT e FROM Empresa e WHERE e.activa = true AND " +
           "(LOWER(e.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(e.ciudad) LIKE LOWER(CONCAT('%', :texto, '%')))")
    List<Empresa> buscarPorTexto(@Param("texto") String texto);

    /**
     * Busca empresas que estan asociadas a un curso especifico.
     */
    @Query("SELECT DISTINCT e FROM Empresa e JOIN e.empresaCursos ec WHERE ec.curso.id = :cursoId AND e.activa = true")
    List<Empresa> findByCursoId(@Param("cursoId") Long cursoId);

    /**
     * Busca empresas que han sido contactadas por un departamento.
     */
    @Query("SELECT DISTINCT e FROM Empresa e JOIN e.contactos c WHERE c.departamento.id = :deptoId AND e.activa = true")
    List<Empresa> findContactadasByDepartamento(@Param("deptoId") Long departamentoId);

    /**
     * Busca empresas con plazas disponibles para un departamento en un año academico.
     */
    @Query("SELECT DISTINCT e FROM Empresa e JOIN e.plazas p WHERE p.departamento.id = :deptoId " +
           "AND p.cursoAcademico = :cursoAcademico AND e.activa = true")
    List<Empresa> findConPlazasByDepartamentoAndCursoAcademico(
            @Param("deptoId") Long departamentoId,
            @Param("cursoAcademico") String cursoAcademico);

    /**
     * Obtiene empresas favoritas de un usuario.
     */
    @Query("SELECT e FROM Empresa e JOIN Favorito f ON e.id = f.empresa.id WHERE f.usuario.id = :usuarioId")
    List<Empresa> findFavoritasByUsuario(@Param("usuarioId") Long usuarioId);

    /**
     * Cuenta empresas hay activas.
     */
    long countByActivaTrue();
    
    /**
     * Carga empresa con cursos, departamentos y contactos en UNA sola query.
     */
    @Query("SELECT DISTINCT e FROM Empresa e " +
           "LEFT JOIN FETCH e.empresaCursos ec " +
           "LEFT JOIN FETCH ec.curso c " +
           "LEFT JOIN FETCH c.departamento " +
           "WHERE e.id = :id")
    Optional<Empresa> findByIdWithDetails(@Param("id") Long id);
}
