-- Tabla de reportes/observaciones relacionadas con estudiantes
CREATE TABLE reportes (
  id CHAR(36) NOT NULL PRIMARY KEY,
  estudiante_id CHAR(36) NOT NULL,
  curso_id CHAR(36) NULL,
  tipo ENUM('conducta','academico','otro') DEFAULT 'otro',
  titulo VARCHAR(200),
  descripcion TEXT,
  creado_por CHAR(36) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL DEFAULT NULL,
  FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE CASCADE,
  FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE SET NULL,
  FOREIGN KEY (creado_por) REFERENCES usuarios(id) ON DELETE SET NULL
) ENGINE=InnoDB;
