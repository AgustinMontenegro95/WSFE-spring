package com.prueba.pruebaAPI.controller;

import com.prueba.pruebaAPI.dominio.Comprobante;
import com.prueba.pruebaAPI.services.ComprobanteService;
import java.util.*;
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
        
        responseBody.put(comprobanteService.obtenerJson(comprobante), false);
        return (new ResponseEntity<>(responseBody, HttpStatus.OK));
    }
    
    
}
