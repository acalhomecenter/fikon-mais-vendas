package br.com.acalfortaleza.fikonmais.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.com.acalfortaleza.fikonmais.FikonApp;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.CondPagamento;
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonChaveRequest;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonCondicao;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonDocumento;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonDocumentoRequest;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonOrcamentoResumo;
import br.com.acalfortaleza.fikonmais.models.fikon.FikonPagamento;
import br.com.acalfortaleza.fikonmais.service.ApiFikon;
import retrofit2.Response;

/**
 * Orçamentos e pedidos na Fikon (rotas comercial/... e pedidos).
 *
 * Fluxo: POST comercial/orcamentos -> PUT comercial/editar (enquanto o cliente decide)
 * -> POST comercial/orcamentos/pedido -> PUT comercial/pedidos/gravar (pagamentos)
 * -> PUT comercial/pedidos/aprovar.
 *
 * Os métodos devolvem os modelos antigos (Orcamento, ItemOrcamento) para as telas não mudarem.
 */
public final class FikonVendasClient {
    /** Texto que as telas antigas tratam como falha ao salvar o orçamento. */
    public static final String ORCAMENTO_NAO_CADASTRADO = "Orcamento não cadastrado!";

    private static final String TAG = "FikonVendasClient";
    private static final String PREFERENCES_NAME = "usuario.salvo";
    /** Código do vendedor na Fikon (ex.: V0042), gravado no login. */
    public static final String KEY_CODIGO_VENDEDOR = "codigoFikon";

    private static final Gson GSON = new Gson();
    private static volatile String ultimoErro;

    private FikonVendasClient() {
    }

    /** true quando há sessão Fikon: os helpers antigos devem usar esta classe. */
    public static boolean ativo() {
        Context context = FikonApp.context();
        return context != null && FikonApiClient.authorization(context) != null;
    }

    /** Mensagem do último erro devolvido pela Fikon, para mostrar ao usuário. */
    public static String ultimoErro() {
        return ultimoErro;
    }

    // ---------- Orçamento ----------

    /**
     * Cria (Numorc == 0) ou edita o orçamento.
     *
     * @return a chave do orçamento como texto, ou {@link #ORCAMENTO_NAO_CADASTRADO}
     */
    public static String salvarOrcamento(Orcamento orcamento) {
        ultimoErro = null;
        Context context = FikonApp.context();
        if (context == null || orcamento == null) {
            return falha("Sessão Fikon indisponível.");
        }
        String authorization = FikonApiClient.authorization(context);
        if (authorization == null) {
            return falha("Sessão Fikon expirada. Faça login novamente.");
        }

        long chave = (long) orcamento.getNumorc();
        FikonDocumentoRequest body = new FikonDocumentoRequest();
        body.itens = toItens(orcamento.getItens());
        if (body.itens.isEmpty()) {
            return falha("Inclua ao menos um item no orçamento.");
        }
        body.frete = orcamento.getVlFrete() > 0 ? orcamento.getVlFrete() : null;

        try {
            ApiFikon api = FikonApiClient.api(context);
            Response<JsonObject> response;
            if (chave > 0) {
                // Editar substitui todos os itens e só altera o cabeçalho enviado.
                body.chcriacao = chave;
                response = api.editarDocumento(body, authorization).execute();
            } else {
                String cliente = FikonApiClient.codigoClienteFikon(orcamento.getCliente());
                if (FikonApiClient.isBlank(cliente)) {
                    return falha("Selecione o cliente do orçamento.");
                }
                body.cliente = cliente;
                body.vendedor = codigoVendedor(context);
                body.chaveEstabelecimento = chaveEstabelecimento(context);
                Date agora = new Date();
                body.emissao = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(agora);
                body.emissaohora = new SimpleDateFormat("HH:mm:ss", Locale.ROOT).format(agora);
                response = api.criarOrcamento(body, authorization).execute();
            }

            if (!response.isSuccessful() || response.body() == null) {
                return falha(FikonApiClient.errorMessage(response, null),
                        "salvar orçamento HTTP " + response.code());
            }
            FikonDocumento documento = primeiroDocumento(response.body().get("data"));
            long salvo = documento != null && documento.idPedido > 0 ? documento.idPedido : chave;
            if (salvo <= 0) {
                return falha("A Fikon não devolveu o número do orçamento.");
            }
            return String.valueOf(salvo);
        } catch (IOException e) {
            Log.e(TAG, "Fikon budget save request failed", e);
            return falha("Falha de comunicação ao salvar o orçamento.");
        }
    }

