package com.fctseek.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT.
 * Intercepta todas las peticiones HTTP y valida el token JWT en la cabecera Authorization.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, 
                                   CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        try {
            // Extraer token de la cabecera
            String jwt = getJwtFromRequest(request);

            // Si hay token y es valido, autenticar
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                // Extraer email del token
                String email = jwtTokenProvider.extractEmail(jwt);

                // Cargar detalles del usuario
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Verificar que el token sea valido para este usuario
                if (jwtTokenProvider.isTokenValid(jwt, userDetails)) {
                    // Crear objeto de autenticación
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // Log del error pero no lanzar excepción para permitir que continúe el filtro
            logger.error("No se pudo establecer la autenticación del usuario: " + e.getMessage());
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT de la cabecera Authorization.
     * Formato esperado: "Bearer <token>"
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }

    /**
     * Excluir ciertos paths del filtro (endpoints publicos).
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/api/auth/login") ||
               path.equals("/api/auth/register") ||
               path.equals("/api/auth/verify") ||
               path.equals("/api/health") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs");
    }
}
