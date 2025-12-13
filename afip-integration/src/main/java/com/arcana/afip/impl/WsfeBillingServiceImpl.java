package com.arcana.afip.impl;

//import ar.com.afip.wsfev1.AlicIva;
//import ar.com.afip.wsfev1.ArrayOfAlicIva;
//import ar.com.afip.wsfev1.CbteAsoc;
//import ar.com.afip.wsfev1.FEAuthRequest;
//import ar.com.afip.wsfev1.FECAECabRequest;
//import ar.com.afip.wsfev1.FECAEDetRequest;
//import ar.com.afip.wsfev1.FECAESolicitar;
//import ar.com.afip.wsfev1.FECAESolicitarResponse;
//import ar.com.afip.wsfev1.FECompUltimoAutorizado;
//import ar.com.afip.wsfev1.FECompUltimoAutorizadoResponse;
//import ar.com.afip.wsfev1.ServiceSoap;
//import com.arcana.afip.api.AfipAuthService;
//import com.arcana.afip.api.WsfeBillingService;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import org.springframework.stereotype.Service;

//@Service
public class WsfeBillingServiceImpl {//implements WsfeBillingService {

//    private final AfipAuthService auth;
//    private final ServiceSoap wsfe;
//
//    public WsfeBillingServiceImpl(AfipAuthService auth, ServiceSoap wsfePort) {
//        this.auth = auth;
//        this.wsfe = wsfePort;
//    }
//
//    @Override
//    public int getLastAuthorizedNumber(int ptoVta, int cbteTipo) {
//        var ta = auth.getValidTa();
//
//        FEAuthRequest a = new FEAuthRequest();
//        a.setCuit(ta.cuit());
//        a.setToken(ta.token());
//        a.setSign(ta.sign());
//
//        FECompUltimoAutorizado req = new FECompUltimoAutorizado();
//        req.setAuth(a);
//        req.setPtoVta(ptoVta);
//        req.setCbteTipo(cbteTipo);
//
//        FECompUltimoAutorizadoResponse resp = wsfe.feCompUltimoAutorizado(req);
//        return resp.getFECompUltimoAutorizadoResult().getCbteNro();
//    }
//
//    @Override
//    public CaeResponse requestCAE(SimpleInvoice inv) {
//        var ta = auth.getValidTa();
//
//        FEAuthRequest a = new FEAuthRequest();
//        a.setCuit(ta.cuit());
//        a.setToken(ta.token());
//        a.setSign(ta.sign());
//
//        FECAECabRequest cab = new FECAECabRequest();
//        cab.setCantReg(1);
//        cab.setCbteTipo(inv.cbteTipo());
//        cab.setPtoVta(inv.ptoVta());
//
//        FECAEDetRequest det = new FECAEDetRequest();
//        det.setConcepto(inv.concepto());
//        det.setDocTipo(inv.docTipo());
//        det.setDocNro(inv.docNro());
//        det.setCbteDesde(inv.cbteNro());
//        det.setCbteHasta(inv.cbteNro());
//        det.setCbteFch(inv.cbteFch().format(DateTimeFormatter.BASIC_ISO_DATE)); // yyyyMMdd
//
//        det.setImpNeto(inv.impNeto());
//        det.setImpIVA(inv.impIva());
//        det.setImpTotal(inv.impTotal());
//        det.setMonId("PES");
//        det.setMonCotiz(1.0);
//
//        // IVA
//        AlicIva alic = new AlicIva();
//        alic.setId(inv.ivaCodigo());            // 3=0%, 4=10.5%, 5=21%, etc.
//        alic.setBaseImp(inv.ivaBaseImp());      // base
//        alic.setImporte(inv.impIva());          // importe
//        ArrayOfAlicIva ivArray = new ArrayOfAlicIva();
//        ivArray.getAlicIva().add(alic);
//        det.setIva(ivArray);
//
//        // (Opcional) comprobantes asociados, tributos, etc.
//        // det.setCbtesAsoc(new ArrayOfCbteAsoc() {{ getCbteAsoc().add(new CbteAsoc()); }});
//
//        FECAESolicitar body = new FECAESolicitar();
//        body.setAuth(a);
//        body.setFeCAEReq(new ar.com.afip.wsfev1.FECAERequest() {{
//            setFeCabReq(cab);
//            getFeDetReq().add(det);
//        }});
//
//        FECAESolicitarResponse resp = wsfe.fecaeSolicitar(body);
//        var result = resp.getFECAESolicitarResult();
//
//        if (result.getErrors() != null && !result.getErrors().getErr().isEmpty()) {
//            var err = result.getErrors().getErr().get(0);
//            throw new IllegalStateException("WSFE error " + err.getCode() + ": " + err.getMsg());
//        }
//        if (result.getFeDetResp().isEmpty()) {
//            throw new IllegalStateException("WSFE empty detail response");
//        }
//
//        var detResp = result.getFeDetResp().get(0);
//        String cae = detResp.getCAE();
//        LocalDate vto = LocalDate.parse(detResp.getCAEFchVto(), DateTimeFormatter.BASIC_ISO_DATE);
//        int nro = detResp.getCbteDesde();
//
//        return new CaeResponse(cae, vto, nro);
//    }
}