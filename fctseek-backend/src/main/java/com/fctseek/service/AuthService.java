package com.fctseek.service;

import com.fctseek.dto.request.LoginRequest;
import com.fctseek.dto.request.RegisterRequest;
import com.fctseek.dto.response.AuthResponse;
import com.fctseek.exception.BadRequestException;
import com.fctseek.exception.UnauthorizedException;
import com.fctseek.model.Departamento;
import com.fctseek.model.Usuario;
import com.fctseek.repository.DepartamentoRepository;
import com.fctseek.repository.UsuarioRepository;
import com.fctseek.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para autenticación y registro de usuarios.
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       DepartamentoRepository departamentoRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.departamentoRepository = departamentoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Autentica un usuario y devuelve el token JWT.
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // Autenticar con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generar token
            String token = jwtTokenProvider.generateToken(authentication);

            // Obtener usuario
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Usuario no encontrado"));

            // Construir respuesta
            AuthResponse.Builder responseBuilder = AuthResponse.builder()
                    .token(token)
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

            return responseBuilder.build();

        } catch (Exception e) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
    }

    /**
     * Registra un nuevo usuario.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Verificar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        // Verificar que el NIF no exista
        if (usuarioRepository.existsByNif(request.getNif())) {
            throw new BadRequestException("El NIF ya está registrado");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setNif(request.getNif().toUpperCase());
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setRol(request.getRol());
        usuario.setActivo(true);

        // Asignar departamento si se proporciona
        if (request.getDepartamentoId() != null) {
            Departamento departamento = departamentoRepository.findById(request.getDepartamentoId())
                    .orElseThrow(() -> new BadRequestException("Departamento no encontrado"));
            usuario.setDepartamento(departamento);
        }

        // Guardar usuario
        usuario = usuarioRepository.save(usuario);

        // Generar token
        String token = jwtTokenProvider.generateTokenFromEmail(usuario.getEmail());

        // Construir respuesta
        AuthResponse.Builder responseBuilder = AuthResponse.builder()
                .token(token)
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

        return responseBuilder.build();
    }

    /**
     * Obtiene el usuario actual autenticado.
     */
    public Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuario no autenticado"));
    }
}
