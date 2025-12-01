package com.example.api.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

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

import com.example.api.dto.request.CreateEntregasActividadRequest;
import com.example.api.dto.request.UpdateEntregasActividadRequest;
import com.example.api.dto.response.EntregasActividadResponse;
import com.example.api.service.EntregasActividadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/entregas")
@Tag(name = "Entregas de Actividad", description = "API para gestión de entregas de actividades por estudiantes")
public class EntregasActividadController {

        private final EntregasActividadService service;

        public EntregasActividadController(EntregasActividadService service) {
                this.service = service;
        }

        @GetMapping
        @Operation(summary = "Listar todas las entregas", description = "Obtiene una lista paginada de todas las entregas activas")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
        })
        public ResponseEntity<Page<EntregasActividadResponse>> getAll(
                        @Parameter(description = "Número de página (inicia en 0)", example = "0") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Tamaño de página", example = "20") @RequestParam(defaultValue = "20") int size) {
                Pageable pageable = PageRequest.of(page, size);
                return ResponseEntity.ok(service.getAll(pageable));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Obtener entrega por ID", description = "Obtiene una entrega específica por su identificador único")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Entrega encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntregasActividadResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Entrega no encontrada")
        })
        public ResponseEntity<EntregasActividadResponse> getById(
                        @Parameter(description = "ID de la entrega") @PathVariable String id) {
                return ResponseEntity.ok(service.getById(id));
        }

        @GetMapping("/actividad/{actividadId}")
        @Operation(summary = "Listar entregas por actividad", description = "Obtiene todas las entregas de una actividad específica")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
        })
        public ResponseEntity<List<EntregasActividadResponse>> getByActividadId(
                        @Parameter(description = "ID de la actividad") @PathVariable String actividadId) {
                return ResponseEntity.ok(service.getByActividadId(actividadId));
        }

        @GetMapping("/estudiante/{estudianteId}")
        @Operation(summary = "Listar entregas por estudiante", description = "Obtiene todas las entregas de un estudiante específico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class)))
        })
        public ResponseEntity<List<EntregasActividadResponse>> getByEstudianteId(
                        @Parameter(description = "ID del estudiante") @PathVariable String estudianteId) {
                return ResponseEntity.ok(service.getByEstudianteId(estudianteId));
        }

        @PostMapping
        @Operation(summary = "Crear entrega", description = "Registra una nueva entrega de actividad")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntregasActividadResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o entrega ya existente"),
                        @ApiResponse(responseCode = "404", description = "Actividad o estudiante no encontrado")
        })
        public ResponseEntity<EntregasActividadResponse> create(
                        @Valid @RequestBody CreateEntregasActividadRequest request,
                        HttpServletRequest httpRequest) {
                EntregasActividadResponse created = service.create(request, httpRequest);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
        }

        @PutMapping("/{id}")
        @Operation(summary = "Actualizar entrega", description = "Actualiza una entrega existente (nota, documento, comentario)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EntregasActividadResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Entrega no encontrada")
        })
        public ResponseEntity<EntregasActividadResponse> update(
                        @Parameter(description = "ID de la entrega") @PathVariable String id,
                        @Valid @RequestBody UpdateEntregasActividadRequest request,
                        HttpServletRequest httpRequest) {
                return ResponseEntity.ok(service.update(id, request, httpRequest));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Eliminar entrega", description = "Realiza un borrado lógico de la entrega")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Eliminado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Entrega no encontrada")
        })
        public ResponseEntity<Void> delete(
                        @Parameter(description = "ID de la entrega") @PathVariable String id,
                        HttpServletRequest httpRequest) {
                service.delete(id, httpRequest);
                return ResponseEntity.noContent().build();
        }
}
