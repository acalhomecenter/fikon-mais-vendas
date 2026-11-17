package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class MotivoDesconto implements Serializable {

    public String CodMotivo;
    public String Descricao;

    public String getCodMotivo() {
        return CodMotivo;
    }

    public void setCodMotivo(String codMotivo) {
        CodMotivo = codMotivo;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }
}
