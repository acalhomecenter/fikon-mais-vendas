package br.com.acalfortaleza.fikonmais.models.fikon;

public class FikonClienteCadastroResponse {
    private boolean sucesso;
    private Dados dados;
    private String mensagem;

    public boolean isSucesso() {
        return sucesso;
    }

    public Dados getDados() {
        return dados;
    }

    public String getMensagem() {
        return mensagem;
    }

    public static class Dados {
        private int versao;
        private int chave;

        public int getVersao() {
            return versao;
        }

        public int getChave() {
            return chave;
        }
    }
}
