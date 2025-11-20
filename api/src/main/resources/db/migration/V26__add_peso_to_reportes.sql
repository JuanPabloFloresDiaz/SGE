-- Agregar columna peso a la tabla reportes
ALTER TABLE reportes 
ADD COLUMN peso ENUM('leve','moderado','grave') DEFAULT 'leve' AFTER tipo;
