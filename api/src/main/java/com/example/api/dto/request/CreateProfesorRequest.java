package com.example.api.dto.request;

import com.example.api.model.Profesor.TipoContrato;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de un nuevo profesor.
 */
@Schema(description = "Datos necesarios para crear un nuevo profesor")
public record CreateProfesorRequest(

        @Schema(description = "ID del usuario asociado al profesor", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
        @NotBlank(message = "El ID del usuario es obligatorio")
        String usuarioId,

        @Schema(description = "Especialidad del profesor", example = "Matemáticas Avanzadas")
        @Size(max = 150, message = "La especialidad no puede exceder 150 caracteres")
        String especialidad,

        @Schema(description = "Tipo de contrato", example = "TIEMPO_COMPLETO", allowableValues = {"TIEMPO_COMPLETO", "MEDIO_TIEMPO", "EVENTUAL"})
        TipoContrato contrato,

        @Schema(description = "Indica si el profesor está activo", example = "true", defaultValue = "true")
        @NotNull(message = "El estado activo es obligatorio")
        Boolean activo
) {
}
