package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo curso.
 */
@Schema(description = "Datos necesarios para crear un nuevo curso")
public record CreateCursoRequest(

        @Schema(description = "ID de la asignatura", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID de la asignatura es obligatorio")
        String asignaturaId,

        @Schema(description = "ID del profesor asignado", example = "550e8400-e29b-41d4-a716-446655440001")
        String profesorId,

        @Schema(description = "ID del periodo académico", example = "550e8400-e29b-41d4-a716-446655440002", required = true)
        @NotBlank(message = "El ID del periodo es obligatorio")
        String periodoId,

        @Schema(description = "Nombre del grupo", example = "Grupo A", required = true)
        @NotBlank(message = "El nombre del grupo es obligatorio")
        @Size(max = 100, message = "El nombre del grupo no puede exceder 100 caracteres")
        String nombreGrupo,

        @Schema(description = "Aula por defecto", example = "Aula 101")
        @Size(max = 100, message = "El aula no puede exceder 100 caracteres")
        String aulaDefault,

        @Schema(description = "Cupo máximo de estudiantes", example = "30", required = true)
        @NotNull(message = "El cupo es obligatorio")
        @Min(value = 0, message = "El cupo debe ser mayor o igual a 0")
        Integer cupo
) {
}
