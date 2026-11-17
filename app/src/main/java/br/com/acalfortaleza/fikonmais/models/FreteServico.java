package br.com.acalfortaleza.fikonmais.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FreteServico implements Serializable {
    @SerializedName("ID_SERVICO")
    private int IdServico;

    @SerializedName("SERVICO_FRETE")
    private String ServicoFrete;

    @SerializedName("VALOR_SERVICO")
    private double ValorServico;

    @SerializedName("STATUS")
    private String Status;

    @SerializedName("tipo_calculo")
    private String TipoCalculo;

    @SerializedName("obs")
    private String Obs;

    @SerializedName("limite_peso")
    private double LimitePeso;

    public int getIdServico() { return IdServico; }
    public void setIdServico(int idServico) { IdServico = idServico; }

    public String getServicoFrete() { return ServicoFrete; }
    public void setServicoFrete(String servicoFrete) { ServicoFrete = servicoFrete; }

    public double getValorServico() { return ValorServico; }
    public void setValorServico(double valorServico) { ValorServico = valorServico; }

    public String getStatus() { return Status; }
    public void setStatus(String status) { Status = status; }

    public String getTipoCalculo() { return TipoCalculo; }
    public void setTipoCalculo(String tipoCalculo) { TipoCalculo = tipoCalculo; }

    public String getObs() { return Obs; }
    public void setObs(String obs) { Obs = obs; }

    public double getLimitePeso() { return LimitePeso; }
    public void setLimitePeso(double limitePeso) { LimitePeso = limitePeso; }
}

