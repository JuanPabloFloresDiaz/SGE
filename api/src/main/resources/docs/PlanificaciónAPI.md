# Planificación de la API - Sistema de Gestión Educativa (SGE)

## 📐 Arquitectura del Proyecto

### Capas de la Aplicación

```
api/
├── src/main/java/com/example/api/
│   ├── model/              # Entidades JPA (mapeo de tablas)
│   │   ├── Rol.java
│   │   ├── Usuario.java
│   │   ├── Estudiante.java
│   │   ├── Profesor.java
│   │   ├── Periodo.java
│   │   ├── Asignatura.java
│   │   ├── Curso.java
│   │   ├── BloqueHorario.java
│   │   ├── HorarioCurso.java
│   │   ├── Inscripcion.java
│   │   ├── Unidad.java
│   │   ├── Tema.java
│   │   ├── Clase.java
│   │   ├── Asistencia.java
│   │   ├── TipoEvaluacion.java
│   │   ├── Evaluacion.java
│   │   ├── Calificacion.java
│   │   └── Reporte.java
│   │
│   ├── repository/         # Interfaces JPA Repository
│   │   ├── RolRepository.java
│   │   ├── UsuarioRepository.java
│   │   ├── EstudianteRepository.java
│   │   └── ... (uno por cada entidad)
│   │
│   ├── service/            # Lógica de negocio
│   │   ├── RolService.java
│   │   ├── UsuarioService.java
│   │   ├── EstudianteService.java
│   │   ├── estructuras/    # Servicios con estructuras de datos
│   │   │   ├── CalificacionesListService.java
│   │   │   ├── InscripcionesQueueService.java
│   │   │   ├── GrafoPrerrequisitosService.java
│   │   │   └── RankingBSTService.java
│   │   └── ... (uno por cada entidad)
│   │
│   ├── controller/         # Endpoints REST
│   │   ├── RolController.java
│   │   ├── UsuarioController.java
│   │   ├── EstudianteController.java
│   │   └── ... (uno por cada entidad)
│   │
│   ├── dto/                # Data Transfer Objects
│   │   ├── request/
│   │   │   ├── CreateEstudianteRequest.java
│   │   │   └── UpdateEstudianteRequest.java
│   │   └── response/
│   │       └── EstudianteResponse.java
│   │
│   ├── config/             # Configuraciones
│   │   ├── OpenApiConfig.java      # Swagger/OpenAPI
│   │   └── CorsConfig.java         # CORS
│   │
│   └── exception/          # Manejo de excepciones
│       ├── GlobalExceptionHandler.java
│       └── ResourceNotFoundException.java
```

### Patrón de Arquitectura: **Layered Architecture (MVC)**

- **Model (Entidades)**: Representación de las tablas de BD
- **Repository**: Acceso a datos (abstracción de JPA)
- **Service**: Lógica de negocio y estructuras de datos
- **Controller**: Exposición de endpoints REST
- **Config**: Configuraciones transversales

---

## 📋 Planificación de Endpoints por Entidad

### Leyenda de Estructuras de Datos:
- 🟢 **Spring/JPA**: Operaciones estándar de JPA
- 🔵 **Lista Ligada**: Historial, navegación secuencial
- 🟣 **LIFO/FIFO**: Stack (undo) o Queue (procesamiento)
- 🟠 **Tabla Hash**: HashMap para búsquedas rápidas, caché
- 🔴 **Grafo**: Relaciones complejas, prerrequisitos
- 🟡 **Árbol BST**: Ranking, búsqueda ordenada
- 🟤 **Búsqueda Binaria**: Búsqueda en arrays ordenados
- 🔷 **Búsqueda Secuencial**: Búsqueda lineal en listas pequeñas
- ⚫ **Burbuja**: Ordenamiento simple

---

## 1. 🏷️ Roles (`/api/roles`) ✅ **COMPLETADO**

### Endpoints:

