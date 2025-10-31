# Árboles Binarios

## 📚 Concepto

Un **Árbol Binario** es una estructura de datos jerárquica donde cada nodo tiene **máximo 2 hijos**: hijo izquierdo e hijo derecho.

### Características:
- **Raíz**: Nodo superior sin padre
- **Hijos**: Cada nodo puede tener 0, 1 o 2 hijos
- **Hojas**: Nodos sin hijos
- **Altura**: Longitud del camino más largo desde la raíz hasta una hoja
- **Nivel**: Distancia desde la raíz (raíz = nivel 0)

### Tipos de Árboles Binarios:

1. **Completo**: Todos los niveles llenos excepto posiblemente el último
2. **Perfecto**: Todos los niveles completamente llenos
3. **Balanceado**: Diferencia de altura entre subárboles ≤ 1
4. **Degenerado**: Cada nodo tiene solo un hijo (como lista enlazada)

---

## 🎯 Casos de Uso en SGE API

1. **Jerarquía de Unidades**: Unidad → Subunidades → Temas
2. **Árbol de Decisiones**: Sistema de calificaciones por niveles
3. **Estructura de Cursos**: Curso → Módulos → Lecciones
4. **Sistema de Categorías**: Categorías y subcategorías de recursos
5. **Expresiones Matemáticas**: Evaluación de fórmulas de calificación
6. **Navegación de Contenidos**: Menú jerárquico de la aplicación

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Árbol de Unidades Didácticas

