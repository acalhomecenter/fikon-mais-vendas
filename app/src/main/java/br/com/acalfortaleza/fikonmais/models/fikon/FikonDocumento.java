package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Detalhe de orçamento ou pedido: dadosVenda de GET pedidos e "data" das rotas comercial/...
 */
public class FikonDocumento {
    @SerializedName("idPedido")
    public long idPedido;

    /** "ORCAMENTO" no detalhe de orçamento. */
    @SerializedName("tipo")
    public String tipo;

    @SerializedName("codigoCliente")
    public String codigoCliente;

    @SerializedName("nomeCliente")
    public String nomeCliente;

    /** Código da loja (texto). */
    @SerializedName("estabelecimento")
    public String estabelecimento;

    @SerializedName("nomeEstabelecimento")
    public String nomeEstabelecimento;

    @SerializedName("vendedor")
    public String vendedor;

    @SerializedName("vendedorCpfCnpj")
    public String vendedorCpfCnpj;

    @SerializedName("dataEmissao")
    public String dataEmissao;

    @SerializedName("totalNegociado")
    public double totalNegociado;

    @SerializedName("linhaDoTempo")
    public LinhaDoTempo linhaDoTempo;

    @SerializedName("produtos")
    public List<Produto> produtos;

    @SerializedName("financeiro")
    public List<Parcela> financeiro;

    @SerializedName("condicoesDeNegociacao")
    public List<FikonPagamento> condicoesDeNegociacao;

    @SerializedName("pedidoAtrelado")
    public PedidoAtrelado pedidoAtrelado;

    public boolean isOrcamento() {
        return "ORCAMENTO".equalsIgnoreCase(tipo);
    }

    public static class LinhaDoTempo {
        /** 1 Criado, 2 Aprovado, 3 Devolução parcial, 4 Devolvido, 5 Cancelado. */
        @SerializedName("statusComercialCodigo")
        public int statusComercialCodigo;

        @SerializedName("statusComercialNome")
        public String statusComercialNome;
    }

    public static class Produto {
        @SerializedName("chaveItem")
        public long chaveItem;

        @SerializedName("codigo")
        public String codigo;

        @SerializedName("nome")
        public String nome;

        @SerializedName("quantidadeTotal")
        public double quantidadeTotal;

        @SerializedName("quantidadeFaturada")
        public double quantidadeFaturada;

        @SerializedName("quantidadeDevolvida")
        public double quantidadeDevolvida;

        @SerializedName("unitario")
        public double unitario;

        @SerializedName("total")
        public double total;

        @SerializedName("statusItemCodigo")
        public int statusItemCodigo;

        @SerializedName("statusItemNome")
        public String statusItemNome;
    }

    public static class Parcela {
        @SerializedName("chaveParcela")
        public long chaveParcela;

        @SerializedName("numeroParcela")
        public int numeroParcela;

        @SerializedName("totalParcelas")
        public int totalParcelas;

        @SerializedName("formaPagamento")
        public String formaPagamento;

        @SerializedName("valorParcela")
        public double valorParcela;

        @SerializedName("vencimento")
        public String vencimento;

        @SerializedName("prazoDias")
        public int prazoDias;
    }

    public static class PedidoAtrelado {
        @SerializedName("chcriacao")
        public long chcriacao;

        @SerializedName("statusCodigo")
        public int statusCodigo;

        @SerializedName("statusNome")
        public String statusNome;
    }
}
