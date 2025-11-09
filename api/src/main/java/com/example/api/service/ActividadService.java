package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateActividadRequest;
import com.example.api.dto.request.UpdateActividadRequest;
import com.example.api.dto.response.ActividadResponse;
import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.dto.response.RolResponse;
import com.example.api.dto.response.UsuarioResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Actividad;
import com.example.api.model.Asignatura;
import com.example.api.model.Profesor;
import com.example.api.repository.ActividadRepository;
import com.example.api.repository.AsignaturaRepository;
import com.example.api.repository.ProfesorRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de actividades.
 */
@Service
@Transactional
public class ActividadService {

    private final ActividadRepository actividadRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final ProfesorRepository profesorRepository;

    /**
     * Constructor con inyección de dependencias.
     */
    public ActividadService(ActividadRepository actividadRepository,
                           AsignaturaRepository asignaturaRepository,
                           ProfesorRepository profesorRepository) {
        this.actividadRepository = actividadRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.profesorRepository = profesorRepository;
    }

    /**
     * Convierte una entidad Actividad a ActividadResponse.
     */
    private ActividadResponse toResponse(Actividad actividad) {
        // Construir AsignaturaResponse
        Asignatura asignatura = actividad.getAsignatura();
        AsignaturaResponse asignaturaResponse = new AsignaturaResponse(
                asignatura.getId(),
                asignatura.getCodigo(),
                asignatura.getNombre(),
                asignatura.getDescripcion(),
                asignatura.getImagenUrl(),
                asignatura.getCreatedAt(),
                asignatura.getUpdatedAt(),
                asignatura.getDeletedAt()
        );

        // Construir ProfesorResponse
        Profesor profesor = actividad.getProfesor();
        RolResponse rolResponse = new RolResponse(
                profesor.getUsuario().getRol().getId(),
                profesor.getUsuario().getRol().getNombre(),
                profesor.getUsuario().getRol().getDescripcion(),
                profesor.getUsuario().getRol().getCreatedAt(),
                profesor.getUsuario().getRol().getUpdatedAt(),
                profesor.getUsuario().getRol().getDeletedAt()
        );

        UsuarioResponse usuarioResponse = new UsuarioResponse(
                profesor.getUsuario().getId(),
                profesor.getUsuario().getUsername(),
                profesor.getUsuario().getNombre(),
                profesor.getUsuario().getEmail(),
                profesor.getUsuario().getTelefono(),
                profesor.getUsuario().getActivo(),
                profesor.getUsuario().getFotoPerfilUrl(),
                rolResponse,
                profesor.getUsuario().getCreatedAt(),
                profesor.getUsuario().getUpdatedAt(),
                profesor.getUsuario().getDeletedAt()
        );

        ProfesorResponse profesorResponse = new ProfesorResponse(
                profesor.getId(),
                usuarioResponse,
                profesor.getEspecialidad(),
                profesor.getContrato(),
                profesor.getActivo(),
                profesor.getCreatedAt(),
                profesor.getUpdatedAt(),
                profesor.getDeletedAt()
        );

        return new ActividadResponse(
                actividad.getId(),
                asignaturaResponse,
                profesorResponse,
                actividad.getTitulo(),
                actividad.getDescripcion(),
                actividad.getFechaApertura(),
                actividad.getFechaCierre(),
                actividad.getImagenUrl(),
                actividad.getDocumentoUrl(),
                actividad.getDocumentoNombre(),
                actividad.getActivo(),
                actividad.getCreatedAt(),
                actividad.getUpdatedAt(),
                actividad.getDeletedAt()
        );
    }

