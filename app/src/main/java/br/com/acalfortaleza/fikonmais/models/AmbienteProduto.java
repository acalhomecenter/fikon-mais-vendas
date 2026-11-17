package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class AmbienteProduto implements Serializable{

    private String Id;
    private double Numorc;
    private long IdAmbiente;
    private int Coditprod;
    private double Quantidade;
    private long IdItemOrcamento;
    private double QtdItemOrcamento;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public double getNumorc() {
        return Numorc;
    }

    public void setNumorc(double numorc) {
        Numorc = numorc;
    }

    public long getIdAmbiente() {
        return IdAmbiente;
    }

    public void setIdAmbiente(long idAmbiente) {
        IdAmbiente = idAmbiente;
    }

    public int getCoditprod() {
        return Coditprod;
    }

    public void setCoditprod(int coditprod) {
        Coditprod = coditprod;
    }

    public double getQuantidade() {
        return Quantidade;
    }

    public void setQuantidade(double quantidade) {
        Quantidade = quantidade;
    }

    public long getIdItemOrcamento() {
        return IdItemOrcamento;
    }

    public void setIdItemOrcamento(long idItemOrcamento) {
        IdItemOrcamento = idItemOrcamento;
    }

    public double getQtdItemOrcamento() {
        return QtdItemOrcamento;
    }

    public void setQtdItemOrcamento(double qtdItemOrcamento) {
        QtdItemOrcamento = qtdItemOrcamento;
    }

}
