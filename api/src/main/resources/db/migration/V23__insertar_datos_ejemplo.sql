-- MIGRACIÓN V23: INSERCIÓN DE DATOS DE EJEMPLO PARA EL SISTEMA EDUCATIVO
-- UTILIZA UUID_TO_BIN y BIN_TO_UUID si es necesario, pero para la inserción directa
-- usaremos CHAR(36) para simplificar, asumiendo que tu base de datos maneja UUIDs como strings.
-- Usamos la función UUID() para generar identificadores.

SET @uuid_admin_1 = UUID();
SET @uuid_admin_2 = UUID();
SET @uuid_prof_1 = UUID();
SET @uuid_prof_2 = UUID();
SET @uuid_prof_3 = UUID();
SET @uuid_prof_4 = UUID();
SET @uuid_prof_5 = UUID();
SET @uuid_prof_6 = UUID();

-- =========================================================================
-- 1. ROLES (Administrador, Estudiante, Profesor)
-- =========================================================================

INSERT INTO roles (id, nombre, descripcion) VALUES
(UUID(), 'Administrador', 'Acceso completo al sistema y configuración.'),
(UUID(), 'Estudiante', 'Acceso limitado a cursos, tareas y notas.'),
(UUID(), 'Profesor', 'Acceso a gestión de cursos, clases y evaluaciones.');

-- =========================================================================
-- 2. TIPOS DE EVALUACIÓN (Corto, Discusión, Examen)
-- =========================================================================

INSERT INTO tipos_evaluacion (id, nombre, descripcion, peso) VALUES
(UUID(), 'Corto', 'Evaluación corta de conocimiento específico.', 15.00),
(UUID(), 'Discusión', 'Participación en foro o debate calificado.', 20.00),
(UUID(), 'Examen', 'Prueba extensa y comprensiva de la unidad.', 40.00);

-- =========================================================================
-- 3. PERÍODOS (3 Trimestrales para 2026)
-- =========================================================================

INSERT INTO periodos (id, nombre, fecha_inicio, fecha_fin, activo) VALUES
(UUID(), 'Trimestre I-2026', '2026-01-05', '2026-03-31', 1),
(UUID(), 'Trimestre II-2026', '2026-04-06', '2026-06-30', 1),
(UUID(), 'Trimestre III-2026', '2026-07-06', '2026-09-30', 0);

-- Guardamos el ID del primer periodo para usarlo en los cursos
SET @periodo_2026_id = (SELECT id FROM periodos WHERE nombre = 'Trimestre I-2026');

-- =========================================================================
-- 4. USUARIOS (20 Total: 2 Admin, 6 Prof, 12 Est)
-- =========================================================================

SET @rol_admin_id = (SELECT id FROM roles WHERE nombre = 'Administrador');
SET @rol_prof_id = (SELECT id FROM roles WHERE nombre = 'Profesor');
SET @rol_est_id = (SELECT id FROM roles WHERE nombre = 'Estudiante');
SET @uuid_clave_generica = '$2a$10$UgEq97wiQWkXOopRIm8cvObV6MmQh5F7.VY6.EqNTCywhfIxfRZ5W'; -- Simulación de hash: Clave123

-- A. ADMINISTRADORES (2)
INSERT INTO usuarios (id, username, password_hash, nombre, email, telefono, rol_id) VALUES
(@uuid_admin_1, 'admin.principal', @uuid_clave_generica, 'Juan Pablo', 'juanpa@colegio.edu', '7700-1100', @rol_admin_id),
(@uuid_admin_2, 'admin.secundario', @uuid_clave_generica, 'Veronica Alejandra', 'vero@colegio.edu', '7700-1101', @rol_admin_id);

-- B. PROFESORES (6)
INSERT INTO usuarios (id, username, password_hash, nombre, email, telefono, rol_id) VALUES
-- P1: Ciencias, Química, Física (Prof. de Ciencias Exactas)
(@uuid_prof_1, 'p.garcia', @uuid_clave_generica, 'Dr. Luis Garcia', 'lgarcia@colegio.edu', '7801-2001', @rol_prof_id),
-- P2: Sociales, Historia (Prof. de Humanidades)
(@uuid_prof_2, 'p.martinez', @uuid_clave_generica, 'Lic. Ana Martinez', 'amartinez@colegio.edu', '7801-2002', @rol_prof_id),
-- P3: Matemática
(@uuid_prof_3, 'p.rodriguez', @uuid_clave_generica, 'MSc. Juan Rodriguez', 'jrodriguez@colegio.edu', '7801-2003', @rol_prof_id),
-- P4: Lenguaje
(@uuid_prof_4, 'p.lopez', @uuid_clave_generica, 'Prof. Elena Lopez', 'elopez@colegio.edu', '7801-2004', @rol_prof_id),
-- P5: Inglés
(@uuid_prof_5, 'p.sanchez', @uuid_clave_generica, 'Teacher Sofia Sanchez', 'ssanchez@colegio.edu', '7801-2005', @rol_prof_id),
-- P6: Programación
(@uuid_prof_6, 'p.ramirez', @uuid_clave_generica, 'Ing. Carlos Ramirez', 'cramirez@colegio.edu', '7801-2006', @rol_prof_id);

