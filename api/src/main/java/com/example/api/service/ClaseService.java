package com.example.api.service;

import java.time.LocalDate;
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

import com.example.api.dto.request.CreateClaseRequest;
import com.example.api.dto.request.UpdateClaseRequest;
import com.example.api.dto.response.ClaseResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Clase;
import com.example.api.model.Curso;
import com.example.api.model.Tema;
import com.example.api.model.Unidad;
import com.example.api.repository.ClaseRepository;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.TemaRepository;
import com.example.api.repository.UnidadRepository;
import com.example.api.mapper.ClaseMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de clases.
 */
@Service
@Transactional
public class ClaseService {

    private final ClaseRepository claseRepository;
    private final CursoRepository cursoRepository;
    private final UnidadRepository unidadRepository;
    private final TemaRepository temaRepository;
    private final ClaseMapper claseMapper;
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    /**
     * Constructor con inyección de dependencias.
     */
    public ClaseService(ClaseRepository claseRepository,
            CursoRepository cursoRepository,
            UnidadRepository unidadRepository,
            TemaRepository temaRepository,
            ClaseMapper claseMapper,
            AuditProducer auditProducer,
            ObjectMapper objectMapper) {
        this.claseRepository = claseRepository;
        this.cursoRepository = cursoRepository;
        this.unidadRepository = unidadRepository;
        this.temaRepository = temaRepository;
        this.claseMapper = claseMapper;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Obtiene todas las clases activas (paginadas).
     */
    @Transactional(readOnly = true)
    public Page<ClaseResponse> getAllClases(Pageable pageable) {
        return claseRepository.findAllActive(pageable)
                .map(claseMapper::toResponse);
    }

    /**
     * Obtiene una clase por su ID.
     */
    @Transactional(readOnly = true)
    public ClaseResponse getClaseById(String id) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada con ID: " + id));
        return claseMapper.toResponse(clase);
    }

    /**
     * Busca clases por curso.
     */
    @Transactional(readOnly = true)
    public List<ClaseResponse> getClasesByCursoId(String cursoId) {
        return claseRepository.findByCursoId(cursoId).stream()
                .map(claseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca clases por fecha.
     */
    @Transactional(readOnly = true)
    public List<ClaseResponse> getClasesByFecha(LocalDate fecha) {
        return claseRepository.findByFecha(fecha).stream()
                .map(claseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las clases ordenadas por fecha y hora.
     */
    @Transactional(readOnly = true)
    public List<ClaseResponse> getClasesOrdenadas() {
        return claseRepository.findAllOrdenadas().stream()
                .map(claseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las clases eliminadas.
     */
    @Transactional(readOnly = true)
    public List<ClaseResponse> getClasesDeleted() {
        return claseRepository.findAllDeleted().stream()
                .map(claseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva clase.
     */
    public ClaseResponse createClase(CreateClaseRequest request, HttpServletRequest httpRequest) {
        // Validar que el curso existe
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));

        // Validar unidad si se proporciona
        Unidad unidad = null;
        if (request.unidadId() != null && !request.unidadId().isBlank()) {
            unidad = unidadRepository.findById(request.unidadId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Unidad no encontrada con ID: " + request.unidadId()));
        }

        // Validar tema si se proporciona
        Tema tema = null;
        if (request.temaId() != null && !request.temaId().isBlank()) {
            tema = temaRepository.findById(request.temaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + request.temaId()));
        }

        Clase clase = claseMapper.toEntity(request);
        clase.setCurso(curso);
        clase.setUnidad(unidad);
        clase.setTema(tema);

        Clase savedClase = claseRepository.save(clase);

        // Audit Log
        logClaseAction("CREATE", savedClase, httpRequest);

        return claseMapper.toResponse(savedClase);
    }

    /**
     * Actualiza una clase existente.
     */
    public ClaseResponse updateClase(String id, UpdateClaseRequest request, HttpServletRequest httpRequest) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada con ID: " + id));

        claseMapper.updateEntityFromDto(request, clase);

        if (request.unidadId() != null) {
            if (request.unidadId().isBlank()) {
                clase.setUnidad(null);
            } else {
                Unidad unidad = unidadRepository.findById(request.unidadId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Unidad no encontrada con ID: " + request.unidadId()));
                clase.setUnidad(unidad);
            }
        }
        if (request.temaId() != null) {
            if (request.temaId().isBlank()) {
                clase.setTema(null);
            } else {
                Tema tema = temaRepository.findById(request.temaId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Tema no encontrado con ID: " + request.temaId()));
                clase.setTema(tema);
            }
        }

        Clase updatedClase = claseRepository.save(clase);

        // Audit Log
        logClaseAction("UPDATE", updatedClase, httpRequest);

        return claseMapper.toResponse(updatedClase);
    }

    /**
     * Elimina lógicamente una clase (soft delete).
     */
    public void deleteClase(String id, HttpServletRequest httpRequest) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada con ID: " + id));
        clase.setDeletedAt(LocalDateTime.now());
        claseRepository.save(clase);

        // Audit Log
        logClaseAction("DELETE", clase, httpRequest);
    }

    private void logClaseAction(String action, Clase clase, HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
            log.setDevice(request.getHeader("User-Agent"));
            log.setTimestamp(java.time.Instant.now());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("claseId", clase.getId());
            bodyMap.put("fecha", clase.getFecha());
            if (clase.getCurso() != null) {
                bodyMap.put("cursoId", clase.getCurso().getId());
            }
            if (clase.getUnidad() != null) {
                bodyMap.put("unidadId", clase.getUnidad().getId());
            }
            if (clase.getTema() != null) {
                bodyMap.put("temaId", clase.getTema().getId());
            }

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Elimina permanentemente una clase.
     */
    public void permanentDeleteClase(String id) {
        if (!claseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Clase no encontrada con ID: " + id);
        }
        claseRepository.deleteById(id);
    }

    /**
     * Restaura una clase eliminada.
     */
    public ClaseResponse restoreClase(String id) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada con ID: " + id));
        clase.setDeletedAt(null);
        Clase restoredClase = claseRepository.save(clase);
        return claseMapper.toResponse(restoredClase);
    }
}
