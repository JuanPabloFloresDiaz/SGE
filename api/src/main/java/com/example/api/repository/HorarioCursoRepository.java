package com.example.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.HorarioCurso;
import com.example.api.model.HorarioCurso.DiaSemana;

/**
 * Repositorio para la entidad HorarioCurso.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface HorarioCursoRepository extends JpaRepository<HorarioCurso, String> {

    /**
     * Obtiene todos los horarios activos (no eliminados).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de horarios activos
     */
    @Query("SELECT h FROM HorarioCurso h WHERE h.deletedAt IS NULL")
    Page<HorarioCurso> findAllActive(Pageable pageable);

    /**
     * Obtiene todos los horarios activos sin paginación.
     *
     * @return Lista de horarios activos
     */
    @Query("SELECT h FROM HorarioCurso h WHERE h.deletedAt IS NULL")
    List<HorarioCurso> findAllActive();

    /**
     * Obtiene todos los horarios eliminados (soft delete).
     *
     * @return Lista de horarios eliminados
     */
    @Query("SELECT h FROM HorarioCurso h WHERE h.deletedAt IS NOT NULL")
    List<HorarioCurso> findAllDeleted();

    /**
     * Busca todos los horarios de un curso específico.
     * Solo incluye horarios no eliminados.
     *
     * @param cursoId ID del curso
     * @return Lista de horarios del curso
     */
    @Query("SELECT h FROM HorarioCurso h WHERE h.curso.id = :cursoId AND h.deletedAt IS NULL")
    List<HorarioCurso> findByCursoId(@Param("cursoId") String cursoId);

    /**
     * Busca horarios por día de la semana.
     * Solo incluye horarios no eliminados.
     *
     * @param dia Día de la semana
     * @return Lista de horarios del día especificado
     */
    @Query("SELECT h FROM HorarioCurso h WHERE h.dia = :dia AND h.deletedAt IS NULL")
    List<HorarioCurso> findByDia(@Param("dia") DiaSemana dia);

    /**
     * Detecta posibles conflictos de horarios.
     * Encuentra horarios que se solapan en el mismo día, bloque y aula.
     *
     * @return Lista de horarios con posibles conflictos
     */
    @Query("SELECT h FROM HorarioCurso h WHERE h.deletedAt IS NULL " +
           "AND EXISTS (SELECT h2 FROM HorarioCurso h2 WHERE h2.id != h.id " +
           "AND h2.dia = h.dia AND h2.bloqueHorario.id = h.bloqueHorario.id " +
           "AND h2.aula = h.aula AND h2.deletedAt IS NULL)")
    List<HorarioCurso> findConflictos();

    /**
     * Verifica si existe un horario para un curso en un día y bloque específico.
     * Útil para validar duplicados antes de crear.
     *
     * @param cursoId ID del curso
     * @param bloqueId ID del bloque de horario
     * @param dia Día de la semana
     * @return true si existe un horario con esos parámetros
     */
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END " +
           "FROM HorarioCurso h WHERE h.curso.id = :cursoId " +
           "AND h.bloqueHorario.id = :bloqueId AND h.dia = :dia " +
           "AND h.deletedAt IS NULL")
    boolean existsByCursoAndBloqueAndDia(@Param("cursoId") String cursoId,
                                          @Param("bloqueId") String bloqueId,
                                          @Param("dia") DiaSemana dia);
}
