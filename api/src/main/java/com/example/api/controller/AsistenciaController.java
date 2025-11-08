package com.example.api.controller;

import java.util.List;
import java.util.Map;

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

import com.example.api.dto.request.CreateAsistenciaRequest;
import com.example.api.dto.request.UpdateAsistenciaRequest;
import com.example.api.dto.response.AsistenciaResponse;
import com.example.api.service.AsistenciaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de asistencias.
 */
@RestController
@RequestMapping("/api/asistencia")
@Tag(name = "Asistencia", description = "API para gestión de asistencias del sistema educativo")
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @GetMapping
    @Operation(summary = "Listar todas las asistencias", description = "Obtiene una lista paginada de todas las asistencias activas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de asistencias obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<AsistenciaResponse>> getAllAsistencias(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(asistenciaService.getAllAsistencias(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener asistencia por ID", description = "Obtiene un registro de asistencia específico por su identificador único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asistencia encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AsistenciaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Asistencia no encontrada")
    })
    public ResponseEntity<AsistenciaResponse> getAsistenciaById(
            @Parameter(description = "ID de la asistencia") @PathVariable String id) {
        return ResponseEntity.ok(asistenciaService.getAsistenciaById(id));
    }

    @GetMapping("/clase/{claseId}")
    @Operation(summary = "Listar asistencias por clase", description = "Obtiene todas las asistencias de una clase específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de asistencias obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<AsistenciaResponse>> getAsistenciasByClaseId(
            @Parameter(description = "ID de la clase") @PathVariable String claseId) {
        return ResponseEntity.ok(asistenciaService.getAsistenciasByClaseId(claseId));
    }

    @GetMapping("/estudiante/{estudianteId}")
    @Operation(summary = "Listar asistencias por estudiante", description = "Obtiene todas las asistencias de un estudiante específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de asistencias obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<AsistenciaResponse>> getAsistenciasByEstudianteId(
            @Parameter(description = "ID del estudiante") @PathVariable String estudianteId) {
        return ResponseEntity.ok(asistenciaService.getAsistenciasByEstudianteId(estudianteId));
    }

    @GetMapping("/estudiante/{estudianteId}/estadisticas")
    @Operation(summary = "Obtener estadísticas de asistencia", 
               description = "Obtiene estadísticas de asistencia de un estudiante usando HashMap (Tabla Hash)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, Object>> getEstadisticasEstudiante(
            @Parameter(description = "ID del estudiante") @PathVariable String estudianteId) {
        return ResponseEntity.ok(asistenciaService.getEstadisticasEstudiante(estudianteId));
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar asistencias eliminadas", description = "Obtiene todas las asistencias que han sido eliminadas (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de asistencias eliminadas obtenida exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<AsistenciaResponse>> getAsistenciasDeleted() {
        return ResponseEntity.ok(asistenciaService.getAsistenciasDeleted());
    }

    @PostMapping
    @Operation(summary = "Registrar asistencia", description = "Crea un nuevo registro de asistencia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asistencia registrada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AsistenciaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<AsistenciaResponse> createAsistencia(@Valid @RequestBody CreateAsistenciaRequest request) {
        AsistenciaResponse createdAsistencia = asistenciaService.createAsistencia(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAsistencia);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar asistencia", description = "Actualiza los datos de un registro de asistencia existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asistencia actualizada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AsistenciaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Asistencia no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<AsistenciaResponse> updateAsistencia(
            @Parameter(description = "ID de la asistencia") @PathVariable String id,
            @Valid @RequestBody UpdateAsistenciaRequest request) {
        return ResponseEntity.ok(asistenciaService.updateAsistencia(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar asistencia (soft delete)", description = "Realiza una eliminación lógica del registro de asistencia")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Asistencia eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Asistencia no encontrada")
    })
    public ResponseEntity<Void> deleteAsistencia(
            @Parameter(description = "ID de la asistencia") @PathVariable String id) {
        asistenciaService.deleteAsistencia(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Eliminar asistencia permanentemente", description = "Elimina definitivamente un registro de asistencia de la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Asistencia eliminada permanentemente"),
            @ApiResponse(responseCode = "404", description = "Asistencia no encontrada")
    })
    public ResponseEntity<Void> permanentDeleteAsistencia(
            @Parameter(description = "ID de la asistencia") @PathVariable String id) {
        asistenciaService.permanentDeleteAsistencia(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar asistencia", description = "Restaura un registro de asistencia previamente eliminado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asistencia restaurada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AsistenciaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Asistencia no encontrada")
    })
    public ResponseEntity<AsistenciaResponse> restoreAsistencia(
            @Parameter(description = "ID de la asistencia") @PathVariable String id) {
        return ResponseEntity.ok(asistenciaService.restoreAsistencia(id));
    }
}
