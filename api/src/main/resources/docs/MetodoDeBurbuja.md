# Método de Burbuja (Bubble Sort)

## 📚 Concepto

El **Bubble Sort** es un algoritmo de ordenamiento simple que **compara pares de elementos adyacentes** y los intercambia si están en el orden incorrecto.

### Funcionamiento:
1. Recorrer el array desde el inicio
2. Comparar cada par de elementos consecutivos
3. Si están desordenados, **intercambiarlos**
4. Repetir hasta que no haya más intercambios
5. El elemento más grande "burbujea" hacia el final en cada pasada

**Nombre**: Se llama "burbuja" porque los elementos grandes "flotan" hacia el final como burbujas.

---

## 🎯 Casos de Uso en SGE API

1. **Ordenar Calificaciones**: Lista pequeña de notas de un estudiante
2. **Ordenar Asistencias por Fecha**: Fechas recientes primero
3. **Ranking Temporal**: Ordenar pocos estudiantes para vista rápida
4. **Datos Casi Ordenados**: Cuando solo hay pocos elementos fuera de lugar
5. **Ordenamiento Educativo**: Mostrar paso a paso cómo se ordena
6. **Validación de Orden**: Verificar si lista está ordenada

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Bubble Sort Básico

```java
// Servicio: Ordenamiento con Bubble Sort
@Service
@Slf4j
public class BubbleSortService {
    
    // Bubble Sort básico - O(n²)
    public int[] bubbleSort(int[] array) {
        int n = array.length;
        boolean intercambio;
        
        for (int i = 0; i < n - 1; i++) {
            intercambio = false;
            
            // Última i elementos ya están en su posición
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    // Intercambiar
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    
                    intercambio = true;
                }
            }
            
            // Si no hubo intercambios, ya está ordenado
            if (!intercambio) {
                log.info("Array ordenado en pasada {}", i + 1);
                break;
            }
        }
        
        return array;
    }
    
    // Bubble Sort optimizado (detección temprana)
    public int[] bubbleSortOptimizado(int[] array) {
        int n = array.length;
        
        for (int i = 0; i < n - 1; i++) {
            boolean intercambio = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    // Swap
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    intercambio = true;
                }
            }
            
            // Optimización: si no hubo intercambios, terminar
            if (!intercambio) {
                break;
            }
        }
        
        return array;
    }
    
    // Bubble Sort con conteo de operaciones
    public BubbleSortStats bubbleSortConStats(int[] array) {
        int n = array.length;
        int comparaciones = 0;
        int intercambios = 0;
        int pasadas = 0;
        
        for (int i = 0; i < n - 1; i++) {
            pasadas++;
            boolean huboIntercambio = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                comparaciones++;
                
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    intercambios++;
                    huboIntercambio = true;
                }
            }
            
            if (!huboIntercambio) {
                break;
            }
        }
        
        return new BubbleSortStats(array, comparaciones, intercambios, pasadas);
    }
}

@Data
@AllArgsConstructor
class BubbleSortStats {
    private int[] arrayOrdenado;
    private int totalComparaciones;
    private int totalIntercambios;
    private int totalPasadas;
}
```

### Ejemplo 2: Ordenar Calificaciones de Estudiantes

