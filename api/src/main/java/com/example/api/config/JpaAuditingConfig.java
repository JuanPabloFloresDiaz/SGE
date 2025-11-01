package com.example.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuración para habilitar JPA Auditing.
 * Permite el uso automático de @CreatedDate y @LastModifiedDate en las entidades.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // La anotación @EnableJpaAuditing es suficiente para activar el auditing
}
