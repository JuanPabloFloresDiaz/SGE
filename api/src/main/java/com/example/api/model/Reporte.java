package com.example.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un reporte generado en el sistema.
 * Puede ser reporte de calificaciones, asistencia, estadísticas, etc.
 */
@Entity
@Table(name = "reportes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reporte extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 9)
    private TipoReporte tipo = TipoReporte.otro;

    @Enumerated(EnumType.STRING)
    @Column(name = "peso", length = 10)
    private PesoReporte peso = PesoReporte.leve;

    @Column(name = "titulo", length = 200)
    private String titulo;

    @Lob
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "creado_por")
    private Usuario creadoPor;

    /**
     * Enum para el tipo de reporte
     */
    public enum TipoReporte {
        @JsonProperty("CONDUCTA")
        conducta,
        @JsonProperty("ACADEMICO")
        academico,
        @JsonProperty("OTRO")
        otro
    }

    /**
     * Enum para el peso/severidad del reporte
     */
    public enum PesoReporte {
        @JsonProperty("LEVE")
        leve,
        @JsonProperty("MODERADO")
        moderado,
        @JsonProperty("GRAVE")
        grave
    }
}