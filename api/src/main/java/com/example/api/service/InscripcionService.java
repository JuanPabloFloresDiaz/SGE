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

import com.example.api.dto.request.CreateInscripcionRequest;
import com.example.api.dto.request.UpdateInscripcionRequest;
import com.example.api.dto.response.InscripcionResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Curso;
import com.example.api.model.Estudiante;
import com.example.api.model.Inscripcion;
import com.example.api.model.Inscripcion.EstadoInscripcion;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.EstudianteRepository;
import com.example.api.repository.InscripcionRepository;
import com.example.api.mapper.InscripcionMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de inscripciones.
 */
@Service
@Transactional
public class InscripcionService {

        private final InscripcionRepository inscripcionRepository;
        private final CursoRepository cursoRepository;
        private final EstudianteRepository estudianteRepository;
        private final InscripcionMapper inscripcionMapper;
        private final AuditProducer auditProducer;
        private final ObjectMapper objectMapper;

        /**
         * Constructor con inyección de dependencias.
         */
        public InscripcionService(InscripcionRepository inscripcionRepository,
                        CursoRepository cursoRepository,
                        EstudianteRepository estudianteRepository,
                        InscripcionMapper inscripcionMapper,
                        AuditProducer auditProducer,
                        ObjectMapper objectMapper) {
                this.inscripcionRepository = inscripcionRepository;
                this.cursoRepository = cursoRepository;
                this.estudianteRepository = estudianteRepository;
                this.inscripcionMapper = inscripcionMapper;
                this.auditProducer = auditProducer;
                this.objectMapper = objectMapper;
        }

        /**
         * Obtiene todas las inscripciones activas (paginadas).
         */
        @Transactional(readOnly = true)
        public Page<InscripcionResponse> getAllInscripciones(Pageable pageable) {
                return inscripcionRepository.findAllActive(pageable)
                                .map(inscripcionMapper::toResponse);
        }

        /**
         * Obtiene una inscripción por su ID.
         */
        @Transactional(readOnly = true)
        public InscripcionResponse getInscripcionById(String id) {
                Inscripcion inscripcion = inscripcionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inscripción no encontrada con ID: " + id));
                return inscripcionMapper.toResponse(inscripcion);
        }

