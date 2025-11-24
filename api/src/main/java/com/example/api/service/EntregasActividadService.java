package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateEntregasActividadRequest;
import com.example.api.dto.request.UpdateEntregasActividadRequest;
import com.example.api.dto.response.EntregasActividadResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.mapper.EntregasActividadMapper;
import com.example.api.model.Actividad;
import com.example.api.model.EntregasActividad;
import com.example.api.model.Estudiante;
import com.example.api.repository.ActividadRepository;
import com.example.api.repository.EntregasActividadRepository;
import com.example.api.repository.EstudianteRepository;

@Service
@Transactional
public class EntregasActividadService {

    private final EntregasActividadRepository repository;
    private final ActividadRepository actividadRepository;
    private final EstudianteRepository estudianteRepository;
    private final EntregasActividadMapper mapper;

    public EntregasActividadService(EntregasActividadRepository repository,
            ActividadRepository actividadRepository,
            EstudianteRepository estudianteRepository,
            EntregasActividadMapper mapper) {
        this.repository = repository;
        this.actividadRepository = actividadRepository;
        this.estudianteRepository = estudianteRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<EntregasActividadResponse> getAll(Pageable pageable) {
        return repository.findAllActive(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EntregasActividadResponse getById(String id) {
        EntregasActividad entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada con ID: " + id));
        return mapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<EntregasActividadResponse> getByActividadId(String actividadId) {
        return repository.findByActividadId(actividadId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EntregasActividadResponse> getByEstudianteId(String estudianteId) {
        return repository.findByEstudianteId(estudianteId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public EntregasActividadResponse create(CreateEntregasActividadRequest request) {
        Actividad actividad = actividadRepository.findById(request.actividadId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Actividad no encontrada con ID: " + request.actividadId()));

        Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudiante no encontrado con ID: " + request.estudianteId()));

        // Verificar si ya existe una entrega para esta actividad y estudiante
        Optional<EntregasActividad> existing = repository.findByActividadIdAndEstudianteId(request.actividadId(),
                request.estudianteId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("El estudiante ya ha realizado una entrega para esta actividad");
        }

        EntregasActividad entity = mapper.toEntity(request);
        entity.setActividad(actividad);
        entity.setEstudiante(estudiante);

        EntregasActividad saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public EntregasActividadResponse update(String id, UpdateEntregasActividadRequest request) {
        EntregasActividad entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada con ID: " + id));

        mapper.updateEntityFromDto(request, entity);

        EntregasActividad updated = repository.save(entity);
        return mapper.toResponse(updated);
    }

    public void delete(String id) {
        EntregasActividad entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada con ID: " + id));
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }
}