    /** Detalhe de um orçamento, já no modelo antigo, numa lista de 0 ou 1 item. */
    public static List<Orcamento> consultarOrcamento(String chave) {
        List<Orcamento> lista = new ArrayList<>();
        long chcriacao = parseLong(chave);
        Context context = FikonApp.context();
        if (chcriacao <= 0 || context == null) {
            return lista;
        }
        String authorization = FikonApiClient.authorization(context);
        if (authorization == null) {
            return null;
        }
        try {
            Response<JsonObject> response = FikonApiClient.api(context)
                    .consultarOrcamento(chcriacao, authorization).execute();
            if (response.code() == 404) {
                return lista;
            }
            if (!response.isSuccessful() || response.body() == null) {
                falha(FikonApiClient.errorMessage(response, null),
                        "consultar orçamento HTTP " + response.code());
                return null;
            }
            FikonDocumento documento = primeiroDocumento(response.body().get("data"));
            if (documento != null) {
                lista.add(toOrcamento(context, documento, true));
            }
            return lista;
        } catch (IOException e) {
            Log.e(TAG, "Fikon budget lookup request failed", e);
            return null;
        }
    }

    /**
     * Pesquisa da tela de orçamentos: "CODIGO" = número do orçamento; "TEXTO" = cliente
     * (código, nome ou CPF/CNPJ). A lista vem resumida (sem itens).
     */
    public static List<Orcamento> pesquisarOrcamentos(String consulta, String tipo) {
        if ("CODIGO".equalsIgnoreCase(tipo) || "CODIO".equalsIgnoreCase(tipo)) {
            return consultarOrcamento(consulta);
        }
        return listar(null, null, consulta);
    }

    /** Orçamentos do vendedor logado no período (datas em yyyy-MM-dd ou dd/MM/yyyy). */
    public static List<Orcamento> listarOrcamentos(String dataInicial, String dataFinal) {
        return listar(normalizarData(dataInicial), normalizarData(dataFinal), null);
    }

    private static List<Orcamento> listar(String dataInicial, String dataFinal, String pessoa) {
        Context context = FikonApp.context();
        if (context == null) {
            return null;
        }
        String authorization = FikonApiClient.authorization(context);
        if (authorization == null) {
            return null;
        }
        try {
            String representante = pessoa == null ? codigoVendedor(context) : null;
            Response<JsonObject> response = FikonApiClient.api(context)
                    .listarOrcamentos(dataInicial, dataFinal, pessoa, representante, null,
                            authorization)
                    .execute();
            if (!response.isSuccessful() || response.body() == null) {
                falha(FikonApiClient.errorMessage(response, null),
                        "listar orçamentos HTTP " + response.code());
                return null;
            }
            List<Orcamento> lista = new ArrayList<>();
            JsonElement data = response.body().get("data");
            if (data != null && data.isJsonArray()) {
                for (JsonElement element : data.getAsJsonArray()) {
                    FikonOrcamentoResumo resumo = GSON.fromJson(element, FikonOrcamentoResumo.class);
                    if (resumo != null) {
                        lista.add(toOrcamento(resumo));
                    }
                }
            }
            return lista;
        } catch (IOException e) {
            Log.e(TAG, "Fikon budget list request failed", e);
            return null;
        }
    }

    // ---------- Condições de pagamento ----------

