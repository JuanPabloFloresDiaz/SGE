package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un usuario existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un usuario existente")
public record UpdateUsuarioRequest(

        @Schema(description = "Nuevo nombre de usuario", example = "jperez2024")
        @Size(min = 3, max = 100, message = "El username debe tener entre 3 y 100 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El username solo puede contener letras, números, puntos, guiones y guiones bajos")
        String username,

        @Schema(description = "Nueva contraseña del usuario", example = "NewPassword123!")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
                message = "La contraseña debe contener al menos una mayúscula, una minúscula y un número")
        String password,

        @Schema(description = "Nuevo nombre completo del usuario", example = "Juan Alberto Pérez García")
        @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
        String nombre,

        @Schema(description = "Nuevo email del usuario", example = "nuevo.email@ejemplo.com")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 150, message = "El email no puede exceder 150 caracteres")
        String email,

        @Schema(description = "Nuevo teléfono del usuario", example = "+506 9999-9999")
        @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
        @Pattern(regexp = "^[+]?[0-9\\s-()]+$", message = "El teléfono solo puede contener números, espacios, guiones, paréntesis y el símbolo +")
        String telefono,

        @Schema(description = "Nuevo ID del rol asignado al usuario", example = "550e8400-e29b-41d4-a716-446655440001")
        String rolId,

        @Schema(description = "Nuevo estado activo del usuario", example = "false")
        Boolean activo
) {
}
