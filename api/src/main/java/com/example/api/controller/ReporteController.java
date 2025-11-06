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
import org.springframework.web.bind.annotation.RestController;

import com.example.api.dto.request.CreateReporteRequest;
import com.example.api.dto.request.UpdateReporteRequest;
import com.example.api.dto.response.ReporteResponse;
import com.example.api.model.Reporte.TipoReporte;
import com.example.api.service.ReporteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar reportes.
 * Implementa Queue (FIFO) para el procesamiento de reportes.
 */
@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "API para gestión de reportes con cola FIFO de procesamiento")
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * Constructor con inyección de dependencias.
     */
    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @Operation(
        summary = "Listar reportes",
        description = "Obtiene todos los reportes activos con paginación"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de reportes obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<Page<ReporteResponse>> getAllReportes(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reporteService.getAllReportes(pageable));
    }

    @Operation(
        summary = "Obtener reporte por ID",
        description = "Obtiene un reporte específico por su ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte encontrado"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReporteResponse> getReporteById(
            @Parameter(description = "ID del reporte") @PathVariable String id) {
        return ResponseEntity.ok(reporteService.getReporteById(id));
    }

    @Operation(
        summary = "Obtener reportes por tipo",
        description = "Lista todos los reportes de un tipo específico (CONDUCTA, ACADEMICO, OTRO)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reportes del tipo obtenidos")
    })
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ReporteResponse>> getReportesByTipo(
            @Parameter(description = "Tipo de reporte", schema = @Schema(allowableValues = {"CONDUCTA", "ACADEMICO", "OTRO"})) 
            @PathVariable TipoReporte tipo) {
        return ResponseEntity.ok(reporteService.getReportesByTipo(tipo));
    }

    @Operation(
        summary = "Obtener reportes por usuario",
        description = "Lista todos los reportes creados por un usuario específico"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reportes del usuario obtenidos")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReporteResponse>> getReportesByUsuario(
            @Parameter(description = "ID del usuario creador") @PathVariable String usuarioId) {
        return ResponseEntity.ok(reporteService.getReportesByUsuarioId(usuarioId));
    }

    @Operation(
        summary = "Obtener reportes por estudiante",
        description = "Lista todos los reportes de un estudiante específico"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reportes del estudiante obtenidos")
    })
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<ReporteResponse>> getReportesByEstudiante(
            @Parameter(description = "ID del estudiante") @PathVariable String estudianteId) {
        return ResponseEntity.ok(reporteService.getReportesByEstudianteId(estudianteId));
    }

    @Operation(
        summary = "Obtener reportes recientes",
        description = "Obtiene los 10 reportes más recientes del sistema"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reportes recientes obtenidos")
    })
    @GetMapping("/recientes")
    public ResponseEntity<List<ReporteResponse>> getReportesRecientes() {
        return ResponseEntity.ok(reporteService.getReportesRecientes());
    }

    @Operation(
        summary = "Agregar reporte a cola de generación (Queue FIFO)",
        description = "Agrega una solicitud de reporte a la cola FIFO para procesamiento asíncrono. " +
                      "Los reportes se procesan en el orden en que son solicitados (First In, First Out). " +
                      "Complejidad: O(1) para agregar a la cola."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Solicitud agregada a la cola de generación")
    })
    @PostMapping("/cola-generacion")
    public ResponseEntity<Map<String, Object>> agregarAColaGeneracion(
            @Valid @RequestBody CreateReporteRequest request) {
        Map<String, Object> resultado = reporteService.agregarAColaGeneracion(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(resultado);
    }

    @Operation(
        summary = "Procesar cola de reportes (Queue FIFO)",
        description = "Procesa todos los reportes pendientes en la cola FIFO. " +
                      "Los reportes se generan en el orden en que fueron solicitados. " +
                      "Complejidad: O(n) donde n es el número de reportes en la cola."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cola procesada exitosamente")
    })
    @PostMapping("/procesar")
    public ResponseEntity<Map<String, Object>> procesarColaReportes() {
        return ResponseEntity.ok(reporteService.procesarColaReportes());
    }

    @Operation(
        summary = "Obtener estado de la cola",
        description = "Obtiene información sobre el estado actual de la cola de reportes"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado de la cola obtenido")
    })
    @GetMapping("/cola-estado")
    public ResponseEntity<Map<String, Object>> getEstadoCola() {
        return ResponseEntity.ok(reporteService.getEstadoCola());
    }

    @Operation(
        summary = "Listar reportes eliminados",
        description = "Obtiene todos los reportes que han sido eliminados lógicamente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de reportes eliminados obtenida")
    })
    @GetMapping("/deleted")
    public ResponseEntity<List<ReporteResponse>> getReportesDeleted() {
        return ResponseEntity.ok(reporteService.getReportesDeleted());
    }

    @Operation(
        summary = "Crear reporte",
        description = "Crea un nuevo reporte inmediatamente (sin cola). " +
                      "Valida que el estudiante, curso y usuario creador existan."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reporte creado exitosamente",
                content = @Content(schema = @Schema(implementation = ReporteResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Estudiante, curso o usuario no encontrado")
    })
    @PostMapping
    public ResponseEntity<ReporteResponse> createReporte(
            @Valid @RequestBody CreateReporteRequest request) {
        ReporteResponse created = reporteService.createReporte(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
        summary = "Actualizar reporte",
        description = "Actualiza un reporte existente. Permite actualización parcial."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReporteResponse> updateReporte(
            @Parameter(description = "ID del reporte") @PathVariable String id,
            @Valid @RequestBody UpdateReporteRequest request) {
        return ResponseEntity.ok(reporteService.updateReporte(id, request));
    }

    @Operation(
        summary = "Eliminar reporte (soft delete)",
        description = "Realiza un borrado lógico del reporte"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reporte eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReporte(
            @Parameter(description = "ID del reporte") @PathVariable String id) {
        reporteService.deleteReporte(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Eliminar reporte permanentemente",
        description = "Elimina físicamente un reporte de la base de datos. Esta acción es irreversible."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reporte eliminado permanentemente"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteReporte(
            @Parameter(description = "ID del reporte") @PathVariable String id) {
        reporteService.permanentDeleteReporte(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Restaurar reporte",
        description = "Restaura un reporte que ha sido eliminado lógicamente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reporte restaurado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado")
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ReporteResponse> restoreReporte(
            @Parameter(description = "ID del reporte") @PathVariable String id) {
        return ResponseEntity.ok(reporteService.restoreReporte(id));
    }
}
