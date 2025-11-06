package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de una unidad existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar una unidad existente")
public record UpdateUnidadRequest(

        @Schema(description = "Nuevo título de la unidad", example = "Fundamentos de Programación")
        @Size(max = 200, message = "El título no puede exceder 200 caracteres")
        String titulo,

        @Schema(description = "Nueva descripción", example = "Unidad actualizada con nuevos contenidos")
        String descripcion,

        @Schema(description = "Nuevo número de la unidad", example = "2")
        Integer numero
) {
}
