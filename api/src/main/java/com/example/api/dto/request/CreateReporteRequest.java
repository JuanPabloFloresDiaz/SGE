package com.example.api.dto.request;

import com.example.api.model.Reporte.TipoReporte;
import com.example.api.model.Reporte.PesoReporte;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo reporte.
 */
@Schema(description = "Datos necesarios para crear un reporte")
public record CreateReporteRequest(

        @Schema(description = "ID del estudiante", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID del estudiante es obligatorio")
        String estudianteId,

        @Schema(description = "ID del curso (opcional)", example = "550e8400-e29b-41d4-a716-446655440001")
        String cursoId,

        @Schema(description = "Tipo de reporte", example = "ACADEMICO", required = true, allowableValues = {"CONDUCTA", "ACADEMICO", "OTRO"})
        @NotNull(message = "El tipo de reporte es obligatorio")
        TipoReporte tipo,

        @Schema(description = "Peso/severidad del reporte", example = "MODERADO", required = true, allowableValues = {"LEVE", "MODERADO", "GRAVE"})
        @NotNull(message = "El peso del reporte es obligatorio")
        PesoReporte peso,

        @Schema(description = "Título del reporte", example = "Bajo rendimiento en matemáticas", required = true)
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título no puede exceder 200 caracteres")
        String titulo,

        @Schema(description = "Descripción detallada del reporte", example = "El estudiante ha mostrado dificultades en álgebra...")
        String descripcion,

        @Schema(description = "ID del usuario que crea el reporte", example = "550e8400-e29b-41d4-a716-446655440002")
        String creadoPorId
) {
}
