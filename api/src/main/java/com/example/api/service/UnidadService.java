package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateUnidadRequest;
import com.example.api.dto.request.UpdateUnidadRequest;
import com.example.api.dto.response.UnidadResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Curso;
import com.example.api.model.Unidad;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.UnidadRepository;
import com.example.api.mapper.UnidadMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de unidades.
 */
@Service
@Transactional
public class UnidadService {

    private final UnidadRepository unidadRepository;
    private final CursoRepository cursoRepository;
    private final UnidadMapper unidadMapper;

    /**
     * Constructor con inyección de dependencias.
     */
    public UnidadService(UnidadRepository unidadRepository,
            CursoRepository cursoRepository,
            UnidadMapper unidadMapper) {
        this.unidadRepository = unidadRepository;
        this.cursoRepository = cursoRepository;
        this.unidadMapper = unidadMapper;
    }

    /**
     * Obtiene todas las unidades activas (paginadas).
     */
    @Transactional(readOnly = true)
    public Page<UnidadResponse> getAllUnidades(Pageable pageable) {
        return unidadRepository.findAllActive(pageable)
                .map(unidadMapper::toResponse);
    }

    /**
     * Obtiene una unidad por su ID.
     */
    @Transactional(readOnly = true)
    public UnidadResponse getUnidadById(String id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));
        return unidadMapper.toResponse(unidad);
    }

    /**
     * Busca unidades por curso.
     */
    @Transactional(readOnly = true)
    public List<UnidadResponse> getUnidadesByCursoId(String cursoId) {
        return unidadRepository.findByCursoId(cursoId).stream()
                .map(unidadMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las unidades eliminadas.
     */
    @Transactional(readOnly = true)
    public List<UnidadResponse> getUnidadesDeleted() {
        return unidadRepository.findAllDeleted().stream()
                .map(unidadMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva unidad.
     */
    public UnidadResponse createUnidad(CreateUnidadRequest request) {
        // Validar que el curso existe
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));

        Unidad unidad = unidadMapper.toEntity(request);
        unidad.setCurso(curso);

        Unidad savedUnidad = unidadRepository.save(unidad);
        return unidadMapper.toResponse(savedUnidad);
    }

    /**
     * Actualiza una unidad existente.
     */
    public UnidadResponse updateUnidad(String id, UpdateUnidadRequest request) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));

        unidadMapper.updateEntityFromDto(request, unidad);

        Unidad updatedUnidad = unidadRepository.save(unidad);
        return unidadMapper.toResponse(updatedUnidad);
    }

    /**
     * Elimina lógicamente una unidad (soft delete).
     */
    public void deleteUnidad(String id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));
        unidad.setDeletedAt(LocalDateTime.now());
        unidadRepository.save(unidad);
    }

    /**
     * Elimina permanentemente una unidad.
     */
    public void permanentDeleteUnidad(String id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));
        unidadRepository.delete(unidad);
    }

    /**
     * Restaura una unidad eliminada.
     */
    public UnidadResponse restoreUnidad(String id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));
        unidad.setDeletedAt(null);
        Unidad restoredUnidad = unidadRepository.save(unidad);
        return unidadMapper.toResponse(restoredUnidad);
    }
}
