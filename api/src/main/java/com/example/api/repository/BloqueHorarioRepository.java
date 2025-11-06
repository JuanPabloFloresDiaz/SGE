package com.example.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.api.model.BloqueHorario;

/**
 * Repositorio para la entidad BloqueHorario.
 * Proporciona métodos JPA para operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface BloqueHorarioRepository extends JpaRepository<BloqueHorario, String> {

    /**
     * Obtiene todos los bloques de horario activos (no eliminados).
     * Soporta paginación.
     *
     * @param pageable Configuración de paginación
     * @return Página de bloques activos
     */
    @Query("SELECT b FROM BloqueHorario b WHERE b.deletedAt IS NULL")
    Page<BloqueHorario> findAllActive(Pageable pageable);

    /**
     * Obtiene todos los bloques de horario activos sin paginación.
     *
     * @return Lista de bloques activos
     */
    @Query("SELECT b FROM BloqueHorario b WHERE b.deletedAt IS NULL")
    List<BloqueHorario> findAllActive();

    /**
     * Obtiene todos los bloques eliminados (soft delete).
     *
     * @return Lista de bloques eliminados
     */
    @Query("SELECT b FROM BloqueHorario b WHERE b.deletedAt IS NOT NULL")
    List<BloqueHorario> findAllDeleted();

    /**
     * Obtiene todos los bloques activos ordenados por hora de inicio.
     * Útil para mostrar la secuencia de bloques del día.
     *
     * @return Lista de bloques ordenados por hora de inicio
     */
    @Query("SELECT b FROM BloqueHorario b WHERE b.deletedAt IS NULL ORDER BY b.inicio ASC")
    List<BloqueHorario> findAllActiveOrderedByInicio();
}
