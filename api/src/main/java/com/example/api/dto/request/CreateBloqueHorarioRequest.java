package com.example.api.dto.request;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo bloque de horario.
 */
@Schema(description = "Datos necesarios para crear un nuevo bloque de horario")
public record CreateBloqueHorarioRequest(

        @Schema(description = "Nombre del bloque", example = "Bloque 1 - Mañana")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String nombre,

        @Schema(description = "Hora de inicio", example = "07:00:00", required = true)
        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime inicio,

        @Schema(description = "Hora de fin", example = "08:00:00", required = true)
        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime fin
) {
}
