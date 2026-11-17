package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class Segmento implements Serializable {

    private int Id;

    private String Descricao;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }
}
