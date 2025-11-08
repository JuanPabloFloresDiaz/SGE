package com.example.api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateProfesorRequest;
import com.example.api.dto.request.UpdateProfesorRequest;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.dto.response.RolResponse;
import com.example.api.dto.response.UsuarioResponse;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Profesor;
import com.example.api.model.Profesor.TipoContrato;
import com.example.api.model.Usuario;
import com.example.api.repository.ProfesorRepository;
import com.example.api.repository.UsuarioRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de profesores.
 */
@Service
@Transactional
public class ProfesorService {

    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param profesorRepository Repositorio de profesores
     * @param usuarioRepository Repositorio de usuarios
     */
    public ProfesorService(ProfesorRepository profesorRepository, 
                          UsuarioRepository usuarioRepository) {
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Convierte una entidad Profesor a ProfesorResponse.
     *
     * @param profesor La entidad Profesor
     * @return El DTO ProfesorResponse
     */
    private ProfesorResponse toResponse(Profesor profesor) {
        UsuarioResponse usuarioResponse = null;
        
        if (profesor.getUsuario() != null) {
            Usuario usuario = profesor.getUsuario();
            RolResponse rolResponse = new RolResponse(
                    usuario.getRol().getId(),
                    usuario.getRol().getNombre(),
                    usuario.getRol().getDescripcion(),
                    usuario.getRol().getCreatedAt(),
                    usuario.getRol().getUpdatedAt(),
                    usuario.getRol().getDeletedAt()
            );

            usuarioResponse = new UsuarioResponse(
                    usuario.getId(),
                    usuario.getUsername(),
                    usuario.getNombre(),
                    usuario.getEmail(),
                    usuario.getTelefono(),
                    usuario.getActivo(),
                    rolResponse,
                    usuario.getCreatedAt(),
                    usuario.getUpdatedAt(),
                    usuario.getDeletedAt()
            );
        }

        return new ProfesorResponse(
                profesor.getId(),
                usuarioResponse,
                profesor.getEspecialidad(),
                profesor.getContrato(),
                profesor.getActivo(),
                profesor.getCreatedAt(),
                profesor.getUpdatedAt(),
                profesor.getDeletedAt()
        );
    }

    /**
     * Obtiene todos los profesores activos (paginados).
     *
     * @param pageable Configuración de paginación
     * @return Página de profesores activos
     */
    @Transactional(readOnly = true)
    public Page<ProfesorResponse> getAllProfesores(Pageable pageable) {
        return profesorRepository.findAllActive(pageable)
                .map(this::toResponse);
    }

    /**
     * Obtiene un profesor por su ID.
     *
     * @param id El ID del profesor
     * @return El profesor encontrado
     * @throws ResourceNotFoundException si el profesor no existe
     */
    @Transactional(readOnly = true)
    public ProfesorResponse getProfesorById(String id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));
        return toResponse(profesor);
    }

    /**
     * Busca profesores por especialidad (búsqueda parcial).
     *
     * @param especialidad Texto a buscar en la especialidad
     * @return Lista de profesores que coinciden
     */
    @Transactional(readOnly = true)
    public List<ProfesorResponse> searchByEspecialidad(String especialidad) {
        return profesorRepository.searchByEspecialidad(especialidad)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo profesores activos.
     *
     * @return Lista de profesores activos
     */
    @Transactional(readOnly = true)
    public List<ProfesorResponse> getProfesoresActivos() {
        return profesorRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene profesores eliminados.
     *
     * @return Lista de profesores eliminados
     */
    @Transactional(readOnly = true)
    public List<ProfesorResponse> getProfesoresDeleted() {
        return profesorRepository.findAllDeleted()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo profesor.
     *
     * @param request Datos del profesor a crear
     * @return El profesor creado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public ProfesorResponse createProfesor(CreateProfesorRequest request) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con ID: " + request.usuarioId()));

        // Crear nuevo profesor
        Profesor profesor = new Profesor();
        profesor.setUsuario(usuario);
        profesor.setEspecialidad(request.especialidad());
        
        // Establecer valores por defecto
        profesor.setContrato(request.contrato() != null ? request.contrato() : TipoContrato.eventual);
        profesor.setActivo(request.activo() != null ? request.activo() : true);

        // Guardar
        Profesor profesorGuardado = profesorRepository.save(profesor);
        return toResponse(profesorGuardado);
    }

    /**
     * Actualiza un profesor existente.
     *
     * @param id El ID del profesor a actualizar
     * @param request Datos a actualizar
     * @return El profesor actualizado
     * @throws ResourceNotFoundException si el profesor no existe
     */
    public ProfesorResponse updateProfesor(String id, UpdateProfesorRequest request) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));

        // Actualizar solo los campos proporcionados
        if (request.especialidad() != null) {
            profesor.setEspecialidad(request.especialidad());
        }
        if (request.contrato() != null) {
            profesor.setContrato(request.contrato());
        }
        if (request.activo() != null) {
            profesor.setActivo(request.activo());
        }

        Profesor profesorActualizado = profesorRepository.save(profesor);
        return toResponse(profesorActualizado);
    }

    /**
     * Realiza eliminación suave de un profesor.
     *
     * @param id El ID del profesor a eliminar
     * @throws ResourceNotFoundException si el profesor no existe
     */
    public void deleteProfesor(String id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));

        profesor.setDeletedAt(LocalDateTime.now());
        profesor.setActivo(false);
        profesorRepository.save(profesor);
    }

    /**
     * Elimina permanentemente un profesor de la base de datos.
     *
     * @param id El ID del profesor a eliminar
     * @throws ResourceNotFoundException si el profesor no existe
     */
    public void permanentDeleteProfesor(String id) {
        if (!profesorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Profesor no encontrado con ID: " + id);
        }
        profesorRepository.deleteById(id);
    }

    /**
     * Restaura un profesor eliminado.
     *
     * @param id El ID del profesor a restaurar
     * @return El profesor restaurado
     * @throws ResourceNotFoundException si el profesor no existe
     */
    public ProfesorResponse restoreProfesor(String id) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));

        profesor.setDeletedAt(null);
        profesor.setActivo(true);
        Profesor profesorRestaurado = profesorRepository.save(profesor);
        return toResponse(profesorRestaurado);
    }
}
