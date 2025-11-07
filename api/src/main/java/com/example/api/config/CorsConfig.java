package com.example.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing) para permitir
 * peticiones desde el frontend.
 */
@Configuration
public class CorsConfig {

    /**
     * Configura el filtro CORS para permitir peticiones desde diferentes orígenes.
     * 
     * @return CorsFilter configurado
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permitir credenciales (cookies, authorization headers, etc.)
        config.setAllowCredentials(true);

        // Orígenes permitidos (frontend)
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:5500",      // Live Server
                "http://localhost:3000",      // React/Vite común
                "http://localhost:8081",      // Otro puerto común
                "http://127.0.0.1:5500",      // Alternativa de localhost
                "http://127.0.0.1:3000",
                "http://localhost:5501"
        ));

        // Métodos HTTP permitidos
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        // Headers permitidos
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Headers expuestos al cliente
        config.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials",
                "Authorization"
        ));

        // Tiempo de caché de la configuración CORS (en segundos)
        config.setMaxAge(3600L);

        // Aplicar la configuración a todos los endpoints
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
