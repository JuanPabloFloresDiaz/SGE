package com.example.api.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.example.api.dto.request.CreateClaseRequest;
import com.example.api.dto.request.UpdateClaseRequest;
import com.example.api.dto.response.ClaseResponse;
import com.example.api.service.ClaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de clases.
 */
@RestController
@RequestMapping("/api/clases")
@Tag(name = "Clases", description = "API para gestión de clases del sistema educativo")
public class ClaseController {

    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las clases", description = "Obtiene una lista paginada de todas las clases activas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clases obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ClaseResponse>> getAllClases(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(claseService.getAllClases(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener clase por ID", description = "Obtiene una clase específica por su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<ClaseResponse> getClaseById(
            @Parameter(description = "ID de la clase") @PathVariable String id) {
        return ResponseEntity.ok(claseService.getClaseById(id));
    }

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Listar clases por curso", description = "Obtiene todas las clases de un curso específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clases obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ClaseResponse>> getClasesByCursoId(
            @Parameter(description = "ID del curso") @PathVariable String cursoId) {
        return ResponseEntity.ok(claseService.getClasesByCursoId(cursoId));
    }

    @GetMapping("/fecha/{fecha}")
    @Operation(summary = "Listar clases por fecha", description = "Obtiene todas las clases de una fecha específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clases obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ClaseResponse>> getClasesByFecha(
            @Parameter(description = "Fecha de las clases (formato: yyyy-MM-dd)") 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(claseService.getClasesByFecha(fecha));
    }

    @GetMapping("/ordenadas")
    @Operation(summary = "Listar clases ordenadas", description = "Obtiene todas las clases ordenadas por fecha y hora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clases obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ClaseResponse>> getClasesOrdenadas() {
        return ResponseEntity.ok(claseService.getClasesOrdenadas());
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar clases eliminadas", description = "Obtiene todas las clases que han sido eliminadas (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clases eliminadas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ClaseResponse>> getClasesDeleted() {
        return ResponseEntity.ok(claseService.getClasesDeleted());
    }

    @PostMapping
    @Operation(summary = "Crear nueva clase", description = "Registra una nueva clase en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Clase creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ClaseResponse> createClase(@Valid @RequestBody CreateClaseRequest request) {
        ClaseResponse createdClase = claseService.createClase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClase);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar clase", description = "Actualiza los datos de una clase existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ClaseResponse> updateClase(
            @Parameter(description = "ID de la clase") @PathVariable String id,
            @Valid @RequestBody UpdateClaseRequest request) {
        return ResponseEntity.ok(claseService.updateClase(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar clase (soft delete)", description = "Realiza una eliminación lógica de la clase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Clase eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<Void> deleteClase(
            @Parameter(description = "ID de la clase") @PathVariable String id) {
        claseService.deleteClase(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Eliminar clase permanentemente", description = "Elimina definitivamente una clase de la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Clase eliminada permanentemente"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<Void> permanentDeleteClase(
            @Parameter(description = "ID de la clase") @PathVariable String id) {
        claseService.permanentDeleteClase(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar clase", description = "Restaura una clase previamente eliminada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase restaurada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<ClaseResponse> restoreClase(
            @Parameter(description = "ID de la clase") @PathVariable String id) {
        return ResponseEntity.ok(claseService.restoreClase(id));
    }
}
