# Punteros y Referencias en Java

## 📚 Concepto

En Java, **no hay punteros explícitos** como en C/C++. En su lugar, Java usa **referencias a objetos**.

### Diferencias clave:
- **C/C++ Puntero**: Dirección de memoria que puedes manipular
- **Java Referencia**: Variable que "apunta" a un objeto, pero no puedes modificar la dirección

### Conceptos importantes:
- **Referencia**: Variable que contiene la ubicación de un objeto en memoria
- **null**: Referencia que no apunta a ningún objeto
- **Pass by value**: Java pasa referencias por valor (copia de la referencia)
- **Garbage Collection**: Limpieza automática de objetos sin referencias

---

## 🎯 Casos de Uso en SGE API

1. **Relaciones entre Entidades**: Estudiante → Curso (referencia)
2. **Navegación Bidireccional**: Tema anterior/siguiente
3. **Cache de Objetos**: Referencias a objetos frecuentemente usados
4. **Patrón Observer**: Referencia a observadores
5. **Lista Circular**: Último nodo apunta al primero
6. **Singleton**: Referencia única a instancia

---

## 💻 Implementación en Spring Boot

### Ejemplo 1: Referencias Básicas

```java
// Modelo: Estudiante con referencia a Curso
@Data
@Entity
@Table(name = "estudiantes")
public class Estudiante {
    @Id
    private String id;
    
    private String nombre;
    private String email;
    
    // Referencia a Curso (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso; // Esta es una REFERENCIA al objeto Curso
    
    // Método que modifica la referencia
    public void cambiarCurso(Curso nuevoCurso) {
        this.curso = nuevoCurso; // Cambia la referencia, no copia el objeto
    }
}

// Servicio: Demostración de referencias
@Service
public class ReferenciaService {
    
    // Ejemplo: Pass by value de referencias
    public void ejemploPassByValue() {
        Estudiante estudiante1 = new Estudiante();
        estudiante1.setNombre("Juan");
        
        // Pasar referencia a método
        modificarEstudiante(estudiante1);
        
        // estudiante1.nombre ahora es "Juan Modificado"
        // porque el objeto fue modificado, aunque la referencia se pasó por valor
        System.out.println(estudiante1.getNombre()); // "Juan Modificado"
    }
    
    private void modificarEstudiante(Estudiante est) {
        // 'est' es una COPIA de la referencia, pero apunta al mismo objeto
        est.setNombre("Juan Modificado"); // Modifica el objeto original
        
        // Esto NO afecta la referencia original
        est = new Estudiante(); // Cambia solo la copia local de la referencia
        est.setNombre("Pedro");
    }
    
    // Ejemplo: Referencias null
    public void ejemploNullPointer() {
        Estudiante estudiante = null; // Referencia nula
        
        // Esto lanza NullPointerException
        try {
            System.out.println(estudiante.getNombre());
        } catch (NullPointerException e) {
            System.out.println("¡Referencia nula!");
        }
        
        // Forma segura de verificar
        if (estudiante != null) {
            System.out.println(estudiante.getNombre());
        }
        
        // Operador Optional (Java 8+)
        Optional<Estudiante> optEstudiante = Optional.ofNullable(estudiante);
        optEstudiante.ifPresent(e -> System.out.println(e.getNombre()));
    }
    
    // Ejemplo: Referencias compartidas
    public void ejemploReferenciasCompartidas() {
        Estudiante est1 = new Estudiante();
        est1.setNombre("María");
        
        Estudiante est2 = est1; // est2 apunta al MISMO objeto que est1
        
        est2.setNombre("María Modificada");
        
        // Ambas referencias ven el cambio
        System.out.println(est1.getNombre()); // "María Modificada"
        System.out.println(est2.getNombre()); // "María Modificada"
    }
}
```

### Ejemplo 2: Lista Doblemente Enlazada (Referencias Bidireccionales)

