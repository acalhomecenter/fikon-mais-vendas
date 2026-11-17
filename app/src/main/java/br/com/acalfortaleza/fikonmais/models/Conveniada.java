package br.com.acalfortaleza.fikonmais.models;

public class Conveniada {

    public int Codcon;
    public String Natcon;
    public String Rescon;
    public double TaxaAdm;


    public int getCodcon() {
        return Codcon;
    }

    public void setCodcon(int codcon) {
        Codcon = codcon;
    }

    public String getNatcon() {
        return Natcon;
    }

    public void setNatcon(String natcon) {
        Natcon = natcon;
    }

    public String getRescon() {
        return Rescon;
    }

    public void setRescon(String rescon) {
        Rescon = rescon;
    }

    public double getTaxaAdm() {
        return TaxaAdm;
    }

    public void setTaxaAdm(double taxaAdm) {
        TaxaAdm = taxaAdm;
    }
}
