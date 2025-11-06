package com.example.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.Curso;

/**
 * Repositorio para la entidad Curso.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, String> {

    /**
     * Obtiene todos los cursos activos (no eliminados).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de cursos activos
     */
    @Query("SELECT c FROM Curso c WHERE c.deletedAt IS NULL")
    Page<Curso> findAllActive(Pageable pageable);

    /**
     * Obtiene todos los cursos activos sin paginación.
     *
     * @return Lista de cursos activos
     */
    @Query("SELECT c FROM Curso c WHERE c.deletedAt IS NULL")
    List<Curso> findAllActive();

    /**
     * Obtiene todos los cursos eliminados (soft delete).
     *
     * @return Lista de cursos eliminados
     */
    @Query("SELECT c FROM Curso c WHERE c.deletedAt IS NOT NULL")
    List<Curso> findAllDeleted();

    /**
     * Busca cursos por periodo académico.
     * Solo incluye cursos no eliminados.
     *
     * @param periodoId ID del periodo
     * @return Lista de cursos del periodo
     */
    @Query("SELECT c FROM Curso c WHERE c.periodo.id = :periodoId AND c.deletedAt IS NULL")
    List<Curso> findByPeriodoId(@Param("periodoId") String periodoId);

    /**
     * Busca cursos asignados a un profesor específico.
     * Solo incluye cursos no eliminados.
     *
     * @param profesorId ID del profesor
     * @return Lista de cursos del profesor
     */
    @Query("SELECT c FROM Curso c WHERE c.profesor.id = :profesorId AND c.deletedAt IS NULL")
    List<Curso> findByProfesorId(@Param("profesorId") String profesorId);

    /**
     * Busca cursos de una asignatura específica.
     * Solo incluye cursos no eliminados.
     *
     * @param asignaturaId ID de la asignatura
     * @return Lista de cursos de la asignatura
     */
    @Query("SELECT c FROM Curso c WHERE c.asignatura.id = :asignaturaId AND c.deletedAt IS NULL")
    List<Curso> findByAsignaturaId(@Param("asignaturaId") String asignaturaId);

    /**
     * Busca cursos cuyo nombre de grupo contenga el texto especificado (case-insensitive).
     * Solo incluye cursos no eliminados.
     *
     * @param nombreGrupo Texto a buscar en el nombre del grupo
     * @return Lista de cursos que coinciden con el criterio
     */
    @Query("SELECT c FROM Curso c WHERE LOWER(c.nombreGrupo) LIKE LOWER(CONCAT('%', :nombreGrupo, '%')) AND c.deletedAt IS NULL")
    List<Curso> searchByNombreGrupo(@Param("nombreGrupo") String nombreGrupo);

    /**
     * Obtiene cursos que tienen cupos disponibles.
     * Un curso tiene cupo disponible si el número de inscripciones es menor que el cupo.
     * Solo incluye cursos no eliminados.
     *
     * @return Lista de cursos con cupos disponibles
     */
    @Query("SELECT c FROM Curso c WHERE c.deletedAt IS NULL AND " +
           "(SELECT COUNT(i) FROM Inscripcion i WHERE i.curso.id = c.id AND i.deletedAt IS NULL) < c.cupo")
    List<Curso> findCursosConCuposDisponibles();

    /**
     * Cuenta el número de inscripciones activas de un curso.
     *
     * @param cursoId ID del curso
     * @return Número de inscripciones activas
     */
    @Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.curso.id = :cursoId AND i.deletedAt IS NULL")
    Long countInscripcionesByCursoId(@Param("cursoId") String cursoId);
}
