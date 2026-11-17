package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class TpNotas implements Serializable {

    private int CodTpNota;
    private String Descricao;

    public int getCodTpNota() {
        return CodTpNota;
    }

    public void setCodTpNota(int codTpNota) {
        CodTpNota = codTpNota;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public String toString(){
        return getCodTpNota()+" - "+ getDescricao();
    }
}
