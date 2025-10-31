# Listas Doblemente Ligadas: Navegación Bidireccional

## 📚 Concepto

Una **lista doblemente ligada** es una estructura de datos donde cada nodo contiene:
- **Dato**: El valor almacenado
- **Puntero siguiente**: Referencia al nodo posterior
- **Puntero anterior**: Referencia al nodo previo

Esto permite recorrer la lista en ambas direcciones (inicio → fin y fin → inicio).

## Características

- **Navegación bidireccional**: Puedes moverte hacia adelante y hacia atrás
- **Inserción/Eliminación eficiente**: O(1) si tienes la referencia al nodo
- **Mayor uso de memoria**: Cada nodo almacena dos punteros en lugar de uno
- **Operaciones**: Insertar, eliminar, buscar, recorrer

---

## 🎯 Casos de Uso en SGE API

### 1. **Navegación de Unidades en un Curso**
Permitir a los estudiantes navegar entre unidades: Unidad 1 ← Unidad 2 → Unidad 3

### 2. **Historial de Modificaciones con Undo/Redo**
Implementar funcionalidad de deshacer/rehacer cambios en evaluaciones.

### 3. **Sistema de Reproducción de Temas**
Navegación como en un reproductor: tema anterior ← tema actual → tema siguiente

### 4. **Gestión de Ventanas de Diálogo**
Mantener un historial de pantallas visitadas con navegación adelante/atrás.

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Navegación de Unidades en un Curso

```java
// DTO: Nodo de Unidad con navegación
@Data
public class UnidadNavegable {
    private String id;
    private String titulo;
    private String descripcion;
    private Integer numero;
    
    // Referencias de navegación
    private UnidadNavegable anterior;
    private UnidadNavegable siguiente;
}

// Servicio: Construcción de lista doblemente ligada
@Service
public class UnidadNavegacionService {
    
    @Autowired
    private UnidadRepository unidadRepository;
    
    // Construir lista doblemente ligada de unidades
    public UnidadNavegable construirListaUnidades(String cursoId) {
        List<Unidad> unidades = unidadRepository
            .findByCursoIdOrderByNumero(cursoId);
        
        if (unidades.isEmpty()) return null;
        
        // Convertir a UnidadNavegable y enlazar
        List<UnidadNavegable> navegables = unidades.stream()
            .map(this::convertirANavegable)
            .collect(Collectors.toList());
        
        // Enlazar bidireccional
        for (int i = 0; i < navegables.size(); i++) {
            UnidadNavegable actual = navegables.get(i);
            
            // Enlazar con anterior
            if (i > 0) {
                actual.setAnterior(navegables.get(i - 1));
            }
            
            // Enlazar con siguiente
            if (i < navegables.size() - 1) {
                actual.setSiguiente(navegables.get(i + 1));
            }
        }
        
        return navegables.get(0); // Retorna la primera unidad
    }
    
    private UnidadNavegable convertirANavegable(Unidad unidad) {
        UnidadNavegable nav = new UnidadNavegable();
        nav.setId(unidad.getId());
        nav.setTitulo(unidad.getTitulo());
        nav.setDescripcion(unidad.getDescripcion());
        nav.setNumero(unidad.getNumero());
        return nav;
    }
}

// Controlador: Endpoints de navegación
@RestController
@RequestMapping("/api/unidades")
@Tag(name = "Unidades", description = "Navegación de unidades del curso")
public class UnidadNavegacionController {
    
    @Autowired
    private UnidadNavegacionService navegacionService;
    
    @Operation(summary = "Obtener unidad con navegación")
    @GetMapping("/{id}/navegacion")
    public ResponseEntity<UnidadNavegacionDTO> obtenerConNavegacion(
            @PathVariable String id) {
        
        UnidadNavegable unidad = navegacionService.obtenerUnidadConNavegacion(id);
        
        UnidadNavegacionDTO dto = new UnidadNavegacionDTO();
        dto.setActual(unidad);
        dto.setAnteriorId(unidad.getAnterior() != null ? 
            unidad.getAnterior().getId() : null);
        dto.setSiguienteId(unidad.getSiguiente() != null ? 
            unidad.getSiguiente().getId() : null);
        
        return ResponseEntity.ok(dto);
    }
    
    @Operation(summary = "Navegar a la unidad siguiente")
    @GetMapping("/{id}/siguiente")
    public ResponseEntity<UnidadNavegable> irASiguiente(@PathVariable String id) {
        UnidadNavegable actual = navegacionService.obtenerUnidadConNavegacion(id);
        
        if (actual.getSiguiente() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
        }
        
        return ResponseEntity.ok(actual.getSiguiente());
    }
    
    @Operation(summary = "Navegar a la unidad anterior")
    @GetMapping("/{id}/anterior")
    public ResponseEntity<UnidadNavegable> irAAnterior(@PathVariable String id) {
        UnidadNavegable actual = navegacionService.obtenerUnidadConNavegacion(id);
        
        if (actual.getAnterior() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(null);
        }
        
        return ResponseEntity.ok(actual.getAnterior());
    }
}
```

### Ejemplo 2: Sistema Undo/Redo para Calificaciones

