package com.lubricentro.backend.service;

import ar.com.afip.wsfev1.*;

public class TestingAfipGenerated {

    public static void main(String[] args) throws Exception {
        // El "Service" es la clase generada que sabe crear el port/endpoint
        Service service = new Service();

        // Obtenés el "port" (proxy) para invocar el WS
        ServiceSoap port = service.getServiceSoap();

        // Construir la request wrapper
        FECompUltimoAutorizado request = new FECompUltimoAutorizado();
        request.setAuth(buildAuthRequest()); // auth armado abajo
        request.setPtoVta(1);                 // punto de venta
        request.setCbteTipo(1);               // tipo de comprobante

        // Llamada al WS
        FECompUltimoAutorizadoResponse response = port.feCompUltimoAutorizado(request);

        // Leer resultado
        int ultimoNro = response.getFECompUltimoAutorizadoResult().getCbteNro();
        System.out.println("Último comprobante autorizado: " + ultimoNro);

        // (Opcional) Probar conectividad con FEDummy
        FEDummyResponse dummy = port.feDummy(new FEDummy());
        System.out.println("Ping dummy: " + dummy.getFEDummyResult().getAppServer()
                + " | " + dummy.getFEDummyResult().getDbServer()
                + " | " + dummy.getFEDummyResult().getAuthServer());
    }

    private static FEAuthRequest buildAuthRequest() {
        FEAuthRequest auth = new FEAuthRequest();
        auth.setToken("TOKEN-WSAA");  // reemplazar por token real
        auth.setSign("SIGN-WSAA");    // reemplazar por sign real
        auth.setCuit(20286169113L);   // tu CUIT
        return auth;
    }
}