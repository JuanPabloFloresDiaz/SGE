-- Tabla de temas dentro de una unidad
CREATE TABLE temas (
  id CHAR(36) NOT NULL PRIMARY KEY,
  unidad_id CHAR(36) NOT NULL,
  numero INT NULL,
  titulo VARCHAR(200) NOT NULL,
  descripcion TEXT,
  duracion_minutos INT UNSIGNED NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL DEFAULT NULL,
  FOREIGN KEY (unidad_id) REFERENCES unidades(id) ON DELETE CASCADE
) ENGINE=InnoDB;
