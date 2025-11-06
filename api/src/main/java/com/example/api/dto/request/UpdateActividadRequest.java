package com.example.api.dto.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de una actividad existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar una actividad existente")
public record UpdateActividadRequest(

        @Schema(description = "Nuevo ID de la asignatura", example = "550e8400-e29b-41d4-a716-446655440000")
        String asignaturaId,

        @Schema(description = "Nuevo ID del profesor", example = "550e8400-e29b-41d4-a716-446655440001")
        String profesorId,

        @Schema(description = "Nuevo título de la actividad", example = "Proyecto Final")
        @Size(max = 200, message = "El título no puede exceder 200 caracteres")
        String titulo,

        @Schema(description = "Nueva descripción de la actividad", example = "Descripción actualizada...")
        String descripcion,

        @Schema(description = "Nueva fecha y hora de apertura", example = "2024-03-20T08:00:00")
        LocalDateTime fechaApertura,

        @Schema(description = "Nueva fecha y hora de cierre", example = "2024-03-27T23:59:59")
        LocalDateTime fechaCierre,

        @Schema(description = "Nueva URL de la imagen", example = "https://ejemplo.com/nueva-imagen.jpg")
        @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
        String imagenUrl,

        @Schema(description = "Nueva URL del documento", example = "https://ejemplo.com/nuevo-documento.pdf")
        @Size(max = 500, message = "La URL del documento no puede exceder 500 caracteres")
        String documentoUrl,

        @Schema(description = "Nuevo nombre del documento", example = "NuevasInstrucciones.pdf")
        @Size(max = 255, message = "El nombre del documento no puede exceder 255 caracteres")
        String documentoNombre,

        @Schema(description = "Nuevo estado activo", example = "false")
        Boolean activo
) {
}