-- C. ESTUDIANTES (12)
SET @uuid_est_1 = UUID(); SET @uuid_est_2 = UUID(); SET @uuid_est_3 = UUID(); SET @uuid_est_4 = UUID();
SET @uuid_est_5 = UUID(); SET @uuid_est_6 = UUID(); SET @uuid_est_7 = UUID(); SET @uuid_est_8 = UUID();
SET @uuid_est_9 = UUID(); SET @uuid_est_10 = UUID(); SET @uuid_est_11 = UUID(); SET @uuid_est_12 = UUID();

INSERT INTO usuarios (id, username, password_hash, nombre, email, telefono, rol_id) VALUES
(@uuid_est_1, 'est.gonzales', @uuid_clave_generica, 'Sofia Gonzales', 's.gonzales@ejemplo.com', '6000-0001', @rol_est_id),
(@uuid_est_2, 'est.perez', @uuid_clave_generica, 'Alejandro Perez', 'a.perez@ejemplo.com', '6000-0002', @rol_est_id),
(@uuid_est_3, 'est.castro', @uuid_clave_generica, 'Brenda Castro', 'b.castro@ejemplo.com', '6000-0003', @rol_est_id),
(@uuid_est_4, 'est.vargas', @uuid_clave_generica, 'Daniel Vargas', 'd.vargas@ejemplo.com', '6000-0004', @rol_est_id),
(@uuid_est_5, 'est.gomez', @uuid_clave_generica, 'Evelyn Gómez', 'e.gomez@ejemplo.com', '6000-0005', @rol_est_id),
(@uuid_est_6, 'est.hernandez', @uuid_clave_generica, 'Fernando Hernández', 'f.hernandez@ejemplo.com', '6000-0006', @rol_est_id),
(@uuid_est_7, 'est.diaz', @uuid_clave_generica, 'Gabriela Díaz', 'g.diaz@ejemplo.com', '6000-0007', @rol_est_id),
(@uuid_est_8, 'est.cruz', @uuid_clave_generica, 'Héctor Cruz', 'h.cruz@ejemplo.com', '6000-0008', @rol_est_id),
(@uuid_est_9, 'est.flores', @uuid_clave_generica, 'Isabel Flores', 'i.flores@ejemplo.com', '6000-0009', @rol_est_id),
(@uuid_est_10, 'est.morales', @uuid_clave_generica, 'José Morales', 'j.morales@ejemplo.com', '6000-0010', @rol_est_id),
(@uuid_est_11, 'est.r.maria', @uuid_clave_generica, 'María Rivera', 'm.rivera@ejemplo.com', '6000-0011', @rol_est_id),
(@uuid_est_12, 'est.r.david', @uuid_clave_generica, 'David Romero', 'd.romero@ejemplo.com', '6000-0012', @rol_est_id);


-- =========================================================================
-- 5. PROFESORES (Detalle)
-- =========================================================================

INSERT INTO profesores (id, usuario_id, especialidad, contrato) VALUES
(UUID(), @uuid_prof_1, 'Biología y Química', 'tiempo_completo'),
(UUID(), @uuid_prof_2, 'Historia y Ciencias Sociales', 'tiempo_completo'),
(UUID(), @uuid_prof_3, 'Matemáticas Avanzadas', 'tiempo_completo'),
(UUID(), @uuid_prof_4, 'Literatura y Gramática', 'medio_tiempo'),
(UUID(), @uuid_prof_5, 'English Linguistics', 'medio_tiempo'),
(UUID(), @uuid_prof_6, 'Desarrollo Web', 'eventual');

-- Guardamos los IDs de los profesores para asignación de cursos
SET @prof_ciencias_id = (SELECT id FROM profesores WHERE usuario_id = @uuid_prof_1); -- P1
SET @prof_sociales_id = (SELECT id FROM profesores WHERE usuario_id = @uuid_prof_2); -- P2
SET @prof_mate_id = (SELECT id FROM profesores WHERE usuario_id = @uuid_prof_3); -- P3
SET @prof_lenguaje_id = (SELECT id FROM profesores WHERE usuario_id = @uuid_prof_4); -- P4
SET @prof_ingles_id = (SELECT id FROM profesores WHERE usuario_id = @uuid_prof_5); -- P5
SET @prof_programacion_id = (SELECT id FROM profesores WHERE usuario_id = @uuid_prof_6); -- P6

-- =========================================================================
-- 6. ESTUDIANTES (Detalle)
-- =========================================================================

INSERT INTO estudiantes (id, usuario_id, codigo_estudiante, fecha_nacimiento, direccion, genero, ingreso) VALUES
(UUID(), @uuid_est_1, 'EST-0001', '2008-05-15', 'Calle Falsa 123, San Salvador', 'F', '2025-01-15'),
(UUID(), @uuid_est_2, 'EST-0002', '2007-11-20', 'Avenida Norte 45, Santa Ana', 'M', '2025-01-15'),
(UUID(), @uuid_est_3, 'EST-0003', '2008-01-01', 'Residencial Los Robles, La Libertad', 'F', '2025-01-15'),
(UUID(), @uuid_est_4, 'EST-0004', '2007-08-10', 'Urbanización El Sol, San Miguel', 'M', '2025-01-15'),
(UUID(), @uuid_est_5, 'EST-0005', '2008-03-25', 'Colonia Guadalupe, Sonsonate', 'F', '2025-01-15'),
(UUID(), @uuid_est_6, 'EST-0006', '2007-07-05', 'Barrio El Centro, Ahuachapán', 'M', '2025-01-15'),
(UUID(), @uuid_est_7, 'EST-0007', '2008-09-30', 'Cantón El Coco, Usulután', 'F', '2025-01-15'),
(UUID(), @uuid_est_8, 'EST-0008', '2007-06-12', 'Condominio Las Flores, Zacatecoluca', 'M', '2025-01-15'),
(UUID(), @uuid_est_9, 'EST-0009', '2008-02-18', 'Pasaje Las Nubes, Chalatenango', 'F', '2025-01-15'),
(UUID(), @uuid_est_10, 'EST-0010', '2007-12-03', 'Calle Principal, Metapán', 'M', '2025-01-15'),
(UUID(), @uuid_est_11, 'EST-0011', '2008-04-22', 'Colonia Bella Vista, Mejicanos', 'F', '2025-01-15'),
(UUID(), @uuid_est_12, 'EST-0012', '2007-10-08', 'Sector 3, Apopa', 'M', '2025-01-15');

