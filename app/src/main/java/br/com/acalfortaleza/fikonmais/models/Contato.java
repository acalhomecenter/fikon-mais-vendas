package br.com.acalfortaleza.fikonmais.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

public class Contato implements Serializable {

    private int Codcli;
    private int Codend;
    private int CodContato;
    private Setor setor;
    private String NomeContato;
    private Cargo cargo;
    private String Dtnascimento;
    private String Email;
    private String Ddd;
    private String Fone;
    private String DddFone1;
    private String Fone1;
    private String DddFone2;
    private String Fone2;
    private String DddCelular;
    private String Celular;
    private String Cgccpf;
    private int FilialCadastro;
    private int Consultor;
    private String Idmoskit;
    private String CodcliGemco;
    /** Código (ou CPF/CNPJ) do cliente na Fikon, usado como codigoCliente ao cadastrar. */
    private String CodigoClienteFikon;

    public String getCodigoClienteFikon() {
        return CodigoClienteFikon;
    }

    public void setCodigoClienteFikon(String codigoClienteFikon) {
        CodigoClienteFikon = codigoClienteFikon;
    }


    public String getIdmoskit() {
        return Idmoskit;
    }

    public void setIdmoskit(String idmoskit) {
        Idmoskit = idmoskit;
    }

    public String getCodcliGemco() {
        return CodcliGemco;
    }

    public void setCodcliGemco(String codcliGemco) {
        CodcliGemco = codcliGemco;
    }

    public int getFilialCadastro() {
        return FilialCadastro;
    }

    public void setFilialCadastro(int filialCadastro) {
        FilialCadastro = filialCadastro;
    }

    public int getConsultor() {
        return Consultor;
    }

    public void setConsultor(int consultor) {
        Consultor = consultor;
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

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public String getNomeContato() {
        return NomeContato;
    }

    public void setNomeContato(String nomeContato) {
        NomeContato = nomeContato;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public String getDtnascimento() {
        return Dtnascimento;
    }

    public void setDtnascimento(String dtnascimento) {
        Dtnascimento = dtnascimento;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDdd() {
        return Ddd;
    }

    public void setDdd(String ddd) {
        Ddd = ddd;
    }

    public String getFone() {
        return Fone;
    }

    public void setFone(String fone) {
        Fone = fone;
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

    public String getCgccpf() {
        return Cgccpf;
    }

    public void setCgccpf(String cgccpf) {
        Cgccpf = cgccpf;
    }

    public int getCodContato() {
        return CodContato;
    }

    public void setCodContato(int codContato) {
        CodContato = codContato;
    }

    @NonNull
    @Override
    public String toString() {

        return getNomeContato()+" - Cpf: "+getCgccpf()+" Email: "+getEmail()+" Contato: "+getDddCelular()+getCelular();
    }

}


