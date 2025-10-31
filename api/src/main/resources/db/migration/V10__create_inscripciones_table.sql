-- Tabla de inscripciones/matrículas de estudiantes en cursos
CREATE TABLE inscripciones (
  id CHAR(36) NOT NULL PRIMARY KEY,
  curso_id CHAR(36) NOT NULL,
  estudiante_id CHAR(36) NOT NULL,
  fecha_inscripcion DATE DEFAULT (CURRENT_DATE),
  estado ENUM('inscrito','retirado','completado') DEFAULT 'inscrito',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL DEFAULT NULL,
  UNIQUE KEY uni_est_curso (curso_id, estudiante_id),
  FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE,
  FOREIGN KEY (estudiante_id) REFERENCES estudiantes(id) ON DELETE CASCADE
) ENGINE=InnoDB;
