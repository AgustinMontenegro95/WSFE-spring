package com.prueba.pruebaAPI.funciones.horarioServerAfip;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class HorarioAfip {

    //static private final Logger LOGGER = Logger.getLogger("mx.hash.ejemplontp.NTPService");

    
    /* - Modulo para obtencion de horario del servidor de afip -
    Parametros de entrada: ****
    Parametro de salida: fechaRecibida.getTime (Long) = Fecha otorgada por el servidor de Afip en formato de milisegundos */
    public Long getNTPDate() {
        
        // Direccion del servidor de hora de la afip
        String[] hosts = new String[]{"time.afip.gov.ar"};
        Date fechaRecibida;
        NTPUDPClient cliente = new NTPUDPClient();
        cliente.setDefaultTimeout(5000);
        for (String host : hosts) {
            try {
                //LOGGER.log(Level.INFO, "Obteniendo fecha desde: {0}", host);
                InetAddress hostAddr = InetAddress.getByName(host);
                TimeInfo fecha = cliente.getTime(hostAddr);
                fechaRecibida = new Date(fecha.getMessage().getTransmitTimeStamp().getTime());
                return fechaRecibida.getTime();
                
            } catch (IOException e) {
                //LOGGER.log(Level.SEVERE, "NO SE PUDO CONECTAR AL SERVIDOR {0}", host);
                //LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        
        //LOGGER.log(Level.WARNING, "No se pudo conectar con servidor, regresando hora local");
        cliente.close();
        return new Date().getTime();
    }
}
