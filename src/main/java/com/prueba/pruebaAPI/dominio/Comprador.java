package com.prueba.pruebaAPI.dominio;

import javax.persistence.Entity;

@Entity
public class Comprador {

    private long docTipo;
    private String docNro;
    private double porcentaje;

    public Comprador() {
    }

    public Comprador(long docTipo, String docNro, double porcentaje) {
        this.docTipo = docTipo;
        this.docNro = docNro;
        this.porcentaje = porcentaje;
    }

    public long getDocTipo() {
        return docTipo;
    }

    public void setDocTipo(long value) {
        this.docTipo = value;
    }

    public String getDocNro() {
        return docNro;
    }

    public void setDocNro(String value) {
        this.docNro = value;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double value) {
        this.porcentaje = value;
    }
}
