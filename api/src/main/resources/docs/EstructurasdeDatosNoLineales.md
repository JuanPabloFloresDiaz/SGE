# Estructuras de Datos No Lineales

## 📚 Concepto

Las **estructuras de datos no lineales** son aquellas donde los elementos **no están organizados secuencialmente**. Un elemento puede tener **múltiples predecesores y sucesores**, formando relaciones jerárquicas o de red.

### Características:
- Elementos organizados en **jerarquías** o **redes**
- **Múltiples niveles** de profundidad
- Un elemento puede conectarse con **varios elementos**
- Más complejas que las lineales

### Ejemplos principales:
1. **Trees** (Árboles): Jerarquías
2. **Graphs** (Grafos): Redes y relaciones

---

## 🎯 Comparación de Estructuras No Lineales

| Estructura | Organización | Conexiones | Altura | Uso típico |
|------------|--------------|------------|--------|------------|
| **Árbol Binario** | Jerárquica | ≤ 2 hijos | Variable | Expresiones, recorridos |
| **BST** | Ordenada | ≤ 2 hijos | O(n) peor | Búsqueda, ordenamiento |
| **AVL** | Balanceada | ≤ 2 hijos | O(log n) | Búsqueda garantizada |
| **N-ario** | Jerárquica | N hijos | Variable | Estructura de curso, menús |
| **Heap** | Parcialmente ordenada | 2 hijos | O(log n) | Priority queue |
| **Grafo** | Red | Muchos | - | Relaciones, redes sociales |

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Árbol Binario - Expresiones Matemáticas

```java
// Nodo de árbol binario
@Data
class NodoExpresion {
    private String valor;
    private NodoExpresion izquierdo;
    private NodoExpresion derecho;
    
    public NodoExpresion(String valor) {
        this.valor = valor;
    }
    
    public boolean esOperador() {
        return valor.matches("[+\\-*/]");
    }
    
    public boolean esHoja() {
        return izquierdo == null && derecho == null;
    }
}

// Servicio: Árbol de expresiones
@Service
public class ExpresionArbolService {
    
    // Construir árbol desde expresión postfija
    public NodoExpresion construirDesdePostfija(String[] tokens) {
        Stack<NodoExpresion> pila = new Stack<>();
        
        for (String token : tokens) {
            NodoExpresion nodo = new NodoExpresion(token);
            
            if (nodo.esOperador()) {
                // Operador: pop dos nodos y crear subárbol
                nodo.setDerecho(pila.pop());
                nodo.setIzquierdo(pila.pop());
            }
            
            pila.push(nodo);
        }
        
        return pila.pop();
    }
    
    // Evaluar árbol de expresión
    public double evaluar(NodoExpresion nodo) {
        if (nodo == null) return 0;
        
        // Hoja: es un número
        if (nodo.esHoja()) {
            return Double.parseDouble(nodo.getValor());
        }
        
        // Nodo interno: es un operador
        double izq = evaluar(nodo.getIzquierdo());
        double der = evaluar(nodo.getDerecho());
        
        return aplicarOperador(nodo.getValor(), izq, der);
    }
    
    private double aplicarOperador(String op, double izq, double der) {
        switch (op) {
            case "+": return izq + der;
            case "-": return izq - der;
            case "*": return izq * der;
            case "/": return izq / der;
            default: throw new IllegalArgumentException("Operador inválido: " + op);
        }
    }
    
    // Conversiones de expresión
    
    // Infija: (A + B)
    public String aInfija(NodoExpresion nodo) {
        if (nodo == null) return "";
        
        if (nodo.esHoja()) {
            return nodo.getValor();
        }
        
        return "(" + aInfija(nodo.getIzquierdo()) + 
               " " + nodo.getValor() + " " + 
               aInfija(nodo.getDerecho()) + ")";
    }
    
    // Prefija: + A B
    public String aPrefija(NodoExpresion nodo) {
        if (nodo == null) return "";
        
        if (nodo.esHoja()) {
            return nodo.getValor();
        }
        
        return nodo.getValor() + " " + 
               aPrefija(nodo.getIzquierdo()) + " " + 
               aPrefija(nodo.getDerecho());
    }
    
    // Postfija: A B +
    public String aPostfija(NodoExpresion nodo) {
        if (nodo == null) return "";
        
        if (nodo.esHoja()) {
            return nodo.getValor();
        }
        
        return aPostfija(nodo.getIzquierdo()) + " " + 
               aPostfija(nodo.getDerecho()) + " " + 
               nodo.getValor();
    }
}
```

