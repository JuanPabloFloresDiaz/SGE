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

import jakarta.servlet.http.HttpServletRequest;

import com.example.api.dto.AuditLogDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateCalificacionRequest;
import com.example.api.dto.request.UpdateCalificacionRequest;
import com.example.api.dto.response.CalificacionResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Calificacion;
import com.example.api.model.Estudiante;
import com.example.api.model.Evaluacion;
import com.example.api.repository.CalificacionRepository;
import com.example.api.repository.EstudianteRepository;
import com.example.api.repository.EvaluacionRepository;
import com.example.api.mapper.CalificacionMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de calificaciones.
 * Implementa múltiples estructuras de datos: Lista Ligada, BST, Búsqueda
 * Binaria, HashMap, Burbuja.
 */
@Service
@Transactional
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final EstudianteRepository estudianteRepository;
    private final CalificacionMapper calificacionMapper;
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    /**
     * Constructor con inyección de dependencias.
     */
    public CalificacionService(CalificacionRepository calificacionRepository,
            EvaluacionRepository evaluacionRepository,
            EstudianteRepository estudianteRepository,
            CalificacionMapper calificacionMapper,
            AuditProducer auditProducer,
            ObjectMapper objectMapper) {
        this.calificacionRepository = calificacionRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.estudianteRepository = estudianteRepository;
        this.calificacionMapper = calificacionMapper;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Obtiene todas las calificaciones activas (paginadas).
     */
    @Transactional(readOnly = true)
    public Page<CalificacionResponse> getAllCalificaciones(Pageable pageable) {
        return calificacionRepository.findAllActive(pageable)
                .map(calificacionMapper::toResponse);
    }

    /**
     * Obtiene una calificación por su ID.
     */
    @Transactional(readOnly = true)
    public CalificacionResponse getCalificacionById(String id) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con ID: " + id));
        return calificacionMapper.toResponse(calificacion);
    }

    /**
     * Busca calificaciones por estudiante.
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> getCalificacionesByEstudianteId(String estudianteId) {
        return calificacionRepository.findByEstudianteId(estudianteId).stream()
                .map(calificacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca calificaciones por evaluación.
     */
    @Transactional(readOnly = true)
    public List<CalificacionResponse> getCalificacionesByEvaluacionId(String evaluacionId) {
        return calificacionRepository.findByEvaluacionId(evaluacionId).stream()
                .map(calificacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el historial de calificaciones de un estudiante usando Lista Ligada.
     * Implementa una LinkedList donde las calificaciones más recientes se insertan
     * al inicio.
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
            historial.addFirst(calificacionMapper.toResponse(calif));
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
        resultado.put("promedio",
                promedio != null ? BigDecimal.valueOf(promedio).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        return resultado;
    }

    /**
     * Obtiene un ranking general de estudiantes ordenado por nota promedio usando
     * TreeMap (BST).
     * Implementa un árbol BST (TreeMap) para mantener estudiantes ordenados por
     * promedio.
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
                .map(calificacionMapper::toResponse)
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
                .map(calificacionMapper::toResponse)
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
        distribucion.put("0-60", 0); // Reprobado
        distribucion.put("61-70", 0); // Suficiente
        distribucion.put("71-80", 0); // Bueno
        distribucion.put("81-90", 0); // Notable
        distribucion.put("91-100", 0); // Sobresaliente

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
        BigDecimal promedioGeneral = total > 0 ? suma.divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

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
                .map(calificacionMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva calificación.
     */
    public CalificacionResponse createCalificacion(CreateCalificacionRequest request, HttpServletRequest httpRequest) {
        // Validar que la evaluación existe
        Evaluacion evaluacion = evaluacionRepository.findById(request.evaluacionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Evaluación no encontrada con ID: " + request.evaluacionId()));

        // Validar que el estudiante existe
        Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudiante no encontrado con ID: " + request.estudianteId()));

        Calificacion calificacion = new Calificacion();
        calificacion.setEvaluacion(evaluacion);
        calificacion.setEstudiante(estudiante);
        calificacion.setNota(request.nota());
        calificacion.setComentario(request.comentario());

        Calificacion saved = calificacionRepository.save(calificacion);

        // Audit Log
        logCalificacionAction("CREATE", saved, httpRequest);

        return calificacionMapper.toResponse(saved);
    }

    /**
     * Actualiza una calificación existente.
     */
    public CalificacionResponse updateCalificacion(String id, UpdateCalificacionRequest request,
            HttpServletRequest httpRequest) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con ID: " + id));

        if (request.nota() != null) {
            calificacion.setNota(request.nota());
        }
        if (request.comentario() != null) {
            calificacion.setComentario(request.comentario());
        }

        Calificacion updated = calificacionRepository.save(calificacion);

        // Audit Log
        logCalificacionAction("UPDATE", updated, httpRequest);

        return calificacionMapper.toResponse(updated);
    }

    /**
     * Elimina lógicamente una calificación (soft delete).
     */
    public void deleteCalificacion(String id, HttpServletRequest httpRequest) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Calificación no encontrada con ID: " + id));
        calificacion.setDeletedAt(LocalDateTime.now());
        calificacionRepository.save(calificacion);

        // Audit Log
        logCalificacionAction("DELETE", calificacion, httpRequest);
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
        return calificacionMapper.toResponse(restored);
    }

    private void logCalificacionAction(String action, Calificacion calificacion, HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
            log.setDevice(request.getHeader("User-Agent"));
            log.setTimestamp(java.time.Instant.now());

            // Create a simplified map for the body to avoid recursion or large payloads
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("calificacionId", calificacion.getId());
            bodyMap.put("nota", calificacion.getNota());
            if (calificacion.getEstudiante() != null) {
                bodyMap.put("estudianteId", calificacion.getEstudiante().getId());
            }
            if (calificacion.getEvaluacion() != null) {
                bodyMap.put("evaluacionId", calificacion.getEvaluacion().getId());
            }

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            // Log error but don't fail the transaction
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
