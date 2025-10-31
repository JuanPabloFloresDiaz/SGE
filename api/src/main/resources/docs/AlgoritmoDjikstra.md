# Algoritmo de Dijkstra

## 📚 Concepto

El **Algoritmo de Dijkstra** encuentra el **camino más corto** desde un nodo origen a todos los demás nodos en un **grafo ponderado** con pesos positivos.

### Características:
- **Greedy**: Selecciona el nodo no visitado con menor distancia
- **Pesos positivos**: No funciona con pesos negativos
- **Complejidad**: O((V + E) log V) con heap de prioridad
- **Resultado**: Distancia mínima a todos los nodos desde el origen

### Funcionamiento:
1. Inicializar distancias: origen = 0, demás = ∞
2. Marcar todos los nodos como no visitados
3. Seleccionar nodo no visitado con menor distancia
4. Actualizar distancias de sus vecinos
5. Marcar nodo como visitado
6. Repetir hasta visitar todos los nodos

---

## 🎯 Casos de Uso en SGE API

1. **Ruta de Aprendizaje Óptima**: Secuencia de asignaturas con menor "dificultad acumulada"
2. **Horario Óptimo**: Minimizar tiempo de desplazamiento entre aulas
3. **Red de Prerrequisitos**: Camino más corto para cumplir requisitos
4. **Sistema de Recomendaciones**: Encontrar cursos relacionados más cercanos
5. **Distribución de Recursos**: Asignar recursos con menor costo
6. **Planificación Académica**: Ruta más eficiente en plan de estudios

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Dijkstra para Ruta de Asignaturas

```java
// Modelo: Arista del grafo (asignatura → siguiente con "peso")
@Data
@AllArgsConstructor
class AristaAsignatura {
    private String destinoId;
    private int peso; // Dificultad, créditos, o tiempo estimado
}

// Modelo: Nodo con distancia (para PriorityQueue)
@Data
@AllArgsConstructor
class NodoDistancia implements Comparable<NodoDistancia> {
    private String nodoId;
    private int distancia;
    
    @Override
    public int compareTo(NodoDistancia otro) {
        return Integer.compare(this.distancia, otro.distancia);
    }
}

// Servicio: Algoritmo de Dijkstra
@Service
@Slf4j
public class DijkstraService {
    
    // Grafo: asignatura → lista de (asignatura_siguiente, peso)
    private Map<String, List<AristaAsignatura>> grafo = new HashMap<>();
    
    @Autowired
    private AsignaturaRepository asignaturaRepository;
    
    // Construir grafo de asignaturas
    @PostConstruct
    public void construirGrafo() {
        List<Asignatura> asignaturas = asignaturaRepository.findAll();
        
        for (Asignatura asignatura : asignaturas) {
            String id = asignatura.getId();
            grafo.putIfAbsent(id, new ArrayList<>());
            
            // Agregar aristas a asignaturas siguientes
            for (AsignaturaSiguiente siguiente : asignatura.getSiguientes()) {
                grafo.get(id).add(new AristaAsignatura(
                    siguiente.getAsignaturaId(),
                    siguiente.getDificultad() // Peso = dificultad
                ));
            }
        }
        
        log.info("Grafo de asignaturas construido: {} nodos", grafo.size());
    }
    
    // Ejecutar Dijkstra desde un nodo origen
    public DijkstraResultado dijkstra(String origen) {
        // Inicializar distancias
        Map<String, Integer> distancias = new HashMap<>();
        Map<String, String> padres = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        
        // Todas las distancias en infinito excepto el origen
        for (String nodo : grafo.keySet()) {
            distancias.put(nodo, Integer.MAX_VALUE);
        }
        distancias.put(origen, 0);
        
        // Cola de prioridad (min-heap)
        PriorityQueue<NodoDistancia> cola = new PriorityQueue<>();
        cola.offer(new NodoDistancia(origen, 0));
        
        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            String nodoActual = actual.getNodoId();
            
            // Si ya fue visitado, continuar
            if (visitados.contains(nodoActual)) {
                continue;
            }
            
            visitados.add(nodoActual);
            
            // Explorar vecinos
            List<AristaAsignatura> vecinos = grafo.getOrDefault(nodoActual, Collections.emptyList());
            
            for (AristaAsignatura arista : vecinos) {
                String vecino = arista.getDestinoId();
                
                if (!visitados.contains(vecino)) {
                    int nuevaDistancia = distancias.get(nodoActual) + arista.getPeso();
                    
                    // Relajación: actualizar si encontramos camino más corto
                    if (nuevaDistancia < distancias.get(vecino)) {
                        distancias.put(vecino, nuevaDistancia);
                        padres.put(vecino, nodoActual);
                        cola.offer(new NodoDistancia(vecino, nuevaDistancia));
                    }
                }
            }
        }
        
        return new DijkstraResultado(origen, distancias, padres);
    }
    
    // Reconstruir camino más corto hacia un destino
    public List<String> obtenerCamino(String origen, String destino) {
        DijkstraResultado resultado = dijkstra(origen);
        
        if (!resultado.getPadres().containsKey(destino)) {
            return Collections.emptyList(); // No hay camino
        }
        
        List<String> camino = new ArrayList<>();
        String actual = destino;
        
        while (actual != null) {
            camino.add(actual);
            actual = resultado.getPadres().get(actual);
        }
        
        Collections.reverse(camino);
        return camino;
    }
    
    // Obtener distancia más corta a un destino
    public int obtenerDistancia(String origen, String destino) {
        DijkstraResultado resultado = dijkstra(origen);
        Integer distancia = resultado.getDistancias().get(destino);
        
        return (distancia == null || distancia == Integer.MAX_VALUE) 
            ? -1 // No alcanzable
            : distancia;
    }
}

@Data
@AllArgsConstructor
class DijkstraResultado {
    private String origen;
    private Map<String, Integer> distancias;  // nodo → distancia mínima
    private Map<String, String> padres;       // nodo → predecesor en camino óptimo
}
```

