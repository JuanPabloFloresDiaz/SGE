# Estructuras de Datos Lineales

## 📚 Concepto

Las **estructuras de datos lineales** son aquellas donde los elementos están organizados **secuencialmente**, uno después del otro. Cada elemento tiene un **predecesor** (excepto el primero) y un **sucesor** (excepto el último).

### Características:
- Elementos organizados en **secuencia**
- Acceso **secuencial** o directo (según tipo)
- **Un solo nivel** de profundidad
- Fáciles de recorrer de inicio a fin

### Ejemplos principales:
1. **Arrays** (Arreglos)
2. **Linked Lists** (Listas ligadas)
3. **Stacks** (Pilas - LIFO)
4. **Queues** (Colas - FIFO)

---

## 🎯 Comparación de Estructuras Lineales

| Estructura | Orden | Acceso | Inserción | Eliminación | Uso típico |
|------------|-------|--------|-----------|-------------|------------|
| **Array** | Fijo | O(1) directo | O(n) | O(n) | Acceso rápido por índice |
| **ArrayList** | Dinámico | O(1) directo | O(n) promedio | O(n) | Lista dinámica |
| **LinkedList** | Secuencial | O(n) | O(1) | O(1) | Inserciones frecuentes |
| **Stack** | LIFO | O(1) solo tope | O(1) | O(1) | Undo, navegación |
| **Queue** | FIFO | O(1) solo frente | O(1) | O(1) | Procesamiento por orden |
| **Deque** | Ambos extremos | O(1) | O(1) | O(1) | Flexibilidad en extremos |

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Arrays - Gestión de Calificaciones

```java
// Servicio: Arrays para calificaciones
@Service
public class CalificacionesArrayService {
    
    // Array de calificaciones de un estudiante
    private double[] calificaciones;
    private int size;
    
    public CalificacionesArrayService() {
        this.calificaciones = new double[10]; // Capacidad inicial
        this.size = 0;
    }
    
    // Agregar calificación
    public void agregarCalificacion(double nota) {
        if (size >= calificaciones.length) {
            redimensionar();
        }
        calificaciones[size++] = nota;
    }
    
    // Redimensionar array (duplicar capacidad)
    private void redimensionar() {
        double[] nuevoArray = new double[calificaciones.length * 2];
        System.arraycopy(calificaciones, 0, nuevoArray, 0, size);
        calificaciones = nuevoArray;
    }
    
    // Obtener calificación por índice
    public double obtenerCalificacion(int indice) {
        if (indice < 0 || indice >= size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango");
        }
        return calificaciones[indice];
    }
    
    // Calcular promedio
    public double calcularPromedio() {
        if (size == 0) return 0.0;
        
        double suma = 0;
        for (int i = 0; i < size; i++) {
            suma += calificaciones[i];
        }
        return suma / size;
    }
    
    // Obtener calificación máxima
    public double obtenerMaxima() {
        if (size == 0) throw new IllegalStateException("No hay calificaciones");
        
        double max = calificaciones[0];
        for (int i = 1; i < size; i++) {
            if (calificaciones[i] > max) {
                max = calificaciones[i];
            }
        }
        return max;
    }
    
    // Ordenar calificaciones (Bubble Sort)
    public void ordenar() {
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - i - 1; j++) {
                if (calificaciones[j] > calificaciones[j + 1]) {
                    // Intercambiar
                    double temp = calificaciones[j];
                    calificaciones[j] = calificaciones[j + 1];
                    calificaciones[j + 1] = temp;
                }
            }
        }
    }
    
    // Obtener todas las calificaciones
    public double[] obtenerTodas() {
        return Arrays.copyOf(calificaciones, size);
    }
}
```

### Ejemplo 2: ArrayList - Lista Dinámica de Estudiantes

