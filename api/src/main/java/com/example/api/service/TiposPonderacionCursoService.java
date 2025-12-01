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

import com.example.api.dto.request.CreateTiposPonderacionCursoRequest;
import com.example.api.dto.request.UpdateTiposPonderacionCursoRequest;
import com.example.api.dto.response.TiposPonderacionCursoResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.mapper.TiposPonderacionCursoMapper;
import com.example.api.model.Curso;
import com.example.api.model.TiposPonderacionCurso;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.TiposPonderacionCursoRepository;

@Service
@Transactional
public class TiposPonderacionCursoService {

    private final TiposPonderacionCursoRepository repository;
    private final CursoRepository cursoRepository;
    private final TiposPonderacionCursoMapper mapper;
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    public TiposPonderacionCursoService(TiposPonderacionCursoRepository repository,
            CursoRepository cursoRepository,
            TiposPonderacionCursoMapper mapper,
            AuditProducer auditProducer,
            ObjectMapper objectMapper) {
        this.repository = repository;
        this.cursoRepository = cursoRepository;
        this.mapper = mapper;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public Page<TiposPonderacionCursoResponse> getAll(Pageable pageable) {
        return repository.findAllActive(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TiposPonderacionCursoResponse getById(String id) {
        TiposPonderacionCurso entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ponderación no encontrado con ID: " + id));
        return mapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<TiposPonderacionCursoResponse> getByCursoId(String cursoId) {
        return repository.findByCursoId(cursoId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public TiposPonderacionCursoResponse create(CreateTiposPonderacionCursoRequest request,
            HttpServletRequest httpRequest) {
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));

        TiposPonderacionCurso entity = mapper.toEntity(request);
        entity.setCurso(curso);

        TiposPonderacionCurso saved = repository.save(entity);

        // Audit Log
        logTiposPonderacionCursoAction("CREATE", saved, httpRequest);

        return mapper.toResponse(saved);
    }

    public TiposPonderacionCursoResponse update(String id, UpdateTiposPonderacionCursoRequest request,
            HttpServletRequest httpRequest) {
        TiposPonderacionCurso entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ponderación no encontrado con ID: " + id));

        mapper.updateEntityFromDto(request, entity);

        TiposPonderacionCurso updated = repository.save(entity);

        // Audit Log
        logTiposPonderacionCursoAction("UPDATE", updated, httpRequest);

        return mapper.toResponse(updated);
    }

    public void delete(String id, HttpServletRequest httpRequest) {
        TiposPonderacionCurso entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ponderación no encontrado con ID: " + id));
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);

        // Audit Log
        logTiposPonderacionCursoAction("DELETE", entity, httpRequest);
    }

    private void logTiposPonderacionCursoAction(String action, TiposPonderacionCurso entity,
            HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
            log.setDevice(request.getHeader("User-Agent"));
            log.setTimestamp(java.time.Instant.now());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("tiposPonderacionCursoId", entity.getId());
            bodyMap.put("nombre", entity.getNombre());
            if (entity.getCurso() != null) {
                bodyMap.put("cursoId", entity.getCurso().getId());
            }

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
