# Búsqueda Binaria

## 📚 Concepto

La **Búsqueda Binaria** es un algoritmo eficiente para encontrar un elemento en un **array ordenado**.

### Funcionamiento:
1. Comparar el elemento buscado con el elemento del **medio**
2. Si es igual: **¡Encontrado!**
3. Si es menor: buscar en la **mitad izquierda**
4. Si es mayor: buscar en la **mitad derecha**
5. Repetir hasta encontrar o agotar elementos

**Requisito**: El array **DEBE estar ordenado**.

### Complejidad:
- **Tiempo**: O(log n) - Divide el espacio de búsqueda a la mitad en cada paso
- **Espacio**: O(1) iterativo, O(log n) recursivo

---

## 🎯 Casos de Uso en SGE API

1. **Buscar Estudiante por Matrícula**: Matrículas ordenadas numéricamente
2. **Buscar Calificación Específica**: En lista ordenada de notas
3. **Buscar Curso por Código**: Códigos alfanuméricos ordenados
4. **Encontrar Primer Estudiante con Promedio ≥ X**: Búsqueda de umbral
5. **Búsqueda de Horarios**: Encontrar clase a cierta hora
6. **Verificar Disponibilidad**: Buscar slot libre en agenda ordenada

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Búsqueda Binaria Básica (Iterativa)

```java
// Servicio: Búsqueda de estudiantes
@Service
public class BusquedaBinariaService {
    
    // Búsqueda binaria iterativa
    public int busquedaBinaria(int[] array, int objetivo) {
        int izquierda = 0;
        int derecha = array.length - 1;
        
        while (izquierda <= derecha) {
            int medio = izquierda + (derecha - izquierda) / 2; // Evita overflow
            
            if (array[medio] == objetivo) {
                return medio; // Encontrado
            }
            
            if (array[medio] < objetivo) {
                izquierda = medio + 1; // Buscar en mitad derecha
            } else {
                derecha = medio - 1; // Buscar en mitad izquierda
            }
        }
        
        return -1; // No encontrado
    }
    
    // Búsqueda binaria recursiva
    public int busquedaBinariaRecursiva(int[] array, int objetivo, int izq, int der) {
        if (izq > der) {
            return -1; // No encontrado
        }
        
        int medio = izq + (der - izq) / 2;
        
        if (array[medio] == objetivo) {
            return medio;
        }
        
        if (array[medio] < objetivo) {
            return busquedaBinariaRecursiva(array, objetivo, medio + 1, der);
        } else {
            return busquedaBinariaRecursiva(array, objetivo, izq, medio - 1);
        }
    }
}
```

### Ejemplo 2: Buscar Estudiante por Matrícula

```java
// Modelo: Estudiante simplificado
@Data
@AllArgsConstructor
class EstudianteMatricula implements Comparable<EstudianteMatricula> {
    private int matricula;
    private String nombre;
    private String email;
    
    @Override
    public int compareTo(EstudianteMatricula otro) {
        return Integer.compare(this.matricula, otro.matricula);
    }
}

// Servicio: Búsqueda de estudiantes
@Service
@Slf4j
public class EstudianteBusquedaService {
    
    private List<EstudianteMatricula> estudiantes = new ArrayList<>();
    
    @Autowired
    private EstudianteRepository estudianteRepository;
    
    @PostConstruct
    public void cargarEstudiantes() {
        // Cargar estudiantes y ordenar por matrícula
        estudiantes = estudianteRepository.findAll().stream()
            .map(e -> new EstudianteMatricula(
                e.getMatricula(),
                e.getNombre(),
                e.getEmail()
            ))
            .sorted()
            .collect(Collectors.toList());
        
        log.info("Estudiantes cargados y ordenados: {}", estudiantes.size());
    }
    
    // Buscar estudiante por matrícula usando búsqueda binaria
    public EstudianteMatricula buscarPorMatricula(int matricula) {
        int indice = busquedaBinariaPorMatricula(matricula);
        
        if (indice == -1) {
            throw new EntityNotFoundException("Estudiante no encontrado: " + matricula);
        }
        
        return estudiantes.get(indice);
    }
    
    private int busquedaBinariaPorMatricula(int matricula) {
        int izq = 0;
        int der = estudiantes.size() - 1;
        
        while (izq <= der) {
            int medio = izq + (der - izq) / 2;
            EstudianteMatricula estudianteMedio = estudiantes.get(medio);
            
            if (estudianteMedio.getMatricula() == matricula) {
                return medio;
            }
            
            if (estudianteMedio.getMatricula() < matricula) {
                izq = medio + 1;
            } else {
                der = medio - 1;
            }
        }
        
        return -1;
    }
    
    // Alternativa: Usar Collections.binarySearch
    public EstudianteMatricula buscarConCollections(int matricula) {
        EstudianteMatricula clave = new EstudianteMatricula(matricula, null, null);
        
        int indice = Collections.binarySearch(estudiantes, clave);
        
        if (indice < 0) {
            throw new EntityNotFoundException("Estudiante no encontrado: " + matricula);
        }
        
        return estudiantes.get(indice);
    }
    
    // Verificar si existe matrícula
    public boolean existeMatricula(int matricula) {
        return busquedaBinariaPorMatricula(matricula) != -1;
    }
}

// Controlador
@RestController
@RequestMapping("/api/busqueda/estudiantes")
@Tag(name = "Búsqueda Binaria", description = "Búsqueda eficiente de estudiantes")
public class BusquedaBinariaController {
    
    @Autowired
    private EstudianteBusquedaService busquedaService;
    
    @Operation(summary = "Buscar estudiante por matrícula")
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<EstudianteMatricula> buscarPorMatricula(
            @PathVariable int matricula) {
        
        EstudianteMatricula estudiante = busquedaService.buscarPorMatricula(matricula);
        return ResponseEntity.ok(estudiante);
    }
    
    @Operation(summary = "Verificar si existe matrícula")
    @GetMapping("/existe/{matricula}")
    public ResponseEntity<Boolean> existeMatricula(@PathVariable int matricula) {
        return ResponseEntity.ok(busquedaService.existeMatricula(matricula));
    }
}
```

