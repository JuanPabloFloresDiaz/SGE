package com.example.api.controller;

import java.math.BigDecimal;
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

import com.example.api.dto.request.CreateCalificacionRequest;
import com.example.api.dto.request.UpdateCalificacionRequest;
import com.example.api.dto.response.CalificacionResponse;
import com.example.api.service.CalificacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar calificaciones.
 * Implementa múltiples estructuras de datos educativas.
 */
@RestController
@RequestMapping("/api/calificaciones")
@Tag(name = "Calificaciones", description = "API para gestión de calificaciones con estructuras de datos avanzadas")
public class CalificacionController {

    private final CalificacionService calificacionService;

    /**
     * Constructor con inyección de dependencias.
     */
    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @Operation(
        summary = "Listar calificaciones",
        description = "Obtiene todas las calificaciones activas con paginación"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de calificaciones obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<Page<CalificacionResponse>> getAllCalificaciones(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(calificacionService.getAllCalificaciones(pageable));
    }

    @Operation(
        summary = "Obtener calificación por ID",
        description = "Obtiene una calificación específica por su ID"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Calificación encontrada"),
        @ApiResponse(responseCode = "404", description = "Calificación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CalificacionResponse> getCalificacionById(
            @Parameter(description = "ID de la calificación") @PathVariable String id) {
        return ResponseEntity.ok(calificacionService.getCalificacionById(id));
    }

    @Operation(
        summary = "Obtener calificaciones por estudiante",
        description = "Lista todas las calificaciones de un estudiante específico"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Calificaciones del estudiante obtenidas")
    })
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<CalificacionResponse>> getCalificacionesByEstudiante(
            @Parameter(description = "ID del estudiante") @PathVariable String estudianteId) {
        return ResponseEntity.ok(calificacionService.getCalificacionesByEstudianteId(estudianteId));
    }

    @Operation(
        summary = "Obtener calificaciones por evaluación",
        description = "Lista todas las calificaciones de una evaluación específica"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Calificaciones de la evaluación obtenidas")
    })
    @GetMapping("/evaluacion/{evaluacionId}")
    public ResponseEntity<List<CalificacionResponse>> getCalificacionesByEvaluacion(
            @Parameter(description = "ID de la evaluación") @PathVariable String evaluacionId) {
        return ResponseEntity.ok(calificacionService.getCalificacionesByEvaluacionId(evaluacionId));
    }

