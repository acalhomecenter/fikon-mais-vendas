package br.com.acalfortaleza.fikonmais.models;

import com.google.gson.annotations.SerializedName;

public class LinksPagamentoResponse {
    @SerializedName("link")
    private String Link;
    
    @SerializedName("status")
    private String Status;
    
    @SerializedName("tipo")
    private String Tipo;
    
    @SerializedName("tokenLink")
    private String TokenLink;

    // Construtor padrão
    public LinksPagamentoResponse() {
    }

    // Construtor com parâmetros
    public LinksPagamentoResponse(String link, String status, String tipo, String tokenLink) {
        this.Link = link;
        this.Status = status;
        this.Tipo = tipo;
        this.TokenLink = tokenLink;
    }

    // Getters
    public String getLink() {
        return Link;
    }

    public String getStatus() {
        return Status;
    }

    public String getTipo() {
        return Tipo;
    }

    public String getTokenLink() {
        return TokenLink;
    }

    // Setters
    public void setLink(String link) {
        this.Link = link;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public void setTipo(String tipo) {
        this.Tipo = tipo;
    }

    public void setTokenLink(String tokenLink) {
        this.TokenLink = tokenLink;
    }

    @Override
    public String toString() {
        return "LinksPagamentoResponse{" +
                "Link='" + Link + '\'' +
                ", Status='" + Status + '\'' +
                ", Tipo='" + Tipo + '\'' +
                ", TokenLink='" + TokenLink + '\'' +
                '}';
    }
}