### Ejemplo 2: BST - Sistema de Ranking

```java
// Nodo BST para ranking
@Data
class NodoBST {
    private Estudiante estudiante;
    private NodoBST izquierdo;
    private NodoBST derecho;
    
    public NodoBST(Estudiante estudiante) {
        this.estudiante = estudiante;
    }
}

// Servicio: BST para ranking de estudiantes
@Service
@Slf4j
public class RankingBSTService {
    
    private NodoBST raiz;
    
    // Insertar estudiante en BST (ordenado por promedio)
    public void insertar(Estudiante estudiante) {
        raiz = insertarRec(raiz, estudiante);
    }
    
    private NodoBST insertarRec(NodoBST nodo, Estudiante estudiante) {
        if (nodo == null) {
            return new NodoBST(estudiante);
        }
        
        if (estudiante.getPromedio() < nodo.getEstudiante().getPromedio()) {
            nodo.setIzquierdo(insertarRec(nodo.getIzquierdo(), estudiante));
        } else {
            nodo.setDerecho(insertarRec(nodo.getDerecho(), estudiante));
        }
        
        return nodo;
    }
    
    // Obtener top N estudiantes (in-order descendente)
    public List<Estudiante> obtenerTopN(int n) {
        List<Estudiante> top = new ArrayList<>();
        inOrderDescendente(raiz, top, n);
        return top;
    }
    
    private void inOrderDescendente(NodoBST nodo, List<Estudiante> lista, int limite) {
        if (nodo == null || lista.size() >= limite) return;
        
        // Primero el subárbol derecho (mayores promedios)
        inOrderDescendente(nodo.getDerecho(), lista, limite);
        
        if (lista.size() < limite) {
            lista.add(nodo.getEstudiante());
        }
        
        inOrderDescendente(nodo.getIzquierdo(), lista, limite);
    }
    
    // Buscar estudiantes en rango de promedio
    public List<Estudiante> buscarEnRango(double min, double max) {
        List<Estudiante> resultados = new ArrayList<>();
        buscarEnRangoRec(raiz, min, max, resultados);
        return resultados;
    }
    
    private void buscarEnRangoRec(NodoBST nodo, double min, double max, 
                                  List<Estudiante> resultados) {
        if (nodo == null) return;
        
        double promedio = nodo.getEstudiante().getPromedio();
        
        // Si el promedio es mayor que min, buscar en izquierdo
        if (promedio > min) {
            buscarEnRangoRec(nodo.getIzquierdo(), min, max, resultados);
        }
        
        // Si está en rango, agregar
        if (promedio >= min && promedio <= max) {
            resultados.add(nodo.getEstudiante());
        }
        
        // Si el promedio es menor que max, buscar en derecho
        if (promedio < max) {
            buscarEnRangoRec(nodo.getDerecho(), min, max, resultados);
        }
    }
    
    // Obtener altura del árbol
    public int obtenerAltura() {
        return obtenerAlturaRec(raiz);
    }
    
    private int obtenerAlturaRec(NodoBST nodo) {
        if (nodo == null) return 0;
        
        int alturaIzq = obtenerAlturaRec(nodo.getIzquierdo());
        int alturaDer = obtenerAlturaRec(nodo.getDerecho());
        
        return 1 + Math.max(alturaIzq, alturaDer);
    }
}
```

### Ejemplo 3: Grafo - Red de Prerrequisitos