| Estado | Método | Endpoint | Descripción | Estructura | Razón |
|--------|--------|----------|-------------|------------|-------|
| ✅ | GET | `/api/roles` | Listar todos los roles activos | 🟢 Spring/JPA | Lista pequeña, findAll() suficiente |
| ✅ | GET | `/api/roles/{id}` | Obtener rol por ID | 🟢 Spring/JPA | findById() - O(1) con índice |
| ✅ | POST | `/api/roles` | Crear nuevo rol | 🟢 Spring/JPA | save() estándar |
| ✅ | PUT | `/api/roles/{id}` | Actualizar rol | 🟢 Spring/JPA | save() con ID existente |
| ✅ | DELETE | `/api/roles/{id}` | Eliminación suave | 🟢 Spring/JPA | Actualizar deleted_at |
| ✅ | DELETE | `/api/roles/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() de JPA |
| ✅ | GET | `/api/roles/search?nombre={nombre}` | Buscar por nombre | 🔷 Búsqueda Secuencial | Lista pequeña (<10), no necesita índice |
| ✅ | GET | `/api/roles/deleted` | Listar roles eliminados | 🟢 Spring/JPA | Query con deletedAt IS NOT NULL |
| ✅ | PATCH | `/api/roles/{id}/restore` | Restaurar rol eliminado | 🟢 Spring/JPA | Actualizar deleted_at a NULL |

### 📊 Resumen de Implementación:
- **Total de endpoints**: 9
- **Implementados**: 9 ✅
- **Pendientes**: 0
- **Archivos creados**:
  - ✅ `RolRepository.java` - Repositorio JPA con queries personalizadas
  - ✅ `RolService.java` - Lógica de negocio con validaciones
  - ✅ `RolController.java` - Controlador REST con Swagger
  - ✅ `CreateRolRequest.java` - DTO Request (Java Record)
  - ✅ `UpdateRolRequest.java` - DTO Request (Java Record)
  - ✅ `RolResponse.java` - DTO Response (Java Record)
  - ✅ `ResourceNotFoundException.java` - Excepción personalizada
  - ✅ `DuplicateResourceException.java` - Excepción personalizada
  - ✅ `GlobalExceptionHandler.java` - Manejador global de excepciones

### 🎯 Características Implementadas:
- ✅ CRUD completo
- ✅ Soft delete con capacidad de restauración
- ✅ Validaciones con Bean Validation (@NotBlank, @Size)
- ✅ Búsqueda case-insensitive por nombre
- ✅ Manejo de excepciones consistente
- ✅ Documentación Swagger completa
- ✅ DTOs con Java Records
- ✅ Respuestas HTTP apropiadas (200, 201, 204, 404, 409)
- ✅ Transacciones con @Transactional

---

## 2. 👤 Usuarios (`/api/usuarios`) ✅ **COMPLETADO**

### Endpoints:

| Estado | Método | Endpoint | Descripción | Estructura | Razón |
|--------|--------|----------|-------------|------------|-------|
| ✅ | GET | `/api/usuarios` | Listar todos los usuarios | 🟢 Spring/JPA | Paginación con PageRequest |
| ✅ | GET | `/api/usuarios/{id}` | Obtener usuario por ID | 🟢 Spring/JPA | findById() |
| ✅ | POST | `/api/usuarios` | Crear nuevo usuario | 🟢 Spring/JPA | save() + hash password |
| ✅ | PUT | `/api/usuarios/{id}` | Actualizar usuario | 🟢 Spring/JPA | save() |
| ✅ | DELETE | `/api/usuarios/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| ✅ | DELETE | `/api/usuarios/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| ✅ | GET | `/api/usuarios/search?username={user}` | Buscar por username | 🟢 Spring/JPA | Query method con índice UNIQUE |
| ✅ | GET | `/api/usuarios/email/{email}` | Buscar por email | 🟢 Spring/JPA | Query method con índice UNIQUE |
| ✅ | GET | `/api/usuarios/rol/{rolId}` | Usuarios por rol | 🟢 Spring/JPA | findByRolId() con índice FK |
| ✅ | GET | `/api/usuarios/activos` | Solo usuarios activos | 🟢 Spring/JPA | findByActivoTrue() |
| ✅ | GET | `/api/usuarios/search/nombre?nombre={nombre}` | Buscar por nombre | 🟢 Spring/JPA | Query con LIKE case-insensitive |
| ✅ | GET | `/api/usuarios/deleted` | Listar usuarios eliminados | 🟢 Spring/JPA | Query con deletedAt IS NOT NULL |
| ✅ | PATCH | `/api/usuarios/{id}/restore` | Restaurar usuario eliminado | 🟢 Spring/JPA | Actualizar deleted_at a NULL |

### 📊 Resumen de Implementación:
- **Total de endpoints**: 13
- **Implementados**: 13 ✅
- **Pendientes**: 0
- **Archivos creados**:
  - ✅ `CreateUsuarioRequest.java` - DTO para creación con validaciones robustas
  - ✅ `UpdateUsuarioRequest.java` - DTO para actualización parcial
  - ✅ `UsuarioResponse.java` - DTO de respuesta (sin password_hash)
  - ✅ `UsuarioRepository.java` - Repositorio JPA con queries personalizadas
  - ✅ `UsuarioService.java` - Servicio con lógica de negocio y encriptación
  - ✅ `UsuarioController.java` - Controlador REST completo
  - ✅ `SecurityConfig.java` - Configuración de Spring Security con BCrypt

### 🎯 Características Implementadas:
- ✅ CRUD completo
- ✅ Encriptación de contraseñas con BCrypt
- ✅ Soft delete con capacidad de restauración
- ✅ Validaciones avanzadas (email, username pattern, password strength)
- ✅ Búsquedas múltiples (username, email, nombre, rol)
- ✅ Paginación opcional en listado
- ✅ Filtro de usuarios activos
- ✅ Manejo de excepciones consistente
- ✅ Documentación Swagger completa
- ✅ DTOs con Java Records
- ✅ Respuestas HTTP apropiadas (200, 201, 204, 404, 409)
- ✅ Transacciones con @Transactional
- ✅ Validación de unicidad de username y email
- ✅ Validación de existencia de rol al crear/actualizar

**Nota sobre login y historial:**
- El endpoint `/api/usuarios/login` se implementará en una fase posterior con JWT
- El endpoint `/api/usuarios/{id}/historial` se implementará cuando se defina la entidad de auditoría

**Razón uso de estructuras:**
- **HashMap (login)**: Se implementará en versión futura para caché de sesiones
- **Lista Ligada (historial)**: Se implementará con entidad de Auditoría

---

## 3. 🎓 Estudiantes (`/api/estudiantes`) ✅ **COMPLETADO (Básico JPA)**

### Endpoints:

| Estado | Método | Endpoint | Descripción | Estructura | Razón |
|--------|--------|----------|-------------|------------|-------|
| ✅ | GET | `/api/estudiantes` | Listar todos | 🟢 Spring/JPA | Paginación estándar |
| ✅ | GET | `/api/estudiantes/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| ✅ | POST | `/api/estudiantes` | Crear estudiante | 🟢 Spring/JPA | save() |
| ✅ | PUT | `/api/estudiantes/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| ✅ | DELETE | `/api/estudiantes/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| ✅ | DELETE | `/api/estudiantes/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| ✅ | GET | `/api/estudiantes/codigo/{codigo}` | Buscar por código | 🟢 Spring/JPA | findByCodigo() con UNIQUE |
| ⏳ | GET | `/api/estudiantes/search?nombre={nombre}` | Buscar por nombre | 🔷 Búsqueda Secuencial | Sin índice, búsqueda like '%nombre%' |
| ⏳ | GET | `/api/estudiantes/ranking` | Ranking por promedio | 🟡 Árbol BST | Ordenamiento eficiente por calificación |
| ⏳ | GET | `/api/estudiantes/ranking/top/{n}` | Top N estudiantes | 🟡 Árbol BST | In-order traversal descendente |
| ⏳ | GET | `/api/estudiantes/{id}/cursos-disponibles` | Cursos que puede tomar | 🔴 Grafo | Verificar prerrequisitos completados |
| ⏳ | GET | `/api/estudiantes/{id}/ruta-aprendizaje` | Ruta óptima de cursos | 🔴 Grafo (Dijkstra) | Camino más corto considerando prerrequisitos |
| ✅ | GET | `/api/estudiantes/genero/{genero}` | Filtrar por género | 🟢 Spring/JPA | findByGenero() |
| ✅ | GET | `/api/estudiantes/activos` | Solo activos | 🟢 Spring/JPA | findByActivoTrue() |

### 📊 Resumen de Implementación:
- **Total de endpoints**: 14
- **Implementados (JPA)**: 9 ✅
- **Pendientes (Estructuras personalizadas)**: 5 ⏳
- **Archivos creados**:
  - ✅ `EstudianteRepository.java` - Repositorio JPA con queries personalizadas
  - ✅ `CreateEstudianteRequest.java` - DTO para creación con validaciones
  - ✅ `UpdateEstudianteRequest.java` - DTO para actualización parcial
  - ✅ `EstudianteResponse.java` - DTO para respuestas con datos anidados
  - ✅ `EstudianteService.java` - Lógica de negocio con Spring/JPA
  - ✅ `EstudianteController.java` - 9 endpoints REST documentados

### 🎯 Características Implementadas:
- ✅ CRUD completo con Spring/JPA
- ✅ Soft delete con capacidad de restauración
- ✅ Validaciones con Bean Validation
- ✅ Búsqueda por código único (índice UNIQUE)
- ✅ Filtros por género y estado activo
- ✅ Paginación en listado general
- ✅ Manejo de excepciones consistente
- ✅ Documentación Swagger completa
- ✅ DTOs con Java Records
- ✅ Respuestas HTTP apropiadas (200, 201, 204, 404, 409)
- ✅ Transacciones con @Transactional
- ✅ Validación de usuario existente al crear
- ✅ Validación de unicidad de código
- ✅ Valores por defecto (genero=O, ingreso=now, activo=true)

### ⏳ Pendientes (Implementación futura con estructuras personalizadas):
- **Búsqueda Secuencial** 🔷: GET `/api/estudiantes/search?nombre={nombre}` - O(n)
- **Árbol BST** 🟡: GET `/api/estudiantes/ranking` - Ranking por promedio
- **Árbol BST** 🟡: GET `/api/estudiantes/ranking/top/{n}` - Top N estudiantes
- **Grafo (BFS)** 🔴: GET `/api/estudiantes/{id}/cursos-disponibles` - O(V+E)
- **Grafo (Dijkstra)** 🔴: GET `/api/estudiantes/{id}/ruta-aprendizaje` - O((V+E)log V)

**Razón uso de estructuras personalizadas (pendientes):**
- **BST (ranking)**: Mantener estudiantes ordenados por promedio, búsqueda O(log n)
- **Grafo (cursos disponibles)**: Verificar prerrequisitos usando BFS/DFS
- **Dijkstra (ruta óptima)**: Encontrar secuencia de cursos más eficiente
- **Búsqueda Secuencial**: Demostrar algoritmo O(n) para búsqueda de texto

---

## 4. 👨‍🏫 Profesores (`/api/profesores`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/profesores` | Listar todos | 🟢 Spring/JPA | findAll() con paginación |
| GET | `/api/profesores/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/profesores` | Crear profesor | 🟢 Spring/JPA | save() |
| PUT | `/api/profesores/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/profesores/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/profesores/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/profesores/search?nombre={nombre}` | Buscar por nombre | 🔷 Búsqueda Secuencial | Pocos profesores, búsqueda lineal |
| GET | `/api/profesores/{id}/cursos` | Cursos asignados | 🟢 Spring/JPA | findByProfesorId() |
| GET | `/api/profesores/{id}/horario` | Horario semanal | 🟢 Spring/JPA | Join con horarios_curso |
| GET | `/api/profesores/departamento/{dept}` | Por departamento | 🟢 Spring/JPA | findByDepartamento() |

