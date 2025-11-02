package com.example.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Usuario;

/**
 * Repositorio para la entidad Usuario.
 * Proporciona métodos para acceder y manipular los datos de usuarios en la base de datos.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    /**
     * Busca un usuario por su username exacto.
     * Solo incluye usuarios no eliminados (deleted_at IS NULL).
     *
     * @param username El nombre de usuario a buscar
     * @return Optional con el usuario si existe
     */
    @Query("SELECT u FROM Usuario u WHERE u.username = :username AND u.deletedAt IS NULL")
    Optional<Usuario> findByUsername(@Param("username") String username);

    /**
     * Busca un usuario por su email exacto.
     * Solo incluye usuarios no eliminados.
     *
     * @param email El email del usuario a buscar
     * @return Optional con el usuario si existe
     */
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<Usuario> findByEmail(@Param("email") String email);

    /**
     * Obtiene todos los usuarios activos (no eliminados).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de usuarios activos
     */
    @Query("SELECT u FROM Usuario u WHERE u.deletedAt IS NULL")
    Page<Usuario> findAllActive(Pageable pageable);

    /**
     * Obtiene todos los usuarios activos sin paginación.
     *
     * @return Lista de usuarios activos
     */
    @Query("SELECT u FROM Usuario u WHERE u.deletedAt IS NULL")
    List<Usuario> findAllActive();

    /**
     * Obtiene todos los usuarios eliminados (soft delete).
     *
     * @return Lista de usuarios eliminados
     */
    @Query("SELECT u FROM Usuario u WHERE u.deletedAt IS NOT NULL")
    List<Usuario> findAllDeleted();

    /**
     * Obtiene usuarios por rol.
     * Solo incluye usuarios activos y no eliminados.
     *
     * @param rolId ID del rol
     * @return Lista de usuarios con ese rol
     */
    @Query("SELECT u FROM Usuario u WHERE u.rol.id = :rolId AND u.deletedAt IS NULL")
    List<Usuario> findByRolId(@Param("rolId") String rolId);

    /**
     * Obtiene solo usuarios activos (activo = true) y no eliminados.
     *
     * @return Lista de usuarios activos
     */
    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND u.deletedAt IS NULL")
    List<Usuario> findByActivoTrue();

    /**
     * Busca usuarios cuyo username contenga el texto especificado (case-insensitive).
     * Solo incluye usuarios no eliminados.
     *
     * @param username Texto a buscar en el username
     * @return Lista de usuarios que coinciden con el criterio
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :username, '%')) AND u.deletedAt IS NULL")
    List<Usuario> searchByUsername(@Param("username") String username);

    /**
     * Busca usuarios cuyo nombre contenga el texto especificado (case-insensitive).
     * Solo incluye usuarios no eliminados.
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de usuarios que coinciden con el criterio
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND u.deletedAt IS NULL")
    List<Usuario> searchByNombre(@Param("nombre") String nombre);

    /**
     * Verifica si existe un usuario con el username especificado (excluyendo un ID opcional).
     * Útil para validar duplicados al crear o actualizar.
     *
     * @param username El username a verificar
     * @param id ID a excluir de la búsqueda (puede ser null)
     * @return true si existe otro usuario con ese username
     */
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.username = :username AND u.deletedAt IS NULL AND (u.id != :id OR :id IS NULL)")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("id") String id);

    /**
     * Verifica si existe un usuario con el email especificado (excluyendo un ID opcional).
     * Útil para validar duplicados al crear o actualizar.
     *
     * @param email El email a verificar
     * @param id ID a excluir de la búsqueda (puede ser null)
     * @return true si existe otro usuario con ese email
     */
    @Query("SELECT COUNT(u) > 0 FROM Usuario u WHERE u.email = :email AND u.deletedAt IS NULL AND (u.id != :id OR :id IS NULL)")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") String id);
}
