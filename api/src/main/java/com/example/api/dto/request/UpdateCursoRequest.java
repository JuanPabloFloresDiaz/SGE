package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un curso existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un curso existente")
public record UpdateCursoRequest(

        @Schema(description = "Nuevo ID de la asignatura", example = "550e8400-e29b-41d4-a716-446655440000")
        String asignaturaId,

        @Schema(description = "Nuevo ID del profesor", example = "550e8400-e29b-41d4-a716-446655440001")
        String profesorId,

        @Schema(description = "Nuevo ID del periodo", example = "550e8400-e29b-41d4-a716-446655440002")
        String periodoId,

        @Schema(description = "Nuevo nombre del grupo", example = "Grupo B")
        @Size(max = 100, message = "El nombre del grupo no puede exceder 100 caracteres")
        String nombreGrupo,

        @Schema(description = "Nueva aula por defecto", example = "Aula 202")
        @Size(max = 100, message = "El aula no puede exceder 100 caracteres")
        String aulaDefault,

        @Schema(description = "Nuevo cupo máximo", example = "35")
        @Min(value = 0, message = "El cupo debe ser mayor o igual a 0")
        Integer cupo
) {
}
