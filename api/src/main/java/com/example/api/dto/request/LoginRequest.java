package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la petición de login.
 */
@Schema(description = "Datos de inicio de sesión")
public record LoginRequest(
        
        @Schema(description = "Nombre de usuario", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El username es obligatorio")
        String username,
        
        @Schema(description = "Contraseña", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
