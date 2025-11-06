package com.example.api.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Periodo;

/**
 * Repositorio para la entidad Periodo.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface PeriodoRepository extends JpaRepository<Periodo, String> {

    /**
     * Obtiene todos los periodos activos (no eliminados).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de periodos activos
     */
    @Query("SELECT p FROM Periodo p WHERE p.deletedAt IS NULL")
    Page<Periodo> findAllActive(Pageable pageable);

    /**
     * Obtiene todos los periodos activos sin paginación.
     *
     * @return Lista de periodos activos
     */
    @Query("SELECT p FROM Periodo p WHERE p.deletedAt IS NULL")
    List<Periodo> findAllActive();

    /**
     * Obtiene solo periodos activos (activo = true) y no eliminados.
     *
     * @return Lista de periodos activos
     */
    @Query("SELECT p FROM Periodo p WHERE p.activo = true AND p.deletedAt IS NULL")
    List<Periodo> findByActivoTrue();

    /**
     * Obtiene todos los periodos eliminados (soft delete).
     *
     * @return Lista de periodos eliminados
     */
    @Query("SELECT p FROM Periodo p WHERE p.deletedAt IS NOT NULL")
    List<Periodo> findAllDeleted();

    /**
     * Busca periodos cuyo nombre contenga el texto especificado (case-insensitive).
     * Solo incluye periodos no eliminados.
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de periodos que coinciden con el criterio
     */
    @Query("SELECT p FROM Periodo p WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.deletedAt IS NULL")
    List<Periodo> searchByNombre(@Param("nombre") String nombre);

    /**
     * Encuentra el periodo actual basado en la fecha actual.
     * Un periodo es actual si la fecha actual está entre fechaInicio y fechaFin.
     *
     * @param fecha Fecha actual para comparar
     * @return El periodo actual si existe
     */
    @Query("SELECT p FROM Periodo p WHERE :fecha BETWEEN p.fechaInicio AND p.fechaFin AND p.activo = true AND p.deletedAt IS NULL")
    Optional<Periodo> findPeriodoActual(@Param("fecha") LocalDate fecha);

    /**
     * Verifica si existe un periodo con el mismo nombre (excluyendo un ID específico).
     * Útil para validar nombres únicos al actualizar.
     *
     * @param nombre El nombre a verificar
     * @param id El ID a excluir de la verificación
     * @return true si existe otro periodo con ese nombre
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Periodo p WHERE LOWER(p.nombre) = LOWER(:nombre) AND p.id != :id AND p.deletedAt IS NULL")
    boolean existsByNombreAndIdNot(@Param("nombre") String nombre, @Param("id") String id);
}
