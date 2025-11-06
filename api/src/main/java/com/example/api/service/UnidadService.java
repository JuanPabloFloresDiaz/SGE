package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateUnidadRequest;
import com.example.api.dto.request.UpdateUnidadRequest;
import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.dto.response.CursoResponse;
import com.example.api.dto.response.PeriodoResponse;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.dto.response.RolResponse;
import com.example.api.dto.response.UnidadResponse;
import com.example.api.dto.response.UsuarioResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Curso;
import com.example.api.model.Unidad;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.UnidadRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de unidades.
 */
@Service
@Transactional
public class UnidadService {

    private final UnidadRepository unidadRepository;
    private final CursoRepository cursoRepository;

    /**
     * Constructor con inyección de dependencias.
     */
    public UnidadService(UnidadRepository unidadRepository,
                        CursoRepository cursoRepository) {
        this.unidadRepository = unidadRepository;
        this.cursoRepository = cursoRepository;
    }

    /**
     * Convierte una entidad Unidad a UnidadResponse.
     */
    private UnidadResponse toResponse(Unidad unidad) {
        Curso curso = unidad.getCurso();
        
        // Construir AsignaturaResponse
        AsignaturaResponse asignaturaResponse = new AsignaturaResponse(
            curso.getAsignatura().getId(),
            curso.getAsignatura().getCodigo(),
            curso.getAsignatura().getNombre(),
            curso.getAsignatura().getDescripcion(),
            curso.getAsignatura().getCreatedAt(),
            curso.getAsignatura().getUpdatedAt(),
            curso.getAsignatura().getDeletedAt()
        );
        
        // Construir RolResponse del usuario del profesor
        RolResponse rolResponse = new RolResponse(
            curso.getProfesor().getUsuario().getRol().getId(),
            curso.getProfesor().getUsuario().getRol().getNombre(),
            curso.getProfesor().getUsuario().getRol().getDescripcion(),
            curso.getProfesor().getUsuario().getRol().getCreatedAt(),
            curso.getProfesor().getUsuario().getRol().getUpdatedAt(),
            curso.getProfesor().getUsuario().getRol().getDeletedAt()
        );
        
        // Construir UsuarioResponse del profesor
        UsuarioResponse usuarioResponse = new UsuarioResponse(
            curso.getProfesor().getUsuario().getId(),
            curso.getProfesor().getUsuario().getUsername(),
            curso.getProfesor().getUsuario().getNombre(),
            curso.getProfesor().getUsuario().getEmail(),
            curso.getProfesor().getUsuario().getTelefono(),
            curso.getProfesor().getUsuario().getActivo(),
            rolResponse,
            curso.getProfesor().getUsuario().getCreatedAt(),
            curso.getProfesor().getUsuario().getUpdatedAt(),
            curso.getProfesor().getUsuario().getDeletedAt()
        );
        
        // Construir ProfesorResponse
        ProfesorResponse profesorResponse = new ProfesorResponse(
            curso.getProfesor().getId(),
            usuarioResponse,
            curso.getProfesor().getEspecialidad(),
            curso.getProfesor().getContrato(),
            curso.getProfesor().getActivo(),
            curso.getProfesor().getCreatedAt(),
            curso.getProfesor().getUpdatedAt(),
            curso.getProfesor().getDeletedAt()
        );
        
        // Construir PeriodoResponse
        PeriodoResponse periodoResponse = new PeriodoResponse(
            curso.getPeriodo().getId(),
            curso.getPeriodo().getNombre(),
            curso.getPeriodo().getFechaInicio(),
            curso.getPeriodo().getFechaFin(),
            curso.getPeriodo().getActivo(),
            curso.getPeriodo().getCreatedAt(),
            curso.getPeriodo().getUpdatedAt(),
            curso.getPeriodo().getDeletedAt()
        );
        
        // Construir CursoResponse
        CursoResponse cursoResponse = new CursoResponse(
            curso.getId(),
            asignaturaResponse,
            profesorResponse,
            periodoResponse,
            curso.getNombreGrupo(),
            curso.getAulaDefault(),
            curso.getCupo(),
            curso.getCreatedAt(),
            curso.getUpdatedAt(),
            curso.getDeletedAt()
        );

        return new UnidadResponse(
            unidad.getId(),
            cursoResponse,
            unidad.getTitulo(),
            unidad.getDescripcion(),
            unidad.getNumero(),
            unidad.getCreatedAt(),
            unidad.getUpdatedAt(),
            unidad.getDeletedAt()
        );
    }

    /**
     * Obtiene todas las unidades activas (paginadas).
     */
    @Transactional(readOnly = true)
    public Page<UnidadResponse> getAllUnidades(Pageable pageable) {
        return unidadRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene una unidad por su ID.
     */
    @Transactional(readOnly = true)
    public UnidadResponse getUnidadById(String id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));
        return toResponse(unidad);
    }

    /**
     * Busca unidades por curso.
     */
    @Transactional(readOnly = true)
    public List<UnidadResponse> getUnidadesByCursoId(String cursoId) {
        return unidadRepository.findByCursoId(cursoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las unidades eliminadas.
     */
    @Transactional(readOnly = true)
    public List<UnidadResponse> getUnidadesDeleted() {
        return unidadRepository.findAllDeleted().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva unidad.
     */
    public UnidadResponse createUnidad(CreateUnidadRequest request) {
        // Validar que el curso existe
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));

        Unidad unidad = new Unidad();
        unidad.setCurso(curso);
        unidad.setTitulo(request.titulo());
        unidad.setDescripcion(request.descripcion());
        unidad.setNumero(request.numero());

        Unidad savedUnidad = unidadRepository.save(unidad);
        return toResponse(savedUnidad);
    }

    /**
     * Actualiza una unidad existente.
     */
    public UnidadResponse updateUnidad(String id, UpdateUnidadRequest request) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));

        if (request.titulo() != null) {
            unidad.setTitulo(request.titulo());
        }
        if (request.descripcion() != null) {
            unidad.setDescripcion(request.descripcion());
        }
        if (request.numero() != null) {
            unidad.setNumero(request.numero());
        }

        Unidad updatedUnidad = unidadRepository.save(unidad);
        return toResponse(updatedUnidad);
    }

    /**
     * Elimina lógicamente una unidad (soft delete).
     */
    public void deleteUnidad(String id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));
        unidad.setDeletedAt(LocalDateTime.now());
        unidadRepository.save(unidad);
    }

    /**
     * Elimina permanentemente una unidad.
     */
    public void permanentDeleteUnidad(String id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));
        unidadRepository.delete(unidad);
    }

    /**
     * Restaura una unidad eliminada.
     */
    public UnidadResponse restoreUnidad(String id) {
        Unidad unidad = unidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + id));
        unidad.setDeletedAt(null);
        Unidad restoredUnidad = unidadRepository.save(unidad);
        return toResponse(restoredUnidad);
    }
}