### Ejemplo 2: Ruta Óptima de Aprendizaje

```java
// Servicio: Planificador de ruta académica
@Service
public class RutaAprendizajeService {
    
    @Autowired
    private DijkstraService dijkstraService;
    
    @Autowired
    private AsignaturaRepository asignaturaRepository;
    
    // Calcular ruta óptima de aprendizaje
    public RutaAcademica calcularRutaOptima(String asignaturaInicio, String asignaturaObjetivo) {
        // Ejecutar Dijkstra
        List<String> caminoIds = dijkstraService.obtenerCamino(asignaturaInicio, asignaturaObjetivo);
        
        if (caminoIds.isEmpty()) {
            throw new IllegalStateException("No existe ruta entre las asignaturas");
        }
        
        // Obtener detalles de asignaturas
        List<AsignaturaInfo> asignaturas = caminoIds.stream()
            .map(id -> asignaturaRepository.findById(id)
                .map(a -> new AsignaturaInfo(
                    a.getId(),
                    a.getCodigo(),
                    a.getNombre(),
                    a.getCreditos()
                ))
                .orElse(null))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        // Calcular métricas
        int dificultadTotal = dijkstraService.obtenerDistancia(asignaturaInicio, asignaturaObjetivo);
        int creditosTotales = asignaturas.stream()
            .mapToInt(AsignaturaInfo::getCreditos)
            .sum();
        
        return RutaAcademica.builder()
            .asignaturaInicio(asignaturaInicio)
            .asignaturaObjetivo(asignaturaObjetivo)
            .camino(asignaturas)
            .totalAsignaturas(asignaturas.size())
            .dificultadAcumulada(dificultadTotal)
            .creditosTotales(creditosTotales)
            .build();
    }
    
    // Encontrar todas las asignaturas alcanzables desde una asignatura
    public Map<String, Integer> obtenerAsignaturasAlcanzables(String asignaturaId) {
        DijkstraResultado resultado = dijkstraService.dijkstra(asignaturaId);
        
        return resultado.getDistancias().entrySet().stream()
            .filter(e -> e.getValue() != Integer.MAX_VALUE)
            .filter(e -> !e.getKey().equals(asignaturaId))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    // Obtener asignaturas más cercanas (menor dificultad)
    public List<AsignaturaDistancia> obtenerMasCercanas(String asignaturaId, int limite) {
        Map<String, Integer> alcanzables = obtenerAsignaturasAlcanzables(asignaturaId);
        
        return alcanzables.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .limit(limite)
            .map(e -> {
                Asignatura asig = asignaturaRepository.findById(e.getKey()).orElse(null);
                return asig != null 
                    ? new AsignaturaDistancia(e.getKey(), asig.getNombre(), e.getValue())
                    : null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}

@Data
@Builder
class RutaAcademica {
    private String asignaturaInicio;
    private String asignaturaObjetivo;
    private List<AsignaturaInfo> camino;
    private int totalAsignaturas;
    private int dificultadAcumulada;
    private int creditosTotales;
}

@Data
@AllArgsConstructor
class AsignaturaInfo {
    private String id;
    private String codigo;
    private String nombre;
    private int creditos;
}

@Data
@AllArgsConstructor
class AsignaturaDistancia {
    private String asignaturaId;
    private String nombre;
    private int distancia;
}

// Controlador
@RestController
@RequestMapping("/api/dijkstra/rutas")
@Tag(name = "Dijkstra", description = "Rutas óptimas de aprendizaje")
public class RutaAprendizajeController {
    
    @Autowired
    private RutaAprendizajeService rutaService;
    
    @Operation(summary = "Calcular ruta óptima entre asignaturas")
    @GetMapping("/optima")
    public ResponseEntity<RutaAcademica> calcularRutaOptima(
            @RequestParam String desde,
            @RequestParam String hasta) {
        
        RutaAcademica ruta = rutaService.calcularRutaOptima(desde, hasta);
        return ResponseEntity.ok(ruta);
    }
    
    @Operation(summary = "Obtener asignaturas alcanzables")
    @GetMapping("/alcanzables/{asignaturaId}")
    public ResponseEntity<Map<String, Integer>> obtenerAlcanzables(
            @PathVariable String asignaturaId) {
        
        return ResponseEntity.ok(rutaService.obtenerAsignaturasAlcanzables(asignaturaId));
    }
    
    @Operation(summary = "Obtener asignaturas más cercanas")
    @GetMapping("/cercanas/{asignaturaId}")
    public ResponseEntity<List<AsignaturaDistancia>> obtenerCercanas(
            @PathVariable String asignaturaId,
            @RequestParam(defaultValue = "5") int limite) {
        
        return ResponseEntity.ok(rutaService.obtenerMasCercanas(asignaturaId, limite));
    }
}
```

