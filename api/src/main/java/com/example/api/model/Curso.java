package com.example.api.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un curso/grupo específico de una asignatura.
 * Un curso es una instancia de una asignatura en un periodo específico.
 */
@Entity
@Table(name = "cursos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Curso extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @ManyToOne
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @ManyToOne
    @JoinColumn(name = "periodo_id", nullable = false)
    private Periodo periodo;

    @Column(name = "nombre_grupo", length = 100)
    private String nombreGrupo;

    @Column(name = "aula_default", length = 100)
    private String aulaDefault;

    @Column(name = "cupo")
    private Integer cupo = 0;

    // Relaciones
    @OneToMany(mappedBy = "curso")
    private List<HorarioCurso> horarios;

    @OneToMany(mappedBy = "curso")
    private List<Inscripcion> inscripciones;

    @OneToMany(mappedBy = "curso")
    private List<Unidad> unidades;

    @OneToMany(mappedBy = "curso")
    private List<Clase> clases;

    @OneToMany(mappedBy = "curso")
    private List<Evaluacion> evaluaciones;
}