```java
// Nodo del árbol binario
@Data
@Builder
public class NodoUnidad {
    private String id;
    private String nombre;
    private String contenido;
    private NodoUnidad izquierdo; // Primera subunidad
    private NodoUnidad derecho;   // Siguiente subunidad del mismo nivel
    
    // Constructor
    public NodoUnidad(String id, String nombre, String contenido) {
        this.id = id;
        this.nombre = nombre;
        this.contenido = contenido;
    }
}

// Servicio: Gestión del árbol de unidades
@Service
public class ArbolUnidadesService {
    
    private NodoUnidad raiz;
    
    // Construir árbol desde base de datos
    public NodoUnidad construirArbol(String cursoId) {
        List<Unidad> unidades = unidadRepository.findByCursoId(cursoId);
        
        if (unidades.isEmpty()) {
            return null;
        }
        
        // Ordenar por nivel y orden
        unidades.sort(Comparator.comparing(Unidad::getNivel)
                               .thenComparing(Unidad::getOrden));
        
        raiz = new NodoUnidad(
            unidades.get(0).getId(),
            unidades.get(0).getNombre(),
            unidades.get(0).getContenido()
        );
        
        // Construir recursivamente
        construirRecursivo(raiz, unidades, 1);
        
        return raiz;
    }
    
    private void construirRecursivo(NodoUnidad padre, List<Unidad> unidades, int indice) {
        if (indice >= unidades.size()) {
            return;
        }
        
        Unidad actual = unidades.get(indice);
        NodoUnidad nodo = new NodoUnidad(
            actual.getId(),
            actual.getNombre(),
            actual.getContenido()
        );
        
        // Decidir si va a la izquierda (subunidad) o derecha (siguiente)
        if (actual.getNivel() > obtenerNivel(padre)) {
            padre.setIzquierdo(nodo);
        } else {
            padre.setDerecho(nodo);
        }
        
        construirRecursivo(nodo, unidades, indice + 1);
    }
    
    // Recorrido Pre-orden (Raíz → Izquierda → Derecha)
    public List<String> recorridoPreOrden() {
        List<String> resultado = new ArrayList<>();
        preOrden(raiz, resultado);
        return resultado;
    }
    
    private void preOrden(NodoUnidad nodo, List<String> resultado) {
        if (nodo == null) {
            return;
        }
        
        resultado.add(nodo.getNombre()); // Procesar raíz
        preOrden(nodo.getIzquierdo(), resultado); // Izquierda
        preOrden(nodo.getDerecho(), resultado);   // Derecha
    }
    
    // Recorrido In-orden (Izquierda → Raíz → Derecha)
    public List<String> recorridoInOrden() {
        List<String> resultado = new ArrayList<>();
        inOrden(raiz, resultado);
        return resultado;
    }
    
    private void inOrden(NodoUnidad nodo, List<String> resultado) {
        if (nodo == null) {
            return;
        }
        
        inOrden(nodo.getIzquierdo(), resultado); // Izquierda
        resultado.add(nodo.getNombre());         // Procesar raíz
        inOrden(nodo.getDerecho(), resultado);   // Derecha
    }
    
    // Recorrido Post-orden (Izquierda → Derecha → Raíz)
    public List<String> recorridoPostOrden() {
        List<String> resultado = new ArrayList<>();
        postOrden(raiz, resultado);
        return resultado;
    }
    
    private void postOrden(NodoUnidad nodo, List<String> resultado) {
        if (nodo == null) {
            return;
        }
        
        postOrden(nodo.getIzquierdo(), resultado); // Izquierda
        postOrden(nodo.getDerecho(), resultado);   // Derecha
        resultado.add(nodo.getNombre());           // Procesar raíz
    }
    
    // Recorrido por niveles (BFS)
    public List<String> recorridoPorNiveles() {
        List<String> resultado = new ArrayList<>();
        
        if (raiz == null) {
            return resultado;
        }
        
        Queue<NodoUnidad> cola = new LinkedList<>();
        cola.offer(raiz);
        
        while (!cola.isEmpty()) {
            NodoUnidad actual = cola.poll();
            resultado.add(actual.getNombre());
            
            if (actual.getIzquierdo() != null) {
                cola.offer(actual.getIzquierdo());
            }
            
            if (actual.getDerecho() != null) {
                cola.offer(actual.getDerecho());
            }
        }
        
        return resultado;
    }
    
    // Buscar unidad por ID (DFS)
    public NodoUnidad buscar(String id) {
        return buscarRecursivo(raiz, id);
    }
    
    private NodoUnidad buscarRecursivo(NodoUnidad nodo, String id) {
        if (nodo == null) {
            return null;
        }
        
        if (nodo.getId().equals(id)) {
            return nodo;
        }
        
        // Buscar en subárbol izquierdo
        NodoUnidad encontrado = buscarRecursivo(nodo.getIzquierdo(), id);
        if (encontrado != null) {
            return encontrado;
        }
        
        // Buscar en subárbol derecho
        return buscarRecursivo(nodo.getDerecho(), id);
    }
    
    // Calcular altura del árbol
    public int calcularAltura() {
        return alturaRecursiva(raiz);
    }
    
    private int alturaRecursiva(NodoUnidad nodo) {
        if (nodo == null) {
            return 0;
        }
        
        int alturaIzq = alturaRecursiva(nodo.getIzquierdo());
        int alturaDer = alturaRecursiva(nodo.getDerecho());
        
        return 1 + Math.max(alturaIzq, alturaDer);
    }
    
    // Contar total de nodos
    public int contarNodos() {
        return contarRecursivo(raiz);
    }
    
    private int contarRecursivo(NodoUnidad nodo) {
        if (nodo == null) {
            return 0;
        }
        
        return 1 + contarRecursivo(nodo.getIzquierdo()) 
                 + contarRecursivo(nodo.getDerecho());
    }
    
    // Contar hojas (nodos sin hijos)
    public int contarHojas() {
        return contarHojasRecursivo(raiz);
    }
    
    private int contarHojasRecursivo(NodoUnidad nodo) {
        if (nodo == null) {
            return 0;
        }
        
        if (nodo.getIzquierdo() == null && nodo.getDerecho() == null) {
            return 1; // Es hoja
        }
        
        return contarHojasRecursivo(nodo.getIzquierdo()) 
             + contarHojasRecursivo(nodo.getDerecho());
    }
    
    private int obtenerNivel(NodoUnidad nodo) {
        // Implementación simplificada
        return 0;
    }
}

// Controlador
@RestController
@RequestMapping("/api/arbol/unidades")
@Tag(name = "Árbol de Unidades", description = "Navegación jerárquica de unidades")
public class ArbolUnidadesController {
    
    @Autowired
    private ArbolUnidadesService arbolService;
    
    @Operation(summary = "Construir árbol de unidades")
    @GetMapping("/construir/{cursoId}")
    public ResponseEntity<NodoUnidad> construirArbol(@PathVariable String cursoId) {
        NodoUnidad raiz = arbolService.construirArbol(cursoId);
        return ResponseEntity.ok(raiz);
    }
    
    @Operation(summary = "Recorrido pre-orden")
    @GetMapping("/recorrido/preorden")
    public ResponseEntity<List<String>> recorridoPreOrden() {
        return ResponseEntity.ok(arbolService.recorridoPreOrden());
    }
    
    @Operation(summary = "Recorrido in-orden")
    @GetMapping("/recorrido/inorden")
    public ResponseEntity<List<String>> recorridoInOrden() {
        return ResponseEntity.ok(arbolService.recorridoInOrden());
    }
    
    @Operation(summary = "Recorrido por niveles")
    @GetMapping("/recorrido/niveles")
    public ResponseEntity<List<String>> recorridoPorNiveles() {
        return ResponseEntity.ok(arbolService.recorridoPorNiveles());
    }
    
    @Operation(summary = "Buscar unidad")
    @GetMapping("/buscar/{id}")
    public ResponseEntity<NodoUnidad> buscar(@PathVariable String id) {
        NodoUnidad nodo = arbolService.buscar(id);
        
        if (nodo == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(nodo);
    }
    
    @Operation(summary = "Estadísticas del árbol")
    @GetMapping("/estadisticas")
    public ResponseEntity<ArbolEstadisticas> obtenerEstadisticas() {
        ArbolEstadisticas stats = new ArbolEstadisticas();
        stats.setAltura(arbolService.calcularAltura());
        stats.setTotalNodos(arbolService.contarNodos());
        stats.setTotalHojas(arbolService.contarHojas());
        
        return ResponseEntity.ok(stats);
    }
}

@Data
class ArbolEstadisticas {
    private int altura;
    private int totalNodos;
    private int totalHojas;
}
```

