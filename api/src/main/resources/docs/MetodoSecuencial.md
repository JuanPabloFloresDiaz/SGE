# Búsqueda Secuencial (Lineal)

## 📚 Concepto

La **Búsqueda Secuencial** (o lineal) es el algoritmo de búsqueda más simple: recorre el array elemento por elemento hasta encontrar el objetivo o llegar al final.

### Funcionamiento:
1. Comenzar desde el primer elemento
2. Comparar cada elemento con el objetivo
3. Si coincide, **retornar índice**
4. Si no coincide, pasar al siguiente
5. Si llega al final sin encontrar, **retornar -1**

### Complejidad:
- **Tiempo**: O(n) - Puede revisar todos los elementos
- **Espacio**: O(1) - No usa memoria adicional
- **Ventaja**: Funciona en arrays **desordenados**

---

## 🎯 Casos de Uso en SGE API

1. **Buscar Estudiante por Nombre**: Sin índice ordenado
2. **Filtrar Calificaciones**: Encontrar todas las notas > 80
3. **Buscar en Listas Pequeñas**: < 10-20 elementos
4. **Validación de Existencia**: Verificar si existe un valor
5. **Búsqueda con Criterio Complejo**: Múltiples condiciones
6. **Datos Frecuentemente Modificados**: Mejor que mantener orden

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Búsqueda Secuencial Básica

```java
// Servicio: Búsqueda secuencial
@Service
public class BusquedaSecuencialService {
    
    // Búsqueda secuencial en array de enteros
    public int busquedaSecuencial(int[] array, int objetivo) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == objetivo) {
                return i; // Elemento encontrado
            }
        }
        return -1; // No encontrado
    }
    
    // Búsqueda secuencial en lista
    public <T> int busquedaSecuencialLista(List<T> lista, T objetivo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).equals(objetivo)) {
                return i;
            }
        }
        return -1;
    }
    
    // Búsqueda con contador de comparaciones
    public ResultadoBusqueda busquedaConStats(int[] array, int objetivo) {
        int comparaciones = 0;
        
        for (int i = 0; i < array.length; i++) {
            comparaciones++;
            
            if (array[i] == objetivo) {
                return new ResultadoBusqueda(i, comparaciones, true);
            }
        }
        
        return new ResultadoBusqueda(-1, comparaciones, false);
    }
    
    // Buscar todas las ocurrencias
    public List<Integer> buscarTodasOcurrencias(int[] array, int objetivo) {
        List<Integer> indices = new ArrayList<>();
        
        for (int i = 0; i < array.length; i++) {
            if (array[i] == objetivo) {
                indices.add(i);
            }
        }
        
        return indices;
    }
}

@Data
@AllArgsConstructor
class ResultadoBusqueda {
    private int indice;
    private int comparaciones;
    private boolean encontrado;
}
```

### Ejemplo 2: Buscar Estudiante por Nombre

```java
// Servicio: Búsqueda de estudiantes
@Service
@Slf4j
public class EstudianteBusquedaSecuencialService {
    
    @Autowired
    private EstudianteRepository estudianteRepository;
    
    // Buscar estudiante por nombre (búsqueda secuencial)
    public Estudiante buscarPorNombre(String nombre) {
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        
        long inicio = System.currentTimeMillis();
        
        for (Estudiante estudiante : estudiantes) {
            if (estudiante.getNombre().equalsIgnoreCase(nombre)) {
                long fin = System.currentTimeMillis();
                log.info("Estudiante encontrado en {} ms", fin - inicio);
                return estudiante;
            }
        }
        
        long fin = System.currentTimeMillis();
        log.info("Estudiante no encontrado. Búsqueda tomó {} ms", fin - inicio);
        
        throw new EntityNotFoundException("Estudiante no encontrado: " + nombre);
    }
    
    // Buscar con criterio personalizado
    public List<Estudiante> buscarPorCriterio(Predicate<Estudiante> criterio) {
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        List<Estudiante> resultados = new ArrayList<>();
        
        for (Estudiante estudiante : estudiantes) {
            if (criterio.test(estudiante)) {
                resultados.add(estudiante);
            }
        }
        
        return resultados;
    }
    
    // Buscar estudiantes con promedio mayor a umbral
    public List<Estudiante> buscarPorPromedioMayorA(double umbral) {
        return buscarPorCriterio(est -> est.getPromedio() > umbral);
    }
    
    // Buscar estudiantes activos de una carrera específica
    public List<Estudiante> buscarActivosPorCarrera(String carrera) {
        return buscarPorCriterio(est -> 
            est.isActivo() && est.getCarrera().equalsIgnoreCase(carrera)
        );
    }
    
    // Contar estudiantes que cumplen criterio
    public long contarPorCriterio(Predicate<Estudiante> criterio) {
        List<Estudiante> estudiantes = estudianteRepository.findAll();
        long contador = 0;
        
        for (Estudiante estudiante : estudiantes) {
            if (criterio.test(estudiante)) {
                contador++;
            }
        }
        
        return contador;
    }
}

// Controlador
@RestController
@RequestMapping("/api/busqueda-secuencial/estudiantes")
@Tag(name = "Búsqueda Secuencial", description = "Búsqueda lineal de estudiantes")
public class BusquedaSecuencialController {
    
    @Autowired
    private EstudianteBusquedaSecuencialService busquedaService;
    
    @Operation(summary = "Buscar estudiante por nombre")
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Estudiante> buscarPorNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(busquedaService.buscarPorNombre(nombre));
    }
    
    @Operation(summary = "Buscar estudiantes con promedio mayor a")
    @GetMapping("/promedio-mayor/{umbral}")
    public ResponseEntity<List<Estudiante>> buscarPorPromedio(@PathVariable double umbral) {
        return ResponseEntity.ok(busquedaService.buscarPorPromedioMayorA(umbral));
    }
    
    @Operation(summary = "Buscar estudiantes activos por carrera")
    @GetMapping("/activos/carrera/{carrera}")
    public ResponseEntity<List<Estudiante>> buscarActivosPorCarrera(@PathVariable String carrera) {
        return ResponseEntity.ok(busquedaService.buscarActivosPorCarrera(carrera));
    }
}
```

