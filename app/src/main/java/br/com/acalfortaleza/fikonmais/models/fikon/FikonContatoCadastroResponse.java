package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Resposta de POST cliente/cadastrarContatoAssociado. */
public class FikonContatoCadastroResponse {
    @SerializedName("sucesso")
    private boolean sucesso;

    @SerializedName("mensagem")
    private String mensagem;

    @SerializedName("dados")
    private Dados dados;

    public boolean isSucesso() {
        return sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Dados getDados() {
        return dados;
    }

    public static class Dados {
        @SerializedName("total")
        private int total;

        @SerializedName("cadastrados")
        private int cadastrados;

        @SerializedName("erros")
        private List<Object> erros;

        public int getTotal() {
            return total;
        }

        public int getCadastrados() {
            return cadastrados;
        }

        public List<Object> getErros() {
            return erros;
        }
    }
}
