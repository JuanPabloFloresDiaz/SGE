package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de una asignatura existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar una asignatura existente")
public record UpdateAsignaturaRequest(

        @Schema(description = "Nuevo código de la asignatura", example = "MAT-102")
        @Size(max = 50, message = "El código no puede exceder 50 caracteres")
        String codigo,

        @Schema(description = "Nuevo nombre de la asignatura", example = "Matemáticas II")
        @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
        String nombre,

        @Schema(description = "Nueva descripción de la asignatura", example = "Continuación del cálculo diferencial e integral")
        String descripcion,

        @Schema(description = "Nueva URL de la imagen de la asignatura", example = "uploads/asignaturas/nueva-imagen.jpg")
        @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
        String imagenUrl
) {
}
