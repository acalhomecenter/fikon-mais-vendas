package br.com.acalfortaleza.fikonmais.model;

public class AtualizarStatusPixResponse {
    private boolean sucesso;
    private String mensagem;
    
    public AtualizarStatusPixResponse() {
    }
    
    public boolean isSucesso() {
        return sucesso;
    }
    
    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }
    
    public String getMensagem() {
        return mensagem;
    }
    
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    
    @Override
    public String toString() {
        return "AtualizarStatusPixResponse{" +
                "sucesso=" + sucesso +
                ", mensagem='" + mensagem + '\'' +
                '}';
    }
}