```java
// Grafo dirigido para cursos
@Data
class GrafoCursos {
    private Map<Long, List<Long>> adyacencia; // cursoId -> lista de prerrequisitos
    
    public GrafoCursos() {
        this.adyacencia = new HashMap<>();
    }
    
    public void agregarCurso(Long cursoId) {
        adyacencia.putIfAbsent(cursoId, new ArrayList<>());
    }
    
    public void agregarPrerrequisito(Long curso, Long prerrequisito) {
        adyacencia.get(curso).add(prerrequisito);
    }
    
    public List<Long> obtenerPrerrequisitos(Long cursoId) {
        return adyacencia.getOrDefault(cursoId, new ArrayList<>());
    }
}

// Servicio: Grafo de prerrequisitos
@Service
@Slf4j
public class GrafoPrerrequisitosService {
    
    @Autowired
    private CursoRepository cursoRepository;
    
    private GrafoCursos grafo;
    
    @PostConstruct
    public void inicializar() {
        construirGrafo();
    }
    
    private void construirGrafo() {
        grafo = new GrafoCursos();
        List<Curso> cursos = cursoRepository.findAll();
        
        // Agregar todos los cursos
        for (Curso curso : cursos) {
            grafo.agregarCurso(curso.getId());
        }
        
        // Agregar prerrequisitos
        for (Curso curso : cursos) {
            if (curso.getPrerrequisitos() != null) {
                for (Curso prereq : curso.getPrerrequisitos()) {
                    grafo.agregarPrerrequisito(curso.getId(), prereq.getId());
                }
            }
        }
        
        log.info("Grafo de prerrequisitos construido con {} cursos", cursos.size());
    }
    
    // Verificar si estudiante puede tomar curso (tiene prerrequisitos)
    public boolean puedeTomarCurso(Long cursoId, Set<Long> cursosCompletados) {
        List<Long> prerrequisitos = grafo.obtenerPrerrequisitos(cursoId);
        
        // Debe tener TODOS los prerrequisitos
        return cursosCompletados.containsAll(prerrequisitos);
    }
    
    // Obtener cursos disponibles para estudiante
    public List<Long> cursosDisponibles(Set<Long> cursosCompletados) {
        List<Long> disponibles = new ArrayList<>();
        
        for (Long cursoId : grafo.getAdyacencia().keySet()) {
            // Si ya lo completó, skip
            if (cursosCompletados.contains(cursoId)) continue;
            
            // Si puede tomarlo, agregar
            if (puedeTomarCurso(cursoId, cursosCompletados)) {
                disponibles.add(cursoId);
            }
        }
        
        return disponibles;
    }
    
    // Ordenamiento topológico (orden de cursos)
    public List<Long> ordenTopologico() {
        Map<Long, Integer> gradoEntrada = new HashMap<>();
        
        // Calcular grado de entrada (cantidad de prerrequisitos)
        for (Long curso : grafo.getAdyacencia().keySet()) {
            gradoEntrada.put(curso, grafo.obtenerPrerrequisitos(curso).size());
        }
        
        // Cola con cursos sin prerrequisitos
        Queue<Long> cola = new LinkedList<>();
        for (Map.Entry<Long, Integer> entry : gradoEntrada.entrySet()) {
            if (entry.getValue() == 0) {
                cola.offer(entry.getKey());
            }
        }
        
        List<Long> ordenamiento = new ArrayList<>();
        
        while (!cola.isEmpty()) {
            Long curso = cola.poll();
            ordenamiento.add(curso);
            
            // Encontrar cursos que tienen este como prerrequisito
            for (Map.Entry<Long, List<Long>> entry : grafo.getAdyacencia().entrySet()) {
                if (entry.getValue().contains(curso)) {
                    Long dependiente = entry.getKey();
                    gradoEntrada.put(dependiente, gradoEntrada.get(dependiente) - 1);
                    
                    if (gradoEntrada.get(dependiente) == 0) {
                        cola.offer(dependiente);
                    }
                }
            }
        }
        
        return ordenamiento;
    }
    
    // Detectar ciclos (prerrequisitos circulares)
    public boolean tieneCiclos() {
        Set<Long> visitados = new HashSet<>();
        Set<Long> pillaRecursion = new HashSet<>();
        
        for (Long curso : grafo.getAdyacencia().keySet()) {
            if (detectarCicloDFS(curso, visitados, pillaRecursion)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean detectarCicloDFS(Long curso, Set<Long> visitados, 
                                     Set<Long> pillaRecursion) {
        visitados.add(curso);
        pillaRecursion.add(curso);
        
        for (Long prereq : grafo.obtenerPrerrequisitos(curso)) {
            if (!visitados.contains(prereq)) {
                if (detectarCicloDFS(prereq, visitados, pillaRecursion)) {
                    return true;
                }
            } else if (pillaRecursion.contains(prereq)) {
                // Ciclo detectado
                return true;
            }
        }
        
        pillaRecursion.remove(curso);
        return false;
    }
}
```

