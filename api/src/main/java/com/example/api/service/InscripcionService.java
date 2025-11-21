package com.example.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateInscripcionRequest;
import com.example.api.dto.request.UpdateInscripcionRequest;
import com.example.api.dto.response.InscripcionResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Curso;
import com.example.api.model.Estudiante;
import com.example.api.model.Inscripcion;
import com.example.api.model.Inscripcion.EstadoInscripcion;
import com.example.api.repository.CursoRepository;
import com.example.api.repository.EstudianteRepository;
import com.example.api.repository.InscripcionRepository;
import com.example.api.mapper.InscripcionMapper;

/**
 * Servicio que contiene la lógica de negocio para la gestión de inscripciones.
 */
@Service
@Transactional
public class InscripcionService {

        private final InscripcionRepository inscripcionRepository;
        private final CursoRepository cursoRepository;
        private final EstudianteRepository estudianteRepository;
        private final InscripcionMapper inscripcionMapper;

        /**
         * Constructor con inyección de dependencias.
         */
        public InscripcionService(InscripcionRepository inscripcionRepository,
                        CursoRepository cursoRepository,
                        EstudianteRepository estudianteRepository,
                        InscripcionMapper inscripcionMapper) {
                this.inscripcionRepository = inscripcionRepository;
                this.cursoRepository = cursoRepository;
                this.estudianteRepository = estudianteRepository;
                this.inscripcionMapper = inscripcionMapper;
        }

        /**
         * Obtiene todas las inscripciones activas (paginadas).
         */
        @Transactional(readOnly = true)
        public Page<InscripcionResponse> getAllInscripciones(Pageable pageable) {
                return inscripcionRepository.findAllActive(pageable)
                                .map(inscripcionMapper::toResponse);
        }

        /**
         * Obtiene una inscripción por su ID.
         */
        @Transactional(readOnly = true)
        public InscripcionResponse getInscripcionById(String id) {
                Inscripcion inscripcion = inscripcionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inscripción no encontrada con ID: " + id));
                return inscripcionMapper.toResponse(inscripcion);
        }

        /**
         * Busca inscripciones por estudiante.
         */
        @Transactional(readOnly = true)
        public List<InscripcionResponse> getInscripcionesByEstudianteId(String estudianteId) {
                return inscripcionRepository.findByEstudianteId(estudianteId)
                                .stream()
                                .map(inscripcionMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Busca inscripciones por curso.
         */
        @Transactional(readOnly = true)
        public List<InscripcionResponse> getInscripcionesByCursoId(String cursoId) {
                return inscripcionRepository.findByCursoId(cursoId)
                                .stream()
                                .map(inscripcionMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Busca inscripciones por estado.
         */
        @Transactional(readOnly = true)
        public List<InscripcionResponse> getInscripcionesByEstado(EstadoInscripcion estado) {
                return inscripcionRepository.findByEstado(estado)
                                .stream()
                                .map(inscripcionMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Obtiene el historial de inscripciones de un estudiante.
         * Incluye todas las inscripciones ordenadas por fecha descendente.
         */
        @Transactional(readOnly = true)
        public List<InscripcionResponse> getHistorialByEstudianteId(String estudianteId) {
                return inscripcionRepository.findHistorialByEstudianteId(estudianteId)
                                .stream()
                                .map(inscripcionMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Obtiene todas las inscripciones eliminadas.
         */
        @Transactional(readOnly = true)
        public List<InscripcionResponse> getInscripcionesDeleted() {
                return inscripcionRepository.findAllDeleted()
                                .stream()
                                .map(inscripcionMapper::toResponse)
                                .collect(Collectors.toList());
        }

        /**
         * Crea una nueva inscripción.
         * Valida que el curso tenga cupos disponibles y que no exista una inscripción
         * duplicada.
         */
        public InscripcionResponse createInscripcion(CreateInscripcionRequest request) {
                // Validar que el curso existe
                Curso curso = cursoRepository.findById(request.cursoId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Curso no encontrado con ID: " + request.cursoId()));

                // Validar que el estudiante existe
                Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Estudiante no encontrado con ID: " + request.estudianteId()));

                // Validar que no existe una inscripción duplicada
                if (inscripcionRepository.existsByEstudianteAndCurso(request.estudianteId(), request.cursoId())) {
                        throw new IllegalArgumentException("El estudiante ya está inscrito en este curso");
                }

                // Validar cupo disponible
                Long inscritosCount = inscripcionRepository.findByCursoId(request.cursoId())
                                .stream()
                                .filter(i -> i.getEstado() == EstadoInscripcion.inscrito)
                                .count();

                if (inscritosCount >= curso.getCupo()) {
                        throw new IllegalArgumentException("El curso no tiene cupos disponibles");
                }

                Inscripcion inscripcion = new Inscripcion();
                inscripcion.setCurso(curso);
                inscripcion.setEstudiante(estudiante);
                inscripcion.setFechaInscripcion(
                                request.fechaInscripcion() != null ? request.fechaInscripcion() : LocalDate.now());
                inscripcion.setEstado(request.estado() != null ? request.estado() : EstadoInscripcion.inscrito);

                Inscripcion savedInscripcion = inscripcionRepository.save(inscripcion);
                return inscripcionMapper.toResponse(savedInscripcion);
        }

        /**
         * Actualiza una inscripción existente.
         */
        public InscripcionResponse updateInscripcion(String id, UpdateInscripcionRequest request) {
                Inscripcion inscripcion = inscripcionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inscripción no encontrada con ID: " + id));

                if (request.fechaInscripcion() != null) {
                        inscripcion.setFechaInscripcion(request.fechaInscripcion());
                }

                if (request.estado() != null) {
                        inscripcion.setEstado(request.estado());
                }

                Inscripcion updatedInscripcion = inscripcionRepository.save(inscripcion);
                return inscripcionMapper.toResponse(updatedInscripcion);
        }

        /**
         * Elimina lógicamente una inscripción (soft delete).
         */
        public void deleteInscripcion(String id) {
                Inscripcion inscripcion = inscripcionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inscripción no encontrada con ID: " + id));
                inscripcion.setDeletedAt(LocalDateTime.now());
                inscripcionRepository.save(inscripcion);
        }

        /**
         * Elimina permanentemente una inscripción.
         */
        public void permanentDeleteInscripcion(String id) {
                if (!inscripcionRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Inscripción no encontrada con ID: " + id);
                }
                inscripcionRepository.deleteById(id);
        }

        /**
         * Restaura una inscripción eliminada lógicamente.
         */
        public InscripcionResponse restoreInscripcion(String id) {
                Inscripcion inscripcion = inscripcionRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inscripción no encontrada con ID: " + id));
                inscripcion.setDeletedAt(null);
                Inscripcion restoredInscripcion = inscripcionRepository.save(inscripcion);
                return inscripcionMapper.toResponse(restoredInscripcion);
        }
}
