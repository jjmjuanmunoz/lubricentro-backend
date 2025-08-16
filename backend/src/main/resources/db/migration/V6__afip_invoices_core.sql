-- Comprobante
CREATE TABLE invoices (
                          id BIGSERIAL PRIMARY KEY,
                          pos_id BIGINT NOT NULL REFERENCES afip_pos(id),
                          cbte_type_code INTEGER NOT NULL,      -- FK lógico a afip_cbte_types.code
                          cbte_number INTEGER,                  -- se completa al emitir
                          concept_code INTEGER NOT NULL DEFAULT 1, -- Productos/Servicios
                          customer_id BIGINT REFERENCES customers(id),
                          currency_code VARCHAR(3) NOT NULL DEFAULT 'ARS',
                          currency_rate NUMERIC(12,6) NOT NULL DEFAULT 1.0,

    -- totales (en moneda del comprobante)
                          net_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
                          iva_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
                          exempt_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
                          tributes_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
                          total_amount NUMERIC(18,2) NOT NULL DEFAULT 0,

    -- AFIP
                          cae VARCHAR(20),
                          cae_expiration DATE,
                          issued_on TIMESTAMP,      -- fecha/hora efectiva de emisión
                          service_from DATE,
                          service_to DATE,
                          due_date DATE,            -- para servicios

    -- estados
                          status VARCHAR(20) NOT NULL DEFAULT 'DRAFT', -- DRAFT|SENT|APPROVED|REJECTED
                          created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_invoices_pos ON invoices(pos_id);
CREATE INDEX idx_invoices_status ON invoices(status);

-- Ítems
CREATE TABLE invoice_items (
                               id BIGSERIAL PRIMARY KEY,
                               invoice_id BIGINT NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
                               product_id BIGINT,  -- opcional, para enlazar con products
                               description VARCHAR(255) NOT NULL,
                               quantity NUMERIC(18,3) NOT NULL,
                               unit_price NUMERIC(18,6) NOT NULL,
                               iva_aliquot_code INTEGER NOT NULL,  -- FK lógico a afip_iva_aliquots.code
                               net_amount NUMERIC(18,2) NOT NULL,
                               iva_amount NUMERIC(18,2) NOT NULL
);

-- Tributos/percepciones adicionales (IIBB/municipal/etc.)
CREATE TABLE invoice_tributes (
                                  id BIGSERIAL PRIMARY KEY,
                                  invoice_id BIGINT NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
                                  code INTEGER NOT NULL,
                                  description VARCHAR(120) NOT NULL,
                                  base_amount NUMERIC(18,2) NOT NULL,
                                  rate NUMERIC(8,4) NOT NULL,
                                  amount NUMERIC(18,2) NOT NULL
);