package com.example.api.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa una asignatura o materia del plan de estudios.
 * Ejemplo: Matemáticas, Programación, Historia, etc.
 */
@Entity
@Table(name = "asignaturas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asignatura extends BaseEntity {

    @Column(name = "codigo", unique = true, length = 50)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Lob
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    // Relaciones
    @OneToMany(mappedBy = "asignatura")
    private List<Curso> cursos;
}
