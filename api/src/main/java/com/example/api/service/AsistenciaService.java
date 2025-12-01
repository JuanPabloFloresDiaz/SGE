package com.example.api.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import com.example.api.dto.AuditLogDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateAsistenciaRequest;
import com.example.api.dto.request.UpdateAsistenciaRequest;
import com.example.api.dto.response.AsistenciaResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Asistencia;
import com.example.api.model.Asistencia.EstadoAsistencia;
import com.example.api.model.Clase;
import com.example.api.model.Estudiante;
import com.example.api.model.Usuario;
import com.example.api.repository.AsistenciaRepository;
import com.example.api.repository.ClaseRepository;
import com.example.api.repository.EstudianteRepository;
import com.example.api.repository.UsuarioRepository;
import com.example.api.mapper.AsistenciaMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de asistencias.
 */
@Service
@Transactional
public class AsistenciaService {

        private final AsistenciaRepository asistenciaRepository;
        private final ClaseRepository claseRepository;
        private final EstudianteRepository estudianteRepository;
        private final UsuarioRepository usuarioRepository;
        private final AsistenciaMapper asistenciaMapper;
        private final AuditProducer auditProducer;
        private final ObjectMapper objectMapper;

        /**
         * Constructor con inyección de dependencias.
         */
        public AsistenciaService(AsistenciaRepository asistenciaRepository,
                        ClaseRepository claseRepository,
                        EstudianteRepository estudianteRepository,
                        UsuarioRepository usuarioRepository,
                        AsistenciaMapper asistenciaMapper,
                        AuditProducer auditProducer,
                        ObjectMapper objectMapper) {
                this.asistenciaRepository = asistenciaRepository;
                this.claseRepository = claseRepository;
                this.estudianteRepository = estudianteRepository;
                this.usuarioRepository = usuarioRepository;
                this.asistenciaMapper = asistenciaMapper;
                this.auditProducer = auditProducer;
                this.objectMapper = objectMapper;
        }

        /**
         * Obtiene todas las asistencias activas (paginadas).
         */
        @Transactional(readOnly = true)
        public Page<AsistenciaResponse> getAllAsistencias(Pageable pageable) {
                return asistenciaRepository.findAllActive(pageable)
                                .map(asistenciaMapper::toResponse);
        }

        /**
         * Obtiene una asistencia por su ID.
         */
        @Transactional(readOnly = true)
        public AsistenciaResponse getAsistenciaById(String id) {
                Asistencia asistencia = asistenciaRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Asistencia no encontrada con ID: " + id));
                return asistenciaMapper.toResponse(asistencia);
        }

