package com.example.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad de Spring Security.
 * Por ahora, permite el acceso a todos los endpoints sin autenticación.
 * El endpoint de login genera tokens JWT, pero no se validan en las peticiones.
 * TODO: Implementar validación de JWT con filtros en versiones futuras.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad.
     * Actualmente permite todas las peticiones sin autenticación.
     *
     * @param http Configurador de seguridad HTTP
     * @return La cadena de filtros configurada
     * @throws Exception Si hay error en la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Permitir endpoints de autenticación
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Permitir Swagger
                        .anyRequest().permitAll() // Permitir todas las demás peticiones por ahora
                );

        return http.build();
    }

    /**
     * Provee un bean de BCryptPasswordEncoder para encriptar contraseñas.
     *
     * @return El encoder de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
