package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FikonFiltroBuscaRequest {
    @SerializedName("textoBusca")
    private String textoBusca;

    @SerializedName("estabelecimento")
    private Integer estabelecimento;

    @SerializedName("classes")
    private List<String> classes = new ArrayList<>();

    @SerializedName("fornecedores")
    private List<String> fornecedores = new ArrayList<>();

    @SerializedName("referenciasFornecedor")
    private List<String> referenciasFornecedor = new ArrayList<>();

    @SerializedName("grupos")
    private List<String> grupos = new ArrayList<>();

    @SerializedName("marcas")
    private List<String> marcas = new ArrayList<>();

    @SerializedName("cores")
    private List<String> cores = new ArrayList<>();

    @SerializedName("tamanhos")
    private List<String> tamanhos = new ArrayList<>();

    public FikonFiltroBuscaRequest(String fornecedor, String linha, String familia, String grupo,
                                   String textoBusca, String codigoFornecedor, Integer estabelecimento) {
        this.textoBusca = textoBusca == null ? "" : textoBusca;
        this.estabelecimento = estabelecimento;
        addIfPresent(fornecedores, fornecedor);
        addIfPresent(classes, linha);
        addIfPresent(classes, familia);
        addIfPresent(grupos, grupo);
        addIfPresent(referenciasFornecedor, codigoFornecedor);
    }

    private static void addIfPresent(List<String> values, String value) {
        if (value != null && !value.trim().isEmpty()) {
            values.add(value);
        }
    }
}
