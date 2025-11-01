package com.example.api.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un tipo de evaluación.
 * Ejemplos: Examen Parcial, Examen Final, Tarea, Proyecto, etc.
 */
@Entity
@Table(name = "tipos_evaluacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoEvaluacion extends BaseEntity {

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "peso", precision = 5, scale = 2)
    private java.math.BigDecimal peso = java.math.BigDecimal.ZERO;

    // Relaciones
    @OneToMany(mappedBy = "tipoEvaluacion")
    private List<Evaluacion> evaluaciones;
}
