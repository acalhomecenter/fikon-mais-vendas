package br.com.acalfortaleza.fikonmais.model;

public class SalvarPixRequest {
    private String txid;
    private String pixCopiaECola;
    private double valor;
    private long numpedven;
    private int codcli;
    private int codfil;
    private String status;

    public SalvarPixRequest() {
    }

    public SalvarPixRequest(String txid, String pixCopiaECola, double valor, long numpedven, int codcli, int codfil, String status) {
        this.txid = txid;
        this.pixCopiaECola = pixCopiaECola;
        this.valor = valor;
        this.numpedven = numpedven;
        this.codcli = codcli;
        this.codfil = codfil;
        this.status = status;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getPixCopiaECola() {
        return pixCopiaECola;
    }

    public void setPixCopiaECola(String pixCopiaECola) {
        this.pixCopiaECola = pixCopiaECola;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public long getNumpedven() {
        return numpedven;
    }

    public void setNumpedven(long numpedven) {
        this.numpedven = numpedven;
    }

    public int getCodcli() {
        return codcli;
    }

    public void setCodcli(int codcli) {
        this.codcli = codcli;
    }

    public int getCodfil() {
        return codfil;
    }

    public void setCodfil(int codfil) {
        this.codfil = codfil;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}