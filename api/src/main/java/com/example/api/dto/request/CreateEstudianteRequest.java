package com.example.api.dto.request;

import java.time.LocalDate;

import com.example.api.model.Estudiante.Genero;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo estudiante.
 * Usa Java Record para inmutabilidad y validaciones.
 */
@Schema(description = "Datos necesarios para crear un nuevo estudiante")
public record CreateEstudianteRequest(

        @Schema(description = "ID del usuario asociado al estudiante", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID del usuario es obligatorio")
        String usuarioId,

        @Schema(description = "Código único del estudiante", example = "EST-2024-001", required = true)
        @NotBlank(message = "El código del estudiante es obligatorio")
        @Size(min = 5, max = 50, message = "El código debe tener entre 5 y 50 caracteres")
        @Pattern(regexp = "^[A-Z0-9-]+$", message = "El código solo puede contener letras mayúsculas, números y guiones")
        String codigoEstudiante,

        @Schema(description = "Fecha de nacimiento del estudiante", example = "2000-05-15", required = true)
        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        LocalDate fechaNacimiento,

        @Schema(description = "Dirección de residencia", example = "Av. Principal 123, San José")
        @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
        String direccion,

        @Schema(description = "Género del estudiante", example = "M", allowableValues = {"M", "F", "O"})
        Genero genero,

        @Schema(description = "Fecha de ingreso al sistema educativo", example = "2024-01-15")
        LocalDate ingreso,

        @Schema(description = "Indica si el estudiante está activo", example = "true", defaultValue = "true")
        Boolean activo
) {
}
