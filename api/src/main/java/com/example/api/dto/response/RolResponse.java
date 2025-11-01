package com.example.api.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Rol.
 * Incluye todos los campos relevantes excepto las relaciones.
 */
@Schema(description = "Información completa de un rol")
public record RolResponse(

        @Schema(description = "ID único del rol", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Nombre del rol", example = "Administrador")
        String nombre,

        @Schema(description = "Descripción del rol", example = "Usuario con acceso completo")
        String descripcion,

        @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-20T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
    // El método fromEntity se movió a RolService para evitar problemas con Lombok en el IDE
}
