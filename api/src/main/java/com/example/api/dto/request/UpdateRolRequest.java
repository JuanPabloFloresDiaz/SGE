package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un rol existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un rol existente")
public record UpdateRolRequest(

        @Schema(description = "Nuevo nombre del rol", example = "Super Administrador")
        @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String nombre,

        @Schema(description = "Nueva descripción del rol", example = "Usuario con permisos administrativos avanzados")
        @Size(max = 255, message = "La descripción no puede exceder 255 caracteres")
        String descripcion
) {
}