### Ejemplo 4: Heap - Sistema de Prioridades

```java
// Servicio: Priority Queue (Heap) para solicitudes
@Service
@Slf4j
public class SolicitudesPrioridadService {
    
    // Min-heap: prioridad más baja primero
    private PriorityQueue<Solicitud> colaPrioridad;
    
    public SolicitudesPrioridadService() {
        // Comparator: menor prioridad numérica = mayor urgencia
        this.colaPrioridad = new PriorityQueue<>(
            Comparator.comparingInt(Solicitud::getPrioridad)
        );
    }
    
    @Data
    @AllArgsConstructor
    public static class Solicitud {
        private Long id;
        private String descripcion;
        private int prioridad; // 1 = urgente, 5 = baja
        private LocalDateTime timestamp;
    }
    
    // Agregar solicitud
    public void agregarSolicitud(Long id, String descripcion, int prioridad) {
        Solicitud solicitud = new Solicitud(
            id, descripcion, prioridad, LocalDateTime.now()
        );
        
        colaPrioridad.offer(solicitud);
        log.info("Solicitud agregada: {} (prioridad {})", descripcion, prioridad);
    }
    
    // Procesar solicitud más urgente
    public Solicitud procesarSiguiente() {
        Solicitud solicitud = colaPrioridad.poll();
        
        if (solicitud != null) {
            log.info("Procesando: {} (prioridad {})", 
                     solicitud.getDescripcion(), 
                     solicitud.getPrioridad());
        }
        
        return solicitud;
    }
    
    // Ver siguiente sin procesar
    public Solicitud verSiguiente() {
        return colaPrioridad.peek();
    }
    
    // Cantidad de solicitudes pendientes
    public int solicitudesPendientes() {
        return colaPrioridad.size();
    }
    
    // Obtener todas las solicitudes (sin orden)
    public List<Solicitud> obtenerTodas() {
        return new ArrayList<>(colaPrioridad);
    }
}
```

### Ejemplo 5: Controlador REST Completo

