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

import com.example.api.dto.request.CreateUnidadRequest;
import com.example.api.dto.request.UpdateUnidadRequest;
import com.example.api.dto.response.UnidadResponse;
import com.example.api.service.UnidadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de unidades didácticas.
 */
@RestController
@RequestMapping("/api/unidades")
@Tag(name = "Unidades", description = "API para gestión de unidades didácticas del sistema educativo")
public class UnidadController {

    private final UnidadService unidadService;

    public UnidadController(UnidadService unidadService) {
        this.unidadService = unidadService;
    }

    @GetMapping
    @Operation(summary = "Listar unidades", description = "Obtiene una lista paginada de todas las unidades activas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de unidades obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<UnidadResponse>> getAllUnidades(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(unidadService.getAllUnidades(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener unidad por ID", description = "Busca y retorna una unidad específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidad encontrada",
                    content = @Content(schema = @Schema(implementation = UnidadResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada", content = @Content)
    })
    public ResponseEntity<UnidadResponse> getUnidadById(
            @Parameter(description = "ID de la unidad") @PathVariable String id) {
        return ResponseEntity.ok(unidadService.getUnidadById(id));
    }

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Unidades de un curso", description = "Obtiene todas las unidades de un curso específico ordenadas por número")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de unidades del curso",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<UnidadResponse>> getUnidadesByCurso(
            @Parameter(description = "ID del curso") @PathVariable String cursoId) {
        return ResponseEntity.ok(unidadService.getUnidadesByCursoId(cursoId));
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar unidades eliminadas", description = "Obtiene todas las unidades que han sido eliminadas lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de unidades eliminadas",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<UnidadResponse>> getUnidadesDeleted() {
        return ResponseEntity.ok(unidadService.getUnidadesDeleted());
    }

    @PostMapping
    @Operation(summary = "Crear unidad", description = "Crea una nueva unidad didáctica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Unidad creada exitosamente",
                    content = @Content(schema = @Schema(implementation = UnidadResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = @Content)
    })
    public ResponseEntity<UnidadResponse> createUnidad(@Valid @RequestBody CreateUnidadRequest request) {
        UnidadResponse created = unidadService.createUnidad(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar unidad", description = "Actualiza los datos de una unidad existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidad actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = UnidadResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    public ResponseEntity<UnidadResponse> updateUnidad(
            @Parameter(description = "ID de la unidad") @PathVariable String id,
            @Valid @RequestBody UpdateUnidadRequest request) {
        return ResponseEntity.ok(unidadService.updateUnidad(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar unidad (soft delete)", description = "Realiza una eliminación lógica de la unidad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Unidad eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada", content = @Content)
    })
    public ResponseEntity<Void> deleteUnidad(
            @Parameter(description = "ID de la unidad") @PathVariable String id) {
        unidadService.deleteUnidad(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Eliminar unidad permanentemente", description = "Elimina permanentemente una unidad de la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Unidad eliminada permanentemente"),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada", content = @Content)
    })
    public ResponseEntity<Void> permanentDeleteUnidad(
            @Parameter(description = "ID de la unidad") @PathVariable String id) {
        unidadService.permanentDeleteUnidad(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar unidad eliminada", description = "Restaura una unidad que fue eliminada lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidad restaurada exitosamente",
                    content = @Content(schema = @Schema(implementation = UnidadResponse.class))),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada", content = @Content)
    })
    public ResponseEntity<UnidadResponse> restoreUnidad(
            @Parameter(description = "ID de la unidad") @PathVariable String id) {
        return ResponseEntity.ok(unidadService.restoreUnidad(id));
    }
}
