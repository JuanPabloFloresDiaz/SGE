package com.example.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Asistencia;

/**
 * Repositorio para la entidad Asistencia.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, String> {

    /**
     * Obtiene todas las asistencias activas (no eliminadas).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de asistencias activas
     */
    @Query("SELECT a FROM Asistencia a WHERE a.deletedAt IS NULL")
    Page<Asistencia> findAllActive(Pageable pageable);

    /**
     * Obtiene todas las asistencias activas sin paginación.
     *
     * @return Lista de asistencias activas
     */
    @Query("SELECT a FROM Asistencia a WHERE a.deletedAt IS NULL")
    List<Asistencia> findAllActive();

    /**
     * Obtiene todas las asistencias eliminadas (soft delete).
     *
     * @return Lista de asistencias eliminadas
     */
    @Query("SELECT a FROM Asistencia a WHERE a.deletedAt IS NOT NULL")
    List<Asistencia> findAllDeleted();

    /**
     * Busca todas las asistencias de una clase específica.
     * Solo incluye asistencias no eliminadas.
     *
     * @param claseId ID de la clase
     * @return Lista de asistencias de la clase
     */
    @Query("SELECT a FROM Asistencia a WHERE a.clase.id = :claseId AND a.deletedAt IS NULL ORDER BY a.estudiante.codigoEstudiante ASC")
    List<Asistencia> findByClaseId(@Param("claseId") String claseId);

    /**
     * Busca todas las asistencias de un estudiante específico.
     * Solo incluye asistencias no eliminadas, ordenadas por fecha de registro.
     *
     * @param estudianteId ID del estudiante
     * @return Lista de asistencias del estudiante
     */
    @Query("SELECT a FROM Asistencia a WHERE a.estudiante.id = :estudianteId AND a.deletedAt IS NULL ORDER BY a.registradoAt DESC")
    List<Asistencia> findByEstudianteId(@Param("estudianteId") String estudianteId);
}
