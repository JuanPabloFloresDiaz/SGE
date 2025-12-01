package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
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

import com.example.api.dto.request.CreateActividadRequest;
import com.example.api.dto.request.UpdateActividadRequest;
import com.example.api.dto.response.ActividadResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Actividad;
import com.example.api.model.Curso;
import com.example.api.model.Profesor;
import com.example.api.model.Curso;
import com.example.api.repository.ActividadRepository;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.ProfesorRepository;
import com.example.api.repository.TiposPonderacionCursoRepository;
import com.example.api.mapper.ActividadMapper;
import com.example.api.model.TiposPonderacionCurso;

/**
 * Servicio que contiene la lógica de negocio para la gestión de actividades.
 */
@Service
@Transactional
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final CursoRepository cursoRepository;
    private final ProfesorRepository profesorRepository;
    private final TiposPonderacionCursoRepository tiposPonderacionCursoRepository;
    private final ActividadMapper actividadMapper;
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    /**
     * Constructor con inyección de dependencias.
     */
    public ActividadService(ActividadRepository actividadRepository,
            CursoRepository cursoRepository,
            ProfesorRepository profesorRepository,
            TiposPonderacionCursoRepository tiposPonderacionCursoRepository,
            ActividadMapper actividadMapper,
            AuditProducer auditProducer,
            ObjectMapper objectMapper) {
        this.actividadRepository = actividadRepository;
        this.cursoRepository = cursoRepository;
        this.profesorRepository = profesorRepository;
        this.tiposPonderacionCursoRepository = tiposPonderacionCursoRepository;
        this.actividadMapper = actividadMapper;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Obtiene todas las actividades activas (paginadas).
     */
    @Transactional(readOnly = true)
    public Page<ActividadResponse> getAllActividades(Pageable pageable) {
        return actividadRepository.findAllActive(pageable)
                .map(actividadMapper::toResponse);
    }

    /**
     * Obtiene una actividad por su ID.
     */
    @Transactional(readOnly = true)
    public ActividadResponse getActividadById(String id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + id));
        return actividadMapper.toResponse(actividad);
    }

    /**
     * Busca actividades por asignatura.
     */
    @Transactional(readOnly = true)
    public List<ActividadResponse> getActividadesByAsignaturaId(String asignaturaId) {
        return actividadRepository.findByAsignaturaId(asignaturaId).stream()
                .map(actividadMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca actividades por profesor.
     */
    @Transactional(readOnly = true)
    public List<ActividadResponse> getActividadesByProfesorId(String profesorId) {
        return actividadRepository.findByProfesorId(profesorId).stream()
                .map(actividadMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las actividades actualmente abiertas.
     */
    @Transactional(readOnly = true)
    public List<ActividadResponse> getActividadesAbiertas() {
        LocalDateTime ahora = LocalDateTime.now();
        return actividadRepository.findActividadesAbiertas(ahora).stream()
                .map(actividadMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las próximas actividades (aún no abiertas).
     */
    @Transactional(readOnly = true)
    public List<ActividadResponse> getProximasActividades() {
        LocalDateTime ahora = LocalDateTime.now();
        return actividadRepository.findProximasActividades(ahora).stream()
                .map(actividadMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las actividades eliminadas.
     */
    @Transactional(readOnly = true)
    public List<ActividadResponse> getActividadesDeleted() {
        return actividadRepository.findAllDeleted().stream()
                .map(actividadMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva actividad.
     */
    public ActividadResponse createActividad(CreateActividadRequest request, HttpServletRequest httpRequest) {
        // Validar que la asignatura existe (implícito al buscar cursos)
        // Asignatura asignatura = asignaturaRepository.findById(request.asignaturaId())
        // .orElseThrow(() -> new ResourceNotFoundException(
        // "Asignatura no encontrada con ID: " + request.asignaturaId()));

        // Validar que el profesor existe
        Profesor profesor = profesorRepository.findById(request.profesorId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Profesor no encontrado con ID: " + request.profesorId()));

        // Validar que la fecha de cierre sea posterior a la de apertura
        if (request.fechaCierre().isBefore(request.fechaApertura()) ||
                request.fechaCierre().isEqual(request.fechaApertura())) {
            throw new IllegalArgumentException("La fecha de cierre debe ser posterior a la fecha de apertura");
        }

        // Validar que el curso existe
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Curso no encontrado con ID: " + request.cursoId()));

        Actividad actividad = actividadMapper.toEntity(request);
        actividad.setCurso(curso);
        actividad.setProfesor(profesor);

        if (request.ponderacionId() != null) {
            TiposPonderacionCurso ponderacion = tiposPonderacionCursoRepository.findById(request.ponderacionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de ponderación no encontrado con ID: " + request.ponderacionId()));
            actividad.setTipoPonderacion(ponderacion);
        }

        Actividad saved = actividadRepository.save(actividad);

        // Audit Log
        logActividadAction("CREATE", saved, httpRequest);

        return actividadMapper.toResponse(saved);
    }

    /**
     * Actualiza una actividad existente.
     */
    public ActividadResponse updateActividad(String id, UpdateActividadRequest request,
            HttpServletRequest httpRequest) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + id));

        if (request.cursoId() != null) {
            Curso curso = cursoRepository.findById(request.cursoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Curso no encontrado con ID: " + request.cursoId()));
            actividad.setCurso(curso);
        }

        if (request.profesorId() != null) {
            Profesor profesor = profesorRepository.findById(request.profesorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Profesor no encontrado con ID: " + request.profesorId()));
            actividad.setProfesor(profesor);
        }

        if (request.ponderacionId() != null) {
            TiposPonderacionCurso ponderacion = tiposPonderacionCursoRepository.findById(request.ponderacionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de ponderación no encontrado con ID: " + request.ponderacionId()));
            actividad.setTipoPonderacion(ponderacion);
        }

        actividadMapper.updateEntityFromDto(request, actividad);

        // Validar fechas si se actualizaron ambas (o una y la otra ya estaba)
        if (actividad.getFechaCierre().isBefore(actividad.getFechaApertura()) ||
                actividad.getFechaCierre().isEqual(actividad.getFechaApertura())) {
            throw new IllegalArgumentException("La fecha de cierre debe ser posterior a la fecha de apertura");
        }

        Actividad updated = actividadRepository.save(actividad);

        // Audit Log
        logActividadAction("UPDATE", updated, httpRequest);

        return actividadMapper.toResponse(updated);
    }

    /**
     * Elimina lógicamente una actividad (soft delete).
     */
    public void deleteActividad(String id, HttpServletRequest httpRequest) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + id));
        actividad.setDeletedAt(LocalDateTime.now());
        actividadRepository.save(actividad);

        // Audit Log
        logActividadAction("DELETE", actividad, httpRequest);
    }

    /**
     * Elimina permanentemente una actividad.
     */
    public void permanentDeleteActividad(String id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + id));
        actividadRepository.delete(actividad);
    }

    /**
     * Restaura una actividad eliminada.
     */
    public ActividadResponse restoreActividad(String id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + id));
        actividad.setDeletedAt(null);
        Actividad restored = actividadRepository.save(actividad);
        return actividadMapper.toResponse(restored);
    }

    private void logActividadAction(String action, Actividad actividad, HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
            log.setDevice(request.getHeader("User-Agent"));
            log.setTimestamp(java.time.Instant.now());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("actividadId", actividad.getId());
            bodyMap.put("titulo", actividad.getTitulo());
            if (actividad.getCurso() != null) {
                bodyMap.put("cursoId", actividad.getCurso().getId());
            }
            if (actividad.getProfesor() != null) {
                bodyMap.put("profesorId", actividad.getProfesor().getId());
            }

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
