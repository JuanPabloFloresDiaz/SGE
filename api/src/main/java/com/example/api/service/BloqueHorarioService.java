package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Constructor con inyección de dependencias.
     *
     * @param bloqueHorarioRepository Repositorio de bloques de horario
     * @param bloqueHorarioMapper     Mapper de bloques de horario
     */
    public BloqueHorarioService(BloqueHorarioRepository bloqueHorarioRepository,
            BloqueHorarioMapper bloqueHorarioMapper) {
        this.bloqueHorarioRepository = bloqueHorarioRepository;
        this.bloqueHorarioMapper = bloqueHorarioMapper;
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
    public BloqueHorarioResponse createBloque(CreateBloqueHorarioRequest request) {
        // Validar que la hora de fin sea posterior a la hora de inicio
        if (request.fin().isBefore(request.inicio()) || request.fin().equals(request.inicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

        BloqueHorario bloque = bloqueHorarioMapper.toEntity(request);

        BloqueHorario savedBloque = bloqueHorarioRepository.save(bloque);
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
    public BloqueHorarioResponse updateBloque(String id, UpdateBloqueHorarioRequest request) {
        BloqueHorario bloque = bloqueHorarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloque de horario no encontrado con ID: " + id));

        bloqueHorarioMapper.updateEntityFromDto(request, bloque);

        // Validar que la hora de fin sea posterior a la hora de inicio
        if (bloque.getFin().isBefore(bloque.getInicio()) || bloque.getFin().equals(bloque.getInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }

        BloqueHorario updatedBloque = bloqueHorarioRepository.save(bloque);
        return bloqueHorarioMapper.toResponse(updatedBloque);
    }

    /**
     * Elimina lógicamente un bloque de horario (soft delete).
     *
     * @param id El ID del bloque a eliminar
     * @throws ResourceNotFoundException si el bloque no existe
     */
    public void deleteBloque(String id) {
        BloqueHorario bloque = bloqueHorarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloque de horario no encontrado con ID: " + id));
        bloque.setDeletedAt(LocalDateTime.now());
        bloqueHorarioRepository.save(bloque);
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
