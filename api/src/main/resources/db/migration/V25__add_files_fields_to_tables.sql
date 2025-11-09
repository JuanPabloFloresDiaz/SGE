-- =============================================================================
-- Migración V25: Agregar campos de archivos/documentos a las tablas
-- =============================================================================

ALTER TABLE clases 
ADD COLUMN documento_url VARCHAR(500) NULL 
COMMENT 'URL del documento/presentación de la clase (PDF, PPT, etc.)';

ALTER TABLE clases 
ADD COLUMN documento_nombre VARCHAR(255) NULL 
COMMENT 'Nombre original del archivo de la clase';

ALTER TABLE unidades 
ADD COLUMN documento_url VARCHAR(500) NULL 
COMMENT 'URL del material complementario de la unidad';

ALTER TABLE unidades 
ADD COLUMN documento_nombre VARCHAR(255) NULL 
COMMENT 'Nombre del archivo de material de la unidad';


ALTER TABLE temas 
ADD COLUMN documento_url VARCHAR(500) NULL 
COMMENT 'URL del recurso educativo del tema';

ALTER TABLE temas 
ADD COLUMN documento_nombre VARCHAR(255) NULL 
COMMENT 'Nombre del archivo del recurso del tema';


ALTER TABLE evaluaciones 
ADD COLUMN documento_url VARCHAR(500) NULL 
COMMENT 'URL del documento de la evaluación (instrucciones, rúbrica, etc.)';

ALTER TABLE evaluaciones 
ADD COLUMN documento_nombre VARCHAR(255) NULL 
COMMENT 'Nombre del archivo de la evaluación';

-- Índices para mejorar el rendimiento de búsquedas
CREATE INDEX idx_clases_documento ON clases(documento_url);
CREATE INDEX idx_unidades_documento ON unidades(documento_url);
CREATE INDEX idx_temas_documento ON temas(documento_url);
CREATE INDEX idx_evaluaciones_documento ON evaluaciones(documento_url);
