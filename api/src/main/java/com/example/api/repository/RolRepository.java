package com.example.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Rol;

/**
 * Repositorio para la entidad Rol.
 * Proporciona métodos para acceder y manipular los datos de roles en la base de datos.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, String> {

    /**
     * Busca un rol por su nombre exacto.
     * Solo incluye roles no eliminados (deleted_at IS NULL).
     *
     * @param nombre El nombre del rol a buscar
     * @return Optional con el rol si existe
     */
    @Query("SELECT r FROM Rol r WHERE r.nombre = :nombre AND r.deletedAt IS NULL")
    Optional<Rol> findByNombre(@Param("nombre") String nombre);

    /**
     * Busca roles cuyo nombre contenga el texto especificado (case-insensitive).
     * Solo incluye roles no eliminados.
     *
     * @param nombre Texto a buscar en el nombre del rol
     * @return Lista de roles que coinciden con el criterio
     */
    @Query("SELECT r FROM Rol r WHERE LOWER(r.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND r.deletedAt IS NULL")
    List<Rol> searchByNombre(@Param("nombre") String nombre);

    /**
     * Obtiene todos los roles activos (no eliminados).
     *
     * @return Lista de roles activos
     */
    @Query("SELECT r FROM Rol r WHERE r.deletedAt IS NULL")
    List<Rol> findAllActive();

    /**
     * Obtiene todos los roles eliminados (soft delete).
     *
     * @return Lista de roles eliminados
     */
    @Query("SELECT r FROM Rol r WHERE r.deletedAt IS NOT NULL")
    List<Rol> findAllDeleted();

    /**
     * Verifica si existe un rol con el nombre especificado (excluyendo un ID opcional).
     * Útil para validar duplicados al crear o actualizar.
     *
     * @param nombre El nombre a verificar
     * @param id ID a excluir de la búsqueda (puede ser null)
     * @return true si existe otro rol con ese nombre
     */
    @Query("SELECT COUNT(r) > 0 FROM Rol r WHERE r.nombre = :nombre AND r.deletedAt IS NULL AND (r.id != :id OR :id IS NULL)")
    boolean existsByNombreAndIdNot(@Param("nombre") String nombre, @Param("id") String id);
}
