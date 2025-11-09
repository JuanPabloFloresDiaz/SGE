package com.example.api.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa una evaluación específica de un curso.
 * Puede ser un examen, tarea, proyecto, etc.
 */
@Entity
@Table(name = "evaluaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evaluacion extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoEvaluacion tipoEvaluacion;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "peso", precision = 5, scale = 2)
    private java.math.BigDecimal peso = java.math.BigDecimal.ZERO;

    @Column(name = "publicado")
    private Boolean publicado = false;

    @Column(name = "documento_url", length = 500)
    private String documentoUrl;

    @Column(name = "documento_nombre", length = 255)
    private String documentoNombre;

    // Relaciones
    @OneToMany(mappedBy = "evaluacion")
    private List<Calificacion> calificaciones;
}
