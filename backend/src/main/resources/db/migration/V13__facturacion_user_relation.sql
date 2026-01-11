-- V13: Extender facturacion para soportar User como cliente
-- Agregar relacion Invoice -> User (clientes del sistema)

-- Agregar columna user_id a invoices (nullable, puede usar customer_id O user_id)
ALTER TABLE invoices
ADD COLUMN user_id BIGINT REFERENCES users(id);

-- Indice para busqueda por usuario
CREATE INDEX idx_invoices_user_id ON invoices(user_id);

-- Agregar campo activo a afip_pos
ALTER TABLE afip_pos
ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

-- Constraint: al menos uno de customer_id o user_id debe estar presente
-- Esto se valida a nivel de aplicacion para mayor flexibilidad

-- Indice compuesto para busquedas por cliente y numero de comprobante
CREATE INDEX idx_invoices_cbte_number ON invoices(cbte_number);
CREATE INDEX idx_invoices_customer_id ON invoices(customer_id);
