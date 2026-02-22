package com.fctseek.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuracion de CORS (Cross-Origin Resource Sharing).
 * Permite que el frontend (React Native, JavaFX) se comunique con el backend desde cualquier puerto.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Origenes permitidos (frontend móvil, web y escritorio)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8081",      // Expo/React Native
            "http://127.0.0.1:8081",
            "http://10.0.2.2:8080",        // Android Emulator
            "exp://localhost:8081",
            "exp://192.168.*.*:8081"       // Dispositivos en red local
        ));
        
        // Para desarrollo, permitir cualquier origen
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // Metodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Headers expuestos al cliente
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Disposition"
        ));
        
        // Permitir credenciales (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Tiempo de cache para preflight requests (1 hora)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
