package com.example.api.service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateReporteRequest;
import com.example.api.dto.request.UpdateReporteRequest;
import com.example.api.dto.response.ReporteResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Curso;
import com.example.api.model.Estudiante;
import com.example.api.model.Reporte;
import com.example.api.model.Reporte.TipoReporte;
import com.example.api.model.Usuario;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.EstudianteRepository;
import com.example.api.repository.ReporteRepository;
import com.example.api.repository.UsuarioRepository;
import com.example.api.mapper.ReporteMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de reportes.
 * Implementa Queue (FIFO) para el procesamiento de reportes en orden.
 */
@Service
@Transactional
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReporteMapper reporteMapper;

    // Cola FIFO para procesamiento de reportes
    private final Queue<Map<String, Object>> colaGeneracionReportes = new LinkedList<>();

    /**
     * Constructor con inyección de dependencias.
     */
    public ReporteService(ReporteRepository reporteRepository,
            EstudianteRepository estudianteRepository,
            CursoRepository cursoRepository,
            UsuarioRepository usuarioRepository,
            ReporteMapper reporteMapper) {
        this.reporteRepository = reporteRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioRepository = usuarioRepository;
        this.reporteMapper = reporteMapper;
    }

    /**
     * Obtiene todos los reportes activos (paginados).
     */
    @Transactional(readOnly = true)
    public Page<ReporteResponse> getAllReportes(Pageable pageable) {
        return reporteRepository.findAllActive(pageable)
                .map(reporteMapper::toResponse);
    }

    /**
     * Obtiene un reporte por su ID.
     */
    @Transactional(readOnly = true)
    public ReporteResponse getReporteById(String id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));
        return reporteMapper.toResponse(reporte);
    }

    /**
     * Busca reportes por tipo.
     */
    @Transactional(readOnly = true)
    public List<ReporteResponse> getReportesByTipo(TipoReporte tipo) {
        return reporteRepository.findByTipo(tipo).stream()
                .map(reporteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca reportes creados por un usuario específico.
     */
    @Transactional(readOnly = true)
    public List<ReporteResponse> getReportesByUsuarioId(String usuarioId) {
        return reporteRepository.findByCreadoPorId(usuarioId).stream()
                .map(reporteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca reportes de un estudiante específico.
     */
    @Transactional(readOnly = true)
    public List<ReporteResponse> getReportesByEstudianteId(String estudianteId) {
        return reporteRepository.findByEstudianteId(estudianteId).stream()
                .map(reporteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los reportes más recientes (últimos 10).
     */
    @Transactional(readOnly = true)
    public List<ReporteResponse> getReportesRecientes() {
        Pageable pageable = PageRequest.of(0, 10);
        return reporteRepository.findRecientes(pageable).stream()
                .map(reporteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Agrega una solicitud de reporte a la cola FIFO para procesamiento.
     * Implementa Queue (FIFO) para procesar reportes en orden de llegada.
     * 
     * Complejidad: O(1) para agregar a la cola
     * Uso: Procesar reportes pesados sin bloquear el sistema
     */
    public Map<String, Object> agregarAColaGeneracion(CreateReporteRequest request) {
        Map<String, Object> solicitud = Map.of(
                "request", request,
                "timestamp", LocalDateTime.now(),
                "estado", "EN_COLA");

        // Agregar a la cola FIFO (LinkedList implementa Queue)
        colaGeneracionReportes.offer(solicitud); // O(1)

        return Map.of(
                "mensaje", "Solicitud agregada a la cola de generación",
                "posicionEnCola", colaGeneracionReportes.size(),
                "timestamp", LocalDateTime.now());
    }

    /**
     * Procesa los reportes de la cola FIFO en orden.
     * Los reportes se procesan en el orden en que fueron solicitados (FIFO).
     * 
     * Complejidad: O(n) donde n es el número de reportes en la cola
     * Uso: Garantizar procesamiento ordenado de reportes
     */
    public Map<String, Object> procesarColaReportes() {
        int procesados = 0;
        List<String> idsCreados = new java.util.ArrayList<>();

        // Procesar todos los reportes en la cola
        while (!colaGeneracionReportes.isEmpty()) {
            Map<String, Object> solicitud = colaGeneracionReportes.poll(); // O(1) - FIFO

            if (solicitud != null) {
                CreateReporteRequest request = (CreateReporteRequest) solicitud.get("request");

                try {
                    // Crear el reporte
                    ReporteResponse reporte = createReporte(request);
                    idsCreados.add(reporte.id());
                    procesados++;
                } catch (Exception e) {
                    // Si falla, continuar con el siguiente
                    // En producción, aquí se manejaría el error apropiadamente
                    continue;
                }
            }
        }

        return Map.of(
                "mensaje", "Procesamiento de cola completado",
                "reportesProcesados", procesados,
                "reportesCreados", idsCreados,
                "reportesRestantes", colaGeneracionReportes.size(),
                "timestamp", LocalDateTime.now());
    }

    /**
     * Obtiene el estado actual de la cola de reportes.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getEstadoCola() {
        return Map.of(
                "reportesEnCola", colaGeneracionReportes.size(),
                "colaVacia", colaGeneracionReportes.isEmpty(),
                "timestamp", LocalDateTime.now());
    }

    /**
     * Obtiene todos los reportes eliminados.
     */
    @Transactional(readOnly = true)
    public List<ReporteResponse> getReportesDeleted() {
        return reporteRepository.findAllDeleted().stream()
                .map(reporteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo reporte.
     */
    public ReporteResponse createReporte(CreateReporteRequest request) {
        // Validar que el estudiante existe
        Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Estudiante no encontrado con ID: " + request.estudianteId()));

        Reporte reporte = new Reporte();
        reporte.setEstudiante(estudiante);
        reporte.setTipo(request.tipo());
        reporte.setPeso(request.peso());
        reporte.setTitulo(request.titulo());
        reporte.setDescripcion(request.descripcion());

        // Validar curso si se proporciona
        if (request.cursoId() != null && !request.cursoId().isBlank()) {
            Curso curso = cursoRepository.findById(request.cursoId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));
            reporte.setCurso(curso);
        }

        // Validar creador si se proporciona
        if (request.creadoPorId() != null && !request.creadoPorId().isBlank()) {
            Usuario creador = usuarioRepository.findById(request.creadoPorId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuario no encontrado con ID: " + request.creadoPorId()));
            reporte.setCreadoPor(creador);
        }

        Reporte saved = reporteRepository.save(reporte);
        return reporteMapper.toResponse(saved);
    }

    /**
     * Actualiza un reporte existente.
     */
    public ReporteResponse updateReporte(String id, UpdateReporteRequest request) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));

        if (request.cursoId() != null) {
            if (request.cursoId().isBlank()) {
                reporte.setCurso(null);
            } else {
                Curso curso = cursoRepository.findById(request.cursoId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Curso no encontrado con ID: " + request.cursoId()));
                reporte.setCurso(curso);
            }
        }

        if (request.tipo() != null) {
            reporte.setTipo(request.tipo());
        }
        if (request.peso() != null) {
            reporte.setPeso(request.peso());
        }
        if (request.titulo() != null) {
            reporte.setTitulo(request.titulo());
        }
        if (request.descripcion() != null) {
            reporte.setDescripcion(request.descripcion());
        }

        Reporte updated = reporteRepository.save(reporte);
        return reporteMapper.toResponse(updated);
    }

    /**
     * Elimina lógicamente un reporte (soft delete).
     */
    public void deleteReporte(String id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));
        reporte.setDeletedAt(LocalDateTime.now());
        reporteRepository.save(reporte);
    }

    /**
     * Elimina permanentemente un reporte.
     */
    public void permanentDeleteReporte(String id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));
        reporteRepository.delete(reporte);
    }

    /**
     * Restaura un reporte eliminado.
     */
    public ReporteResponse restoreReporte(String id) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reporte no encontrado con ID: " + id));
        reporte.setDeletedAt(null);
        Reporte restored = reporteRepository.save(reporte);
        return reporteMapper.toResponse(restored);
    }
}
