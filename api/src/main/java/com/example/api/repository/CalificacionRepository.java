package com.example.api.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Calificacion;

/**
 * Repositorio para la entidad Calificacion.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, String> {

    /**
     * Obtiene todas las calificaciones activas (no eliminadas).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de calificaciones activas
     */
    @Query("SELECT c FROM Calificacion c WHERE c.deletedAt IS NULL")
    Page<Calificacion> findAllActive(Pageable pageable);

    /**
     * Obtiene todas las calificaciones activas sin paginación.
     *
     * @return Lista de calificaciones activas
     */
    @Query("SELECT c FROM Calificacion c WHERE c.deletedAt IS NULL")
    List<Calificacion> findAllActive();

    /**
     * Obtiene todas las calificaciones eliminadas (soft delete).
     *
     * @return Lista de calificaciones eliminadas
     */
    @Query("SELECT c FROM Calificacion c WHERE c.deletedAt IS NOT NULL")
    List<Calificacion> findAllDeleted();

    /**
     * Busca todas las calificaciones de un estudiante específico.
     * Solo incluye calificaciones no eliminadas, ordenadas por fecha de creación DESC.
     *
     * @param estudianteId ID del estudiante
     * @return Lista de calificaciones del estudiante
     */
    @Query("SELECT c FROM Calificacion c WHERE c.estudiante.id = :estudianteId AND c.deletedAt IS NULL ORDER BY c.createdAt DESC")
    List<Calificacion> findByEstudianteId(@Param("estudianteId") String estudianteId);

    /**
     * Busca todas las calificaciones de una evaluación específica.
     * Solo incluye calificaciones no eliminadas, ordenadas por nota DESC.
     *
     * @param evaluacionId ID de la evaluación
     * @return Lista de calificaciones de la evaluación
     */
    @Query("SELECT c FROM Calificacion c WHERE c.evaluacion.id = :evaluacionId AND c.deletedAt IS NULL ORDER BY c.nota DESC")
    List<Calificacion> findByEvaluacionId(@Param("evaluacionId") String evaluacionId);

    /**
     * Calcula el promedio de notas de un estudiante.
     * Solo incluye calificaciones no eliminadas.
     *
     * @param estudianteId ID del estudiante
     * @return Promedio de notas
     */
    @Query("SELECT AVG(c.nota) FROM Calificacion c WHERE c.estudiante.id = :estudianteId AND c.deletedAt IS NULL")
    Double calcularPromedioEstudiante(@Param("estudianteId") String estudianteId);

    /**
     * Busca calificaciones en un rango de notas.
     * Solo incluye calificaciones no eliminadas, ordenadas por nota DESC.
     *
     * @param min Nota mínima (inclusive)
     * @param max Nota máxima (inclusive)
     * @return Lista de calificaciones en el rango
     */
    @Query("SELECT c FROM Calificacion c WHERE c.nota BETWEEN :min AND :max AND c.deletedAt IS NULL ORDER BY c.nota DESC")
    List<Calificacion> findByNotaRange(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    /**
     * Obtiene todas las calificaciones de un curso específico.
     * Solo incluye calificaciones no eliminadas.
     *
     * @param cursoId ID del curso
     * @return Lista de calificaciones del curso
     */
    @Query("SELECT c FROM Calificacion c WHERE c.evaluacion.curso.id = :cursoId AND c.deletedAt IS NULL")
    List<Calificacion> findByCursoId(@Param("cursoId") String cursoId);
}
