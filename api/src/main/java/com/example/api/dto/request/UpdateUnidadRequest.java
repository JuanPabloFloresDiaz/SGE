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
        Integer numero,

        @Schema(description = "Nueva URL del documento de la unidad", example = "uploads/unidades/nuevo-documento.pdf")
        @Size(max = 500, message = "La URL del documento no puede exceder 500 caracteres")
        String documentoUrl,

        @Schema(description = "Nuevo nombre del documento de la unidad", example = "nuevo-material.pdf")
        @Size(max = 255, message = "El nombre del documento no puede exceder 255 caracteres")
        String documentoNombre
) {
}
