package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.example.api.dto.AuditLogDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    public EntregasActividadService(EntregasActividadRepository repository,
            ActividadRepository actividadRepository,
            EstudianteRepository estudianteRepository,
            EntregasActividadMapper mapper,
            AuditProducer auditProducer,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.actividadRepository = actividadRepository;
        this.estudianteRepository = estudianteRepository;
        this.mapper = mapper;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
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

    public EntregasActividadResponse create(CreateEntregasActividadRequest request, HttpServletRequest httpRequest) {
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

        // Audit Log
        logEntregasActividadAction("CREATE", saved, httpRequest);

        return mapper.toResponse(saved);
    }

    public EntregasActividadResponse update(String id, UpdateEntregasActividadRequest request,
            HttpServletRequest httpRequest) {
        EntregasActividad entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada con ID: " + id));

        mapper.updateEntityFromDto(request, entity);

        EntregasActividad updated = repository.save(entity);

        // Audit Log
        logEntregasActividadAction("UPDATE", updated, httpRequest);

        return mapper.toResponse(updated);
    }

    public void delete(String id, HttpServletRequest httpRequest) {
        EntregasActividad entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrega no encontrada con ID: " + id));
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);

        // Audit Log
        logEntregasActividadAction("DELETE", entity, httpRequest);
    }

    private void logEntregasActividadAction(String action, EntregasActividad entity, HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
            log.setDevice(request.getHeader("User-Agent"));
            log.setTimestamp(java.time.Instant.now());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("entregaId", entity.getId());
            if (entity.getActividad() != null) {
                bodyMap.put("actividadId", entity.getActividad().getId());
            }
            if (entity.getEstudiante() != null) {
                bodyMap.put("estudianteId", entity.getEstudiante().getId());
            }

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
