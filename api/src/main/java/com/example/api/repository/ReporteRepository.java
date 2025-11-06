package com.example.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Reporte;
import com.example.api.model.Reporte.TipoReporte;

/**
 * Repositorio para la entidad Reporte.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface ReporteRepository extends JpaRepository<Reporte, String> {

    /**
     * Obtiene todos los reportes activos (no eliminados).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de reportes activos
     */
    @Query("SELECT r FROM Reporte r WHERE r.deletedAt IS NULL")
    Page<Reporte> findAllActive(Pageable pageable);

    /**
     * Obtiene todos los reportes activos sin paginación.
     *
     * @return Lista de reportes activos
     */
    @Query("SELECT r FROM Reporte r WHERE r.deletedAt IS NULL")
    List<Reporte> findAllActive();

    /**
     * Obtiene todos los reportes eliminados (soft delete).
     *
     * @return Lista de reportes eliminados
     */
    @Query("SELECT r FROM Reporte r WHERE r.deletedAt IS NOT NULL")
    List<Reporte> findAllDeleted();

    /**
     * Busca reportes por tipo específico.
     * Solo incluye reportes no eliminados.
     *
     * @param tipo Tipo de reporte
     * @return Lista de reportes del tipo especificado
     */
    @Query("SELECT r FROM Reporte r WHERE r.tipo = :tipo AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    List<Reporte> findByTipo(@Param("tipo") TipoReporte tipo);

    /**
     * Busca reportes creados por un usuario específico.
     * Solo incluye reportes no eliminados.
     *
     * @param usuarioId ID del usuario creador
     * @return Lista de reportes creados por el usuario
     */
    @Query("SELECT r FROM Reporte r WHERE r.creadoPor.id = :usuarioId AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    List<Reporte> findByCreadoPorId(@Param("usuarioId") String usuarioId);

    /**
     * Busca reportes de un estudiante específico.
     * Solo incluye reportes no eliminados.
     *
     * @param estudianteId ID del estudiante
     * @return Lista de reportes del estudiante
     */
    @Query("SELECT r FROM Reporte r WHERE r.estudiante.id = :estudianteId AND r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    List<Reporte> findByEstudianteId(@Param("estudianteId") String estudianteId);

    /**
     * Obtiene los reportes más recientes (últimos 10).
     * Solo incluye reportes no eliminados, ordenados por fecha de creación DESC.
     *
     * @return Lista de los 10 reportes más recientes
     */
    @Query(value = "SELECT r FROM Reporte r WHERE r.deletedAt IS NULL ORDER BY r.createdAt DESC")
    List<Reporte> findRecientes(Pageable pageable);
}
