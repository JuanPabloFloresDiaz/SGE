package com.example.api.dto.request;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo periodo académico.
 */
@Schema(description = "Datos necesarios para crear un nuevo periodo académico")
public record CreatePeriodoRequest(

        @Schema(description = "Nombre del periodo", example = "Primer Semestre 2024", required = true)
        @NotBlank(message = "El nombre del periodo es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @Schema(description = "Fecha de inicio del periodo", example = "2024-01-15", required = true)
        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @Schema(description = "Fecha de fin del periodo", example = "2024-06-30", required = true)
        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaFin,

        @Schema(description = "Indica si el periodo está activo", example = "true", defaultValue = "true")
        @NotNull(message = "El estado activo es obligatorio")
        Boolean activo
) {
}
