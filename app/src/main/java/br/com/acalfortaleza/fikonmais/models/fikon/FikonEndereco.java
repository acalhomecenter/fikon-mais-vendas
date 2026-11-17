package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FikonEndereco implements Serializable {
    @SerializedName("cep")
    private String cep;

    @SerializedName("logradouro")
    private String logradouro;

    @SerializedName("numero")
    private String numero;

    @SerializedName("bairro")
    private String bairro;

    @SerializedName("cidade")
    private String cidade;

    @SerializedName("uf")
    private String uf;

    @SerializedName("complemento")
    private String complemento;

    @SerializedName("pontoRefer")
    private String pontoRefer;

    @SerializedName("pais")
    private String pais;

    @SerializedName("zona")
    private String zona;

    @SerializedName("tipoLogradouro")
    private String tipoLogradouro;

    @SerializedName("chave")
    private Integer chave;

    @SerializedName("entrega")
    private boolean entrega;

    @SerializedName("cobranca")
    private Boolean cobranca;

    public String getCep() {
        return cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public String getBairro() {
        return bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public String getUf() {
        return uf;
    }

    public String getComplemento() {
        return complemento;
    }

    public String getPontoRefer() {
        return pontoRefer;
    }

    public String getPais() {
        return pais;
    }

    public String getZona() {
        return zona;
    }

    public String getTipoLogradouro() {
        return tipoLogradouro;
    }

    public Integer getChave() {
        return chave;
    }

    public boolean isEntrega() {
        return entrega;
    }

    public Boolean getCobranca() {
        return cobranca;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public void setPontoRefer(String pontoRefer) {
        this.pontoRefer = pontoRefer;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public void setTipoLogradouro(String tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }

    public void setChave(Integer chave) {
        this.chave = chave;
    }

    public void setEntrega(boolean entrega) {
        this.entrega = entrega;
    }

    public void setCobranca(Boolean cobranca) {
        this.cobranca = cobranca;
    }
}
