package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class DadosAdcionais implements Serializable {

    private int DiasUltPedido;
    private String VendaArquiteto;
    private String Status;
    private double Ltv;
    private String Idmoskit;

    public String getIdmoskit() {
        return Idmoskit;
    }

    public void setIdmoskit(String idmoskit) {
        Idmoskit = idmoskit;
    }

    public int getDiasUltPedido() {
        return DiasUltPedido;
    }

    public void setDiasUltPedido(int diasUltPedido) {
        DiasUltPedido = diasUltPedido;
    }

    public String getVendaArquiteto() {
        return VendaArquiteto;
    }

    public void setVendaArquiteto(String vendaArquiteto) {
        VendaArquiteto = vendaArquiteto;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public double getLtv() {
        return Ltv;
    }

    public void setLtv(double ltv) {
        Ltv = ltv;
    }
}
