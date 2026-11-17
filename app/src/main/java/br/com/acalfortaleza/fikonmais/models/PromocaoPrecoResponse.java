package br.com.acalfortaleza.fikonmais.models;

import java.util.List;

public class PromocaoPrecoResponse {

    public String codigoPromocao;
    public List<PromocaoPrecoProduto> produtos;
    public String quantidadeDisponivel;
    public double descontoValorTotal;
    public String mensagemPromocional;
    public boolean esgotado;
}
