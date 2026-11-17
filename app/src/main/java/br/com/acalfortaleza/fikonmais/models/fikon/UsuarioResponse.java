package br.com.acalfortaleza.fikonmais.models.fikon;

import br.com.acalfortaleza.fikonmais.models.Usuario;

public class UsuarioResponse {
    private boolean sucesso;
    private Usuario dados;

    public boolean isSucesso() {
        return sucesso;
    }

    public Usuario getDados() {
        return dados;
    }
}
