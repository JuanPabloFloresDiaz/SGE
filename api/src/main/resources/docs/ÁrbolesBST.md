# Árboles BST (Binary Search Tree)

## 📚 Concepto

Un **BST (Árbol Binario de Búsqueda)** es un árbol binario con una propiedad especial:

**Regla BST**: Para cada nodo:
- Todos los valores del **subárbol izquierdo** son **menores**
- Todos los valores del **subárbol derecho** son **mayores**

Esta propiedad permite búsquedas eficientes en O(log n) promedio.

### Ejemplo visual:
```
        50
       /  \
      30   70
     / \   / \
    20 40 60 80
```
- 20, 30, 40 < 50
- 60, 70, 80 > 50

---

## 🎯 Casos de Uso en SGE API

1. **Índice de Calificaciones Ordenadas**: Búsqueda rápida por nota
2. **Estudiantes por Promedio**: Ranking ordenado
3. **Horarios Ordenados**: Búsqueda por hora de inicio
4. **Asignaturas por Código**: Búsqueda alfabética eficiente
5. **Sistema de Prioridades**: Colas con prioridad dinámica
6. **Búsqueda de Rangos**: Calificaciones entre 70-85

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: BST de Calificaciones

```java
// Nodo del BST
@Data
class NodoBST {
    private double calificacion;
    private String estudianteId;
    private String evaluacionId;
    private NodoBST izquierdo;
    private NodoBST derecho;
    
    public NodoBST(double calificacion, String estudianteId, String evaluacionId) {
        this.calificacion = calificacion;
        this.estudianteId = estudianteId;
        this.evaluacionId = evaluacionId;
    }
}

// Servicio: BST de calificaciones
@Service
public class CalificacionesBSTService {
    
    private NodoBST raiz;
    
    // Insertar calificación - O(log n) promedio
    public void insertar(double calificacion, String estudianteId, String evaluacionId) {
        raiz = insertarRecursivo(raiz, calificacion, estudianteId, evaluacionId);
    }
    
    private NodoBST insertarRecursivo(NodoBST nodo, double calificacion, 
                                      String estudianteId, String evaluacionId) {
        // Caso base: crear nuevo nodo
        if (nodo == null) {
            return new NodoBST(calificacion, estudianteId, evaluacionId);
        }
        
        // Insertar en subárbol correspondiente
        if (calificacion < nodo.getCalificacion()) {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), 
                calificacion, estudianteId, evaluacionId));
        } else if (calificacion > nodo.getCalificacion()) {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), 
                calificacion, estudianteId, evaluacionId));
        }
        // Si son iguales, podríamos manejarlo con una lista en el nodo
        
        return nodo;
    }
    
    // Buscar calificación exacta - O(log n) promedio
    public NodoBST buscar(double calificacion) {
        return buscarRecursivo(raiz, calificacion);
    }
    
    private NodoBST buscarRecursivo(NodoBST nodo, double calificacion) {
        // Caso base: no encontrado o encontrado
        if (nodo == null || nodo.getCalificacion() == calificacion) {
            return nodo;
        }
        
        // Buscar en subárbol correspondiente
        if (calificacion < nodo.getCalificacion()) {
            return buscarRecursivo(nodo.getIzquierdo(), calificacion);
        } else {
            return buscarRecursivo(nodo.getDerecho(), calificacion);
        }
    }
    
    // Buscar mínimo (nodo más a la izquierda)
    public NodoBST buscarMinimo() {
        if (raiz == null) return null;
        return buscarMinimoRecursivo(raiz);
    }
    
    private NodoBST buscarMinimoRecursivo(NodoBST nodo) {
        if (nodo.getIzquierdo() == null) {
            return nodo;
        }
        return buscarMinimoRecursivo(nodo.getIzquierdo());
    }
    
    // Buscar máximo (nodo más a la derecha)
    public NodoBST buscarMaximo() {
        if (raiz == null) return null;
        return buscarMaximoRecursivo(raiz);
    }
    
    private NodoBST buscarMaximoRecursivo(NodoBST nodo) {
        if (nodo.getDerecho() == null) {
            return nodo;
        }
        return buscarMaximoRecursivo(nodo.getDerecho());
    }
    
    // Eliminar nodo - O(log n) promedio
    public void eliminar(double calificacion) {
        raiz = eliminarRecursivo(raiz, calificacion);
    }
    
    private NodoBST eliminarRecursivo(NodoBST nodo, double calificacion) {
        if (nodo == null) {
            return null;
        }
        
        // Buscar el nodo a eliminar
        if (calificacion < nodo.getCalificacion()) {
            nodo.setIzquierdo(eliminarRecursivo(nodo.getIzquierdo(), calificacion));
        } else if (calificacion > nodo.getCalificacion()) {
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), calificacion));
        } else {
            // Nodo encontrado: 3 casos
            
            // Caso 1: Nodo hoja (sin hijos)
            if (nodo.getIzquierdo() == null && nodo.getDerecho() == null) {
                return null;
            }
            
            // Caso 2: Nodo con un hijo
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            }
            if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            
            // Caso 3: Nodo con dos hijos
            // Reemplazar con el sucesor (mínimo del subárbol derecho)
            NodoBST sucesor = buscarMinimoRecursivo(nodo.getDerecho());
            nodo.setCalificacion(sucesor.getCalificacion());
            nodo.setEstudianteId(sucesor.getEstudianteId());
            nodo.setEvaluacionId(sucesor.getEvaluacionId());
            
            // Eliminar el sucesor
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), sucesor.getCalificacion()));
        }
        
        return nodo;
    }
    
    // Recorrido in-orden (devuelve calificaciones ordenadas)
    public List<CalificacionDTO> obtenerOrdenadas() {
        List<CalificacionDTO> resultado = new ArrayList<>();
        inOrden(raiz, resultado);
        return resultado;
    }
    
    private void inOrden(NodoBST nodo, List<CalificacionDTO> resultado) {
        if (nodo == null) return;
        
        inOrden(nodo.getIzquierdo(), resultado);
        
        resultado.add(CalificacionDTO.builder()
            .calificacion(nodo.getCalificacion())
            .estudianteId(nodo.getEstudianteId())
            .evaluacionId(nodo.getEvaluacionId())
            .build());
        
        inOrden(nodo.getDerecho(), resultado);
    }
    
    // Buscar calificaciones en rango [min, max]
    public List<CalificacionDTO> buscarEnRango(double min, double max) {
        List<CalificacionDTO> resultado = new ArrayList<>();
        buscarRangoRecursivo(raiz, min, max, resultado);
        return resultado;
    }
    
    private void buscarRangoRecursivo(NodoBST nodo, double min, double max, 
                                     List<CalificacionDTO> resultado) {
        if (nodo == null) return;
        
        // Si el valor actual > min, buscar en izquierda
        if (nodo.getCalificacion() > min) {
            buscarRangoRecursivo(nodo.getIzquierdo(), min, max, resultado);
        }
        
        // Si el valor está en rango, agregarlo
        if (nodo.getCalificacion() >= min && nodo.getCalificacion() <= max) {
            resultado.add(CalificacionDTO.builder()
                .calificacion(nodo.getCalificacion())
                .estudianteId(nodo.getEstudianteId())
                .evaluacionId(nodo.getEvaluacionId())
                .build());
        }
        
        // Si el valor actual < max, buscar en derecha
        if (nodo.getCalificacion() < max) {
            buscarRangoRecursivo(nodo.getDerecho(), min, max, resultado);
        }
    }
    
    // Contar nodos en rango
    public int contarEnRango(double min, double max) {
        return contarRangoRecursivo(raiz, min, max);
    }
    
    private int contarRangoRecursivo(NodoBST nodo, double min, double max) {
        if (nodo == null) return 0;
        
        if (nodo.getCalificacion() < min) {
            return contarRangoRecursivo(nodo.getDerecho(), min, max);
        }
        
        if (nodo.getCalificacion() > max) {
            return contarRangoRecursivo(nodo.getIzquierdo(), min, max);
        }
        
        return 1 + contarRangoRecursivo(nodo.getIzquierdo(), min, max)
                 + contarRangoRecursivo(nodo.getDerecho(), min, max);
    }
    
    // Verificar si es un BST válido
    public boolean esValidoBST() {
        return esValidoBSTRecursivo(raiz, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
    
    private boolean esValidoBSTRecursivo(NodoBST nodo, double min, double max) {
        if (nodo == null) return true;
        
        // Verificar propiedad BST
        if (nodo.getCalificacion() <= min || nodo.getCalificacion() >= max) {
            return false;
        }
        
        // Verificar recursivamente
        return esValidoBSTRecursivo(nodo.getIzquierdo(), min, nodo.getCalificacion())
            && esValidoBSTRecursivo(nodo.getDerecho(), nodo.getCalificacion(), max);
    }
}

@Data
@Builder
class CalificacionDTO {
    private double calificacion;
    private String estudianteId;
    private String evaluacionId;
}

// Controlador
@RestController
@RequestMapping("/api/bst/calificaciones")
@Tag(name = "BST Calificaciones", description = "Búsquedas ordenadas de calificaciones")
public class CalificacionesBSTController {
    
    @Autowired
    private CalificacionesBSTService bstService;
    
    @Operation(summary = "Insertar calificación")
    @PostMapping("/insertar")
    public ResponseEntity<String> insertar(
            @RequestParam double calificacion,
            @RequestParam String estudianteId,
            @RequestParam String evaluacionId) {
        
        bstService.insertar(calificacion, estudianteId, evaluacionId);
        return ResponseEntity.ok("Calificación insertada");
    }
    
    @Operation(summary = "Obtener calificaciones ordenadas")
    @GetMapping("/ordenadas")
    public ResponseEntity<List<CalificacionDTO>> obtenerOrdenadas() {
        return ResponseEntity.ok(bstService.obtenerOrdenadas());
    }
    
    @Operation(summary = "Buscar calificaciones en rango")
    @GetMapping("/rango")
    public ResponseEntity<List<CalificacionDTO>> buscarRango(
            @RequestParam double min,
            @RequestParam double max) {
        
        return ResponseEntity.ok(bstService.buscarEnRango(min, max));
    }
    
    @Operation(summary = "Obtener mejor calificación")
    @GetMapping("/mejor")
    public ResponseEntity<CalificacionDTO> obtenerMejor() {
        NodoBST nodo = bstService.buscarMaximo();
        
        if (nodo == null) {
            return ResponseEntity.notFound().build();
        }
        
        CalificacionDTO dto = CalificacionDTO.builder()
            .calificacion(nodo.getCalificacion())
            .estudianteId(nodo.getEstudianteId())
            .evaluacionId(nodo.getEvaluacionId())
            .build();
        
        return ResponseEntity.ok(dto);
    }
    
    @Operation(summary = "Contar calificaciones en rango")
    @GetMapping("/contar-rango")
    public ResponseEntity<Integer> contarRango(
            @RequestParam double min,
            @RequestParam double max) {
        
        return ResponseEntity.ok(bstService.contarEnRango(min, max));
    }
}
```

