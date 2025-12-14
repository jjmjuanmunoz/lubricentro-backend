package com.arcana.afip.api;

import java.time.LocalDate;

public interface WsfeBillingService {

    /** Último comprobante autorizado para (ptoVta, tipoCbte). */
    int getLastAuthorizedNumber(int ptoVta, int cbteTipo);

    /** Solicita CAE para un comprobante mínimo (ejemplo simple). */
    record CaeResponse(String cae, LocalDate caeVto, int cbteNro) {}

    CaeResponse requestCAE(SimpleInvoice invoice);

    /** DTO mínimo para demo; luego lo reemplazás por tu modelo. */
    record SimpleInvoice(
            int concepto,           // 1 prod, 2 serv, 3 ambos
            int docTipo, long docNro,
            int cbteTipo, int ptoVta, int cbteNro,
            LocalDate cbteFch,
            double impNeto, double impIva, double impTotal,
            int ivaCodigo, double ivaBaseImp, double ivaAlicuota // ej: 5=21%
    ) {}
}