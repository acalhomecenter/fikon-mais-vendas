package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;
import java.util.List;

public class ItemOrcamento implements Serializable {

    private long Id;
    private double Numorc;
    private int Coditprod;
    private String Codbarras;
    private String Ncm;
    private String Descricao;
    private int Item;
    private int Codvendr;
    private double PrecoUnit;
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
    private String Fornecedor;
    private String Fllotevencto;
    private long Codlinha;
    private String Linha;
    private String Situacao;
    private String Skuid;
    private String CodMotivo;
    private String MotivoDesc;
    private String BloqueioDesc;
    private double VldescPromo;
    private String NomePromo;
    private String MensagemPromo;
    private String CodigoPromo;
    private String IsMotor;
    private List<PrecoConc> PrecoConc;
    private List<AmbienteProduto> ListAmbientesProdutos;
    private boolean AberturaPallet;
    private long FluigEncomenda;
    private String DataPrevisaoEncomenda;
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

    public String getIsMotor() {
        return IsMotor;
    }

    public void setIsMotor(String isMotor) {
        IsMotor = isMotor;
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

    public String getMensagemPromo() {
        return MensagemPromo;
    }

    public void setMensagemPromo(String mensagemPromo) {
        MensagemPromo = mensagemPromo;
    }

    public String getCodigoPromo() {
        return CodigoPromo;
    }

    public void setCodigoPromo(String codigoPromo) {
        CodigoPromo = codigoPromo;
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

    public String getSkuid() {
        return Skuid;
    }

    public void setSkuid(String skuid) {
        Skuid = skuid;
    }

    public String getSituacao() {
        return Situacao;
    }

    public void setSituacao(String situacao) {
        Situacao = situacao;
    }

    public long getCodlinha() {
        return Codlinha;
    }

    public void setCodlinha(long codlinha) {
        Codlinha = codlinha;
    }

    public String getLinha() {
        return Linha;
    }

    public void setLinha(String linha) {
        Linha = linha;
    }

    public String getFllotevencto() {
        return Fllotevencto;
    }

    public void setFllotevencto(String fllotevencto) {
        Fllotevencto = fllotevencto;
    }

    public double getPeso() {
        return Peso;
    }

    public void setPeso(double peso) {
        Peso = peso;
    }

    public String getFornecedor() {
        return Fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        Fornecedor = fornecedor;
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

    public double getNumorc() {
        return Numorc;
    }

    public void setNumorc(double numorc) {
        Numorc = numorc;
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
        PrecoUnit = precoUnit;
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

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public List<AmbienteProduto> getListAmbientesProdutos() {
        return ListAmbientesProdutos;
    }

    public void setListAmbientesProdutos(List<AmbienteProduto> listAmbientesProdutos) {
        ListAmbientesProdutos = listAmbientesProdutos;
    }

    public boolean isAberturaPallet() {
        return AberturaPallet;
    }

    public void setAberturaPallet(boolean aberturaPallet) {
        AberturaPallet = aberturaPallet;
    }

    public long getFluigEncomenda() {
        return FluigEncomenda;
    }

    public void setFluigEncomenda(long fluigEncomenda) {
        FluigEncomenda = fluigEncomenda;
    }

    public String getDataPrevisaoEncomenda() {
        return DataPrevisaoEncomenda;
    }

    public void setDataPrevisaoEncomenda(String dataPrevisaoEncomenda) {
        DataPrevisaoEncomenda = dataPrevisaoEncomenda;
    }

    public String getUpsellTipo() {
        return UpsellTipo;
    }

    public void setUpsellTipo(String upsellTipo) {
        UpsellTipo = upsellTipo;
    }

}
