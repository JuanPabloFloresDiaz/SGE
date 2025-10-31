-- Tabla de calificaciones (notas) de estudiantes en evaluaciones
CREATE TABLE calificaciones (
  id CHAR(36) NOT NULL PRIMARY KEY,
  evaluacion_id CHAR(36) NOT NULL,
  estudiante_id CHAR(36) NOT NULL,
  nota DECIMAL(6,2) NOT NULL,
  comentario VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL DEFAULT NULL,
  UNIQUE KEY uni_eval_est (evaluacion_id, estudiante_id),
  FOREIGN KEY (evaluacion_id) REFERENCES evaluaciones(id) ON DELETE CASCADE,
  FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE CASCADE
) ENGINE=InnoDB;
