package com.example.api.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Usuario.
 * No incluye el password_hash por seguridad.
 */
@Schema(description = "Información completa de un usuario")
public record UsuarioResponse(

        @Schema(description = "ID único del usuario", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Nombre de usuario", example = "jperez")
        String username,

        @Schema(description = "Nombre completo del usuario", example = "Juan Pérez García")
        String nombre,

        @Schema(description = "Email del usuario", example = "juan.perez@ejemplo.com")
        String email,

        @Schema(description = "Teléfono del usuario", example = "+506 8888-8888")
        String telefono,

        @Schema(description = "Estado activo del usuario", example = "true")
        Boolean activo,

        @Schema(description = "URL de la foto de perfil del usuario", example = "uploads/usuarios/foto123.jpg")
        String fotoPerfilUrl,

        @Schema(description = "Información del rol asignado")
        RolResponse rol,

        @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-20T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
