package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateTiposPonderacionCursoRequest;
import com.example.api.dto.request.UpdateTiposPonderacionCursoRequest;
import com.example.api.dto.response.TiposPonderacionCursoResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.mapper.TiposPonderacionCursoMapper;
import com.example.api.model.Curso;
import com.example.api.model.TiposPonderacionCurso;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.TiposPonderacionCursoRepository;

@Service
@Transactional
public class TiposPonderacionCursoService {

    private final TiposPonderacionCursoRepository repository;
    private final CursoRepository cursoRepository;
    private final TiposPonderacionCursoMapper mapper;

    public TiposPonderacionCursoService(TiposPonderacionCursoRepository repository,
            CursoRepository cursoRepository,
            TiposPonderacionCursoMapper mapper) {
        this.repository = repository;
        this.cursoRepository = cursoRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<TiposPonderacionCursoResponse> getAll(Pageable pageable) {
        return repository.findAllActive(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TiposPonderacionCursoResponse getById(String id) {
        TiposPonderacionCurso entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ponderación no encontrado con ID: " + id));
        return mapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<TiposPonderacionCursoResponse> getByCursoId(String cursoId) {
        return repository.findByCursoId(cursoId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public TiposPonderacionCursoResponse create(CreateTiposPonderacionCursoRequest request) {
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));

        TiposPonderacionCurso entity = mapper.toEntity(request);
        entity.setCurso(curso);

        TiposPonderacionCurso saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public TiposPonderacionCursoResponse update(String id, UpdateTiposPonderacionCursoRequest request) {
        TiposPonderacionCurso entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ponderación no encontrado con ID: " + id));

        mapper.updateEntityFromDto(request, entity);

        TiposPonderacionCurso updated = repository.save(entity);
        return mapper.toResponse(updated);
    }

    public void delete(String id) {
        TiposPonderacionCurso entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de ponderación no encontrado con ID: " + id));
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }
}
