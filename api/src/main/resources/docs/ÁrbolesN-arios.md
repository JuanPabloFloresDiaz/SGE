# Árboles N-arios

## 📚 Concepto

Un **Árbol N-ario** es un árbol donde cada nodo puede tener **hasta N hijos**, no solo 2 como en árboles binarios.

### Características:
- **N hijos**: Cada nodo puede tener 0 a N hijos
- **Flexible**: No hay restricción en el número de hijos
- **Jerárquico**: Ideal para estructuras con múltiples niveles
- **Casos comunes**: N=3 (ternario), N=4 (cuaternario), N=∞ (ilimitado)

### Ejemplos comunes:
- **Sistemas de archivos**: Carpetas con múltiples subcarpetas
- **Organigrama**: Jefe con múltiples subordinados
- **Árbol DOM**: HTML con múltiples elementos hijos
- **Índices de bases de datos**: B-Tree, B+ Tree

---

## 🎯 Casos de Uso en SGE API

1. **Estructura de Cursos**: Curso → Unidades → Temas → Subtemas
2. **Organigrama Escolar**: Director → Coordinadores → Profesores
3. **Categorías de Recursos**: Categoría → Subcategorías → Recursos
4. **Sistema de Menús**: Menú principal → Submenús → Opciones
5. **Jerarquía de Permisos**: Roles → Subr roles → Permisos específicos
6. **Comentarios Anidados**: Comentario → Respuestas → Sub-respuestas

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Árbol N-ario Básico

```java
// Nodo N-ario genérico
@Data
class NodoNArio<T> {
    private T valor;
    private List<NodoNArio<T>> hijos;
    
    public NodoNArio(T valor) {
        this.valor = valor;
        this.hijos = new ArrayList<>();
    }
    
    public void agregarHijo(NodoNArio<T> hijo) {
        this.hijos.add(hijo);
    }
    
    public boolean esHoja() {
        return hijos.isEmpty();
    }
    
    public int numeroHijos() {
        return hijos.size();
    }
}

// Servicio: Operaciones básicas
@Service
public class ArbolNArioBa sicoService<T> {
    
    private NodoNArio<T> raiz;
    
    public ArbolNArioBasicoService(T valorRaiz) {
        this.raiz = new NodoNArio<>(valorRaiz);
    }
    
    // Recorrido pre-orden (DFS)
    public List<T> recorridoPreOrden() {
        List<T> resultado = new ArrayList<>();
        preOrden(raiz, resultado);
        return resultado;
    }
    
    private void preOrden(NodoNArio<T> nodo, List<T> resultado) {
        if (nodo == null) return;
        
        resultado.add(nodo.getValor()); // Procesar raíz
        
        for (NodoNArio<T> hijo : nodo.getHijos()) {
            preOrden(hijo, resultado); // Procesar hijos
        }
    }
    
    // Recorrido post-orden
    public List<T> recorridoPostOrden() {
        List<T> resultado = new ArrayList<>();
        postOrden(raiz, resultado);
        return resultado;
    }
    
    private void postOrden(NodoNArio<T> nodo, List<T> resultado) {
        if (nodo == null) return;
        
        for (NodoNArio<T> hijo : nodo.getHijos()) {
            postOrden(hijo, resultado); // Procesar hijos primero
        }
        
        resultado.add(nodo.getValor()); // Luego raíz
    }
    
    // Recorrido por niveles (BFS)
    public List<T> recorridoPorNiveles() {
        List<T> resultado = new ArrayList<>();
        
        if (raiz == null) return resultado;
        
        Queue<NodoNArio<T>> cola = new LinkedList<>();
        cola.offer(raiz);
        
        while (!cola.isEmpty()) {
            NodoNArio<T> actual = cola.poll();
            resultado.add(actual.getValor());
            
            // Agregar todos los hijos a la cola
            cola.addAll(actual.getHijos());
        }
        
        return resultado;
    }
    
    // Calcular altura del árbol
    public int calcularAltura() {
        return calcularAlturaRecursiva(raiz);
    }
    
    private int calcularAlturaRecursiva(NodoNArio<T> nodo) {
        if (nodo == null || nodo.esHoja()) {
            return 0;
        }
        
        int alturaMaxima = 0;
        
        for (NodoNArio<T> hijo : nodo.getHijos()) {
            int alturaHijo = calcularAlturaRecursiva(hijo);
            alturaMaxima = Math.max(alturaMaxima, alturaHijo);
        }
        
        return 1 + alturaMaxima;
    }
    
    // Contar total de nodos
    public int contarNodos() {
        return contarNodosRecursivo(raiz);
    }
    
    private int contarNodosRecursivo(NodoNArio<T> nodo) {
        if (nodo == null) return 0;
        
        int total = 1; // Contar el nodo actual
        
        for (NodoNArio<T> hijo : nodo.getHijos()) {
            total += contarNodosRecursivo(hijo);
        }
        
        return total;
    }
    
    // Contar hojas
    public int contarHojas() {
        return contarHojasRecursivo(raiz);
    }
    
    private int contarHojasRecursivo(NodoNArio<T> nodo) {
        if (nodo == null) return 0;
        
        if (nodo.esHoja()) {
            return 1;
        }
        
        int totalHojas = 0;
        
        for (NodoNArio<T> hijo : nodo.getHijos()) {
            totalHojas += contarHojasRecursivo(hijo);
        }
        
        return totalHojas;
    }
    
    // Buscar nodo por valor (DFS)
    public NodoNArio<T> buscar(T valor) {
        return buscarRecursivo(raiz, valor);
    }
    
    private NodoNArio<T> buscarRecursivo(NodoNArio<T> nodo, T valor) {
        if (nodo == null) return null;
        
        if (nodo.getValor().equals(valor)) {
            return nodo;
        }
        
        for (NodoNArio<T> hijo : nodo.getHijos()) {
            NodoNArio<T> encontrado = buscarRecursivo(hijo, valor);
            if (encontrado != null) {
                return encontrado;
            }
        }
        
        return null;
    }
}
```

