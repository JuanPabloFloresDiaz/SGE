# SGE API - Sistema de Gestión Educativa

API REST desarrollada con Spring Boot para la gestión de un sistema educativo.

## 📋 Prerequisitos

- Java 17 o superior
- Maven 3.6+
- Docker y Docker Compose

## 🚀 Inicio Rápido

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
