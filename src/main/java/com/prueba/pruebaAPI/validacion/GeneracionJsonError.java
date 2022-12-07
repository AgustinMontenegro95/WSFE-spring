package com.prueba.pruebaAPI.validacion;

import java.util.ArrayList;
import java.util.List;

public class GeneracionJsonError {

    private List<String> listaErrores;

    public GeneracionJsonError(List<String> listaErrores) {
        this.listaErrores = listaErrores;
    }

    public GeneracionJsonError() {
        this.listaErrores = new ArrayList<>();
    }

    public void generarJson() {

        try {
            String errores = "\"Errores\": {";

            if (listaErrores.size() == 1) {
                errores += (listaErrores.get(listaErrores.size() - 1));
            } else {
                for (int indice = 0; indice < listaErrores.size() - 1; indice++) {
                    errores += (listaErrores.get(indice) + ",\n");
                }
                errores += listaErrores.get(listaErrores.size() - 1);
            }
            errores += "}";
            System.out.println(errores);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public List<String> recuperarErrores() {
        return listaErrores;
    }

    public void agregarError(String nuevoError) {
        listaErrores.add(nuevoError);
    }
}
