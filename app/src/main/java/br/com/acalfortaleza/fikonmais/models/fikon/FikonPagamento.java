package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

/** Item de "pagamentos" em orçamento, pedido e gravar. A soma tem de bater com o total. */
public class FikonPagamento {
    /** Código do tipo de documento: CC, CD, PIX, BO, DH... */
    @SerializedName("tipo")
    public String tipo;

    @SerializedName("valor")
    public double valor;

    @SerializedName("qtdParcelas")
    public Integer qtdParcelas;

    @SerializedName("bandeira")
    public String bandeira;

    @SerializedName("rede")
    public String rede;

    @SerializedName("idTransacao")
    public String idTransacao;

    @SerializedName("autorizacaoTransacao")
    public String autorizacaoTransacao;

    /** chave de pedidos/pesquisarCondicoes. */
    @SerializedName("chaveCondNeg")
    public Long chaveCondNeg;

    @SerializedName("diasPrimeiroVencimento")
    public Integer diasPrimeiroVencimento;

    @SerializedName("diasEntreParcelas")
    public Integer diasEntreParcelas;

    @SerializedName("nossoNumero")
    public String nossoNumero;

    public FikonPagamento() {
    }

    public FikonPagamento(String tipo, double valor) {
        this.tipo = tipo;
        this.valor = valor;
    }
}
