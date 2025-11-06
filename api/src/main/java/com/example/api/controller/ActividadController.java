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

import com.example.api.dto.request.CreateActividadRequest;
import com.example.api.dto.request.UpdateActividadRequest;
import com.example.api.dto.response.ActividadResponse;
import com.example.api.service.ActividadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de actividades.
 */
@RestController
@RequestMapping("/api/actividades")
@Tag(name = "Actividades", description = "API para gestión de actividades del sistema educativo")
public class ActividadController {

    private final ActividadService actividadService;

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las actividades", description = "Obtiene una lista paginada de todas las actividades activas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de actividades obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ActividadResponse>> getAllActividades(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(actividadService.getAllActividades(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener actividad por ID", description = "Obtiene una actividad específica por su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actividad encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActividadResponse.class))),
            @ApiResponse(responseCode = "404", description = "Actividad no encontrada")
    })
    public ResponseEntity<ActividadResponse> getActividadById(
            @Parameter(description = "ID de la actividad") @PathVariable String id) {
        return ResponseEntity.ok(actividadService.getActividadById(id));
    }

    @GetMapping("/asignatura/{asignaturaId}")
    @Operation(summary = "Listar actividades por asignatura", description = "Obtiene todas las actividades de una asignatura específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de actividades obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ActividadResponse>> getActividadesByAsignaturaId(
            @Parameter(description = "ID de la asignatura") @PathVariable String asignaturaId) {
        return ResponseEntity.ok(actividadService.getActividadesByAsignaturaId(asignaturaId));
    }

    @GetMapping("/profesor/{profesorId}")
    @Operation(summary = "Listar actividades por profesor", description = "Obtiene todas las actividades de un profesor específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de actividades obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ActividadResponse>> getActividadesByProfesorId(
            @Parameter(description = "ID del profesor") @PathVariable String profesorId) {
        return ResponseEntity.ok(actividadService.getActividadesByProfesorId(profesorId));
    }

    @GetMapping("/abiertas")
    @Operation(summary = "Listar actividades abiertas", 
               description = "Obtiene todas las actividades que están actualmente abiertas (fecha actual entre apertura y cierre)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de actividades abiertas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ActividadResponse>> getActividadesAbiertas() {
        return ResponseEntity.ok(actividadService.getActividadesAbiertas());
    }

    @GetMapping("/proximas")
    @Operation(summary = "Listar próximas actividades", 
               description = "Obtiene todas las actividades que aún no han abierto (fecha de apertura futura)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de próximas actividades obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ActividadResponse>> getProximasActividades() {
        return ResponseEntity.ok(actividadService.getProximasActividades());
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar actividades eliminadas", description = "Obtiene todas las actividades que han sido eliminadas lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de actividades eliminadas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<ActividadResponse>> getActividadesDeleted() {
        return ResponseEntity.ok(actividadService.getActividadesDeleted());
    }

    @PostMapping
    @Operation(summary = "Crear actividad", description = "Crea una nueva actividad. Valida que la asignatura y el profesor existan, y que las fechas sean coherentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Actividad creada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActividadResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Asignatura o profesor no encontrado")
    })
    public ResponseEntity<ActividadResponse> createActividad(
            @Valid @RequestBody CreateActividadRequest request) {
        ActividadResponse created = actividadService.createActividad(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar actividad", description = "Actualiza una actividad existente. Permite actualización parcial de los campos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actividad actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActividadResponse.class))),
            @ApiResponse(responseCode = "404", description = "Actividad no encontrada")
    })
    public ResponseEntity<ActividadResponse> updateActividad(
            @Parameter(description = "ID de la actividad") @PathVariable String id,
            @Valid @RequestBody UpdateActividadRequest request) {
        return ResponseEntity.ok(actividadService.updateActividad(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar actividad (soft delete)", description = "Realiza un borrado lógico de la actividad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Actividad eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Actividad no encontrada")
    })
    public ResponseEntity<Void> deleteActividad(
            @Parameter(description = "ID de la actividad") @PathVariable String id) {
        actividadService.deleteActividad(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Eliminar actividad permanentemente", description = "Elimina físicamente la actividad de la base de datos. Esta acción es irreversible.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Actividad eliminada permanentemente"),
            @ApiResponse(responseCode = "404", description = "Actividad no encontrada")
    })
    public ResponseEntity<Void> permanentDeleteActividad(
            @Parameter(description = "ID de la actividad") @PathVariable String id) {
        actividadService.permanentDeleteActividad(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar actividad", description = "Restaura una actividad que ha sido eliminada lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actividad restaurada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ActividadResponse.class))),
            @ApiResponse(responseCode = "404", description = "Actividad no encontrada")
    })
    public ResponseEntity<ActividadResponse> restoreActividad(
            @Parameter(description = "ID de la actividad") @PathVariable String id) {
        return ResponseEntity.ok(actividadService.restoreActividad(id));
    }
}
