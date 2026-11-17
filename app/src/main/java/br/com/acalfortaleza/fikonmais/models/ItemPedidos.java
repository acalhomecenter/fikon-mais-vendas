package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;
import java.util.List;

public class ItemPedidos implements Serializable {

    private double Numpedven;
    private int Coditprod;
    private String Codbarras;
    private String Ncm;
    private String Linha;
    private String Descricao;
    private int Item;
    private int Codvendr;
    private double PrecoUnit;
    private double PrecoPrimario;
    private double Preco2;
    private double VlFrete;
    private double Dpi;
    private double VlDescItem;
    private double Qtcomp;
    private double Qtemb;
    private double Encargos;
    private double Icms;
    private double IcmsRed;
    private double Custo;
    private double PmargRent;
    private String Imagem;
    private String Status;
    private double Peso;
    private String Unidade;
    private String Fornecedor;
    private String Tpped ;
    private String TppedDesc ;
    private int CodFilOrig ;
    private String FilOrig ;
    private int CodFilTransf;
    private String FilTransf;
    private String Dtentrega;
    private List<Lotevencto> Lotevencto;
    private String Numpedtransf;
    private String Encomenda;
    private String Fllotevencto;
    private String CodMotivo;
    private String MotivoDesc;
    private String CodigoCupom;
    private String MultiplosLote;
    private String Isalteracao;
    private EstoqueValidacao EstoqueValidacao;
    private double QuantidadeAnterior;
    private int CodfilAnt;
    private double VldescPromo;
    private String NomePromo;
    private String MensagemPromo;
    private String CodigoPromo;
    private String LoteSelecionado;
    private String Situacao;
    private String IsMotor;
    private String BloqueioDesc;
    private List<PrecoConc> PrecoConc;
    private String UpsellTipo;
    private double PercentualComissao;
    private double PercIncentivo;

    public double getPercentualComissao() {
        return PercentualComissao;
    }

    public void setPercentualComissao(double percentualComissao) {
        PercentualComissao = percentualComissao;
    }

    public double getPercIncentivo() {
        return PercIncentivo;
    }

    public void setPercIncentivo(double percIncentivo) {
        PercIncentivo = percIncentivo;
    }

    public String getBloqueioDesc() {
        return BloqueioDesc;
    }

    public void setBloqueioDesc(String bloqueioDesc) {
        BloqueioDesc = bloqueioDesc;
    }

    public List<br.com.acalfortaleza.fikonmais.models.PrecoConc> getPrecoConc() {
        return PrecoConc;
    }

    public void setPrecoConc(List<br.com.acalfortaleza.fikonmais.models.PrecoConc> precoConc) {
        PrecoConc = precoConc;
    }

    public String getIsMotor() {
        return IsMotor;
    }

    public void setIsMotor(String isMotor) {
        IsMotor = isMotor;
    }

    public String getSituacao() {
        return Situacao;
    }

    public void setSituacao(String situacao) {
        Situacao = situacao;
    }

    public String getLoteSelecionado() {
        return LoteSelecionado;
    }

    public void setLoteSelecionado(String loteSelecionado) {
        LoteSelecionado = loteSelecionado;
    }

    public double getVldescPromo() {
        return VldescPromo;
    }

    public void setVldescPromo(double vldescPromo) {
        VldescPromo = vldescPromo;
    }

    public String getNomePromo() {
        return NomePromo;
    }

    public void setNomePromo(String nomePromo) {
        NomePromo = nomePromo;
    }

    public String getCodigoPromo() {
        return CodigoPromo;
    }

    public void setCodigoPromo(String codigoPromo) {
        CodigoPromo = codigoPromo;
    }

    public String getMensagemPromo() {
        return MensagemPromo;
    }

    public void setMensagemPromo(String mensagemPromo) {
        MensagemPromo = mensagemPromo;
    }

    public int getCodfilAnt() {
        return CodfilAnt;
    }

    public void setCodfilAnt(int codfilAnt) {
        CodfilAnt = codfilAnt;
    }

    public double getQuantidadeAnterior() {
        return QuantidadeAnterior;
    }

    public void setQuantidadeAnterior(double quantidadeAnterior) {
        QuantidadeAnterior = quantidadeAnterior;
    }
    public String getLinha() {
        return Linha;
    }

    public void setLinha(String linha) {
        Linha = linha;
    }
    public EstoqueValidacao getEstoqueValidacao() {
        return EstoqueValidacao;
    }

    public void setEstoqueValidacao(EstoqueValidacao estoqueValidacao) {
        this.EstoqueValidacao = estoqueValidacao;
    }

    public String getIsalteracao() {
        return Isalteracao;
    }

    public void setIsalteracao(String isalteracao) {
        Isalteracao = isalteracao;
    }

    public String getMultiplosLote() {
        return MultiplosLote;
    }

    public void setMultiplosLote(String multiplosLote) {
        MultiplosLote = multiplosLote;
    }

    public String getCodMotivo() {
        return CodMotivo;
    }

    public void setCodMotivo(String codMotivo) {
        CodMotivo = codMotivo;
    }

    public String getMotivoDesc() {
        return MotivoDesc;
    }

    public void setMotivoDesc(String motivoDesc) {
        MotivoDesc = motivoDesc;
    }

    public String getCodigoCupom() {
        return CodigoCupom;
    }

