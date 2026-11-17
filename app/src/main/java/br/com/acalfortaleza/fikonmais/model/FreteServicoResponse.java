
package br.com.acalfortaleza.fikonmais.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FreteServicoResponse {

    @SerializedName("Pedido")
    private long pedido;

    @SerializedName("IdsSelecionados")
    private List<Integer> idsSelecionados;

    @SerializedName("ValorTotal")
    private double valorTotal;

    @SerializedName("DataCriacao")
    private String dataCriacao;

    public long getPedido() {
        return pedido;
    }

    public void setPedido(long pedido) {
        this.pedido = pedido;
    }

    public List<Integer> getIdsSelecionados() {
        return idsSelecionados;
    }

    public void setIdsSelecionados(List<Integer> idsSelecionados) {
        this.idsSelecionados = idsSelecionados;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
