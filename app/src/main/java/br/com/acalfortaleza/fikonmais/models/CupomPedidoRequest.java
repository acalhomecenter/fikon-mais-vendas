package br.com.acalfortaleza.fikonmais.models;

import com.google.gson.annotations.SerializedName;

public class CupomPedidoRequest {
    @SerializedName("numpedven")
    public long numpedven;
    @SerializedName("codcli")
    public int codcli;
    @SerializedName("statusPedido")
    public int statusPedido;
    @SerializedName("idReserva")
    public int idReserva;
    @SerializedName("codigoCupom")
    public String codigoCupom;
}
