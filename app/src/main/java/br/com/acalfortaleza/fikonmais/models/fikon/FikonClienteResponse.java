package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FikonClienteResponse {
    @SerializedName("sucesso")
    private boolean sucesso;

    @SerializedName("dados")
    private List<FikonCliente> dados;

    @SerializedName("mensagem")
    private String mensagem;

    public boolean isSucesso() {
        return sucesso;
    }

    public List<FikonCliente> getDados() {
        return dados;
    }

    public String getMensagem() {
        return mensagem;
    }
}
