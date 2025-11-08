package com.example.api.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa la inscripción/matrícula de un estudiante en un curso.
 */
@Entity
@Table(name = "inscripciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inscripcion extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(name = "fecha_inscripcion")
    private LocalDate fechaInscripcion = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 11)
    private EstadoInscripcion estado = EstadoInscripcion.inscrito;

    /**
     * Enum para el estado de la inscripción
     */
    public enum EstadoInscripcion {
        inscrito,
        retirado,
        completado
    }
}
