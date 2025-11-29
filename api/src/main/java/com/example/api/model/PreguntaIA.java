package com.example.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "preguntas_ia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaIA extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_id", nullable = false)
    private Conversacion conversacion;

    @Column(name = "pregunta", columnDefinition = "TEXT", nullable = false)
    private String pregunta;

    @Column(name = "respuesta", columnDefinition = "TEXT")
    private String respuesta;
}
