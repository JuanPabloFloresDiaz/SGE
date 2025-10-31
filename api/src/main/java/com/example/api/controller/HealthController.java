package com.example.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Endpoints para verificar el estado de la API")
public class HealthController {

    @Operation(
            summary = "Verificar estado de la API",
            description = "Retorna el estado actual de la API y la hora del servidor"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API funcionando correctamente")
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "SGE API está funcionando correctamente");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Información de la API",
            description = "Retorna información general sobre la API"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información obtenida exitosamente")
    })
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> info = new HashMap<>();
        info.put("name", "SGE API - Sistema de Gestión Educativa");
        info.put("description", "API REST para la gestión de estudiantes, profesores, cursos y evaluaciones");
        info.put("version", "1.0.0");
        info.put("documentation", "/swagger-ui.html");
        return ResponseEntity.ok(info);
    }
}
