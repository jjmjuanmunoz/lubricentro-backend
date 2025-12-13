//package com.arcana.afip.config;
//
//import ar.com.afip.wsaa.LoginCMS;
//import ar.com.afip.wsaa.LoginCMSService;
//import ar.com.afip.wsfev1.Service;
//import ar.com.afip.wsfev1.ServiceSoap;
//import java.util.concurrent.TimeUnit;
//import org.apache.cxf.ext.logging.LoggingFeature;
//import org.apache.cxf.frontend.ClientProxy;
//import org.apache.cxf.transport.http.HTTPConduit;
//import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableConfigurationProperties(AfipProperties.class)
//public class AfipClientsConfig {
//
//    @Bean
//    public LoginCMS wsaaPort(AfipProperties props) {
//        final String defaultEndpoint = props.isHomologation()
//                ? "https://wsaahomo.afip.gov.ar/ws/services/LoginCms"
//                : "https://wsaa.afip.gov.ar/ws/services/LoginCms";
//
//        LoginCMSService svc = new LoginCMSService();
//        LoginCMS port = svc.getLoginCms();
//        setEndpointAndTimeouts(port, props.getWsfe().getTimeoutMs(), defaultEndpoint, props.getWsaa().getEndpoint());
//        return port;
//    }
//
//    @Bean
//    public ServiceSoap wsfePort(AfipProperties props) {
//        final String defaultEndpoint = props.isHomologation()
//                ? "https://wswhomo.afip.gov.ar/wsfev1/service.asmx"
//                : "https://servicios1.afip.gov.ar/wsfev1/service.asmx";
//
//        Service svc = new Service();
//        ServiceSoap port = svc.getServiceSoap();
//        setEndpointAndTimeouts(port, props.getWsfe().getTimeoutMs(), defaultEndpoint, props.getWsfe().getEndpoint());
//        return port;
//    }
//
//    private void setEndpointAndTimeouts(Object port, int timeoutMs, String defaultEndpoint, String override) {
//        var client = ClientProxy.getClient(port);
//        String endpoint = (override != null && !override.isBlank()) ? override : defaultEndpoint;
//        client.getEndpoint().getEndpointInfo().setAddress(endpoint);
//        client.getEndpoint().getActiveFeatures().add(new LoggingFeature());
//
//        HTTPConduit conduit = (HTTPConduit) client.getConduit();
//        HTTPClientPolicy p = new HTTPClientPolicy();
//        p.setConnectionTimeout(TimeUnit.MILLISECONDS.toMillis(timeoutMs));
//        p.setReceiveTimeout(TimeUnit.MILLISECONDS.toMillis(timeoutMs));
//        p.setAllowChunking(true);
//        conduit.setClient(p);
//    }
//}