```java
// Servicio: ArrayList para gestión dinámica
@Service
@Slf4j
public class EstudiantesListService {
    
    @Autowired
    private EstudianteRepository estudianteRepository;
    
    // Lista dinámica en memoria
    private ArrayList<Estudiante> estudiantesActivos;
    
    @PostConstruct
    public void inicializar() {
        cargarEstudiantes();
    }
    
    private void cargarEstudiantes() {
        List<Estudiante> todos = estudianteRepository.findAll();
        estudiantesActivos = new ArrayList<>(todos.size());
        
        for (Estudiante est : todos) {
            if (est.isActivo()) {
                estudiantesActivos.add(est);
            }
        }
        
        log.info("Cargados {} estudiantes activos", estudiantesActivos.size());
    }
    
    // Agregar estudiante (O(1) amortizado)
    public void agregarEstudiante(Estudiante estudiante) {
        estudiantesActivos.add(estudiante);
    }
    
    // Insertar en posición específica (O(n))
    public void insertarEn(int posicion, Estudiante estudiante) {
        if (posicion < 0 || posicion > estudiantesActivos.size()) {
            throw new IndexOutOfBoundsException();
        }
        estudiantesActivos.add(posicion, estudiante);
    }
    
    // Eliminar estudiante (O(n))
    public boolean eliminarEstudiante(Long id) {
        return estudiantesActivos.removeIf(est -> est.getId().equals(id));
    }
    
    // Buscar por índice (O(1))
    public Estudiante obtenerPorIndice(int indice) {
        return estudiantesActivos.get(indice);
    }
    
    // Buscar por matrícula (O(n))
    public Estudiante buscarPorMatricula(String matricula) {
        for (Estudiante est : estudiantesActivos) {
            if (est.getMatricula().equals(matricula)) {
                return est;
            }
        }
        return null;
    }
    
    // Filtrar por promedio
    public List<Estudiante> filtrarPorPromedio(double minimo, double maximo) {
        List<Estudiante> filtrados = new ArrayList<>();
        
        for (Estudiante est : estudiantesActivos) {
            if (est.getPromedio() >= minimo && est.getPromedio() <= maximo) {
                filtrados.add(est);
            }
        }
        
        return filtrados;
    }
    
    // Ordenar por promedio (usando ArrayList.sort)
    public void ordenarPorPromedio() {
        estudiantesActivos.sort(
            Comparator.comparingDouble(Estudiante::getPromedio).reversed()
        );
    }
}
```

### Ejemplo 3: LinkedList - Historial de Acciones

```java
// Nodo de lista ligada
@Data
class NodoAccion {
    private String accion;
    private LocalDateTime timestamp;
    private NodoAccion siguiente;
    
    public NodoAccion(String accion) {
        this.accion = accion;
        this.timestamp = LocalDateTime.now();
        this.siguiente = null;
    }
}

// Servicio: Lista ligada para historial
@Service
public class HistorialAccionesService {
    
    private NodoAccion cabeza;
    private int tamaño;
    
    public HistorialAccionesService() {
        this.cabeza = null;
        this.tamaño = 0;
    }
    
    // Agregar acción al inicio (O(1))
    public void registrarAccion(String accion) {
        NodoAccion nuevoNodo = new NodoAccion(accion);
        nuevoNodo.setSiguiente(cabeza);
        cabeza = nuevoNodo;
        tamaño++;
    }
    
    // Obtener última acción (O(1))
    public String obtenerUltimaAccion() {
        if (cabeza == null) {
            throw new IllegalStateException("No hay acciones en el historial");
        }
        return cabeza.getAccion();
    }
    
    // Eliminar última acción (O(1))
    public void eliminarUltimaAccion() {
        if (cabeza == null) return;
        
        cabeza = cabeza.getSiguiente();
        tamaño--;
    }
    
    // Listar todas las acciones (O(n))
    public List<String> listarAcciones() {
        List<String> acciones = new ArrayList<>();
        NodoAccion actual = cabeza;
        
        while (actual != null) {
            acciones.add(actual.getAccion() + " - " + actual.getTimestamp());
            actual = actual.getSiguiente();
        }
        
        return acciones;
    }
    
    // Buscar acción (O(n))
    public boolean contieneAccion(String accion) {
        NodoAccion actual = cabeza;
        
        while (actual != null) {
            if (actual.getAccion().contains(accion)) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        
        return false;
    }
    
    public int getTamaño() {
        return tamaño;
    }
}
```

### Ejemplo 4: Stack y Queue - Sistema de Notificaciones

