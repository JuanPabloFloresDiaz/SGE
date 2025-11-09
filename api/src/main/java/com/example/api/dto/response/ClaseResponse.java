package com.example.api.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Clase.
 */
@Schema(description = "Información completa de una clase")
public record ClaseResponse(

        @Schema(description = "ID único de la clase", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información del curso")
        CursoResponse curso,

        @Schema(description = "Fecha de la clase", example = "2024-03-15")
        LocalDate fecha,

        @Schema(description = "Hora de inicio", example = "08:00:00")
        LocalTime inicio,

        @Schema(description = "Hora de fin", example = "10:00:00")
        LocalTime fin,

        @Schema(description = "Información de la unidad (si aplica)")
        UnidadResponse unidad,

        @Schema(description = "Información del tema (si aplica)")
        TemaResponse tema,

        @Schema(description = "Notas sobre la clase", example = "Se revisó el tema de variables")
        String notas,

        @Schema(description = "URL del documento de la clase", example = "uploads/clases/documento123.pdf")
        String documentoUrl,

        @Schema(description = "Nombre del documento de la clase", example = "presentacion-clase1.pdf")
        String documentoNombre,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
