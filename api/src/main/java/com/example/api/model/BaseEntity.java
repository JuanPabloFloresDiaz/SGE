package com.example.api.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad base que contiene campos comunes para auditoría y borrado lógico.
 * Implementa el principio DRY evitando repetir estos campos en cada entidad.
 * Sigue el principio SOLID (SRP) al tener una sola responsabilidad: proveer funcionalidad de auditoría.
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
public abstract class BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Realiza un borrado lógico estableciendo la fecha de eliminación.
     * Implementa el principio KISS manteniendo la lógica simple.
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Verifica si la entidad ha sido borrada lógicamente.
     * @return true si la entidad está marcada como eliminada
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * Restaura una entidad borrada lógicamente.
     */
    public void restore() {
        this.deletedAt = null;
    }
}
