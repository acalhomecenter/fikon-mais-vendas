package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class FormaPagamento implements Serializable {

    private String Forma;
    private String Descricao;
    private int CodEve;
    private String NumAut ;
    private String FlTef ;
    private String Reseve ;
    private String FlAvista ;
    private int Parcela ;
    private int ParcelaMax ;
    private int Nrdiasvc1 ;
    private int Interdias ;
    private int Portador;
    private double Vlminprc;
    private String Flautcre;

    public double getVlminprc() {
        return Vlminprc;
    }

    public void setVlminprc(double vlminprc) {
        Vlminprc = vlminprc;
    }

    public String getFlautcre() {
        return Flautcre;
    }

    public void setFlautcre(String flautcre) {
        Flautcre = flautcre;
    }

    public void setCodEve(int codEve) {
        CodEve = codEve;
    }

    public int getPortador() {
        return Portador;
    }

    public void setPortador(int portador) {
        Portador = portador;
    }

    public String getNumAut() {
        return NumAut;
    }

    public void setNumAut(String numAut) {
        NumAut = numAut;
    }

    public String getFlTef() {
        return FlTef;
    }

    public void setFlTef(String flTef) {
        FlTef = flTef;
    }

    public String getReseve() {
        return Reseve;
    }

    public void setReseve(String reseve) {
        Reseve = reseve;
    }

    public String getFlAvista() {
        return FlAvista;
    }

    public void setFlAvista(String flAvista) {
        FlAvista = flAvista;
    }

    public int getParcela() {
        return Parcela;
    }

    public void setParcela(int parcela) {
        Parcela = parcela;
    }

    public int getParcelaMax() {
        return ParcelaMax;
    }

    public void setParcelaMax(int parcelaMax) {
        ParcelaMax = parcelaMax;
    }

    public int getNrdiasvc1() {
        return Nrdiasvc1;
    }

    public void setNrdiasvc1(int nrdiasvc1) {
        Nrdiasvc1 = nrdiasvc1;
    }

    public int getInterdias() {
        return Interdias;
    }

    public void setInterdias(int interdias) {
        Interdias = interdias;
    }

    public String getForma() {
        return Forma;
    }

    public void setForma(String forma) {
        Forma = forma;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public int getCodEve() {
        return CodEve;
    }

    public void setCodeve(int codeve) {
        CodEve = codeve;
    }
}
