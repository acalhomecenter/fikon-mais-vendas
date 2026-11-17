package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

/** Condição de pagamento de GET pedidos/pesquisarCondicoes. */
public class FikonCondicao {
    @SerializedName("chave")
    public long chave;

    @SerializedName("codigo")
    public String codigo;

    @SerializedName("nome")
    public String nome;

    @SerializedName("tipoDeDocumento")
    public List<Ref> tipoDeDocumento;

    @SerializedName("numParcelas")
    public Parcelas numParcelas;

    @SerializedName("bandeira")
    public List<Ref> bandeira;

    @SerializedName("rede")
    public String rede;

    @SerializedName("tipoParcelamento")
    public String tipoParcelamento;

    @SerializedName("valorMinimoDaParcela")
    public double valorMinimoDaParcela;

    public static class Ref {
        @SerializedName("chave")
        public long chave;

        @SerializedName("nome")
        public String nome;
    }

    public static class Parcelas {
        @SerializedName("minimo")
        public int minimo;

        @SerializedName("maximo")
        public int maximo;
    }

    public int parcelasMinimo() {
        return numParcelas == null || numParcelas.minimo < 1 ? 1 : numParcelas.minimo;
    }

    public int parcelasMaximo() {
        int max = numParcelas == null ? 1 : numParcelas.maximo;
        return Math.max(parcelasMinimo(), max);
    }

    public String nomeTipoDocumento() {
        return tipoDeDocumento == null || tipoDeDocumento.isEmpty() || tipoDeDocumento.get(0) == null
                ? null : tipoDeDocumento.get(0).nome;
    }

    /**
     * Código do tipo de documento que "pagamentos[].tipo" espera (CC, CD, PIX, BO, DH).
     * A rota de condições só traz o nome do tipo, então o código é deduzido dele.
     */
    public String codigoTipoDocumento() {
        String nomeTipo = nomeTipoDocumento();
        String texto = normalizar(nomeTipo != null ? nomeTipo : nome);
        if (texto.contains("PIX")) {
            return "PIX";
        }
        if (texto.contains("DEBITO")) {
            return "CD";
        }
        if (texto.contains("CREDITO") || texto.contains("CARTAO")) {
            return "CC";
        }
        if (texto.contains("BOLETO")) {
            return "BO";
        }
        if (texto.contains("DINHEIRO") || texto.contains("ESPECIE")) {
            return "DH";
        }
        return null;
    }

    private static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return nome;
    }
}
