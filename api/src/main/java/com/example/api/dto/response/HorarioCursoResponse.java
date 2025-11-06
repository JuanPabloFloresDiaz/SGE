package com.example.api.dto.response;

import java.time.LocalDateTime;

import com.example.api.model.HorarioCurso.DiaSemana;
import com.example.api.model.HorarioCurso.TipoHorario;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad HorarioCurso.
 */
@Schema(description = "Información completa de un horario de curso")
public record HorarioCursoResponse(

        @Schema(description = "ID único del horario", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información del curso")
        CursoResponse curso,

        @Schema(description = "Información del bloque de horario")
        BloqueHorarioResponse bloqueHorario,

        @Schema(description = "Día de la semana", example = "LUN")
        DiaSemana dia,

        @Schema(description = "Aula donde se imparte", example = "Aula 201")
        String aula,

        @Schema(description = "Tipo de horario", example = "REGULAR")
        TipoHorario tipo,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
