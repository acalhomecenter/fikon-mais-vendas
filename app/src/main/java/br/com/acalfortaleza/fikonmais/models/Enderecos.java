package br.com.acalfortaleza.fikonmais.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonEndereco;

public class Enderecos implements Serializable {



    private int Codcli;
    private int Codend;
    private String Cep;
    private String Codlograd;
    private String Ender;
    private String Numero;
    private String Bairro;
    private String Cidade;
    private String Uf;
    private String Complemento;
    private String Tpender;
    private String Referencia;
    private String DddFone1;
    private String Fone1;
    private String DddFone2;
    private String Fone2;
    private String DddCelular;
    private String Celular;
    private String Email;
    private String Cgccpf;
    private String Inscricao;
    private double Vlfrte;
    private int Praca;
    private List<Fretes> Fretes;
    private String Fldomingo;
    private String Flsegunda;
    private String Flterca;
    private String Flquarta;
    private String Flquinta;
    private String Flsexta;
    private String Flsabado;
    private transient FikonEndereco fikonMetadata;

    public FikonEndereco getFikonMetadata() {
        return fikonMetadata;
    }

    public void setFikonMetadata(FikonEndereco fikonMetadata) {
        this.fikonMetadata = fikonMetadata;
    }


    public double getVlfrte() {
        return Vlfrte;
    }

    public void setVlfrte(double vlfrte) {
        Vlfrte = vlfrte;
    }

    public List<br.com.acalfortaleza.fikonmais.models.Fretes> getFretes() {
        return Fretes;
    }

    public void setFretes(List<br.com.acalfortaleza.fikonmais.models.Fretes> fretes) {
        Fretes = fretes;
    }


    public int getPraca() {
        return Praca;
    }

    public void setPraca(int praca) {
        Praca = praca;
    }

    public String getFldomingo() {
        return Fldomingo;
    }

    public void setFldomingo(String fldomingo) {
        Fldomingo = fldomingo;
    }

    public String getFlsegunda() {
        return Flsegunda;
    }

    public void setFlsegunda(String flsegunda) {
        Flsegunda = flsegunda;
    }

    public String getFlterca() {
        return Flterca;
    }

    public void setFlterca(String flterca) {
        Flterca = flterca;
    }

    public String getFlquarta() {
        return Flquarta;
    }

    public void setFlquarta(String flquarta) {
        Flquarta = flquarta;
    }

    public String getFlquinta() {
        return Flquinta;
    }

    public void setFlquinta(String flquinta) {
        Flquinta = flquinta;
    }

    public String getFlsexta() {
        return Flsexta;
    }

    public void setFlsexta(String flsexta) {
        Flsexta = flsexta;
    }

    public String getFlsabado() {
        return Flsabado;
    }

    public void setFlsabado(String flsabado) {
        Flsabado = flsabado;
    }

    public String getCgccpf() {
        return Cgccpf;
    }

    public void setCgccpf(String cgccpf) {
        Cgccpf = cgccpf;
    }

    public String getDddFone1() {
        return DddFone1;
    }

    public void setDddFone1(String dddFone1) {
        DddFone1 = dddFone1;
    }

    public String getFone1() {
        return Fone1;
    }

    public void setFone1(String fone1) {
        Fone1 = fone1;
    }

    public String getDddFone2() {
        return DddFone2;
    }

    public void setDddFone2(String dddFone2) {
        DddFone2 = dddFone2;
    }

    public String getFone2() {
        return Fone2;
    }

    public void setFone2(String fone2) {
        Fone2 = fone2;
    }

    public String getDddCelular() {
        return DddCelular;
    }

    public void setDddCelular(String dddCelular) {
        DddCelular = dddCelular;
    }

    public String getCelular() {
        return Celular;
    }

    public void setCelular(String celular) {
        Celular = celular;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getInscricao() {
        return Inscricao;
    }

    public void setInscricao(String inscricao) {
        Inscricao = inscricao;
    }

    public String getReferencia() {
        return Referencia;
    }

    public void setReferencia(String referencia) {
        Referencia = referencia;
    }

    public String getTpender() {
        return Tpender;
    }

    public void setTpender(String tpender) {
        Tpender = tpender;
    }

    public String getBairro() {
        return Bairro;
    }

    public void setBairro(String bairro) {
        Bairro = bairro;
    }

    public int getCodcli() {
        return Codcli;
    }

    public void setCodcli(int codcli) {
        Codcli = codcli;
    }

    public int getCodend() {
        return Codend;
    }

    public void setCodend(int codend) {
        Codend = codend;
    }

    public String getCep() {
        return Cep;
    }

    public void setCep(String cep) {
        Cep = cep;
    }

    public String getCodlograd() {
        return Codlograd;
    }

    public void setCodlograd(String codlograd) {
        Codlograd = codlograd;
    }

    public String getEnder() {
        return Ender;
    }

    public void setEnder(String ender) {
        Ender = ender;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getCidade() {
        return Cidade;
    }

    public void setCidade(String cidade) {
        Cidade = cidade;
    }

    public String getUf() {
        return Uf;
    }

    public void setUf(String uf) {
        Uf = uf;
    }

    public String getComplemento() {
        return Complemento;
    }

    public void setComplemento(String complemento) {
        Complemento = complemento;
    }

    @NonNull
    @Override
    public String toString() {
        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return getCodlograd()+" "+getEnder()+" "+getNumero()+"  "+getBairro()+" "+getCidade()+"-"+getUf()+" CEP: "+getCep();
    }
}