-- Guardamos los IDs de los estudiantes para futuras tablas
SET @est_id_1 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0001');
SET @est_id_2 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0002');
SET @est_id_3 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0003');
SET @est_id_4 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0004');
SET @est_id_5 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0005');
SET @est_id_6 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0006');
SET @est_id_7 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0007');
SET @est_id_8 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0008');
SET @est_id_9 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0009');
SET @est_id_10 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0010');
SET @est_id_11 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0011');
SET @est_id_12 = (SELECT id FROM estudiantes WHERE codigo_estudiante = 'EST-0012');


-- =========================================================================
-- 7. ASIGNATURAS (9 Esenciales)
-- =========================================================================

INSERT INTO asignaturas (id, codigo, nombre, descripcion) VALUES
(UUID(), 'MAT', 'Matemática', 'Estudio de números, estructuras y cambio.'),
(UUID(), 'LEN', 'Lenguaje', 'Análisis literario y competencias comunicativas.'),
(UUID(), 'SOC', 'Sociales', 'Introducción a la sociología y el civismo.'),
(UUID(), 'CIE', 'Ciencias', 'Estudio de la biología fundamental.'),
(UUID(), 'FIS', 'Física', 'Principios de mecánica clásica y energía.'),
(UUID(), 'QUI', 'Química', 'Composición, estructura y propiedades de la materia.'),
(UUID(), 'HIS', 'Historia', 'Estudio de los eventos históricos mundiales.'),
(UUID(), 'ING', 'Inglés', 'Desarrollo de habilidades en el idioma inglés.'),
(UUID(), 'PRO', 'Programación', 'Introducción a la lógica y codificación.');


-- =========================================================================
-- 8. CURSOS (9 Registros, uno por asignatura, asignando profesores)
-- Reglas: P1(Ciencias, Física, Química), P2(Sociales, Historia), P3/P4/P5/P6(1 c/u)
-- =========================================================================

-- Usaremos variables para los IDs de las asignaturas
SET @asig_mate = (SELECT id FROM asignaturas WHERE nombre = 'Matemática');
SET @asig_len = (SELECT id FROM asignaturas WHERE nombre = 'Lenguaje');
SET @asig_soc = (SELECT id FROM asignaturas WHERE nombre = 'Sociales');
SET @asig_cie = (SELECT id FROM asignaturas WHERE nombre = 'Ciencias');
SET @asig_fis = (SELECT id FROM asignaturas WHERE nombre = 'Física');
SET @asig_qui = (SELECT id FROM asignaturas WHERE nombre = 'Química');
SET @asig_his = (SELECT id FROM asignaturas WHERE nombre = 'Historia');
SET @asig_ing = (SELECT id FROM asignaturas WHERE nombre = 'Inglés');
SET @asig_pro = (SELECT id FROM asignaturas WHERE nombre = 'Programación');

INSERT INTO cursos (id, asignatura_id, profesor_id, periodo_id, nombre_grupo, aula_default, cupo) VALUES
(UUID(), @asig_mate, @prof_mate_id, @periodo_2026_id, 'Matemática 101', 'A-101', 30),
(UUID(), @asig_len, @prof_lenguaje_id, @periodo_2026_id, 'Lenguaje 101', 'A-102', 30),
(UUID(), @asig_soc, @prof_sociales_id, @periodo_2026_id, 'Sociales 101', 'B-201', 30),
(UUID(), @asig_cie, @prof_ciencias_id, @periodo_2026_id, 'Ciencias 101', 'C-301', 30),
(UUID(), @asig_fis, @prof_ciencias_id, @periodo_2026_id, 'Física 101', 'C-302', 30),
(UUID(), @asig_qui, @prof_ciencias_id, @periodo_2026_id, 'Química 101', 'C-303', 30),
(UUID(), @asig_his, @prof_sociales_id, @periodo_2026_id, 'Historia 101', 'B-202', 30),
(UUID(), @asig_ing, @prof_ingles_id, @periodo_2026_id, 'Inglés 101', 'A-103', 30),
(UUID(), @asig_pro, @prof_programacion_id, @periodo_2026_id, 'Programación 101', 'D-401', 30);

