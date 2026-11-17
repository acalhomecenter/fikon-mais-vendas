package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class Setor implements Serializable {

    private int Codsetor;
    private String Descricao;


    public int getCodsetor() {
        return Codsetor;
    }

    public void setCodsetor(int codsetor) {
        Codsetor = codsetor;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }
}
