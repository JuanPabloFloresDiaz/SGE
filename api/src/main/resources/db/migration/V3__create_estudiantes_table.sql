-- Tabla de estudiantes
CREATE TABLE estudiantes (
  id CHAR(36) NOT NULL PRIMARY KEY,
  usuario_id CHAR(36) NULL,
  codigo_estudiante VARCHAR(50) UNIQUE,
  fecha_nacimiento DATE,
  direccion VARCHAR(255),
  genero ENUM('M','F','O') DEFAULT 'O',
  ingreso DATE,
  activo TINYINT(1) DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL DEFAULT NULL,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;
