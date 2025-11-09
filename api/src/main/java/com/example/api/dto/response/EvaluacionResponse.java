package com.example.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Evaluacion.
 */
@Schema(description = "Información completa de una evaluación")
public record EvaluacionResponse(

        @Schema(description = "ID único de la evaluación", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información del curso")
        CursoResponse curso,

        @Schema(description = "Información del tipo de evaluación")
        TipoEvaluacionResponse tipoEvaluacion,

        @Schema(description = "Nombre de la evaluación", example = "Examen Parcial 1")
        String nombre,

        @Schema(description = "Fecha de la evaluación", example = "2024-03-15")
        LocalDate fecha,

        @Schema(description = "Peso de la evaluación en porcentaje", example = "30.00")
        BigDecimal peso,

        @Schema(description = "Indica si la evaluación está publicada", example = "false")
        Boolean publicado,

        @Schema(description = "URL del documento de la evaluación", example = "uploads/evaluaciones/documento123.pdf")
        String documentoUrl,

        @Schema(description = "Nombre del documento de la evaluación", example = "instrucciones-examen.pdf")
        String documentoNombre,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
