package br.com.acalfortaleza.fikonmais.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.BeneficioPromocao;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.CondPagamento;
import br.com.acalfortaleza.fikonmais.models.Conveniada;
import br.com.acalfortaleza.fikonmais.models.Credito;
import br.com.acalfortaleza.fikonmais.models.Filial;
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.FormaPagamento;
import br.com.acalfortaleza.fikonmais.models.HorarioRetira;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Lotevencto;
import br.com.acalfortaleza.fikonmais.models.Midias;
import br.com.acalfortaleza.fikonmais.models.MotivoDesconto;
import br.com.acalfortaleza.fikonmais.models.Notas;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.PedTransferencia;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.PoliticaDesconto;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.models.Recibo;
import br.com.acalfortaleza.fikonmais.models.RetonoBeneficio;
import br.com.acalfortaleza.fikonmais.models.TpNotas;
import br.com.acalfortaleza.fikonmais.models.UtilPedido;
import br.com.acalfortaleza.fikonmais.models.VendaPerfeita;
import br.com.acalfortaleza.fikonmais.popup.PopupTpNota;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.ResponseBody;
import android.os.Environment;
import java.io.File;

public class PedidosHelper {

    private Retrofit retrofit;
    private OkHttpClient client;
    private Retrofit retrofitMotor;
    private OkHttpClient clientMotor;
    private double qtemb;
    private List<CondPagamento> listaCondicoes;

    public  PedidosHelper(){

        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Teste automático removido - correção implementada
        // testarConsultaLinks();

    }




    public String SetPedidos(Pedidos pedido) throws IOException, XmlPullParserException, ParseException {
        if (FikonVendasClient.ativo()) {
            return FikonVendasClient.salvarPedido(pedido);
        }



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> post = service.savePedido( pedido, "Bearer " + service.token);
        logPayloadEnvioPedido(pedido, post);

        final Response<String> response = post.execute();
        final String body = response.body();
        String errorBody = null;

        if (response.errorBody() != null) {
            errorBody = response.errorBody().string();
        }

        Log.d("FreteEnvioDebug",
                "Resposta savePedido: codigoHttp=" + response.code()
                        + " sucesso=" + response.isSuccessful()
                        + " body=" + body
                        + " errorBody=" + errorBody);

        return body;

    }

    private void logPayloadEnvioPedido(Pedidos pedido, Call<String> post) {
        final String tag = "FreteEnvioDebug";

        if (pedido == null) {
            Log.d(tag, "savePedido chamado com pedido=null");
            return;
        }

        BigDecimal subtotalItens = BigDecimal.ZERO;
        BigDecimal descontoItens = BigDecimal.ZERO;
        BigDecimal freteItensSomado = BigDecimal.ZERO;
        int quantidadeItens = 0;

        if (pedido.getItens() != null) {
            quantidadeItens = pedido.getItens().size();
            for (ItemPedidos item : pedido.getItens()) {
                if (item == null) {
                    continue;
                }

                BigDecimal qt = BigDecimal.valueOf(item.getQtcomp());
                BigDecimal preco = BigDecimal.valueOf(item.getPrecoUnit());
                BigDecimal descontoItem = BigDecimal.valueOf(item.getVlDescItem());
                BigDecimal freteItem = BigDecimal.valueOf(item.getVlFrete());
                BigDecimal totalLiquidoItem = preco
                        .subtract(descontoItem)
                        .max(BigDecimal.ZERO)
                        .multiply(qt)
                        .setScale(2, RoundingMode.HALF_UP);

                subtotalItens = subtotalItens.add(totalLiquidoItem);
                descontoItens = descontoItens.add(
                        descontoItem.multiply(qt).setScale(2, RoundingMode.HALF_UP)
                );
                freteItensSomado = freteItensSomado.add(freteItem.multiply(qt));
            }
        }

        BigDecimal totalItensLiquido = subtotalItens.setScale(2, RoundingMode.HALF_UP);
        BigDecimal freteCabecalho = BigDecimal.valueOf(Math.max(0.0d, pedido.getVlFrete())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal servicosCabecalho = BigDecimal.valueOf(Math.max(0.0d, pedido.getVlServicosAdicionais())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalEnviado = BigDecimal.valueOf(Math.max(0.0d, pedido.getVlTotal())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalEsperado = totalItensLiquido.add(freteCabecalho).add(servicosCabecalho).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalComFreteDuplicado = totalEnviado.add(freteCabecalho).setScale(2, RoundingMode.HALF_UP);

        Log.d(tag, "===== INICIO SAVE PEDIDO =====");
        Log.d(tag, "url=" + post.request().url());
        Log.d(tag,
                "resumo="
                        + " numpedven=" + pedido.getNumpedven()
                        + " numorc=" + pedido.getNumorc()
                        + " codfil=" + pedido.getCodfil()
                        + " codvendr=" + pedido.getCodvendr()
                        + " codcli=" + (pedido.getCliente() != null ? pedido.getCliente().getCodcli() : 0)
                        + " tpnota=" + (pedido.getTpnota() != null ? pedido.getTpnota().getCodTpNota() : 0)
                        + " formaPagamento=" + (pedido.getFormaPagamento() != null ? pedido.getFormaPagamento().getForma() : "null")
                        + " formaPagamentoDesc=" + (pedido.getFormaPagamento() != null ? pedido.getFormaPagamento().getDescricao() : "null")
                        + " codMidia=" + pedido.getCodMidia()
                        + " endfat=" + pedido.getEndfat()
                        + " endcob=" + pedido.getEndcob()
                        + " endent=" + pedido.getEndent()
                        + " codmodal=" + pedido.getCodmodal()
                        + " itens=" + quantidadeItens
                        + " subtotalItens=" + subtotalItens.setScale(2, RoundingMode.HALF_UP)
                        + " descontoItens=" + descontoItens.setScale(2, RoundingMode.HALF_UP)
                        + " totalItensLiquido=" + totalItensLiquido
                        + " vlFreteCabecalho=" + freteCabecalho
                        + " vlServicosCabecalho=" + servicosCabecalho
                        + " vlTotalEnviado=" + totalEnviado
                        + " totalEsperadoSemDuplicar=" + totalEsperado
                        + " totalSeDuplicarFrete=" + totalComFreteDuplicado
                        + " somaFreteItens=" + freteItensSomado.setScale(2, RoundingMode.HALF_UP));
        Log.d(tag,
                "cabecalho_completo="
                        + " obs=" + pedido.getObs()
                        + " obsAdicional=" + pedido.getObsAdicional()
                        + " condpgto=" + (pedido.getCondPagamento() != null ? pedido.getCondPagamento().getCondpgto() : "null")
                        + " descricaoCondicao=" + (pedido.getCondPagamento() != null ? pedido.getCondPagamento().getDescricao() : "null")
                        + " qtprc=" + pedido.getQtprc()
                        + " vlPrc=" + pedido.getVlPrc()
                        + " vlEntrada=" + pedido.getVlEntrada()
                        + " vlTotal=" + pedido.getVlTotal()
                        + " vlDesconto=" + pedido.getVlDesconto()
                        + " vlFrete=" + pedido.getVlFrete()
                        + " vlServicosAdicionais=" + pedido.getVlServicosAdicionais()
                        + " servicosIds=" + pedido.getServicosAdicionaisIds()
                        + " servicosNomes=" + pedido.getServicosAdicionaisNomes());
        Log.d(tag,
                "promocao_pedido="
                        + " isMotor=" + pedido.getIsMotor()
                        + " codigoPromo=" + pedido.getCodigoPromo()
                        + " nomePromo=" + pedido.getNomepromo()
                        + " vlDescPromo=" + pedido.getVlDescPromo()
                        + " mensagemPromo=" + pedido.getMensagemPromo());

        if (pedido.getItens() != null) {
            for (int i = 0; i < pedido.getItens().size(); i++) {
                ItemPedidos item = pedido.getItens().get(i);
                if (item == null) {
                    Log.d(tag, "item[" + i + "]=null");
                    continue;
                }

                BigDecimal qt = BigDecimal.valueOf(item.getQtcomp());
                BigDecimal totalItem = BigDecimal.valueOf(item.getPrecoUnit()).multiply(qt).setScale(2, RoundingMode.HALF_UP);
                BigDecimal totalDesc = BigDecimal.valueOf(item.getVlDescItem()).multiply(qt).setScale(2, RoundingMode.HALF_UP);
                BigDecimal totalLiquidoItem = BigDecimal.valueOf(item.getPrecoUnit())
                        .subtract(BigDecimal.valueOf(item.getVlDescItem()))
                        .max(BigDecimal.ZERO)
                        .multiply(qt)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal totalFreteItem = BigDecimal.valueOf(item.getVlFrete()).multiply(qt).setScale(2, RoundingMode.HALF_UP);

                Log.d(tag,
                        "item[" + i + "]="
                                + " codprod=" + item.getCoditprod()
                                + " codbarra=" + item.getCodbarras()
                                + " descricao=" + item.getDescricao()
                                + " tpped=" + item.getTpped()
                                + " qtcomp=" + item.getQtcomp()
                                + " precoUnit=" + item.getPrecoUnit()
                                + " totalItem=" + totalItem
                                + " vlDescItem=" + item.getVlDescItem()
                                + " totalDescItem=" + totalDesc
                                + " totalLiquidoItem=" + totalLiquidoItem
                                + " vlFreteItem=" + item.getVlFrete()
                                + " totalFreteItem=" + totalFreteItem
                                + " isMotor=" + item.getIsMotor()
                                + " codigoPromo=" + item.getCodigoPromo()
                                + " nomePromo=" + item.getNomePromo()
                                + " vlDescPromo=" + item.getVldescPromo()
                                + " mensagemPromo=" + item.getMensagemPromo()
                                + " codMotivo=" + item.getCodMotivo()
                                + " motivoDesc=" + item.getMotivoDesc()
                                + " situacao=" + item.getSituacao()
                                + " codFilOrig=" + item.getCodFilOrig()
                                + " filOrig=" + item.getFilOrig()
                                + " encomenda=" + item.getEncomenda());
            }
        }

        if (pedido.getFinanceiro() != null) {
            for (int i = 0; i < pedido.getFinanceiro().size(); i++) {
                Financeiro fin = pedido.getFinanceiro().get(i);
                if (fin == null) {
                    Log.d(tag, "financeiro[" + i + "]=null");
                    continue;
                }

                Log.d(tag,
                        "financeiro[" + i + "]="
                                + " condpgto=" + fin.getCondpgto()
                                + " condicao=" + fin.getCondicao()
                                + " forma=" + fin.getForma()
                                + " codeve=" + fin.getCodEve()
                                + " qtprc=" + fin.getQtprc()
                                + " numprc=" + fin.getNumprc()
                                + " valor=" + fin.getValor()
                                + " dtvcto=" + fin.getDtvcto());
            }
        } else {
            Log.d(tag, "financeiro=null");
        }

        logTextoLongo(tag, "pedido_json=", new Gson().toJson(pedido));
        Log.d(tag, "===== FIM SAVE PEDIDO =====");
    }

    private void logTextoLongo(String tag, String prefixo, String texto) {
        if (texto == null) {
            Log.d(tag, prefixo + "null");
            return;
        }

        final int tamanhoBloco = 3000;
        for (int inicio = 0; inicio < texto.length(); inicio += tamanhoBloco) {
            int fim = Math.min(texto.length(), inicio + tamanhoBloco);
            Log.d(tag, prefixo + texto.substring(inicio, fim));
        }
    }



    public List<BeneficioPromocao> CalcularPromocao(String codigoPromo , Pedidos pedido) throws IOException, XmlPullParserException, ParseException {

        List<BeneficioPromocao> retorno = new ArrayList<>();

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<BeneficioPromocao>> post = service.consultarPromocao(codigoPromo,pedido,"Bearer " + service.token);


        retorno = post.execute().body();

        if(retorno != null  && retorno.size()>0){


            return retorno    ;

        }else {
            return null;
        }



    }



    public String SetObs(String pedido, String obs) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> post = service.setObs( pedido,obs, "Bearer " + service.token);

        return post.execute().body();

    }

    public List<String> InicializaPedido(int Codvendr,int Codfil, int Tpnota, String Coditprod, double Qtcomp, double Precounit) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<String>> integra = service.InicializarPedido(Codvendr,Codfil,Tpnota,Coditprod,Qtcomp,Precounit , "Bearer " + service.token);

        Log.d("OrcGerarPedidoDebug",
                "InicializaPedido request"
                        + " | codvendr=" + Codvendr
                        + " | codfil=" + Codfil
                        + " | tpnota=" + Tpnota
                        + " | coditprod=" + Coditprod
                        + " | qtcomp=" + Qtcomp
                        + " | precounit=" + Precounit);

        final Response<List<String>> response = integra.execute();
        final List<String> body = response.body();
        String erroBody = null;

        if (response.errorBody() != null) {
            erroBody = response.errorBody().string();
        }

        Log.d("OrcGerarPedidoDebug",
                "InicializaPedido response"
                        + " | http=" + response.code()
                        + " | sucesso=" + response.isSuccessful()
                        + " | body=" + String.valueOf(body)
                        + " | errorBody=" + String.valueOf(erroBody));

        return body;

    }


    public List<ItemPedidos> ConsultarEstoque(List<ItemPedidos> itemPedidos) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<ItemPedidos>> integra = service.ConsultaEstoque(itemPedidos,  "Bearer " + service.token);

        return integra.execute().body();

    }

