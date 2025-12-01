package com.example.api.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import com.example.api.dto.AuditLogDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateEstudianteRequest;
import com.example.api.dto.request.UpdateEstudianteRequest;
import com.example.api.dto.response.EstudianteResponse;
import com.example.api.exception.DuplicateResourceException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.mapper.EstudianteMapper;
import com.example.api.model.Estudiante;
import com.example.api.model.Estudiante.Genero;
import com.example.api.model.Usuario;
import com.example.api.repository.EstudianteRepository;
import com.example.api.repository.UsuarioRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de estudiantes.
 */
@Service
@Transactional
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstudianteMapper estudianteMapper;
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param estudianteRepository Repositorio de estudiantes
     * @param usuarioRepository    Repositorio de usuarios
     * @param estudianteMapper     Mapper de estudiantes
     */
    public EstudianteService(EstudianteRepository estudianteRepository,
            UsuarioRepository usuarioRepository,
            EstudianteMapper estudianteMapper,
            AuditProducer auditProducer,
            ObjectMapper objectMapper) {
        this.estudianteRepository = estudianteRepository;
        this.usuarioRepository = usuarioRepository;
        this.estudianteMapper = estudianteMapper;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Obtiene todos los estudiantes activos (paginados).
     *
     * @param pageable Configuración de paginación
     * @return Página de estudiantes activos
     */
    @Transactional(readOnly = true)
    public Page<EstudianteResponse> getAllEstudiantes(Pageable pageable) {
        return estudianteRepository.findAllActive(pageable)
                .map(estudianteMapper::toResponse);
    }

    /**
     * Obtiene un estudiante por su ID.
     *
     * @param id El ID del estudiante
     * @return El estudiante encontrado
     * @throws ResourceNotFoundException si el estudiante no existe
     */
    @Transactional(readOnly = true)
    public EstudianteResponse getEstudianteById(String id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        return estudianteMapper.toResponse(estudiante);
    }

    /**
     * Busca un estudiante por su código único.
     *
     * @param codigo El código del estudiante
     * @return El estudiante encontrado
     * @throws ResourceNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public EstudianteResponse getEstudianteByCodigo(String codigo) {
        Estudiante estudiante = estudianteRepository.findByCodigoEstudiante(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con código: " + codigo));

        return estudianteMapper.toResponse(estudiante);
    }

    /**
     * Obtiene estudiantes filtrados por género.
     *
     * @param genero El género a filtrar
     * @return Lista de estudiantes del género especificado
     */
    @Transactional(readOnly = true)
    public List<EstudianteResponse> getEstudiantesByGenero(Genero genero) {
        return estudianteRepository.findByGenero(genero).stream()
                .map(estudianteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo estudiantes activos.
     *
     * @return Lista de estudiantes activos
     */
    @Transactional(readOnly = true)
    public List<EstudianteResponse> getEstudiantesActivos() {
        return estudianteRepository.findByActivoTrue().stream()
                .map(estudianteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo estudiante.
     *
     * @param request Datos del estudiante a crear
     * @return El estudiante creado
     * @throws ResourceNotFoundException  si el usuario no existe
     * @throws DuplicateResourceException si el código ya existe
     */
    public EstudianteResponse createEstudiante(CreateEstudianteRequest request, HttpServletRequest httpRequest) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.usuarioId()));

        // Verificar que el código no exista
        if (estudianteRepository.findByCodigoEstudiante(request.codigoEstudiante()).isPresent()) {
            throw new DuplicateResourceException(
                    "Ya existe un estudiante con el código: " + request.codigoEstudiante());
        }

        // Crear estudiante
        Estudiante estudiante = estudianteMapper.toEntity(request);
        estudiante.setUsuario(usuario);

        Estudiante guardado = estudianteRepository.save(estudiante);

        // Audit Log
        logEstudianteAction("CREATE", guardado, httpRequest);

        return estudianteMapper.toResponse(guardado);
    }

    /**
     * Actualiza un estudiante existente.
     *
     * @param id      ID del estudiante a actualizar
     * @param request Datos a actualizar
     * @return El estudiante actualizado
     * @throws ResourceNotFoundException  si el estudiante no existe
     * @throws DuplicateResourceException si el nuevo código ya existe
     */
    public EstudianteResponse updateEstudiante(String id, UpdateEstudianteRequest request,
            HttpServletRequest httpRequest) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        // Actualizar código si cambió y verificar unicidad
        if (request.codigoEstudiante() != null
                && !request.codigoEstudiante().equals(estudiante.getCodigoEstudiante())) {
            if (estudianteRepository.findByCodigoEstudiante(request.codigoEstudiante()).isPresent()) {
                throw new DuplicateResourceException(
                        "Ya existe otro estudiante con el código: " + request.codigoEstudiante());
            }
            estudiante.setCodigoEstudiante(request.codigoEstudiante());
        }

        // Actualizar campos opcionales
        estudianteMapper.updateEntityFromDto(request, estudiante);

        Estudiante actualizado = estudianteRepository.save(estudiante);

        // Audit Log
        logEstudianteAction("UPDATE", actualizado, httpRequest);

        return estudianteMapper.toResponse(actualizado);
    }

    /**
     * Elimina un estudiante (soft delete).
     *
     * @param id ID del estudiante a eliminar
     * @throws ResourceNotFoundException si el estudiante no existe
     */
    public void deleteEstudiante(String id, HttpServletRequest httpRequest) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        estudiante.setActivo(false);
        estudianteRepository.save(estudiante);

        // Audit Log
        logEstudianteAction("DELETE", estudiante, httpRequest);
    }

    /**
     * Elimina permanentemente un estudiante de la base de datos.
     *
     * @param id ID del estudiante a eliminar
     * @throws ResourceNotFoundException si el estudiante no existe
     */
    public void permanentDeleteEstudiante(String id) {
        if (!estudianteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Estudiante no encontrado con ID: " + id);
        }

        estudianteRepository.deleteById(id);
    }

    private void logEstudianteAction(String action, Estudiante estudiante, HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
            log.setDevice(request.getHeader("User-Agent"));
            log.setTimestamp(java.time.Instant.now());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("estudianteId", estudiante.getId());
            bodyMap.put("codigoEstudiante", estudiante.getCodigoEstudiante());
            if (estudiante.getUsuario() != null) {
                bodyMap.put("usuarioId", estudiante.getUsuario().getId());
            }

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
