package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

/** Linha da lista de GET comercial/orcamentos (sem chcriacao). */
public class FikonOrcamentoResumo {
    @SerializedName("chcriacao")
    public long chcriacao;

    @SerializedName("emissao")
    public String emissao;

    @SerializedName("pessoa")
    public Ref pessoa;

    @SerializedName("representante")
    public Ref representante;

    @SerializedName("estabelecimento")
    public Ref estabelecimento;

    @SerializedName("totalNegociado")
    public double totalNegociado;

    @SerializedName("cancelado")
    public boolean cancelado;

    @SerializedName("pedidoAtrelado")
    public FikonDocumento.PedidoAtrelado pedidoAtrelado;

    public static class Ref {
        @SerializedName("chave")
        public long chave;

        @SerializedName("nome")
        public String nome;
    }
}
