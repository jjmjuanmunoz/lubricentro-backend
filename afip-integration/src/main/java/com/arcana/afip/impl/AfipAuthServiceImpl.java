//package com.arcana.afip.impl;
//
////import ar.com.afip.wsaa.LoginCMS;
////import ar.com.afip.wsaa.LoginCmsResponse;
//import com.arcana.afip.api.AfipAuthService;
//import com.arcana.afip.config.AfipProperties;
//import com.arcana.afip.pki.LtrSigner;
//import java.time.OffsetDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.concurrent.atomic.AtomicReference;
////import org.springframework.stereotype.Service;
//
////@Service
//public class AfipAuthServiceImpl implements AfipAuthService {
////
////    private final AfipProperties props;
////    //private final LoginCMS wsaa;
////
////    private final AtomicReference<Ta> cache = new AtomicReference<>();
////
////    public AfipAuthServiceImpl(AfipProperties props, LoginCMS wsaaPort) {
////        this.props = props;
////        this.wsaa = wsaaPort;
////    }
////
//    @Override
//    public Ta getValidTa() {
////        Ta current = cache.get();
////        if (current != null && current.expiration().isAfter(OffsetDateTime.now().plusMinutes(5))) {
////            return current;
////        }
////        // Build & sign LTR
////        String ltrXml = LtrSigner.buildLoginTicketRequestXml(props.getWsaa().getService(), props.getCuit());
////        String cmsB64 = LtrSigner.signCmsBase64(props.getWsaa().getP12Path(), props.getWsaa().getP12Password(), ltrXml);
////
////        LoginCmsResponse resp = wsaa.loginCms(cmsB64);
////        // El TA viene como XML en resp.getLoginCmsReturn()
////        Ta ta = parseTa(resp.getLoginCmsReturn());
////        cache.set(ta);
//        return null;
//    }
////
////    private Ta parseTa(String taXml) {
////        // Parse rápido (simple) de campos clave; si preferís, usá JAXB.
////        // Buscamos <token>, <sign> y <expirationTime>
////        String token = extract(taXml, "<token>", "</token>");
////        String sign = extract(taXml, "<sign>", "</sign>");
////        String exp = extract(taXml, "<expirationTime>", "</expirationTime>");
////        OffsetDateTime expiration = OffsetDateTime.parse(exp, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
////        return new Ta(token, sign, props.getCuit(), expiration);
////    }
////
////    private static String extract(String xml, String start, String end) {
////        int i = xml.indexOf(start);
////        int j = xml.indexOf(end);
////        if (i < 0 || j < 0 || j <= i) throw new IllegalStateException("TA XML missing " + start + " or " + end);
////        return xml.substring(i + start.length(), j).trim();
////    }
//}