    public String AtualizarOrc(String numorc, String numped) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> postOrcamento = service.atualizarOrc( numorc,numped, "Bearer " + service.token);

        return postOrcamento.execute().body();

    }


    public void GerarPedido(Context context, Orcamento orcamento, int prazo, String filorig) {

        DecimalFormat dfInteiro = new DecimalFormat("0");
        logParametrosOrcamentoGerarPedido(orcamento, prazo, filorig);

        // 1. Transforma o orçamento em um objeto Pedido (sem os itens ainda)
        Pedidos pedido = TransformarEmPedido(orcamento);
        List<ItemPedidos> lista_itensPed = new ArrayList<>();

        // 2. Cria a lista de itens a partir dos itens do orçamento
        Log.d("OrcToPedDebug", "GerarPedido | numorc=" + orcamento.getNumorc()
                + " | condpgto=" + (orcamento.getCondPagamento() != null ? orcamento.getCondPagamento().getCondpgto() : "null")
                + " | descricao=" + (orcamento.getCondPagamento() != null ? orcamento.getCondPagamento().getDescricao() : "null")
                + " | qtprc=" + orcamento.getQtprc()
                + " | vlEntrada=" + orcamento.getVlEntrada()
                + " | pedidoNovoSempreAvista=true");

        for (int i = 0; i < orcamento.getItens().size(); i++) {
            ItemOrcamento io = orcamento.getItens().get(i);
            try {
                String imgSel = io.getImagem();
                int cod = io.getCoditprod();
                if (imgSel != null && !imgSel.equals("") && imgSel.startsWith("http")) {
                    int h = Math.abs(imgSel.hashCode());
                    File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "OrcamentoImgCache");
                    File cached = new File(folder, "orc_" + cod + "_" + h + ".png");
                    if (cached.exists() && cached.length() > 0) {
                        io.setImagem(cached.getAbsolutePath());
                        android.util.Log.d("ORC_TO_PED_CACHE", "COD " + cod + " usando cache " + cached.getAbsolutePath());
                    } else {
                        android.util.Log.d("ORC_TO_PED_CACHE", "COD " + cod + " sem cache específico, mantendo URL");
                    }
                }
            } catch (Exception ignored) { }
            ItemPedidos item = setItemPedidos(io, prazo, filorig, orcamento.getCodfil(), false);
            lista_itensPed.add(item);
            try {
                String img = item.getImagem();
                int cod = item.getCoditprod();
                Log.d("ORC_PED_IMG", "ItemPedidos pós-cópia COD " + cod + " imagem " + String.valueOf(img));
            } catch (Exception ex) {
                Log.e("ORC_PED_IMG", "Falha ao logar imagem do item do orçamento", ex);
            }
        }

        // 3. Adiciona a lista de itens completa ao objeto pedido
        pedido.setItens(lista_itensPed);
        atualizarTotalInicialPedido(pedido);
        logPedidoGeradoDoOrcamento(pedido);

        new Thread(() -> {
            try {
                ProdutoHelper prodHelper = new ProdutoHelper();
                for (int i = 0; i < lista_itensPed.size(); i++) {
                    ItemPedidos it = lista_itensPed.get(i);
                    String ref = it.getImagem();
                    int cod = it.getCoditprod();
                    String urlToUpdate = null;
                    if (ref != null && !ref.equals("")) {
                        if (ref.startsWith("http")) {
                            urlToUpdate = ref;
                        } else {
                            try {
                                if (ref.endsWith(".png")) {
                                    String sidecarPath = ref.substring(0, ref.length() - 4) + ".url";
                                    java.io.File sidecar = new java.io.File(sidecarPath);
                                    if (sidecar.exists() && sidecar.length() > 0) {
                                        java.io.FileInputStream fis = new java.io.FileInputStream(sidecar);
                                        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                                        byte[] buf = new byte[512];
                                        int r;
                                        while ((r = fis.read(buf)) != -1) {
                                            baos.write(buf, 0, r);
                                        }
                                        fis.close();
                                        urlToUpdate = new String(baos.toByteArray());
                                    }
                                }
                            } catch (Exception ignored) { }
                        }
                    }
                    if (urlToUpdate != null && !urlToUpdate.equals("") && urlToUpdate.startsWith("http")) {
                        Log.d("ORC_PED_IMG", "Atualizando IMAGEM na VTEX_AUDITORIA_PRODUTO para COD " + cod + " com URL: " + urlToUpdate);
                        try {
                            prodHelper.AtualizarImagemVtex(cod, urlToUpdate);
                            Log.d("ORC_PED_IMG", "Atualização concluída para COD " + cod);
                        } catch (Exception ex) {
                            Log.e("ORC_PED_IMG", "Erro ao atualizar IMAGEM para COD " + cod, ex);
                        }
                    } else {
                        Log.d("ORC_PED_IMG", "Sem URL para atualizar COD " + cod + " (ref: " + String.valueOf(ref) + ")");
                    }
                }
            } catch (Exception ex) {
                Log.e("ORC_PED_IMG", "Falha geral ao atualizar imagens na VTEX_AUDITORIA_PRODUTO", ex);
            }
        }).start();
        // --- [A LÓGICA FINAL E CORRETA] ---
        // 4. AGORA que o pedido tem os itens, verificamos se há serviços
        /*double valorServicos = pedido.getVlServicosAdicionais();
        boolean temServicos = valorServicos > 0;

        // 5. Se tiver serviços, forçamos TODOS os itens para o tipo 'E' (Entrega)
        if (temServicos) {
            android.util.Log.d("FreteCarry", "Serviços detectados. Forçando TODOS os itens do pedido para Tpped='E'.");
            for (ItemPedidos itemDoPedido : pedido.getItens()) {
                itemDoPedido.setTpped("E");
            }
        }*/
        // --- FIM DA LÓGICA FINAL ---

        // 6. Prepara e inicia a próxima tela com o objeto 'pedido' 100% correto
        Intent intent = new Intent(context, PopupTpNota.class);
        intent.putExtra("pedido", pedido);
        intent.putExtra("numorc", dfInteiro.format(orcamento.getNumorc()));

        // Adiciona a lista de IDs de serviço ao Intent para ser usada pela próxima tela.
        //intent.putExtra("servicos_adicionais_ids", (ArrayList<Integer>) idsServicosAdicionais);
        //intent.putExtra("orcamento", true); // Flag para a lógica na Pedidos_LancamentoActivity

        context.startActivity(intent);
    }

    private void logParametrosOrcamentoGerarPedido(Orcamento orcamento, int prazo, String filorig) {
        final String tag = "OrcGerarPedidoDebug";

        if (orcamento == null) {
            Log.d(tag, "GerarPedido chamado com orcamento=null");
            return;
        }

        Log.d(tag, "===== INICIO PARAMETROS ORCAMENTO GERAR PEDIDO =====");
        Log.d(tag,
                "cabecalho_orc="
                        + " numorc=" + orcamento.getNumorc()
                        + " numorcGemco=" + orcamento.getNumorcGemco()
                        + " numpedven=" + orcamento.getNumpedven()
                        + " codfil=" + orcamento.getCodfil()
                        + " codvendr=" + orcamento.getCodvendr()
                        + " codcli=" + (orcamento.getCliente() != null ? orcamento.getCliente().getCodcli() : 0)
                        + " prazo=" + prazo
                        + " filorig=" + filorig
                        + " condpgto=" + (orcamento.getCondPagamento() != null ? orcamento.getCondPagamento().getCondpgto() : "null")
                        + " descricaoCondicao=" + (orcamento.getCondPagamento() != null ? orcamento.getCondPagamento().getDescricao() : "null")
                        + " qtprc=" + orcamento.getQtprc()
                        + " vlPrc=" + orcamento.getVlPrc()
                        + " vlEntrada=" + orcamento.getVlEntrada()
                        + " vlTotal=" + orcamento.getVlTotal()
                        + " vlDesconto=" + orcamento.getVlDesconto()
                        + " vlFrete=" + orcamento.getVlFrete()
                        + " vlServicos=" + orcamento.getVlServicosAdicionais()
                        + " peso=" + orcamento.getPeso()
                        + " codArq=" + orcamento.getCodArq());

        if (orcamento.getItens() != null) {
            for (int i = 0; i < orcamento.getItens().size(); i++) {
                ItemOrcamento item = orcamento.getItens().get(i);
                if (item == null) {
                    Log.d(tag, "item_orc[" + i + "]=null");
                    continue;
                }

                Log.d(tag,
                        "item_orc[" + i + "]="
                                + " codprod=" + item.getCoditprod()
                                + " descricao=" + item.getDescricao()
                                + " qtcomp=" + item.getQtcomp()
                                + " precoUnit=" + item.getPrecoUnit()
                                + " vlDescItem=" + item.getVlDescItem()
                                + " dpi=" + item.getDpi()
                                + " situacao=" + item.getSituacao());
            }
        } else {
            Log.d(tag, "itens_orc=null");
        }

        logTextoLongo(tag, "orcamento_json=", new Gson().toJson(orcamento));
        Log.d(tag, "===== FIM PARAMETROS ORCAMENTO GERAR PEDIDO =====");
    }

    private void logPedidoGeradoDoOrcamento(Pedidos pedido) {
        final String tag = "OrcGerarPedidoDebug";

        if (pedido == null) {
            Log.d(tag, "pedido_gerado=null");
            return;
        }

        Log.d(tag, "===== INICIO PEDIDO GERADO DO ORCAMENTO =====");
        Log.d(tag,
                "cabecalho_pedido="
                        + " numpedven=" + pedido.getNumpedven()
                        + " codfil=" + pedido.getCodfil()
                        + " codvendr=" + pedido.getCodvendr()
                        + " codcli=" + (pedido.getCliente() != null ? pedido.getCliente().getCodcli() : 0)
                        + " condpgto=" + (pedido.getCondPagamento() != null ? pedido.getCondPagamento().getCondpgto() : "null")
                        + " descricaoCondicao=" + (pedido.getCondPagamento() != null ? pedido.getCondPagamento().getDescricao() : "null")
                        + " qtprc=" + pedido.getQtprc()
                        + " vlPrc=" + pedido.getVlPrc()
                        + " vlEntrada=" + pedido.getVlEntrada()
                        + " vlTotal=" + pedido.getVlTotal()
                        + " vlDesconto=" + pedido.getVlDesconto()
                        + " vlFrete=" + pedido.getVlFrete()
                        + " vlServicos=" + pedido.getVlServicosAdicionais()
                        + " peso=" + pedido.getPeso()
                        + " codArq=" + pedido.getCodArq());

        if (pedido.getItens() != null) {
            for (int i = 0; i < pedido.getItens().size(); i++) {
                ItemPedidos item = pedido.getItens().get(i);
                if (item == null) {
                    Log.d(tag, "item_pedido[" + i + "]=null");
                    continue;
                }

                Log.d(tag,
                        "item_pedido[" + i + "]="
                                + " codprod=" + item.getCoditprod()
                                + " descricao=" + item.getDescricao()
                                + " qtcomp=" + item.getQtcomp()
                                + " precoUnit=" + item.getPrecoUnit()
                                + " vlDescItem=" + item.getVlDescItem()
                                + " dpi=" + item.getDpi()
                                + " tpped=" + item.getTpped()
                                + " situacao=" + item.getSituacao()
                                + " filOrig=" + item.getFilOrig()
                                + " codFilOrig=" + item.getCodFilOrig());
            }
        } else {
            Log.d(tag, "itens_pedido=null");
        }

        logTextoLongo(tag, "pedido_gerado_json=", new Gson().toJson(pedido));
        Log.d(tag, "===== FIM PEDIDO GERADO DO ORCAMENTO =====");
    }
    public ItemPedidos setItemPedidos(ItemOrcamento item, int prazo ,String Filial, int codfil, boolean orcamentoAPrazo){

        ItemPedidos itemPedidos = new ItemPedidos();

        itemPedidos.setDescricao(item.getDescricao());

        if(item.getQtemb()==0){
            qtemb=1;
        }else {
            qtemb=item.getQtemb();
        }


        itemPedidos.setCoditprod(item.getCoditprod());
        itemPedidos.setPrecoUnit(item.getPrecoUnit());
        itemPedidos.setCodbarras(item.getCodbarras());
        itemPedidos.setQtcomp(item.getQtcomp());
        itemPedidos.setQtemb(qtemb);
        itemPedidos.setEncargos(item.getEncargos());
        itemPedidos.setIcms(item.getIcms());
        itemPedidos.setIcmsRed(item.getIcmsRed());
        itemPedidos.setCusto(item.getCusto());
        itemPedidos.setNcm(item.getNcm());
        itemPedidos.setPeso(item.getPeso());
        itemPedidos.setFornecedor(item.getFornecedor());
        itemPedidos.setImagem(item.getImagem());
        itemPedidos.setDtentrega(calculaDataEntrega(0));
        itemPedidos.setQuantidadeAnterior(0.00d);
        itemPedidos.setCodfilAnt(0);
        itemPedidos.setStatus("1");
        if(item.getCodlinha() == 1) {
            itemPedidos.setTpped("P");
            itemPedidos.setTppedDesc("Retira Posterior");
        }
        else{
            itemPedidos.setTpped("R");
            itemPedidos.setTppedDesc("Retira");
        }
        if(item.getSituacao().equals("EN")){
            itemPedidos.setEncomenda("E");
        }else{
            itemPedidos.setEncomenda("N");
        }

        itemPedidos.setLotevencto(new ArrayList<Lotevencto>()) ;
        itemPedidos.setFllotevencto(item.getFllotevencto());
        if(codfil==501){
            itemPedidos.setTpped("P");
            itemPedidos.setTppedDesc("Retira Posterior");
            itemPedidos.setFilOrig("Pe.Cicero");
            itemPedidos.setCodFilOrig(5);
        }else{
            itemPedidos.setFilOrig(Filial);
            itemPedidos.setCodFilOrig(codfil);
        }


        itemPedidos.setSituacao(item.getSituacao());
        itemPedidos.setCodMotivo(item.getCodMotivo() != null ? item.getCodMotivo() : "");
        itemPedidos.setMotivoDesc(item.getMotivoDesc() != null ? item.getMotivoDesc() : "");
        itemPedidos.setVlDescItem(item.getVlDescItem());
        itemPedidos.setDpi(item.getDpi());
        itemPedidos.setVldescPromo(item.getVldescPromo());
        itemPedidos.setNomePromo(item.getNomePromo() != null ? item.getNomePromo() : "");
        itemPedidos.setMensagemPromo(item.getMensagemPromo() != null ? item.getMensagemPromo() : "");
        itemPedidos.setCodigoPromo(item.getCodigoPromo() != null ? item.getCodigoPromo() : "");
        itemPedidos.setIsMotor(item.getIsMotor());
        itemPedidos.setPrecoConc(item.getPrecoConc());
        itemPedidos.setBloqueioDesc(item.getBloqueioDesc());
        itemPedidos.setPercentualComissao(item.getPercentualComissao());
        itemPedidos.setPercIncentivo(item.getPercIncentivo());





        return itemPedidos;
    }

    private boolean isOrcamentoAPrazo(Orcamento orcamento) {
        if (orcamento == null || orcamento.getCondPagamento() == null) {
            return false;
        }

        String descricaoCondicao = orcamento.getCondPagamento().getDescricao();
        if (descricaoCondicao != null) {
            String descricaoNormalizada = descricaoCondicao.toUpperCase().trim();
            if (descricaoNormalizada.contains("A VISTA") || descricaoNormalizada.contains("AVISTA")) {
                return false;
            }
            if (descricaoNormalizada.contains("A PRAZO")) {
                return true;
            }
        }

        // Em orcamentos ja fechados, 1 parcela sem entrada representa fluxo a vista,
        // mesmo quando o codigo da condicao nao eh "01".
        if (orcamento.getQtprc() <= 1 && orcamento.getVlEntrada() <= 0d) {
            return false;
        }

        if (orcamento.getCondPagamento().getCondpgto() != null
                && !orcamento.getCondPagamento().getCondpgto().trim().isEmpty()) {
            return !"01".equals(orcamento.getCondPagamento().getCondpgto().trim());
        }

        return false;
    }

    private boolean isItemPromocional(ItemOrcamento item) {
        return item != null && item.getSituacao() != null && "PR".equalsIgnoreCase(item.getSituacao().trim());
    }

    private double reverterPrecoUnitarioBrutoPromocionalPrazo(ItemOrcamento item) {
        BigDecimal precoBrutoAtual = BigDecimal.valueOf(item.getPrecoUnit());
        BigDecimal descontoUnitario = BigDecimal.valueOf(item.getVlDescItem());

        return precoBrutoAtual.subtract(descontoUnitario)
                .multiply(BigDecimal.valueOf(0.95d))
                .add(descontoUnitario)
                .setScale(6, RoundingMode.HALF_EVEN)
                .doubleValue();
    }


    public ItemPedidos IncluirItem(double qtd, ProdutoGemco gemco, String descricao, String imagem, int prazo ,String Filial, int codfil){

        ItemPedidos itemPedidos = new ItemPedidos();

        if(!descricao.equals("")) {
            itemPedidos.setDescricao(descricao);
        }else {
            if(gemco!=null){
                itemPedidos.setDescricao(gemco.Descricao);
            }

        }

        if(gemco.Qtemb==0){
            qtemb=1;
        }else {
            qtemb=gemco.Qtemb;
        }


        itemPedidos.setCoditprod(gemco.Codigo);
        itemPedidos.setPrecoUnit(gemco.Preco);
        itemPedidos.setCodbarras(gemco.CodigoBarra.get(0).CodBarra);
        itemPedidos.setQtcomp(qtd);
        itemPedidos.setQtemb(qtemb);
        itemPedidos.setEncargos(gemco.Encargos);
        itemPedidos.setIcms(gemco.Icms);
        itemPedidos.setIcmsRed(gemco.IcmsRed);
        itemPedidos.setCusto(gemco.Custo);
        itemPedidos.setNcm(gemco.Ncm);
        itemPedidos.setPeso(gemco.Peso);
        itemPedidos.setImagem(imagem);
        itemPedidos.setUnidade(gemco.Unidade);
        itemPedidos.setFornecedor(gemco.Fornecedor);
        itemPedidos.setDtentrega(calculaDataEntrega(prazo));
        itemPedidos.setStatus("1");
        itemPedidos.setQuantidadeAnterior(0.00d);
        itemPedidos.setCodfilAnt(0);
        itemPedidos.setTpped("P");
        itemPedidos.setTppedDesc("Retira Posterior");
        itemPedidos.setEncomenda("N");
        itemPedidos.setLotevencto(new ArrayList<Lotevencto>()) ;
        itemPedidos.setFllotevencto(gemco.Lotevencto);
        if(codfil==501){
            itemPedidos.setFilOrig("Pe.Cicero");
            itemPedidos.setCodFilOrig(5);
        }else{
            itemPedidos.setFilOrig(Filial);
            itemPedidos.setCodFilOrig(codfil);
        }

        itemPedidos.setSituacao(gemco.Situacao);
        itemPedidos.setMotivoDesc("");
        itemPedidos.setCodMotivo("");
        itemPedidos.setVlDescItem(0.00d);
        itemPedidos.setDpi(0.00d);
        itemPedidos.setVldescPromo(0.00d);
        itemPedidos.setCodigoPromo("");
        itemPedidos.setNomePromo("");
        itemPedidos.setMensagemPromo("");
        itemPedidos.setBloqueioDesc(gemco.BloqueioDesc);
        itemPedidos.setPercentualComissao(gemco.PercentualComissao);
        itemPedidos.setPercIncentivo(gemco.PercIncentivo);




        return itemPedidos;
    }

    public Pedidos TransformarEmPedido(Orcamento orc) {

        Pedidos pedido = new Pedidos();

        // -- Bloco Único e Correto de Cópia dos Dados --
        pedido.setCodfil(orc.getCodfil());
        pedido.setFilial(orc.getFilial());
        pedido.setCodvendr(orc.getCodvendr());
        pedido.setCliente(orc.getCliente());
        pedido.setVlTotal(0.00d);
        pedido.setVlFrete(0.00d);
        pedido.setVlDesconto(orc.getVlDesconto());
        // Pedido novo nunca deve herdar condição/parcelamento do orçamento.
        // A primeira abertura do pedido sempre começa em A VISTA.
        pedido.setCondPagamento(null);
        pedido.setQtprc(1);
        pedido.setVlPrc(0.00d);
        pedido.setVlEntrada(0.00d);
        pedido.setPmargRent(orc.getPmargRent());
        pedido.setConsultor(orc.getConsultor());
        pedido.setPeso(orc.getPeso());
        pedido.setArquiteto(orc.getArquiteto());
        pedido.setCodArq(orc.getCodArq());
        pedido.setCodigoPromo(orc.getCodigoPromo());
        pedido.setNomepromo(orc.getNomepromo());
        pedido.setMensagemPromo(orc.getMensagemPromo());
        pedido.setVlDescPromo(orc.getVlDescPromo());
        pedido.setIsMotor(orc.getIsMotor());
        pedido.setLote("");
        pedido.setEncomenda("");
        pedido.setTransferencia(0);
        pedido.setFinanceiro(null);
        pedido.setMidia(null);
        pedido.setTpnota(null);
        pedido.setTpped("P"); // Padrão
        Log.d("PromoAPrazoOrcToPed",
                "Pedido gerado sem herdar condicao do orcamento. numorc="
                        + orc.getNumorc());
        //pedido.setVlFreteSelecionado(orc.getVlFreteSelecionado());

        if (orc.getCliente() != null && orc.getCliente().getEnderecos() != null && !orc.getCliente().getEnderecos().isEmpty()) {
            pedido.setPraca(orc.getCliente().getEnderecos().get(0).getPraca());
        } else {
            pedido.setPraca(0);
        }

        // --- [A PARTE MAIS IMPORTANTE] ---
        // Copia o valor do frete por tonelada e dos serviços do orçamento para o objeto PEDIDO.
        /*pedido.setVlFreteSelecionado(orc.getVlFreteSelecionado());
        android.util.Log.d("FreteCarry", "Copiando VlFreteSelecionado do Orçamento para o Pedido: " + orc.getVlFreteSelecionado());

        try {
            int numOrcKey = (int) orc.getNumorc();
            Double serv = br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.get(numOrcKey);
            java.util.List<Integer> idsServ = br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.get(numOrcKey);

            if (serv != null && serv > 0) {
                // GRAVA O VALOR DIRETAMENTE NO OBJETO PEDIDO
                pedido.setVlServicosAdicionais(serv);
                android.util.Log.d("FreteCarry", "Gravando VlServicosAdicionais ("+ serv +") no objeto Pedido.");
            }
            if (idsServ != null && !idsServ.isEmpty()) {
                pedido.setServicosAdicionaisIds(idsServ);
            }
        } catch (Exception ignore) {}*/

        return pedido;
    }

    private void atualizarTotalInicialPedido(Pedidos pedido) {
        if (pedido == null || pedido.getItens() == null) {
            return;
        }

        BigDecimal valorTotalItens = new BigDecimal("0.00");
        BigDecimal valorDescontoTotal = new BigDecimal("0.00");

        for (ItemPedidos item : pedido.getItens()) {
            BigDecimal quantidade = BigDecimal.valueOf(item.getQtcomp());
            BigDecimal totalLiquidoItem = BigDecimal.valueOf(item.getPrecoUnit())
                    .subtract(BigDecimal.valueOf(item.getVlDescItem()))
                    .max(BigDecimal.ZERO)
                    .multiply(quantidade)
                    .setScale(2, RoundingMode.HALF_UP);
            valorTotalItens = valorTotalItens.add(totalLiquidoItem);

            BigDecimal descontoItem = BigDecimal.valueOf(item.getVlDescItem())
                    .multiply(quantidade)
                    .setScale(2, RoundingMode.HALF_UP);
            valorDescontoTotal = valorDescontoTotal.add(descontoItem);
        }

        valorTotalItens = valorTotalItens.setScale(2, RoundingMode.HALF_UP);
        valorDescontoTotal = valorDescontoTotal.setScale(2, RoundingMode.HALF_UP);

        pedido.setVlTotal(valorTotalItens.doubleValue());
        pedido.setVlDesconto(valorDescontoTotal.doubleValue());

        Log.d("PromoAPrazoOrcToPed",
                "Total inicial do pedido recalculado pelos itens. vlTotal="
                        + pedido.getVlTotal()
                        + " vlDesconto=" + pedido.getVlDesconto());
    }


    public List<Pedidos> GetPedidos(String dtinicial, String dtfinal, int codvendr, int codfil, int codarq) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Pedidos>> request= service.GetPedidos( dtinicial, dtfinal, codvendr, codfil,codarq ,"Bearer " + service.token);

        return request.execute().body();

    }


    public List<Pedidos> GetPedidosList(String dtinicial, String dtfinal, int codvendr, int codfil, int codarq, String consulta, String tipo) throws IOException, XmlPullParserException, ParseException {

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Pedidos>> request= service.GetPedidosList( dtinicial, dtfinal, codvendr, codfil,codarq,consulta,tipo ,"Bearer " + service.token);

        return request.execute().body();

    }


    public List<Notas> GetNotasPedidos( double numpedven) throws IOException, XmlPullParserException, ParseException {

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Notas>> request= service.GetNotasPedido( numpedven ,"Bearer " + service.token);

        return request.execute().body();

    }


    public Cliente GetClientePedidos( String cccpf, int codfil, String cpfconsultor) throws IOException, XmlPullParserException, ParseException {

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Cliente>> request= service.GetClienteCgccpf( cccpf, codfil, cpfconsultor ,"Bearer " + service.token);

        List<Cliente> cliente = request.execute().body();

        return cliente.get(0);

    }


    public List<PedTransferencia> GetTransfPedidos(double numpedven) throws IOException, XmlPullParserException, ParseException {

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<PedTransferencia>> request= service.GetTransfPedido( numpedven ,"Bearer " + service.token);

        return request.execute().body();

    }

    public List<Financeiro> GetFinanceiroPedidos(double numpedven, int codfil) throws IOException, XmlPullParserException, ParseException {

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Financeiro>> request= service.GetFinanceiroPedido( numpedven ,codfil,"Bearer " + service.token);

        return request.execute().body();

    }



    public List<Pedidos> GetPedidosConsulta(String consulta, String tipo) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Pedidos>> request = service.GetPedidosConsulta( consulta, tipo, "Bearer " + service.token);

        return request.execute().body();

    }



    public List<TpNotas> GetTpNotas() throws IOException, XmlPullParserException, ParseException {
        if (FikonVendasClient.ativo()) {
            // Na Fikon o tipo de saída do pedido vem do orçamento transformado:
            // não há escolha de tipo de nota pelo app.
            List<TpNotas> tipos = new ArrayList<>();
            TpNotas pedidoVenda = new TpNotas();
            pedidoVenda.setCodTpNota(1);
            pedidoVenda.setDescricao("PEDIDO DE VENDA");
            tipos.add(pedidoVenda);
            return tipos;
        }




        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<TpNotas>> request = service.GetTpNotas(  "Bearer " + service.token);

        return request.execute().body();

    }


    public List<Midias> GetMidias() throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Midias>> request = service.GetMidias("Bearer " + service.token);

        return request.execute().body();

    }


    public List<Conveniada> GetConveniadas() throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Conveniada>> request = service.GetConveniadas("Bearer " + service.token);

        return request.execute().body();

    }




    public List<Filial> GetFiliais() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Filial>> request = service.GetFilial(  "Bearer " + service.token);

        return request.execute().body();

    }


    public List<MotivoDesconto> GetMotivoDesc() throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<MotivoDesconto>> request = service.GetMotivosDesconto(  "Bearer " + service.token);

        return request.execute().body();

    }

    public List<FormaPagamento> GetFormas(int codfil, String condpgto) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<FormaPagamento>> request = service.GetFormasPgto(  codfil,condpgto,"Bearer " + service.token);

        return request.execute().body();

    }

    public List<HorarioRetira> GetHorarios(String data, String tipo) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<HorarioRetira>> request = service.GetHorarios(  data,tipo,"Bearer " + service.token);

        return request.execute().body();

    }

    public String SetAgendamentoRetira(String numpedven, String codcli, String data, String hora, String peso, String placa, String tipo, int codfil, int codvendr, String status, String usuario, Cliente cliente, String agendamento, String paletizado) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> request = service.SetAgendamentoRetira( numpedven,codcli,data,hora,peso,placa,tipo,codfil,codvendr,status,usuario,cliente,agendamento,paletizado,"Bearer " + service.token);

        return request.execute().body();

    }


    public List<Recibo> GetRecibos(String codcli) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Recibo>> request = service.GetRecibos(  codcli,"Bearer " + service.token);

        return request.execute().body();

    }


    public String calculaDataEntrega(int prazo){

        int daysToAdd = prazo; // quantidade de dias a adicionar

        // Incializa data
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < daysToAdd; i++) {
            cal.add(Calendar.DATE, 1);
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
                    //|| feriados.contains(cal.getTime()))
            {
                daysToAdd++;
            }
        }

        return s.format(cal.getTime());

    }

    public String LiberacaoComercial(String numpedven, int codlib, int codfil, String temcredi) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> request = service.LiberacaoComercial( numpedven, codlib, codfil, temcredi, "Bearer " + service.token);

        return request.execute().body();

    }

    public String LiberacaoCredito(String numpedven, int codlib, int codfil, String liberador, String codcli) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> request = service.LiberacaoCredito(  numpedven, codlib, codfil, liberador, codcli,"Bearer " + service.token);

        return request.execute().body();

    }


    public Credito VerificarCredito(String codcli, double valorPedido) throws IOException, XmlPullParserException, ParseException {



        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<Credito> getVerificaCredito = service.getVerificarCredito( codcli,valorPedido, "Bearer " + service.token);

        Credito retorno = getVerificaCredito.execute().body();

        return retorno;

    }


    public List<Lotevencto> GetLoteventoProduto(String coditprod, String filorig, Double qtde ) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<List<Lotevencto>> request = service.GetLotevenctoProduto(coditprod, filorig,qtde,"Bearer " + service.token);

        return request.execute().body();

    }

    public UtilPedido GetStatusPedido(String numpedven ) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<UtilPedido> request = service.getStatusPedido(numpedven,"Bearer " + service.token);

        return request.execute().body();

    }


    public VendaPerfeita ObterMetasVendaPerfeita(int codigoVendedor, int codigoFilial, String natJurCliente, String dataPedido){

        try {

            dataPedido="";
            //codigoFilial = 1;
            //codigoVendedor = 641;

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<VendaPerfeita> request = service.GetMetasVendaPerfeita(codigoFilial, codigoVendedor, natJurCliente, dataPedido, "Bearer " + service.token);

            return request.execute().body();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;

    }

    public static VendaPerfeita ObterIndicadoresVendaPerfeita(Pedidos pedido, double freteFinal)
    {

        try {

            VendaPerfeita indicadores = new VendaPerfeita();

            List<Integer> SKUs = new ArrayList<>();
            BigDecimal valorItens = BigDecimal.ZERO;
            BigDecimal valorDescontoManual = BigDecimal.ZERO;
            BigDecimal valorDescontoMotor = BigDecimal.ZERO;
            double freteOriginal = 0;
            boolean temEntrega = false;
            boolean calcularFreteFinal = false;

            if(freteFinal == 0){
                calcularFreteFinal = true;
            }

            if(pedido.getItens()!=null && pedido.getItens().size()>0){

                for (ItemPedidos item: pedido.getItens()) {

                    if(item.getTpped().equals("E")){
                        temEntrega = true;
                    }

                    if(!SKUs.contains(item.getCoditprod())){
                        SKUs.add(item.getCoditprod());
                    }

                    BigDecimal valorItem = BigDecimal.valueOf(item.getPrecoUnit())
                            .multiply(BigDecimal.valueOf(item.getQtcomp()))
                            .setScale(2, RoundingMode.HALF_UP);
                    BigDecimal valorDesconto = BigDecimal.valueOf(item.getVlDescItem())
                            .multiply(BigDecimal.valueOf(item.getQtcomp()))
                            .setScale(2, RoundingMode.HALF_UP);

                    valorItens = valorItens.add(valorItem);

                    if(valorDesconto.compareTo(BigDecimal.ZERO) > 0) {

                        if (item.getMotivoDesc() == null || item.getMotivoDesc().trim().equals("")) {
                            valorDescontoMotor = valorDescontoMotor.add(valorDesconto);
                        } else {
                            valorDescontoManual = valorDescontoManual.add(valorDesconto);
                        }

                    }

                }

            }

            indicadores.Conversao = false;
            if(pedido!=null && pedido.getNumpedven()>0 && pedido.getCliente()!=null && pedido.getCliente().getCodcli()>0){
                indicadores.Conversao = true;
            }

            indicadores.SKU = SKUs.size();

            indicadores.TicketMedio = -1000;
            if(indicadores.SKU > 0) {
                indicadores.TicketMedio = valorItens
                        .subtract(valorDescontoManual)
                        .subtract(valorDescontoMotor)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
            }

            indicadores.PercentualDesconto = 1000;
            if(valorItens.compareTo(BigDecimal.ZERO) > 0) {
                indicadores.PercentualDesconto = valorDescontoManual
                        .divide(valorItens, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100d))
                        .doubleValue();
                indicadores.PercentualDesconto = BigDecimal.valueOf(indicadores.PercentualDesconto).setScale(3,RoundingMode.UP).doubleValue();
            }

            indicadores.PercentualFrete = -1000;
            if(pedido.getItens()!=null && pedido.getItens().size() > 0) {

                indicadores.PercentualFrete = 100;

                if(temEntrega){

                    if (pedido.getVlFreteSelecionado() > 0 && (pedido.getPesoEntrega() / 1000) > 1.0) {
                        freteOriginal = Math.floor(pedido.getVlFreteSelecionado() * (pedido.getPesoEntrega() / 1000));
                    } else if (pedido.getVlFreteSelecionado() > 0) {
                        freteOriginal = pedido.getVlFreteSelecionado();
                    } else if (pedido.getVlFrete() > 0) {
                        freteOriginal = pedido.getVlFrete();
                    }

                    if(freteFinal <= 0){

                        if (pedido.getVlFrete() > 0) {
                            freteFinal = pedido.getVlFrete();
                        } else {
                            freteFinal = freteOriginal;
                        }

                    }

                    if (freteFinal > 0 && freteOriginal > 0) {
                        indicadores.PercentualFrete = (freteFinal / freteOriginal) * 100;
                        indicadores.PercentualFrete = BigDecimal.valueOf(indicadores.PercentualFrete).setScale(2,RoundingMode.FLOOR).doubleValue();
                    }

                    if(indicadores.TicketMedio > 0) {
                        indicadores.TicketMedio += freteFinal;
                    }

                }

            }

            return indicadores;

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;

    }

    public static void AtualizarLayoutVendaPerfeita(
        VendaPerfeita indicadores, VendaPerfeita metas,
        TextView txtConversao, TextView txtSku, TextView txtTicketMedio,
        TextView txtDesconto, TextView txtEntrega
    )
    {

        try{

            final int[] porcentagem = {0};

            class MudaLayout{

                void Visivel(TextView textView, int cor){
                    porcentagem[0] += 20;
                    textView.setText(porcentagem[0] + "%");
                    textView.setPadding(2,2,2,2);
                    textView.setBackgroundColor(cor);
                }
                void Invisivel(TextView textView){
                    textView.setText("");
                    textView.setPadding(0,0,0,0);
                    textView.setBackgroundColor(Color.WHITE);
                }

            }

            MudaLayout mudaLayout = new MudaLayout();

            mudaLayout.Invisivel(txtConversao);
            mudaLayout.Invisivel(txtSku);
            mudaLayout.Invisivel(txtTicketMedio);
            mudaLayout.Invisivel(txtDesconto);
            mudaLayout.Invisivel(txtEntrega);

            if (metas != null && indicadores != null) {

                if (indicadores.Conversao == metas.Conversao) {
                    mudaLayout.Visivel(txtConversao, Color.parseColor("#D93830"));
                }
                if (indicadores.SKU >= metas.SKU) {
                    mudaLayout.Visivel(txtSku, Color.parseColor("#805D3C"));
                }
                if (indicadores.TicketMedio >= metas.TicketMedio) {
                    mudaLayout.Visivel(txtTicketMedio, Color.parseColor("#00BF63"));
                }
                if (indicadores.PercentualDesconto <= metas.PercentualDesconto) {
                    mudaLayout.Visivel(txtDesconto, Color.parseColor("#FFDE59"));
                }
                if (indicadores.PercentualFrete >= metas.PercentualFrete) {
                    mudaLayout.Visivel(txtEntrega, Color.parseColor("#019EE3"));
                }

            }

        }
        catch (Exception ex){

        }

    }

    public PoliticaDesconto ConsultarPoliticaDesconto(Pedidos pedido){

        try {

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<PoliticaDesconto> request = service.GetPoliticaDesconto(pedido, "Bearer " + service.token);

            Response<PoliticaDesconto> response = request.execute();

            if(response.isSuccessful()){
                return response.body();
            }

            if(response.code() == 404){
                return new PoliticaDesconto();
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;

    }

    public String ConsultarLinksPagamento(long pedido){

        try {

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<ResponseBody> request = service.GetLinksPagamento(pedido, "Bearer " + service.token);

            Response<ResponseBody> response = request.execute();

            if(response.isSuccessful()){
                Log.d("PedidosHelper", "ConsultarLinksPagamento sucesso - Código: " + response.code());
                Log.d("PedidosHelper", "ConsultarLinksPagamento - URL da requisição: " + request.request().url());
                Log.d("PedidosHelper", "ConsultarLinksPagamento - Headers: " + request.request().headers());
                
                String responseBody = response.body().string();
                Log.d("PedidosHelper", "Resposta bruta: " + responseBody);
                
                // Processa o JSON array retornado pela API
                return processarJsonArrayResposta(responseBody, pedido);
            }
            else {
                Log.e("PedidosHelper", "ConsultarLinksPagamento erro - Código: " + response.code());
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Erro desconhecido";
                Log.e("PedidosHelper", "ConsultarLinksPagamento - Corpo do erro: " + errorBody);
                Log.e("PedidosHelper", "Erro: " + errorBody);
                return "Erro ao consultar links de pagamento do pedido - HTTP " + response.code();
            }

        }
        catch (Exception ex){
            Log.e("PedidosHelper", "Exceção em ConsultarLinksPagamento: " + ex.getMessage());
            ex.printStackTrace();
        }

        return "Erro ao consultar links de pagamento do pedido";

    }

    public String ConsultarLinksCartao(long pedido){

        try {

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<ResponseBody> request = service.GetLinksCartao(pedido, "Bearer " + service.token);

            Response<ResponseBody> response = request.execute();
            Log.d("PedidosHelper", "ConsultarLinksCartao - Código de resposta: " + response.code());
            Log.d("PedidosHelper", "ConsultarLinksCartao - URL da requisição: " + request.request().url());
            Log.d("PedidosHelper", "ConsultarLinksCartao - Headers: " + request.request().headers());
            
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Log.d("PedidosHelper", "Resposta bruta: " + responseBody);
                
                // Processa o JSON array retornado pela API
                return processarJsonArrayResposta(responseBody, pedido);
            } else {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Erro desconhecido";
                Log.e("PedidosHelper", "ConsultarLinksCartao - Erro HTTP " + response.code() + ": " + response.message());
                Log.e("PedidosHelper", "ConsultarLinksCartao - Corpo do erro: " + errorBody);
                return "Erro ao consultar links de cartão do pedido - HTTP " + response.code();
            }

        }
        catch (Exception ex){
            Log.e("PedidosHelper", "ConsultarLinksCartao - Exceção: " + ex.getMessage(), ex);
            return "Erro ao consultar links de cartão do pedido - Exceção: " + ex.getMessage();
        }

    }

    private String processarJsonArrayResposta(String jsonResponse, long pedido) {
        // LOG DETALHADO: JSON bruto da resposta
        Log.d("PedidosHelper_DEBUG", "=== RESPOSTA BRUTA DA API ===");
        Log.d("PedidosHelper_DEBUG", "Pedido: " + pedido);
        Log.d("PedidosHelper_DEBUG", "JSON Response: " + jsonResponse);
        Log.d("PedidosHelper_DEBUG", "Tamanho da resposta: " + (jsonResponse != null ? jsonResponse.length() : "null"));
        
        try {
            // Primeiro tenta processar como JSONArray diretamente
            JSONArray linksArray = new JSONArray(jsonResponse);
            Log.d("PedidosHelper_DEBUG", "Processando como JSONArray - Tamanho: " + linksArray.length());
            
            if (linksArray.length() == 0) {
                Log.d("PedidosHelper_DEBUG", "Array vazio - retornando mensagem padrão");
                return "Nenhum link de pagamento encontrado";
            }
            
            // LOG DETALHADO: Verifica cada item do array
            Log.d("PedidosHelper_DEBUG", "=== VERIFICANDO PAGAMENTO EFETUADO ===");
            for (int i = 0; i < linksArray.length(); i++) {
                JSONObject link = linksArray.getJSONObject(i);
                Log.d("PedidosHelper_DEBUG", "Item " + i + ": " + link.toString());
                
                // Verifica múltiplos campos que podem indicar pagamento
                String passouNoCaixa = link.optString("PASSOU_NO_CAIXA", "");
                String pagamentoEfetuado = link.optString("PagamentoEfetuado", "");
                String status = link.optString("Status", "");
                String statusPagamento = link.optString("StatusPagamento", "");
                
                Log.d("PedidosHelper_DEBUG", "  PASSOU_NO_CAIXA: '" + passouNoCaixa + "'");
                Log.d("PedidosHelper_DEBUG", "  PagamentoEfetuado: '" + pagamentoEfetuado + "'");
                Log.d("PedidosHelper_DEBUG", "  Status: '" + status + "'");
                Log.d("PedidosHelper_DEBUG", "  StatusPagamento: '" + statusPagamento + "'");
                
                // Verifica se algum campo indica pagamento já efetuado
                if ("S".equals(passouNoCaixa) || 
                    "true".equalsIgnoreCase(pagamentoEfetuado) ||
                    "PAGO".equalsIgnoreCase(status) ||
                    "PAGO".equalsIgnoreCase(statusPagamento)) {
                    Log.d("PedidosHelper_DEBUG", "*** PAGAMENTO JÁ EFETUADO DETECTADO! ***");
                    return "PAGAMENTO_JA_EFETUADO";
                }
            }
            
            StringBuilder resultado = new StringBuilder();
            resultado.append("Links de pagamento do pedido ").append(pedido).append(":\n\n");
            
            for (int i = 0; i < linksArray.length(); i++) {
                JSONObject link = linksArray.getJSONObject(i);
                String tipo = link.optString("Tipo", "N/A");
                String status = link.optString("Status", "N/A");
                String linkUrl = link.optString("Link", "N/A");
                
                String tipoDescricao;
                switch (tipo) {
                    case "P":
                        tipoDescricao = "PIX";
                        break;
                    case "C":
                        tipoDescricao = "Cartão de Crédito";
                        break;
                    case "D":
                        tipoDescricao = "Cartão de Débito";
                        break;
                    default:
                        tipoDescricao = "Outros";
                        break;
                }
                
                resultado.append("• ").append(tipoDescricao).append("\n");
                resultado.append("  Link: ").append(linkUrl).append("\n\n");
            }
            
            return resultado.toString();
            
        } catch (JSONException e) {
            Log.e("PedidosHelper_DEBUG", "Erro ao processar JSON Array: " + e.getMessage());
            Log.d("PedidosHelper_DEBUG", "=== TENTANDO PROCESSAR COMO JSONObject ===");
            
            // Fallback: tenta processar como JSONObject único
            try {
                JSONObject jsonObj = new JSONObject(jsonResponse);
                Log.d("PedidosHelper_DEBUG", "JSONObject criado com sucesso: " + jsonObj.toString());
                
                // Verifica se há erro na resposta
                if (jsonObj.has("erro")) {
                    Log.d("PedidosHelper_DEBUG", "Erro na resposta da API: " + jsonObj.getString("erro"));
                    return "Erro ao consultar links de pagamento do pedido";
                }
                
                // LOG DETALHADO: Verifica campos de pagamento no objeto principal
                Log.d("PedidosHelper_DEBUG", "=== VERIFICANDO PAGAMENTO NO OBJETO PRINCIPAL ===");
                String passouNoCaixa = jsonObj.optString("PASSOU_NO_CAIXA", "");
                String pagamentoEfetuado = jsonObj.optString("PagamentoEfetuado", "");
                String status = jsonObj.optString("Status", "");
                String statusPagamento = jsonObj.optString("StatusPagamento", "");
                String mensagem = jsonObj.optString("Mensagem", "");
                
                Log.d("PedidosHelper_DEBUG", "  PASSOU_NO_CAIXA: '" + passouNoCaixa + "'");
                Log.d("PedidosHelper_DEBUG", "  PagamentoEfetuado: '" + pagamentoEfetuado + "'");
                Log.d("PedidosHelper_DEBUG", "  Status: '" + status + "'");
                Log.d("PedidosHelper_DEBUG", "  StatusPagamento: '" + statusPagamento + "'");
                Log.d("PedidosHelper_DEBUG", "  Mensagem: '" + mensagem + "'");
                
                // Verifica se o pagamento já foi efetuado
                if ("S".equals(passouNoCaixa) || 
                    "true".equalsIgnoreCase(pagamentoEfetuado) ||
                    "PAGO".equalsIgnoreCase(status) ||
                    "PAGO".equalsIgnoreCase(statusPagamento) ||
                    (mensagem != null && mensagem.toUpperCase().contains("PAGAMENTO DESTE PEDIDO JÁ EFETUADO"))) {
                    Log.d("PedidosHelper_DEBUG", "*** PAGAMENTO JÁ EFETUADO DETECTADO NO OBJETO PRINCIPAL! ***");
                    return "PAGAMENTO_JA_EFETUADO";
                }
                
                // Verifica se é um link único (tem os campos Link, Tipo, Status)
                if (jsonObj.has("Link") && jsonObj.has("Tipo")) {
                    Log.d("PedidosHelper", "Processando JSONObject único como link de pagamento");
                    
                    String tipo = jsonObj.optString("Tipo", "N/A");
                String statusLink = jsonObj.optString("Status", "N/A");
                String linkUrl = jsonObj.optString("Link", "N/A");
                    
                    String tipoDescricao;
                    switch (tipo) {
                        case "P":
                            tipoDescricao = "PIX";
                            break;
                        case "C":
                            tipoDescricao = "Cartão de Crédito";
                            break;
                        case "D":
                            tipoDescricao = "Cartão de Débito";
                            break;
                        default:
                            tipoDescricao = "Outros";
                            break;
                    }
                    
                    StringBuilder resultado = new StringBuilder();
                    resultado.append("Links de pagamento do pedido ").append(pedido).append(":\n\n");
                    resultado.append("• ").append(tipoDescricao).append("\n");
                    resultado.append("  Link: ").append(linkUrl).append("\n\n");
                    
                    return resultado.toString();
                }
                
                // Verifica se há array de links no campo 'links'
                if (jsonObj.has("links")) {
                    JSONArray linksArray = jsonObj.getJSONArray("links");
                    Log.d("PedidosHelper_DEBUG", "=== VERIFICANDO ARRAY DE LINKS ===");
                    Log.d("PedidosHelper_DEBUG", "Tamanho do array de links: " + linksArray.length());
                    
                    if (linksArray.length() == 0) {
                        Log.d("PedidosHelper_DEBUG", "Array de links vazio");
                        return "Nenhum link de pagamento encontrado";
                    }
                    
                    // Verifica se algum link indica pagamento já efetuado
                    for (int i = 0; i < linksArray.length(); i++) {
                        JSONObject link = linksArray.getJSONObject(i);
                        Log.d("PedidosHelper_DEBUG", "Link " + i + ": " + link.toString());
                        
                        String passouNoCaixaLink = link.optString("PASSOU_NO_CAIXA", "");
                        String pagamentoEfetuadoLink = link.optString("PagamentoEfetuado", "");
                        String statusLink = link.optString("Status", "");
                        String statusPagamentoLink = link.optString("StatusPagamento", "");
                        
                        Log.d("PedidosHelper_DEBUG", "  Link PASSOU_NO_CAIXA: '" + passouNoCaixaLink + "'");
                        Log.d("PedidosHelper_DEBUG", "  Link PagamentoEfetuado: '" + pagamentoEfetuadoLink + "'");
                        Log.d("PedidosHelper_DEBUG", "  Link Status: '" + statusLink + "'");
                        Log.d("PedidosHelper_DEBUG", "  Link StatusPagamento: '" + statusPagamentoLink + "'");
                        
                        if ("S".equals(passouNoCaixaLink) ||
                            "true".equalsIgnoreCase(pagamentoEfetuadoLink) ||
                            "PAGO".equalsIgnoreCase(statusLink) ||
                            "PAGO".equalsIgnoreCase(statusPagamentoLink)) {
                            Log.d("PedidosHelper_DEBUG", "*** PAGAMENTO JÁ EFETUADO DETECTADO NO ARRAY DE LINKS! ***");
                            return "PAGAMENTO_JA_EFETUADO";
                        }
                    }
                    
                    StringBuilder resultado = new StringBuilder();
                    resultado.append("Links de pagamento do pedido ").append(jsonObj.optLong("pedido")).append(":\n\n");
                    
                    for (int i = 0; i < linksArray.length(); i++) {
                        JSONObject link = linksArray.getJSONObject(i);
                        String tipo = link.optString("Tipo", "N/A");
                        String statusLinkItem = link.optString("Status", "N/A");
                        String linkUrl = link.optString("Link", "N/A");
                        
                        String tipoDescricao;
                        switch (tipo) {
                            case "P":
                                tipoDescricao = "PIX";
                                break;
                            case "C":
                                tipoDescricao = "Cartão de Crédito";
                                break;
                            case "D":
                                tipoDescricao = "Cartão de Débito";
                                break;
                            default:
                                tipoDescricao = "Outros";
                                break;
                        }
                        
                        resultado.append("• ").append(tipoDescricao).append("\n");
                        resultado.append("  Link: ").append(linkUrl).append("\n\n");
                    }
                    
                    return resultado.toString();
                }
                
                // Se não tem nem Link nem links, não há links de pagamento
                Log.d("PedidosHelper", "Resposta não contém links de pagamento válidos");
                return "Nenhum link de pagamento encontrado";
                
            } catch (JSONException e2) {
                Log.e("PedidosHelper", "Erro ao processar JSON Object: " + e2.getMessage());
                // Se não é JSON válido, pode ser uma string simples de sucesso
                if (jsonResponse != null && jsonResponse.contains("Links de pagamento")) {
                    return jsonResponse;
                }
                return "Erro ao processar resposta da API";
            }
        }
    }


    public String ConsultarLinksComFallback(long pedido){

        try {
            Log.d("PedidosHelper", "Iniciando consulta de links para pedido: " + pedido);
            
            // Primeiro tenta consultar links de pagamento (versão atual) com timeout
            String resultado = null;
            try {
                resultado = ConsultarLinksPagamento(pedido);
                Log.d("PedidosHelper", "Resultado ConsultarLinksPagamento: " + resultado);
            } catch (Exception e) {
                Log.e("PedidosHelper", "Erro na primeira consulta: " + e.getMessage());
            }
            
            // Se não contém erro, retorna o resultado já processado
            if(resultado != null && !resultado.contains("Erro ao consultar")){
                Log.d("PedidosHelper", "Resultado formatado: " + resultado);
                return resultado;
            }
            
            // Se falhou, tenta consultar links de cartão (nova versão) com tratamento de erro
            Log.d("PedidosHelper", "Tentando ConsultarLinksCartao como fallback");
            try {
                resultado = ConsultarLinksCartao(pedido);
                Log.d("PedidosHelper", "Resultado ConsultarLinksCartao: " + resultado);
            } catch (Exception e) {
                Log.e("PedidosHelper", "Erro no fallback: " + e.getMessage());
                return "Erro ao consultar links de pagamento do pedido";
            }
            
            // Se também falhou, retorna mensagem de erro padrão
            if(resultado != null && resultado.contains("Erro ao consultar")){
                Log.e("PedidosHelper", "Ambas as APIs falharam para pedido: " + pedido);
                return "Erro ao consultar links de pagamento do pedido";
            }
            
            // Retorna o resultado do fallback já processado
            Log.d("PedidosHelper", "Resultado fallback formatado: " + resultado);
            return resultado;
            
        }
        catch (Exception ex){
            Log.e("PedidosHelper", "Exceção geral em ConsultarLinksComFallback: " + ex.getMessage());
            ex.printStackTrace();
            return "Erro ao consultar links de pagamento do pedido";
        }

    }

    public String EnviarLinksPagamento(Pedidos pedido){

        try {

            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<String> request = service.EnviarLinksPagamento(pedido, "Bearer " + service.token);

            Response<String> response = request.execute();

            if(response.isSuccessful()){
                return response.body();
            }
            else {
                return response.errorBody().string();
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return "Erro ao enviar links de pagamento do pedido";

    }

    public String EnviarLinksPagamentoPix(Pedidos pedido){

        try {
            Log.d("PedidosHelper", "Iniciando EnviarLinksPagamentoPix para pedido: " + pedido.getNumpedven());
            
            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<String> request = service.EnviarLinksPagamentoPix(pedido, "Bearer " + service.token);
            
            Log.d("PedidosHelper", "EnviarLinksPagamentoPix - URL da requisição: " + request.request().url());
            Log.d("PedidosHelper", "EnviarLinksPagamentoPix - Headers: " + request.request().headers());

            Response<String> response = request.execute();
            
            Log.d("PedidosHelper", "EnviarLinksPagamentoPix - Código de resposta: " + response.code());

            if(response.isSuccessful()){
                String responseBody = response.body();
                Log.d("PedidosHelper", "EnviarLinksPagamentoPix sucesso - Resposta: " + responseBody);
                return responseBody;
            }
            else {
                String errorBody = response.errorBody().string();
                Log.e("PedidosHelper", "EnviarLinksPagamentoPix erro - Código: " + response.code() + ", Erro: " + errorBody);
                return errorBody;
            }

        }
        catch (Exception ex){
            Log.e("PedidosHelper", "Exceção em EnviarLinksPagamentoPix: " + ex.getMessage());
            ex.printStackTrace();
        }

        return "Erro ao enviar links de pagamento do pedido";

    }

    public String ReenviarPixWhatsapp(Pedidos pedido){

        try {
            Log.d("PedidosHelper", "Iniciando ReenviarPixWhatsapp para pedido: " + pedido.getNumpedven());
            
            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<String> request = service.ReenviarPixWhatsapp(pedido, "Bearer " + service.token);
            
            Log.d("PedidosHelper", "ReenviarPixWhatsapp - URL da requisição: " + request.request().url());
            Log.d("PedidosHelper", "ReenviarPixWhatsapp - Headers: " + request.request().headers());

            Response<String> response = request.execute();
            
            Log.d("PedidosHelper", "ReenviarPixWhatsapp - Código de resposta: " + response.code());

            if(response.isSuccessful()){
                String responseBody = response.body();
                Log.d("PedidosHelper", "ReenviarPixWhatsapp sucesso - Resposta: " + responseBody);
                return responseBody;
            }
            else {
                String errorBody = response.errorBody().string();
                Log.e("PedidosHelper", "ReenviarPixWhatsapp erro - Código: " + response.code() + ", Erro: " + errorBody);
                return errorBody;
            }

        }
        catch (Exception ex){
            Log.e("PedidosHelper", "Exceção em ReenviarPixWhatsapp: " + ex.getMessage());
            ex.printStackTrace();
        }

        return "Erro ao reenviar PIX via WhatsApp";

    }

    public String EnviarWhatsappLinks(Pedidos pedido){

        try {
            Log.d("PedidosHelper", "Iniciando EnviarWhatsappLinks para pedido: " + pedido.getNumpedven());
            
            ApiGemco service = retrofit.create(ApiGemco.class);

            Call<String> request = service.EnviarWhatsappLinks(pedido, "Bearer " + service.token);
            
            Log.d("PedidosHelper", "EnviarWhatsappLinks - URL da requisição: " + request.request().url());
            Log.d("PedidosHelper", "EnviarWhatsappLinks - Headers: " + request.request().headers());

            Response<String> response = request.execute();
            
            Log.d("PedidosHelper", "EnviarWhatsappLinks - Código de resposta: " + response.code());

            if(response.isSuccessful()){
                String responseBody = response.body();
                Log.d("PedidosHelper", "EnviarWhatsappLinks sucesso - Resposta: " + responseBody);
                return responseBody;
            }
            else {
                String errorBody = response.errorBody().string();
                Log.e("PedidosHelper", "EnviarWhatsappLinks erro - Código: " + response.code() + ", Erro: " + errorBody);
                return errorBody;
            }

        }
        catch (Exception ex){
            Log.e("PedidosHelper", "Exceção em EnviarWhatsappLinks: " + ex.getMessage());
            ex.printStackTrace();
        }

        return "Erro ao enviar links de pagamento via WhatsApp";

    }

    public String ReenviarLinksWhatsapp(Pedidos pedido){
        try {
            Log.d("PedidosHelper", "Iniciando ReenviarLinksWhatsapp para pedido: " + pedido.getNumpedven());
            
            // Primeiro verifica se o pedido tem PIX
            boolean temPix = verificarSePedidoTemPix(pedido);
            
            if (temPix) {
                Log.d("PedidosHelper", "Pedido tem PIX, usando ReenviarPixWhatsapp");
                String resultado = ReenviarPixWhatsapp(pedido);
                
                // Verifica se o resultado indica sucesso
                if (resultado != null && (resultado.contains("sucesso") || resultado.contains("enviado"))) {
                    return resultado;
                } else {
                    Log.w("PedidosHelper", "ReenviarPixWhatsapp falhou, tentando EnviarWhatsappLinks como fallback");
                    return EnviarWhatsappLinks(pedido);
                }
            } else {
                Log.d("PedidosHelper", "Pedido não tem PIX, usando EnviarWhatsappLinks para cartão");
                return EnviarWhatsappLinks(pedido);
            }
            
        } catch (Exception ex) {
            Log.e("PedidosHelper", "Exceção em ReenviarLinksWhatsapp: " + ex.getMessage());
            ex.printStackTrace();
            return "Erro ao reenviar links de pagamento via WhatsApp";
        }
    }
    
    private boolean verificarSePedidoTemPix(Pedidos pedido) {
        try {
            // Consulta os links de pagamento para verificar se tem PIX
            String links = ConsultarLinksComFallback((long)pedido.getNumpedven());
            
            // Verifica se a resposta contém PIX
            boolean temPix = links != null && links.toLowerCase().contains("pix");
            
            Log.d("PedidosHelper", "Verificação PIX para pedido " + pedido.getNumpedven() + ": " + temPix);
            
            return temPix;
            
        } catch (Exception ex) {
            Log.e("PedidosHelper", "Erro ao verificar se pedido tem PIX: " + ex.getMessage());
            // Em caso de erro, assume que não tem PIX e tenta cartão
            return false;
        }
    }

    // Método de teste temporário para verificar ConsultarLinksPagamento diretamente
    public void testarConsultaLinks() {
        Log.d("PedidosHelper", "=== INICIANDO TESTE DE CONSULTA DE LINKS ===");
        Log.d("PedidosHelper", "Testando ConsultarLinksPagamento diretamente com pedido: 11264439");
        
        try {
            String resultado = ConsultarLinksPagamento(11264439);
            Log.d("PedidosHelper", "=== RESULTADO DO TESTE DIRETO ===");
            Log.d("PedidosHelper", "Resultado: " + resultado);
            Log.d("PedidosHelper", "=== FIM DO TESTE ===");
        } catch (Exception e) {
            Log.e("PedidosHelper", "Erro durante o teste: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
