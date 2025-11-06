package com.example.api.dto.request;

import java.time.LocalDate;

import com.example.api.model.Inscripcion.EstadoInscripcion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la creación de una nueva inscripción.
 */
@Schema(description = "Datos necesarios para crear una nueva inscripción")
public record CreateInscripcionRequest(

        @Schema(description = "ID del curso", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID del curso es obligatorio")
        String cursoId,

        @Schema(description = "ID del estudiante", example = "550e8400-e29b-41d4-a716-446655440001", required = true)
        @NotBlank(message = "El ID del estudiante es obligatorio")
        String estudianteId,

        @Schema(description = "Fecha de inscripción", example = "2024-01-15")
        LocalDate fechaInscripcion,

        @Schema(description = "Estado inicial de la inscripción", example = "INSCRITO", defaultValue = "INSCRITO", allowableValues = {"INSCRITO", "RETIRADO", "COMPLETADO"})
        @NotNull(message = "El estado es obligatorio")
        EstadoInscripcion estado
) {
}
