package com.example.api.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de una calificación existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar una calificación existente")
public record UpdateCalificacionRequest(

        @Schema(description = "Nueva nota", example = "90.00")
        @DecimalMin(value = "0.00", message = "La nota no puede ser negativa")
        @DecimalMax(value = "100.00", message = "La nota no puede exceder 100")
        BigDecimal nota,

        @Schema(description = "Nuevo comentario", example = "Mejoró significativamente")
        @Size(max = 255, message = "El comentario no puede exceder 255 caracteres")
        String comentario
) {
}