        /**
         * Busca asistencias por clase.
         */
        @Transactional(readOnly = true)
        public List<AsistenciaResponse> getAsistenciasByClaseId(String claseId) {
                return asistenciaRepository.findByClaseId(claseId).stream()
                                .map(asistenciaMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Busca asistencias por estudiante.
         */
        @Transactional(readOnly = true)
        public List<AsistenciaResponse> getAsistenciasByEstudianteId(String estudianteId) {
                return asistenciaRepository.findByEstudianteId(estudianteId).stream()
                                .map(asistenciaMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Obtiene estadísticas de asistencia de un estudiante usando HashMap.
         * Implementa el uso de Tabla Hash como estructura de datos.
         */
        @Transactional(readOnly = true)
        public Map<String, Object> getEstadisticasEstudiante(String estudianteId) {
                List<Asistencia> asistencias = asistenciaRepository.findByEstudianteId(estudianteId);

                // Usar HashMap para contar frecuencia de estados
                HashMap<EstadoAsistencia, Integer> conteoEstados = new HashMap<>();

                // Inicializar contadores
                for (EstadoAsistencia estado : EstadoAsistencia.values()) {
                        conteoEstados.put(estado, 0);
                }

                // Contar cada estado
                for (Asistencia asistencia : asistencias) {
                        EstadoAsistencia estado = asistencia.getEstado();
                        conteoEstados.put(estado, conteoEstados.get(estado) + 1);
                }

                int totalClases = asistencias.size();
                double porcentajeAsistencia = totalClases > 0
                                ? (conteoEstados.get(EstadoAsistencia.presente)
                                                + conteoEstados.get(EstadoAsistencia.tarde)) * 100.0 / totalClases
                                : 0.0;

                // Construir respuesta
                Map<String, Object> estadisticas = new HashMap<>();
                estadisticas.put("estudianteId", estudianteId);
                estadisticas.put("totalClases", totalClases);
                estadisticas.put("presente", conteoEstados.get(EstadoAsistencia.presente));
                estadisticas.put("ausente", conteoEstados.get(EstadoAsistencia.ausente));
                estadisticas.put("tarde", conteoEstados.get(EstadoAsistencia.tarde));
                estadisticas.put("justificado", conteoEstados.get(EstadoAsistencia.justificado));
                estadisticas.put("porcentajeAsistencia", Math.round(porcentajeAsistencia * 100.0) / 100.0);

                return estadisticas;
        }

        /**
         * Obtiene todas las asistencias eliminadas.
         */
        @Transactional(readOnly = true)
        public List<AsistenciaResponse> getAsistenciasDeleted() {
                return asistenciaRepository.findAllDeleted().stream()
                                .map(asistenciaMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Crea una nueva asistencia.
         */
        public AsistenciaResponse createAsistencia(CreateAsistenciaRequest request, HttpServletRequest httpRequest) {
                // Validar que la clase existe
                Clase clase = claseRepository.findById(request.claseId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Clase no encontrada con ID: " + request.claseId()));

                // Validar que el estudiante existe
                Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Estudiante no encontrado con ID: " + request.estudianteId()));

                // Validar usuario que registra si se proporciona
                Usuario registradoPor = null;
                if (request.registradoPorId() != null && !request.registradoPorId().isBlank()) {
                        registradoPor = usuarioRepository.findById(request.registradoPorId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Usuario no encontrado con ID: " + request.registradoPorId()));
                }

                Asistencia asistencia = new Asistencia();
                asistencia.setClase(clase);
                asistencia.setEstudiante(estudiante);
                asistencia.setEstado(request.estado());
                asistencia.setObservacion(request.observacion());
                asistencia.setRegistradoPor(registradoPor);
                asistencia.setRegistradoAt(
                                request.registradoAt() != null ? request.registradoAt() : LocalDateTime.now());

                Asistencia savedAsistencia = asistenciaRepository.save(asistencia);

                // Audit Log
                logAsistenciaAction("CREATE", savedAsistencia, httpRequest);

                return asistenciaMapper.toResponse(savedAsistencia);
        }

        /**
         * Actualiza una asistencia existente.
         */
        public AsistenciaResponse updateAsistencia(String id, UpdateAsistenciaRequest request,
                        HttpServletRequest httpRequest) {
                Asistencia asistencia = asistenciaRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Asistencia no encontrada con ID: " + id));

                if (request.estado() != null) {
                        asistencia.setEstado(request.estado());
                }
                if (request.observacion() != null) {
                        asistencia.setObservacion(request.observacion());
                }
                if (request.registradoPorId() != null) {
                        if (request.registradoPorId().isBlank()) {
                                asistencia.setRegistradoPor(null);
                        } else {
                                Usuario registradoPor = usuarioRepository.findById(request.registradoPorId())
                                                .orElseThrow(() -> new ResourceNotFoundException(
                                                                "Usuario no encontrado con ID: "
                                                                                + request.registradoPorId()));
                                asistencia.setRegistradoPor(registradoPor);
                        }
                }
                if (request.registradoAt() != null) {
                        asistencia.setRegistradoAt(request.registradoAt());
                }

                Asistencia updatedAsistencia = asistenciaRepository.save(asistencia);

                // Audit Log
                logAsistenciaAction("UPDATE", updatedAsistencia, httpRequest);

                return asistenciaMapper.toResponse(updatedAsistencia);
        }

        /**
         * Elimina lógicamente una asistencia (soft delete).
         */
        public void deleteAsistencia(String id, HttpServletRequest httpRequest) {
                Asistencia asistencia = asistenciaRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Asistencia no encontrada con ID: " + id));
                asistencia.setDeletedAt(LocalDateTime.now());
                asistenciaRepository.save(asistencia);

                // Audit Log
                logAsistenciaAction("DELETE", asistencia, httpRequest);
        }

        private void logAsistenciaAction(String action, Asistencia asistencia, HttpServletRequest request) {
                try {
                        AuditLogDTO log = new AuditLogDTO();
                        log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
                        log.setAction(action);
                        log.setEndpoint(request.getRequestURI());
                        log.setIpAddress(request.getRemoteAddr());
                        log.setDevice(request.getHeader("User-Agent"));
                        log.setTimestamp(java.time.Instant.now());

                        Map<String, Object> bodyMap = new HashMap<>();
                        bodyMap.put("asistenciaId", asistencia.getId());
                        bodyMap.put("estado", asistencia.getEstado());
                        if (asistencia.getClase() != null) {
                                bodyMap.put("claseId", asistencia.getClase().getId());
                        }
                        if (asistencia.getEstudiante() != null) {
                                bodyMap.put("estudianteId", asistencia.getEstudiante().getId());
                        }

                        log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

                        auditProducer.sendAuditLog(log);
                } catch (Exception e) {
                        System.err.println("Error sending audit log: " + e.getMessage());
                        e.printStackTrace();
                }
        }

        /**
         * Elimina permanentemente una asistencia.
         */
        public void permanentDeleteAsistencia(String id) {
                if (!asistenciaRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Asistencia no encontrada con ID: " + id);
                }
                asistenciaRepository.deleteById(id);
        }

        /**
         * Restaura una asistencia eliminada.
         */
        public AsistenciaResponse restoreAsistencia(String id) {
                Asistencia asistencia = asistenciaRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Asistencia no encontrada con ID: " + id));
                asistencia.setDeletedAt(null);
                Asistencia restoredAsistencia = asistenciaRepository.save(asistencia);
                return asistenciaMapper.toResponse(restoredAsistencia);
        }
}