        /**
         * Busca inscripciones por estudiante.
         */
        @Transactional(readOnly = true)
        public List<InscripcionResponse> getInscripcionesByEstudianteId(String estudianteId) {
                return inscripcionRepository.findByEstudianteId(estudianteId)
                                .stream()
                                .map(inscripcionMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Busca inscripciones por curso.
         */
        @Transactional(readOnly = true)
        public List<InscripcionResponse> getInscripcionesByCursoId(String cursoId) {
                return inscripcionRepository.findByCursoId(cursoId)
                                .stream()
                                .map(inscripcionMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Busca inscripciones por estado.
         */
        @Transactional(readOnly = true)
        public List<InscripcionResponse> getInscripcionesByEstado(EstadoInscripcion estado) {
                return inscripcionRepository.findByEstado(estado)
                                .stream()
                                .map(inscripcionMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Obtiene el historial de inscripciones de un estudiante.
         * Incluye todas las inscripciones ordenadas por fecha descendente.
         */
        @Transactional(readOnly = true)
        public List<InscripcionResponse> getHistorialByEstudianteId(String estudianteId) {
                return inscripcionRepository.findHistorialByEstudianteId(estudianteId)
                                .stream()
                                .map(inscripcionMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Obtiene todas las inscripciones eliminadas.
         */
        @Transactional(readOnly = true)
        public List<InscripcionResponse> getInscripcionesDeleted() {
                return inscripcionRepository.findAllDeleted()
                                .stream()
                                .map(inscripcionMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Crea una nueva inscripción.
         * Valida que el curso tenga cupos disponibles y que no exista una inscripción
         * duplicada.
         */
        public InscripcionResponse createInscripcion(CreateInscripcionRequest request, HttpServletRequest httpRequest) {
                // Validar que el curso existe
                Curso curso = cursoRepository.findById(request.cursoId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Curso no encontrado con ID: " + request.cursoId()));

                // Validar que el estudiante existe
                Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Estudiante no encontrado con ID: " + request.estudianteId()));

                // Validar que no existe una inscripción duplicada
                if (inscripcionRepository.existsByEstudianteAndCurso(request.estudianteId(), request.cursoId())) {
                        throw new IllegalArgumentException("El estudiante ya está inscrito en este curso");
                }

                // Validar cupo disponible
                Long inscritosCount = inscripcionRepository.findByCursoId(request.cursoId())
                                .stream()
                                .filter(i -> i.getEstado() == EstadoInscripcion.inscrito)
                                .count();

                if (inscritosCount >= curso.getCupo()) {
                        throw new IllegalArgumentException("El curso no tiene cupos disponibles");
                }

                Inscripcion inscripcion = new Inscripcion();
                inscripcion.setCurso(curso);
                inscripcion.setEstudiante(estudiante);
                inscripcion.setFechaInscripcion(
                                request.fechaInscripcion() != null ? request.fechaInscripcion() : LocalDate.now());
                inscripcion.setEstado(request.estado() != null ? request.estado() : EstadoInscripcion.inscrito);

                Inscripcion savedInscripcion = inscripcionRepository.save(inscripcion);

                // Audit Log
                logInscripcionAction("CREATE", savedInscripcion, httpRequest);

                return inscripcionMapper.toResponse(savedInscripcion);
        }

        /**
         * Actualiza una inscripción existente.
         */
        public InscripcionResponse updateInscripcion(String id, UpdateInscripcionRequest request,
                        HttpServletRequest httpRequest) {
                Inscripcion inscripcion = inscripcionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inscripción no encontrada con ID: " + id));

                if (request.fechaInscripcion() != null) {
                        inscripcion.setFechaInscripcion(request.fechaInscripcion());
                }

                if (request.estado() != null) {
                        inscripcion.setEstado(request.estado());
                }

                Inscripcion updatedInscripcion = inscripcionRepository.save(inscripcion);

                // Audit Log
                logInscripcionAction("UPDATE", updatedInscripcion, httpRequest);

                return inscripcionMapper.toResponse(updatedInscripcion);
        }

        /**
         * Elimina lógicamente una inscripción (soft delete).
         */
        public void deleteInscripcion(String id, HttpServletRequest httpRequest) {
                Inscripcion inscripcion = inscripcionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inscripción no encontrada con ID: " + id));
                inscripcion.setDeletedAt(LocalDateTime.now());
                inscripcionRepository.save(inscripcion);

                // Audit Log
                logInscripcionAction("DELETE", inscripcion, httpRequest);
        }

        private void logInscripcionAction(String action, Inscripcion inscripcion, HttpServletRequest request) {
                try {
                        AuditLogDTO log = new AuditLogDTO();
                        log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
                        log.setAction(action);
                        log.setEndpoint(request.getRequestURI());
                        log.setIpAddress(request.getRemoteAddr());
                        log.setDevice(request.getHeader("User-Agent"));
                        log.setTimestamp(java.time.Instant.now());

                        Map<String, Object> bodyMap = new HashMap<>();
                        bodyMap.put("inscripcionId", inscripcion.getId());
                        bodyMap.put("fechaInscripcion", inscripcion.getFechaInscripcion());
                        bodyMap.put("estado", inscripcion.getEstado());
                        if (inscripcion.getCurso() != null) {
                                bodyMap.put("cursoId", inscripcion.getCurso().getId());
                        }
                        if (inscripcion.getEstudiante() != null) {
                                bodyMap.put("estudianteId", inscripcion.getEstudiante().getId());
                        }

                        log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

                        auditProducer.sendAuditLog(log);
                } catch (Exception e) {
                        System.err.println("Error sending audit log: " + e.getMessage());
                        e.printStackTrace();
                }
        }

        /**
         * Elimina permanentemente una inscripción.
         */
        public void permanentDeleteInscripcion(String id) {
                if (!inscripcionRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Inscripción no encontrada con ID: " + id);
                }
                inscripcionRepository.deleteById(id);
        }

        /**
         * Restaura una inscripción eliminada lógicamente.
         */
        public InscripcionResponse restoreInscripcion(String id) {
                Inscripcion inscripcion = inscripcionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inscripción no encontrada con ID: " + id));
                inscripcion.setDeletedAt(null);
                Inscripcion restoredInscripcion = inscripcionRepository.save(inscripcion);
                return inscripcionMapper.toResponse(restoredInscripcion);
        }
}
