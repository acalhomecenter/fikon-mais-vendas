package br.com.acalfortaleza.fikonmais.model;

import com.google.gson.annotations.SerializedName;

public class FreteServicoOrcamento {
    @SerializedName("valor")
    private double valor;

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
