# Tablas Hash (HashMap)

## 📚 Concepto

Una **Tabla Hash** es una estructura de datos que permite almacenar y recuperar valores mediante una **clave única**. Utiliza una **función hash** para convertir la clave en un índice del array interno.

### Características principales:
- **Acceso O(1)**: Búsqueda, inserción y eliminación en tiempo constante (promedio)
- **Clave-Valor**: Cada elemento tiene una clave única asociada a un valor
- **Función Hash**: Convierte claves en índices del array
- **Colisiones**: Cuando dos claves generan el mismo índice (se resuelve con listas enlazadas o rehashing)
- **Factor de carga**: Ratio entre elementos y capacidad (cuando es alto, se redimensiona)

### Funcionamiento:
```
Clave → Función Hash → Índice → Valor
"EST001" → hash() → 42 → Estudiante{nombre: "Juan"}
```

---

## 🎯 Casos de Uso en SGE API

1. **Cache de Estudiantes**: Búsqueda rápida de estudiantes por ID
2. **Índice de Cursos**: Acceso instantáneo a cursos por código
3. **Sesiones de Usuario**: Almacenar sesiones activas por token
4. **Configuraciones**: Parámetros del sistema por clave
5. **Caché de Calificaciones**: Calificaciones calculadas por estudiante
6. **Mapeo de Roles**: Permisos asociados a cada rol

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Caché de Estudiantes

```java
// Servicio: Caché de estudiantes usando HashMap
@Service
@Slf4j
public class EstudianteCacheService {
    
    // HashMap para almacenar estudiantes en memoria
    private Map<String, Estudiante> cache = new ConcurrentHashMap<>();
    
    @Autowired
    private EstudianteRepository estudianteRepository;
    
    // Obtener estudiante (primero del caché, luego de BD)
    public Estudiante obtenerEstudiante(String id) {
        // O(1) - Buscar en caché
        if (cache.containsKey(id)) {
            log.info("Estudiante {} encontrado en caché", id);
            return cache.get(id);
        }
        
        // Si no está en caché, buscar en BD
        log.info("Buscando estudiante {} en base de datos", id);
        Estudiante estudiante = estudianteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado"));
        
        // Guardar en caché para futuras consultas
        cache.put(id, estudiante);
        
        return estudiante;
    }
    
    // Actualizar estudiante (BD y caché)
    public Estudiante actualizarEstudiante(String id, Estudiante estudianteActualizado) {
        Estudiante guardado = estudianteRepository.save(estudianteActualizado);
        
        // Actualizar caché - O(1)
        cache.put(id, guardado);
        log.info("Estudiante {} actualizado en caché", id);
        
        return guardado;
    }
    
    // Eliminar estudiante
    public void eliminarEstudiante(String id) {
        estudianteRepository.deleteById(id);
        
        // Remover de caché - O(1)
        cache.remove(id);
        log.info("Estudiante {} eliminado del caché", id);
    }
    
    // Limpiar caché completo
    public void limpiarCache() {
        cache.clear();
        log.info("Caché limpiado. {} estudiantes removidos", cache.size());
    }
    
    // Obtener estadísticas del caché
    public CacheStats obtenerEstadisticas() {
        return new CacheStats(cache.size(), cache.keySet());
    }
}

// DTO para estadísticas
@Data
@AllArgsConstructor
public class CacheStats {
    private int totalElementos;
    private Set<String> claves;
}

// Controlador
@RestController
@RequestMapping("/api/cache/estudiantes")
@Tag(name = "Caché", description = "Gestión de caché de estudiantes")
public class EstudianteCacheController {
    
    @Autowired
    private EstudianteCacheService cacheService;
    
    @Operation(summary = "Obtener estudiante (con caché)")
    @GetMapping("/{id}")
    public ResponseEntity<Estudiante> obtenerEstudiante(@PathVariable String id) {
        return ResponseEntity.ok(cacheService.obtenerEstudiante(id));
    }
    
    @Operation(summary = "Limpiar caché")
    @DeleteMapping("/cache")
    public ResponseEntity<String> limpiarCache() {
        cacheService.limpiarCache();
        return ResponseEntity.ok("Caché limpiado exitosamente");
    }
    
    @Operation(summary = "Estadísticas del caché")
    @GetMapping("/cache/stats")
    public ResponseEntity<CacheStats> obtenerEstadisticas() {
        return ResponseEntity.ok(cacheService.obtenerEstadisticas());
    }
}
```

### Ejemplo 2: Índice de Cursos por Código

