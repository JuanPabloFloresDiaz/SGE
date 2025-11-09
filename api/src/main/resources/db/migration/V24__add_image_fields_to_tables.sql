-- V24__add_image_fields_to_tables.sql

-- Agregar foto de perfil a usuarios
ALTER TABLE usuarios 
ADD COLUMN foto_perfil_url VARCHAR(500) NULL 
COMMENT 'URL de la foto de perfil del usuario';

-- Agregar foto a estudiantes (opcional, podría usar la de usuario)
ALTER TABLE estudiantes 
ADD COLUMN foto_url VARCHAR(500) NULL 
COMMENT 'URL de la foto del estudiante';

-- Agregar imagen a cursos
ALTER TABLE cursos 
ADD COLUMN imagen_url VARCHAR(500) NULL 
COMMENT 'Imagen representativa del curso/grado';

-- Agregar imagen a asignaturas
ALTER TABLE asignaturas 
ADD COLUMN imagen_url VARCHAR(500) NULL 
COMMENT 'Imagen identificativa de la asignatura/materia';