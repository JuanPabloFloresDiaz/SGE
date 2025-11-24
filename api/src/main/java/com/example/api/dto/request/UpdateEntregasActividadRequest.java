package com.example.api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record UpdateEntregasActividadRequest(
        @DecimalMin(value = "0.0", message = "La nota debe ser mayor o igual a 0") @DecimalMax(value = "10.0", message = "La nota no puede ser mayor a 10") BigDecimal nota,

        @Size(max = 500, message = "La URL del documento no puede exceder los 500 caracteres") String documentoUrl,

        @Size(max = 255, message = "El comentario no puede exceder los 255 caracteres") String comentarioProfesor) {
}
