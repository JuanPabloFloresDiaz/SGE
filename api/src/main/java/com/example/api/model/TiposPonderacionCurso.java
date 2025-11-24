package com.example.api.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que define los tipos de ponderación para un curso específico.
 * Ejemplo: "Exámenes" (40%), "Tareas" (30%), "Proyecto Final" (30%).
 * La suma de los pesos porcentuales de todos los tipos de un curso debe ser
 * 100%.
 */
@Entity
@Table(name = "tipos_ponderacion_curso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TiposPonderacionCurso extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "peso_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal pesoPorcentaje;
}
