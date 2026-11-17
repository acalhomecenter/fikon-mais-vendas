package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Estoque implements Serializable {

    public int CodFil;
    public long FikonEstabelecimentoChave;
    public boolean EstoqueFikon;
    public String Filial;
    public String Disponivel;
    public String Fisico;
    public String Reserva;
    public String Transito;
    public String Futuro;
    public String Mostruario;
    public String Previsao;
    public String Validade;
    public List<Lotes> Lotes;
    public List<Pontas> Pontas;
}
