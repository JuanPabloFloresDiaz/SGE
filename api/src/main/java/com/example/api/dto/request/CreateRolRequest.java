package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo rol.
 * Usa Java Record para inmutabilidad y validaciones.
 */
@Schema(description = "Datos necesarios para crear un nuevo rol")
public record CreateRolRequest(

        @Schema(description = "Nombre único del rol", example = "Administrador", required = true)
        @NotBlank(message = "El nombre del rol es obligatorio")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String nombre,

        @Schema(description = "Descripción del rol", example = "Usuario con acceso completo al sistema")
        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        String descripcion
) {
}