```java
// Modelo: Calificación
@Data
@AllArgsConstructor
class CalificacionEstudiante implements Comparable<CalificacionEstudiante> {
    private String estudianteId;
    private String nombreEstudiante;
    private double nota;
    private LocalDate fecha;
    
    @Override
    public int compareTo(CalificacionEstudiante otra) {
        return Double.compare(this.nota, otra.nota);
    }
}

// Servicio: Ordenar calificaciones
@Service
public class CalificacionOrdenamientoService {
    
    // Ordenar calificaciones por nota (ascendente)
    public List<CalificacionEstudiante> ordenarPorNota(List<CalificacionEstudiante> calificaciones) {
        List<CalificacionEstudiante> lista = new ArrayList<>(calificaciones);
        int n = lista.size();
        
        for (int i = 0; i < n - 1; i++) {
            boolean intercambio = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).getNota() > lista.get(j + 1).getNota()) {
                    // Intercambiar
                    CalificacionEstudiante temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                    intercambio = true;
                }
            }
            
            if (!intercambio) break;
        }
        
        return lista;
    }
    
    // Ordenar por nota (descendente) - mejores primero
    public List<CalificacionEstudiante> ordenarPorNotaDesc(List<CalificacionEstudiante> calificaciones) {
        List<CalificacionEstudiante> lista = new ArrayList<>(calificaciones);
        int n = lista.size();
        
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).getNota() < lista.get(j + 1).getNota()) {
                    CalificacionEstudiante temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
        
        return lista;
    }
    
    // Ordenar por fecha
    public List<CalificacionEstudiante> ordenarPorFecha(List<CalificacionEstudiante> calificaciones) {
        List<CalificacionEstudiante> lista = new ArrayList<>(calificaciones);
        int n = lista.size();
        
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).getFecha().isAfter(lista.get(j + 1).getFecha())) {
                    CalificacionEstudiante temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
        
        return lista;
    }
}

// Controlador
@RestController
@RequestMapping("/api/ordenamiento/calificaciones")
@Tag(name = "Ordenamiento", description = "Ordenar calificaciones con Bubble Sort")
public class OrdenamientoController {
    
    @Autowired
    private CalificacionOrdenamientoService ordenamientoService;
    
    @Autowired
    private CalificacionRepository calificacionRepository;
    
    @Operation(summary = "Obtener calificaciones ordenadas por nota (ascendente)")
    @GetMapping("/curso/{cursoId}/por-nota-asc")
    public ResponseEntity<List<CalificacionEstudiante>> ordenarPorNotaAsc(
            @PathVariable String cursoId) {
        
        List<CalificacionEstudiante> calificaciones = 
            obtenerCalificaciones(cursoId);
        
        List<CalificacionEstudiante> ordenadas = 
            ordenamientoService.ordenarPorNota(calificaciones);
        
        return ResponseEntity.ok(ordenadas);
    }
    
    @Operation(summary = "Obtener top calificaciones (descendente)")
    @GetMapping("/curso/{cursoId}/top")
    public ResponseEntity<List<CalificacionEstudiante>> obtenerTop(
            @PathVariable String cursoId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<CalificacionEstudiante> calificaciones = 
            obtenerCalificaciones(cursoId);
        
        List<CalificacionEstudiante> ordenadas = 
            ordenamientoService.ordenarPorNotaDesc(calificaciones);
        
        return ResponseEntity.ok(
            ordenadas.stream().limit(limit).collect(Collectors.toList())
        );
    }
    
    private List<CalificacionEstudiante> obtenerCalificaciones(String cursoId) {
        return calificacionRepository.findByCursoId(cursoId).stream()
            .map(c -> new CalificacionEstudiante(
                c.getEstudianteId(),
                c.getNombreEstudiante(),
                c.getNota(),
                c.getFecha()
            ))
            .collect(Collectors.toList());
    }
}
```

### Ejemplo 3: Visualización Paso a Paso (Educativo)

```java
// Servicio: Visualización del algoritmo
@Service
public class BubbleSortVisualizacionService {
    
    @Data
    @AllArgsConstructor
    public static class PasoBubbleSort {
        private int numeroPasada;
        private int indiceComparacion;
        private int[] arrayActual;
        private boolean huboIntercambio;
        private String descripcion;
    }
    
    // Ejecutar Bubble Sort con captura de cada paso
    public List<PasoBubbleSort> bubbleSortPasoAPaso(int[] array) {
        List<PasoBubbleSort> pasos = new ArrayList<>();
        int n = array.length;
        int[] arr = Arrays.copyOf(array, n);
        
        pasos.add(new PasoBubbleSort(
            0, -1, Arrays.copyOf(arr, n), false, "Array inicial"
        ));
        
        for (int i = 0; i < n - 1; i++) {
            boolean huboIntercambio = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                boolean intercambio = false;
                
                if (arr[j] > arr[j + 1]) {
                    // Intercambiar
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    intercambio = true;
                    huboIntercambio = true;
                }
                
                // Capturar paso
                pasos.add(new PasoBubbleSort(
                    i + 1,
                    j,
                    Arrays.copyOf(arr, n),
                    intercambio,
                    String.format("Pasada %d: Comparando índices %d y %d %s",
                        i + 1, j, j + 1,
                        intercambio ? "(intercambiados)" : "(sin cambio)")
                ));
            }
            
            if (!huboIntercambio) {
                pasos.add(new PasoBubbleSort(
                    i + 1, -1, Arrays.copyOf(arr, n), false,
                    "Array ordenado - algoritmo terminado"
                ));
                break;
            }
        }
        
        return pasos;
    }
    
    // Generar representación visual ASCII
    public String visualizarPaso(int[] array, int indiceComparando) {
        StringBuilder sb = new StringBuilder();
        
        // Dibujar array
        sb.append("Array: ");
        for (int i = 0; i < array.length; i++) {
            if (i == indiceComparando || i == indiceComparando + 1) {
                sb.append(String.format("[%d]", array[i]));
            } else {
                sb.append(String.format(" %d ", array[i]));
            }
            sb.append(" ");
        }
        
        return sb.toString();
    }
}

// Controlador para visualización
@RestController
@RequestMapping("/api/bubble-sort/visualizar")
@Tag(name = "Visualización", description = "Ver Bubble Sort paso a paso")
public class BubbleSortVisualizacionController {
    
    @Autowired
    private BubbleSortVisualizacionService visualizacionService;
    
    @Operation(summary = "Ejecutar Bubble Sort con pasos detallados")
    @PostMapping("/paso-a-paso")
    public ResponseEntity<List<BubbleSortVisualizacionService.PasoBubbleSort>> ejecutarPasoAPaso(
            @RequestBody int[] array) {
        
        List<BubbleSortVisualizacionService.PasoBubbleSort> pasos =
            visualizacionService.bubbleSortPasoAPaso(array);
        
        return ResponseEntity.ok(pasos);
    }
}
```

