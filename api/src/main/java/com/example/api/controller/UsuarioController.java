package com.example.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
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

import com.example.api.dto.request.CreateUsuarioRequest;
import com.example.api.dto.request.UpdateUsuarioRequest;
import com.example.api.dto.response.UsuarioResponse;
import com.example.api.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de usuarios.
 * Proporciona endpoints para CRUD completo de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios del sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param usuarioService Servicio de usuarios
     */
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Listar todos los usuarios activos con paginación",
            description = "Obtiene una lista paginada de todos los usuarios que no han sido eliminados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<?> getAllUsuarios(
            @Parameter(description = "Número de página (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Si es false, retorna lista sin paginación", example = "true")
            @RequestParam(defaultValue = "true") boolean paginated) {
        
        if (paginated) {
            Page<UsuarioResponse> usuarios = usuarioService.getAllUsuarios(page, size);
            return ResponseEntity.ok(usuarios);
        } else {
            List<UsuarioResponse> usuarios = usuarioService.getAllUsuarios();
            return ResponseEntity.ok(usuarios);
        }
    }

    @Operation(
            summary = "Listar usuarios eliminados",
            description = "Obtiene una lista de todos los usuarios que han sido eliminados (soft delete)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios eliminados obtenida exitosamente"
            )
    })
    @GetMapping("/deleted")
    public ResponseEntity<List<UsuarioResponse>> getAllDeletedUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.getAllDeletedUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Obtiene un usuario específico utilizando su ID único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> getUsuarioById(
            @Parameter(description = "ID único del usuario", required = true)
            @PathVariable String id) {
        UsuarioResponse usuario = usuarioService.getUsuarioById(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(
            summary = "Buscar usuario por username",
            description = "Busca un usuario específico por su nombre de usuario exacto"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @GetMapping("/search/username")
    public ResponseEntity<UsuarioResponse> getUsuarioByUsername(
            @Parameter(description = "Username exacto a buscar", required = true)
            @RequestParam String username) {
        UsuarioResponse usuario = usuarioService.getUsuarioByUsername(username);
        return ResponseEntity.ok(usuario);
    }

    @Operation(
            summary = "Buscar usuario por email",
            description = "Busca un usuario específico por su email exacto"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> getUsuarioByEmail(
            @Parameter(description = "Email exacto a buscar", required = true)
            @PathVariable String email) {
        UsuarioResponse usuario = usuarioService.getUsuarioByEmail(email);
        return ResponseEntity.ok(usuario);
    }

    @Operation(
            summary = "Obtener usuarios por rol",
            description = "Obtiene todos los usuarios que tienen un rol específico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida exitosamente"
            )
    })
    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<UsuarioResponse>> getUsuariosByRol(
            @Parameter(description = "ID del rol", required = true)
            @PathVariable String rolId) {
        List<UsuarioResponse> usuarios = usuarioService.getUsuariosByRol(rolId);
        return ResponseEntity.ok(usuarios);
    }

    @Operation(
            summary = "Obtener usuarios activos",
            description = "Obtiene todos los usuarios con estado activo = true"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios activos obtenida exitosamente"
            )
    })
    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponse>> getUsuariosActivos() {
        List<UsuarioResponse> usuarios = usuarioService.getUsuariosActivos();
        return ResponseEntity.ok(usuarios);
    }

    @Operation(
            summary = "Buscar usuarios por username (búsqueda parcial)",
            description = "Busca usuarios que contengan el texto especificado en su username"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda realizada exitosamente"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<List<UsuarioResponse>> searchUsuarios(
            @Parameter(description = "Texto a buscar en el username", required = true)
            @RequestParam String username) {
        List<UsuarioResponse> usuarios = usuarioService.searchUsuariosByUsername(username);
        return ResponseEntity.ok(usuarios);
    }

    @Operation(
            summary = "Buscar usuarios por nombre (búsqueda parcial)",
            description = "Busca usuarios que contengan el texto especificado en su nombre completo"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda realizada exitosamente"
            )
    })
    @GetMapping("/search/nombre")
    public ResponseEntity<List<UsuarioResponse>> searchUsuariosByNombre(
            @Parameter(description = "Texto a buscar en el nombre", required = true)
            @RequestParam String nombre) {
        List<UsuarioResponse> usuarios = usuarioService.searchUsuariosByNombre(nombre);
        return ResponseEntity.ok(usuarios);
    }

    @Operation(
            summary = "Crear un nuevo usuario",
            description = "Crea un nuevo usuario en el sistema con los datos proporcionados. La contraseña será encriptada automáticamente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ya existe un usuario con ese username o email"
            )
    })
    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuario(
            @Parameter(description = "Datos del usuario a crear", required = true)
            @Valid @RequestBody CreateUsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.createUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @Operation(
            summary = "Actualizar un usuario existente",
            description = "Actualiza los datos de un usuario existente. Todos los campos son opcionales."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El nuevo username o email ya está en uso"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(
            @Parameter(description = "ID del usuario a actualizar", required = true)
            @PathVariable String id,
            @Parameter(description = "Datos actualizados del usuario", required = true)
            @Valid @RequestBody UpdateUsuarioRequest request) {
        UsuarioResponse usuario = usuarioService.updateUsuario(id, request);
        return ResponseEntity.ok(usuario);
    }

    @Operation(
            summary = "Eliminar usuario (soft delete)",
            description = "Realiza una eliminación lógica del usuario, estableciendo la fecha de eliminación"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(
            @Parameter(description = "ID del usuario a eliminar", required = true)
            @PathVariable String id) {
        usuarioService.softDeleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Restaurar usuario eliminado",
            description = "Restaura un usuario que fue eliminado lógicamente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario restaurado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado o no estaba eliminado"
            )
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<UsuarioResponse> restoreUsuario(
            @Parameter(description = "ID del usuario a restaurar", required = true)
            @PathVariable String id) {
        UsuarioResponse usuario = usuarioService.restoreUsuario(id);
        return ResponseEntity.ok(usuario);
    }

    @Operation(
            summary = "Eliminar usuario permanentemente",
            description = "Elimina definitivamente un usuario de la base de datos. ADVERTENCIA: Esta operación no se puede deshacer."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado permanentemente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteUsuario(
            @Parameter(description = "ID del usuario a eliminar permanentemente", required = true)
            @PathVariable String id) {
        usuarioService.permanentDeleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
