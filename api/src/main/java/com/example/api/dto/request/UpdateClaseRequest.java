package com.example.api.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para la actualización de una clase existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar una clase existente")
public record UpdateClaseRequest(

        @Schema(description = "Nueva fecha de la clase", example = "2024-03-20")
        LocalDate fecha,

        @Schema(description = "Nueva hora de inicio", example = "09:00:00")
        LocalTime inicio,

        @Schema(description = "Nueva hora de fin", example = "11:00:00")
        LocalTime fin,

        @Schema(description = "Nuevo ID de la unidad", example = "550e8400-e29b-41d4-a716-446655440001")
        String unidadId,

        @Schema(description = "Nuevo ID del tema", example = "550e8400-e29b-41d4-a716-446655440002")
        String temaId,

        @Schema(description = "Nuevas notas sobre la clase", example = "Se repasó el tema anterior")
        String notas
) {
}
