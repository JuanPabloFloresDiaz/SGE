package com.example.api.dto.response;

import java.time.LocalDateTime;

import com.example.api.model.Asistencia.EstadoAsistencia;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Asistencia.
 */
@Schema(description = "Información completa de un registro de asistencia")
public record AsistenciaResponse(

        @Schema(description = "ID único del registro de asistencia", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información de la clase")
        ClaseResponse clase,

        @Schema(description = "Información del estudiante")
        EstudianteResponse estudiante,

        @Schema(description = "Estado de asistencia", example = "PRESENTE")
        EstadoAsistencia estado,

        @Schema(description = "Observación sobre la asistencia", example = "Llegó 10 minutos tarde")
        String observacion,

        @Schema(description = "Información del usuario que registró la asistencia")
        UsuarioResponse registradoPor,

        @Schema(description = "Fecha y hora del registro", example = "2024-03-15T10:30:00")
        LocalDateTime registradoAt,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
