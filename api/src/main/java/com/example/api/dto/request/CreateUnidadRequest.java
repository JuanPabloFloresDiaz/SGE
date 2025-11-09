package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de una nueva unidad.
 */
@Schema(description = "Datos necesarios para crear una nueva unidad didáctica")
public record CreateUnidadRequest(

        @Schema(description = "ID del curso", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID del curso es obligatorio")
        String cursoId,

        @Schema(description = "Título de la unidad", example = "Introducción a la Programación", required = true)
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título no puede exceder 200 caracteres")
        String titulo,

        @Schema(description = "Descripción de la unidad", example = "En esta unidad aprenderemos los conceptos básicos de programación")
        String descripcion,

        @Schema(description = "Número de la unidad", example = "1")
        Integer numero,

        @Schema(description = "URL del documento de la unidad", example = "uploads/unidades/documento123.pdf")
        @Size(max = 500, message = "La URL del documento no puede exceder 500 caracteres")
        String documentoUrl,

        @Schema(description = "Nombre del documento de la unidad", example = "material-unidad1.pdf")
        @Size(max = 255, message = "El nombre del documento no puede exceder 255 caracteres")
        String documentoNombre
) {
}
