SGE Database Structure
======================
Common Fields (BaseEntity)
--------------------------
All tables (except schema_version) include:
- id: CHAR(36) (UUID, PK)
- created_at: DATETIME
- updated_at: DATETIME
- deleted_at: DATETIME (Soft Delete)

Tables
------

1. usuarios
   - username: VARCHAR(100) (Unique)
   - password_hash: VARCHAR(255)
   - nombre: VARCHAR(120)
   - email: VARCHAR(150) (Unique)
   - telefono: VARCHAR(30)
   - activo: BOOLEAN
   - foto_perfil_url: VARCHAR(500)
   - rol_id: CHAR(36) (FK -> roles.id)

2. roles
   - nombre: VARCHAR(50) (Unique)
   - descripcion: VARCHAR(255)

3. estudiantes
   - usuario_id: CHAR(36) (FK -> usuarios.id, OneToOne)
   - codigo_estudiante: VARCHAR(50) (Unique)
   - fecha_nacimiento: DATE
   - direccion: VARCHAR(255)
   - genero: ENUM('M', 'F', 'O')
   - ingreso: DATE
   - activo: BOOLEAN
   - foto_url: VARCHAR(500)

4. profesores
   - usuario_id: CHAR(36) (FK -> usuarios.id, OneToOne)
   - especialidad: VARCHAR(150)
   - contrato: ENUM('tiempo_completo', 'medio_tiempo', 'eventual')
   - activo: BOOLEAN

5. asignaturas
   - codigo: VARCHAR(50) (Unique)
   - nombre: VARCHAR(200)
   - descripcion: TEXT
   - imagen_url: VARCHAR(500)

6. periodos
   - nombre: VARCHAR(100)
   - fecha_inicio: DATE
   - fecha_fin: DATE
   - activo: BOOLEAN

7. cursos
   - asignatura_id: CHAR(36) (FK -> asignaturas.id)
   - profesor_id: CHAR(36) (FK -> profesores.id)
   - periodo_id: CHAR(36) (FK -> periodos.id)
   - nombre_grupo: VARCHAR(100)
   - aula_default: VARCHAR(100)
   - cupo: INTEGER
   - imagen_url: VARCHAR(500)

8. inscripciones
   - curso_id: CHAR(36) (FK -> cursos.id)
   - estudiante_id: CHAR(36) (FK -> estudiantes.id)
   - fecha_inscripcion: DATE
   - estado: ENUM('inscrito', 'retirado', 'completado')

9. unidades
   - curso_id: CHAR(36) (FK -> cursos.id)
   - titulo: VARCHAR(200)
   - descripcion: TEXT
   - numero: INTEGER
   - documento_url: VARCHAR(500)
   - documento_nombre: VARCHAR(255)

10. temas
    - unidad_id: CHAR(36) (FK -> unidades.id)
    - titulo: VARCHAR(200)
    - descripcion: TEXT
    - numero: INTEGER
    - duracion_minutos: INTEGER
    - documento_url: VARCHAR(500)
    - documento_nombre: VARCHAR(255)

11. bloques_horario
    - nombre: VARCHAR(100)
    - inicio: TIME
    - fin: TIME

12. horarios_curso
    - curso_id: CHAR(36) (FK -> cursos.id)
    - bloque_id: CHAR(36) (FK -> bloques_horario.id)
    - dia: ENUM('LUN', 'MAR', 'MIE', 'JUE', 'VIE', 'SAB', 'DOM')
    - aula: VARCHAR(100)
    - tipo: ENUM('regular', 'laboratorio', 'taller', 'otro')

13. clases
    - curso_id: CHAR(36) (FK -> cursos.id)
    - fecha: DATE
    - inicio: TIME
    - fin: TIME
    - unidad_id: CHAR(36) (FK -> unidades.id)
    - tema_id: CHAR(36) (FK -> temas.id)
    - notas: TEXT
    - documento_url: VARCHAR(500)
    - documento_nombre: VARCHAR(255)

14. asistencia
    - clase_id: CHAR(36) (FK -> clases.id)
    - estudiante_id: CHAR(36) (FK -> estudiantes.id)
    - estado: ENUM('presente', 'ausente', 'tarde', 'justificado')
    - observacion: VARCHAR(255)
    - registrado_por: CHAR(36) (FK -> usuarios.id)
    - registrado_at: DATETIME

15. tipos_evaluacion
    - nombre: VARCHAR(100)
    - descripcion: VARCHAR(255)

16. evaluaciones
    - curso_id: CHAR(36) (FK -> cursos.id)
    - tipo_id: CHAR(36) (FK -> tipos_evaluacion.id)
    - ponderacion_id: CHAR(36) (FK -> tipos_ponderacion_curso.id)
    - nombre: VARCHAR(200)
    - fecha: DATE
    - peso: DECIMAL(5,2)
    - publicado: BOOLEAN
    - documento_url: VARCHAR(500)
    - documento_nombre: VARCHAR(255)

17. calificaciones
    - evaluacion_id: CHAR(36) (FK -> evaluaciones.id)
    - estudiante_id: CHAR(36) (FK -> estudiantes.id)
    - nota: DECIMAL(6,2)
    - comentario: VARCHAR(255)

18. actividades
    - curso_id: CHAR(36) (FK -> cursos.id)
    - ponderacion_id: CHAR(36) (FK -> tipos_ponderacion_curso.id)
    - profesor_id: CHAR(36) (FK -> profesores.id)
    - titulo: VARCHAR(200)
    - descripcion: TEXT
    - fecha_apertura: DATETIME
    - fecha_cierre: DATETIME
    - imagen_url: VARCHAR(500)
    - documento_url: VARCHAR(500)
    - documento_nombre: VARCHAR(255)
    - activo: BOOLEAN
    - peso: DECIMAL(5,2)

19. reportes
    - estudiante_id: CHAR(36) (FK -> estudiantes.id)
    - curso_id: CHAR(36) (FK -> cursos.id)
    - tipo: ENUM('conducta', 'academico', 'otro')
    - peso: ENUM('leve', 'moderado', 'grave')
    - titulo: VARCHAR(200)
    - descripcion: TEXT
    - creado_por: CHAR(36) (FK -> usuarios.id)

20. tipos_ponderacion_curso
    - curso_id: CHAR(36) (FK -> cursos.id)
    - nombre: VARCHAR(100)
    - peso_porcentaje: DECIMAL(5,2)

21. entregas_actividad
    - actividad_id: CHAR(36) (FK -> actividades.id)
    - estudiante_id: CHAR(36) (FK -> estudiantes.id)
    - nota: DECIMAL(6,2)
    - fecha_entrega: DATETIME
    - documento_url: VARCHAR(500)
    - comentario_profesor: VARCHAR(255)