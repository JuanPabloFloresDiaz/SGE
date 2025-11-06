package com.example.api.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Curso.
 */
@Schema(description = "Información completa de un curso")
public record CursoResponse(

        @Schema(description = "ID único del curso", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información de la asignatura")
        AsignaturaResponse asignatura,

        @Schema(description = "Información del profesor asignado")
        ProfesorResponse profesor,

        @Schema(description = "Información del periodo académico")
        PeriodoResponse periodo,

        @Schema(description = "Nombre del grupo", example = "Grupo A")
        String nombreGrupo,

        @Schema(description = "Aula por defecto", example = "Aula 101")
        String aulaDefault,

        @Schema(description = "Cupo máximo de estudiantes", example = "30")
        Integer cupo,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
