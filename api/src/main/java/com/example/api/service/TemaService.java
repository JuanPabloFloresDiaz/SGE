package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateTemaRequest;
import com.example.api.dto.request.UpdateTemaRequest;
import com.example.api.dto.response.TemaResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Tema;
import com.example.api.model.Unidad;
import com.example.api.repository.TemaRepository;
import com.example.api.repository.UnidadRepository;
import com.example.api.mapper.TemaMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de temas.
 */
@Service
@Transactional
public class TemaService {

    private final TemaRepository temaRepository;
    private final UnidadRepository unidadRepository;
    private final TemaMapper temaMapper;

    /**
     * Constructor con inyección de dependencias.
     */
    public TemaService(TemaRepository temaRepository,
            UnidadRepository unidadRepository,
            TemaMapper temaMapper) {
        this.temaRepository = temaRepository;
        this.unidadRepository = unidadRepository;
        this.temaMapper = temaMapper;
    }

    /**
     * Obtiene todos los temas activos (paginados).
     */
    @Transactional(readOnly = true)
    public Page<TemaResponse> getAllTemas(Pageable pageable) {
        return temaRepository.findAllActive(pageable)
                .map(temaMapper::toResponse);
    }

    /**
     * Obtiene un tema por su ID.
     */
    @Transactional(readOnly = true)
    public TemaResponse getTemaById(String id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));
        return temaMapper.toResponse(tema);
    }

    /**
     * Busca temas por unidad.
     */
    @Transactional(readOnly = true)
    public List<TemaResponse> getTemasByUnidadId(String unidadId) {
        return temaRepository.findByUnidadId(unidadId).stream()
                .map(temaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca temas por título.
     */
    @Transactional(readOnly = true)
    public List<TemaResponse> getTemasByTitulo(String titulo) {
        return temaRepository.findByTituloContaining(titulo).stream()
                .map(temaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los temas eliminados.
     */
    @Transactional(readOnly = true)
    public List<TemaResponse> getTemasDeleted() {
        return temaRepository.findAllDeleted().stream()
                .map(temaMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo tema.
     */
    public TemaResponse createTema(CreateTemaRequest request) {
        // Validar que la unidad existe
        Unidad unidad = unidadRepository.findById(request.unidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Unidad no encontrada con ID: " + request.unidadId()));

        Tema tema = new Tema();
        tema.setUnidad(unidad);
        tema.setTitulo(request.titulo());
        tema.setDescripcion(request.descripcion());
        tema.setNumero(request.numero());
        tema.setDuracionMinutos(request.duracionMinutos());
        tema.setDocumentoUrl(request.documentoUrl());
        tema.setDocumentoNombre(request.documentoNombre());

        Tema savedTema = temaRepository.save(tema);
        return temaMapper.toResponse(savedTema);
    }

    /**
     * Actualiza un tema existente.
     */
    public TemaResponse updateTema(String id, UpdateTemaRequest request) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));

        if (request.titulo() != null) {
            tema.setTitulo(request.titulo());
        }
        if (request.descripcion() != null) {
            tema.setDescripcion(request.descripcion());
        }
        if (request.numero() != null) {
            tema.setNumero(request.numero());
        }
        if (request.duracionMinutos() != null) {
            tema.setDuracionMinutos(request.duracionMinutos());
        }
        if (request.documentoUrl() != null) {
            tema.setDocumentoUrl(request.documentoUrl());
        }
        if (request.documentoNombre() != null) {
            tema.setDocumentoNombre(request.documentoNombre());
        }

        Tema updatedTema = temaRepository.save(tema);
        return temaMapper.toResponse(updatedTema);
    }

    /**
     * Elimina lógicamente un tema (soft delete).
     */
    public void deleteTema(String id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));
        tema.setDeletedAt(LocalDateTime.now());
        temaRepository.save(tema);
    }

    /**
     * Elimina permanentemente un tema.
     */
    public void permanentDeleteTema(String id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));
        temaRepository.delete(tema);
    }

    /**
     * Restaura un tema eliminado.
     */
    public TemaResponse restoreTema(String id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tema no encontrado con ID: " + id));
        tema.setDeletedAt(null);
        Tema restoredTema = temaRepository.save(tema);
        return temaMapper.toResponse(restoredTema);
    }
}
