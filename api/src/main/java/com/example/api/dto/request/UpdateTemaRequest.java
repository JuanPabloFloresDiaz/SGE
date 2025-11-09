package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un tema existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un tema existente")
public record UpdateTemaRequest(

        @Schema(description = "Nuevo título del tema", example = "Variables, Constantes y Tipos de Datos")
        @Size(max = 200, message = "El título no puede exceder 200 caracteres")
        String titulo,

        @Schema(description = "Nueva descripción", example = "Tema actualizado con nuevos contenidos")
        String descripcion,

        @Schema(description = "Nuevo número del tema", example = "2")
        Integer numero,

        @Schema(description = "Nueva duración estimada en minutos", example = "60")
        @Positive(message = "La duración debe ser un número positivo")
        Integer duracionMinutos,

        @Schema(description = "Nueva URL del documento del tema", example = "uploads/temas/nuevo-documento.pdf")
        @Size(max = 500, message = "La URL del documento no puede exceder 500 caracteres")
        String documentoUrl,

        @Schema(description = "Nuevo nombre del documento del tema", example = "nuevo-recurso.pdf")
        @Size(max = 255, message = "El nombre del documento no puede exceder 255 caracteres")
        String documentoNombre
) {
}
