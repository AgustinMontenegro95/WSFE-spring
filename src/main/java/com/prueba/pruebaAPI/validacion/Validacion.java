package com.prueba.pruebaAPI.validacion;

import com.prueba.pruebaAPI.dominio.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Validacion {

    Comprobante informacion;
    List<String> errores = new ArrayList<>();
    
    public Validacion(Comprobante informacion){
        this.informacion = informacion;
    }

    public boolean validar() throws ParseException {
        boolean validado = true;
        
        errores = new ArrayList<>();
        
        if(!validacionAuth()){
            validado = false;
        }
        
        if(!validacionFeCabReq()){
            validado = false;
        }
        
        if(!validacionFeDetReq()){
            validado = false;
        }
        
        return validado;
    }
    
    public void agregarError(String error){
        errores.add(error);
    }

    public boolean validarFecha(String fecha) {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSSS");
            //formatoFecha.setLenient(false);
            Date fch = formatoFecha.parse(fecha);
//            if(fch.before(new Date())){
//                return false;
//            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    private boolean validacionAuth() {
        boolean result = true;
        int prueba = String.valueOf(informacion.getAuth().getCuit()).length();
        
        if (String.valueOf(informacion.getAuth().getCuit()).length() < 10 || String.valueOf(informacion.getAuth().getCuit()).length() > 11) {
            errores.add("\"Auth-Cuit\": \"El valor de Cuit no tiene la longitud requerida (10-11)\"");
            result = false;
        }

        boolean res = false;
        res = validarFecha(informacion.getAuth().getTEnvio());

        if (!res) {
            errores.add("\"Auth-tEnvio\": \"La fecha posee un formato invalido\"");
            result = false;
        }

        return result;
    }

    private boolean validacionFeCabReq() {
        boolean result = true;

        if (informacion.getFeCabReq().getCantReg() != 1) {
            errores.add("\"FeCabReq-CantReg\": \"El valor de CantReg es distinto de 1\"");
            result = false;
        }

        if (informacion.getFeCabReq().getPtoVta() < 1 || informacion.getFeCabReq().getPtoVta() > 99998 || String.valueOf(informacion.getFeCabReq().getPtoVta()) == null) {
            errores.add("\"FeCabReq-PtoVta\": \"El punto de venta tiene que ser un valor comprendido entre 1 y 99998\"");
            result = false;
        }

        if (informacion.getFeCabReq().getCbteTipo() < 1 || informacion.getFeCabReq().getCbteTipo() > 10) {
            errores.add("\"FeCabReq-CbteTipo\": \"El tipo de comprobante debe tener un valor comprendido entre 1 y 10\"");
            result = false;
        }

        return result;
    }

    private boolean validacionFeDetReq() {
        boolean result = true;

        if (informacion.getFeDetReq().getConcepto() <= 0 || informacion.getFeDetReq().getConcepto() >= 4) {
            errores.add("\"FeDetReq-Concepto\": \"El valor de concepto del comprobante debe ser 1 (productos), 2 (servicios) o 3 (productos y servicios)\"");
            result = false;
        }

        if (!String.valueOf(informacion.getFeDetReq().getDocTipo()).isEmpty()) {
            for (int contador = 25; contador <= 79; contador++) {
                if (informacion.getFeDetReq().getDocTipo() == contador) {
                    errores.add("\"FeDetReq-DocTipo\": \"El valor del código de documento identificatorio del comprador se encuentra fuera del rango válido\"");
                    result = false;
                }
            }
            for (int contador = 81; contador <= 85; contador++) {
                if (informacion.getFeDetReq().getDocTipo() == contador) {
                    errores.add("\"FeDetReq-DocTipo\": \"El valor del código de documento identificatorio del comprado se encuentra fuera del rango válido\"");
                    result = false;
                }
            }
            if (informacion.getFeDetReq().getDocTipo() == 88) {
                errores.add("\"FeDetReq-DocTipo\": \"El valor del código de documento identificatorio del comprado se encuentra fuera del rango válido\"");
                result = false;

            }
            for (int contador = 97; contador <= 98; contador++) {
                if (informacion.getFeDetReq().getDocTipo() == contador) {
                    errores.add("\"FeDetReq-DocTipo\": \"El valor del código de documento identificatorio del comprado se encuentra fuera del rango válido\"");
                    result = false;
                }
            }

        }

        if (String.valueOf(informacion.getFeDetReq().getCbteFch()).length() != 8) {
            errores.add("\"FeDetReq-CbteFch\": \"El valor de la fecha del comprobante es inválido\"");
            result = false;
        }

        if (informacion.getFeDetReq().getImpTotal() < 0) {
            errores.add("\"FeDetReq-ImpTotal\": \"El valor del importe total debe ser mayor que 0\"");
            result = false;
        }

        if (informacion.getFeDetReq().getImpTotConc() < 0) {
            errores.add("\"FeDetReq-ImpTotConc\": \"El valor del importe total debe ser mayor o igual que 0\"");
            result = false;
        }

        if (informacion.getFeDetReq().getImpNeto() < 0) {
            errores.add("\"FeDetReq-ImpNeto\": \"El valor del importe neto debe ser mayor que 0\"");
            result = false;
        }

        if (informacion.getFeDetReq().getImpOpEx() < 0) {
            errores.add("\"FeDetReq-ImpOpEx\": \"El valor del importe exento debe ser mayor o igual que 0\"");
            result = false;
        }

        if (informacion.getFeDetReq().getImpTrib() < 0) {
            errores.add("\"FeDetReq-ImpTrib\": \"El valor de la suma de los importes del array de tributos debe ser mayor o igual que 0\"");
            result = false;
        }

        if (informacion.getFeDetReq().getImpIVA() < 0) {
            errores.add("\"FeDetReq-ImpIVA\": \"El valor de la suma de los importes del array de IVA debe ser mayor que o igual que 0\"");
            result = false;
        }

        if (informacion.getFeDetReq().getMonID().length() != 3) {
            errores.add("\"FeDetReq-MonID\": \"El valor del código de moneda debe tener una longitud = 3\"");
            result = false;
        }

        if (informacion.getFeDetReq().getMonCotiz() < 1) {
            errores.add("\"FeDetReq-MonCotiz\": \"El valor de cotización de moneda informada debe ser mayor que 0\"");
            result = false;
        }

        //Comprobantes Asociados
        if (informacion.getFeDetReq().getCbtesAsoc() != null) {
            try {
                if (!validacionCbtesAsoc()) {
                    result = false;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
        //Tributos
        if (informacion.getFeDetReq().getTributos() != null) {
            try {
                if (!validacionTributos()) {
                    result = false;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
        //IVA
        if (informacion.getFeDetReq().getIva() != null) {
            try {
                if (!validacionIVA()) {
                    result = false;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
        //Compradores
        if (informacion.getFeDetReq().getCompradores() != null) {
            try {
                if (!validacionCompradores()) {
                    result = false;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
        
        if(!validacionDetalleFactura()){
            result = false;
        }
        
        if (informacion.getEmpresa() != null){
            if(!validacionEmpresa()){
                result = false;
            }
        }
        
        if(informacion.getCliente() != null){
            if(!validacionCliente()){
                result = false;
            }
        }
        
        return result;
    }

    private boolean validacionCbtesAsoc() {
        boolean result = true;

        for (CbtesAsoc cbteAsoc : informacion.getFeDetReq().getCbtesAsoc()) {
            if (cbteAsoc.getTipo() < 0) {
                errores.add("\"FeCabReq-CbtesAsoc-Tipo\": \"El valor de tipo debe ser mayor que 0\"");
                result = false;
            }
            if (cbteAsoc.getPtoVta() < 0) {
                errores.add("\"FeCabReq-CbtesAsoc-PtoVta\": \"El valor de punto de venta debe ser mayor que 0\"");
                result = false;
            }
            if (cbteAsoc.getNro() < 0) {
                errores.add("\"FeCabReq-CbtesAsoc-Nro\": \"El valor de número de comprobante asociado debe ser mayor que 0\"");
                result = false;
            }
        }
        return result;
    }

    private boolean validacionTributos() {
        boolean result = true;

        for (Tributo tributo : informacion.getFeDetReq().getTributos()) {
            if (tributo.getID() < 0) {
                errores.add("\"FeCabReq-Tributos-Id\": \"El valor de tipo debe ser mayor que 0\"");
                result = false;
            }
            if (tributo.getDesc().isEmpty() || tributo.getDesc().length() > 80) {
                errores.add("\"FeCabReq-Tributos-Desc\": \"El valor de descripción no debe tener una longitud entre 0 y 80 caracteres\"");
                result = false;
            }
            if (tributo.getBaseImp() < 0) {
                errores.add("\"FeCabReq-Tributos-BaseImp\": \"El valor de base imponible para la determinación del tributo debe ser mayor que 0\"");
                result = false;
            }
            if (tributo.getAlic() < 0) {
                errores.add("\"FeCabReq-Tributos-Alic\": \"El valor de alicuota asociado debe ser mayor que 0\"");
                result = false;
            }
            if (tributo.getImporte() < 0) {
                errores.add("\"FeCabReq-Tributos-Importe\": \"El valor del importe debe ser mayor que 0\"");
                result = false;
            }
        }
        return result;
    }

    private boolean validacionIVA() {
        boolean result = true;

        for (Iva iva : informacion.getFeDetReq().getIva()) {
            if (iva.getID() < 3 || iva.getID() == 7 || iva.getID() > 9) {
                errores.add("\"FeCabReq-Iva-Id\": \"El valor de id del IVA debe ser 3, 4, 5, 6, 8 o 9\"");
                result = false;
            }
            if (iva.getBaseImp() < 0) {
                errores.add("\"FeCabReq-Iva-BaseImp\": \"El valor de base imponible para la determinación de la alícuota debe ser mayor que 0\"");
                result = false;
            }
            if (iva.getImporte() < 0) {
                errores.add("\"FeCabReq-Iva-Ìmporte\": \"El valor del importe debe ser mayor que 0\"");
                result = false;
            }
        }
        return result;
    }
        
    private boolean validacionCompradores() {
        boolean result = true;

        for (Comprador comprador : informacion.getFeDetReq().getCompradores()) {
            if (comprador.getDocTipo() < 0) {
                errores.add("\"FeCabReq-Compradores-DocTipo\": \"El valor del tipo de documento debe ser mayor que 0");
                result = false;
            }
            if (comprador.getDocNro().isEmpty() || comprador.getDocNro().length() > 80) {
                errores.add("\"FeCabReq-Compradores-DocNro\": \"El valor del número de documento tiene que tener una longitud entre 1 y 80\"");
                result = false;
            }
            if (comprador.getPorcentaje() < 0) {
                errores.add("\"FeCabReq-Compradores-Porcentaje\": \"El valor del porcentaje de titularidad que tiene el comprador debe ser mayor que 0\"");
                result = false;
            }
        }
        return result;
    }

    private boolean validacionDetalleFactura(){
        if(informacion.getDetalleFactura() == null){
            errores.add("La lista de detalles de la factura esta vacia");
            return false;
        } else {
            return true;
        }
    }
    
    private boolean validacionEmpresa(){
        if(informacion.getEmpresa().getNombre().isBlank() || informacion.getEmpresa().getRazonSocial().isBlank() || informacion.getEmpresa().getIngBrutos().isBlank() || informacion.getEmpresa().getFchIniAct().isBlank() || informacion.getEmpresa().getDomicilioComercial().isBlank() || informacion.getEmpresa().getCondIva().isBlank()){
            errores.add("Los datos de la empresa no estan correctamente cargados");
            return false;
        } else {
            return true;
        }
    }
    
    private boolean validacionCliente(){
        if(informacion.getCliente().getApNoRaSo().isBlank() || informacion.getCliente().getCondIva().isBlank() ||informacion.getCliente().getCondVta().isBlank() || informacion.getCliente().getDniCuit().isBlank() || informacion.getCliente().getDomicilio().isBlank()){
            errores.add("Los datos del cliente no estan correctamente cargados");
            return false;
        } else {
            return true;
        }
    }
    
    public List<String> getError() {
        return errores;
    }

}
