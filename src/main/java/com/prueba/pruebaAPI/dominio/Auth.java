package com.prueba.pruebaAPI.dominio;

import javax.persistence.Entity;

@Entity
public class Auth {

    private String token;
    private String sign;
    private long cuit;
    private String tEnvio;

    public Auth() {
    }

    public Auth(String token, String sign, long cuit, String tEnvio) {
        this.token = token;
        this.sign = sign;
        this.cuit = cuit;
        this.tEnvio = tEnvio;
    }
    
    public String getToken() {
        return token;
    }

    public void setToken(String value) {
        this.token = value;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String value) {
        this.sign = value;
    }

    public long getCuit() {
        return cuit;
    }

    public void setCuit(long value) {
        this.cuit = value;
    }

    public String getTEnvio() {
        return tEnvio;
    }

    public void setTEnvio(String value) {
        this.tEnvio = value;
    }
}
