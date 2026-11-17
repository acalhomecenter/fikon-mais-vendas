package br.com.acalfortaleza.fikonmais.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CupomPromocionalResponse {
    @SerializedName("sucesso")
    public boolean sucesso;
    @SerializedName("dados")
    public Dados dados;
    @SerializedName("mensagem")
    public String mensagem;

    public static class Dados {
        @SerializedName("cupom")
        public Cupom cupom;
        @SerializedName("identificadorReserva")
        public int identificadorReserva;
        @SerializedName("descontosAplicados")
        public List<DescontoAplicado> descontosAplicados;
    }

    public static class Cupom {
        @SerializedName("codigo")
        public String codigo;
        @SerializedName("descricao")
        public String descricao;
        @SerializedName("tipoDesconto")
        public String tipoDesconto;
    }

    public static class DescontoAplicado {
        @SerializedName("codigo")
        public int codigo;
        @SerializedName("recurso")
        public Integer recurso;
        @SerializedName("unitario")
        public double unitario;
        @SerializedName("quantidade")
        public double quantidade;
        @SerializedName("total")
        public double total;
        @SerializedName("tipoDesconto")
        public String tipoDesconto;
        @SerializedName("valorDesconto")
        public double valorDesconto;
    }
}
