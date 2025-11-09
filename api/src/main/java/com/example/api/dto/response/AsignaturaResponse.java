package com.example.api.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Asignatura.
 */
@Schema(description = "Información completa de una asignatura")
public record AsignaturaResponse(

        @Schema(description = "ID único de la asignatura", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Código único de la asignatura", example = "MAT-101")
        String codigo,

        @Schema(description = "Nombre de la asignatura", example = "Matemáticas I")
        String nombre,

        @Schema(description = "Descripción de la asignatura", example = "Introducción al cálculo diferencial e integral")
        String descripcion,

        @Schema(description = "URL de la imagen de la asignatura", example = "uploads/asignaturas/imagen123.jpg")
        String imagenUrl,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
