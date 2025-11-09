package com.example.api.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.api.model.Estudiante.Genero;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Estudiante.
 * Incluye información básica del usuario asociado.
 */
@Schema(description = "Información completa de un estudiante")
public record EstudianteResponse(

        @Schema(description = "ID único del estudiante", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información del usuario asociado")
        UsuarioResponse usuario,

        @Schema(description = "Código único del estudiante", example = "EST-2024-001")
        String codigoEstudiante,

        @Schema(description = "Fecha de nacimiento", example = "2000-05-15")
        LocalDate fechaNacimiento,

        @Schema(description = "Dirección de residencia", example = "Av. Principal 123, San José")
        String direccion,

        @Schema(description = "Género del estudiante", example = "M")
        Genero genero,

        @Schema(description = "Fecha de ingreso", example = "2024-01-15")
        LocalDate ingreso,

        @Schema(description = "Estado activo del estudiante", example = "true")
        Boolean activo,

        @Schema(description = "URL de la foto del estudiante", example = "uploads/estudiantes/foto123.jpg")
        String fotoUrl,

        @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-20T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
