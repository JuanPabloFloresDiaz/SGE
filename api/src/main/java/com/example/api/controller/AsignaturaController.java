package com.example.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import com.example.api.dto.request.CreateAsignaturaRequest;
import com.example.api.dto.request.UpdateAsignaturaRequest;
import com.example.api.dto.response.AsignaturaResponse;
import com.example.api.service.AsignaturaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de asignaturas.
 * Proporciona endpoints para crear, leer, actualizar y eliminar asignaturas del sistema.
 */
@RestController
@RequestMapping("/api/asignaturas")
@Tag(name = "Asignaturas", description = "API para gestión de asignaturas del sistema educativo")
public class AsignaturaController {

    private final AsignaturaService asignaturaService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param asignaturaService Servicio de asignaturas
     */
    public AsignaturaController(AsignaturaService asignaturaService) {
        this.asignaturaService = asignaturaService;
    }

    /**
     * Obtiene una lista paginada de todas las asignaturas activas.
     *
     * @param pageable Configuración de paginación (page, size, sort)
     * @return Página de asignaturas activas
     */
    @GetMapping
    @Operation(
            summary = "Listar asignaturas activas",
            description = "Obtiene una lista paginada de todas las asignaturas activas (no eliminadas) del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de asignaturas obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    public ResponseEntity<Page<AsignaturaResponse>> getAllAsignaturas(
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        Page<AsignaturaResponse> asignaturas = asignaturaService.getAllAsignaturas(pageable);
        return ResponseEntity.ok(asignaturas);
    }

    /**
     * Obtiene una asignatura específica por su ID.
     *
     * @param id El ID de la asignatura a buscar
     * @return La asignatura encontrada
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener asignatura por ID",
            description = "Busca y retorna una asignatura específica utilizando su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Asignatura encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = AsignaturaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Asignatura no encontrada con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<AsignaturaResponse> getAsignaturaById(
            @Parameter(description = "ID único de la asignatura", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        AsignaturaResponse asignatura = asignaturaService.getAsignaturaById(id);
        return ResponseEntity.ok(asignatura);
    }

    /**
     * Busca una asignatura por su código único.
     *
     * @param codigo Código de la asignatura
     * @return La asignatura encontrada
     */
    @GetMapping("/codigo/{codigo}")
    @Operation(
            summary = "Obtener asignatura por código",
            description = "Busca y retorna una asignatura utilizando su código único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Asignatura encontrada exitosamente",
                    content = @Content(schema = @Schema(implementation = AsignaturaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Asignatura no encontrada con el código proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<AsignaturaResponse> getAsignaturaByCodigo(
            @Parameter(description = "Código único de la asignatura", example = "MAT-101")
            @PathVariable String codigo) {
        AsignaturaResponse asignatura = asignaturaService.getAsignaturaByCodigo(codigo);
        return ResponseEntity.ok(asignatura);
    }

    /**
     * Busca asignaturas por nombre (búsqueda parcial).
     *
     * @param nombre Texto a buscar en el campo nombre
     * @return Lista de asignaturas que coinciden con la búsqueda
     */
    @GetMapping("/search")
    @Operation(
            summary = "Buscar asignaturas por nombre",
            description = "Realiza una búsqueda parcial de asignaturas por su nombre"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda completada exitosamente",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<List<AsignaturaResponse>> searchByNombre(
            @Parameter(description = "Texto a buscar en el nombre", example = "Matemáticas")
            @RequestParam String nombre) {
        List<AsignaturaResponse> asignaturas = asignaturaService.searchByNombre(nombre);
        return ResponseEntity.ok(asignaturas);
    }

    /**
     * Obtiene asignaturas eliminadas (soft delete).
     *
     * @return Lista de asignaturas eliminadas
     */
    @GetMapping("/deleted")
    @Operation(
            summary = "Listar asignaturas eliminadas",
            description = "Obtiene una lista de asignaturas que han sido eliminadas lógicamente (soft delete)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de asignaturas eliminadas obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<List<AsignaturaResponse>> getAsignaturasDeleted() {
        List<AsignaturaResponse> asignaturas = asignaturaService.getAsignaturasDeleted();
        return ResponseEntity.ok(asignaturas);
    }

    /**
     * Crea una nueva asignatura.
     *
     * @param request Datos de la asignatura a crear
     * @return La asignatura creada con código de estado 201
     */
    @PostMapping
    @Operation(
            summary = "Crear nueva asignatura",
            description = "Registra una nueva asignatura en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Asignatura creada exitosamente",
                    content = @Content(schema = @Schema(implementation = AsignaturaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o código duplicado",
                    content = @Content
            )
    })
    public ResponseEntity<AsignaturaResponse> createAsignatura(
            @Parameter(description = "Datos de la asignatura a crear")
            @Valid @RequestBody CreateAsignaturaRequest request) {
        AsignaturaResponse asignatura = asignaturaService.createAsignatura(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(asignatura);
    }

    /**
     * Actualiza una asignatura existente.
     *
     * @param id El ID de la asignatura a actualizar
     * @param request Datos a actualizar
     * @return La asignatura actualizada
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar asignatura",
            description = "Actualiza los datos de una asignatura existente. Solo se actualizan los campos proporcionados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Asignatura actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = AsignaturaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o código duplicado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Asignatura no encontrada con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<AsignaturaResponse> updateAsignatura(
            @Parameter(description = "ID único de la asignatura", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id,
            @Parameter(description = "Datos de la asignatura a actualizar")
            @Valid @RequestBody UpdateAsignaturaRequest request) {
        AsignaturaResponse asignatura = asignaturaService.updateAsignatura(id, request);
        return ResponseEntity.ok(asignatura);
    }

    /**
     * Realiza eliminación suave (soft delete) de una asignatura.
     *
     * @param id El ID de la asignatura a eliminar
     * @return Respuesta sin contenido (204)
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar asignatura (soft delete)",
            description = "Realiza una eliminación lógica de la asignatura, marcándola como eliminada pero manteniendo sus datos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Asignatura eliminada exitosamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Asignatura no encontrada con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteAsignatura(
            @Parameter(description = "ID único de la asignatura", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        asignaturaService.deleteAsignatura(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina permanentemente una asignatura de la base de datos.
     *
     * @param id El ID de la asignatura a eliminar
     * @return Respuesta sin contenido (204)
     */
    @DeleteMapping("/{id}/permanent")
    @Operation(
            summary = "Eliminar asignatura permanentemente",
            description = "Elimina completamente una asignatura de la base de datos. Esta acción no se puede deshacer."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Asignatura eliminada permanentemente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Asignatura no encontrada con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> permanentDeleteAsignatura(
            @Parameter(description = "ID único de la asignatura", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        asignaturaService.permanentDeleteAsignatura(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Restaura una asignatura eliminada.
     *
     * @param id El ID de la asignatura a restaurar
     * @return La asignatura restaurada
     */
    @PatchMapping("/{id}/restore")
    @Operation(
            summary = "Restaurar asignatura eliminada",
            description = "Restaura una asignatura que fue eliminada lógicamente, reactivándola en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Asignatura restaurada exitosamente",
                    content = @Content(schema = @Schema(implementation = AsignaturaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Asignatura no encontrada con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<AsignaturaResponse> restoreAsignatura(
            @Parameter(description = "ID único de la asignatura", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        AsignaturaResponse asignatura = asignaturaService.restoreAsignatura(id);
        return ResponseEntity.ok(asignatura);
    }
}
