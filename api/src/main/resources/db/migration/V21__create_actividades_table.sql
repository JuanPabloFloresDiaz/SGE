-- Tabla de Actividades
CREATE TABLE actividades (
    id CHAR(36) NOT NULL PRIMARY KEY,
    asignatura_id CHAR(36) NOT NULL,
    profesor_id CHAR(36) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_apertura DATETIME NOT NULL,
    fecha_cierre DATETIME NOT NULL,
    imagen_url VARCHAR(500),
    documento_url VARCHAR(500),
    documento_nombre VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT true,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL DEFAULT NULL,
    CONSTRAINT fk_actividad_asignatura FOREIGN KEY (asignatura_id) 
        REFERENCES asignaturas(id) ON DELETE CASCADE,
    CONSTRAINT fk_actividad_profesor FOREIGN KEY (profesor_id) 
        REFERENCES profesores(id) ON DELETE CASCADE,
    CONSTRAINT chk_fechas_actividad CHECK (fecha_cierre > fecha_apertura)
) ENGINE=InnoDB;
