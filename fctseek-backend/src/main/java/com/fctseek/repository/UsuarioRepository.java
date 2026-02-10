package com.fctseek.repository;

import com.fctseek.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para operaciones CRUD de Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su email.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por su NIF.
     */
    Optional<Usuario> findByNif(String nif);

    /**
     * Verifica si existe un usuario con ese email.
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con el NIF dado.
     */
    boolean existsByNif(String nif);

    /**
     * Busca usuarios por rol.
     */
    List<Usuario> findByRol(String rol);

    /**
     * Busca usuarios activos por rol.
     */
    List<Usuario> findByRolAndActivoTrue(String rol);

    /**
     * Busca usuarios por departamento.
     */
    List<Usuario> findByDepartamentoId(Long departamentoId);

    /**
     * Busca profesores activos de un departamento.
     */
    @Query("SELECT u FROM Usuario u WHERE u.departamento.id = :deptoId AND u.rol = 'PROFESOR' AND u.activo = true")
    List<Usuario> findProfesoresByDepartamento(@Param("deptoId") Long departamentoId);

    /**
     * Busca usuarios cuyo nombre o apellidos contengan el texto.
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Usuario> buscarPorNombreOApellidos(@Param("texto") String texto);
}
