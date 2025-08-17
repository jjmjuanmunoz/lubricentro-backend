-- WSAA: almacenamiento del TA (token & sign) por ambiente
CREATE TABLE afip_wsaa_ticket (
                                  id BIGSERIAL PRIMARY KEY,
                                  target VARCHAR(50) NOT NULL,       -- "wsfe"
                                  homologation BOOLEAN NOT NULL,     -- true homologación, false producción
                                  unique_id BIGINT NOT NULL,         -- uniqueId del LoginTicketResponse
                                  token TEXT NOT NULL,
                                  sign TEXT NOT NULL,
                                  generation_time TIMESTAMP NOT NULL,
                                  expiration_time TIMESTAMP NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Logs de request/response WSFE (útil para auditoría y debugging)
CREATE TABLE afip_wsfe_log (
                               id BIGSERIAL PRIMARY KEY,
                               invoice_id BIGINT REFERENCES invoices(id) ON DELETE SET NULL,
                               request_xml TEXT,
                               response_xml TEXT,
                               status VARCHAR(20),      -- OK/ERROR
                               message TEXT,
                               created_at TIMESTAMP NOT NULL DEFAULT NOW()
);