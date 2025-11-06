package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateTemaRequest;
import com.example.api.dto.request.UpdateTemaRequest;
import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.dto.response.CursoResponse;
import com.example.api.dto.response.PeriodoResponse;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.dto.response.RolResponse;
import com.example.api.dto.response.TemaResponse;
import com.example.api.dto.response.UnidadResponse;
import com.example.api.dto.response.UsuarioResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Tema;
import com.example.api.model.Unidad;
import com.example.api.repository.TemaRepository;
import com.example.api.repository.UnidadRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de temas.
 */
@Service
@Transactional
public class TemaService {

    private final TemaRepository temaRepository;
    private final UnidadRepository unidadRepository;

    /**
     * Constructor con inyección de dependencias.
     */
    public TemaService(TemaRepository temaRepository,
                      UnidadRepository unidadRepository) {
        this.temaRepository = temaRepository;
        this.unidadRepository = unidadRepository;
    }

    /**
     * Convierte una entidad Tema a TemaResponse.
     */
    private TemaResponse toResponse(Tema tema) {
        Unidad unidad = tema.getUnidad();
        
        // Construir AsignaturaResponse
        AsignaturaResponse asignaturaResponse = new AsignaturaResponse(
            unidad.getCurso().getAsignatura().getId(),
            unidad.getCurso().getAsignatura().getCodigo(),
            unidad.getCurso().getAsignatura().getNombre(),
            unidad.getCurso().getAsignatura().getDescripcion(),
            unidad.getCurso().getAsignatura().getCreatedAt(),
            unidad.getCurso().getAsignatura().getUpdatedAt(),
            unidad.getCurso().getAsignatura().getDeletedAt()
        );
        
        // Construir RolResponse
        RolResponse rolResponse = new RolResponse(
            unidad.getCurso().getProfesor().getUsuario().getRol().getId(),
            unidad.getCurso().getProfesor().getUsuario().getRol().getNombre(),
            unidad.getCurso().getProfesor().getUsuario().getRol().getDescripcion(),
            unidad.getCurso().getProfesor().getUsuario().getRol().getCreatedAt(),
            unidad.getCurso().getProfesor().getUsuario().getRol().getUpdatedAt(),
            unidad.getCurso().getProfesor().getUsuario().getRol().getDeletedAt()
        );
        
        // Construir UsuarioResponse
        UsuarioResponse usuarioResponse = new UsuarioResponse(
            unidad.getCurso().getProfesor().getUsuario().getId(),
            unidad.getCurso().getProfesor().getUsuario().getUsername(),
            unidad.getCurso().getProfesor().getUsuario().getNombre(),
            unidad.getCurso().getProfesor().getUsuario().getEmail(),
            unidad.getCurso().getProfesor().getUsuario().getTelefono(),
            unidad.getCurso().getProfesor().getUsuario().getActivo(),
            rolResponse,
            unidad.getCurso().getProfesor().getUsuario().getCreatedAt(),
            unidad.getCurso().getProfesor().getUsuario().getUpdatedAt(),
            unidad.getCurso().getProfesor().getUsuario().getDeletedAt()
        );
        
        // Construir ProfesorResponse
        ProfesorResponse profesorResponse = new ProfesorResponse(
            unidad.getCurso().getProfesor().getId(),
            usuarioResponse,
            unidad.getCurso().getProfesor().getEspecialidad(),
            unidad.getCurso().getProfesor().getContrato(),
            unidad.getCurso().getProfesor().getActivo(),
            unidad.getCurso().getProfesor().getCreatedAt(),
            unidad.getCurso().getProfesor().getUpdatedAt(),
            unidad.getCurso().getProfesor().getDeletedAt()
        );
        
        // Construir PeriodoResponse
        PeriodoResponse periodoResponse = new PeriodoResponse(
            unidad.getCurso().getPeriodo().getId(),
            unidad.getCurso().getPeriodo().getNombre(),
            unidad.getCurso().getPeriodo().getFechaInicio(),
            unidad.getCurso().getPeriodo().getFechaFin(),
            unidad.getCurso().getPeriodo().getActivo(),
            unidad.getCurso().getPeriodo().getCreatedAt(),
            unidad.getCurso().getPeriodo().getUpdatedAt(),
            unidad.getCurso().getPeriodo().getDeletedAt()
        );
        
        // Construir CursoResponse
        CursoResponse cursoResponse = new CursoResponse(
            unidad.getCurso().getId(),
            asignaturaResponse,
            profesorResponse,
            periodoResponse,
            unidad.getCurso().getNombreGrupo(),
            unidad.getCurso().getAulaDefault(),
            unidad.getCurso().getCupo(),
            unidad.getCurso().getCreatedAt(),
            unidad.getCurso().getUpdatedAt(),
            unidad.getCurso().getDeletedAt()
        );
        
        // Construir UnidadResponse
        UnidadResponse unidadResponse = new UnidadResponse(
            unidad.getId(),
            cursoResponse,
            unidad.getTitulo(),
            unidad.getDescripcion(),
            unidad.getNumero(),
            unidad.getCreatedAt(),
            unidad.getUpdatedAt(),
            unidad.getDeletedAt()
        );

        return new TemaResponse(
            tema.getId(),
            unidadResponse,
            tema.getTitulo(),
            tema.getDescripcion(),
            tema.getNumero(),
            tema.getDuracionMinutos(),
            tema.getCreatedAt(),
            tema.getUpdatedAt(),
            tema.getDeletedAt()
        );
    }

