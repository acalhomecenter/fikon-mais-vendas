package br.com.acalfortaleza.fikonmais.model;

import com.google.gson.annotations.SerializedName;

public class AtualizarStatusPixRequest {
    @SerializedName("TokenLink")
    private String tokenLink;
    
    @SerializedName("Status")
    private String status;

    public AtualizarStatusPixRequest() {
    }

    public AtualizarStatusPixRequest(String tokenLink, String status) {
        this.tokenLink = tokenLink;
        this.status = status;
    }

    public String getTokenLink() {
        return tokenLink;
    }

    public void setTokenLink(String tokenLink) {
        this.tokenLink = tokenLink;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AtualizarStatusPixRequest{" +
                "tokenLink='" + tokenLink + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}