package com.example.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Calificacion.
 */
@Schema(description = "Información completa de una calificación")
public record CalificacionResponse(

        @Schema(description = "ID único de la calificación", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información de la evaluación")
        EvaluacionResponse evaluacion,

        @Schema(description = "Información del estudiante")
        EstudianteResponse estudiante,

        @Schema(description = "Nota obtenida", example = "85.50")
        BigDecimal nota,

        @Schema(description = "Comentario sobre la calificación", example = "Excelente trabajo")
        String comentario,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
