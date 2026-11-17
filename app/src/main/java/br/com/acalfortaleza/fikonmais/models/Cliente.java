package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import br.com.acalfortaleza.fikonmais.models.Credz.*;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonCliente;

public class Cliente implements Serializable {


    private int Codcli;
    private int Digcli;
    private String Natjur;
    private String Cgccpf;
    private String Tpinsc;
    private String Inscr;
    private String Nomcli;
    private String Fantasia;
    private String Numdoc;
    private String Emissor;
    private String UfEmissor;
    private String FilialCadastro;
    private String Consultor;
    private String Naturalidade;
    private String Sitcred;
    private String DtCadast;
    private String DtUltmComp;
    private String Sexo;
    private int CodCargo;
    private String Cargo;
    private String Ufnatural;
    private Double VlLimCre;
    private String DtUltAltCre;
    private int QtCompras;
    private Double VlTotCompra;
    private Double VlMaiorCompra;
    private String DtAtuCad;
    private Double VlUltCompra;
    private String DtMaiorCompra;
    private String Email;
    private String Ddd;
    private String Fone;
    private String DddFone1;
    private String Fone1;
    private String DddFone2;
    private String Fone2;
    private String DddCelular;
    private String Celular;
    private int Qtdendereco;
    private String Dtnasc;
    private int Codvendr;
    private String Tipo;
    private String Obs;
    private List<Enderecos> Enderecos;
    private List<Contato> Contatos;
    private DadosAdcionais DadosAdcionais;
    private CampanhaCliente Campanha;
    private Double Saldo;
    private Segmento Segmento;
    private Qualification qualification;
    private boolean InvitationEnviado;
    private Boolean temCartaoAcal;
    private int Meses;
    private String Profissao;
    private transient FikonCliente fikonMetadata;

    public FikonCliente getFikonMetadata() {
        return fikonMetadata;
    }

    public void setFikonMetadata(FikonCliente fikonMetadata) {
        this.fikonMetadata = fikonMetadata;
    }

    private void writeObject(ObjectOutputStream output) throws IOException {
        output.defaultWriteObject();
        output.writeObject(fikonMetadata);
    }