### Ejemplo 2: BST con TreeSet de Java (Más Simple)

```java
// Servicio usando TreeSet (implementa BST internamente)
@Service
public class RankingEstudiantesService {
    
    // TreeSet mantiene orden automático
    private TreeSet<EstudianteRanking> ranking = new TreeSet<>(
        Comparator.comparing(EstudianteRanking::getPromedio).reversed()
            .thenComparing(EstudianteRanking::getNombre)
    );
    
    @Data
    @AllArgsConstructor
    static class EstudianteRanking {
        private String id;
        private String nombre;
        private double promedio;
    }
    
    // Agregar estudiante al ranking
    public void agregarEstudiante(String id, String nombre, double promedio) {
        ranking.add(new EstudianteRanking(id, nombre, promedio));
    }
    
    // Obtener top N estudiantes
    public List<EstudianteRanking> obtenerTop(int n) {
        return ranking.stream()
            .limit(n)
            .collect(Collectors.toList());
    }
    
    // Obtener estudiantes con promedio mayor a umbral
    public List<EstudianteRanking> obtenerMayoresA(double umbral) {
        EstudianteRanking dummy = new EstudianteRanking(null, null, umbral);
        
        return ranking.tailSet(dummy, true).stream()
            .collect(Collectors.toList());
    }
    
    // Obtener rango de estudiantes
    public List<EstudianteRanking> obtenerRango(double min, double max) {
        EstudianteRanking dummyMin = new EstudianteRanking(null, null, min);
        EstudianteRanking dummyMax = new EstudianteRanking(null, null, max);
        
        return ranking.subSet(dummyMax, true, dummyMin, true).stream()
            .collect(Collectors.toList());
    }
    
    // Obtener ranking completo
    public List<EstudianteRanking> obtenerTodos() {
        return new ArrayList<>(ranking);
    }
    
    // Obtener posición de un estudiante
    public int obtenerPosicion(String estudianteId) {
        int posicion = 1;
        
        for (EstudianteRanking estudiante : ranking) {
            if (estudiante.getId().equals(estudianteId)) {
                return posicion;
            }
            posicion++;
        }
        
        return -1; // No encontrado
    }
}
```

