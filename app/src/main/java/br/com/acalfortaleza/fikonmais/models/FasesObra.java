package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class FasesObra implements Serializable {

    public int Id;
    public String Descricao;

    public String toString(){
        return Descricao;
    }
}
