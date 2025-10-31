# Estructuras LIFO y FIFO: Pilas y Colas

## 📚 Conceptos

### LIFO (Last In, First Out) - Pilas (Stack)
El **último elemento en entrar** es el **primero en salir**. Como una pila de platos: solo puedes tomar el de arriba.

**Operaciones principales:**
- `push()`: Agregar elemento al tope
- `pop()`: Eliminar y retornar elemento del tope
- `peek()`: Ver elemento del tope sin eliminarlo
- `isEmpty()`: Verificar si está vacía

### FIFO (First In, First Out) - Colas (Queue)
El **primer elemento en entrar** es el **primero en salir**. Como una fila de personas: el primero en llegar es el primero en ser atendido.

**Operaciones principales:**
- `enqueue()`: Agregar elemento al final
- `dequeue()`: Eliminar y retornar elemento del frente
- `peek()`: Ver elemento del frente sin eliminarlo
- `isEmpty()`: Verificar si está vacía

---

## 🎯 Casos de Uso en SGE API

### LIFO (Pilas)

1. **Historial de Navegación**: Botón "Atrás" en la aplicación
2. **Deshacer Operaciones**: Undo de cambios en calificaciones
3. **Evaluación de Expresiones**: Cálculo de promedios con paréntesis
4. **Validación de Sintaxis**: Verificar balance de llaves/paréntesis en fórmulas

### FIFO (Colas)

1. **Cola de Inscripciones**: Procesar solicitudes por orden de llegada
2. **Notificaciones Pendientes**: Enviar notificaciones en orden
3. **Tareas Programadas**: Procesar reportes en orden de solicitud
4. **Sistema de Turnos**: Atención a estudiantes en orden de llegada

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: LIFO - Historial de Navegación

```java
// Servicio: Historial de navegación con Pila
@Service
public class NavegacionHistorialService {
    
    // Usar Stack de Java
    private Map<String, Stack<String>> historialPorUsuario = new ConcurrentHashMap<>();
    
    // Agregar página al historial
    public void visitarPagina(String usuarioId, String paginaUrl) {
        Stack<String> historial = historialPorUsuario
            .computeIfAbsent(usuarioId, k -> new Stack<>());
        
        historial.push(paginaUrl);
    }
    
    // Navegar atrás (pop)
    public String volverAtras(String usuarioId) {
        Stack<String> historial = historialPorUsuario.get(usuarioId);
        
        if (historial == null || historial.isEmpty()) {
            return null;
        }
        
        // Sacar la página actual
        historial.pop();
        
        // Retornar la página anterior (sin sacarla)
        return historial.isEmpty() ? null : historial.peek();
    }
    
    // Ver página actual sin eliminarla
    public String paginaActual(String usuarioId) {
        Stack<String> historial = historialPorUsuario.get(usuarioId);
        return (historial == null || historial.isEmpty()) ? null : historial.peek();
    }
}

// Controlador
@RestController
@RequestMapping("/api/navegacion")
public class NavegacionController {
    
    @Autowired
    private NavegacionHistorialService historialService;
    
    @PostMapping("/visitar")
    public ResponseEntity<Void> visitarPagina(
            @RequestParam String usuarioId,
            @RequestParam String paginaUrl) {
        
        historialService.visitarPagina(usuarioId, paginaUrl);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/atras")
    public ResponseEntity<String> volverAtras(@RequestParam String usuarioId) {
        String paginaAnterior = historialService.volverAtras(usuarioId);
        
        if (paginaAnterior == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(paginaAnterior);
    }
}
```

### Ejemplo 2: LIFO - Sistema de Deshacer (Undo Stack)

