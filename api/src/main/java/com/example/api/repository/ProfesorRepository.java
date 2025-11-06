package com.example.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Profesor;

/**
 * Repositorio para la entidad Profesor.
 * Proporciona métodos JPA para operaciones CRUD básicas.
 */
@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, String> {

    /**
     * Obtiene todos los profesores activos (no eliminados).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de profesores activos
     */
    @Query("SELECT p FROM Profesor p WHERE p.deletedAt IS NULL")
    Page<Profesor> findAllActive(Pageable pageable);

    /**
     * Obtiene todos los profesores activos sin paginación.
     *
     * @return Lista de profesores activos
     */
    @Query("SELECT p FROM Profesor p WHERE p.deletedAt IS NULL")
    List<Profesor> findAllActive();

    /**
     * Obtiene solo profesores activos (activo = true) y no eliminados.
     *
     * @return Lista de profesores activos
     */
    @Query("SELECT p FROM Profesor p WHERE p.activo = true AND p.deletedAt IS NULL")
    List<Profesor> findByActivoTrue();

    /**
     * Obtiene todos los profesores eliminados (soft delete).
     *
     * @return Lista de profesores eliminados
     */
    @Query("SELECT p FROM Profesor p WHERE p.deletedAt IS NOT NULL")
    List<Profesor> findAllDeleted();

    /**
     * Busca profesores cuya especialidad contenga el texto especificado (case-insensitive).
     * Solo incluye profesores no eliminados.
     *
     * @param especialidad Texto a buscar en la especialidad
     * @return Lista de profesores que coinciden con el criterio
     */
    @Query("SELECT p FROM Profesor p WHERE LOWER(p.especialidad) LIKE LOWER(CONCAT('%', :especialidad, '%')) AND p.deletedAt IS NULL")
    List<Profesor> searchByEspecialidad(@Param("especialidad") String especialidad);

    /**
     * Busca un profesor por su usuario asociado.
     * Solo incluye profesores no eliminados.
     *
     * @param usuarioId ID del usuario
     * @return Lista de profesores asociados a ese usuario (debería ser 0 o 1)
     */
    @Query("SELECT p FROM Profesor p WHERE p.usuario.id = :usuarioId AND p.deletedAt IS NULL")
    List<Profesor> findByUsuarioId(@Param("usuarioId") String usuarioId);
}
