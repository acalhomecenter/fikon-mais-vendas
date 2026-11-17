package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class Midias implements Serializable {

    private String CodMidia;
    private String Descricao;

    public String getCodMidia() {
        return CodMidia;
    }

    public void setCodMidia(String codMidia) {
        CodMidia = codMidia;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }
}
