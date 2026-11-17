package br.com.acalfortaleza.fikonmais.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CupomPromocionalRequest {
    @SerializedName("cupom")
    public String cupom;
    @SerializedName("pessoa")
    public String pessoa;
    @SerializedName("chcriacao")
    public Integer chcriacao;
    @SerializedName("estabelecimento")
    public int estabelecimento;
    @SerializedName("itens")
    public List<Item> itens;

    public static class Item {
        @SerializedName("recurso")
        public Integer recurso;
        @SerializedName("codigo")
        public int codigo;
        @SerializedName("quantidade")
        public double quantidade;
        @SerializedName("unitario")
        public double unitario;
    }
}
