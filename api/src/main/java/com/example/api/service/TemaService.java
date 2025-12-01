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

import com.example.api.dto.request.CreateTemaRequest;
import com.example.api.dto.request.UpdateTemaRequest;
import com.example.api.dto.response.TemaResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Tema;
import com.example.api.model.Unidad;
import com.example.api.repository.TemaRepository;
import com.example.api.repository.UnidadRepository;
import com.example.api.mapper.TemaMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de temas.
 */
@Service
@Transactional
public class TemaService {

    private final TemaRepository temaRepository;
    private final UnidadRepository unidadRepository;
    private final TemaMapper temaMapper;
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    /**
     * Constructor con inyección de dependencias.
     */
    public TemaService(TemaRepository temaRepository,
            UnidadRepository unidadRepository,
            TemaMapper temaMapper,
            AuditProducer auditProducer,
            ObjectMapper objectMapper) {
        this.temaRepository = temaRepository;
        this.unidadRepository = unidadRepository;
        this.temaMapper = temaMapper;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Obtiene todos los temas activos (paginados).
     */
    @Transactional(readOnly = true)
    public Page<TemaResponse> getAllTemas(Pageable pageable) {
        return temaRepository.findAllActive(pageable)
                .map(temaMapper::toResponse);
    }

    /**
     * Obtiene un tema por su ID.
     */
    @Transactional(readOnly = true)
    public TemaResponse getTemaById(String id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));
        return temaMapper.toResponse(tema);
    }

    /**
     * Busca temas por unidad.
     */
    @Transactional(readOnly = true)
    public List<TemaResponse> getTemasByUnidadId(String unidadId) {
        return temaRepository.findByUnidadId(unidadId).stream()
                .map(temaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca temas por título.
     */
    @Transactional(readOnly = true)
    public List<TemaResponse> getTemasByTitulo(String titulo) {
        return temaRepository.findByTituloContaining(titulo).stream()
                .map(temaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los temas eliminados.
     */
    @Transactional(readOnly = true)
    public List<TemaResponse> getTemasDeleted() {
        return temaRepository.findAllDeleted().stream()
                .map(temaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo tema.
     */
    public TemaResponse createTema(CreateTemaRequest request, HttpServletRequest httpRequest) {
        // Validar que la unidad existe
        Unidad unidad = unidadRepository.findById(request.unidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + request.unidadId()));

        Tema tema = temaMapper.toEntity(request);
        tema.setUnidad(unidad);

        Tema savedTema = temaRepository.save(tema);

        // Audit Log
        logTemaAction("CREATE", savedTema, httpRequest);

        return temaMapper.toResponse(savedTema);
    }

    /**
     * Actualiza un tema existente.
     */
    public TemaResponse updateTema(String id, UpdateTemaRequest request, HttpServletRequest httpRequest) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));

        temaMapper.updateEntityFromDto(request, tema);

        Tema updatedTema = temaRepository.save(tema);

        // Audit Log
        logTemaAction("UPDATE", updatedTema, httpRequest);

        return temaMapper.toResponse(updatedTema);
    }

    /**
     * Elimina lógicamente un tema (soft delete).
     */
    public void deleteTema(String id, HttpServletRequest httpRequest) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));
        tema.setDeletedAt(LocalDateTime.now());
        temaRepository.save(tema);

        // Audit Log
        logTemaAction("DELETE", tema, httpRequest);
    }

    /**
     * Elimina permanentemente un tema.
     */
    public void permanentDeleteTema(String id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));
        temaRepository.delete(tema);
    }

    /**
     * Restaura un tema eliminado.
     */
    public TemaResponse restoreTema(String id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));
        tema.setDeletedAt(null);
        Tema restoredTema = temaRepository.save(tema);
        return temaMapper.toResponse(restoredTema);
    }

    private void logTemaAction(String action, Tema tema, HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
            log.setDevice(request.getHeader("User-Agent"));
            log.setTimestamp(java.time.Instant.now());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("temaId", tema.getId());
            bodyMap.put("titulo", tema.getTitulo());
            bodyMap.put("numero", tema.getNumero());
            if (tema.getUnidad() != null) {
                bodyMap.put("unidadId", tema.getUnidad().getId());
            }

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