### Ejemplo 3: Dijkstra con Visualización de Pasos

```java
// Servicio: Dijkstra con pasos detallados
@Service
public class DijkstraVisualizacionService {
    
    @Data
    @AllArgsConstructor
    public static class PasoDijkstra {
        private int numeroPaso;
        private String nodoActual;
        private Map<String, Integer> distanciasActuales;
        private String accion;
        private List<String> nodosVisitados;
    }
    
    public List<PasoDijkstra> dijkstraConPasos(
            Map<String, List<AristaAsignatura>> grafo, 
            String origen) {
        
        List<PasoDijkstra> pasos = new ArrayList<>();
        Map<String, Integer> distancias = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        PriorityQueue<NodoDistancia> cola = new PriorityQueue<>();
        
        // Inicialización
        for (String nodo : grafo.keySet()) {
            distancias.put(nodo, Integer.MAX_VALUE);
        }
        distancias.put(origen, 0);
        cola.offer(new NodoDistancia(origen, 0));
        
        pasos.add(new PasoDijkstra(
            0, origen, new HashMap<>(distancias),
            "Inicialización: distancia origen = 0, demás = ∞",
            new ArrayList<>(visitados)
        ));
        
        int paso = 1;
        
        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            String nodoActual = actual.getNodoId();
            
            if (visitados.contains(nodoActual)) {
                continue;
            }
            
            visitados.add(nodoActual);
            
            pasos.add(new PasoDijkstra(
                paso++, nodoActual, new HashMap<>(distancias),
                String.format("Visitando nodo %s (distancia: %d)", 
                    nodoActual, distancias.get(nodoActual)),
                new ArrayList<>(visitados)
            ));
            
            // Explorar vecinos
            List<AristaAsignatura> vecinos = grafo.getOrDefault(nodoActual, Collections.emptyList());
            
            for (AristaAsignatura arista : vecinos) {
                String vecino = arista.getDestinoId();
                
                if (!visitados.contains(vecino)) {
                    int nuevaDistancia = distancias.get(nodoActual) + arista.getPeso();
                    
                    if (nuevaDistancia < distancias.get(vecino)) {
                        int distanciaAnterior = distancias.get(vecino);
                        distancias.put(vecino, nuevaDistancia);
                        cola.offer(new NodoDistancia(vecino, nuevaDistancia));
                        
                        pasos.add(new PasoDijkstra(
                            paso++, nodoActual, new HashMap<>(distancias),
                            String.format("Relajación: distancia a %s: %s → %d", 
                                vecino, 
                                distanciaAnterior == Integer.MAX_VALUE ? "∞" : distanciaAnterior,
                                nuevaDistancia),
                            new ArrayList<>(visitados)
                        ));
                    }
                }
            }
        }
        
        return pasos;
    }
}
```

