-- V27__correcion_calificaciones.sql

-- =============================================================================
-- FASE 1: Creación de nuevas tablas y columnas
-- =============================================================================

-- Crear tabla tipos_ponderacion_curso
CREATE TABLE tipos_ponderacion_curso (
    id CHAR(36) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    deleted_at DATETIME(6),
    curso_id CHAR(36) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    peso_porcentaje DECIMAL(5,2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_tipos_ponderacion_curso FOREIGN KEY (curso_id) REFERENCES cursos (id)
) ENGINE=InnoDB;

-- Crear tabla entregas_actividad
CREATE TABLE entregas_actividad (
    id CHAR(36) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6),
    deleted_at DATETIME(6),
    actividad_id CHAR(36) NOT NULL,
    estudiante_id CHAR(36) NOT NULL,
    nota DECIMAL(6,2),
    fecha_entrega DATETIME(6),
    documento_url VARCHAR(500),
    comentario_profesor VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT uk_entrega_actividad_estudiante UNIQUE (actividad_id, estudiante_id),
    CONSTRAINT fk_entregas_actividad FOREIGN KEY (actividad_id) REFERENCES actividades (id),
    CONSTRAINT fk_entregas_estudiante FOREIGN KEY (estudiante_id) REFERENCES estudiantes (id)
) ENGINE=InnoDB;

-- Modificar tabla actividades
-- Agregar columnas curso_id, ponderacion_id, peso
-- curso_id se permite NULL temporalmente para la migración
ALTER TABLE actividades 
ADD COLUMN curso_id CHAR(36) NULL,
ADD COLUMN ponderacion_id CHAR(36) NULL,
ADD COLUMN peso DECIMAL(5,2) NULL;

-- Agregar FKs a actividades
ALTER TABLE actividades
ADD CONSTRAINT fk_actividades_curso FOREIGN KEY (curso_id) REFERENCES cursos (id),
ADD CONSTRAINT fk_actividades_ponderacion FOREIGN KEY (ponderacion_id) REFERENCES tipos_ponderacion_curso (id);

-- Modificar tabla evaluaciones
-- Agregar columna ponderacion_id
ALTER TABLE evaluaciones
ADD COLUMN ponderacion_id CHAR(36) NULL;

-- Agregar FK a evaluaciones
ALTER TABLE evaluaciones
ADD CONSTRAINT fk_evaluaciones_ponderacion FOREIGN KEY (ponderacion_id) REFERENCES tipos_ponderacion_curso (id);


-- =============================================================================
-- FASE 2: Transición de Datos
-- =============================================================================

-- Migrar actividades existentes a un curso
-- Lógica: Asignar al primer curso encontrado que pertenezca a la misma asignatura
UPDATE actividades a
SET curso_id = (
    SELECT c.id 
    FROM cursos c 
    WHERE c.asignatura_id = a.asignatura_id 
    LIMIT 1
)
WHERE curso_id IS NULL;

-- NOTA: Si hay actividades que quedaron con curso_id NULL (porque la asignatura no tiene cursos),
-- se mantendrán así o se deberían eliminar/tratar manualmente.
-- Para asegurar integridad futura, idealmente no deberían quedar nulos si se va a hacer NOT NULL.

-- =============================================================================
-- FASE 3: Limpieza (Opcional / Diferida)
-- =============================================================================

-- Eliminar columna asignatura_id de actividades
-- Se recomienda verificar la migración antes de ejecutar esto en producción.
-- Por ahora lo incluimos para cumplir con la estructura final.

-- Primero eliminamos la FK
ALTER TABLE actividades DROP FOREIGN KEY fk_actividad_asignatura; -- Ajustar nombre de FK si es diferente

-- Luego eliminamos la columna
ALTER TABLE actividades DROP COLUMN asignatura_id;

-- Eliminar columna peso de tipos_evaluacion
ALTER TABLE tipos_evaluacion DROP COLUMN peso;
