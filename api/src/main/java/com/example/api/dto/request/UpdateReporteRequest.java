package com.example.api.dto.request;

import com.example.api.model.Reporte.TipoReporte;
import com.example.api.model.Reporte.PesoReporte;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un reporte existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un reporte existente")
public record UpdateReporteRequest(

        @Schema(description = "Nuevo ID del curso", example = "550e8400-e29b-41d4-a716-446655440001")
        String cursoId,

        @Schema(description = "Nuevo tipo de reporte", example = "CONDUCTA", allowableValues = {"CONDUCTA", "ACADEMICO", "OTRO"})
        TipoReporte tipo,

        @Schema(description = "Nuevo peso/severidad del reporte", example = "GRAVE", allowableValues = {"LEVE", "MODERADO", "GRAVE"})
        PesoReporte peso,

        @Schema(description = "Nuevo título del reporte", example = "Mejora en comportamiento")
        @Size(max = 200, message = "El título no puede exceder 200 caracteres")
        String titulo,

        @Schema(description = "Nueva descripción del reporte", example = "El estudiante ha mostrado mejoras...")
        String descripcion
) {
}
