-- Tabla de cursos/grupos
CREATE TABLE cursos (
  id CHAR(36) NOT NULL PRIMARY KEY,
  asignatura_id CHAR(36) NOT NULL,
  profesor_id CHAR(36) NULL,
  periodo_id CHAR(36) NOT NULL,
  nombre_grupo VARCHAR(100),
  aula_default VARCHAR(100),
  cupo INT UNSIGNED DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL DEFAULT NULL,
  FOREIGN KEY (asignatura_id) REFERENCES asignaturas(id) ON DELETE RESTRICT,
  FOREIGN KEY (profesor_id) REFERENCES profesores(id) ON DELETE SET NULL,
  FOREIGN KEY (periodo_id) REFERENCES periodos(id) ON DELETE RESTRICT
) ENGINE=InnoDB;
