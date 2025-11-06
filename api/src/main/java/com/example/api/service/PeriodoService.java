package com.example.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreatePeriodoRequest;
import com.example.api.dto.request.UpdatePeriodoRequest;
import com.example.api.dto.response.PeriodoResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Periodo;
import com.example.api.repository.PeriodoRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de periodos académicos.
 */
@Service
@Transactional
public class PeriodoService {

    private final PeriodoRepository periodoRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param periodoRepository Repositorio de periodos
     */
    public PeriodoService(PeriodoRepository periodoRepository) {
        this.periodoRepository = periodoRepository;
    }

    /**
     * Convierte una entidad Periodo a PeriodoResponse.
     *
     * @param periodo La entidad Periodo
     * @return El DTO PeriodoResponse
     */
    private PeriodoResponse toResponse(Periodo periodo) {
        return new PeriodoResponse(
                periodo.getId(),
                periodo.getNombre(),
                periodo.getFechaInicio(),
                periodo.getFechaFin(),
                periodo.getActivo(),
                periodo.getCreatedAt(),
                periodo.getUpdatedAt(),
                periodo.getDeletedAt()
        );
    }

    /**
     * Obtiene todos los periodos activos (paginados).
     *
     * @param pageable Configuración de paginación
     * @return Página de periodos activos
     */
    @Transactional(readOnly = true)
    public Page<PeriodoResponse> getAllPeriodos(Pageable pageable) {
        return periodoRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene un periodo por su ID.
     *
     * @param id El ID del periodo
     * @return El periodo encontrado
     * @throws ResourceNotFoundException si el periodo no existe
     */
    @Transactional(readOnly = true)
    public PeriodoResponse getPeriodoById(String id) {
        Periodo periodo = periodoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Periodo no encontrado con ID: " + id));
        return toResponse(periodo);
    }

    /**
     * Busca periodos por nombre (búsqueda parcial).
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de periodos que coinciden
     */
    @Transactional(readOnly = true)
    public List<PeriodoResponse> searchByNombre(String nombre) {
        return periodoRepository.searchByNombre(nombre)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo periodos activos.
     *
     * @return Lista de periodos activos
     */
    @Transactional(readOnly = true)
    public List<PeriodoResponse> getPeriodosActivos() {
        return periodoRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene periodos eliminados.
     *
     * @return Lista de periodos eliminados
     */
    @Transactional(readOnly = true)
    public List<PeriodoResponse> getPeriodosDeleted() {
        return periodoRepository.findAllDeleted()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el periodo actual basado en la fecha actual del sistema.
     *
     * @return El periodo actual si existe
     * @throws ResourceNotFoundException si no existe un periodo actual
     */
    @Transactional(readOnly = true)
    public PeriodoResponse getPeriodoActual() {
        LocalDate hoy = LocalDate.now();
        Periodo periodo = periodoRepository.findPeriodoActual(hoy)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un periodo actual activo"));
        return toResponse(periodo);
    }

    /**
     * Crea un nuevo periodo.
     *
     * @param request Datos del periodo a crear
     * @return El periodo creado
     */
    public PeriodoResponse createPeriodo(CreatePeriodoRequest request) {
        // Validar que fechaFin sea posterior a fechaInicio
        if (request.fechaFin().isBefore(request.fechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // Crear nuevo periodo
        Periodo periodo = new Periodo();
        periodo.setNombre(request.nombre());
        periodo.setFechaInicio(request.fechaInicio());
        periodo.setFechaFin(request.fechaFin());
        periodo.setActivo(request.activo() != null ? request.activo() : true);

        // Guardar
        Periodo periodoGuardado = periodoRepository.save(periodo);
        return toResponse(periodoGuardado);
    }

    /**
     * Actualiza un periodo existente.
     *
     * @param id El ID del periodo a actualizar
     * @param request Datos a actualizar
     * @return El periodo actualizado
     * @throws ResourceNotFoundException si el periodo no existe
     */
    public PeriodoResponse updatePeriodo(String id, UpdatePeriodoRequest request) {
        Periodo periodo = periodoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Periodo no encontrado con ID: " + id));

        // Actualizar solo los campos proporcionados
        if (request.nombre() != null) {
            // Verificar si el nombre ya existe (excluyendo el periodo actual)
            if (periodoRepository.existsByNombreAndIdNot(request.nombre(), id)) {
                throw new IllegalArgumentException("Ya existe un periodo con el nombre: " + request.nombre());
            }
            periodo.setNombre(request.nombre());
        }
        if (request.fechaInicio() != null) {
            periodo.setFechaInicio(request.fechaInicio());
        }
        if (request.fechaFin() != null) {
            periodo.setFechaFin(request.fechaFin());
        }
        if (request.activo() != null) {
            periodo.setActivo(request.activo());
        }

        // Validar que fechaFin sea posterior a fechaInicio (si ambas están definidas)
        if (periodo.getFechaInicio() != null && periodo.getFechaFin() != null) {
            if (periodo.getFechaFin().isBefore(periodo.getFechaInicio())) {
                throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
            }
        }

        Periodo periodoActualizado = periodoRepository.save(periodo);
        return toResponse(periodoActualizado);
    }

    /**
     * Realiza eliminación suave de un periodo.
     *
     * @param id El ID del periodo a eliminar
     * @throws ResourceNotFoundException si el periodo no existe
     */
    public void deletePeriodo(String id) {
        Periodo periodo = periodoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Periodo no encontrado con ID: " + id));

        periodo.setDeletedAt(LocalDateTime.now());
        periodo.setActivo(false);
        periodoRepository.save(periodo);
    }

    /**
     * Elimina permanentemente un periodo de la base de datos.
     *
     * @param id El ID del periodo a eliminar
     * @throws ResourceNotFoundException si el periodo no existe
     */
    public void permanentDeletePeriodo(String id) {
        if (!periodoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Periodo no encontrado con ID: " + id);
        }
        periodoRepository.deleteById(id);
    }

    /**
     * Restaura un periodo eliminado.
     *
     * @param id El ID del periodo a restaurar
     * @return El periodo restaurado
     * @throws ResourceNotFoundException si el periodo no existe
     */
    public PeriodoResponse restorePeriodo(String id) {
        Periodo periodo = periodoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Periodo no encontrado con ID: " + id));

        periodo.setDeletedAt(null);
        periodo.setActivo(true);
        Periodo periodoRestaurado = periodoRepository.save(periodo);
        return toResponse(periodoRestaurado);
    }
}
