package com.example.api.controller;

import java.util.List;

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

import com.example.api.dto.request.CreateTemaRequest;
import com.example.api.dto.request.UpdateTemaRequest;
import com.example.api.dto.response.TemaResponse;
import com.example.api.service.TemaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de temas.
 */
@RestController
@RequestMapping("/api/temas")
@Tag(name = "Temas", description = "API para gestión de temas del sistema educativo")
public class TemaController {

    private final TemaService temaService;

    public TemaController(TemaService temaService) {
        this.temaService = temaService;
    }

    @GetMapping
    @Operation(summary = "Listar temas", description = "Obtiene una lista paginada de todos los temas activos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de temas obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<TemaResponse>> getAllTemas(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(temaService.getAllTemas(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tema por ID", description = "Busca y retorna un tema específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema encontrado",
                    content = @Content(schema = @Schema(implementation = TemaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tema no encontrado", content = @Content)
    })
    public ResponseEntity<TemaResponse> getTemaById(
            @Parameter(description = "ID del tema") @PathVariable String id) {
        return ResponseEntity.ok(temaService.getTemaById(id));
    }

    @GetMapping("/unidad/{unidadId}")
    @Operation(summary = "Temas de una unidad", description = "Obtiene todos los temas de una unidad específica ordenados por número")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de temas de la unidad",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<TemaResponse>> getTemasByUnidad(
            @Parameter(description = "ID de la unidad") @PathVariable String unidadId) {
        return ResponseEntity.ok(temaService.getTemasByUnidadId(unidadId));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar temas por título", description = "Busca temas que contengan el texto especificado en el título")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de temas encontrados",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<TemaResponse>> searchTemasByTitulo(
            @Parameter(description = "Texto a buscar en el título") @RequestParam String titulo) {
        return ResponseEntity.ok(temaService.getTemasByTitulo(titulo));
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar temas eliminados", description = "Obtiene todos los temas que han sido eliminados lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de temas eliminados",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<TemaResponse>> getTemasDeleted() {
        return ResponseEntity.ok(temaService.getTemasDeleted());
    }

    @PostMapping
    @Operation(summary = "Crear tema", description = "Crea un nuevo tema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tema creado exitosamente",
                    content = @Content(schema = @Schema(implementation = TemaResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada", content = @Content)
    })
    public ResponseEntity<TemaResponse> createTema(@Valid @RequestBody CreateTemaRequest request) {
        TemaResponse created = temaService.createTema(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tema", description = "Actualiza los datos de un tema existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = TemaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tema no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    public ResponseEntity<TemaResponse> updateTema(
            @Parameter(description = "ID del tema") @PathVariable String id,
            @Valid @RequestBody UpdateTemaRequest request) {
        return ResponseEntity.ok(temaService.updateTema(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tema (soft delete)", description = "Realiza una eliminación lógica del tema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tema eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tema no encontrado", content = @Content)
    })
    public ResponseEntity<Void> deleteTema(
            @Parameter(description = "ID del tema") @PathVariable String id) {
        temaService.deleteTema(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Eliminar tema permanentemente", description = "Elimina permanentemente un tema de la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tema eliminado permanentemente"),
            @ApiResponse(responseCode = "404", description = "Tema no encontrado", content = @Content)
    })
    public ResponseEntity<Void> permanentDeleteTema(
            @Parameter(description = "ID del tema") @PathVariable String id) {
        temaService.permanentDeleteTema(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar tema eliminado", description = "Restaura un tema que fue eliminado lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema restaurado exitosamente",
                    content = @Content(schema = @Schema(implementation = TemaResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tema no encontrado", content = @Content)
    })
    public ResponseEntity<TemaResponse> restoreTema(
            @Parameter(description = "ID del tema") @PathVariable String id) {
        return ResponseEntity.ok(temaService.restoreTema(id));
    }
}
