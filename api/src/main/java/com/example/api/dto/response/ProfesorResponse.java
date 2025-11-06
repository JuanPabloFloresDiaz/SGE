package com.example.api.dto.response;

import java.time.LocalDateTime;

import com.example.api.model.Profesor.TipoContrato;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Profesor.
 * Incluye información básica del usuario asociado.
 */
@Schema(description = "Información completa de un profesor")
public record ProfesorResponse(

        @Schema(description = "ID único del profesor", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información del usuario asociado")
        UsuarioResponse usuario,

        @Schema(description = "Especialidad del profesor", example = "Matemáticas Avanzadas")
        String especialidad,

        @Schema(description = "Tipo de contrato", example = "TIEMPO_COMPLETO")
        TipoContrato contrato,

        @Schema(description = "Estado activo del profesor", example = "true")
        Boolean activo,

        @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-20T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
