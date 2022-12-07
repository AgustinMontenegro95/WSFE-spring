package com.prueba.pruebaAPI.dominio;

import javax.persistence.Entity;

@Entity
public class DetalleFactura {

    private long codigo;
    private String prodServ;
    private long cantidad;
    private String unidadMedida;
    private double precioUnitario;
    private double bonificacion;
    private double impBonificacion;
    private double subtotal;
    private long alicuotaIVA;
    private long subTotalCIVA;

    public DetalleFactura() {
    }

    public DetalleFactura(long codigo, String prodServ, long cantidad, String unidadMedida, double precioUnitario, double bonificacion, double impBonificacion, double subtotal, long alicuotaIVA, long subTotalCIVA) {
        this.codigo = codigo;
        this.prodServ = prodServ;
        this.cantidad = cantidad;
        this.unidadMedida = unidadMedida;
        this.precioUnitario = precioUnitario;
        this.bonificacion = bonificacion;
        this.impBonificacion = impBonificacion;
        this.subtotal = subtotal;
        this.alicuotaIVA = alicuotaIVA;
        this.subTotalCIVA = subTotalCIVA;
    }

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long value) {
        this.codigo = value;
    }

    public String getProdServ() {
        return prodServ;
    }

    public void setProdServ(String value) {
        this.prodServ = value;
    }

    public long getCantidad() {
        return cantidad;
    }

    public void setCantidad(long value) {
        this.cantidad = value;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String value) {
        this.unidadMedida = value;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double value) {
        this.precioUnitario = value;
    }

    public double getBonificacion() {
        return bonificacion;
    }

    public void setBonificacion(double value) {
        this.bonificacion = value;
    }

    public double getImpBonificacion() {
        return impBonificacion;
    }

    public void setImpBonificacion(double value) {
        this.impBonificacion = value;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double value) {
        this.subtotal = value;
    }

    public long getAlicuotaIVA() {
        return alicuotaIVA;
    }

    public void setAlicuotaIVA(long value) {
        this.alicuotaIVA = value;
    }

    public long getSubTotalCIVA() {
        return subTotalCIVA;
    }

    public void setSubTotalCIVA(long value) {
        this.subTotalCIVA = value;
    }

}
