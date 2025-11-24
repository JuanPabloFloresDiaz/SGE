package com.example.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TiposPonderacionCursoResponse(
        String id,
        String cursoId,
        String nombre,
        BigDecimal pesoPorcentaje,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
