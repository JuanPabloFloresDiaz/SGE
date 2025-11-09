package com.example.api.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Unidad.
 */
@Schema(description = "Información completa de una unidad didáctica")
public record UnidadResponse(

        @Schema(description = "ID único de la unidad", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información del curso")
        CursoResponse curso,

        @Schema(description = "Título de la unidad", example = "Introducción a la Programación")
        String titulo,

        @Schema(description = "Descripción de la unidad", example = "En esta unidad aprenderemos los conceptos básicos")
        String descripcion,

        @Schema(description = "Número de la unidad", example = "1")
        Integer numero,

        @Schema(description = "URL del documento de la unidad", example = "uploads/unidades/documento123.pdf")
        String documentoUrl,

        @Schema(description = "Nombre del documento de la unidad", example = "material-unidad1.pdf")
        String documentoNombre,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
