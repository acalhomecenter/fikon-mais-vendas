package br.com.acalfortaleza.fikonmais.models.Produto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FiltrosProdutoResponse {

    @SerializedName("sucesso")
    private boolean sucesso;

    @SerializedName("dados")
    private DadosFiltros dados;

    @SerializedName("mensagem")
    private String mensagem;

    public boolean isSucesso() {
        return sucesso;
    }

    public DadosFiltros getDados() {
        return dados;
    }

    public String getMensagem() {
        return mensagem;
    }

    public static class DadosFiltros {
        @SerializedName("tamanhos")
        private List<ItemFiltro> tamanhos;

        @SerializedName("referenciasFornecedor")
        private List<String> referenciasFornecedor;

        @SerializedName("marcas")
        private List<ItemFiltro> marcas;

        @SerializedName("familias")
        private List<ItemFiltro> familias;

        @SerializedName("cores")
        private List<ItemFiltro> cores;

        @SerializedName("linhas")
        private List<ItemFiltro> linhas;

        @SerializedName("fornecedores")
        private List<ItemFiltro> fornecedores;

        @SerializedName("grupos")
        private List<ItemFiltro> grupos;

        public List<ItemFiltro> getTamanhos() {
            return tamanhos;
        }

        public List<String> getReferenciasFornecedor() {
            return referenciasFornecedor;
        }

        public List<ItemFiltro> getMarcas() {
            return marcas;
        }

        public List<ItemFiltro> getFamilias() {
            return familias;
        }

        public List<ItemFiltro> getCores() {
            return cores;
        }

        public List<ItemFiltro> getLinhas() {
            return linhas;
        }

        public List<ItemFiltro> getFornecedores() {
            return fornecedores;
        }

        public List<ItemFiltro> getGrupos() {
            return grupos;
        }
    }

    public static class ItemFiltro {
        @SerializedName("chave")
        private Long chave;

        @SerializedName("nome")
        private String nome;

        public Long getChave() {
            return chave;
        }

        public String getNome() {
            return nome;
        }
    }
}
