package br.com.acalfortaleza.fikonmais.models;

import java.util.List;

public class BeneficioPromocao{

    private String codigoPromocao;
    private double descontoValorTotal;
    private boolean esgotado;
    private Object mensagemPromocional;
    private String nomePromocao;
    private List<Produtos> produtos;
    private String quantidadeDisponivel;
    private String vigencia;


    public String getCodigoPromocao() {
        return codigoPromocao;
    }

    public void setCodigoPromocao(String codigoPromocao) {
        this.codigoPromocao = codigoPromocao;
    }

    public double getDescontoValorTotal() {
        return descontoValorTotal;
    }

    public void setDescontoValorTotal(double descontoValorTotal) {
        this.descontoValorTotal = descontoValorTotal;
    }

    public boolean isEsgotado() {
        return esgotado;
    }

    public void setEsgotado(boolean esgotado) {
        this.esgotado = esgotado;
    }

    public Object getMensagemPromocional() {
        return mensagemPromocional;
    }

    public void setMensagemPromocional(Object mensagemPromocional) {
        this.mensagemPromocional = mensagemPromocional;
    }

    public String getNomePromocao() {
        return nomePromocao;
    }

    public void setNomePromocao(String nomePromocao) {
        this.nomePromocao = nomePromocao;
    }

    public List<Produtos> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produtos> produtos) {
        this.produtos = produtos;
    }

    public String getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(String quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public String getVigencia() {
        return vigencia;
    }

    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
    }
}
