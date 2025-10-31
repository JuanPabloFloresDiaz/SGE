# 📚 Guía de Swagger/OpenAPI para SGE API

## ¿Qué es Swagger?

Swagger UI es una herramienta que proporciona documentación interactiva de tu API REST. Permite:
- 📖 Ver todos los endpoints disponibles
- 🧪 Probar los endpoints directamente desde el navegador
- 📝 Ver los modelos de datos (request/response)
- 🔍 Ver descripciones detalladas de cada operación

## Acceso

Una vez que la aplicación esté corriendo (`mvn spring-boot:run`):

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

## Cómo usar Swagger UI

### 1. Ver endpoints disponibles

Los endpoints están organizados por **tags** (categorías):
- **Health**: Endpoints para verificar el estado de la API

### 2. Probar un endpoint

1. Haz clic en el endpoint que quieras probar
2. Haz clic en el botón **"Try it out"**
3. Completa los parámetros necesarios (si los hay)
4. Haz clic en **"Execute"**
5. Verás la respuesta directamente en el navegador

### 3. Ver modelos de datos

En la parte inferior de Swagger UI verás la sección **"Schemas"** con todos los modelos de datos (DTOs) utilizados por la API.

## Ejemplo: Probar el endpoint /api/health

1. Abre http://localhost:8080/swagger-ui.html
2. Busca el tag **"Health"**
3. Haz clic en **GET /api/health**
4. Haz clic en **"Try it out"**
5. Haz clic en **"Execute"**
6. Verás una respuesta como:
```json
{
  "status": "UP",
  "message": "SGE API está funcionando correctamente",
  "timestamp": "2025-10-31T16:00:00",
  "version": "1.0.0"
}
```

## Documentar tus propios endpoints

Cuando crees nuevos controladores, usa estas anotaciones:

### En la clase del controlador:

```java
@RestController
@RequestMapping("/api/estudiantes")
@Tag(name = "Estudiantes", description = "Endpoints para gestión de estudiantes")
public class EstudianteController {
    // ...
}
```

### En los métodos:

```java
@Operation(
    summary = "Obtener todos los estudiantes",
    description = "Retorna una lista paginada de todos los estudiantes"
)
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Estudiantes obtenidos exitosamente"),
    @ApiResponse(responseCode = "404", description = "No se encontraron estudiantes")
})
@GetMapping
public ResponseEntity<List<EstudianteDTO>> getAllEstudiantes() {
    // ...
}
```

### En los parámetros:

```java
@GetMapping("/{id}")
public ResponseEntity<EstudianteDTO> getEstudianteById(
    @Parameter(description = "ID del estudiante", required = true)
    @PathVariable String id
) {
    // ...
}
```

### En los DTOs:

```java
@Schema(description = "Datos de un estudiante")
public class EstudianteDTO {
    
    @Schema(description = "ID único del estudiante", example = "123e4567-e89b-12d3-a456-426614174000")
    private String id;
    
    @Schema(description = "Código del estudiante", example = "EST001")
    private String codigoEstudiante;
    
    @Schema(description = "Fecha de nacimiento", example = "2000-01-15")
    private LocalDate fechaNacimiento;
}
```

## Anotaciones útiles

| Anotación | Uso | Ubicación |
|-----------|-----|-----------|
| `@Tag` | Agrupar endpoints | Clase del controlador |
| `@Operation` | Describir operación | Método del controlador |
| `@ApiResponses` | Describir respuestas posibles | Método del controlador |
| `@ApiResponse` | Describir una respuesta específica | Dentro de @ApiResponses |
| `@Parameter` | Describir parámetro | Parámetro del método |
| `@Schema` | Describir modelo de datos | Clase DTO o campos |

## Configuración

La configuración de Swagger está en:
- **Código:** `src/main/java/com/example/api/config/OpenApiConfig.java`
- **Properties:** `src/main/resources/application.properties`

## Tips

1. **Siempre documenta tus endpoints**: Ayuda al equipo a entender qué hace cada uno
2. **Usa ejemplos**: Los ejemplos en los DTOs ayudan a entender el formato esperado
3. **Describe los códigos de respuesta**: No solo el 200, también 400, 404, 500, etc.
4. **Agrupa endpoints relacionados**: Usa tags para organizar mejor la documentación

## Recursos adicionales

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [Swagger Annotations Guide](https://github.com/swagger-api/swagger-core/wiki/Annotations)
- [OpenAPI Specification](https://swagger.io/specification/)
