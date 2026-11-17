package br.com.acalfortaleza.fikonmais.models;

public class ValidacaoEstoque {
    public int Id ;
    public int Item ;
    public String Produto ;
    public double QtdSolicitada ;
    public String Lotevencto ;
    public String Responsavel;
    public double QtdVerificada ;
    public String DtVerificacao ;
    public String Status ;
    public String Obs ;
    public String Tempo ;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getItem() {
        return Item;
    }

    public void setItem(int item) {
        Item = item;
    }

    public String getProduto() {
        return Produto;
    }

    public void setProduto(String produto) {
        Produto = produto;
    }

    public double getQtdSolicitada() {
        return QtdSolicitada;
    }

    public void setQtdSolicitada(double qtdSolicitada) {
        QtdSolicitada = qtdSolicitada;
    }

    public String getLotevencto() {
        return Lotevencto;
    }

    public void setLotevencto(String lotevencto) {
        Lotevencto = lotevencto;
    }

    public String getResponsavel() {
        return Responsavel;
    }

    public void setResponsavel(String responsavel) {
        Responsavel = responsavel;
    }

    public double getQtdVerificada() {
        return QtdVerificada;
    }

    public void setQtdVerificada(double qtdVerificada) {
        QtdVerificada = qtdVerificada;
    }

    public String getDtVerificacao() {
        return DtVerificacao;
    }

    public void setDtVerificacao(String dtVerificacao) {
        DtVerificacao = dtVerificacao;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getObs() {
        return Obs;
    }

    public void setObs(String obs) {
        Obs = obs;
    }

    public String getTempo() {
        return Tempo;
    }

    public void setTempo(String tempo) {
        Tempo = tempo;
    }
}
