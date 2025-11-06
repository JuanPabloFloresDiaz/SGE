package com.example.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad TipoEvaluacion.
 */
@Schema(description = "Información completa de un tipo de evaluación")
public record TipoEvaluacionResponse(

        @Schema(description = "ID único del tipo de evaluación", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Nombre del tipo de evaluación", example = "Examen Parcial")
        String nombre,

        @Schema(description = "Descripción del tipo de evaluación", example = "Examen teórico de medio período")
        String descripcion,

        @Schema(description = "Peso del tipo de evaluación en porcentaje", example = "30.00")
        BigDecimal peso,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