```java
// Modelo: Acción reversible
@Data
public class Accion {
    private String tipo; // "crear", "modificar", "eliminar"
    private String entidad;
    private Object estadoAnterior;
    private Object estadoNuevo;
    private LocalDateTime timestamp;
}

// Servicio: Gestor de Undo
@Service
public class UndoService {
    
    private Map<String, Stack<Accion>> pilasPorUsuario = new ConcurrentHashMap<>();
    
    // Registrar acción (push)
    public void registrarAccion(String usuarioId, Accion accion) {
        Stack<Accion> pila = pilasPorUsuario
            .computeIfAbsent(usuarioId, k -> new Stack<>());
        
        accion.setTimestamp(LocalDateTime.now());
        pila.push(accion);
    }
    
    // Deshacer última acción (pop)
    public Accion deshacer(String usuarioId) {
        Stack<Accion> pila = pilasPorUsuario.get(usuarioId);
        
        if (pila == null || pila.isEmpty()) {
            throw new IllegalStateException("No hay acciones para deshacer");
        }
        
        return pila.pop();
    }
    
    // Ver última acción sin deshacerla
    public Accion verUltimaAccion(String usuarioId) {
        Stack<Accion> pila = pilasPorUsuario.get(usuarioId);
        return (pila == null || pila.isEmpty()) ? null : pila.peek();
    }
}
```

### Ejemplo 3: FIFO - Cola de Inscripciones

```java
// Modelo: Solicitud de inscripción
@Data
public class SolicitudInscripcion {
    private String id;
    private String estudianteId;
    private String cursoId;
    private LocalDateTime fechaSolicitud;
    private String estado; // "pendiente", "procesada", "rechazada"
}

// Servicio: Cola de inscripciones
@Service
public class InscripcionColaService {
    
    // Usar Queue de Java (LinkedList implementa Queue)
    private Map<String, Queue<SolicitudInscripcion>> colasPorCurso 
        = new ConcurrentHashMap<>();
    
    // Agregar solicitud a la cola (enqueue)
    public void agregarSolicitud(String cursoId, String estudianteId) {
        Queue<SolicitudInscripcion> cola = colasPorCurso
            .computeIfAbsent(cursoId, k -> new LinkedList<>());
        
        SolicitudInscripcion solicitud = new SolicitudInscripcion();
        solicitud.setId(UUID.randomUUID().toString());
        solicitud.setEstudianteId(estudianteId);
        solicitud.setCursoId(cursoId);
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstado("pendiente");
        
        cola.offer(solicitud); // offer() agrega al final
    }
    
    // Procesar siguiente solicitud (dequeue)
    public SolicitudInscripcion procesarSiguiente(String cursoId) {
        Queue<SolicitudInscripcion> cola = colasPorCurso.get(cursoId);
        
        if (cola == null || cola.isEmpty()) {
            return null;
        }
        
        return cola.poll(); // poll() remueve y retorna el primero
    }
    
    // Ver siguiente sin procesar
    public SolicitudInscripcion verSiguiente(String cursoId) {
        Queue<SolicitudInscripcion> cola = colasPorCurso.get(cursoId);
        return (cola == null || cola.isEmpty()) ? null : cola.peek();
    }
    
    // Obtener tamaño de la cola
    public int obtenerTamanoCola(String cursoId) {
        Queue<SolicitudInscripcion> cola = colasPorCurso.get(cursoId);
        return (cola == null) ? 0 : cola.size();
    }
}

// Controlador
@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "Inscripciones", description = "Gestión de cola de inscripciones")
public class InscripcionColaController {
    
    @Autowired
    private InscripcionColaService colaService;
    
    @Autowired
    private InscripcionService inscripcionService;
    
    @Operation(summary = "Agregar solicitud a la cola")
    @PostMapping("/solicitar")
    public ResponseEntity<String> solicitarInscripcion(
            @RequestParam String cursoId,
            @RequestParam String estudianteId) {
        
        colaService.agregarSolicitud(cursoId, estudianteId);
        return ResponseEntity.ok("Solicitud agregada a la cola");
    }
    
    @Operation(summary = "Procesar siguiente solicitud en la cola")
    @PostMapping("/procesar/{cursoId}")
    public ResponseEntity<SolicitudInscripcion> procesarSiguiente(
            @PathVariable String cursoId) {
        
        SolicitudInscripcion solicitud = colaService.procesarSiguiente(cursoId);
        
        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Procesar la inscripción
        inscripcionService.procesarInscripcion(solicitud);
        
        return ResponseEntity.ok(solicitud);
    }
    
    @Operation(summary = "Ver estado de la cola")
    @GetMapping("/cola/{cursoId}")
    public ResponseEntity<ColaEstadoDTO> verEstadoCola(@PathVariable String cursoId) {
        ColaEstadoDTO estado = new ColaEstadoDTO();
        estado.setCursoId(cursoId);
        estado.setTamanoCola(colaService.obtenerTamanoCola(cursoId));
        estado.setSiguiente(colaService.verSiguiente(cursoId));
        
        return ResponseEntity.ok(estado);
    }
}
```

