-- Tabla de asistencia por clase
CREATE TABLE asistencia (
  id CHAR(36) NOT NULL PRIMARY KEY,
  clase_id CHAR(36) NOT NULL,
  estudiante_id CHAR(36) NOT NULL,
  estado ENUM('presente','ausente','tarde','justificado') NOT NULL,
  observacion VARCHAR(255),
  registrado_por CHAR(36) NULL,
  registrado_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL DEFAULT NULL,
  UNIQUE KEY uni_clase_est (clase_id, estudiante_id),
  FOREIGN KEY (clase_id) REFERENCES clases(id) ON DELETE CASCADE,
  FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE CASCADE,
  FOREIGN KEY (registrado_por) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;
