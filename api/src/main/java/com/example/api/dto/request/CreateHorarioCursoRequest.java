package com.example.api.dto.request;

import com.example.api.model.HorarioCurso.DiaSemana;
import com.example.api.model.HorarioCurso.TipoHorario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo horario de curso.
 */
@Schema(description = "Datos necesarios para crear un nuevo horario de curso")
public record CreateHorarioCursoRequest(

        @Schema(description = "ID del curso", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID del curso es obligatorio")
        String cursoId,

        @Schema(description = "ID del bloque de horario", example = "550e8400-e29b-41d4-a716-446655440001", required = true)
        @NotBlank(message = "El ID del bloque de horario es obligatorio")
        String bloqueId,

        @Schema(description = "Día de la semana", example = "LUN", required = true, allowableValues = {"LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM"})
        @NotNull(message = "El día de la semana es obligatorio")
        DiaSemana dia,

        @Schema(description = "Aula donde se imparte", example = "Aula 201")
        @Size(max = 100, message = "El aula no puede exceder 100 caracteres")
        String aula,

        @Schema(description = "Tipo de horario", example = "REGULAR", allowableValues = {"REGULAR", "LABORATORIO", "TALLER", "OTRO"})
        TipoHorario tipo
) {
}
