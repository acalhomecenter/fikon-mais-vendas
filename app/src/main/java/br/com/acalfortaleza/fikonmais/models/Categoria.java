package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class Categoria implements Serializable {

    public String Descricao;
    public double VendaBruta ;
    public double Vldesconto ;
    public double PercDesc;

    public double getPercDesc() {
        return PercDesc;
    }

    public void setPercDesc(double percDesc) {
        PercDesc = percDesc;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public double getVendaBruta() {
        return VendaBruta;
    }

    public void setVendaBruta(double vendaBruta) {
        VendaBruta = vendaBruta;
    }

    public double getVldesconto() {
        return Vldesconto;
    }

    public void setVldesconto(double vldesconto) {
        Vldesconto = vldesconto;
    }
}
