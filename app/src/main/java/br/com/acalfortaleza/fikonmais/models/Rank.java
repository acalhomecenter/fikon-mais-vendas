package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class Rank implements Serializable {

    private Integer Posicao;
    private String Nome;
    private Double Pontos;
    private Double VendaLiquidaTotal;

    public Integer getPosicao() {
        return Posicao;
    }

    public void setPosicao(Integer posicao) {
        Posicao = posicao;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public Double getPontos() {
        return Pontos;
    }

    public void setPontos(Double pontos) {
        Pontos = pontos;
    }

    public Double getVendaLiquidaTotal() {
        return VendaLiquidaTotal;
    }

    public void setVendaLiquidaTotal(Double vendaLiquidaTotal) {
        VendaLiquidaTotal = vendaLiquidaTotal;
    }

}
