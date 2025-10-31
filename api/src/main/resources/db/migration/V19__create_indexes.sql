-- Crear índices útiles
CREATE INDEX idx_usuario_rol ON usuarios(rol_id);
CREATE INDEX idx_inscripcion_curso ON inscripciones(curso_id);
CREATE INDEX idx_inscripcion_estudiante ON inscripciones(estudiante_id);
CREATE INDEX idx_calificaciones_estudiante ON calificaciones(estudiante_id);
CREATE INDEX idx_calificaciones_evaluacion ON calificaciones(evaluacion_id);
CREATE INDEX idx_asistencia_clase ON asistencia(clase_id);
CREATE INDEX idx_asistencia_estudiante ON asistencia(estudiante_id);
CREATE INDEX idx_clases_curso ON clases(curso_id);
CREATE INDEX idx_evaluaciones_curso ON evaluaciones(curso_id);
CREATE INDEX idx_cursos_asignatura ON cursos(asignatura_id);
CREATE INDEX idx_cursos_profesor ON cursos(profesor_id);
CREATE INDEX idx_cursos_periodo ON cursos(periodo_id);
