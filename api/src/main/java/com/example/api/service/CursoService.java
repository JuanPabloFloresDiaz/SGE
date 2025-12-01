package com.example.api.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import com.example.api.dto.AuditLogDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateCursoRequest;
import com.example.api.dto.request.UpdateCursoRequest;
import com.example.api.dto.response.CursoResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Asignatura;
import com.example.api.model.Curso;
import com.example.api.model.Periodo;
import com.example.api.model.Profesor;
import com.example.api.repository.AsignaturaRepository;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.PeriodoRepository;
import com.example.api.repository.ProfesorRepository;
import com.example.api.mapper.CursoMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de cursos.
 */
@Service
@Transactional
public class CursoService {

    private final CursoRepository cursoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final ProfesorRepository profesorRepository;
    private final PeriodoRepository periodoRepository;
    private final CursoMapper cursoMapper;
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    /**
     * Constructor con inyección de dependencias.
     */
    public CursoService(CursoRepository cursoRepository,
            PeriodoRepository periodoRepository,
            AsignaturaRepository asignaturaRepository,
            ProfesorRepository profesorRepository,
            CursoMapper cursoMapper,
            AuditProducer auditProducer,
            ObjectMapper objectMapper) {
        this.cursoRepository = cursoRepository;
        this.periodoRepository = periodoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.profesorRepository = profesorRepository;
        this.cursoMapper = cursoMapper;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Convierte una entidad Curso a CursoResponse.
     */

    /**
     * Obtiene todos los cursos activos (paginados).
     */
    @Transactional(readOnly = true)
    public Page<CursoResponse> getAllCursos(Pageable pageable) {
        return cursoRepository.findAllActive(pageable)
                .map(cursoMapper::toResponse);
    }

    /**
     * Obtiene un curso por su ID.
     */
    @Transactional(readOnly = true)
    public CursoResponse getCursoById(String id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));
        return cursoMapper.toResponse(curso);
    }

    /**
     * Busca cursos por periodo.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> getCursosByPeriodoId(String periodoId) {
        return cursoRepository.findByPeriodoId(periodoId)
                .stream()
                .map(cursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca cursos por profesor.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> getCursosByProfesorId(String profesorId) {
        return cursoRepository.findByProfesorId(profesorId)
                .stream()
                .map(cursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca cursos por asignatura.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> getCursosByAsignaturaId(String asignaturaId) {
        return cursoRepository.findByAsignaturaId(asignaturaId)
                .stream()
                .map(cursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca cursos por nombre de grupo.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> searchByNombreGrupo(String nombreGrupo) {
        return cursoRepository.searchByNombreGrupo(nombreGrupo)
                .stream()
                .map(cursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene cursos con cupos disponibles.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> getCursosConCuposDisponibles() {
        return cursoRepository.findCursosConCuposDisponibles()
                .stream()
                .map(cursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la disponibilidad de cupos de un curso.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDisponibilidadCupo(String id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));

        Long inscritos = cursoRepository.countInscripcionesByCursoId(id);
        Integer cupoTotal = curso.getCupo();
        Integer cuposDisponibles = cupoTotal - inscritos.intValue();

        Map<String, Object> disponibilidad = new HashMap<>();
        disponibilidad.put("cursoId", id);
        disponibilidad.put("nombreGrupo", curso.getNombreGrupo());
        disponibilidad.put("cupoTotal", cupoTotal);
        disponibilidad.put("inscritos", inscritos);
        disponibilidad.put("cuposDisponibles", cuposDisponibles);
        disponibilidad.put("lleno", cuposDisponibles <= 0);

        return disponibilidad;
    }

    /**
     * Obtiene cursos eliminados.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> getCursosDeleted() {
        return cursoRepository.findAllDeleted()
                .stream()
                .map(cursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo curso.
     */
    public CursoResponse createCurso(CreateCursoRequest request, HttpServletRequest httpRequest) {
        // Verificar que la asignatura existe
        Asignatura asignatura = asignaturaRepository.findById(request.asignaturaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asignatura no encontrada con ID: " + request.asignaturaId()));

        // Verificar que el periodo existe
        Periodo periodo = periodoRepository.findById(request.periodoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Periodo no encontrado con ID: " + request.periodoId()));

        // Verificar que el profesor existe (si se proporciona)
        Profesor profesor = null;
        if (request.profesorId() != null && !request.profesorId().isBlank()) {
            profesor = profesorRepository.findById(request.profesorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Profesor no encontrado con ID: " + request.profesorId()));
        }

        // Crear nuevo curso
        Curso curso = cursoMapper.toEntity(request);
        curso.setAsignatura(asignatura);
        curso.setPeriodo(periodo);
        curso.setProfesor(profesor);

        // Guardar
        Curso cursoGuardado = cursoRepository.save(curso);

        // Audit Log
        logCursoAction("CREATE", cursoGuardado, httpRequest);

        return cursoMapper.toResponse(cursoGuardado);
    }

    /**
     * Actualiza un curso existente.
     */
    public CursoResponse updateCurso(String id, UpdateCursoRequest request, HttpServletRequest httpRequest) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));

        // Actualizar solo los campos proporcionados
        cursoMapper.updateEntityFromDto(request, curso);

        // Actualizar relaciones manualmente
        if (request.asignaturaId() != null) {
            Asignatura asignatura = asignaturaRepository.findById(request.asignaturaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Asignatura no encontrada con ID: " + request.asignaturaId()));
            curso.setAsignatura(asignatura);
        }

        if (request.profesorId() != null) {
            if (request.profesorId().isBlank()) {
                curso.setProfesor(null);
            } else {
                Profesor profesor = profesorRepository.findById(request.profesorId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Profesor no encontrado con ID: " + request.profesorId()));
                curso.setProfesor(profesor);
            }
        }

        if (request.periodoId() != null) {
            Periodo periodo = periodoRepository.findById(request.periodoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Periodo no encontrado con ID: " + request.periodoId()));
            curso.setPeriodo(periodo);
        }

        Curso cursoActualizado = cursoRepository.save(curso);

        // Audit Log
        logCursoAction("UPDATE", cursoActualizado, httpRequest);

        return cursoMapper.toResponse(cursoActualizado);
    }

    /**
     * Realiza eliminación suave de un curso.
     */
    public void deleteCurso(String id, HttpServletRequest httpRequest) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));

        curso.setDeletedAt(LocalDateTime.now());
        cursoRepository.save(curso);

        // Audit Log
        logCursoAction("DELETE", curso, httpRequest);
    }

    /**
     * Elimina permanentemente un curso de la base de datos.
     */
    public void permanentDeleteCurso(String id) {
        if (!cursoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Curso no encontrado con ID: " + id);
        }
        cursoRepository.deleteById(id);
    }

    /**
     * Restaura un curso eliminado.
     */
    public CursoResponse restoreCurso(String id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));

        curso.setDeletedAt(null);
        Curso cursoRestaurado = cursoRepository.save(curso);
        return cursoMapper.toResponse(cursoRestaurado);
    }

    private void logCursoAction(String action, Curso curso, HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
            log.setDevice(request.getHeader("User-Agent"));
            log.setTimestamp(java.time.Instant.now());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("cursoId", curso.getId());
            bodyMap.put("nombreGrupo", curso.getNombreGrupo());
            if (curso.getAsignatura() != null) {
                bodyMap.put("asignaturaId", curso.getAsignatura().getId());
            }
            if (curso.getPeriodo() != null) {
                bodyMap.put("periodoId", curso.getPeriodo().getId());
            }

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
