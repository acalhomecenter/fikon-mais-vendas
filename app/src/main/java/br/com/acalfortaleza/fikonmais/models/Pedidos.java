package br.com.acalfortaleza.fikonmais.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class Pedidos implements Serializable {

    @SerializedName(value = "Numpedven", alternate = {"numpedven", "numPedido", "pedido"})
    private double Numpedven;
    private double Numdav;
    private int Codfil;
    private String Filial;
    private int Codvendr;
    private Cliente Cliente;
    @SerializedName(value = "VlTotal", alternate = {"vlTotal", "vlrTotal", "total"})
    private double VlTotal;
    @SerializedName(value = "VlDesconto", alternate = {"vlDesconto", "vlrDesconto", "desconto"})
    private double VlDesconto;
    private double Dpi;
    private String Status;
    private int CodStatus;
    private String Dtpedido;
    private CondPagamento CondPagamento;
    private FormaPagamento FormaPagamento;
    private int Qtprc;
    private double VlPrc;
    @SerializedName(value = "VlFrete", alternate = {"vlFrete", "vlrFrete", "frete"})
    private double VlFrete;
    private double VlEntrada;
    private double PmargRent;
    private String Consultor;
    private List<ItemPedidos> Itens;
    private double Peso;
    private double PesoEntrega;
    private List<Notas> Notas;
    private String Obs;
    private String ObsAdicional;
    private String Arquiteto;
    private String CodMidia;
    private int CodArq;
    private Midias Midia;
    private TpNotas Tpnota;
    private String Lote;
    private String Encomenda;
    private int Transferencia;
    private String Tpped;
    private int Praca;
    private int Endfat;
    private int Endcob;
    private int Endent;
    private int Codmodal;
    @SerializedName(value = "vlFreteSelecionado", alternate = {"VlFreteSelecionado", "vlFreteSel"})
    private double vlFreteSelecionado;
    @SerializedName(value = "vlFreteCalculado", alternate = {"VlFreteCalculado", "vlFreteCalc"})
    private double vlFreteCalculado;
    private List<Financeiro> Financeiro;
    private String Numorc;
    private List<PedTransferencia>PedTransferencias;
    private String Agendamento;
    private String Paletizado;
    private List<Recibo> Recibos;
    private String CodigoPromo;
    private String Nomepromo;
    private String MensagemPromo;
    private double VlDescPromo;
    private String isMotor;
    private String urlMotor;
    private String statusCredito;
    private boolean GerenteUsuario;
    private String TipoMotor;
    private java.util.List<Integer> ServicosAdicionaisIds;
    private java.util.List<String> ServicosAdicionaisNomes;
    @SerializedName(value = "VlServicosAdicionais", alternate = {"vlServicosAdicionais", "vlrServicosAdicionais", "servicosAdicionais"})
    private double VlServicosAdicionais;

    private VendaPerfeita IndicadoresVendaPerfeita;
    private VendaPerfeita MetasVendaPerfeita;


    public String getTipoMotor() {
        return TipoMotor;
    }

    public void setTipoMotor(String tipoMotor) {
        TipoMotor = tipoMotor;
    }

    public java.util.List<Integer> getServicosAdicionaisIds() { return ServicosAdicionaisIds; }
    public void setServicosAdicionaisIds(java.util.List<Integer> ids) { ServicosAdicionaisIds = ids; }

    public java.util.List<String> getServicosAdicionaisNomes() { return ServicosAdicionaisNomes; }
    public void setServicosAdicionaisNomes(java.util.List<String> nomes) { ServicosAdicionaisNomes = nomes; }

    public double getVlServicosAdicionais() { return VlServicosAdicionais; }
    public void setVlServicosAdicionais(double vlServicosAdicionais) { VlServicosAdicionais = vlServicosAdicionais; }

    public double getVlFreteCalculado() {
        return vlFreteCalculado;
    }

    public void setVlFreteCalculado(double vlFreteCalculado) {
        this.vlFreteCalculado = vlFreteCalculado;
    }

    public double getPesoEntrega() {
        return PesoEntrega;
    }

    public void setPesoEntrega(double pesoEntrega) {
        PesoEntrega= pesoEntrega;
    }

    public int getCodmodal() {
        return Codmodal;
    }

    public void setCodmodal(int codmodal) {
        Codmodal = codmodal;
    }

    public String getStatusCredito() {
        return statusCredito;
    }

    public void setStatusCredito(String statusCredito) {
        this.statusCredito = statusCredito;
    }

    public boolean isGerenteUsuario() {
        return GerenteUsuario;
    }

    public void setGerenteUsuario(boolean gerenteUsuario) {
        GerenteUsuario = gerenteUsuario;
    }

    public String getUrlMotor() {
        return urlMotor;
    }

    public void setUrlMotor(String urlMotor) {
        this.urlMotor = urlMotor;
    }

    public String getCodigoPromo() {
        return CodigoPromo;
    }

    public void setCodigoPromo(String codigoPromo) {
        CodigoPromo = codigoPromo;
    }

    public String getNomepromo() {
        return Nomepromo;
    }

    public void setNomepromo(String nomepromo) {
        Nomepromo = nomepromo;
    }

    public String getMensagemPromo() {
        return MensagemPromo;
    }

    public void setMensagemPromo(String mensagemPromo) {
        MensagemPromo = mensagemPromo;
    }

    public double getVlDescPromo() {
        return VlDescPromo;
    }

    public void setVlDescPromo(double vlDescPromo) {
        VlDescPromo = vlDescPromo;
    }

    public String getIsMotor() {
        return isMotor;
    }

    public void setIsMotor(String isMotor) {
        this.isMotor = isMotor;
    }

    public List<Recibo> getRecibos() {
        return Recibos;
    }

    public void setRecibos(List<Recibo> recibos) {
        Recibos = recibos;
    }

    public String getPaletizado() {
        return Paletizado;
    }

    public void setPaletizado(String paletizado) {
        Paletizado = paletizado;
    }

    public String getAgendamento() {
        return Agendamento;
    }

    public void setAgendamento(String agendamento) {
        Agendamento = agendamento;
    }

    public List<PedTransferencia> getPedTransferencias() {
        return PedTransferencias;
    }

    public void setPedTransferencias(List<PedTransferencia> pedTransferencias) {
        PedTransferencias = pedTransferencias;
    }

    public String getNumorc() {
        return Numorc;
    }

    public void setNumorc(String numorc) {
        Numorc = numorc;
    }

    public double getVlFreteSelecionado() {
        return vlFreteSelecionado;
    }

    public void setVlFreteSelecionado(double vlFreteSelecionado) {
        this.vlFreteSelecionado = vlFreteSelecionado;
    }

    public int getPraca() {
        return Praca;
    }

    public void setPraca(int praca) {
        Praca = praca;
    }

    public int getEndfat() {
        return Endfat;
    }

    public void setEndfat(int endfat) {
        Endfat = endfat;
    }

    public int getEndcob() {
        return Endcob;
    }

    public void setEndcob(int endcob) {
        Endcob = endcob;
    }

    public int getEndent() {
        return Endent;
    }

    public void setEndent(int endent) {
        Endent = endent;
    }

    public String getCodMidia() {
        return CodMidia;
    }

    public void setCodMidia(String codMidia) {
        CodMidia = codMidia;
    }

    public String getTpped() {
        return Tpped;
    }

    public void setTpped(String tpped) {
        Tpped = tpped;
    }

    public double getNumdav() {
        return Numdav;
    }

    public void setNumdav(double numdav) {
        Numdav = numdav;
    }

    public Midias getMidia() {
        return Midia;
    }

    public void setMidia(Midias midia) {
        Midia = midia;
    }

    public String getEncomenda() {
        return Encomenda;
    }

    public void setEncomenda(String encomenda) {
        Encomenda = encomenda;
    }

    public int getTransferencia() {
        return Transferencia;
    }

    public void setTransferencia(int transferencia) {
        Transferencia = transferencia;
    }

    public String getLote() {
        return Lote;
    }

    public void setLote(String lote) {
        Lote = lote;
    }

    public List<Financeiro> getFinanceiro() {
        return Financeiro;
    }

    public void setFinanceiro(List<Financeiro> Financeiro) {
        this.Financeiro = Financeiro;
    }

    public br.com.acalfortaleza.fikonmais.models.FormaPagamento getFormaPagamento() {
        return FormaPagamento;
    }

    public void setFormaPagamento(br.com.acalfortaleza.fikonmais.models.FormaPagamento formaPagamento) {
        FormaPagamento = formaPagamento;
    }

    public int getCodArq() {
        return CodArq;
    }

    public void setCodArq(int codArq) {
        CodArq = codArq;
    }

    public TpNotas getTpnota() {
        return Tpnota;
    }

    public void setTpnota(TpNotas tpnota) {
        Tpnota = tpnota;
    }

    public static final Comparator<Pedidos> ORDER_AZ = new Comparator<Pedidos>() {
        @Override
        public int compare(Pedidos pedidos, Pedidos t1) {

            return pedidos.getCliente().getNomcli().compareTo(t1.getCliente().getNomcli());
        }
    };


    public static final Comparator<Pedidos> ORDER_ZA = new Comparator<Pedidos>() {
        @Override
        public int compare(Pedidos pedidos, Pedidos t1) {

            return t1.getCliente().getNomcli().compareTo(pedidos.getCliente().getNomcli());
        }
    };

    public static final Comparator<Pedidos> ORDER_VALOR = new Comparator<Pedidos>() {
        @Override
        public int compare(Pedidos pedidos, Pedidos t1) {

            return Double.compare(pedidos.getVlTotal(),t1.getVlTotal());
        }
    };

    public static final Comparator<Pedidos> ORDER_VALORDESC = new Comparator<Pedidos>() {
        @Override
        public int compare(Pedidos pedidos, Pedidos t1) {

            return Double.compare(t1.getVlTotal(),pedidos.getVlTotal());
        }
    };

    public String getArquiteto() {
        return Arquiteto;
    }

    public void setArquiteto(String arquiteto) {
        Arquiteto = arquiteto;
    }


    public String getObs() {
        return Obs;
    }

    public void setObs(String obs) {
        Obs = obs;
    }

    public String getObsAdicional() {
        return ObsAdicional;
    }

    public void setObsAdicional(String obsAdicional) {
        ObsAdicional = obsAdicional;
    }

    public List<br.com.acalfortaleza.fikonmais.models.Notas> getNotas() {
        return Notas;
    }

    public void setNotas(List<br.com.acalfortaleza.fikonmais.models.Notas> notas) {
        Notas = notas;
    }

    public double getNumpedven() {
        return Numpedven;
    }

    public void setNumpedven(double numpedven) {
        Numpedven = numpedven;
    }

    public int getCodfil() {
        return Codfil;
    }

    public void setCodfil(int codfil) {
        Codfil = codfil;
    }

    public String getFilial() {
        return Filial;
    }

    public void setFilial(String filial) {
        Filial = filial;
    }

    public int getCodvendr() {
        return Codvendr;
    }

    public void setCodvendr(int codvendr) {
        Codvendr = codvendr;
    }

    public br.com.acalfortaleza.fikonmais.models.Cliente getCliente() {
        return Cliente;
    }

    public void setCliente(br.com.acalfortaleza.fikonmais.models.Cliente cliente) {
        Cliente = cliente;
    }

    public double getVlTotal() {
        return VlTotal;
    }

    public void setVlTotal(double vlTotal) {
        VlTotal = vlTotal;
    }

    public double getVlDesconto() {
        return VlDesconto;
    }

    public void setVlDesconto(double vlDesconto) {
        VlDesconto = vlDesconto;
    }

    public double getDpi() {
        return Dpi;
    }

    public void setDpi(double dpi) {
        Dpi = dpi;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getCodStatus() {
        return CodStatus;
    }

    public void setCodStatus(int codStatus) {
        CodStatus = codStatus;
    }

    public String getDtpedido() {
        return Dtpedido;
    }

    public void setDtpedido(String dtpedido) {
        Dtpedido = dtpedido;
    }

    public br.com.acalfortaleza.fikonmais.models.CondPagamento getCondPagamento() {
        return CondPagamento;
    }

    public void setCondPagamento(br.com.acalfortaleza.fikonmais.models.CondPagamento condPagamento) {
        CondPagamento = condPagamento;
    }

    public int getQtprc() {
        return Qtprc;
    }

    public void setQtprc(int qtprc) {
        Qtprc = qtprc;
    }

    public double getVlPrc() {
        return VlPrc;
    }

    public void setVlPrc(double vlPrc) {
        VlPrc = vlPrc;
    }

    public double getVlFrete() {
        return VlFrete;
    }

    public void setVlFrete(double vlFrete) {
        VlFrete = vlFrete;
    }

    public double getVlEntrada() {
        return VlEntrada;
    }

    public void setVlEntrada(double vlEntrada) {
        VlEntrada = vlEntrada;
    }

    public double getPmargRent() {
        return PmargRent;
    }

    public void setPmargRent(double pmargRent) {
        PmargRent = pmargRent;
    }

    public String getConsultor() {
        return Consultor;
    }

    public void setConsultor(String consultor) {
        Consultor = consultor;
    }

    public List<ItemPedidos> getItens() {
        return Itens;
    }

    public void setItens(List<ItemPedidos> itens) {
        Itens = itens;
    }

    public double getPeso() {
        return Peso;
    }

    public void setPeso(double peso) {
        Peso = peso;
    }

    public VendaPerfeita getIndicadoresVendaPerfeita() {
        return IndicadoresVendaPerfeita;
    }

    public void setIndicadoresVendaPerfeita(VendaPerfeita indicadoresVendaPerfeita) {
        IndicadoresVendaPerfeita = indicadoresVendaPerfeita;
    }

    public VendaPerfeita getMetasVendaPerfeita() {
        return MetasVendaPerfeita;
    }

    public void setMetasVendaPerfeita(VendaPerfeita metasVendaPerfeita) {
        MetasVendaPerfeita = metasVendaPerfeita;
    }

}