### Ejemplo 2: Evaluador de Expresiones (Árbol de Expresiones)

```java
// Nodo para expresiones matemáticas
@Data
class NodoExpresion {
    private String valor; // Operador o número
    private NodoExpresion izquierdo;
    private NodoExpresion derecho;
    
    public NodoExpresion(String valor) {
        this.valor = valor;
    }
    
    public boolean esOperador() {
        return "+".equals(valor) || "-".equals(valor) || 
               "*".equals(valor) || "/".equals(valor);
    }
}

// Servicio: Evaluador de fórmulas de calificación
@Service
public class EvaluadorExpresionesService {
    
    // Construir árbol desde expresión en notación postfija (RPN)
    // Ejemplo: "3 4 + 2 *" = (3 + 4) * 2 = 14
    public NodoExpresion construirDesdePostfija(String expresion) {
        Stack<NodoExpresion> pila = new Stack<>();
        String[] tokens = expresion.split(" ");
        
        for (String token : tokens) {
            NodoExpresion nodo = new NodoExpresion(token);
            
            if (nodo.esOperador()) {
                // Operador: sacar dos nodos de la pila
                nodo.setDerecho(pila.pop());
                nodo.setIzquierdo(pila.pop());
            }
            
            pila.push(nodo);
        }
        
        return pila.pop();
    }
    
    // Evaluar árbol de expresión
    public double evaluar(NodoExpresion nodo) {
        if (nodo == null) {
            return 0;
        }
        
        // Si es hoja (número), retornar valor
        if (!nodo.esOperador()) {
            return Double.parseDouble(nodo.getValor());
        }
        
        // Si es operador, evaluar recursivamente
        double izq = evaluar(nodo.getIzquierdo());
        double der = evaluar(nodo.getDerecho());
        
        return aplicarOperador(nodo.getValor(), izq, der);
    }
    
    private double aplicarOperador(String operador, double izq, double der) {
        switch (operador) {
            case "+": return izq + der;
            case "-": return izq - der;
            case "*": return izq * der;
            case "/": 
                if (der == 0) {
                    throw new ArithmeticException("División por cero");
                }
                return izq / der;
            default:
                throw new IllegalArgumentException("Operador desconocido: " + operador);
        }
    }
    
    // Convertir árbol a notación infija (con paréntesis)
    public String aInfija(NodoExpresion nodo) {
        if (nodo == null) {
            return "";
        }
        
        if (!nodo.esOperador()) {
            return nodo.getValor();
        }
        
        return "(" + aInfija(nodo.getIzquierdo()) + " " + 
               nodo.getValor() + " " + 
               aInfija(nodo.getDerecho()) + ")";
    }
    
    // Ejemplo: Calcular nota final con fórmula personalizada
    public double calcularNotaFinal(Map<String, Double> calificaciones, String formula) {
        // Formula ejemplo: "examen1 0.3 * examen2 0.3 * proyecto 0.4 * + +"
        // = (examen1 * 0.3) + (examen2 * 0.3) + (proyecto * 0.4)
        
        // Reemplazar variables con valores
        for (Map.Entry<String, Double> entry : calificaciones.entrySet()) {
            formula = formula.replace(entry.getKey(), entry.getValue().toString());
        }
        
        NodoExpresion arbol = construirDesdePostfija(formula);
        return evaluar(arbol);
    }
}
```