-- Guardamos los IDs de los cursos (usaremos los nombres para subconsultas más seguras)
SET @curso_mate = (SELECT id FROM cursos WHERE nombre_grupo = 'Matemática 101');
SET @curso_len = (SELECT id FROM cursos WHERE nombre_grupo = 'Lenguaje 101');
SET @curso_soc = (SELECT id FROM cursos WHERE nombre_grupo = 'Sociales 101');
SET @curso_cie = (SELECT id FROM cursos WHERE nombre_grupo = 'Ciencias 101');
SET @curso_fis = (SELECT id FROM cursos WHERE nombre_grupo = 'Física 101');
SET @curso_qui = (SELECT id FROM cursos WHERE nombre_grupo = 'Química 101');
SET @curso_his = (SELECT id FROM cursos WHERE nombre_grupo = 'Historia 101');
SET @curso_ing = (SELECT id FROM cursos WHERE nombre_grupo = 'Inglés 101');
SET @curso_pro = (SELECT id FROM cursos WHERE nombre_grupo = 'Programación 101');


-- =========================================================================
-- 9. BLOQUES DE HORARIO (45 min, 06:00 a 16:00, 10-15 min descanso)
-- Total de 11 bloques de 45 min con 15 min de descanso intermedio.
-- 06:00 - 06:45 (Bloque 1)
-- 07:00 - 07:45 (Bloque 2)
-- 08:00 - 08:45 (Bloque 3)
-- 09:00 - 09:45 (Bloque 4)
-- 10:00 - 10:45 (Bloque 5)
-- 11:00 - 11:45 (Bloque 6 - Almuerzo/Descanso largo)
-- 12:45 - 13:30 (Bloque 7)
-- 13:45 - 14:30 (Bloque 8)
-- 14:45 - 15:30 (Bloque 9)
-- 15:45 - 16:30 (Bloque 10) -- Se extiende 30 min para cubrir el rango.
-- =========================================================================

INSERT INTO bloques_horario (id, nombre, inicio, fin) VALUES
(UUID(), 'B1 - Inicio', '06:00:00', '06:45:00'),
(UUID(), 'B2 - Mañana Temprana', '07:00:00', '07:45:00'),
(UUID(), 'B3 - Media Mañana 1', '08:00:00', '08:45:00'),
(UUID(), 'B4 - Media Mañana 2', '09:00:00', '09:45:00'),
(UUID(), 'B5 - Antes Break', '10:00:00', '10:45:00'),
(UUID(), 'B6 - Almuerzo Clase', '11:00:00', '11:45:00'),
(UUID(), 'B7 - Tarde 1', '12:45:00', '13:30:00'),
(UUID(), 'B8 - Tarde 2', '13:45:00', '14:30:00'),
(UUID(), 'B9 - Tarde 3', '14:45:00', '15:30:00'),
(UUID(), 'B10 - Fin de Jornada', '15:45:00', '16:30:00'); -- Ajustado para llegar a 16:30

-- =========================================================================
-- 10. HORARIOS DE CURSO (Diversificación en días y bloques)
-- Cada curso tendrá 4 o 5 sesiones por semana.
-- =========================================================================

-- MATEMÁTICA (4 sesiones)
INSERT INTO horarios_curso (id, curso_id, bloque_id, dia, aula) VALUES
(UUID(), @curso_mate, (SELECT id FROM bloques_horario WHERE nombre = 'B1 - Inicio'), 'LUN', 'A-101'),
(UUID(), @curso_mate, (SELECT id FROM bloques_horario WHERE nombre = 'B2 - Mañana Temprana'), 'MIE', 'A-101'),
(UUID(), @curso_mate, (SELECT id FROM bloques_horario WHERE nombre = 'B3 - Media Mañana 1'), 'JUE', 'A-101'),
(UUID(), @curso_mate, (SELECT id FROM bloques_horario WHERE nombre = 'B4 - Media Mañana 2'), 'VIE', 'A-101');

-- LENGUAJE (4 sesiones)
INSERT INTO horarios_curso (id, curso_id, bloque_id, dia, aula) VALUES
(UUID(), @curso_len, (SELECT id FROM bloques_horario WHERE nombre = 'B5 - Antes Break'), 'LUN', 'A-102'),
(UUID(), @curso_len, (SELECT id FROM bloques_horario WHERE nombre = 'B6 - Almuerzo Clase'), 'MAR', 'A-102'),
(UUID(), @curso_len, (SELECT id FROM bloques_horario WHERE nombre = 'B7 - Tarde 1'), 'JUE', 'A-102'),
(UUID(), @curso_len, (SELECT id FROM bloques_horario WHERE nombre = 'B8 - Tarde 2'), 'VIE', 'A-102');

-- SOCIALES (3 sesiones)
INSERT INTO horarios_curso (id, curso_id, bloque_id, dia, aula) VALUES
(UUID(), @curso_soc, (SELECT id FROM bloques_horario WHERE nombre = 'B9 - Tarde 3'), 'LUN', 'B-201'),
(UUID(), @curso_soc, (SELECT id FROM bloques_horario WHERE nombre = 'B10 - Fin de Jornada'), 'MIE', 'B-201'),
(UUID(), @curso_soc, (SELECT id FROM bloques_horario WHERE nombre = 'B1 - Inicio'), 'VIE', 'B-201');

