package com.prueba.pruebaAPI.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prueba.pruebaAPI.dominio.Comprobante;
import com.prueba.pruebaAPI.services.ComprobanteService;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/obtenerCAE")
public class ComprobanteController {

    private ComprobanteService comprobanteService;

    public ComprobanteController() {
        this.comprobanteService = new ComprobanteService();
    }

    @GetMapping("/Comprobante")
    public ResponseEntity<?> obtenerCae(@RequestBody Comprobante comprobante) {
        Map<String, Object> responseBody = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        String resultado = null, fchProceso = null, obsCode = null, obsMsg = null, cae = null, caeVto = null, urlPublica = null, log = null;

        try {
            JsonNode nodoRespuesta = mapper.readTree(comprobanteService.obtenerJson(comprobante));

            System.out.println("Campos: " + nodoRespuesta.get("Log"));
            resultado = nodoRespuesta.get("FeCAESolicitar").get("Resultado").asText();
            fchProceso = nodoRespuesta.get("FeCAESolicitar").get("FchProceso").asText();
            obsCode = nodoRespuesta.get("FeCAESolicitar").get("Obs-Code").asText();
            obsMsg = nodoRespuesta.get("FeCAESolicitar").get("Obs-Msg").asText();
            cae = nodoRespuesta.get("FeCAESolicitar").get("CAE").asText();
            caeVto = nodoRespuesta.get("FeCAESolicitar").get("CAEFchVto").asText();
            urlPublica = "" + nodoRespuesta.get("UrlPublica");

            responseBody.put("Resultado", resultado);
            responseBody.put("FchProceso", fchProceso);
            responseBody.put("Obs-Code", obsCode);
            responseBody.put("Obs-Msg", obsMsg);
            responseBody.put("CAE", cae);
            responseBody.put("CAEFchVto", caeVto);
            responseBody.put("UrlPublica", urlPublica);
            responseBody.put("Log Consulta", comprobante);
            responseBody.put("Log Respuesta", nodoRespuesta.get("Log"));

        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        return (new ResponseEntity<>(responseBody, HttpStatus.OK));
    }
}
