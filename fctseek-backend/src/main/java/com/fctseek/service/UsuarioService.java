package com.fctseek.service;

import com.fctseek.dto.response.UsuarioResponse;
import com.fctseek.exception.BadRequestException;
import com.fctseek.exception.ResourceNotFoundException;
import com.fctseek.model.Usuario;
import com.fctseek.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de usuarios.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Obtiene el usuario actual autenticado.
     */
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    /**
     * Obtiene el perfil del usuario actual.
     */
    public UsuarioResponse getMyProfile() {
        Usuario usuario = getCurrentUser();
        return UsuarioResponse.fromEntity(usuario);
    }

    /**
     * Obtiene un usuario por ID.
     */
    public UsuarioResponse getById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return UsuarioResponse.fromEntity(usuario);
    }

    /**
     * Lista todos los usuarios.
     */
    public List<UsuarioResponse> getAll() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista usuarios por rol.
     */
    public List<UsuarioResponse> getByRol(String rol) {
        return usuarioRepository.findByRolAndActivoTrue(rol).stream()
                .map(UsuarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista profesores de un departamento.
     */
    public List<UsuarioResponse> getProfesoresByDepartamento(Long departamentoId) {
        return usuarioRepository.findProfesoresByDepartamento(departamentoId).stream()
                .map(UsuarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el perfil del usuario actual.
     */
    @Transactional
    public UsuarioResponse updateMyProfile(String nombre, String apellidos) {
        Usuario usuario = getCurrentUser();
        
        if (nombre != null && !nombre.isBlank()) {
            usuario.setNombre(nombre);
        }
        if (apellidos != null && !apellidos.isBlank()) {
            usuario.setApellidos(apellidos);
        }
        
        usuario = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(usuario);
    }

    /**
     * Cambia la contraseña del usuario actual.
     */
    @Transactional
    public void changePassword(String currentPassword, String newPassword) {
        Usuario usuario = getCurrentUser();
        
        // Verificar contraseña actual
        if (!passwordEncoder.matches(currentPassword, usuario.getPasswordHash())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }
        
        // Validar nueva contraseña
        if (newPassword == null || newPassword.length() < 6) {
            throw new BadRequestException("La nueva contraseña debe tener al menos 6 caracteres");
        }
        
        // Actualizar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    /**
     * Desactiva un usuario (solo para administradores).
     */
    @Transactional
    public void deactivateUser(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Activa un usuario.
     */
    @Transactional
    public void activateUser(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }
}
