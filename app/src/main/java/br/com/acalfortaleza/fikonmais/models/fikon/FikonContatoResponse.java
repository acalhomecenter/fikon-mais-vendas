package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Resposta de GET cliente/contatoAssociado: { sucesso, dados: [contatos] }. */
public class FikonContatoResponse {
    @SerializedName("sucesso")
    private boolean sucesso;

    @SerializedName("mensagem")
    private String mensagem;

    @SerializedName("dados")
    private List<FikonContato> dados;

    public boolean isSucesso() {
        return sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public List<FikonContato> getDados() {
        return dados;
    }
}
