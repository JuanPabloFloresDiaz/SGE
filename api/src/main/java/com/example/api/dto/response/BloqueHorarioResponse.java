package com.example.api.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad BloqueHorario.
 */
@Schema(description = "Información completa de un bloque de horario")
public record BloqueHorarioResponse(

        @Schema(description = "ID único del bloque", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Nombre del bloque", example = "Bloque 1 - Mañana")
        String nombre,

        @Schema(description = "Hora de inicio", example = "07:00:00")
        LocalTime inicio,

        @Schema(description = "Hora de fin", example = "08:00:00")
        LocalTime fin,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
