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
import org.springframework.web.bind.annotation.RestController;

import com.example.api.dto.request.CreateInscripcionRequest;
import com.example.api.dto.request.UpdateInscripcionRequest;
import com.example.api.dto.response.InscripcionResponse;
import com.example.api.model.Inscripcion.EstadoInscripcion;
import com.example.api.service.InscripcionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de inscripciones.
 */
@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "Inscripciones", description = "API para gestión de inscripciones del sistema educativo")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    @GetMapping
    @Operation(summary = "Listar inscripciones", description = "Obtiene una lista paginada de todas las inscripciones activas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<InscripcionResponse>> getAllInscripciones(
            @PageableDefault(size = 20, sort = "fechaInscripcion") Pageable pageable) {
        return ResponseEntity.ok(inscripcionService.getAllInscripciones(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inscripción por ID", description = "Busca y retorna una inscripción específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción encontrada",
                    content = @Content(schema = @Schema(implementation = InscripcionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada", content = @Content)
    })
    public ResponseEntity<InscripcionResponse> getInscripcionById(
            @Parameter(description = "ID de la inscripción") @PathVariable String id) {
        return ResponseEntity.ok(inscripcionService.getInscripcionById(id));
    }

    @GetMapping("/estudiante/{estudianteId}")
    @Operation(summary = "Inscripciones de estudiante", description = "Obtiene todas las inscripciones activas de un estudiante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones del estudiante",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<InscripcionResponse>> getInscripcionesByEstudiante(
            @Parameter(description = "ID del estudiante") @PathVariable String estudianteId) {
        return ResponseEntity.ok(inscripcionService.getInscripcionesByEstudianteId(estudianteId));
    }

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Inscripciones de curso", description = "Obtiene todas las inscripciones de un curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones del curso",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<InscripcionResponse>> getInscripcionesByCurso(
            @Parameter(description = "ID del curso") @PathVariable String cursoId) {
        return ResponseEntity.ok(inscripcionService.getInscripcionesByCursoId(cursoId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Inscripciones por estado", description = "Obtiene inscripciones filtradas por estado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones con el estado especificado",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<InscripcionResponse>> getInscripcionesByEstado(
            @Parameter(description = "Estado de la inscripción", example = "INSCRITO") @PathVariable EstadoInscripcion estado) {
        return ResponseEntity.ok(inscripcionService.getInscripcionesByEstado(estado));
    }

    @GetMapping("/{estudianteId}/historial")
    @Operation(summary = "Historial de inscripciones", description = "Obtiene el historial completo de inscripciones de un estudiante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de inscripciones obtenido",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<InscripcionResponse>> getHistorialByEstudiante(
            @Parameter(description = "ID del estudiante") @PathVariable String estudianteId) {
        return ResponseEntity.ok(inscripcionService.getHistorialByEstudianteId(estudianteId));
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar inscripciones eliminadas", description = "Obtiene inscripciones eliminadas lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de inscripciones eliminadas",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<InscripcionResponse>> getInscripcionesDeleted() {
        return ResponseEntity.ok(inscripcionService.getInscripcionesDeleted());
    }

    @PostMapping
    @Operation(summary = "Crear inscripción", description = "Registra una nueva inscripción validando cupos disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inscripción creada exitosamente",
                    content = @Content(schema = @Schema(implementation = InscripcionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, inscripción duplicada o sin cupos disponibles", content = @Content),
            @ApiResponse(responseCode = "404", description = "Curso o estudiante no encontrado", content = @Content)
    })
    public ResponseEntity<InscripcionResponse> createInscripcion(
            @Parameter(description = "Datos de la inscripción") @Valid @RequestBody CreateInscripcionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripcionService.createInscripcion(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar inscripción", description = "Actualiza los datos de una inscripción existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción actualizada",
                    content = @Content(schema = @Schema(implementation = InscripcionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada", content = @Content)
    })
    public ResponseEntity<InscripcionResponse> updateInscripcion(
            @Parameter(description = "ID de la inscripción") @PathVariable String id,
            @Parameter(description = "Datos a actualizar") @Valid @RequestBody UpdateInscripcionRequest request) {
        return ResponseEntity.ok(inscripcionService.updateInscripcion(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar inscripción (soft delete)", description = "Elimina lógicamente una inscripción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inscripción eliminada"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada", content = @Content)
    })
    public ResponseEntity<Void> deleteInscripcion(
            @Parameter(description = "ID de la inscripción") @PathVariable String id) {
        inscripcionService.deleteInscripcion(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Eliminar inscripción permanentemente", description = "Elimina completamente una inscripción")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inscripción eliminada permanentemente"),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada", content = @Content)
    })
    public ResponseEntity<Void> permanentDeleteInscripcion(
            @Parameter(description = "ID de la inscripción") @PathVariable String id) {
        inscripcionService.permanentDeleteInscripcion(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar inscripción", description = "Restaura una inscripción eliminada lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscripción restaurada",
                    content = @Content(schema = @Schema(implementation = InscripcionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Inscripción no encontrada", content = @Content)
    })
    public ResponseEntity<InscripcionResponse> restoreInscripcion(
            @Parameter(description = "ID de la inscripción") @PathVariable String id) {
        return ResponseEntity.ok(inscripcionService.restoreInscripcion(id));
    }
}