    @Operation(
        summary = "Obtener historial del estudiante (Lista Ligada)",
        description = "Obtiene el historial de calificaciones usando estructura de Lista Ligada. " +
                      "Las calificaciones más recientes se muestran primero mediante inserción al inicio (addFirst). " +
                      "Complejidad: O(1) por inserción."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    })
    @GetMapping("/estudiante/{estudianteId}/historial")
    public ResponseEntity<List<CalificacionResponse>> getHistorialEstudiante(
            @Parameter(description = "ID del estudiante") @PathVariable String estudianteId) {
        return ResponseEntity.ok(calificacionService.getHistorialEstudiante(estudianteId));
    }

    @Operation(
        summary = "Calcular promedio del estudiante",
        description = "Calcula el promedio de calificaciones de un estudiante"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promedio calculado exitosamente")
    })
    @GetMapping("/estudiante/{estudianteId}/promedio")
    public ResponseEntity<Map<String, Object>> calcularPromedioEstudiante(
            @Parameter(description = "ID del estudiante") @PathVariable String estudianteId) {
        return ResponseEntity.ok(calificacionService.calcularPromedioEstudiante(estudianteId));
    }

    @Operation(
        summary = "Obtener ranking general (Árbol BST)",
        description = "Obtiene el ranking de estudiantes usando Árbol BST (TreeMap). " +
                      "Los estudiantes se ordenan automáticamente por promedio en un árbol balanceado. " +
                      "Complejidad: O(n log n) para construcción, O(log n) para búsqueda."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ranking obtenido exitosamente")
    })
    @GetMapping("/ranking")
    public ResponseEntity<List<Map<String, Object>>> getRankingGeneral() {
        return ResponseEntity.ok(calificacionService.getRankingGeneral());
    }

    @Operation(
        summary = "Obtener ranking por curso (Árbol BST)",
        description = "Obtiene el ranking de estudiantes de un curso usando Árbol BST. " +
                      "Similar al ranking general pero filtrado por curso específico."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ranking del curso obtenido")
    })
    @GetMapping("/ranking/curso/{cursoId}")
    public ResponseEntity<List<Map<String, Object>>> getRankingCurso(
            @Parameter(description = "ID del curso") @PathVariable String cursoId) {
        return ResponseEntity.ok(calificacionService.getRankingCurso(cursoId));
    }

    @Operation(
        summary = "Buscar por rango de notas (Búsqueda Binaria)",
        description = "Busca calificaciones en un rango de notas usando Búsqueda Binaria. " +
                      "Primero ordena las calificaciones y luego aplica búsqueda binaria para eficiencia. " +
                      "Complejidad: O(log n) para búsqueda en array ordenado."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Búsqueda completada exitosamente")
    })
    @GetMapping("/buscar-nota")
    public ResponseEntity<List<CalificacionResponse>> buscarPorRangoNota(
            @Parameter(description = "Nota mínima") @RequestParam BigDecimal min,
            @Parameter(description = "Nota máxima") @RequestParam BigDecimal max) {
        return ResponseEntity.ok(calificacionService.buscarPorRangoNota(min, max));
    }

    @Operation(
        summary = "Ordenar por nota (Algoritmo de Burbuja)",
        description = "Ordena calificaciones usando el algoritmo de Burbuja (Bubble Sort). " +
                      "Implementación educativa del ordenamiento más básico. " +
                      "Complejidad: O(n²) en el peor caso, optimizado con bandera de intercambio."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Calificaciones ordenadas exitosamente")
    })
    @GetMapping("/ordenar")
    public ResponseEntity<List<CalificacionResponse>> ordenarPorNota() {
        return ResponseEntity.ok(calificacionService.ordenarPorNota());
    }

    @Operation(
        summary = "Obtener estadísticas (Tabla Hash)",
        description = "Obtiene estadísticas de distribución de notas usando HashMap (Tabla Hash). " +
                      "Cuenta frecuencia de rangos: 0-60, 61-70, 71-80, 81-90, 91-100. " +
                      "Complejidad: O(1) para inserciones y búsquedas en el HashMap."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente")
    })
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticas() {
        return ResponseEntity.ok(calificacionService.getEstadisticas());
    }

    @Operation(
        summary = "Listar calificaciones eliminadas",
        description = "Obtiene todas las calificaciones que han sido eliminadas lógicamente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de calificaciones eliminadas obtenida")
    })
    @GetMapping("/deleted")
    public ResponseEntity<List<CalificacionResponse>> getCalificacionesDeleted() {
        return ResponseEntity.ok(calificacionService.getCalificacionesDeleted());
    }

    @Operation(
        summary = "Crear calificación",
        description = "Crea una nueva calificación para un estudiante en una evaluación. " +
                      "Valida que tanto la evaluación como el estudiante existan."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Calificación creada exitosamente",
                content = @Content(schema = @Schema(implementation = CalificacionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Evaluación o estudiante no encontrado")
    })
    @PostMapping
    public ResponseEntity<CalificacionResponse> createCalificacion(
            @Valid @RequestBody CreateCalificacionRequest request) {
        CalificacionResponse created = calificacionService.createCalificacion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
        summary = "Actualizar calificación",
        description = "Actualiza una calificación existente. Permite actualización parcial."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Calificación actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Calificación no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CalificacionResponse> updateCalificacion(
            @Parameter(description = "ID de la calificación") @PathVariable String id,
            @Valid @RequestBody UpdateCalificacionRequest request) {
        return ResponseEntity.ok(calificacionService.updateCalificacion(id, request));
    }

    @Operation(
        summary = "Eliminar calificación (soft delete)",
        description = "Realiza un borrado lógico de la calificación"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Calificación eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Calificación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalificacion(
            @Parameter(description = "ID de la calificación") @PathVariable String id) {
        calificacionService.deleteCalificacion(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Eliminar calificación permanentemente",
        description = "Elimina físicamente una calificación de la base de datos. Esta acción es irreversible."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Calificación eliminada permanentemente"),
        @ApiResponse(responseCode = "404", description = "Calificación no encontrada")
    })
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteCalificacion(
            @Parameter(description = "ID de la calificación") @PathVariable String id) {
        calificacionService.permanentDeleteCalificacion(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Restaurar calificación",
        description = "Restaura una calificación que ha sido eliminada lógicamente"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Calificación restaurada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Calificación no encontrada")
    })
    @PatchMapping("/{id}/restore")
    public ResponseEntity<CalificacionResponse> restoreCalificacion(
            @Parameter(description = "ID de la calificación") @PathVariable String id) {
        return ResponseEntity.ok(calificacionService.restoreCalificacion(id));
    }
}
