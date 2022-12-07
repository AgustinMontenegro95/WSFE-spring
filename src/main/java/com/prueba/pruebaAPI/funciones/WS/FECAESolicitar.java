package com.prueba.pruebaAPI.funciones.WS;

import com.prueba.pruebaAPI.dominio.*;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;
import jakarta.xml.soap.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class FECAESolicitar {

    String soapEndpointUrl = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";
    String soapAction = "http://ar.gov.afip.dif.FEV1/FECAESolicitar";

    private static void createSoapEnvelope(SOAPMessage soapMessage, Comprobante informacion) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();
        FECompUltimoAutorizado cbteServ = new FECompUltimoAutorizado();
        int cbteValido = cbteServ.callSoapWebService(informacion);

        String myNamespace = "ar";
        String myNamespaceURI = "http://ar.gov.afip.dif.FEV1/";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.setPrefix("soap");
        envelope.getHeader().setPrefix("soap");
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");

        /*
            Constructed SOAP Request Message:
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:myNamespace="http://www.webserviceX.NET">
                <SOAP-ENV:Header/>
                <SOAP-ENV:Body>
                    <myNamespace:GetInfoByCity>
                        <myNamespace:USCity>New York</myNamespace:USCity>
                    </myNamespace:GetInfoByCity>
                </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>
         */
        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        soapBody.setPrefix("soap");

        //FeCAESolicitar
        SOAPElement feCaeSolicitar = soapBody.addChildElement("FECAESolicitar", myNamespace);

        //Auth
        SOAPElement auth = feCaeSolicitar.addChildElement("Auth", myNamespace);

        // Token
        SOAPElement auth_token = auth.addChildElement("Token", myNamespace);
        auth_token.addTextNode(informacion.getAuth().getToken());

        // Sign
        SOAPElement auth_sign = auth.addChildElement("Sign", myNamespace);
        auth_sign.addTextNode(informacion.getAuth().getSign());

        // Cuit
        SOAPElement auth_cuit = auth.addChildElement("Cuit", myNamespace);
        auth_cuit.setTextContent("20126954167");

        // FeCaeReq    
        SOAPElement feCaeReq = feCaeSolicitar.addChildElement("FeCAEReq", myNamespace);

        // FeCabReq
        SOAPElement feCabReq = feCaeReq.addChildElement("FeCabReq", myNamespace);

        // CantReg
        SOAPElement cab_cantReg = feCabReq.addChildElement("CantReg", myNamespace);
        cab_cantReg.setTextContent("" + informacion.getFeCabReq().getCantReg());

        // CantReg
        SOAPElement cab_ptoVenta = feCabReq.addChildElement("PtoVta", myNamespace);
        cab_ptoVenta.setTextContent("" + informacion.getFeCabReq().getPtoVta());

        // CantReg
        SOAPElement cab_cbteTipo = feCabReq.addChildElement("CbteTipo", myNamespace);
        cab_cbteTipo.setTextContent("" + informacion.getFeCabReq().getCbteTipo());

        // FeDetReq
        SOAPElement feDetReq = feCaeReq.addChildElement("FeDetReq", myNamespace);

        // FeDetReq
        SOAPElement feCaeDetRequest = feDetReq.addChildElement("FECAEDetRequest", myNamespace);

        // Concepto
        SOAPElement det_Concepto = feCaeDetRequest.addChildElement("Concepto", myNamespace);
        det_Concepto.setTextContent("" + informacion.getFeDetReq().getConcepto());

        // Doc Tipo
        SOAPElement det_docTipo = feCaeDetRequest.addChildElement("DocTipo", myNamespace);
        det_docTipo.setTextContent("" + informacion.getFeDetReq().getDocTipo());

        // Doc Nro
        SOAPElement det_docNro = feCaeDetRequest.addChildElement("DocNro", myNamespace);
        det_docNro.setTextContent("" + informacion.getFeDetReq().getDocNro());

        // Cbte Desde
        SOAPElement det_cbteDesde = feCaeDetRequest.addChildElement("CbteDesde", myNamespace);
        det_cbteDesde.setTextContent("" + cbteValido);

        // Cbte Hasta
        SOAPElement det_cbteHasta = feCaeDetRequest.addChildElement("CbteHasta", myNamespace);
        det_cbteHasta.setTextContent("" + cbteValido);

        // Cbte Fecha
        SOAPElement det_cbteFch = feCaeDetRequest.addChildElement("CbteFch", myNamespace);
        det_cbteFch.setTextContent("" + informacion.getFeDetReq().getCbteFch());

        // Imp Total
        SOAPElement det_impTotal = feCaeDetRequest.addChildElement("ImpTotal", myNamespace);
        det_impTotal.setTextContent("" + informacion.getFeDetReq().getImpTotal());

        // Imp Total Conc
        SOAPElement det_impTotalConc = feCaeDetRequest.addChildElement("ImpTotConc", myNamespace);
        det_impTotalConc.setTextContent("" + informacion.getFeDetReq().getImpTotConc());

        // Imp Neto
        SOAPElement det_impNeto = feCaeDetRequest.addChildElement("ImpNeto", myNamespace);
        det_impNeto.setTextContent("" + informacion.getFeDetReq().getImpNeto());

        // Imp Op Ex
        SOAPElement det_impOpEx = feCaeDetRequest.addChildElement("ImpOpEx", myNamespace);
        det_impOpEx.setTextContent("" + informacion.getFeDetReq().getImpOpEx());

        // Imp Trib
        SOAPElement det_impTrib = feCaeDetRequest.addChildElement("ImpTrib", myNamespace);
        det_impTrib.setTextContent("" + informacion.getFeDetReq().getImpTrib());

        // Imp Iva
        SOAPElement det_impIVA = feCaeDetRequest.addChildElement("ImpIVA", myNamespace);
        det_impIVA.setTextContent("" + informacion.getFeDetReq().getImpIVA());

        // MonId
        SOAPElement det_monId = feCaeDetRequest.addChildElement("MonId", myNamespace);
        det_monId.setTextContent("" + informacion.getFeDetReq().getMonID());

        // Mon Cotiz
        SOAPElement det_monCotiz = feCaeDetRequest.addChildElement("MonCotiz", myNamespace);
        det_monCotiz.setTextContent("" + informacion.getFeDetReq().getMonCotiz());

        //Verificacion de los comprobantes asociados
        if (informacion.getFeDetReq().getCbtesAsoc() != null) {
            if (informacion.getFeDetReq().getCbtesAsoc().length > 0) {

                SOAPElement cbtes = feCaeDetRequest.addChildElement("CbtesAsoc", myNamespace);
                for (CbtesAsoc cbteAsoc : informacion.getFeDetReq().getCbtesAsoc()) {

                    SOAPElement cbte = cbtes.addChildElement("CbteAsoc", myNamespace);

                    // Tipo
                    SOAPElement cbte_tipo = cbte.addChildElement("Tipo", myNamespace);
                    cbte_tipo.setTextContent("" + cbteAsoc.getTipo());

                    // PtoVenta
                    SOAPElement cbte_PtoVenta = cbte.addChildElement("PtoVta", myNamespace);
                    cbte_PtoVenta.setTextContent("" + cbteAsoc.getPtoVta());

                    // Nro
                    SOAPElement cbte_Nro = cbte.addChildElement("Nro", myNamespace);
                    cbte_Nro.setTextContent("" + cbteAsoc.getNro());
                }
            }
        }

        //Verificacion de los tributos
        if (informacion.getFeDetReq().getTributos() != null) {
            if (informacion.getFeDetReq().getTributos().length > 0) {

                SOAPElement tributos = feCaeDetRequest.addChildElement("Tributos", myNamespace);
                for (Tributo tributo : informacion.getFeDetReq().getTributos()) {

                    SOAPElement trib = tributos.addChildElement("Tributo", myNamespace);

                    // Id
                    SOAPElement trib_id = trib.addChildElement("Id", myNamespace);
                    trib_id.setTextContent("" + tributo.getID());

                    // Base Imp
                    SOAPElement trib_baseImp = trib.addChildElement("BaseImp", myNamespace);
                    trib_baseImp.setTextContent("" + tributo.getBaseImp());

                    // Alic
                    SOAPElement trib_alic = trib.addChildElement("Alic", myNamespace);
                    trib_alic.setTextContent("" + tributo.getAlic());

                    // Importe
                    SOAPElement trib_importe = trib.addChildElement("Importe", myNamespace);
                    trib_importe.setTextContent("" + tributo.getImporte());
                }
            }
        }

        //Verificacion de los iva
        if (informacion.getFeDetReq().getIva() != null) {
            if (informacion.getFeDetReq().getIva().length > 0) {

                SOAPElement iva = feCaeDetRequest.addChildElement("Iva", myNamespace);
                for (Iva ivaelem : informacion.getFeDetReq().getIva()) {

                    SOAPElement alicIva = iva.addChildElement("AlicIva", myNamespace);

                    // Tipo
                    SOAPElement iva_id = alicIva.addChildElement("Id", myNamespace);
                    iva_id.setTextContent("" + ivaelem.getID());

                    // PtoVenta
                    SOAPElement iva_baseImp = alicIva.addChildElement("BaseImp", myNamespace);
                    iva_baseImp.setTextContent("" + ivaelem.getBaseImp());

                    // Nro
                    SOAPElement iva_importe = alicIva.addChildElement("Importe", myNamespace);
                    iva_importe.setTextContent("" + ivaelem.getImporte());
                }
            }
        }

        //Verificacion de los compradores
        if (informacion.getFeDetReq().getCompradores() != null) {
            if (informacion.getFeDetReq().getCompradores().length > 0) {

                SOAPElement compradores = feCaeDetRequest.addChildElement("Compradores", myNamespace);
                for (Comprador comprador : informacion.getFeDetReq().getCompradores()) {

                    SOAPElement comp = compradores.addChildElement("Comprador", myNamespace);

                    // Doc Tipo
                    SOAPElement comp_docTipo = comp.addChildElement("DocTipo", myNamespace);
                    comp_docTipo.setTextContent("" + comprador.getDocTipo());

                    // Doc Nro
                    SOAPElement comp_nroDoc = comp.addChildElement("DocNro", myNamespace);
                    comp_nroDoc.setTextContent("" + comprador.getDocNro());

                    // Porcentaje
                    SOAPElement comp_porc = comp.addChildElement("Porcentaje", myNamespace);
                    comp_porc.setTextContent("" + comprador.getPorcentaje());
                }
            }
        }

    }

    public String[] callSoapWebService(Comprobante informacion) {
        String[] result = new String[8];
        // result[0] = XML response
        // result[1] = Resultado
        // result[2] = FchProceso
        // result[3] = Obs-code
        // result[4] = Obs-msg
        // result[5] = CAE
        // result[6] = CAEFchVto
        // result[7] = Nro Comprobante

        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction, informacion), soapEndpointUrl);

            // Print the SOAP Response
            System.out.println("Response SOAP Message:");
            soapResponse.writeTo(System.out);
            System.out.println();

            //Mensaje de respuesta FECAESolicitar
            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream();
                soapResponse.writeTo(baos);
                result[0] = baos.toString();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            soapConnection.close();

            try {
                Reader tokenReader = new StringReader(result[0]);
                Document tokenDoc = new SAXReader(false).read(tokenReader);
                System.out.println(tokenDoc.getText());
                // Obtencion del token y firma desde el documento de respuesta 
                result[1] = tokenDoc.valueOf("/soap:Envelope/soap:Body/*[name()='FECAESolicitarResponse']/*[name()='FECAESolicitarResult']/*[name()='FeDetResp']/*[name()='FECAEDetResponse']/*[name()='Resultado']");
                result[2] = tokenDoc.valueOf("/soap:Envelope/soap:Body/*[name()='FECAESolicitarResponse']/*[name()='FECAESolicitarResult']/*[name()='FeCabResp']/*[name()='FchProceso']");
                result[3] = tokenDoc.valueOf("/soap:Envelope/soap:Body/*[name()='FECAESolicitarResponse']/*[name()='FECAESolicitarResult']/*[name()='FeDetResp']/*[name()='FECAEDetResponse']/*[name()='Observaciones']/*[name()='Obs']/*[name()='Code']");
                result[4] = tokenDoc.valueOf("/soap:Envelope/soap:Body/*[name()='FECAESolicitarResponse']/*[name()='FECAESolicitarResult']/*[name()='FeDetResp']/*[name()='FECAEDetResponse']/*[name()='Observaciones']/*[name()='Obs']/*[name()='Msg']");
                result[5] = tokenDoc.valueOf("/soap:Envelope/soap:Body/*[name()='FECAESolicitarResponse']/*[name()='FECAESolicitarResult']/*[name()='FeDetResp']/*[name()='FECAEDetResponse']/*[name()='CAE']");
                result[6] = tokenDoc.valueOf("/soap:Envelope/soap:Body/*[name()='FECAESolicitarResponse']/*[name()='FECAESolicitarResult']/*[name()='FeDetResp']/*[name()='FECAEDetResponse']/*[name()='CAEFchVto']");
                result[7] = tokenDoc.valueOf("/soap:Envelope/soap:Body/*[name()='FECAESolicitarResponse']/*[name()='FECAESolicitarResult']/*[name()='FeDetResp']/*[name()='FECAEDetResponse']/*[name()='CbteDesde']");
                
            } catch (DocumentException e) {
                System.out.println(e);
            }

        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
        return result;
    }


    private static SOAPMessage createSOAPRequest(String soapAction, Comprobante informacion) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage soapMessage = messageFactory.createMessage();

        createSoapEnvelope(soapMessage, informacion);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        soapMessage.saveChanges();

        /* Print the request message, just for debugging purposes */
        System.out.println("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println("\n");

        return soapMessage;
    }

}
