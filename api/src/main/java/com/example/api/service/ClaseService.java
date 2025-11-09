package com.example.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateClaseRequest;
import com.example.api.dto.request.UpdateClaseRequest;
import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.dto.response.ClaseResponse;
import com.example.api.dto.response.CursoResponse;
import com.example.api.dto.response.PeriodoResponse;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.dto.response.RolResponse;
import com.example.api.dto.response.TemaResponse;
import com.example.api.dto.response.UnidadResponse;
import com.example.api.dto.response.UsuarioResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Clase;
import com.example.api.model.Curso;
import com.example.api.model.Tema;
import com.example.api.model.Unidad;
import com.example.api.repository.ClaseRepository;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.TemaRepository;
import com.example.api.repository.UnidadRepository;

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

    /**
     * Constructor con inyección de dependencias.
     */
    public ClaseService(ClaseRepository claseRepository,
                       CursoRepository cursoRepository,
                       UnidadRepository unidadRepository,
                       TemaRepository temaRepository) {
        this.claseRepository = claseRepository;
        this.cursoRepository = cursoRepository;
        this.unidadRepository = unidadRepository;
        this.temaRepository = temaRepository;
    }

    /**
     * Convierte una entidad Clase a ClaseResponse.
     */
    private ClaseResponse toResponse(Clase clase) {
        // Construir AsignaturaResponse
        AsignaturaResponse asignaturaResponse = new AsignaturaResponse(
                clase.getCurso().getAsignatura().getId(),
                clase.getCurso().getAsignatura().getCodigo(),
                clase.getCurso().getAsignatura().getNombre(),
                clase.getCurso().getAsignatura().getDescripcion(),
                clase.getCurso().getAsignatura().getImagenUrl(),
                clase.getCurso().getAsignatura().getCreatedAt(),
                clase.getCurso().getAsignatura().getUpdatedAt(),
                clase.getCurso().getAsignatura().getDeletedAt()
        );

        // Construir RolResponse
        RolResponse rolResponse = new RolResponse(
                clase.getCurso().getProfesor().getUsuario().getRol().getId(),
                clase.getCurso().getProfesor().getUsuario().getRol().getNombre(),
                clase.getCurso().getProfesor().getUsuario().getRol().getDescripcion(),
                clase.getCurso().getProfesor().getUsuario().getRol().getCreatedAt(),
                clase.getCurso().getProfesor().getUsuario().getRol().getUpdatedAt(),
                clase.getCurso().getProfesor().getUsuario().getRol().getDeletedAt()
        );

        // Construir UsuarioResponse
        UsuarioResponse usuarioResponse = new UsuarioResponse(
                clase.getCurso().getProfesor().getUsuario().getId(),
                clase.getCurso().getProfesor().getUsuario().getUsername(),
                clase.getCurso().getProfesor().getUsuario().getNombre(),
                clase.getCurso().getProfesor().getUsuario().getEmail(),
                clase.getCurso().getProfesor().getUsuario().getTelefono(),
                clase.getCurso().getProfesor().getUsuario().getActivo(),
                clase.getCurso().getProfesor().getUsuario().getFotoPerfilUrl(),
                rolResponse,
                clase.getCurso().getProfesor().getUsuario().getCreatedAt(),
                clase.getCurso().getProfesor().getUsuario().getUpdatedAt(),
                clase.getCurso().getProfesor().getUsuario().getDeletedAt()
        );

        // Construir ProfesorResponse
        ProfesorResponse profesorResponse = new ProfesorResponse(
                clase.getCurso().getProfesor().getId(),
                usuarioResponse,
                clase.getCurso().getProfesor().getEspecialidad(),
                clase.getCurso().getProfesor().getContrato(),
                clase.getCurso().getProfesor().getActivo(),
                clase.getCurso().getProfesor().getCreatedAt(),
                clase.getCurso().getProfesor().getUpdatedAt(),
                clase.getCurso().getProfesor().getDeletedAt()
        );

        // Construir PeriodoResponse
        PeriodoResponse periodoResponse = new PeriodoResponse(
                clase.getCurso().getPeriodo().getId(),
                clase.getCurso().getPeriodo().getNombre(),
                clase.getCurso().getPeriodo().getFechaInicio(),
                clase.getCurso().getPeriodo().getFechaFin(),
                clase.getCurso().getPeriodo().getActivo(),
                clase.getCurso().getPeriodo().getCreatedAt(),
                clase.getCurso().getPeriodo().getUpdatedAt(),
                clase.getCurso().getPeriodo().getDeletedAt()
        );

        // Construir CursoResponse
        CursoResponse cursoResponse = new CursoResponse(
                clase.getCurso().getId(),
                asignaturaResponse,
                profesorResponse,
                periodoResponse,
                clase.getCurso().getNombreGrupo(),
                clase.getCurso().getAulaDefault(),
                clase.getCurso().getCupo(),
                clase.getCurso().getImagenUrl(),
                clase.getCurso().getCreatedAt(),
                clase.getCurso().getUpdatedAt(),
                clase.getCurso().getDeletedAt()
        );

        // Construir UnidadResponse si existe
        UnidadResponse unidadResponse = null;
        if (clase.getUnidad() != null) {
            unidadResponse = new UnidadResponse(
                    clase.getUnidad().getId(),
                    cursoResponse, // Reutilizamos el cursoResponse ya construido
                    clase.getUnidad().getTitulo(),
                    clase.getUnidad().getDescripcion(),
                    clase.getUnidad().getNumero(),
                    clase.getUnidad().getDocumentoUrl(),
                    clase.getUnidad().getDocumentoNombre(),
                    clase.getUnidad().getCreatedAt(),
                    clase.getUnidad().getUpdatedAt(),
                    clase.getUnidad().getDeletedAt()
            );
        }

        // Construir TemaResponse si existe
        TemaResponse temaResponse = null;
        if (clase.getTema() != null) {
            // Construir UnidadResponse para el tema (similar estructura al cursoResponse principal)
            AsignaturaResponse asignaturaDelTema = new AsignaturaResponse(
                    clase.getTema().getUnidad().getCurso().getAsignatura().getId(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getCodigo(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getNombre(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getDescripcion(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getImagenUrl(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getAsignatura().getDeletedAt()
            );

            RolResponse rolDelTema = new RolResponse(
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getId(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getNombre(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getDescripcion(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getRol().getDeletedAt()
            );

            UsuarioResponse usuarioDelTema = new UsuarioResponse(
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getId(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getUsername(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getNombre(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getEmail(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getTelefono(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getActivo(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getFotoPerfilUrl(),
                    rolDelTema,
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUsuario().getDeletedAt()
            );

            ProfesorResponse profesorDelTema = new ProfesorResponse(
                    clase.getTema().getUnidad().getCurso().getProfesor().getId(),
                    usuarioDelTema,
                    clase.getTema().getUnidad().getCurso().getProfesor().getEspecialidad(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getContrato(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getActivo(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getProfesor().getDeletedAt()
            );

            PeriodoResponse periodoDelTema = new PeriodoResponse(
                    clase.getTema().getUnidad().getCurso().getPeriodo().getId(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getNombre(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getFechaInicio(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getFechaFin(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getActivo(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getPeriodo().getDeletedAt()
            );

            CursoResponse cursoDelTema = new CursoResponse(
                    clase.getTema().getUnidad().getCurso().getId(),
                    asignaturaDelTema,
                    profesorDelTema,
                    periodoDelTema,
                    clase.getTema().getUnidad().getCurso().getNombreGrupo(),
                    clase.getTema().getUnidad().getCurso().getAulaDefault(),
                    clase.getTema().getUnidad().getCurso().getCupo(),
                    clase.getTema().getUnidad().getCurso().getImagenUrl(),
                    clase.getTema().getUnidad().getCurso().getCreatedAt(),
                    clase.getTema().getUnidad().getCurso().getUpdatedAt(),
                    clase.getTema().getUnidad().getCurso().getDeletedAt()
            );

            UnidadResponse unidadDelTema = new UnidadResponse(
                    clase.getTema().getUnidad().getId(),
                    cursoDelTema,
                    clase.getTema().getUnidad().getTitulo(),
                    clase.getTema().getUnidad().getDescripcion(),
                    clase.getTema().getUnidad().getNumero(),
                    clase.getTema().getUnidad().getDocumentoUrl(),
                    clase.getTema().getUnidad().getDocumentoNombre(),
                    clase.getTema().getUnidad().getCreatedAt(),
                    clase.getTema().getUnidad().getUpdatedAt(),
                    clase.getTema().getUnidad().getDeletedAt()
            );

            temaResponse = new TemaResponse(
                    clase.getTema().getId(),
                    unidadDelTema,
                    clase.getTema().getTitulo(),
                    clase.getTema().getDescripcion(),
                    clase.getTema().getNumero(),
                    clase.getTema().getDuracionMinutos(),
                    clase.getTema().getDocumentoUrl(),
                    clase.getTema().getDocumentoNombre(),
                    clase.getTema().getCreatedAt(),
                    clase.getTema().getUpdatedAt(),
                    clase.getTema().getDeletedAt()
            );
        }

        return new ClaseResponse(
                clase.getId(),
                cursoResponse,
                clase.getFecha(),
                clase.getInicio(),
                clase.getFin(),
                unidadResponse,
                temaResponse,
                clase.getNotas(),
                clase.getDocumentoUrl(),
                clase.getDocumentoNombre(),
                clase.getCreatedAt(),
                clase.getUpdatedAt(),
                clase.getDeletedAt()
        );
    }

    /**
     * Obtiene todas las clases activas (paginadas).
     */
    @Transactional(readOnly = true)
    public Page<ClaseResponse> getAllClases(Pageable pageable) {
        return claseRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene una clase por su ID.
     */
    @Transactional(readOnly = true)
    public ClaseResponse getClaseById(String id) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada con ID: " + id));
        return toResponse(clase);
    }

    /**
     * Busca clases por curso.
     */
    @Transactional(readOnly = true)
    public List<ClaseResponse> getClasesByCursoId(String cursoId) {
        return claseRepository.findByCursoId(cursoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca clases por fecha.
     */
    @Transactional(readOnly = true)
    public List<ClaseResponse> getClasesByFecha(LocalDate fecha) {
        return claseRepository.findByFecha(fecha).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las clases ordenadas por fecha y hora.
     */
    @Transactional(readOnly = true)
    public List<ClaseResponse> getClasesOrdenadas() {
        return claseRepository.findAllOrdenadas().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las clases eliminadas.
     */
    @Transactional(readOnly = true)
    public List<ClaseResponse> getClasesDeleted() {
        return claseRepository.findAllDeleted().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva clase.
     */
    public ClaseResponse createClase(CreateClaseRequest request) {
        // Validar que el curso existe
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));

        // Validar unidad si se proporciona
        Unidad unidad = null;
        if (request.unidadId() != null && !request.unidadId().isBlank()) {
            unidad = unidadRepository.findById(request.unidadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + request.unidadId()));
        }

        // Validar tema si se proporciona
        Tema tema = null;
        if (request.temaId() != null && !request.temaId().isBlank()) {
            tema = temaRepository.findById(request.temaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + request.temaId()));
        }

        Clase clase = new Clase();
        clase.setCurso(curso);
        clase.setFecha(request.fecha());
        clase.setInicio(request.inicio());
        clase.setFin(request.fin());
        clase.setUnidad(unidad);
        clase.setTema(tema);
        clase.setNotas(request.notas());
        clase.setDocumentoUrl(request.documentoUrl());
        clase.setDocumentoNombre(request.documentoNombre());

        Clase savedClase = claseRepository.save(clase);
        return toResponse(savedClase);
    }

    /**
     * Actualiza una clase existente.
     */
    public ClaseResponse updateClase(String id, UpdateClaseRequest request) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada con ID: " + id));

        if (request.fecha() != null) {
            clase.setFecha(request.fecha());
        }
        if (request.inicio() != null) {
            clase.setInicio(request.inicio());
        }
        if (request.fin() != null) {
            clase.setFin(request.fin());
        }
        if (request.unidadId() != null) {
            if (request.unidadId().isBlank()) {
                clase.setUnidad(null);
            } else {
                Unidad unidad = unidadRepository.findById(request.unidadId())
                        .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + request.unidadId()));
                clase.setUnidad(unidad);
            }
        }
        if (request.temaId() != null) {
            if (request.temaId().isBlank()) {
                clase.setTema(null);
            } else {
                Tema tema = temaRepository.findById(request.temaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + request.temaId()));
                clase.setTema(tema);
            }
        }
        if (request.notas() != null) {
            clase.setNotas(request.notas());
        }
        if (request.documentoUrl() != null) {
            clase.setDocumentoUrl(request.documentoUrl());
        }
        if (request.documentoNombre() != null) {
            clase.setDocumentoNombre(request.documentoNombre());
        }

        Clase updatedClase = claseRepository.save(clase);
        return toResponse(updatedClase);
    }

    /**
     * Elimina lógicamente una clase (soft delete).
     */
    public void deleteClase(String id) {
        Clase clase = claseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clase no encontrada con ID: " + id));
        clase.setDeletedAt(LocalDateTime.now());
        claseRepository.save(clase);
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
        return toResponse(restoredClase);
    }
}
