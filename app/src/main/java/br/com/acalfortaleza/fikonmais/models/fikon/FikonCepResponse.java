package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/** Resposta de GET frete/consultarCep: { retorno, erros }. */
public class FikonCepResponse {
    @SerializedName("retorno")
    private Endereco retorno;

    @SerializedName("erros")
    private List<String> erros;

    public Endereco getRetorno() {
        return retorno;
    }

    public List<String> getErros() {
        return erros;
    }

    public static class Endereco {
        @SerializedName("logradouro")
        private String logradouro;

        @SerializedName("tipoLogradouro")
        private String tipoLogradouro;

        @SerializedName("bairro")
        private String bairro;

        /** Cidade. */
        @SerializedName("localidade")
        private String localidade;

        /** Sigla (ViaCEP) ou nome do estado (tabela CEP do ERP). */
        @SerializedName("uf")
        private String uf;

        public String getLogradouro() {
            return logradouro;
        }

        public String getTipoLogradouro() {
            return tipoLogradouro;
        }

        public String getBairro() {
            return bairro;
        }

        public String getLocalidade() {
            return localidade;
        }

        public String getUf() {
            return uf;
        }
    }
}
