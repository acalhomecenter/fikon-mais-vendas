package br.com.acalfortaleza.fikonmais.helper;

import android.content.Context;
import android.util.Log;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.AmbienteProduto;
import br.com.acalfortaleza.fikonmais.models.FilialFikonGenco;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.NomeAmbiente;
import br.com.acalfortaleza.fikonmais.models.PrecoConc;
import br.com.acalfortaleza.fikonmais.models.PrecoDeProduto;
import br.com.acalfortaleza.fikonmais.models.PromocaoPrecoProduto;
import br.com.acalfortaleza.fikonmais.models.PromocaoPrecoRequest;
import br.com.acalfortaleza.fikonmais.models.PromocaoPrecoResponse;
import br.com.acalfortaleza.fikonmais.models.Produto.FiltrosProdutoResponse;
import br.com.acalfortaleza.fikonmais.models.ProdutoDetalhe;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.models.ProdutoVtex;
import br.com.acalfortaleza.fikonmais.models.SugestaoProdutoVtex;
import br.com.acalfortaleza.fikonmais.models.ValidacaoEstoque;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonFiltroBuscaRequest;
import br.com.acalfortaleza.fikonmais.service.ApiVtex;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProdutoHelper {

    private Context context;
    private Retrofit retrofit;
    private Retrofit retrofitVtex;
    private Retrofit retrofitPromocao;
    private OkHttpClient client;
    private List<ProdutoGemco> mListGemco;

    private List<PrecoConc> mList;
    private List<ProdutoGemco> produtosGemco = new ArrayList<>();
    private ProdutoVtex produtosVtex;
    private ProdutoVtex retornoVtex;

    private ProdutoVtex retornoVtexUpsell;






   public ProdutoHelper(){

       client = new OkHttpClient.Builder()
               .connectTimeout(30, TimeUnit.SECONDS)
               .readTimeout(30,TimeUnit.SECONDS).build();
       retrofit = new Retrofit.Builder()
               .baseUrl(ApiGemco.BASE_URL)
               .client(client)
               .addConverterFactory(GsonConverterFactory.create())
               .build();
       retrofitVtex = new Retrofit.Builder()
               .baseUrl(ApiVtex.BASE_URL)
               .client(client)
               .addConverterFactory(GsonConverterFactory.create())
               .build();
       retrofitPromocao = new Retrofit.Builder()
               .baseUrl(ApiGemco.PROMO_BASE_URL)
               .client(client)
               .addConverterFactory(GsonConverterFactory.create())
               .build();

   }

   public ProdutoHelper(Context context) {
       this();
       this.context = context.getApplicationContext();
   }



    public List<ProdutoGemco> ConsultarProduto(String descricao, String codFilPreco) throws IOException, XmlPullParserException, ParseException {

        mListGemco=new ArrayList<>();


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<ProdutoGemco>> requestProduto = service.listProdutoGemco("TEXTO", descricao, codFilPreco, "Bearer " + service.token);

        Log.d("ConsultaProdutoDebug", "Consulta texto iniciada | descricao=" + descricao + " | codFilPreco=" + codFilPreco);

        mListGemco = requestProduto.execute().body();

        Log.d("ConsultaProdutoDebug", "Consulta texto retorno | totalItens=" + (mListGemco == null ? 0 : mListGemco.size())
                + " | itensPR=" + contarItensPromocionais(mListGemco));
        aplicarPrecosPromocionais(mListGemco, codFilPreco);



        return mListGemco;

    }



    public List<ProdutoGemco> ConsultarProdutoAvancado(String fornecedor, String linha, String familia, String grupo,String subgrupo,String especificacao,String situacao , String descricao , String codforne, int codfil  ) throws IOException, XmlPullParserException, ParseException {

        mListGemco=new ArrayList<>();

        if (context != null && isBlank(subgrupo) && isBlank(especificacao) && isBlank(situacao)) {
            String chaveEstabelecimento = FikonApiClient.getEstabelecimento(context);
            FikonFiltroBuscaRequest filtro = new FikonFiltroBuscaRequest(
                    fornecedor, linha, familia, grupo, descricao, codforne,
                    isBlank(chaveEstabelecimento) ? null : Integer.valueOf(chaveEstabelecimento));
            List<ProdutoGemco> fikonProducts =
                    FikonApiClient.consultarProdutosPorFiltro(context, filtro);
            enrichFikonProducts(fikonProducts, String.valueOf(codfil));
            aplicarPrecosPromocionais(fikonProducts, String.valueOf(codfil));
            return fikonProducts;
        }

        final ApiGemco service = retrofit.create(ApiGemco.class);


// LINHA CORRIGIDA
        final Call<List<ProdutoGemco>> requestProduto = service.listProdutoGemcoAvancado(fornecedor, linha, familia, grupo, subgrupo, especificacao, situacao, descricao,codforne, codfil, "Bearer " + service.token);

        Log.d("ConsultaProdutoDebug", "Consulta avancada iniciada | fornecedor=" + fornecedor
                + " | linha=" + linha
                + " | familia=" + familia
                + " | grupo=" + grupo
                + " | subgrupo=" + subgrupo
                + " | especificacao=" + especificacao
                + " | situacao=" + situacao
                + " | descricao=" + descricao
                + " | codforne=" + codforne
                + " | codfil=" + codfil);

        mListGemco = requestProduto.execute().body();

        Log.d("ConsultaProdutoDebug", "Consulta avancada retorno | totalItens=" + (mListGemco == null ? 0 : mListGemco.size())
                + " | itensPR=" + contarItensPromocionais(mListGemco));
        aplicarPrecosPromocionais(mListGemco, String.valueOf(codfil));
        logResumoComissoes("Consulta avancada", mListGemco);


        return mListGemco;

    }

    public List<ProdutoGemco> ConsultarProdutoPorCodigo(String codigo, String tipoBusca,
                                                          String estabelecimento)
            throws IOException, XmlPullParserException, ParseException {
        if (context == null) {
            throw new IOException("Fikon product lookup requires an authenticated context");
        }
        String chaveEstabelecimento = isBlank(estabelecimento)
                ? FikonApiClient.getEstabelecimento(context) : estabelecimento;
        if (isBlank(chaveEstabelecimento) || "0".equals(chaveEstabelecimento)) {
            throw new IOException("Fikon establishment key is missing from the authenticated session");
        }

        List<ProdutoGemco> produtos = FikonApiClient.consultarProdutosPorCodigo(
                context, codigo, tipoBusca, chaveEstabelecimento);
        enrichFikonProducts(produtos, chaveEstabelecimento);
        aplicarPrecosPromocionais(produtos, chaveEstabelecimento);
        return produtos;
    }

    public FiltrosProdutoResponse GetFiltrosProdutos() throws IOException {
        if (context == null) {
            throw new IOException("Fikon product filters require an authenticated context");
        }
        return FikonApiClient.consultarFiltrosProdutos(context);
    }

    private void enrichFikonProducts(List<ProdutoGemco> products, String establishment) {
        if (products == null) {
            return;
        }
        for (ProdutoGemco product : products) {
            if (product == null) {
                continue;
            }
            if (!isBlank(product.Skuid)) {
                try {
                    ProdutoVtex vtex = ConsultarProdutoVtex(product.Skuid);
                    if (vtex != null) {
                        if (!isBlank(vtex.NameComplete)) {
                            product.Descricao = vtex.NameComplete;
                        }
                        if (vtex.Images != null) {
                            if (product.Imagens == null) {
                                product.Imagens = new ArrayList<>();
                            }
                            for (ProdutoVtex.Images image : vtex.Images) {
                                if (image != null && !isBlank(image.ImageUrl)
                                        && !containsImage(product.Imagens, image.ImageUrl)) {
                                    product.Imagens.add(image);
                                }
                            }
                        }
                    }
                } catch (IOException | XmlPullParserException | ParseException e) {
                    Log.e("ConsultaProdutoDebug",
                            "VTEX enrichment failed for Fikon product " + product.Codigo, e);
                }
            }
        }
    }

    private boolean containsImage(List<ProdutoVtex.Images> images, String url) {
        for (ProdutoVtex.Images image : images) {
            if (image != null && url.equals(image.ImageUrl)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }


    public List<ProdutoGemco> ConsultarProdutoPonta(String fornecedor, String linha, String familia, String grupo,String subgrupo,String especificacao,String situacao, int codfil) throws IOException, XmlPullParserException, ParseException {

        mListGemco=new ArrayList<>();


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<ProdutoGemco>> requestProduto = service.listProdutoPonta(fornecedor, linha, familia, grupo, subgrupo, especificacao, situacao , codfil, "Bearer " + service.token);

        mListGemco = requestProduto.execute().body();

        Log.d("ConsultaProdutoDebug", "Consulta ponta retorno | totalItens=" + (mListGemco == null ? 0 : mListGemco.size())
                + " | itensPR=" + contarItensPromocionais(mListGemco));
        aplicarPrecosPromocionais(mListGemco, String.valueOf(codfil));
        logResumoComissoes("Consulta ponta", mListGemco);

        return mListGemco;

    }


    public List<PrecoConc> consultaPrecoConc(String codigo) throws IOException, XmlPullParserException, ParseException {

        mList=new ArrayList<>();


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<PrecoConc>> request = service.listPrecoConc(codigo ,"Bearer " + service.token);

        mList = request.execute().body();


        return mList;

    }

    public PrecoDeProduto consultarPrecoDePromocao(int coditprod, int codfil) throws IOException {
        final ApiGemco service = retrofit.create(ApiGemco.class);
        final Call<PrecoDeProduto> request = service.getPrecoDePromocao(coditprod, codfil, "Bearer " + service.token);
        Log.d("PrecoDeDebug", "HTTP request | endpoint=api/v1/Produtos/PrecoDePromocao"
                + " | coditprod=" + coditprod
                + " | codfil=" + codfil);
        Response<PrecoDeProduto> response = request.execute();
        Log.d("PrecoDeDebug", "HTTP response | code=" + response.code()
                + " | bodyNull=" + (response.body() == null));
        return response.body();
    }

    public void aplicarPrecosPromocionais(List<ProdutoGemco> produtos, String codfil) {
        if (produtos == null || produtos.isEmpty()) {
            Log.d("PromocaoPrecoDebug", "Consulta promocional ignorada | lista vazia");
            return;
        }

        for (ProdutoGemco produto : produtos) {
            if (produto == null) {
                continue;
            }

            if (produto.PrecoOriginalConsulta > 0) {
                produto.Preco = produto.PrecoOriginalConsulta;
            } else {
                produto.PrecoOriginalConsulta = produto.Preco;
            }

            produto.PrecoAtualPromocional = 0d;
            produto.PrecoAnteriorPromocional = 0d;
            produto.DescontoPromocional = 0d;
            produto.NomePromocao = null;
            produto.VigenciaPromocao = null;
            produto.PromocaoFikon = false;
        }

        final Integer estabelecimento = resolverEstabelecimentoPromocional(codfil);
        if (estabelecimento == null || estabelecimento <= 0) {
            Log.w("PromocaoPrecoDebug", "Consulta promocional cancelada | codfil=" + codfil
                    + " | motivo=filial_fikon_nao_encontrada");
            return;
        }
        final List<PromocaoPrecoRequest> itens = new ArrayList<>();
        final Map<Integer, ProdutoGemco> produtosPorPosicao = new HashMap<>();
        final Map<String, ProdutoGemco> produtosPorCodigo = new HashMap<>();

        for (int i = 0; i < produtos.size(); i++) {
            ProdutoGemco produto = produtos.get(i);
            if (produto == null || produto.Codigo <= 0) {
                continue;
            }

            int posicaoItem = i + 1;
            String codigoProduto = String.valueOf(produto.Codigo).trim();
            itens.add(new PromocaoPrecoRequest(codigoProduto, posicaoItem, estabelecimento));
            produtosPorPosicao.put(posicaoItem, produto);
            produtosPorCodigo.put(codigoProduto, produto);

            if (produto.PrecoOriginalConsulta <= 0) {
                produto.PrecoOriginalConsulta = produto.Preco;
            }
        }

        if (itens.isEmpty()) {
            Log.d("PromocaoPrecoDebug", "Consulta promocional ignorada | sem itens validos");
            return;
        }

        try {
            final ApiGemco service = retrofitPromocao.create(ApiGemco.class);
            final Call<PromocaoPrecoResponse> request = service.consultarPrecosPromocionais(itens, "Bearer " + ApiGemco.PROMO_TOKEN);

            Log.d("PromocaoPrecoDebug", "POST promocao iniciado | codfil=" + codfil
                    + " | estabelecimento=" + estabelecimento
                    + " | totalItens=" + itens.size());

            Response<PromocaoPrecoResponse> response = request.execute();
            PromocaoPrecoResponse body = response.body();

            Log.d("PromocaoPrecoDebug", "POST promocao retorno | code=" + response.code()
                    + " | bodyNull=" + (body == null)
                    + " | produtosRetornados=" + (body == null || body.produtos == null ? 0 : body.produtos.size())
                    + " | mensagem=" + (body == null ? "" : body.mensagemPromocional));

            if (!response.isSuccessful() || body == null || body.produtos == null || body.produtos.isEmpty()) {
                return;
            }

            for (PromocaoPrecoProduto precoPromocional : body.produtos) {
                if (precoPromocional == null) {
                    continue;
                }

                ProdutoGemco produto = produtosPorPosicao.get(precoPromocional.posicaoItem);
                if (produto == null && precoPromocional.codigo != null) {
                    produto = produtosPorCodigo.get(precoPromocional.codigo.trim());
                }

                if (produto == null) {
                    Log.w("PromocaoPrecoDebug", "Produto promocional nao localizado | codigo=" + precoPromocional.codigo
                            + " | posicaoItem=" + precoPromocional.posicaoItem);
                    continue;
                }

                if (produto.PrecoOriginalConsulta <= 0) {
                    produto.PrecoOriginalConsulta = produto.Preco;
                }

                produto.PrecoAtualPromocional = precoPromocional.precoAtual;
                produto.PrecoAnteriorPromocional = precoPromocional.precoAnterior;
                produto.DescontoPromocional = precoPromocional.desconto;
                produto.NomePromocao = precoPromocional.nomePromocao;
                produto.VigenciaPromocao = precoPromocional.vigencia;
                produto.PromocaoFikon = true;

                Log.d("PromocaoPrecoDebug", "Preco aplicado | codigo=" + produto.Codigo
                        + " | precoOriginal=" + produto.PrecoOriginalConsulta
                        + " | precoGemco=" + produto.Preco
                        + " | precoAtualFikon=" + produto.PrecoAtualPromocional
                        + " | precoAnterior=" + produto.PrecoAnteriorPromocional
                        + " | desconto=" + produto.DescontoPromocional);
            }
        } catch (Exception ex) {
            Log.e("PromocaoPrecoDebug", "Falha ao consultar endpoint promocional | codfil=" + codfil
                    + " | estabelecimento=" + estabelecimento, ex);
        }
    }

    public PromocaoPrecoProduto consultarPrecoPromocionalProduto(int codigoProduto, String codfil) {
        if (codigoProduto <= 0) {
            Log.d("PromocaoPrecoDebug", "Consulta promocional produto ignorada | codigo invalido");
            return null;
        }

        final Integer estabelecimento = resolverEstabelecimentoPromocional(codfil);
        if (estabelecimento == null || estabelecimento <= 0) {
            Log.w("PromocaoPrecoDebug", "Consulta promocional produto cancelada | codfil=" + codfil
                    + " | motivo=filial_fikon_nao_encontrada");
            return null;
        }

        List<PromocaoPrecoRequest> itens = new ArrayList<>();
        itens.add(new PromocaoPrecoRequest(
                String.valueOf(codigoProduto).trim(),
                1,
                estabelecimento
        ));

        try {
            final ApiGemco service = retrofitPromocao.create(ApiGemco.class);

            final Call<PromocaoPrecoResponse> request =
                    service.consultarPrecosPromocionais(
                            itens,
                            "Bearer " + ApiGemco.PROMO_TOKEN
                    );

            Log.d("PromocaoPrecoDebug",
                    "POST promocao produto iniciado | codfil=" + codfil
                            + " | estabelecimento=" + estabelecimento
                            + " | codigo=" + codigoProduto);

            Response<PromocaoPrecoResponse> response = request.execute();
            PromocaoPrecoResponse body = response.body();

            Log.d("PromocaoPrecoDebug",
                    "POST promocao produto retorno | code=" + response.code()
                            + " | bodyNull=" + (body == null)
                            + " | produtosRetornados="
                            + (body == null || body.produtos == null
                            ? 0
                            : body.produtos.size())
                            + " | mensagem="
                            + (body == null ? "" : body.mensagemPromocional));

            if (!response.isSuccessful()
                    || body == null
                    || body.produtos == null
                    || body.produtos.isEmpty()) {
                return null;
            }

            for (PromocaoPrecoProduto precoPromocional : body.produtos) {
                if (precoPromocional == null) {
                    continue;
                }

                if (precoPromocional.posicaoItem == 1) {
                    return precoPromocional;
                }
            }

        } catch (Exception ex) {
            Log.e("PromocaoPrecoDebug",
                    "Falha ao consultar endpoint promocional (produto)"
                            + " | codfil=" + codfil
                            + " | estabelecimento=" + estabelecimento
                            + " | codigo=" + codigoProduto,
                    ex);
        }

        return null;
    }

    public List<PromocaoPrecoProduto> consultarPromocoesFikonPedido(List<ItemPedidos> itensPedido, String codfil) {
        List<PromocaoPrecoProduto> promocoesEncontradas = new ArrayList<>();
        if (itensPedido == null || itensPedido.isEmpty()) {
            Log.d("PromocaoPrecoDebug", "Consulta promocional pedido ignorada | lista vazia");
            return promocoesEncontradas;
        }

        final Integer estabelecimento = resolverEstabelecimentoPromocional(codfil);
        if (estabelecimento == null || estabelecimento <= 0) {
            Log.w("PromocaoPrecoDebug", "Consulta promocional pedido cancelada | codfil=" + codfil
                    + " | motivo=filial_fikon_nao_encontrada");
            return promocoesEncontradas;
        }

        final ApiGemco service = retrofitPromocao.create(ApiGemco.class);

        for (int i = 0; i < itensPedido.size(); i++) {
            ItemPedidos item = itensPedido.get(i);
            if (item == null || item.getCoditprod() <= 0) {
                continue;
            }

            int posicaoItem = item.getItem() > 0 ? item.getItem() : (i + 1);
            String codigoProduto = String.valueOf(item.getCoditprod()).trim();
            List<PromocaoPrecoRequest> itensRequest = new ArrayList<>();
            itensRequest.add(new PromocaoPrecoRequest(codigoProduto, posicaoItem, estabelecimento));

            try {
                Log.d("PromocaoPrecoDebug", "POST promocao pedido iniciado"
                        + " | codigo=" + codigoProduto
                        + " | posicaoItem=" + posicaoItem
                        + " | estabelecimento=" + estabelecimento);

                Call<PromocaoPrecoResponse> request = service.consultarPrecosPromocionais(itensRequest, "Bearer " + ApiGemco.PROMO_TOKEN);
                Response<PromocaoPrecoResponse> response = request.execute();
                PromocaoPrecoResponse body = response.body();

                Log.d("PromocaoPrecoDebug", "POST promocao pedido retorno"
                        + " | codigo=" + codigoProduto
                        + " | posicaoItem=" + posicaoItem
                        + " | code=" + response.code()
                        + " | bodyNull=" + (body == null)
                        + " | produtosRetornados=" + (body == null || body.produtos == null ? 0 : body.produtos.size()));

                if (!response.isSuccessful() || body == null || body.produtos == null || body.produtos.isEmpty()) {
                    continue;
                }

                for (PromocaoPrecoProduto promocao : body.produtos) {
                    if (promocao == null) {
                        continue;
                    }
                    if (promocao.precoAtual <= 0) {
                        continue;
                    }
                    if (vigenciaExpirada(promocao.vigencia)) {
                        Log.d("PromocaoPrecoDebug", "Promocao ignorada por vigencia expirada"
                                + " | codigo=" + codigoProduto
                                + " | posicaoItem=" + posicaoItem
                                + " | vigencia=" + promocao.vigencia);
                        continue;
                    }

                    String codigoRetornado = promocao.codigo == null ? "" : promocao.codigo.trim();
                    if (!codigoProduto.equals(codigoRetornado) && promocao.posicaoItem != posicaoItem) {
                        continue;
                    }

                    promocao.codigoPromocao = body.codigoPromocao;
                    promocao.mensagemPromocional = body.mensagemPromocional;
                    promocoesEncontradas.add(promocao);
                    break;
                }
            } catch (Exception ex) {
                Log.e("PromocaoPrecoDebug", "Falha ao consultar promocao Fikon para item do pedido"
                        + " | codigo=" + codigoProduto
                        + " | posicaoItem=" + posicaoItem, ex);
            }
        }

        return promocoesEncontradas;
    }

    private boolean vigenciaExpirada(String vigencia) {
        if (vigencia == null || vigencia.trim().isEmpty()) {
            return false;
        }

        try {
            SimpleDateFormat formatoApi = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            formatoApi.setLenient(false);

            Calendar dataVigencia = Calendar.getInstance();
            dataVigencia.setTime(formatoApi.parse(vigencia.trim()));
            zerarHorario(dataVigencia);

            Calendar hoje = Calendar.getInstance();
            zerarHorario(hoje);

            return dataVigencia.before(hoje);
        } catch (Exception ex) {
            Log.w("PromocaoPrecoDebug", "Falha ao validar vigencia da promocao | vigencia=" + vigencia, ex);
            return false;
        }
    }

    private void zerarHorario(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private Integer resolverEstabelecimentoPromocional(String codfil) {
        int filialGenco = 0;

        try {
            filialGenco = Integer.parseInt(codfil == null ? "0" : codfil.trim());
        } catch (NumberFormatException ex) {
            Log.w("PromocaoPrecoDebug", "codfil invalido para resolver estabelecimento | codfil=" + codfil, ex);
            return null;
        }

        try {
            FilialFikonGenco filialFikon = consultarFilialFikon(filialGenco);
            if (filialFikon != null && filialFikon.FilialFikon > 0) {
                Log.d("PromocaoPrecoDebug", "Estabelecimento resolvido via ApiGemco | codfil=" + filialGenco
                        + " | estabelecimento=" + filialFikon.FilialFikon
                        + " | filialNome=" + filialFikon.FilialNome);
                return filialFikon.FilialFikon;
            }
        } catch (Exception ex) {
            Log.e("PromocaoPrecoDebug", "Falha ao consultar filial Fikon na ApiGemco | codfil=" + filialGenco, ex);
        }

        Log.w("PromocaoPrecoDebug", "Mapeamento filial_fikon nao encontrado | codfil=" + filialGenco
                + " | POST promocional nao sera executado");
        return null;
    }

    public Integer resolverEstabelecimentoPedido(int codfil) {
        return resolverEstabelecimentoPromocional(String.valueOf(codfil));
    }

    public FilialFikonGenco consultarFilialFikon(int codfil) throws IOException {
        final ApiGemco service = retrofit.create(ApiGemco.class);
        final Call<FilialFikonGenco> request = service.consultarFilialFikon(codfil, "Bearer " + ApiGemco.token);

        Log.d("PromocaoPrecoDebug", "Consulta filial Fikon iniciada | endpoint=api/v1/Produtos/FilialFikon"
                + " | codfil=" + codfil);

        Response<FilialFikonGenco> response = request.execute();
        FilialFikonGenco body = response.body();

        Log.d("PromocaoPrecoDebug", "Consulta filial Fikon retorno | code=" + response.code()
                + " | bodyNull=" + (body == null)
                + " | filialFikon=" + (body == null ? 0 : body.FilialFikon));

        return body;
    }

    private int contarItensPromocionais(List<ProdutoGemco> produtos) {
        if (produtos == null || produtos.isEmpty()) {
            return 0;
        }

        int total = 0;
        for (ProdutoGemco produto : produtos) {
            if (produto != null && "PR".equals(produto.Situacao)) {
                total++;
            }
        }
        return total;
    }

    private void logResumoComissoes(String origem, List<ProdutoGemco> produtos) {
        if (produtos == null || produtos.isEmpty()) {
            Log.d("ConsultaProdutoDebug", origem + " | resumoComissoes=sem itens");
            return;
        }

        int limite = Math.min(produtos.size(), 10);
        for (int i = 0; i < limite; i++) {
            ProdutoGemco produto = produtos.get(i);
            if (produto == null) {
                continue;
            }

            Log.d("ConsultaProdutoDebug", origem
                    + " | idx=" + i
                    + " | codigo=" + produto.Codigo
                    + " | situacao=" + produto.Situacao
                    + " | percentualComissao=" + produto.PercentualComissao
                    + " | percIncentivo=" + produto.PercIncentivo
                    + " | incentivado=" + produto.Incentivado);
        }
    }





    public List<ProdutoGemco> ConsultarProdutoPorCodigo(String codigo, String codFilPreco) throws IOException, XmlPullParserException, ParseException {

        mListGemco=new ArrayList<>();
        produtosGemco=new ArrayList<>();

        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<ProdutoGemco>> requestProduto = service.listProdutoGemco("CODIGO", codigo, codFilPreco, "Bearer " + service.token);

        mListGemco = requestProduto.execute().body();

        if(mListGemco != null && mListGemco.size()>0) {

            for (ProdutoGemco p : mListGemco) {

                retornoVtex = ConsultarProdutoVtex(p.Skuid);

                if (retornoVtex != null ) {
                    p.Descricao = retornoVtex.NameComplete;

                }

                if (retornoVtex != null && retornoVtex.Images != null && retornoVtex.Images.size()>0) {
                    p.Imagens = retornoVtex.Images;
                }

                if (p.Upsell != null && p.Upsell.size() > 0) {
                for(int i = 0 ; i < p.Upsell.size(); i++){
                   if(p.Upsell.get(i) != null){
                       retornoVtexUpsell = ConsultarProdutoVtex(p.Upsell.get(i).Skuid );

                       if (retornoVtexUpsell != null ) {
                           p.Upsell.get(i).Descricao = retornoVtexUpsell.NameComplete;

                       }

                       if (retornoVtexUpsell != null && retornoVtexUpsell.Images != null && retornoVtexUpsell.Images.size()>0) {
                           p.Upsell.get(i).Imagens = retornoVtexUpsell.Images;
                       }
                   }


                }
                }
                
                produtosGemco.add(p);
            }

        }

        aplicarPrecosPromocionais(produtosGemco, codFilPreco);


        return produtosGemco;

    }

    public HashMap<Integer, ProdutoGemco> ConsultarComissoesProdutosPorCodigo(List<Integer> codigos, String codFilPreco) throws IOException {
        HashMap<Integer, ProdutoGemco> retorno = new HashMap<>();
        if (codigos == null || codigos.isEmpty()) {
            return retorno;
        }

        final ApiGemco service = retrofit.create(ApiGemco.class);
        LinkedHashSet<Integer> codigosUnicos = new LinkedHashSet<>(codigos);

        for (Integer codigo : codigosUnicos) {
            if (codigo == null || codigo <= 0) {
                continue;
            }

            final Call<List<ProdutoGemco>> requestProduto = service.listProdutoGemco("CODIGO", String.valueOf(codigo), codFilPreco, "Bearer " + service.token);
            List<ProdutoGemco> produtos = requestProduto.execute().body();

            if (produtos == null || produtos.isEmpty()) {
                Log.d("ConsultaProdutoDebug", "Consulta comissao sem retorno | codigo=" + codigo + " | codFilPreco=" + codFilPreco);
                continue;
            }

            ProdutoGemco produtoEncontrado = null;
            for (ProdutoGemco produto : produtos) {
                if (produto != null && produto.Codigo == codigo) {
                    produtoEncontrado = produto;
                    break;
                }
            }

            if (produtoEncontrado == null) {
                produtoEncontrado = produtos.get(0);
            }

            if (produtoEncontrado != null) {
                retorno.put(codigo, produtoEncontrado);
                Log.d("ConsultaProdutoDebug",
                        "Consulta comissao retorno | codigo=" + codigo
                                + " | codFilPreco=" + codFilPreco
                                + " | percentualComissao=" + produtoEncontrado.PercentualComissao
                                + " | percIncentivo=" + produtoEncontrado.PercIncentivo);
            }
        }

        return retorno;
    }


    public List<ProdutoGemco> ConsultarProdutoEstoque(String codigo, String codFilPreco) throws IOException, XmlPullParserException, ParseException {

        mListGemco=new ArrayList<>();
        produtosGemco=new ArrayList<>();

        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<ProdutoGemco>> requestProduto = service.listProdutoGemco("CODIGO", codigo, codFilPreco, "Bearer " + service.token);

        mListGemco = requestProduto.execute().body();

        aplicarPrecosPromocionais(mListGemco, codFilPreco);

        return mListGemco;

    }

    public List<ProdutoGemco> ConsultarProdutoPorBarcode(String codigo, String codFilPreco) throws IOException, XmlPullParserException, ParseException {

        mListGemco = new ArrayList<>();
        produtosGemco = new ArrayList<>();

        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<ProdutoGemco>> requestProduto = service.listProdutoGemco("BARCODE", codigo, codFilPreco , "Bearer " + service.token);

        mListGemco = requestProduto.execute().body();

        if(mListGemco != null && mListGemco.size()>0) {

            for (ProdutoGemco p : mListGemco) {

                retornoVtex = ConsultarProdutoVtex(p.Skuid);

                if (retornoVtex != null ) {
                    p.Descricao = retornoVtex.NameComplete;

                }

                if (retornoVtex != null && retornoVtex.Images.size()>0) {
                    p.Imagens = retornoVtex.Images;
                }

                produtosGemco.add(p);
            }

        }

        aplicarPrecosPromocionais(produtosGemco, codFilPreco);


        return produtosGemco;

    }


    public List<ProdutoDetalhe> GetProdutosDetalhes() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<ProdutoDetalhe>> request = service.listProdutoDetalhados(  "Bearer " + service.token);

        return request.execute().body();

    }


    public List<ProdutoDetalhe> GetProdutosDetalhesForn() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<ProdutoDetalhe>> request = service.listProdutoDetalhadosForn(  "Bearer " + service.token);

        return request.execute().body();

    }


    public List<ProdutoDetalhe> GetProdutosDetalhesLinha() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<ProdutoDetalhe>> request = service.listProdutoDetalhadosLinha(  "Bearer " + service.token);

        return request.execute().body();

    }


    public List<ProdutoDetalhe> GetProdutosDetalhesFamilia() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<ProdutoDetalhe>> request = service.listProdutoDetalhadosFamilia(  "Bearer " + service.token);

        return request.execute().body();

    }


    public List<ProdutoDetalhe> GetProdutosDetalhesGrupo() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<ProdutoDetalhe>> request = service.listProdutoDetalhadosGrupo(  "Bearer " + service.token);

        return request.execute().body();

    }


    public List<ProdutoDetalhe> GetProdutosDetalhesSbGrupo() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<ProdutoDetalhe>> request = service.listProdutoDetalhadosSbgrupo(  "Bearer " + service.token);

        return request.execute().body();

    }


    public List<ProdutoDetalhe> GetProdutosDetalhesEspecific() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<ProdutoDetalhe>> request = service.listProdutoDetalhadosEspecific(  "Bearer " + service.token);

        return request.execute().body();

    }

    public List<ProdutoDetalhe> GetProdutosDetalhesSituacao() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<ProdutoDetalhe>> request = service.listProdutoDetalhadosSituacao(  "Bearer " + service.token);

        return request.execute().body();

    }


    public ProdutoVtex ConsultarProdutoVtex(String skuid) throws IOException, XmlPullParserException, ParseException {



        final ApiVtex service = retrofitVtex.create(ApiVtex.class);

        final Call<ProdutoVtex> requestProduto = service.getProdutoVtex(skuid, service.appkey,service.token);

       try{
           produtosVtex = requestProduto.execute().body();


           return produtosVtex;

       } catch (Exception e) {
           return null;
       }

       finally {
           try {
               if (produtosVtex != null && produtosVtex.Images != null) {
                   for (int i = 0; i < produtosVtex.Images.size(); i++) {
                       if (produtosVtex.Images.get(i) != null && produtosVtex.Images.get(i).ImageUrl != null) {
                           Log.d("VTEX_IMAGES", "SKU " + skuid + " URL " + produtosVtex.Images.get(i).ImageUrl);
                       }
                   }
               } else {
                   Log.d("VTEX_IMAGES", "SKU " + skuid + " sem imagens");
               }
           } catch (Exception ignored) { }
       }

    }


    public String InsertValidacao(int filialEstoque,int filped, int codvendr,int coditprod,double qtdsolicitada,String lotvencto) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> request = service.setValidacaoEstoque(filialEstoque,filped,codvendr,coditprod ,qtdsolicitada,lotvencto,"Bearer " + service.token);

        return request.execute().body();

    }


    public List<ValidacaoEstoque> ConsultarValidacoes(int codvendr) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<ValidacaoEstoque>> request = service.getValidacaoEstoque( codvendr,"Bearer " + service.token);

        return request.execute().body();

    }


    public List<SugestaoProdutoVtex> ConsultarSugestoesProdutoVtex(String skuid) throws IOException, XmlPullParserException, ParseException{

        final ApiVtex service = retrofitVtex.create(ApiVtex.class);

        final ProdutoVtex produtoVtex = ConsultarProdutoVtex(skuid);

        final Call<List<SugestaoProdutoVtex>> request = service.getSugestoesProdutoVtex(produtoVtex.ProductId.toString(), service.appkey, service.token);

        return request.execute().body();

    }

    public List<NomeAmbiente> ConsultarNomesAmbientes(){

       try {

           ApiGemco service = retrofit.create(ApiGemco.class);

           Call<List<NomeAmbiente>> request = service.consultarNomesAmbientes("Bearer " + service.token);

           Response<List<NomeAmbiente>> response = request.execute();

           if(response.isSuccessful()){
               return response.body();
           }

           return new ArrayList<>();

       }
       catch (Exception ex){
           ex.printStackTrace();
       }

       return null;

    }

    public void AtualizarImagemVtex(int codigo, String imagem) throws IOException, XmlPullParserException, ParseException {
        final ApiGemco service = retrofit.create(ApiGemco.class);
        final Call<String> requestGet = service.atualizarImagemVtexGet(codigo, imagem, "Bearer " + service.token);
        try {
            Response<String> resp = requestGet.execute();
            String body = resp.body();
            String errorBody = null;
            if (resp.errorBody() != null) {
                errorBody = resp.errorBody().string();
            }
            android.util.Log.d("ORC_PED_IMG", "Resp atualizar imagem COD " + codigo
                    + " code " + resp.code()
                    + " sucesso " + resp.isSuccessful()
                    + " body " + String.valueOf(body)
                    + " errorBody " + String.valueOf(errorBody));
        } catch (Exception ex) {
            android.util.Log.e("ORC_PED_IMG", "Falha HTTP ao atualizar imagem COD " + codigo, ex);
            throw ex;
        }
    }


}
