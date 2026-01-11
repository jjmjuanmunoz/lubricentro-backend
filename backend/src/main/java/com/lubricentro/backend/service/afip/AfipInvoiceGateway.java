package com.lubricentro.backend.service.afip;

import com.lubricentro.backend.entity.Invoice;

/**
 * Gateway para la emision de facturas electronicas en AFIP.
 *
 * Esta interfaz abstrae la comunicacion con los servicios de AFIP,
 * permitiendo usar un mock para desarrollo/testing y la implementacion
 * real para produccion.
 *
 * Para conectar AFIP real, implementar esta interfaz usando el modulo
 * afip-integration con los servicios WSAA y WSFEv1.
 */
public interface AfipInvoiceGateway {

    /**
     * Emite una factura electronica en AFIP.
     *
     * @param invoice La factura a emitir (debe tener todos los datos necesarios)
     * @return Resultado de la emision con CAE si fue exitosa
     */
    AfipInvoiceResult emitirFactura(Invoice invoice);

    /**
     * Obtiene el ultimo numero de comprobante autorizado para un punto de venta y tipo.
     *
     * @param puntoVenta Numero del punto de venta
     * @param tipoComprobante Codigo del tipo de comprobante AFIP
     * @return Ultimo numero autorizado (0 si no hay comprobantes)
     */
    Integer obtenerUltimoComprobante(int puntoVenta, int tipoComprobante);

    /**
     * Verifica si el servicio AFIP esta disponible.
     *
     * @return true si el servicio responde correctamente
     */
    boolean isServiceAvailable();
}
