package com.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo usuario.
 * Usa Java Record para inmutabilidad y validaciones.
 */
@Schema(description = "Datos necesarios para crear un nuevo usuario")
public record CreateUsuarioRequest(

        @Schema(description = "Nombre de usuario único", example = "jperez", required = true)
        @NotBlank(message = "El username es obligatorio")
        @Size(min = 3, max = 100, message = "El username debe tener entre 3 y 100 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El username solo puede contener letras, números, puntos, guiones y guiones bajos")
        String username,

        @Schema(description = "Contraseña del usuario", example = "Password123!", required = true)
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
                message = "La contraseña debe contener al menos una mayúscula, una minúscula y un número")
        String password,

        @Schema(description = "Nombre completo del usuario", example = "Juan Pérez García")
        @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
        String nombre,

        @Schema(description = "Email único del usuario", example = "juan.perez@ejemplo.com")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 150, message = "El email no puede exceder 150 caracteres")
        String email,

        @Schema(description = "Teléfono del usuario", example = "+506 8888-8888")
        @Size(max = 30, message = "El teléfono no puede exceder 30 caracteres")
        @Pattern(regexp = "^[+]?[0-9\\s-()]+$", message = "El teléfono solo puede contener números, espacios, guiones, paréntesis y el símbolo +")
        String telefono,

        @Schema(description = "ID del rol asignado al usuario", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El rol es obligatorio")
        String rolId,

        @Schema(description = "Indica si el usuario está activo", example = "true", defaultValue = "true")
        @NotNull(message = "El estado activo es obligatorio")
        Boolean activo,

        @Schema(description = "URL de la foto de perfil del usuario", example = "uploads/usuarios/foto123.jpg")
        @Size(max = 500, message = "La URL de la foto de perfil no puede exceder 500 caracteres")
        String fotoPerfilUrl
) {
}
