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

import com.example.api.dto.request.CreateHorarioCursoRequest;
import com.example.api.dto.request.UpdateHorarioCursoRequest;
import com.example.api.dto.response.HorarioCursoResponse;
import com.example.api.model.HorarioCurso.DiaSemana;
import com.example.api.service.HorarioCursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de horarios de curso.
 */
@RestController
@RequestMapping("/api/horarios-curso")
@Tag(name = "Horarios Curso", description = "API para gestión de horarios de cursos del sistema educativo")
public class HorarioCursoController {

    private final HorarioCursoService horarioCursoService;

    public HorarioCursoController(HorarioCursoService horarioCursoService) {
        this.horarioCursoService = horarioCursoService;
    }

    @GetMapping
    @Operation(summary = "Listar horarios de curso", description = "Obtiene una lista paginada de todos los horarios activos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horarios obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<HorarioCursoResponse>> getAllHorarios(
            @PageableDefault(size = 20, sort = "dia") Pageable pageable) {
        return ResponseEntity.ok(horarioCursoService.getAllHorarios(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener horario por ID", description = "Busca y retorna un horario de curso específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario encontrado",
                    content = @Content(schema = @Schema(implementation = HorarioCursoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado", content = @Content)
    })
    public ResponseEntity<HorarioCursoResponse> getHorarioById(
            @Parameter(description = "ID del horario") @PathVariable String id) {
        return ResponseEntity.ok(horarioCursoService.getHorarioById(id));
    }

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Horarios de un curso", description = "Obtiene todos los horarios de un curso específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horarios del curso",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<HorarioCursoResponse>> getHorariosByCurso(
            @Parameter(description = "ID del curso") @PathVariable String cursoId) {
        return ResponseEntity.ok(horarioCursoService.getHorariosByCursoId(cursoId));
    }

    @GetMapping("/dia/{dia}")
    @Operation(summary = "Horarios por día", description = "Obtiene todos los horarios de un día de la semana")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horarios del día",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<HorarioCursoResponse>> getHorariosByDia(
            @Parameter(description = "Día de la semana", example = "LUN") @PathVariable DiaSemana dia) {
        return ResponseEntity.ok(horarioCursoService.getHorariosByDia(dia));
    }

    @GetMapping("/conflictos")
    @Operation(summary = "Detectar conflictos", description = "Detecta horarios que se solapan en el mismo día, bloque y aula")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de conflictos detectados",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<HorarioCursoResponse>> getConflictos() {
        return ResponseEntity.ok(horarioCursoService.getConflictos());
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar horarios eliminados", description = "Obtiene horarios eliminados lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horarios eliminados",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<HorarioCursoResponse>> getHorariosDeleted() {
        return ResponseEntity.ok(horarioCursoService.getHorariosDeleted());
    }

    @PostMapping
    @Operation(summary = "Crear horario de curso", description = "Registra un nuevo horario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Horario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = HorarioCursoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o horario duplicado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Curso o bloque de horario no encontrado", content = @Content)
    })
    public ResponseEntity<HorarioCursoResponse> createHorario(
            @Parameter(description = "Datos del horario") @Valid @RequestBody CreateHorarioCursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioCursoService.createHorario(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar horario", description = "Actualiza los datos de un horario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario actualizado",
                    content = @Content(schema = @Schema(implementation = HorarioCursoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Horario, curso o bloque no encontrado", content = @Content)
    })
    public ResponseEntity<HorarioCursoResponse> updateHorario(
            @Parameter(description = "ID del horario") @PathVariable String id,
            @Parameter(description = "Datos a actualizar") @Valid @RequestBody UpdateHorarioCursoRequest request) {
        return ResponseEntity.ok(horarioCursoService.updateHorario(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar horario (soft delete)", description = "Elimina lógicamente un horario de curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Horario eliminado"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado", content = @Content)
    })
    public ResponseEntity<Void> deleteHorario(
            @Parameter(description = "ID del horario") @PathVariable String id) {
        horarioCursoService.deleteHorario(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Eliminar horario permanentemente", description = "Elimina completamente un horario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Horario eliminado permanentemente"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado", content = @Content)
    })
    public ResponseEntity<Void> permanentDeleteHorario(
            @Parameter(description = "ID del horario") @PathVariable String id) {
        horarioCursoService.permanentDeleteHorario(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar horario", description = "Restaura un horario eliminado lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario restaurado",
                    content = @Content(schema = @Schema(implementation = HorarioCursoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado", content = @Content)
    })
    public ResponseEntity<HorarioCursoResponse> restoreHorario(
            @Parameter(description = "ID del horario") @PathVariable String id) {
        return ResponseEntity.ok(horarioCursoService.restoreHorario(id));
    }
}
