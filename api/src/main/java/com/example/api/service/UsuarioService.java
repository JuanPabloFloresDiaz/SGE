package com.example.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateUsuarioRequest;
import com.example.api.dto.request.UpdateUsuarioRequest;
import com.example.api.dto.response.RolResponse;
import com.example.api.dto.response.UsuarioResponse;
import com.example.api.exception.DuplicateResourceException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Rol;
import com.example.api.model.Usuario;
import com.example.api.repository.RolRepository;
import com.example.api.repository.UsuarioRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de usuarios.
 */
@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param usuarioRepository Repositorio de usuarios
     * @param rolRepository Repositorio de roles
     */
    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Convierte una entidad Usuario a UsuarioResponse.
     * Método auxiliar para evitar problemas de Lombok en el IDE.
     *
     * @param usuario La entidad Usuario
     * @return El DTO UsuarioResponse
     */
    private UsuarioResponse toResponse(Usuario usuario) {
        RolResponse rolResponse = usuario.getRol() != null ? new RolResponse(
                usuario.getRol().getId(),
                usuario.getRol().getNombre(),
                usuario.getRol().getDescripcion(),
                usuario.getRol().getCreatedAt(),
                usuario.getRol().getUpdatedAt(),
                usuario.getRol().getDeletedAt()
        ) : null;

        return new UsuarioResponse(
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

    /**
     * Obtiene todos los usuarios activos con paginación.
     *
     * @param page Número de página (0-indexed)
     * @param size Tamaño de página
     * @return Página de usuarios activos
     */
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> getAllUsuarios(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return usuarioRepository.findAllActive(pageRequest)
                .map(this::toResponse);
    }

    /**
     * Obtiene todos los usuarios activos sin paginación.
     *
     * @return Lista de usuarios activos
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAllUsuarios() {
        return usuarioRepository.findAllActive()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los usuarios eliminados (soft delete).
     *
     * @return Lista de usuarios eliminados
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAllDeletedUsuarios() {
        return usuarioRepository.findAllDeleted()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id El ID del usuario
     * @return El usuario encontrado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public UsuarioResponse getUsuarioById(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        if (usuario.isDeleted()) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }

        return toResponse(usuario);
    }

    /**
     * Busca un usuario por su username.
     *
     * @param username El username a buscar
     * @return El usuario encontrado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public UsuarioResponse getUsuarioByUsername(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));

        return toResponse(usuario);
    }

    /**
     * Busca un usuario por su email.
     *
     * @param email El email a buscar
     * @return El usuario encontrado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Transactional(readOnly = true)
    public UsuarioResponse getUsuarioByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        return toResponse(usuario);
    }

    /**
     * Obtiene usuarios por rol.
     *
     * @param rolId ID del rol
     * @return Lista de usuarios con ese rol
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> getUsuariosByRol(String rolId) {
        return usuarioRepository.findByRolId(rolId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene solo usuarios activos (activo = true).
     *
     * @return Lista de usuarios activos
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> getUsuariosActivos() {
        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca usuarios por username (búsqueda parcial).
     *
     * @param username Texto a buscar en el username
     * @return Lista de usuarios que coinciden
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> searchUsuariosByUsername(String username) {
        return usuarioRepository.searchByUsername(username)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca usuarios por nombre (búsqueda parcial).
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de usuarios que coinciden
     */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> searchUsuariosByNombre(String nombre) {
        return usuarioRepository.searchByNombre(nombre)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param request Datos del usuario a crear
     * @return El usuario creado
     * @throws DuplicateResourceException si ya existe un usuario con ese username o email
     * @throws ResourceNotFoundException si el rol no existe
     */
    public UsuarioResponse createUsuario(CreateUsuarioRequest request) {
        // Validar que no exista un usuario con el mismo username
        if (usuarioRepository.existsByUsernameAndIdNot(request.username(), null)) {
            throw new DuplicateResourceException("Ya existe un usuario con el username: " + request.username());
        }

        // Validar que no exista un usuario con el mismo email (si se proporciona)
        if (request.email() != null && usuarioRepository.existsByEmailAndIdNot(request.email(), null)) {
            throw new DuplicateResourceException("Ya existe un usuario con el email: " + request.email());
        }

        // Validar que el rol exista
        Rol rol = rolRepository.findById(request.rolId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", request.rolId()));

        if (rol.isDeleted()) {
            throw new ResourceNotFoundException("Rol", "id", request.rolId());
        }

        // Crear el usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setTelefono(request.telefono());
        usuario.setRol(rol);
        usuario.setActivo(request.activo() != null ? request.activo() : true);

        Usuario savedUsuario = usuarioRepository.save(usuario);
        return toResponse(savedUsuario);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id El ID del usuario a actualizar
     * @param request Datos actualizados del usuario
     * @return El usuario actualizado
     * @throws ResourceNotFoundException si el usuario o rol no existe
     * @throws DuplicateResourceException si el nuevo username o email ya existe
     */
    public UsuarioResponse updateUsuario(String id, UpdateUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        if (usuario.isDeleted()) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }

        // Actualizar username si se proporciona
        if (request.username() != null && !request.username().equals(usuario.getUsername())) {
            if (usuarioRepository.existsByUsernameAndIdNot(request.username(), id)) {
                throw new DuplicateResourceException("Ya existe un usuario con el username: " + request.username());
            }
            usuario.setUsername(request.username());
        }

        // Actualizar password si se proporciona
        if (request.password() != null) {
            usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        // Actualizar nombre si se proporciona
        if (request.nombre() != null) {
            usuario.setNombre(request.nombre());
        }

        // Actualizar email si se proporciona
        if (request.email() != null && !request.email().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmailAndIdNot(request.email(), id)) {
                throw new DuplicateResourceException("Ya existe un usuario con el email: " + request.email());
            }
            usuario.setEmail(request.email());
        }

        // Actualizar teléfono si se proporciona
        if (request.telefono() != null) {
            usuario.setTelefono(request.telefono());
        }

        // Actualizar rol si se proporciona
        if (request.rolId() != null && !request.rolId().equals(usuario.getRol().getId())) {
            Rol rol = rolRepository.findById(request.rolId())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", request.rolId()));

            if (rol.isDeleted()) {
                throw new ResourceNotFoundException("Rol", "id", request.rolId());
            }

            usuario.setRol(rol);
        }

        // Actualizar estado activo si se proporciona
        if (request.activo() != null) {
            usuario.setActivo(request.activo());
        }

        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return toResponse(updatedUsuario);
    }

    /**
     * Realiza una eliminación suave (soft delete) de un usuario.
     *
     * @param id El ID del usuario a eliminar
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public void softDeleteUsuario(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        if (usuario.isDeleted()) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }

        usuario.softDelete();
        usuarioRepository.save(usuario);
    }

    /**
     * Restaura un usuario previamente eliminado (soft delete).
     *
     * @param id El ID del usuario a restaurar
     * @throws ResourceNotFoundException si el usuario no existe o no estaba eliminado
     */
    public UsuarioResponse restoreUsuario(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        if (!usuario.isDeleted()) {
            throw new ResourceNotFoundException("El usuario no está eliminado");
        }

        usuario.restore();
        Usuario restoredUsuario = usuarioRepository.save(usuario);
        return toResponse(restoredUsuario);
    }

    /**
     * Elimina permanentemente un usuario de la base de datos.
     * ADVERTENCIA: Esta operación no se puede deshacer.
     *
     * @param id El ID del usuario a eliminar permanentemente
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public void permanentDeleteUsuario(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuarioRepository.delete(usuario);
    }
}
