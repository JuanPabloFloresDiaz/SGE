-- Tabla de horarios por curso
CREATE TABLE horarios_curso (
  id CHAR(36) NOT NULL PRIMARY KEY,
  curso_id CHAR(36) NOT NULL,
  bloque_id CHAR(36) NOT NULL,
  dia ENUM('LUN','MAR','MIE','JUE','VIE','SAB','DOM') NOT NULL,
  aula VARCHAR(100) NULL,
  tipo ENUM('regular','laboratorio','taller','otro') DEFAULT 'regular',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  deleted_at DATETIME NULL DEFAULT NULL,
  UNIQUE KEY uni_horario_curso (curso_id, bloque_id, dia),
  FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE,
  FOREIGN KEY (bloque_id) REFERENCES bloques_horario(id) ON DELETE RESTRICT
) ENGINE=InnoDB;
