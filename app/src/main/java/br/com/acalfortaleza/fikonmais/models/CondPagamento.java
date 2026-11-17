package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class CondPagamento implements Serializable {

    private String Condpgto;
    private String Descricao;
    private String TemEntrada;
    private String ValidarCredito;

    public String getValidarCredito() {
        return ValidarCredito;
    }

    public void setValidarCredito(String validarCredito) {
        ValidarCredito = validarCredito;
    }

    public String getCondpgto() {
        return Condpgto;
    }

    public void setCondpgto(String condpgto) {
        Condpgto = condpgto;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public String getTemEntrada() {
        return TemEntrada;
    }

    public void setTemEntrada(String temEntrada) {
        TemEntrada = temEntrada;
    }

    public String toString(){
        return getCondpgto()+" - "+ getDescricao();
    }
}
