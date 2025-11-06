package com.example.api.dto.request;

import com.example.api.model.Profesor.TipoContrato;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para la actualización de un profesor existente.
 * Todos los campos son opcionales para permitir actualizaciones parciales.
 */
@Schema(description = "Datos para actualizar un profesor existente")
public record UpdateProfesorRequest(

        @Schema(description = "Nueva especialidad del profesor", example = "Física Cuántica")
        @Size(max = 150, message = "La especialidad no puede exceder 150 caracteres")
        String especialidad,

        @Schema(description = "Nuevo tipo de contrato", example = "MEDIO_TIEMPO", allowableValues = {"TIEMPO_COMPLETO", "MEDIO_TIEMPO", "EVENTUAL"})
        TipoContrato contrato,

        @Schema(description = "Nuevo estado activo del profesor", example = "false")
        Boolean activo
) {
}