```java
// Servicio: Búsqueda rápida de cursos
@Service
public class CursoIndexService {
    
    // HashMap: código → Curso
    private Map<String, Curso> indicePorCodigo = new HashMap<>();
    
    // HashMap: nombre → Lista de cursos
    private Map<String, List<Curso>> indicePorNombre = new HashMap<>();
    
    @Autowired
    private CursoRepository cursoRepository;
    
    @PostConstruct
    public void inicializarIndices() {
        cargarIndices();
    }
    
    // Cargar todos los cursos en índices
    public void cargarIndices() {
        List<Curso> cursos = cursoRepository.findAll();
        
        indicePorCodigo.clear();
        indicePorNombre.clear();
        
        for (Curso curso : cursos) {
            // Índice por código - O(1)
            indicePorCodigo.put(curso.getCodigo(), curso);
            
            // Índice por nombre - O(1)
            indicePorNombre
                .computeIfAbsent(curso.getNombre(), k -> new ArrayList<>())
                .add(curso);
        }
        
        log.info("Índices cargados: {} cursos", cursos.size());
    }
    
    // Buscar curso por código - O(1)
    public Curso buscarPorCodigo(String codigo) {
        Curso curso = indicePorCodigo.get(codigo);
        
        if (curso == null) {
            throw new EntityNotFoundException("Curso no encontrado: " + codigo);
        }
        
        return curso;
    }
    
    // Buscar cursos por nombre - O(1)
    public List<Curso> buscarPorNombre(String nombre) {
        return indicePorNombre.getOrDefault(nombre, Collections.emptyList());
    }
    
    // Verificar si existe curso - O(1)
    public boolean existeCodigo(String codigo) {
        return indicePorCodigo.containsKey(codigo);
    }
    
    // Agregar curso al índice
    public void agregarCurso(Curso curso) {
        indicePorCodigo.put(curso.getCodigo(), curso);
        indicePorNombre
            .computeIfAbsent(curso.getNombre(), k -> new ArrayList<>())
            .add(curso);
    }
    
    // Obtener todos los códigos - O(1) para obtener el conjunto
    public Set<String> obtenerTodosCodigos() {
        return indicePorCodigo.keySet();
    }
}
```

### Ejemplo 3: Sistema de Configuración con HashMap

```java
// Enum: Tipos de configuración
public enum ConfigKey {
    MAX_ESTUDIANTES_POR_CURSO("max.estudiantes.curso", "30"),
    NOTA_MINIMA_APROBACION("nota.minima.aprobacion", "60"),
    PORCENTAJE_ASISTENCIA_MINIMA("asistencia.minima", "80"),
    DIAS_LIMITE_INSCRIPCION("inscripcion.dias.limite", "7"),
    EMAIL_NOTIFICACIONES("email.notificaciones", "true");
    
    private final String key;
    private final String defaultValue;
    
    ConfigKey(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }
    
    public String getKey() { return key; }
    public String getDefaultValue() { return defaultValue; }
}

// Servicio: Gestor de configuraciones
@Service
public class ConfiguracionService {
    
    // HashMap para almacenar configuraciones
    private Map<String, String> configuraciones = new ConcurrentHashMap<>();
    
    @Autowired
    private ConfiguracionRepository configuracionRepository;
    
    @PostConstruct
    public void cargarConfiguraciones() {
        // Cargar desde BD
        List<ConfiguracionEntity> configs = configuracionRepository.findAll();
        
        for (ConfiguracionEntity config : configs) {
            configuraciones.put(config.getClave(), config.getValor());
        }
        
        // Cargar valores por defecto si no existen
        for (ConfigKey key : ConfigKey.values()) {
            configuraciones.putIfAbsent(key.getKey(), key.getDefaultValue());
        }
        
        log.info("Configuraciones cargadas: {}", configuraciones.size());
    }
    
    // Obtener configuración - O(1)
    public String obtenerConfig(ConfigKey key) {
        return configuraciones.getOrDefault(key.getKey(), key.getDefaultValue());
    }
    
    // Obtener configuración como Integer
    public Integer obtenerConfigInt(ConfigKey key) {
        String valor = obtenerConfig(key);
        return Integer.parseInt(valor);
    }
    
    // Obtener configuración como Boolean
    public Boolean obtenerConfigBoolean(ConfigKey key) {
        String valor = obtenerConfig(key);
        return Boolean.parseBoolean(valor);
    }
    
    // Actualizar configuración - O(1)
    public void actualizarConfig(ConfigKey key, String valor) {
        configuraciones.put(key.getKey(), valor);
        
        // Persistir en BD
        ConfiguracionEntity entity = configuracionRepository
            .findByClave(key.getKey())
            .orElse(new ConfiguracionEntity());
        
        entity.setClave(key.getKey());
        entity.setValor(valor);
        configuracionRepository.save(entity);
        
        log.info("Configuración actualizada: {} = {}", key.getKey(), valor);
    }
    
    // Obtener todas las configuraciones
    public Map<String, String> obtenerTodasConfiguraciones() {
        return new HashMap<>(configuraciones); // Copia defensiva
    }
}

// Controlador
@RestController
@RequestMapping("/api/configuracion")
@Tag(name = "Configuración", description = "Gestión de parámetros del sistema")
public class ConfiguracionController {
    
    @Autowired
    private ConfiguracionService configService;
    
    @Operation(summary = "Obtener todas las configuraciones")
    @GetMapping
    public ResponseEntity<Map<String, String>> obtenerTodas() {
        return ResponseEntity.ok(configService.obtenerTodasConfiguraciones());
    }
    
    @Operation(summary = "Obtener configuración específica")
    @GetMapping("/{key}")
    public ResponseEntity<String> obtenerConfig(@PathVariable String key) {
        try {
            ConfigKey configKey = ConfigKey.valueOf(key.toUpperCase());
            return ResponseEntity.ok(configService.obtenerConfig(configKey));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Clave de configuración inválida");
        }
    }
    
    @Operation(summary = "Actualizar configuración")
    @PutMapping("/{key}")
    public ResponseEntity<String> actualizarConfig(
            @PathVariable String key,
            @RequestParam String valor) {
        
        try {
            ConfigKey configKey = ConfigKey.valueOf(key.toUpperCase());
            configService.actualizarConfig(configKey, valor);
            return ResponseEntity.ok("Configuración actualizada");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Clave de configuración inválida");
        }
    }
}
```