```java
// Modelo: Acción reversible
@Data
public class AccionCalificacion {
    private String id;
    private String evaluacionId;
    private String estudianteId;
    private Double notaAnterior;
    private Double notaNueva;
    private LocalDateTime fecha;
    
    // Enlaces bidireccionales
    private AccionCalificacion anterior; // Undo
    private AccionCalificacion siguiente; // Redo
}

// Servicio: Gestor de Undo/Redo
@Service
public class CalificacionUndoRedoService {
    
    private Map<String, AccionCalificacion> estadoActual = new ConcurrentHashMap<>();
    
    // Registrar cambio (agregar al final de la lista)
    public void registrarCambio(String evaluacionId, String estudianteId,
                               Double notaAnterior, Double notaNueva) {
        
        String key = evaluacionId + "_" + estudianteId;
        AccionCalificacion actual = estadoActual.get(key);
        
        AccionCalificacion nuevaAccion = new AccionCalificacion();
        nuevaAccion.setId(UUID.randomUUID().toString());
        nuevaAccion.setEvaluacionId(evaluacionId);
        nuevaAccion.setEstudianteId(estudianteId);
        nuevaAccion.setNotaAnterior(notaAnterior);
        nuevaAccion.setNotaNueva(notaNueva);
        nuevaAccion.setFecha(LocalDateTime.now());
        
        if (actual != null) {
            actual.setSiguiente(nuevaAccion);
            nuevaAccion.setAnterior(actual);
        }
        
        estadoActual.put(key, nuevaAccion);
    }
    
    // Deshacer último cambio
    public AccionCalificacion deshacer(String evaluacionId, String estudianteId) {
        String key = evaluacionId + "_" + estudianteId;
        AccionCalificacion actual = estadoActual.get(key);
        
        if (actual == null || actual.getAnterior() == null) {
            return null; // No hay nada que deshacer
        }
        
        AccionCalificacion anterior = actual.getAnterior();
        estadoActual.put(key, anterior);
        
        return anterior;
    }
    
    // Rehacer cambio
    public AccionCalificacion rehacer(String evaluacionId, String estudianteId) {
        String key = evaluacionId + "_" + estudianteId;
        AccionCalificacion actual = estadoActual.get(key);
        
        if (actual == null || actual.getSiguiente() == null) {
            return null; // No hay nada que rehacer
        }
        
        AccionCalificacion siguiente = actual.getSiguiente();
        estadoActual.put(key, siguiente);
        
        return siguiente;
    }
}

// Controlador: Endpoints de Undo/Redo
@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionUndoController {
    
    @Autowired
    private CalificacionUndoRedoService undoRedoService;
    
    @PostMapping("/undo")
    public ResponseEntity<AccionCalificacion> deshacer(
            @RequestParam String evaluacionId,
            @RequestParam String estudianteId) {
        
        AccionCalificacion accion = undoRedoService.deshacer(
            evaluacionId, estudianteId);
        
        if (accion == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(accion);
    }
    
    @PostMapping("/redo")
    public ResponseEntity<AccionCalificacion> rehacer(
            @RequestParam String evaluacionId,
            @RequestParam String estudianteId) {
        
        AccionCalificacion accion = undoRedoService.rehacer(
            evaluacionId, estudianteId);
        
        if (accion == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.ok(accion);
    }
}
```

### Ejemplo 3: Implementación Manual de Nodo Doble

```java
// Clase genérica de nodo doblemente ligado
@Data
public class NodoDoble<T> {
    private T dato;
    private NodoDoble<T> anterior;
    private NodoDoble<T> siguiente;
    
    public NodoDoble(T dato) {
        this.dato = dato;
        this.anterior = null;
        this.siguiente = null;
    }
}

// Lista doblemente ligada genérica
public class ListaDoble<T> {
    private NodoDoble<T> cabeza;
    private NodoDoble<T> cola;
    private int tamaño;
    
    // Insertar al final
    public void insertarAlFinal(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            nuevo.setAnterior(cola);
            cola = nuevo;
        }
        tamaño++;
    }
    
    // Insertar al inicio
    public void insertarAlInicio(T dato) {
        NodoDoble<T> nuevo = new NodoDoble<>(dato);
        
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            nuevo.setSiguiente(cabeza);
            cabeza.setAnterior(nuevo);
            cabeza = nuevo;
        }
        tamaño++;
    }
    
    // Eliminar por dato
    public boolean eliminar(T dato) {
        NodoDoble<T> actual = cabeza;
        
        while (actual != null) {
            if (actual.getDato().equals(dato)) {
                if (actual.getAnterior() != null) {
                    actual.getAnterior().setSiguiente(actual.getSiguiente());
                } else {
                    cabeza = actual.getSiguiente();
                }
                
                if (actual.getSiguiente() != null) {
                    actual.getSiguiente().setAnterior(actual.getAnterior());
                } else {
                    cola = actual.getAnterior();
                }
                
                tamaño--;
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }
    
    // Recorrer hacia adelante
    public List<T> recorrerAdelante() {
        List<T> lista = new ArrayList<>();
        NodoDoble<T> actual = cabeza;
        
        while (actual != null) {
            lista.add(actual.getDato());
            actual = actual.getSiguiente();
        }
        
        return lista;
    }
    
    // Recorrer hacia atrás
    public List<T> recorrerAtras() {
        List<T> lista = new ArrayList<>();
        NodoDoble<T> actual = cola;
        
        while (actual != null) {
            lista.add(actual.getDato());
            actual = actual.getAnterior();
        }
        
        return lista;
    }
}
```

---

## 🎓 Ventajas

1. **Navegación bidireccional**: Movimiento en ambas direcciones
2. **Eliminación eficiente**: O(1) con referencia al nodo
3. **Flexibilidad**: Inserción en cualquier posición
4. **Undo/Redo**: Ideal para historial de acciones

## ⚠️ Desventajas

- Mayor uso de memoria (2 punteros por nodo)
- Más compleja de implementar que lista simple
- Mantenimiento de dos punteros requiere cuidado

## 📊 Complejidad

| Operación | Complejidad |
|-----------|-------------|
| Insertar al inicio/final | O(1) |
| Eliminar con referencia | O(1) |
| Búsqueda | O(n) |
| Acceso por índice | O(n) |
