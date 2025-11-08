package com.example.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
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

import com.example.api.dto.request.CreateProfesorRequest;
import com.example.api.dto.request.UpdateProfesorRequest;
import com.example.api.dto.response.ProfesorResponse;
import com.example.api.service.ProfesorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de profesores.
 * Proporciona endpoints para crear, leer, actualizar y eliminar profesores del sistema.
 */
@RestController
@RequestMapping("/api/profesores")
@Tag(name = "Profesores", description = "API para gestión de profesores del sistema educativo")
public class ProfesorController {

    private final ProfesorService profesorService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param profesorService Servicio de profesores
     */
    public ProfesorController(ProfesorService profesorService) {
        this.profesorService = profesorService;
    }

    /**
     * Obtiene una lista paginada de todos los profesores activos.
     *
     * @param pageable Configuración de paginación (page, size, sort)
     * @return Página de profesores activos
     */
    @GetMapping
    @Operation(
            summary = "Listar profesores activos",
            description = "Obtiene una lista paginada de todos los profesores activos (no eliminados) del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de profesores obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    public ResponseEntity<Page<ProfesorResponse>> getAllProfesores(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProfesorResponse> profesores = profesorService.getAllProfesores(pageable);
        return ResponseEntity.ok(profesores);
    }

    /**
     * Obtiene un profesor específico por su ID.
     *
     * @param id El ID del profesor a buscar
     * @return El profesor encontrado
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener profesor por ID",
            description = "Busca y retorna un profesor específico utilizando su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profesor encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = ProfesorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profesor no encontrado con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<ProfesorResponse> getProfesorById(
            @Parameter(description = "ID único del profesor", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        ProfesorResponse profesor = profesorService.getProfesorById(id);
        return ResponseEntity.ok(profesor);
    }

    /**
     * Busca profesores por especialidad (búsqueda parcial).
     *
     * @param especialidad Texto a buscar en el campo especialidad
     * @return Lista de profesores que coinciden con la búsqueda
     */
    @GetMapping("/search")
    @Operation(
            summary = "Buscar profesores por especialidad",
            description = "Realiza una búsqueda parcial de profesores por su campo de especialidad"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda completada exitosamente",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<List<ProfesorResponse>> searchByEspecialidad(
            @Parameter(description = "Texto a buscar en la especialidad", example = "Matemáticas")
            @RequestParam String especialidad) {
        List<ProfesorResponse> profesores = profesorService.searchByEspecialidad(especialidad);
        return ResponseEntity.ok(profesores);
    }

    /**
     * Obtiene solo los profesores activos.
     *
     * @return Lista de profesores activos
     */
    @GetMapping("/activos")
    @Operation(
            summary = "Listar profesores activos",
            description = "Obtiene una lista completa de profesores con estado activo=true"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de profesores activos obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<List<ProfesorResponse>> getProfesoresActivos() {
        List<ProfesorResponse> profesores = profesorService.getProfesoresActivos();
        return ResponseEntity.ok(profesores);
    }

    /**
     * Obtiene profesores eliminados (soft delete).
     *
     * @return Lista de profesores eliminados
     */
    @GetMapping("/deleted")
    @Operation(
            summary = "Listar profesores eliminados",
            description = "Obtiene una lista de profesores que han sido eliminados lógicamente (soft delete)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de profesores eliminados obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<List<ProfesorResponse>> getProfesoresDeleted() {
        List<ProfesorResponse> profesores = profesorService.getProfesoresDeleted();
        return ResponseEntity.ok(profesores);
    }

    /**
     * Crea un nuevo profesor.
     *
     * @param request Datos del profesor a crear
     * @return El profesor creado con código de estado 201
     */
    @PostMapping
    @Operation(
            summary = "Crear nuevo profesor",
            description = "Registra un nuevo profesor en el sistema asociándolo a un usuario existente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Profesor creado exitosamente",
                    content = @Content(schema = @Schema(implementation = ProfesorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<ProfesorResponse> createProfesor(
            @Parameter(description = "Datos del profesor a crear")
            @Valid @RequestBody CreateProfesorRequest request) {
        ProfesorResponse profesor = profesorService.createProfesor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(profesor);
    }

    /**
     * Actualiza un profesor existente.
     *
     * @param id El ID del profesor a actualizar
     * @param request Datos a actualizar
     * @return El profesor actualizado
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar profesor",
            description = "Actualiza los datos de un profesor existente. Solo se actualizan los campos proporcionados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profesor actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = ProfesorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profesor no encontrado con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<ProfesorResponse> updateProfesor(
            @Parameter(description = "ID único del profesor", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id,
            @Parameter(description = "Datos del profesor a actualizar")
            @Valid @RequestBody UpdateProfesorRequest request) {
        ProfesorResponse profesor = profesorService.updateProfesor(id, request);
        return ResponseEntity.ok(profesor);
    }

    /**
     * Realiza eliminación suave (soft delete) de un profesor.
     *
     * @param id El ID del profesor a eliminar
     * @return Respuesta sin contenido (204)
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar profesor (soft delete)",
            description = "Realiza una eliminación lógica del profesor, marcándolo como eliminado pero manteniendo sus datos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Profesor eliminado exitosamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profesor no encontrado con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteProfesor(
            @Parameter(description = "ID único del profesor", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        profesorService.deleteProfesor(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina permanentemente un profesor de la base de datos.
     *
     * @param id El ID del profesor a eliminar
     * @return Respuesta sin contenido (204)
     */
    @DeleteMapping("/{id}/permanent")
    @Operation(
            summary = "Eliminar profesor permanentemente",
            description = "Elimina completamente un profesor de la base de datos. Esta acción no se puede deshacer."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Profesor eliminado permanentemente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profesor no encontrado con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> permanentDeleteProfesor(
            @Parameter(description = "ID único del profesor", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        profesorService.permanentDeleteProfesor(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Restaura un profesor eliminado.
     *
     * @param id El ID del profesor a restaurar
     * @return El profesor restaurado
     */
    @PatchMapping("/{id}/restore")
    @Operation(
            summary = "Restaurar profesor eliminado",
            description = "Restaura un profesor que fue eliminado lógicamente, reactivándolo en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profesor restaurado exitosamente",
                    content = @Content(schema = @Schema(implementation = ProfesorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Profesor no encontrado con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<ProfesorResponse> restoreProfesor(
            @Parameter(description = "ID único del profesor", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        ProfesorResponse profesor = profesorService.restoreProfesor(id);
        return ResponseEntity.ok(profesor);
    }
}