-- CIENCIAS (5 sesiones)
INSERT INTO horarios_curso (id, curso_id, bloque_id, dia, aula) VALUES
(UUID(), @curso_cie, (SELECT id FROM bloques_horario WHERE nombre = 'B2 - Mañana Temprana'), 'LUN', 'C-301'),
(UUID(), @curso_cie, (SELECT id FROM bloques_horario WHERE nombre = 'B3 - Media Mañana 1'), 'MAR', 'C-301'),
(UUID(), @curso_cie, (SELECT id FROM bloques_horario WHERE nombre = 'B4 - Media Mañana 2'), 'MIE', 'C-301'),
(UUID(), @curso_cie, (SELECT id FROM bloques_horario WHERE nombre = 'B5 - Antes Break'), 'JUE', 'C-301'),
(UUID(), @curso_cie, (SELECT id FROM bloques_horario WHERE nombre = 'B6 - Almuerzo Clase'), 'VIE', 'C-301');

-- FÍSICA (3 sesiones)
INSERT INTO horarios_curso (id, curso_id, bloque_id, dia, aula) VALUES
(UUID(), @curso_fis, (SELECT id FROM bloques_horario WHERE nombre = 'B7 - Tarde 1'), 'LUN', 'C-302'),
(UUID(), @curso_fis, (SELECT id FROM bloques_horario WHERE nombre = 'B8 - Tarde 2'), 'MIE', 'C-302'),
(UUID(), @curso_fis, (SELECT id FROM bloques_horario WHERE nombre = 'B9 - Tarde 3'), 'VIE', 'C-302');

-- QUÍMICA (3 sesiones)
INSERT INTO horarios_curso (id, curso_id, bloque_id, dia, aula) VALUES
(UUID(), @curso_qui, (SELECT id FROM bloques_horario WHERE nombre = 'B10 - Fin de Jornada'), 'LUN', 'C-303'),
(UUID(), @curso_qui, (SELECT id FROM bloques_horario WHERE nombre = 'B1 - Inicio'), 'MAR', 'C-303'),
(UUID(), @curso_qui, (SELECT id FROM bloques_horario WHERE nombre = 'B2 - Mañana Temprana'), 'JUE', 'C-303');

-- HISTORIA (4 sesiones)
INSERT INTO horarios_curso (id, curso_id, bloque_id, dia, aula) VALUES
(UUID(), @curso_his, (SELECT id FROM bloques_horario WHERE nombre = 'B3 - Media Mañana 1'), 'LUN', 'B-202'),
(UUID(), @curso_his, (SELECT id FROM bloques_horario WHERE nombre = 'B4 - Media Mañana 2'), 'MAR', 'B-202'),
(UUID(), @curso_his, (SELECT id FROM bloques_horario WHERE nombre = 'B5 - Antes Break'), 'MIE', 'B-202'),
(UUID(), @curso_his, (SELECT id FROM bloques_horario WHERE nombre = 'B6 - Almuerzo Clase'), 'JUE', 'B-202');

-- INGLÉS (4 sesiones)
INSERT INTO horarios_curso (id, curso_id, bloque_id, dia, aula) VALUES
(UUID(), @curso_ing, (SELECT id FROM bloques_horario WHERE nombre = 'B7 - Tarde 1'), 'MAR', 'A-103'),
(UUID(), @curso_ing, (SELECT id FROM bloques_horario WHERE nombre = 'B8 - Tarde 2'), 'MIE', 'A-103'),
(UUID(), @curso_ing, (SELECT id FROM bloques_horario WHERE nombre = 'B9 - Tarde 3'), 'JUE', 'A-103'),
(UUID(), @curso_ing, (SELECT id FROM bloques_horario WHERE nombre = 'B10 - Fin de Jornada'), 'VIE', 'A-103');

-- PROGRAMACIÓN (4 sesiones)
INSERT INTO horarios_curso (id, curso_id, bloque_id, dia, aula) VALUES
(UUID(), @curso_pro, (SELECT id FROM bloques_horario WHERE nombre = 'B1 - Inicio'), 'JUE', 'D-401'),
(UUID(), @curso_pro, (SELECT id FROM bloques_horario WHERE nombre = 'B2 - Mañana Temprana'), 'VIE', 'D-401'),
(UUID(), @curso_pro, (SELECT id FROM bloques_horario WHERE nombre = 'B3 - Media Mañana 1'), 'VIE', 'D-401'),
(UUID(), @curso_pro, (SELECT id FROM bloques_horario WHERE nombre = 'B4 - Media Mañana 2'), 'VIE', 'D-401');

-- =========================================================================
-- 11. INSCRIPCIONES (Al menos 5 estudiantes por curso)
-- 12 estudiantes en total. Curso A(5), B(5), C(5), D(5), E(5), F(5), G(5), H(5), I(5)
-- Total de 45 inscripciones (12 estudiantes * 4 = 48 si todos llevaran 4, haremos 5 por curso)
-- =========================================================================

-- Inscribimos 5 estudiantes en cada uno de los 9 cursos (45 inscripciones)
-- Estudiantes 1-5 al curso 1 (Matemática)
INSERT INTO inscripciones (id, curso_id, estudiante_id) VALUES
(UUID(), @curso_mate, @est_id_1), (UUID(), @curso_mate, @est_id_2), (UUID(), @curso_mate, @est_id_3),
(UUID(), @curso_mate, @est_id_4), (UUID(), @curso_mate, @est_id_5);

