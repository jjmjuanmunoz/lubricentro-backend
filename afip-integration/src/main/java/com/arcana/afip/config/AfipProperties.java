//package com.arcana.afip.config;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//@ConfigurationProperties(prefix = "afip")
//public class AfipProperties {
//
//    private long cuit;
//    private boolean homologation;
//
//    private Wsaa wsaa = new Wsaa();
//    private Wsfe wsfe = new Wsfe();
//
//    public long getCuit() { return cuit; }
//    public void setCuit(long cuit) { this.cuit = cuit; }
//
//    public boolean isHomologation() { return homologation; }
//    public void setHomologation(boolean homologation) { this.homologation = homologation; }
//
//    public Wsaa getWsaa() { return wsaa; }
//    public Wsfe getWsfe() { return wsfe; }
//
//    public static class Wsaa {
//        private String p12Path;        // ruta absoluta o classpath:
//        private String p12Password;
//        private String service = "wsfe"; // “dest” del LTR
//        private String endpoint;       // si no se setea, se decide por homologation
//
//        public String getP12Path() { return p12Path; }
//        public void setP12Path(String p12Path) { this.p12Path = p12Path; }
//
//        public String getP12Password() { return p12Password; }
//        public void setP12Password(String p12Password) { this.p12Password = p12Password; }
//
//        public String getService() { return service; }
//        public void setService(String service) { this.service = service; }
//
//        public String getEndpoint() { return endpoint; }
//        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
//    }
//
//    public static class Wsfe {
//        private String endpoint;    // idem: se puede inferir por homologation
//        private int timeoutMs = 20000;
//
//        public String getEndpoint() { return endpoint; }
//        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
//        public int getTimeoutMs() { return timeoutMs; }
//        public void setTimeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; }
//    }
//}