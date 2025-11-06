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

import com.example.api.dto.request.CreateTipoEvaluacionRequest;
import com.example.api.dto.request.UpdateTipoEvaluacionRequest;
import com.example.api.dto.response.TipoEvaluacionResponse;
import com.example.api.service.TipoEvaluacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de tipos de evaluación.
 */
@RestController
@RequestMapping("/api/tipos-evaluacion")
@Tag(name = "Tipos de Evaluación", description = "API para gestión de tipos de evaluación del sistema educativo")
public class TipoEvaluacionController {

    private final TipoEvaluacionService tipoEvaluacionService;

    public TipoEvaluacionController(TipoEvaluacionService tipoEvaluacionService) {
        this.tipoEvaluacionService = tipoEvaluacionService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los tipos de evaluación", description = "Obtiene una lista paginada de todos los tipos de evaluación activos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tipos de evaluación obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<TipoEvaluacionResponse>> getAllTiposEvaluacion(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(tipoEvaluacionService.getAllTiposEvaluacion(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo de evaluación por ID", description = "Obtiene un tipo de evaluación específico por su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de evaluación encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoEvaluacionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de evaluación no encontrado")
    })
    public ResponseEntity<TipoEvaluacionResponse> getTipoEvaluacionById(
            @Parameter(description = "ID del tipo de evaluación") @PathVariable String id) {
        return ResponseEntity.ok(tipoEvaluacionService.getTipoEvaluacionById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar tipo de evaluación por nombre", 
               description = "Busca un tipo de evaluación por su nombre usando búsqueda secuencial (apropiada para listas pequeñas)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de evaluación encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoEvaluacionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de evaluación no encontrado")
    })
    public ResponseEntity<TipoEvaluacionResponse> searchByNombre(
            @Parameter(description = "Nombre del tipo de evaluación a buscar") @RequestParam String nombre) {
        return ResponseEntity.ok(tipoEvaluacionService.searchByNombre(nombre));
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar tipos de evaluación eliminados", description = "Obtiene una lista de todos los tipos de evaluación eliminados (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tipos de evaluación eliminados obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<TipoEvaluacionResponse>> getTiposEvaluacionDeleted() {
        return ResponseEntity.ok(tipoEvaluacionService.getTiposEvaluacionDeleted());
    }

    @PostMapping
    @Operation(summary = "Crear nuevo tipo de evaluación", description = "Crea un nuevo tipo de evaluación en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de evaluación creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoEvaluacionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<TipoEvaluacionResponse> createTipoEvaluacion(
            @Valid @RequestBody CreateTipoEvaluacionRequest request) {
        TipoEvaluacionResponse created = tipoEvaluacionService.createTipoEvaluacion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo de evaluación", description = "Actualiza un tipo de evaluación existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de evaluación actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoEvaluacionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de evaluación no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<TipoEvaluacionResponse> updateTipoEvaluacion(
            @Parameter(description = "ID del tipo de evaluación") @PathVariable String id,
            @Valid @RequestBody UpdateTipoEvaluacionRequest request) {
        return ResponseEntity.ok(tipoEvaluacionService.updateTipoEvaluacion(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo de evaluación (soft delete)", description = "Realiza una eliminación lógica del tipo de evaluación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tipo de evaluación eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tipo de evaluación no encontrado")
    })
    public ResponseEntity<Void> deleteTipoEvaluacion(
            @Parameter(description = "ID del tipo de evaluación") @PathVariable String id) {
        tipoEvaluacionService.deleteTipoEvaluacion(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Eliminar tipo de evaluación permanentemente", description = "Elimina definitivamente un tipo de evaluación de la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tipo de evaluación eliminado permanentemente"),
            @ApiResponse(responseCode = "404", description = "Tipo de evaluación no encontrado")
    })
    public ResponseEntity<Void> permanentDeleteTipoEvaluacion(
            @Parameter(description = "ID del tipo de evaluación") @PathVariable String id) {
        tipoEvaluacionService.permanentDeleteTipoEvaluacion(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar tipo de evaluación", description = "Restaura un tipo de evaluación previamente eliminado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de evaluación restaurado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TipoEvaluacionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de evaluación no encontrado")
    })
    public ResponseEntity<TipoEvaluacionResponse> restoreTipoEvaluacion(
            @Parameter(description = "ID del tipo de evaluación") @PathVariable String id) {
        return ResponseEntity.ok(tipoEvaluacionService.restoreTipoEvaluacion(id));
    }
}
