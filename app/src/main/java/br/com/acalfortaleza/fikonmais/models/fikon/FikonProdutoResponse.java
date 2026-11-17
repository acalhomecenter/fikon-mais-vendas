package br.com.acalfortaleza.fikonmais.models.fikon;

import java.util.List;

public class FikonProdutoResponse {
    private boolean sucesso;
    private List<FikonProduto> dados;
    private String mensagem;

    public boolean isSucesso() {
        return sucesso;
    }

    public List<FikonProduto> getDados() {
        return dados;
    }

    public String getMensagem() {
        return mensagem;
    }
}
