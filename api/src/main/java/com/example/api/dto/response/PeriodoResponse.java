package com.example.api.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Periodo.
 */
@Schema(description = "Información completa de un periodo académico")
public record PeriodoResponse(

        @Schema(description = "ID único del periodo", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Nombre del periodo", example = "Primer Semestre 2024")
        String nombre,

        @Schema(description = "Fecha de inicio del periodo", example = "2024-01-15")
        LocalDate fechaInicio,

        @Schema(description = "Fecha de fin del periodo", example = "2024-06-30")
        LocalDate fechaFin,

        @Schema(description = "Estado activo del periodo", example = "true")
        Boolean activo,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