---

## 5. 📅 Periodos (`/api/periodos`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/periodos` | Listar todos | 🟢 Spring/JPA | Lista pequeña (<20 periodos) |
| GET | `/api/periodos/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/periodos` | Crear periodo | 🟢 Spring/JPA | save() |
| PUT | `/api/periodos/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/periodos/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/periodos/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/periodos/actual` | Periodo actual | 🟢 Spring/JPA | Filtrar por fechas BETWEEN |
| GET | `/api/periodos/ordenados` | Ordenados por fecha | 🟢 Spring/JPA | ORDER BY fecha_inicio DESC |

---

## 6. 📚 Asignaturas (`/api/asignaturas`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/asignaturas` | Listar todas | 🟢 Spring/JPA | findAll() con paginación |
| GET | `/api/asignaturas/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/asignaturas` | Crear asignatura | 🟢 Spring/JPA | save() |
| PUT | `/api/asignaturas/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/asignaturas/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/asignaturas/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/asignaturas/codigo/{codigo}` | Buscar por código | 🟢 Spring/JPA | findByCodigo() con UNIQUE |
| GET | `/api/asignaturas/search?nombre={nombre}` | Buscar por nombre | 🔷 Búsqueda Secuencial | Like '%nombre%' |
| GET | `/api/asignaturas/{id}/estructura` | Estructura completa | 🟡 Árbol N-ario | Asignatura → Unidades → Temas |
| POST | `/api/asignaturas/{id}/prerequisitos` | Agregar prerrequisito | 🔴 Grafo | Construir grafo de dependencias |
| GET | `/api/asignaturas/{id}/prerequisitos` | Ver prerrequisitos | 🔴 Grafo | BFS desde asignatura |
| GET | `/api/asignaturas/orden-recomendado` | Orden topológico | 🔴 Grafo | Topological sort de prerrequisitos |

