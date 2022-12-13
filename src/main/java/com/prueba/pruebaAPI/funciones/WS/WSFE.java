package com.prueba.pruebaAPI.funciones.WS;

import com.prueba.pruebaAPI.dominio.Comprobante;
import com.prueba.pruebaAPI.validacion.*;
import com.prueba.pruebaAPI.funciones.WSAA.*;
import com.prueba.pruebaAPI.funciones.generacionPDF.GeneracionPDF;
import com.prueba.pruebaAPI.funciones.horarioServerAfip.HorarioAfip;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;

public class WSFE {

    private String resultadoFinal;

    @Autowired
    GeneracionPDF generacionPDF;

    public WSFE() {
        this.resultadoFinal = "";
    }

    public synchronized String conexion(Comprobante informacion) {
        Validacion validacion = new Validacion(informacion);
        FECAESolicitar feCAE = new FECAESolicitar();
        ConnectAndResponseWSAA con = new ConnectAndResponseWSAA();
        String[] resWSAA = null;
        String[] responseFeCAESolicitar;
        GeneracionPDF generacionPDF = new GeneracionPDF();
        String urlPublica = "";

        boolean resValidacion = false;
        try {
            resValidacion = validacion.validar();
        } catch (ParseException ex) {
            Logger.getLogger(WSFE.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (resValidacion) {
            System.out.println("validacion true");
            //verificar token and sign
            if (informacion.getAuth().getToken().isEmpty() || informacion.getAuth().getSign().isEmpty()) {
                System.out.println("Token y sign vacios");
                //llamar al wsaa
                resWSAA = con.obtenerRespuesta();
                if (resWSAA != null) {
                    //resWSAA[3] exptime tiene diferente formato -> 2002­01­01T00:00:02­03:00
                    //armar json response (agregar token, sign y exptime)
                    informacion.getAuth().setToken(resWSAA[0]);
                    System.out.println("getToken: " + informacion.getAuth().getToken());
                    informacion.getAuth().setSign(resWSAA[1]);
                    System.out.println("getSign: " + informacion.getAuth().getSign());
                    responseFeCAESolicitar = feCAE.callSoapWebService(informacion);
                    if (informacion.getGenerarPdf()) {
                        try {
                            generacionPDF.generarPDF(informacion, responseFeCAESolicitar);
                            //generacionPDF.generarPDF(informacion, responseFeCAESolicitar);
                            urlPublica = generacionPDF.getUrlPublica();
                        } catch (IOException ex) {
                            Logger.getLogger(WSFE.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    //generar respuesta json
                    resultadoFinal = generarJSONRespuesta(responseFeCAESolicitar, resWSAA, urlPublica);
                } else {
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
            } else {
                //verificar que tEnvio no esta caducado
                LocalDateTime tEnvio = LocalDateTime.parse(informacion.getAuth().getTEnvio());
                LocalDateTime dateTime = LocalDateTime.now();
                if (dateTime.plusHours(2).isBefore(tEnvio)) {
                    responseFeCAESolicitar = feCAE.callSoapWebService(informacion);
                    System.out.println("Tiempo correcto");
                    System.out.println("Solicita PDF: " + informacion.getGenerarPdf());
                    if (informacion.getGenerarPdf()) {
                        try {
                            generacionPDF.generarPDF(informacion, responseFeCAESolicitar);
                            urlPublica = generacionPDF.getUrlPublica();
                        } catch (IOException ex) {
                            Logger.getLogger(WSFE.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    resultadoFinal = generarJSONRespuesta(responseFeCAESolicitar, resWSAA, urlPublica);
                } else {
                    //llamar al wsaa
                    resWSAA = con.obtenerRespuesta();
                    if (resWSAA != null) {
                        //resWSAA[3] exptime tiene diferente formato
                        //armar json response (agregar token, sign y exptime)
                        informacion.getAuth().setToken(resWSAA[0]);
                        informacion.getAuth().setSign(resWSAA[1]);
                        responseFeCAESolicitar = feCAE.callSoapWebService(informacion);
                        System.out.println("Solicita PDF: " + informacion.getGenerarPdf());
                        if (informacion.getGenerarPdf()) {
                            try {
                                generacionPDF.generarPDF(informacion, responseFeCAESolicitar);
                                urlPublica = generacionPDF.getUrlPublica();
                            } catch (IOException ex) {
                                Logger.getLogger(WSFE.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        //generar respuesta json
                        resultadoFinal = generarJSONRespuesta(responseFeCAESolicitar, resWSAA, urlPublica);
                        System.out.println("Tiempo incorrecto");
                    } else {
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

        String result = "{\"FeCAESolicitar\": {"
                + "\"Resultado\":\"" + responseFeCAESolicitar[1] + "\","
                + "\"FchProceso\":\"" + responseFeCAESolicitar[2] + "\","
                + "\"Obs-Code\":\"" + responseFeCAESolicitar[3] + "\","
                + "\"Obs-Msg\":\"" + responseFeCAESolicitar[4] + "\","
                + "\"CAE\":\"" + responseFeCAESolicitar[5] + "\","
                + "\"CAEFchVto\":\"" + responseFeCAESolicitar[6] + "\"},"
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
            result += "}";
        }
        System.out.println("" + result);
        return result;
    }
}
