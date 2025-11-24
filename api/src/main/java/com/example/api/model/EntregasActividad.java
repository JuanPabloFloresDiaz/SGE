package com.example.api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa la entrega de una actividad por parte de un
 * estudiante.
 */
@Entity
@Table(name = "entregas_actividad", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "actividad_id", "estudiante_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntregasActividad extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividad actividad;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(name = "nota", precision = 6, scale = 2)
    private BigDecimal nota;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega = LocalDateTime.now();

    @Column(name = "documento_url", length = 500)
    private String documentoUrl;

    @Column(name = "comentario_profesor", length = 255)
    private String comentarioProfesor;
}
