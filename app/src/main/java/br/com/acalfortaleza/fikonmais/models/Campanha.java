package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;
import java.util.Currency;
import java.util.List;

public class Campanha implements Serializable {

    private String Mes;
    private Integer CodigoConsultor;
    private String NomeConsultor;
    private Double VendaLiquida;
    private Double PercentualFaturamento;
    private Double PontuacaoMes;
    private List<IndicadorCampanha> Indicadores;
    private Double SomatorioPontos;
    private Rank Rank;


    public String getMes() {
        return Mes;
    }

    public void setMes(String mes) {
        Mes = mes;
    }

    public Integer getCodigoConsultor() {
        return CodigoConsultor;
    }

    public void setCodigoConsultor(Integer codigoConsultor) {
        CodigoConsultor = codigoConsultor;
    }

    public String getNomeConsultor() {
        return NomeConsultor;
    }

    public void setNomeConsultor(String nomeConsultor) {
        NomeConsultor = nomeConsultor;
    }

    public Double getVendaLiquida() {
        return VendaLiquida;
    }

    public void setVendaLiquida(Double vendaLiquida) {
        VendaLiquida = vendaLiquida;
    }

    public Double getPercentualFaturamento() {
        return PercentualFaturamento;
    }

    public void setPercentualFaturamento(Double percentualFaturamento) {
        PercentualFaturamento = percentualFaturamento;
    }

    public Double getPontuacaoMes() {
        return PontuacaoMes;
    }

    public void setPontuacaoMes(Double pontuacaoMes) {
        PontuacaoMes = pontuacaoMes;
    }

    public List<IndicadorCampanha> getIndicadores() {
        return Indicadores;
    }

    public void setIndicadores(List<IndicadorCampanha> indicadores) {
        Indicadores = indicadores;
    }

    public Double getSomatorioPontos() {
        return SomatorioPontos;
    }

    public void setSomatorioPontos(Double somatorioPontos) {
        SomatorioPontos = somatorioPontos;
    }

    public br.com.acalfortaleza.fikonmais.models.Rank getRank() {
        return Rank;
    }

    public void setRank(br.com.acalfortaleza.fikonmais.models.Rank rank) {
        Rank = rank;
    }

}