**Razón uso de estructuras:**
- **Árbol N-ario**: Asignatura tiene múltiples unidades, cada unidad múltiples temas
- **Grafo**: Modelar prerrequisitos entre asignaturas (algunas requieren otras)

---

## 7. 🏫 Cursos (`/api/cursos`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/cursos` | Listar todos | 🟢 Spring/JPA | findAll() con paginación |
| GET | `/api/cursos/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/cursos` | Crear curso | 🟢 Spring/JPA | save() |
| PUT | `/api/cursos/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/cursos/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/cursos/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/cursos/periodo/{periodoId}` | Cursos por periodo | 🟢 Spring/JPA | findByPeriodoId() con índice |
| GET | `/api/cursos/profesor/{profesorId}` | Cursos de profesor | 🟢 Spring/JPA | findByProfesorId() |
| GET | `/api/cursos/asignatura/{asignaturaId}` | Cursos de asignatura | 🟢 Spring/JPA | findByAsignaturaId() |
| GET | `/api/cursos/{id}/disponibilidad` | Cupos disponibles | 🟢 Spring/JPA | cupo - count(inscripciones) |
| GET | `/api/cursos/disponibles` | Con cupos disponibles | 🟢 Spring/JPA | Subquery con COUNT |
| GET | `/api/cursos/search?nombre={nombre}` | Buscar por nombre | 🔷 Búsqueda Secuencial | Búsqueda flexible |
| GET | `/api/cursos/{id}/estadisticas` | Estadísticas del curso | 🟠 Tabla Hash | Frecuencia de calificaciones |

