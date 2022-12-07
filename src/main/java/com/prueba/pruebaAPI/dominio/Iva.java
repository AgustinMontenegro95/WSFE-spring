package com.prueba.pruebaAPI.dominio;

import javax.persistence.Entity;

@Entity
public class Iva {

    private long id;
    private double baseImp;
    private double importe;

    public Iva() {
    }

    public Iva(long id, double baseImp, double importe) {
        this.id = id;
        this.baseImp = baseImp;
        this.importe = importe;
    }

    public long getID() {
        return id;
    }

    public void setID(long value) {
        this.id = value;
    }

    public double getBaseImp() {
        return baseImp;
    }

    public void setBaseImp(double value) {
        this.baseImp = value;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double value) {
        this.importe = value;
    }

}
