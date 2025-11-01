package com.example.api.model;

import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un bloque de horario disponible.
 * Ejemplo: 07:00-08:00, 08:00-09:00, etc.
 */
@Entity
@Table(name = "bloques_horario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BloqueHorario extends BaseEntity {

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "inicio", nullable = false)
    private LocalTime inicio;

    @Column(name = "fin", nullable = false)
    private LocalTime fin;

    // Relaciones
    @OneToMany(mappedBy = "bloqueHorario")
    private List<HorarioCurso> horariosCurso;
}