**Razón uso de estructuras:**
- **HashMap**: Contar frecuencia de rangos de calificaciones (0-60, 61-80, 81-100)

---

## 8. 🕐 Bloques Horario (`/api/bloques-horario`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/bloques-horario` | Listar todos | 🟢 Spring/JPA | Lista pequeña (~12 bloques) |
| GET | `/api/bloques-horario/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/bloques-horario` | Crear bloque | 🟢 Spring/JPA | save() |
| PUT | `/api/bloques-horario/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/bloques-horario/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/bloques-horario/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/bloques-horario/ordenados` | Ordenados por hora | ⚫ Burbuja | Lista pequeña, demostrar algoritmo |

**Razón uso de estructuras:**
- **Burbuja**: Con pocos elementos (~12), es didáctico y suficiente

---

## 9. 📋 Horarios Curso (`/api/horarios-curso`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/horarios-curso` | Listar todos | 🟢 Spring/JPA | findAll() |
| GET | `/api/horarios-curso/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/horarios-curso` | Crear horario | 🟢 Spring/JPA | save() |
| PUT | `/api/horarios-curso/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/horarios-curso/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/horarios-curso/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/horarios-curso/curso/{cursoId}` | Horarios de un curso | 🟢 Spring/JPA | findByCursoId() |
| GET | `/api/horarios-curso/dia/{dia}` | Horarios por día | 🟢 Spring/JPA | findByDiaSemana() |
| GET | `/api/horarios-curso/conflictos` | Detectar conflictos | 🔴 Grafo | Verificar solapamiento de horarios |

**Razón uso de estructuras:**
- **Grafo**: Nodos = horarios, aristas = conflicto de horario/aula

---

