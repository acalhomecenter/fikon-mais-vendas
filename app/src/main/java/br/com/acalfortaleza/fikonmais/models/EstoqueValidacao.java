package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class EstoqueValidacao implements Serializable {

    public String Temestoque ;
    public String Temumlote;
    public String Lote ;
    public String Temmaisdeumlote ;



    public String getTemestoque() {
        return Temestoque;
    }

    public void setTemestoque(String temestoque) {
        Temestoque = temestoque;
    }

    public String getLote() {
        return Lote;
    }

    public void setLote(String lote) {
        Lote = lote;
    }

    public String getTemmaisdeumlote() {
        return Temmaisdeumlote;
    }

    public void setTemmaisdeumlote(String temmaisdeumlote) {
        Temmaisdeumlote = temmaisdeumlote;
    }

    public String getTemumlote() {
        return Temumlote;
    }

    public void setTemumlote(String temumlote) {
        Temumlote = temumlote;
    }
}
