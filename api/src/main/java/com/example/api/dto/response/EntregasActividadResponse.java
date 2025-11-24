package com.example.api.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EntregasActividadResponse(
        String id,
        String actividadId,
        String estudianteId,
        BigDecimal nota,
        LocalDateTime fechaEntrega,
        String documentoUrl,
        String comentarioProfesor,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
