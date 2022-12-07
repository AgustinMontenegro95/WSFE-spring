package com.prueba.pruebaAPI.dominio;

import javax.persistence.Entity;

@Entity
public class Tributo {

    private long id;
    private String desc;
    private double baseImp;
    private double alic;
    private double importe;

    public Tributo() {
    }

    public Tributo(long id, String desc, double baseImp, double alic, double importe) {
        this.id = id;
        this.desc = desc;
        this.baseImp = baseImp;
        this.alic = alic;
        this.importe = importe;
    }

    public long getID() {
        return id;
    }

    public void setID(long value) {
        this.id = value;
    }
    
    public String getDesc() {
        return desc;
    }

    public void setDesc(String value) {
        this.desc = value;
    }

    public double getBaseImp() {
        return baseImp;
    }

    public void setBaseImp(double value) {
        this.baseImp = value;
    }

    public double getAlic() {
        return alic;
    }

    public void setAlic(double value) {
        this.alic = value;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double value) {
        this.importe = value;
    }
}
