package com.prueba.pruebaAPI.dominio;

import javax.persistence.Entity;

@Entity
public class FeCabReq {

    private long cantReg;
    private long ptoVta;
    private long cbteTipo;

    public FeCabReq() {
    }

    public FeCabReq(long cantReg, long ptoVta, long cbteTipo) {
        this.cantReg = cantReg;
        this.ptoVta = ptoVta;
        this.cbteTipo = cbteTipo;
    }

    public long getCantReg() {
        return cantReg;
    }

    public void setCantReg(long value) {
        this.cantReg = value;
    }

    public long getPtoVta() {
        return ptoVta;
    }

    public void setPtoVta(long value) {
        this.ptoVta = value;
    }

    public long getCbteTipo() {
        return cbteTipo;
    }

    public void setCbteTipo(long value) {
        this.cbteTipo = value;
    }
}
