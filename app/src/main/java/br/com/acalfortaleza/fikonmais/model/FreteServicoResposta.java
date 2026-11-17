// Crie este novo arquivo: br/com/acalfortaleza/acalmais/model/FreteServicoResposta.java

package br.com.acalfortaleza.fikonmais.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FreteServicoResposta {

    @SerializedName("Id")
    private int id;

    @SerializedName("Orcamento")
    private int orcamento;

    // O campo que nos interessa
    @SerializedName("ValorTotal")
    private double valorTotal;

    @SerializedName("IdsSelecionados")
    private List<Integer> idsSelecionados;

    // Não precisamos mapear todos os campos, apenas os que vamos usar.
    // Mas se quiser mapear os outros, pode adicioná-los aqui.

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<Integer> getIdsSelecionados() {
        return idsSelecionados;
    }

    public void setIdsSelecionados(List<Integer> idsSelecionados) {
        this.idsSelecionados = idsSelecionados;
    }

    // Getters e setters para os outros campos se precisar
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(int orcamento) {
        this.orcamento = orcamento;
    }
}