### Ejemplo 3: Encontrar Primer Elemento ≥ Umbral

```java
// Servicio: Búsquedas avanzadas
@Service
public class BusquedaAvanzadaService {
    
    // Encontrar el primer estudiante con promedio >= umbral
    public int buscarPrimerMayorIgual(List<Double> promediosOrdenados, double umbral) {
        int izq = 0;
        int der = promediosOrdenados.size() - 1;
        int resultado = -1;
        
        while (izq <= der) {
            int medio = izq + (der - izq) / 2;
            
            if (promediosOrdenados.get(medio) >= umbral) {
                resultado = medio; // Candidato encontrado
                der = medio - 1;   // Seguir buscando a la izquierda
            } else {
                izq = medio + 1;   // Buscar a la derecha
            }
        }
        
        return resultado;
    }
    
    // Encontrar el último estudiante con promedio <= umbral
    public int buscarUltimoMenorIgual(List<Double> promediosOrdenados, double umbral) {
        int izq = 0;
        int der = promediosOrdenados.size() - 1;
        int resultado = -1;
        
        while (izq <= der) {
            int medio = izq + (der - izq) / 2;
            
            if (promediosOrdenados.get(medio) <= umbral) {
                resultado = medio; // Candidato encontrado
                izq = medio + 1;   // Seguir buscando a la derecha
            } else {
                der = medio - 1;   // Buscar a la izquierda
            }
        }
        
        return resultado;
    }
    
    // Contar estudiantes con promedio en rango [min, max]
    public int contarEnRango(List<Double> promediosOrdenados, double min, double max) {
        int primerMayor = buscarPrimerMayorIgual(promediosOrdenados, min);
        int ultimoMenor = buscarUltimoMenorIgual(promediosOrdenados, max);
        
        if (primerMayor == -1 || ultimoMenor == -1) {
            return 0;
        }
        
        return ultimoMenor - primerMayor + 1;
    }
    
    // Buscar posición de inserción para mantener orden
    public int buscarPosicionInsercion(List<Integer> listaOrdenada, int valor) {
        int izq = 0;
        int der = listaOrdenada.size();
        
        while (izq < der) {
            int medio = izq + (der - izq) / 2;
            
            if (listaOrdenada.get(medio) < valor) {
                izq = medio + 1;
            } else {
                der = medio;
            }
        }
        
        return izq;
    }
}
```

### Ejemplo 4: Búsqueda en Array Rotado

```java
// Servicio: Búsqueda en arrays especiales
@Service
public class BusquedaEspecialService {
    
    // Buscar en array rotado [4,5,6,7,0,1,2] buscar 0 → índice 4
    public int buscarEnArrayRotado(int[] array, int objetivo) {
        int izq = 0;
        int der = array.length - 1;
        
        while (izq <= der) {
            int medio = izq + (der - izq) / 2;
            
            if (array[medio] == objetivo) {
                return medio;
            }
            
            // Determinar qué mitad está ordenada
            if (array[izq] <= array[medio]) {
                // Mitad izquierda está ordenada
                if (objetivo >= array[izq] && objetivo < array[medio]) {
                    der = medio - 1;
                } else {
                    izq = medio + 1;
                }
            } else {
                // Mitad derecha está ordenada
                if (objetivo > array[medio] && objetivo <= array[der]) {
                    izq = medio + 1;
                } else {
                    der = medio - 1;
                }
            }
        }
        
        return -1;
    }
    
    // Encontrar mínimo en array rotado
    public int encontrarMinimo(int[] array) {
        int izq = 0;
        int der = array.length - 1;
        
        while (izq < der) {
            int medio = izq + (der - izq) / 2;
            
            if (array[medio] > array[der]) {
                izq = medio + 1; // Mínimo está en la derecha
            } else {
                der = medio; // Mínimo está en la izquierda o es el medio
            }
        }
        
        return array[izq];
    }
    
    // Búsqueda en matriz 2D ordenada (cada fila ordenada)
    public boolean buscarEnMatriz(int[][] matriz, int objetivo) {
        if (matriz == null || matriz.length == 0) {
            return false;
        }
        
        int filas = matriz.length;
        int columnas = matriz[0].length;
        
        // Tratar la matriz como array 1D
        int izq = 0;
        int der = filas * columnas - 1;
        
        while (izq <= der) {
            int medio = izq + (der - izq) / 2;
            int fila = medio / columnas;
            int columna = medio % columnas;
            int valorMedio = matriz[fila][columna];
            
            if (valorMedio == objetivo) {
                return true;
            }
            
            if (valorMedio < objetivo) {
                izq = medio + 1;
            } else {
                der = medio - 1;
            }
        }
        
        return false;
    }
}
```

