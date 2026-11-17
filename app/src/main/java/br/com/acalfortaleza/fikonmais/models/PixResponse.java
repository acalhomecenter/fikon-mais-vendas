package br.com.acalfortaleza.fikonmais.models;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PixResponse {

    private Data data;
    private String payment_date;

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }

    public String getPayment_date() { return payment_date; }
    public void setPayment_date(String payment_date) { this.payment_date = payment_date; }

    public static class Data {
        private Calendario calendario;
        private Devedor devedor;
        private Loc loc;
        private String location;
        private Valor valor;
        private String chave;
        private String solicitacaoPagador;
        private String txid;
        private int revisao;
        private String status;
        private String pixCopiaECola;
        private Object pix;  // como está null, usei Object ou pode criar outra classe se souber o formato

        public Calendario getCalendario() { return calendario; }
        public void setCalendario(Calendario calendario) { this.calendario = calendario; }

        public Devedor getDevedor() { return devedor; }
        public void setDevedor(Devedor devedor) { this.devedor = devedor; }

        public Loc getLoc() { return loc; }
        public void setLoc(Loc loc) { this.loc = loc; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public Valor getValor() { return valor; }
        public void setValor(Valor valor) { this.valor = valor; }

        public String getChave() { return chave; }
        public void setChave(String chave) { this.chave = chave; }

        public String getSolicitacaoPagador() { return solicitacaoPagador; }
        public void setSolicitacaoPagador(String solicitacaoPagador) { this.solicitacaoPagador = solicitacaoPagador; }

        public String getTxid() { return txid; }
        public void setTxid(String txid) { this.txid = txid; }

        public int getRevisao() { return revisao; }
        public void setRevisao(int revisao) { this.revisao = revisao; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getPixCopiaECola() { return pixCopiaECola; }
        public void setPixCopiaECola(String pixCopiaECola) { this.pixCopiaECola = pixCopiaECola; }

        public Object getPix() { return pix; }
        public void setPix(Object pix) { this.pix = pix; }
    }

    public static class Calendario {
        private String criacao; // string com data ISO
        private int expiracao;

        public String getCriacao() { return criacao; }
        public void setCriacao(String criacao) { this.criacao = criacao; }

        public int getExpiracao() { return expiracao; }
        public void setExpiracao(int expiracao) { this.expiracao = expiracao; }
    }

    public static class Devedor {
        private String cnpj;
        private String nome;

        public String getCnpj() { return cnpj; }
        public void setCnpj(String cnpj) { this.cnpj = cnpj; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
    }

    public static class Loc {
        private String id; // <-- ALTERADO DE 'long' PARA 'String'
        private String location;
        private String tipoCob;
        private String criacao;

        // Métodos getter e setter também devem ser atualizados para String
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getTipoCob() { return tipoCob; }
        public void setTipoCob(String tipoCob) { this.tipoCob = tipoCob; }

        public String getCriacao() { return criacao; }
        public void setCriacao(String criacao) { this.criacao = criacao; }
    }

    public static class Valor {
        private String original; // no JSON é string com número decimal
        private int modalidadeAlteracao;

        public String getOriginal() { return original; }
        public void setOriginal(String original) { this.original = original; }

        public int getModalidadeAlteracao() { return modalidadeAlteracao; }
        public void setModalidadeAlteracao(int modalidadeAlteracao) { this.modalidadeAlteracao = modalidadeAlteracao; }
    }

}
