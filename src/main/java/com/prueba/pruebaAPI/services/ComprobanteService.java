package com.prueba.pruebaAPI.services;

import com.prueba.pruebaAPI.dominio.Comprobante;
import com.prueba.pruebaAPI.funciones.WS.WSFE;
import org.springframework.stereotype.Service;

@Service
public class ComprobanteService {
    
    private WSFE wsfe;
    
    public String obtenerJson(Comprobante comprobante){
        String respuesta;
        wsfe = new WSFE();
        System.out.println(comprobante.getCliente().getApNoRaSo());
        respuesta = wsfe.conexion(comprobante);
        return respuesta;
    }
    
    
}
