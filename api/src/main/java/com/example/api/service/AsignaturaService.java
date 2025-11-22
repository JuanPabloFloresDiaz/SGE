package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateAsignaturaRequest;
import com.example.api.dto.request.UpdateAsignaturaRequest;
import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.mapper.AsignaturaMapper;
import com.example.api.model.Asignatura;
import com.example.api.repository.AsignaturaRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de asignaturas.
 */
@Service
@Transactional
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;
    private final AsignaturaMapper asignaturaMapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param asignaturaRepository Repositorio de asignaturas
     * @param asignaturaMapper     Mapper de asignaturas
     */
    public AsignaturaService(AsignaturaRepository asignaturaRepository, AsignaturaMapper asignaturaMapper) {
        this.asignaturaRepository = asignaturaRepository;
        this.asignaturaMapper = asignaturaMapper;
    }

    /**
     * Obtiene todas las asignaturas activas (paginadas).
     *
     * @param pageable Configuración de paginación
     * @return Página de asignaturas activas
     */
    @Transactional(readOnly = true)
    public Page<AsignaturaResponse> getAllAsignaturas(Pageable pageable) {
        return asignaturaRepository.findAllActive(pageable)
                .map(asignaturaMapper::toResponse);
    }

    /**
     * Obtiene una asignatura por su ID.
     *
     * @param id El ID de la asignatura
     * @return La asignatura encontrada
     * @throws ResourceNotFoundException si la asignatura no existe
     */
    @Transactional(readOnly = true)
    public AsignaturaResponse getAsignaturaById(String id) {
        Asignatura asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada con ID: " + id));
        return asignaturaMapper.toResponse(asignatura);
    }

    /**
     * Busca una asignatura por su código único.
     *
     * @param codigo Código de la asignatura
     * @return La asignatura encontrada
     * @throws ResourceNotFoundException si la asignatura no existe
     */
    @Transactional(readOnly = true)
    public AsignaturaResponse getAsignaturaByCodigo(String codigo) {
        Asignatura asignatura = asignaturaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada con código: " + codigo));
        return asignaturaMapper.toResponse(asignatura);
    }

    /**
     * Busca asignaturas por nombre (búsqueda parcial).
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de asignaturas que coinciden
     */
    @Transactional(readOnly = true)
    public List<AsignaturaResponse> searchByNombre(String nombre) {
        return asignaturaRepository.searchByNombre(nombre)
                .stream()
                .map(asignaturaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene asignaturas eliminadas.
     *
     * @return Lista de asignaturas eliminadas
     */
    @Transactional(readOnly = true)
    public List<AsignaturaResponse> getAsignaturasDeleted() {
        return asignaturaRepository.findAllDeleted()
                .stream()
                .map(asignaturaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva asignatura.
     *
     * @param request Datos de la asignatura a crear
     * @return La asignatura creada
     * @throws IllegalArgumentException si el código ya existe
     */
    public AsignaturaResponse createAsignatura(CreateAsignaturaRequest request) {
        // Verificar que el código no exista
        if (asignaturaRepository.existsByCodigo(request.codigo())) {
            throw new IllegalArgumentException("Ya existe una asignatura con el código: " + request.codigo());
        }

        // Crear nueva asignatura
        Asignatura asignatura = asignaturaMapper.toEntity(request);

        // Guardar
        Asignatura asignaturaGuardada = asignaturaRepository.save(asignatura);
        return asignaturaMapper.toResponse(asignaturaGuardada);
    }

    /**
     * Actualiza una asignatura existente.
     *
     * @param id      El ID de la asignatura a actualizar
     * @param request Datos a actualizar
     * @return La asignatura actualizada
     * @throws ResourceNotFoundException si la asignatura no existe
     * @throws IllegalArgumentException  si el código ya existe
     */
    public AsignaturaResponse updateAsignatura(String id, UpdateAsignaturaRequest request) {
        Asignatura asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada con ID: " + id));

        // Actualizar solo los campos proporcionados
        if (request.codigo() != null) {
            // Verificar si el código ya existe (excluyendo la asignatura actual)
            if (asignaturaRepository.existsByCodigoAndIdNot(request.codigo(), id)) {
                throw new IllegalArgumentException("Ya existe una asignatura con el código: " + request.codigo());
            }
        }
        
        asignaturaMapper.updateEntityFromDto(request, asignatura);

        Asignatura asignaturaActualizada = asignaturaRepository.save(asignatura);
        return asignaturaMapper.toResponse(asignaturaActualizada);
    }

    /**
     * Realiza eliminación suave de una asignatura.
     *
     * @param id El ID de la asignatura a eliminar
     * @throws ResourceNotFoundException si la asignatura no existe
     */
    public void deleteAsignatura(String id) {
        Asignatura asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada con ID: " + id));

        asignatura.setDeletedAt(LocalDateTime.now());
        asignaturaRepository.save(asignatura);
    }

    /**
     * Elimina permanentemente una asignatura de la base de datos.
     *
     * @param id El ID de la asignatura a eliminar
     * @throws ResourceNotFoundException si la asignatura no existe
     */
    public void permanentDeleteAsignatura(String id) {
        if (!asignaturaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Asignatura no encontrada con ID: " + id);
        }
        asignaturaRepository.deleteById(id);
    }

    /**
     * Restaura una asignatura eliminada.
     *
     * @param id El ID de la asignatura a restaurar
     * @return La asignatura restaurada
     * @throws ResourceNotFoundException si la asignatura no existe
     */
    public AsignaturaResponse restoreAsignatura(String id) {
        Asignatura asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura no encontrada con ID: " + id));

        asignatura.setDeletedAt(null);
        Asignatura asignaturaRestaurada = asignaturaRepository.save(asignatura);
        return asignaturaMapper.toResponse(asignaturaRestaurada);
    }
}
