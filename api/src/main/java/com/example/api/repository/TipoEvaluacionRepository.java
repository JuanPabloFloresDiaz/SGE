package com.example.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.TipoEvaluacion;

/**
 * Repositorio para la entidad TipoEvaluacion.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface TipoEvaluacionRepository extends JpaRepository<TipoEvaluacion, String> {

    /**
     * Obtiene todos los tipos de evaluación activos (no eliminados).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de tipos de evaluación activos
     */
    @Query("SELECT t FROM TipoEvaluacion t WHERE t.deletedAt IS NULL")
    Page<TipoEvaluacion> findAllActive(Pageable pageable);

    /**
     * Obtiene todos los tipos de evaluación activos sin paginación.
     *
     * @return Lista de tipos de evaluación activos
     */
    @Query("SELECT t FROM TipoEvaluacion t WHERE t.deletedAt IS NULL")
    List<TipoEvaluacion> findAllActive();

    /**
     * Obtiene todos los tipos de evaluación eliminados (soft delete).
     *
     * @return Lista de tipos de evaluación eliminados
     */
    @Query("SELECT t FROM TipoEvaluacion t WHERE t.deletedAt IS NOT NULL")
    List<TipoEvaluacion> findAllDeleted();

    /**
     * Busca un tipo de evaluación por nombre (case-insensitive).
     * Solo busca entre tipos activos (no eliminados).
     * Implementa búsqueda secuencial para lista pequeña.
     *
     * @param nombre Nombre del tipo de evaluación a buscar
     * @return Optional con el tipo de evaluación encontrado
     */
    @Query("SELECT t FROM TipoEvaluacion t WHERE LOWER(t.nombre) = LOWER(:nombre) AND t.deletedAt IS NULL")
    Optional<TipoEvaluacion> findByNombreIgnoreCase(@Param("nombre") String nombre);
}
