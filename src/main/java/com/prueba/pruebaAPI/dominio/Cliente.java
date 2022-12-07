package com.prueba.pruebaAPI.dominio;

import javax.persistence.Entity;

@Entity
public class Cliente {

    private String dniCuit;
    private String condIva;
    private String condVta;
    private String apNoRaSo;
    private String domicilio;

    public Cliente() {
    }

    public Cliente(String dniCuit, String condIva, String condVta, String apNoRaSo, String domicilio) {
        this.dniCuit = dniCuit;
        this.condIva = condIva;
        this.condVta = condVta;
        this.apNoRaSo = apNoRaSo;
        this.domicilio = domicilio;
    }

    public String getDniCuit() {
        return dniCuit;
    }

    public void setDniCuit(String value) {
        this.dniCuit = value;
    }

    public String getCondIva() {
        return condIva;
    }

    public void setCondIva(String value) {
        this.condIva = value;
    }

    public String getCondVta() {
        return condVta;
    }

    public void setCondVta(String value) {
        this.condVta = value;
    }

    public String getApNoRaSo() {
        return apNoRaSo;
    }

    public void setApNoRaSo(String value) {
        this.apNoRaSo = value;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String value) {
        this.domicilio = value;
    }
}
