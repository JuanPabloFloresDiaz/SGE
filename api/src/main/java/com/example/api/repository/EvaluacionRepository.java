package com.example.api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Evaluacion;

/**
 * Repositorio para la entidad Evaluacion.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, String> {

    /**
     * Obtiene todas las evaluaciones activas (no eliminadas).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de evaluaciones activas
     */
    @Query("SELECT e FROM Evaluacion e WHERE e.deletedAt IS NULL ORDER BY e.fecha DESC")
    Page<Evaluacion> findAllActive(Pageable pageable);

    /**
     * Obtiene todas las evaluaciones activas sin paginación.
     *
     * @return Lista de evaluaciones activas
     */
    @Query("SELECT e FROM Evaluacion e WHERE e.deletedAt IS NULL ORDER BY e.fecha DESC")
    List<Evaluacion> findAllActive();

    /**
     * Obtiene todas las evaluaciones eliminadas (soft delete).
     *
     * @return Lista de evaluaciones eliminadas
     */
    @Query("SELECT e FROM Evaluacion e WHERE e.deletedAt IS NOT NULL")
    List<Evaluacion> findAllDeleted();

    /**
     * Busca todas las evaluaciones de un curso específico.
     * Solo incluye evaluaciones no eliminadas.
     *
     * @param cursoId ID del curso
     * @return Lista de evaluaciones del curso
     */
    @Query("SELECT e FROM Evaluacion e WHERE e.curso.id = :cursoId AND e.deletedAt IS NULL ORDER BY e.fecha ASC")
    List<Evaluacion> findByCursoId(@Param("cursoId") String cursoId);

    /**
     * Busca todas las evaluaciones de un tipo específico.
     * Solo incluye evaluaciones no eliminadas.
     *
     * @param tipoId ID del tipo de evaluación
     * @return Lista de evaluaciones del tipo
     */
    @Query("SELECT e FROM Evaluacion e WHERE e.tipoEvaluacion.id = :tipoId AND e.deletedAt IS NULL ORDER BY e.fecha ASC")
    List<Evaluacion> findByTipoEvaluacionId(@Param("tipoId") String tipoId);

    /**
     * Obtiene las próximas evaluaciones (fecha >= hoy).
     * Solo incluye evaluaciones no eliminadas y publicadas.
     *
     * @param fechaActual Fecha actual
     * @return Lista de próximas evaluaciones
     */
    @Query("SELECT e FROM Evaluacion e WHERE e.fecha >= :fechaActual AND e.deletedAt IS NULL AND e.publicado = true ORDER BY e.fecha ASC")
    List<Evaluacion> findProximasEvaluaciones(@Param("fechaActual") LocalDate fechaActual);
}
