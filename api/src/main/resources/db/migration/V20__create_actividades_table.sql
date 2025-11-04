-- Tabla de Actividades
CREATE TABLE actividades (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    asignatura_id UUID NOT NULL,
    profesor_id UUID NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_apertura TIMESTAMP NOT NULL,
    fecha_cierre TIMESTAMP NOT NULL,
    imagen_url VARCHAR(500),
    documento_url VARCHAR(500),
    documento_nombre VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    CONSTRAINT fk_actividad_asignatura FOREIGN KEY (asignatura_id) 
        REFERENCES asignaturas(id) ON DELETE CASCADE,
    CONSTRAINT fk_actividad_profesor FOREIGN KEY (profesor_id) 
        REFERENCES profesores(id) ON DELETE CASCADE,
    CONSTRAINT chk_fechas_actividad CHECK (fecha_cierre > fecha_apertura)
);
