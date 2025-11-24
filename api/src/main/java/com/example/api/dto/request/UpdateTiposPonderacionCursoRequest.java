package com.example.api.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public record UpdateTiposPonderacionCursoRequest(
        @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres") String nombre,

        @DecimalMin(value = "0.0", inclusive = false, message = "El peso debe ser mayor a 0") @DecimalMax(value = "100.0", inclusive = true, message = "El peso no puede ser mayor a 100") BigDecimal pesoPorcentaje) {
}
