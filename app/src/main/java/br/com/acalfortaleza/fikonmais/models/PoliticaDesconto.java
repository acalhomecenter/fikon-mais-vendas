package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;
import java.util.List;

public class PoliticaDesconto implements Serializable{

    private String Id;
    private Boolean Permitido;
    private Double PercentualDescontoItem;
    private Double PercentualMargem;
    private Double PercentualDescontoFrete;
    private String Observacao;


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Boolean getPermitido() {
        return Permitido;
    }

    public void setPermitido(Boolean permitido) {
        Permitido = permitido;
    }

    public Double getPercentualDescontoItem() {
        return PercentualDescontoItem;
    }

    public void setPercentualDescontoItem(Double percentualDescontoItem) {
        PercentualDescontoItem = percentualDescontoItem;
    }

    public Double getPercentualMargem() {
        return PercentualMargem;
    }

    public void setPercentualMargem(Double percentualMargem) {
        PercentualMargem = percentualMargem;
    }

    public Double getPercentualDescontoFrete() {
        return PercentualDescontoFrete;
    }

    public void setPercentualDescontoFrete(Double percentualDescontoFrete) {
        PercentualDescontoFrete = percentualDescontoFrete;
    }

    public String getObservacao() {
        return Observacao;
    }

    public void setObservacao(String observacao) {
        Observacao = observacao;
    }

}