    private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
        input.defaultReadObject();
        fikonMetadata = (FikonCliente) input.readObject();
    }



    public String getProfissao() {
        return Profissao;
    }

    public void setProfissao(String profissao) {
        Profissao = profissao;
    }

    public int getMeses() {
        return Meses;
    }

    public void setMeses(int meses) {
        Meses = meses;
    }

    public br.com.acalfortaleza.fikonmais.models.Segmento getSegmento() {
        return Segmento;
    }

    public void setSegmento(br.com.acalfortaleza.fikonmais.models.Segmento segmento) {
        Segmento = segmento;
    }

    public Double getSaldo() {
        return Saldo;
    }

    public void setSaldo(Double saldo) {
        Saldo = saldo;
    }

    public CampanhaCliente getCampanha() {
        return Campanha;
    }

    public void setCampanha(CampanhaCliente campanha) {
        Campanha = campanha;
    }

    public int getCodCargo() {
        return CodCargo;
    }

    public void setCodCargo(int codCargo) {
        CodCargo = codCargo;
    }

    public List<Contato> getContatos() {
        return Contatos;
    }

    public void setContatos(List<Contato> contatos) {
        Contatos = contatos;
    }

    public br.com.acalfortaleza.fikonmais.models.DadosAdcionais getDadosAdcionais() {
        return DadosAdcionais;
    }

    public void setDadosAdcionais(br.com.acalfortaleza.fikonmais.models.DadosAdcionais dadosAdcionais) {
        DadosAdcionais = dadosAdcionais;
    }

    public int getDigcli() {
        return Digcli;
    }

    public void setDigcli(int digcli) {
        Digcli = digcli;
    }

    public String getConsultor() {
        return Consultor;
    }

    public void setConsultor(String consultor) {
        Consultor = consultor;
    }

    public List<Enderecos> getEnderecos() {
        return Enderecos;
    }

    public void setEnderecos(List<Enderecos> Enderecos) {
        this.Enderecos = Enderecos;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public int getCodvendr() {
        return Codvendr;
    }

    public void setCodvendr(int codvendr) {
        Codvendr = codvendr;
    }

    public String getDtnasc() {
        return Dtnasc;
    }

    public void setDtnasc(String dtnasc) {
        Dtnasc = dtnasc;
    }

    public int getQtdendereco() {
        return Qtdendereco;
    }

    public void setQtdendereco(int qtdendereco) {
        Qtdendereco = qtdendereco;
    }

    public int getCodcli() {
        return Codcli;
    }

    public void setCodcli(int codcli) {
        Codcli = codcli;
    }

    public String getNatjur() {
        return Natjur;
    }

    public void setNatjur(String natjur) {
        Natjur = natjur;
    }

    public String getCgccpf() {
        return Cgccpf;
    }

    public void setCgccpf(String cgccpf) {
        Cgccpf = cgccpf;
    }

    public String getTpinsc() {
        return Tpinsc;
    }

    public void setTpinsc(String tpinsc) {
        Tpinsc = tpinsc;
    }

    public String getInscr() {
        return Inscr;
    }

    public void setInscr(String inscr) {
        Inscr = inscr;
    }

    public String getNomcli() {
        return Nomcli;
    }

    public void setNomcli(String nomcli) {
        Nomcli = nomcli;
    }

    public String getFantasia() {
        return Fantasia;
    }

    public void setFantasia(String fantasia) {
        Fantasia = fantasia;
    }

    public String getNumdoc() {
        return Numdoc;
    }

    public void setNumdoc(String numdoc) {
        Numdoc = numdoc;
    }

    public String getEmissor() {
        return Emissor;
    }

    public void setEmissor(String emissor) {
        Emissor = emissor;
    }

    public String getUfEmissor() {
        return UfEmissor;
    }

    public void setUfEmissor(String ufEmissor) {
        UfEmissor = ufEmissor;
    }

    public String getFilialCadastro() {
        return FilialCadastro;
    }

    public void setFilialCadastro(String filialCadastro) {
        FilialCadastro = filialCadastro;
    }

    public String getNaturalidade() {
        return Naturalidade;
    }

    public void setNaturalidade(String naturalidade) {
        Naturalidade = naturalidade;
    }

    public String getSitcred() {
        return Sitcred;
    }

    public void setSitcred(String sitcred) {
        Sitcred = sitcred;
    }

    public String getDtCadast() {
        return DtCadast;
    }

    public void setDtCadast(String dtCadast) {
        DtCadast = dtCadast;
    }

    public String getDtUltmComp() {
        return DtUltmComp;
    }

    public void setDtUltmComp(String dtUltmComp) {
        DtUltmComp = dtUltmComp;
    }

    public String getSexo() {
        return Sexo;
    }

    public void setSexo(String sexo) {
        Sexo = sexo;
    }

    public String getCargo() {
        return Cargo;
    }

    public void setCargo(String cargo) {
        Cargo = cargo;
    }

    public String getUfnatural() {
        return Ufnatural;
    }

    public void setUfnatural(String ufnatural) {
        Ufnatural = ufnatural;
    }

    public Double getVlLimCre() {
        return VlLimCre;
    }

    public void setVlLimCre(Double vlLimCre) {
        VlLimCre = vlLimCre;
    }

    public String getDtUltAltCre() {
        return DtUltAltCre;
    }

    public void setDtUltAltCre(String dtUltAltCre) {
        DtUltAltCre = dtUltAltCre;
    }

    public int getQtCompras() {
        return QtCompras;
    }

    public void setQtCompras(int qtCompras) {
        QtCompras = qtCompras;
    }

    public Double getVlTotCompra() {
        return VlTotCompra;
    }

    public void setVlTotCompra(Double vlTotCompra) {
        VlTotCompra = vlTotCompra;
    }

    public Double getVlMaiorCompra() {
        return VlMaiorCompra;
    }

    public void setVlMaiorCompra(Double vlMaiorCompra) {
        VlMaiorCompra = vlMaiorCompra;
    }

    public String getDtAtuCad() {
        return DtAtuCad;
    }

    public void setDtAtuCad(String dtAtuCad) {
        DtAtuCad = dtAtuCad;
    }

    public Double getVlUltCompra() {
        return VlUltCompra;
    }

    public void setVlUltCompra(Double vlUltCompra) {
        VlUltCompra = vlUltCompra;
    }

    public String getDtMaiorCompra() {
        return DtMaiorCompra;
    }

    public void setDtMaiorCompra(String dtMaiorCompra) {
        DtMaiorCompra = dtMaiorCompra;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDdd() {
        return Ddd;
    }

    public void setDdd(String ddd) {
        Ddd = ddd;
    }

    public String getFone() {
        return Fone;
    }

    public void setFone(String fone) {
        Fone = fone;
    }

    public String getDddFone1() {
        return DddFone1;
    }

    public void setDddFone1(String dddFone1) {
        DddFone1 = dddFone1;
    }

    public String getFone1() {
        return Fone1;
    }

    public void setFone1(String fone1) {
        Fone1 = fone1;
    }

    public String getDddFone2() {
        return DddFone2;
    }

    public void setDddFone2(String dddFone2) {
        DddFone2 = dddFone2;
    }

    public String getFone2() {
        return Fone2;
    }

    public void setFone2(String fone2) {
        Fone2 = fone2;
    }

    public String getDddCelular() {
        return DddCelular;
    }

    public void setDddCelular(String dddCelular) {
        DddCelular = dddCelular;
    }

    public String getCelular() {
        return Celular;
    }

    public void setCelular(String celular) {
        Celular = celular;
    }

    public String getObs() {
        return Obs;
    }

    public void setObs(String obs) {
        this.Obs = obs;
    }

    public Qualification getQualification() {
        return qualification;
    }

    public void setQualification(Qualification qualification) {
        this.qualification = qualification;
    }

    public boolean isInvitationEnviado() {
        return InvitationEnviado;
    }

    public void setInvitationEnviado(boolean invitationEnviado) {
        InvitationEnviado = invitationEnviado;
    }

    public Boolean isTemCartaoAcal() {
        return temCartaoAcal;
    }

    public void setTemCartaoAcal(Boolean temCartaoAcal) {
        this.temCartaoAcal = temCartaoAcal;
    }

}
