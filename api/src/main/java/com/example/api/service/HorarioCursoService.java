package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateHorarioCursoRequest;
import com.example.api.dto.request.UpdateHorarioCursoRequest;
import com.example.api.dto.response.HorarioCursoResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.BloqueHorario;
import com.example.api.model.Curso;
import com.example.api.model.HorarioCurso;
import com.example.api.model.HorarioCurso.DiaSemana;
import com.example.api.model.HorarioCurso.TipoHorario;
import com.example.api.repository.BloqueHorarioRepository;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.HorarioCursoRepository;
import com.example.api.mapper.HorarioCursoMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de horarios de
 * curso.
 */
@Service
@Transactional
public class HorarioCursoService {

    private final HorarioCursoRepository horarioCursoRepository;
    private final CursoRepository cursoRepository;
    private final BloqueHorarioRepository bloqueHorarioRepository;
    private final HorarioCursoMapper horarioCursoMapper;

    /**
     * Constructor con inyección de dependencias.
     */
    public HorarioCursoService(HorarioCursoRepository horarioCursoRepository,
            CursoRepository cursoRepository,
            BloqueHorarioRepository bloqueHorarioRepository,
            HorarioCursoMapper horarioCursoMapper) {
        this.horarioCursoRepository = horarioCursoRepository;
        this.cursoRepository = cursoRepository;
        this.bloqueHorarioRepository = bloqueHorarioRepository;
        this.horarioCursoMapper = horarioCursoMapper;
    }

    /**
     * Obtiene todos los horarios activos (paginados).
     */
    @Transactional(readOnly = true)
    public Page<HorarioCursoResponse> getAllHorarios(Pageable pageable) {
        return horarioCursoRepository.findAllActive(pageable)
                .map(horarioCursoMapper::toResponse);
    }

    /**
     * Obtiene un horario por su ID.
     */
    @Transactional(readOnly = true)
    public HorarioCursoResponse getHorarioById(String id) {
        HorarioCurso horario = horarioCursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario de curso no encontrado con ID: " + id));
        return horarioCursoMapper.toResponse(horario);
    }

    /**
     * Busca horarios por curso.
     */
    @Transactional(readOnly = true)
    public List<HorarioCursoResponse> getHorariosByCursoId(String cursoId) {
        return horarioCursoRepository.findByCursoId(cursoId)
                .stream()
                .map(horarioCursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca horarios por día de la semana.
     */
    @Transactional(readOnly = true)
    public List<HorarioCursoResponse> getHorariosByDia(DiaSemana dia) {
        return horarioCursoRepository.findByDia(dia)
                .stream()
                .map(horarioCursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Detecta conflictos de horarios.
     * Encuentra horarios que se solapan en el mismo día, bloque y aula.
     */
    @Transactional(readOnly = true)
    public List<HorarioCursoResponse> getConflictos() {
        return horarioCursoRepository.findConflictos()
                .stream()
                .map(horarioCursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los horarios eliminados.
     */
    @Transactional(readOnly = true)
    public List<HorarioCursoResponse> getHorariosDeleted() {
        return horarioCursoRepository.findAllDeleted()
                .stream()
                .map(horarioCursoMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo horario de curso.
     */
    public HorarioCursoResponse createHorario(CreateHorarioCursoRequest request) {
        // Validar que el curso existe
        Curso curso = cursoRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));

        // Validar que el bloque horario existe
        BloqueHorario bloque = bloqueHorarioRepository.findById(request.bloqueId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bloque de horario no encontrado con ID: " + request.bloqueId()));

        // Validar que no existe un horario duplicado (mismo curso, bloque y día)
        if (horarioCursoRepository.existsByCursoAndBloqueAndDia(request.cursoId(), request.bloqueId(), request.dia())) {
            throw new IllegalArgumentException("Ya existe un horario para este curso en el mismo bloque y día");
        }

        HorarioCurso horario = horarioCursoMapper.toEntity(request);
        horario.setCurso(curso);
        horario.setBloqueHorario(bloque);
        if (horario.getTipo() == null) {
            horario.setTipo(TipoHorario.regular);
        }

        HorarioCurso savedHorario = horarioCursoRepository.save(horario);
        return horarioCursoMapper.toResponse(savedHorario);
    }

    /**
     * Actualiza un horario de curso existente.
     */
    public HorarioCursoResponse updateHorario(String id, UpdateHorarioCursoRequest request) {
        HorarioCurso horario = horarioCursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario de curso no encontrado con ID: " + id));

        horarioCursoMapper.updateEntityFromDto(request, horario);

        if (request.cursoId() != null) {
            Curso curso = cursoRepository.findById(request.cursoId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.cursoId()));
            horario.setCurso(curso);
        }

        if (request.bloqueId() != null) {
            BloqueHorario bloque = bloqueHorarioRepository.findById(request.bloqueId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Bloque de horario no encontrado con ID: " + request.bloqueId()));
            horario.setBloqueHorario(bloque);
        }

        // Validar que no se crea un duplicado al actualizar
        String cursoIdFinal = horario.getCurso().getId();
        String bloqueIdFinal = horario.getBloqueHorario().getId();
        DiaSemana diaFinal = horario.getDia();

        // Verificar duplicados excluyendo el horario actual
        List<HorarioCurso> posiblesDuplicados = horarioCursoRepository.findByCursoId(cursoIdFinal)
                .stream()
                .filter(h -> !h.getId().equals(id))
                .filter(h -> h.getBloqueHorario().getId().equals(bloqueIdFinal))
                .filter(h -> h.getDia() == diaFinal)
                .collect(Collectors.toList());

        if (!posiblesDuplicados.isEmpty()) {
            throw new IllegalArgumentException("Ya existe un horario para este curso en el mismo bloque y día");
        }

        HorarioCurso updatedHorario = horarioCursoRepository.save(horario);
        return horarioCursoMapper.toResponse(updatedHorario);
    }

    /**
     * Elimina lógicamente un horario (soft delete).
     */
    public void deleteHorario(String id) {
        HorarioCurso horario = horarioCursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario de curso no encontrado con ID: " + id));
        horario.setDeletedAt(LocalDateTime.now());
        horarioCursoRepository.save(horario);
    }

    /**
     * Elimina permanentemente un horario.
     */
    public void permanentDeleteHorario(String id) {
        if (!horarioCursoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Horario de curso no encontrado con ID: " + id);
        }
        horarioCursoRepository.deleteById(id);
    }

    /**
     * Restaura un horario eliminado lógicamente.
     */
    public HorarioCursoResponse restoreHorario(String id) {
        HorarioCurso horario = horarioCursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario de curso no encontrado con ID: " + id));
        horario.setDeletedAt(null);
        HorarioCurso restoredHorario = horarioCursoRepository.save(horario);
        return horarioCursoMapper.toResponse(restoredHorario);
    }
}
