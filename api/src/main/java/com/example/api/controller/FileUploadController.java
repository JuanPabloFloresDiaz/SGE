package com.example.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.api.service.StorageService;
import com.example.api.service.StorageService.FileCategory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador para manejar la subida y eliminación de archivos.
 */
@RestController
@RequestMapping("/api/files")
@Tag(name = "Archivos", description = "API para gestión de archivos e imágenes")
public class FileUploadController {

    private final StorageService storageService;

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(
        summary = "Subir archivo", 
        description = "Sube un archivo a una categoría específica. El archivo se guardará en una carpeta según la categoría."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo subido exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Archivo inválido o categoría no especificada")
    })
    public ResponseEntity<Map<String, String>> uploadFile(
            @Parameter(
                description = "Archivo a subir (imagen, PDF, documento, etc.)", 
                required = true,
                content = @Content(mediaType = "multipart/form-data")
            )
            @RequestParam("file") MultipartFile file,
            
            @Parameter(
                description = "Categoría donde se guardará el archivo", 
                required = true,
                schema = @Schema(
                    type = "string",
                    allowableValues = {"ACTIVIDADES", "ESTUDIANTES", "CURSOS", "ASIGNATURAS", "CLASES", "UNIDADES", "TEMAS", "EVALUACIONES", "PROFESORES", "USUARIOS", "MATERIALES", "OTROS"},
                    example = "ACTIVIDADES"
                )
            )
            @RequestParam("category") String categoryStr) {

        try {
            // Convertir string a enum
            FileCategory category;
            try {
                category = FileCategory.valueOf(categoryStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Categoría inválida. Valores permitidos: " + 
                                          "ACTIVIDADES, ESTUDIANTES, CURSOS, ASIGNATURAS, CLASES, UNIDADES, TEMAS, EVALUACIONES, PROFESORES, USUARIOS, MATERIALES, OTROS");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Guardar archivo
            String filePath = storageService.storeFile(file, category);

            // Construir URL de acceso
            String fileUrl = "/uploads/" + filePath;

            Map<String, String> response = new HashMap<>();
            response.put("fileName", file.getOriginalFilename());
            response.put("filePath", filePath); // Path relativo (para guardar en DB)
            response.put("fileUrl", fileUrl);   // URL pública para acceder
            response.put("category", category.getFolderName());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al subir el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Eliminar archivo", description = "Elimina un archivo del sistema de archivos usando su path relativo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Archivo eliminado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Archivo no encontrado")
    })
    public ResponseEntity<Map<String, String>> deleteFile(
            @Parameter(description = "Path relativo del archivo", example = "actividades/abc123.jpg", required = true)
            @RequestParam("filePath") String filePath) {

        try {
            boolean deleted = storageService.deleteFile(filePath);

            Map<String, String> response = new HashMap<>();
            if (deleted) {
                response.put("message", "Archivo eliminado exitosamente");
                response.put("filePath", filePath);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Archivo no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error al eliminar el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
