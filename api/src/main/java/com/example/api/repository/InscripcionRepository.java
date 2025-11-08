package com.example.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Inscripcion;
import com.example.api.model.Inscripcion.EstadoInscripcion;

/**
 * Repositorio para la entidad Inscripcion.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, String> {

    /**
     * Obtiene todas las inscripciones activas (no eliminadas).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de inscripciones activas
     */
    @Query("SELECT i FROM Inscripcion i WHERE i.deletedAt IS NULL ORDER BY i.fechaInscripcion DESC")
    Page<Inscripcion> findAllActive(Pageable pageable);

    /**
     * Obtiene todas las inscripciones activas sin paginación.
     *
     * @return Lista de inscripciones activas
     */
    @Query("SELECT i FROM Inscripcion i WHERE i.deletedAt IS NULL ORDER BY i.fechaInscripcion DESC")
    List<Inscripcion> findAllActive();

    /**
     * Obtiene todas las inscripciones eliminadas (soft delete).
     *
     * @return Lista de inscripciones eliminadas
     */
    @Query("SELECT i FROM Inscripcion i WHERE i.deletedAt IS NOT NULL")
    List<Inscripcion> findAllDeleted();

    /**
     * Busca todas las inscripciones de un estudiante específico.
     * Solo incluye inscripciones no eliminadas.
     *
     * @param estudianteId ID del estudiante
     * @return Lista de inscripciones del estudiante
     */
    @Query("SELECT i FROM Inscripcion i WHERE i.estudiante.id = :estudianteId AND i.deletedAt IS NULL")
    List<Inscripcion> findByEstudianteId(@Param("estudianteId") String estudianteId);

    /**
     * Busca todas las inscripciones de un curso específico.
     * Solo incluye inscripciones no eliminadas.
     *
     * @param cursoId ID del curso
     * @return Lista de inscripciones del curso
     */
    @Query("SELECT i FROM Inscripcion i WHERE i.curso.id = :cursoId AND i.deletedAt IS NULL")
    List<Inscripcion> findByCursoId(@Param("cursoId") String cursoId);

    /**
     * Busca inscripciones por estado.
     * Solo incluye inscripciones no eliminadas.
     *
     * @param estado Estado de la inscripción
     * @return Lista de inscripciones con el estado especificado
     */
    @Query("SELECT i FROM Inscripcion i WHERE i.estado = :estado AND i.deletedAt IS NULL")
    List<Inscripcion> findByEstado(@Param("estado") EstadoInscripcion estado);

    /**
     * Verifica si existe una inscripción para un estudiante en un curso específico.
     * Útil para validar duplicados antes de crear.
     *
     * @param estudianteId ID del estudiante
     * @param cursoId ID del curso
     * @return true si existe una inscripción activa
     */
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END " +
           "FROM Inscripcion i WHERE i.estudiante.id = :estudianteId " +
           "AND i.curso.id = :cursoId AND i.deletedAt IS NULL")
    boolean existsByEstudianteAndCurso(@Param("estudianteId") String estudianteId,
                                        @Param("cursoId") String cursoId);

    /**
     * Obtiene el historial de inscripciones de un estudiante ordenadas por fecha descendente.
     * Incluye todas las inscripciones (activas y eliminadas).
     *
     * @param estudianteId ID del estudiante
     * @return Lista de inscripciones ordenadas por fecha más reciente
     */
    @Query("SELECT i FROM Inscripcion i WHERE i.estudiante.id = :estudianteId " +
           "ORDER BY i.fechaInscripcion DESC, i.createdAt DESC")
    List<Inscripcion> findHistorialByEstudianteId(@Param("estudianteId") String estudianteId);
}
