package com.example.api.dto.response;

import java.time.LocalDateTime;

import com.example.api.model.Reporte.TipoReporte;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Reporte.
 */
@Schema(description = "Información completa de un reporte")
public record ReporteResponse(

        @Schema(description = "ID único del reporte", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información del estudiante")
        EstudianteResponse estudiante,

        @Schema(description = "Información del curso (opcional)")
        CursoResponse curso,

        @Schema(description = "Tipo de reporte", example = "ACADEMICO")
        TipoReporte tipo,

        @Schema(description = "Título del reporte", example = "Bajo rendimiento en matemáticas")
        String titulo,

        @Schema(description = "Descripción detallada del reporte")
        String descripcion,

        @Schema(description = "Información del usuario que creó el reporte")
        UsuarioResponse creadoPor,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
