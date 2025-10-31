package com.example.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sgeOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Servidor de Desarrollo");

        Contact contact = new Contact();
        contact.setName("Equipo de Desarrollo SGE");
        contact.setEmail("equipo@sge.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("SGE API - Sistema de Gestión Educativa")
                .version("1.0.0")
                .contact(contact)
                .description("API REST para la gestión de un sistema educativo. " +
                        "Incluye gestión de estudiantes, profesores, cursos, evaluaciones, " +
                        "asistencia y más.")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
