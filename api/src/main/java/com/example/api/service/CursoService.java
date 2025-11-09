package com.example.api.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateCursoRequest;
import com.example.api.dto.request.UpdateCursoRequest;
import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.dto.response.CursoResponse;
import com.example.api.dto.response.PeriodoResponse;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Asignatura;
import com.example.api.model.Curso;
import com.example.api.model.Periodo;
import com.example.api.model.Profesor;
import com.example.api.repository.AsignaturaRepository;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.PeriodoRepository;
import com.example.api.repository.ProfesorRepository;

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

    /**
     * Constructor con inyección de dependencias.
     */
    public CursoService(CursoRepository cursoRepository,
                       AsignaturaRepository asignaturaRepository,
                       ProfesorRepository profesorRepository,
                       PeriodoRepository periodoRepository) {
        this.cursoRepository = cursoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.profesorRepository = profesorRepository;
        this.periodoRepository = periodoRepository;
    }

    /**
     * Convierte una entidad Curso a CursoResponse.
     */
    private CursoResponse toResponse(Curso curso) {
        AsignaturaResponse asignaturaResponse = null;
        if (curso.getAsignatura() != null) {
            Asignatura a = curso.getAsignatura();
            asignaturaResponse = new AsignaturaResponse(
                    a.getId(),
                    a.getCodigo(),
                    a.getNombre(),
                    a.getDescripcion(),
                    a.getImagenUrl(),
                    a.getCreatedAt(),
                    a.getUpdatedAt(),
                    a.getDeletedAt()
            );
        }

        ProfesorResponse profesorResponse = null;
        if (curso.getProfesor() != null) {
            Profesor p = curso.getProfesor();
            profesorResponse = new ProfesorResponse(
                    p.getId(),
                    null, // UsuarioResponse simplificado para evitar recursión
                    p.getEspecialidad(),
                    p.getContrato(),
                    p.getActivo(),
                    p.getCreatedAt(),
                    p.getUpdatedAt(),
                    p.getDeletedAt()
            );
        }

        PeriodoResponse periodoResponse = null;
        if (curso.getPeriodo() != null) {
            Periodo pe = curso.getPeriodo();
            periodoResponse = new PeriodoResponse(
                    pe.getId(),
                    pe.getNombre(),
                    pe.getFechaInicio(),
                    pe.getFechaFin(),
                    pe.getActivo(),
                    pe.getCreatedAt(),
                    pe.getUpdatedAt(),
                    pe.getDeletedAt()
            );
        }

        return new CursoResponse(
                curso.getId(),
                asignaturaResponse,
                profesorResponse,
                periodoResponse,
                curso.getNombreGrupo(),
                curso.getAulaDefault(),
                curso.getCupo(),
                curso.getImagenUrl(),
                curso.getCreatedAt(),
                curso.getUpdatedAt(),
                curso.getDeletedAt()
        );
    }

    /**
     * Obtiene todos los cursos activos (paginados).
     */
    @Transactional(readOnly = true)
    public Page<CursoResponse> getAllCursos(Pageable pageable) {
        return cursoRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene un curso por su ID.
     */
    @Transactional(readOnly = true)
    public CursoResponse getCursoById(String id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));
        return toResponse(curso);
    }

    /**
     * Busca cursos por periodo.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> getCursosByPeriodoId(String periodoId) {
        return cursoRepository.findByPeriodoId(periodoId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca cursos por profesor.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> getCursosByProfesorId(String profesorId) {
        return cursoRepository.findByProfesorId(profesorId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca cursos por asignatura.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> getCursosByAsignaturaId(String asignaturaId) {
        return cursoRepository.findByAsignaturaId(asignaturaId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca cursos por nombre de grupo.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> searchByNombreGrupo(String nombreGrupo) {
        return cursoRepository.searchByNombreGrupo(nombreGrupo)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene cursos con cupos disponibles.
     */
    @Transactional(readOnly = true)
    public List<CursoResponse> getCursosConCuposDisponibles() {
        return cursoRepository.findCursosConCuposDisponibles()
                .stream()
                .map(this::toResponse)
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
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo curso.
     */
    public CursoResponse createCurso(CreateCursoRequest request) {
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
        Curso curso = new Curso();
        curso.setAsignatura(asignatura);
        curso.setProfesor(profesor);
        curso.setPeriodo(periodo);
        curso.setNombreGrupo(request.nombreGrupo());
        curso.setAulaDefault(request.aulaDefault());
        curso.setCupo(request.cupo());
        curso.setImagenUrl(request.imagenUrl());

        // Guardar
        Curso cursoGuardado = cursoRepository.save(curso);
        return toResponse(cursoGuardado);
    }

    /**
     * Actualiza un curso existente.
     */
    public CursoResponse updateCurso(String id, UpdateCursoRequest request) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));

        // Actualizar solo los campos proporcionados
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

        if (request.nombreGrupo() != null) {
            curso.setNombreGrupo(request.nombreGrupo());
        }
        if (request.aulaDefault() != null) {
            curso.setAulaDefault(request.aulaDefault());
        }
        if (request.cupo() != null) {
            curso.setCupo(request.cupo());
        }
        if (request.imagenUrl() != null) {
            curso.setImagenUrl(request.imagenUrl());
        }

        Curso cursoActualizado = cursoRepository.save(curso);
        return toResponse(cursoActualizado);
    }

    /**
     * Realiza eliminación suave de un curso.
     */
    public void deleteCurso(String id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));

        curso.setDeletedAt(LocalDateTime.now());
        cursoRepository.save(curso);
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
        return toResponse(cursoRestaurado);
    }
}