### Ejemplo 4: Comparación de Performance

```java
// Servicio: Benchmark de algoritmos
@Service
@Slf4j
public class OrdenamientoBenchmarkService {
    
    @Data
    @AllArgsConstructor
    public static class ResultadoBenchmark {
        private String algoritmo;
        private int tamanoDatos;
        private long tiempoMs;
        private int comparaciones;
        private int intercambios;
    }
    
    public List<ResultadoBenchmark> compararAlgoritmos(int tamano) {
        List<ResultadoBenchmark> resultados = new ArrayList<>();
        
        // Generar datos aleatorios
        int[] datos = generarDatosAleatorios(tamano);
        
        // Benchmark Bubble Sort
        resultados.add(benchmarkBubbleSort(Arrays.copyOf(datos, tamano)));
        
        // Benchmark Java Sort (TimSort - O(n log n))
        resultados.add(benchmarkJavaSort(Arrays.copyOf(datos, tamano)));
        
        return resultados;
    }
    
    private ResultadoBenchmark benchmarkBubbleSort(int[] array) {
        long inicio = System.currentTimeMillis();
        int[] contador = {0, 0}; // [comparaciones, intercambios]
        
        bubbleSortConConteo(array, contador);
        
        long fin = System.currentTimeMillis();
        
        return new ResultadoBenchmark(
            "Bubble Sort",
            array.length,
            fin - inicio,
            contador[0],
            contador[1]
        );
    }
    
    private ResultadoBenchmark benchmarkJavaSort(int[] array) {
        long inicio = System.currentTimeMillis();
        
        Arrays.sort(array);
        
        long fin = System.currentTimeMillis();
        
        return new ResultadoBenchmark(
            "Java Arrays.sort (TimSort)",
            array.length,
            fin - inicio,
            -1, // No aplica
            -1  // No aplica
        );
    }
    
    private void bubbleSortConConteo(int[] array, int[] contador) {
        int n = array.length;
        
        for (int i = 0; i < n - 1; i++) {
            boolean intercambio = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                contador[0]++; // Comparación
                
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    contador[1]++; // Intercambio
                    intercambio = true;
                }
            }
            
            if (!intercambio) break;
        }
    }
    
    private int[] generarDatosAleatorios(int tamano) {
        Random random = new Random();
        return random.ints(tamano, 0, 1000).toArray();
    }
}
```

---

## 🎓 Análisis de Complejidad

| Caso | Complejidad | Explicación |
|------|-------------|-------------|
| **Mejor caso** | O(n) | Array ya ordenado (con optimización) |
| **Caso promedio** | **O(n²)** | Array aleatorio |
| **Peor caso** | **O(n²)** | Array en orden inverso |
| **Espacio** | O(1) | In-place (no usa memoria extra) |

### Número de Operaciones:
- **Comparaciones**: O(n²) en peor caso
- **Intercambios**: O(n²) en peor caso
- **Pasadas**: Máximo n-1, mínimo 1

---

## 📊 Comparación con Otros Algoritmos

| Algoritmo | Mejor | Promedio | Peor | Estable | In-place |
|-----------|-------|----------|------|---------|----------|
| **Bubble Sort** | O(n) | O(n²) | O(n²) | ✅ Sí | ✅ Sí |
| Quick Sort | O(n log n) | O(n log n) | O(n²) | ❌ No | ✅ Sí |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | ✅ Sí | ❌ No |
| Insertion Sort | O(n) | O(n²) | O(n²) | ✅ Sí | ✅ Sí |
| Java TimSort | O(n) | O(n log n) | O(n log n) | ✅ Sí | ❌ No |

---

## ⚠️ Consideraciones

### ✅ Ventajas:
- **Muy simple** de entender e implementar
- **Estable**: Mantiene el orden relativo de elementos iguales
- **In-place**: No requiere memoria adicional
- **Eficiente para datos casi ordenados** (con optimización)
- **Detecta arrays ordenados** rápidamente

### ❌ Desventajas:
- **Muy lento** para arrays grandes: O(n²)
- **No escalable**: Inútil para más de 100 elementos
- Muchas comparaciones e intercambios

### Cuándo usar Bubble Sort:
- ✅ Arrays **muy pequeños** (< 10-20 elementos)
- ✅ **Propósitos educativos** (enseñar algoritmos)
- ✅ Datos **casi ordenados**
- ✅ Cuando necesitas **estabilidad** y simplicidad

### Cuándo NO usar:
- ❌ Arrays grandes (> 50 elementos)
- ❌ Aplicaciones de **producción**
- ❌ Cuando necesitas **performance** (usar QuickSort, MergeSort o TimSort)

---

## 💡 Optimizaciones

1. **Detección temprana**: Detener si no hay intercambios
2. **Seguimiento del último intercambio**: Reducir rango en cada pasada
3. **Bubble Sort bidireccional (Cocktail Sort)**: Alternar dirección de pasadas