package com.prueba.pruebaAPI.controller;

import com.prueba.pruebaAPI.dominio.Comprobante;
import com.prueba.pruebaAPI.services.ComprobanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ComprobanteController {

    @Autowired
    private ComprobanteService comprobanteService;
    private boolean ocupado = false;
    

    public ComprobanteController() {
        this.comprobanteService = new ComprobanteService();
    }
    
    @Operation(summary = "Obtiene como resultado un JSON con todos los detalles de la factura generada.")
    @Tag(name="Factura electrónica", description="Operaciones de WSFE")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json"))
    @PostMapping("/obtenerCAE")
    public ResponseEntity<?> obtenerCae(@RequestBody Comprobante comprobante) {
        
        Map<String, Object> responseBody = new HashMap<>();
        
        if(!ocupado){
            ocupado = true;
            responseBody = comprobanteService.obtenerJson(comprobante);
            ocupado = false;
        } else {
            responseBody.put("Servicio", "El servicio se encuentra ocupado, espere e intente nuevamente");
        }
        return new ResponseEntity(responseBody, HttpStatus.OK);
    }

}
