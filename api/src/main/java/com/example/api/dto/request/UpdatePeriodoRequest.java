package com.example.api.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un periodo académico existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un periodo académico existente")
public record UpdatePeriodoRequest(

        @Schema(description = "Nuevo nombre del periodo", example = "Segundo Semestre 2024")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @Schema(description = "Nueva fecha de inicio", example = "2024-08-01")
        LocalDate fechaInicio,

        @Schema(description = "Nueva fecha de fin", example = "2024-12-15")
        LocalDate fechaFin,

        @Schema(description = "Nuevo estado activo del periodo", example = "false")
        Boolean activo
) {
}
