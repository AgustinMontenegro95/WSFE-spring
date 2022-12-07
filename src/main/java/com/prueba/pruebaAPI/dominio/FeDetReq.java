package com.prueba.pruebaAPI.dominio;

import javax.persistence.Entity;

@Entity
public class FeDetReq {

    private long concepto;
    private long docTipo;
    private long docNro;
    private String cbteFch;
    private double impTotal;
    private double impTotConc;
    private double impNeto;
    private double impOpEx;
    private double impTrib;
    private double impIVA;
    private String monID;
    private long monCotiz;
    private CbtesAsoc[] cbtesAsoc;
    private Tributo[] tributos;
    private Iva[] iva;
    private Comprador[] compradores;

    public FeDetReq() {
    }

    public FeDetReq(long concepto, long docTipo, long docNro, String cbteFch, double impTotal, double impTotConc, double impNeto, double impOpEx, double impTrib, double impIVA, String monID, long monCotiz, CbtesAsoc[] cbtesAsoc, Tributo[] tributos, Iva[] iva, Comprador[] compradores) {
        this.concepto = concepto;
        this.docTipo = docTipo;
        this.docNro = docNro;
        this.cbteFch = cbteFch;
        this.impTotal = impTotal;
        this.impTotConc = impTotConc;
        this.impNeto = impNeto;
        this.impOpEx = impOpEx;
        this.impTrib = impTrib;
        this.impIVA = impIVA;
        this.monID = monID;
        this.monCotiz = monCotiz;
        this.cbtesAsoc = cbtesAsoc;
        this.tributos = tributos;
        this.iva = iva;
        this.compradores = compradores;
    }

    public long getConcepto() {
        return concepto;
    }

    public void setConcepto(long value) {
        this.concepto = value;
    }

    public long getDocTipo() {
        return docTipo;
    }

    public void setDocTipo(long value) {
        this.docTipo = value;
    }

    public long getDocNro() {
        return docNro;
    }

    public void setDocNro(long value) {
        this.docNro = value;
    }

    public String getCbteFch() {
        return cbteFch;
    }

    public void setCbteFch(String value) {
        this.cbteFch = value;
    }

    public double getImpTotal() {
        return impTotal;
    }

    public void setImpTotal(double value) {
        this.impTotal = value;
    }

    public double getImpTotConc() {
        return impTotConc;
    }

    public void setImpTotConc(double value) {
        this.impTotConc = value;
    }

    public double getImpNeto() {
        return impNeto;
    }

    public void setImpNeto(double value) {
        this.impNeto = value;
    }

    public double getImpOpEx() {
        return impOpEx;
    }

    public void setImpOpEx(double value) {
        this.impOpEx = value;
    }

    public double getImpTrib() {
        return impTrib;
    }

    public void setImpTrib(double value) {
        this.impTrib = value;
    }

    public double getImpIVA() {
        return impIVA;
    }

    public void setImpIVA(double value) {
        this.impIVA = value;
    }

    public String getMonID() {
        return monID;
    }

    public void setMonID(String value) {
        this.monID = value;
    }

    public long getMonCotiz() {
        return monCotiz;
    }

    public void setMonCotiz(long value) {
        this.monCotiz = value;
    }

    public CbtesAsoc[] getCbtesAsoc() {
        return cbtesAsoc;
    }

    public void setCbtesAsoc(CbtesAsoc[] value) {
        this.cbtesAsoc = value;
    }

    public Tributo[] getTributos() {
        return tributos;
    }

    public void setTributos(Tributo[] value) {
        this.tributos = value;
    }

    public Iva[] getIva() {
        return iva;
    }

    public void setIva(Iva[] value) {
        this.iva = value;
    }

    public Comprador[] getCompradores() {
        return compradores;
    }

    public void setCompradores(Comprador[] value) {
        this.compradores = value;
    }

}