### Ejemplo 2: Estructura de Cursos (Curso → Unidades → Temas)

```java
// Modelo: Nodo de contenido educativo
@Data
class NodoContenido {
    private String id;
    private String titulo;
    private String tipo; // "curso", "unidad", "tema", "subtema"
    private String contenido;
    private int orden;
    private List<NodoContenido> hijos;
    
    public NodoContenido(String id, String titulo, String tipo) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.hijos = new ArrayList<>();
    }
    
    public void agregarHijo(NodoContenido hijo) {
        this.hijos.add(hijo);
        // Ordenar hijos por orden
        this.hijos.sort(Comparator.comparingInt(NodoContenido::getOrden));
    }
}

// Servicio: Gestión de estructura de cursos
@Service
public class EstructuraCursoService {
    
    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private UnidadRepository unidadRepository;
    
    @Autowired
    private TemaRepository temaRepository;
    
    // Construir árbol completo del curso
    public NodoContenido construirArbolCurso(String cursoId) {
        Curso curso = cursoRepository.findById(cursoId)
            .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado"));
        
        NodoContenido raiz = new NodoContenido(
            curso.getId(),
            curso.getNombre(),
            "curso"
        );
        
        // Cargar unidades
        List<Unidad> unidades = unidadRepository.findByCursoIdOrderByOrden(cursoId);
        
        for (Unidad unidad : unidades) {
            NodoContenido nodoUnidad = new NodoContenido(
                unidad.getId(),
                unidad.getTitulo(),
                "unidad"
            );
            nodoUnidad.setOrden(unidad.getOrden());
            nodoUnidad.setContenido(unidad.getDescripcion());
            
            // Cargar temas de la unidad
            List<Tema> temas = temaRepository.findByUnidadIdOrderByOrden(unidad.getId());
            
            for (Tema tema : temas) {
                NodoContenido nodoTema = new NodoContenido(
                    tema.getId(),
                    tema.getTitulo(),
                    "tema"
                );
                nodoTema.setOrden(tema.getOrden());
                nodoTema.setContenido(tema.getContenido());
                
                nodoUnidad.agregarHijo(nodoTema);
            }
            
            raiz.agregarHijo(nodoUnidad);
        }
        
        return raiz;
    }
    
    // Obtener tabla de contenidos (índice)
    public List<ItemIndice> generarIndice(String cursoId) {
        NodoContenido arbol = construirArbolCurso(cursoId);
        List<ItemIndice> indice = new ArrayList<>();
        
        generarIndiceRecursivo(arbol, indice, 0, "");
        
        return indice;
    }
    
    private void generarIndiceRecursivo(NodoContenido nodo, 
                                       List<ItemIndice> indice, 
                                       int nivel,
                                       String numeracion) {
        if (nivel > 0) { // No incluir el curso en el índice
            indice.add(new ItemIndice(
                numeracion,
                nodo.getTitulo(),
                nodo.getTipo(),
                nivel
            ));
        }
        
        int contador = 1;
        for (NodoContenido hijo : nodo.getHijos()) {
            String nuevaNumeracion = nivel == 0 
                ? String.valueOf(contador)
                : numeracion + "." + contador;
            
            generarIndiceRecursivo(hijo, indice, nivel + 1, nuevaNumeracion);
            contador++;
        }
    }
    
    // Contar elementos por tipo
    public Map<String, Integer> contarPorTipo(String cursoId) {
        NodoContenido arbol = construirArbolCurso(cursoId);
        Map<String, Integer> contadores = new HashMap<>();
        
        contarPorTipoRecursivo(arbol, contadores);
        
        return contadores;
    }
    
    private void contarPorTipoRecursivo(NodoContenido nodo, Map<String, Integer> contadores) {
        contadores.put(nodo.getTipo(), 
            contadores.getOrDefault(nodo.getTipo(), 0) + 1);
        
        for (NodoContenido hijo : nodo.getHijos()) {
            contarPorTipoRecursivo(hijo, contadores);
        }
    }
    
    // Obtener ruta completa hacia un nodo
    public List<String> obtenerRuta(String cursoId, String nodoId) {
        NodoContenido arbol = construirArbolCurso(cursoId);
        List<String> ruta = new ArrayList<>();
        
        if (encontrarRuta(arbol, nodoId, ruta)) {
            return ruta;
        }
        
        return Collections.emptyList();
    }
    
    private boolean encontrarRuta(NodoContenido nodo, String nodoId, List<String> ruta) {
        ruta.add(nodo.getTitulo());
        
        if (nodo.getId().equals(nodoId)) {
            return true;
        }
        
        for (NodoContenido hijo : nodo.getHijos()) {
            if (encontrarRuta(hijo, nodoId, ruta)) {
                return true;
            }
        }
        
        ruta.remove(ruta.size() - 1);
        return false;
    }
}

@Data
@AllArgsConstructor
class ItemIndice {
    private String numeracion; // "1", "1.1", "1.1.1"
    private String titulo;
    private String tipo;
    private int nivel;
}

// Controlador
@RestController
@RequestMapping("/api/arbol-nario/cursos")
@Tag(name = "Estructura de Cursos", description = "Árbol N-ario de contenido educativo")
public class EstructuraCursoController {
    
    @Autowired
    private EstructuraCursoService estructuraService;
    
    @Operation(summary = "Obtener estructura completa del curso")
    @GetMapping("/{cursoId}/estructura")
    public ResponseEntity<NodoContenido> obtenerEstructura(@PathVariable String cursoId) {
        return ResponseEntity.ok(estructuraService.construirArbolCurso(cursoId));
    }
    
    @Operation(summary = "Generar índice del curso")
    @GetMapping("/{cursoId}/indice")
    public ResponseEntity<List<ItemIndice>> generarIndice(@PathVariable String cursoId) {
        return ResponseEntity.ok(estructuraService.generarIndice(cursoId));
    }
    
    @Operation(summary = "Contar elementos por tipo")
    @GetMapping("/{cursoId}/estadisticas")
    public ResponseEntity<Map<String, Integer>> contarPorTipo(@PathVariable String cursoId) {
        return ResponseEntity.ok(estructuraService.contarPorTipo(cursoId));
    }
    
    @Operation(summary = "Obtener ruta breadcrumb")
    @GetMapping("/{cursoId}/ruta/{nodoId}")
    public ResponseEntity<List<String>> obtenerRuta(
            @PathVariable String cursoId,
            @PathVariable String nodoId) {
        
        return ResponseEntity.ok(estructuraService.obtenerRuta(cursoId, nodoId));
    }
}
```

