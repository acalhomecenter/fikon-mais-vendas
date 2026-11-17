package br.com.acalfortaleza.fikonmais.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

public class Fretes implements Serializable {

    private int CodFrete;
    private String Modalidade;
    private double Vlfrete;

    public int getCodFrete() {
        return CodFrete;
    }

    public void setCodFrete(int codFrete) {
        CodFrete = codFrete;
    }

    public String getModalidade() {
        return Modalidade;
    }

    public void setModalidade(String modalidade) {
        Modalidade = modalidade;
    }

    public double getVlfrete() {
        return Vlfrete;
    }

    public void setVlfrete(double vlfrete) {
        Vlfrete = vlfrete;
    }

    @NonNull
    @Override
    public String toString() {
        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return getCodFrete()+" - "+getModalidade().trim()+" - "+" FRETE: " +getVlfrete() ;
    }
}



