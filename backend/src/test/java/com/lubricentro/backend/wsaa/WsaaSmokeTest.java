package com.lubricentro.backend.wsaa;
//
//import ar.com.afip.wsaa.LoginCMS;
//import ar.com.afip.wsaa.LoginCMSService;
//import java.io.ByteArrayInputStream;
//import java.io.FileInputStream;
//import java.nio.charset.StandardCharsets;
//import java.security.KeyStore;
//import java.security.PrivateKey;
//import java.security.cert.X509Certificate;
//import java.time.OffsetDateTime;
//import java.time.ZoneOffset;
//import java.util.Base64;
//import java.util.UUID;
//import javax.xml.parsers.DocumentBuilderFactory;
//
//import ar.com.afip.wsaa.LoginCmsResponse;
//import ar.com.afip.wsaa.LoginCms_Type;
//import org.apache.cxf.ext.logging.LoggingFeature;
//import org.apache.cxf.frontend.ClientProxy;
//import org.apache.cxf.transport.http.HTTPConduit;
//import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
//import org.bouncycastle.cert.jcajce.JcaCertStore;
//import org.bouncycastle.cms.CMSProcessableByteArray;
//import org.bouncycastle.cms.CMSSignedData;
//import org.bouncycastle.cms.CMSSignedDataGenerator;
//import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
//import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
//import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
//import org.w3c.dom.Document;
//import org.w3c.dom.NodeList;

/**
 * Smoke test para WSAA (homologación): genera LTR, firma PKCS#7 y llama LoginCms.
 * Imprime TOKEN y SIGN si todo salió OK.
 *
 * Ejecutar con: mvn -q -DskipTests -Dexec.mainClass=com.lubricentro.backend.wsaa.WsaaSmokeTest exec:java
 * (o desde el IDE con Run 'main')
 */
public class WsaaSmokeTest {
//    private static final String P12_PATH = "/Users/juanjoDev/Documents/Afip/p12/afip-homo.p12";
//    private static final String P12_PASSWORD = "";
//    private static final String SERVICE = "wsfe";
//    private static final String ENDPOINT_HOMO = "https://wsaahomo.afip.gov.ar/ws/services/LoginCms";
//
//    public static void main(String[] args) throws Exception {
//        // Keystore PKCS#12
//        KeyStore ks = KeyStore.getInstance("PKCS12");
//        try (FileInputStream fis = new FileInputStream(P12_PATH)) {
//            ks.load(fis, P12_PASSWORD.toCharArray());
//        }
//        String alias = ks.aliases().nextElement();
//        PrivateKey key = (PrivateKey) ks.getKey(alias, P12_PASSWORD.toCharArray());
//        X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
//
//        // LTR con XML declaration y zona -03:00 (AFIP)
//        String ltrXml = buildLoginTicketRequestXml(SERVICE);
//        System.out.println("LTR XML:\n" + ltrXml);
//
//        // CMS PKCS#7 encapsulado (DER) → Base64 sin saltos
//        String cmsBase64 = signCmsEncapsulated(ltrXml, key, cert);
//
//        // Port + endpoint + timeouts + logging
//        LoginCMSService svc = new LoginCMSService();
//        LoginCMS port = svc.getLoginCms();
//        var client = ClientProxy.getClient(port);
//        client.getEndpoint().getEndpointInfo().setAddress(ENDPOINT_HOMO);
//
//        // logging CXF (request/response SOAP)
//        client.getEndpoint().getActiveFeatures().add(new LoggingFeature());
//
//        HTTPConduit conduit = (HTTPConduit) client.getConduit();
//        HTTPClientPolicy policy = new HTTPClientPolicy();
//        policy.setConnectionTimeout(10_000);
//        policy.setReceiveTimeout(20_000);
//        policy.setAllowChunking(true);
//        conduit.setClient(policy);
//
//        // Wrapper: in0 = CMS Base64
//        LoginCms_Type req = new LoginCms_Type();
//        req.setIn0(cmsBase64);
//
//        // Invocar
//        LoginCmsResponse resp = port.loginCms(req);
//        String taXml = resp.getLoginCmsReturn();
//        System.out.println("\nTA XML:\n" + taXml);
//
//        // Parsear
//        var ta = parseTa(taXml);
//        System.out.println("\nTOKEN: " + ta.token);
//        System.out.println("SIGN : " + ta.sign);
//        System.out.println("GEN  : " + ta.generationTime);
//        System.out.println("EXP  : " + ta.expirationTime);
//    }
//
//    private static String buildLoginTicketRequestXml(String service) {
//        // Horario de Buenos Aires (-03:00) y ventana ±5 min
//        ZoneOffset AR = ZoneOffset.of("-03:00");
//        OffsetDateTime now = OffsetDateTime.now(AR);
//        OffsetDateTime from = now.minusMinutes(5);
//        OffsetDateTime to   = now.plusMinutes(5);
//        long uid = (System.currentTimeMillis() / 1000L); // epoch seconds
//
//        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<loginTicketRequest version=\"1.0\">" +
//                "  <header>" +
//                "    <uniqueId>" + uid + "</uniqueId>" +
//                "    <generationTime>" + from + "</generationTime>" +
//                "    <expirationTime>" + to + "</expirationTime>" +
//                "  </header>" +
//                "  <service>" + service + "</service>" +
//                "</loginTicketRequest>";
//    }
//
//    private static String signCmsEncapsulated(String xml, PrivateKey key, X509Certificate cert) throws Exception {
//        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
//        var signer = new JcaContentSignerBuilder("SHA256withRSA").build(key);
//        var sigInfo = new JcaSignerInfoGeneratorBuilder(
//                new JcaDigestCalculatorProviderBuilder().build()
//        ).build(signer, cert);
//
//        gen.addSignerInfoGenerator(sigInfo);
//        gen.addCertificates(new JcaCertStore(java.util.List.of(cert)));
//
//        byte[] data = xml.getBytes(StandardCharsets.UTF_8);
//        CMSSignedData sigData = gen.generate(new CMSProcessableByteArray(data), true); // true = encapsulado
//        return Base64.getEncoder().encodeToString(sigData.getEncoded()); // sin saltos
//    }
//
//    private static class Ta {
//        String token, sign;
//        String generationTime, expirationTime;
//    }
//
//    private static Ta parseTa(String xml) throws Exception {
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        dbf.setNamespaceAware(false);
//        Document doc = dbf.newDocumentBuilder()
//                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
//
//        Ta p = new Ta();
//        p.token = text(doc, "token");
//        p.sign  = text(doc, "sign");
//        p.generationTime = text(doc, "generationTime");
//        p.expirationTime = text(doc, "expirationTime");
//        return p;
//    }
//
//    private static String text(Document doc, String tag) {
//        NodeList nl = doc.getElementsByTagName(tag);
//        if (nl.getLength() == 0) throw new IllegalStateException("Missing tag: " + tag);
//        return nl.item(0).getTextContent();
//    }
}