package br.com.acalfortaleza.fikonmais;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.appcompat.widget.Toolbar;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.OutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;


import br.com.acalfortaleza.fikonmais.adapters.ItemPedidoAdapter;
import br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener;
import br.com.acalfortaleza.fikonmais.applications.TemplatePDFPedido;
import br.com.acalfortaleza.fikonmais.fragments.ProdutoFragments;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.modal.ModalItens;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Lotevencto;
import br.com.acalfortaleza.fikonmais.models.VendaPerfeita;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.PrecoConc;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.models.UtilPedido;
import br.com.acalfortaleza.fikonmais.popup.PopupCodigoMotor;
import br.com.acalfortaleza.fikonmais.popup.PopupEnderecoEntrega;
import br.com.acalfortaleza.fikonmais.popup.PopupItemPed;
import br.com.acalfortaleza.fikonmais.popup.PopupItemPedTodos;
import br.com.acalfortaleza.fikonmais.popup.PopupPedFechamento;
import br.com.acalfortaleza.fikonmais.popup.PopupInserirCupomPromocao;
import br.com.acalfortaleza.fikonmais.popup.PopupVerificaPromoFikon;
import br.com.acalfortaleza.fikonmais.popup.PopupVendaPerfeita;
import br.com.acalfortaleza.fikonmais.service.FreteServicoResponse;