### Ejemplo 4: Contador de Frecuencias (Análisis de Datos)

```java
// Servicio: Estadísticas de asistencia por estudiante
@Service
public class EstadisticasAsistenciaService {
    
    @Autowired
    private AsistenciaRepository asistenciaRepository;
    
    // Contar asistencias por estudiante usando HashMap
    public Map<String, Integer> contarAsistenciasPorEstudiante(String cursoId) {
        List<Asistencia> asistencias = asistenciaRepository.findByCursoId(cursoId);
        
        // HashMap para contar - O(n) donde n = número de asistencias
        Map<String, Integer> contadores = new HashMap<>();
        
        for (Asistencia asistencia : asistencias) {
            if ("presente".equals(asistencia.getEstado())) {
                String estudianteId = asistencia.getEstudianteId();
                
                // Incrementar contador - O(1)
                contadores.put(estudianteId, 
                    contadores.getOrDefault(estudianteId, 0) + 1);
            }
        }
        
        return contadores;
    }
    
    // Calcular porcentajes de asistencia
    public Map<String, Double> calcularPorcentajesAsistencia(String cursoId) {
        Map<String, Integer> contadores = contarAsistenciasPorEstudiante(cursoId);
        
        int totalClases = asistenciaRepository.countClasesByCurso(cursoId);
        
        Map<String, Double> porcentajes = new HashMap<>();
        
        for (Map.Entry<String, Integer> entry : contadores.entrySet()) {
            double porcentaje = (entry.getValue() * 100.0) / totalClases;
            porcentajes.put(entry.getKey(), porcentaje);
        }
        
        return porcentajes;
    }
    
    // Encontrar estudiantes con baja asistencia
    public List<String> estudiantesConBajaAsistencia(String cursoId, double umbral) {
        Map<String, Double> porcentajes = calcularPorcentajesAsistencia(cursoId);
        
        return porcentajes.entrySet().stream()
            .filter(entry -> entry.getValue() < umbral)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
}
```

---

## 🎓 Tipos de HashMap en Java

| Tipo | Thread-Safe | Orden | Uso |
|------|-------------|-------|-----|
| `HashMap` | ❌ No | No garantizado | Uso general |
| `ConcurrentHashMap` | ✅ Sí | No garantizado | Aplicaciones multi-thread |
| `LinkedHashMap` | ❌ No | Inserción | Mantener orden |
| `TreeMap` | ❌ No | Ordenado (keys) | Claves ordenadas |
| `Hashtable` | ✅ Sí (legacy) | No garantizado | **Obsoleto** |

---

## ⚠️ Consideraciones

### Ventajas:
- **O(1) promedio** para búsqueda, inserción y eliminación
- Muy eficiente para grandes volúmenes de datos
- Flexible: cualquier objeto puede ser clave (si implementa hashCode() y equals())

### Desventajas:
- **No mantiene orden** (usar LinkedHashMap si se necesita)
- Requiere buena función hash para evitar colisiones
- Consume más memoria que arrays

### Buenas prácticas:
- Usar **claves inmutables** (String, Integer, etc.)
- Implementar correctamente `hashCode()` y `equals()` en claves personalizadas
- Establecer capacidad inicial si se conoce el tamaño aproximado
- Usar `ConcurrentHashMap` en entornos multi-thread

---

## 📊 Complejidad

| Operación | Promedio | Peor caso |
|-----------|----------|-----------|
| Búsqueda (`get`) | O(1) | O(n) |
| Inserción (`put`) | O(1) | O(n) |
| Eliminación (`remove`) | O(1) | O(n) |
| Verificar existencia (`containsKey`) | O(1) | O(n) |

**Nota**: El peor caso O(n) ocurre cuando hay muchas colisiones, pero es raro con buenas funciones hash.

---

## 🔥 Ejemplo de Colisiones

```java
// Mal ejemplo: función hash que causa colisiones
class MalaClave {
    private String valor;
    
    @Override
    public int hashCode() {
        return 42; // ¡Siempre el mismo hash! Todas las claves colisionan
    }
}

// Buen ejemplo: usar Objects.hash()
class BuenaClave {
    private String id;
    private String tipo;
    
    @Override
    public int hashCode() {
        return Objects.hash(id, tipo); // Buena distribución
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BuenaClave that = (BuenaClave) o;
        return Objects.equals(id, that.id) && Objects.equals(tipo, that.tipo);
    }
}
```