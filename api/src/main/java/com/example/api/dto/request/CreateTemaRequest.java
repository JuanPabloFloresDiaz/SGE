package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo tema.
 */
@Schema(description = "Datos necesarios para crear un nuevo tema")
public record CreateTemaRequest(

        @Schema(description = "ID de la unidad", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID de la unidad es obligatorio")
        String unidadId,

        @Schema(description = "Título del tema", example = "Variables y Tipos de Datos", required = true)
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título no puede exceder 200 caracteres")
        String titulo,

        @Schema(description = "Descripción del tema", example = "En este tema aprenderemos sobre variables y sus tipos")
        String descripcion,

        @Schema(description = "Número del tema", example = "1")
        Integer numero,

        @Schema(description = "Duración estimada en minutos", example = "45")
        @Positive(message = "La duración debe ser un número positivo")
        Integer duracionMinutos,

        @Schema(description = "URL del documento del tema", example = "uploads/temas/documento123.pdf")
        @Size(max = 500, message = "La URL del documento no puede exceder 500 caracteres")
        String documentoUrl,

        @Schema(description = "Nombre del documento del tema", example = "recurso-tema1.pdf")
        @Size(max = 255, message = "El nombre del documento no puede exceder 255 caracteres")
        String documentoNombre
) {
}
