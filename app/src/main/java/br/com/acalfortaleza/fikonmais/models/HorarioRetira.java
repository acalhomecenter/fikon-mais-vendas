package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

public class HorarioRetira implements Serializable {

    private String Horario;
    private String Vagas;


    public String getHorario() {
        return Horario;
    }

    public void setHorario(String horario) {
        Horario = horario;
    }

    public String getVagas() {
        return Vagas;
    }

    public void setVagas(String vagas) {
        Vagas = vagas;
    }
}
