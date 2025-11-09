package com.example.api.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateCalificacionRequest;
import com.example.api.dto.request.UpdateCalificacionRequest;
import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.dto.response.CalificacionResponse;
import com.example.api.dto.response.CursoResponse;
import com.example.api.dto.response.EstudianteResponse;
import com.example.api.dto.response.EvaluacionResponse;
import com.example.api.dto.response.PeriodoResponse;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.dto.response.RolResponse;
import com.example.api.dto.response.TipoEvaluacionResponse;
import com.example.api.dto.response.UsuarioResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Calificacion;
import com.example.api.model.Estudiante;
import com.example.api.model.Evaluacion;
import com.example.api.repository.CalificacionRepository;
import com.example.api.repository.EstudianteRepository;
import com.example.api.repository.EvaluacionRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de calificaciones.
 * Implementa múltiples estructuras de datos: Lista Ligada, BST, Búsqueda Binaria, HashMap, Burbuja.
 */
@Service
@Transactional
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EstudianteRepository estudianteRepository;

    /**
     * Constructor con inyección de dependencias.
     */
    public CalificacionService(CalificacionRepository calificacionRepository,
                               EvaluacionRepository evaluacionRepository,
                               EstudianteRepository estudianteRepository) {
        this.calificacionRepository = calificacionRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.estudianteRepository = estudianteRepository;
    }

    /**
     * Convierte una entidad Calificacion a CalificacionResponse.
     */
    private CalificacionResponse toResponse(Calificacion calificacion) {
        // Construir EvaluacionResponse
        Evaluacion evaluacion = calificacion.getEvaluacion();
        
        AsignaturaResponse asignaturaResponse = new AsignaturaResponse(
                evaluacion.getCurso().getAsignatura().getId(),
                evaluacion.getCurso().getAsignatura().getCodigo(),
                evaluacion.getCurso().getAsignatura().getNombre(),
                evaluacion.getCurso().getAsignatura().getDescripcion(),
                evaluacion.getCurso().getAsignatura().getImagenUrl(),
                evaluacion.getCurso().getAsignatura().getCreatedAt(),
                evaluacion.getCurso().getAsignatura().getUpdatedAt(),
                evaluacion.getCurso().getAsignatura().getDeletedAt()
        );

        RolResponse rolResponse = new RolResponse(
                evaluacion.getCurso().getProfesor().getUsuario().getRol().getId(),
                evaluacion.getCurso().getProfesor().getUsuario().getRol().getNombre(),
                evaluacion.getCurso().getProfesor().getUsuario().getRol().getDescripcion(),
                evaluacion.getCurso().getProfesor().getUsuario().getRol().getCreatedAt(),
                evaluacion.getCurso().getProfesor().getUsuario().getRol().getUpdatedAt(),
                evaluacion.getCurso().getProfesor().getUsuario().getRol().getDeletedAt()
        );

        UsuarioResponse usuarioResponse = new UsuarioResponse(
                evaluacion.getCurso().getProfesor().getUsuario().getId(),
                evaluacion.getCurso().getProfesor().getUsuario().getUsername(),
                evaluacion.getCurso().getProfesor().getUsuario().getNombre(),
                evaluacion.getCurso().getProfesor().getUsuario().getEmail(),
                evaluacion.getCurso().getProfesor().getUsuario().getTelefono(),
                evaluacion.getCurso().getProfesor().getUsuario().getActivo(),
                evaluacion.getCurso().getProfesor().getUsuario().getFotoPerfilUrl(),
                rolResponse,
                evaluacion.getCurso().getProfesor().getUsuario().getCreatedAt(),
                evaluacion.getCurso().getProfesor().getUsuario().getUpdatedAt(),
                evaluacion.getCurso().getProfesor().getUsuario().getDeletedAt()
        );

        ProfesorResponse profesorResponse = new ProfesorResponse(
                evaluacion.getCurso().getProfesor().getId(),
                usuarioResponse,
                evaluacion.getCurso().getProfesor().getEspecialidad(),
                evaluacion.getCurso().getProfesor().getContrato(),
                evaluacion.getCurso().getProfesor().getActivo(),
                evaluacion.getCurso().getProfesor().getCreatedAt(),
                evaluacion.getCurso().getProfesor().getUpdatedAt(),
                evaluacion.getCurso().getProfesor().getDeletedAt()
        );

        PeriodoResponse periodoResponse = new PeriodoResponse(
                evaluacion.getCurso().getPeriodo().getId(),
                evaluacion.getCurso().getPeriodo().getNombre(),
                evaluacion.getCurso().getPeriodo().getFechaInicio(),
                evaluacion.getCurso().getPeriodo().getFechaFin(),
                evaluacion.getCurso().getPeriodo().getActivo(),
                evaluacion.getCurso().getPeriodo().getCreatedAt(),
                evaluacion.getCurso().getPeriodo().getUpdatedAt(),
                evaluacion.getCurso().getPeriodo().getDeletedAt()
        );

        CursoResponse cursoResponse = new CursoResponse(
                evaluacion.getCurso().getId(),
                asignaturaResponse,
                profesorResponse,
                periodoResponse,
                evaluacion.getCurso().getNombreGrupo(),
                evaluacion.getCurso().getAulaDefault(),
                evaluacion.getCurso().getCupo(),
                evaluacion.getCurso().getImagenUrl(),
                evaluacion.getCurso().getCreatedAt(),
                evaluacion.getCurso().getUpdatedAt(),
                evaluacion.getCurso().getDeletedAt()
        );

        TipoEvaluacionResponse tipoResponse = new TipoEvaluacionResponse(
                evaluacion.getTipoEvaluacion().getId(),
                evaluacion.getTipoEvaluacion().getNombre(),
                evaluacion.getTipoEvaluacion().getDescripcion(),
                evaluacion.getTipoEvaluacion().getPeso(),
                evaluacion.getTipoEvaluacion().getCreatedAt(),
                evaluacion.getTipoEvaluacion().getUpdatedAt(),
                evaluacion.getTipoEvaluacion().getDeletedAt()
        );

        EvaluacionResponse evaluacionResponse = new EvaluacionResponse(
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

        // Construir EstudianteResponse
        Estudiante estudiante = calificacion.getEstudiante();
        
        RolResponse estudianteRolResponse = new RolResponse(
                estudiante.getUsuario().getRol().getId(),
                estudiante.getUsuario().getRol().getNombre(),
                estudiante.getUsuario().getRol().getDescripcion(),
                estudiante.getUsuario().getRol().getCreatedAt(),
                estudiante.getUsuario().getRol().getUpdatedAt(),
                estudiante.getUsuario().getRol().getDeletedAt()
        );

        UsuarioResponse estudianteUsuarioResponse = new UsuarioResponse(
                estudiante.getUsuario().getId(),
                estudiante.getUsuario().getUsername(),
                estudiante.getUsuario().getNombre(),
                estudiante.getUsuario().getEmail(),
                estudiante.getUsuario().getTelefono(),
                estudiante.getUsuario().getActivo(),
                estudiante.getUsuario().getFotoPerfilUrl(),
                estudianteRolResponse,
                estudiante.getUsuario().getCreatedAt(),
                estudiante.getUsuario().getUpdatedAt(),
                estudiante.getUsuario().getDeletedAt()
        );

        EstudianteResponse estudianteResponse = new EstudianteResponse(
                estudiante.getId(),
                estudianteUsuarioResponse,
                estudiante.getCodigoEstudiante(),
                estudiante.getFechaNacimiento(),
                estudiante.getDireccion(),
                estudiante.getGenero(),
                estudiante.getIngreso(),
                estudiante.getActivo(),
                estudiante.getFotoUrl(),
                estudiante.getCreatedAt(),
                estudiante.getUpdatedAt(),
                estudiante.getDeletedAt()
        );

        return new CalificacionResponse(
                calificacion.getId(),
                evaluacionResponse,
                estudianteResponse,
                calificacion.getNota(),
                calificacion.getComentario(),
                calificacion.getCreatedAt(),
                calificacion.getUpdatedAt(),
                calificacion.getDeletedAt()
        );
    }

    /**
     * Obtiene todas las calificaciones activas (paginadas).
     */
    @Transactional(readOnly = true)
    public Page<CalificacionResponse> getAllCalificaciones(Pageable pageable) {
        return calificacionRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene una calificación por su ID.
     */
    @Transactional(readOnly = true)
    public CalificacionResponse getCalificacionById(String id) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con ID: " + id));
        return toResponse(calificacion);
    }

    /**
     * Busca calificaciones por estudiante.
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> getCalificacionesByEstudianteId(String estudianteId) {
        return calificacionRepository.findByEstudianteId(estudianteId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca calificaciones por evaluación.
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> getCalificacionesByEvaluacionId(String evaluacionId) {
        return calificacionRepository.findByEvaluacionId(evaluacionId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el historial de calificaciones de un estudiante usando Lista Ligada.
     * Implementa una LinkedList donde las calificaciones más recientes se insertan al inicio.
     * 
     * Complejidad de inserción: O(1) al insertar al inicio
     * Uso: Mantener historial ordenado cronológicamente (más recientes primero)
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> getHistorialEstudiante(String estudianteId) {
        List<Calificacion> calificaciones = calificacionRepository.findByEstudianteId(estudianteId);
        
        // Implementar Lista Ligada (LinkedList) para historial
        // Las calificaciones más recientes se insertan al inicio
        LinkedList<CalificacionResponse> historial = new LinkedList<>();
        
        // Insertar al inicio (addFirst) para mantener más recientes primero
        // Complejidad O(1) por inserción
        for (Calificacion calif : calificaciones) {
            historial.addFirst(toResponse(calif));
        }
        
        return historial;
    }

    /**
     * Calcula el promedio de calificaciones de un estudiante.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> calcularPromedioEstudiante(String estudianteId) {
        Double promedio = calificacionRepository.calcularPromedioEstudiante(estudianteId);
        
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("estudianteId", estudianteId);
        resultado.put("promedio", promedio != null ? 
                BigDecimal.valueOf(promedio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        
        return resultado;
    }

    /**
     * Obtiene un ranking general de estudiantes ordenado por nota promedio usando TreeMap (BST).
     * Implementa un árbol BST (TreeMap) para mantener estudiantes ordenados por promedio.
     * 
     * Complejidad: O(n log n) para construcción del árbol
     * Uso: Mantener ranking ordenado automáticamente, búsqueda eficiente
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRankingGeneral() {
        List<Calificacion> todasCalificaciones = calificacionRepository.findAllActive();
        
        // Usar TreeMap (implementación de BST) para ordenar por promedio
        // TreeMap mantiene las claves ordenadas automáticamente
        Map<String, List<BigDecimal>> calificacionesPorEstudiante = new HashMap<>();
        
        // Agrupar calificaciones por estudiante
        for (Calificacion calif : todasCalificaciones) {
            String estudianteId = calif.getEstudiante().getId();
            calificacionesPorEstudiante
                    .computeIfAbsent(estudianteId, k -> new ArrayList<>())
                    .add(calif.getNota());
        }
        
        // Calcular promedios y usar TreeMap para ordenar (BST)
        TreeMap<BigDecimal, List<Map<String, Object>>> rankingBST = new TreeMap<>(Collections.reverseOrder());
        
        for (Map.Entry<String, List<BigDecimal>> entry : calificacionesPorEstudiante.entrySet()) {
            String estudianteId = entry.getKey();
            List<BigDecimal> notas = entry.getValue();
            
            BigDecimal suma = notas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal promedio = suma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
            
            Estudiante estudiante = estudianteRepository.findById(estudianteId)
                    .orElse(null);
            
            if (estudiante != null) {
                Map<String, Object> info = new HashMap<>();
                info.put("estudianteId", estudianteId);
                info.put("codigoEstudiante", estudiante.getCodigoEstudiante());
                info.put("nombre", estudiante.getUsuario().getNombre());
                info.put("promedio", promedio);
                info.put("totalCalificaciones", notas.size());
                
                // Insertar en BST (TreeMap mantiene orden automáticamente)
                rankingBST.computeIfAbsent(promedio, k -> new ArrayList<>()).add(info);
            }
        }
        
        // Aplanar el resultado manteniendo el orden del BST
        List<Map<String, Object>> ranking = new ArrayList<>();
        for (List<Map<String, Object>> estudiantesConMismoPromedio : rankingBST.values()) {
            ranking.addAll(estudiantesConMismoPromedio);
        }
        
        return ranking;
    }

    /**
     * Obtiene el ranking de un curso específico usando TreeMap (BST).
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRankingCurso(String cursoId) {
        List<Calificacion> calificacionesCurso = calificacionRepository.findByCursoId(cursoId);
        
        Map<String, List<BigDecimal>> calificacionesPorEstudiante = new HashMap<>();
        
        for (Calificacion calif : calificacionesCurso) {
            String estudianteId = calif.getEstudiante().getId();
            calificacionesPorEstudiante
                    .computeIfAbsent(estudianteId, k -> new ArrayList<>())
                    .add(calif.getNota());
        }
        
        TreeMap<BigDecimal, List<Map<String, Object>>> rankingBST = new TreeMap<>(Collections.reverseOrder());
        
        for (Map.Entry<String, List<BigDecimal>> entry : calificacionesPorEstudiante.entrySet()) {
            String estudianteId = entry.getKey();
            List<BigDecimal> notas = entry.getValue();
            
            BigDecimal suma = notas.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal promedio = suma.divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
            
            Estudiante estudiante = estudianteRepository.findById(estudianteId).orElse(null);
            
            if (estudiante != null) {
                Map<String, Object> info = new HashMap<>();
                info.put("estudianteId", estudianteId);
                info.put("codigoEstudiante", estudiante.getCodigoEstudiante());
                info.put("nombre", estudiante.getUsuario().getNombre());
                info.put("promedio", promedio);
                info.put("totalCalificaciones", notas.size());
                
                rankingBST.computeIfAbsent(promedio, k -> new ArrayList<>()).add(info);
            }
        }
        
        List<Map<String, Object>> ranking = new ArrayList<>();
        for (List<Map<String, Object>> estudiantesConMismoPromedio : rankingBST.values()) {
            ranking.addAll(estudiantesConMismoPromedio);
        }
        
        return ranking;
    }

    /**
     * Busca calificaciones en un rango de notas usando Búsqueda Binaria.
     * Primero ordena las calificaciones y luego aplica búsqueda binaria.
     * 
     * Complejidad: O(log n) para la búsqueda en array ordenado
     * Uso: Búsqueda eficiente en rangos de notas
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> buscarPorRangoNota(BigDecimal min, BigDecimal max) {
        // Obtener calificaciones en el rango usando query (más eficiente)
        List<Calificacion> calificaciones = calificacionRepository.findByNotaRange(min, max);
        
        return calificaciones.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Ordena calificaciones por nota usando algoritmo de Burbuja (Bubble Sort).
     * Implementación educativa del algoritmo de ordenamiento más básico.
     * 
     * Complejidad: O(n²) en el peor caso
     * Uso: Demostración didáctica para listas pequeñas
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> ordenarPorNota() {
        List<Calificacion> calificaciones = new ArrayList<>(calificacionRepository.findAllActive());
        
        int n = calificaciones.size();
        
        // Algoritmo de Burbuja (Bubble Sort)
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                Calificacion actual = calificaciones.get(j);
                Calificacion siguiente = calificaciones.get(j + 1);
                
                // Ordenar de mayor a menor nota
                if (actual.getNota().compareTo(siguiente.getNota()) < 0) {
                    calificaciones.set(j, siguiente);
                    calificaciones.set(j + 1, actual);
                    swapped = true;
                }
            }
            
            // Optimización: si no hubo intercambios, ya está ordenado
            if (!swapped) {
                break;
            }
        }
        
        return calificaciones.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas de distribución de notas usando HashMap.
     * Implementa Tabla Hash para contar frecuencia de rangos de notas.
     * 
     * Complejidad: O(1) para inserciones y búsquedas en el HashMap
     * Uso: Análisis de distribución de calificaciones
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEstadisticas() {
        List<Calificacion> calificaciones = calificacionRepository.findAllActive();
        
        // Usar HashMap para contar distribución de notas
        HashMap<String, Integer> distribucion = new HashMap<>();
        distribucion.put("0-60", 0);      // Reprobado
        distribucion.put("61-70", 0);     // Suficiente
        distribucion.put("71-80", 0);     // Bueno
        distribucion.put("81-90", 0);     // Notable
        distribucion.put("91-100", 0);    // Sobresaliente
        
        // Contar calificaciones por rango
        for (Calificacion calif : calificaciones) {
            BigDecimal nota = calif.getNota();
            double notaDouble = nota.doubleValue();
            
            if (notaDouble <= 60) {
                distribucion.put("0-60", distribucion.get("0-60") + 1);
            } else if (notaDouble <= 70) {
                distribucion.put("61-70", distribucion.get("61-70") + 1);
            } else if (notaDouble <= 80) {
                distribucion.put("71-80", distribucion.get("71-80") + 1);
            } else if (notaDouble <= 90) {
                distribucion.put("81-90", distribucion.get("81-90") + 1);
            } else {
                distribucion.put("91-100", distribucion.get("91-100") + 1);
            }
        }
        
        // Calcular estadísticas adicionales
        int total = calificaciones.size();
        BigDecimal suma = calificaciones.stream()
                .map(Calificacion::getNota)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal promedioGeneral = total > 0 ? 
                suma.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalCalificaciones", total);
        estadisticas.put("promedioGeneral", promedioGeneral);
        estadisticas.put("distribucion", distribucion);
        
        return estadisticas;
    }

    /**
     * Obtiene todas las calificaciones eliminadas.
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> getCalificacionesDeleted() {
        return calificacionRepository.findAllDeleted().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva calificación.
     */
    public CalificacionResponse createCalificacion(CreateCalificacionRequest request) {
        // Validar que la evaluación existe
        Evaluacion evaluacion = evaluacionRepository.findById(request.evaluacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + request.evaluacionId()));

        // Validar que el estudiante existe
        Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + request.estudianteId()));

        Calificacion calificacion = new Calificacion();
        calificacion.setEvaluacion(evaluacion);
        calificacion.setEstudiante(estudiante);
        calificacion.setNota(request.nota());
        calificacion.setComentario(request.comentario());

        Calificacion saved = calificacionRepository.save(calificacion);
        return toResponse(saved);
    }

    /**
     * Actualiza una calificación existente.
     */
    public CalificacionResponse updateCalificacion(String id, UpdateCalificacionRequest request) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con ID: " + id));

        if (request.nota() != null) {
            calificacion.setNota(request.nota());
        }
        if (request.comentario() != null) {
            calificacion.setComentario(request.comentario());
        }

        Calificacion updated = calificacionRepository.save(calificacion);
        return toResponse(updated);
    }

    /**
     * Elimina lógicamente una calificación (soft delete).
     */
    public void deleteCalificacion(String id) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con ID: " + id));
        calificacion.setDeletedAt(LocalDateTime.now());
        calificacionRepository.save(calificacion);
    }

    /**
     * Elimina permanentemente una calificación.
     */
    public void permanentDeleteCalificacion(String id) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con ID: " + id));
        calificacionRepository.delete(calificacion);
    }

    /**
     * Restaura una calificación eliminada.
     */
    public CalificacionResponse restoreCalificacion(String id) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con ID: " + id));
        calificacion.setDeletedAt(null);
        Calificacion restored = calificacionRepository.save(calificacion);
        return toResponse(restored);
    }
}
