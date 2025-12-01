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

import com.example.api.dto.request.CreateTipoEvaluacionRequest;
import com.example.api.dto.request.UpdateTipoEvaluacionRequest;
import com.example.api.dto.response.TipoEvaluacionResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.TipoEvaluacion;
import com.example.api.repository.TipoEvaluacionRepository;
import com.example.api.mapper.TipoEvaluacionMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de tipos de
 * evaluación.
 */
@Service
@Transactional
public class TipoEvaluacionService {

    private final TipoEvaluacionRepository tipoEvaluacionRepository;
    private final TipoEvaluacionMapper tipoEvaluacionMapper;
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    /**
     * Constructor con inyección de dependencias.
     */
    public TipoEvaluacionService(TipoEvaluacionRepository tipoEvaluacionRepository,
            TipoEvaluacionMapper tipoEvaluacionMapper,
            AuditProducer auditProducer,
            ObjectMapper objectMapper) {
        this.tipoEvaluacionRepository = tipoEvaluacionRepository;
        this.tipoEvaluacionMapper = tipoEvaluacionMapper;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Obtiene todos los tipos de evaluación activos (paginados).
     */
    @Transactional(readOnly = true)
    public Page<TipoEvaluacionResponse> getAllTiposEvaluacion(Pageable pageable) {
        return tipoEvaluacionRepository.findAllActive(pageable)
                .map(tipoEvaluacionMapper::toResponse);
    }

    /**
     * Obtiene un tipo de evaluación por su ID.
     */
    @Transactional(readOnly = true)
    public TipoEvaluacionResponse getTipoEvaluacionById(String id) {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + id));
        return tipoEvaluacionMapper.toResponse(tipoEvaluacion);
    }

    /**
     * Busca un tipo de evaluación por nombre usando búsqueda secuencial.
     * Implementa búsqueda secuencial como estructura de datos para lista pequeña.
     */
    @Transactional(readOnly = true)
    public TipoEvaluacionResponse searchByNombre(String nombre) {
        // Obtener todos los tipos activos (lista pequeña, ~5-10 elementos)
        List<TipoEvaluacion> tiposActivos = tipoEvaluacionRepository.findAllActive();

        // Búsqueda secuencial (lineal)
        // Complejidad: O(n) donde n es el número de tipos (~5-10)
        // Apropiado para listas pequeñas donde no se justifica optimización
        for (TipoEvaluacion tipo : tiposActivos) {
            if (tipo.getNombre().equalsIgnoreCase(nombre)) {
                return tipoEvaluacionMapper.toResponse(tipo);
            }
        }

        throw new ResourceNotFoundException("Tipo de evaluación no encontrado con nombre: " + nombre);
    }

    /**
     * Obtiene todos los tipos de evaluación eliminados.
     */
    @Transactional(readOnly = true)
    public List<TipoEvaluacionResponse> getTiposEvaluacionDeleted() {
        return tipoEvaluacionRepository.findAllDeleted().stream()
                .map(tipoEvaluacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo tipo de evaluación.
     */
    public TipoEvaluacionResponse createTipoEvaluacion(CreateTipoEvaluacionRequest request,
            HttpServletRequest httpRequest) {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionMapper.toEntity(request);
        // Peso eliminado de la entidad

        TipoEvaluacion saved = tipoEvaluacionRepository.save(tipoEvaluacion);

        // Audit Log
        logTipoEvaluacionAction("CREATE", saved, httpRequest);

        return tipoEvaluacionMapper.toResponse(saved);
    }

    /**
     * Actualiza un tipo de evaluación existente.
     */
    public TipoEvaluacionResponse updateTipoEvaluacion(String id, UpdateTipoEvaluacionRequest request,
            HttpServletRequest httpRequest) {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + id));

        tipoEvaluacionMapper.updateEntityFromDto(request, tipoEvaluacion);

        TipoEvaluacion updated = tipoEvaluacionRepository.save(tipoEvaluacion);

        // Audit Log
        logTipoEvaluacionAction("UPDATE", updated, httpRequest);

        return tipoEvaluacionMapper.toResponse(updated);
    }

    /**
     * Elimina lógicamente un tipo de evaluación (soft delete).
     */
    public void deleteTipoEvaluacion(String id, HttpServletRequest httpRequest) {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + id));
        tipoEvaluacion.setDeletedAt(LocalDateTime.now());
        tipoEvaluacionRepository.save(tipoEvaluacion);

        // Audit Log
        logTipoEvaluacionAction("DELETE", tipoEvaluacion, httpRequest);
    }

    /**
     * Elimina permanentemente un tipo de evaluación.
     */
    public void permanentDeleteTipoEvaluacion(String id) {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + id));
        tipoEvaluacionRepository.delete(tipoEvaluacion);
    }

    /**
     * Restaura un tipo de evaluación eliminado.
     */
    public TipoEvaluacionResponse restoreTipoEvaluacion(String id) {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + id));
        tipoEvaluacion.setDeletedAt(null);
        TipoEvaluacion restored = tipoEvaluacionRepository.save(tipoEvaluacion);
        return tipoEvaluacionMapper.toResponse(restored);
    }

    private void logTipoEvaluacionAction(String action, TipoEvaluacion tipoEvaluacion, HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
            log.setDevice(request.getHeader("User-Agent"));
            log.setTimestamp(java.time.Instant.now());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("tipoEvaluacionId", tipoEvaluacion.getId());
            bodyMap.put("nombre", tipoEvaluacion.getNombre());

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
