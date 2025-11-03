package com.example.api.dto.request;

import java.time.LocalDate;

import com.example.api.model.Estudiante.Genero;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un estudiante existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un estudiante existente")
public record UpdateEstudianteRequest(

        @Schema(description = "Nuevo código del estudiante", example = "EST-2024-002")
        @Size(min = 5, max = 50, message = "El código debe tener entre 5 y 50 caracteres")
        @Pattern(regexp = "^[A-Z0-9-]+$", message = "El código solo puede contener letras mayúsculas, números y guiones")
        String codigoEstudiante,

        @Schema(description = "Nueva fecha de nacimiento", example = "2000-06-20")
        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        LocalDate fechaNacimiento,

        @Schema(description = "Nueva dirección de residencia", example = "Calle Secundaria 456, Heredia")
        @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
        String direccion,

        @Schema(description = "Nuevo género del estudiante", example = "F", allowableValues = {"M", "F", "O"})
        Genero genero,

        @Schema(description = "Nueva fecha de ingreso", example = "2024-02-01")
        LocalDate ingreso,

        @Schema(description = "Nuevo estado activo del estudiante", example = "false")
        Boolean activo
) {
}
