package com.example.api.dto.request;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un bloque de horario existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un bloque de horario existente")
public record UpdateBloqueHorarioRequest(

        @Schema(description = "Nuevo nombre del bloque", example = "Bloque 2 - Tarde")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @Schema(description = "Nueva hora de inicio", example = "08:00:00")
        LocalTime inicio,

        @Schema(description = "Nueva hora de fin", example = "09:00:00")
        LocalTime fin
) {
}
