package com.example.api.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de una nueva evaluación.
 */
@Schema(description = "Datos necesarios para crear una evaluación")
public record CreateEvaluacionRequest(

        @Schema(description = "ID del curso", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID del curso es obligatorio")
        String cursoId,

        @Schema(description = "ID del tipo de evaluación", example = "550e8400-e29b-41d4-a716-446655440001", required = true)
        @NotBlank(message = "El ID del tipo de evaluación es obligatorio")
        String tipoId,

        @Schema(description = "Nombre de la evaluación", example = "Examen Parcial 1", required = true)
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
        String nombre,

        @Schema(description = "Fecha de la evaluación", example = "2024-03-15")
        LocalDate fecha,

        @Schema(description = "Peso de la evaluación en porcentaje", example = "30.00")
        @DecimalMin(value = "0.00", message = "El peso no puede ser negativo")
        @DecimalMax(value = "100.00", message = "El peso no puede exceder 100")
        BigDecimal peso,

        @Schema(description = "Indica si la evaluación está publicada", example = "false")
        Boolean publicado,

        @Schema(description = "URL del documento de la evaluación", example = "uploads/evaluaciones/documento123.pdf")
        @Size(max = 500, message = "La URL del documento no puede exceder 500 caracteres")
        String documentoUrl,

        @Schema(description = "Nombre del documento de la evaluación", example = "instrucciones-examen.pdf")
        @Size(max = 255, message = "El nombre del documento no puede exceder 255 caracteres")
        String documentoNombre
) {
}
