package com.example.api.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de una evaluación existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar una evaluación existente")
public record UpdateEvaluacionRequest(

        @Schema(description = "Nuevo ID del curso", example = "550e8400-e29b-41d4-a716-446655440000")
        String cursoId,

        @Schema(description = "Nuevo ID del tipo de evaluación", example = "550e8400-e29b-41d4-a716-446655440001")
        String tipoId,

        @Schema(description = "Nuevo nombre de la evaluación", example = "Examen Final")
        @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
        String nombre,

        @Schema(description = "Nueva fecha de la evaluación", example = "2024-06-20")
        LocalDate fecha,

        @Schema(description = "Nuevo peso en porcentaje", example = "40.00")
        @DecimalMin(value = "0.00", message = "El peso no puede ser negativo")
        @DecimalMax(value = "100.00", message = "El peso no puede exceder 100")
        BigDecimal peso,

        @Schema(description = "Nuevo estado de publicación", example = "true")
        Boolean publicado,

        @Schema(description = "Nueva URL del documento de la evaluación", example = "uploads/evaluaciones/nuevo-documento.pdf")
        @Size(max = 500, message = "La URL del documento no puede exceder 500 caracteres")
        String documentoUrl,

        @Schema(description = "Nuevo nombre del documento de la evaluación", example = "nueva-rubrica.pdf")
        @Size(max = 255, message = "El nombre del documento no puede exceder 255 caracteres")
        String documentoNombre
) {
}
