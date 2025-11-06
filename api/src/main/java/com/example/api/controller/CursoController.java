package com.example.api.controller;

import java.util.List;
import java.util.Map;

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

import com.example.api.dto.request.CreateCursoRequest;
import com.example.api.dto.request.UpdateCursoRequest;
import com.example.api.dto.response.CursoResponse;
import com.example.api.service.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de cursos.
 */
@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos", description = "API para gestión de cursos del sistema educativo")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    @Operation(summary = "Listar cursos activos", description = "Obtiene una lista paginada de todos los cursos activos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<CursoResponse>> getAllCursos(
            @PageableDefault(size = 10, sort = "nombreGrupo") Pageable pageable) {
        return ResponseEntity.ok(cursoService.getAllCursos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener curso por ID", description = "Busca y retorna un curso específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso encontrado",
                    content = @Content(schema = @Schema(implementation = CursoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = @Content)
    })
    public ResponseEntity<CursoResponse> getCursoById(
            @Parameter(description = "ID del curso") @PathVariable String id) {
        return ResponseEntity.ok(cursoService.getCursoById(id));
    }

    @GetMapping("/periodo/{periodoId}")
    @Operation(summary = "Cursos por periodo", description = "Obtiene todos los cursos de un periodo académico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos del periodo",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<CursoResponse>> getCursosByPeriodo(
            @Parameter(description = "ID del periodo") @PathVariable String periodoId) {
        return ResponseEntity.ok(cursoService.getCursosByPeriodoId(periodoId));
    }

    @GetMapping("/profesor/{profesorId}")
    @Operation(summary = "Cursos de profesor", description = "Obtiene todos los cursos asignados a un profesor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos del profesor",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<CursoResponse>> getCursosByProfesor(
            @Parameter(description = "ID del profesor") @PathVariable String profesorId) {
        return ResponseEntity.ok(cursoService.getCursosByProfesorId(profesorId));
    }

    @GetMapping("/asignatura/{asignaturaId}")
    @Operation(summary = "Cursos de asignatura", description = "Obtiene todos los cursos de una asignatura")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos de la asignatura",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<CursoResponse>> getCursosByAsignatura(
            @Parameter(description = "ID de la asignatura") @PathVariable String asignaturaId) {
        return ResponseEntity.ok(cursoService.getCursosByAsignaturaId(asignaturaId));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar cursos por nombre", description = "Realiza búsqueda parcial por nombre de grupo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda completada",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<CursoResponse>> searchByNombre(
            @Parameter(description = "Texto a buscar") @RequestParam String nombre) {
        return ResponseEntity.ok(cursoService.searchByNombreGrupo(nombre));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Cursos con cupos", description = "Obtiene cursos que tienen cupos disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos con cupo",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<CursoResponse>> getCursosConCupos() {
        return ResponseEntity.ok(cursoService.getCursosConCuposDisponibles());
    }

    @GetMapping("/{id}/disponibilidad")
    @Operation(summary = "Disponibilidad de cupo", description = "Obtiene información sobre cupos disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información de disponibilidad obtenida",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> getDisponibilidad(
            @Parameter(description = "ID del curso") @PathVariable String id) {
        return ResponseEntity.ok(cursoService.getDisponibilidadCupo(id));
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar cursos eliminados", description = "Obtiene cursos eliminados lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cursos eliminados",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<CursoResponse>> getCursosDeleted() {
        return ResponseEntity.ok(cursoService.getCursosDeleted());
    }

    @PostMapping
    @Operation(summary = "Crear curso", description = "Registra un nuevo curso en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Curso creado exitosamente",
                    content = @Content(schema = @Schema(implementation = CursoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Asignatura, periodo o profesor no encontrado", content = @Content)
    })
    public ResponseEntity<CursoResponse> createCurso(
            @Parameter(description = "Datos del curso") @Valid @RequestBody CreateCursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.createCurso(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar curso", description = "Actualiza los datos de un curso existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso actualizado",
                    content = @Content(schema = @Schema(implementation = CursoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Curso o entidad relacionada no encontrada", content = @Content)
    })
    public ResponseEntity<CursoResponse> updateCurso(
            @Parameter(description = "ID del curso") @PathVariable String id,
            @Parameter(description = "Datos a actualizar") @Valid @RequestBody UpdateCursoRequest request) {
        return ResponseEntity.ok(cursoService.updateCurso(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar curso (soft delete)", description = "Elimina lógicamente un curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Curso eliminado"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = @Content)
    })
    public ResponseEntity<Void> deleteCurso(
            @Parameter(description = "ID del curso") @PathVariable String id) {
        cursoService.deleteCurso(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Eliminar curso permanentemente", description = "Elimina completamente un curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Curso eliminado permanentemente"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = @Content)
    })
    public ResponseEntity<Void> permanentDeleteCurso(
            @Parameter(description = "ID del curso") @PathVariable String id) {
        cursoService.permanentDeleteCurso(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar curso", description = "Restaura un curso eliminado lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso restaurado",
                    content = @Content(schema = @Schema(implementation = CursoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado", content = @Content)
    })
    public ResponseEntity<CursoResponse> restoreCurso(
            @Parameter(description = "ID del curso") @PathVariable String id) {
        return ResponseEntity.ok(cursoService.restoreCurso(id));
    }
}
