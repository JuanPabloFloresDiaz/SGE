package com.example.api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Clase;

/**
 * Repositorio para la entidad Clase.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface ClaseRepository extends JpaRepository<Clase, String> {

    /**
     * Obtiene todas las clases activas (no eliminadas).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de clases activas
     */
    @Query("SELECT c FROM Clase c WHERE c.deletedAt IS NULL")
    Page<Clase> findAllActive(Pageable pageable);

    /**
     * Obtiene todas las clases activas sin paginación.
     *
     * @return Lista de clases activas
     */
    @Query("SELECT c FROM Clase c WHERE c.deletedAt IS NULL")
    List<Clase> findAllActive();

    /**
     * Obtiene todas las clases eliminadas (soft delete).
     *
     * @return Lista de clases eliminadas
     */
    @Query("SELECT c FROM Clase c WHERE c.deletedAt IS NOT NULL")
    List<Clase> findAllDeleted();

    /**
     * Busca todas las clases de un curso específico.
     * Solo incluye clases no eliminadas, ordenadas por fecha y hora.
     *
     * @param cursoId ID del curso
     * @return Lista de clases del curso
     */
    @Query("SELECT c FROM Clase c WHERE c.curso.id = :cursoId AND c.deletedAt IS NULL ORDER BY c.fecha DESC, c.inicio ASC")
    List<Clase> findByCursoId(@Param("cursoId") String cursoId);

    /**
     * Busca todas las clases por fecha específica.
     * Solo incluye clases no eliminadas, ordenadas por hora de inicio.
     *
     * @param fecha Fecha de las clases
     * @return Lista de clases en la fecha especificada
     */
    @Query("SELECT c FROM Clase c WHERE c.fecha = :fecha AND c.deletedAt IS NULL ORDER BY c.inicio ASC")
    List<Clase> findByFecha(@Param("fecha") LocalDate fecha);

    /**
     * Obtiene todas las clases ordenadas por fecha y hora.
     * Solo incluye clases no eliminadas.
     *
     * @return Lista de clases ordenadas
     */
    @Query("SELECT c FROM Clase c WHERE c.deletedAt IS NULL ORDER BY c.fecha DESC, c.inicio ASC")
    List<Clase> findAllOrdenadas();
}
