package com.prueba.pruebaAPI.funciones.WSAA;

import java.io.*;
import java.util.Properties;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public class ConnectAndResponseWSAA {

    /* Conexion y respuesta del WSAA
    Parametros de entrada: ****
    Parametro de salida: TokenAndSign (String) = Vector en el cual se ubican el token y la firma obtenida */
    public String[] obtenerRespuesta() {

        String[] TokenAndSign = new String[3];
        RequestWSAA inicio = new RequestWSAA();
        Properties prop = new Properties();
        String respuesta = null;

        System.setProperty("https.proxyHost", "");
        System.setProperty("https.proxyPort", "8080");

        // Propiedades de red
        System.setProperty("https.proxyHost", prop.getProperty("https_proxy", ""));
        System.setProperty("https.proxyPort", prop.getProperty("https_proxy_port", "8080"));
        System.setProperty("https.proxyUser", prop.getProperty("https_proxy_user", ""));
        System.setProperty("https.proxyPassword", prop.getProperty("http_proxy_password", ""));
        System.setProperty("java.net.useSystemProxies", "true");

        // Obtiene la respuesta utilizando el metodo para llamar al WSAA
        try {
            respuesta = inicio.llamarWSAA();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Lectura del mensaje de respuesta
        if (respuesta != null) {
            try {
                System.out.println(respuesta);
                Reader tokenReader = new StringReader(respuesta);
                Document tokenDoc = new SAXReader(false).read(tokenReader);

                // Obtencion del token y firma desde el documento de respuesta 
                String token = tokenDoc.valueOf("/loginTicketResponse/credentials/token");
                String sign = tokenDoc.valueOf("/loginTicketResponse/credentials/sign");
                String duration = tokenDoc.valueOf("/loginTicketResponse/header/expirationTime");

                System.out.println("\n\nTOKEN: " + token);
                System.out.println("\nSIGN: " + sign);

                TokenAndSign[0] = token;
                TokenAndSign[1] = sign;
                TokenAndSign[2] = duration;

            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            return null;
        }

        return TokenAndSign;
    }
}
