# Árboles AVL

## 📚 Concepto

Un **Árbol AVL** es un **BST auto-balanceado** que garantiza O(log n) en todas las operaciones.

**Propiedad AVL**: Para cada nodo, la **diferencia de altura** entre sus subárboles izquierdo y derecho es **máximo 1**.

```
Factor de Balance (FB) = Altura(izquierdo) - Altura(derecho)
FB debe estar en rango: [-1, 0, 1]
```

Cuando se viola esta propiedad tras una inserción/eliminación, se realizan **rotaciones** para rebalancear.

### Tipos de Rotaciones:

1. **Rotación Simple Derecha (RR)**: Caso izquierda-izquierda
2. **Rotación Simple Izquierda (LL)**: Caso derecha-derecha
3. **Rotación Doble Izquierda-Derecha (LR)**: Caso izquierda-derecha
4. **Rotación Doble Derecha-Izquierda (RL)**: Caso derecha-izquierda

---

## 🎯 Casos de Uso en SGE API

1. **Índice de Estudiantes**: Gran volumen con búsquedas frecuentes
2. **Cache Ordenado**: Mantener datos ordenados con inserciones/eliminaciones
3. **Ranking en Tiempo Real**: Actualización constante de posiciones
4. **Búsquedas de Rango**: Calificaciones, promedios, asistencias
5. **Sistema de Prioridades Dinámico**: Cola con cambios frecuentes
6. **Base de Datos en Memoria**: Índices ordenados eficientes

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: AVL de Estudiantes por Promedio

