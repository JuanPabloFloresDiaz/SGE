package com.example.api.model;

import java.time.LocalDate;
import java.time.LocalTime;
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
 * Entidad que representa una sesión de clase específica.
 * Registra cada vez que se imparte una clase de un curso.
 */
@Entity
@Table(name = "clases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Clase extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "inicio")
    private LocalTime inicio;

    @Column(name = "fin")
    private LocalTime fin;

    @ManyToOne
    @JoinColumn(name = "unidad_id")
    private Unidad unidad;

    @ManyToOne
    @JoinColumn(name = "tema_id")
    private Tema tema;

    @Lob
    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    // Relaciones
    @OneToMany(mappedBy = "clase")
    private List<Asistencia> asistencias;
}
