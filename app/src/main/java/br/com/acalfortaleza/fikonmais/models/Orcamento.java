package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Orcamento implements Serializable {


    private double Numorc;
    private  int Codfil;
    private String Filial;
    private  int Codvendr ;
    private Cliente Cliente;
    private double VlTotal;
    private double VlDesconto;
    private double Dpi;
    private String Status;
    private String DtEmissao;
    private String DtValidade;
    private double NumorcGemco;
    private double Numpedven;
    private CondPagamento CondPagamento;
    private int Qtprc;
    private double VlPrc;
    private double VlEntrada;
    private double VlFrete;
    private double PmargRent;
    private String Consultor;
    private String DtInclusao;
    private String DtAlteracao;
    private List<ItemOrcamento> Itens;
    private double Peso;
    private String Arquiteto;
    private String FaseObra;
    private String CodContato;
    private String Idmoskit;
    private int Codaux;
    private  String Link_Carrinho;
    private List<OrcCustomField> OrcCustomFields;
    private int CodArq;
    private String UrlMotor;
    private String CodigoPromo;
    private String Nomepromo;
    private String MensagemPromo;
    private double VlDescPromo;
    private String isMotor;

    private double vlFreteSelecionado;
    private double VlFreteCalculado;
    private double VlServicosAdicionais;

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

    public double getVlServicosAdicionais() {
        return VlServicosAdicionais;
    }

    public void setVlServicosAdicionais(double vlServicosAdicionais) {
        VlServicosAdicionais = vlServicosAdicionais;
    }

    public double getVlFreteSelecionado() {
        return vlFreteSelecionado;
    }

    public void setVlFreteSelecionado(double vlFreteSelecionado) {
        this.vlFreteSelecionado = vlFreteSelecionado;
    }

    public double getVlFreteCalculado() {
        return VlFreteCalculado;
    }

    public void setVlFreteCalculado(double vlFreteCalculado) {
        VlFreteCalculado = vlFreteCalculado;
    }

    public int getCodArq() {
        return CodArq;
    }

    public void setCodArq(int codArq) {
        CodArq = codArq;
    }

    public String getLink_Carrinho() {
        return Link_Carrinho;
    }

    public void setLink_Carrinho(String link_Carrinho) {
        Link_Carrinho = link_Carrinho;
    }

    public int getCodaux() {
        return Codaux;
    }

    public void setCodaux(int codaux) {
        Codaux = codaux;
    }

    public List<OrcCustomField> getOrcCustomFields() {
        return OrcCustomFields;
    }

    public void setOrcCustomFields(List<OrcCustomField> orcCustomFields) {
        OrcCustomFields = orcCustomFields;
    }

    public String getIdmoskit() {
        return Idmoskit;
    }

    public void setIdmoskit(String idmoskit) {
        Idmoskit = idmoskit;
    }

    public String getCodContato() {
        return CodContato;
    }

    public void setCodContato(String codContato) {
        CodContato = codContato;
    }

    public static final Comparator<Orcamento> ORDER_AZ = new Comparator<Orcamento>() {
        @Override
        public int compare(Orcamento a, Orcamento b) {

            return a.getCliente().getNomcli().compareTo(b.getCliente().getNomcli());
        }
    };


    public static final Comparator<Orcamento> ORDER_ZA = new Comparator<Orcamento>() {
        @Override
        public int compare(Orcamento a, Orcamento b) {

            return b.getCliente().getNomcli().compareTo(a.getCliente().getNomcli());
        }
    };

    public static final Comparator<Orcamento> ORDER_VALOR = new Comparator<Orcamento>() {
        @Override
        public int compare(Orcamento a, Orcamento b) {

            return Double.compare(a.getVlTotal(),b.getVlTotal());
        }
    };

    public static final Comparator<Orcamento> ORDER_VALORDESC = new Comparator<Orcamento>() {
        @Override
        public int compare(Orcamento a, Orcamento b) {

            return Double.compare(b.getVlTotal(),a.getVlTotal());
        }
    };

    public String getArquiteto() {
        return Arquiteto;
    }

    public void setArquiteto(String arquiteto) {
        Arquiteto = arquiteto;
    }

    public String getFaseObra() {
        return FaseObra;
    }

    public void setFaseObra(String faseObra) {
        FaseObra = faseObra;
    }

    public String getDtInclusao() {
        return DtInclusao;
    }

    public void setDtInclusao(String dtInclusao) {
        DtInclusao = dtInclusao;
    }

    public double getPeso() {
        return Peso;
    }

    public void setPeso(double peso) {
        Peso = peso;
    }

    public double getVlEntrada() {
        return VlEntrada;
    }

    public void setVlEntrada(double vlEntrada) {
        VlEntrada = vlEntrada;
    }

    public br.com.acalfortaleza.fikonmais.models.CondPagamento getCondPagamento() {
        return CondPagamento;
    }

    public void setCondPagamento(br.com.acalfortaleza.fikonmais.models.CondPagamento condPagamento) {
        CondPagamento = condPagamento;
    }

    public double getNumorc() {
        return Numorc;
    }

    public void setNumorc(double numorc) {
        Numorc = numorc;
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

    public String getDtEmissao() {
        return DtEmissao;
    }

    public void setDtEmissao(String dtEmissao) {
        DtEmissao = dtEmissao;
    }

    public String getDtValidade() {
        return DtValidade;
    }

    public void setDtValidade(String dtValidade) {
        DtValidade = dtValidade;
    }

    public double getNumorcGemco() {
        return NumorcGemco;
    }

    public void setNumorcGemco(double numorcGemco) {
        NumorcGemco = numorcGemco;
    }

    public double getNumpedven() {
        return Numpedven;
    }

    public void setNumpedven(double numpedven) {
        Numpedven = numpedven;
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

    public String getDtAlteracao() {
        return DtAlteracao;
    }

    public void setDtAlteracao(String dtAlteracao) {
        DtAlteracao = dtAlteracao;
    }

    public List<ItemOrcamento> getItens() {
        return Itens;
    }

    public void setItens(List<ItemOrcamento> itens) {
        Itens = itens;
    }

    public String getUrlMotor() {
        return UrlMotor;
    }

    public void setUrlMotor(String urlMotor) {
        UrlMotor = urlMotor;
    }
}