## 10. 📝 Inscripciones (`/api/inscripciones`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/inscripciones` | Listar todas | 🟢 Spring/JPA | findAll() con paginación |
| GET | `/api/inscripciones/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/inscripciones` | Crear inscripción | 🟢 Spring/JPA | save() + validar cupo |
| PUT | `/api/inscripciones/{id}` | Actualizar estado | 🟢 Spring/JPA | save() |
| DELETE | `/api/inscripciones/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/inscripciones/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/inscripciones/estudiante/{estudianteId}` | Por estudiante | 🟢 Spring/JPA | findByEstudianteId() |
| GET | `/api/inscripciones/curso/{cursoId}` | Por curso | 🟢 Spring/JPA | findByCursoId() |
| GET | `/api/inscripciones/estado/{estado}` | Por estado | 🟢 Spring/JPA | findByEstado() |
| POST | `/api/inscripciones/cola` | Agregar a cola de espera | 🟣 FIFO (Queue) | Procesar inscripciones por orden |
| POST | `/api/inscripciones/procesar` | Procesar cola | 🟣 FIFO (Queue) | Primero en llegar, primero inscrito |
| GET | `/api/inscripciones/{estudianteId}/historial` | Historial | 🔵 Lista Ligada | Inserciones al inicio (más recientes) |

**Razón uso de estructuras:**
- **Queue (FIFO)**: Cola de espera cuando curso lleno, procesar por orden de llegada
- **Lista Ligada**: Historial de inscripciones (agregar al inicio constantemente)

---

## 11. 📖 Unidades (`/api/unidades`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/unidades` | Listar todas | 🟢 Spring/JPA | findAll() |
| GET | `/api/unidades/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/unidades` | Crear unidad | 🟢 Spring/JPA | save() |
| PUT | `/api/unidades/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/unidades/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/unidades/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/unidades/curso/{cursoId}` | Unidades de curso | 🟢 Spring/JPA | findByCursoId() |
| GET | `/api/unidades/{id}/navegacion` | Navegación unidad | 🔵 Lista Doblemente Ligada | Anterior/Siguiente unidad |
| POST | `/api/unidades/{id}/reordenar` | Cambiar orden | 🔵 Lista Doblemente Ligada | Mover nodos en lista |

**Razón uso de estructuras:**
- **Lista Doblemente Ligada**: Navegar entre unidades (anterior/siguiente)

---

## 12. 📄 Temas (`/api/temas`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/temas` | Listar todos | 🟢 Spring/JPA | findAll() |
| GET | `/api/temas/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/temas` | Crear tema | 🟢 Spring/JPA | save() |
| PUT | `/api/temas/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/temas/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/temas/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/temas/unidad/{unidadId}` | Temas de unidad | 🟢 Spring/JPA | findByUnidadId() |
| GET | `/api/temas/{id}/navegacion` | Navegación tema | 🔵 Lista Doblemente Ligada | Anterior/Siguiente tema |
| GET | `/api/temas/search?titulo={titulo}` | Buscar por título | 🔷 Búsqueda Secuencial | Búsqueda flexible en texto |

**Razón uso de estructuras:**
- **Lista Doblemente Ligada**: Navegar entre temas de una unidad

---

## 13. 🎥 Clases (`/api/clases`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/clases` | Listar todas | 🟢 Spring/JPA | findAll() con paginación |
| GET | `/api/clases/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/clases` | Crear clase | 🟢 Spring/JPA | save() |
| PUT | `/api/clases/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/clases/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/clases/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/clases/curso/{cursoId}` | Clases de curso | 🟢 Spring/JPA | findByCursoId() |
| GET | `/api/clases/fecha/{fecha}` | Clases por fecha | 🟢 Spring/JPA | findByFecha() |
| GET | `/api/clases/ordenadas` | Clases ordenadas | 🟢 Spring/JPA | ORDER BY fecha, hora |

---

