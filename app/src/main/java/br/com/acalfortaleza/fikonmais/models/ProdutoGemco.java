package br.com.acalfortaleza.fikonmais.models;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;

public class ProdutoGemco implements Serializable {

    public int Codigo;
    public String Descricao;
    public double Preco;
    public double Preco2;
    public String Fornecedor;
    public String Linha;
    public String Situacao;
    public String Unidade;
    public String Comprador;
    public double Qtemb;
    public double Encargos;
    public double Icms;
    public double IcmsRed;
    public double Custo;
    public String Ncm;
    public double Peso;
    public String Imagem;
    public List<CodigoBarra> CodigoBarra;
    public List<ProdutoVtex.Images> Imagens;
    public String Caracteristicas;
    public int Selecao;
    public String BloqueioDesc;
    public String Incentivado;

    public String Lotevencto;
    public String Skuid;
    public List<PrecoConc> PrecoConc;
    public String AtualizacaoPreco;
    public double PrecoPor;

    public List<ProdutoGemco> Upsell;

    public double PercentualComissao;
    public double PercIncentivo;
    public double PrecoOriginalConsulta;
    public double PrecoAtualPromocional;
    public double PrecoAnteriorPromocional;
    public double DescontoPromocional;
    public String NomePromocao;
    public String VigenciaPromocao;
    public boolean PromocaoFikon;


    public static final Comparator<ProdutoGemco> ORDER_AZ = new Comparator<ProdutoGemco>() {
        @Override
        public int compare(ProdutoGemco produto, ProdutoGemco t1) {

            return produto.Descricao.compareTo(t1.Descricao);
        }
    };


    public static final Comparator<ProdutoGemco> ORDER_ZA= new Comparator<ProdutoGemco>() {
        @Override
        public int compare(ProdutoGemco produto, ProdutoGemco t1) {

            return t1.Descricao.compareTo(produto.Descricao);
        }
    };

    public static final Comparator<ProdutoGemco> ORDER_VALOR = new Comparator<ProdutoGemco>() {
        @Override
        public int compare(ProdutoGemco produto, ProdutoGemco t1) {

            return Double.compare(produto.Preco,t1.Preco);
        }
    };

    public static final Comparator<ProdutoGemco> ORDER_VALORDESC = new Comparator<ProdutoGemco>() {
        @Override
        public int compare(ProdutoGemco produto, ProdutoGemco t1) {

            return Double.compare(t1.Preco,produto.Preco);
        }
    };


}
