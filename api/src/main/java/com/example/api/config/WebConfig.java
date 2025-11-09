package com.example.api.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración para exponer el directorio de archivos subidos
 * para que sean accesibles mediante URLs públicas.
 * 
 * Ejemplo: Si un archivo está en uploads/actividades/imagen.jpg,
 * será accesible en: http://localhost:8080/uploads/actividades/imagen.jpg
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Obtener la ruta absoluta del directorio uploads
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadLocation = "file:" + uploadPath.toString() + "/";

        // Exponer el directorio uploads y todos sus subdirectorios
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation);
    }
}