## 14. ✅ Asistencia (`/api/asistencia`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/asistencia` | Listar todas | 🟢 Spring/JPA | findAll() |
| GET | `/api/asistencia/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/asistencia` | Registrar asistencia | 🟢 Spring/JPA | save() |
| PUT | `/api/asistencia/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/asistencia/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/asistencia/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/asistencia/clase/{claseId}` | Asistencia de clase | 🟢 Spring/JPA | findByClaseId() |
| GET | `/api/asistencia/estudiante/{estudianteId}` | Por estudiante | 🟢 Spring/JPA | findByEstudianteId() |
| GET | `/api/asistencia/estudiante/{estudianteId}/estadisticas` | Estadísticas | 🟠 Tabla Hash | Contar presente/ausente/tardanza |
| POST | `/api/asistencia/reporte` | Generar reporte | 🟢 Spring/JPA | Aggregate queries |

**Razón uso de estructuras:**
- **HashMap**: Contar frecuencia de estados (presente/ausente/tardanza)

---

## 15. 📊 Tipos Evaluación (`/api/tipos-evaluacion`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/tipos-evaluacion` | Listar todos | 🟢 Spring/JPA | Lista pequeña (~5 tipos) |
| GET | `/api/tipos-evaluacion/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/tipos-evaluacion` | Crear tipo | 🟢 Spring/JPA | save() |
| PUT | `/api/tipos-evaluacion/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/tipos-evaluacion/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/tipos-evaluacion/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/tipos-evaluacion/search?nombre={nombre}` | Buscar por nombre | 🔷 Búsqueda Secuencial | Lista muy pequeña |

---

## 16. 📝 Evaluaciones (`/api/evaluaciones`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/evaluaciones` | Listar todas | 🟢 Spring/JPA | findAll() con paginación |
| GET | `/api/evaluaciones/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/evaluaciones` | Crear evaluación | 🟢 Spring/JPA | save() |
| PUT | `/api/evaluaciones/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/evaluaciones/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/evaluaciones/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/evaluaciones/curso/{cursoId}` | Por curso | 🟢 Spring/JPA | findByCursoId() |
| GET | `/api/evaluaciones/tipo/{tipoId}` | Por tipo | 🟢 Spring/JPA | findByTipoEvaluacionId() |
| GET | `/api/evaluaciones/proximas` | Próximas evaluaciones | 🟢 Spring/JPA | WHERE fecha >= CURDATE() |
| GET | `/api/evaluaciones/ordenadas` | Ordenadas por fecha | ⚫ Burbuja | Demostrar ordenamiento (si lista pequeña) |

**Razón uso de estructuras:**
- **Burbuja**: Ordenar evaluaciones si son pocas (<20), propósito educativo

---

## 17. 💯 Calificaciones (`/api/calificaciones`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/calificaciones` | Listar todas | 🟢 Spring/JPA | findAll() con paginación |
| GET | `/api/calificaciones/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/calificaciones` | Crear calificación | 🟢 Spring/JPA | save() |
| PUT | `/api/calificaciones/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/calificaciones/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/calificaciones/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/calificaciones/estudiante/{estudianteId}` | Por estudiante | 🟢 Spring/JPA | findByEstudianteId() |
| GET | `/api/calificaciones/evaluacion/{evaluacionId}` | Por evaluación | 🟢 Spring/JPA | findByEvaluacionId() |
| GET | `/api/calificaciones/estudiante/{estudianteId}/historial` | Historial | 🔵 Lista Ligada | Inserciones al inicio (más recientes) |
| GET | `/api/calificaciones/estudiante/{estudianteId}/promedio` | Calcular promedio | 🟢 Spring/JPA | AVG(nota) |
| GET | `/api/calificaciones/ranking` | Ranking general | 🟡 Árbol BST | Mantener ordenado por nota |
| GET | `/api/calificaciones/ranking/curso/{cursoId}` | Ranking de curso | 🟡 Árbol BST | BST por curso |
| GET | `/api/calificaciones/buscar-nota?min={min}&max={max}` | Buscar en rango | 🟤 Búsqueda Binaria | Si array ordenado de notas |
| GET | `/api/calificaciones/ordenar` | Ordenar por nota | ⚫ Burbuja | Demostrar algoritmo (lista pequeña) |
| GET | `/api/calificaciones/estadisticas` | Estadísticas | 🟠 Tabla Hash | Frecuencia de rangos de notas |

**Razón uso de estructuras:**
- **Lista Ligada**: Historial de calificaciones (agregar al inicio)
- **BST**: Ranking ordenado por nota, búsqueda eficiente
- **Búsqueda Binaria**: Encontrar estudiantes en rango de notas
- **HashMap**: Contar distribución de notas (0-60, 61-80, 81-100)
- **Burbuja**: Ordenamiento didáctico para listas pequeñas

