//package com.arcana.afip.pki;
//
//import java.io.FileInputStream;
//import java.nio.charset.StandardCharsets;
//import java.security.KeyStore;
//import java.security.PrivateKey;
//import java.security.cert.X509Certificate;
//import java.time.OffsetDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Base64;
//import org.bouncycastle.cms.CMSProcessableByteArray;
//import org.bouncycastle.cms.CMSSignedData;
//import org.bouncycastle.cms.CMSSignedDataGenerator;
//import org.bouncycastle.cms.SignerInfoGenerator;
//import org.bouncycastle.operator.ContentSigner;
//import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
//import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
//import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
//import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
//
//public final class LtrSigner {
//
//    private LtrSigner() {}
//
//    public static String buildLoginTicketRequestXml(String service, long cuit) {
//        // WSAA suele tolerar offsets de minutos; mantenemos ahora +- 5 min de margen si querés.
//        OffsetDateTime now = OffsetDateTime.now();
//        OffsetDateTime until = now.plusHours(12);
//        String uniqueId = String.valueOf(System.currentTimeMillis() / 1000);
//
//        // Formato de fecha: yyyy-MM-dd'T'HH:mm:ssXXX (ISO-8601)
//        DateTimeFormatter fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
//
//        return """
//               <loginTicketRequest version="1.0">
//                 <header>
//                   <uniqueId>%s</uniqueId>
//                   <generationTime>%s</generationTime>
//                   <expirationTime>%s</expirationTime>
//                 </header>
//                 <service>%s</service>
//                 <source>%d</source>
//               </loginTicketRequest>
//               """.formatted(uniqueId, fmt.format(now.minusMinutes(5)), fmt.format(until), service, cuit);
//    }
//
//    public static String signCmsBase64(String p12Path, String p12Password, String xml) {
//        try (FileInputStream fis = new FileInputStream(p12Path)) {
//            KeyStore ks = KeyStore.getInstance("PKCS12");
//            ks.load(fis, p12Password.toCharArray());
//
//            String alias = ks.aliases().nextElement();
//            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, p12Password.toCharArray());
//            X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
//
//            ContentSigner sha256withRSA = new JcaContentSignerBuilder("SHA256withRSA")
//                    .build(privateKey);
//            SignerInfoGenerator sigInfoGen = new JcaSignerInfoGeneratorBuilder(
//                    new JcaDigestCalculatorProviderBuilder().build())
//                    .build(sha256withRSA, new JcaX509CertificateHolder(cert));
//
//            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
//            gen.addSignerInfoGenerator(sigInfoGen);
//            gen.addCertificate(new JcaX509CertificateHolder(cert));
//
//            byte[] data = xml.getBytes(StandardCharsets.UTF_8);
//            CMSSignedData signed = gen.generate(new CMSProcessableByteArray(data), true);
//            return Base64.getEncoder().encodeToString(signed.getEncoded());
//        } catch (Exception e) {
//            throw new IllegalStateException("Error signing LTR CMS: " + e.getMessage(), e);
//        }
//    }
//}