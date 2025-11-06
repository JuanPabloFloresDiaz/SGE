package com.example.api.dto.request;

import java.time.LocalDate;

import com.example.api.model.Inscripcion.EstadoInscripcion;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para la actualización de una inscripción existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar una inscripción existente")
public record UpdateInscripcionRequest(

        @Schema(description = "Nueva fecha de inscripción", example = "2024-01-20")
        LocalDate fechaInscripcion,

        @Schema(description = "Nuevo estado de la inscripción", example = "COMPLETADO", allowableValues = {"INSCRITO", "RETIRADO", "COMPLETADO"})
        EstadoInscripcion estado
) {
}