---

## 18. 📈 Reportes (`/api/reportes`)

### Endpoints:

| Método | Endpoint | Descripción | Estructura | Razón |
|--------|----------|-------------|------------|-------|
| GET | `/api/reportes` | Listar todos | 🟢 Spring/JPA | findAll() |
| GET | `/api/reportes/{id}` | Obtener por ID | 🟢 Spring/JPA | findById() |
| POST | `/api/reportes` | Crear reporte | 🟢 Spring/JPA | save() |
| PUT | `/api/reportes/{id}` | Actualizar | 🟢 Spring/JPA | save() |
| DELETE | `/api/reportes/{id}` | Eliminación suave | 🟢 Spring/JPA | deleted_at |
| DELETE | `/api/reportes/{id}/permanent` | Eliminación definitiva | 🟢 Spring/JPA | delete() |
| GET | `/api/reportes/tipo/{tipo}` | Por tipo | 🟢 Spring/JPA | findByTipo() |
| GET | `/api/reportes/usuario/{usuarioId}` | Por usuario | 🟢 Spring/JPA | findByGeneradoPorId() |
| GET | `/api/reportes/recientes` | Reportes recientes | 🟢 Spring/JPA | ORDER BY created_at DESC LIMIT 10 |
| POST | `/api/reportes/cola-generacion` | Agregar a cola | 🟣 FIFO (Queue) | Procesar generación por orden |
| POST | `/api/reportes/procesar` | Procesar cola | 🟣 FIFO (Queue) | Generar reportes en orden |

**Razón uso de estructuras:**
- **Queue (FIFO)**: Procesar reportes pesados en orden de solicitud

---

## 🎯 Resumen de Uso de Estructuras

| Estructura | Casos de Uso | Cantidad de Endpoints |
|------------|--------------|----------------------|
| 🟢 **Spring/JPA** | CRUD estándar, queries simples | ~150 (80%) |
| 🔵 **Lista Ligada** | Historial, navegación secuencial | 6 |
| 🟣 **FIFO (Queue)** | Colas de espera, procesamiento por orden | 4 |
| 🟠 **Tabla Hash** | Caché, frecuencias, estadísticas | 5 |
| 🔴 **Grafo** | Prerrequisitos, conflictos, redes | 6 |
| 🟡 **Árbol BST** | Rankings ordenados | 4 |
| 🟤 **Búsqueda Binaria** | Búsqueda en rangos ordenados | 1 |
| 🔷 **Búsqueda Secuencial** | Búsquedas flexibles en listas pequeñas | 8 |
| ⚫ **Burbuja** | Ordenamiento didáctico | 3 |

---

## 📝 Notas de Implementación

### Prioridad de Desarrollo:

1. **Fase 1 - CRUD Básico** (🟢 Spring/JPA):
   - Implementar todos los endpoints básicos primero
   - Asegurar funcionamiento de crear, leer, actualizar, eliminar
   - Testing básico

2. **Fase 2 - Estructuras Educativas**:
   - Implementar estructuras más complejas
   - Servicios especializados en carpeta `service/estructuras/`
   - Documentar cada implementación

3. **Fase 3 - Optimización**:
   - Caché con Redis
   - Índices adicionales en BD
   - Paginación optimizada

### Consideraciones:

- **Soft Delete**: Todas las entidades tienen `deleted_at`, nunca eliminar físicamente por defecto
- **Paginación**: Usar `PageRequest` para listas grandes (>100 elementos)
- **Validación**: DTOs con `@Valid` y `@NotNull`, `@Size`, etc.
- **Swagger**: Todos los endpoints documentados con `@Operation`
- **Testing**: Objetivo 80% cobertura
- **Estructuras de datos**: Usar cuando sean pedagógicamente valiosas o técnicamente superiores

---

## 🚀 Total de Endpoints Planificados

- **18 Entidades** × ~10 endpoints promedio = **~180 endpoints**
- **CRUD básico**: ~150 endpoints (Spring/JPA)
- **Estructuras de datos**: ~30 endpoints especializados
- **Tiempo estimado**: 4-6 semanas de desarrollo