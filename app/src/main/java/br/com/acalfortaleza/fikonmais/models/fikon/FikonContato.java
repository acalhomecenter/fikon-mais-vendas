package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/** Pessoa de contato de um cliente (classe Contatos da Fikon). */
public class FikonContato implements Serializable {
    @SerializedName("nome")
    private String nome;

    /** Obrigatório no cadastro. */
    @SerializedName("celular")
    private String celular;

    @SerializedName("fone")
    private String fone;

    @SerializedName("email")
    private String email;

    @SerializedName("setor")
    private String setor;

    /** Chave, nome ou código do cargo. */
    @SerializedName("cargo")
    private String cargo;

    @SerializedName("observacao")
    private String observacao;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
