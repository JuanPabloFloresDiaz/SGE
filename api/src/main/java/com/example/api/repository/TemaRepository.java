package com.example.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Tema;

/**
 * Repositorio para la entidad Tema.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface TemaRepository extends JpaRepository<Tema, String> {

    /**
     * Obtiene todos los temas activos (no eliminados).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de temas activos
     */
    @Query("SELECT t FROM Tema t WHERE t.deletedAt IS NULL")
    Page<Tema> findAllActive(Pageable pageable);

    /**
     * Obtiene todos los temas activos sin paginación.
     *
     * @return Lista de temas activos
     */
    @Query("SELECT t FROM Tema t WHERE t.deletedAt IS NULL")
    List<Tema> findAllActive();

    /**
     * Obtiene todos los temas eliminados (soft delete).
     *
     * @return Lista de temas eliminados
     */
    @Query("SELECT t FROM Tema t WHERE t.deletedAt IS NOT NULL")
    List<Tema> findAllDeleted();

    /**
     * Busca todos los temas de una unidad específica.
     * Solo incluye temas no eliminados, ordenados por número.
     *
     * @param unidadId ID de la unidad
     * @return Lista de temas de la unidad
     */
    @Query("SELECT t FROM Tema t WHERE t.unidad.id = :unidadId AND t.deletedAt IS NULL ORDER BY t.numero ASC, t.createdAt ASC")
    List<Tema> findByUnidadId(@Param("unidadId") String unidadId);

    /**
     * Busca temas por título (búsqueda parcial case-insensitive).
     * Solo incluye temas no eliminados.
     *
     * @param titulo Título o parte del título a buscar
     * @return Lista de temas que coinciden con el título
     */
    @Query("SELECT t FROM Tema t WHERE LOWER(t.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')) AND t.deletedAt IS NULL")
    List<Tema> findByTituloContaining(@Param("titulo") String titulo);
}