import java.util.concurrent.TimeUnit;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Pedidos_LancamentoActivity extends BaseAcalActivity  implements ModalItens.ActionListener{
    private static final String DEBUG_SERVER_URL = "http://10.1.5.112:7777/event";
    private static final String DEBUG_SESSION_ID = "saved-order-total";
    private static final String DEBUG_RUN_ID = "pre-fix";
    private static final String DEBUG_LOGCAT_TAG = "SavedOrderTotalDebug";
    private static final String PEDIDO_ABERTURA_DEBUG_TAG = "PEDIDO_ABERTURA_DEBUG";
    private static final int REQUEST_VERIFICA_PROMO_FIKON = 4107;
    private static final int REQUEST_FECHAMENTO_PEDIDO = 4108;
    private static final int REQUEST_INSERIR_CUPOM_PROMOCAO = 4109;
    private static final String EXTRA_RECARREGAR_PEDIDO_APOS_FINALIZACAO = "recarregar_pedido_apos_finalizacao";
    private static final String EXTRA_BLOQUEAR_BOTOES_APOS_FINALIZACAO = "bloquear_botoes_apos_finalizacao";
    private static final String EXTRA_NUMPEDVEN_RECARREGAR = "numpedven_recarregar";

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    public static Button btn_finalizar;
    private AlertDialog alertaDialog;
    private static ItemPedidoAdapter adapter;
    private List<ItemPedidos> mList;
    private RecyclerView mRecycleView;
    public static Pedidos pedido;
    public static Pedidos pedidoSalvoOriginal = null;
    private static ImageView img_cliente;
    private CardView cardCliente;

    private CardView cardTotais;
    private static TextView txtNome;
    private static TextView txtEmail;
    private static TextView txtCgccpf;
    private static TextView txtFone;
    private static TextView txtTotal;
    private static TextView txtQtdItens;
    private static TextView txtPeso;
    private static TextView txtPesoEntrega;
    private static TextView txtMargem;
    private static TextView txtServEntrega;
    public static TextView txtNumpedido;
    private static TextView txtFrete;
    private int index;
    private boolean alteracao;
    public static boolean alteracaoFrete =false;

    private static HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private ProdutoHelper produtoHelper;
    private ProgressDialog dialog;
    private Handler handler;
    private String retorno;
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    final DecimalFormat df = new DecimalFormat("0.00");
    final DecimalFormat dfInteiro = new DecimalFormat("0");
    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
    private PedidosHelper helper;
    private ItemPedidos itemPedidos;
    private static DatabaseReference myRef;
    private static FirebaseDatabase database;
    private static boolean isGerente;
    private static ImageView marg_indicador;
    public static Button btn_adcionar;
    private String temEntrega="";

    private static  String iniciaComFrete="";
    private static boolean preservarTotalCarregado = false;
    private static double vlTotalCarregado = 0.0d;
    private static boolean pedidoAlteradoLocalmente = false;

    public static double vlfrete =0.00d;
    public static double servMemPed =0.00d;
    public static List<Integer> idsServMemPed = new ArrayList<>();
    // Flag para indicar se o usuário modificou seleções de serviços localmente
    public static boolean servicosAdicionaisAlterados = false;
    // Timestamp da última modificação local (ms desde epoch)
    public static long servicosAdicionaisLastModified = 0L;

    private UtilPedido statusPedido;
    private static android.content.Context appContext;
    private static Pedidos_LancamentoActivity instanciaAtual;
    private static boolean refreshPedidoPendente = false;
    private boolean bloquearBotoesEdicaoAposFinalizacao = false;
    private boolean debugAberturaApiEmAndamento = false;
    private long ultimoPedidoDebugAberturaApi = -1L;

    private  boolean retornoStatus = false;

    private long mLastClickTime = 0;

    private static TextView VendaPerfeitaConversao;
    private static TextView VendaPerfeitaSKU;
    private static TextView VendaPerfeitaTicket;
    private static TextView VendaPerfeitaDesconto;
    private static TextView VendaPerfeitaEntrega;
    private static LinearLayout LayoutVendaPerfeita;


    public static void AlteraDataItens(String date){

        for(int i = 0 ; i < pedido.getItens().size(); i++){
            atualizaItem(pedido.getItens().get(i),i);
        }

    }



    public static void prepararPreservacaoTotalCarregado(Pedidos pedidoAtual) {
        if (pedidoAtual != null && pedidoAtual.getNumpedven() > 0) {
            preservarTotalCarregado = true;
            vlTotalCarregado = pedidoAtual.getVlTotal();
        } else {
            preservarTotalCarregado = false;
            vlTotalCarregado = 0.0d;
        }
    }

    private static void liberarPreservacaoTotalCarregado() {
        preservarTotalCarregado = false;
        vlTotalCarregado = 0.0d;
    }

    private static void sincronizarPoliticaTotalCarregado(Pedidos pedidoAtual) {
        if (possuiItemNovoLocal(pedidoAtual)) {
            pedidoAlteradoLocalmente = true;
        }

        if (isPedidoExistente(pedidoAtual) && !pedidoAlteradoLocalmente) {
            prepararPreservacaoTotalCarregado(pedidoAtual);
        } else {
            liberarPreservacaoTotalCarregado();
        }
    }

    public static void marcarTotalComoRecalculavel() {
        liberarPreservacaoTotalCarregado();
        pedidoAlteradoLocalmente = true;
    }

    private static double getTotalParaExibicao(double totalCalculado) {
        // ===== OPCAO 2 USUARIO: TUDO HALF_UP (arredondamento comercial, 5,7789→5.78) =====
        // Substituiu FLOOR (truncamento) por HALF_UP para bater com a tela do sistema que
        // exibe "valor do item = 11,99 - 6,21 = 5,78".
        java.math.BigDecimal totalCalculadoFLOOR = java.math.BigDecimal.valueOf(totalCalculado).setScale(2, java.math.RoundingMode.HALF_UP);
        if (isPedidoExistente(pedido) && preservarTotalCarregado) {
            java.math.BigDecimal vlTotalCarregadoBD = java.math.BigDecimal.valueOf(vlTotalCarregado).setScale(2, java.math.RoundingMode.HALF_UP);
            java.math.BigDecimal diff = vlTotalCarregadoBD.subtract(totalCalculadoFLOOR).abs();
            if (diff.compareTo(java.math.BigDecimal.valueOf(0.02d)) > 0) {
                return vlTotalCarregado;
            }
        }
        return totalCalculadoFLOOR.doubleValue();
    }

    private static boolean isPedidoExistente(Pedidos pedidoAtual) {
        return pedidoAtual != null && pedidoAtual.getNumpedven() > 0;
    }

    private static boolean possuiItemNovoLocal(Pedidos pedidoAtual) {
        if (!isPedidoExistente(pedidoAtual) || pedidoAtual.getItens() == null) {
            return false;
        }

        for (ItemPedidos item : pedidoAtual.getItens()) {
            if (item != null && item.getNumpedven() <= 0) {
                return true;
            }
        }

        return false;
    }


    public static void carregaDadosCliente(Cliente cliente) {
        pedido.setCliente(cliente);

        txtNome.setText(cliente.getNomcli());
        if (cliente.getNatjur().equals("F")) {
            img_cliente.setImageResource(R.drawable.ic_pessoaf);
            txtCgccpf.setText("Cpf: " + cliente.getCgccpf().substring(0, 3) + "." + cliente.getCgccpf().substring(3, 6) + "." + cliente.getCgccpf().substring(6, 9) + "-" + cliente.getCgccpf().substring(9, 11));
        } else {
            img_cliente.setImageResource(R.drawable.ic_pessoaj);
            txtCgccpf.setText("Cnpj: " + cliente.getCgccpf().substring(0, 2) + "." + cliente.getCgccpf().substring(2, 5) + "." + cliente.getCgccpf().substring(5, 8) + "/" + cliente.getCgccpf().substring(8, 12) + "-" + cliente.getCgccpf().substring(12, 14));
        }

        if (cliente.getSexo().equals("F")) {
            img_cliente.setImageResource(R.drawable.ic_pessoam);
        }
        if (!cliente.getCelular().equals("")) {
            txtFone.setText("Celular: (" + cliente.getDddCelular() + ") " + cliente.getCelular());
        } else {
            txtFone.setText("");
        }

        if (!cliente.getEmail().equals("")) {
            txtEmail.setText("E-mail: " + cliente.getEmail());
        } else {
            txtEmail.setText("");
        }
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.ped_lanc_status_bar_bg);
        if (statusBarBg == null) {
            return;
        }

        ViewGroup.LayoutParams params = statusBarBg.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBarBg.setLayoutParams(params);
    }

    private void ajustarRodapeBotoes() {
        if (btn_finalizar != null) {
            final int margemBase = dpToPx(16);
            final ViewGroup.MarginLayoutParams paramsBaseSalvar = (ViewGroup.MarginLayoutParams) btn_finalizar.getLayoutParams();
            final int margemOriginalSalvar = paramsBaseSalvar.bottomMargin;
            ViewCompat.setOnApplyWindowInsetsListener(btn_finalizar, (view, insets) -> {
                androidx.core.graphics.Insets navigationInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                ViewGroup.MarginLayoutParams paramsSalvar = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                paramsSalvar.bottomMargin = margemOriginalSalvar + margemBase + navigationInsets.bottom;
                view.setLayoutParams(paramsSalvar);
                return insets;
            });
            ViewCompat.requestApplyInsets(btn_finalizar);
        }

        if (btn_adcionar != null) {
            final int margemBase = dpToPx(16);
            final ViewGroup.MarginLayoutParams paramsBaseAdicionar = (ViewGroup.MarginLayoutParams) btn_adcionar.getLayoutParams();
            final int margemOriginalAdicionar = paramsBaseAdicionar.bottomMargin;
            ViewCompat.setOnApplyWindowInsetsListener(btn_adcionar, (view, insets) -> {
                androidx.core.graphics.Insets navigationInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                ViewGroup.MarginLayoutParams paramsAdicionar = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                paramsAdicionar.bottomMargin = margemOriginalAdicionar + margemBase + navigationInsets.bottom;
                view.setLayoutParams(paramsAdicionar);
                return insets;
            });
            ViewCompat.requestApplyInsets(btn_adcionar);
        }
    }

    private void ajustarListaRodape() {
        if (mRecycleView == null) {
            return;
        }

        final int paddingLeftBase = mRecycleView.getPaddingLeft();
        final int paddingTopBase = mRecycleView.getPaddingTop();
        final int paddingRightBase = mRecycleView.getPaddingRight();
        final int paddingBottomBase = mRecycleView.getPaddingBottom();

        mRecycleView.setClipToPadding(false);
        ViewCompat.setOnApplyWindowInsetsListener(mRecycleView, (view, insets) -> {
            int alturaRodape = Math.max(
                    btn_finalizar != null ? Math.max(btn_finalizar.getHeight(), dpToPx(48)) : dpToPx(48),
                    btn_adcionar != null ? Math.max(btn_adcionar.getHeight(), dpToPx(48)) : dpToPx(48)
            );
            int espacoExtra = Math.max(dpToPx(16), alturaRodape + dpToPx(16));
            view.setPadding(
                    paddingLeftBase,
                    paddingTopBase,
                    paddingRightBase,
                    paddingBottomBase + espacoExtra
            );
            return insets;
        });
        ViewCompat.requestApplyInsets(mRecycleView);
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        );
    }

    public static void atualizaTotais(List<ItemPedidos> listaItens) {

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        final DecimalFormat df = new DecimalFormat("0.00");

        BigDecimal valorTotal =  new BigDecimal("0.00");
        BigDecimal valorTotalItem =  new BigDecimal("0.00");
        BigDecimal valorTotalLiquidoExibicao = new BigDecimal("0.00");
        BigDecimal valorTotalMarg = new BigDecimal("0.00");
        BigDecimal valorTMargem = new BigDecimal("0.00");
        BigDecimal valorCusto = new BigDecimal("0.00");
        BigDecimal valorFrete = new BigDecimal("0.00");
        double valorEncargos = 0.00d;
        BigDecimal valorIcms = new BigDecimal("0.00");
        double valorMargem = 0.00d;
        BigDecimal pesoTotal = new BigDecimal("0.00");
        BigDecimal pesoEntrega = new BigDecimal("0.00");
        BigDecimal valorIcmRed = new BigDecimal("0.00");
        BigDecimal valorDesconto = new BigDecimal("0.00");
        BigDecimal valorDescontoM = new BigDecimal("0.00");
        BigDecimal calcDesconto = new BigDecimal("0.00");

        txtQtdItens.setText("Qtd. Itens: " + listaItens.size());

        // Prioriza ler os serviços do banco (API ConsultarFreteServicoSelecionado) quando
        // estivermos carregando um pedido existente. Só usa cache/local como fallback.
        try {
            if (pedido != null && pedido.getNumpedven() > 0) {
                // Faz a consulta ao servidor em background para obter IDs oficiais dos serviços deste pedido
                new Thread(() -> {
                    try {
                        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS).build();
                        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                                .baseUrl(br.com.acalfortaleza.fikonmais.service.ApiGemco.BASE_URL)
                                .client(client)
                                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                                .build();
                        br.com.acalfortaleza.fikonmais.service.ApiGemco api = retrofit.create(br.com.acalfortaleza.fikonmais.service.ApiGemco.class);
                        String token = "Bearer " + br.com.acalfortaleza.fikonmais.service.ApiGemco.token;

                        retrofit2.Response<br.com.acalfortaleza.fikonmais.service.FreteServicoResponse> resp = api.ConsultarFreteServicoSelecionado((long) pedido.getNumpedven(), token).execute();
                        if (resp.isSuccessful() && resp.body() != null) {
                            java.util.List<Integer> ids = resp.body().getIdsSelecionados();
                            if (ids != null && !ids.isEmpty()) {
                                // Atualiza o pedido com os IDs oficiais do servidor
                                pedido.setServicosAdicionaisIds(ids);

                                // Agora busca a lista completa de serviços para mapear IDs -> nomes
                                retrofit2.Response<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> allResp = api.GetFreteServico(token).execute();
                                if (allResp.isSuccessful() && allResp.body() != null) {
                                    java.util.List<String> nomes = new java.util.ArrayList<>();
                                    for (br.com.acalfortaleza.fikonmais.models.FreteServico fs : allResp.body()) {
                                        if (ids.contains(fs.getIdServico())) nomes.add(fs.getServicoFrete());
                                    }
                                    if (!nomes.isEmpty()) {
                                        pedido.setServicosAdicionaisNomes(nomes);
                                        if (appContext instanceof android.app.Activity) {
                                            ((android.app.Activity) appContext).runOnUiThread(() -> txtServEntrega.setText("Serv. Entrega: " + android.text.TextUtils.join(", ", nomes)));
                                        }
                                        return;
                                    }
                                }
                            }
                        }

                        // Se não retornou nada do servidor, cai para o fallback local
                        if (pedido.getServicosAdicionaisNomes() != null && !pedido.getServicosAdicionaisNomes().isEmpty()) {
                            if (appContext instanceof android.app.Activity) {
                                ((android.app.Activity) appContext).runOnUiThread(() -> txtServEntrega.setText("Serv. Entrega: " + android.text.TextUtils.join(", ", pedido.getServicosAdicionaisNomes())));
                            }
                        } else if (pedido.getServicosAdicionaisIds() != null && !pedido.getServicosAdicionaisIds().isEmpty()) {
                            // fallback: mapear usando lista completa (assíncrona)
                            okhttp3.OkHttpClient client2 = new okhttp3.OkHttpClient.Builder().connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS).readTimeout(30, java.util.concurrent.TimeUnit.SECONDS).build();
                            retrofit2.Retrofit retrofitFallback = new retrofit2.Retrofit.Builder()
                                    .baseUrl(br.com.acalfortaleza.fikonmais.service.ApiGemco.BASE_URL)
                                    .client(client2)
                                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                                    .build();
                            br.com.acalfortaleza.fikonmais.service.ApiGemco api2 = retrofitFallback.create(br.com.acalfortaleza.fikonmais.service.ApiGemco.class);
                            retrofit2.Call<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> call = api2.GetFreteServico("Bearer " + br.com.acalfortaleza.fikonmais.service.ApiGemco.token);
                            call.enqueue(new retrofit2.Callback<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>>() {
                                @Override
                                public void onResponse(retrofit2.Call<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> call, retrofit2.Response<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        java.util.List<String> nomes = new java.util.ArrayList<>();
                                        for (br.com.acalfortaleza.fikonmais.models.FreteServico fs : response.body()) {
                                            if (pedido.getServicosAdicionaisIds().contains(fs.getIdServico())) nomes.add(fs.getServicoFrete());
                                        }
                                        if (!nomes.isEmpty()) {
                                            pedido.setServicosAdicionaisNomes(nomes);
                                            txtServEntrega.setText("Serv. Entrega: " + android.text.TextUtils.join(", ", nomes));
                                            return;
                                        }
                                    }
                                    txtServEntrega.setText("Serv. Entrega: -");
                                }

                                @Override
                                public void onFailure(retrofit2.Call<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> call, Throwable t) {
                                    txtServEntrega.setText("Serv. Entrega: -");
                                }
                            });
                        } else {
                            if (appContext instanceof android.app.Activity) {
                                ((android.app.Activity) appContext).runOnUiThread(() -> txtServEntrega.setText("Serv. Entrega: -"));
                            }
                        }

                    } catch (Exception e) {
                        android.util.Log.e("FreteDebug", "Erro ao buscar serviços do pedido no servidor", e);
                        if (appContext instanceof android.app.Activity) {
                            ((android.app.Activity) appContext).runOnUiThread(() -> {
                                if (pedido.getServicosAdicionaisNomes() != null && !pedido.getServicosAdicionaisNomes().isEmpty()) {
                                    txtServEntrega.setText("Serv. Entrega: " + android.text.TextUtils.join(", ", pedido.getServicosAdicionaisNomes()));
                                } else {
                                    txtServEntrega.setText("Serv. Entrega: -");
                                }
                            });
                        }
                    }
                }).start();
            } else {
                // Pedido novo / sem numped: usa nomes locais ou fallback
                if (pedido != null) {
                    if (pedido.getServicosAdicionaisNomes() != null && !pedido.getServicosAdicionaisNomes().isEmpty()) {
                        txtServEntrega.setText("Serv. Entrega: " + android.text.TextUtils.join(", ", pedido.getServicosAdicionaisNomes()));
                    } else if (pedido.getServicosAdicionaisIds() != null && !pedido.getServicosAdicionaisIds().isEmpty()) {
                        // fallback mapear ids para nomes assincronamente
                        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder().connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS).readTimeout(30, java.util.concurrent.TimeUnit.SECONDS).build();
                        retrofit2.Retrofit retrofitFallback2 = new retrofit2.Retrofit.Builder()
                                .baseUrl(br.com.acalfortaleza.fikonmais.service.ApiGemco.BASE_URL)
                                .client(client)
                                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                                .build();
                        br.com.acalfortaleza.fikonmais.service.ApiGemco api = retrofitFallback2.create(br.com.acalfortaleza.fikonmais.service.ApiGemco.class);
                        retrofit2.Call<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> call = api.GetFreteServico("Bearer " + br.com.acalfortaleza.fikonmais.service.ApiGemco.token);
                        call.enqueue(new retrofit2.Callback<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>>() {
                            @Override
                            public void onResponse(retrofit2.Call<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> call, retrofit2.Response<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    java.util.List<String> nomes = new java.util.ArrayList<>();
                                    for (br.com.acalfortaleza.fikonmais.models.FreteServico fs : response.body()) {
                                        if (pedido.getServicosAdicionaisIds().contains(fs.getIdServico())) nomes.add(fs.getServicoFrete());
                                    }
                                    if (!nomes.isEmpty()) {
                                        pedido.setServicosAdicionaisNomes(nomes);
                                        txtServEntrega.setText("Serv. Entrega: " + android.text.TextUtils.join(", ", nomes));
                                        return;
                                    }
                                }
                                txtServEntrega.setText("Serv. Entrega: -");
                            }

                            @Override
                            public void onFailure(retrofit2.Call<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> call, Throwable t) {
                                txtServEntrega.setText("Serv. Entrega: -");
                            }
                        });
                    } else {
                        txtServEntrega.setText("Serv. Entrega: -");
                    }
                }
            }
        } catch (Exception ex) {
            txtServEntrega.setText("Serv. Entrega: -");
        }

        iniciaComFrete = "N";
        if (pedido != null && (pedido.getVlFrete() > 0.0d || pedido.getVlFreteSelecionado() > 0.0d || pedido.getVlServicosAdicionais() > 0.0d)) {
            iniciaComFrete = "S";
        }
        for (ItemPedidos item : listaItens) {

            if(item.getTpped().equals("E") ){

                iniciaComFrete = "S" ;

            }


            double precoUnitarioExibicao = getValorExibicaoPedido(item, item.getPrecoUnit());
            valorTotalItem = BigDecimal.valueOf(precoUnitarioExibicao).multiply(BigDecimal.valueOf(item.getQtcomp()));
            valorTotal = valorTotal.add(valorTotalItem);
            valorTotalLiquidoExibicao = valorTotalLiquidoExibicao.add(getValorLiquidoLinhaExibicao(item));
            valorFrete = valorFrete.add(BigDecimal.valueOf(item.getVlFrete()).setScale(7,RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(item.getQtcomp())));
            valorTotalMarg = BigDecimal.valueOf(precoUnitarioExibicao).multiply(BigDecimal.valueOf(item.getQtcomp()));
            valorTMargem = valorTMargem.add(valorTotalMarg);
            valorCusto = valorCusto.add(BigDecimal.valueOf((item.getCusto()) * item.getQtcomp()));
            BigDecimal calcDescontoExibicao = getValorDescontoLinhaExibicao(item);
            calcDesconto = BigDecimal.valueOf(item.getVlDescItem()).multiply(BigDecimal.valueOf(item.getQtcomp()));
            valorDesconto = valorDesconto.add(calcDescontoExibicao);
            valorDescontoM = valorDescontoM.add(calcDesconto);

            pesoTotal = pesoTotal.add(BigDecimal.valueOf(item.getQtcomp()  * item.getPeso()));


            if(item.getTpped().equals("E")){
                pesoEntrega= pesoEntrega.add(BigDecimal.valueOf(item.getQtcomp()  * item.getPeso()));
            }


            if (item.getIcmsRed() == 0) {
                valorIcms = valorIcms.add(BigDecimal.valueOf((((precoUnitarioExibicao - item.getVlDescItem() ) * item.getQtcomp()) * item.getIcms()) / 100));

            } else {
                valorIcms = valorIcms.add(BigDecimal.valueOf(((((precoUnitarioExibicao - item.getVlDescItem() ) * item.getQtcomp()) - (((precoUnitarioExibicao - item.getVlDescItem() ) * item.getQtcomp()) * item.getIcmsRed()) / 100)
                        * item.getIcms()) / 100));

            }





        }

        valorTotal = valorTotal.setScale(6, RoundingMode.HALF_UP);
        valorDesconto = valorDesconto.setScale(6, RoundingMode.HALF_UP);


        valorTMargem = valorTMargem.subtract(valorDescontoM);

        if (listaItens.size() == 0 || valorTMargem.doubleValue()==0 || valorCusto.doubleValue() == 0) {

            valorEncargos = 0.00d;
            valorMargem = 0.00d;
        } else {

            valorEncargos = ((valorTMargem).doubleValue() * listaItens.get(0).getEncargos()) / 100;

            //calculo anterior
            //valorMargem = ((valorTotalMarg - valorEncargos - valorIcms - valorCusto) / (valorTotalMarg - valorEncargos - valorIcms)) * 100;

            valorMargem = ((valorTMargem.doubleValue() - valorEncargos - valorIcms.doubleValue() - valorCusto.doubleValue()) / (valorTMargem.doubleValue())) * 100;

        }




        pedido.setPmargRent(valorMargem);
        pedido.setVlDesconto(valorDesconto.setScale(2, RoundingMode.HALF_UP).doubleValue());
        pedido.setPeso(pesoTotal.doubleValue());
        pedido.setPesoEntrega(pesoEntrega.doubleValue());

        boolean pedidoTemEntrega = "S".equals(iniciaComFrete);
        double baseFrete = 0;
        if (pedidoTemEntrega) {
            if (pedido.getVlFrete() > 0) {
                double freteTotalRaw = pedidoSalvoOriginal != null ? pedidoSalvoOriginal.getVlFrete() : pedido.getVlFrete();
                baseFrete = freteTotalRaw;
            } else if (pedido.getPesoEntrega() > 0 && pedido.getVlFreteSelecionado() > 0) {
                if ((pedido.getPesoEntrega() / 1000) > 1.0) {
                    baseFrete = Math.floor(pedido.getVlFreteSelecionado() * (pedido.getPesoEntrega() / 1000));
                } else {
                    baseFrete = pedido.getVlFreteSelecionado();
                }
            } else {
                baseFrete = 0;
            }
        } else {
            baseFrete = 0;
        }

        android.util.Log.d("FreteDebug", "Calc base dados iniciaComFrete=" + iniciaComFrete + ", sel=" + pedido.getVlFreteSelecionado() + ", pesoEntrega=" + pedido.getPesoEntrega() + ", base=" + baseFrete);


        double servMem = pedido.getVlServicosAdicionais();
        android.util.Log.d("FreteDebug", "Lendo VlServicosAdicionais diretamente do objeto pedido: " + servMem);
        if (!pedidoTemEntrega) {
            baseFrete = 0.0d;
            servMem = 0.0d;
            pedido.setVlFrete(0.0d);
            pedido.setVlServicosAdicionais(0.0d);
            pedido.setServicosAdicionaisIds(new ArrayList<>());
            pedido.setServicosAdicionaisNomes(new ArrayList<>());
             vlfrete = 0.0d;
        } else if (pedido.getVlFrete() > 0) {
            vlfrete = pedido.getVlFrete();
        } else {
            vlfrete = baseFrete;
        }

        double freteTotalRawDisplay = (pedidoSalvoOriginal != null && pedidoSalvoOriginal.getVlFrete() > 0)
                ? pedidoSalvoOriginal.getVlFrete()
                : (pedido != null ? pedido.getVlFrete() : 0.0d);

        android.util.Log.d("FreteDebug", "Frete exibido (direto da API) = " + freteTotalRawDisplay);
        txtFrete.setText("Frete: " + real.format(freteTotalRawDisplay));
        double totalCalculado = valorTotalLiquidoExibicao.add(BigDecimal.valueOf(freteTotalRawDisplay))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        double totalParaExibicao = getTotalParaExibicao(totalCalculado);
        pedido.setVlTotal(totalParaExibicao);
        // #region debug-point H:totais-imediato
        reportDebugEvent("H", "Pedidos_LancamentoActivity.atualizaTotais",
                "[DEBUG] totais calculados imediatamente apos refresh",
                buildTotaisDebugSnapshot("totais-imediato", listaItens,
                        valorTotal.setScale(2, java.math.RoundingMode.HALF_UP),
                        valorDesconto.setScale(2, java.math.RoundingMode.HALF_UP),
                        baseFrete, servMem, vlfrete, totalCalculado, totalParaExibicao));
        // #endregion
        txtTotal.setText("Total: " + real.format(totalParaExibicao));

        if (pedido.getNumpedven() > 0 && iniciaComFrete.equals("S") && baseFrete > 0) {
            final double baseFreteFinal = baseFrete;
            final BigDecimal finalValorTotal = valorTotal;
            final BigDecimal finalValorDesconto = valorDesconto;
            final BigDecimal finalValorTotalLiquidoExibicao = valorTotalLiquidoExibicao.setScale(2, RoundingMode.DOWN);

            // Adicionando um log para ver o valor do frete base ANTES da chamada da API
            android.util.Log.d("FreteDebug", "-> INICIANDO THREAD. Frete base calculado: " + baseFreteFinal);

            new Thread(() -> {
                try {
                    // 1. Configura o cliente Retrofit
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS).build();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ApiGemco.BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ApiGemco api = retrofit.create(ApiGemco.class);
                    String token = "Bearer " + ApiGemco.token;

                    // 2. Executa a chamada
                    android.util.Log.d("FreteDebug", "--> CHAMANDO API para pedido: " + pedido.getNumpedven());
                    retrofit2.Response<FreteServicoResponse> response = api.ConsultarFreteServicoSelecionado((long) pedido.getNumpedven(), token).execute();

                    if (response.isSuccessful() && response.body() != null) {
                        FreteServicoResponse servicoResponse = response.body();
                        android.util.Log.d("FreteDebug", "---> RESPOSTA DA API: SUCESSO.");

                        // 3. Extrai o ValorTotal e os Ids
                        if (servicoResponse.getValorTotal() != null) {
                            double valorServico = servicoResponse.getValorTotal().doubleValue();
                            List<Integer> idsServicos = servicoResponse.getIdsSelecionados();
                            android.util.Log.d("FreteDebug", "----> IDs retornados da API: " + idsServicos);

                            // LOG PARA VER O VALOR DO SERVIÇO RETORNADO
                            android.util.Log.d("FreteDebug", "----> Valor do serviço retornado da API: " + valorServico);

                            if (valorServico > 0) {
                                double novoValorFreteServidor = baseFreteFinal + valorServico;
                                android.util.Log.d("FreteDebug", "-----> SOMA (API): Frete Base (" + baseFreteFinal + ") + Serviço (" + valorServico + ") = Novo Frete (" + novoValorFreteServidor + ")");

                                double freteTotalParaExibir;

                                if (Pedidos_LancamentoActivity.servicosAdicionaisAlterados) {
                                    android.util.Log.d("FreteDebug", "Seleções locais detectadas — preservando servicos locais. Ignorando overwrite da API.");
                                    double vlServicosLocal = pedido.getVlServicosAdicionais() > 0 ? pedido.getVlServicosAdicionais() : (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed > 0 ? br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed : 0.0d);
                                    pedido.setVlFrete(baseFreteFinal);
                                    pedido.setVlServicosAdicionais(vlServicosLocal);
                                    freteTotalParaExibir = baseFreteFinal + vlServicosLocal;
                                } else {
                                    double freteTotalRaw = pedidoSalvoOriginal != null ? pedidoSalvoOriginal.getVlFrete() : Math.max(0.0d, pedido.getVlFrete());
                                    double baseFreteNormalizado = freteTotalRaw;

                                    pedido.setVlFrete(baseFreteNormalizado);
                                    pedido.setVlServicosAdicionais(valorServico);
                                    if (idsServicos != null) {
                                        pedido.setServicosAdicionaisIds(idsServicos);
                                        idsServMemPed = new ArrayList<>(idsServicos);
                                        android.util.Log.d("FreteDebug", "IDs atualizados em idsServMemPed: " + idsServMemPed);
                                    }
                                    freteTotalParaExibir = baseFreteNormalizado;
                                }

                                double freteExibicaoAsync = freteTotalParaExibir;

                                double totalCalculadoServidor = finalValorTotalLiquidoExibicao
                                        .add(BigDecimal.valueOf(freteTotalParaExibir))
                                        .setScale(2, RoundingMode.HALF_UP)
                                        .doubleValue();
                                double totalParaExibicaoServidor = getTotalParaExibicao(totalCalculadoServidor);
                                pedido.setVlTotal(totalParaExibicaoServidor);
                                final double freteExibicaoFinalAsync = freteExibicaoAsync;
                                // #region debug-point I:totais-async-servidor
                                reportDebugEvent("I", "Pedidos_LancamentoActivity.atualizaTotais",
                                        "[DEBUG] totais recalculados apos retorno async de frete/servicos",
                                        buildTotaisDebugSnapshot("totais-async-servidor", listaItens,
                                                finalValorTotal.setScale(2, java.math.RoundingMode.HALF_UP),
                                                finalValorDesconto.setScale(2, java.math.RoundingMode.HALF_UP),
                                                baseFreteFinal, valorServico, freteExibicaoFinalAsync, totalCalculadoServidor, totalParaExibicaoServidor));
                                // #endregion

                                if (appContext instanceof Activity) {
                                    ((Activity) appContext).runOnUiThread(() -> {
                                        android.util.Log.d("FreteDebug", "------> ATUALIZANDO TELA (UI Thread). Frete: " + freteExibicaoFinalAsync + ", Total: " + pedido.getVlTotal());
                                        txtFrete.setText("Frete: " + real.format(freteExibicaoFinalAsync));
                                        txtTotal.setText("Total: " + real.format(pedido.getVlTotal()));

                                        pedido.setIndicadoresVendaPerfeita( PedidosHelper.ObterIndicadoresVendaPerfeita(pedido, freteTotalParaExibir) );
                                        AtualizarLayoutVendaPerfeita();
                                    });
                                }
                            } else {
                                android.util.Log.w("FreteDebug", "----> API retornou sucesso, mas o valor do serviço é 0 ou nulo. Nenhuma soma será feita.");
                            }
                        } else {
                            android.util.Log.w("FreteDebug", "---> RESPOSTA DA API: SUCESSO, mas corpo da resposta (ValorTotal) é nulo.");
                        }
                    } else {
                        // LOG EM CASO DE FALHA NA REQUISIÇÃO
                        android.util.Log.e("FreteDebug", "---> RESPOSTA DA API: FALHA. Código: " + response.code() + ", Mensagem: " + response.message());
                    }
                } catch (Exception ex) {
                    android.util.Log.e("FreteDebug", "!!!! ERRO CATCH na thread de busca de serviço !!!!", ex);
                }
            }).start();
        }



        if (valorMargem >= 36) {
            txtMargem.setTextColor(Color.parseColor("#00bfa5"));
            marg_indicador.setImageResource(R.drawable.fundo_bt_flutuante);
        }
        if (valorMargem >= 34 && valorMargem < 36) {
            txtMargem.setTextColor(Color.parseColor("#C8671E"));
            marg_indicador.setImageResource(R.drawable.fundo_bt_flutuante_orange);
        }

        if (valorMargem < 34) {
            txtMargem.setTextColor(Color.parseColor("#A61553"));
            marg_indicador.setImageResource(R.drawable.fundo_bt_flutuante_red);
        }

        txtPeso.setText("Peso Total: " + df.format(pesoTotal) + "kg");

        txtPesoEntrega.setText("Peso Entrega: " + df.format(pesoEntrega) + "kg");

        txtMargem.setText("Margem: " + df.format(valorMargem) + "%");



        if(pedido.getCodStatus() < 5 ) {
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("pedidos");

            myRef.child(usuarioLogado.get("codigo")).setValue(pedido);
        }

        if(isGerente){

            txtMargem.setText("Margem: " + df.format(valorMargem) + "%");

            txtMargem.setVisibility(View.VISIBLE);

        }else{

            txtMargem.setText("Margem: ");

            txtMargem.setVisibility(View.INVISIBLE);

        }
    }

    public static void setIniciaComFrete(String valor) {
        iniciaComFrete = valor;
    }

    public static void atualizaItem(ItemPedidos item, int position) {

        adapter.updateListitem(item, position);

    }

    public static void adicionarItemAoPedido(ItemPedidos item) {
        if (item == null || adapter == null) {
            return;
        }

        int position = ItemPedidoAdapter.mList != null ? ItemPedidoAdapter.mList.size() : 0;
        adapter.addListitem(item, position);

        if (pedido != null) {
            pedido.setItens(ItemPedidoAdapter.mList);
            ConsultaActivity.pedido = pedido;
            ProdutoFragments.pedido = pedido;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pedido, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (ItemPedidoAdapter.mList != null && pedido.getCodStatus() == 9) {
                pedido.setItens(ItemPedidoAdapter.mList);
                ConsultaActivity.pedido = pedido;
                ProdutoFragments.pedido = pedido;
                finish();
            } else {
                if(pedido.getStatus()==null || pedido.getCodStatus() == 10){
                    finish();

                }else{
                    if(alteracao==false){
                        pedido.setItens(ItemPedidoAdapter.mList);
                        ConsultaActivity.pedido = pedido;
                        ProdutoFragments.pedido = pedido;
                        finish();
                    }else
                    {
                        finish();
                    }

                }



            }
            finish();
            return true;
        }

        if(id == R.id.menu_agendar){

            //Toast.makeText(this, "Essa opção estara disponivel em breve!", Toast.LENGTH_SHORT).show();
            if(validarAgendamento(pedido.getItens()).equals("S")){
                Intent intentA = new Intent(Pedidos_LancamentoActivity.this, AgendamentoRetiraActivity.class);
                intentA.putExtra("pedido", (pedido));
                startActivity(intentA);
            }else{
                Toast.makeText(this, "Pedido não possui retirada para realizar agendamento!", Toast.LENGTH_SHORT).show();
            }




        }


        if (id == R.id.menu_compartilhar) {

            if (pedido.getNumpedven() > 0 && pedido.getFinanceiro() != null  && pedido.getFinanceiro().size()>0) {

                GerarPDF(pedido);


            } else {
                Toast.makeText(Pedidos_LancamentoActivity.this, "Pedido ainda não foi finalizado!", Toast.LENGTH_SHORT).show();
            }


        }


        if (id == R.id.menu_endereco) {
           /* boolean pedidoTemEntrega = false;
            if (ItemPedidoAdapter.mList != null) {
                pedido.setItens(ItemPedidoAdapter.mList);
            }
            if (pedido != null && pedido.getItens() != null) {
                for (ItemPedidos pedItem : pedido.getItens()) {
                    if ("E".equals(pedItem.getTpped())) { pedidoTemEntrega = true; break; }
                }
            }
            if (pedidoTemEntrega) {*/
            Intent intent = new Intent(Pedidos_LancamentoActivity.this, PopupEnderecoEntrega.class);
            intent.putExtra("isOrcamento", false);
            intent.putExtra("endereco", (Serializable) pedido.getCliente().getEnderecos());
            startActivity(intent);
            /*} else {
                pedido.setUrlMotor(usuarioLogado.get("urlmotor"));
                Intent intentMotor = new Intent(Pedidos_LancamentoActivity.this, PopupCodigoMotor.class);
                intentMotor.putExtra("pedido", pedido);
                startActivity(intentMotor);
            }*/
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onBackPressed() {

        if (ItemPedidoAdapter.mList != null && pedido.getCodStatus() == 9) {
            pedido.setItens(ItemPedidoAdapter.mList);
            ConsultaActivity.pedido = pedido;
            ProdutoFragments.pedido = pedido;
            finish();
        } else {
            if (pedido.getStatus() == null || pedido.getCodStatus() == 10) {
                finish();

            } else {
                if (alteracao == false) {
                    pedido.setItens(ItemPedidoAdapter.mList);
                    ConsultaActivity.pedido = pedido;
                    ProdutoFragments.pedido = pedido;
                    finish();
                } else {
                    finish();
                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_pedidos_lancamento);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.ped_lanc_toolbar);
        setSupportActionBar(toolbar);
        instanciaAtual = this;
        appContext = Pedidos_LancamentoActivity.this; // ANTES: appContext = getApplicationContext();
        // 1. Limpa os valores estáticos para garantir que não haja lixo de um pedido anterior.
        servMemPed = 0.0;
        idsServMemPed = new ArrayList<>();

        // 2. Verifica se o Intent contém a lista de IDs que enviamos do PedidosHelper/PopupTpNota.
        if (getIntent().hasExtra("servicos_adicionais_ids")) {
            // 3. Pega a lista do Intent e a armazena na variável estática 'idsServMemPed'.
            idsServMemPed = getIntent().getIntegerArrayListExtra("servicos_adicionais_ids");
            android.util.Log.d("FreteServicoCarry", "IDs de serviço recebidos na Tela de Pedido: " + idsServMemPed);
        }

        pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
        pedidoSalvoOriginal = clonarPedido(pedido);
        registrarResumoPedidoValoresDebug("Pedidos_LancamentoActivity_intent", pedido);

        if (pedido != null) {
            // Copia IDs vindos do Intent para o objeto pedido
            pedido.setServicosAdicionaisIds(idsServMemPed);
            // Fallback: se o Intent não trouxe IDs, mas o objeto 'pedido' já tem (copiados do orçamento), usa-os.
            if ((idsServMemPed == null || idsServMemPed.isEmpty())
                    && pedido.getServicosAdicionaisIds() != null
                    && !pedido.getServicosAdicionaisIds().isEmpty()) {
                idsServMemPed = new java.util.ArrayList<>(pedido.getServicosAdicionaisIds());
                android.util.Log.d("FreteServicoCarry", "Fallback aplicado. IDs carregados do objeto Pedido: " + idsServMemPed);
            }
            if (idsServMemPed == null || idsServMemPed.isEmpty()) {
                try {
                    String sNumOrc = getIntent().getStringExtra("numorc");
                    if (sNumOrc != null && !sNumOrc.isEmpty()) {
                        int numOrcKey = Integer.parseInt(sNumOrc);
                        java.util.List<Integer> ids = br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.get(numOrcKey);
                        if (ids == null) ids = new java.util.ArrayList<>();
                        if (ids != null && !ids.isEmpty()) {
                            idsServMemPed = new java.util.ArrayList<>(ids);
                            pedido.setServicosAdicionaisIds(idsServMemPed);
                            android.util.Log.d("FreteServicoCarry", "Fallback OrcamentoActivity aplicado. IDs: " + idsServMemPed);
                        }
                    }
                } catch (Exception ignore) {}
            }
        }

        if(getIntent().hasExtra("orcamento")){
            // Usamos a variável estática que já foi populada.
            if (idsServMemPed != null && !idsServMemPed.isEmpty()) {
                android.util.Log.d("FreteServicoCarry", "Forçando tipo de entrega para 'E' em todos os itens do pedido.");
                if (pedido != null && pedido.getItens() != null) {
                    for (ItemPedidos item : pedido.getItens()) {
                        item.setTpped("E");
                    }
                }
            }
        }

        registrarDumpCompletoPedidoAoEntrar("Pedidos_LancamentoActivity_entrada", pedido);

        statusPedido = new UtilPedido();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Pedido de Venda" );
        if(pedido != null){
            getSupportActionBar().setSubtitle(dfInteiro.format(pedido.getNumpedven()));
        }else{
            getSupportActionBar().setSubtitle("");
        }

        getSupportActionBar().setElevation(0);

        handler = new Handler();

        mRecycleView = findViewById(R.id.ped_lanc_listaItens);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        img_cliente = findViewById(R.id.ped_lanc_imgCliente);
        txtNome = findViewById(R.id.ped_lanc_txtCliente);
        txtCgccpf = findViewById(R.id.ped_lanc_txtCgccpf);
        txtEmail = findViewById(R.id.ped_lanc_txtEmail);
        txtFone = findViewById(R.id.ped_lanc_tctFone);
        txtPeso = findViewById(R.id.ped_lanc_txtPeso);
        txtPesoEntrega = findViewById(R.id.ped_lanc_txtPesoFrete);
        txtQtdItens = findViewById(R.id.ped_lanc_txtQtd);
        txtServEntrega = findViewById(R.id.ped_lanc_txtServEntrega);
        txtTotal = findViewById(R.id.ped_lanc_txtTotalOrc);
        txtMargem = findViewById(R.id.ped_lanc_txtMargem);
        txtFrete = findViewById(R.id.ped_lanc_txtFrete);
        btn_finalizar = findViewById(R.id.ped_lanc_btnSalvar);
        cardCliente = findViewById(R.id.ped_lanc_cardCliente);
        cardTotais = findViewById(R.id.ped_lanc_cardTotal);
        txtNumpedido = findViewById(R.id.ped_lanc_numOrc);
        marg_indicador = findViewById(R.id.adp_ped_indicador2);
        btn_adcionar = findViewById(R.id.ped_lanc_btnAdcionar);
        ajustarRodapeBotoes();
        ajustarListaRodape();

        VendaPerfeitaConversao = findViewById(R.id.ActivityPedidosLancamento_VendaPerfeitaConversao);
        VendaPerfeitaSKU = findViewById(R.id.ActivityPedidosLancamento_VendaPerfeitaSKU);
        VendaPerfeitaTicket = findViewById(R.id.ActivityPedidosLancamento_VendaPerfeitaTicket);
        VendaPerfeitaDesconto = findViewById(R.id.ActivityPedidosLancamento_VendaPerfeitaDesconto);
        VendaPerfeitaEntrega = findViewById(R.id.ActivityPedidosLancamento_VendaPerfeitaEntrega);
        LayoutVendaPerfeita = findViewById(R.id.ActivityPedidosLancamento_LayoutVendaPerfeita);

        alteracao = getIntent().getBooleanExtra("alteracao", false);
        helper = new PedidosHelper();
        loginHelper = new LoginHelper(Pedidos_LancamentoActivity.this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();

        if(usuarioLogado.get("isgerente").equals("SIM")){
            isGerente = true;
        }else{
            isGerente = false;
        }

        registrarDiagnosticoStatusPedidoAoEntrar();

        if (pedido != null) {
            if (deveNormalizarPedidoAoAbrir(pedido)) {
                restaurarMarcadoresPromocionaisSalvos(pedido);
                normalizarPrecoUnitarioItensPromocionaisCarregados(pedido);
            }

            sincronizarPoliticaTotalCarregado(pedido);
            final DecimalFormat dfInteiro = new DecimalFormat("0");
            mList = pedido.getItens();
            adapter = new ItemPedidoAdapter(this, mList,isGerente);
            mRecycleView.setAdapter(adapter);
            ConsultarMetasVendaPerfeita();
            atualizaTotais(pedido.getItens());
            registrarResumoPedidoValoresDebug("Pedidos_LancamentoActivity_apos_atualizaTotais", pedido);
            if (pedido.getCliente() != null) {
                carregaDadosCliente(pedido.getCliente());
            }
            atualizarEstadoBotoesPedido();
        }

        bloquearBotoesEdicaoAposFinalizacao = getIntent().getBooleanExtra(EXTRA_BLOQUEAR_BOTOES_APOS_FINALIZACAO, false);
        if (bloquearBotoesEdicaoAposFinalizacao) {
            atualizarEstadoBotoesPedido();
            iniciarRecarregamentoPedidoAposFinalizacaoSeNecessario();
        }

        btn_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                // #region debug-point A:click-finalizar
                reportDebugEvent("A", "Pedidos_LancamentoActivity.btn_finalizar.onClick",
                        "[DEBUG] clique em finalizar com snapshot do pedido antes das validacoes",
                        buildPedidoDebugSnapshot("btn_finalizar.onClick", pedido, refreshPedidoPendente));
                // #endregion

                for(int i = 0 ; i < pedido.getItens().size() ; i++){

                    if(pedido.getItens().get(i).getDpi()>7 && pedido.getCliente().getProfissao() != null && !pedido.getCliente().getProfissao().equals("")){
                        Toast.makeText(Pedidos_LancamentoActivity.this, "Cliente é um profissional Acal, desconto maximo permitido 7%!",Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (adapter.getItemCount() > 0) {
                    if (txtNome.getText().equals("Informe o cliente")) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Pedidos_LancamentoActivity.this);
                        builder.setTitle("Ops :|");
                        builder.setMessage("Para finalizar o pedido, informe o cliente!");
                        builder
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        Intent intent = new Intent(Pedidos_LancamentoActivity.this, ClientesActivity.class);
                                        intent.putExtra("pedido", true);
                                        startActivity(intent);
                                    }
                                });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.create().show();
                    } else {

                        if (pedido.getCliente().getSitcred() != null && pedido.getCliente().getSitcred().equals("Bloqueia Venda")) {
                            Toast.makeText(Pedidos_LancamentoActivity.this, "Cadastro do Cliente está com Situaçao: Bloqueia Venda!", Toast.LENGTH_LONG).show();
                        } else {
                            validarStatus(String.valueOf(pedido.getNumpedven()));
                        }
                    }
                } else {
                    Toast.makeText(Pedidos_LancamentoActivity.this, "Informe o produto!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pedido.getCliente() != null){
                    if(pedido.getCodStatus() != 6 ) {
                        Intent intent = new Intent(Pedidos_LancamentoActivity.this, ClientesActivity.class);
                        intent.putExtra("pedido", true);
                        intent.putExtra("cgccpf", pedido.getCliente().getCgccpf());
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(Pedidos_LancamentoActivity.this, ClientesActivity.class);
                        intent.putExtra("cgccpf", pedido.getCliente().getCgccpf());
                        startActivity(intent);
                    }
                }else{
                    Intent intent = new Intent(Pedidos_LancamentoActivity.this, ClientesActivity.class);
                    intent.putExtra("pedido", true);
                    startActivity(intent);
                }
            }
        });

        cardTotais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resultado = "";
                String motor="";
                String valordositens="";
                String valordosdesconto="";
                String valordofrete="";
                String totaldositens="";
                String totalcomfrete="";
                String pesoTotal="";
                String percdesc="";
                String consultor="";
                String filial="";
                String condpgto="";
                String percFrete="";
                double freteSalvo = Math.max(0.0, pedido.getVlFrete());
                double freteServ = Math.max(0.0, pedido.getVlServicosAdicionais());
                double freteTotal;

                // ===== CALCULO CONFIÁVEL (REGRA CONFERÊNCIA: FLOOR POR ITEM) =====
                // Nunca mais usar "VlTotal - frete" para calcular itens (frágil).
                // ====== IMPORTANTE: 2 ACUMULADORES SEPARADOS (CORREÇÃO DESCONTO 2X) ======
                //   - valorItensBRUTO_FLOOR = SOMA item-a-item (PrecoUnit * Qtde) → LINHA 1 "Valor dos itens"
                //       (NÃO subtrai desconto AQUI; desconto é aplicado UMA vez só no total abaixo)
                //   - totalItensLIQUIDO = valorItensBRUTO - VlDesconto(pedido) - VlDescPromo(pedido)
                //       → LINHA "Valor total dos itens" e base para total geral com frete.
                // Se subtrair descU dentro do loop E novamente VlDesconto abaixo = DESCONTO APLICADO 2X (bug).
                java.math.BigDecimal valorItensBRUTO_FLOOR = java.math.BigDecimal.ZERO;
                if (pedido.getItens() != null) {
                    for (ItemPedidos item : pedido.getItens()) {
                        if (item == null) continue;
                        java.math.BigDecimal precoU = java.math.BigDecimal.valueOf(item.getPrecoUnit());
                        java.math.BigDecimal qtde = java.math.BigDecimal.valueOf(item.getQtcomp());
                        // Para pedido salvo, o bruto exibido da linha deve seguir o mesmo
                        // criterio usado na exibicao/PDF: arredondar para baixo no total da linha.
                        java.math.BigDecimal brutoItem = precoU.multiply(qtde)
                                .setScale(2, java.math.RoundingMode.DOWN);
                        valorItensBRUTO_FLOOR = valorItensBRUTO_FLOOR.add(brutoItem);
                    }
                }
                double itensBruto = valorItensBRUTO_FLOOR.doubleValue();
                double descontoItens = getValorDescontoResumoPedido(pedido).doubleValue();
                double descPromoItens = pedido.getVlDescPromo();
                double descontoTotal = descontoItens + descPromoItens;

                // ===== Valor dos itens = BRUTO (SEM DESCONTO NENHUM APLICADO AINDA) =====
                double valorItensResumo = Math.max(0.0, itensBruto);

                // ===== Total dos itens = BRUTO - descontoTotal (aplicado EXATAMENTE UMA VEZ) =====
                double totalItensResumo = Math.max(0.0, itensBruto - descontoTotal);

                if (freteServ > 0.0d && freteSalvo > 0.0d) {
                    double totalRef = Math.max(0.0, pedido.getVlTotal());
                    double t1 = Math.max(0.0d, totalItensResumo + freteSalvo);
                    double t2 = Math.max(0.0d, totalItensResumo + freteSalvo + freteServ);
                    boolean freteCabecalhoJaIncluiServico = totalRef > 0.0d && Math.abs(totalRef - t1) <= Math.abs(totalRef - t2);
                    freteTotal = freteCabecalhoJaIncluiServico ? freteSalvo : (freteSalvo + freteServ);
                } else {
                    freteTotal = freteSalvo > 0.0d ? freteSalvo : freteServ;
                }

                double totalPedidoResumoCalculado = Math.max(0.0, totalItensResumo + freteTotal);

                // Valor exibido "Valor total do pedido / Total com frete" SEMPRE usa o cálculo
                // confiável (itens + frete), NÃO usa pedido.getVlTotal() que pode estar inconsistente
                // logo após fechamento popup (antes de recarregar do servidor).
                double totalPedidoResumo = totalPedidoResumoCalculado;
                String valortotalpedido = "Valor total do pedido: " + real.format(totalPedidoResumo);

                // ===== % FRETE: fórmula SIMPLES e CONSISTENTE (padrão outra tela info pedido) =====
                // REMOVIDO getPercFretePrevisto (calculava % sobre VlFreteSelecionado * peso,
                // gerando absurdos como 450% quando VlFreteSelecionado estava = serviço 40 e frete real 180).
                // Fórmula nova (igual a outra tela info pedido que estava correta em 180%):
                //   % frete = (ValorFrete / ValorItensLiquidoTotal) * 100
                // Base de cálculo = totalItensResumo = total dos itens LÍQUIDO após desconto (100 no exemplo usuário).
                // Com isso: frete=180 / itens=100 * 100 = 180% (igual tela correta, ao invés de 450%).
                double baseItensPercFrete = Math.max(1.0, totalItensResumo); // evita /0 (minimo R$1,00)
                double percentualFrete = (freteTotal / baseItensPercFrete) * 100.0;

                if (pedido.getTipoMotor() != null && !pedido.getTipoMotor().equals("")) {
                    if(pedido.getTipoMotor().equals("T")){
                        motor = "<b>" +  "Motor de Promoção: " + "</b>"+ "Desconto no Total";
                    }else{
                        motor = "<b>" +  "Motor de Promoção: " + "</b>"+ "Desconto nos Itens";
                    }
                }

                if (freteTotal > 0 && pedido.getDtpedido() == null )  {
                    valordositens =  "Valor dos itens: " + real.format(valorItensResumo);
                    valordosdesconto = "Valor do desconto: "+ real.format(descontoItens);
                    percdesc = "% do desconto: "+ df.format((descontoItens/(itensBruto))*100) +"%";
                    valordofrete = "Valor do frete: " + real.format(freteTotal);
                    percFrete =  "% do frete: "  + df.format(percentualFrete) + "%";
                    totaldositens = "Valor total dos itens: " + real.format(totalItensResumo);
                    totalcomfrete = "Valor total com frete: " + real.format(totalPedidoResumo);
                    pesoTotal =    "Peso total: " + df.format(pedido.getPeso()) + "kg";
                    consultor =   "Consultor: " + (pedido.getCodvendr()) +" - "+pedido.getConsultor() ;
                    filial =      "Filial: " +  (pedido.getFilial()) ;
                    condpgto =    "Cond. Pagamento: "  + (pedido.getFinanceiro().get(0).getCondicao()) ;

                    resultado = resultado + ("<b>" + valordositens + "<br />" +
                            "<b>" + valordosdesconto+ " <br />" +
                            "<b>" + percdesc+ " <br />" +
                            "<b>" + totaldositens + " <br />" +
                            "<b>" + valordofrete+ " <br />" +
                            "<b>" + percFrete+ " <br />" +
                            "<b>" + pesoTotal + " <br />" +
                            "<b>" + valortotalpedido + " <br />" +
                            "<b>" + totalcomfrete + " <br />" +
                            "<br />" +
                            "<b>" + consultor + " <br />" +
                            "<b>" + filial + " <br />" +
                            "<b>" + condpgto + " <br />" +
                            "<br />" +
                            motor + ""+ "<br />"+
                            "<br />"
                    );
                }
                if (freteTotal > 0 && pedido.getDtpedido() != null && alteracaoFrete==false){
                    valordositens =  "Valor dos itens: " + real.format(valorItensResumo);
                    valordosdesconto = "Valor do desconto: "+ real.format(descontoItens);
                    percdesc = "% do desconto: "+ df.format((descontoItens/(itensBruto) )*100) +" %";
                    valordofrete = "Valor do frete: " + real.format(freteTotal);
                    percFrete =  "% do frete: "  + df.format(percentualFrete) + "%";
                    totaldositens = "Valor total dos itens: " + real.format(totalItensResumo);
                    totalcomfrete = "Valor total com frete: " +  real.format(totalPedidoResumo);
                    pesoTotal =    "Peso total: " + df.format(pedido.getPeso()) + "kg";
                    consultor =   "Consultor: " + (pedido.getCodvendr()) +" - "+pedido.getConsultor() ;
                    filial =      "Filial: " +  (pedido.getFilial()) ;
                    condpgto =    "Cond. Pagamento: "  + (pedido.getFinanceiro().get(0).getCondicao()) ;


                    resultado = resultado + ("<b>" + valordositens + "<br />" +
                            "<b>" + valordosdesconto+ " <br />" +
                            "<b>" + percdesc+ " <br />" +
                            "<b>" + totaldositens + " <br />" +
                            "<b>" + valordofrete+ " <br />" +
                            "<b>" + percFrete+ " <br />" +
                            "<b>" + pesoTotal + " <br />" +
                            "<b>" + valortotalpedido + " <br />" +
                            "<b>" + totalcomfrete + " <br />" +
                            "<br />" +
                            "<b>" + consultor + " <br />" +
                            "<b>" + filial + " <br />" +
                            "<b>" + condpgto + " <br />" +
                            "<br />" +
                            motor + ""+ "<br />"+
                            "<br />"
                    );
                }
                if (freteTotal > 0 && pedido.getDtpedido() != null && alteracaoFrete==true)  {
                    valordositens =  "Valor dos itens: " + real.format(valorItensResumo);
                    valordosdesconto = "Valor do desconto: "+ real.format(descontoItens);
                    percdesc = "% do desconto: "+ df.format((descontoItens/(itensBruto))*100) +"%";
                    valordofrete = "Valor do frete: " + real.format(freteTotal);
                    percFrete =  "% do frete: "  + df.format(percentualFrete) + "%";
                    totaldositens = "Valor total dos itens: " + real.format(totalItensResumo);
                    totalcomfrete = "Valor total com frete: " +  real.format(totalPedidoResumo);
                    pesoTotal =    "Peso total: " + df.format(pedido.getPeso()) + "kg";
                    consultor =   "Consultor: " + (pedido.getCodvendr()) +" - "+pedido.getConsultor() ;
                    filial =      "Filial: " +  (pedido.getFilial()) ;
                    condpgto =    "Cond. Pagamento: "  + (pedido.getFinanceiro().get(0).getCondicao()) ;


                    resultado = resultado + ("<b>" + valordositens + "<br />" +
                            "<b>" + valordosdesconto+ " <br />" +
                            "<b>" + percdesc + " <br />" +
                            "<b>" + totaldositens + " <br />" +
                            "<b>" + valordofrete+ " <br />" +
                            "<b>" + percFrete+ " <br />" +
                            "<b>" + pesoTotal + " <br />" +
                            "<b>" + valortotalpedido + " <br />" +
                            "<b>" + totalcomfrete + " <br />" +
                            "<br />" +
                            "<b>" + consultor + " <br />" +
                            "<b>" + filial + " <br />" +
                            "<b>" + condpgto + " <br />" +
                            "<br />" +
                            motor + ""+ "<br />"+
                            "<br />"
                    );
                }
                if(freteTotal <= 0) {
                    valordositens =  "Valor dos itens: " + real.format(valorItensResumo);
                    valordosdesconto = "Valor do desconto: "+ real.format(descontoItens);
                    percdesc = "% do desconto: "+ df.format((descontoItens/(itensBruto))*100) +"%";
                    valordofrete = "Valor do frete: " + real.format(freteTotal);
                    totaldositens = "Valor total dos itens: " + real.format(totalItensResumo);
                    totalcomfrete = "Valor total com frete: " +  real.format(totalPedidoResumo);
                    pesoTotal =    "Peso total: " + df.format(pedido.getPeso()) + "kg";
                    consultor =   "Consultor: " + (pedido.getCodvendr()) +" - "+pedido.getConsultor() ;
                    filial =      "Filial: " +  (pedido.getFilial()) ;
                    condpgto =    "Cond. Pagamento: "  + (pedido.getFinanceiro().get(0).getCondicao()) ;


                    resultado = resultado + ("<b>" + valordositens + "<br />" +
                            "<b>" + valordosdesconto+ " <br />" +
                            "<b>" + percdesc + " <br />" +
                            "<b>" + totaldositens + " <br />" +
                            "<b>" + pesoTotal + " <br />" +
                            "<b>" + valortotalpedido + " <br />" +
                            "<b>" + totalcomfrete + " <br />" +
                            "<br />" +
                            "<b>" + consultor + " <br />" +
                            "<b>" + filial + " <br />" +
                            "<b>" + condpgto + " <br />" +
                            "<br />" +
                            motor + ""+ "<br />"+
                            "<br />"
                    );
                }

                AlertDialog.Builder alerta = new AlertDialog.Builder(Pedidos_LancamentoActivity.this);
                alerta.setIcon(R.drawable.ic_card_info);
                alerta.setCancelable(false);
                alerta.setTitle("Pedido nº " + dfInteiro.format(pedido.getNumpedven()));
                alerta.setMessage(Html.fromHtml(resultado));
                alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertaDialog = alerta.create();
                alertaDialog.show();
            }
        });

        btn_adcionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (pedido == null) {
                    pedido = new Pedidos();
                    pedido.setItens(new ArrayList<ItemPedidos>());
                    Intent intent = new Intent(Pedidos_LancamentoActivity.this, ConsultaActivity.class);
                    startActivity(intent);
                    ConsultaActivity.pedido = pedido;
                    finish();

                } else {
                    if (ConsultaActivity.pedido == null || pedido.getCodStatus() == 9 || pedido.getCodStatus() <= 3 ) {
                        pedido.setItens(ItemPedidoAdapter.mList);
                        ConsultaActivity.pedido = pedido;
                        if(ProdutoFragments.class != null){
                            ProdutoFragments.pedido = pedido;
                        }
                        Intent intent = new Intent(Pedidos_LancamentoActivity.this, ConsultaActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        pedido.setItens(ItemPedidoAdapter.mList);
                        ConsultaActivity.pedido = pedido;
                        if(ProdutoFragments.class != null){
                            ProdutoFragments.pedido = pedido;
                        }
                        finish();
                    }
                }
            }
        });

        if (getIntent().getSerializableExtra("cliente") != null) {
            pedido = new Pedidos();
            pedido.setCliente((Cliente) getIntent().getSerializableExtra("cliente"));
            pedido.setItens(new ArrayList<ItemPedidos>());
            carregaDadosCliente(pedido.getCliente());
            Intent intent = new Intent(Pedidos_LancamentoActivity.this, ConsultaActivity.class);
            startActivity(intent);
            ConsultaActivity.pedido = pedido;
            finish();
        }

        mRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecycleView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(pedido.getCodStatus() < 5 || pedido.getCodStatus() == 9 ) {
                            if(btn_finalizar.isEnabled()) {
                                ModalItens modalBottomSheet = new ModalItens();
                                modalBottomSheet.setActionListener(Pedidos_LancamentoActivity.this);
                                modalBottomSheet.show(getSupportFragmentManager(), "modalMenu");
                                itemPedidos = mList.get(position);
                                index = position;
                            }
                        }
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                        Intent i = new Intent(Pedidos_LancamentoActivity.this, ConsultaEstoqueActivity.class);
                        i.putExtra("codigo",String.valueOf(mList.get(position).getCoditprod()));
                        startActivity(i);
                    }
                })
        );

        LayoutVendaPerfeita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pedidos_LancamentoActivity.this, PopupVendaPerfeita.class);
                intent.putExtra("indicadores", pedido.getIndicadoresVendaPerfeita());
                intent.putExtra("metas", pedido.getMetasVendaPerfeita());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instanciaAtual == this) {
            instanciaAtual = null;
        }
        if (pedido != null) {
            pedido.setVlServicosAdicionais(0.0d);
        }
        // Limpar estado local ao sair da activity
        // Isso garante que na próxima abertura, a API será a fonte de verdade
        servicosAdicionaisAlterados = false;
        servicosAdicionaisLastModified = 0;
        servMemPed = 0.0;
        idsServMemPed = new ArrayList<>();

        android.content.SharedPreferences sp = getApplicationContext().getSharedPreferences("frete_pedido", android.content.Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor ed = sp.edit();
        ed.remove("servicos_total_sem_pedido");
        ed.remove("servicos_total");
        ed.remove("servicos_ids");
        ed.remove("servicos_nomes");
        ed.apply();

        android.util.Log.d("FreteDebug", "onDestroy: Estado local de serviços resetado (preparando para próxima abertura)");
    }

    @Override
    protected void onResume() {
        super.onResume();
        instanciaAtual = this;
        // #region debug-point D:on-resume
        reportDebugEvent("D", "Pedidos_LancamentoActivity.onResume",
                "[DEBUG] onResume antes de aplicar pedido na tela",
                buildPedidoDebugSnapshot("onResume", pedido, refreshPedidoPendente));
        // #endregion
        aplicarPedidoAtualNaTelaSeNecessario(refreshPedidoPendente);
        registrarDiagnosticoStatusPedidoAoEntrar();
    }

    public static void notificarPedidoAtualizadoAposFinalizacao() {
        pedidoAlteradoLocalmente = false;
        prepararPreservacaoTotalCarregado(pedido);
        refreshPedidoPendente = true;
    }

    private void aplicarPedidoAtualNaTelaSeNecessario(boolean forcarRefresh) {
        if (!forcarRefresh && pedido == null) {
            return;
        }

        if (pedido == null) {
            refreshPedidoPendente = false;
            return;
        }

        // #region debug-point E:aplicar-inicio
        reportDebugEvent("E", "Pedidos_LancamentoActivity.aplicarPedidoAtualNaTelaSeNecessario",
                "[DEBUG] inicio do refresh da tela de pedido",
                buildPedidoDebugSnapshot("aplicar-inicio", pedido, forcarRefresh));
        // #endregion

        if (deveNormalizarPedidoAoAbrir(pedido)) {
            restaurarMarcadoresPromocionaisSalvos(pedido);
            normalizarPrecoUnitarioItensPromocionaisCarregados(pedido);
        }

        sincronizarPoliticaTotalCarregado(pedido);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Pedido de Venda");
            getSupportActionBar().setSubtitle(dfInteiro.format(pedido.getNumpedven()));
        }

        if (txtNumpedido != null) {
            txtNumpedido.setText(dfInteiro.format(pedido.getNumpedven()));
        }

        if (pedido.getCliente() != null) {
            carregaDadosCliente(pedido.getCliente());
        }

        mList = pedido.getItens() != null ? pedido.getItens() : new ArrayList<>();
        if (mRecycleView != null) {
            adapter = new ItemPedidoAdapter(this, mList, isGerente);
            mRecycleView.setAdapter(adapter);
        }
        atualizarComissoesItensSalvosAoEntrar();

        atualizaTotais(mList);
        atualizarEstadoBotoesPedido();
        try {
            AtualizarLayoutVendaPerfeita();
        } catch (Exception ignore) {
        }
        // #region debug-point F:aplicar-fim
        reportDebugEvent("F", "Pedidos_LancamentoActivity.aplicarPedidoAtualNaTelaSeNecessario",
                "[DEBUG] fim do refresh da tela de pedido",
                buildPedidoDebugSnapshot("aplicar-fim", pedido, forcarRefresh));
        // #endregion
        refreshPedidoPendente = false;
    }

    private void atualizarComissoesItensSalvosAoEntrar() {
        if (pedido == null || pedido.getNumpedven() <= 0 || pedido.getItens() == null || pedido.getItens().isEmpty()) {
            return;
        }

        final List<ItemPedidos> itensPedido = pedido.getItens();
        final String codFilPreco = obterCodFilPrecoPedido();

        new Thread(() -> {
            try {
                if (produtoHelper == null) {
                    produtoHelper = new ProdutoHelper();
                }

                List<Integer> codigos = new ArrayList<>();
                for (ItemPedidos item : itensPedido) {
                    if (item != null && item.getCoditprod() > 0) {
                        codigos.add(item.getCoditprod());
                    }
                }

                HashMap<Integer, ProdutoGemco> produtosPorCodigo = produtoHelper.ConsultarComissoesProdutosPorCodigo(codigos, codFilPreco);
                boolean alterou = false;

                for (ItemPedidos item : itensPedido) {
                    if (item == null) {
                        continue;
                    }

                    ProdutoGemco produto = produtosPorCodigo.get(item.getCoditprod());
                    if (produto == null) {
                        continue;
                    }

                    if (Double.compare(item.getPercentualComissao(), produto.PercentualComissao) != 0
                            || Double.compare(item.getPercIncentivo(), produto.PercIncentivo) != 0) {
                        item.setPercentualComissao(produto.PercentualComissao);
                        item.setPercIncentivo(produto.PercIncentivo);
                        alterou = true;
                    }
                }

                if (alterou) {
                    runOnUiThread(() -> {
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("ComissaoItemDebug", "Falha ao atualizar comissao dos itens do pedido salvo", e);
            }
        }).start();
    }

    private String obterCodFilPrecoPedido() {
        if (pedido != null && pedido.getCodfil() > 0) {
            return String.valueOf(pedido.getCodfil());
        }

        if (usuarioLogado != null && usuarioLogado.get("codfil") != null && !usuarioLogado.get("codfil").trim().isEmpty()) {
            return usuarioLogado.get("codfil");
        }

        return "1";
    }

    private boolean deveNormalizarPedidoAoAbrir(Pedidos pedidoAtual) {
        return pedidoAtual != null
                && pedidoAtual.getNumpedven() > 0
                && !pedidoAlteradoLocalmente;
    }

    private JSONObject buildPedidoDebugSnapshot(String fase, Pedidos pedidoRef, boolean forcarRefresh) {
        JSONObject data = new JSONObject();
        try {
            data.put("fase", fase);
            data.put("forcarRefresh", forcarRefresh);
            data.put("refreshPedidoPendente", refreshPedidoPendente);
            data.put("pedidoAlteradoLocalmente", pedidoAlteradoLocalmente);
            data.put("numpedven", pedidoRef != null ? pedidoRef.getNumpedven() : 0);
            data.put("vlTotal", pedidoRef != null ? pedidoRef.getVlTotal() : 0);
            data.put("itens", pedidoRef != null && pedidoRef.getItens() != null ? pedidoRef.getItens().size() : 0);
            data.put("adapterItens", ItemPedidoAdapter.mList != null ? ItemPedidoAdapter.mList.size() : -1);
            data.put("consultaItens", ConsultaActivity.pedido != null && ConsultaActivity.pedido.getItens() != null
                    ? ConsultaActivity.pedido.getItens().size() : -1);

            if (pedidoRef != null && pedidoRef.getItens() != null && !pedidoRef.getItens().isEmpty()) {
                ItemPedidos item = pedidoRef.getItens().get(0);
                if (item != null) {
                    data.put("codprod0", item.getCoditprod());
                    data.put("precoUnit0", item.getPrecoUnit());
                    data.put("precoPrimario0", item.getPrecoPrimario());
                    data.put("vlDescItem0", item.getVlDescItem());
                    data.put("dpi0", item.getDpi());
                    data.put("isMotor0", item.getIsMotor());
                    data.put("situacao0", item.getSituacao());
                    data.put("qt0", item.getQtcomp());
                }
            }
        } catch (Exception ignore) {
        }
        return data;
    }

    private JSONObject buildFechamentoDebugSnapshot(String fase, Pedidos pedidoAtual, Pedidos pedidoBaseAvista, Pedidos pedidoAPrazo) {
        JSONObject data = new JSONObject();
        try {
            data.put("fase", fase);
            data.put("pedidoItens", pedidoAtual != null && pedidoAtual.getItens() != null ? pedidoAtual.getItens().size() : -1);
            data.put("pedidoVlTotal", pedidoAtual != null ? pedidoAtual.getVlTotal() : -1d);
            data.put("pedidoAlteradoLocalmente", pedidoAlteradoLocalmente);
            data.put("adapterItens", ItemPedidoAdapter.mList != null ? ItemPedidoAdapter.mList.size() : -1);
            data.put("consultaItens", ConsultaActivity.pedido != null && ConsultaActivity.pedido.getItens() != null
                    ? ConsultaActivity.pedido.getItens().size() : -1);
            data.put("pedidoBaseAvistaItens", pedidoBaseAvista != null && pedidoBaseAvista.getItens() != null ? pedidoBaseAvista.getItens().size() : -1);
            data.put("pedidoBaseAvistaVlTotal", pedidoBaseAvista != null ? pedidoBaseAvista.getVlTotal() : -1d);
            data.put("pedidoAPrazoItens", pedidoAPrazo != null && pedidoAPrazo.getItens() != null ? pedidoAPrazo.getItens().size() : -1);
            data.put("pedidoAPrazoVlTotal", pedidoAPrazo != null ? pedidoAPrazo.getVlTotal() : -1d);
        } catch (Exception ignore) {
        }
        return data;
    }

    private static JSONObject buildTotaisDebugSnapshot(String fase, List<ItemPedidos> listaItens,
                                                       BigDecimal valorTotalItens, BigDecimal valorDesconto,
                                                       double baseFrete, double valorServicos, double freteExibido,
                                                       double totalCalculado, double totalExibicao) {
        JSONObject data = new JSONObject();
        try {
            data.put("fase", fase);
            data.put("numpedven", pedido != null ? pedido.getNumpedven() : 0);
            data.put("listaItens", listaItens != null ? listaItens.size() : 0);
            data.put("valorTotalItens", valorTotalItens != null ? valorTotalItens.doubleValue() : 0);
            data.put("valorDesconto", valorDesconto != null ? valorDesconto.doubleValue() : 0);
            data.put("baseFrete", baseFrete);
            data.put("valorServicos", valorServicos);
            data.put("freteExibido", freteExibido);
            data.put("totalCalculado", totalCalculado);
            data.put("totalExibicao", totalExibicao);
            data.put("pedidoVlTotal", pedido != null ? pedido.getVlTotal() : 0);
            data.put("pedidoVlFrete", pedido != null ? pedido.getVlFrete() : 0);
            data.put("pedidoVlServicosAdicionais", pedido != null ? pedido.getVlServicosAdicionais() : 0);
            data.put("refreshPedidoPendente", refreshPedidoPendente);
            data.put("pedidoAlteradoLocalmente", pedidoAlteradoLocalmente);

            if (pedido != null && pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                ItemPedidos item = pedido.getItens().get(0);
                if (item != null) {
                    data.put("codprod0", item.getCoditprod());
                    data.put("precoUnit0", item.getPrecoUnit());
                    data.put("vlDescItem0", item.getVlDescItem());
                    data.put("qt0", item.getQtcomp());
                }
            }
        } catch (Exception ignore) {
        }
        return data;
    }

    private static void reportDebugEvent(String hypothesisId, String location, String msg, JSONObject data) {
        try {
            Log.d(DEBUG_LOGCAT_TAG, hypothesisId + " | " + location + " | " + msg + " | data=" + (data != null ? data.toString() : "{}"));
        } catch (Exception ignore) {
        }
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                JSONObject payload = new JSONObject();
                payload.put("sessionId", DEBUG_SESSION_ID);
                payload.put("runId", DEBUG_RUN_ID);
                payload.put("hypothesisId", hypothesisId);
                payload.put("location", location);
                payload.put("msg", msg);
                payload.put("data", data != null ? data : new JSONObject());
                payload.put("ts", System.currentTimeMillis());

                URL url = new URL(DEBUG_SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                byte[] body = payload.toString().getBytes("UTF-8");
                connection.setFixedLengthStreamingMode(body.length);
                OutputStream os = connection.getOutputStream();
                os.write(body);
                os.flush();
                os.close();
                connection.getResponseCode();
            } catch (Exception ignore) {
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }

    private void restaurarMarcadoresPromocionaisSalvos(Pedidos pedidoAtual) {
        if (pedidoAtual == null || pedidoAtual.getItens() == null || pedidoAtual.getItens().isEmpty()) {
            return;
        }

        // ===== CORRECAO CRITICA (Desc=6.50 forçado quando era 6.21) =====
        // > "A informação não esta puxando atualizada na tela de pedido lancamento activity
        //    ela continua sempre puxando 6,50 no desconto mesmo os valores não sendo mais esses"
        //
        // CAUSA 100% CONFIRMADA:
        //  SharedPreferences "pedidoPromocaoPrazo" foi gravado na 1ª vez em VISTA
        //  (VlDescItem=6.50). Depois ao EDITAR o pedido para PRAZO (e salvar corretamente
        //  6.21 no BD), o Popup NÃO atualizava esse cache (ou atualizava com valores
        //  inconsistentes). Ao retornar para Lancamento, ESTE MÉTODO CEGAMENTE sobrescrevia
        //  item.setVlDescItem(6.50) / setPrecoUnit(12.28) do CACHE VELHO → sempre voltava
        //  6.50 forçado (ignorando completamente o valor correto 6.21 que acabou de ser
        //  salvo no banco).
        //
        // CORRECAO (separacao rigorosa de responsabilidade):
        //  - Cache SharedPreferences serve SÓ para MARCADORES DE PROMOÇÃO (IsMotor,
        //    NomePromo, CodigoPromo, MensagemPromo, Situacao se vazia, etc) em pedidos
        //    NOVOS / ORCAMENTO (Numpedven <= 0).
        //  - Se o pedido JÁ ESTÁ SALVO (Numpedven > 0) → valores NUMÉRICOS (PrecoUnit,
        //    PrecoPrimario, VlDescItem, Dpi) VÊM EXCLUSIVAMENTE DO BANCO (fonte verdade).
        //    NUNCA MAIS sobrescrevemos estes campos do cache.
        final boolean pedidoJaSalvoBd = pedidoAtual.getNumpedven() > 0L;

        android.content.SharedPreferences preferences = getApplicationContext().getSharedPreferences("pedidoPromocaoPrazo", android.content.Context.MODE_PRIVATE);
        String jsonItens = preferences.getString("itens_" + (long) pedidoAtual.getNumpedven(), null);
        if (jsonItens == null || jsonItens.trim().isEmpty()) {
            return;
        }

        ItemPedidos[] itensCache = new Gson().fromJson(jsonItens, ItemPedidos[].class);
        if (itensCache == null || itensCache.length == 0) {
            return;
        }

        for (ItemPedidos itemAtual : pedidoAtual.getItens()) {
            ItemPedidos itemCache = localizarItemCorrespondenteCache(itensCache, itemAtual);
            if (itemAtual == null || itemCache == null) {
                continue;
            }

            // ================= MARCADORES (sempre, pois BD pode não persistir eles) ===========
            if (itemCache.getIsMotor() != null) {
                itemAtual.setIsMotor(itemCache.getIsMotor());
            }
            if (itemCache.getCodigoPromo() != null) {
                itemAtual.setCodigoPromo(itemCache.getCodigoPromo());
            }
            if (itemCache.getNomePromo() != null) {
                itemAtual.setNomePromo(itemCache.getNomePromo());
            }
            if (itemCache.getMensagemPromo() != null) {
                itemAtual.setMensagemPromo(itemCache.getMensagemPromo());
            }
            // VlDescPromo pode vir do cache (ajuda visual na barra promo, não afeta total)
            if (itemCache.getVldescPromo() > 0d) {
                itemAtual.setVldescPromo(itemCache.getVldescPromo());
            }

            if ((itemAtual.getSituacao() == null || itemAtual.getSituacao().trim().isEmpty())
                    && itemCache.getSituacao() != null) {
                itemAtual.setSituacao(itemCache.getSituacao());
            }

            // ================= VALORES NUMÉRICOS (SÓ SE ORÇAMENTO / NÃO SALVO) ===========
            // Se pedido JÁ SALVO (Numpedven > 0), BD é soberano → PULAR completamente.
            if (!pedidoJaSalvoBd) {
                if (itemCache.getPrecoUnit() > 0d) {
                    itemAtual.setPrecoUnit(
                            BigDecimal.valueOf(itemCache.getPrecoUnit())
                                    .setScale(2, RoundingMode.HALF_EVEN)
                                    .doubleValue()
                    );
                }

                if (itemCache.getPrecoPrimario() > 0d && itemAtual.getPrecoPrimario() <= 0d) {
                    itemAtual.setPrecoPrimario(
                            BigDecimal.valueOf(itemCache.getPrecoPrimario())
                                    .setScale(2, RoundingMode.HALF_EVEN)
                                    .doubleValue()
                    );
                }

                if (itemCache.getVlDescItem() > 0d) {
                    itemAtual.setVlDescItem(
                            BigDecimal.valueOf(itemCache.getVlDescItem())
                                    .setScale(6, RoundingMode.HALF_EVEN)
                                    .doubleValue()
                    );
                    itemAtual.setDpi(itemCache.getDpi());
                }
            }
        }
    }

    private ItemPedidos localizarItemCorrespondenteCache(ItemPedidos[] itensCache, ItemPedidos itemAtual) {
        if (itensCache == null || itemAtual == null) {
            return null;
        }

        for (ItemPedidos itemCache : itensCache) {
            if (itemCache == null) {
                continue;
            }

            boolean mesmoCodigo = itemCache.getCoditprod() == itemAtual.getCoditprod();
            boolean mesmaQuantidade = Math.abs(itemCache.getQtcomp() - itemAtual.getQtcomp()) < 0.0001d;
            boolean mesmoItem = itemCache.getItem() > 0 && itemAtual.getItem() > 0 && itemCache.getItem() == itemAtual.getItem();

            if (mesmoCodigo && (mesmoItem || mesmaQuantidade)) {
                return itemCache;
            }
        }

        return null;
    }

    private static void normalizarPrecoUnitarioItensPromocionaisCarregados(Pedidos pedidoAtual) {
        normalizarPrecoUnitarioItensPromocionaisCarregadosCore(pedidoAtual);
    }

    // Versao PUBLIC STATIC para chamar de PedidosActivity.java (antes de abrir Lancamento!)
    public static void normalizarPrecoUnitarioItensPromocionaisCarregadosStatic(Pedidos pedidoAtual) {
        normalizarPrecoUnitarioItensPromocionaisCarregadosCore(pedidoAtual);
    }

    private static void normalizarPrecoUnitarioItensPromocionaisCarregadosCore(Pedidos pedidoAtual) {
        if (pedidoAtual == null || pedidoAtual.getItens() == null) {
            return;
        }

        // ================= PEDIDO JÁ SALVO NO BANCO (Numpedven > 0) =================
        // Se o pedido já existe e está salvo no banco de dados/API, preservamos fielmente
        // os valores de desconto (VlDescItem e Dpi) retornados do servidor.
        if (pedidoAtual.getNumpedven() > 0) {
            for (ItemPedidos item : pedidoAtual.getItens()) {
                if (item == null) {
                    continue;
                }

                // Garante que o PrecoPrimario (base a vista) esteja preenchido
                if (item.getPrecoPrimario() <= 0.0d && item.getPrecoUnit() > 0.0d) {
                    item.setPrecoPrimario(item.getPrecoUnit());
                }

                // Se o item tem VlDescItem > 0 mas Dpi está zerado ou nulo, calcula o Dpi%
                if (item.getVlDescItem() > 0.0d && item.getPrecoUnit() > 0.0d && item.getDpi() <= 0.0d) {
                    double dpiCalc = BigDecimal.valueOf((item.getVlDescItem() / item.getPrecoUnit()) * 100.0d)
                            .setScale(6, RoundingMode.HALF_EVEN)
                            .doubleValue();
                    item.setDpi(dpiCalc);
                }
                // Se o item tem Dpi% > 0 mas VlDescItem está zerado ou nulo, calcula VlDescItem
                else if (item.getDpi() > 0.0d && item.getVlDescItem() <= 0.0d && item.getPrecoUnit() > 0.0d) {
                    double descCalc = BigDecimal.valueOf((item.getDpi() / 100.0d) * item.getPrecoUnit())
                            .setScale(6, RoundingMode.HALF_EVEN)
                            .doubleValue();
                    item.setVlDescItem(descCalc);
                }
            }
            return;
        }

        for (ItemPedidos item : pedidoAtual.getItens()) {
            if (item == null) {
                continue;
            }

                boolean descontoMotor = item.getIsMotor() != null
                    && "S".equalsIgnoreCase(item.getIsMotor().trim());
                if (descontoMotor) {
                if (item.getVlDescItem() <= 0.0d && item.getVldescPromo() > 0.0d) {
                    item.setVlDescItem(item.getVldescPromo());
                }
                continue;
                }

                double precoBase = item.getPrecoPrimario() > 0.0d
                    ? item.getPrecoPrimario()
                    : item.getPrecoUnit();
                item.setPrecoUnit(precoBase);
                item.setVlDescItem(0.0d);
                item.setDpi(0.0d);
                item.setVldescPromo(0.0d);
                item.setCodMotivo("");
                item.setMotivoDesc("");
                item.setCodigoPromo("");
                item.setNomePromo("");
                item.setMensagemPromo("");
        }
        if (pedidoAtual.getNumpedven() < 0) {
        boolean pedidoJaComAcrescimoPrazo = pedidoTemPrecoPromocionalJaComAcrescimoPrazo(pedidoAtual);
        for (ItemPedidos item : pedidoAtual.getItens()) {
            if (item == null) {
                continue;
            }

            // ===== REGRA NUCLEAR VERBATIM isMotor=S (Fikon promocional) =====
            // (VERBATIM usuario 10/09 17:00: "o que precisa ajustar é somente quando o item
            //  for ismotor, o acrescimo só vai se dar por conta do condicao A PRAZO, nunca
            //  vai diminuir se for a vista, pois o preço que vem da fikon ja é o promocionado")
            //
            // Ação: ZERAR TOTALMENTE VlDescItem e Dpi% para isMotor.
            // - A VISTA: Preco = o valor que veio da Fikon (79,90 ex: item 861915). Nunca menor.
            // - A PRAZO: só o PrecoUnit aplica /0.95 (84.11), VlDesc permanece 0.
            // PrecoPrimario: Inicializa com PU (79.90) se PP <=0: vira a base vista fixa (79.90).
            // Isso corrige instantaneamente qualquer pedido salvo que estava com 79.90 → 75.90
            // por ter VlDesc=4.00 subtraído em cima do preço já promocional.
            if (item.getIsMotor() != null && "S".equalsIgnoreCase(item.getIsMotor().trim())) {
                if (item.getPrecoPrimario() <= 0d && item.getPrecoUnit() > 0d) {
                    double precoBaseVista = BigDecimal.valueOf(item.getPrecoUnit())
                            .setScale(2, RoundingMode.HALF_EVEN)
                            .doubleValue();
                    item.setPrecoPrimario(precoBaseVista);
                }
                BigDecimal precoBaseVista = BigDecimal.valueOf(
                                item.getPrecoPrimario() > 0d ? item.getPrecoPrimario() : item.getPrecoUnit())
                        .setScale(2, RoundingMode.HALF_EVEN);
                BigDecimal descontoAtual = BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem()))
                        .setScale(6, RoundingMode.HALF_EVEN);
                if (precoBaseVista.compareTo(BigDecimal.ZERO) > 0) {
                    item.setDpi(descontoAtual.multiply(BigDecimal.valueOf(100.0d))
                            .divide(precoBaseVista, 6, RoundingMode.HALF_EVEN)
                            .doubleValue());
                } else {
                    item.setDpi(0.0d);
                }
                continue; // isMotor nao precisa de mais nenhuma regra abaixo
            }

            // ===== CORRECAO CRITICA: RECUPERAR BASE VISTA de rodadas anteriores corrompidas =====
            // Cenario do item 861915 Numpedven=13843437:
            //   Rodadas anteriores com BUG sobrescreveram PrecoPrimario = 88.32 (prazo duplicado!)
            //   quando deveria ser 79.90 (base vista). Hoje o item chega com:
            //     PrecoPrimario = 88.32 | PrecoUnit = 88.32 (iguais!).
            //   Depois /0.95 de novo dá 92.97 → loop infinito.
            //
            // SOLUCAO: detectar numericamente se o PrecoPrimario atual é SUSPEITO de estar em
            // estado prazo corrompido. Regra de 3 pilares (qualquer um que bater, reverter!):
            //   (A) PrecoPrimario > 0 e é numericamente IGUAL ao PrecoUnit (diferença < 1 centavo)
            //       → PrecoPrimario tem que SER base vista (diferente de PU prazo!). Se for igual,
            //       com certeza PrecoPrimario foi sobrescrito indevidamente em rodadas anteriores.
            //   (B) PrecoPrimario atual eh MUITO PROXIMO do esperado PRAZO (PP atual ≈ baseVistaAntiga / 0.95)
            //       → por definição PrecoPrimario deve ser base vista (NÃO prazo /0.95).
            //   (C) Fallback (ajuda casos raros): se item é motor/promocional e ainda não tem
            //       PrecoPrimario de base vista reconhecivel.
            //
            // Se bater: reverter a formula do calcularPrecoUnitarioBrutoPromocionalPrazo para
            // chegar na base vista original. E gravar no item.setPrecoPrimario() (corrige permanentemente
            // a base vista desde já).
            if (item.getPrecoPrimario() > 0d && isItemPromocional(item)) {
                BigDecimal ppAtual = BigDecimal.valueOf(item.getPrecoPrimario()).setScale(2, RoundingMode.HALF_EVEN);
                BigDecimal puAtual = BigDecimal.valueOf(item.getPrecoUnit()).setScale(2, RoundingMode.HALF_EVEN);
                BigDecimal descAtual = BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem())).setScale(6, RoundingMode.HALF_EVEN);
                BigDecimal TOL_CENTAVO = new BigDecimal("0.01");
                boolean precoPrimarioIgualPrecoUnit = ppAtual.subtract(puAtual).abs().compareTo(TOL_CENTAVO) <= 0;

                // Heuristica: PrecoPrimario SUSPEITO se for >= 1.04 * precoMinimoEsperadoBaseVista
                // (PrecoPrimario nao pode ser o prazo, tem que ser a base menor = vista).
                // Revertendo a formula de calcularPrecoUnitarioBrutoPromocionalPrazo:
                //   precoUnitPrazo = (baseVista − desconto)/0.95 + desconto
                //   baseVista = (precoUnitPrazo − desconto) * 0.95 + desconto
                BigDecimal TOL_5_PCT = ppAtual.multiply(new BigDecimal("0.05")).max(new BigDecimal("0.02"));
                // Considera que o PrecoPrimario atual poderia ser "prazo embutido": reverte p/ chegar base vista.
                BigDecimal baseVistaRevertidaDoPP = ppAtual.subtract(descAtual)
                        .multiply(new BigDecimal("0.95")).add(descAtual)
                        .setScale(2, RoundingMode.HALF_EVEN);
                boolean ppPareceSerPrazo = ppAtual.compareTo(baseVistaRevertidaDoPP) > 0
                        && ppAtual.subtract(baseVistaRevertidaDoPP).abs().compareTo(TOL_5_PCT) >= 0;

                if (precoPrimarioIgualPrecoUnit || ppPareceSerPrazo) {
                    // PRECO PRIMARIO ESTAVA CORROMPIDO. Recalculamos a base vista correta e
                    // sobrescrevemos PrecoPrimario uma unica vez (agora ele se torna imutavel
                    // depois dessa normalizacao).
                    double baseVistaCorreta;
                    if (pedidoJaComAcrescimoPrazo && isItemPromocionalPorSituacaoSemMotor(item)) {
                        baseVistaCorreta = reverterPrecoUnitarioBrutoPromocionalPrazo(item.getPrecoUnit(), item);
                    } else {
                        baseVistaCorreta = baseVistaRevertidaDoPP.doubleValue();
                    }
                    item.setPrecoPrimario(
                            BigDecimal.valueOf(baseVistaCorreta)
                                    .setScale(2, RoundingMode.HALF_EVEN)
                                    .doubleValue()
                    );
                    continue; // PrecoPrimario resolvido, pular resto
                }
            }

            if (item.getPrecoPrimario() <= 0d && item.getPrecoUnit() > 0d) {
                double precoPrimario = item.getPrecoUnit();
                if (pedidoJaComAcrescimoPrazo && isItemPromocionalPorSituacaoSemMotor(item)) {
                    precoPrimario = reverterPrecoUnitarioBrutoPromocionalPrazo(item.getPrecoUnit(), item);
                }
                item.setPrecoPrimario(
                        BigDecimal.valueOf(precoPrimario)
                                .setScale(2, RoundingMode.HALF_EVEN)
                                .doubleValue()
                );
            }

            // ===== CORRECAO CRITICA: CONSISTENCIA VlDescItem x PrecoPrimario x PrecoUnit =====
            // Resolve o bug do usuario: "item Fikon 79.90 baixou para 75.90".
            // CAUSA RAIZ (dois caminhos):
            //   CAMINHO A (comum em Fikon):
            //     1. PopupVerificaPromoFikon gera:
            //        PrecoUnit = 83.90 (preco tabela original) | VlDescItem = 4.00 (desconto Fikon)
            //        Liquido esperado A VISTA = 83.90 - 4.00 = 79.90 ✅ (preco promocional)
            //     2. Nossa regra anterior de BASE VISTA (PrecoPrimario = PrecoUnit quando PP=0)
            //        → PrecoPrimario = 83.90 (igual PU, OK). Depois aplicarPrecoDaCondicao (A VISTA)
            //        → helper redefine base vista = getPrecoUnitarioBrutoBaseVista = PU ou PP = 83.90.
            //        Ate aqui OK. Mas...
            //
            //   CAMINHO B (o que o usuario relata, 79.90 → 75.90):
            //     1. PrecoUnit da tela/adapter = 79.90 (ja o preco final promocional exibido).
            //     2. Inicializacao PrecoPrimario (quando PP<=0) = PrecoUnit → PP = 79.90.
            //     3. VlDescItem CONTINUA = 4.00 (calculado em cima do preco tabela 83.90 original).
            //     4. Resultado liquido = PP - VlDescItem = 79.90 - 4.00 = 75.90 ❌!
            //        (desconto de 4.00 aplicado DEMAIS, em uma base que ja era promocional!).
            //
            // SOLUCAO: Garantir numericamente a consistencia:
            //   "O liquido a vista do item, calculado por (PP - vlDesc), deve ser IGUAL ao
            //    liquido a vista calculado por (PU - vlDesc) que sempre foi a conta original."
            //
            //   LiquidoEsperado = PrecoUnit (tabela) − VlDescItem (desconto Fikon) = preco promocional.
            //   LiquidoCalcPP   = PrecoPrimario (base vista nova) − VlDescItem_Ajustar
            //
            //   Igualando as duas para achar VlDescItem correto em CENÁRIO B:
            //      VlDescItem_Ajustar = PrecoPrimario - LiquidoEsperado
            //                         = PrecoPrimario - (PrecoUnit - VlDescItem_Atual)
            //
            //   Exemplo CENÁRIO B:
            //      PP=79.90, PU=79.90, VlDescAtual=4.00 → LiquidoEsp = 79.90-4 = 75.90.
            //      VlDescAjust = 79.90 - (79.90-4) = 4.00 → não resolve? Wait (errado!).
            //
            //   Vamos mudar a percepção: Se o item tem CodigoPromo ou NomePromo (Fikon),
            //   é PROMOÇÃO FIKON. O preco PROMOCIONAL de fato é o preco A VISTA LIQUIDO.
            //   Este valor deve ser PRESERVADO a todo custo. Não importa o que PP/PU/Desc são,
            //   o LIQUIDO A VISTA final deve ser igual ao preco promocional Fikon.
            //
            //   Como recuperar esse valor SEM O HISTORICO? O menor entre PP-Desc e PU-Desc é
            //   o provavel preco promocional. Ou o preco que a Fikon retornou.
            //   SIMPLESMENTE: se (PP - Desc) != (PU - Desc) → PP != PU com certeza!
            //   → Ajustar VlDescItem = max(0, PP - (PU - VlDescItem_Atual))
            //     → assim Liquido por PP fica igual a Liquido por PU = preco promocional.
            //
            //   Exemplo B: PP=79.90, PU=79.90, Desc=4.00 (PU - Desc = 75.90, liquido baixo!)
            //     ... neste caso PU e Desc são incoerentes (PU já é o promocional mas Desc tb existe).
            //     Aplicar reverso para checar: se item já tem CodPromo e Dpi >0 e (VlDescItem = 4.00)
            //     e PP == PU → VlDescItem deve ser = 0 (pois o preco promocional já é o PU novo).
            //
            //   Formula FINAL GENERICA 100% robusta (qualquer caso, não depende de flag):
            //     liquidoPorPrecoUnit = PU - vlDescAtual (liquido historico correto)
            //     liquidoPorPrecoPrimario = PP - vlDescAtual (liquido novo, pode ter dado 75.90 errado)
            //     Se (|liquidoPorPU - liquidoPorPP| > 0.02) → INCONSISTENCIA DETECTADA.
            //     Correção: novoVlDesc = max(0, PP - liquidoPorPrecoUnit).
            //     → (PP - novoVlDesc) = liquidoPorPrecoUnit = mesmo valor. Iguala os liquidos.
            //
            // Testes na formula:
            //   Caso A (correto): PP=83.90 PU=83.90 Desc=4.00 → liqPU=79.90; liqPP=79.90 → OK.
            //   Caso B (erro usuario): PP=79.90 PU=83.90 Desc=4.00 → liqPU=79.90; liqPP=75.90 ❌.
            //     → novoVlDesc = max(0, 79.90 - 79.90) = 0 ✅.
            //     → (PP - Desc novo) = 79.90 - 0 = 79.90 (preco promocional correto!).
            //   Caso B2: PP=79.90 PU=79.90 Desc=4.00 (PU já era promocional, veio da tela adapter).
            //     → liqPU = 75.90, liqPP = 75.90 (iguais, mas errados!).
            //     → Formula sozinha não pega por que são iguais. Para esse, segunda regra:
            //       Se item tem CodigoPromo/NomePromo/mensagemPromo (Fikon), e DPI = VlDesc / PU ≈ 0%
            //       → ou seja, se VlDescItem > 0 mas o Dpi = VlDesc / PP for muito abaixo do esperado
            //         (pois PP nao bate), assumir que LiquidoCorretoAVista = PU ou PP (menor deles).
            //
            // Resumindo: aplicar ajuste DUAS REGRAS:
            if (isItemPromocional(item)) {
                BigDecimal pu = BigDecimal.valueOf(item.getPrecoUnit()).setScale(2, RoundingMode.HALF_EVEN);
                BigDecimal pp = BigDecimal.valueOf(item.getPrecoPrimario()).setScale(2, RoundingMode.HALF_EVEN);
                BigDecimal desc = BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem())).setScale(6, RoundingMode.HALF_EVEN);
                BigDecimal liqPorPU = pu.subtract(desc).setScale(2, RoundingMode.HALF_EVEN);
                BigDecimal liqPorPP = pp.subtract(desc).setScale(2, RoundingMode.HALF_EVEN);
                BigDecimal TOL = new BigDecimal("0.02");

                // Regra 1: Iguala liquidos se PP != PU
                if (pp.compareTo(pu) != 0 && liqPorPU.subtract(liqPorPP).abs().compareTo(TOL) > 0) {
                    BigDecimal novoVlDesc = pp.subtract(liqPorPU).max(BigDecimal.ZERO);
                    item.setVlDescItem(novoVlDesc.setScale(6, RoundingMode.HALF_EVEN).doubleValue());
                    desc = novoVlDesc;
                }

                // Regra 2: Caso PU == PP == preco final promocional Fikon, mas VlDesc > 0 ainda existe
                // (Ex: PP=79.90 PU=79.90 Desc=4.00. Liquido = 75.90 errado!).
                // Heuristica: se tem atributos de promocao Fikon (CodPromo ou NomePromo ou isMotor S),
                // e o liquido (PU - Desc) = X. Comparar X com PP: se X < PP e se a diferença for
                // proxima de VlDesc (ou seja, o desconto realmente estava diminuindo um valor que
                // já era o promocional), zerar VlDesc.
                boolean temMarcaFikon = (item.getCodigoPromo() != null && !item.getCodigoPromo().trim().isEmpty())
                        || (item.getNomePromo() != null && !item.getNomePromo().trim().isEmpty())
                        || (item.getMensagemPromo() != null && !item.getMensagemPromo().trim().isEmpty())
                        || "S".equalsIgnoreCase(item.getIsMotor());
                BigDecimal liqAgora = pp.subtract(desc).setScale(2, RoundingMode.HALF_EVEN);
                if (temMarcaFikon
                        && !("S".equalsIgnoreCase(item.getIsMotor()) || "SIM".equalsIgnoreCase(item.getIsMotor()))
                        && pu.compareTo(pp) == 0
                        && desc.compareTo(BigDecimal.ZERO) > 0) {
                    // PU == PP (base = preco promocional), mas Desc > 0 → aplicando 2x desconto!
                    // Liquido devera ser = PU (o proprio preco promocional), sem desconto.
                    // Ou: se existe VlDescPromo > 0 no item, o desconto esta salvo como atributo
                    // visual, nao precisa em VlDescItem.
                    if (item.getVldescPromo() > 0.0d) {
                        item.setVlDescItem(0.0d);
                    } else {
                        // Fallback: Se VlDescPromo == 0, confiar no menor liquido entre atual
                        // e (PP). Isso garante que nao vamos subtrair 2x.
                        BigDecimal precoPromocionalMinimo = pu.min(liqAgora.add(desc));
                        BigDecimal descMaxPermitido = pp.subtract(precoPromocionalMinimo).max(BigDecimal.ZERO);
                        if (desc.compareTo(descMaxPermitido) > 0) {
                            item.setVlDescItem(descMaxPermitido.setScale(6, RoundingMode.HALF_EVEN).doubleValue());
                        }
                    }
                }

                // Regra 3: Ajustar DPI (percentual) sempre coerente com VlDescItem novo.
                if (pp.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal vlDescNovo = BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem()));
                    double novoDpi = vlDescNovo.multiply(BigDecimal.valueOf(100.0d))
                            .divide(pp, 6, RoundingMode.HALF_EVEN)
                            .doubleValue();
                    item.setDpi(novoDpi);
                }
            }
        }
        }
    }

    private static boolean pedidoTemPrecoPromocionalJaComAcrescimoPrazo(Pedidos pedidoAtual) {
        return false;
    }

    private static double obterFreteCarregadoComparacao(Pedidos pedidoAtual) {
        if (pedidoAtual == null) {
            return 0.0d;
        }

        double freteSalvo = Math.max(0.0d, pedidoAtual.getVlFrete());
        double servicosSalvos = Math.max(0.0d, pedidoAtual.getVlServicosAdicionais());
        return freteSalvo > 0.0d ? freteSalvo : servicosSalvos;
    }

    private static boolean isItemPromocionalPorSituacaoSemMotor(ItemPedidos item) {
        return item != null
                && item.getSituacao() != null
                && "PR".equalsIgnoreCase(item.getSituacao().trim())
                && (item.getIsMotor() == null
                || item.getIsMotor().trim().isEmpty()
                || (!"S".equalsIgnoreCase(item.getIsMotor().trim())
                && !"SIM".equalsIgnoreCase(item.getIsMotor().trim())));
    }

    private static double calcularTotalItensComparacao(Pedidos pedidoAtual, boolean aplicarRegraPromocional) {
        if (pedidoAtual == null || pedidoAtual.getItens() == null) {
            return 0.0d;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidos item : pedidoAtual.getItens()) {
            if (item == null) {
                continue;
            }

            double precoBruto = item.getPrecoUnit();
            if (aplicarRegraPromocional && isItemPromocional(item)) {
                precoBruto = ((item.getPrecoUnit() - item.getVlDescItem()) / 0.95d) + item.getVlDescItem();
            }

            double valorLiquido = (precoBruto - item.getVlDescItem()) * item.getQtcomp();
            total = total.add(BigDecimal.valueOf(valorLiquido));
        }

        return total.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    private static double resolverPrecoPrimarioItem(ItemPedidos item) {
        if (item == null) {
            return 0.0d;
        }

        if ("S".equalsIgnoreCase(item.getIsMotor()) || "SIM".equalsIgnoreCase(item.getIsMotor())) {
            double precoBase = item.getPrecoUnit();
            if (item.getPrecoPrimario() > 0d) {
                precoBase = Math.max(precoBase, item.getPrecoPrimario());
            }
            if (precoBase <= 0d && item.getPrecoPrimario() > 0d) {
                precoBase = item.getPrecoPrimario();
            }
            precoBase = BigDecimal.valueOf(precoBase)
                    .setScale(2, RoundingMode.HALF_EVEN)
                    .doubleValue();
            if (item.getPrecoPrimario() <= 0d) {
                item.setPrecoPrimario(precoBase);
            }
            return precoBase;
        }

        if (item.getPrecoPrimario() > 0d) {
            return BigDecimal.valueOf(item.getPrecoPrimario())
                    .setScale(2, RoundingMode.HALF_EVEN)
                    .doubleValue();
        }

        double precoAtual = item.getPrecoUnit();
        double precoPrimario = BigDecimal.valueOf(precoAtual)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
        item.setPrecoPrimario(precoPrimario);
        return precoPrimario;
    }

    public static double getPrecoPrimarioItemExibicao(ItemPedidos item) {
        return resolverPrecoPrimarioItem(item);
    }

    public static Pedidos clonarPedido(Pedidos pedidoBase) {
        if (pedidoBase == null) {
            return null;
        }

        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
            oos.writeObject(pedidoBase);
            oos.close();

            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
            java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
            return (Pedidos) ois.readObject();
        } catch (Exception e) {
            android.util.Log.e("ClonarPedido", "Erro ao clonar pedido via Serializable", e);
            try {
                return new Gson().fromJson(new Gson().toJson(pedidoBase), Pedidos.class);
            } catch (Exception ignore) {
                return null;
            }
        }
    }

    public static Pedidos criarPedidoBaseAvistaParaFechamento(Pedidos pedidoBase) {
        if (pedidoBase == null) {
            return null;
        }

        Pedidos pedidoClonado = clonarPedido(pedidoBase);
        if (pedidoSalvoOriginal != null && pedidoSalvoOriginal.getVlFrete() > 0) {
            pedidoClonado.setVlFrete(pedidoSalvoOriginal.getVlFrete());
        }
        if (pedidoClonado == null || pedidoClonado.getItens() == null) {
            return pedidoClonado;
        }

        for (ItemPedidos item : pedidoClonado.getItens()) {
            if (item == null) {
                continue;
            }
            item.setPrecoUnit(getPrecoPrimarioItemExibicao(item));
            item.setPrecoPrimario(getPrecoPrimarioItemExibicao(item));
        }

        return pedidoClonado;
    }

    private static double reverterPrecoUnitarioBrutoPromocionalPrazo(double precoBrutoAtual, ItemPedidos item) {
        return BigDecimal.valueOf(precoBrutoAtual)
                .setScale(6, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private void atualizarEstadoBotoesPedido() {
        if (pedido == null || btn_adcionar == null || btn_finalizar == null || usuarioLogado == null) {
            return;
        }

        if (pedido.getNumpedven() > 0 && pedido.getCodStatus() < 5) {
            btn_finalizar.setText("ALTERAR PEDIDO");
        } else if (pedido.getNumpedven() > 0 && pedido.getCodStatus() == 9) {
            btn_finalizar.setText("FINALIZAR PEDIDO");
        }

        boolean podeEditar = pedido.getCodStatus() < 5 || pedido.getCodStatus() == 9;
        boolean usuarioPodeEditar = false;

        try {
            usuarioPodeEditar = pedido.getCodvendr() == Integer.parseInt(usuarioLogado.get("codigo"))
                    || isGerente
                    || "AUXILIAR DE VENDAS CORPORATIVO FX U".equals(usuarioLogado.get("cargo"))
                    || "AUXILIAR DE VENDAS FX U".equals(usuarioLogado.get("cargo"))
                    || "AUXILIAR DE VENDAS MULTICANAIS FX U".equals(usuarioLogado.get("cargo"));
        } catch (Exception ignore) {
        }

        boolean habilitar = podeEditar && usuarioPodeEditar;
        if (bloquearBotoesEdicaoAposFinalizacao) {
            habilitar = false;
        }
        btn_adcionar.setEnabled(habilitar);
        btn_adcionar.setBackgroundResource(habilitar ? R.drawable.backgroudbuttom : R.drawable.backgroundbuttondisabled);
        btn_finalizar.setEnabled(habilitar);
        btn_finalizar.setBackgroundResource(habilitar ? R.drawable.backgroudbuttom : R.drawable.backgroundbuttondisabled);
    }


    public double getPeso(List<ItemPedidos> itens) {
        double peso = 0.00;

        for (ItemPedidos p : itens) {
            peso = peso + (p.getPeso()*p.getQtcomp());

        }
        return peso;
    }


    public double getVlItens(List<ItemPedidos> itens) {
        double valor = 0.00;

        for (ItemPedidos p : itens) {
            valor = valor + (getValorExibicaoPedido(p, p.getPrecoUnit()) * p.getQtcomp());

        }
        return valor;
    }

    public double getPercFretePrevisto(List<ItemPedidos> itens , double vlfreteOrig, double vlfrete) {
        double peso = 0.00;
        double freteCalculado = 0.00;

        for (ItemPedidos p : itens) {
            peso = peso + (p.getPeso()*p.getQtcomp());

        }

        if ((peso / 1000) > 1.0) {
            freteCalculado = Math.floor(vlfreteOrig * (peso / 1000));
        } else {
            freteCalculado = vlfreteOrig;
        }
        return (vlfrete/freteCalculado)*100;
    }



    public double getDesconto(List<ItemPedidos> itens) {
        double desconto = 0.00;

        for (ItemPedidos p : itens) {
            desconto = desconto + (p.getVlDescItem()*p.getQtcomp());

        }
        return desconto;
    }


    public double getDescontoPerc(List<ItemPedidos> itens) {
        double desconto = 0.00;
        double vlbruto= 0.00;

        for (ItemPedidos p : itens) {
            desconto = desconto + (p.getVlDescItem()*p.getQtcomp());

            vlbruto = vlbruto + (getValorExibicaoPedido(p, p.getPrecoUnit()) * p.getQtcomp());

        }
        return (desconto/vlbruto)*100;
    }

    private void GerarPDF(final Pedidos pedido) {


        dialog = new ProgressDialog(Pedidos_LancamentoActivity.this);
        dialog.setMessage("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {


                    final DecimalFormat dfInteiro = new DecimalFormat("0");

                    String[] cabecalho = {"Imagem", "Código Acal", "Produto", "Qtd", "Preço", "Preço Total"};


                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();

                    String cgccpf;
                    String fone;
                    String endereco ="";


                    if (pedido.getVlFrete() > 0) {
                        endereco = ("End. de entrega: " + pedido.getCliente().getEnderecos().get(pedido.getEndent()).getCodlograd()+" "+pedido.getCliente().getEnderecos().get(pedido.getEndent()).getEnder()+", "+pedido.getCliente().getEnderecos().get(pedido.getEndent()).getNumero()+ " - "+pedido.getCliente().getEnderecos().get(pedido.getEndent()).getComplemento()+" "+pedido.getCliente().getEnderecos().get(pedido.getEndent()).getBairro()+" "+pedido.getCliente().getEnderecos().get(pedido.getEndent()).getCidade()+"-"+pedido.getCliente().getEnderecos().get(pedido.getEndent()).getUf()+" CEP: "+pedido.getCliente().getEnderecos().get(pedido.getEndent()).getCep() );
                    } else {
                        endereco = "";
                    }

                    double totalLocalBase = pedido.getVlTotal();

                    OkHttpClient clientVal = new OkHttpClient.Builder().connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS).readTimeout(10, java.util.concurrent.TimeUnit.SECONDS).build();
                    Retrofit retrofitVal = new Retrofit.Builder().baseUrl(ApiGemco.BASE_URL).client(clientVal).addConverterFactory(GsonConverterFactory.create()).build();
                    ApiGemco apiVal = retrofitVal.create(ApiGemco.class);
                    String tokenVal = "Bearer " + ApiGemco.token;

                    double totalServerBase = -1.0d;
                    retrofit2.Response<java.util.List<br.com.acalfortaleza.fikonmais.models.Pedidos>> respPed = apiVal.GetPedidosConsulta(String.valueOf((long)pedido.getNumpedven()), "CODIGO", tokenVal).execute();
                    if (respPed.isSuccessful() && respPed.body() != null && !respPed.body().isEmpty()) {
                        br.com.acalfortaleza.fikonmais.models.Pedidos pedServer = respPed.body().get(0);
                        totalServerBase = pedServer.getVlTotal();
                    }

                    final Pedidos pedidoPdf = pedidoSalvoOriginal != null ? pedidoSalvoOriginal : pedido;

                    if (pedidoPdf.getCodStatus() != 5) {
                        if (totalServerBase >= 0 && Math.abs(totalLocalBase - totalServerBase) > 0.01d) {
                            runOnUiThread(() -> android.widget.Toast.makeText(Pedidos_LancamentoActivity.this, "Pedido foi alterado! Retorne ou Salve o pedido para gerar PDF.", android.widget.Toast.LENGTH_LONG).show());
                            return;
                        }
                    }

                    TemplatePDFPedido templatePdf = new TemplatePDFPedido(Pedidos_LancamentoActivity.this);
                    templatePdf.openDocument(dfInteiro.format(pedidoPdf.getNumpedven()));
                    templatePdf.addMetaData("Pedido", "Acal+", "Acal");
                    boolean layoutFilial150 = templatePdf.isLayoutFilial150();
                    if (layoutFilial150) {
                        preencherMarcaEUnidadePdf(pedidoPdf, apiVal, tokenVal);
                    }

                    if (pedidoPdf.getCliente().getCelular().equals("")) {
                        fone = "Contato:";
                    } else {
                        fone = "Contato: (" + pedidoPdf.getCliente().getDddCelular() + ") " + pedidoPdf.getCliente().getCelular();
                    }

                    if (layoutFilial150) {
                        templatePdf.addCabecalhoFilial150(dfInteiro.format(pedidoPdf.getNumpedven()), dateFormat.format(date));
                    } else {
                        templatePdf.addTitulo("ACAL HOME CENTER - " + pedidoPdf.getFilial(), "Pedido número: " + dfInteiro.format(pedidoPdf.getNumpedven()), dateFormat.format(date),pedidoPdf.getAgendamento());
                    }
                    String[] dadosCliente150;
                    if (pedidoPdf.getCliente().getNatjur().equals("F")) {
                        cgccpf = "Cpf: " + pedidoPdf.getCliente().getCgccpf().substring(0, 3) + "." + pedidoPdf.getCliente().getCgccpf().substring(3, 6) + "." + pedidoPdf.getCliente().getCgccpf().substring(6, 9) + "-" + pedidoPdf.getCliente().getCgccpf().substring(9, 11);
                        dadosCliente150 = new String[]{"Nome: " + pedidoPdf.getCliente().getNomcli(), cgccpf,
                                "E-mail: " + pedidoPdf.getCliente().getEmail(), fone};
                    } else {
                        cgccpf = "Cnpj: " + pedidoPdf.getCliente().getCgccpf().substring(0, 2) + "." + pedidoPdf.getCliente().getCgccpf().substring(2, 5) + "." + pedidoPdf.getCliente().getCgccpf().substring(5, 8) + "/" + pedidoPdf.getCliente().getCgccpf().substring(8, 12) + "-" + pedidoPdf.getCliente().getCgccpf().substring(12, 14);
                        dadosCliente150 = new String[]{"Razão social: " + pedidoPdf.getCliente().getNomcli(),
                                "Fantasia: " + pedidoPdf.getCliente().getFantasia(), cgccpf,
                                "E-mail: " + pedidoPdf.getCliente().getEmail(), fone};
                    }

                    String[] dadosClienteLegado = new String[dadosCliente150.length + (endereco.isEmpty() ? 0 : 1)];
                    System.arraycopy(dadosCliente150, 0, dadosClienteLegado, 0, dadosCliente150.length);
                    if (!endereco.isEmpty()) {
                        dadosClienteLegado[dadosCliente150.length] = endereco;
                    }

                    String[] dadosConsultorLegado = new String[]{
                            "Nome: " + pedidoPdf.getConsultor(),
                            "Email: " + usuarioLogado.get("email"),
                            "Fone/WhatsApp: " + usuarioLogado.get("celular")
                    };

                    int lanc = 0;
                    String forma="";
                    String financeiro ="";

                    for(int i = 0 ; i < pedidoPdf.getFinanceiro().size(); i++ ){
                        if(  lanc < pedidoPdf.getFinanceiro().get(i).getNumlan()){
                            lanc = pedidoPdf.getFinanceiro().get(i).getNumlan();

                            switch (pedidoPdf.getFinanceiro().get(i).getForma()){
                                case 1:
                                    forma = pedidoPdf.getFinanceiro().get(i).getEvento() + " " +real.format(pedidoPdf.getFinanceiro().get(i).getValor());
                                    break;
                                case 5:
                                    forma = pedidoPdf.getFinanceiro().get(i).getEvento() + " " +real.format(pedidoPdf.getFinanceiro().get(i).getValor());
                                    break;
                                case 29:
                                    forma = pedidoPdf.getFinanceiro().get(i).getEvento() + " " +real.format(pedidoPdf.getFinanceiro().get(i).getValor());
                                    break;
                                default:
                                    forma = pedidoPdf.getFinanceiro().get(i).getEvento()+" em "+pedidoPdf.getFinanceiro().get(i).getQtprc()+"X de " + real.format(pedidoPdf.getFinanceiro().get(i).getValor())+" s/juros";
                                    break;

                            }

                            if(financeiro.equals("")){

                                financeiro= forma;
                            }else{
                                financeiro = financeiro+" / " +forma;
                            }

                        }
                    }

                    String[] financeiroFilial150 = new String[0];
                    String[] condicoesFilial150 = new String[0];
                    String[] totaisLegado = new String[0];
                    try {
                        if(pedidoPdf.getDtpedido() == null){
                            Date dateval = new Date();
                        }else{
                            Date dateval = entrada.parse(pedidoPdf.getDtpedido());
                        }
                        double freteServ = Math.max(0.0, pedidoPdf.getVlServicosAdicionais());
                        double freteSalvo = Math.max(0.0, pedidoPdf.getVlFrete());

                        double valorItensSomente = getValorItensLiquidoPdf(pedidoPdf).doubleValue();
                        double valorDescontoPdf = getValorDescontoPdf(pedidoPdf).doubleValue();
                        double valorItensCabecalho = getValorItensCabecalhoPdf(pedidoPdf).doubleValue();

                        double freteTotalPdf;
                        if (freteServ > 0.0d && freteSalvo > 0.0d) {
                            double totalRef = Math.max(0.0, pedidoPdf.getVlTotal());
                            double t1 = Math.max(0.0d, valorItensSomente + freteSalvo);
                            double t2 = Math.max(0.0d, valorItensSomente + freteSalvo + freteServ);
                            boolean freteCabecalhoJaIncluiServico = totalRef > 0.0d && Math.abs(totalRef - t1) <= Math.abs(totalRef - t2);
                            freteTotalPdf = freteCabecalhoJaIncluiServico ? freteSalvo : (freteSalvo + freteServ);
                        } else {
                            freteTotalPdf = freteSalvo > 0.0d ? freteSalvo : freteServ;
                        }

// TOTAL COM FRETE = ITENS LÍQUIDO + FRETE TOTAL
                        double totalPedidoComFretePdf = BigDecimal.valueOf(
                                        Math.max(0.0d, valorItensSomente + freteTotalPdf)
                                )
                                .setScale(2, RoundingMode.DOWN)
                                .doubleValue();
                        boolean entrega = temEntregaPedido(pedidoPdf, freteTotalPdf);

                        android.util.Log.d(
                                "PDF_TOTAL_DEBUG",
                                "pedido=" + (long) pedidoPdf.getNumpedven()
                                        + " | valorItensSomentePdf=" + valorItensSomente
                                        + " | valorDescontoPdf=" + valorDescontoPdf
                                        + " | freteBase=" + freteSalvo
                                        + " | freteServico=" + freteServ
                                        + " | freteTotalPdf=" + freteTotalPdf
                                        + " | totalPedidoComFretePdf=" + totalPedidoComFretePdf
                        );

                        financeiroFilial150 = new String[]{
                                "Valor dos itens: " + real.format(valorItensCabecalho),
                                "Valor do desconto: " + real.format(valorDescontoPdf),
                                "Valor total dos itens: " + real.format(valorItensSomente),
                                "Tipo de frete: " + (entrega ? "CIF" : "FOB"),
                                "Valor do frete: " + (entrega ? real.format(freteTotalPdf) : "N/A"),
                                "Valor total com frete: " + real.format(totalPedidoComFretePdf)
                        };
                        condicoesFilial150 = new String[]{
                                "Forma de pagamento: " + financeiro,
                                pedidoPdf.getCondPagamento() == null ? "" : "Condição de pagamento: " + pedidoPdf.getCondPagamento().getDescricao(),
                                "Peso total: " + df.format(pedidoPdf.getPeso()) + "kg",
                                pedidoPdf.getAgendamento() == null ? "" : "Agendamento: " + pedidoPdf.getAgendamento()
                        };
                        if (!layoutFilial150) {
                            totaisLegado = new String[]{
                                    "Valor dos itens: " + real.format(valorItensCabecalho),
                                    "Valor do desconto: " + real.format(valorDescontoPdf),
                                    "Valor total dos itens: " + real.format(valorItensSomente),
                                    "Valor do frete: " + real.format(freteTotalPdf),
                                    "Valor total com frete: " + real.format(totalPedidoComFretePdf),
                                    "Peso total: " + df.format(pedidoPdf.getPeso()) + "kg",
                                    "Forma de pagamento: " + financeiro,
                                    pedidoPdf.getCondPagamento() == null ? "" : "Condição de pagamento: " + pedidoPdf.getCondPagamento().getDescricao(),
                                    pedidoPdf.getAgendamento() == null ? "" : "Agendamento: " + pedidoPdf.getAgendamento()
                            };
                        }
                        alteracaoFrete = false;

                    } catch (Exception e) {

                    }


                    if (layoutFilial150) {
                        templatePdf.addResumoFilial150(
                                dadosCliente150,
                                new String[]{"Nome: Pedro Alves", "E-mail: pedroalves@acalhomecenter.com.br", "Fone/WhatsApp: (85) 98899-8470"},
                                new String[]{"Nome: " + pedidoPdf.getConsultor(), "E-mail: " + usuarioLogado.get("email"), "Fone/WhatsApp: " + usuarioLogado.get("celular")},
                                endereco.isEmpty() ? null : new String[]{endereco},
                                financeiroFilial150,
                                condicoesFilial150
                        );
                        templatePdf.createTableFilial150(
                                getCabecalhoItensFilial150(pedidoPdf),
                                getItensComPrecoFilial150(pedidoPdf)
                        );
                        templatePdf.addTermoFilial150();
                    } else {
                        templatePdf.addResumoLegado(dadosClienteLegado, dadosConsultorLegado, totaisLegado);
                        templatePdf.createTableComPreco(cabecalho, getItensComPreco(pedidoPdf));
                        templatePdf.addTermo();
                    }
                    templatePdf.closeDocument();
                    templatePdf.viewPdf( Pedidos_LancamentoActivity.this);


                } catch (Exception e) {

                } finally {
                    dialog.dismiss();
                }

            }
        }.start();

    }




    private boolean temEntregaPedido(Pedidos pedido, double valorFrete) {
        if (pedido == null) {
            return false;
        }
        if (pedido.getVlFreteSelecionado() > 0.0d || valorFrete > 0.0d) {
            return true;
        }
        if (pedido.getItens() == null) {
            return false;
        }
        for (ItemPedidos item : pedido.getItens()) {
            if (item != null && "E".equalsIgnoreCase(item.getTpped())) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<String[]> getItensSemPreco(Pedidos pedido) {

        ArrayList<String[]> rows = new ArrayList<>();

        for (ItemPedidos item : pedido.getItens()) {
            rows.add(new String[]{item.getImagem(), String.valueOf(item.getCoditprod()), item.getDescricao(), df.format(item.getQtcomp())});
        }

        return rows;

    }


    private ArrayList<String[]> getItensComPreco(Pedidos pedido) {

        ArrayList<String[]> rows = new ArrayList<>();

        for (ItemPedidos item : pedido.getItens()) {
            rows.add(new String[]{
                    item.getImagem(),
                    String.valueOf(item.getCoditprod()),
                    item.getDescricao(),
                    df.format(item.getQtcomp()),
                    real.format(getValorLiquidoUnitarioPdf(item).doubleValue()),
                    real.format(getValorLiquidoTotalPdf(item).doubleValue())
            });
        }

        return rows;

    }

    private ArrayList<String[]> getItensComPrecoFilial150(Pedidos pedido) {
        ArrayList<String[]> rows = new ArrayList<>();
        boolean incluirMarca = temMarcaDisponivel(pedido);
        boolean incluirUnidade = temUnidadeDisponivel(pedido);

        for (ItemPedidos item : pedido.getItens()) {
            ArrayList<String> row = new ArrayList<>();
            row.add(item.getImagem());
            row.add(String.valueOf(item.getCoditprod()));
            row.add(item.getDescricao());
            if (incluirMarca) {
                row.add(item.getFornecedor());
            }
            if (incluirUnidade) {
                row.add(item.getUnidade());
            }
            row.add(df.format(item.getQtcomp()));
            row.add(real.format(getValorLiquidoUnitarioPdf(item).doubleValue()));
            row.add(real.format(getValorLiquidoTotalPdf(item).doubleValue()));
            rows.add(row.toArray(new String[0]));
        }

        return rows;
    }

    private String[] getCabecalhoItensFilial150(Pedidos pedido) {
        ArrayList<String> headers = new ArrayList<>();
        headers.add("Imagem");
        headers.add("Código Acal");
        headers.add("Produto");
        if (temMarcaDisponivel(pedido)) {
            headers.add("Marca");
        }
        if (temUnidadeDisponivel(pedido)) {
            headers.add("Unidade");
        }
        headers.add("Qtd");
        headers.add("Preço Unit.");
        headers.add("Preço Total");
        return headers.toArray(new String[0]);
    }

    private boolean temMarcaDisponivel(Pedidos pedido) {
        if (pedido == null || pedido.getItens() == null) {
            return false;
        }
        for (ItemPedidos item : pedido.getItens()) {
            if (item != null && item.getFornecedor() != null && !item.getFornecedor().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean temUnidadeDisponivel(Pedidos pedido) {
        if (pedido == null || pedido.getItens() == null) {
            return false;
        }
        for (ItemPedidos item : pedido.getItens()) {
            if (item != null && item.getUnidade() != null && !item.getUnidade().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void preencherMarcaEUnidadePdf(Pedidos pedido, ApiGemco api, String authorization) {
        if (pedido == null || pedido.getItens() == null) {
            return;
        }

        HashMap<Integer, ProdutoGemco> produtosConsultados = new HashMap<>();
        HashMap<Integer, Boolean> codigosConsultados = new HashMap<>();
        for (ItemPedidos item : pedido.getItens()) {
            if (item == null || item.getCoditprod() <= 0) {
                continue;
            }
            boolean precisaFornecedor = item.getFornecedor() == null || item.getFornecedor().trim().isEmpty();
            boolean precisaUnidade = item.getUnidade() == null || item.getUnidade().trim().isEmpty();
            if (!precisaFornecedor && !precisaUnidade) {
                continue;
            }

            int codigoProduto = item.getCoditprod();
            if (!codigosConsultados.containsKey(codigoProduto)) {
                codigosConsultados.put(codigoProduto, true);
                try {
                    retrofit2.Response<List<ProdutoGemco>> response = api.listProdutoGemco(
                            "CODIGO",
                            String.valueOf(codigoProduto),
                            String.valueOf(pedido.getCodfil()),
                            authorization
                    ).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        for (ProdutoGemco produto : response.body()) {
                            if (produto != null && produto.Codigo == codigoProduto) {
                                produtosConsultados.put(codigoProduto, produto);
                                break;
                            }
                        }
                        if (!produtosConsultados.containsKey(codigoProduto)) {
                            Log.w("PDF_PEDIDO_METADATA", "Produto não encontrado no catálogo para completar marca/unidade. Código: " + codigoProduto);
                        }
                    } else {
                        Log.w("PDF_PEDIDO_METADATA", "Falha ao consultar marca/unidade do produto " + codigoProduto
                                + ". HTTP " + response.code());
                    }
                } catch (IOException e) {
                    Log.e("PDF_PEDIDO_METADATA", "Erro ao consultar marca/unidade do produto " + codigoProduto, e);
                }
            }

            ProdutoGemco produto = produtosConsultados.get(codigoProduto);
            if (produto != null) {
                if (precisaFornecedor) {
                    item.setFornecedor(produto.Fornecedor);
                }
                if (precisaUnidade) {
                    item.setUnidade(produto.Unidade);
                }
            }
        }
    }

    private BigDecimal getValorLiquidoUnitarioPdfPreciso(ItemPedidos item) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_EVEN);
        }

        BigDecimal precoLiquido = BigDecimal.valueOf(item.getPrecoUnit())
                .subtract(BigDecimal.valueOf(item.getVlDescItem()));

        return precoLiquido.setScale(6, RoundingMode.HALF_EVEN);
    }

    private BigDecimal getValorLiquidoUnitarioPdf(ItemPedidos item) {
        return getValorLiquidoUnitarioPdfPreciso(item)
                .setScale(2, RoundingMode.FLOOR);
    }

    private BigDecimal getValorLiquidoTotalPdf(ItemPedidos item) {
        return getValorLiquidoLinhaExibicao(item);
    }

    private BigDecimal getValorItensLiquidoPdf(Pedidos pedidoRef) {
        BigDecimal valorItens = BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return valorItens;
        }

        for (ItemPedidos item : pedidoRef.getItens()) {
            valorItens = valorItens.add(getValorLiquidoTotalPdf(item));
        }

        return valorItens.setScale(2, RoundingMode.DOWN);
    }

    private BigDecimal getValorItensBrutoPadraoPdf(Pedidos pedidoRef) {
        BigDecimal valorItens = BigDecimal.ZERO.setScale(2, RoundingMode.FLOOR);
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return valorItens;
        }

        for (ItemPedidos item : pedidoRef.getItens()) {
            valorItens = valorItens.add(
                    BigDecimal.valueOf(getPrecoPrimarioItemExibicao(item))
                            .multiply(BigDecimal.valueOf(item.getQtcomp()))
                            .setScale(2, RoundingMode.FLOOR)
            );
        }

        return valorItens.setScale(2, RoundingMode.FLOOR);
    }

    private BigDecimal getValorDescontoPdf(Pedidos pedidoRef) {
        BigDecimal valorDesconto = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return valorDesconto;
        }

        for (ItemPedidos item : pedidoRef.getItens()) {
            valorDesconto = valorDesconto.add(getValorDescontoLinhaExibicao(item)).setScale(2, RoundingMode.HALF_UP);
        }

        if (valorDesconto.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return valorDesconto.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getValorItensCabecalhoPdf(Pedidos pedidoRef) {
        return getValorItensBrutoPadraoPdf(pedidoRef).setScale(2, RoundingMode.DOWN);
    }

    private static BigDecimal getValorDescontoResumoPedido(Pedidos pedidoRef) {
        BigDecimal valorDesconto = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return valorDesconto;
        }

        for (ItemPedidos item : pedidoRef.getItens()) {
            valorDesconto = valorDesconto.add(getValorDescontoLinhaExibicao(item)).setScale(2, RoundingMode.HALF_UP);
        }

        return valorDesconto.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal getValorDescontoLinhaExibicao(ItemPedidos item) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        // Para pedido salvo, o desconto persistido da linha deve seguir a mesma
        // regra do backend: desconto unitario * quantidade, arredondado so no total.
        BigDecimal valorDescontoLinha = BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem()))
                .multiply(BigDecimal.valueOf(Math.max(0.0d, item.getQtcomp())))
                .setScale(2, RoundingMode.HALF_UP);

        if (valorDescontoLinha.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return valorDescontoLinha;
    }

    private static BigDecimal getValorBrutoLinhaExibicao(ItemPedidos item) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);
        }

        return BigDecimal.valueOf(getPrecoPrimarioItemExibicao(item))
                .multiply(BigDecimal.valueOf(item.getQtcomp()))
                .setScale(2, RoundingMode.DOWN);
    }

    private static BigDecimal getValorLiquidoLinhaExibicao(ItemPedidos item) {
        BigDecimal valorBrutoLinha = getValorBrutoLinhaExibicao(item);
        BigDecimal valorDescontoLinha = getValorDescontoLinhaExibicao(item);
        BigDecimal valorLiquidoLinha = valorBrutoLinha.subtract(valorDescontoLinha).setScale(2, RoundingMode.DOWN);
        if (valorLiquidoLinha.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.DOWN);
        }
        return valorLiquidoLinha;
    }

    private static double getValorExibicaoPedido(ItemPedidos item, double valorBase) {
        if (item == null) {
            return valorBase;
        }

        if ("S".equalsIgnoreCase(item.getIsMotor()) || "SIM".equalsIgnoreCase(item.getIsMotor())) {
            return BigDecimal.valueOf(getPrecoPrimarioItemExibicao(item))
                    .setScale(2, RoundingMode.HALF_EVEN)
                    .doubleValue();
        }

        return valorBase;
    }

    public static boolean isPedidoAVista() {
        if (pedido == null || pedido.getCondPagamento() == null) {
            return false;
        }
        String condicaoDescricao = pedido.getCondPagamento().getDescricao();
        String condicaoCodigo = pedido.getCondPagamento().getCondpgto();

        if (condicaoDescricao != null && condicaoDescricao.toUpperCase().contains("A PRAZO")) {
            return false;
        }
        if (condicaoDescricao != null && (condicaoDescricao.toUpperCase().contains("A VISTA") || condicaoDescricao.toUpperCase().contains("AVISTA"))) {
            return true;
        }
        return "01".equals(condicaoCodigo);
    }

    public static boolean deveAplicarPromocaoPrazoNaExibicao(ItemPedidos item) {
        return false;
    }

    private static boolean isItemNovoLocalEmPedidoExistente(ItemPedidos item) {
        return pedido != null
                && pedido.getNumpedven() > 0
                && item != null
                && item.getNumpedven() <= 0;
    }

    private static boolean isItemPromocional(ItemPedidos item) {
        if (item == null) {
            return false;
        }
        return isItemPromocionalPorSituacao(item) || temDescontoMotorPromocional(item);
    }

    private static boolean isItemPromocionalPorSituacao(ItemPedidos item) {
        if (item == null) return false;
        return item.getSituacao() != null && "PR".equalsIgnoreCase(item.getSituacao().trim());
    }

    private static boolean temDescontoMotorPromocional(ItemPedidos item) {
        if (item == null) return false;
        return item.getIsMotor() != null
                && ("S".equalsIgnoreCase(item.getIsMotor().trim()) || "SIM".equalsIgnoreCase(item.getIsMotor().trim()))
                && (item.getVlDescItem() > 0
                || item.getVldescPromo() > 0
                || possuiTexto(item.getCodigoPromo())
                || possuiTexto(item.getNomePromo()));
    }

    private static boolean isPedidoAPrazo() {
        try {
            if (pedido != null) {
                if (pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
                    Financeiro financeiro = pedido.getFinanceiro().get(0);
                    if (financeiro != null) {
                        if ("87".equals(financeiro.getCondpgto())
                                && (financeiro.getForma() == 142 || financeiro.getCodEve() == 142)) {
                            return false;
                        }
                        if (financeiro.getCondicao() != null && financeiro.getCondicao().toUpperCase().contains("A PRAZO")) {
                            return true;
                        }
                        if (financeiro.getCondpgto() != null && !financeiro.getCondpgto().trim().isEmpty()) {
                            return !"01".equals(financeiro.getCondpgto().trim());
                        }
                    }
                }

                if (pedido.getCondPagamento() != null && pedido.getCondPagamento().getDescricao() != null) {
                    return pedido.getCondPagamento().getDescricao().toUpperCase().contains("A PRAZO");
                }
            }
        } catch (Exception ignore) {
        }

        return false;
    }

    private static boolean possuiTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    @Override
    public void onButtonClick(int id) {

        switch (id) {
            case R.id.mod_item_editar:
                Intent intent = new Intent(Pedidos_LancamentoActivity.this, PopupItemPed.class);
                intent.putExtra("item", itemPedidos);
                intent.putExtra("position", index);
                startActivity(intent);
                break;

            case R.id.mod_item_editarTodos:
                Intent todos = new Intent(Pedidos_LancamentoActivity.this, PopupItemPedTodos.class);
                todos.putExtra("pedido", pedido);
                todos.putExtra("item", itemPedidos);
                startActivity(todos);
                break;

            case R.id.mod_item_pesquisa:

                String resultado = "";
                NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

                DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");


                Date date = null;
                if(itemPedidos.getPrecoConc() != null && itemPedidos.getPrecoConc().size()>0 && usuarioLogado.get("isgerente").equals("SIM")){



                    for (PrecoConc p : itemPedidos.getPrecoConc()) {





                        try {
                            date = entrada.parse(p.DataPesquisa);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

                        resultado = resultado + "<b>" + "Concorrente: "+ "</b>" + p.Concorrente + " <br />" +
                                "<b>" + "Preço: "+ "</b>" + real.format(p.Preco )+ "" + " <br />" +
                                "<b>" + "Data: "+ "</b>"  + formato.format(date) + "" + " <br />" +
                                "<br />";



                    }






                    AlertDialog.Builder alerta = new AlertDialog.Builder(Pedidos_LancamentoActivity.this);
                    alerta.setIcon(R.drawable.ic_nav_cnpj);
                    alerta.setCancelable(false);
                    alerta.setTitle("Preço dos Concorrentes");
                    alerta.setMessage(Html.fromHtml(resultado));
                    alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    alertaDialog = alerta.create();
                    alertaDialog.show();

                }else{
                    Toast.makeText(Pedidos_LancamentoActivity.this, "Não existe pesquisa de preço para esse produto!", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.mod_item_estoque:
                Intent i = new Intent(Pedidos_LancamentoActivity.this, ConsultaEstoqueActivity.class);
                i.putExtra("codigo",String.valueOf(itemPedidos.getCoditprod()));
                startActivity(i);
                break;

            case R.id.mod_item_excluir:
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Pedidos_LancamentoActivity.this);

                builder.setTitle("Atenção :|");
                builder.setMessage("Deseja retirar esse item do pedido?");
                builder
                        .setCancelable(true)
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                adapter.removeListItem(index);



                            }

                        });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                    }
                });


                builder.create().show();
                break;

        }


    }


    public String validarAgendamento(List<ItemPedidos> itens) {
        String retorno = "N";

        for (ItemPedidos p : itens) {
            if(p.getCodFilOrig() == 5 && p.getTpped().equals("P") && Integer.parseInt(p.getStatus()) < 6){
                retorno="S";
            }


        }
        return retorno;
    }


    public void validarStatus(String numpedven){

        dialog = new ProgressDialog(this);
        dialog.setMessage("Verificando...");
        dialog.setCancelable(false);
        dialog.show();new Thread() {

            public void run() {
                try {
                    statusPedido = helper.GetStatusPedido(numpedven);

                    if (statusPedido != null && statusPedido.Utilizado != null && statusPedido.Utilizado.equals("S") && statusPedido.Status<5) {
                        runOnUiThread(() -> Toast.makeText(Pedidos_LancamentoActivity.this, "Pedido esta sendo utilizado por outro modulo! ", Toast.LENGTH_SHORT).show());
                        retornoStatus = true;
                    } else if (statusPedido != null && statusPedido.Utilizado != null && statusPedido.Status>=5 && statusPedido.Status < 9 && (statusPedido.Utilizado.equals("N") || statusPedido.Utilizado.equals("S")) ) {
                        runOnUiThread(() -> Toast.makeText(Pedidos_LancamentoActivity.this, "Pedido já passou pelo caixa, verificar o status! ", Toast.LENGTH_SHORT).show());
                        retornoStatus = true;
                    }else if (statusPedido != null && statusPedido.Utilizado != null && statusPedido.Utilizado.equals("N") && statusPedido.Status<5) {
                        retornoStatus = false;
                    }

                } catch (UnknownHostException e) {
                    runOnUiThread(() -> Toast.makeText(Pedidos_LancamentoActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show());
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    runOnUiThread(() -> Toast.makeText(Pedidos_LancamentoActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show());
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();

                        if(retornoStatus == false){
                            for (int i = 0; i < pedido.getItens().size(); i++) {

                                if (pedido.getItens().get(i).getTpped().equals("E")) {
                                    temEntrega = "S";
                                }

                            }

                            if (temEntrega.equals("")) {
                                pedido.setVlFreteSelecionado(0);
                            }

                            if (temEntrega.equals("S") && pedido.getVlFreteSelecionado() > 0) {

                                if (pedido.getVlFreteSelecionado() > 0 && (pedido.getPesoEntrega() / 1000) > 1.0) {

                                    pedido.setVlFreteCalculado(Math.floor(pedido.getVlFreteSelecionado() * (pedido.getPesoEntrega() / 1000)));
                                } else {
                                    pedido.setVlFreteCalculado(0.00d);
                                }
                                pedido.setUrlMotor(usuarioLogado.get("urlmotor"));
                                // Intent intent = new Intent(Pedidos_LancamentoActivity.this, PopupCodigoMotor.class);
                                // intent.putExtra("pedido", pedido);
                                // startActivity(intent);
                                abrirPopupVerificaPromoFikon();
                                temEntrega = "";
                            } else if (temEntrega.equals("") && pedido.getVlFreteSelecionado() == 0) {
                                pedido.setUrlMotor(usuarioLogado.get("urlmotor"));
                                // Intent intent = new Intent(Pedidos_LancamentoActivity.this, PopupCodigoMotor.class);
                                // intent.putExtra("pedido", pedido);
                                // startActivity(intent);
                                abrirPopupVerificaPromoFikon();
                            } else {
                                Toast.makeText(Pedidos_LancamentoActivity.this, "Selecione o valor de frete por tonelada!", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }


                }
            }

        }.start();
    }


    private void abrirPopupVerificaPromoFikon() {
        Pedidos pedidoParaPromocao = clonarPedido(pedido);
        Intent intent = new Intent(Pedidos_LancamentoActivity.this, PopupVerificaPromoFikon.class);
        intent.putExtra("pedido", pedidoParaPromocao);
        startActivityForResult(intent, REQUEST_VERIFICA_PROMO_FIKON);
    }

    private void abrirPopupInserirCupomPromocao() {
        Intent intent = new Intent(Pedidos_LancamentoActivity.this, PopupInserirCupomPromocao.class);
        intent.putExtra("pedido", clonarPedido(pedido));
        startActivityForResult(intent, REQUEST_INSERIR_CUPOM_PROMOCAO);
    }

    private void abrirPopupFechamentoPedido(String codigoCupom, int identificadorReserva,
                                            Pedidos pedidoParaFechamento) {
        Pedidos pedidoBaseAvista = criarPedidoBaseAvistaParaFechamento(pedidoParaFechamento);
        Pedidos pedidoAPrazo = clonarPedido(pedidoParaFechamento);
        if (pedidoSalvoOriginal != null && pedidoSalvoOriginal.getVlFrete() > 0) {
            pedidoAPrazo.setVlFrete(pedidoSalvoOriginal.getVlFrete());
        }
        // #region debug-point B:abrir-popup-fechamento
        reportDebugEvent("B", "Pedidos_LancamentoActivity.abrirPopupFechamentoPedido",
                "[DEBUG] montando objetos enviados para o PopupPedFechamento",
                buildFechamentoDebugSnapshot("abrirPopupFechamentoPedido", pedido, pedidoBaseAvista, pedidoAPrazo));
        // #endregion
        Intent intent = new Intent(Pedidos_LancamentoActivity.this, PopupPedFechamento.class);
        intent.putExtra("pedido", pedidoAPrazo);
        intent.putExtra("pedidoBaseAvista", pedidoBaseAvista);
        intent.putExtra("pedidoAPrazo", pedidoAPrazo);
        intent.putExtra("pedidoAlteradoLocalmente", pedidoAlteradoLocalmente);
        intent.putExtra("frete_base_ja_normalizado", false); // O popup sempre deduz o serviço do frete total da API (ex: 140 - 40 = 100)
        if (codigoCupom != null && !codigoCupom.isEmpty()) {
            intent.putExtra(PopupInserirCupomPromocao.EXTRA_CUPOM_CODIGO, codigoCupom);
            intent.putExtra(PopupInserirCupomPromocao.EXTRA_CUPOM_ID_RESERVA, identificadorReserva);
        }
        startActivityForResult(intent, REQUEST_FECHAMENTO_PEDIDO);
    }

    private void aplicarPedidoAtualizadoPosPromocao(Pedidos pedidoAtualizado) {
        if (pedidoAtualizado == null) {
            return;
        }

        pedido = pedidoAtualizado;
        pedidoSalvoOriginal = clonarPedido(pedido);
        aplicarPedidoAtualNaTelaSeNecessario(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VERIFICA_PROMO_FIKON) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Serializable pedidoAtualizado = data.getSerializableExtra(PopupVerificaPromoFikon.EXTRA_PEDIDO_RESULTADO);
                if (pedidoAtualizado instanceof Pedidos) {
                    aplicarPedidoAtualizadoPosPromocao((Pedidos) pedidoAtualizado);
                }
            }
            abrirPopupInserirCupomPromocao();
            return;
        }

        if (requestCode == REQUEST_INSERIR_CUPOM_PROMOCAO) {
            String codigoCupom = null;
            int identificadorReserva = 0;
            Pedidos pedidoParaFechamento = pedido;
            if (resultCode == Activity.RESULT_OK && data != null) {
                Serializable pedidoAtualizado = data.getSerializableExtra(PopupInserirCupomPromocao.EXTRA_PEDIDO_RESULTADO);
                if (pedidoAtualizado instanceof Pedidos) {
                    pedidoParaFechamento = (Pedidos) pedidoAtualizado;
                }
                codigoCupom = data.getStringExtra(PopupInserirCupomPromocao.EXTRA_CUPOM_CODIGO);
                identificadorReserva = data.getIntExtra(PopupInserirCupomPromocao.EXTRA_CUPOM_ID_RESERVA, 0);
            }
            abrirPopupFechamentoPedido(codigoCupom, identificadorReserva, pedidoParaFechamento);
            return;
        }

        if (requestCode == REQUEST_FECHAMENTO_PEDIDO && resultCode == Activity.RESULT_OK && data != null) {
            Serializable pedidoAtualizado = data.getSerializableExtra(PopupPedFechamento.EXTRA_PEDIDO_RESULTADO);
            Pedidos pedidoRetorno = pedidoAtualizado instanceof Pedidos ? (Pedidos) pedidoAtualizado : pedido;
            long numeroPedido = pedidoRetorno != null ? (long) pedidoRetorno.getNumpedven() : 0L;

            Intent intent = new Intent(Pedidos_LancamentoActivity.this, Pedidos_LancamentoActivity.class);
            intent.putExtra("pedido", pedidoRetorno);
            intent.putExtra("alteracao", alteracao);
            intent.putExtra(EXTRA_RECARREGAR_PEDIDO_APOS_FINALIZACAO, true);
            intent.putExtra(EXTRA_BLOQUEAR_BOTOES_APOS_FINALIZACAO, true);
            intent.putExtra(EXTRA_NUMPEDVEN_RECARREGAR, numeroPedido);
            startActivity(intent);
            finish();
            return;
        }
    }

    private void iniciarRecarregamentoPedidoAposFinalizacaoSeNecessario() {
        if (!getIntent().getBooleanExtra(EXTRA_RECARREGAR_PEDIDO_APOS_FINALIZACAO, false)) {
            return;
        }

        long numeroPedido = getIntent().getLongExtra(EXTRA_NUMPEDVEN_RECARREGAR, 0L);
        if (numeroPedido <= 0 && pedido != null) {
            numeroPedido = (long) pedido.getNumpedven();
        }

        if (numeroPedido <= 0) {
            return;
        }

        final long numeroPedidoFinal = numeroPedido;
        final ProgressDialog progressDialog = new ProgressDialog(Pedidos_LancamentoActivity.this);
        progressDialog.setMessage("Atualizando pedido finalizado...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(() -> {
            Pedidos pedidoApi = null;

            try {
                OkHttpClient clientVal = new OkHttpClient.Builder()
                        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                        .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                        .build();
                Retrofit retrofitVal = new Retrofit.Builder()
                        .baseUrl(ApiGemco.BASE_URL)
                        .client(clientVal)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiGemco apiVal = retrofitVal.create(ApiGemco.class);
                String tokenVal = "Bearer " + ApiGemco.token;

                retrofit2.Response<java.util.List<br.com.acalfortaleza.fikonmais.models.Pedidos>> respPed =
                        apiVal.GetPedidosConsulta(String.valueOf(numeroPedidoFinal), "CODIGO", tokenVal).execute();

                if (respPed.isSuccessful() && respPed.body() != null && !respPed.body().isEmpty()) {
                    pedidoApi = respPed.body().get(0);
                }
            } catch (Exception ignore) {
            }

            final Pedidos pedidoApiFinal = pedidoApi;
            runOnUiThread(() -> {
                try {
                    if (pedidoApiFinal != null) {
                        pedidoAlteradoLocalmente = false;
                        servicosAdicionaisAlterados = false;
                        servicosAdicionaisLastModified = 0;
                        servMemPed = 0.0d;
                        idsServMemPed = new ArrayList<>();
                        prepararPreservacaoTotalCarregado(pedidoApiFinal);
                        pedido = pedidoApiFinal;
                        pedidoSalvoOriginal = clonarPedido(pedido);
                    }
                    aplicarPedidoAtualNaTelaSeNecessario(true);
                    atualizarEstadoBotoesPedido();
                } catch (Exception ignore) {
                } finally {
                    progressDialog.dismiss();
                }
            });
        }).start();
    }


    public void ConsultarMetasVendaPerfeita(){

        try{

            ProgressDialog progressDialog = new ProgressDialog(Pedidos_LancamentoActivity.this);
            progressDialog.setMessage("Verificando Venda Perfeita");
            progressDialog.setCancelable(false);
            progressDialog.show();

            CountDownLatch countDown = new CountDownLatch(1);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    if(helper == null){
                        helper = new PedidosHelper();
                    }

                    if(pedido.getMetasVendaPerfeita() == null && pedido.getCliente()!=null) {
                        pedido.setMetasVendaPerfeita( helper.ObterMetasVendaPerfeita(pedido.getCodvendr(), pedido.getCodfil(), pedido.getCliente().getNatjur(), pedido.getDtpedido()) );
                    }

                    countDown.countDown();
                    progressDialog.dismiss();

                }

            }).start();

            countDown.await();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public static void AtualizarLayoutVendaPerfeita(){

        try{

            PedidosHelper.AtualizarLayoutVendaPerfeita(
                    pedido.getIndicadoresVendaPerfeita(), pedido.getMetasVendaPerfeita(),
                    VendaPerfeitaConversao, VendaPerfeitaSKU, VendaPerfeitaTicket, VendaPerfeitaDesconto, VendaPerfeitaEntrega
            );

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void registrarDiagnosticoStatusPedidoAoEntrar() {
        if (pedido == null) {
            Log.e("PEDIDO_STATUS_DEBUG", "Pedido_LancamentoActivity aberta sem objeto de pedido.");
            return;
        }

        List<String> motivos = levantarMotivosProvaveisStatusPedido();
        StringBuilder sb = new StringBuilder();
        sb.append("\n================ PEDIDO_STATUS_DEBUG ================\n");
        sb.append("Pedido: ").append(dfInteiro.format(pedido.getNumpedven())).append("\n");
        sb.append("Cliente: ").append(pedido.getCliente() != null ? pedido.getCliente().getCodcli() + " - " + pedido.getCliente().getNomcli() : "NAO INFORMADO").append("\n");
        sb.append("CodStatus: ").append(pedido.getCodStatus()).append(" - ").append(descreverStatusPedido(pedido.getCodStatus())).append("\n");
        sb.append("StatusCredito: ").append(valorOuPadrao(pedido.getStatusCredito())).append("\n");
        sb.append("VlDesconto: ").append(real.format(pedido.getVlDesconto())).append("\n");
        sb.append("MargemRent: ").append(df.format(pedido.getPmargRent())).append("%\n");
        sb.append("Pedido IsMotor: ").append(valorOuPadrao(pedido.getIsMotor())).append("\n");
        sb.append("Financeiros: ").append(pedido.getFinanceiro() != null ? pedido.getFinanceiro().size() : 0).append("\n");
        if (pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
            Financeiro financeiroAtual = pedido.getFinanceiro().get(0);
            boolean tratarComoAvistaPix = "87".equals(financeiroAtual.getCondpgto())
                    && (financeiroAtual.getForma() == 142 || financeiroAtual.getCodEve() == 142);
            sb.append("Condicao/Forma atual: ")
                    .append("condpgto=").append(valorOuPadrao(financeiroAtual.getCondpgto()))
                    .append(" | condicao=").append(valorOuPadrao(financeiroAtual.getCondicao()))
                    .append(" | forma=").append(financeiroAtual.getForma())
                    .append(" | codEve=").append(financeiroAtual.getCodEve())
                    .append(" | valor=").append(real.format(financeiroAtual.getValor()))
                    .append("\n");
            sb.append("TratamentoPagamentoAtual: ")
                    .append(tratarComoAvistaPix ? "A VISTA (PIX LINK 142)" : "PADRAO DA CONDICAO")
                    .append("\n");

            for (int i = 0; i < pedido.getFinanceiro().size(); i++) {
                Financeiro financeiro = pedido.getFinanceiro().get(i);
                if (financeiro == null) continue;

                sb.append("Financeiro[").append(i).append("]: ")
                        .append("condpgto=").append(valorOuPadrao(financeiro.getCondpgto()))
                        .append(" | condicao=").append(valorOuPadrao(financeiro.getCondicao()))
                        .append(" | forma=").append(financeiro.getForma())
                        .append(" | codEve=").append(financeiro.getCodEve())
                        .append(" | qtprc=").append(financeiro.getQtprc())
                        .append(" | valor=").append(real.format(financeiro.getValor()))
                        .append("\n");
            }
        } else {
            sb.append("Condicao/Forma atual: SEM FINANCEIRO CARREGADO\n");
        }
        sb.append("Itens: ").append(pedido.getItens() != null ? pedido.getItens().size() : 0).append("\n");
        sb.append("Motivos detectados:\n");

        if (motivos.isEmpty()) {
            sb.append(" - Nenhuma regra objetiva identificada com os dados carregados nesse momento.\n");
        } else {
            for (String motivo : motivos) {
                sb.append(" - ").append(motivo).append("\n");
            }
        }

        sb.append("====================================================");
        Log.e("PEDIDO_STATUS_DEBUG", sb.toString());
        registrarDiagnosticoAberturaPedidoNaApiSeSemItens();
    }

    private void registrarResumoPedidoValoresDebug(String origem, Pedidos pedidoRef) {
        if (pedidoRef == null) {
            android.util.Log.d("PEDIDO_VALORES_DEBUG", origem + " | pedido=null");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(origem)
                .append(" | numpedven=").append((long) pedidoRef.getNumpedven())
                .append(" | vlTotal=").append(pedidoRef.getVlTotal())
                .append(" | vlFrete=").append(pedidoRef.getVlFrete())
                .append(" | vlServicos=").append(pedidoRef.getVlServicosAdicionais())
                .append(" | vlfreteTela=").append(vlfrete)
                .append(" | vlDesconto=").append(pedidoRef.getVlDesconto())
                .append(" | peso=").append(pedidoRef.getPeso())
                .append(" | pesoEntrega=").append(pedidoRef.getPesoEntrega())
                .append(" | itens=").append(pedidoRef.getItens() != null ? pedidoRef.getItens().size() : 0)
                .append(" | iniciaComFrete=").append(iniciaComFrete);

        try {
            if (pedidoRef.getFinanceiro() != null && !pedidoRef.getFinanceiro().isEmpty() && pedidoRef.getFinanceiro().get(0) != null) {
                sb.append(" | condpgto=").append(pedidoRef.getFinanceiro().get(0).getCondpgto())
                        .append(" | condicao=").append(pedidoRef.getFinanceiro().get(0).getCondicao())
                        .append(" | forma=").append(pedidoRef.getFinanceiro().get(0).getForma());
            }
        } catch (Exception ignore) {
        }

        try {
            if (pedidoRef.getCondPagamento() != null) {
                sb.append(" | condpgtoPedido=").append(pedidoRef.getCondPagamento().getCondpgto())
                        .append(" | condDescPedido=").append(pedidoRef.getCondPagamento().getDescricao());
            }
        } catch (Exception ignore) {
        }

        android.util.Log.d("PEDIDO_VALORES_DEBUG", sb.toString());

        if (pedidoRef.getItens() == null) {
            return;
        }

        for (int i = 0; i < pedidoRef.getItens().size(); i++) {
            ItemPedidos item = pedidoRef.getItens().get(i);
            if (item == null) {
                continue;
            }

            android.util.Log.d(
                    "PEDIDO_VALORES_DEBUG",
                    origem
                            + " | item[" + i + "]"
                            + " cod=" + item.getCoditprod()
                            + " desc=" + item.getDescricao()
                            + " qtd=" + item.getQtcomp()
                            + " precoUnit=" + item.getPrecoUnit()
                            + " precoPrimario=" + item.getPrecoPrimario()
                            + " precoExibicao=" + getValorExibicaoPedido(item, item.getPrecoUnit())
                            + " vlDescItem=" + item.getVlDescItem()
                            + " vlFreteItem=" + item.getVlFrete()
                            + " situacao=" + item.getSituacao()
                            + " isMotor=" + item.getIsMotor()
                            + " tpped=" + item.getTpped()
            );
        }
    }

    private void registrarDumpCompletoPedidoAoEntrar(String origem, Pedidos pedidoRef) {
        if (pedidoRef == null) {
            Log.d(PEDIDO_ABERTURA_DEBUG_TAG, origem + " | pedido=null");
            return;
        }

        try {
            Gson gsonDebug = new GsonBuilder().serializeNulls().create();
            String pedidoJson = gsonDebug.toJson(pedidoRef);
            final int chunkSize = 3000;
            final int totalPartes = (int) Math.ceil((double) pedidoJson.length() / chunkSize);

            Log.d(
                    PEDIDO_ABERTURA_DEBUG_TAG,
                    origem
                            + " | inicio_dump_completo"
                            + " | numpedven=" + (long) pedidoRef.getNumpedven()
                            + " | itens=" + (pedidoRef.getItens() != null ? pedidoRef.getItens().size() : 0)
                            + " | tamanhoJson=" + pedidoJson.length()
                            + " | partes=" + totalPartes
            );

            for (int inicio = 0, parte = 1; inicio < pedidoJson.length(); inicio += chunkSize, parte++) {
                int fim = Math.min(pedidoJson.length(), inicio + chunkSize);
                Log.d(
                        PEDIDO_ABERTURA_DEBUG_TAG,
                        origem + " | json_parte " + parte + "/" + totalPartes + ":\n" + pedidoJson.substring(inicio, fim)
                );
            }

            Log.d(PEDIDO_ABERTURA_DEBUG_TAG, origem + " | fim_dump_completo");
        } catch (Exception e) {
            Log.e(PEDIDO_ABERTURA_DEBUG_TAG, origem + " | erro_ao_gerar_dump_completo", e);
        }
    }

    private void registrarDiagnosticoAberturaPedidoNaApiSeSemItens() {
        if (pedido == null || pedido.getNumpedven() <= 0) {
            return;
        }

        boolean semItens = pedido.getItens() == null || pedido.getItens().isEmpty();
        if (!semItens || debugAberturaApiEmAndamento || ultimoPedidoDebugAberturaApi == (long) pedido.getNumpedven()) {
            return;
        }

        debugAberturaApiEmAndamento = true;
        ultimoPedidoDebugAberturaApi = (long) pedido.getNumpedven();

        new Thread(() -> {
            try {
                long numeroPedido = (long) pedido.getNumpedven();

                OkHttpClient clientDebug = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build();

                Retrofit retrofitDebug = new Retrofit.Builder()
                        .baseUrl(ApiGemco.BASE_URL)
                        .client(clientDebug)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiGemco apiDebug = retrofitDebug.create(ApiGemco.class);
                String tokenDebug = "Bearer " + ApiGemco.token;

                Log.e(PEDIDO_ABERTURA_DEBUG_TAG, "===== INICIO DEBUG ABERTURA PEDIDO =====");
                Log.e(PEDIDO_ABERTURA_DEBUG_TAG,
                        "pedido_local="
                                + " numpedven=" + numeroPedido
                                + " itensLocal=" + (pedido.getItens() != null ? pedido.getItens().size() : 0)
                                + " vlTotal=" + pedido.getVlTotal()
                                + " vlDesconto=" + pedido.getVlDesconto()
                                + " codStatus=" + pedido.getCodStatus());

                retrofit2.Response<List<Pedidos>> response =
                        apiDebug.GetPedidosConsulta(String.valueOf(numeroPedido), "CODIGO", tokenDebug).execute();

                Log.e(PEDIDO_ABERTURA_DEBUG_TAG,
                        "api_response="
                                + " sucesso=" + response.isSuccessful()
                                + " code=" + response.code()
                                + " message=" + response.message());

                String errorBody = null;
                try {
                    if (response.errorBody() != null) {
                        errorBody = response.errorBody().string();
                    }
                } catch (Exception ignore) {
                }

                logTextoLongo(PEDIDO_ABERTURA_DEBUG_TAG, "api_errorBody=", errorBody);

                List<Pedidos> pedidosApi = response.body();
                logTextoLongo(PEDIDO_ABERTURA_DEBUG_TAG, "api_body_json=", new Gson().toJson(pedidosApi));

                if (pedidosApi == null) {
                    Log.e(PEDIDO_ABERTURA_DEBUG_TAG, "api_body=null");
                } else if (pedidosApi.isEmpty()) {
                    Log.e(PEDIDO_ABERTURA_DEBUG_TAG, "api_body_vazio=true");
                } else {
                    for (int i = 0; i < pedidosApi.size(); i++) {
                        Pedidos pedidoApi = pedidosApi.get(i);
                        int qtdItens = pedidoApi != null && pedidoApi.getItens() != null ? pedidoApi.getItens().size() : 0;
                        Log.e(PEDIDO_ABERTURA_DEBUG_TAG,
                                "pedido_api[" + i + "]="
                                        + " numpedven=" + (pedidoApi != null ? pedidoApi.getNumpedven() : 0)
                                        + " vlTotal=" + (pedidoApi != null ? pedidoApi.getVlTotal() : 0)
                                        + " vlDesconto=" + (pedidoApi != null ? pedidoApi.getVlDesconto() : 0)
                                        + " codStatus=" + (pedidoApi != null ? pedidoApi.getCodStatus() : 0)
                                        + " itens=" + qtdItens);

                        if (pedidoApi != null && pedidoApi.getItens() != null) {
                            for (int j = 0; j < pedidoApi.getItens().size(); j++) {
                                ItemPedidos itemApi = pedidoApi.getItens().get(j);
                                if (itemApi == null) {
                                    Log.e(PEDIDO_ABERTURA_DEBUG_TAG, "pedido_api[" + i + "].item[" + j + "]=null");
                                    continue;
                                }

                                Log.e(PEDIDO_ABERTURA_DEBUG_TAG,
                                        "pedido_api[" + i + "].item[" + j + "]="
                                                + " coditprod=" + itemApi.getCoditprod()
                                                + " descricao=" + itemApi.getDescricao()
                                                + " qtcomp=" + itemApi.getQtcomp()
                                                + " precoUnit=" + itemApi.getPrecoUnit()
                                                + " vlDescItem=" + itemApi.getVlDescItem()
                                                + " codFilOrig=" + itemApi.getCodFilOrig()
                                                + " tpped=" + itemApi.getTpped());
                            }
                        }
                    }
                }

                Log.e(PEDIDO_ABERTURA_DEBUG_TAG, "===== FIM DEBUG ABERTURA PEDIDO =====");
            } catch (Exception e) {
                Log.e(PEDIDO_ABERTURA_DEBUG_TAG, "Falha ao consultar pedido na abertura", e);
            } finally {
                debugAberturaApiEmAndamento = false;
            }
        }).start();
    }

    private void logTextoLongo(String tag, String prefixo, String texto) {
        if (texto == null) {
            Log.e(tag, prefixo + "null");
            return;
        }

        final int tamanhoBloco = 3000;
        for (int inicio = 0; inicio < texto.length(); inicio += tamanhoBloco) {
            int fim = Math.min(texto.length(), inicio + tamanhoBloco);
            Log.e(tag, prefixo + texto.substring(inicio, fim));
        }
    }

    private List<String> levantarMotivosProvaveisStatusPedido() {
        List<String> motivos = new ArrayList<>();

        if (pedido.getCodStatus() == 1) {
            adicionarMotivosLiberacaoComercial(motivos);
        } else if (pedido.getCodStatus() == 2) {
            adicionarMotivosCredito(motivos);
        } else if (pedido.getCodStatus() == 3) {
            motivos.add("Pedido esta liberado para seguir fluxo normal.");
            if ("TEM SALDO".equalsIgnoreCase(valorOuVazio(pedido.getStatusCredito()))) {
                motivos.add("Status de credito indica TEM SALDO.");
            }
        } else if (pedido.getCodStatus() == 9) {
            motivos.add("Pedido inicializado e aguardando finalizacao/fechamento.");
        } else {
            motivos.add("Status sem regra de diagnostico dedicada nesta tela.");
        }

        return motivos;
    }

    private void adicionarMotivosLiberacaoComercial(List<String> motivos) {
        if (pedidoPossuiItemMotorPromocional()) {
            motivos.add("Pedido com item de promocao do motor (isMotor = S), sem necessidade de liberacao comercial por desconto/margem.");
            return;
        }

        if (pedido.getObsAdicional() != null && pedido.getObsAdicional().contains("[NAO_GERAR_LINKS]")) {
            motivos.add("Pedido marcado com [NAO_GERAR_LINKS], bloqueando geracao imediata de links/aprovacao.");
        }

        if (pedido.getFinanceiro() != null) {
            for (Financeiro financeiro : pedido.getFinanceiro()) {
                String evento = valorOuVazio(financeiro.getEvento()).trim();
                if (("CARTAO".equalsIgnoreCase(evento) || "CARTAO MAN".equalsIgnoreCase(evento))
                        && financeiro.getQtprc() >= 7 && financeiro.getQtprc() <= 10) {
                    motivos.add("Parcelamento em " + dfInteiro.format(financeiro.getQtprc()) + "x no evento " + evento + " exige liberacao comercial.");
                }
            }
        }

        double descontoPermitido = parseDoubleUsuario("descontopermitido");
        double margemPermitida = parseDoubleUsuario("percmargem");

        if (pedido.getItens() != null) {
            for (ItemPedidos item : pedido.getItens()) {
                if (item == null) continue;

                if (item.getDpi() > descontoPermitido && !isMotor(item.getIsMotor())) {
                    motivos.add("Item " + item.getCoditprod() + " com desconto " + df.format(item.getDpi()) + "% acima do permitido " + df.format(descontoPermitido) + "%.");
                    break;
                }
            }
        }

        if (pedido.getVlDesconto() > 0 && pedido.getPmargRent() <= margemPermitida && !isMotor(pedido.getIsMotor())) {
            motivos.add("Margem do pedido " + df.format(pedido.getPmargRent()) + "% menor/igual ao permitido " + df.format(margemPermitida) + "% com desconto aplicado.");
        }

        if (motivos.isEmpty()) {
            motivos.add("Status 1 encontrado, mas a regra exata nao ficou evidente apenas pelos dados carregados no pedido.");
        }
    }

    private void adicionarMotivosCredito(List<String> motivos) {
        if ("SEM SALDO".equalsIgnoreCase(valorOuVazio(pedido.getStatusCredito()))) {
            motivos.add("Status de credito retornou SEM SALDO.");
        } else if (pedido.getStatusCredito() == null) {
            motivos.add("Status de credito nulo; fluxo de credito pode ter deixado o pedido pendente.");
        }

        if (pedido.getFinanceiro() != null) {
            for (Financeiro financeiro : pedido.getFinanceiro()) {
                if ("S".equalsIgnoreCase(valorOuVazio(financeiro.getFlautcre()))) {
                    motivos.add("Financeiro com autorizacao de credito obrigatoria (flautcre = S).");
                    break;
                }
                if (financeiro.getForma() == 15 && pedido.getStatusCredito() == null) {
                    motivos.add("Forma 15 com status de credito nulo costuma manter pedido em analise de credito.");
                }
            }
        }

        if (motivos.isEmpty()) {
            motivos.add("Status 2 indica validacao/analise de credito pendente.");
        }
    }

    private String descreverStatusPedido(int codStatus) {
        switch (codStatus) {
            case 1:
                return "LIBERACAO COMERCIAL";
            case 2:
                return "ANALISE DE CREDITO";
            case 3:
                return "LIBERADO";
            case 9:
                return "ABERTO PARA FINALIZACAO";
            default:
                return "STATUS " + codStatus;
        }
    }

    private boolean isMotor(String valor) {
        String normalizado = valorOuVazio(valor).trim();
        return "S".equalsIgnoreCase(normalizado) || "SIM".equalsIgnoreCase(normalizado);
    }

    private boolean pedidoPossuiItemMotorPromocional() {
        if (pedido == null) {
            return false;
        }

        if (isMotor(pedido.getIsMotor())) {
            return true;
        }

        if (pedido.getItens() == null) {
            return false;
        }

        for (ItemPedidos item : pedido.getItens()) {
            if (item != null && isMotor(item.getIsMotor())) {
                return true;
            }
        }

        return false;
    }

    private String valorOuPadrao(String valor) {
        String texto = valorOuVazio(valor);
        return texto.isEmpty() ? "NAO INFORMADO" : texto;
    }

    private String valorOuVazio(String valor) {
        return valor == null ? "" : valor;
    }

    private double parseDoubleUsuario(String chave) {
        if (usuarioLogado == null) return 0d;
        try {
            String valor = usuarioLogado.get(chave);
            if (valor == null || valor.trim().isEmpty()) {
                return 0d;
            }
            return Double.parseDouble(valor.replace(",", ".").trim());
        } catch (Exception e) {
            return 0d;
        }
    }

}
