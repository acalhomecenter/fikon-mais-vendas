package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Corpo de POST comercial/orcamentos (mesmos campos de POST pedidos) e de PUT comercial/editar.
 * Campos null não são enviados (Gson omite null), o que no editar significa "não alterar".
 */
public class FikonDocumentoRequest {
    /** Só no editar: chave do orçamento ou pedido. */
    @SerializedName("chcriacao")
    public Long chcriacao;

    /** Chave, CPF/CNPJ ou código do cliente. */
    @SerializedName("cliente")
    public String cliente;

    /** Código, chave ou nome do representante. */
    @SerializedName("vendedor")
    public String vendedor;

    @SerializedName("chaveEstabelecimento")
    public Integer chaveEstabelecimento;

    /** YYYY-MM-DD */
    @SerializedName("emissao")
    public String emissao;

    /** HH:MM:SS */
    @SerializedName("emissaohora")
    public String emissaohora;

    /** No orçamento aceita YYYY-MM-DD; no POST pedidos exige YYYY-MM-DD HH:MM:SS. */
    @SerializedName("dataEntrega")
    public String dataEntrega;

    @SerializedName("idDocumento")
    public String idDocumento;

    @SerializedName("observacao")
    public String observacao;

    @SerializedName("frete")
    public Double frete;

    @SerializedName("desconto")
    public Double desconto;

    @SerializedName("itens")
    public List<Item> itens;

    @SerializedName("pagamentos")
    public List<FikonPagamento> pagamentos;

    public static class Item {
        /** Código, chave ou chave de integração do produto. */
        @SerializedName("idProduto")
        public String idProduto;

        @SerializedName("quantidade")
        public double quantidade;

        @SerializedName("unitario")
        public Double unitario;

        /** Obrigatório só em POST pedidos; no orçamento é trocado pelo de orçamento. */
        @SerializedName("tiposaida")
        public Long tiposaida;

        @SerializedName("lote")
        public String lote;

        @SerializedName("observacao")
        public String observacao;
    }

    public List<Item> itensOuVazio() {
        if (itens == null) {
            itens = new ArrayList<>();
        }
        return itens;
    }
}
