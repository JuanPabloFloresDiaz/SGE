-- Índices para mejorar el rendimiento
CREATE INDEX idx_actividades_asignatura ON actividades(asignatura_id);
CREATE INDEX idx_actividades_profesor ON actividades(profesor_id);
CREATE INDEX idx_actividades_fecha_apertura ON actividades(fecha_apertura);
CREATE INDEX idx_actividades_fecha_cierre ON actividades(fecha_cierre);
CREATE INDEX idx_actividades_activo ON actividades(activo);
CREATE INDEX idx_actividades_deleted_at ON actividades(deleted_at);