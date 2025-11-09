package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de una nueva asignatura.
 */
@Schema(description = "Datos necesarios para crear una nueva asignatura")
public record CreateAsignaturaRequest(

        @Schema(description = "Código único de la asignatura", example = "MAT-101", required = true)
        @NotBlank(message = "El código de la asignatura es obligatorio")
        @Size(max = 50, message = "El código no puede exceder 50 caracteres")
        String codigo,

        @Schema(description = "Nombre de la asignatura", example = "Matemáticas I", required = true)
        @NotBlank(message = "El nombre de la asignatura es obligatorio")
        @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
        String nombre,

        @Schema(description = "Descripción detallada de la asignatura", example = "Introducción al cálculo diferencial e integral")
        String descripcion,

        @Schema(description = "URL de la imagen de la asignatura", example = "uploads/asignaturas/imagen123.jpg")
        @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
        String imagenUrl
) {
}
