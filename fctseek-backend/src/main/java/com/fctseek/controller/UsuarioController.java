package com.fctseek.controller;

import com.fctseek.dto.response.ApiResponse;
import com.fctseek.dto.response.UsuarioResponse;
import com.fctseek.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para gestión de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * GET /api/usuarios/me
     * Obtiene el perfil del usuario actual.
     */
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> getMyProfile() {
        return ResponseEntity.ok(usuarioService.getMyProfile());
    }

    /**
     * PUT /api/usuarios/me
     * Actualiza el perfil del usuario actual.
     */
    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> updateMyProfile(@RequestBody Map<String, String> request) {
        String nombre = request.get("nombre");
        String apellidos = request.get("apellidos");
        return ResponseEntity.ok(usuarioService.updateMyProfile(nombre, apellidos));
    }

    /**
     * PUT /api/usuarios/me/password
     * Cambia la contraseña del usuario actual.
     */
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody Map<String, String> request) {
        String currentPassword = request.get("currentPassword");
        String newPassword = request.get("newPassword");
        usuarioService.changePassword(currentPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.ok("Contraseña actualizada correctamente"));
    }

    /**
     * GET /api/usuarios
     * Lista todos los usuarios (solo profesores).
     */
    @GetMapping
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<List<UsuarioResponse>> getAll() {
        return ResponseEntity.ok(usuarioService.getAll());
    }

    /**
     * GET /api/usuarios/{id}
     * Obtiene un usuario por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getById(id));
    }

    /**
     * GET /api/usuarios/rol/{rol}
     * Lista usuarios por rol.
     */
    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<List<UsuarioResponse>> getByRol(@PathVariable String rol) {
        return ResponseEntity.ok(usuarioService.getByRol(rol.toUpperCase()));
    }

    /**
     * GET /api/usuarios/departamento/{departamentoId}/profesores
     * Lista profesores de un departamento.
     */
    @GetMapping("/departamento/{departamentoId}/profesores")
    public ResponseEntity<List<UsuarioResponse>> getProfesoresByDepartamento(
            @PathVariable Long departamentoId) {
        return ResponseEntity.ok(usuarioService.getProfesoresByDepartamento(departamentoId));
    }
}
