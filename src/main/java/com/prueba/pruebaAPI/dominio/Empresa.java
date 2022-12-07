package com.prueba.pruebaAPI.dominio;

import javax.persistence.Entity;

@Entity
public class Empresa {

    private String nombre;
    private String razonSocial;
    private String ingBrutos;
    private String domicilioComercial;
    private String condIva;
    private String fchIniAct;

    public Empresa() {
    }

    public Empresa(String nombre, String razonSocial, String ingBrutos, String domicilioComercial, String condIva, String fchIniAct) {
        this.nombre = nombre;
        this.razonSocial = razonSocial;
        this.ingBrutos = ingBrutos;
        this.domicilioComercial = domicilioComercial;
        this.condIva = condIva;
        this.fchIniAct = fchIniAct;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String value) {
        this.nombre = value;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String value) {
        this.razonSocial = value;
    }

    public String getIngBrutos() {
        return ingBrutos;
    }

    public void setIngBrutos(String value) {
        this.ingBrutos = value;
    }

    public String getDomicilioComercial() {
        return domicilioComercial;
    }

    public void setDomicilioComercial(String value) {
        this.domicilioComercial = value;
    }

    public String getCondIva() {
        return condIva;
    }

    public void setCondIva(String value) {
        this.condIva = value;
    }

    public String getFchIniAct() {
        return fchIniAct;
    }

    public void setFchIniAct(String value) {
        this.fchIniAct = value;
    }
}
