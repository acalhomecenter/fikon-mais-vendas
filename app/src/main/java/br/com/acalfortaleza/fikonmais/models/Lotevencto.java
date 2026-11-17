package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class Lotevencto  implements Serializable {

    private String Descricao;
    private float Qtde;

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public float getQtde() {
        return Qtde;
    }

    public void setQtde(float qtde) {
        Qtde = qtde;
    }
}
