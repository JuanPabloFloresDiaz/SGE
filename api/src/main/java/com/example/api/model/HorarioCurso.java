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
 * Entidad que representa el horario específico de un curso.
 * Define en qué día y bloque horario se imparte un curso.
 */
@Entity
@Table(name = "horarios_curso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioCurso extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "bloque_id", nullable = false)
    private BloqueHorario bloqueHorario;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia", nullable = false, length = 3)
    private DiaSemana dia;

    @Column(name = "aula", length = 100)
    private String aula;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 11)
    private TipoHorario tipo = TipoHorario.regular;

    /**
     * Enum para los días de la semana
     */
    public enum DiaSemana {
        LUN,
        MAR,
        MIE,
        JUE,
        VIE,
        SAB,
        DOM
    }

    /**
     * Enum para el tipo de horario
     */
    public enum TipoHorario {
        regular,
        laboratorio,
        taller,
        otro
    }
}
