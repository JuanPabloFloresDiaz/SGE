package com.example.api.model;

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
 * Entidad que representa el registro de asistencia de un estudiante a una clase.
 */
@Entity
@Table(name = "asistencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asistencia extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "clase_id", nullable = false)
    private Clase clase;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 12)
    private EstadoAsistencia estado = EstadoAsistencia.AUSENTE;

    @Column(name = "observacion", length = 255)
    private String observacion;

    @ManyToOne
    @JoinColumn(name = "registrado_por")
    private Usuario registradoPor;

    @Column(name = "registrado_at", nullable = false)
    private java.time.LocalDateTime registradoAt = java.time.LocalDateTime.now();

    /**
     * Enum para el estado de asistencia
     */
    public enum EstadoAsistencia {
        PRESENTE,
        AUSENTE,
        TARDE,
        JUSTIFICADO
    }
}
