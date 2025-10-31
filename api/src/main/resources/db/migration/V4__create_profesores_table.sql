-- Tabla de profesores
CREATE TABLE profesores (
  id CHAR(36) NOT NULL PRIMARY KEY,
  usuario_id CHAR(36) NULL,
  especialidad VARCHAR(150),
  contrato ENUM('tiempo_completo','medio_tiempo','eventual') DEFAULT 'eventual',
  activo TINYINT(1) DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL DEFAULT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;
