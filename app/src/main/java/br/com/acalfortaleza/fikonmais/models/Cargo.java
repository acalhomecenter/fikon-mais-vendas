package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class Cargo implements Serializable {

    private int Codcargo;
    private String Descricao;

    public int getCodcargo() {
        return Codcargo;
    }

    public void setCodcargo(int codcargo) {
        Codcargo = codcargo;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }
}
