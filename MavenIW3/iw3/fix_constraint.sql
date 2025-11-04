-- Script para arreglar el constraint de contra_activacion
-- Este constraint debe permitir múltiples NULL porque la contraseña
-- solo se asigna en el Punto 2 (pesaje inicial)

USE iw3_2025;

-- 1. Eliminar el constraint único existente
ALTER TABLE ordenes DROP INDEX UKkquinjwfjisvx0u6jj9flgt3g;

-- 2. Modificar la columna para permitir NULL explícitamente
ALTER TABLE ordenes MODIFY COLUMN contra_activacion INT NULL;

-- 3. Crear el constraint único que permita múltiples NULL
-- En MySQL 8+, un índice único permite múltiples NULL
ALTER TABLE ordenes ADD CONSTRAINT UKkquinjwfjisvx0u6jj9flgt3g UNIQUE (contra_activacion);

-- 4. Verificar que las órdenes existentes tengan NULL en vez de 0
UPDATE ordenes SET contra_activacion = NULL WHERE contra_activacion = 0;
