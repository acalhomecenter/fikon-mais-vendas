package br.com.acalfortaleza.fikonmais.models.fikon;

import java.io.Serializable;
import java.util.List;

import br.com.acalfortaleza.fikonmais.models.CodigoBarra;
import br.com.acalfortaleza.fikonmais.models.PrecoConc;
import br.com.acalfortaleza.fikonmais.models.ProdutoVtex;

public class FikonProduto implements Serializable {
    public String marcaNome;
    public String codigo;
    public List<EstoquePorLoja> estoquesPorLoja;
    public String ncm;
    public double precoVigente;
    public List<CodigoProduto> codigos;
    public List<ImagemProduto> imagens;
    public String classeNome;
    public String cor;
    public String modelo;
    public long chave;
    public String nome;
    public long chaveclasse;
    public CustosETributos custosETributos;
    public String inativo;

    public String Descricao;
    public double Preco;
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
    public List<FikonProduto> Upsell;
    public double PercentualComissao;
    public double PercIncentivo;

    public static class EstoquePorLoja implements Serializable {
        public List<ItemEstoque> itensEstoque;
        public String nomeEstabelecimento;
        public long estabelecimentoChave;
        public String Previsao;
    }

    public static class ItemEstoque implements Serializable {
        public List<Lote> lotes;
        public long recursoChave;
        public String nomeRecurso;
        public double saldoTotal;
    }

    public static class Lote implements Serializable {
        public long loteChave;
        public double quantidade;
        public String validade;
        public String nomeLote;
        public String codigoLote;
    }

    public static class CodigoProduto implements Serializable {
        public long chave;
        public String tipo;
        public String codigo;
    }

    public static class ImagemProduto implements Serializable {
        public String url;
        public boolean principal;
        public long arquivoChave;
    }

    public static class CustosETributos implements Serializable {
        public double custo;
        public double encargos;
        public double icms;
    }
}
