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
import com.example.api.dto.response.EvaluacionResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Curso;
import com.example.api.model.Evaluacion;
import com.example.api.model.TipoEvaluacion;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.EvaluacionRepository;
import com.example.api.repository.TipoEvaluacionRepository;
import com.example.api.mapper.EvaluacionMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de evaluaciones.
 */
@Service
@Transactional
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final CursoRepository cursoRepository;
    private final TipoEvaluacionRepository tipoEvaluacionRepository;
    private final EvaluacionMapper evaluacionMapper;

    /**
     * Constructor con inyección de dependencias.
     */
    public EvaluacionService(EvaluacionRepository evaluacionRepository,
            CursoRepository cursoRepository,
            TipoEvaluacionRepository tipoEvaluacionRepository,
            EvaluacionMapper evaluacionMapper) {
        this.evaluacionRepository = evaluacionRepository;
        this.cursoRepository = cursoRepository;
        this.tipoEvaluacionRepository = tipoEvaluacionRepository;
        this.evaluacionMapper = evaluacionMapper;
    }

    /**
     * Obtiene todas las evaluaciones activas (paginadas).
     */
    @Transactional(readOnly = true)
    public Page<EvaluacionResponse> getAllEvaluaciones(Pageable pageable) {
        return evaluacionRepository.findAllActive(pageable)
                .map(evaluacionMapper::toResponse);
    }

    /**
     * Obtiene una evaluación por su ID.
     */
    @Transactional(readOnly = true)
    public EvaluacionResponse getEvaluacionById(String id) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));
        return evaluacionMapper.toResponse(evaluacion);
    }

    /**
     * Busca evaluaciones por curso.
     */
    @Transactional(readOnly = true)
    public List<EvaluacionResponse> getEvaluacionesByCursoId(String cursoId) {
        return evaluacionRepository.findByCursoId(cursoId).stream()
                .map(evaluacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca evaluaciones por tipo.
     */
    @Transactional(readOnly = true)
    public List<EvaluacionResponse> getEvaluacionesByTipoId(String tipoId) {
        return evaluacionRepository.findByTipoEvaluacionId(tipoId).stream()
                .map(evaluacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las próximas evaluaciones (fecha >= hoy).
     */
    @Transactional(readOnly = true)
    public List<EvaluacionResponse> getProximasEvaluaciones() {
        LocalDate hoy = LocalDate.now();
        return evaluacionRepository.findProximasEvaluaciones(hoy).stream()
                .map(evaluacionMapper::toResponse)
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
                .map(evaluacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las evaluaciones eliminadas.
     */
    @Transactional(readOnly = true)
    public List<EvaluacionResponse> getEvaluacionesDeleted() {
        return evaluacionRepository.findAllDeleted().stream()
                .map(evaluacionMapper::toResponse)
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
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tipo de evaluación no encontrado con ID: " + request.tipoId()));

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
        return evaluacionMapper.toResponse(saved);
    }

    /**
     * Actualiza una evaluación existente.
     */
    public EvaluacionResponse updateEvaluacion(String id, UpdateEvaluacionRequest request) {
        Evaluacion evaluacion = evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));

        if (request.cursoId() != null) {
            Curso curso = cursoRepository.findById(request.cursoId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));
            evaluacion.setCurso(curso);
        }
        if (request.tipoId() != null) {
            TipoEvaluacion tipo = tipoEvaluacionRepository.findById(request.tipoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tipo de evaluación no encontrado con ID: " + request.tipoId()));
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
        return evaluacionMapper.toResponse(updated);
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
        return evaluacionMapper.toResponse(restored);
    }
}
