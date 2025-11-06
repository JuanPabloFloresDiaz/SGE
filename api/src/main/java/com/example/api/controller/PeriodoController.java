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

import com.example.api.dto.request.CreatePeriodoRequest;
import com.example.api.dto.request.UpdatePeriodoRequest;
import com.example.api.dto.response.PeriodoResponse;
import com.example.api.service.PeriodoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de periodos académicos.
 * Proporciona endpoints para crear, leer, actualizar y eliminar periodos del sistema.
 */
@RestController
@RequestMapping("/api/periodos")
@Tag(name = "Periodos", description = "API para gestión de periodos académicos del sistema educativo")
public class PeriodoController {

    private final PeriodoService periodoService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param periodoService Servicio de periodos
     */
    public PeriodoController(PeriodoService periodoService) {
        this.periodoService = periodoService;
    }

    /**
     * Obtiene una lista paginada de todos los periodos activos.
     *
     * @param pageable Configuración de paginación (page, size, sort)
     * @return Página de periodos activos
     */
    @GetMapping
    @Operation(
            summary = "Listar periodos activos",
            description = "Obtiene una lista paginada de todos los periodos activos (no eliminados) del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de periodos obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    public ResponseEntity<Page<PeriodoResponse>> getAllPeriodos(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<PeriodoResponse> periodos = periodoService.getAllPeriodos(pageable);
        return ResponseEntity.ok(periodos);
    }

    /**
     * Obtiene un periodo específico por su ID.
     *
     * @param id El ID del periodo a buscar
     * @return El periodo encontrado
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener periodo por ID",
            description = "Busca y retorna un periodo específico utilizando su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Periodo encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = PeriodoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Periodo no encontrado con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<PeriodoResponse> getPeriodoById(
            @Parameter(description = "ID único del periodo", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        PeriodoResponse periodo = periodoService.getPeriodoById(id);
        return ResponseEntity.ok(periodo);
    }

    /**
     * Busca periodos por nombre (búsqueda parcial).
     *
     * @param nombre Texto a buscar en el campo nombre
     * @return Lista de periodos que coinciden con la búsqueda
     */
    @GetMapping("/search")
    @Operation(
            summary = "Buscar periodos por nombre",
            description = "Realiza una búsqueda parcial de periodos por su nombre"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Búsqueda completada exitosamente",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<List<PeriodoResponse>> searchByNombre(
            @Parameter(description = "Texto a buscar en el nombre", example = "2024")
            @RequestParam String nombre) {
        List<PeriodoResponse> periodos = periodoService.searchByNombre(nombre);
        return ResponseEntity.ok(periodos);
    }

    /**
     * Obtiene solo los periodos activos.
     *
     * @return Lista de periodos activos
     */
    @GetMapping("/activos")
    @Operation(
            summary = "Listar periodos activos",
            description = "Obtiene una lista completa de periodos con estado activo=true"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de periodos activos obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<List<PeriodoResponse>> getPeriodosActivos() {
        List<PeriodoResponse> periodos = periodoService.getPeriodosActivos();
        return ResponseEntity.ok(periodos);
    }

    /**
     * Obtiene el periodo actual basado en la fecha del sistema.
     *
     * @return El periodo actual
     */
    @GetMapping("/actual")
    @Operation(
            summary = "Obtener periodo actual",
            description = "Retorna el periodo académico que está activo en la fecha actual (fecha actual entre fechaInicio y fechaFin)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Periodo actual encontrado exitosamente",
                    content = @Content(schema = @Schema(implementation = PeriodoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No existe un periodo actual activo",
                    content = @Content
            )
    })
    public ResponseEntity<PeriodoResponse> getPeriodoActual() {
        PeriodoResponse periodo = periodoService.getPeriodoActual();
        return ResponseEntity.ok(periodo);
    }

    /**
     * Obtiene periodos eliminados (soft delete).
     *
     * @return Lista de periodos eliminados
     */
    @GetMapping("/deleted")
    @Operation(
            summary = "Listar periodos eliminados",
            description = "Obtiene una lista de periodos que han sido eliminados lógicamente (soft delete)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de periodos eliminados obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = List.class))
            )
    })
    public ResponseEntity<List<PeriodoResponse>> getPeriodosDeleted() {
        List<PeriodoResponse> periodos = periodoService.getPeriodosDeleted();
        return ResponseEntity.ok(periodos);
    }

    /**
     * Crea un nuevo periodo.
     *
     * @param request Datos del periodo a crear
     * @return El periodo creado con código de estado 201
     */
    @PostMapping
    @Operation(
            summary = "Crear nuevo periodo",
            description = "Registra un nuevo periodo académico en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Periodo creado exitosamente",
                    content = @Content(schema = @Schema(implementation = PeriodoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o fecha de fin anterior a fecha de inicio",
                    content = @Content
            )
    })
    public ResponseEntity<PeriodoResponse> createPeriodo(
            @Parameter(description = "Datos del periodo a crear")
            @Valid @RequestBody CreatePeriodoRequest request) {
        PeriodoResponse periodo = periodoService.createPeriodo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(periodo);
    }

    /**
     * Actualiza un periodo existente.
     *
     * @param id El ID del periodo a actualizar
     * @param request Datos a actualizar
     * @return El periodo actualizado
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar periodo",
            description = "Actualiza los datos de un periodo existente. Solo se actualizan los campos proporcionados."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Periodo actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = PeriodoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o conflicto de fechas",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Periodo no encontrado con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<PeriodoResponse> updatePeriodo(
            @Parameter(description = "ID único del periodo", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id,
            @Parameter(description = "Datos del periodo a actualizar")
            @Valid @RequestBody UpdatePeriodoRequest request) {
        PeriodoResponse periodo = periodoService.updatePeriodo(id, request);
        return ResponseEntity.ok(periodo);
    }

    /**
     * Realiza eliminación suave (soft delete) de un periodo.
     *
     * @param id El ID del periodo a eliminar
     * @return Respuesta sin contenido (204)
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar periodo (soft delete)",
            description = "Realiza una eliminación lógica del periodo, marcándolo como eliminado pero manteniendo sus datos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Periodo eliminado exitosamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Periodo no encontrado con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deletePeriodo(
            @Parameter(description = "ID único del periodo", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        periodoService.deletePeriodo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina permanentemente un periodo de la base de datos.
     *
     * @param id El ID del periodo a eliminar
     * @return Respuesta sin contenido (204)
     */
    @DeleteMapping("/{id}/permanent")
    @Operation(
            summary = "Eliminar periodo permanentemente",
            description = "Elimina completamente un periodo de la base de datos. Esta acción no se puede deshacer."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Periodo eliminado permanentemente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Periodo no encontrado con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> permanentDeletePeriodo(
            @Parameter(description = "ID único del periodo", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        periodoService.permanentDeletePeriodo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Restaura un periodo eliminado.
     *
     * @param id El ID del periodo a restaurar
     * @return El periodo restaurado
     */
    @PatchMapping("/{id}/restore")
    @Operation(
            summary = "Restaurar periodo eliminado",
            description = "Restaura un periodo que fue eliminado lógicamente, reactivándolo en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Periodo restaurado exitosamente",
                    content = @Content(schema = @Schema(implementation = PeriodoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Periodo no encontrado con el ID proporcionado",
                    content = @Content
            )
    })
    public ResponseEntity<PeriodoResponse> restorePeriodo(
            @Parameter(description = "ID único del periodo", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        PeriodoResponse periodo = periodoService.restorePeriodo(id);
        return ResponseEntity.ok(periodo);
    }
}
