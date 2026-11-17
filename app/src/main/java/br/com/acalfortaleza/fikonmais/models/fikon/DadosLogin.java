package br.com.acalfortaleza.fikonmais.models.fikon;

public class DadosLogin {
    public String login;
    public String senha;
    public String aplicacao;

    public DadosLogin(String login, String senha, String aplicacao) {
        this.login = login;
        this.senha = senha;
        this.aplicacao = aplicacao;
    }
}
