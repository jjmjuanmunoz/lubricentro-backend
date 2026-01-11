# Integración con AFIP – WSAA & WSFEv1

Este proyecto implementa la integración con **AFIP** para la emisión de comprobantes electrónicos.  
Se utilizan **dos servicios SOAP (WSDL)**: uno para autenticación (**WSAA**) y otro para facturación (**WSFEv1**).

---

## 🔑 WSAA – Autenticación
- **WSDL Homologación:**  
  `https://wsaahomo.afip.gov.ar/ws/services/LoginCms?wsdl`
- **WSDL Producción:**  
  `https://wsaa.afip.gov.ar/ws/services/LoginCms?wsdl`
- **Método principal:** `loginCms`
- **Entrada:** CMS firmado con nuestro certificado (`.crt`) y clave privada (`.key`).
- **Salida:** Ticket de Acceso (TA) con:
    - `token`
    - `sign`
    - `expirationTime`

👉 El **TA dura aprox. 12 horas**. Debemos cachearlo y renovarlo antes de su vencimiento.

---

## 🧾 WSFEv1 – Facturación Electrónica
- **WSDL Homologación:**  
  `https://wswhomo.afip.gov.ar/wsfev1/service.asmx?WSDL`
- **WSDL Producción:**  
  `https://servicios1.afip.gov.ar/wsfev1/service.asmx?WSDL`
- **Entrada:** `token`, `sign`, `cuit`.
- **Operaciones principales:**
    - `FEDummy`: prueba de conectividad.
    - `feCompUltimoAutorizado`: consultar último comprobante emitido.
    - `FECAESolicitar`: solicitar autorización (CAE) de una nueva factura.

---

## 📌 Flujo de Autenticación + Facturación
1. Generar **LoginTicketRequest (LTR)**.
2. Firmar con `.crt` + `.key` → se obtiene un CMS.
3. Enviar CMS a **WSAA** → obtener **TA** (`token + sign`).
4. Invocar **WSFEv1** con ese TA:
    - Validar conectividad con `FEDummy`.
    - Consultar último comprobante (`feCompUltimoAutorizado`).
    - Emitir nuevas facturas (`FECAESolicitar`).
5. Renovar el TA cuando caduque (~12 hs).

---

## ✅ Estado actual
- [x] Alta en AFIP y configuración del servicio.
- [x] Certificado y clave generados (`.crt` y `.key`).
- [x] Cliente SOAP para **WSAA** y **WSFEv1** generado.
- [x] Probado **WSAA** → obtenemos `TOKEN` y `SIGN`.
- [x] Conectividad validada con **WSFEv1 (FEDummy)**.
- [ ] Próximo paso: implementar `feCompUltimoAutorizado` y `FECAESolicitar` en homologación.

---

## 🚀 Próximos pasos
- Emitir la primera factura en **Homologación**.
- Testear ciclo completo de facturación.
- Migrar a **Producción** con el certificado correspondiente.  