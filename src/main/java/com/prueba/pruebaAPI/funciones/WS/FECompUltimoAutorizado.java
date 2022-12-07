package com.prueba.pruebaAPI.funciones.WS;

import com.prueba.pruebaAPI.dominio.*;
import jakarta.xml.soap.*;

public class FECompUltimoAutorizado {

    String soapEndpointUrl = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";
    String soapAction = "http://ar.gov.afip.dif.FEV1/FECompUltimoAutorizado";

    //Paso 3
    public void createSoapEnvelope(SOAPMessage soapMessage, Comprobante informacion) throws SOAPException {
        SOAPPart soapPart = soapMessage.getSOAPPart();

        String myNamespace = "ar";
        String myNamespaceURI = "http://ar.gov.afip.dif.FEV1/";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.setPrefix("soap");
        envelope.getHeader().setPrefix("soap");
        envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);
        envelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        soapBody.setPrefix("soap");

        //FeCAESolicitar
        SOAPElement feCompUltimoAutorizado = soapBody.addChildElement("FECompUltimoAutorizado", myNamespace);

        //Auth
        SOAPElement auth = feCompUltimoAutorizado.addChildElement("Auth", myNamespace);

        // Token
        SOAPElement auth_token = auth.addChildElement("Token", myNamespace);
        auth_token.addTextNode(informacion.getAuth().getToken());

        // Sign
        SOAPElement auth_sign = auth.addChildElement("Sign", myNamespace);
        auth_sign.addTextNode(informacion.getAuth().getSign());

        // Cuit
        SOAPElement auth_cuit = auth.addChildElement("Cuit", myNamespace);
        auth_cuit.setTextContent("20126954167");

        // PtoVenta    
        SOAPElement ptoVta = feCompUltimoAutorizado.addChildElement("PtoVta", myNamespace);
        ptoVta.setTextContent("" + informacion.getFeCabReq().getPtoVta());

        // FeCaeReq    
        SOAPElement cbteTipo = feCompUltimoAutorizado.addChildElement("CbteTipo", myNamespace);
        cbteTipo.setTextContent("" + informacion.getFeCabReq().getCbteTipo());

    }

    //Paso 1
    public int callSoapWebService(Comprobante informacion) {
        
        int valor = 0;
        
        try {
            // Create SOAP Connection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction, informacion), soapEndpointUrl);
            System.out.println(informacion.getFeCabReq().getCbteTipo());
            String eliminacion = ""+informacion.getFeCabReq().getCbteTipo() + "" + informacion.getFeCabReq().getPtoVta();
            valor = Integer.parseInt(soapResponse.getSOAPBody().getTextContent().substring(eliminacion.length(), soapResponse.getSOAPBody().getTextContent().length()));
            
            System.out.println("Response SOAP Message:");
            soapResponse.writeTo(System.out);
            System.out.println();
            System.out.println("Valor esperado: " + (valor+1));
            
            soapConnection.close();
            return valor+1;

        } catch (Exception e) {
            System.err.println("\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
            e.printStackTrace();
        }
        
        return valor;
    }

    //Paso 2
    public SOAPMessage createSOAPRequest(String soapAction, Comprobante informacion) throws Exception {
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

/* Componentes de una aplicacion Android
 1 - Vistas: 
 2 - Layout
 3 - Activity
 4 - Fragment
 5 - Intencion
 6 - Receptor de anuncios broadcast
 7 - Administrador de contenido
*/