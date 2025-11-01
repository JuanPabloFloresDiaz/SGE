package com.example.api.model;

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
 * Entidad que representa un tema específico dentro de una unidad.
 * Ejemplo: Tema 1.1: Variables y Tipos de Datos
 */
@Entity
@Table(name = "temas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tema extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "unidad_id", nullable = false)
    private Unidad unidad;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Lob
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;
}