```java
// Servicio: Stack (LIFO) y Queue (FIFO)
@Service
@Slf4j
public class NotificacionesService {
    
    // Stack para undo de acciones
    private Stack<String> pilaUndo;
    
    // Queue para notificaciones pendientes
    private Queue<Notificacion> colaNotificaciones;
    
    // Deque para notificaciones prioritarias
    private Deque<Notificacion> dequeNotificaciones;
    
    public NotificacionesService() {
        this.pilaUndo = new Stack<>();
        this.colaNotificaciones = new LinkedList<>();
        this.dequeNotificaciones = new ArrayDeque<>();
    }
    
    // --- Stack (LIFO) para Undo ---
    
    public void registrarAccion(String accion) {
        pilaUndo.push(accion);
        log.info("Acción registrada: {}", accion);
    }
    
    public String deshacer() {
        if (pilaUndo.isEmpty()) {
            throw new IllegalStateException("No hay acciones para deshacer");
        }
        
        String accion = pilaUndo.pop();
        log.info("Deshaciendo: {}", accion);
        return accion;
    }
    
    public String verUltimaAccion() {
        if (pilaUndo.isEmpty()) return null;
        return pilaUndo.peek(); // No elimina
    }
    
    // --- Queue (FIFO) para notificaciones ---
    
    public void agregarNotificacion(Notificacion notificacion) {
        colaNotificaciones.offer(notificacion);
        log.info("Notificación encolada: {}", notificacion.getMensaje());
    }
    
    public Notificacion procesarSiguienteNotificacion() {
        Notificacion notif = colaNotificaciones.poll();
        
        if (notif != null) {
            log.info("Procesando notificación: {}", notif.getMensaje());
        }
        
        return notif;
    }
    
    public int notificacionesPendientes() {
        return colaNotificaciones.size();
    }
    
    // --- Deque para notificaciones con prioridad ---
    
    public void agregarNotificacionPrioritaria(Notificacion notificacion) {
        // Alta prioridad: al frente
        if (notificacion.getPrioridad() == Prioridad.ALTA) {
            dequeNotificaciones.addFirst(notificacion);
        } else {
            // Normal: al final
            dequeNotificaciones.addLast(notificacion);
        }
    }
    
    public Notificacion procesarDesdeDeque() {
        return dequeNotificaciones.pollFirst();
    }
}

@Data
@AllArgsConstructor
class Notificacion {
    private String mensaje;
    private Prioridad prioridad;
    private LocalDateTime timestamp;
}

enum Prioridad {
    ALTA, MEDIA, BAJA
}
```

### Ejemplo 5: Controlador REST Completo

```java
// Controlador: Operaciones con estructuras lineales
@RestController
@RequestMapping("/api/estructuras-lineales")
@Tag(name = "Estructuras Lineales", description = "Ejemplos de estructuras de datos lineales")
public class EstructurasLinealesController {
    
    @Autowired
    private CalificacionesArrayService calificacionesService;
    
    @Autowired
    private EstudiantesListService estudiantesListService;
    
    @Autowired
    private HistorialAccionesService historialService;
    
    @Autowired
    private NotificacionesService notificacionesService;
    
    // --- Arrays ---
    
    @Operation(summary = "Agregar calificación")
    @PostMapping("/calificaciones")
    public ResponseEntity<String> agregarCalificacion(@RequestParam double nota) {
        calificacionesService.agregarCalificacion(nota);
        return ResponseEntity.ok("Calificación agregada");
    }
    
    @Operation(summary = "Calcular promedio")
    @GetMapping("/calificaciones/promedio")
    public ResponseEntity<Double> calcularPromedio() {
        return ResponseEntity.ok(calificacionesService.calcularPromedio());
    }
    
    @Operation(summary = "Obtener todas las calificaciones")
    @GetMapping("/calificaciones")
    public ResponseEntity<double[]> obtenerCalificaciones() {
        return ResponseEntity.ok(calificacionesService.obtenerTodas());
    }
    
    // --- ArrayList ---
    
    @Operation(summary = "Filtrar estudiantes por promedio")
    @GetMapping("/estudiantes/filtrar")
    public ResponseEntity<List<Estudiante>> filtrarEstudiantes(
            @RequestParam double minimo,
            @RequestParam double maximo) {
        return ResponseEntity.ok(
            estudiantesListService.filtrarPorPromedio(minimo, maximo)
        );
    }
    
    @Operation(summary = "Ordenar estudiantes por promedio")
    @PostMapping("/estudiantes/ordenar")
    public ResponseEntity<String> ordenarEstudiantes() {
        estudiantesListService.ordenarPorPromedio();
        return ResponseEntity.ok("Estudiantes ordenados");
    }
    
    // --- LinkedList ---
    
    @Operation(summary = "Registrar acción en historial")
    @PostMapping("/historial")
    public ResponseEntity<String> registrarAccion(@RequestParam String accion) {
        historialService.registrarAccion(accion);
        return ResponseEntity.ok("Acción registrada");
    }
    
    @Operation(summary = "Listar historial de acciones")
    @GetMapping("/historial")
    public ResponseEntity<List<String>> listarHistorial() {
        return ResponseEntity.ok(historialService.listarAcciones());
    }
    
    // --- Stack ---
    
    @Operation(summary = "Deshacer última acción (Stack)")
    @PostMapping("/undo")
    public ResponseEntity<String> deshacer() {
        String accion = notificacionesService.deshacer();
        return ResponseEntity.ok("Deshecho: " + accion);
    }
    
    // --- Queue ---
    
    @Operation(summary = "Agregar notificación (Queue)")
    @PostMapping("/notificaciones")
    public ResponseEntity<String> agregarNotificacion(@RequestParam String mensaje) {
        Notificacion notif = new Notificacion(
            mensaje, 
            Prioridad.MEDIA, 
            LocalDateTime.now()
        );
        notificacionesService.agregarNotificacion(notif);
        return ResponseEntity.ok("Notificación agregada");
    }
    
    @Operation(summary = "Procesar siguiente notificación")
    @PostMapping("/notificaciones/procesar")
    public ResponseEntity<Notificacion> procesarNotificacion() {
        Notificacion notif = notificacionesService.procesarSiguienteNotificacion();
        return ResponseEntity.ok(notif);
    }
    
    @Operation(summary = "Notificaciones pendientes")
    @GetMapping("/notificaciones/pendientes")
    public ResponseEntity<Integer> notificacionesPendientes() {
        return ResponseEntity.ok(notificacionesService.notificacionesPendientes());
    }
}
```

