package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class IndicadorCampanha implements Serializable {

    private String Nome;
    private Double ValorAtingido;
    private Double Meta;
    private String Tipo;
    private Double Pontos;


    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public Double getValorAtingido() {
        return ValorAtingido;
    }

    public void setValorAtingido(Double valorAtingido) {
        ValorAtingido = valorAtingido;
    }

    public Double getMeta() {
        return Meta;
    }

    public void setMeta(Double meta) {
        Meta = meta;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public Double getPontos() {
        return Pontos;
    }

    public void setPontos(Double pontos) {
        Pontos = pontos;
    }


}