```java
// Nodo AVL
@Data
class NodoAVL {
    private double promedio;
    private String estudianteId;
    private String nombre;
    private int altura;
    private NodoAVL izquierdo;
    private NodoAVL derecho;
    
    public NodoAVL(double promedio, String estudianteId, String nombre) {
        this.promedio = promedio;
        this.estudianteId = estudianteId;
        this.nombre = nombre;
        this.altura = 1; // Hoja tiene altura 1
    }
}

// Servicio: AVL Tree
@Service
@Slf4j
public class EstudiantesAVLService {
    
    private NodoAVL raiz;
    
    // Obtener altura del nodo
    private int altura(NodoAVL nodo) {
        return nodo == null ? 0 : nodo.getAltura();
    }
    
    // Calcular factor de balance
    private int factorBalance(NodoAVL nodo) {
        return nodo == null ? 0 : altura(nodo.getIzquierdo()) - altura(nodo.getDerecho());
    }
    
    // Actualizar altura del nodo
    private void actualizarAltura(NodoAVL nodo) {
        if (nodo != null) {
            nodo.setAltura(1 + Math.max(altura(nodo.getIzquierdo()), 
                                       altura(nodo.getDerecho())));
        }
    }
    
    // Rotación simple derecha
    private NodoAVL rotacionDerecha(NodoAVL y) {
        NodoAVL x = y.getIzquierdo();
        NodoAVL T2 = x.getDerecho();
        
        // Realizar rotación
        x.setDerecho(y);
        y.setIzquierdo(T2);
        
        // Actualizar alturas
        actualizarAltura(y);
        actualizarAltura(x);
        
        log.debug("Rotación derecha aplicada en nodo con promedio: {}", y.getPromedio());
        return x; // Nueva raíz
    }
    
    // Rotación simple izquierda
    private NodoAVL rotacionIzquierda(NodoAVL x) {
        NodoAVL y = x.getDerecho();
        NodoAVL T2 = y.getIzquierdo();
        
        // Realizar rotación
        y.setIzquierdo(x);
        x.setDerecho(T2);
        
        // Actualizar alturas
        actualizarAltura(x);
        actualizarAltura(y);
        
        log.debug("Rotación izquierda aplicada en nodo con promedio: {}", x.getPromedio());
        return y; // Nueva raíz
    }
    
    // Insertar estudiante (con rebalanceo automático)
    public void insertar(double promedio, String estudianteId, String nombre) {
        raiz = insertarRecursivo(raiz, promedio, estudianteId, nombre);
    }
    
    private NodoAVL insertarRecursivo(NodoAVL nodo, double promedio, 
                                      String estudianteId, String nombre) {
        // 1. Inserción BST normal
        if (nodo == null) {
            return new NodoAVL(promedio, estudianteId, nombre);
        }
        
        if (promedio < nodo.getPromedio()) {
            nodo.setIzquierdo(insertarRecursivo(nodo.getIzquierdo(), 
                promedio, estudianteId, nombre));
        } else if (promedio > nodo.getPromedio()) {
            nodo.setDerecho(insertarRecursivo(nodo.getDerecho(), 
                promedio, estudianteId, nombre));
        } else {
            return nodo; // Duplicados no permitidos
        }
        
        // 2. Actualizar altura
        actualizarAltura(nodo);
        
        // 3. Calcular factor de balance
        int balance = factorBalance(nodo);
        
        // 4. Casos de desbalanceo y rotaciones
        
        // Caso Izquierda-Izquierda (LL)
        if (balance > 1 && promedio < nodo.getIzquierdo().getPromedio()) {
            return rotacionDerecha(nodo);
        }
        
        // Caso Derecha-Derecha (RR)
        if (balance < -1 && promedio > nodo.getDerecho().getPromedio()) {
            return rotacionIzquierda(nodo);
        }
        
        // Caso Izquierda-Derecha (LR)
        if (balance > 1 && promedio > nodo.getIzquierdo().getPromedio()) {
            nodo.setIzquierdo(rotacionIzquierda(nodo.getIzquierdo()));
            return rotacionDerecha(nodo);
        }
        
        // Caso Derecha-Izquierda (RL)
        if (balance < -1 && promedio < nodo.getDerecho().getPromedio()) {
            nodo.setDerecho(rotacionDerecha(nodo.getDerecho()));
            return rotacionIzquierda(nodo);
        }
        
        return nodo; // Ya está balanceado
    }
    
    // Buscar estudiante - O(log n) garantizado
    public NodoAVL buscar(double promedio) {
        return buscarRecursivo(raiz, promedio);
    }
    
    private NodoAVL buscarRecursivo(NodoAVL nodo, double promedio) {
        if (nodo == null || nodo.getPromedio() == promedio) {
            return nodo;
        }
        
        if (promedio < nodo.getPromedio()) {
            return buscarRecursivo(nodo.getIzquierdo(), promedio);
        } else {
            return buscarRecursivo(nodo.getDerecho(), promedio);
        }
    }
    
    // Eliminar estudiante (con rebalanceo)
    public void eliminar(double promedio) {
        raiz = eliminarRecursivo(raiz, promedio);
    }
    
    private NodoAVL eliminarRecursivo(NodoAVL nodo, double promedio) {
        // 1. Eliminación BST normal
        if (nodo == null) {
            return null;
        }
        
        if (promedio < nodo.getPromedio()) {
            nodo.setIzquierdo(eliminarRecursivo(nodo.getIzquierdo(), promedio));
        } else if (promedio > nodo.getPromedio()) {
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), promedio));
        } else {
            // Nodo encontrado
            
            // Caso: nodo con 0 o 1 hijo
            if (nodo.getIzquierdo() == null) {
                return nodo.getDerecho();
            } else if (nodo.getDerecho() == null) {
                return nodo.getIzquierdo();
            }
            
            // Caso: nodo con 2 hijos
            NodoAVL sucesor = buscarMinimo(nodo.getDerecho());
            nodo.setPromedio(sucesor.getPromedio());
            nodo.setEstudianteId(sucesor.getEstudianteId());
            nodo.setNombre(sucesor.getNombre());
            nodo.setDerecho(eliminarRecursivo(nodo.getDerecho(), sucesor.getPromedio()));
        }
        
        // 2. Actualizar altura
        actualizarAltura(nodo);
        
        // 3. Rebalancear
        int balance = factorBalance(nodo);
        
        // Caso LL
        if (balance > 1 && factorBalance(nodo.getIzquierdo()) >= 0) {
            return rotacionDerecha(nodo);
        }
        
        // Caso RR
        if (balance < -1 && factorBalance(nodo.getDerecho()) <= 0) {
            return rotacionIzquierda(nodo);
        }
        
        // Caso LR
        if (balance > 1 && factorBalance(nodo.getIzquierdo()) < 0) {
            nodo.setIzquierdo(rotacionIzquierda(nodo.getIzquierdo()));
            return rotacionDerecha(nodo);
        }
        
        // Caso RL
        if (balance < -1 && factorBalance(nodo.getDerecho()) > 0) {
            nodo.setDerecho(rotacionDerecha(nodo.getDerecho()));
            return rotacionIzquierda(nodo);
        }
        
        return nodo;
    }
    
    private NodoAVL buscarMinimo(NodoAVL nodo) {
        while (nodo.getIzquierdo() != null) {
            nodo = nodo.getIzquierdo();
        }
        return nodo;
    }
    
    // Obtener estudiantes ordenados por promedio (in-orden)
    public List<EstudiantePromedioDTO> obtenerOrdenados() {
        List<EstudiantePromedioDTO> resultado = new ArrayList<>();
        inOrden(raiz, resultado);
        return resultado;
    }
    
    private void inOrden(NodoAVL nodo, List<EstudiantePromedioDTO> resultado) {
        if (nodo == null) return;
        
        inOrden(nodo.getIzquierdo(), resultado);
        resultado.add(EstudiantePromedioDTO.builder()
            .estudianteId(nodo.getEstudianteId())
            .nombre(nodo.getNombre())
            .promedio(nodo.getPromedio())
            .build());
        inOrden(nodo.getDerecho(), resultado);
    }
    
    // Obtener top N estudiantes (mayores promedios)
    public List<EstudiantePromedioDTO> obtenerTop(int n) {
        List<EstudiantePromedioDTO> todos = obtenerOrdenados();
        Collections.reverse(todos); // Mayor a menor
        return todos.stream().limit(n).collect(Collectors.toList());
    }
    
    // Buscar estudiantes en rango de promedio
    public List<EstudiantePromedioDTO> buscarEnRango(double min, double max) {
        List<EstudiantePromedioDTO> resultado = new ArrayList<>();
        buscarRangoRecursivo(raiz, min, max, resultado);
        return resultado;
    }
    
    private void buscarRangoRecursivo(NodoAVL nodo, double min, double max, 
                                     List<EstudiantePromedioDTO> resultado) {
        if (nodo == null) return;
        
        if (nodo.getPromedio() > min) {
            buscarRangoRecursivo(nodo.getIzquierdo(), min, max, resultado);
        }
        
        if (nodo.getPromedio() >= min && nodo.getPromedio() <= max) {
            resultado.add(EstudiantePromedioDTO.builder()
                .estudianteId(nodo.getEstudianteId())
                .nombre(nodo.getNombre())
                .promedio(nodo.getPromedio())
                .build());
        }
        
        if (nodo.getPromedio() < max) {
            buscarRangoRecursivo(nodo.getDerecho(), min, max, resultado);
        }
    }
    
    // Verificar si el árbol está balanceado
    public boolean estaBalanceado() {
        return verificarBalanceo(raiz);
    }
    
    private boolean verificarBalanceo(NodoAVL nodo) {
        if (nodo == null) return true;
        
        int balance = factorBalance(nodo);
        
        if (Math.abs(balance) > 1) {
            return false;
        }
        
        return verificarBalanceo(nodo.getIzquierdo()) && 
               verificarBalanceo(nodo.getDerecho());
    }
    
    // Obtener altura del árbol
    public int obtenerAltura() {
        return altura(raiz);
    }
}

@Data
@Builder
class EstudiantePromedioDTO {
    private String estudianteId;
    private String nombre;
    private double promedio;
}

// Controlador
@RestController
@RequestMapping("/api/avl/estudiantes")
@Tag(name = "AVL Estudiantes", description = "Ranking balanceado de estudiantes")
public class EstudiantesAVLController {
    
    @Autowired
    private EstudiantesAVLService avlService;
    
    @Operation(summary = "Insertar estudiante")
    @PostMapping("/insertar")
    public ResponseEntity<String> insertar(
            @RequestParam String estudianteId,
            @RequestParam String nombre,
            @RequestParam double promedio) {
        
        avlService.insertar(promedio, estudianteId, nombre);
        return ResponseEntity.ok("Estudiante insertado y árbol balanceado");
    }
    
    @Operation(summary = "Obtener top N estudiantes")
    @GetMapping("/top/{n}")
    public ResponseEntity<List<EstudiantePromedioDTO>> obtenerTop(@PathVariable int n) {
        return ResponseEntity.ok(avlService.obtenerTop(n));
    }
    
    @Operation(summary = "Buscar estudiantes por rango de promedio")
    @GetMapping("/rango")
    public ResponseEntity<List<EstudiantePromedioDTO>> buscarRango(
            @RequestParam double min,
            @RequestParam double max) {
        
        return ResponseEntity.ok(avlService.buscarEnRango(min, max));
    }
    
    @Operation(summary = "Obtener todos ordenados")
    @GetMapping("/ordenados")
    public ResponseEntity<List<EstudiantePromedioDTO>> obtenerOrdenados() {
        return ResponseEntity.ok(avlService.obtenerOrdenados());
    }
    
    @Operation(summary = "Verificar balanceo del árbol")
    @GetMapping("/balanceado")
    public ResponseEntity<Map<String, Object>> verificarBalanceo() {
        Map<String, Object> info = new HashMap<>();
        info.put("balanceado", avlService.estaBalanceado());
        info.put("altura", avlService.obtenerAltura());
        
        return ResponseEntity.ok(info);
    }
    
    @Operation(summary = "Eliminar estudiante")
    @DeleteMapping("/{promedio}")
    public ResponseEntity<String> eliminar(@PathVariable double promedio) {
        avlService.eliminar(promedio);
        return ResponseEntity.ok("Estudiante eliminado y árbol rebalanceado");
    }
}
```

