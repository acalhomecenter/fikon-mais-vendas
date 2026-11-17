package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class CampanhaCliente implements Serializable {

    private float VendaLiquida;
    private float PontosUtilizados;
    private float Saldo;


    public float getVendaLiquida() {
        return VendaLiquida;
    }

    public void setVendaLiquida(float vendaLiquida) {
        VendaLiquida = vendaLiquida;
    }

    public float getPontosUtilizados() {
        return PontosUtilizados;
    }

    public void setPontosUtilizados(float pontosUtilizados) {
        PontosUtilizados = pontosUtilizados;
    }

    public float getSaldo() {
        return Saldo;
    }

    public void setSaldo(float saldo) {
        Saldo = saldo;
    }
}
