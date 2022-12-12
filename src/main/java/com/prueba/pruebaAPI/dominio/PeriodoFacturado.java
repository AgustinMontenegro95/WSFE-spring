package com.prueba.pruebaAPI.dominio;

public class PeriodoFacturado {
    
    private String pfDesde;
    private String pfHasta;
    private String pfVencimiento;

    public PeriodoFacturado() {
    }

    public PeriodoFacturado(String pfDesde, String pfHasta, String pfVencimiento) {
        this.pfDesde = pfDesde;
        this.pfHasta = pfHasta;
        this.pfVencimiento = pfVencimiento;
    }

    public String getPfDesde() {
        return pfDesde;
    }

    public void setPfDesde(String pfDesde) {
        this.pfDesde = pfDesde;
    }

    public String getPfHasta() {
        return pfHasta;
    }

    public void setPfHasta(String pfHasta) {
        this.pfHasta = pfHasta;
    }

    public String getPfVencimiento() {
        return pfVencimiento;
    }

    public void setPfVencimiento(String pfVencimiento) {
        this.pfVencimiento = pfVencimiento;
    }
    
}