### Ejemplo 5: Aplicación Práctica - Sistema de Calificaciones

```java
// Servicio: Análisis de calificaciones con búsqueda binaria
@Service
public class CalificacionAnalisisService {
    
    @Autowired
    private CalificacionRepository calificacionRepository;
    
    // Obtener estudiantes con calificación en rango
    public List<EstudianteCalificacion> obtenerPorRango(
            String cursoId, double notaMin, double notaMax) {
        
        // Obtener calificaciones ordenadas
        List<EstudianteCalificacion> calificaciones = 
            calificacionRepository.findByCursoIdOrderByNotaAsc(cursoId);
        
        // Buscar índice inicial
        int inicio = buscarPrimeraMayorIgual(calificaciones, notaMin);
        if (inicio == -1) {
            return Collections.emptyList();
        }
        
        // Buscar índice final
        int fin = buscarUltimaMenorIgual(calificaciones, notaMax);
        if (fin == -1 || fin < inicio) {
            return Collections.emptyList();
        }
        
        return calificaciones.subList(inicio, fin + 1);
    }
    
    private int buscarPrimeraMayorIgual(List<EstudianteCalificacion> lista, double umbral) {
        int izq = 0;
        int der = lista.size() - 1;
        int resultado = -1;
        
        while (izq <= der) {
            int medio = izq + (der - izq) / 2;
            
            if (lista.get(medio).getNota() >= umbral) {
                resultado = medio;
                der = medio - 1;
            } else {
                izq = medio + 1;
            }
        }
        
        return resultado;
    }
    
    private int buscarUltimaMenorIgual(List<EstudianteCalificacion> lista, double umbral) {
        int izq = 0;
        int der = lista.size() - 1;
        int resultado = -1;
        
        while (izq <= der) {
            int medio = izq + (der - izq) / 2;
            
            if (lista.get(medio).getNota() <= umbral) {
                resultado = medio;
                izq = medio + 1;
            } else {
                der = medio - 1;
            }
        }
        
        return resultado;
    }
    
    // Calcular percentil (ej: percentil 75 = top 25%)
    public double calcularPercentil(String cursoId, int percentil) {
        List<Double> notas = calificacionRepository
            .findByCursoIdOrderByNotaAsc(cursoId)
            .stream()
            .map(EstudianteCalificacion::getNota)
            .collect(Collectors.toList());
        
        if (notas.isEmpty()) {
            return 0.0;
        }
        
        int indice = (percentil * notas.size()) / 100;
        indice = Math.min(indice, notas.size() - 1);
        
        return notas.get(indice);
    }
}

@Data
class EstudianteCalificacion {
    private String estudianteId;
    private double nota;
}
```

---

## 🎓 Comparación con Búsqueda Lineal

| Característica | Búsqueda Lineal | Búsqueda Binaria |
|----------------|-----------------|------------------|
| Complejidad | O(n) | **O(log n)** ✅ |
| Requisito | Ninguno | **Array ordenado** |
| Mejor caso | O(1) | O(1) |
| Peor caso | O(n) | **O(log n)** ✅ |
| Uso | Arrays pequeños/desordenados | **Arrays grandes ordenados** |

---

## 📊 Eficiencia Comparativa

| Tamaño (n) | Búsqueda Lineal | Búsqueda Binaria | Mejora |
|------------|-----------------|------------------|--------|
| 100 | 100 | 7 | **14x más rápido** |
| 1,000 | 1,000 | 10 | **100x más rápido** |
| 10,000 | 10,000 | 14 | **714x más rápido** |
| 1,000,000 | 1,000,000 | 20 | **50,000x más rápido** ✅ |

---

## ⚠️ Consideraciones

### Ventajas:
- **Muy eficiente** para arrays grandes: O(log n)
- Simple de implementar
- Funciona con cualquier tipo comparable

### Desventajas:
- **Requiere datos ordenados**
- No es eficiente para datos dinámicos (muchas inserciones)
- Para datos pequeños (<10 elementos), búsqueda lineal puede ser más rápida

### Cuándo usar:
- ✅ Arrays grandes y **estáticos** u ordenados
- ✅ Búsquedas **frecuentes**
- ✅ Cuando el costo de ordenar se amortiza con muchas búsquedas

### Cuándo NO usar:
- ❌ Arrays **desordenados** (ordenar primero cuesta O(n log n))
- ❌ Inserciones/eliminaciones frecuentes
- ❌ Arrays muy pequeños