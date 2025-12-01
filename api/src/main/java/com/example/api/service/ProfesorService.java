package com.example.api.service;

import java.time.LocalDateTime;
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

import com.example.api.dto.request.CreateProfesorRequest;
import com.example.api.dto.request.UpdateProfesorRequest;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.mapper.ProfesorMapper;
import com.example.api.model.Profesor;

import com.example.api.model.Usuario;
import com.example.api.repository.ProfesorRepository;
import com.example.api.repository.UsuarioRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de profesores.
 */
@Service
@Transactional
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfesorMapper profesorMapper;
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param profesorRepository Repositorio de profesores
     * @param usuarioRepository  Repositorio de usuarios
     * @param profesorMapper     Mapper de profesores
     */
    public ProfesorService(ProfesorRepository profesorRepository,
            UsuarioRepository usuarioRepository,
            ProfesorMapper profesorMapper,
            AuditProducer auditProducer,
            ObjectMapper objectMapper) {
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
        this.profesorMapper = profesorMapper;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Obtiene todos los profesores activos (paginados).
     *
     * @param pageable Configuración de paginación
     * @return Página de profesores activos
     */
    @Transactional(readOnly = true)
    public Page<ProfesorResponse> getAllProfesores(Pageable pageable) {
        return profesorRepository.findAllActive(pageable)
                .map(profesorMapper::toResponse);
    }

    /**
     * Obtiene un profesor por su ID.
     *
     * @param id El ID del profesor
     * @return El profesor encontrado
     * @throws ResourceNotFoundException si el profesor no existe
     */
    @Transactional(readOnly = true)
    public ProfesorResponse getProfesorById(String id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));
        return profesorMapper.toResponse(profesor);
    }

    /**
     * Busca profesores por especialidad (búsqueda parcial).
     *
     * @param especialidad Texto a buscar en la especialidad
     * @return Lista de profesores que coinciden
     */
    @Transactional(readOnly = true)
    public List<ProfesorResponse> searchByEspecialidad(String especialidad) {
        return profesorRepository.searchByEspecialidad(especialidad)
                .stream()
                .map(profesorMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo profesores activos.
     *
     * @return Lista de profesores activos
     */
    @Transactional(readOnly = true)
    public List<ProfesorResponse> getProfesoresActivos() {
        return profesorRepository.findByActivoTrue()
                .stream()
                .map(profesorMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene profesores eliminados.
     *
     * @return Lista de profesores eliminados
     */
    @Transactional(readOnly = true)
    public List<ProfesorResponse> getProfesoresDeleted() {
        return profesorRepository.findAllDeleted()
                .stream()
                .map(profesorMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo profesor.
     *
     * @param request Datos del profesor a crear
     * @return El profesor creado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public ProfesorResponse createProfesor(CreateProfesorRequest request, HttpServletRequest httpRequest) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con ID: " + request.usuarioId()));

        // Crear nuevo profesor
        Profesor profesor = profesorMapper.toEntity(request);
        profesor.setUsuario(usuario);

        // Guardar
        Profesor profesorGuardado = profesorRepository.save(profesor);

        // Audit Log
        logProfesorAction("CREATE", profesorGuardado, httpRequest);

        return profesorMapper.toResponse(profesorGuardado);
    }

    /**
     * Actualiza un profesor existente.
     *
     * @param id      El ID del profesor a actualizar
     * @param request Datos a actualizar
     * @return El profesor actualizado
     * @throws ResourceNotFoundException si el profesor no existe
     */
    public ProfesorResponse updateProfesor(String id, UpdateProfesorRequest request, HttpServletRequest httpRequest) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));

        // Actualizar solo los campos proporcionados
        profesorMapper.updateEntityFromDto(request, profesor);

        Profesor profesorActualizado = profesorRepository.save(profesor);

        // Audit Log
        logProfesorAction("UPDATE", profesorActualizado, httpRequest);

        return profesorMapper.toResponse(profesorActualizado);
    }

    /**
     * Realiza eliminación suave de un profesor.
     *
     * @param id El ID del profesor a eliminar
     * @throws ResourceNotFoundException si el profesor no existe
     */
    public void deleteProfesor(String id, HttpServletRequest httpRequest) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));

        profesor.setDeletedAt(LocalDateTime.now());
        profesor.setActivo(false);
        profesorRepository.save(profesor);

        // Audit Log
        logProfesorAction("DELETE", profesor, httpRequest);
    }

    /**
     * Elimina permanentemente un profesor de la base de datos.
     *
     * @param id El ID del profesor a eliminar
     * @throws ResourceNotFoundException si el profesor no existe
     */
    public void permanentDeleteProfesor(String id) {
        if (!profesorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Profesor no encontrado con ID: " + id);
        }
        profesorRepository.deleteById(id);
    }

    /**
     * Restaura un profesor eliminado.
     *
     * @param id El ID del profesor a restaurar
     * @return El profesor restaurado
     * @throws ResourceNotFoundException si el profesor no existe
     */
    public ProfesorResponse restoreProfesor(String id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));

        profesor.setDeletedAt(null);
        profesor.setActivo(true);
        Profesor profesorRestaurado = profesorRepository.save(profesor);
        return profesorMapper.toResponse(profesorRestaurado);
    }

    private void logProfesorAction(String action, Profesor profesor, HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
            log.setDevice(request.getHeader("User-Agent"));
            log.setTimestamp(java.time.Instant.now());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("profesorId", profesor.getId());
            bodyMap.put("especialidad", profesor.getEspecialidad());
            if (profesor.getUsuario() != null) {
                bodyMap.put("usuarioId", profesor.getUsuario().getId());
            }

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
