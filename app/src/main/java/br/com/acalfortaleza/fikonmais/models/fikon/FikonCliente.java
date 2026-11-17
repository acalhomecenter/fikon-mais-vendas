package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FikonCliente implements Serializable {
    @SerializedName("codigo")
    private String codigo;

    @SerializedName("chave")
    private int chave;

    @SerializedName("cpfcnpj")
    private String cpfcnpj;

    @SerializedName("nome")
    private String nome;

    @SerializedName("Natjur")
    private String natjur;

    @SerializedName("Tpinsc")
    private String tpinsc;

    @SerializedName("Inscr")
    private String inscr;

    @SerializedName("Fantasia")
    private String fantasia;

    @SerializedName("email")
    private String email;

    @SerializedName("fone")
    private String fone;

    @SerializedName("celular")
    private String celular;

    @SerializedName("observacao")
    private String observacao;

    @SerializedName("classe")
    private String classe;

    @SerializedName("emailBoleto")
    private String emailBoleto;

    @SerializedName("enderecosAdicionais")
    private List<FikonEndereco> enderecosAdicionais;

    @SerializedName("razaoSocial")
    private String razaoSocial;

    @SerializedName("homepage")
    private String homepage;

    @SerializedName("matrizGrupo")
    private String matrizGrupo;

    @SerializedName("fiscal")
    private Fiscal fiscal;

    @SerializedName("zonaRepresentante")
    private String zonaRepresentante;

    @SerializedName("vendedor")
    private String vendedor;

    @SerializedName("enderecoPrincipal")
    private FikonEndereco enderecoPrincipal;

    @SerializedName("pessoal")
    private Pessoal pessoal;

    @SerializedName("segmento")
    private String segmento;

    @SerializedName("emailAlternativo")
    private String emailAlternativo;

    private int Codcli;
    private int Digcli;
    private String FilialCadastro;
    private String Consultor;
    private String Naturalidade;
    private String Sitcred;
    private String DtCadast;
    private String DtUltmComp;
    private String Sexo;
    private int CodCargo;
    private String Cargo;
    private String Ufnatural;
    private Double VlLimCre;
    private String DtUltAltCre;
    private int QtCompras;
    private Double VlTotCompra;
    private Double VlMaiorCompra;
    private String DtAtuCad;
    private Double VlUltCompra;
    private String DtMaiorCompra;
    private String Ddd;
    private String DddFone1;
    private String Fone1;
    private String DddFone2;
    private String Fone2;
    private String DddCelular;
    private int Qtdendereco;
    private String Dtnasc;
    private int Codvendr;
    private String Tipo;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getChave() {
        return chave;
    }

    public void setChave(int chave) {
        this.chave = chave;
    }

    public int getCodcli() {
        return Codcli;
    }

    public int getDigcli() {
        return Digcli;
    }

    public String getDtnasc() {
        return Dtnasc;
    }

    public String getSexo() {
        return Sexo;
    }

    public String getDdd() {
        return Ddd;
    }

    public void setDdd(String ddd) {
        Ddd = ddd;
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

    public String getFilialCadastro() {
        return FilialCadastro;
    }

    public void setFilialCadastro(String filialCadastro) {
        FilialCadastro = filialCadastro;
    }

    public String getConsultor() {
        return Consultor;
    }

    public void setConsultor(String consultor) {
        Consultor = consultor;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public void setDtnasc(String dtnasc) {
        Dtnasc = dtnasc;
    }

    public void setSexo(String sexo) {
        Sexo = sexo;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public String getCpfcnpj() {
        return cpfcnpj;
    }

    public void setCpfcnpj(String cpfcnpj) {
        this.cpfcnpj = cpfcnpj;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNatjur(String natjur) {
        this.natjur = natjur;
    }

    public String getNatjur() {
        return natjur;
    }

    public void setTpinsc(String tpinsc) {
        this.tpinsc = tpinsc;
    }

    public String getTpinsc() {
        return tpinsc;
    }

    public void setInscr(String inscr) {
        this.inscr = inscr;
    }

    public String getInscr() {
        return inscr;
    }

    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    public String getFantasia() {
        return fantasia;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public String getFone() {
        return fone;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCelular() {
        return celular;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public FikonEndereco getEnderecoPrincipal() {
        return enderecoPrincipal;
    }

    public void setEnderecoPrincipal(FikonEndereco enderecoPrincipal) {
        this.enderecoPrincipal = enderecoPrincipal;
    }

    public List<FikonEndereco> getEnderecosAdicionais() {
        return enderecosAdicionais;
    }

    public void setEnderecosAdicionais(List<FikonEndereco> enderecosAdicionais) {
        this.enderecosAdicionais = enderecosAdicionais;
    }

    public static class Fiscal implements Serializable {
        public String inscMunicipal;
        public String compFiscal;
        public String tipoEnteGov;
        public String validadeSuframa;
        public String suframa;
        public String inscEstadual;
        public String caracteristica;
        public String obsSuframa;
    }

    public static class Pessoal implements Serializable {
        public String emissaoRg;
        public String nacionalidade;
        public String naturalidade;
        public String cartProfissional;
        public String passaporte;
        public String rg;
        public String nomeMae;
        public String sexo;
        public String nascimento;
        public String estadoCivil;
        public String emissorRg;
        public String cnh;
        public String emissaoCartProfis;
        public String nomePai;
        public String categHabilit;
    }
}