    private static final Map<String, FikonCondicao> CONDICOES = new HashMap<>();

    /**
     * Condições que o App pode oferecer, convertidas para o modelo das telas:
     * Condpgto = chave da condição na Fikon, Descricao = nome.
     */
    public static List<CondPagamento> listarCondicoesPagamento() throws IOException {
        Context context = FikonApp.context();
        String authorization = context == null ? null : FikonApiClient.authorization(context);
        if (authorization == null) {
            throw new IOException("Sessão Fikon expirada. Faça login novamente.");
        }
        Response<JsonObject> response = FikonApiClient.api(context)
                .pesquisarCondicoes(authorization).execute();
        if (!response.isSuccessful() || response.body() == null) {
            String mensagem = FikonApiClient.errorMessage(response, null);
            throw new IOException(FikonApiClient.isBlank(mensagem)
                    ? "Falha ao consultar as condições de pagamento (HTTP " + response.code() + ")"
                    : mensagem);
        }
        List<CondPagamento> lista = new ArrayList<>();
        JsonElement condicoes = response.body().get("condicoes");
        synchronized (CONDICOES) {
            CONDICOES.clear();
            if (condicoes != null && condicoes.isJsonArray()) {
                for (JsonElement element : condicoes.getAsJsonArray()) {
                    FikonCondicao condicao = GSON.fromJson(element, FikonCondicao.class);
                    if (condicao == null || condicao.chave == 0) {
                        continue;
                    }
                    String chave = String.valueOf(condicao.chave);
                    CONDICOES.put(chave, condicao);
                    CondPagamento cond = new CondPagamento();
                    cond.setCondpgto(chave);
                    cond.setDescricao(condicao.nome);
                    lista.add(cond);
                }
            }
        }
        return lista;
    }

    /** Condição já carregada por {@link #listarCondicoesPagamento()}, pela chave. */
    public static FikonCondicao condicao(String chave) {
        if (chave == null) {
            return null;
        }
        synchronized (CONDICOES) {
            return CONDICOES.get(chave.trim());
        }
    }

    // ---------- Pedido ----------

    /** Texto que PopupPedFechamento trata como falha ao salvar o pedido. */
    public static final String PEDIDO_NAO_CADASTRADO = "Pedido não cadastrado!";

    private static volatile String ultimaPendencia;

    /** Pendência comercial/crédito da última aprovação (pedido gravado mas não aprovado). */
    public static String ultimaPendencia() {
        return ultimaPendencia;
    }

    /**
     * Cria o pedido a partir do orçamento (substitui o InicializaPedido do sistema antigo).
     * Antes, regrava os itens do orçamento com os do pedido montado no app.
     *
     * @return chave do pedido, ou 0 (ver {@link #ultimoErro()})
     */
    public static long iniciarPedido(String numorc, Pedidos pedido) {
        long chaveOrcamento = parseLong(numorc);
        if (chaveOrcamento <= 0) {
            falha("Gere o pedido a partir de um orçamento salvo.");
            return 0;
        }
        return transformarEmPedido(chaveOrcamento);
    }

