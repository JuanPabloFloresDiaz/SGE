package com.example.api.model;

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
 * Entidad que representa un profesor del sistema educativo.
 */
@Entity
@Table(name = "profesores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Profesor extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "especialidad", length = 150)
    private String especialidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "contrato", length = 15)
    private TipoContrato contrato = TipoContrato.eventual;

    @Column(name = "activo")
    private Boolean activo = true;

    /**
     * Enum para el tipo de contrato
     */
    public enum TipoContrato {
        tiempo_completo,
        medio_tiempo,
        eventual
    }

    // Relaciones
    @OneToMany(mappedBy = "profesor")
    private List<Curso> cursos;
}
