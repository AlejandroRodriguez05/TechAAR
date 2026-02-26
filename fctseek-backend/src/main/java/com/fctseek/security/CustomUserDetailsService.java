package com.fctseek.security;

import com.fctseek.model.Usuario;
import com.fctseek.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio que carga los detalles del usuario desde la base de datos.
 * Implementa UserDetailsService de Spring Security.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga un usuario por su email (usado como username).
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado con email: " + email
                ));

        // Verificar que el usuario esté activo
        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario desactivado: " + email);
        }

        // Crear autoridad basada en el rol
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol());

        // Retornar UserDetails de Spring Security
        return new User(
            usuario.getEmail(),
            usuario.getPasswordHash(),
            usuario.getActivo(),           // enabled
            true,                           // accountNonExpired
            true,                           // credentialsNonExpired
            true,                           // accountNonLocked
            Collections.singletonList(authority)
        );
    }

    /**
     * Carga un usuario por su ID.
     */
    public UserDetails loadUserById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuario no encontrado con ID: " + id
                ));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol());

        return new User(
            usuario.getEmail(),
            usuario.getPasswordHash(),
            usuario.getActivo(),
            true,
            true,
            true,
            Collections.singletonList(authority)
        );
    }
}
