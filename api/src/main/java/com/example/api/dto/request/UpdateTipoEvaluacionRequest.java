package com.example.api.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un tipo de evaluación existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un tipo de evaluación existente")
public record UpdateTipoEvaluacionRequest(

        @Schema(description = "Nuevo nombre del tipo de evaluación", example = "Examen Final")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @Schema(description = "Nueva descripción", example = "Examen teórico de fin de período")
        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        String descripcion,

        @Schema(description = "Nuevo peso en porcentaje", example = "40.00")
        @DecimalMin(value = "0.00", message = "El peso no puede ser negativo")
        @DecimalMax(value = "100.00", message = "El peso no puede exceder 100")
        BigDecimal peso
) {
}