    public void setCodigoCupom(String codigoCupom) {
        CodigoCupom = codigoCupom;
    }

    public List<br.com.acalfortaleza.fikonmais.models.Lotevencto> getLotevencto() {
        return Lotevencto;
    }

    public void setLotevencto(List<br.com.acalfortaleza.fikonmais.models.Lotevencto> lotevencto) {
        Lotevencto = lotevencto;
    }

    public String getFllotevencto() {
        return Fllotevencto;
    }

    public void setFllotevencto(String fllotevencto) {
        Fllotevencto = fllotevencto;
    }

    public String getEncomenda() {
        return Encomenda;
    }

    public void setEncomenda(String encomenda) {
        Encomenda = encomenda;
    }

    public String getNumpedtransf() {
        return Numpedtransf;
    }

    public void setNumpedtransf(String numpedtransf) {
        Numpedtransf = numpedtransf;
    }


    public double getVlFrete() {
        return VlFrete;
    }

    public void setVlFrete(double vlFrete) {
        VlFrete = vlFrete;
    }

    public double getPreco2() {
        return Preco2;
    }

    public void setPreco2(double preco2) {
        Preco2 = preco2;
    }

    public String getDtentrega() {
        return Dtentrega;
    }

    public void setDtentrega(String dtentrega) {
        Dtentrega = dtentrega;
    }

    public int getCodFilTransf() {
        return CodFilTransf;
    }

    public void setCodFilTransf(int codFilTransf) {
        CodFilTransf = codFilTransf;
    }

    public String getTpped() {
        return Tpped;
    }

    public void setTpped(String tpped) {
        Tpped = tpped;
    }

    public String getTppedDesc() {
        return TppedDesc;
    }

    public void setTppedDesc(String tppedDesc) {
        TppedDesc = tppedDesc;
    }

    public int getCodFilOrig() {
        return CodFilOrig;
    }

    public void setCodFilOrig(int codFilOrig) {
        CodFilOrig = codFilOrig;
    }

    public String getFilOrig() {
        return FilOrig;
    }

    public void setFilOrig(String filOrig) {
        FilOrig = filOrig;
    }

    public String getFilTransf() {
        return FilTransf;
    }

    public void setFilTransf(String filTransf) {
        FilTransf = filTransf;
    }

    public String getUnidade() {
        return Unidade;
    }

    public void setUnidade(String unidade) {
        Unidade = unidade;
    }

    public String getFornecedor() {
        return Fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        Fornecedor = fornecedor;
    }

    public double getNumpedven() {
        return Numpedven;
    }

    public void setNumpedven(double numpedven) {
        Numpedven = numpedven;
    }

    public int getCoditprod() {
        return Coditprod;
    }

    public void setCoditprod(int coditprod) {
        Coditprod = coditprod;
    }

    public String getCodbarras() {
        return Codbarras;
    }

    public void setCodbarras(String codbarras) {
        Codbarras = codbarras;
    }

    public String getNcm() {
        return Ncm;
    }

    public void setNcm(String ncm) {
        Ncm = ncm;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public int getItem() {
        return Item;
    }

    public void setItem(int item) {
        Item = item;
    }

    public int getCodvendr() {
        return Codvendr;
    }

    public void setCodvendr(int codvendr) {
        Codvendr = codvendr;
    }

    public double getPrecoUnit() {
        return PrecoUnit;
    }

    public void setPrecoUnit(double precoUnit) {
        if (PrecoPrimario <= 0d && precoUnit > 0d) {
            PrecoPrimario = precoUnit;
        }
        PrecoUnit = precoUnit;
    }

    public double getPrecoPrimario() {
        return PrecoPrimario;
    }

    public void setPrecoPrimario(double precoPrimario) {
        PrecoPrimario = precoPrimario;
    }

    public double getDpi() {
        return Dpi;
    }

    public void setDpi(double dpi) {
        Dpi = dpi;
    }

    public double getVlDescItem() {
        return VlDescItem;
    }

    public void setVlDescItem(double vlDescItem) {
        VlDescItem = vlDescItem;
    }

    public double getQtcomp() {
        return Qtcomp;
    }

    public void setQtcomp(double qtcomp) {
        Qtcomp = qtcomp;
    }

    public double getQtemb() {
        return Qtemb;
    }

    public void setQtemb(double qtemb) {
        Qtemb = qtemb;
    }

    public double getEncargos() {
        return Encargos;
    }

    public void setEncargos(double encargos) {
        Encargos = encargos;
    }

    public double getIcms() {
        return Icms;
    }

    public void setIcms(double icms) {
        Icms = icms;
    }

    public double getIcmsRed() {
        return IcmsRed;
    }

    public void setIcmsRed(double icmsRed) {
        IcmsRed = icmsRed;
    }

    public double getCusto() {
        return Custo;
    }

    public void setCusto(double custo) {
        Custo = custo;
    }

    public double getPmargRent() {
        return PmargRent;
    }

    public void setPmargRent(double pmargRent) {
        PmargRent = pmargRent;
    }

    public String getImagem() {
        return Imagem;
    }

    public void setImagem(String imagem) {
        Imagem = imagem;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public double getPeso() {
        return Peso;
    }

    public void setPeso(double peso) {
        Peso = peso;
    }

    public String getUpsellTipo() {
        return UpsellTipo;
    }

    public void setUpsellTipo(String upsellTipo) {
        UpsellTipo = upsellTipo;
    }
}
