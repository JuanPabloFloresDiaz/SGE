package com.example.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Estudiante;
import com.example.api.model.Estudiante.Genero;

/**
 * Repositorio para la entidad Estudiante.
 * Proporciona métodos JPA para operaciones CRUD básicas.
 */
@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, String> {

    /**
     * Busca un estudiante por su código único.
     * Solo incluye estudiantes no eliminados.
     *
     * @param codigoEstudiante El código del estudiante a buscar
     * @return Optional con el estudiante si existe
     */
    @Query("SELECT e FROM Estudiante e WHERE e.codigoEstudiante = :codigo AND e.deletedAt IS NULL")
    Optional<Estudiante> findByCodigoEstudiante(@Param("codigo") String codigoEstudiante);

    /**
     * Obtiene todos los estudiantes activos (no eliminados).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de estudiantes activos
     */
    @Query("SELECT e FROM Estudiante e WHERE e.deletedAt IS NULL")
    Page<Estudiante> findAllActive(Pageable pageable);

    /**
     * Obtiene todos los estudiantes activos sin paginación.
     *
     * @return Lista de estudiantes activos
     */
    @Query("SELECT e FROM Estudiante e WHERE e.deletedAt IS NULL")
    List<Estudiante> findAllActive();

    /**
     * Obtiene estudiantes por género.
     * Solo incluye estudiantes activos y no eliminados.
     *
     * @param genero El género a filtrar
     * @return Lista de estudiantes del género especificado
     */
    @Query("SELECT e FROM Estudiante e WHERE e.genero = :genero AND e.deletedAt IS NULL")
    List<Estudiante> findByGenero(@Param("genero") Genero genero);

    /**
     * Obtiene solo estudiantes activos (activo = true) y no eliminados.
     *
     * @return Lista de estudiantes activos
     */
    @Query("SELECT e FROM Estudiante e WHERE e.activo = true AND e.deletedAt IS NULL")
    List<Estudiante> findByActivoTrue();

    /**
     * Obtiene todos los estudiantes eliminados (soft delete).
     *
     * @return Lista de estudiantes eliminados
     */
    @Query("SELECT e FROM Estudiante e WHERE e.deletedAt IS NOT NULL")
    List<Estudiante> findAllDeleted();

    /**
     * Verifica si existe un estudiante con el código especificado (excluyendo un ID opcional).
     * Útil para validar duplicados al crear o actualizar.
     *
     * @param codigo El código a verificar
     * @param id ID a excluir de la búsqueda (puede ser null)
     * @return true si existe otro estudiante con ese código
     */
    @Query("SELECT COUNT(e) > 0 FROM Estudiante e WHERE e.codigoEstudiante = :codigo AND e.deletedAt IS NULL AND (e.id != :id OR :id IS NULL)")
    boolean existsByCodigoEstudianteAndIdNot(@Param("codigo") String codigo, @Param("id") String id);
}
