# Grafos

## 📚 Concepto

Un **Grafo** es una estructura de datos que representa **relaciones** entre objetos. Está compuesto por:

- **Vértices (nodos)**: Los objetos o entidades
- **Aristas (edges)**: Las conexiones entre vértices

### Tipos de Grafos:

**Por dirección:**
- **Dirigidos**: Las aristas tienen dirección (A → B)
- **No dirigidos**: Las aristas son bidireccionales (A ↔ B)

**Por peso:**
- **Ponderados**: Las aristas tienen un peso/costo
- **No ponderados**: Todas las aristas tienen el mismo peso

**Por conectividad:**
- **Conexo**: Existe camino entre cualquier par de vértices
- **Desconexo**: Algunos vértices no están conectados

### Representaciones:

1. **Lista de adyacencia**: HashMap<Vértice, List<Vértice>>
2. **Matriz de adyacencia**: boolean[][] o int[][]

---

## 🎯 Casos de Uso en SGE API

1. **Prerrequisitos de Asignaturas**: Grafo dirigido (Cálculo I → Cálculo II)
2. **Red de Estudiantes**: Grafo no dirigido (compañeros de clase)
3. **Dependencias de Temas**: Grafo dirigido (Tema 1 → Tema 2)
4. **Sistema de Recomendaciones**: Estudiantes con intereses similares
5. **Rutas de Aprendizaje**: Caminos óptimos en plan de estudios
6. **Horarios de Clases**: Detectar conflictos de horario

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Grafo de Prerrequisitos (Dirigido)

```java
// Modelo: Relación de prerrequisitos
@Data
@Builder
public class Asignatura {
    private String id;
    private String codigo;
    private String nombre;
    private List<String> prerrequisitos; // IDs de asignaturas previas
}

// Servicio: Grafo de prerrequisitos
@Service
public class PrerrequisitoGrafoService {
    
    // Lista de adyacencia: asignatura → lista de asignaturas que la requieren
    private Map<String, List<String>> grafoDependencias = new HashMap<>();
    
    // Inverso: asignatura → lista de prerrequisitos
    private Map<String, List<String>> grafoPrerrequisitos = new HashMap<>();
    
    @Autowired
    private AsignaturaRepository asignaturaRepository;
    
    @PostConstruct
    public void construirGrafo() {
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        
        for (Asignatura asignatura : asignaturas) {
            String id = asignatura.getId();
            
            // Inicializar listas
            grafoDependencias.putIfAbsent(id, new ArrayList<>());
            grafoPrerrequisitos.put(id, new ArrayList<>(asignatura.getPrerrequisitos()));
            
            // Construir aristas
            for (String prerrequisito : asignatura.getPrerrequisitos()) {
                grafoDependencias
                    .computeIfAbsent(prerrequisito, k -> new ArrayList<>())
                    .add(id);
            }
        }
        
        log.info("Grafo de prerrequisitos construido: {} asignaturas", asignaturas.size());
    }
    
    // Verificar si estudiante puede inscribirse (tiene prerrequisitos aprobados)
    public boolean puedeInscribirse(String estudianteId, String asignaturaId) {
        List<String> prerrequisitos = grafoPrerrequisitos.get(asignaturaId);
        
        if (prerrequisitos == null || prerrequisitos.isEmpty()) {
            return true; // No tiene prerrequisitos
        }
        
        // Verificar que tiene todas las asignaturas previas aprobadas
        for (String prerreq : prerrequisitos) {
            if (!tieneAsignaturaAprobada(estudianteId, prerreq)) {
                return false;
            }
        }
        
        return true;
    }
    
    // Obtener asignaturas que dependen de esta (hacia adelante)
    public List<String> obtenerDependientes(String asignaturaId) {
        return grafoDependencias.getOrDefault(asignaturaId, Collections.emptyList());
    }
    
    // Obtener plan de estudios sugerido (Ordenamiento Topológico)
    public List<String> obtenerPlanEstudios() {
        Map<String, Integer> gradoEntrada = new HashMap<>();
        
        // Calcular grado de entrada (número de prerrequisitos)
        for (String asignatura : grafoPrerrequisitos.keySet()) {
            gradoEntrada.put(asignatura, grafoPrerrequisitos.get(asignatura).size());
        }
        
        // Cola con asignaturas sin prerrequisitos
        Queue<String> cola = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : gradoEntrada.entrySet()) {
            if (entry.getValue() == 0) {
                cola.offer(entry.getKey());
            }
        }
        
        List<String> planEstudios = new ArrayList<>();
        
        // Algoritmo de Kahn (Topological Sort)
        while (!cola.isEmpty()) {
            String actual = cola.poll();
            planEstudios.add(actual);
            
            // Reducir grado de entrada de dependientes
            for (String dependiente : grafoDependencias.getOrDefault(actual, Collections.emptyList())) {
                int nuevoGrado = gradoEntrada.get(dependiente) - 1;
                gradoEntrada.put(dependiente, nuevoGrado);
                
                if (nuevoGrado == 0) {
                    cola.offer(dependiente);
                }
            }
        }
        
        // Verificar ciclos
        if (planEstudios.size() != grafoPrerrequisitos.size()) {
            throw new IllegalStateException("Ciclo detectado en prerrequisitos");
        }
        
        return planEstudios;
    }
    
    // Detectar ciclos (DFS)
    public boolean tieneCiclos() {
        Set<String> visitados = new HashSet<>();
        Set<String> enProceso = new HashSet<>();
        
        for (String asignatura : grafoPrerrequisitos.keySet()) {
            if (detectarCicloDFS(asignatura, visitados, enProceso)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean detectarCicloDFS(String nodo, Set<String> visitados, Set<String> enProceso) {
        if (enProceso.contains(nodo)) {
            return true; // Ciclo detectado
        }
        
        if (visitados.contains(nodo)) {
            return false;
        }
        
        visitados.add(nodo);
        enProceso.add(nodo);
        
        for (String vecino : grafoDependencias.getOrDefault(nodo, Collections.emptyList())) {
            if (detectarCicloDFS(vecino, visitados, enProceso)) {
                return true;
            }
        }
        
        enProceso.remove(nodo);
        return false;
    }
    
    private boolean tieneAsignaturaAprobada(String estudianteId, String asignaturaId) {
        // Lógica para verificar en BD
        // ...
        return true; // Placeholder
    }
}

// Controlador
@RestController
@RequestMapping("/api/grafos/prerrequisitos")
@Tag(name = "Prerrequisitos", description = "Gestión de dependencias entre asignaturas")
public class PrerrequisitoController {
    
    @Autowired
    private PrerrequisitoGrafoService grafoService;
    
    @Operation(summary = "Verificar si puede inscribirse")
    @GetMapping("/puede-inscribirse")
    public ResponseEntity<Boolean> puedeInscribirse(
            @RequestParam String estudianteId,
            @RequestParam String asignaturaId) {
        
        boolean puede = grafoService.puedeInscribirse(estudianteId, asignaturaId);
        return ResponseEntity.ok(puede);
    }
    
    @Operation(summary = "Obtener plan de estudios sugerido")
    @GetMapping("/plan-estudios")
    public ResponseEntity<List<String>> obtenerPlanEstudios() {
        return ResponseEntity.ok(grafoService.obtenerPlanEstudios());
    }
    
    @Operation(summary = "Verificar ciclos en prerrequisitos")
    @GetMapping("/verificar-ciclos")
    public ResponseEntity<Boolean> verificarCiclos() {
        return ResponseEntity.ok(grafoService.tieneCiclos());
    }
}
```