    /**
     * Obtiene todos los temas activos (paginados).
     */
    @Transactional(readOnly = true)
    public Page<TemaResponse> getAllTemas(Pageable pageable) {
        return temaRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene un tema por su ID.
     */
    @Transactional(readOnly = true)
    public TemaResponse getTemaById(String id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));
        return toResponse(tema);
    }

    /**
     * Busca temas por unidad.
     */
    @Transactional(readOnly = true)
    public List<TemaResponse> getTemasByUnidadId(String unidadId) {
        return temaRepository.findByUnidadId(unidadId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca temas por título.
     */
    @Transactional(readOnly = true)
    public List<TemaResponse> getTemasByTitulo(String titulo) {
        return temaRepository.findByTituloContaining(titulo).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los temas eliminados.
     */
    @Transactional(readOnly = true)
    public List<TemaResponse> getTemasDeleted() {
        return temaRepository.findAllDeleted().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo tema.
     */
    public TemaResponse createTema(CreateTemaRequest request) {
        // Validar que la unidad existe
        Unidad unidad = unidadRepository.findById(request.unidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + request.unidadId()));

        Tema tema = new Tema();
        tema.setUnidad(unidad);
        tema.setTitulo(request.titulo());
        tema.setDescripcion(request.descripcion());
        tema.setNumero(request.numero());
        tema.setDuracionMinutos(request.duracionMinutos());

        Tema savedTema = temaRepository.save(tema);
        return toResponse(savedTema);
    }

    /**
     * Actualiza un tema existente.
     */
    public TemaResponse updateTema(String id, UpdateTemaRequest request) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));

        if (request.titulo() != null) {
            tema.setTitulo(request.titulo());
        }
        if (request.descripcion() != null) {
            tema.setDescripcion(request.descripcion());
        }
        if (request.numero() != null) {
            tema.setNumero(request.numero());
        }
        if (request.duracionMinutos() != null) {
            tema.setDuracionMinutos(request.duracionMinutos());
        }

        Tema updatedTema = temaRepository.save(tema);
        return toResponse(updatedTema);
    }

    /**
     * Elimina lógicamente un tema (soft delete).
     */
    public void deleteTema(String id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));
        tema.setDeletedAt(LocalDateTime.now());
        temaRepository.save(tema);
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
        return toResponse(restoredTema);
    }
}
