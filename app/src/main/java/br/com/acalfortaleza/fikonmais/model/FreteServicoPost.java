package br.com.acalfortaleza.fikonmais.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FreteServicoPost {
    @SerializedName("Orcamento")
    private int Orcamento;
    @SerializedName("IdsSelecionados")
    private List<Integer> IdsSelecionados;
    @SerializedName("ValorTotal")
    private double ValorTotal;

    public int getOrcamento() { return Orcamento; }
    public void setOrcamento(int orcamento) { Orcamento = orcamento; }
    public List<Integer> getIdsSelecionados() { return IdsSelecionados; }
    public void setIdsSelecionados(List<Integer> idsSelecionados) { IdsSelecionados = idsSelecionados; }
    public double getValorTotal() { return ValorTotal; }
    public void setValorTotal(double valorTotal) { ValorTotal = valorTotal; }
}
