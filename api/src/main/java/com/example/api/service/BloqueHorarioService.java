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

import com.example.api.dto.request.CreateBloqueHorarioRequest;
import com.example.api.dto.request.UpdateBloqueHorarioRequest;
import com.example.api.dto.response.BloqueHorarioResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.BloqueHorario;
import com.example.api.repository.BloqueHorarioRepository;
import com.example.api.mapper.BloqueHorarioMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de bloques de
 * horario.
 */
@Service
@Transactional
public class BloqueHorarioService {

    private final BloqueHorarioRepository bloqueHorarioRepository;
    private final BloqueHorarioMapper bloqueHorarioMapper;
    private final AuditProducer auditProducer;
    private final ObjectMapper objectMapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param bloqueHorarioRepository Repositorio de bloques de horario
     * @param bloqueHorarioMapper     Mapper de bloques de horario
     */
    public BloqueHorarioService(BloqueHorarioRepository bloqueHorarioRepository,
            BloqueHorarioMapper bloqueHorarioMapper,
            AuditProducer auditProducer,
            ObjectMapper objectMapper) {
        this.bloqueHorarioRepository = bloqueHorarioRepository;
        this.bloqueHorarioMapper = bloqueHorarioMapper;
        this.auditProducer = auditProducer;
        this.objectMapper = objectMapper;
    }

    /**
     * Obtiene todos los bloques de horario activos (paginados).
     *
     * @param pageable Configuración de paginación
     * @return Página de bloques activos
     */
    @Transactional(readOnly = true)
    public Page<BloqueHorarioResponse> getAllBloques(Pageable pageable) {
        return bloqueHorarioRepository.findAllActive(pageable)
                .map(bloqueHorarioMapper::toResponse);
    }

    /**
     * Obtiene un bloque de horario por su ID.
     *
     * @param id El ID del bloque
     * @return El bloque encontrado
     * @throws ResourceNotFoundException si el bloque no existe
     */
    @Transactional(readOnly = true)
    public BloqueHorarioResponse getBloqueById(String id) {
        BloqueHorario bloque = bloqueHorarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloque de horario no encontrado con ID: " + id));
        return bloqueHorarioMapper.toResponse(bloque);
    }

    /**
     * Obtiene todos los bloques activos ordenados por hora de inicio.
     * Útil para mostrar la secuencia completa del día.
     *
     * @return Lista de bloques ordenados por inicio
     */
    @Transactional(readOnly = true)
    public List<BloqueHorarioResponse> getBloquesOrdenados() {
        return bloqueHorarioRepository.findAllActiveOrderedByInicio()
                .stream()
                .map(bloqueHorarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los bloques eliminados.
     *
     * @return Lista de bloques eliminados
     */
    @Transactional(readOnly = true)
    public List<BloqueHorarioResponse> getBloquesDeleted() {
        return bloqueHorarioRepository.findAllDeleted()
                .stream()
                .map(bloqueHorarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo bloque de horario.
     *
     * @param request Datos del bloque a crear
     * @return El bloque creado
     * @throws IllegalArgumentException si la hora de fin es anterior o igual a la
     *                                  hora de inicio
     */
    public BloqueHorarioResponse createBloque(CreateBloqueHorarioRequest request, HttpServletRequest httpRequest) {
        // Validar que la hora de fin sea posterior a la hora de inicio
        if (request.fin().isBefore(request.inicio()) || request.fin().equals(request.inicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

        BloqueHorario bloque = bloqueHorarioMapper.toEntity(request);

        BloqueHorario savedBloque = bloqueHorarioRepository.save(bloque);

        // Audit Log
        logBloqueHorarioAction("CREATE", savedBloque, httpRequest);

        return bloqueHorarioMapper.toResponse(savedBloque);
    }

    /**
     * Actualiza un bloque de horario existente.
     *
     * @param id      El ID del bloque a actualizar
     * @param request Datos de actualización
     * @return El bloque actualizado
     * @throws ResourceNotFoundException si el bloque no existe
     * @throws IllegalArgumentException  si la hora de fin es anterior o igual a la
     *                                   hora de inicio
     */
    public BloqueHorarioResponse updateBloque(String id, UpdateBloqueHorarioRequest request,
            HttpServletRequest httpRequest) {
        BloqueHorario bloque = bloqueHorarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloque de horario no encontrado con ID: " + id));

        bloqueHorarioMapper.updateEntityFromDto(request, bloque);

        // Validar que la hora de fin sea posterior a la hora de inicio
        if (bloque.getFin().isBefore(bloque.getInicio()) || bloque.getFin().equals(bloque.getInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

        BloqueHorario updatedBloque = bloqueHorarioRepository.save(bloque);

        // Audit Log
        logBloqueHorarioAction("UPDATE", updatedBloque, httpRequest);

        return bloqueHorarioMapper.toResponse(updatedBloque);
    }

    /**
     * Elimina lógicamente un bloque de horario (soft delete).
     *
     * @param id El ID del bloque a eliminar
     * @throws ResourceNotFoundException si el bloque no existe
     */
    public void deleteBloque(String id, HttpServletRequest httpRequest) {
        BloqueHorario bloque = bloqueHorarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloque de horario no encontrado con ID: " + id));
        bloque.setDeletedAt(LocalDateTime.now());
        bloqueHorarioRepository.save(bloque);

        // Audit Log
        logBloqueHorarioAction("DELETE", bloque, httpRequest);
    }

    private void logBloqueHorarioAction(String action, BloqueHorario bloque, HttpServletRequest request) {
        try {
            AuditLogDTO log = new AuditLogDTO();
            log.setUserId("SYSTEM_ADMIN"); // Hardcoded as requested
            log.setAction(action);
            log.setEndpoint(request.getRequestURI());
            log.setIpAddress(request.getRemoteAddr());
            log.setDevice(request.getHeader("User-Agent"));
            log.setTimestamp(java.time.Instant.now());

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("bloqueId", bloque.getId());
            bodyMap.put("nombre", bloque.getNombre());
            bodyMap.put("inicio", bloque.getInicio());
            bodyMap.put("fin", bloque.getFin());

            log.setRequestBody(objectMapper.writeValueAsString(bodyMap));

            auditProducer.sendAuditLog(log);
        } catch (Exception e) {
            System.err.println("Error sending audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Elimina permanentemente un bloque de horario.
     *
     * @param id El ID del bloque a eliminar permanentemente
     * @throws ResourceNotFoundException si el bloque no existe
     */
    public void permanentDeleteBloque(String id) {
        if (!bloqueHorarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bloque de horario no encontrado con ID: " + id);
        }
        bloqueHorarioRepository.deleteById(id);
    }

    /**
     * Restaura un bloque de horario eliminado lógicamente.
     *
     * @param id El ID del bloque a restaurar
     * @return El bloque restaurado
     * @throws ResourceNotFoundException si el bloque no existe
     */
    public BloqueHorarioResponse restoreBloque(String id) {
        BloqueHorario bloque = bloqueHorarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloque de horario no encontrado con ID: " + id));
        bloque.setDeletedAt(null);
        BloqueHorario restoredBloque = bloqueHorarioRepository.save(bloque);
        return bloqueHorarioMapper.toResponse(restoredBloque);
    }
}
