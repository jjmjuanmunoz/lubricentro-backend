-- V12__add_fiscal_fields_to_users.sql
-- Agregar campos fiscales AFIP a la tabla users

-- Campos de identificación personal
ALTER TABLE users ADD COLUMN IF NOT EXISTS nombre VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS apellido VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS razon_social VARCHAR(200);

-- Tipo de documento (DNI, CUIT, CUIL, etc.) - almacenado como enum string
ALTER TABLE users ADD COLUMN IF NOT EXISTS tipo_documento VARCHAR(20);
ALTER TABLE users ADD COLUMN IF NOT EXISTS numero_documento VARCHAR(20);

-- Condición frente al IVA
ALTER TABLE users ADD COLUMN IF NOT EXISTS condicion_iva VARCHAR(40);

-- Contacto adicional
ALTER TABLE users ADD COLUMN IF NOT EXISTS telefono VARCHAR(30);

-- Domicilio fiscal (embebido en la misma tabla)
ALTER TABLE users ADD COLUMN IF NOT EXISTS domicilio_calle VARCHAR(200);
ALTER TABLE users ADD COLUMN IF NOT EXISTS domicilio_numero VARCHAR(20);
ALTER TABLE users ADD COLUMN IF NOT EXISTS domicilio_piso VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS domicilio_depto VARCHAR(50);
ALTER TABLE users ADD COLUMN IF NOT EXISTS domicilio_localidad VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS domicilio_provincia VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS domicilio_codigo_postal VARCHAR(10);
ALTER TABLE users ADD COLUMN IF NOT EXISTS domicilio_pais VARCHAR(100) DEFAULT 'Argentina';

-- Índices para búsquedas frecuentes
CREATE INDEX IF NOT EXISTS idx_users_numero_documento ON users(numero_documento);
CREATE INDEX IF NOT EXISTS idx_users_tipo_numero_doc ON users(tipo_documento, numero_documento);
CREATE INDEX IF NOT EXISTS idx_users_nombre ON users(nombre);
CREATE INDEX IF NOT EXISTS idx_users_apellido ON users(apellido);

-- Comentarios para documentación
COMMENT ON COLUMN users.tipo_documento IS 'Tipo de documento AFIP: DNI, CUIT, CUIL, PASAPORTE, etc.';
COMMENT ON COLUMN users.numero_documento IS 'Número de documento (sin guiones ni puntos)';
COMMENT ON COLUMN users.condicion_iva IS 'Condición frente al IVA según AFIP';
COMMENT ON COLUMN users.razon_social IS 'Razón social para facturación (personas jurídicas)';
