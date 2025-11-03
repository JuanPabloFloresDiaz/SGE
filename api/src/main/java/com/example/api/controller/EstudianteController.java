package com.example.api.controller;

import java.util.List;

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

import com.example.api.dto.request.CreateEstudianteRequest;
import com.example.api.dto.request.UpdateEstudianteRequest;
import com.example.api.dto.response.EstudianteResponse;
import com.example.api.model.Estudiante.Genero;
import com.example.api.service.EstudianteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de estudiantes.
 * Proporciona endpoints CRUD básicos usando Spring/JPA.
 */
@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "Estudiantes", description = "API para la gestión de estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param estudianteService Servicio de estudiantes
     */
    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @Operation(
            summary = "Listar todos los estudiantes activos",
            description = "Obtiene una lista paginada de todos los estudiantes que no han sido eliminados"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de estudiantes obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EstudianteResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de paginación inválidos"
            )
    })
    @GetMapping
    public ResponseEntity<Page<EstudianteResponse>> getAllEstudiantes(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<EstudianteResponse> estudiantes = estudianteService.getAllEstudiantes(pageable);
        return ResponseEntity.ok(estudiantes);
    }

    @Operation(
            summary = "Obtener estudiante por ID",
            description = "Busca un estudiante específico por su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estudiante encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EstudianteResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante no encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponse> getEstudianteById(
            @Parameter(description = "ID del estudiante", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        
        EstudianteResponse estudiante = estudianteService.getEstudianteById(id);
        return ResponseEntity.ok(estudiante);
    }

    @Operation(
            summary = "Buscar estudiante por código",
            description = "Busca un estudiante por su código único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estudiante encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EstudianteResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante no encontrado"
            )
    })
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<EstudianteResponse> getEstudianteByCodigo(
            @Parameter(description = "Código del estudiante", required = true, example = "EST-2024-001")
            @PathVariable String codigo) {
        
        EstudianteResponse estudiante = estudianteService.getEstudianteByCodigo(codigo);
        return ResponseEntity.ok(estudiante);
    }

    @Operation(
            summary = "Filtrar estudiantes por género",
            description = "Obtiene todos los estudiantes de un género específico"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de estudiantes obtenida exitosamente"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Género inválido"
            )
    })
    @GetMapping("/genero/{genero}")
    public ResponseEntity<List<EstudianteResponse>> getEstudiantesByGenero(
            @Parameter(description = "Género (M/F/O)", required = true, example = "M")
            @PathVariable Genero genero) {
        
        List<EstudianteResponse> estudiantes = estudianteService.getEstudiantesByGenero(genero);
        return ResponseEntity.ok(estudiantes);
    }

    @Operation(
            summary = "Obtener estudiantes activos",
            description = "Lista todos los estudiantes con estado activo = true"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de estudiantes activos obtenida exitosamente"
            )
    })
    @GetMapping("/activos")
    public ResponseEntity<List<EstudianteResponse>> getEstudiantesActivos() {
        List<EstudianteResponse> estudiantes = estudianteService.getEstudiantesActivos();
        return ResponseEntity.ok(estudiantes);
    }

    @Operation(
            summary = "Crear nuevo estudiante",
            description = "Registra un nuevo estudiante en el sistema asociado a un usuario existente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Estudiante creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EstudianteResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El código de estudiante ya existe"
            )
    })
    @PostMapping
    public ResponseEntity<EstudianteResponse> createEstudiante(
            @Valid @RequestBody CreateEstudianteRequest request) {
        
        EstudianteResponse nuevoEstudiante = estudianteService.createEstudiante(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEstudiante);
    }

    @Operation(
            summary = "Actualizar estudiante",
            description = "Actualiza la información de un estudiante existente. Los campos no proporcionados no se modifican."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estudiante actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EstudianteResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante no encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "El código de estudiante ya existe"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponse> updateEstudiante(
            @Parameter(description = "ID del estudiante", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id,
            
            @Valid @RequestBody UpdateEstudianteRequest request) {
        
        EstudianteResponse estudianteActualizado = estudianteService.updateEstudiante(id, request);
        return ResponseEntity.ok(estudianteActualizado);
    }

    @Operation(
            summary = "Eliminar estudiante (soft delete)",
            description = "Desactiva un estudiante sin eliminarlo físicamente de la base de datos"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Estudiante desactivado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante no encontrado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEstudiante(
            @Parameter(description = "ID del estudiante", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        
        estudianteService.deleteEstudiante(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Eliminar estudiante permanentemente",
            description = "Elimina físicamente un estudiante de la base de datos. Esta acción no se puede deshacer."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Estudiante eliminado permanentemente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Estudiante no encontrado"
            )
    })
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteEstudiante(
            @Parameter(description = "ID del estudiante", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String id) {
        
        estudianteService.permanentDeleteEstudiante(id);
        return ResponseEntity.noContent().build();
    }
}
