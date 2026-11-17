package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class Arquiteto implements Serializable {

    private int Codarq;
    private String Nomearq;
    private String Cgccpf;

    public int getCodarq() {
        return Codarq;
    }

    public void setCodarq(int codarq) {
        Codarq = codarq;
    }

    public String getNomearq() {
        return Nomearq;
    }

    public void setNomearq(String nomearq) {
        Nomearq = nomearq;
    }

    public String getCgccpf() {
        return Cgccpf;
    }

    public void setCgccpf(String cgccpf) {
        Cgccpf = cgccpf;
    }

    public String toString(){
        return Nomearq;
    }
}
