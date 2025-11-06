package com.example.api.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.api.model.Inscripcion.EstadoInscripcion;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Inscripcion.
 */
@Schema(description = "Información completa de una inscripción")
public record InscripcionResponse(

        @Schema(description = "ID único de la inscripción", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información del curso")
        CursoResponse curso,

        @Schema(description = "Información del estudiante")
        EstudianteResponse estudiante,

        @Schema(description = "Fecha de inscripción", example = "2024-01-15")
        LocalDate fechaInscripcion,

        @Schema(description = "Estado de la inscripción", example = "INSCRITO")
        EstadoInscripcion estado,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
