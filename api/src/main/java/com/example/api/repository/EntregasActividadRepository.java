package com.example.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.EntregasActividad;

@Repository
public interface EntregasActividadRepository extends JpaRepository<EntregasActividad, String> {

    @Query("SELECT e FROM EntregasActividad e WHERE e.deletedAt IS NULL ORDER BY e.fechaEntrega DESC")
    Page<EntregasActividad> findAllActive(Pageable pageable);

    @Query("SELECT e FROM EntregasActividad e WHERE e.actividad.id = :actividadId AND e.deletedAt IS NULL ORDER BY e.fechaEntrega DESC")
    List<EntregasActividad> findByActividadId(@Param("actividadId") String actividadId);

    @Query("SELECT e FROM EntregasActividad e WHERE e.estudiante.id = :estudianteId AND e.deletedAt IS NULL ORDER BY e.fechaEntrega DESC")
    List<EntregasActividad> findByEstudianteId(@Param("estudianteId") String estudianteId);

    @Query("SELECT e FROM EntregasActividad e WHERE e.actividad.id = :actividadId AND e.estudiante.id = :estudianteId AND e.deletedAt IS NULL")
    Optional<EntregasActividad> findByActividadIdAndEstudianteId(@Param("actividadId") String actividadId,
            @Param("estudianteId") String estudianteId);
}