```java
// Controlador: Estructuras no lineales
@RestController
@RequestMapping("/api/estructuras-no-lineales")
@Tag(name = "Estructuras No Lineales", description = "Árboles y grafos")
public class EstructurasNoLinealesController {
    
    @Autowired
    private ExpresionArbolService expresionService;
    
    @Autowired
    private RankingBSTService rankingService;
    
    @Autowired
    private GrafoPrerrequisitosService grafoService;
    
    @Autowired
    private SolicitudesPrioridadService prioridadService;
    
    // --- Árbol de Expresiones ---
    
    @Operation(summary = "Evaluar expresión postfija")
    @PostMapping("/expresion/evaluar")
    public ResponseEntity<Double> evaluarExpresion(@RequestBody String[] tokens) {
        NodoExpresion raiz = expresionService.construirDesdePostfija(tokens);
        double resultado = expresionService.evaluar(raiz);
        return ResponseEntity.ok(resultado);
    }
    
    @Operation(summary = "Convertir expresión a infija")
    @PostMapping("/expresion/infija")
    public ResponseEntity<String> aInfija(@RequestBody String[] tokens) {
        NodoExpresion raiz = expresionService.construirDesdePostfija(tokens);
        String infija = expresionService.aInfija(raiz);
        return ResponseEntity.ok(infija);
    }
    
    // --- BST Ranking ---
    
    @Operation(summary = "Obtener top N estudiantes")
    @GetMapping("/ranking/top/{n}")
    public ResponseEntity<List<Estudiante>> obtenerTop(@PathVariable int n) {
        return ResponseEntity.ok(rankingService.obtenerTopN(n));
    }
    
    @Operation(summary = "Buscar estudiantes por rango de promedio")
    @GetMapping("/ranking/rango")
    public ResponseEntity<List<Estudiante>> buscarPorRango(
            @RequestParam double min,
            @RequestParam double max) {
        return ResponseEntity.ok(rankingService.buscarEnRango(min, max));
    }
    
    @Operation(summary = "Obtener altura del árbol de ranking")
    @GetMapping("/ranking/altura")
    public ResponseEntity<Integer> obtenerAltura() {
        return ResponseEntity.ok(rankingService.obtenerAltura());
    }
    
    // --- Grafo de Prerrequisitos ---
    
    @Operation(summary = "Verificar si puede tomar curso")
    @PostMapping("/cursos/puede-tomar/{cursoId}")
    public ResponseEntity<Boolean> puedeTomarCurso(
            @PathVariable Long cursoId,
            @RequestBody Set<Long> cursosCompletados) {
        boolean puede = grafoService.puedeTomarCurso(cursoId, cursosCompletados);
        return ResponseEntity.ok(puede);
    }
    
    @Operation(summary = "Obtener cursos disponibles")
    @PostMapping("/cursos/disponibles")
    public ResponseEntity<List<Long>> cursosDisponibles(
            @RequestBody Set<Long> cursosCompletados) {
        return ResponseEntity.ok(grafoService.cursosDisponibles(cursosCompletados));
    }
    
    @Operation(summary = "Obtener orden topológico de cursos")
    @GetMapping("/cursos/orden-topologico")
    public ResponseEntity<List<Long>> ordenTopologico() {
        return ResponseEntity.ok(grafoService.ordenTopologico());
    }
    
    @Operation(summary = "Detectar ciclos en prerrequisitos")
    @GetMapping("/cursos/tiene-ciclos")
    public ResponseEntity<Boolean> tieneCiclos() {
        return ResponseEntity.ok(grafoService.tieneCiclos());
    }
    
    // --- Priority Queue (Heap) ---
    
    @Operation(summary = "Agregar solicitud con prioridad")
    @PostMapping("/solicitudes")
    public ResponseEntity<String> agregarSolicitud(
            @RequestParam Long id,
            @RequestParam String descripcion,
            @RequestParam int prioridad) {
        prioridadService.agregarSolicitud(id, descripcion, prioridad);
        return ResponseEntity.ok("Solicitud agregada");
    }
    
    @Operation(summary = "Procesar siguiente solicitud (más urgente)")
    @PostMapping("/solicitudes/procesar")
    public ResponseEntity<SolicitudesPrioridadService.Solicitud> procesarSolicitud() {
        return ResponseEntity.ok(prioridadService.procesarSiguiente());
    }
    
    @Operation(summary = "Ver siguiente solicitud sin procesar")
    @GetMapping("/solicitudes/siguiente")
    public ResponseEntity<SolicitudesPrioridadService.Solicitud> verSiguiente() {
        return ResponseEntity.ok(prioridadService.verSiguiente());
    }
}
```

---

## 🎯 Casos de Uso en SGE API

### Árboles:
- **Estructura de curso**: Curso → Unidades → Temas (N-ario)
- **Ranking de estudiantes**: Ordenados por promedio (BST)
- **Expresiones matemáticas**: Evaluar fórmulas (Binario)
- **Sistema de archivos**: Carpetas y archivos (N-ario)
- **Menú de navegación**: Jerarquía de opciones (N-ario)

### Grafos:
- **Red de prerrequisitos**: Cursos que dependen de otros
- **Red social**: Estudiantes conectados
- **Mapa de campus**: Edificios y caminos
- **Sistema de recomendación**: Cursos similares
- **Ruta óptima**: Camino más corto entre ubicaciones

### Heaps:
- **Cola de prioridad**: Solicitudes urgentes primero
- **Sistema de notificaciones**: Ordenadas por importancia
- **Asignación de recursos**: Priorizar estudiantes

---

## 📊 Comparación: Lineales vs No Lineales

| Aspecto | Lineales | No Lineales |
|---------|----------|-------------|
| **Organización** | Secuencial | Jerárquica/Red |
| **Niveles** | 1 nivel | Múltiples niveles |
| **Recorrido** | Simple (inicio→fin) | Complejo (DFS, BFS) |
| **Relaciones** | 1 predecesor, 1 sucesor | Múltiples conexiones |
| **Ejemplos** | Array, List, Stack, Queue | Tree, Graph, Heap |
| **Uso** | Colecciones simples | Jerarquías, redes |

---

## 📈 Complejidad de Operaciones

