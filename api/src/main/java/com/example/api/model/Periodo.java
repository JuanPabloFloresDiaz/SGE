package com.example.api.model;

import java.time.LocalDate;
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
 * Entidad que representa un periodo académico (semestre, trimestre, etc.)
 */
@Entity
@Table(name = "periodos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Periodo extends BaseEntity {

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "activo")
    private Boolean activo = true;

    // Relaciones
    @OneToMany(mappedBy = "periodo")
    private List<Curso> cursos;
}
