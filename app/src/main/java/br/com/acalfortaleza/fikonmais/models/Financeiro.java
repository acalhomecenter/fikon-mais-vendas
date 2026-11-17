package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Financeiro implements Serializable {

    private int Numprc ;
    private int Numlan ;
    private String Evento ;
    private double Valor ;
    private String Numbco ;
    private String Numage ;
    private String Numcta ;
    private String Numchq ;
    private String Conveniada ;
    private String Condicao ;
    private String Condpgto ;
    private int CodEve;
    private int Forma ;
    private String Numaut ;
    private String Numdoc ;
    private String Dtvcto ;
    private int Numcartao ;
    private String Fltef ;
    private String Flmultneg ;
    private String Pjuros ;
    private int Qtprc ;
    private String Numautorizacao ;
    private String Tid ;
    private int Codcon;
    private double Taxadm;
    private int Portador;
    private String Flautcre;
    private int Codfilcrc;
    private String Numtit;
    private int Codorigcrc;
    private String Cgccpf;
    private String Desdtit;
    private String Codrec;

    public String getCodrec() {
        return Codrec;
    }

    public void setCodrec(String codrec) {
        Codrec = codrec;
    }

    public String getDesdtit() {
        return Desdtit;
    }

    public void setDesdtit(String desdtit) {
        Desdtit = desdtit;
    }

    public int getCodfilcrc() {
        return Codfilcrc;
    }

    public void setCodfilcrc(int codfilcrc) {
        Codfilcrc = codfilcrc;
    }

    public String getNumtit() {
        return Numtit;
    }

    public void setNumtit(String numtit) {
        Numtit = numtit;
    }

    public int getCodorigcrc() {
        return Codorigcrc;
    }

    public void setCodorigcrc(int codorigcrc) {
        Codorigcrc = codorigcrc;
    }

    public String getCgccpf() {
        return Cgccpf;
    }

    public void setCgccpf(String cgccpf) {
        Cgccpf = cgccpf;
    }

    public String getFlautcre() {
        return Flautcre;
    }

    public void setFlautcre(String flautcre) {
        Flautcre = flautcre;
    }

    public int getPortador() {
        return Portador;
    }

    public void setPortador(int portador) {
        Portador = portador;
    }

    public double getTaxadm() {
        return Taxadm;
    }

    public void setTaxadm(double taxadm) {
        Taxadm = taxadm;
    }

    public int getCodcon() {
        return Codcon;
    }

    public void setCodcon(int codcon) {
        Codcon = codcon;
    }

    public int getCodEve() {
        return CodEve;
    }

    public void setCodEve(int codeve) {
        CodEve = codeve;
    }

    public String getTid() {
        return Tid;
    }

    public void setTid(String tid) {
        Tid = tid;
    }

    public String getNumdoc() {
        return Numdoc;
    }

    public void setNumdoc(String numdoc) {
        Numdoc = numdoc;
    }

    public int getNumprc() {
        return Numprc;
    }

    public void setNumprc(int numprc) {
        Numprc = numprc;
    }

    public int getNumlan() {
        return Numlan;
    }

    public void setNumlan(int numlan) {
        Numlan = numlan;
    }

    public String getEvento() {
        return Evento;
    }

    public void setEvento(String evento) {
        Evento = evento;
    }

    public double getValor() {
        return Valor;
    }

    public void setValor(double valor) {
        Valor = valor;
    }

    public String getNumbco() {
        return Numbco;
    }

    public void setNumbco(String numbco) {
        Numbco = numbco;
    }

    public String getNumage() {
        return Numage;
    }

    public void setNumage(String numage) {
        Numage = numage;
    }

    public String getNumcta() {
        return Numcta;
    }

    public void setNumcta(String numcta) {
        Numcta = numcta;
    }

    public String getNumchq() {
        return Numchq;
    }

    public void setNumchq(String numchq) {
        Numchq = numchq;
    }

    public String getConveniada() {
        return Conveniada;
    }

    public void setConveniada(String conveniada) {
        Conveniada = conveniada;
    }

    public String getCondicao() {
        return Condicao;
    }

    public void setCondicao(String condicao) {
        Condicao = condicao;
    }

    public String getCondpgto() {
        return Condpgto;
    }

    public void setCondpgto(String condpgto) {
        Condpgto = condpgto;
    }

    public int getForma() {
        return Forma;
    }

    public void setForma(int forma) {
        Forma = forma;
    }

    public String getNumaut() {
        return Numaut;
    }

    public void setNumaut(String numaut) {
        Numaut = numaut;
    }

    public String getDtvcto() {
        return Dtvcto;
    }

    public void setDtvcto(String dtvcto) {
        Dtvcto = dtvcto;
    }

    public int getNumcartao() {
        return Numcartao;
    }

    public void setNumcartao(int numcartao) {
        Numcartao = numcartao;
    }

    public String getFltef() {
        return Fltef;
    }

    public void setFltef(String fltef) {
        Fltef = fltef;
    }

    public String getFlmultneg() {
        return Flmultneg;
    }

    public void setFlmultneg(String flmultneg) {
        Flmultneg = flmultneg;
    }

    public String getPjuros() {
        return Pjuros;
    }

    public void setPjuros(String pjuros) {
        Pjuros = pjuros;
    }

    public int getQtprc() {
        return Qtprc;
    }

    public void setQtprc(int qtprc) {
        Qtprc = qtprc;
    }

    public String getNumautorizacao() {
        return Numautorizacao;
    }

    public void setNumautorizacao(String numautorizacao) {
        Numautorizacao = numautorizacao;
    }

    public String toString(){
        return getNumprc()+" - "+ getEvento();
    }
}