| Estructura | Búsqueda | Inserción | Eliminación | Espacio |
|------------|----------|-----------|-------------|---------|
| **Árbol Binario** | O(n) | O(n) | O(n) | O(n) |
| **BST (balanceado)** | O(log n) | O(log n) | O(log n) | O(n) |
| **BST (desbalanceado)** | O(n) | O(n) | O(n) | O(n) |
| **AVL** | **O(log n)** ✅ | **O(log n)** | **O(log n)** | O(n) |
| **Heap** | O(n) | **O(log n)** | **O(log n)** | O(n) |
| **Grafo (lista adyacencia)** | O(V+E) | O(1) | O(E) | O(V+E) |

---

## ⚠️ Consideraciones

### Árboles:

#### Ventajas:
- ✅ Representan **jerarquías** naturalmente
- ✅ Búsqueda eficiente (si balanceado): **O(log n)**
- ✅ Operaciones ordenadas (BST)
- ✅ Recorridos flexibles (in-order, pre-order, post-order)

#### Desventajas:
- ❌ Más complejos que estructuras lineales
- ❌ Pueden **desbalancearse** (BST → O(n))
- ❌ Requieren más memoria (punteros)

### Grafos:

#### Ventajas:
- ✅ Modelan **relaciones complejas**
- ✅ Flexibles (dirigidos/no dirigidos, ponderados)
- ✅ Algoritmos poderosos (Dijkstra, BFS, DFS)

#### Desventajas:
- ❌ Más complejos de implementar
- ❌ Algoritmos costosos: **O(V²)** o **O(E log V)**
- ❌ Pueden tener **ciclos**

### Heaps:

#### Ventajas:
- ✅ Inserción/eliminación: **O(log n)**
- ✅ Acceso al mínimo/máximo: **O(1)**
- ✅ Perfecto para **priority queues**

#### Desventajas:
- ❌ Búsqueda general: **O(n)**
- ❌ No ordenado completamente

---

## 💡 Decisión: ¿Cuál usar?

```
¿Necesitas representar jerarquías?
│
├─ SÍ → ¿Cuántos hijos por nodo?
│   ├─ Máximo 2 → ¿Necesitas búsqueda rápida?
│   │   ├─ SÍ → **BST** (o AVL si garantizado)
│   │   └─ NO → **Árbol Binario**
│   │
│   └─ Más de 2 → **Árbol N-ario**
│
└─ NO → ¿Necesitas relaciones complejas?
    │
    ├─ SÍ → **Grafo**
    │
    └─ NO → ¿Necesitas procesamiento por prioridad?
        ├─ SÍ → **Heap** (Priority Queue)
        └─ NO → Usar estructura lineal
```

### Reglas generales:
- **Árbol Binario**: Expresiones, decisiones simples
- **BST/AVL**: Búsqueda rápida con orden
- **N-ario**: Jerarquías con muchos hijos (cursos, menús)
- **Heap**: Priority queue, Top K elementos
- **Grafo**: Relaciones complejas, redes, prerrequisitos

---

## 🔄 Recorridos de Árboles

### Pre-order (Raíz → Izq → Der):
```
Uso: Copiar árbol, serialización
```

### In-order (Izq → Raíz → Der):
```
Uso: BST ordenado ascendente
```

### Post-order (Izq → Der → Raíz):
```
Uso: Eliminar árbol, expresiones postfijas
```

### BFS (por niveles):
```
Uso: Encontrar nodos cercanos, árbol de mínima profundidad
```

---

## 🎓 Cuándo usar No Lineales

### Usa Árboles cuando:
- ✅ Tienes **jerarquías** (organización, estructura de curso)
- ✅ Necesitas **búsqueda rápida** con orden (BST)
- ✅ Quieres **recorridos específicos** (in-order, pre-order)
- ✅ Representar **decisiones** (árboles de decisión)

### Usa Grafos cuando:
- ✅ Tienes **relaciones complejas** (red social, prerrequisitos)
- ✅ Necesitas encontrar **caminos** (rutas, dependencias)
- ✅ Modelar **redes** (campus, internet, carreteras)
- ✅ Detectar **ciclos** (dependencias circulares)

### Usa Lineales cuando:
- ✅ Datos **secuenciales** simples
- ✅ No hay **jerarquías** ni **relaciones** complejas
- ✅ Acceso **directo por índice** (Array/ArrayList)
- ✅ Procesamiento **FIFO/LIFO** (Queue/Stack)