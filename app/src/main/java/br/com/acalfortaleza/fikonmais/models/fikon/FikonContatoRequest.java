package br.com.acalfortaleza.fikonmais.models.fikon;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/** Corpo de POST cliente/cadastrarContatoAssociado. */
public class FikonContatoRequest {
    /** Código, chave, nome ou CPF/CNPJ do cliente. */
    @SerializedName("codigoCliente")
    private final String codigoCliente;

    @SerializedName("contatos")
    private final List<FikonContato> contatos;

    public FikonContatoRequest(String codigoCliente, List<FikonContato> contatos) {
        this.codigoCliente = codigoCliente;
        this.contatos = contatos == null ? new ArrayList<FikonContato>() : contatos;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public List<FikonContato> getContatos() {
        return contatos;
    }
}
