package com.example.api.dto.request;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de una nueva actividad.
 */
@Schema(description = "Datos necesarios para crear una actividad")
public record CreateActividadRequest(

        @Schema(description = "ID de la asignatura", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID de la asignatura es obligatorio")
        String asignaturaId,

        @Schema(description = "ID del profesor", example = "550e8400-e29b-41d4-a716-446655440001", required = true)
        @NotBlank(message = "El ID del profesor es obligatorio")
        String profesorId,

        @Schema(description = "Título de la actividad", example = "Tarea: Investigación sobre algoritmos", required = true)
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título no puede exceder 200 caracteres")
        String titulo,

        @Schema(description = "Descripción detallada de la actividad", example = "Realizar una investigación sobre algoritmos de ordenamiento...", required = true)
        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @Schema(description = "Fecha y hora de apertura de la actividad", example = "2024-03-15T08:00:00", required = true)
        @NotNull(message = "La fecha de apertura es obligatoria")
        LocalDateTime fechaApertura,

        @Schema(description = "Fecha y hora de cierre de la actividad", example = "2024-03-22T23:59:59", required = true)
        @NotNull(message = "La fecha de cierre es obligatoria")
        LocalDateTime fechaCierre,

        @Schema(description = "URL de la imagen asociada", example = "https://ejemplo.com/imagen.jpg")
        @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
        String imagenUrl,

        @Schema(description = "URL del documento adjunto", example = "https://ejemplo.com/documento.pdf")
        @Size(max = 500, message = "La URL del documento no puede exceder 500 caracteres")
        String documentoUrl,

        @Schema(description = "Nombre del documento adjunto", example = "Instrucciones.pdf")
        @Size(max = 255, message = "El nombre del documento no puede exceder 255 caracteres")
        String documentoNombre,

        @Schema(description = "Indica si la actividad está activa", example = "true")
        Boolean activo
) {
}