-- Estudiantes 6-10 al curso 2 (Lenguaje)
INSERT INTO inscripciones (id, curso_id, estudiante_id) VALUES
(UUID(), @curso_len, @est_id_6), (UUID(), @curso_len, @est_id_7), (UUID(), @curso_len, @est_id_8),
(UUID(), @curso_len, @est_id_9), (UUID(), @curso_len, @est_id_10);

-- Estudiantes 1, 2, 7, 8, 11 al curso 3 (Sociales)
INSERT INTO inscripciones (id, curso_id, estudiante_id) VALUES
(UUID(), @curso_soc, @est_id_1), (UUID(), @curso_soc, @est_id_2), (UUID(), @curso_soc, @est_id_7),
(UUID(), @curso_soc, @est_id_8), (UUID(), @curso_soc, @est_id_11);

-- Estudiantes 3, 4, 9, 10, 12 al curso 4 (Ciencias)
INSERT INTO inscripciones (id, curso_id, estudiante_id) VALUES
(UUID(), @curso_cie, @est_id_3), (UUID(), @curso_cie, @est_id_4), (UUID(), @curso_cie, @est_id_9),
(UUID(), @curso_cie, @est_id_10), (UUID(), @curso_cie, @est_id_12);

-- Estudiantes 1, 5, 6, 11, 12 al curso 5 (Física)
INSERT INTO inscripciones (id, curso_id, estudiante_id) VALUES
(UUID(), @curso_fis, @est_id_1), (UUID(), @curso_fis, @est_id_5), (UUID(), @curso_fis, @est_id_6),
(UUID(), @curso_fis, @est_id_11), (UUID(), @curso_fis, @est_id_12);

-- Estudiantes 2, 3, 7, 8, 9 al curso 6 (Química)
INSERT INTO inscripciones (id, curso_id, estudiante_id) VALUES
(UUID(), @curso_qui, @est_id_2), (UUID(), @curso_qui, @est_id_3), (UUID(), @curso_qui, @est_id_7),
(UUID(), @curso_qui, @est_id_8), (UUID(), @curso_qui, @est_id_9);

-- Estudiantes 4, 5, 10, 11, 12 al curso 7 (Historia)
INSERT INTO inscripciones (id, curso_id, estudiante_id) VALUES
(UUID(), @curso_his, @est_id_4), (UUID(), @curso_his, @est_id_5), (UUID(), @curso_his, @est_id_10),
(UUID(), @curso_his, @est_id_11), (UUID(), @curso_his, @est_id_12);

-- Estudiantes 1, 3, 5, 7, 9, 11 al curso 8 (Inglés) -- 6 estudiantes
INSERT INTO inscripciones (id, curso_id, estudiante_id) VALUES
(UUID(), @curso_ing, @est_id_1), (UUID(), @curso_ing, @est_id_3), (UUID(), @curso_ing, @est_id_5),
(UUID(), @curso_ing, @est_id_7), (UUID(), @curso_ing, @est_id_9), (UUID(), @curso_ing, @est_id_11);

-- Estudiantes 2, 4, 6, 8, 10, 12 al curso 9 (Programación) -- 6 estudiantes
INSERT INTO inscripciones (id, curso_id, estudiante_id) VALUES
(UUID(), @curso_pro, @est_id_2), (UUID(), @curso_pro, @est_id_4), (UUID(), @curso_pro, @est_id_6),
(UUID(), @curso_pro, @est_id_8), (UUID(), @curso_pro, @est_id_10), (UUID(), @curso_pro, @est_id_12);


-- =========================================================================
-- 12. UNIDADES (27 Total: 3 por cada uno de los 9 cursos)
-- =========================================================================

-- Creamos 3 unidades por curso. Guardamos los IDs de las Unidades 1, 2 y 3 del curso de PROGRAMACIÓN
SET @unidad_pro_1 = UUID();
SET @unidad_pro_2 = UUID();
SET @unidad_pro_3 = UUID();

-- PROGRAMACIÓN (Unidades)
INSERT INTO unidades (id, curso_id, numero, titulo, descripcion) VALUES
(@unidad_pro_1, @curso_pro, 1, 'Introducción a Algoritmos', 'Fundamentos de la lógica de programación y pseudocódigo.'),
(@unidad_pro_2, @curso_pro, 2, 'Estructuras de Control', 'Uso de condicionales y ciclos.'),
(@unidad_pro_3, @curso_pro, 3, 'Funciones y Módulos', 'Organización de código y reutilización.');

-- MATEMÁTICA (Unidades)
INSERT INTO unidades (id, curso_id, numero, titulo) VALUES
(UUID(), @curso_mate, 1, 'Álgebra Lineal'),
(UUID(), @curso_mate, 2, 'Cálculo Diferencial'),
(UUID(), @curso_mate, 3, 'Geometría Analítica');

-- LENGUAJE (Unidades)
INSERT INTO unidades (id, curso_id, numero, titulo) VALUES
(UUID(), @curso_len, 1, 'Ortografía Avanzada'),
(UUID(), @curso_len, 2, 'Análisis de Novelas'),
(UUID(), @curso_len, 3, 'Escritura Creativa');

-- SOCIALES (Unidades)
INSERT INTO unidades (id, curso_id, numero, titulo) VALUES
(UUID(), @curso_soc, 1, 'Democracia y Ciudadanía'),
(UUID(), @curso_soc, 2, 'Globalización'),
(UUID(), @curso_soc, 3, 'Culturas Precolombinas');

