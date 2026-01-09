-- Script de prueba para verificar la estructura de la base de datos
-- Este script muestra cómo deberían verse las tablas después de las migraciones

-- Verificar tabla clients
DESCRIBE clients;

-- Verificar tabla bank_accounts
DESCRIBE bank_accounts;

-- Verificar tabla credit_cards
DESCRIBE credit_cards;

-- Verificar tabla bank_movements
DESCRIBE bank_movements;

-- Verificar las foreign keys
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE
    REFERENCED_TABLE_SCHEMA = 'Bank'
    AND TABLE_NAME IN ('bank_accounts', 'credit_cards', 'bank_movements');

