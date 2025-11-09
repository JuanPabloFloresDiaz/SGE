package com.example.api.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la creación de una nueva clase.
 */
@Schema(description = "Datos necesarios para crear una nueva clase")
public record CreateClaseRequest(

        @Schema(description = "ID del curso", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID del curso es obligatorio")
        String cursoId,

        @Schema(description = "Fecha de la clase", example = "2024-03-15", required = true)
        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @Schema(description = "Hora de inicio", example = "08:00:00")
        LocalTime inicio,

        @Schema(description = "Hora de fin", example = "10:00:00")
        LocalTime fin,

        @Schema(description = "ID de la unidad", example = "550e8400-e29b-41d4-a716-446655440001")
        String unidadId,

        @Schema(description = "ID del tema", example = "550e8400-e29b-41d4-a716-446655440002")
        String temaId,

        @Schema(description = "Notas sobre la clase", example = "Se revisó el tema de variables")
        String notas,

        @Schema(description = "URL del documento de la clase", example = "uploads/clases/documento123.pdf")
        @jakarta.validation.constraints.Size(max = 500, message = "La URL del documento no puede exceder 500 caracteres")
        String documentoUrl,

        @Schema(description = "Nombre del documento de la clase", example = "presentacion-clase1.pdf")
        @jakarta.validation.constraints.Size(max = 255, message = "El nombre del documento no puede exceder 255 caracteres")
        String documentoNombre
) {
}