### Ejemplo 2: Red Social de Estudiantes (Grafo No Dirigido)

```java
// Servicio: Red de estudiantes (compañeros de clase)
@Service
public class RedEstudiantesService {
    
    // Grafo no dirigido: HashMap con listas de adyacencia
    private Map<String, Set<String>> redEstudiantes = new HashMap<>();
    
    @Autowired
    private InscripcionRepository inscripcionRepository;
    
    // Construir red basada en cursos compartidos
    public void construirRed() {
        List<Inscripcion> inscripciones = inscripcionRepository.findAll();
        
        // Agrupar estudiantes por curso
        Map<String, List<String>> estudiantesPorCurso = inscripciones.stream()
            .collect(Collectors.groupingBy(
                Inscripcion::getCursoId,
                Collectors.mapping(Inscripcion::getEstudianteId, Collectors.toList())
            ));
        
        // Crear aristas entre estudiantes del mismo curso
        for (List<String> estudiantes : estudiantesPorCurso.values()) {
            for (int i = 0; i < estudiantes.size(); i++) {
                for (int j = i + 1; j < estudiantes.size(); j++) {
                    agregarConexion(estudiantes.get(i), estudiantes.get(j));
                }
            }
        }
        
        log.info("Red de estudiantes construida: {} nodos", redEstudiantes.size());
    }
    
    // Agregar conexión bidireccional
    private void agregarConexion(String estudiante1, String estudiante2) {
        redEstudiantes
            .computeIfAbsent(estudiante1, k -> new HashSet<>())
            .add(estudiante2);
        
        redEstudiantes
            .computeIfAbsent(estudiante2, k -> new HashSet<>())
            .add(estudiante1);
    }
    
    // Obtener compañeros directos (vecinos)
    public Set<String> obtenerCompaneros(String estudianteId) {
        return redEstudiantes.getOrDefault(estudianteId, Collections.emptySet());
    }
    
    // BFS: Encontrar camino más corto entre dos estudiantes
    public List<String> encontrarCamino(String origen, String destino) {
        if (origen.equals(destino)) {
            return Collections.singletonList(origen);
        }
        
        Queue<String> cola = new LinkedList<>();
        Map<String, String> padres = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        
        cola.offer(origen);
        visitados.add(origen);
        padres.put(origen, null);
        
        while (!cola.isEmpty()) {
            String actual = cola.poll();
            
            if (actual.equals(destino)) {
                return reconstruirCamino(padres, destino);
            }
            
            for (String vecino : redEstudiantes.getOrDefault(actual, Collections.emptySet())) {
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    padres.put(vecino, actual);
                    cola.offer(vecino);
                }
            }
        }
        
        return Collections.emptyList(); // No hay camino
    }
    
    private List<String> reconstruirCamino(Map<String, String> padres, String destino) {
        List<String> camino = new ArrayList<>();
        String actual = destino;
        
        while (actual != null) {
            camino.add(actual);
            actual = padres.get(actual);
        }
        
        Collections.reverse(camino);
        return camino;
    }
    
    // DFS: Obtener todos los estudiantes conectados (componente conexa)
    public Set<String> obtenerGrupoConectado(String estudianteId) {
        Set<String> grupo = new HashSet<>();
        dfs(estudianteId, grupo);
        return grupo;
    }
    
    private void dfs(String nodo, Set<String> visitados) {
        visitados.add(nodo);
        
        for (String vecino : redEstudiantes.getOrDefault(nodo, Collections.emptySet())) {
            if (!visitados.contains(vecino)) {
                dfs(vecino, visitados);
            }
        }
    }
    
    // Calcular grado de conexión (número de compañeros)
    public int calcularGrado(String estudianteId) {
        return redEstudiantes.getOrDefault(estudianteId, Collections.emptySet()).size();
    }
    
    // Encontrar estudiantes más conectados
    public List<Map.Entry<String, Integer>> obtenerMasConectados(int top) {
        return redEstudiantes.entrySet().stream()
            .map(entry -> Map.entry(entry.getKey(), entry.getValue().size()))
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(top)
            .collect(Collectors.toList());
    }
}
```

