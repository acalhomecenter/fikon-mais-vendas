package br.com.acalfortaleza.fikonmais.model;

import java.util.List;

public class FreteServicoSelecionadoRequest {
    private long pedido;
    private List<Integer> idsSelecionados;
    private double valorTotal;

    public long getPedido() { return pedido; }
    public void setPedido(long pedido) { this.pedido = pedido; }

    public List<Integer> getIdsSelecionados() { return idsSelecionados; }
    public void setIdsSelecionados(List<Integer> idsSelecionados) { this.idsSelecionados = idsSelecionados; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
}

