# AFIP – Integración WSAA & WSFE

Este módulo implementa la integración con los servicios web de AFIP para **autenticación (WSAA)** y **facturación electrónica (WSFE)**.

---

## 🔑 WSAA (Web Service de Autenticación y Autorización)
El WSAA se utiliza para solicitar las credenciales (**TOKEN** y **SIGN**) que permiten invocar los demás servicios de AFIP (por ejemplo WSFE).

- **Homologación (test):**  
  `https://wsaahomo.afip.gov.ar/ws/services/LoginCms?wsdl`

- **Producción:**  
  `https://wsaa.afip.gov.ar/ws/services/LoginCms?wsdl`

---

## 📄 WSFE (Web Service de Facturación Electrónica)
El WSFE permite generar facturas electrónicas, obtener CAE, consultar comprobantes, entre otros.

- **Homologación (test):**  
  `https://wswhomo.afip.gov.ar/wsfev1/service.asmx?WSDL`

- **Producción:**  
  `https://wsfe.afip.gov.ar/wsfev1/service.asmx?WSDL`

---

## ⚙️ Flujo de Autenticación y Facturación
1. Generar el **Login Ticket Request (LTR)** firmado con la clave privada.
2. Invocar `loginCms()` en el **WSAA** y obtener `TOKEN` + `SIGN`.
3. Usar `TOKEN` + `SIGN` para invocar métodos del **WSFE** (ej: emitir facturas).

---

## 🛠️ Generación de Clases a partir de WSDL
Para generar las clases Java desde los WSDL, ejecutar:

```bash
mvn -q -DskipTests -Pcodegen-ws generate-sources