package br.com.acalfortaleza.fikonmais.models;

public class PromocaoPrecoRequest {

    public String codigo;
    public int posicaoItem;
    public int estabelecimento;

    public PromocaoPrecoRequest(String codigo, int posicaoItem, int estabelecimento) {
        this.codigo = codigo;
        this.posicaoItem = posicaoItem;
        this.estabelecimento = estabelecimento;
    }
}
