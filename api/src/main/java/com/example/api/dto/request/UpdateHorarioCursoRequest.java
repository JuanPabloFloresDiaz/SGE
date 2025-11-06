package com.example.api.dto.request;

import com.example.api.model.HorarioCurso.DiaSemana;
import com.example.api.model.HorarioCurso.TipoHorario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un horario de curso existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un horario de curso existente")
public record UpdateHorarioCursoRequest(

        @Schema(description = "Nuevo ID del curso", example = "550e8400-e29b-41d4-a716-446655440000")
        String cursoId,

        @Schema(description = "Nuevo ID del bloque de horario", example = "550e8400-e29b-41d4-a716-446655440001")
        String bloqueId,

        @Schema(description = "Nuevo día de la semana", example = "MAR", allowableValues = {"LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM"})
        DiaSemana dia,

        @Schema(description = "Nueva aula", example = "Aula 302")
        @Size(max = 100, message = "El aula no puede exceder 100 caracteres")
        String aula,

        @Schema(description = "Nuevo tipo de horario", example = "LABORATORIO", allowableValues = {"REGULAR", "LABORATORIO", "TALLER", "OTRO"})
        TipoHorario tipo
) {
}