### Ejemplo 3: Sistema de Comentarios Anidados

```java
// Modelo: Comentario con respuestas
@Data
@Builder
class Comentario {
    private String id;
    private String autor;
    private String contenido;
    private LocalDateTime fecha;
    private List<Comentario> respuestas;
    
    public Comentario(String id, String autor, String contenido, LocalDateTime fecha) {
        this.id = id;
        this.autor = autor;
        this.contenido = contenido;
        this.fecha = fecha;
        this.respuestas = new ArrayList<>();
    }
    
    public void agregarRespuesta(Comentario respuesta) {
        this.respuestas.add(respuesta);
    }
}

// Servicio: Gestión de comentarios anidados
@Service
public class ComentariosService {
    
    private Map<String, Comentario> comentariosRaiz = new ConcurrentHashMap<>();
    
    // Agregar comentario raíz
    public Comentario agregarComentario(String autor, String contenido) {
        Comentario comentario = new Comentario(
            UUID.randomUUID().toString(),
            autor,
            contenido,
            LocalDateTime.now()
        );
        
        comentariosRaiz.put(comentario.getId(), comentario);
        return comentario;
    }
    
    // Agregar respuesta a un comentario
    public Comentario agregarRespuesta(String comentarioPadreId, 
                                      String autor, 
                                      String contenido) {
        Comentario padre = buscarComentario(comentarioPadreId);
        
        if (padre == null) {
            throw new EntityNotFoundException("Comentario padre no encontrado");
        }
        
        Comentario respuesta = new Comentario(
            UUID.randomUUID().toString(),
            autor,
            contenido,
            LocalDateTime.now()
        );
        
        padre.agregarRespuesta(respuesta);
        return respuesta;
    }
    
    // Buscar comentario en todo el árbol
    private Comentario buscarComentario(String id) {
        for (Comentario raiz : comentariosRaiz.values()) {
            Comentario encontrado = buscarEnArbol(raiz, id);
            if (encontrado != null) {
                return encontrado;
            }
        }
        return null;
    }
    
    private Comentario buscarEnArbol(Comentario nodo, String id) {
        if (nodo.getId().equals(id)) {
            return nodo;
        }
        
        for (Comentario respuesta : nodo.getRespuestas()) {
            Comentario encontrado = buscarEnArbol(respuesta, id);
            if (encontrado != null) {
                return encontrado;
            }
        }
        
        return null;
    }
    
    // Obtener todos los comentarios (aplanado)
    public List<ComentarioDTO> obtenerTodosAplanados() {
        List<ComentarioDTO> resultado = new ArrayList<>();
        
        for (Comentario raiz : comentariosRaiz.values()) {
            aplanarComentarios(raiz, resultado, 0);
        }
        
        return resultado;
    }
    
    private void aplanarComentarios(Comentario nodo, 
                                   List<ComentarioDTO> resultado, 
                                   int nivel) {
        resultado.add(ComentarioDTO.builder()
            .id(nodo.getId())
            .autor(nodo.getAutor())
            .contenido(nodo.getContenido())
            .fecha(nodo.getFecha())
            .nivel(nivel)
            .totalRespuestas(nodo.getRespuestas().size())
            .build());
        
        for (Comentario respuesta : nodo.getRespuestas()) {
            aplanarComentarios(respuesta, resultado, nivel + 1);
        }
    }
    
    // Contar total de comentarios (incluye respuestas)
    public int contarTotal() {
        int total = 0;
        for (Comentario raiz : comentariosRaiz.values()) {
            total += contarNodos(raiz);
        }
        return total;
    }
    
    private int contarNodos(Comentario nodo) {
        int total = 1;
        for (Comentario respuesta : nodo.getRespuestas()) {
            total += contarNodos(respuesta);
        }
        return total;
    }
}

@Data
@Builder
class ComentarioDTO {
    private String id;
    private String autor;
    private String contenido;
    private LocalDateTime fecha;
    private int nivel;
    private int totalRespuestas;
}
```