### Ejemplo 3: Búsqueda con Early Exit

```java
// Servicio: Optimizaciones de búsqueda secuencial
@Service
public class BusquedaOptimizadaService {
    
    // Búsqueda con centinela (evita verificar límites)
    public int busquedaConCentinela(int[] array, int objetivo) {
        int n = array.length;
        
        if (n == 0) return -1;
        
        // Guardar último elemento
        int ultimo = array[n - 1];
        
        // Poner objetivo como centinela al final
        array[n - 1] = objetivo;
        
        int i = 0;
        while (array[i] != objetivo) {
            i++;
        }
        
        // Restaurar último elemento
        array[n - 1] = ultimo;
        
        // Verificar si encontramos el objetivo o llegamos al final
        if (i < n - 1 || array[n - 1] == objetivo) {
            return i;
        }
        
        return -1;
    }
    
    // Búsqueda con ordenamiento por frecuencia (Move-to-Front)
    public int busquedaConMTF(List<Integer> lista, int objetivo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == objetivo) {
                // Mover elemento encontrado al frente
                if (i > 0) {
                    int elemento = lista.remove(i);
                    lista.add(0, elemento);
                }
                return 0; // Ahora está en posición 0
            }
        }
        return -1;
    }
    
    // Búsqueda con salida temprana (múltiples condiciones)
    public Estudiante buscarConCondicionesMultiples(
            List<Estudiante> estudiantes,
            String nombre,
            String email,
            String matricula) {
        
        for (Estudiante est : estudiantes) {
            // Early exit: primera coincidencia exacta
            if (est.getMatricula().equals(matricula)) {
                return est;
            }
            
            // Segunda prioridad: email
            if (est.getEmail().equalsIgnoreCase(email)) {
                return est;
            }
            
            // Tercera prioridad: nombre
            if (est.getNombre().equalsIgnoreCase(nombre)) {
                return est;
            }
        }
        
        return null;
    }
}
```

### Ejemplo 4: Búsqueda en Strings (Substring)

```java
// Servicio: Búsqueda en texto
@Service
public class BusquedaTextoService {
    
    // Buscar substring en texto (algoritmo naive)
    public List<Integer> buscarSubstring(String texto, String patron) {
        List<Integer> posiciones = new ArrayList<>();
        int n = texto.length();
        int m = patron.length();
        
        // Búsqueda secuencial de cada posición posible
        for (int i = 0; i <= n - m; i++) {
            boolean coincide = true;
            
            // Verificar si patron coincide en posición i
            for (int j = 0; j < m; j++) {
                if (texto.charAt(i + j) != patron.charAt(j)) {
                    coincide = false;
                    break;
                }
            }
            
            if (coincide) {
                posiciones.add(i);
            }
        }
        
        return posiciones;
    }
    
    // Buscar cursos por palabra clave en nombre o descripción
    public List<Curso> buscarCursosPorPalabraClave(String palabraClave) {
        List<Curso> todosCursos = cursoRepository.findAll();
        List<Curso> resultados = new ArrayList<>();
        
        String palabraLower = palabraClave.toLowerCase();
        
        for (Curso curso : todosCursos) {
            // Búsqueda secuencial en nombre
            if (curso.getNombre().toLowerCase().contains(palabraLower)) {
                resultados.add(curso);
                continue;
            }
            
            // Búsqueda secuencial en descripción
            if (curso.getDescripcion() != null && 
                curso.getDescripcion().toLowerCase().contains(palabraLower)) {
                resultados.add(curso);
            }
        }
        
        return resultados;
    }
}
```

### Ejemplo 5: Comparación de Performance

