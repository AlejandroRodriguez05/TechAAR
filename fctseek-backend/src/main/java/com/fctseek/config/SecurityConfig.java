package com.fctseek.config;

import com.fctseek.security.JwtAuthenticationFilter;
import com.fctseek.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuración de Spring Security.
 * Define las reglas de seguridad, endpoints públicos/protegidos y configuración JWT.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                         CustomUserDetailsService userDetailsService,
                         CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF (no necesario para API REST con JWT)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // Configurar reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (sin autenticación)
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/verify").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/error").permitAll()
                
                // Swagger/OpenAPI (si se añade en el futuro)
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // Endpoints de solo lectura para usuarios autenticados
                .requestMatchers(HttpMethod.GET, "/api/departamentos/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/cursos/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/empresas/**").authenticated()
                
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Sin sesiones (stateless para JWT)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Usar nuestro AuthenticationProvider
            .authenticationProvider(authenticationProvider())
            
            // Añadir filtro JWT antes del filtro de autenticación estándar
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

@Bean
public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
