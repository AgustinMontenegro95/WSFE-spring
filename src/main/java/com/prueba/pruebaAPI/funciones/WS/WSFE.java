package com.prueba.pruebaAPI.funciones.WS;

import com.prueba.pruebaAPI.dominio.Comprobante;
import com.prueba.pruebaAPI.validacion.*;
import com.prueba.pruebaAPI.funciones.WSAA.*;
import com.prueba.pruebaAPI.funciones.generacionPDF.GeneracionPDF;
import com.prueba.pruebaAPI.funciones.horarioServerAfip.HorarioAfip;
import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.json.XML;

public class WSFE {

    private String resultadoFinal;

    public WSFE() {
        this.resultadoFinal = "";
    }

    public synchronized String conexion(Comprobante informacion) {
        
        // Objetos
        Validacion validacion = new Validacion(informacion);
        FECAESolicitar feCAE = new FECAESolicitar();
        ConnectAndResponseWSAA con = new ConnectAndResponseWSAA();
        String[] resWSAA = null;
        String[] responseFeCAESolicitar = null;
        GeneracionPDF generacionPDF = new GeneracionPDF();
        String urlPublica = "";

        // Validacion de campos
        boolean resValidacion = false;
        try {
            resValidacion = validacion.validar();
        } catch (ParseException ex) {
            Logger.getLogger(WSFE.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Si supera la validacion
        if (resValidacion) {
            
            // Verificar que el campo de token y firma no esten vacios, en caso de estar vacios
            if (informacion.getAuth().getToken().isEmpty() || informacion.getAuth().getSign().isEmpty()) {
                
                // Llamar al WSAA para obtener un nuevo token y firma
                resWSAA = con.obtenerRespuesta();
                
                // Si contesta de manera exitosa
                if (resWSAA != null) {
                    
                    // Asigna los nuevos valores al comprobante y llama al WS 
                    informacion.getAuth().setToken(resWSAA[0]);
                    informacion.getAuth().setSign(resWSAA[1]);
                    responseFeCAESolicitar = feCAE.callSoapWebService(informacion);
                    
                    // Verifica si desea generar y almacenar la factura en el servidor
                    if (informacion.getGenerarPdf()) {
                        
                        if(responseFeCAESolicitar[5] != null && !responseFeCAESolicitar[5].isBlank()){
                            try {
                                generacionPDF.generarPDF(informacion, responseFeCAESolicitar);
                                urlPublica = generacionPDF.getUrlPublica();
                            } catch (IOException ex) {
                                Logger.getLogger(WSFE.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    
                    // Genera el json de respuesta
                    resultadoFinal = generarJSONRespuesta(responseFeCAESolicitar, resWSAA, urlPublica);
                    
                } else {
                    
                    /*
                        Validacion de fecha de generacion pendiente...
                    */
                    
                    validacion.agregarError("\" Error \" : \"El CEE ya posee un Ticket de Acceso valido para el WS solicitado\"");
                    if (validacion.getError().size() > 0) {
                        if (validacion.getError().size() == 1) {
                            resultadoFinal = "{ " + validacion.getError().get(0) + " }";
                        } else {
                            resultadoFinal = "{";
                            for (int i = 0; i < validacion.getError().size() - 1; i++) {
                                resultadoFinal += (validacion.getError().get(i) + ",");
                                System.out.println(resultadoFinal);
                            }
                            resultadoFinal += validacion.getError().get(validacion.getError().size() - 1);
                            resultadoFinal += "}";
                        }
                    }
                }
                
                
            } else {    // En caso de que los campos esten cargados, se verifica el tiempo de envio
                
                // Verifica que tEnvio no esta caducado
                LocalDateTime tEnvio = LocalDateTime.parse(informacion.getAuth().getTEnvio());
                LocalDateTime dateTime = LocalDateTime.now();
                System.out.println("Fecha y hora del servidor: " + dateTime);
                if (dateTime.minusHours(3).isBefore(tEnvio)) {
                    
                    // Si el tiempo de envio es correcto, llama al WS
                    responseFeCAESolicitar = feCAE.callSoapWebService(informacion);

                    // Verifica si desea generar y almacenar la factura en el servidor
                    if (informacion.getGenerarPdf()) {
                        
                        if(responseFeCAESolicitar[5] != null && !responseFeCAESolicitar[5].isBlank()){
                            try {
                                generacionPDF.generarPDF(informacion, responseFeCAESolicitar);
                                urlPublica = generacionPDF.getUrlPublica();
                            } catch (IOException ex) {
                                 Logger.getLogger(WSFE.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    
                    // Genera el json de respuesta
                    resultadoFinal = generarJSONRespuesta(responseFeCAESolicitar, resWSAA, urlPublica);
                    
                } else {    // Si la fecha de envio es incorrecta
                    
                    // Llamar al WSAA para obtener un nuevo token y firma
                    resWSAA = con.obtenerRespuesta();
                    
                    // Si contesta de manera exitosa
                    if (resWSAA != null) {
                        
                        // Asigna los nuevos valores al comprobante y llama al WS 
                        informacion.getAuth().setToken(resWSAA[0]);
                        informacion.getAuth().setSign(resWSAA[1]);
                        responseFeCAESolicitar = feCAE.callSoapWebService(informacion);
                        
                        // Verifica si desea generar y almacenar la factura en el servidor
                        if (informacion.getGenerarPdf()) {
                            
                            if(responseFeCAESolicitar[5] != null && !responseFeCAESolicitar[5].isBlank()){
                                try {
                                    generacionPDF.generarPDF(informacion, responseFeCAESolicitar);
                                    urlPublica = generacionPDF.getUrlPublica();
                                } catch (IOException ex) {
                                    Logger.getLogger(WSFE.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                        
                        // Genera el json de respuesta
                        resultadoFinal = generarJSONRespuesta(responseFeCAESolicitar, resWSAA, urlPublica);
                        
                    } else {
                        
                        /*
                            Validacion de fecha de generacion pendiente...
                        */
                        
                        validacion.agregarError("\" Error \" : \"El CEE ya posee un Ticket de Acceso valido para el WS solicitado\"");
                        if (validacion.getError().size() > 0) {
                            if (validacion.getError().size() == 1) {
                                resultadoFinal = "{ " + validacion.getError().get(0) + " }";
                            } else {
                                resultadoFinal = "{";
                                for (int i = 0; i < validacion.getError().size() - 1; i++) {
                                    resultadoFinal += (validacion.getError().get(i) + ",");
                                    System.out.println(resultadoFinal);
                                }
                                resultadoFinal += validacion.getError().get(validacion.getError().size() - 1);
                                resultadoFinal += "}";
                            }
                        }
                    }

                }
            }
        } else {
            
            /*
                Validacion de fecha de generacion pendiente...
            */
            
            if (validacion.getError().size() > 0) {
                resultadoFinal = "{";
                for (int i = 0; i < validacion.getError().size() - 1; i++) {
                    resultadoFinal += (validacion.getError().get(i) + ",");
                    System.out.println(resultadoFinal);
                }
                resultadoFinal += validacion.getError().get(validacion.getError().size() - 1);
                resultadoFinal += "}";
            }
        }
        return resultadoFinal;
    }

    public String generarJSONRespuesta(String[] responseFeCAESolicitar, String[] resWSAA, String urlPublica) {
        int PRETTY_PRINT_INDENT_FACTOR = 4;
        JSONObject xmlJSONObj = XML.toJSONObject(responseFeCAESolicitar[0]);
        String log = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);

        String result = "{"
                + "\"UrlPublica\":\"" + urlPublica + "\","
                + "\"Log\":" + log + "";
        if (resWSAA != null) {
            if (resWSAA[0] != null || !resWSAA[0].isEmpty()) {
                result += ",\"Actualizacion\": {"
                        + "\"Token\":\"" + resWSAA[0] + "\","
                        + "\"Sign\":\"" + resWSAA[1] + "\","
                        + "\"FchVto\":\"" + resWSAA[2] + "\"}}";
            }
        } else {
            result = result + '}';
        }
        System.out.println("" + result);
        return result;
    }
}
