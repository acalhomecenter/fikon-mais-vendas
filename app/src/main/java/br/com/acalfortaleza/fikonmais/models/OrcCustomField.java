package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class OrcCustomField implements Serializable {

    private int Id;
    private String Value;


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