---

## 🎯 Casos de Uso en SGE API

### Arrays:
- **Calificaciones** de un estudiante en un curso
- **Horarios** de clases (7 días, 12 bloques horarios)
- **Estadísticas mensuales** (12 meses)

### ArrayList:
- **Lista de estudiantes** (dinámico, acceso por índice)
- **Cursos disponibles** (agregar/eliminar frecuentemente)
- **Búsquedas y filtros** (promedio, carrera, etc.)

### LinkedList:
- **Historial de calificaciones** (inserciones al inicio)
- **Log de acciones** del usuario
- **Lista de reproducción** de contenido educativo

### Stack (LIFO):
- **Undo/Redo** de acciones
- **Navegación** (volver página anterior)
- **Validación de paréntesis** en fórmulas

### Queue (FIFO):
- **Cola de inscripciones**
- **Procesamiento de solicitudes**
- **Notificaciones** por enviar

---

## 📊 Complejidad de Operaciones

| Operación | Array | ArrayList | LinkedList | Stack | Queue |
|-----------|-------|-----------|------------|-------|-------|
| Acceso por índice | **O(1)** | **O(1)** | O(n) | - | - |
| Búsqueda | O(n) | O(n) | O(n) | O(n) | O(n) |
| Inserción al inicio | O(n) | O(n) | **O(1)** | **O(1)** | **O(1)** |
| Inserción al final | O(1) | **O(1)** amort. | O(n) | **O(1)** | **O(1)** |
| Eliminación inicio | O(n) | O(n) | **O(1)** | **O(1)** | **O(1)** |
| Eliminación final | O(1) | **O(1)** | O(n) | **O(1)** | - |

---

## ⚠️ Consideraciones

### Arrays:
- ✅ Acceso rápido por índice: **O(1)**
- ✅ Memoria contigua (cache-friendly)
- ❌ Tamaño **fijo**
- ❌ Inserciones/eliminaciones costosas: **O(n)**

### ArrayList:
- ✅ Tamaño **dinámico**
- ✅ Acceso rápido: **O(1)**
- ❌ Redimensionamiento costoso
- ❌ Inserciones en medio: **O(n)**

### LinkedList:
- ✅ Inserciones/eliminaciones al inicio: **O(1)**
- ✅ Sin redimensionamiento
- ❌ Acceso por índice: **O(n)**
- ❌ Más memoria (punteros)

### Stack:
- ✅ Push/Pop: **O(1)**
- ✅ Perfecto para LIFO (undo, navegación)
- ❌ Solo acceso al tope

### Queue:
- ✅ Enqueue/Dequeue: **O(1)**
- ✅ Perfecto para FIFO (procesamiento por orden)
- ❌ Solo acceso al frente

---

## 💡 Decisión: ¿Cuál usar?

```
¿Necesitas acceso rápido por índice?
│
├─ SÍ → ¿Tamaño fijo o dinámico?
│   ├─ FIJO → **Array**
│   └─ DINÁMICO → **ArrayList**
│
└─ NO → ¿Muchas inserciones/eliminaciones?
    │
    ├─ AL INICIO/FIN → **LinkedList**
    │
    └─ ¿Orden específico?
        ├─ LIFO (último primero) → **Stack**
        ├─ FIFO (primero primero) → **Queue**
        └─ AMBOS LADOS → **Deque**
```

### Reglas generales:
- **Array/ArrayList**: Cuando necesitas **acceso rápido** por índice
- **LinkedList**: Cuando tienes **muchas inserciones/eliminaciones**
- **Stack**: Para operaciones **LIFO** (undo, navegación)
- **Queue**: Para procesamiento **FIFO** (por orden de llegada)
- **Deque**: Cuando necesitas **flexibilidad en ambos extremos**