```java
// Servicio: Benchmark búsqueda secuencial vs binaria
@Service
@Slf4j
public class BusquedaBenchmarkService {
    
    @Data
    @AllArgsConstructor
    public static class ResultadoBenchmark {
        private String algoritmo;
        private long tiempoMs;
        private int comparaciones;
        private boolean encontrado;
    }
    
    public List<ResultadoBenchmark> compararBusquedas(int tamaño, int objetivo) {
        List<ResultadoBenchmark> resultados = new ArrayList<>();
        
        // Generar datos
        int[] datosDesordenados = generarDatosAleatorios(tamaño);
        int[] datosOrdenados = Arrays.copyOf(datosDesordenados, tamaño);
        Arrays.sort(datosOrdenados);
        
        // Benchmark búsqueda secuencial
        resultados.add(benchmarkSecuencial(datosDesordenados, objetivo));
        
        // Benchmark búsqueda binaria
        resultados.add(benchmarkBinaria(datosOrdenados, objetivo));
        
        // Benchmark Java indexOf
        resultados.add(benchmarkIndexOf(datosDesordenados, objetivo));
        
        return resultados;
    }
    
    private ResultadoBenchmark benchmarkSecuencial(int[] array, int objetivo) {
        long inicio = System.nanoTime();
        int comparaciones = 0;
        int indice = -1;
        
        for (int i = 0; i < array.length; i++) {
            comparaciones++;
            if (array[i] == objetivo) {
                indice = i;
                break;
            }
        }
        
        long fin = System.nanoTime();
        
        return new ResultadoBenchmark(
            "Búsqueda Secuencial",
            (fin - inicio) / 1_000_000, // ns a ms
            comparaciones,
            indice != -1
        );
    }
    
    private ResultadoBenchmark benchmarkBinaria(int[] array, int objetivo) {
        long inicio = System.nanoTime();
        
        int indice = Arrays.binarySearch(array, objetivo);
        
        long fin = System.nanoTime();
        
        return new ResultadoBenchmark(
            "Búsqueda Binaria",
            (fin - inicio) / 1_000_000,
            (int) (Math.log(array.length) / Math.log(2)), // Estimado
            indice >= 0
        );
    }
    
    private ResultadoBenchmark benchmarkIndexOf(int[] array, int objetivo) {
        List<Integer> lista = Arrays.stream(array).boxed().collect(Collectors.toList());
        
        long inicio = System.nanoTime();
        int indice = lista.indexOf(objetivo);
        long fin = System.nanoTime();
        
        return new ResultadoBenchmark(
            "Java indexOf",
            (fin - inicio) / 1_000_000,
            -1, // No disponible
            indice != -1
        );
    }
    
    private int[] generarDatosAleatorios(int tamaño) {
        Random random = new Random();
        return random.ints(tamaño, 0, tamaño * 10).toArray();
    }
}
```

---

## 🎓 Comparación con Búsqueda Binaria

| Característica | Búsqueda Secuencial | Búsqueda Binaria |
|----------------|---------------------|------------------|
| Complejidad | O(n) | **O(log n)** ✅ |
| Requisito | Ninguno | **Array ordenado** |
| Mejor caso | O(1) | O(1) |
| Peor caso | O(n) | O(log n) |
| Implementación | Muy simple | Moderada |
| Uso | Arrays pequeños/desordenados | Arrays grandes ordenados |

---

## 📊 Eficiencia por Tamaño

| Tamaño (n) | Mejor caso | Caso promedio | Peor caso |
|------------|------------|---------------|-----------|
| 10 | 1 comparación | 5 comparaciones | 10 comparaciones |
| 100 | 1 | 50 | 100 |
| 1,000 | 1 | 500 | 1,000 |
| 10,000 | 1 | 5,000 | 10,000 |

---

## ⚠️ Consideraciones

### ✅ Ventajas:
- **Muy simple** de implementar
- Funciona con datos **desordenados**
- No requiere preprocesamiento
- **Mejor para arrays pequeños** (< 10-20)

### ❌ Desventajas:
- **Lento para arrays grandes**: O(n)
- Revisa todos los elementos en peor caso
- No aprovecha orden (si existe)

### Cuándo usar Búsqueda Secuencial:
- ✅ Arrays **pequeños** (< 20 elementos)
- ✅ Datos **desordenados**
- ✅ Búsqueda poco frecuente
- ✅ Criterios de búsqueda **complejos**
- ✅ Cuando ordenar cuesta más que buscar

### Cuándo NO usar:
- ❌ Arrays **grandes** y ordenados (usar búsqueda binaria)
- ❌ Búsquedas **muy frecuentes** (crear índice)
- ❌ Requisitos de **alta performance**

---

## 💡 Optimizaciones

1. **Move-to-Front**: Mover elemento encontrado al inicio
2. **Centinela**: Evitar verificación de límites
3. **Early Exit**: Salir apenas se encuentre
4. **Ordenar por frecuencia**: Elementos más usados primero