package com.example.api.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para la entidad Tema.
 */
@Schema(description = "Información completa de un tema")
public record TemaResponse(

        @Schema(description = "ID único del tema", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Información de la unidad")
        UnidadResponse unidad,

        @Schema(description = "Título del tema", example = "Variables y Tipos de Datos")
        String titulo,

        @Schema(description = "Descripción del tema", example = "En este tema aprenderemos sobre variables")
        String descripcion,

        @Schema(description = "Número del tema", example = "1")
        Integer numero,

        @Schema(description = "Duración estimada en minutos", example = "45")
        Integer duracionMinutos,

        @Schema(description = "URL del documento del tema", example = "uploads/temas/documento123.pdf")
        String documentoUrl,

        @Schema(description = "Nombre del documento del tema", example = "recurso-tema1.pdf")
        String documentoNombre,

        @Schema(description = "Fecha de creación", example = "2024-01-01T10:30:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización", example = "2024-01-05T15:45:00")
        LocalDateTime updatedAt,

        @Schema(description = "Fecha de eliminación (null si está activo)", example = "null")
        LocalDateTime deletedAt
) {
}
