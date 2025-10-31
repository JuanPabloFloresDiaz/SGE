# Listas Ligadas: Almacenamiento Dinámico con Apuntadores

## 📚 Concepto

Las **listas ligadas** (o enlazadas) son estructuras de datos dinámicas donde cada elemento (nodo) contiene un dato y una referencia (puntero) al siguiente elemento. A diferencia de los arrays, no requieren memoria contigua y permiten inserciones/eliminaciones eficientes.

## Tipos Principales de Listas Ligadas

### Lista Simplemente Ligada
Cada nodo contiene un único apuntador que señala exclusivamente al siguiente nodo de la secuencia. Es la implementación más básica y común.

**Operaciones:** O(1) inserción al inicio, O(n) búsqueda

### Lista Doblemente Ligada
Cada nodo mantiene dos apuntadores: uno hacia el nodo siguiente y otro hacia el nodo anterior, permitiendo navegación bidireccional.

**Operaciones:** O(1) inserción/eliminación en ambos extremos, navegación bidireccional

### Lista Circular
El último nodo de la lista apunta de regreso al primer nodo, creando un ciclo cerrado que permite recorridos infinitos.

**Uso:** Round-robin scheduling, buffers circulares

---

## 🎯 Casos de Uso en SGE API

### 1. **Historial de Cambios en Calificaciones**
Mantener un historial ordenado cronológicamente de cambios en las calificaciones de un estudiante.

### 2. **Cola de Inscripciones Pendientes**
Procesar solicitudes de inscripción a cursos en orden de llegada (FIFO).

### 3. **Navegación de Temas en una Unidad**
Permitir avanzar y retroceder entre temas de una unidad (tema anterior ← actual → tema siguiente).

### 4. **Lista de Asistencia por Fecha**
Enlazar registros de asistencia de un estudiante ordenados por fecha.

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Lista Simplemente Ligada - Historial de Calificaciones

```java
// Modelo: Nodo del historial
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionHistorial {
    private LocalDateTime fecha;
    private Double notaAnterior;
    private Double notaNueva;
    private String motivo;
    private CalificacionHistorial siguiente; // Puntero al siguiente cambio
}

// Servicio: Gestión del historial
@Service
public class CalificacionHistorialService {
    
    // Agregar un cambio al inicio (O(1))
    public CalificacionHistorial agregarCambio(
            CalificacionHistorial cabeza,
            Double notaAnterior,
            Double notaNueva,
            String motivo) {
        
        CalificacionHistorial nuevoCambio = new CalificacionHistorial(
            LocalDateTime.now(),
            notaAnterior,
            notaNueva,
            motivo,
            cabeza // El nuevo nodo apunta a la antigua cabeza
        );
        
        return nuevoCambio; // Retorna la nueva cabeza
    }
    
    // Obtener historial completo
    public List<CalificacionHistorial> obtenerHistorial(CalificacionHistorial cabeza) {
        List<CalificacionHistorial> historial = new ArrayList<>();
        CalificacionHistorial actual = cabeza;
        
        while (actual != null) {
            historial.add(actual);
            actual = actual.getSiguiente();
        }
        
        return historial;
    }
}
```

### Ejemplo 2: Lista Doblemente Ligada - Navegación de Temas

```java
// Entidad: Tema con navegación bidireccional
@Entity
@Table(name = "temas")
@Data
public class Tema {
    @Id
    private String id;
    
    private String titulo;
    private String descripcion;
    
    @ManyToOne
    @JoinColumn(name = "unidad_id")
    private Unidad unidad;
    
    // Referencias bidireccionales (no mapeadas en BD)
    @Transient
    private Tema anterior;
    
    @Transient
    private Tema siguiente;
}

// Servicio: Construcción de lista doblemente ligada
@Service
public class TemaNavegacionService {
    
    @Autowired
    private TemaRepository temaRepository;
    
    // Construir lista doblemente ligada de temas de una unidad
    public Tema construirListaTemas(String unidadId) {
        List<Tema> temas = temaRepository.findByUnidadIdOrderByNumero(unidadId);
        
        if (temas.isEmpty()) return null;
        
        // Enlazar temas bidireccional
        for (int i = 0; i < temas.size(); i++) {
            Tema actual = temas.get(i);
            
            if (i > 0) {
                actual.setAnterior(temas.get(i - 1));
            }
            
            if (i < temas.size() - 1) {
                actual.setSiguiente(temas.get(i + 1));
            }
        }
        
        return temas.get(0); // Retorna el primer tema
    }
}

// Controlador: Navegación de temas
@RestController
@RequestMapping("/api/temas")
public class TemaNavegacionController {
    
    @Autowired
    private TemaNavegacionService navegacionService;
    
    @GetMapping("/{id}/siguiente")
    public ResponseEntity<TemaDTO> obtenerSiguiente(@PathVariable String id) {
        Tema tema = navegacionService.obtenerTemaConNavegacion(id);
        
        if (tema.getSiguiente() == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertirADTO(tema.getSiguiente()));
    }
    
    @GetMapping("/{id}/anterior")
    public ResponseEntity<TemaDTO> obtenerAnterior(@PathVariable String id) {
        Tema tema = navegacionService.obtenerTemaConNavegacion(id);
        
        if (tema.getAnterior() == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(convertirADTO(tema.getAnterior()));
    }
}
```

### Ejemplo 3: Cola de Inscripciones (Lista Simple como Queue)

```java
// DTO: Solicitud de inscripción
@Data
public class SolicitudInscripcion {
    private String estudianteId;
    private String cursoId;
    private LocalDateTime fechaSolicitud;
    private SolicitudInscripcion siguiente;
}

// Servicio: Cola de inscripciones
@Service
public class InscripcionColaService {
    
    private SolicitudInscripcion cabeza = null;
    private SolicitudInscripcion cola = null;
    
    // Agregar solicitud al final (enqueue)
    public void agregarSolicitud(String estudianteId, String cursoId) {
        SolicitudInscripcion nueva = new SolicitudInscripcion();
        nueva.setEstudianteId(estudianteId);
        nueva.setCursoId(cursoId);
        nueva.setFechaSolicitud(LocalDateTime.now());
        nueva.setSiguiente(null);
        
        if (cola == null) {
            cabeza = nueva;
            cola = nueva;
        } else {
            cola.setSiguiente(nueva);
            cola = nueva;
        }
    }
    
    // Procesar siguiente solicitud (dequeue)
    public SolicitudInscripcion procesarSiguiente() {
        if (cabeza == null) return null;
        
        SolicitudInscripcion solicitud = cabeza;
        cabeza = cabeza.getSiguiente();
        
        if (cabeza == null) {
            cola = null;
        }
        
        return solicitud;
    }
}
```

---

## 🎓 Ventajas en Spring Boot

1. **Flexibilidad**: Fácil inserción/eliminación sin reorganizar memoria
2. **Eficiencia**: O(1) para operaciones en los extremos
3. **Navegación**: Ideal para relaciones ordenadas (anterior/siguiente)
4. **Memoria Dinámica**: Crece según necesidad

## ⚠️ Consideraciones

- **Persistencia**: Las listas enlazadas suelen ser estructuras en memoria, no se persisten directamente en BD
- **JPA Relations**: Para relaciones persistentes, usar `@OneToMany` y `@ManyToOne`
- **Caché**: Útil para construir listas en memoria desde datos de BD
- **Complejidad**: O(n) para búsquedas y acceso por índice