    /**
     * Fecha o pedido: atualiza itens e frete, grava com as formas de pagamento e aprova.
     *
     * @return chave do pedido como texto; em falha, {@link #PEDIDO_NAO_CADASTRADO} + motivo
     */
    public static String salvarPedido(Pedidos pedido) {
        ultimoErro = null;
        ultimaPendencia = null;
        if (pedido == null) {
            return PEDIDO_NAO_CADASTRADO + " Pedido vazio.";
        }
        long chavePedido = (long) pedido.getNumpedven();
        if (chavePedido <= 0) {
            chavePedido = iniciarPedido(pedido.getNumorc(), pedido);
            if (chavePedido <= 0) {
                return falhaPedido();
            }
        }

        Context context = FikonApp.context();
        String authorization = context == null ? null : FikonApiClient.authorization(context);
        if (authorization == null) {
            falha("Sessão Fikon expirada. Faça login novamente.");
            return falhaPedido();
        }

        // 1. Itens e frete como estão no app (o editar substitui todos os itens).
        FikonDocumentoRequest edicao = new FikonDocumentoRequest();
        edicao.chcriacao = chavePedido;
        edicao.itens = toItensPedido(pedido.getItens());
        if (edicao.itens.isEmpty()) {
            falha("Inclua ao menos um item no pedido.");
            return falhaPedido();
        }
        // A Fikon não tem campo de serviços adicionais: eles entram no frete para o total
        // bater com as formas de pagamento.
        double frete = Math.max(0d, pedido.getVlFrete()) + Math.max(0d, pedido.getVlServicosAdicionais());
        edicao.frete = arredondar(frete);
        if (!FikonApiClient.isBlank(pedido.getObs())) {
            edicao.observacao = pedido.getObs();
        }
        List<FikonPagamento> pagamentos = toPagamentos(pedido.getFinanceiro());
        // No pedido, editar com itens novos e sem pagamentos é recusado se o total mudou;
        // os pagamentos vão juntos para refazer a negociação.
        edicao.pagamentos = pagamentos.isEmpty() ? null : pagamentos;
        try {
            Response<JsonObject> response = FikonApiClient.api(context)
                    .editarDocumento(edicao, authorization).execute();
            if (!response.isSuccessful()) {
                falha(FikonApiClient.errorMessage(response, null),
                        "editar pedido HTTP " + response.code());
                return falhaPedido();
            }
        } catch (IOException e) {
            Log.e(TAG, "Fikon order edit request failed", e);
            falha("Falha de comunicação ao atualizar o pedido.");
            return falhaPedido();
        }

        // 2. Gravar: cria a negociação e valida o estoque.
        if (gravarPedido(chavePedido, pagamentos) == null) {
            return falhaPedido();
        }

        // 3. Aprovar: libera para o caixa, ou registra a pendência comercial/crédito.
        Aprovacao aprovacao = aprovarPedido(chavePedido);
        if (aprovacao == null) {
            // Gravado, mas a aprovação falhou: o pedido existe e pode ser aprovado depois.
            ultimaPendencia = "Pedido gravado, mas não aprovado: " + ultimoErro;
        } else if (!aprovacao.aprovado) {
            ultimaPendencia = FikonApiClient.isBlank(aprovacao.pendencia)
                    ? "Pedido aguardando liberação." : aprovacao.pendencia;
        }
        return String.valueOf(chavePedido);
    }

    private static String falhaPedido() {
        return PEDIDO_NAO_CADASTRADO + (FikonApiClient.isBlank(ultimoErro) ? "" : " " + ultimoErro);
    }

    private static List<FikonDocumentoRequest.Item> toItensPedido(List<ItemPedidos> itens) {
        List<FikonDocumentoRequest.Item> result = new ArrayList<>();
        if (itens == null) {
            return result;
        }
        for (ItemPedidos item : itens) {
            if (item == null || item.getCoditprod() <= 0 || item.getQtcomp() <= 0) {
                continue;
            }
            FikonDocumentoRequest.Item target = new FikonDocumentoRequest.Item();
            target.idProduto = String.valueOf(item.getCoditprod());
            target.quantidade = item.getQtcomp();
            double unitario = item.getPrecoUnit() - Math.max(0d, item.getVlDescItem());
            target.unitario = arredondar(Math.max(0d, unitario));
            if (!FikonApiClient.isBlank(item.getLoteSelecionado())) {
                target.lote = item.getLoteSelecionado();
            }
            result.add(target);
        }
        return result;
    }

