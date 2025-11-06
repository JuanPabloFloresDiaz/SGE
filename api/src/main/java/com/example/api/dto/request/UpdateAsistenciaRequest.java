package com.example.api.dto.request;

import java.time.LocalDateTime;

import com.example.api.model.Asistencia.EstadoAsistencia;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un registro de asistencia existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un registro de asistencia existente")
public record UpdateAsistenciaRequest(

        @Schema(description = "Nuevo estado de asistencia", example = "JUSTIFICADO", allowableValues = {"PRESENTE", "AUSENTE", "TARDE", "JUSTIFICADO"})
        EstadoAsistencia estado,

        @Schema(description = "Nueva observación", example = "Presentó justificante médico")
        @Size(max = 255, message = "La observación no puede exceder 255 caracteres")
        String observacion,

        @Schema(description = "Nuevo ID del usuario que registra", example = "550e8400-e29b-41d4-a716-446655440002")
        String registradoPorId,

        @Schema(description = "Nueva fecha y hora del registro", example = "2024-03-15T11:00:00")
        LocalDateTime registradoAt
) {
}
