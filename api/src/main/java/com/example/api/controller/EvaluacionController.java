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

import com.example.api.dto.request.CreateEvaluacionRequest;
import com.example.api.dto.request.UpdateEvaluacionRequest;
import com.example.api.dto.response.EvaluacionResponse;
import com.example.api.service.EvaluacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de evaluaciones.
 */
@RestController
@RequestMapping("/api/evaluaciones")
@Tag(name = "Evaluaciones", description = "API para gestión de evaluaciones del sistema educativo")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    public EvaluacionController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las evaluaciones", description = "Obtiene una lista paginada de todas las evaluaciones activas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de evaluaciones obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<EvaluacionResponse>> getAllEvaluaciones(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(evaluacionService.getAllEvaluaciones(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener evaluación por ID", description = "Obtiene una evaluación específica por su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluación encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EvaluacionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<EvaluacionResponse> getEvaluacionById(
            @Parameter(description = "ID de la evaluación") @PathVariable String id) {
        return ResponseEntity.ok(evaluacionService.getEvaluacionById(id));
    }

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Listar evaluaciones por curso", description = "Obtiene todas las evaluaciones de un curso específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de evaluaciones obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<EvaluacionResponse>> getEvaluacionesByCursoId(
            @Parameter(description = "ID del curso") @PathVariable String cursoId) {
        return ResponseEntity.ok(evaluacionService.getEvaluacionesByCursoId(cursoId));
    }

    @GetMapping("/tipo/{tipoId}")
    @Operation(summary = "Listar evaluaciones por tipo", description = "Obtiene todas las evaluaciones de un tipo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de evaluaciones obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<EvaluacionResponse>> getEvaluacionesByTipoId(
            @Parameter(description = "ID del tipo de evaluación") @PathVariable String tipoId) {
        return ResponseEntity.ok(evaluacionService.getEvaluacionesByTipoId(tipoId));
    }

    @GetMapping("/proximas")
    @Operation(summary = "Listar próximas evaluaciones", 
               description = "Obtiene todas las evaluaciones futuras (fecha >= hoy) que están publicadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de próximas evaluaciones obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<EvaluacionResponse>> getProximasEvaluaciones() {
        return ResponseEntity.ok(evaluacionService.getProximasEvaluaciones());
    }

    @GetMapping("/ordenadas")
    @Operation(summary = "Listar evaluaciones ordenadas por fecha", 
               description = "Obtiene todas las evaluaciones ordenadas por fecha usando el algoritmo Bubble Sort (propósito educativo)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de evaluaciones ordenadas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<EvaluacionResponse>> getEvaluacionesOrdenadas() {
        return ResponseEntity.ok(evaluacionService.getEvaluacionesOrdenadas());
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar evaluaciones eliminadas", description = "Obtiene una lista de todas las evaluaciones eliminadas (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de evaluaciones eliminadas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<EvaluacionResponse>> getEvaluacionesDeleted() {
        return ResponseEntity.ok(evaluacionService.getEvaluacionesDeleted());
    }

    @PostMapping
    @Operation(summary = "Crear nueva evaluación", description = "Crea una nueva evaluación en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evaluación creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EvaluacionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<EvaluacionResponse> createEvaluacion(
            @Valid @RequestBody CreateEvaluacionRequest request) {
        EvaluacionResponse created = evaluacionService.createEvaluacion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evaluación", description = "Actualiza una evaluación existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluación actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EvaluacionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<EvaluacionResponse> updateEvaluacion(
            @Parameter(description = "ID de la evaluación") @PathVariable String id,
            @Valid @RequestBody UpdateEvaluacionRequest request) {
        return ResponseEntity.ok(evaluacionService.updateEvaluacion(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar evaluación (soft delete)", description = "Realiza una eliminación lógica de la evaluación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evaluación eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<Void> deleteEvaluacion(
            @Parameter(description = "ID de la evaluación") @PathVariable String id) {
        evaluacionService.deleteEvaluacion(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Eliminar evaluación permanentemente", description = "Elimina definitivamente una evaluación de la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evaluación eliminada permanentemente"),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<Void> permanentDeleteEvaluacion(
            @Parameter(description = "ID de la evaluación") @PathVariable String id) {
        evaluacionService.permanentDeleteEvaluacion(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar evaluación", description = "Restaura una evaluación previamente eliminada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluación restaurada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EvaluacionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<EvaluacionResponse> restoreEvaluacion(
            @Parameter(description = "ID de la evaluación") @PathVariable String id) {
        return ResponseEntity.ok(evaluacionService.restoreEvaluacion(id));
    }
}
