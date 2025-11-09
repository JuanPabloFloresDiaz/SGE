package com.example.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateEvaluacionRequest;
import com.example.api.dto.request.UpdateEvaluacionRequest;
import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.dto.response.CursoResponse;
import com.example.api.dto.response.EvaluacionResponse;
import com.example.api.dto.response.PeriodoResponse;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.dto.response.RolResponse;
import com.example.api.dto.response.TipoEvaluacionResponse;
import com.example.api.dto.response.UsuarioResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Curso;
import com.example.api.model.Evaluacion;
import com.example.api.model.TipoEvaluacion;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.EvaluacionRepository;
import com.example.api.repository.TipoEvaluacionRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de evaluaciones.
 */
@Service
@Transactional
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final CursoRepository cursoRepository;
    private final TipoEvaluacionRepository tipoEvaluacionRepository;

    /**
     * Constructor con inyección de dependencias.
     */
    public EvaluacionService(EvaluacionRepository evaluacionRepository,
                            CursoRepository cursoRepository,
                            TipoEvaluacionRepository tipoEvaluacionRepository) {
        this.evaluacionRepository = evaluacionRepository;
        this.cursoRepository = cursoRepository;
        this.tipoEvaluacionRepository = tipoEvaluacionRepository;
    }

    /**
     * Convierte una entidad Evaluacion a EvaluacionResponse.
     */
    private EvaluacionResponse toResponse(Evaluacion evaluacion) {
        // Construir CursoResponse
        Curso curso = evaluacion.getCurso();
        
        AsignaturaResponse asignaturaResponse = new AsignaturaResponse(
                curso.getAsignatura().getId(),
                curso.getAsignatura().getCodigo(),
                curso.getAsignatura().getNombre(),
                curso.getAsignatura().getDescripcion(),
                curso.getAsignatura().getImagenUrl(),
                curso.getAsignatura().getCreatedAt(),
                curso.getAsignatura().getUpdatedAt(),
                curso.getAsignatura().getDeletedAt()
        );

        RolResponse rolResponse = new RolResponse(
                curso.getProfesor().getUsuario().getRol().getId(),
                curso.getProfesor().getUsuario().getRol().getNombre(),
                curso.getProfesor().getUsuario().getRol().getDescripcion(),
                curso.getProfesor().getUsuario().getRol().getCreatedAt(),
                curso.getProfesor().getUsuario().getRol().getUpdatedAt(),
                curso.getProfesor().getUsuario().getRol().getDeletedAt()
        );

        UsuarioResponse usuarioResponse = new UsuarioResponse(
                curso.getProfesor().getUsuario().getId(),
                curso.getProfesor().getUsuario().getUsername(),
                curso.getProfesor().getUsuario().getNombre(),
                curso.getProfesor().getUsuario().getEmail(),
                curso.getProfesor().getUsuario().getTelefono(),
                curso.getProfesor().getUsuario().getActivo(),
                curso.getProfesor().getUsuario().getFotoPerfilUrl(),
                rolResponse,
                curso.getProfesor().getUsuario().getCreatedAt(),
                curso.getProfesor().getUsuario().getUpdatedAt(),
                curso.getProfesor().getUsuario().getDeletedAt()
        );

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

        CursoResponse cursoResponse = new CursoResponse(
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

        // Construir TipoEvaluacionResponse
        TipoEvaluacion tipo = evaluacion.getTipoEvaluacion();
        TipoEvaluacionResponse tipoResponse = new TipoEvaluacionResponse(
                tipo.getId(),
                tipo.getNombre(),
                tipo.getDescripcion(),
                tipo.getPeso(),
                tipo.getCreatedAt(),
                tipo.getUpdatedAt(),
                tipo.getDeletedAt()
        );

        return new EvaluacionResponse(
                evaluacion.getId(),
                cursoResponse,
                tipoResponse,
                evaluacion.getNombre(),
                evaluacion.getFecha(),
                evaluacion.getPeso(),
                evaluacion.getPublicado(),
                evaluacion.getDocumentoUrl(),
                evaluacion.getDocumentoNombre(),
                evaluacion.getCreatedAt(),
                evaluacion.getUpdatedAt(),
                evaluacion.getDeletedAt()
        );
    }

    /**
     * Obtiene todas las evaluaciones activas (paginadas).
     */
    @Transactional(readOnly = true)
    public Page<EvaluacionResponse> getAllEvaluaciones(Pageable pageable) {
        return evaluacionRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene una evaluación por su ID.
     */
    @Transactional(readOnly = true)
    public EvaluacionResponse getEvaluacionById(String id) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));
        return toResponse(evaluacion);
    }

    /**
     * Busca evaluaciones por curso.
     */
    @Transactional(readOnly = true)
    public List<EvaluacionResponse> getEvaluacionesByCursoId(String cursoId) {
        return evaluacionRepository.findByCursoId(cursoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca evaluaciones por tipo.
     */
    @Transactional(readOnly = true)
    public List<EvaluacionResponse> getEvaluacionesByTipoId(String tipoId) {
        return evaluacionRepository.findByTipoEvaluacionId(tipoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las próximas evaluaciones (fecha >= hoy).
     */
    @Transactional(readOnly = true)
    public List<EvaluacionResponse> getProximasEvaluaciones() {
        LocalDate hoy = LocalDate.now();
        return evaluacionRepository.findProximasEvaluaciones(hoy).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Ordena evaluaciones por fecha usando el algoritmo de ordenamiento burbuja.
     * Implementa Bubble Sort como estructura de datos con propósito educativo.
     * Apropiado para listas pequeñas (<20 elementos).
     * 
     * Complejidad: O(n²) en el peor caso
     * Uso: Demostración didáctica del algoritmo de ordenamiento más básico
     */
    @Transactional(readOnly = true)
    public List<EvaluacionResponse> getEvaluacionesOrdenadas() {
        // Obtener todas las evaluaciones activas
        List<Evaluacion> evaluaciones = new ArrayList<>(evaluacionRepository.findAllActive());
        
        int n = evaluaciones.size();
        
        // Algoritmo de ordenamiento burbuja (Bubble Sort)
        // Compara elementos adyacentes e intercambia si están en orden incorrecto
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            
            // Última i elementos ya están ordenados
            for (int j = 0; j < n - i - 1; j++) {
                Evaluacion actual = evaluaciones.get(j);
                Evaluacion siguiente = evaluaciones.get(j + 1);
                
                // Comparar fechas (null se considera mayor, va al final)
                boolean debeIntercambiar = false;
                
                if (actual.getFecha() == null && siguiente.getFecha() != null) {
                    debeIntercambiar = true;
                } else if (actual.getFecha() != null && siguiente.getFecha() != null) {
                    if (actual.getFecha().isAfter(siguiente.getFecha())) {
                        debeIntercambiar = true;
                    }
                }
                
                // Intercambiar si están en orden incorrecto
                if (debeIntercambiar) {
                    evaluaciones.set(j, siguiente);
                    evaluaciones.set(j + 1, actual);
                    swapped = true;
                }
            }
            
            // Si no hubo intercambios, la lista ya está ordenada
            if (!swapped) {
                break;
            }
        }
        
        // Convertir a response
        return evaluaciones.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las evaluaciones eliminadas.
     */
    @Transactional(readOnly = true)
    public List<EvaluacionResponse> getEvaluacionesDeleted() {
        return evaluacionRepository.findAllDeleted().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva evaluación.
     */
    public EvaluacionResponse createEvaluacion(CreateEvaluacionRequest request) {
        // Validar que el curso existe
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));

        // Validar que el tipo de evaluación existe
        TipoEvaluacion tipo = tipoEvaluacionRepository.findById(request.tipoId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + request.tipoId()));

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setCurso(curso);
        evaluacion.setTipoEvaluacion(tipo);
        evaluacion.setNombre(request.nombre());
        evaluacion.setFecha(request.fecha());
        evaluacion.setPeso(request.peso() != null ? request.peso() : java.math.BigDecimal.ZERO);
        evaluacion.setPublicado(request.publicado() != null ? request.publicado() : false);
        evaluacion.setDocumentoUrl(request.documentoUrl());
        evaluacion.setDocumentoNombre(request.documentoNombre());

        Evaluacion saved = evaluacionRepository.save(evaluacion);
        return toResponse(saved);
    }

    /**
     * Actualiza una evaluación existente.
     */
    public EvaluacionResponse updateEvaluacion(String id, UpdateEvaluacionRequest request) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));

        if (request.cursoId() != null) {
            Curso curso = cursoRepository.findById(request.cursoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));
            evaluacion.setCurso(curso);
        }
        if (request.tipoId() != null) {
            TipoEvaluacion tipo = tipoEvaluacionRepository.findById(request.tipoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + request.tipoId()));
            evaluacion.setTipoEvaluacion(tipo);
        }
        if (request.nombre() != null) {
            evaluacion.setNombre(request.nombre());
        }
        if (request.fecha() != null) {
            evaluacion.setFecha(request.fecha());
        }
        if (request.peso() != null) {
            evaluacion.setPeso(request.peso());
        }
        if (request.publicado() != null) {
            evaluacion.setPublicado(request.publicado());
        }
        if (request.documentoUrl() != null) {
            evaluacion.setDocumentoUrl(request.documentoUrl());
        }
        if (request.documentoNombre() != null) {
            evaluacion.setDocumentoNombre(request.documentoNombre());
        }

        Evaluacion updated = evaluacionRepository.save(evaluacion);
        return toResponse(updated);
    }

    /**
     * Elimina lógicamente una evaluación (soft delete).
     */
    public void deleteEvaluacion(String id) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));
        evaluacion.setDeletedAt(LocalDateTime.now());
        evaluacionRepository.save(evaluacion);
    }

    /**
     * Elimina permanentemente una evaluación.
     */
    public void permanentDeleteEvaluacion(String id) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));
        evaluacionRepository.delete(evaluacion);
    }

    /**
     * Restaura una evaluación eliminada.
     */
    public EvaluacionResponse restoreEvaluacion(String id) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));
        evaluacion.setDeletedAt(null);
        Evaluacion restored = evaluacionRepository.save(evaluacion);
        return toResponse(restored);
    }
}
