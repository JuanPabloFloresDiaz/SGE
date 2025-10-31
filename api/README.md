# SGE API - Sistema de Gestión Educativa

API REST desarrollada con Spring Boot para la gestión de un sistema educativo.

## 📋 Prerequisitos

- Java 17 o superior
- Maven 3.6+
- Docker y Docker Compose

## 🚀 Inicio Rápido

### 0. Ejecución de Script start-dev.sh
# Primera vez:
```bash
chmod +x start-dev.sh    # Dar permisos de ejecución
./start-dev.sh           # Levantar MySQL
mvn spring-boot:run      # Ejecutar la API
```

### 1. Levantar la base de datos MySQL con Docker

```bash
docker-compose up -d
```

Esto levantará MySQL 8.0 en el puerto **3311** con:
- Base de datos: `SGE`
- Usuario root: `root` / `root`
- Usuario alternativo: `sge_user` / `sge_password`

### 2. Verificar que MySQL esté corriendo

```bash
docker-compose ps
```

### 3. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

O desde tu IDE, ejecutar la clase `ApiApplication.java`

### 4. Verificar que funciona

La aplicación estará disponible en: `http://localhost:8080`

### 5. Acceder a Swagger UI

Una vez que la aplicación esté corriendo, accede a la documentación interactiva:

**Swagger UI:** http://localhost:8080/swagger-ui.html

**OpenAPI JSON:** http://localhost:8080/api-docs

Desde Swagger UI podrás:
- 📖 Ver todos los endpoints disponibles
- 🧪 Probar los endpoints directamente desde el navegador
- 📝 Ver los modelos de datos (DTOs)
- 🔍 Ver descripciones detalladas de cada operación

## 📚 Documentación de la API (Swagger)

La API utiliza **SpringDoc OpenAPI 3** (Swagger) para documentación interactiva.

### Endpoints de prueba disponibles:

- `GET /api/health` - Verificar el estado de la API
- `GET /api/info` - Información general de la API

### Cómo usar Swagger UI:

1. Abre http://localhost:8080/swagger-ui.html en tu navegador
2. Verás todos los endpoints organizados por tags
3. Haz clic en cualquier endpoint para ver detalles
4. Usa el botón "Try it out" para probar el endpoint
5. Los resultados se mostrarán directamente en el navegador

📖 **Para más detalles sobre cómo documentar tus endpoints, consulta [SWAGGER_GUIDE.md](./SWAGGER_GUIDE.md)**

## 🗄️ Base de Datos

### Estructura

La base de datos incluye las siguientes tablas:
- `roles` - Roles de usuarios
- `usuarios` - Usuarios del sistema
- `estudiantes` - Información de estudiantes
- `profesores` - Información de profesores
- `periodos` - Periodos académicos
- `asignaturas` - Materias
- `cursos` - Cursos/grupos
- `bloques_horario` - Bloques de horario
- `horarios_curso` - Horarios por curso
- `inscripciones` - Matrículas de estudiantes
- `unidades` - Unidades de curso
- `temas` - Temas de unidades
- `clases` - Sesiones de clase
- `asistencia` - Registro de asistencia
- `tipos_evaluacion` - Tipos de evaluación
- `evaluaciones` - Evaluaciones/actividades
- `calificaciones` - Notas de estudiantes
- `reportes` - Reportes sobre estudiantes

### Migraciones

Las migraciones se ejecutan automáticamente con Flyway al iniciar la aplicación.
Los archivos de migración están en: `src/main/resources/db/migration/`

**⚠️ Importante sobre Flyway:**
- Flyway **NO ejecuta dos veces** la misma migración
- Mantiene un historial en la tabla `flyway_schema_history`
- Puedes reiniciar la aplicación sin problemas
- Solo ejecuta migraciones nuevas que no estén en el historial

**Ver historial de migraciones:**
```bash
docker exec -it sge-mysql mysql -u root -proot -e "SELECT installed_rank, version, description, success FROM SGE.flyway_schema_history;"
```

### Conectarse a MySQL directamente

```bash
docker exec -it sge-mysql mysql -u root -p
# Password: root
```

O usando un cliente MySQL:
- Host: `localhost`
- Puerto: `3311`
- Usuario: `root`
- Password: `root`
- Base de datos: `SGE`

## 🛠️ Comandos Útiles

### Docker Compose

```bash
# Levantar los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener los servicios
docker-compose down

# Detener y eliminar volúmenes (CUIDADO: borra todos los datos)
docker-compose down -v

# Reiniciar MySQL
docker-compose restart mysql
```

### Maven

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar tests
mvn test

# Ejecutar la aplicación
mvn spring-boot:run

# Limpiar y compilar
mvn clean package
```

## 📦 Tecnologías

- Spring Boot 3.2.0
- Spring Data JPA
- Spring Web
- Spring Validation
- SpringDoc OpenAPI 3 (Swagger UI)
- Flyway (migraciones de BD)
- MySQL 8.0
- Lombok
- Maven

## 👥 Para el Equipo

### Primera vez que clonas el proyecto:

1. Asegúrate de tener Docker instalado
2. Ejecuta `docker-compose up -d` en la carpeta del proyecto
3. Ejecuta `mvn clean install` para descargar dependencias
4. Ejecuta `mvn spring-boot:run` o corre desde tu IDE

### Si ya tienes el proyecto:

1. Asegúrate de que MySQL esté corriendo: `docker-compose up -d`
2. Ejecuta la aplicación normalmente

### Nota importante:

- El puerto **3311** se usa para evitar conflictos con instalaciones locales de MySQL
- Todos deben usar el mismo `compose.yaml` para mantener consistencia
- La base de datos se crea automáticamente con Flyway

## 📝 Configuración

La configuración de la base de datos está en:
- `src/main/resources/application.properties` (configuración principal)
- `src/main/resources/application-dev.properties` (perfil de desarrollo)

Por defecto, la aplicación usa:
- URL: `jdbc:mysql://localhost:3311/SGE`
- Usuario: `root`
- Password: `root`

## ❓ Preguntas Frecuentes (FAQ)

### ¿Puedo reiniciar la aplicación sin problemas?
**Sí.** Flyway detecta automáticamente qué migraciones ya se ejecutaron y no las vuelve a ejecutar.

### ¿Qué pasa si detengo y vuelvo a levantar MySQL?
Los datos se mantienen en un volumen de Docker. Tus tablas y datos seguirán ahí.

### ¿Cómo borro todos los datos y empiezo de cero?
```bash
docker-compose down -v  # ⚠️ CUIDADO: Esto borra TODOS los datos
docker-compose up -d
mvn spring-boot:run     # Flyway volverá a ejecutar todas las migraciones
```

### ¿Cómo agrego una nueva migración?
1. Crea un nuevo archivo en `src/main/resources/db/migration/`
2. Nómbralo siguiendo el patrón: `V20__descripcion.sql` (siguiente número)
3. Al reiniciar la aplicación, Flyway la ejecutará automáticamente

### ¿Puedo modificar una migración ya ejecutada?
**No.** Flyway verifica los checksums. Si modificas una migración ejecutada, dará error.
Solución: Crea una nueva migración con los cambios (V21, V22, etc.)

### El puerto 3311 ya está en uso
Cambia el puerto en `compose.yaml`:
```yaml
ports:
  - '3312:3306'  # Cambia 3311 por otro puerto
```
Y actualiza `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3312/SGE...
```

