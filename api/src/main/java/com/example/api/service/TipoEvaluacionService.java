package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateTipoEvaluacionRequest;
import com.example.api.dto.request.UpdateTipoEvaluacionRequest;
import com.example.api.dto.response.TipoEvaluacionResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.TipoEvaluacion;
import com.example.api.repository.TipoEvaluacionRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de tipos de evaluación.
 */
@Service
@Transactional
public class TipoEvaluacionService {

    private final TipoEvaluacionRepository tipoEvaluacionRepository;

    /**
     * Constructor con inyección de dependencias.
     */
    public TipoEvaluacionService(TipoEvaluacionRepository tipoEvaluacionRepository) {
        this.tipoEvaluacionRepository = tipoEvaluacionRepository;
    }

    /**
     * Convierte una entidad TipoEvaluacion a TipoEvaluacionResponse.
     */
    private TipoEvaluacionResponse toResponse(TipoEvaluacion tipoEvaluacion) {
        return new TipoEvaluacionResponse(
                tipoEvaluacion.getId(),
                tipoEvaluacion.getNombre(),
                tipoEvaluacion.getDescripcion(),
                tipoEvaluacion.getPeso(),
                tipoEvaluacion.getCreatedAt(),
                tipoEvaluacion.getUpdatedAt(),
                tipoEvaluacion.getDeletedAt()
        );
    }

    /**
     * Obtiene todos los tipos de evaluación activos (paginados).
     */
    @Transactional(readOnly = true)
    public Page<TipoEvaluacionResponse> getAllTiposEvaluacion(Pageable pageable) {
        return tipoEvaluacionRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene un tipo de evaluación por su ID.
     */
    @Transactional(readOnly = true)
    public TipoEvaluacionResponse getTipoEvaluacionById(String id) {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + id));
        return toResponse(tipoEvaluacion);
    }

    /**
     * Busca un tipo de evaluación por nombre usando búsqueda secuencial.
     * Implementa búsqueda secuencial como estructura de datos para lista pequeña.
     */
    @Transactional(readOnly = true)
    public TipoEvaluacionResponse searchByNombre(String nombre) {
        // Obtener todos los tipos activos (lista pequeña, ~5-10 elementos)
        List<TipoEvaluacion> tiposActivos = tipoEvaluacionRepository.findAllActive();
        
        // Búsqueda secuencial (lineal)
        // Complejidad: O(n) donde n es el número de tipos (~5-10)
        // Apropiado para listas pequeñas donde no se justifica optimización
        for (TipoEvaluacion tipo : tiposActivos) {
            if (tipo.getNombre().equalsIgnoreCase(nombre)) {
                return toResponse(tipo);
            }
        }
        
        throw new ResourceNotFoundException("Tipo de evaluación no encontrado con nombre: " + nombre);
    }

    /**
     * Obtiene todos los tipos de evaluación eliminados.
     */
    @Transactional(readOnly = true)
    public List<TipoEvaluacionResponse> getTiposEvaluacionDeleted() {
        return tipoEvaluacionRepository.findAllDeleted().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo tipo de evaluación.
     */
    public TipoEvaluacionResponse createTipoEvaluacion(CreateTipoEvaluacionRequest request) {
        TipoEvaluacion tipoEvaluacion = new TipoEvaluacion();
        tipoEvaluacion.setNombre(request.nombre());
        tipoEvaluacion.setDescripcion(request.descripcion());
        tipoEvaluacion.setPeso(request.peso() != null ? request.peso() : java.math.BigDecimal.ZERO);

        TipoEvaluacion saved = tipoEvaluacionRepository.save(tipoEvaluacion);
        return toResponse(saved);
    }

    /**
     * Actualiza un tipo de evaluación existente.
     */
    public TipoEvaluacionResponse updateTipoEvaluacion(String id, UpdateTipoEvaluacionRequest request) {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + id));

        if (request.nombre() != null) {
            tipoEvaluacion.setNombre(request.nombre());
        }
        if (request.descripcion() != null) {
            tipoEvaluacion.setDescripcion(request.descripcion());
        }
        if (request.peso() != null) {
            tipoEvaluacion.setPeso(request.peso());
        }

        TipoEvaluacion updated = tipoEvaluacionRepository.save(tipoEvaluacion);
        return toResponse(updated);
    }

    /**
     * Elimina lógicamente un tipo de evaluación (soft delete).
     */
    public void deleteTipoEvaluacion(String id) {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + id));
        tipoEvaluacion.setDeletedAt(LocalDateTime.now());
        tipoEvaluacionRepository.save(tipoEvaluacion);
    }

    /**
     * Elimina permanentemente un tipo de evaluación.
     */
    public void permanentDeleteTipoEvaluacion(String id) {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + id));
        tipoEvaluacionRepository.delete(tipoEvaluacion);
    }

    /**
     * Restaura un tipo de evaluación eliminado.
     */
    public TipoEvaluacionResponse restoreTipoEvaluacion(String id) {
        TipoEvaluacion tipoEvaluacion = tipoEvaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de evaluación no encontrado con ID: " + id));
        tipoEvaluacion.setDeletedAt(null);
        TipoEvaluacion restored = tipoEvaluacionRepository.save(tipoEvaluacion);
        return toResponse(restored);
    }
}
