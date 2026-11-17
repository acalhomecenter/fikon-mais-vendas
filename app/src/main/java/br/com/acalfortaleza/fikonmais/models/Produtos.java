package br.com.acalfortaleza.fikonmais.models;

public class Produtos {
    private int posicaoItem;
    private String codigo;
    private double desconto;

    public int getPosicaoItem() {
        return posicaoItem;
    }

    public void setPosicaoItem(int posicaoItem) {
        this.posicaoItem = posicaoItem;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }
}