    /** Financeiro das telas (Condpgto = chave da condição Fikon) -> pagamentos da Fikon. */
    private static List<FikonPagamento> toPagamentos(List<Financeiro> financeiro) {
        List<FikonPagamento> pagamentos = new ArrayList<>();
        if (financeiro == null) {
            return pagamentos;
        }
        for (Financeiro fin : financeiro) {
            if (fin == null || fin.getValor() <= 0) {
                continue;
            }
            FikonCondicao condicao = condicao(fin.getCondpgto());
            FikonPagamento pagamento = new FikonPagamento();
            pagamento.valor = arredondar(fin.getValor());
            if (condicao != null) {
                pagamento.chaveCondNeg = condicao.chave;
                pagamento.tipo = condicao.codigoTipoDocumento();
                pagamento.rede = condicao.rede;
            }
            if (fin.getQtprc() > 0) {
                pagamento.qtdParcelas = fin.getQtprc();
            }
            pagamentos.add(pagamento);
        }
        return pagamentos;
    }

    /** Resultado da aprovação: aprovado=false com pendência quando a política ou o crédito seguram. */
    public static class Aprovacao {
        public boolean aprovado;
        public String pendencia;
        public FikonDocumento pedido;
    }

    /**
     * Transforma o orçamento em pedido (ou atualiza o pedido já atrelado).
     *
     * @return chave do pedido, ou 0 em caso de falha (ver {@link #ultimoErro()})
     */
    public static long transformarEmPedido(long chaveOrcamento) {
        JsonElement data = executarComercial("transformar em pedido", chaveOrcamento,
                Operacao.TRANSFORMAR, null);
        if (data == null || !data.isJsonObject()) {
            return 0;
        }
        FikonDocumento pedido = documento(data.getAsJsonObject().get("pedido"));
        return pedido == null ? 0 : pedido.idPedido;
    }

    /** Cria a negociação e valida o estoque. Sem pagamentos, usa os guardados no orçamento. */
    public static FikonDocumento gravarPedido(long chavePedido, List<FikonPagamento> pagamentos) {
        return documento(executarComercial("gravar pedido", chavePedido,
                Operacao.GRAVAR, pagamentos));
    }

    public static Aprovacao aprovarPedido(long chavePedido) {
        JsonElement data = executarComercial("aprovar pedido", chavePedido,
                Operacao.APROVAR, null);
        if (data == null || !data.isJsonObject()) {
            return null;
        }
        JsonObject json = data.getAsJsonObject();
        Aprovacao aprovacao = new Aprovacao();
        aprovacao.aprovado = json.has("aprovado") && !json.get("aprovado").isJsonNull()
                && json.get("aprovado").getAsBoolean();
        aprovacao.pendencia = json.has("pendencia") && !json.get("pendencia").isJsonNull()
                ? json.get("pendencia").getAsString() : null;
        aprovacao.pedido = documento(json.get("pedido"));
        return aprovacao;
    }

    public static FikonDocumento desaprovarPedido(long chavePedido) {
        return documento(executarComercial("desaprovar pedido", chavePedido,
                Operacao.DESAPROVAR, null));
    }

    /** Detalhe do pedido (aceita também a chave de uma baixa ou devolução). */
    public static FikonDocumento consultarPedido(long idPedido) {
        ultimoErro = null;
        Context context = FikonApp.context();
        String authorization = context == null ? null : FikonApiClient.authorization(context);
        if (authorization == null) {
            falha("Sessão Fikon expirada. Faça login novamente.");
            return null;
        }
        try {
            Response<JsonObject> response = FikonApiClient.api(context)
                    .consultarPedido(idPedido, authorization).execute();
            if (!response.isSuccessful() || response.body() == null) {
                falha(FikonApiClient.errorMessage(response, null),
                        "consultar pedido HTTP " + response.code());
                return null;
            }
            return documento(response.body().get("dadosVenda"));
        } catch (IOException e) {
            Log.e(TAG, "Fikon order lookup request failed", e);
            falha("Falha de comunicação ao consultar o pedido.");
            return null;
        }
    }

