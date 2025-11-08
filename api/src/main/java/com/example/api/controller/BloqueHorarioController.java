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

import com.example.api.dto.request.CreateBloqueHorarioRequest;
import com.example.api.dto.request.UpdateBloqueHorarioRequest;
import com.example.api.dto.response.BloqueHorarioResponse;
import com.example.api.service.BloqueHorarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para gestionar operaciones CRUD de bloques de horario.
 */
@RestController
@RequestMapping("/api/bloques-horario")
@Tag(name = "Bloques Horario", description = "API para gestión de bloques de horario del sistema educativo")
public class BloqueHorarioController {

    private final BloqueHorarioService bloqueHorarioService;

    public BloqueHorarioController(BloqueHorarioService bloqueHorarioService) {
        this.bloqueHorarioService = bloqueHorarioService;
    }

    @GetMapping
    @Operation(summary = "Listar bloques de horario", description = "Obtiene una lista paginada de todos los bloques activos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de bloques obtenida exitosamente",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<BloqueHorarioResponse>> getAllBloques(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(bloqueHorarioService.getAllBloques(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener bloque por ID", description = "Busca y retorna un bloque de horario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bloque encontrado",
                    content = @Content(schema = @Schema(implementation = BloqueHorarioResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bloque no encontrado", content = @Content)
    })
    public ResponseEntity<BloqueHorarioResponse> getBloqueById(
            @Parameter(description = "ID del bloque") @PathVariable String id) {
        return ResponseEntity.ok(bloqueHorarioService.getBloqueById(id));
    }

    @GetMapping("/ordenados")
    @Operation(summary = "Bloques ordenados por hora", description = "Obtiene todos los bloques ordenados por hora de inicio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista ordenada de bloques",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<BloqueHorarioResponse>> getBloquesOrdenados() {
        return ResponseEntity.ok(bloqueHorarioService.getBloquesOrdenados());
    }

    @GetMapping("/deleted")
    @Operation(summary = "Listar bloques eliminados", description = "Obtiene bloques eliminados lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de bloques eliminados",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    public ResponseEntity<List<BloqueHorarioResponse>> getBloquesDeleted() {
        return ResponseEntity.ok(bloqueHorarioService.getBloquesDeleted());
    }

    @PostMapping
    @Operation(summary = "Crear bloque de horario", description = "Registra un nuevo bloque en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bloque creado exitosamente",
                    content = @Content(schema = @Schema(implementation = BloqueHorarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o hora de fin anterior a hora de inicio", content = @Content)
    })
    public ResponseEntity<BloqueHorarioResponse> createBloque(
            @Parameter(description = "Datos del bloque") @Valid @RequestBody CreateBloqueHorarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bloqueHorarioService.createBloque(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar bloque", description = "Actualiza los datos de un bloque existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bloque actualizado",
                    content = @Content(schema = @Schema(implementation = BloqueHorarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bloque no encontrado", content = @Content)
    })
    public ResponseEntity<BloqueHorarioResponse> updateBloque(
            @Parameter(description = "ID del bloque") @PathVariable String id,
            @Parameter(description = "Datos a actualizar") @Valid @RequestBody UpdateBloqueHorarioRequest request) {
        return ResponseEntity.ok(bloqueHorarioService.updateBloque(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar bloque (soft delete)", description = "Elimina lógicamente un bloque de horario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bloque eliminado"),
            @ApiResponse(responseCode = "404", description = "Bloque no encontrado", content = @Content)
    })
    public ResponseEntity<Void> deleteBloque(
            @Parameter(description = "ID del bloque") @PathVariable String id) {
        bloqueHorarioService.deleteBloque(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Eliminar bloque permanentemente", description = "Elimina completamente un bloque")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bloque eliminado permanentemente"),
            @ApiResponse(responseCode = "404", description = "Bloque no encontrado", content = @Content)
    })
    public ResponseEntity<Void> permanentDeleteBloque(
            @Parameter(description = "ID del bloque") @PathVariable String id) {
        bloqueHorarioService.permanentDeleteBloque(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restaurar bloque", description = "Restaura un bloque eliminado lógicamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bloque restaurado",
                    content = @Content(schema = @Schema(implementation = BloqueHorarioResponse.class))),
            @ApiResponse(responseCode = "404", description = "Bloque no encontrado", content = @Content)
    })
    public ResponseEntity<BloqueHorarioResponse> restoreBloque(
            @Parameter(description = "ID del bloque") @PathVariable String id) {
        return ResponseEntity.ok(bloqueHorarioService.restoreBloque(id));
    }
}
