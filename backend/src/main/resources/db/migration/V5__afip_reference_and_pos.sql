-- Tablas de referencia AFIP (códigos se cargan luego)
CREATE TABLE afip_doc_types (
                                code INTEGER PRIMARY KEY,
                                description VARCHAR(120) NOT NULL
);
CREATE TABLE afip_cbte_types (
                                 code INTEGER PRIMARY KEY,
                                 description VARCHAR(120) NOT NULL
);
CREATE TABLE afip_iva_aliquots (
                                   code INTEGER PRIMARY KEY,
                                   description VARCHAR(120) NOT NULL,
                                   rate NUMERIC(5,2) NOT NULL -- ej. 21.00
);
CREATE TABLE afip_concepts (
                               code INTEGER PRIMARY KEY,  -- 1=Productos, 2=Servicios, etc.
                               description VARCHAR(120) NOT NULL
);
CREATE TABLE afip_currencies (
                                 code VARCHAR(3) PRIMARY KEY,  -- ARS, USD, etc.
                                 description VARCHAR(120) NOT NULL,
                                 quote NUMERIC(12,6) NOT NULL DEFAULT 1.0
);

-- Puntos de venta (POS)
CREATE TABLE afip_pos (
                          id BIGSERIAL PRIMARY KEY,
                          pos_number INTEGER NOT NULL,
                          description VARCHAR(120),
                          cbte_type_scope VARCHAR(20) NOT NULL DEFAULT 'A_B', -- alcance (A/B/C)
                          homologation BOOLEAN NOT NULL DEFAULT TRUE, -- true: homologación
                          UNIQUE (pos_number)
);

CREATE TABLE customers (
                           id BIGSERIAL PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           doc_type INTEGER, -- FK lógico a afip_doc_types.code
                           doc_number VARCHAR(20),
                           email VARCHAR(255),
                           address VARCHAR(255),
                           is_active BOOLEAN NOT NULL DEFAULT TRUE
);