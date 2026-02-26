package com.fctseek.controller;

import com.fctseek.dto.request.LoginRequest;
import com.fctseek.dto.request.RegisterRequest;
import com.fctseek.dto.response.ApiResponse;
import com.fctseek.dto.response.AuthResponse;
import com.fctseek.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticación de usuarios.
 * Endpoints públicos: login y registro.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/login
     * Autentica un usuario y devuelve un token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/register
     * Registra un nuevo usuario.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/auth/me
     * Obtiene los datos del usuario autenticado actual.
     */
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> me() {
        var usuario = authService.getCurrentUser();
        AuthResponse.Builder responseBuilder = AuthResponse.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .apellidos(usuario.getApellidos())
                .nif(usuario.getNif())
                .rol(usuario.getRol());

        if (usuario.getDepartamento() != null) {
            responseBuilder
                .departamentoId(usuario.getDepartamento().getId())
                .departamentoNombre(usuario.getDepartamento().getNombre());
        }

        return ResponseEntity.ok(responseBuilder.build());
    }

    /**
     * POST /api/auth/verify
     * Verifica si el token actual es válido.
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verify() {
        // Si llegamos aquí, el token es válido (el filtro JWT ya lo verificó)
        return ResponseEntity.ok(ApiResponse.ok("Token válido"));
    }
}
