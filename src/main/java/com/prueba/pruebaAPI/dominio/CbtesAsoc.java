package com.prueba.pruebaAPI.dominio;

import javax.persistence.Entity;

@Entity
public class CbtesAsoc {
    
    private long tipo;
    private long ptoVta;
    private long nro;

    public CbtesAsoc() {
    }

    public CbtesAsoc(long tipo, long ptoVta, long nro) {
        this.tipo = tipo;
        this.ptoVta = ptoVta;
        this.nro = nro;
    }

    public long getTipo() { return tipo; }
    public void setTipo(long value) { this.tipo = value; }

    public long getPtoVta() { return ptoVta; }
    public void setPtoVta(long value) { this.ptoVta = value; }

    public long getNro() { return nro; }
    public void setNro(long value) { this.nro = value; }
}
