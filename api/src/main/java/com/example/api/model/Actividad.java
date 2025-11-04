package com.example.api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa una actividad o tarea asignada por un profesor.
 * Las actividades pertenecen a una asignatura específica y tienen fechas de apertura y cierre.
 */
@Entity
@Table(name = "actividades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Actividad extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @ManyToOne
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Lob
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre", nullable = false)
    private LocalDateTime fechaCierre;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "documento_url", length = 500)
    private String documentoUrl;

    @Column(name = "documento_nombre", length = 255)
    private String documentoNombre;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    /**
     * Verifica si la actividad está abierta en el momento actual.
     * @return true si la fecha actual está entre la fecha de apertura y cierre
     */
    public boolean estaAbierta() {
        LocalDateTime ahora = LocalDateTime.now();
        return !ahora.isBefore(fechaApertura) && !ahora.isAfter(fechaCierre);
    }

    /**
     * Verifica si la actividad ya cerró.
     * @return true si la fecha actual es posterior a la fecha de cierre
     */
    public boolean estaCerrada() {
        return LocalDateTime.now().isAfter(fechaCierre);
    }

    /**
     * Verifica si la actividad aún no ha abierto.
     * @return true si la fecha actual es anterior a la fecha de apertura
     */
    public boolean noHaAbierto() {
        return LocalDateTime.now().isBefore(fechaApertura);
    }
}
