package com.example.api.dto.request;

import java.time.LocalDateTime;

import com.example.api.model.Asistencia.EstadoAsistencia;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo registro de asistencia.
 */
@Schema(description = "Datos necesarios para crear un registro de asistencia")
public record CreateAsistenciaRequest(

        @Schema(description = "ID de la clase", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID de la clase es obligatorio")
        String claseId,

        @Schema(description = "ID del estudiante", example = "550e8400-e29b-41d4-a716-446655440001", required = true)
        @NotBlank(message = "El ID del estudiante es obligatorio")
        String estudianteId,

        @Schema(description = "Estado de asistencia", example = "PRESENTE", required = true, allowableValues = {"PRESENTE", "AUSENTE", "TARDE", "JUSTIFICADO"})
        @NotNull(message = "El estado de asistencia es obligatorio")
        EstadoAsistencia estado,

        @Schema(description = "Observación sobre la asistencia", example = "Llegó 10 minutos tarde")
        @Size(max = 255, message = "La observación no puede exceder 255 caracteres")
        String observacion,

        @Schema(description = "ID del usuario que registra la asistencia", example = "550e8400-e29b-41d4-a716-446655440002")
        String registradoPorId,

        @Schema(description = "Fecha y hora del registro", example = "2024-03-15T10:30:00")
        LocalDateTime registradoAt
) {
}