    /**
     * Obtiene todas las actividades activas (paginadas).
     */
    @Transactional(readOnly = true)
    public Page<ActividadResponse> getAllActividades(Pageable pageable) {
        return actividadRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene una actividad por su ID.
     */
    @Transactional(readOnly = true)
    public ActividadResponse getActividadById(String id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + id));
        return toResponse(actividad);
    }

    /**
     * Busca actividades por asignatura.
     */
    @Transactional(readOnly = true)
    public List<ActividadResponse> getActividadesByAsignaturaId(String asignaturaId) {
        return actividadRepository.findByAsignaturaId(asignaturaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca actividades por profesor.
     */
    @Transactional(readOnly = true)
    public List<ActividadResponse> getActividadesByProfesorId(String profesorId) {
        return actividadRepository.findByProfesorId(profesorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las actividades actualmente abiertas.
     */
    @Transactional(readOnly = true)
    public List<ActividadResponse> getActividadesAbiertas() {
        LocalDateTime ahora = LocalDateTime.now();
        return actividadRepository.findActividadesAbiertas(ahora).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las próximas actividades (aún no abiertas).
     */
    @Transactional(readOnly = true)
    public List<ActividadResponse> getProximasActividades() {
        LocalDateTime ahora = LocalDateTime.now();
        return actividadRepository.findProximasActividades(ahora).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las actividades eliminadas.
     */
    @Transactional(readOnly = true)
    public List<ActividadResponse> getActividadesDeleted() {
        return actividadRepository.findAllDeleted().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva actividad.
     */
    public ActividadResponse createActividad(CreateActividadRequest request) {
        // Validar que la asignatura existe
        Asignatura asignatura = asignaturaRepository.findById(request.asignaturaId())
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada con ID: " + request.asignaturaId()));

        // Validar que el profesor existe
        Profesor profesor = profesorRepository.findById(request.profesorId())
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + request.profesorId()));

        // Validar que la fecha de cierre sea posterior a la de apertura
        if (request.fechaCierre().isBefore(request.fechaApertura()) || 
            request.fechaCierre().isEqual(request.fechaApertura())) {
            throw new IllegalArgumentException("La fecha de cierre debe ser posterior a la fecha de apertura");
        }

        Actividad actividad = new Actividad();
        actividad.setAsignatura(asignatura);
        actividad.setProfesor(profesor);
        actividad.setTitulo(request.titulo());
        actividad.setDescripcion(request.descripcion());
        actividad.setFechaApertura(request.fechaApertura());
        actividad.setFechaCierre(request.fechaCierre());
        actividad.setImagenUrl(request.imagenUrl());
        actividad.setDocumentoUrl(request.documentoUrl());
        actividad.setDocumentoNombre(request.documentoNombre());
        actividad.setActivo(request.activo() != null ? request.activo() : true);

        Actividad saved = actividadRepository.save(actividad);
        return toResponse(saved);
    }

    /**
     * Actualiza una actividad existente.
     */
    public ActividadResponse updateActividad(String id, UpdateActividadRequest request) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + id));

        if (request.asignaturaId() != null) {
            Asignatura asignatura = asignaturaRepository.findById(request.asignaturaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada con ID: " + request.asignaturaId()));
            actividad.setAsignatura(asignatura);
        }

        if (request.profesorId() != null) {
            Profesor profesor = profesorRepository.findById(request.profesorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + request.profesorId()));
            actividad.setProfesor(profesor);
        }

        if (request.titulo() != null) {
            actividad.setTitulo(request.titulo());
        }
        if (request.descripcion() != null) {
            actividad.setDescripcion(request.descripcion());
        }
        if (request.fechaApertura() != null) {
            actividad.setFechaApertura(request.fechaApertura());
        }
        if (request.fechaCierre() != null) {
            actividad.setFechaCierre(request.fechaCierre());
        }

        // Validar fechas si se actualizaron ambas
        if (actividad.getFechaCierre().isBefore(actividad.getFechaApertura()) || 
            actividad.getFechaCierre().isEqual(actividad.getFechaApertura())) {
            throw new IllegalArgumentException("La fecha de cierre debe ser posterior a la fecha de apertura");
        }

        if (request.imagenUrl() != null) {
            actividad.setImagenUrl(request.imagenUrl());
        }
        if (request.documentoUrl() != null) {
            actividad.setDocumentoUrl(request.documentoUrl());
        }
        if (request.documentoNombre() != null) {
            actividad.setDocumentoNombre(request.documentoNombre());
        }
        if (request.activo() != null) {
            actividad.setActivo(request.activo());
        }

        Actividad updated = actividadRepository.save(actividad);
        return toResponse(updated);
    }

    /**
     * Elimina lógicamente una actividad (soft delete).
     */
    public void deleteActividad(String id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + id));
        actividad.setDeletedAt(LocalDateTime.now());
        actividadRepository.save(actividad);
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
        return toResponse(restored);
    }
}
