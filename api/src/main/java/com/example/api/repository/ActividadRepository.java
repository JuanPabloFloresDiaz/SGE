package com.example.api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Actividad;

/**
 * Repositorio para la entidad Actividad.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface ActividadRepository extends JpaRepository<Actividad, String> {

    /**
     * Obtiene todas las actividades activas (no eliminadas).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de actividades activas
     */
    @Query("SELECT a FROM Actividad a WHERE a.deletedAt IS NULL")
    Page<Actividad> findAllActive(Pageable pageable);

    /**
     * Obtiene todas las actividades activas sin paginación.
     *
     * @return Lista de actividades activas
     */
    @Query("SELECT a FROM Actividad a WHERE a.deletedAt IS NULL")
    List<Actividad> findAllActive();

    /**
     * Obtiene todas las actividades eliminadas (soft delete).
     *
     * @return Lista de actividades eliminadas
     */
    @Query("SELECT a FROM Actividad a WHERE a.deletedAt IS NOT NULL")
    List<Actividad> findAllDeleted();

    /**
     * Busca todas las actividades de una asignatura específica.
     * Solo incluye actividades no eliminadas.
     *
     * @param asignaturaId ID de la asignatura
     * @return Lista de actividades de la asignatura
     */
    @Query("SELECT a FROM Actividad a WHERE a.asignatura.id = :asignaturaId AND a.deletedAt IS NULL ORDER BY a.fechaApertura DESC")
    List<Actividad> findByAsignaturaId(@Param("asignaturaId") String asignaturaId);

    /**
     * Busca todas las actividades de un profesor específico.
     * Solo incluye actividades no eliminadas.
     *
     * @param profesorId ID del profesor
     * @return Lista de actividades del profesor
     */
    @Query("SELECT a FROM Actividad a WHERE a.profesor.id = :profesorId AND a.deletedAt IS NULL ORDER BY a.fechaApertura DESC")
    List<Actividad> findByProfesorId(@Param("profesorId") String profesorId);

    /**
     * Obtiene las actividades actualmente abiertas (fecha actual entre apertura y cierre).
     * Solo incluye actividades no eliminadas y activas.
     *
     * @param fechaActual Fecha y hora actual
     * @return Lista de actividades abiertas
     */
    @Query("SELECT a FROM Actividad a WHERE :fechaActual BETWEEN a.fechaApertura AND a.fechaCierre AND a.deletedAt IS NULL AND a.activo = true ORDER BY a.fechaCierre ASC")
    List<Actividad> findActividadesAbiertas(@Param("fechaActual") LocalDateTime fechaActual);

    /**
     * Obtiene las próximas actividades (fecha de apertura futura).
     * Solo incluye actividades no eliminadas y activas.
     *
     * @param fechaActual Fecha y hora actual
     * @return Lista de próximas actividades
     */
    @Query("SELECT a FROM Actividad a WHERE a.fechaApertura > :fechaActual AND a.deletedAt IS NULL AND a.activo = true ORDER BY a.fechaApertura ASC")
    List<Actividad> findProximasActividades(@Param("fechaActual") LocalDateTime fechaActual);
}