    /** Exclui pedido ou orçamento não aprovado. @return mensagem para o usuário */
    public static String excluirPedido(long idPedido) {
        ultimoErro = null;
        Context context = FikonApp.context();
        String authorization = context == null ? null : FikonApiClient.authorization(context);
        if (authorization == null) {
            falha("Sessão Fikon expirada. Faça login novamente.");
            return ultimoErro;
        }
        try {
            Response<JsonObject> response = FikonApiClient.api(context)
                    .excluirPedido(idPedido, authorization).execute();
            if (!response.isSuccessful() || response.body() == null) {
                falha(FikonApiClient.errorMessage(response, null),
                        "excluir pedido HTTP " + response.code());
                return ultimoErro;
            }
            JsonElement mensagem = response.body().get("mensagem");
            return mensagem != null && mensagem.isJsonPrimitive()
                    ? mensagem.getAsString() : "Pedido excluído com sucesso.";
        } catch (IOException e) {
            Log.e(TAG, "Fikon order delete request failed", e);
            falha("Falha de comunicação ao excluir o pedido.");
            return ultimoErro;
        }
    }

    private enum Operacao { TRANSFORMAR, GRAVAR, APROVAR, DESAPROVAR }

    private static JsonElement executarComercial(String descricao, long chave, Operacao operacao,
                                                 List<FikonPagamento> pagamentos) {
        ultimoErro = null;
        Context context = FikonApp.context();
        String authorization = context == null ? null : FikonApiClient.authorization(context);
        if (authorization == null) {
            falha("Sessão Fikon expirada. Faça login novamente.");
            return null;
        }
        if (chave <= 0) {
            falha("Documento inválido para " + descricao + ".");
            return null;
        }
        FikonChaveRequest body = new FikonChaveRequest(chave,
                pagamentos == null || pagamentos.isEmpty() ? null : pagamentos);
        ApiFikon api = FikonApiClient.api(context);
        try {
            Response<JsonObject> response;
            switch (operacao) {
                case TRANSFORMAR:
                    response = api.transformarEmPedido(body, authorization).execute();
                    break;
                case GRAVAR:
                    response = api.gravarPedido(body, authorization).execute();
                    break;
                case APROVAR:
                    response = api.aprovarPedido(body, authorization).execute();
                    break;
                default:
                    response = api.desaprovarPedido(body, authorization).execute();
                    break;
            }
            if (!response.isSuccessful() || response.body() == null) {
                falha(FikonApiClient.errorMessage(response, null),
                        descricao + " HTTP " + response.code());
                return null;
            }
            return response.body().get("data");
        } catch (IOException e) {
            Log.e(TAG, "Fikon " + descricao + " request failed", e);
            falha("Falha de comunicação ao " + descricao + ".");
            return null;
        }
    }

    // ---------- Conversões ----------

    /**
     * Itens do app -> itens da Fikon. O unitário vai já com o desconto do item
     * (PrecoUnit - VlDescItem), por isso o cabeçalho não leva "desconto".
     */
    private static List<FikonDocumentoRequest.Item> toItens(List<ItemOrcamento> itens) {
        List<FikonDocumentoRequest.Item> result = new ArrayList<>();
        if (itens == null) {
            return result;
        }
        for (ItemOrcamento item : itens) {
            if (item == null || item.getCoditprod() <= 0 || item.getQtcomp() <= 0) {
                continue;
            }
            FikonDocumentoRequest.Item target = new FikonDocumentoRequest.Item();
            target.idProduto = String.valueOf(item.getCoditprod());
            target.quantidade = item.getQtcomp();
            double unitario = item.getPrecoUnit() - Math.max(0d, item.getVlDescItem());
            target.unitario = arredondar(Math.max(0d, unitario));
            result.add(target);
        }
        return result;
    }

