package com.example.api.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Actividad.
 */
@Schema(description = "Información completa de una actividad")
public record ActividadResponse(

        @Schema(description = "ID único de la actividad", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información de la asignatura")
        AsignaturaResponse asignatura,

        @Schema(description = "Información del profesor")
        ProfesorResponse profesor,

        @Schema(description = "Título de la actividad", example = "Tarea: Investigación sobre algoritmos")
        String titulo,

        @Schema(description = "Descripción detallada de la actividad")
        String descripcion,

        @Schema(description = "Fecha y hora de apertura", example = "2024-03-15T08:00:00")
        LocalDateTime fechaApertura,

        @Schema(description = "Fecha y hora de cierre", example = "2024-03-22T23:59:59")
        LocalDateTime fechaCierre,

        @Schema(description = "URL de la imagen asociada", example = "https://ejemplo.com/imagen.jpg")
        String imagenUrl,

        @Schema(description = "URL del documento adjunto", example = "https://ejemplo.com/documento.pdf")
        String documentoUrl,

        @Schema(description = "Nombre del documento adjunto", example = "Instrucciones.pdf")
        String documentoNombre,

        @Schema(description = "Indica si la actividad está activa", example = "true")
        Boolean activo,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