```java
// Nodo con referencias anterior y siguiente
@Data
class NodoDoble<T> {
    private T dato;
    private NodoDoble<T> anterior; // Referencia al nodo anterior
    private NodoDoble<T> siguiente; // Referencia al nodo siguiente
    
    public NodoDoble(T dato) {
        this.dato = dato;
        this.anterior = null;
        this.siguiente = null;
    }
}

// Lista doblemente enlazada
@Service
public class ListaDobleService<T> {
    
    private NodoDoble<T> cabeza;
    private NodoDoble<T> cola;
    private int tamaño;
    
    public ListaDobleService() {
        this.cabeza = null;
        this.cola = null;
        this.tamaño = 0;
    }
    
    // Insertar al inicio
    public void insertarAlInicio(T dato) {
        NodoDoble<T> nuevoNodo = new NodoDoble<>(dato);
        
        if (cabeza == null) {
            // Lista vacía
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            // Establecer referencias bidireccionales
            nuevoNodo.setSiguiente(cabeza);
            cabeza.setAnterior(nuevoNodo);
            cabeza = nuevoNodo;
        }
        
        tamaño++;
    }
    
    // Insertar al final
    public void insertarAlFinal(T dato) {
        NodoDoble<T> nuevoNodo = new NodoDoble<>(dato);
        
        if (cola == null) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            cola.setSiguiente(nuevoNodo);
            nuevoNodo.setAnterior(cola);
            cola = nuevoNodo;
        }
        
        tamaño++;
    }
    
    // Navegar hacia adelante usando referencias
    public List<T> recorrerAdelante() {
        List<T> elementos = new ArrayList<>();
        NodoDoble<T> actual = cabeza;
        
        while (actual != null) {
            elementos.add(actual.getDato());
            actual = actual.getSiguiente(); // Seguir la referencia
        }
        
        return elementos;
    }
    
    // Navegar hacia atrás usando referencias
    public List<T> recorrerAtras() {
        List<T> elementos = new ArrayList<>();
        NodoDoble<T> actual = cola;
        
        while (actual != null) {
            elementos.add(actual.getDato());
            actual = actual.getAnterior(); // Seguir la referencia inversa
        }
        
        return elementos;
    }
}
```

### Ejemplo 3: Referencias Cíclicas y Garbage Collection

```java
// Ejemplo: Lista circular (último nodo apunta al primero)
@Service
public class ListaCircularService {
    
    @Data
    static class NodoCircular {
        private String dato;
        private NodoCircular siguiente;
        
        public NodoCircular(String dato) {
            this.dato = dato;
        }
    }
    
    private NodoCircular cabeza;
    
    // Crear lista circular
    public void crearListaCircular() {
        NodoCircular nodo1 = new NodoCircular("A");
        NodoCircular nodo2 = new NodoCircular("B");
        NodoCircular nodo3 = new NodoCircular("C");
        
        // Referencias en cadena
        nodo1.setSiguiente(nodo2);
        nodo2.setSiguiente(nodo3);
        nodo3.setSiguiente(nodo1); // ¡Referencia circular!
        
        cabeza = nodo1;
    }
    
    // Recorrer lista circular (con límite para evitar loop infinito)
    public List<String> recorrer(int maxElementos) {
        List<String> elementos = new ArrayList<>();
        
        if (cabeza == null) return elementos;
        
        NodoCircular actual = cabeza;
        int contador = 0;
        
        do {
            elementos.add(actual.getDato());
            actual = actual.getSiguiente();
            contador++;
        } while (actual != cabeza && contador < maxElementos);
        
        return elementos;
    }
    
    // Demostración de Garbage Collection
    public void ejemploGarbageCollection() {
        NodoCircular nodo = new NodoCircular("Temporal");
        
        // nodo tiene una referencia
        System.out.println("Nodo creado: " + nodo.getDato());
        
        // Eliminar la referencia
        nodo = null;
        
        // En algún momento, el Garbage Collector liberará la memoria
        // No necesitas hacerlo manualmente (como free() en C)
        System.gc(); // Sugerir recolección (no garantizado)
    }
}
```

### Ejemplo 4: WeakReference y SoftReference

```java
// Servicio: Referencias débiles para caché
@Service
public class CacheConReferenciasDebilesService {
    
    // WeakReference: Se libera cuando no hay referencias fuertes
    private Map<String, WeakReference<Estudiante>> cacheDebil = new ConcurrentHashMap<>();
    
    // SoftReference: Se libera solo si hay presión de memoria
    private Map<String, SoftReference<Estudiante>> cacheSuave = new ConcurrentHashMap<>();
    
    // Agregar a caché débil
    public void agregarCacheDebil(String id, Estudiante estudiante) {
        cacheDebil.put(id, new WeakReference<>(estudiante));
    }
    
    // Obtener de caché débil
    public Estudiante obtenerCacheDebil(String id) {
        WeakReference<Estudiante> ref = cacheDebil.get(id);
        
        if (ref != null) {
            Estudiante est = ref.get(); // Puede retornar null si fue recolectado
            
            if (est != null) {
                return est;
            } else {
                // Referencia fue recolectada
                cacheDebil.remove(id);
                return null;
            }
        }
        
        return null;
    }
    
    // Agregar a caché suave
    public void agregarCacheSuave(String id, Estudiante estudiante) {
        cacheSuave.put(id, new SoftReference<>(estudiante));
    }
    
    // Obtener de caché suave
    public Estudiante obtenerCacheSuave(String id) {
        SoftReference<Estudiante> ref = cacheSuave.get(id);
        
        if (ref != null) {
            Estudiante est = ref.get();
            
            if (est != null) {
                return est;
            } else {
                cacheSuave.remove(id);
                return null;
            }
        }
        
        return null;
    }
}
```

### Ejemplo 5: Patrón Observer con Referencias

