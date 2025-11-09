package com.example.api.model;

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
 * Entidad que representa una unidad didáctica dentro de un curso.
 * Ejemplo: Unidad 1: Introducción a la Programación
 */
@Entity
@Table(name = "unidades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Unidad extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Lob
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "documento_url", length = 500)
    private String documentoUrl;

    @Column(name = "documento_nombre", length = 255)
    private String documentoNombre;

    // Relaciones
    @OneToMany(mappedBy = "unidad")
    private List<Tema> temas;
}
