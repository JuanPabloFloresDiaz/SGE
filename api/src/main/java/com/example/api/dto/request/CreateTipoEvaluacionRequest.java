package com.example.api.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo tipo de evaluación.
 */
@Schema(description = "Datos necesarios para crear un tipo de evaluación")
public record CreateTipoEvaluacionRequest(

        @Schema(description = "Nombre del tipo de evaluación", example = "Examen Parcial", required = true)
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @Schema(description = "Descripción del tipo de evaluación", example = "Examen teórico de medio período")
        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        String descripcion,

        @Schema(description = "Peso del tipo de evaluación en porcentaje", example = "30.00")
        @DecimalMin(value = "0.00", message = "El peso no puede ser negativo")
        @DecimalMax(value = "100.00", message = "El peso no puede exceder 100")
        BigDecimal peso
) {
}
