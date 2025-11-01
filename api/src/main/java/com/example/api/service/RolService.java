package com.example.api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.api.dto.request.CreateRolRequest;
import com.example.api.dto.request.UpdateRolRequest;
import com.example.api.dto.response.RolResponse;
import com.example.api.exception.DuplicateResourceException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Rol;
import com.example.api.repository.RolRepository;

/**
 * Servicio que contiene la lógica de negocio para la gestión de roles.
 */
@Service
@Transactional
public class RolService {

    private final RolRepository rolRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param rolRepository Repositorio de roles
     */
    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    /**
     * Convierte una entidad Rol a RolResponse.
     * Método auxiliar para evitar problemas de Lombok en el IDE.
     *
     * @param rol La entidad Rol
     * @return El DTO RolResponse
     */
    private RolResponse toResponse(Rol rol) {
        return new RolResponse(
                rol.getId(),
                rol.getNombre(),
                rol.getDescripcion(),
                rol.getCreatedAt(),
                rol.getUpdatedAt(),
                rol.getDeletedAt()
        );
    }

    /**
     * Obtiene todos los roles activos.
     *
     * @return Lista de roles activos
     */
    @Transactional(readOnly = true)
    public List<RolResponse> getAllRoles() {
        return rolRepository.findAllActive()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los roles eliminados (soft delete).
     *
     * @return Lista de roles eliminados
     */
    @Transactional(readOnly = true)
    public List<RolResponse> getAllDeletedRoles() {
        return rolRepository.findAllDeleted()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un rol por su ID.
     *
     * @param id El ID del rol
     * @return El rol encontrado
     * @throws ResourceNotFoundException si el rol no existe
     */
    @Transactional(readOnly = true)
    public RolResponse getRolById(String id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));

        if (rol.isDeleted()) {
            throw new ResourceNotFoundException("Rol", "id", id);
        }

        return toResponse(rol);
    }

    /**
     * Busca roles por nombre (búsqueda parcial).
     *
     * @param nombre Texto a buscar en el nombre
     * @return Lista de roles que coinciden
     */
    @Transactional(readOnly = true)
    public List<RolResponse> searchRolesByNombre(String nombre) {
        return rolRepository.searchByNombre(nombre)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo rol.
     *
     * @param request Datos del rol a crear
     * @return El rol creado
     * @throws DuplicateResourceException si ya existe un rol con ese nombre
     */
    public RolResponse createRol(CreateRolRequest request) {
        // Validar que no exista un rol con el mismo nombre
        if (rolRepository.existsByNombreAndIdNot(request.nombre(), null)) {
            throw new DuplicateResourceException("Rol", "nombre", request.nombre());
        }

        Rol rol = new Rol();
        rol.setNombre(request.nombre());
        rol.setDescripcion(request.descripcion());

        Rol savedRol = rolRepository.save(rol);
        return toResponse(savedRol);
    }

    /**
     * Actualiza un rol existente.
     *
     * @param id El ID del rol a actualizar
     * @param request Datos actualizados del rol
     * @return El rol actualizado
     * @throws ResourceNotFoundException si el rol no existe
     * @throws DuplicateResourceException si el nuevo nombre ya existe
     */
    public RolResponse updateRol(String id, UpdateRolRequest request) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));

        if (rol.isDeleted()) {
            throw new ResourceNotFoundException("Rol", "id", id);
        }

        // Si se va a actualizar el nombre, validar que no exista otro rol con ese nombre
        if (request.nombre() != null && !request.nombre().equals(rol.getNombre())) {
            if (rolRepository.existsByNombreAndIdNot(request.nombre(), id)) {
                throw new DuplicateResourceException("Rol", "nombre", request.nombre());
            }
            rol.setNombre(request.nombre());
        }

        // Actualizar descripción si se proporciona
        if (request.descripcion() != null) {
            rol.setDescripcion(request.descripcion());
        }

        Rol updatedRol = rolRepository.save(rol);
        return toResponse(updatedRol);
    }

    /**
     * Realiza una eliminación suave (soft delete) de un rol.
     *
     * @param id El ID del rol a eliminar
     * @throws ResourceNotFoundException si el rol no existe
     */
    public void softDeleteRol(String id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));

        if (rol.isDeleted()) {
            throw new ResourceNotFoundException("Rol", "id", id);
        }

        rol.softDelete();
        rolRepository.save(rol);
    }

    /**
     * Restaura un rol previamente eliminado (soft delete).
     *
     * @param id El ID del rol a restaurar
     * @throws ResourceNotFoundException si el rol no existe o no estaba eliminado
     */
    public RolResponse restoreRol(String id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));

        if (!rol.isDeleted()) {
            throw new IllegalStateException("El rol con ID " + id + " no está eliminado");
        }

        rol.restore();
        Rol restoredRol = rolRepository.save(rol);
        return toResponse(restoredRol);
    }

    /**
     * Elimina permanentemente un rol de la base de datos.
     * ADVERTENCIA: Esta operación no se puede deshacer.
     *
     * @param id El ID del rol a eliminar permanentemente
     * @throws ResourceNotFoundException si el rol no existe
     */
    public void permanentDeleteRol(String id) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));

        rolRepository.delete(rol);
    }
}
