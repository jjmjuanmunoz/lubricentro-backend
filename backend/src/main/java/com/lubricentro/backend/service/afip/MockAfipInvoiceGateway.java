package com.lubricentro.backend.service.afip;

import com.lubricentro.backend.entity.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementacion mock del gateway AFIP para desarrollo y testing.
 *
 * Esta implementacion genera CAEs ficticios y simula el comportamiento
 * del servicio WSFEv1 de AFIP sin hacer llamadas externas.
 *
 * Los numeros de comprobante se mantienen en memoria por punto de venta
 * y tipo de comprobante, reiniciandose al reiniciar la aplicacion.
 */
@Service
public class MockAfipInvoiceGateway implements AfipInvoiceGateway {

    private static final Logger log = LoggerFactory.getLogger(MockAfipInvoiceGateway.class);

    // Almacena ultimo numero por clave "posId-cbteType"
    private final Map<String, AtomicInteger> lastNumbers = new ConcurrentHashMap<>();

    @Override
    public AfipInvoiceResult emitirFactura(Invoice invoice) {
        log.info("[MOCK AFIP] Emitiendo factura - POS: {}, Tipo: {}, Cliente: {}",
                invoice.getPos().getPosNumber(),
                invoice.getCbteTypeCode(),
                invoice.getNombreCliente());

        // Simular validaciones basicas
        if (invoice.getItems() == null || invoice.getItems().isEmpty()) {
            log.warn("[MOCK AFIP] Factura rechazada: sin items");
            return AfipInvoiceResult.rejected("La factura debe tener al menos un item");
        }

        if (invoice.getTotalAmount() == null || invoice.getTotalAmount().signum() <= 0) {
            log.warn("[MOCK AFIP] Factura rechazada: monto invalido");
            return AfipInvoiceResult.rejected("El monto total debe ser mayor a cero");
        }

        // Generar numero de comprobante
        int posNumber = invoice.getPos().getPosNumber();
        int cbteType = invoice.getCbteTypeCode();
        Integer nextNumber = obtenerUltimoComprobante(posNumber, cbteType) + 1;

        // Incrementar contador
        String key = posNumber + "-" + cbteType;
        lastNumbers.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();

        // Generar CAE mock
        String cae = generateMockCae();
        LocalDate caeVencimiento = LocalDate.now().plusDays(10);

        log.info("[MOCK AFIP] Factura aprobada - CAE: {}, Numero: {}, Vencimiento: {}",
                cae, nextNumber, caeVencimiento);

        return AfipInvoiceResult.success(cae, caeVencimiento, nextNumber);
    }

    @Override
    public Integer obtenerUltimoComprobante(int puntoVenta, int tipoComprobante) {
        String key = puntoVenta + "-" + tipoComprobante;
        AtomicInteger counter = lastNumbers.get(key);
        return counter != null ? counter.get() : 0;
    }

    @Override
    public boolean isServiceAvailable() {
        // Mock siempre disponible
        log.debug("[MOCK AFIP] Service check - disponible");
        return true;
    }

    /**
     * Genera un CAE ficticio con formato similar al real.
     * Formato: 14 digitos numericos.
     */
    private String generateMockCae() {
        String nano = String.valueOf(System.nanoTime());
        String rand = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        String base = nano.substring(Math.max(0, nano.length() - 8)) + rand;
        // Asegurar que solo tenga digitos y sea de 14 caracteres
        String numericOnly = base.replaceAll("[^0-9]", "");
        if (numericOnly.length() < 14) {
            numericOnly = numericOnly + "00000000000000";
        }
        return numericOnly.substring(0, 14);
    }

    /**
     * Inicializa el ultimo numero de comprobante desde la base de datos.
     * Usado para sincronizar el mock con los datos existentes.
     */
    public void initializeFromDatabase(int puntoVenta, int tipoComprobante, int ultimoNumero) {
        String key = puntoVenta + "-" + tipoComprobante;
        lastNumbers.put(key, new AtomicInteger(ultimoNumero));
        log.info("[MOCK AFIP] Inicializado contador - POS: {}, Tipo: {}, Ultimo: {}",
                puntoVenta, tipoComprobante, ultimoNumero);
    }
}