### Ejemplo 3: Búsqueda de Sucesor y Predecesor

```java
// Servicio: Operaciones avanzadas en BST
@Service
public class BSTOperacionesService {
    
    private NodoBST raiz;
    
    // Encontrar sucesor (siguiente mayor)
    public NodoBST encontrarSucesor(double valor) {
        NodoBST actual = buscar(valor);
        
        if (actual == null) {
            return null;
        }
        
        // Caso 1: Tiene subárbol derecho
        if (actual.getDerecho() != null) {
            return buscarMinimoRecursivo(actual.getDerecho());
        }
        
        // Caso 2: No tiene subárbol derecho
        // Buscar el ancestro cuyo hijo izquierdo contiene a actual
        NodoBST sucesor = null;
        NodoBST nodo = raiz;
        
        while (nodo != null) {
            if (valor < nodo.getCalificacion()) {
                sucesor = nodo;
                nodo = nodo.getIzquierdo();
            } else if (valor > nodo.getCalificacion()) {
                nodo = nodo.getDerecho();
            } else {
                break;
            }
        }
        
        return sucesor;
    }
    
    // Encontrar predecesor (anterior menor)
    public NodoBST encontrarPredecesor(double valor) {
        NodoBST actual = buscar(valor);
        
        if (actual == null) {
            return null;
        }
        
        // Caso 1: Tiene subárbol izquierdo
        if (actual.getIzquierdo() != null) {
            return buscarMaximoRecursivo(actual.getIzquierdo());
        }
        
        // Caso 2: No tiene subárbol izquierdo
        NodoBST predecesor = null;
        NodoBST nodo = raiz;
        
        while (nodo != null) {
            if (valor > nodo.getCalificacion()) {
                predecesor = nodo;
                nodo = nodo.getDerecho();
            } else if (valor < nodo.getCalificacion()) {
                nodo = nodo.getIzquierdo();
            } else {
                break;
            }
        }
        
        return predecesor;
    }
    
    // Encontrar k-ésimo elemento más pequeño
    public NodoBST encontrarKesimo(int k) {
        int[] contador = {0};
        return encontrarKesimoRecursivo(raiz, k, contador);
    }
    
    private NodoBST encontrarKesimoRecursivo(NodoBST nodo, int k, int[] contador) {
        if (nodo == null) {
            return null;
        }
        
        // Buscar en izquierda
        NodoBST izq = encontrarKesimoRecursivo(nodo.getIzquierdo(), k, contador);
        if (izq != null) {
            return izq;
        }
        
        // Procesar nodo actual
        contador[0]++;
        if (contador[0] == k) {
            return nodo;
        }
        
        // Buscar en derecha
        return encontrarKesimoRecursivo(nodo.getDerecho(), k, contador);
    }
    
    private NodoBST buscar(double valor) {
        // Implementación de búsqueda
        return null; // Placeholder
    }
    
    private NodoBST buscarMinimoRecursivo(NodoBST nodo) {
        if (nodo.getIzquierdo() == null) return nodo;
        return buscarMinimoRecursivo(nodo.getIzquierdo());
    }
    
    private NodoBST buscarMaximoRecursivo(NodoBST nodo) {
        if (nodo.getDerecho() == null) return nodo;
        return buscarMaximoRecursivo(nodo.getDerecho());
    }
}
```

---

## 🎓 Ventajas vs Desventajas

### ✅ Ventajas:
- Búsqueda, inserción, eliminación en **O(log n)** promedio
- Recorrido in-orden da elementos **ordenados**
- Búsquedas de **rango eficientes**
- Operaciones de min/max en O(log n)

### ❌ Desventajas:
- Puede **desbalancearse** → degradarse a O(n)
- Requiere **rebalanceo** para garantizar O(log n)
- No thread-safe por defecto

---

## 📊 Complejidad

| Operación | Promedio | Peor caso | Mejor caso |
|-----------|----------|-----------|------------|
| Búsqueda | O(log n) | O(n) | O(1) |
| Inserción | O(log n) | O(n) | O(1) |
| Eliminación | O(log n) | O(n) | O(1) |
| Mínimo/Máximo | O(log n) | O(n) | O(1) |

---

## ⚠️ Consideraciones

- Para garantizar O(log n), usar **AVL** o **Red-Black Tree**
- Java's `TreeMap` y `TreeSet` implementan BST balanceado
- BST no es la mejor opción si los datos están casi ordenados
- Para datos desordenados aleatorios, BST funciona bien