-- CIENCIAS (Unidades)
INSERT INTO unidades (id, curso_id, numero, titulo) VALUES
(UUID(), @curso_cie, 1, 'La Célula y sus Componentes'),
(UUID(), @curso_cie, 2, 'Genética Básica'),
(UUID(), @curso_cie, 3, 'Ecosistemas');

-- FÍSICA (Unidades)
INSERT INTO unidades (id, curso_id, numero, titulo) VALUES
(UUID(), @curso_fis, 1, 'Cinemática'),
(UUID(), @curso_fis, 2, 'Leyes de Newton'),
(UUID(), @curso_fis, 3, 'Trabajo y Energía');

-- QUÍMICA (Unidades)
INSERT INTO unidades (id, curso_id, numero, titulo) VALUES
(UUID(), @curso_qui, 1, 'Nomenclatura Química'),
(UUID(), @curso_qui, 2, 'Reacciones Químicas'),
(UUID(), @curso_qui, 3, 'Soluciones');

-- HISTORIA (Unidades)
INSERT INTO unidades (id, curso_id, numero, titulo) VALUES
(UUID(), @curso_his, 1, 'Revolución Industrial'),
(UUID(), @curso_his, 2, 'Guerras Mundiales'),
(UUID(), @curso_his, 3, 'Historia de El Salvador');

-- INGLÉS (Unidades)
INSERT INTO unidades (id, curso_id, numero, titulo) VALUES
(UUID(), @curso_ing, 1, 'Simple Tenses'),
(UUID(), @curso_ing, 2, 'Conditionals'),
(UUID(), @curso_ing, 3, 'Reading Comprehension');


-- =========================================================================
-- 13. TEMAS (54 Total: 2 por cada una de las 27 unidades)
-- =========================================================================

-- Guardamos IDs de los temas de la Unidad 1 de PROGRAMACIÓN
SET @tema_pro_1_1 = UUID();
SET @tema_pro_1_2 = UUID();

-- TEMAS para UNIDAD 1 de PROGRAMACIÓN
INSERT INTO temas (id, unidad_id, numero, titulo, duracion_minutos) VALUES
(@tema_pro_1_1, @unidad_pro_1, 1, 'Introducción al Pensamiento Lógico', 90),
(@tema_pro_1_2, @unidad_pro_1, 2, 'Variables y Tipos de Datos', 90);

-- TEMAS para UNIDAD 2 de PROGRAMACIÓN
INSERT INTO temas (id, unidad_id, numero, titulo, duracion_minutos) VALUES
(UUID(), @unidad_pro_2, 1, 'Estructuras IF-ELSE', 90),
(UUID(), @unidad_pro_2, 2, 'Bucles FOR y WHILE', 90);

-- TEMAS para UNIDAD 3 de PROGRAMACIÓN
INSERT INTO temas (id, unidad_id, numero, titulo, duracion_minutos) VALUES
(UUID(), @unidad_pro_3, 1, 'Definición de Funciones', 90),
(UUID(), @unidad_pro_3, 2, 'Scope y Recursividad', 90);

-- Solo un ejemplo para las demás unidades (las restantes 24 unidades x 2 temas = 48 temas)
INSERT INTO temas (id, unidad_id, numero, titulo, duracion_minutos)
SELECT UUID(), U.id, 1, CONCAT('Tema ', U.numero, '.1: Concepto Inicial'), 45
FROM unidades U WHERE U.curso_id <> @curso_pro;

INSERT INTO temas (id, unidad_id, numero, titulo, duracion_minutos)
SELECT UUID(), U.id, 2, CONCAT('Tema ', U.numero, '.2: Aplicación Práctica'), 45
FROM unidades U WHERE U.curso_id <> @curso_pro;


-- =========================================================================
-- 14. CLASES (1 Clase por Tema. Usaremos los horarios para coherencia)
-- Clases se crearán para el curso de PROGRAMACIÓN
-- =========================================================================

SET @bloque_pro_clase = (SELECT id FROM bloques_horario WHERE nombre = 'B1 - Inicio');

-- 1. Clase para el Tema 1 (LUN, B1, 06:00-06:45)
SET @clase_pro_1 = UUID();
INSERT INTO clases (id, curso_id, fecha, inicio, fin, unidad_id, tema_id, notas) VALUES
(@clase_pro_1, @curso_pro, '2026-01-08', '06:00:00', '06:45:00', @unidad_pro_1, @tema_pro_1_1, 'Clase inaugural de lógica de programación. Se espera que esta clase se haya asignado al día JUE');

-- 2. Clase para el Tema 2 (LUN, B2, 07:00-07:45)
SET @clase_pro_2 = UUID();
INSERT INTO clases (id, curso_id, fecha, inicio, fin, unidad_id, tema_id, notas) VALUES
(@clase_pro_2, @curso_pro, '2026-01-09', '07:00:00', '07:45:00', @unidad_pro_1, @tema_pro_1_2, 'Introducción formal a las variables.');

-- 3. Clase para el Tema 3
INSERT INTO clases (id, curso_id, fecha, inicio, fin, unidad_id, tema_id, notas) VALUES
(UUID(), @curso_pro, '2026-01-15', '06:00:00', '06:45:00', @unidad_pro_2, (SELECT id FROM temas WHERE titulo = 'Estructuras IF-ELSE'), 'Práctica de condicionales.');

-- Y así se harían las 51 clases restantes...