```java
// Interface: Observer
interface CalificacionObserver {
    void notificarCambioCalificacion(String estudianteId, double nuevaNota);
}

// Servicio: Subject con lista de observers (referencias)
@Service
public class CalificacionSubject {
    
    // Lista de referencias a observers
    private List<CalificacionObserver> observers = new CopyOnWriteArrayList<>();
    
    // Registrar observer (agregar referencia)
    public void registrarObserver(CalificacionObserver observer) {
        observers.add(observer); // Guarda la referencia
    }
    
    // Desregistrar observer (eliminar referencia)
    public void desregistrarObserver(CalificacionObserver observer) {
        observers.remove(observer);
    }
    
    // Notificar a todos los observers
    public void actualizarCalificacion(String estudianteId, double nuevaNota) {
        // Iterar sobre las referencias
        for (CalificacionObserver observer : observers) {
            observer.notificarCambioCalificacion(estudianteId, nuevaNota);
        }
    }
}

// Implementación: Observer concreto
@Component
public class EmailNotificacionObserver implements CalificacionObserver {
    
    @Override
    public void notificarCambioCalificacion(String estudianteId, double nuevaNota) {
        System.out.println("Enviando email: Estudiante " + estudianteId + 
                         " tiene nueva nota: " + nuevaNota);
    }
}
```

### Ejemplo 6: Clonación Profunda vs Superficial

```java
// Servicio: Clonación de objetos
@Service
public class ClonacionService {
    
    @Data
    @AllArgsConstructor
    static class Direccion {
        private String calle;
        private String ciudad;
    }
    
    @Data
    @AllArgsConstructor
    static class Persona {
        private String nombre;
        private Direccion direccion; // Referencia a objeto
    }
    
    // Clonación superficial (shallow copy)
    public void ejemploClonacionSuperficial() {
        Direccion dir = new Direccion("Calle 123", "Madrid");
        Persona persona1 = new Persona("Juan", dir);
        
        // Copia superficial: copia la referencia, no el objeto
        Persona persona2 = new Persona(persona1.getNombre(), persona1.getDireccion());
        
        // Modificar dirección en persona2
        persona2.getDireccion().setCalle("Calle 456");
        
        // ¡También cambia en persona1! (comparten la misma referencia)
        System.out.println(persona1.getDireccion().getCalle()); // "Calle 456"
    }
    
    // Clonación profunda (deep copy)
    public void ejemploClonacionProfunda() {
        Direccion dir = new Direccion("Calle 123", "Madrid");
        Persona persona1 = new Persona("Juan", dir);
        
        // Copia profunda: crea nuevos objetos
        Direccion dirCopia = new Direccion(dir.getCalle(), dir.getCiudad());
        Persona persona2 = new Persona(persona1.getNombre(), dirCopia);
        
        // Modificar dirección en persona2
        persona2.getDireccion().setCalle("Calle 456");
        
        // persona1 NO cambia (tienen objetos separados)
        System.out.println(persona1.getDireccion().getCalle()); // "Calle 123"
    }
}
```

---

## 🎓 Java vs C/C++ Punteros

| Característica | C/C++ Puntero | Java Referencia |
|----------------|---------------|-----------------|
| Aritmética | ✅ Sí (`ptr++`) | ❌ No |
| Null | `NULL` o `nullptr` | `null` |
| Desreferencia | `*ptr` | Automática (`.`) |
| Liberación memoria | Manual (`free`, `delete`) | Automática (GC) |
| Puntero a puntero | ✅ Sí (`**ptr`) | ❌ No directo |
| Seguridad | ⚠️ Peligroso | ✅ Más seguro |

---

## 📊 Tipos de Referencias en Java

| Tipo | Comportamiento GC | Uso |
|------|-------------------|-----|
| **Strong** (normal) | Nunca recolectado mientras existe la referencia | Uso general |
| **WeakReference** | Recolectado en el siguiente GC | Caché que puede ser liberado |
| **SoftReference** | Recolectado solo con presión de memoria | Caché sensible a memoria |
| **PhantomReference** | Para limpieza post-recolección | Casos muy específicos |

---

## ⚠️ Consideraciones

### ✅ Ventajas de Referencias en Java:
- **Seguras**: No hay aritmética de punteros peligrosa
- **Automáticas**: Garbage Collection maneja la memoria
- **Simples**: Sintaxis más clara que punteros C/C++

### ❌ Problemas Comunes:
- **NullPointerException**: Acceso a referencia nula
- **Memory leaks**: Referencias no liberadas (aunque menos común que en C++)
- **Referencias cíclicas**: Pueden causar problemas si no se manejan bien

### Mejores Prácticas:
1. **Verificar null** antes de usar referencias
2. Usar **Optional<T>** para valores opcionales
3. **Evitar referencias cíclicas** innecesarias
4. Usar **@NonNull** annotations
5. Implementar **equals()** y **hashCode()** correctamente
6. Cuidado con **referencias en colecciones** (pueden prevenir GC)