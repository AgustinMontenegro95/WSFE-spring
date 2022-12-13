package com.prueba.pruebaAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.pruebaAPI.dominio.Comprobante;
import com.prueba.pruebaAPI.services.ComprobanteService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/obtenerCAE")
public class ComprobanteController {

    @Autowired
    private ComprobanteService comprobanteService;

    public ComprobanteController() {
        this.comprobanteService = new ComprobanteService();
    }
    
    @GetMapping("/getCAE")
    public ResponseEntity<?> obtenerCae(@RequestBody Comprobante comprobante) {
        Map<String, Object> responseBody = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        String respuesta = comprobanteService.obtenerJson(comprobante);

        if (!respuesta.isEmpty() && !respuesta.isBlank()) {
            try {
                JsonNode nodoRespuesta = mapper.readTree(respuesta);

                if (nodoRespuesta.has("FeCAESolicitar")) {
                    responseBody.put("Resultado", nodoRespuesta.get("FeCAESolicitar").get("Resultado").asText());
                    responseBody.put("UrlPublica", nodoRespuesta.get("UrlPublica"));
                    responseBody.put("Log Consulta", comprobante.getAuth());
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

        return (new ResponseEntity<>(responseBody, HttpStatus.OK));
    }

}
