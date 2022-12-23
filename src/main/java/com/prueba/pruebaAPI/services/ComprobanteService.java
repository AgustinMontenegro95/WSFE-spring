package com.prueba.pruebaAPI.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.pruebaAPI.controller.ComprobanteController;
import com.prueba.pruebaAPI.dominio.Comprobante;
import com.prueba.pruebaAPI.funciones.WS.WSFE;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

@Service
public class ComprobanteService {
    
    private WSFE wsfe = new WSFE();
    
    public Map<String, Object> obtenerJson(Comprobante comprobante){
        
        Map<String, Object> responseBody = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        String respuesta = wsfe.conexion(comprobante);
        
        if (!respuesta.isEmpty() && !respuesta.isBlank()) {
            try {
                JsonNode nodoRespuesta = mapper.readTree(respuesta);

                if (nodoRespuesta.has("UrlPublica")) {
                    responseBody.put("UrlPublica", nodoRespuesta.get("UrlPublica"));
                    responseBody.put("Log Respuesta", nodoRespuesta.get("Log"));
                    if (nodoRespuesta.has("Actualizacion")) {
                        responseBody.put("Token", nodoRespuesta.get("Actualizacion").get("Token").asText());
                        responseBody.put("Sign", nodoRespuesta.get("Actualizacion").get("Sign").asText());
                        responseBody.put("FchVto", nodoRespuesta.get("Actualizacion").get("FchVto").asText());
                    }
                } else {
                    responseBody.put("Error", "Generacion de la factura fallida");
                    responseBody.put("Errores", respuesta);
                }

            } catch (JsonProcessingException ex) {
                Logger.getLogger(ComprobanteController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        
        return responseBody;
    }
    
    
}
