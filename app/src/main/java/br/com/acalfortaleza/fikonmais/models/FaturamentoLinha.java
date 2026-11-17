package br.com.acalfortaleza.fikonmais.models;

public class FaturamentoLinha {

    public int IdConsultor;
    public int Codlinha;
    public String Linha;
    public double Meta;
    public double VlLiquida;
    public double PercMeta;
    public String Status;


    public int getIdConsultor() {
        return IdConsultor;
    }

    public void setIdConsultor(int idConsultor) {
        IdConsultor = idConsultor;
    }

    public int getCodlinha() {
        return Codlinha;
    }

    public void setCodlinha(int codlinha) {
        Codlinha = codlinha;
    }

    public String getLinha() {
        return Linha;
    }

    public void setLinha(String linha) {
        Linha = linha;
    }

    public double getMeta() {
        return Meta;
    }

    public void setMeta(double meta) {
        Meta = meta;
    }

    public double getVlLiquida() {
        return VlLiquida;
    }

    public void setVlLiquida(double vlLiquida) {
        this.VlLiquida = vlLiquida;
    }

    public double getPercMeta() {
        return PercMeta;
    }

    public void setPercMeta(double percMeta) {
        PercMeta = percMeta;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