---

## 🎓 Comparación con Árboles Binarios

| Característica | Árbol Binario | Árbol N-ario |
|----------------|---------------|--------------|
| Hijos por nodo | Máximo 2 | Hasta N (o ilimitado) |
| Flexibilidad | Limitada | **Alta** ✅ |
| Uso típico | BST, heap | Sistemas de archivos, menús |
| Complejidad búsqueda | O(log n) - O(n) | O(n) |
| Almacenamiento | Lista/Array de hijos | Lista dinámica |

---

## 📊 Complejidad

| Operación | Complejidad |
|-----------|-------------|
| Búsqueda (DFS/BFS) | O(n) |
| Inserción | O(1) si tienes el padre |
| Recorrido completo | O(n) |
| Altura | O(n) |
| Contar nodos | O(n) |

---

## ⚠️ Consideraciones

### ✅ Ventajas:
- **Flexibilidad**: Número variable de hijos
- **Natural**: Modela estructuras jerárquicas reales
- **Escalable**: Fácil agregar nuevos niveles

### ❌ Desventajas:
- **Búsqueda lenta**: O(n) sin optimizaciones
- **No balanceado**: Puede crecer de forma desigual
- Más complejo que estructuras lineales

### Cuándo usar:
- ✅ Jerarquías con múltiples niveles
- ✅ Sistemas de archivos
- ✅ Organigramas
- ✅ Menús de navegación
- ✅ Categorización