### Ejemplo 2: Visualización de Rotaciones

```java
// Servicio: Monitoreo de rotaciones
@Service
public class AVLMonitorService {
    
    private List<RotacionInfo> historialRotaciones = new ArrayList<>();
    
    @Data
    @AllArgsConstructor
    public static class RotacionInfo {
        private LocalDateTime timestamp;
        private String tipo; // "simple_derecha", "simple_izquierda", "doble_lr", "doble_rl"
        private double valorNodo;
        private int factorBalanceAntes;
    }
    
    public void registrarRotacion(String tipo, double valor, int fbAntes) {
        historialRotaciones.add(new RotacionInfo(
            LocalDateTime.now(), tipo, valor, fbAntes
        ));
    }
    
    public List<RotacionInfo> obtenerHistorial() {
        return new ArrayList<>(historialRotaciones);
    }
    
    public Map<String, Long> obtenerEstadisticas() {
        return historialRotaciones.stream()
            .collect(Collectors.groupingBy(
                RotacionInfo::getTipo,
                Collectors.counting()
            ));
    }
}
```

---

## 🎓 Comparación AVL vs BST Normal

| Característica | BST | AVL |
|----------------|-----|-----|
| Búsqueda (peor caso) | O(n) | **O(log n)** ✅ |
| Inserción (peor caso) | O(n) | **O(log n)** ✅ |
| Eliminación (peor caso) | O(n) | **O(log n)** ✅ |
| Balanceado | ❌ No | ✅ Sí |
| Rotaciones por inserción | 0 | Máximo 2 |
| Rotaciones por eliminación | 0 | Máximo O(log n) |
| Complejidad implementación | Baja | **Alta** |

---

## 📊 Complejidad Garantizada

| Operación | Complejidad | Garantía |
|-----------|-------------|----------|
| Búsqueda | **O(log n)** | ✅ Siempre |
| Inserción | **O(log n)** | ✅ Siempre |
| Eliminación | **O(log n)** | ✅ Siempre |
| Buscar min/max | **O(log n)** | ✅ Siempre |
| Recorrido | O(n) | ✅ Siempre |

---

## ⚠️ Consideraciones

### Cuándo usar AVL:
- **Búsquedas frecuentes** más que inserciones/eliminaciones
- Necesitas **garantía de O(log n)**
- Conjuntos de datos grandes con acceso frecuente

### Cuándo NO usar AVL:
- Inserciones/eliminaciones muy frecuentes (Red-Black es mejor)
- Datos casi estáticos (BST simple basta)
- Memoria limitada (overhead de altura en cada nodo)

### Alternativas:
- **Red-Black Tree**: Menos rotaciones en inserción/eliminación
- **B-Tree**: Mejor para bases de datos (menos altura)
- **Treap**: Aleatorización para balance probabilístico