package com.example.api.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de una nueva calificación.
 */
@Schema(description = "Datos necesarios para crear una calificación")
public record CreateCalificacionRequest(

        @Schema(description = "ID de la evaluación", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID de la evaluación es obligatorio")
        String evaluacionId,

        @Schema(description = "ID del estudiante", example = "550e8400-e29b-41d4-a716-446655440001", required = true)
        @NotBlank(message = "El ID del estudiante es obligatorio")
        String estudianteId,

        @Schema(description = "Nota de la calificación", example = "85.50", required = true)
        @NotNull(message = "La nota es obligatoria")
        @DecimalMin(value = "0.00", message = "La nota no puede ser negativa")
        @DecimalMax(value = "100.00", message = "La nota no puede exceder 100")
        BigDecimal nota,

        @Schema(description = "Comentario sobre la calificación", example = "Excelente trabajo")
        @Size(max = 255, message = "El comentario no puede exceder 255 caracteres")
        String comentario
) {
}
