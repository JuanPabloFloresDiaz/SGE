package com.example.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Asignatura;

/**
 * Repositorio para la entidad Asignatura.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, String> {

    /**
     * Obtiene todas las asignaturas activas (no eliminadas).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de asignaturas activas
     */
    @Query("SELECT a FROM Asignatura a WHERE a.deletedAt IS NULL ORDER BY a.nombre ASC")
    Page<Asignatura> findAllActive(Pageable pageable);

    /**
     * Obtiene todas las asignaturas activas sin paginación.
     *
     * @return Lista de asignaturas activas
     */
    @Query("SELECT a FROM Asignatura a WHERE a.deletedAt IS NULL ORDER BY a.nombre ASC")
    List<Asignatura> findAllActive();

    /**
     * Obtiene todas las asignaturas eliminadas (soft delete).
     *
     * @return Lista de asignaturas eliminadas
     */
    @Query("SELECT a FROM Asignatura a WHERE a.deletedAt IS NOT NULL")
    List<Asignatura> findAllDeleted();

    /**
     * Busca una asignatura por su código único.
     * Solo incluye asignaturas no eliminadas.
     *
     * @param codigo Código de la asignatura
     * @return La asignatura si existe
     */
    @Query("SELECT a FROM Asignatura a WHERE a.codigo = :codigo AND a.deletedAt IS NULL")
    Optional<Asignatura> findByCodigo(@Param("codigo") String codigo);

    /**
     * Busca asignaturas cuyo nombre contenga el texto especificado (case-insensitive).
     * Solo incluye asignaturas no eliminadas.
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de asignaturas que coinciden con el criterio
     */
    @Query("SELECT a FROM Asignatura a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND a.deletedAt IS NULL")
    List<Asignatura> searchByNombre(@Param("nombre") String nombre);

    /**
     * Verifica si existe una asignatura con el mismo código.
     * Útil para validar códigos únicos al crear.
     *
     * @param codigo El código a verificar
     * @return true si existe una asignatura con ese código
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Asignatura a WHERE a.codigo = :codigo AND a.deletedAt IS NULL")
    boolean existsByCodigo(@Param("codigo") String codigo);

    /**
     * Verifica si existe una asignatura con el mismo código (excluyendo un ID específico).
     * Útil para validar códigos únicos al actualizar.
     *
     * @param codigo El código a verificar
     * @param id El ID a excluir de la verificación
     * @return true si existe otra asignatura con ese código
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Asignatura a WHERE a.codigo = :codigo AND a.id != :id AND a.deletedAt IS NULL")
    boolean existsByCodigoAndIdNot(@Param("codigo") String codigo, @Param("id") String id);
}
