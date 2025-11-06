package com.example.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para la respuesta de autenticación.
 */
@Schema(description = "Respuesta de autenticación exitosa")
public record AuthResponse(
        
        @Schema(description = "Token JWT de acceso", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,
        
        @Schema(description = "Tipo de token", example = "Bearer")
        String type,
        
        @Schema(description = "Información del usuario autenticado")
        UsuarioResponse usuario
) {
    /**
     * Constructor que establece el tipo de token por defecto como "Bearer".
     */
    public AuthResponse(String token, UsuarioResponse usuario) {
        this(token, "Bearer", usuario);
    }
}