    private static Orcamento toOrcamento(Context context, FikonDocumento source,
                                         boolean completarDados) {
        Orcamento orcamento = new Orcamento();
        orcamento.setNumorc(source.idPedido);
        orcamento.setDtEmissao(source.dataEmissao);
        orcamento.setDtInclusao(source.dataEmissao);
        orcamento.setFilial(source.nomeEstabelecimento);
        orcamento.setCodfil(parseInt(source.estabelecimento));
        orcamento.setConsultor(source.vendedor);
        orcamento.setVlDesconto(0d);
        orcamento.setVlFrete(0d);
        if (source.pedidoAtrelado != null && source.pedidoAtrelado.chcriacao > 0) {
            orcamento.setNumpedven(source.pedidoAtrelado.chcriacao);
            orcamento.setStatus("Concluido");
        } else {
            orcamento.setStatus("Pendente");
        }

        Cliente cliente = null;
        if (completarDados && !FikonApiClient.isBlank(source.codigoCliente)) {
            List<Cliente> clientes = FikonApiClient.consultarClientes(context, source.codigoCliente);
            if (clientes != null && !clientes.isEmpty()) {
                cliente = clientes.get(0);
            }
        }
        if (cliente == null) {
            cliente = clienteSoNome(source.codigoCliente, source.nomeCliente);
        }
        orcamento.setCliente(cliente);

        List<ItemOrcamento> itens = new ArrayList<>();
        double total = 0d;
        if (source.produtos != null) {
            String estabelecimento = FikonApiClient.getEstabelecimento(context);
            Map<String, ProdutoGemco> cache = new HashMap<>();
            int posicao = 1;
            for (FikonDocumento.Produto produto : source.produtos) {
                if (produto == null) {
                    continue;
                }
                ItemOrcamento item = toItem(context, produto, estabelecimento,
                        completarDados ? cache : null);
                item.setNumorc(source.idPedido);
                item.setItem(posicao++);
                item.setId(produto.chaveItem);
                itens.add(item);
                total += produto.total > 0 ? produto.total : produto.unitario * produto.quantidadeTotal;
            }
        }
        orcamento.setItens(itens);
        orcamento.setVlTotal(arredondar(total > 0 ? total : source.totalNegociado));
        return orcamento;
    }

    /**
     * Item da Fikon -> ItemOrcamento. Com cache != null, busca o produto (GET recurso) para
     * trazer custo, tributos, código de barras e imagem, que o detalhe do orçamento não traz.
     */
    private static ItemOrcamento toItem(Context context, FikonDocumento.Produto source,
                                        String estabelecimento, Map<String, ProdutoGemco> cache) {
        ProdutoGemco produto = null;
        if (cache != null && !FikonApiClient.isBlank(source.codigo)) {
            if (cache.containsKey(source.codigo)) {
                produto = cache.get(source.codigo);
            } else {
                List<ProdutoGemco> encontrados = FikonApiClient.consultarProdutosPorCodigo(
                        context, source.codigo, "CODIGO", estabelecimento);
                produto = encontrados != null && !encontrados.isEmpty() ? encontrados.get(0) : null;
                cache.put(source.codigo, produto);
            }
        }

        ItemOrcamento item;
        if (produto != null) {
            item = new OrcamentoHelper().setItemOrcamento(source.quantidadeTotal, produto,
                    FikonApiClient.isBlank(source.nome) ? "" : source.nome,
                    produto.Imagem);
        } else {
            item = new ItemOrcamento();
            item.setCoditprod(parseInt(source.codigo));
            item.setDescricao(source.nome);
            item.setCodbarras(source.codigo);
            item.setQtemb(1d);
            item.setImagem("");
        }
        item.setQtcomp(source.quantidadeTotal);
        item.setPrecoUnit(source.unitario);
        item.setVlDescItem(0d);
        item.setStatus(source.statusItemNome);
        return item;
    }

