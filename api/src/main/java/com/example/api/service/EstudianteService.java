package com.example.api.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateEstudianteRequest;
import com.example.api.dto.request.UpdateEstudianteRequest;
import com.example.api.dto.response.EstudianteResponse;
import com.example.api.exception.DuplicateResourceException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.mapper.EstudianteMapper;
import com.example.api.model.Estudiante;
import com.example.api.model.Estudiante.Genero;
import com.example.api.model.Usuario;
import com.example.api.repository.EstudianteRepository;
import com.example.api.repository.UsuarioRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de estudiantes.
 */
@Service
@Transactional
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstudianteMapper estudianteMapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param estudianteRepository Repositorio de estudiantes
     * @param usuarioRepository    Repositorio de usuarios
     * @param estudianteMapper     Mapper de estudiantes
     */
    public EstudianteService(EstudianteRepository estudianteRepository,
            UsuarioRepository usuarioRepository,
            EstudianteMapper estudianteMapper) {
        this.estudianteRepository = estudianteRepository;
        this.usuarioRepository = usuarioRepository;
        this.estudianteMapper = estudianteMapper;
    }

    /**
     * Obtiene todos los estudiantes activos (paginados).
     *
     * @param pageable Configuración de paginación
     * @return Página de estudiantes activos
     */
    @Transactional(readOnly = true)
    public Page<EstudianteResponse> getAllEstudiantes(Pageable pageable) {
        return estudianteRepository.findAllActive(pageable)
                .map(estudianteMapper::toResponse);
    }

    /**
     * Obtiene un estudiante por su ID.
     *
     * @param id El ID del estudiante
     * @return El estudiante encontrado
     * @throws ResourceNotFoundException si el estudiante no existe
     */
    @Transactional(readOnly = true)
    public EstudianteResponse getEstudianteById(String id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        return estudianteMapper.toResponse(estudiante);
    }

    /**
     * Busca un estudiante por su código único.
     *
     * @param codigo El código del estudiante
     * @return El estudiante encontrado
     * @throws ResourceNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public EstudianteResponse getEstudianteByCodigo(String codigo) {
        Estudiante estudiante = estudianteRepository.findByCodigoEstudiante(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con código: " + codigo));

        return estudianteMapper.toResponse(estudiante);
    }

    /**
     * Obtiene estudiantes filtrados por género.
     *
     * @param genero El género a filtrar
     * @return Lista de estudiantes del género especificado
     */
    @Transactional(readOnly = true)
    public List<EstudianteResponse> getEstudiantesByGenero(Genero genero) {
        return estudianteRepository.findByGenero(genero).stream()
                .map(estudianteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo estudiantes activos.
     *
     * @return Lista de estudiantes activos
     */
    @Transactional(readOnly = true)
    public List<EstudianteResponse> getEstudiantesActivos() {
        return estudianteRepository.findByActivoTrue().stream()
                .map(estudianteMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo estudiante.
     *
     * @param request Datos del estudiante a crear
     * @return El estudiante creado
     * @throws ResourceNotFoundException  si el usuario no existe
     * @throws DuplicateResourceException si el código ya existe
     */
    public EstudianteResponse createEstudiante(CreateEstudianteRequest request) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.usuarioId()));

        // Verificar que el código no exista
        if (estudianteRepository.findByCodigoEstudiante(request.codigoEstudiante()).isPresent()) {
            throw new DuplicateResourceException(
                    "Ya existe un estudiante con el código: " + request.codigoEstudiante());
        }

        // Crear estudiante
        Estudiante estudiante = new Estudiante();
        estudiante.setUsuario(usuario);
        estudiante.setCodigoEstudiante(request.codigoEstudiante());
        estudiante.setFechaNacimiento(request.fechaNacimiento());
        estudiante.setDireccion(request.direccion());
        estudiante.setGenero(request.genero() != null ? request.genero() : Genero.O);
        estudiante.setIngreso(request.ingreso() != null ? request.ingreso() : LocalDate.now());
        estudiante.setActivo(request.activo() != null ? request.activo() : true);
        estudiante.setFotoUrl(request.fotoUrl());

        Estudiante guardado = estudianteRepository.save(estudiante);
        return estudianteMapper.toResponse(guardado);
    }

    /**
     * Actualiza un estudiante existente.
     *
     * @param id      ID del estudiante a actualizar
     * @param request Datos a actualizar
     * @return El estudiante actualizado
     * @throws ResourceNotFoundException  si el estudiante no existe
     * @throws DuplicateResourceException si el nuevo código ya existe
     */
    public EstudianteResponse updateEstudiante(String id, UpdateEstudianteRequest request) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        // Actualizar código si cambió y verificar unicidad
        if (request.codigoEstudiante() != null
                && !request.codigoEstudiante().equals(estudiante.getCodigoEstudiante())) {
            if (estudianteRepository.findByCodigoEstudiante(request.codigoEstudiante()).isPresent()) {
                throw new DuplicateResourceException(
                        "Ya existe otro estudiante con el código: " + request.codigoEstudiante());
            }
            estudiante.setCodigoEstudiante(request.codigoEstudiante());
        }

        // Actualizar campos opcionales
        if (request.fechaNacimiento() != null) {
            estudiante.setFechaNacimiento(request.fechaNacimiento());
        }
        if (request.direccion() != null) {
            estudiante.setDireccion(request.direccion());
        }
        if (request.genero() != null) {
            estudiante.setGenero(request.genero());
        }
        if (request.ingreso() != null) {
            estudiante.setIngreso(request.ingreso());
        }
        if (request.activo() != null) {
            estudiante.setActivo(request.activo());
        }
        if (request.fotoUrl() != null) {
            estudiante.setFotoUrl(request.fotoUrl());
        }

        Estudiante actualizado = estudianteRepository.save(estudiante);
        return estudianteMapper.toResponse(actualizado);
    }

    /**
     * Elimina un estudiante (soft delete).
     *
     * @param id ID del estudiante a eliminar
     * @throws ResourceNotFoundException si el estudiante no existe
     */
    public void deleteEstudiante(String id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        estudiante.setActivo(false);
        estudianteRepository.save(estudiante);
    }

    /**
     * Elimina permanentemente un estudiante de la base de datos.
     *
     * @param id ID del estudiante a eliminar
     * @throws ResourceNotFoundException si el estudiante no existe
     */
    public void permanentDeleteEstudiante(String id) {
        if (!estudianteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Estudiante no encontrado con ID: " + id);
        }

        estudianteRepository.deleteById(id);
    }
}
