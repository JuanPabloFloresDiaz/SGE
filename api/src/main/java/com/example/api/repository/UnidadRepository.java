package com.example.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Unidad;

/**
 * Repositorio para la entidad Unidad.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface UnidadRepository extends JpaRepository<Unidad, String> {

    /**
     * Obtiene todas las unidades activas (no eliminadas).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de unidades activas
     */
    @Query("SELECT u FROM Unidad u WHERE u.deletedAt IS NULL")
    Page<Unidad> findAllActive(Pageable pageable);

    /**
     * Obtiene todas las unidades activas sin paginación.
     *
     * @return Lista de unidades activas
     */
    @Query("SELECT u FROM Unidad u WHERE u.deletedAt IS NULL")
    List<Unidad> findAllActive();

    /**
     * Obtiene todas las unidades eliminadas (soft delete).
     *
     * @return Lista de unidades eliminadas
     */
    @Query("SELECT u FROM Unidad u WHERE u.deletedAt IS NOT NULL")
    List<Unidad> findAllDeleted();

    /**
     * Busca todas las unidades de un curso específico.
     * Solo incluye unidades no eliminadas, ordenadas por número.
     *
     * @param cursoId ID del curso
     * @return Lista de unidades del curso
     */
    @Query("SELECT u FROM Unidad u WHERE u.curso.id = :cursoId AND u.deletedAt IS NULL ORDER BY u.numero ASC, u.createdAt ASC")
    List<Unidad> findByCursoId(@Param("cursoId") String cursoId);
}
