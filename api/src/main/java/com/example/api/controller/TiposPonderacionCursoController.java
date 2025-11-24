package com.example.api.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.dto.request.CreateTiposPonderacionCursoRequest;
import com.example.api.dto.request.UpdateTiposPonderacionCursoRequest;
import com.example.api.dto.response.TiposPonderacionCursoResponse;
import com.example.api.service.TiposPonderacionCursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tipos-ponderacion")
@Tag(name = "Tipos de Ponderación", description = "API para gestión de tipos de ponderación de cursos")
public class TiposPonderacionCursoController {

    private final TiposPonderacionCursoService service;

    public TiposPonderacionCursoController(TiposPonderacionCursoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos los tipos de ponderación", description = "Obtiene una lista paginada de todos los tipos de ponderación activos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<TiposPonderacionCursoResponse>> getAll(
            @Parameter(description = "Número de página (inicia en 0)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo de ponderación por ID", description = "Obtiene un tipo de ponderación específico por su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de ponderación encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TiposPonderacionCursoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de ponderación no encontrado")
    })
    public ResponseEntity<TiposPonderacionCursoResponse> getById(
            @Parameter(description = "ID del tipo de ponderación") @PathVariable String id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Listar tipos de ponderación por curso", description = "Obtiene todos los tipos de ponderación de un curso específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<TiposPonderacionCursoResponse>> getByCursoId(
            @Parameter(description = "ID del curso") @PathVariable String cursoId) {
        return ResponseEntity.ok(service.getByCursoId(cursoId));
    }

    @PostMapping
    @Operation(summary = "Crear tipo de ponderación", description = "Crea un nuevo tipo de ponderación para un curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TiposPonderacionCursoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public ResponseEntity<TiposPonderacionCursoResponse> create(
            @Valid @RequestBody CreateTiposPonderacionCursoRequest request) {
        TiposPonderacionCursoResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo de ponderación", description = "Actualiza un tipo de ponderación existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TiposPonderacionCursoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tipo de ponderación no encontrado")
    })
    public ResponseEntity<TiposPonderacionCursoResponse> update(
            @Parameter(description = "ID del tipo de ponderación") @PathVariable String id,
            @Valid @RequestBody UpdateTiposPonderacionCursoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tipo de ponderación", description = "Realiza un borrado lógico del tipo de ponderación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tipo de ponderación no encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del tipo de ponderación") @PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