### Ejemplo 4: FIFO - Sistema de Notificaciones

```java
// Modelo: Notificación
@Data
public class Notificacion {
    private String id;
    private String destinatarioId;
    private String tipo; // "info", "warning", "success", "error"
    private String mensaje;
    private LocalDateTime fechaCreacion;
    private boolean enviada;
}

// Servicio: Cola de notificaciones
@Service
@Slf4j
public class NotificacionColaService {
    
    private Queue<Notificacion> colaNotificaciones = new PriorityQueue<>(
        Comparator.comparing(Notificacion::getFechaCreacion)
    );
    
    @Autowired
    private NotificacionRepository notificacionRepository;
    
    // Agregar notificación a la cola
    public void encolarNotificacion(String destinatarioId, String tipo, String mensaje) {
        Notificacion notif = new Notificacion();
        notif.setId(UUID.randomUUID().toString());
        notif.setDestinatarioId(destinatarioId);
        notif.setTipo(tipo);
        notif.setMensaje(mensaje);
        notif.setFechaCreacion(LocalDateTime.now());
        notif.setEnviada(false);
        
        colaNotificaciones.offer(notif);
        log.info("Notificación encolada para usuario: {}", destinatarioId);
    }
    
    // Procesar notificaciones pendientes (batch)
    @Scheduled(fixedDelay = 5000) // Cada 5 segundos
    public void procesarCola() {
        int procesadas = 0;
        
        while (!colaNotificaciones.isEmpty() && procesadas < 10) {
            Notificacion notif = colaNotificaciones.poll();
            
            try {
                // Enviar notificación (email, push, etc.)
                enviarNotificacion(notif);
                notif.setEnviada(true);
                notificacionRepository.save(notif);
                procesadas++;
            } catch (Exception e) {
                log.error("Error al enviar notificación: {}", e.getMessage());
                // Reencolar si falla
                colaNotificaciones.offer(notif);
            }
        }
        
        if (procesadas > 0) {
            log.info("Procesadas {} notificaciones", procesadas);
        }
    }
    
    private void enviarNotificacion(Notificacion notif) {
        // Lógica de envío (email, SMS, push, etc.)
        log.info("Enviando notificación a {}: {}", 
            notif.getDestinatarioId(), notif.getMensaje());
    }
}
```

---

## 🎓 Comparación

| Característica | LIFO (Pila) | FIFO (Cola) |
|----------------|-------------|-------------|
| Orden de salida | Último en entrar | Primero en entrar |
| Uso típico | Deshacer, historial | Tareas, turnos |
| Estructura | Stack | Queue |
| Clase Java | `Stack<T>` | `LinkedList<T>` o `ArrayDeque<T>` |
| Operación principal | `push()/pop()` | `offer()/poll()` |

## ⚠️ Consideraciones

- **Stack** es thread-safe pero más lento. Para single-thread usar `ArrayDeque`
- **LinkedList** implementa Queue pero es menos eficiente que `ArrayDeque`
- Para colas de prioridad usar `PriorityQueue`
- En producción considerar usar colas distribuidas (RabbitMQ, Kafka)

## 📊 Complejidad

Ambas estructuras tienen:
- **Inserción**: O(1)
- **Eliminación**: O(1)
- **Búsqueda**: O(n)
- **Acceso**: O(1) solo al extremo relevante