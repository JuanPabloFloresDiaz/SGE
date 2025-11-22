# JavaSpringAPI - Sistema de Gestión Educativa Modular

Este proyecto es una plataforma educativa modular que integra una API REST en Spring Boot con servicios de orquestación de datos (Airflow), almacenamiento de objetos (MinIO), caché (Redis) y base de datos relacional (PostgreSQL/MySQL).

## 🏗️ Arquitectura de Microservicios

El proyecto se compone de los siguientes módulos:

### 1. @[api] (Backend Principal)
El núcleo del sistema desarrollado en **Spring Boot**. Maneja la lógica de negocio, gestión de usuarios, cursos y calificaciones.
- **Tecnologías**: Java 17, Spring Boot 3.2, Spring Data JPA, Flyway, **AWS SDK v2 (S3)**.
- **Almacenamiento**: Integrado nativamente con **MinIO** para la gestión de archivos.
- **Documentación**: Para detalles de ejecución y endpoints, consulta el [README interno](./api/README.md).

### 2. @[AirflowETLService] (Orquestación de Datos)
Servicio encargado de los procesos ETL (Extracción, Transformación y Carga).
- **Función**: Automatiza flujos de trabajo como la promoción estudiantil o reportes masivos.
- **Estado**: Actualmente los scripts ETL operan localmente, pero están diseñados para integrarse con MinIO en el futuro.
- **Componentes**:
  - **Webserver**: Interfaz gráfica para monitorear DAGs (Puerto 8089).
  - **Scheduler**: Planifica la ejecución de tareas.
  - **Worker**: Ejecuta las tareas utilizando Celery.
  - **Triggerer**: Maneja eventos asíncronos.

### 3. @[RedisService] (Caché y Broker)
Servicio de base de datos en memoria.
- **Función**:
  - Actúa como **Message Broker** para Celery (comunicación entre Airflow Scheduler y Workers).
  - Caché para la API (si se implementa).

### 4. @[PostgresService] (Base de Datos de Airflow)
Base de datos dedicada para los metadatos de Airflow.
- **Función**: Almacena el estado de los DAGs, usuarios de Airflow y configuraciones.
- **Puerto**: Expuesto en 5446 (interno 5432) para evitar conflictos con la DB de la API.

---

## 🚀 Inicialización de Servicios (Backend Stack)

Para levantar toda la infraestructura de soporte (Airflow, MinIO, Redis, Postgres), hemos preparado un archivo `docker-compose.dev.yml` que orquesta todos estos servicios.

### Prerrequisitos
- Docker y Docker Compose instalados.
- Puertos libres: 8089 (Airflow), 9000/9001 (MinIO), 6379 (Redis), 5446 (Postgres).

### Pasos para Ejecutar

1. **Variables de Entorno**:
   Asegúrate de tener el archivo `.env` en la raíz del proyecto. Este archivo contiene las credenciales necesarias (generado automáticamente o creado manualmente).

2. **Levantar el Stack**:
   Ejecuta el siguiente comando en la raíz del proyecto:
   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   ```

3. **Verificar Servicios**:
   - **Airflow**: http://localhost:8089 (Usuario/Pass: `admin`/`admin` o lo configurado en `.env`).
   - **MinIO**: http://localhost:9001 (Usuario/Pass: `minioadmin`/`minioadmin`).

### Notas Importantes
- **Persistencia**: Los datos de Postgres, Redis y MinIO se guardan en volúmenes de Docker (`javaspringapi_*`).
- **Red**: Todos los servicios se comunican a través de la red `javaspringapi_default`.

---

## ☕ Ejecución de la API (Java Spring Boot)

La API se ejecuta de manera independiente al stack de servicios, pero puede interactuar con ellos.

1. **Base de Datos de la API**:
   La API utiliza su propia base de datos (MySQL por defecto). Sigue las instrucciones en [api/README.md](./api/README.md) para levantarla.

2. **Correr la API**:
   ```bash
   cd api
   mvn spring-boot:run
   ```
   
Consulta el [README de la API](./api/README.md) para más detalles sobre endpoints, Swagger y configuración.