### Ejemplo 3: Árbol de Decisiones para Clasificación

```java
// Servicio: Clasificar estudiantes por rendimiento
@Service
public class ClasificadorEstudiantesService {
    
    // Nodo de decisión
    @Data
    static class NodoDecision {
        private String pregunta;       // "¿Promedio > 80?"
        private String clasificacion;  // "Excelente" (solo en hojas)
        private NodoDecision si;       // Rama verdadera
        private NodoDecision no;       // Rama falsa
        
        public boolean esHoja() {
            return clasificacion != null;
        }
    }
    
    private NodoDecision raiz;
    
    @PostConstruct
    public void construirArbolDecision() {
        // Construir árbol de decisión manualmente
        raiz = new NodoDecision();
        raiz.setPregunta("¿Promedio >= 85?");
        
        // Rama SI (promedio >= 85)
        NodoDecision nodoAlto = new NodoDecision();
        nodoAlto.setPregunta("¿Asistencia >= 90?");
        raiz.setSi(nodoAlto);
        
        NodoDecision excelente = new NodoDecision();
        excelente.setClasificacion("Excelente");
        nodoAlto.setSi(excelente);
        
        NodoDecision muyBueno = new NodoDecision();
        muyBueno.setClasificacion("Muy Bueno");
        nodoAlto.setNo(muyBueno);
        
        // Rama NO (promedio < 85)
        NodoDecision nodoMedio = new NodoDecision();
        nodoMedio.setPregunta("¿Promedio >= 70?");
        raiz.setNo(nodoMedio);
        
        NodoDecision bueno = new NodoDecision();
        bueno.setClasificacion("Bueno");
        nodoMedio.setSi(bueno);
        
        NodoDecision necesitaMejora = new NodoDecision();
        necesitaMejora.setClasificacion("Necesita Mejorar");
        nodoMedio.setNo(necesitaMejora);
    }
    
    // Clasificar estudiante usando el árbol
    public String clasificar(double promedio, double asistencia) {
        return clasificarRecursivo(raiz, promedio, asistencia);
    }
    
    private String clasificarRecursivo(NodoDecision nodo, double promedio, double asistencia) {
        if (nodo.esHoja()) {
            return nodo.getClasificacion();
        }
        
        boolean condicion;
        
        if (nodo.getPregunta().contains("Promedio")) {
            double umbral = extraerUmbral(nodo.getPregunta());
            condicion = promedio >= umbral;
        } else if (nodo.getPregunta().contains("Asistencia")) {
            double umbral = extraerUmbral(nodo.getPregunta());
            condicion = asistencia >= umbral;
        } else {
            condicion = false;
        }
        
        return condicion 
            ? clasificarRecursivo(nodo.getSi(), promedio, asistencia)
            : clasificarRecursivo(nodo.getNo(), promedio, asistencia);
    }
    
    private double extraerUmbral(String pregunta) {
        // Extraer número de la pregunta
        String[] partes = pregunta.split(">=?");
        if (partes.length < 2) return 0;
        
        String numero = partes[1].trim().replaceAll("[^0-9.]", "");
        return Double.parseDouble(numero);
    }
}
```

---

## 🎓 Recorridos del Árbol

| Recorrido | Orden | Uso típico |
|-----------|-------|-----------|
| **Pre-orden** | Raíz → Izq → Der | Copiar árbol, prefijo de expresiones |
| **In-orden** | Izq → Raíz → Der | Orden ascendente (BST) |
| **Post-orden** | Izq → Der → Raíz | Liberar memoria, postfijo |
| **Por niveles** (BFS) | Nivel 0 → 1 → 2... | Imprimir por niveles |

---

## 📊 Complejidad

| Operación | Promedio | Peor caso |
|-----------|----------|-----------|
| Búsqueda | O(log n) | O(n) |
| Inserción | O(log n) | O(n) |
| Eliminación | O(log n) | O(n) |
| Recorrido | O(n) | O(n) |
| Altura | O(n) | O(n) |

**Nota**: El peor caso O(n) ocurre en árboles degenerados (como lista enlazada).

---

## ⚠️ Consideraciones

- Los árboles binarios **no balanceados** pueden degradarse a O(n)
- Para mantener O(log n) usar **árboles balanceados** (AVL, Red-Black)
- Los recorridos recursivos pueden causar **stack overflow** en árboles muy profundos
- Considerar implementación **iterativa con pila** para árboles grandes