    private static Orcamento toOrcamento(FikonOrcamentoResumo source) {
        Orcamento orcamento = new Orcamento();
        orcamento.setNumorc(source.chcriacao);
        orcamento.setDtEmissao(source.emissao);
        orcamento.setDtInclusao(source.emissao);
        orcamento.setVlTotal(source.totalNegociado);
        orcamento.setVlDesconto(0d);
        orcamento.setVlFrete(0d);
        if (source.estabelecimento != null) {
            orcamento.setFilial(source.estabelecimento.nome);
        }
        if (source.representante != null) {
            orcamento.setConsultor(source.representante.nome);
        }
        orcamento.setCliente(clienteSoNome(null,
                source.pessoa == null ? null : source.pessoa.nome));
        if (source.cancelado) {
            orcamento.setStatus("Cancelado");
        } else if (source.pedidoAtrelado != null && source.pedidoAtrelado.chcriacao > 0) {
            orcamento.setNumpedven(source.pedidoAtrelado.chcriacao);
            orcamento.setStatus("Concluido");
        } else {
            orcamento.setStatus("Pendente");
        }
        orcamento.setItens(new ArrayList<ItemOrcamento>());
        return orcamento;
    }

    /** Cliente mínimo para as listas: as telas leem nome e chamam equals em Natjur. */
    private static Cliente clienteSoNome(String codigo, String nome) {
        Cliente cliente = new Cliente();
        cliente.setNomcli(nome == null ? "" : nome);
        cliente.setCgccpf("");
        cliente.setNatjur("F");
        if (!FikonApiClient.isBlank(codigo)) {
            br.com.acalfortaleza.fikonmais.models.fikon.FikonCliente metadata =
                    new br.com.acalfortaleza.fikonmais.models.fikon.FikonCliente();
            metadata.setCodigo(codigo);
            cliente.setFikonMetadata(metadata);
        }
        return cliente;
    }

    /** "data" pode vir como objeto ou como lista com um documento. */
    private static FikonDocumento primeiroDocumento(JsonElement data) {
        if (data == null || data.isJsonNull()) {
            return null;
        }
        if (data.isJsonArray()) {
            JsonArray array = data.getAsJsonArray();
            return array.size() == 0 ? null : documento(array.get(0));
        }
        return documento(data);
    }

    private static FikonDocumento documento(JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        return GSON.fromJson(element, FikonDocumento.class);
    }

    private static String codigoVendedor(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        String codigo = preferences.getString(KEY_CODIGO_VENDEDOR, null);
        return FikonApiClient.isBlank(codigo) ? preferences.getString("codigo", null) : codigo;
    }

    private static Integer chaveEstabelecimento(Context context) {
        int chave = parseInt(FikonApiClient.getEstabelecimento(context));
        return chave > 0 ? chave : null;
    }

    private static String normalizarData(String data) {
        if (data == null) {
            return null;
        }
        String valor = data.trim();
        if (valor.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return valor.substring(6, 10) + "-" + valor.substring(3, 5) + "-" + valor.substring(0, 2);
        }
        return valor.length() >= 10 ? valor.substring(0, 10) : valor;
    }

    private static String falha(String mensagem) {
        return falha(mensagem, null);
    }

    private static String falha(String mensagem, String contexto) {
        ultimoErro = FikonApiClient.isBlank(mensagem) ? "Falha na comunicação com a Fikon." : mensagem;
        Log.e(TAG, (contexto == null ? "" : contexto + ": ") + ultimoErro);
        return ORCAMENTO_NAO_CADASTRADO;
    }

    private static double arredondar(double valor) {
        return Math.round(valor * 100d) / 100d;
    }

    private static int parseInt(String value) {
        String digits = FikonApiClient.onlyDigits(value);
        if (digits.isEmpty() || digits.length() > 9) {
            return 0;
        }
        return Integer.parseInt(digits);
    }

    private static long parseLong(String value) {
        if (value == null) {
            return 0;
        }
        String trimmed = value.trim();
        int ponto = trimmed.indexOf('.');
        if (ponto >= 0) {
            trimmed = trimmed.substring(0, ponto);
        }
        try {
            return Long.parseLong(trimmed);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
