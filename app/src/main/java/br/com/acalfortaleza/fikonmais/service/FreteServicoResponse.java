
package br.com.acalfortaleza.fikonmais.service;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;

public class FreteServicoResponse {

    @SerializedName("Pedido")
    private long pedido;

    @SerializedName("IdsSelecionados")
    private List<Integer> idsSelecionados;

    @SerializedName("ValorTotal")
    private BigDecimal valorTotal;

    // Getters
    public long getPedido() {
        return pedido;
    }

    public List<Integer> getIdsSelecionados() {
        return idsSelecionados;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }
}
