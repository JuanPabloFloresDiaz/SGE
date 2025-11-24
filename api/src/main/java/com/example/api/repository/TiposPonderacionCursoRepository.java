package com.example.api.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.api.model.TiposPonderacionCurso;

@Repository
public interface TiposPonderacionCursoRepository extends JpaRepository<TiposPonderacionCurso, String> {

    @Query("SELECT t FROM TiposPonderacionCurso t WHERE t.deletedAt IS NULL ORDER BY t.nombre ASC")
    Page<TiposPonderacionCurso> findAllActive(Pageable pageable);

    @Query("SELECT t FROM TiposPonderacionCurso t WHERE t.curso.id = :cursoId AND t.deletedAt IS NULL ORDER BY t.nombre ASC")
    List<TiposPonderacionCurso> findByCursoId(@Param("cursoId") String cursoId);
}
