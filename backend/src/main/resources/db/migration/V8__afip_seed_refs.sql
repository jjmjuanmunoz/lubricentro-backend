-- IVA aliquots (subset)
INSERT INTO afip_iva_aliquots (code, description, rate)
VALUES (3, 'IVA 0%', 0.00),
       (4, 'IVA 10.5%', 10.50),
       (5, 'IVA 21%', 21.00) ON CONFLICT (code) DO NOTHING;

-- Doc types (subset)
INSERT INTO afip_doc_types (code, description)
VALUES (80, 'CUIT'),
       (86, 'CUIL'),
       (96, 'DNI'),
       (99, 'Consumidor Final') ON CONFLICT (code) DO NOTHING;

-- Comprobante types (subset)
INSERT INTO afip_cbte_types (code, description)
VALUES (1, 'Factura A'),
       (6, 'Factura B'),
       (11, 'Factura C') ON CONFLICT (code) DO NOTHING;

-- Concept
INSERT INTO afip_concepts (code, description)
VALUES (1, 'Productos'),
       (2, 'Servicios'),
       (3, 'Productos y Servicios') ON CONFLICT (code) DO NOTHING;

-- Currency
INSERT INTO afip_currencies (code, description, quote)
VALUES ('ARS', 'Peso Argentino', 1.0) ON CONFLICT (code) DO NOTHING;