### Ejemplo 4: Aplicación Práctica - Horario Óptimo

```java
// Servicio: Optimización de horarios con Dijkstra
@Service
public class HorarioOptimoService {
    
    // Modelo: Clase con ubicación
    @Data
    @AllArgsConstructor
    static class ClaseUbicacion {
        private String claseId;
        private String aula;
        private LocalTime horaInicio;
        private int piso;
        private String edificio;
    }
    
    // Calcular tiempo de desplazamiento entre aulas
    private int calcularTiempoDesplazamiento(ClaseUbicacion desde, ClaseUbicacion hasta) {
        int tiempo = 0;
        
        // Mismo edificio
        if (desde.getEdificio().equals(hasta.getEdificio())) {
            tiempo = Math.abs(desde.getPiso() - hasta.getPiso()) * 2; // 2 min por piso
        } else {
            tiempo = 10; // 10 min entre edificios
        }
        
        return tiempo;
    }
    
    // Encontrar secuencia óptima de clases
    public List<ClaseUbicacion> optimizarSecuenciaClases(
            List<ClaseUbicacion> clasesDelDia) {
        
        // Construir grafo de clases con tiempos de desplazamiento
        Map<String, List<AristaAsignatura>> grafo = new HashMap<>();
        
        for (int i = 0; i < clasesDelDia.size(); i++) {
            ClaseUbicacion clase1 = clasesDelDia.get(i);
            grafo.put(clase1.getClaseId(), new ArrayList<>());
            
            for (int j = 0; j < clasesDelDia.size(); j++) {
                if (i != j) {
                    ClaseUbicacion clase2 = clasesDelDia.get(j);
                    int tiempo = calcularTiempoDesplazamiento(clase1, clase2);
                    
                    grafo.get(clase1.getClaseId()).add(
                        new AristaAsignatura(clase2.getClaseId(), tiempo)
                    );
                }
            }
        }
        
        // Aplicar Dijkstra desde la primera clase
        // Retornar secuencia óptima
        return clasesDelDia; // Implementación simplificada
    }
}
```

---

## 🎓 Comparación con Otros Algoritmos

| Algoritmo | Pesos | Complejidad | Uso |
|-----------|-------|-------------|-----|
| **Dijkstra** | Positivos | O((V+E) log V) | Camino más corto desde un origen |
| Bellman-Ford | Cualquiera | O(VE) | Detecta ciclos negativos |
| Floyd-Warshall | Cualquiera | O(V³) | Todos los pares de caminos |
| A* | Positivos | Variable | Dijkstra con heurística |
| BFS | Sin pesos | O(V+E) | Grafos no ponderados |

---

## 📊 Complejidad

| Implementación | Complejidad | Espacio |
|----------------|-------------|---------|
| Con array | O(V²) | O(V) |
| Con min-heap | **O((V+E) log V)** ✅ | O(V) |
| Con Fibonacci heap | O(E + V log V) | O(V) |

Donde: V = vértices, E = aristas

---

## ⚠️ Consideraciones

### ✅ Ventajas:
- **Óptimo**: Garantiza el camino más corto
- **Eficiente**: O((V+E) log V) con heap
- **Versátil**: Muchas aplicaciones prácticas

### ❌ Limitaciones:
- **No funciona con pesos negativos**
- Calcula desde un solo origen (usar Floyd-Warshall para todos los pares)
- Requiere grafo completo en memoria

### Cuándo usar Dijkstra:
- ✅ Encontrar camino más corto en grafos con **pesos positivos**
- ✅ Redes, rutas, costos, distancias
- ✅ Cuando necesitas el camino óptimo desde un origen

### Alternativas:
- **Bellman-Ford**: Si hay pesos negativos
- **A***: Si tienes una heurística (más rápido)
- **BFS**: Si todos los pesos son iguales (1)