### Ejemplo 3: Matriz de Adyacencia (Horarios de Clases)

```java
// Servicio: Detectar conflictos de horario usando matriz de adyacencia
@Service
public class ConflictoHorarioService {
    
    @Autowired
    private CursoRepository cursoRepository;
    
    // Detectar conflictos de horario entre cursos
    public Map<String, List<String>> detectarConflictos() {
        List<Curso> cursos = cursoRepository.findAll();
        int n = cursos.size();
        
        // Matriz de adyacencia: true si hay conflicto
        boolean[][] matrizConflictos = new boolean[n][n];
        
        // Llenar matriz
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (hayConflictoHorario(cursos.get(i), cursos.get(j))) {
                    matrizConflictos[i][j] = true;
                    matrizConflictos[j][i] = true; // Simétrica
                }
            }
        }
        
        // Convertir matriz a lista de conflictos
        Map<String, List<String>> conflictos = new HashMap<>();
        
        for (int i = 0; i < n; i++) {
            List<String> cursosConflictivos = new ArrayList<>();
            
            for (int j = 0; j < n; j++) {
                if (matrizConflictos[i][j]) {
                    cursosConflictivos.add(cursos.get(j).getId());
                }
            }
            
            if (!cursosConflictivos.isEmpty()) {
                conflictos.put(cursos.get(i).getId(), cursosConflictivos);
            }
        }
        
        return conflictos;
    }
    
    private boolean hayConflictoHorario(Curso curso1, Curso curso2) {
        // Verificar si los horarios se solapan
        // Implementación simplificada
        return curso1.getDia().equals(curso2.getDia()) &&
               horariosSeSuperponen(curso1.getHoraInicio(), curso1.getHoraFin(),
                                   curso2.getHoraInicio(), curso2.getHoraFin());
    }
    
    private boolean horariosSeSuperponen(LocalTime inicio1, LocalTime fin1,
                                        LocalTime inicio2, LocalTime fin2) {
        return inicio1.isBefore(fin2) && inicio2.isBefore(fin1);
    }
}
```

---

## 🎓 Algoritmos Comunes en Grafos

| Algoritmo | Propósito | Complejidad |
|-----------|-----------|-------------|
| **BFS** (Breadth-First Search) | Camino más corto (no ponderado) | O(V + E) |
| **DFS** (Depth-First Search) | Explorar todos los nodos | O(V + E) |
| **Dijkstra** | Camino más corto (ponderado) | O(V²) o O(E log V) |
| **Topological Sort** | Ordenar dependencias | O(V + E) |
| **Detección de ciclos** | Validar grafos acíclicos | O(V + E) |

Donde: V = vértices, E = aristas

---

## ⚠️ Consideraciones

### Cuándo usar Grafos:
- Modelar relaciones complejas
- Encontrar caminos o conexiones
- Detectar dependencias o ciclos
- Redes sociales, mapas, prerrequisitos

### Lista vs Matriz de Adyacencia:

| Característica | Lista | Matriz |
|----------------|-------|--------|
| Espacio | O(V + E) | O(V²) |
| Buscar arista | O(grado) | O(1) |
| Iterar vecinos | O(grado) | O(V) |
| Mejor para | Grafos dispersos | Grafos densos |

---

## 📊 Complejidad

- **Construcción**: O(V + E)
- **BFS/DFS**: O(V + E)
- **Dijkstra**: O((V + E) log V) con heap
- **Topological Sort**: O(V + E)