-- =========================================================================
-- 15. ASISTENCIA (Registro de una clase: Clase 1 de Programación)
-- Estudiantes inscritos en Programación: 2, 4, 6, 8, 10, 12 (6 estudiantes)
-- =========================================================================

INSERT INTO asistencia (id, clase_id, estudiante_id, estado, observacion, registrado_por)
SELECT
    UUID(),
    @clase_pro_1,
    E.id,
    CASE WHEN U.username IN ('est.gonzales', 'est.perez') THEN 'ausente' ELSE 'presente' END, -- Simulamos 2 ausencias
    CASE WHEN U.username = 'est.perez' THEN 'Problema médico' ELSE NULL END,
    @uuid_prof_6 -- Registrado por Prof. de Programación
FROM estudiantes E
JOIN usuarios U ON E.usuario_id = U.id
JOIN inscripciones I ON E.id = I.estudiante_id
WHERE I.curso_id = @curso_pro;

-- =========================================================================
-- 16. EVALUACIONES (3 tipos: Corto, Discusión, Examen - Asignadas al curso PROGRAMACIÓN)
-- =========================================================================

SET @tipo_corto = (SELECT id FROM tipos_evaluacion WHERE nombre = 'Corto');
SET @tipo_disc = (SELECT id FROM tipos_evaluacion WHERE nombre = 'Discusión');
SET @tipo_exam = (SELECT id FROM tipos_evaluacion WHERE nombre = 'Examen');

SET @eval_corto = UUID();
SET @eval_disc = UUID();
SET @eval_exam = UUID();

INSERT INTO evaluaciones (id, curso_id, tipo_id, nombre, fecha, peso, publicado) VALUES
(@eval_corto, @curso_pro, @tipo_corto, 'Corto: Pseudocódigo', '2026-01-20', 15.00, 1),
(@eval_disc, @curso_pro, @tipo_disc, 'Discusión: Ventajas del IF', '2026-02-05', 20.00, 1),
(@eval_exam, @curso_pro, @tipo_exam, 'Examen: Unidad 1 y 2', '2026-03-01', 40.00, 0);

-- =========================================================================
-- 17. CALIFICACIONES (Para las 3 evaluaciones del curso PROGRAMACIÓN)
-- Estudiantes en Prog: 2, 4, 6, 8, 10, 12 (6 estudiantes)
-- =========================================================================

-- Corto (6 notas)
INSERT INTO calificaciones (id, evaluacion_id, estudiante_id, nota) VALUES
(UUID(), @eval_corto, @est_id_2, 8.5),
(UUID(), @eval_corto, @est_id_4, 7.0),
(UUID(), @eval_corto, @est_id_6, 9.8),
(UUID(), @eval_corto, @est_id_8, 5.5), -- Bajo
(UUID(), @eval_corto, @est_id_10, 10.0),
(UUID(), @eval_corto, @est_id_12, 6.2);

-- Discusión (6 notas)
INSERT INTO calificaciones (id, evaluacion_id, estudiante_id, nota) VALUES
(UUID(), @eval_disc, @est_id_2, 9.0),
(UUID(), @eval_disc, @est_id_4, 8.8),
(UUID(), @eval_disc, @est_id_6, 7.5),
(UUID(), @eval_disc, @est_id_8, 6.0), -- Bajo
(UUID(), @eval_disc, @est_id_10, 9.5),
(UUID(), @eval_disc, @est_id_12, 7.8);

-- Examen (6 notas)
INSERT INTO calificaciones (id, evaluacion_id, estudiante_id, nota) VALUES
(UUID(), @eval_exam, @est_id_2, 7.0),
(UUID(), @eval_exam, @est_id_4, 6.5),
(UUID(), @eval_exam, @est_id_6, 8.0),
(UUID(), @eval_exam, @est_id_8, 4.0), -- MUY Bajo rendimiento
(UUID(), @eval_exam, @est_id_10, 9.0),
(UUID(), @eval_exam, @est_id_12, 5.0);


-- =========================================================================
-- 18. REPORTES DE CONDUCTA (1 Registro: Académico, Bajo Rendimiento)
-- Dirigido a EST-0008 (Héctor Cruz, que tuvo 5.5, 6.0 y 4.0) en el curso PROGRAMACIÓN.
-- =========================================================================

INSERT INTO reportes (id, estudiante_id, curso_id, tipo, titulo, descripcion, creado_por) VALUES
(UUID(), @est_id_8, @curso_pro, 'academico', 'Bajo Rendimiento Consistente',
'El estudiante Héctor Cruz muestra un desempeño significativamente bajo en las evaluaciones del curso de Programación, con notas que no alcanzan el mínimo en la mayoría de las actividades. Se requiere plan de mejora.',
@uuid_prof_6); -- Creado por Prof. de Programación

-- =========================================================================
-- 19. ACTIVIDADES (1 Registro de Ejemplo)
-- Asignado al profesor de Ciencias (P1) y asignatura Física
-- =========================================================================

INSERT INTO actividades (id, asignatura_id, profesor_id, titulo, descripcion, fecha_apertura, fecha_cierre, activo) VALUES
(UUID(), @asig_fis, @prof_ciencias_id, 'Cuestionario de Leyes de Newton',
'Responder a las preguntas sobre las tres Leyes de Newton y su aplicación en la vida diaria.',
'2026-02-01 08:00:00', '2026-02-08 23:59:00', true);