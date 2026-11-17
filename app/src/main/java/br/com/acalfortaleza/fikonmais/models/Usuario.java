package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Usuario implements Serializable {

    @SerializedName("id")
    private int Id;
    @SerializedName("nome")
    private String Nome;
    @SerializedName("matricula")
    private String Matricula;
    @SerializedName("nascimento")
    private String Nascimento;
    @SerializedName("cpf")
    private String Cpf;
    @SerializedName("login")
    private String Login;
    @SerializedName("senha")
    private String Senha;
    @SerializedName("filial")
    private String Filial;
    @SerializedName("area")
    private String Area;
    @SerializedName("setor")
    private String Setor;
    @SerializedName("cargo")
    private String Cargo;
    @SerializedName("codigo")
    private String Codigo;
    private String LoginFluig;
    private String token;
    private String consultor;
    private int classe;
    private String usuario;
    private int chaveUsuario;
    private String celular;
    private String email;
    @SerializedName(value = "chaveestabelecimento",
            alternate = {"chaveEstabelecimento", "chave_estabelecimento"})
    private int chaveestabelecimento;
    private String imprimeorc;
    private double percfrete;
    private String isgerente;
    private double margem;
    private double percdesconto;
    @SerializedName("codigo_estabelecimento")
    private String codigoEstabelecimento;
    private String estabelecimento;
    private String mensagem;
    @SerializedName("tipo_usuario")
    private String tipoUsuario;
    private String base;

    public String getLoginFluig() {
        return LoginFluig;
    }

    public void setLoginFluig(String loginFluig) {
        LoginFluig = loginFluig;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getMatricula() {
        return Matricula;
    }

    public void setMatricula(String matricula) {
        Matricula = matricula;
    }

    public String getNascimento() {
        return Nascimento;
    }

    public void setNascimento(String nascimento) {
        Nascimento = nascimento;
    }

    public String getCpf() {
        return Cpf;
    }

    public void setCpf(String cpf) {
        Cpf = cpf;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public String getSenha() {
        return Senha;
    }

    public void setSenha(String senha) {
        Senha = senha;
    }

    public String getFilial() {
        return Filial;
    }

    public void setFilial(String filial) {
        Filial = filial;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getSetor() {
        return Setor;
    }

    public void setSetor(String setor) {
        Setor = setor;
    }

    public String getCargo() {
        return Cargo;
    }

    public void setCargo(String cargo) {
        Cargo = cargo;
    }

    public String getCodigo() {
        return Codigo;
    }

    public void setCodigo(String codigo) {
        Codigo = codigo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getConsultor() {
        return consultor;
    }

    public void setConsultor(String consultor) {
        this.consultor = consultor;
    }

    public int getClasse() {
        return classe;
    }

    public void setClasse(int classe) {
        this.classe = classe;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getChaveUsuario() {
        return chaveUsuario;
    }

    public void setChaveUsuario(int chaveUsuario) {
        this.chaveUsuario = chaveUsuario;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getChaveestabelecimento() {
        return chaveestabelecimento;
    }

    public void setChaveestabelecimento(int chaveestabelecimento) {
        this.chaveestabelecimento = chaveestabelecimento;
    }

    public String getImprimeorc() {
        return imprimeorc;
    }

    public void setImprimeorc(String imprimeorc) {
        this.imprimeorc = imprimeorc;
    }

    public double getPercfrete() {
        return percfrete;
    }

    public void setPercfrete(double percfrete) {
        this.percfrete = percfrete;
    }

    public String getIsgerente() {
        return isgerente;
    }

    public void setIsgerente(String isgerente) {
        this.isgerente = isgerente;
    }

    public double getMargem() {
        return margem;
    }

    public void setMargem(double margem) {
        this.margem = margem;
    }

    public double getPercdesconto() {
        return percdesconto;
    }

    public void setPercdesconto(double percdesconto) {
        this.percdesconto = percdesconto;
    }

    public String getCodigoEstabelecimento() {
        return codigoEstabelecimento;
    }

    public void setCodigoEstabelecimento(String codigoEstabelecimento) {
        this.codigoEstabelecimento = codigoEstabelecimento;
    }

    public String getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(String estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }
}
