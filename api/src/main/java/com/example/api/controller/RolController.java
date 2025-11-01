package com.example.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.dto.request.CreateRolRequest;
import com.example.api.dto.request.UpdateRolRequest;
import com.example.api.dto.response.RolResponse;
import com.example.api.service.RolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de roles.
 * Proporciona endpoints para CRUD completo de roles.
 */
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "API para la gestión de roles de usuario")
public class RolController {

    private final RolService rolService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param rolService Servicio de roles
     */
    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @Operation(
            summary = "Listar todos los roles activos",
            description = "Obtiene una lista de todos los roles que no han sido eliminados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de roles obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RolResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<RolResponse>> getAllRoles() {
        List<RolResponse> roles = rolService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(
            summary = "Listar roles eliminados",
            description = "Obtiene una lista de todos los roles que han sido eliminados (soft delete)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de roles eliminados obtenida exitosamente"
            )
    })
    @GetMapping("/deleted")
    public ResponseEntity<List<RolResponse>> getAllDeletedRoles() {
        List<RolResponse> roles = rolService.getAllDeletedRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(
            summary = "Obtener rol por ID",
            description = "Obtiene un rol específico utilizando su ID único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RolResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rol no encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RolResponse> getRolById(
            @Parameter(description = "ID único del rol", required = true)
            @PathVariable String id) {
        RolResponse rol = rolService.getRolById(id);
        return ResponseEntity.ok(rol);
    }

    @Operation(
            summary = "Buscar roles por nombre",
            description = "Busca roles que contengan el texto especificado en su nombre (búsqueda parcial)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda realizada exitosamente"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<List<RolResponse>> searchRoles(
            @Parameter(description = "Texto a buscar en el nombre del rol", required = true)
            @RequestParam String nombre) {
        List<RolResponse> roles = rolService.searchRolesByNombre(nombre);
        return ResponseEntity.ok(roles);
    }

    @Operation(
            summary = "Crear un nuevo rol",
            description = "Crea un nuevo rol en el sistema con los datos proporcionados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Rol creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RolResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe un rol con ese nombre"
            )
    })
    @PostMapping
    public ResponseEntity<RolResponse> createRol(
            @Parameter(description = "Datos del rol a crear", required = true)
            @Valid @RequestBody CreateRolRequest request) {
        RolResponse createdRol = rolService.createRol(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRol);
    }

    @Operation(
            summary = "Actualizar un rol existente",
            description = "Actualiza los datos de un rol existente. Los campos no proporcionados no se modifican"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RolResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rol no encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe otro rol con ese nombre"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<RolResponse> updateRol(
            @Parameter(description = "ID único del rol", required = true)
            @PathVariable String id,
            @Parameter(description = "Datos actualizados del rol", required = true)
            @Valid @RequestBody UpdateRolRequest request) {
        RolResponse updatedRol = rolService.updateRol(id, request);
        return ResponseEntity.ok(updatedRol);
    }

    @Operation(
            summary = "Eliminar un rol (soft delete)",
            description = "Marca un rol como eliminado sin borrarlo físicamente de la base de datos. Puede ser restaurado posteriormente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Rol eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rol no encontrado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteRol(
            @Parameter(description = "ID único del rol", required = true)
            @PathVariable String id) {
        rolService.softDeleteRol(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Restaurar un rol eliminado",
            description = "Restaura un rol que fue eliminado previamente con soft delete"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol restaurado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RolResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rol no encontrado o no estaba eliminado"
            )
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<RolResponse> restoreRol(
            @Parameter(description = "ID único del rol", required = true)
            @PathVariable String id) {
        RolResponse restoredRol = rolService.restoreRol(id);
        return ResponseEntity.ok(restoredRol);
    }

    @Operation(
            summary = "Eliminar permanentemente un rol",
            description = "Elimina físicamente un rol de la base de datos. ADVERTENCIA: Esta acción no se puede deshacer"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Rol eliminado permanentemente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rol no encontrado"
            )
    })
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteRol(
            @Parameter(description = "ID único del rol", required = true)
            @PathVariable String id) {
        rolService.permanentDeleteRol(id);
        return ResponseEntity.noContent().build();
    }
}
