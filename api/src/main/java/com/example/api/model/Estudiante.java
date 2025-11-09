package com.example.api.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un estudiante del sistema educativo.
 */
@Entity
@Table(name = "estudiantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "codigo_estudiante", unique = true, length = 50)
    private String codigoEstudiante;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero", length = 1)
    private Genero genero = Genero.O;

    @Column(name = "ingreso")
    private LocalDate ingreso;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    // Relaciones
    @OneToMany(mappedBy = "estudiante")
    private List<Inscripcion> inscripciones;

    @OneToMany(mappedBy = "estudiante")
    private List<Asistencia> asistencias;

    @OneToMany(mappedBy = "estudiante")
    private List<Calificacion> calificaciones;

    /**
     * Enum para el género del estudiante
     */
    public enum Genero {
        M, // Masculino
        F, // Femenino
        O  // Otro
    }
}
