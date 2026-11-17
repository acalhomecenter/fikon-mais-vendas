package br.com.acalfortaleza.fikonmais.popup;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.OutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.AgendamentoRetiraActivity;
import br.com.acalfortaleza.fikonmais.ConsultaActivity;
import br.com.acalfortaleza.fikonmais.PedidosActivity;
import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.FinanceiroAdapter;
import br.com.acalfortaleza.fikonmais.async.ConsultaEstoqueAsync;
import br.com.acalfortaleza.fikonmais.async.ConsultaEstoqueInterface;
import br.com.acalfortaleza.fikonmais.dao.ArquitetoDao;
import br.com.acalfortaleza.fikonmais.dao.CondPagamentoDao;
import br.com.acalfortaleza.fikonmais.dao.FasesObraDao;
import br.com.acalfortaleza.fikonmais.fragments.ProdutoFragments;
import br.com.acalfortaleza.fikonmais.helper.FikonPagamentoDialog;
import br.com.acalfortaleza.fikonmais.helper.FikonVendasClient;
import br.com.acalfortaleza.fikonmais.helper.FixPayHelper;
import br.com.acalfortaleza.fikonmais.helper.LogHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.Arquiteto;
import br.com.acalfortaleza.fikonmais.models.CondPagamento;
import br.com.acalfortaleza.fikonmais.models.Credz.Plan;
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Lotevencto;
import br.com.acalfortaleza.fikonmais.models.Midias;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.PixRequest;
import br.com.acalfortaleza.fikonmais.models.PixResponse;
import br.com.acalfortaleza.fikonmais.models.PoliticaDesconto;
import br.com.acalfortaleza.fikonmais.model.SalvarPixRequest;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import br.com.acalfortaleza.fikonmais.models.VendaPerfeita;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupPedFechamento extends Activity implements ConsultaEstoqueInterface {
    public static final String EXTRA_PEDIDO_RESULTADO = "pedido_resultado_finalizacao";
    private static final String DEBUG_SERVER_URL = "http://10.1.5.112:7777/event";
    private static final String DEBUG_SESSION_ID = "saved-order-total";
    private static final String DEBUG_RUN_ID = "pre-fix";
    private static final String DEBUG_LOGCAT_TAG = "SavedOrderTotalDebug";
    private static final int REQUEST_CODE_AGENDAMENTO_FINALIZACAO = 2041;
    private static final int ID_SERVICO_DESCONTO_FRETE_GERENTE = 101;
    private static PopupPedFechamento instanciaAtual;

    // Cache global para valores de serviços por pedido (usado na listagem de pedidos)
    public static java.util.Map<Integer, Double> servicosFretePorPedido = new java.util.HashMap<>();
    public static java.util.Map<Integer, java.util.List<Integer>> idsServicosFretePorPedido = new java.util.HashMap<>();

    // Exceção personalizada para PIX já pago
    public static class PixJaPagoException extends Exception {
        public PixJaPagoException(String message) {
            super(message);
        }
    }

    @Override
    public void onBackPressed() {
        try { pedido.setVlServicosAdicionais(0.0); } catch (Exception ignore) {}
        try { pedido.setServicosAdicionaisIds(new java.util.ArrayList<>()); } catch (Exception ignore) {}
        try { if (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido != null) { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setVlServicosAdicionais(0.0); br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setServicosAdicionaisIds(new java.util.ArrayList<>()); } } catch (Exception ignore) {}
        try { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed = 0.0; } catch (Exception ignore) {}
        try { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed = new java.util.ArrayList<>(); } catch (Exception ignore) {}
        try { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisAlterados = false; br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisLastModified = 0; } catch (Exception ignore) {}
        try { int numKey = (int) pedido.getNumpedven(); servicosFretePorPedido.remove(numKey); idsServicosFretePorPedido.remove(numKey); } catch (Exception ignore) {}
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            try { pedido.setVlServicosAdicionais(0.0); } catch (Exception ignore) {}
            try { pedido.setServicosAdicionaisIds(new java.util.ArrayList<>()); } catch (Exception ignore) {}
            try { if (edtServicosFrete != null) edtServicosFrete.setText(new java.text.DecimalFormat("0.00").format(0.0)); } catch (Exception ignore) {}
            try { if (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido != null) { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setVlServicosAdicionais(0.0); br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setServicosAdicionaisIds(new java.util.ArrayList<>()); } } catch (Exception ignore) {}
            try { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed = 0.0; } catch (Exception ignore) {}
            try { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed = new java.util.ArrayList<>(); } catch (Exception ignore) {}
            try { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisAlterados = false; br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisLastModified = 0; } catch (Exception ignore) {}
            try { int numKey = (int) pedido.getNumpedven(); servicosFretePorPedido.remove(numKey); idsServicosFretePorPedido.remove(numKey); } catch (Exception ignore) {}
            try {
                android.content.SharedPreferences sp = getApplicationContext().getSharedPreferences("frete_pedido", android.content.Context.MODE_PRIVATE);
                android.content.SharedPreferences.Editor ed = sp.edit();
                ed.remove("servicos_total_sem_pedido");
                ed.remove("servicos_total");
                ed.apply();
            } catch (Exception ignore) {}
        }
    }

    @Override
    protected void onDestroy() {
        if (instanciaAtual == this) {
            instanciaAtual = null;
        }
        try { pedido.setVlServicosAdicionais(0.0); } catch (Exception ignore) {}
        try { pedido.setServicosAdicionaisIds(new java.util.ArrayList<>()); } catch (Exception ignore) {}
        try { if (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido != null) { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setVlServicosAdicionais(0.0); br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setServicosAdicionaisIds(new java.util.ArrayList<>()); } } catch (Exception ignore) {}
        try { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed = 0.0; } catch (Exception ignore) {}
        try { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed = new java.util.ArrayList<>(); } catch (Exception ignore) {}
        try { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisAlterados = false; br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisLastModified = 0; } catch (Exception ignore) {}
        try { int numKey = (int) pedido.getNumpedven(); servicosFretePorPedido.remove(numKey); idsServicosFretePorPedido.remove(numKey); } catch (Exception ignore) {}
        try {
            android.content.SharedPreferences sp = getApplicationContext().getSharedPreferences("frete_pedido", android.content.Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor ed = sp.edit();
            ed.remove("servicos_total_sem_pedido");
            ed.remove("servicos_total");
            ed.apply();
        } catch (Exception ignore) {}
        android.util.Log.d("FreteDebug", "onDestroy: Estado local de serviços resetado (preparando para próxima abertura)");
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }
    private List<CondPagamento> mList;
    private List<Midias> mListMidias;
    private List<Arquiteto> mListArquitetos;
    private PedidosHelper helper;
    private FixPayHelper helperFixpay;
    private ProgressDialog dialog;
    private Handler handler;
    private CondPagamentoDao dao;
    private FasesObraDao daoFases;
    private ArquitetoDao daoArquiteto;
    private TextInputEditText edtCondpgto;
    private TextInputEditText edtArquiteto;
    private TextInputEditText edtMidia;
    private TextInputEditText edtFrete;
    private TextInputEditText edtServicosFrete;
    private Switch switchRetira;
    private TextInputLayout layout_midia;
    private TextInputLayout layout_frete;
    private TextInputLayout layout_condpgto;
    private Button btn_fechar;
    private TextView txtTotal;
    private TextView txtMargem;
    private Button btn_finalizar;
    public static Pedidos pedido;
    public static Pedidos pedidoAvista;
    private boolean preservarTotalCarregado = false;
    private double vlTotalCarregado = 0.0d;

    private String condicao_selecionada = "N";
    private String fase_selecionada = "N";
    private String retorno = "N";
    private PixResponse pixResponse;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper helperLogin;
    final DecimalFormat dfInteiro = new DecimalFormat("0");
    private DecimalFormat df;
    private DecimalFormatSymbols symbols;
    private SpinnerDialog spinnerDialog;
    private SpinnerDialog spinnerDialogFase;
    private SpinnerDialog spinnerCondicao;
    public static TextView txtCondicao;
    public static ImageView imgFinanceiro;
    public ExpandableListView mListView;
    private List<Financeiro> mListFin;
    public static FinanceiroAdapter adapter;
    private boolean pedidoAlteradoLocalmente = false;
    private String codigoCupom;
    private int identificadorReservaCupom;
    private String erroRegistroCupom;

    private boolean isPedidoExistente(Pedidos pedidoAtual) {
        return pedidoAtual != null && pedidoAtual.getNumpedven() > 0;
    }

    private boolean possuiItemNovoLocal(Pedidos pedidoAtual) {
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

    private boolean possuiItemPromocionalNovoLocal(Pedidos pedidoAtual) {
        if (!isPedidoExistente(pedidoAtual) || pedidoAtual.getItens() == null) {
            return false;
        }

        for (ItemPedidos item : pedidoAtual.getItens()) {
            if (item != null && item.getNumpedven() <= 0 && isItemPromocional(item)) {
                return true;
            }
        }

        return false;
    }

    private void prepararPreservacaoTotalCarregado(Pedidos pedidoAtual) {
        if (isPedidoExistente(pedidoAtual)) {
            preservarTotalCarregado = true;
            vlTotalCarregado = BigDecimal.valueOf(Math.max(0.0d, pedidoAtual.getVlTotal()))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        } else {
            preservarTotalCarregado = false;
            vlTotalCarregado = 0.0d;
        }
    }

    private void liberarPreservacaoTotalCarregado() {
        preservarTotalCarregado = false;
        vlTotalCarregado = 0.0d;
    }

    private void sincronizarPreservacaoTotalAoAbrir(Pedidos pedidoAtual) {
        if (possuiItemNovoLocal(pedidoAtual)) {
            pedidoAlteradoLocalmente = true;
        }

        if (pedidoAlteradoLocalmente) {
            liberarPreservacaoTotalCarregado();
            return;
        }

        prepararPreservacaoTotalCarregado(pedidoAtual);
    }

    private JSONObject buildAberturaDebugSnapshot(String fase, Pedidos pedidoAtual) {
        JSONObject data = new JSONObject();
        try {
            data.put("fase", fase);
            data.put("pedidoItens", pedidoAtual != null && pedidoAtual.getItens() != null ? pedidoAtual.getItens().size() : -1);
            data.put("pedidoVlTotal", pedidoAtual != null ? pedidoAtual.getVlTotal() : -1d);
            data.put("pedidoAlteradoLocalmente", pedidoAlteradoLocalmente);
            data.put("preservarTotalCarregado", preservarTotalCarregado);
            data.put("vlTotalCarregado", vlTotalCarregado);
            if (pedidoAtual != null && pedidoAtual.getItens() != null && !pedidoAtual.getItens().isEmpty()) {
                ItemPedidos item = pedidoAtual.getItens().get(0);
                if (item != null) {
                    data.put("codprod0", item.getCoditprod());
                    data.put("qt0", item.getQtcomp());
                    data.put("precoUnit0", item.getPrecoUnit());
                    data.put("vlDescItem0", item.getVlDescItem());
                }
            }
        } catch (Exception ignore) {
        }
        return data;
    }

    private double getTotalParaExibicao(BigDecimal totalCalculado) {
        BigDecimal totalCalculadoFLOOR = totalCalculado.setScale(2, RoundingMode.HALF_UP);
        return totalCalculadoFLOOR.doubleValue();
    }

    private boolean isMesmoTexto(String valorA, String valorB) {
        String textoA = valorA == null ? "" : valorA.trim();
        String textoB = valorB == null ? "" : valorB.trim();
        return textoA.equalsIgnoreCase(textoB);
    }

    private String obterCodigoCondicaoOriginalPedido(Pedidos pedidoRef) {
        if (pedidoRef == null) {
            return "";
        }

        try {
            if (pedidoRef.getFinanceiro() != null && !pedidoRef.getFinanceiro().isEmpty()) {
                Financeiro financeiro = pedidoRef.getFinanceiro().get(0);
                if (financeiro != null && financeiro.getCondpgto() != null) {
                    return financeiro.getCondpgto().trim();
                }
            }
        } catch (Exception ignore) {
        }

        try {
            if (pedidoRef.getCondPagamento() != null && pedidoRef.getCondPagamento().getCondpgto() != null) {
                return pedidoRef.getCondPagamento().getCondpgto().trim();
            }
        } catch (Exception ignore) {
        }

        return "";
    }

    private String obterDescricaoCondicaoOriginalPedido(Pedidos pedidoRef) {
        if (pedidoRef == null) {
            return "";
        }

        try {
            if (pedidoRef.getFinanceiro() != null && !pedidoRef.getFinanceiro().isEmpty()) {
                Financeiro financeiro = pedidoRef.getFinanceiro().get(0);
                if (financeiro != null && financeiro.getCondicao() != null) {
                    return financeiro.getCondicao().trim();
                }
            }
        } catch (Exception ignore) {
        }

        try {
            if (pedidoRef.getCondPagamento() != null && pedidoRef.getCondPagamento().getDescricao() != null) {
                return pedidoRef.getCondPagamento().getDescricao().trim();
            }
        } catch (Exception ignore) {
        }

        return "";
    }

    private boolean isCondicaoAtualIgualCondicaoOriginalPrazo() {
        if (!pedidoCarregadoOriginalmenteAPrazo) {
            return false;
        }

        String codigoAtual = "N".equals(condicao_selecionada) ? obterCodigoCondicaoOriginalPedido(pedido) : condicao_selecionada;
        String descricaoAtual = obterDescricaoCondicaoAtual();

        return isCondicaoAPrazo(codigoAtual, descricaoAtual)
                && isMesmoTexto(condicaoOriginalCodigo, codigoAtual)
                && (condicaoOriginalDescricao.isEmpty() || isMesmoTexto(condicaoOriginalDescricao, descricaoAtual));
    }

    private boolean pedidoPossuiCondicaoSalva(Pedidos pedidoRef) {
        return possuiTexto(obterCodigoCondicaoOriginalPedido(pedidoRef))
                || possuiTexto(obterDescricaoCondicaoOriginalPedido(pedidoRef));
    }

    private boolean isCondicaoSelecionadaIgualOriginal(String condicaoCodigo, String condicaoDescricao) {
        return isMesmoTexto(condicaoOriginalCodigo, condicaoCodigo)
                && (condicaoOriginalDescricao.isEmpty() || isMesmoTexto(condicaoOriginalDescricao, condicaoDescricao));
    }

    private void aplicarCondicaoPadraoAvistaSeNecessario() {
        if (pedidoPossuiCondicaoSalva(pedido)) {
            return;
        }

        condicao_selecionada = "01";
        if (edtCondpgto != null) {
            edtCondpgto.setText("A VISTA");
        }

        if (pedido == null) {
            return;
        }

        CondPagamento condPagamentoPadrao = pedido.getCondPagamento();
        if (condPagamentoPadrao == null) {
            condPagamentoPadrao = new CondPagamento();
        }
        condPagamentoPadrao.setCondpgto("01");
        condPagamentoPadrao.setDescricao("A VISTA");
        pedido.setCondPagamento(condPagamentoPadrao);

        if (pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
            for (Financeiro financeiro : pedido.getFinanceiro()) {
                if (financeiro == null) {
                    continue;
                }
                financeiro.setCondpgto("01");
                financeiro.setCondicao("A VISTA");
            }
        }

        aplicarPrecoDaCondicaoNosItens(pedido, "01", "A VISTA");
    }
    private HashMap<String, Financeiro> mListItens;
    private ArrayList<String> listaMidias = new ArrayList<>();
    private ArrayList<String> listaArq = new ArrayList<>();
    private ArrayList<String> listaCond = new ArrayList<>();
    private String liberado = "";
    private String temFrete = "N";
    private double descontoped = 0.00f;
    private double freteOrig = 0.00d;
    public static Plan planoEscolhidoCredz;
    private long mLastClickTime = 0;
    private boolean isCalculoFreteConcluido = false;
    private boolean servicosFromCache = false;
    private boolean isSaving = false;
    private boolean pedidoCarregadoOriginalmenteAPrazo = false;
    private boolean pedidoCarregadoComPrecoPromocionalPrazo = false;
    private String condicaoOriginalCodigo = "";
    private String condicaoOriginalDescricao = "";
    private boolean aguardandoRetornoPopupServicos = false;
    private boolean descontoFreteEspecialAtivo = false;
    private boolean agendamentoPreFinalizacaoEmAndamento = false;
    private String agendamentoPendenteData;
    private String agendamentoPendenteHora;
    private String agendamentoPendenteTipo;
    private String agendamentoPendentePaletizado;
    private String agendamentoPendenteDescricao;

    private static TextView VendaPerfeitaConversao;
    private static TextView VendaPerfeitaSKU;
    private static TextView VendaPerfeitaTicket;
    private static TextView VendaPerfeitaDesconto;
    private static TextView VendaPerfeitaEntrega;

    private LinearLayout LayoutVendaPerfeita;

    private PoliticaDesconto politicaDesconto = new PoliticaDesconto();
    private boolean ignorarValidacaoDesconto = false;
    private boolean avisoPromocionalPrazoExibidoAoAbrir = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_pedfechamento);
        instanciaAtual = this;

        // --- Bloco de inicialização de views e helpers (sem alterações) ---
        pedido = clonarPedido((Pedidos) getIntent().getSerializableExtra("pedido"));
        codigoCupom = getIntent().getStringExtra(PopupInserirCupomPromocao.EXTRA_CUPOM_CODIGO);
        identificadorReservaCupom = getIntent().getIntExtra(PopupInserirCupomPromocao.EXTRA_CUPOM_ID_RESERVA, 0);
        pedidoAlteradoLocalmente = getIntent().getBooleanExtra("pedidoAlteradoLocalmente", false);
        sincronizarPreservacaoTotalAoAbrir(pedido);
        // #region debug-point C:oncreate-popup-fechamento
        reportDebugEvent("C", "PopupPedFechamento.onCreate",
                "[DEBUG] pedido recebido ao abrir o PopupPedFechamento",
                buildAberturaDebugSnapshot("onCreate", pedido));
        // #endregion
        ignorarValidacaoDesconto = getIntent().getBooleanExtra("ignorarValidacaoDesconto", false);
        pedidoCarregadoOriginalmenteAPrazo = isPedidoOriginalAPrazo();
        // --- CORRECAO DUPLICACAO ACRESCIMO NO 631006 (MOTOR S) ---
        // ANTES:  pedidoCarregadoComPrecoPromocionalPrazo = pedidoCarregadoOriginalmenteAPrazo;
        // Essa atribuicao CEGA causava DUPLICACAO de /0.95:
        // - se o pedido veio a prazo MAS os itens FORAM carregados SEM acréscimo (ou vice-versa),
        //   entao a flag nao correspondia ao estado REAL dos preços.
        // DEPOIS: sempre detectamos via método dedicado que COMPARA as duas versoes
        // (total sem regra vs total com regra) e escolhe a que mais se aproxima da referencia.
        pedidoCarregadoComPrecoPromocionalPrazo = pedidoTemPrecoPromocionalJaComAcrescimoPrazo(pedido);
        // Logamos a decisao para auditoria
        Log.d("PromoAPrazoDebug",
                "inicializacao_detectada_onCreate"
                        + " | pedido=" + (pedido != null ? pedido.getNumpedven() : 0)
                        + " | pedidoCarregadoOriginalmenteAPrazo=" + pedidoCarregadoOriginalmenteAPrazo
                        + " | pedidoCarregadoComPrecoPromocionalPrazo(DETECTADO)=" + pedidoCarregadoComPrecoPromocionalPrazo);
        condicaoOriginalCodigo = obterCodigoCondicaoOriginalPedido(pedido);
        condicaoOriginalDescricao = obterDescricaoCondicaoOriginalPedido(pedido);

        helper = new PedidosHelper();
        handler = new Handler();
        helperLogin = new LoginHelper(this);
        helperFixpay = new FixPayHelper();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .9));

        mListFin = new ArrayList<>();
        mListItens = new HashMap<String, Financeiro>();
        adapter = new FinanceiroAdapter(this, mListFin, mListItens, false);
        mList = new ArrayList<>();
        mListArquitetos = new ArrayList<>();
        usuarioLogado = helperLogin.GetUsuarioSalvo();

        mListView = findViewById(R.id.pop_fin_list);
        txtCondicao = findViewById(R.id.pop_fin_condicao);
        imgFinanceiro = findViewById(R.id.pop_ped_imgFin);
        edtCondpgto = findViewById(R.id.pop_ped_spinner);
        edtMidia = findViewById(R.id.pop_ped_edtMidia);
        edtArquiteto = findViewById(R.id.pop_ped_edtArquiteto);
        edtFrete = findViewById(R.id.pop_ped_edtFrete);
        edtServicosFrete = findViewById(R.id.pop_ped_edtServicosFrete);
        try {
            int switchId = getResources().getIdentifier("PopupPedFechamento_SwitchRetira", "id", getPackageName());
            if (switchId != 0) {
                switchRetira = findViewById(switchId);
            } else {
                switchRetira = null;
            }
        } catch (Exception ignore) { switchRetira = null; }
        btn_finalizar = findViewById(R.id.pop_ped_btn_Finalizar);
        isCalculoFreteConcluido = false;
        if (edtFrete != null) edtFrete.setEnabled(false);
        if (edtServicosFrete != null) edtServicosFrete.setEnabled(false);
        if (btn_finalizar != null) btn_finalizar.setEnabled(false);
        layout_midia = findViewById(R.id.pop_ped_layoutMidia);
        layout_condpgto = findViewById(R.id.pop_ped_layoutcondicao);
        layout_frete = findViewById(R.id.pop_ped_layoutFrete);
        txtTotal = findViewById(R.id.pop_ped_txtTotal);
        txtMargem = findViewById(R.id.pop_ped_txtMargem);
        btn_fechar = findViewById(R.id.pop_ped_btnFechar);
        // ... VendaPerfeita findViewByIds ...
        VendaPerfeitaConversao = findViewById(R.id.PopupPedidoFechamento_VendaPerfeitaConversao);
        VendaPerfeitaSKU = findViewById(R.id.PopupPedidoFechamento_VendaPerfeitaSKU);
        VendaPerfeitaTicket= findViewById(R.id.PopupPedidoFechamento_VendaPerfeitaTicket);
        VendaPerfeitaDesconto = findViewById(R.id.PopupPedidoFechamento_VendaPerfeitaDesconto);
        VendaPerfeitaEntrega = findViewById(R.id.PopupPedidoFechamento_VendaPerfeitaEntrega);
        LayoutVendaPerfeita = findViewById(R.id.PopupPedidoFechamento_LayoutVendaPerfeita);

        symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        df = new DecimalFormat("0.00", symbols);
        // --- Fim do bloco de inicialização ---

        // --- INÍCIO DA LÓGICA DE CORREÇÃO ---

        // Se este pedido foi gerado a partir de um orçamento, tentar carregar o valor
        // dos serviços de frete que já está em cache no PopupOrcFechamento e aplicar
        // ao objeto `pedido` e ao input `edtServicosFrete`.
        try {
            Intent srcIntent = getIntent();
            int numOrc = 0;
            String numOrcStr = srcIntent.getStringExtra("numorc");
            if (numOrcStr == null || numOrcStr.isEmpty()) {
                numOrc = srcIntent.getIntExtra("numOrc", 0);
            } else {
                try { numOrc = Integer.parseInt(numOrcStr); } catch(Exception ex) { numOrc = 0; }
            }

            if (numOrc > 0) {
                Double servCached = br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.getOrDefault(numOrc, null);
                if (servCached != null) {
                    pedido.setVlServicosAdicionais(servCached);
                    edtServicosFrete.setText(df.format(servCached));
                    java.util.List<Integer> ids = br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.getOrDefault(numOrc, new java.util.ArrayList<>());
                    if (ids != null && !ids.isEmpty()) {
                        pedido.setServicosAdicionaisIds(ids);
                    }
                    servicosFromCache = true;
                    Log.d("FreteDebug", "Carregado servicos do cache do orcamento " + numOrc + ": " + servCached);
                }
            } else {
                // Se não veio o número do orçamento, pode ter vindo direto a lista de ids
                java.util.ArrayList<Integer> idsExtra = (java.util.ArrayList<Integer>) srcIntent.getSerializableExtra("servicos_adicionais_ids");
                if (idsExtra != null && !idsExtra.isEmpty()) {
                    // Tenta encontrar um orçamento no cache com a mesma lista de ids
                    for (Integer key : br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.keySet()) {
                        java.util.List<Integer> idsList = br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.get(key);
                        if (idsList != null && idsList.equals(idsExtra)) {
                            Double servVal = br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.getOrDefault(key, 0.0);
                            pedido.setVlServicosAdicionais(servVal);
                            pedido.setServicosAdicionaisIds(idsList);
                            edtServicosFrete.setText(df.format(servVal));
                            servicosFromCache = true;
                            Log.d("FreteDebug", "Carregado servicos do cache (ids matched) from orcamento " + key + ": " + servVal);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d("FreteDebug", "Não foi possível carregar servicos do cache do orçamento", e);
        }

        final android.app.ProgressDialog progressDialog = android.app.ProgressDialog.show(PopupPedFechamento.this, "", "Carregando dados de frete e serviços...", true);

        new Thread(() -> {
            try {
                if (pedido != null && pedido.getVlServicosAdicionais() <= 0 && pedido.getNumpedven() > 0) {
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
                        if (resp.isSuccessful() && resp.body() != null && resp.body().getValorTotal() != null) {
                            double valorServico = resp.body().getValorTotal().doubleValue();
                            java.util.List<Integer> ids = resp.body().getIdsSelecionados();
                            pedido.setVlServicosAdicionais(valorServico);
                            if (ids != null) pedido.setServicosAdicionaisIds(ids);
                        }
                    } catch (Exception ignore) {}
                }

                if (pedido != null && pedido.getItens() != null) {
                    for(ItemPedidos item : pedido.getItens()){
                        if("E".equals(item.getTpped())){
                            temFrete = "S";
                            break;
                        }
                    }
                }

                double freteBase = 0.00;
                double pesoEntregaCalc = 0.0;
                try {
                    if (pedido != null && pedido.getItens() != null) {
                        for (br.com.acalfortaleza.fikonmais.models.ItemPedidos it : pedido.getItens()) {
                            if ("E".equals(it.getTpped())) {
                                pesoEntregaCalc += (it.getQtcomp() * it.getPeso());
                            }
                        }
                        pedido.setPesoEntrega(pesoEntregaCalc);
                    }
                } catch (Exception ignore) {}

                if (!"S".equals(temFrete) && pedido != null) {
                    pedido.setVlFrete(0.0d);
                    pedido.setVlServicosAdicionais(0.0d);
                    pedido.setVlFreteSelecionado(0.0d);
                    pedido.setServicosAdicionaisIds(new java.util.ArrayList<>());
                    pedido.setServicosAdicionaisNomes(new java.util.ArrayList<>());
                    freteOrig = 0.0d;
                }

                if("S".equals(temFrete)){
                    if (pedido.getVlFrete() > 0) {
                        double freteTotalApi = (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedidoSalvoOriginal != null
                                && br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedidoSalvoOriginal.getVlFrete() > 0)
                                ? br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedidoSalvoOriginal.getVlFrete()
                                : pedido.getVlFrete();
                        double vlServCabecalho = pedido.getVlServicosAdicionais() > 0 ? pedido.getVlServicosAdicionais() : 0.0;
                        double baseOnly = (vlServCabecalho > 0.0d && freteTotalApi > vlServCabecalho)
                                ? (freteTotalApi - vlServCabecalho)
                                : freteTotalApi;
                        if (baseOnly <= 0.0d && freteTotalApi > 0.0d) {
                            baseOnly = freteTotalApi;
                        }
                        freteBase = Math.max(0.0d, baseOnly);
                        freteOrig = freteBase;
                    } else if (pedido.getVlFreteSelecionado() > 0){
                        if ((pedido.getPesoEntrega() / 1000) > 1.0) {
                            freteBase = Math.floor(pedido.getVlFreteSelecionado() * (pedido.getPesoEntrega() / 1000));
                        } else {
                            freteBase = pedido.getVlFreteSelecionado();
                        }
                        freteOrig = freteBase;
                    }
                }

                double valorServicos = pedido != null ? pedido.getVlServicosAdicionais() : 0.0;
                if (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisAlterados || br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed > 0) {
                    valorServicos = 0.0;
                    if (pedido != null) pedido.setVlServicosAdicionais(0.0);
                }
                if (valorServicos <= 0.0) {
                    valorServicos = 0.0;
                    if (pedido != null) {
                        pedido.setVlServicosAdicionais(0.0);
                        try {
                            pedido.setServicosAdicionaisIds(new java.util.ArrayList<>());
                            int numPed = (int) pedido.getNumpedven();
                            servicosFretePorPedido.put(numPed, 0.0);
                            idsServicosFretePorPedido.put(numPed, new java.util.ArrayList<>());
                        } catch (Exception ignore) {}
                    }
                }

                final double freteBaseFinal = freteBase;
                final double valorServicosFinal = valorServicos;

                runOnUiThread(() -> {
                    try {
                        if (switchRetira != null) {
                            switchRetira.setChecked(!"S".equals(temFrete));
                        }

                        if (edtFrete != null) edtFrete.setText(df.format(freteBaseFinal));
                        if (edtServicosFrete != null) edtServicosFrete.setText(df.format(valorServicosFinal));
                        Log.d("FreteDebug", "Fechamento SINCRONIZADO: edtFrete=" + df.format(freteBaseFinal) + ", edtServicosFrete=" + df.format(valorServicosFinal));
                        sincronizarCondicaoAtualDoPedido();
                        aplicarCondicaoPadraoAvistaSeNecessario();

                        if (pedido != null && pedido.getItens() != null) {
                            atualizaTotais(pedido.getItens());
                        }

                        isCalculoFreteConcluido = true;
                        atualizarPermissaoEdicaoFrete();
                        if (btn_finalizar != null) btn_finalizar.setEnabled(true);
                    } catch (Exception e) {
                        Log.e("FreteDebug", "Erro ao atualizar UI no load sincrono", e);
                    } finally {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("FreteDebug", "Erro na thread de load sincrono", e);
                runOnUiThread(() -> {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    isCalculoFreteConcluido = true;
                    if (btn_finalizar != null) btn_finalizar.setEnabled(true);
                });
            }
        }).start();

        isCalculoFreteConcluido = true;

        // --- FIM DA LÓGICA DE CORREÇÃO ---

        // --- Início dos Listeners e Configurações da UI ---
        atualizarPermissaoEdicaoFrete();
        if (btn_finalizar != null) btn_finalizar.setEnabled(true);

        btn_fechar.setOnClickListener(view -> {
            finish();
        });

        edtFrete.setOnEditorActionListener((textView, i, event) -> {
            if(event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || i == EditorInfo.IME_ACTION_DONE) {
                if (edtFrete.getText().toString().equals("")){
                    edtFrete.setText("0.00");
                }
                if(validarDescontoFrete()){
                    liberarPreservacaoTotalCarregado();
                    if (pedido != null && pedido.getItens() != null) {
                        atualizaTotais(pedido.getItens());
                    }
                }
            }
            return false;
        });

        edtServicosFrete.setEnabled("S".equals(temFrete) && (switchRetira == null || !switchRetira.isChecked()));

        if (switchRetira != null) {
            switchRetira.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    boolean habilitarServicos = "S".equals(temFrete) && !isChecked;
                    edtServicosFrete.setEnabled(habilitarServicos);
                    if (!habilitarServicos) {
                        liberarPreservacaoTotalCarregado();
                        edtServicosFrete.setText(df.format(0.00));
                        pedido.setVlServicosAdicionais(0.0);
                        try {
                            pedido.setServicosAdicionaisIds(new java.util.ArrayList<>());
                            pedido.setServicosAdicionaisNomes(new java.util.ArrayList<>());
                            br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed = 0.0;
                            br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed = new java.util.ArrayList<>();
                            int numKey = (int) pedido.getNumpedven();
                            servicosFretePorPedido.put(numKey, 0.0);
                            idsServicosFretePorPedido.put(numKey, new java.util.ArrayList<>());
                        } catch (Exception ignore) {}
                    }
                    if (isChecked) {
                        liberarPreservacaoTotalCarregado();
                        edtFrete.setText(df.format(0.00));
                        pedido.setVlFrete(0.0);
                        temFrete = "N";
                        try {
                            if (pedido != null && pedido.getItens() != null) {
                                for (int i = 0; i < pedido.getItens().size(); i++) {
                                    br.com.acalfortaleza.fikonmais.models.ItemPedidos it = pedido.getItens().get(i);
                                    it.setTpped("P");
                                    it.setTppedDesc("Retira");
                                    try { br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.atualizaItem(it, i); } catch (Exception ignore) {}
                                }
                            }
                        } catch (Exception ignore) {}
                    } else {
                        liberarPreservacaoTotalCarregado();
                        double baseFreteCalc = 0.0;
                        try {
                            if ("S".equals(temFrete) && pedido.getVlFreteSelecionado() > 0){
                                if ((pedido.getPesoEntrega() / 1000) > 1.0) {
                                    baseFreteCalc = Math.floor(pedido.getVlFreteSelecionado() * (pedido.getPesoEntrega() / 1000));
                                } else {
                                    baseFreteCalc = pedido.getVlFreteSelecionado();
                                }
                            }
                        } catch (Exception ignore) {}
                        edtFrete.setText(df.format(baseFreteCalc));
                        pedido.setVlFrete(baseFreteCalc);
                    }
                    if (pedido != null && pedido.getItens() != null) {
                        atualizaTotais(pedido.getItens());
                    }
                }
            });
        }
        edtServicosFrete.setOnTouchListener((v, event) -> {
            if (!edtServicosFrete.isEnabled()) return false;
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                android.graphics.drawable.Drawable[] ds = edtServicosFrete.getCompoundDrawables();
                if (ds != null && ds.length > DRAWABLE_RIGHT && ds[DRAWABLE_RIGHT] != null && event.getRawX() >= (edtServicosFrete.getRight() - ds[DRAWABLE_RIGHT].getBounds().width())) {
                    Intent intent = new Intent(PopupPedFechamento.this, PopupServicosAdicionais.class);
                    double pesoPed = (pedido != null) ? pedido.getPeso() : 0.0;
                    intent.putExtra("pesoTotal", pesoPed);
                    android.util.Log.d("FreteServAdd", "Abrindo PopupServicosAdicionais para Pedido com peso: " + pesoPed + " KG");
                    aguardandoRetornoPopupServicos = true;
                    startActivity(intent);
                    return true;
                }
            }
            return false;
        });

        mListView.setAdapter(adapter);

        ConsultarCombobox();



        atualizarPermissaoEdicaoFrete();

        try {
            br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisAlterados = false;
            br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed = 0.0;
        } catch (Exception ignore) {}

        spinnerCondicao = new SpinnerDialog(PopupPedFechamento.this, listaCond, "Selecione uma condição de pagamento");
        spinnerCondicao.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {


                edtCondpgto.setText(item);
                android.util.Log.d("FreteDebug", "Fechamento Condicao selecionada=" + item
                        + ", edtFrete=" + edtFrete.getText().toString()
                        + ", VlFrete=" + pedido.getVlFrete()
                        + ", VlServicosAdicionais=" + pedido.getVlServicosAdicionais());
                if (position >= 0) {
                    if (edtFrete.getText().toString().equals("")){
                        edtFrete.setText("0.00");

                    }
                    // 1. Pega os valores atualizados dos campos de texto
                    BigDecimal valorFreteBase = new BigDecimal("0.00");
                    BigDecimal valorServicos = new BigDecimal("0.00");
                    try {
                        valorFreteBase = new BigDecimal(edtFrete.getText().toString().replace(",", "."));
                        valorServicos = new BigDecimal(edtServicosFrete.getText().toString().replace(",", "."));
                    } catch (NumberFormatException e) {
                        Log.e("FreteDebug", "Erro de conversão ao abrir Forma de Pagamento.");
                    }

                    String condicaoCodigoAnterior = "N".equals(condicao_selecionada) ? "" : condicao_selecionada;
                    String condicaoDescricaoAnterior = obterDescricaoCondicaoAtual();
                    String condicaoCodigoSelecionada = mList.get(position).getCondpgto();
                    String condicaoDescricaoSelecionada = mList.get(position).getDescricao();

                    final BigDecimal valorFreteBaseFinal = valorFreteBase;
                    final BigDecimal valorServicosFinal = valorServicos;

                    Runnable acaoAbrirFormaPagamento = () -> {
                        boolean voltouParaCondicaoOriginal = isCondicaoSelecionadaIgualOriginal(condicaoCodigoSelecionada, condicaoDescricaoSelecionada);
                        boolean condicaoMudou = !isMesmoTexto(condicaoCodigoAnterior, condicaoCodigoSelecionada)
                                || !isMesmoTexto(condicaoDescricaoAnterior, condicaoDescricaoSelecionada);
                        if (condicaoMudou) {
                            liberarPreservacaoTotalCarregado();
                        }
                        condicao_selecionada = condicaoCodigoSelecionada;
                        boolean condicaoSelecionadaAPrazo = isCondicaoAPrazo(condicaoCodigoSelecionada, condicaoDescricaoSelecionada);
                        if (condicaoMudou && !condicaoSelecionadaAPrazo) {
                            avisoDescontoAVistaExibido = false; // Reset to allow showing when they change
                            tentarExibirAvisoDescontoAVistaAoAbrir();
                        }

                        Pedidos pedidoParaUsar;
                        Pedidos pedidoBaseAvista = (Pedidos) getIntent().getSerializableExtra("pedidoBaseAvista");
                        if (pedidoBaseAvista == null) {
                            pedidoBaseAvista = normalizarPedidoBaseAvista((Pedidos) getIntent().getSerializableExtra("pedido"));
                        }
                        Pedidos pedidoBasePrazo = (Pedidos) getIntent().getSerializableExtra("pedidoAPrazo");
                        if (pedidoBasePrazo == null) {

                            if (pedidoBaseAvista != null) {
                                pedidoBasePrazo = clonarPedido(pedidoBaseAvista);
                            } else {
                                pedidoBasePrazo = clonarPedido(pedido);
                                if (pedidoBasePrazo != null) {
                                    // Base LIMPA: nao carrega frete/servicos da UI para o calculo de subtotal original
                                    pedidoBasePrazo.setVlFrete(0.0d);
                                    pedidoBasePrazo.setVlServicosAdicionais(0.0d);
                                    // Reconstroi VlTotal apenas com os itens para nao contaminar com frete da tela
                                    BigDecimal subItensLimpo = BigDecimal.ZERO;
                                    if (pedidoBasePrazo.getItens() != null) {
                                        for (ItemPedidos it : pedidoBasePrazo.getItens()) {
                                            if (it == null) continue;
                                            BigDecimal precoU = BigDecimal.valueOf(Math.max(0.0d, it.getPrecoUnit()));
                                            BigDecimal descU = BigDecimal.valueOf(Math.max(0.0d, it.getVlDescItem()));
                                            BigDecimal qtde = BigDecimal.valueOf(Math.max(1, it.getQtcomp()));
                                            subItensLimpo = subItensLimpo.add(precoU.subtract(descU).max(BigDecimal.ZERO).multiply(qtde));
                                        }
                                    }
                                    pedidoBasePrazo.setVlTotal(subItensLimpo.setScale(2, RoundingMode.HALF_UP).doubleValue());
                                }
                            }
                        }
                        boolean podeReativarPreservacaoTotal = condicaoMudou
                                && isPedidoExistente(pedido)
                                && voltouParaCondicaoOriginal
                                && pedidoBasePrazo != null
                                && !possuiItemNovoLocal(pedidoBasePrazo);
                        if (podeReativarPreservacaoTotal) {
                            prepararPreservacaoTotalCarregado(pedidoBasePrazo);
                        }
                        boolean pedidoAtualPixLinkAvista = isPixLinkPedidoSelecionado(pedido);

                        if (pedidoAtualPixLinkAvista) {
                            if (pedidoBaseAvista == null) {
                                pedidoBaseAvista = pedido;
                            }
                            pedidoParaUsar = clonarPedido(pedidoBaseAvista);
                            pedidoAvista = clonarPedido(pedidoBaseAvista);
                        } else {
                            pedidoParaUsar = condicaoSelecionadaAPrazo ? clonarPedido(pedidoBasePrazo) : clonarPedido(pedidoBaseAvista);
                            if (pedidoParaUsar == null) {
                                pedidoParaUsar = clonarPedido(pedido);
                            }

                            if ("87".equals(condicaoCodigoSelecionada)) {
                                pedidoAvista = clonarPedido(pedidoBaseAvista);
                            } else {
                                pedidoAvista = clonarPedido(pedido);
                            }
                        }

                        if (pedidoParaUsar == null) {
                            pedidoParaUsar = clonarPedido(pedido); // Fallback
                        }
                        if (pedidoAvista == null) {
                            pedidoAvista = clonarPedido(pedidoParaUsar);
                        }

                        boolean voltouParaCondicaoOriginalPrazo = pedidoCarregadoOriginalmenteAPrazo
                                && condicaoSelecionadaAPrazo
                                && pedidoBasePrazo != null
                                && voltouParaCondicaoOriginal
                                && !possuiItemNovoLocal(pedidoBasePrazo);

                        boolean reutilizarSubtotalOriginalPrazo = voltouParaCondicaoOriginalPrazo
                                && !deveUsarTotalRecalculadoDosItens(pedidoParaUsar);

                        // 3. RECALCULA o subtotal dos itens para o pedido escolhido
                        BigDecimal subTotalFinal;
                        if (reutilizarSubtotalOriginalPrazo) {
                            pedidoParaUsar = clonarPedido(pedidoBasePrazo);
                            if (pedidoParaUsar == null) {
                                pedidoParaUsar = clonarPedido(pedido);
                            }
                            copiarDescontosLocaisItens(pedido, pedidoParaUsar);
                            copiarDescontosLocaisItens(pedido, pedidoAvista);
                            aplicarPrecoDaCondicaoNosItens(pedidoParaUsar, condicaoCodigoSelecionada, condicaoDescricaoSelecionada);
                            aplicarPrecoDaCondicaoNosItens(pedidoAvista, "01", "A VISTA");
                            BigDecimal subTotalRecalc = BigDecimal.ZERO;
                            for (ItemPedidos i : pedidoParaUsar.getItens()) {
                                subTotalRecalc = subTotalRecalc.add(
                                        calcularTotalItemConsiderandoPromocaoPrazo(i, condicaoCodigoSelecionada, condicaoDescricaoSelecionada)
                                );
                            }
                            subTotalRecalc = subTotalRecalc.setScale(2, RoundingMode.HALF_UP);
                            BigDecimal subtotalOriginal = obterSubtotalOriginalPrazoCarregado(pedidoBasePrazo);
                            subTotalFinal = subTotalRecalc.compareTo(subtotalOriginal) > 0 ? subTotalRecalc : subtotalOriginal;
                            Log.d("PromoAPrazoDebug",
                                    "Reutilizando subtotal original do pedido a prazo"
                                            + " | pedido=" + (pedidoBasePrazo != null ? pedidoBasePrazo.getNumpedven() : 0)
                                            + " | subtotalOriginalPrazo=" + subtotalOriginal
                                            + " | subTotalRecalc=" + subTotalRecalc
                                            + " | subTotalFinal=" + subTotalFinal
                                            + " | condOriginalCodigo=" + condicaoOriginalCodigo
                                            + " | condOriginalDescricao=" + condicaoOriginalDescricao
                                            + " | condCodigo=" + condicaoCodigoSelecionada
                                            + " | condDesc=" + condicaoDescricaoSelecionada);
                        } else {
                            copiarDescontosLocaisItens(pedido, pedidoParaUsar);
                            copiarDescontosLocaisItens(pedido, pedidoAvista);
                            aplicarPrecoDaCondicaoNosItens(pedidoParaUsar, condicaoCodigoSelecionada, condicaoDescricaoSelecionada);
                            aplicarPrecoDaCondicaoNosItens(pedidoAvista, "01", "A VISTA");
                            BigDecimal subTotalItens = new BigDecimal("0.00");
                            for(ItemPedidos i : pedidoParaUsar.getItens()) {
                                subTotalItens = subTotalItens.add(calcularTotalItemConsiderandoPromocaoPrazo(i, condicaoCodigoSelecionada, condicaoDescricaoSelecionada));
                            }
                            subTotalFinal = subTotalItens.setScale(2, RoundingMode.HALF_UP);
                        }

                        BigDecimal totalGeralAvista = BigDecimal.ZERO;
                        if ("87".equals(condicaoCodigoSelecionada) && pedidoAvista != null && pedidoAvista.getItens() != null) {
                            BigDecimal subTotalItensAvista = new BigDecimal("0.00");
                            for (ItemPedidos itemAvista : pedidoAvista.getItens()) {
                                subTotalItensAvista = subTotalItensAvista.add(
                                        calcularTotalItemConsiderandoPromocaoPrazo(itemAvista, "01", "A VISTA")
                                );
                            }
                            totalGeralAvista = subTotalItensAvista.add(valorFreteBaseFinal).add(valorServicosFinal).setScale(2, RoundingMode.HALF_UP);
                        }

                        BigDecimal totalGeral = subTotalFinal.add(valorFreteBaseFinal).add(valorServicosFinal).setScale(2, RoundingMode.HALF_UP);
                        double totalParaFormaPgto = getTotalParaExibicao(totalGeral);
                        if (reutilizarSubtotalOriginalPrazo) {
                            preservarTotalCarregado = true;
                            vlTotalCarregado = totalParaFormaPgto;
                        }
                        pedidoParaUsar.setVlTotal(totalParaFormaPgto);
                        if ("87".equals(condicaoCodigoSelecionada) && totalGeralAvista.compareTo(BigDecimal.ZERO) > 0) {
                            pedidoAvista.setVlTotal(getTotalParaExibicao(totalGeralAvista));
                        } else {
                            pedidoAvista.setVlTotal(totalParaFormaPgto);
                        }

                        Log.d("FreteDebug", "Enviando para FormaPgto com Total: " + totalParaFormaPgto + " | totalCalculado=" + totalGeral + " | preservarTotalCarregado=" + preservarTotalCarregado);

                        // 5. Configura os dados da condição antes de refletir na UI
                        Financeiro fin = new Financeiro();
                        fin.setCondpgto(condicaoCodigoSelecionada);
                        fin.setCondicao(condicaoDescricaoSelecionada);
                        fin.setValor(totalParaFormaPgto);
                        List<Financeiro> list = new ArrayList<>();
                        list.add(fin);

                        CondPagamento condPagamentoSelecionada = new CondPagamento();
                        condPagamentoSelecionada.setCondpgto(condicaoCodigoSelecionada);
                        condPagamentoSelecionada.setDescricao(condicaoDescricaoSelecionada);

                        pedidoParaUsar.setFinanceiro(list);
                        pedidoParaUsar.setCondPagamento(condPagamentoSelecionada);
                        pedidoAvista.setFinanceiro(list);
                        pedidoAvista.setCondPagamento(condPagamentoSelecionada);

                        // 5.1. Atualiza a instância usada na UI do popup usando o mesmo fluxo central de totais.
                        pedido = pedidoParaUsar;
                        if (pedido != null && pedido.getItens() != null) {
                            atualizaTotais(pedido.getItens());
                        } else {
                            txtTotal.setText("Total: " + java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR")).format(totalParaFormaPgto));
                        }

                        double totalFinalParaFormaPgto = pedido != null ? pedido.getVlTotal() : totalParaFormaPgto;
                        fin.setValor(totalFinalParaFormaPgto);
                        if (pedidoParaUsar != null) {
                            pedidoParaUsar.setVlTotal(totalFinalParaFormaPgto);
                        }

                        logDiagnosticoPromocaoAPrazo(
                                "condicao_selecionada",
                                pedidoParaUsar,
                                condicaoCodigoSelecionada,
                                condicaoDescricaoSelecionada
                        );
                        abrirPopupFormaPagamentoComAviso(pedidoParaUsar, pedidoAvista, totalFinalParaFormaPgto, condicaoCodigoSelecionada, condicaoDescricaoSelecionada);
                    };

                    boolean condicaoMudouForCheck = !isMesmoTexto(condicaoDescricaoAnterior, condicaoDescricaoSelecionada); // simplified
                    boolean condicaoSelecionadaAPrazo = isCondicaoAPrazo(condicaoCodigoSelecionada, condicaoDescricaoSelecionada);

                    if (condicaoSelecionadaAPrazo) {
                        avisoPromocionalPrazoExibidoAoAbrir = false;
                        condicao_selecionada = condicaoCodigoSelecionada;
                        tentarExibirAvisoPromocionalAPrazoAoAbrir(acaoAbrirFormaPagamento);
                    } else {
                        condicao_selecionada = condicaoCodigoSelecionada;
                        acaoAbrirFormaPagamento.run();
                    }

                }
            }
        });



        spinnerDialog = new SpinnerDialog(PopupPedFechamento.this, listaArq, "Selecione um Arquiteto");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                if(item.equals("Retirar Especificador")){
                    edtArquiteto.setText("");
                    pedido.setArquiteto("");
                    pedido.setCodArq(0);
                }else{
                    edtArquiteto.setText(item);
                    pedido.setArquiteto(item);
                    pedido.setCodArq(mListArquitetos.get(position).getCodarq());
                }

            }
        });



        spinnerDialogFase = new SpinnerDialog(PopupPedFechamento.this, listaMidias, "Selecione uma Mídia");
        spinnerDialogFase.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtMidia.setText(item);
                pedido.setMidia(mListMidias.get(position));
                pedido.setCodMidia(mListMidias.get(position).getCodMidia());
                fase_selecionada = "S";
            }
        });


        if (pedido != null) {
            if (pedido.getArquiteto() != null && !pedido.getArquiteto().equals("")) {
                edtArquiteto.setText(pedido.getArquiteto());
            }

            if (pedido.getMidia() != null && !pedido.getMidia().getDescricao().equals("")) {
                edtMidia.setText(pedido.getMidia().getDescricao());
                fase_selecionada = "S";

            }
        }


        edtArquiteto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtArquiteto.getRight() - edtArquiteto.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        edtMidia.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtMidia.getRight() - edtMidia.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialogFase.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        edtCondpgto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;




                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtCondpgto.getRight() - edtCondpgto.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerCondicao.showSpinerDialog();

                    }
                }
                return false;
            }
        });





        btn_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (!validarCondicao() || !validarForma() || !validarDescontoFrete()){

                    if (pedido != null && pedido.getItens() != null) {
                        atualizaTotais(pedido.getItens());
                    }


                } else {
                    if (Integer.parseInt(usuarioLogado.get("codigo")) == 0 && (!usuarioLogado.get("cargo").toString().equals("AUXILIAR DE VENDAS CORPORATIVO FX U") && !usuarioLogado.get("cargo").toString().equals("AUXILIAR DE VENDAS FX U")) ) {
                        Toast.makeText(PopupPedFechamento.this, "Atenção :| apenas consultores podem utilizar essa função!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        if (temFrete.equals("S") && pedido.getCodmodal() == 0) {
                            Intent intent = new Intent(PopupPedFechamento.this, PopupEnderecoEntrega.class);
                            intent.putExtra("fromFinalize", true);
                            intent.putExtra("endereco", (java.io.Serializable) pedido.getCliente().getEnderecos());
                            intent.putExtra("pedido", pedido);
                            startActivity(intent);
                            return;
                        }
                        if (deveAbrirAgendamentoAntesDaFinalizacao(pedido) && !agendamentoPreFinalizacaoEmAndamento) {
                            abrirAgendamentoAntesDaFinalizacao();
                            return;
                        }
                        continuarFluxoFinalizacao();
                    }

                }
            }
        });


        if (pedido != null && pedido.getItens() != null) {
            atualizaTotais(pedido.getItens()); // <--- PELA CHAMADA CORRETA
        }


        AtualizarLayoutVendaPerfeita();

        LayoutVendaPerfeita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PopupPedFechamento.this, PopupVendaPerfeita.class);
                intent.putExtra("indicadores", pedido.getIndicadoresVendaPerfeita());
                intent.putExtra("metas", pedido.getMetasVendaPerfeita());
                startActivity(intent);
            }
        });

        // edtCondpgto.post(PopupPedFechamento.this::tentarExibirAvisoPromocionalAPrazoAoAbrir);

    }

    private void carregarPrecos2Pendentes(Runnable onComplete) {
        onComplete.run();
    }

    private void buscarPreco2Recursivo(java.util.List<ItemPedidos> itens, int index, android.app.ProgressDialog progress, Runnable onComplete) {
        onComplete.run();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CODE_AGENDAMENTO_FINALIZACAO) {
            return;
        }

        agendamentoPreFinalizacaoEmAndamento = false;

        if (resultCode == RESULT_OK && data != null) {
            agendamentoPendenteData = data.getStringExtra(AgendamentoRetiraActivity.EXTRA_AGENDAMENTO_DATA);
            agendamentoPendenteHora = data.getStringExtra(AgendamentoRetiraActivity.EXTRA_AGENDAMENTO_HORA);
            agendamentoPendenteTipo = data.getStringExtra(AgendamentoRetiraActivity.EXTRA_AGENDAMENTO_TIPO);
            agendamentoPendentePaletizado = data.getStringExtra(AgendamentoRetiraActivity.EXTRA_AGENDAMENTO_PALETIZADO);
            agendamentoPendenteDescricao = data.getStringExtra(AgendamentoRetiraActivity.EXTRA_AGENDAMENTO_DESCRICAO);
        } else {
            limparAgendamentoPendente();
        }

        continuarFluxoFinalizacao();
    }

    private void abrirAgendamentoAntesDaFinalizacao() {
        agendamentoPreFinalizacaoEmAndamento = true;
        Intent intentAgendamento = new Intent(PopupPedFechamento.this, AgendamentoRetiraActivity.class);
        intentAgendamento.putExtra("pedido", pedido);
        intentAgendamento.putExtra(AgendamentoRetiraActivity.EXTRA_RETORNAR_RESULTADO, true);
        startActivityForResult(intentAgendamento, REQUEST_CODE_AGENDAMENTO_FINALIZACAO);
    }

    private void continuarFluxoFinalizacao() {
        if (edtFrete.getText().toString().equals("")) {
            edtFrete.setText("0.00");
        }
        if (isSaving) {
            return;
        }
        isSaving = true;
        try { btn_finalizar.setEnabled(false); } catch (Exception ignore) {}
        logDiagnosticoPromocaoAPrazo(
                "antes_carregaPedido",
                pedido,
                condicao_selecionada,
                obterDescricaoCondicaoAtual()
        );
        carregaPedido();
    }

    public void ConsultarCombobox() {

        dialog = new ProgressDialog(PopupPedFechamento.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {


                    if (FikonVendasClient.ativo()) {
                        // Condições vêm da Fikon; mídias e especificadores ainda são do
                        // sistema antigo e não podem impedir o carregamento das condições.
                        mList = helperLogin.getListaCondicoes(Integer.parseInt(usuarioLogado.get("codfil")));
                        try {
                            mListMidias = helper.GetMidias();
                        } catch (Exception e) {
                            Log.w("FikonPedido", "Mídias indisponíveis", e);
                        }
                        try {
                            mListArquitetos = helperLogin.GetArquitetos();
                        } catch (Exception e) {
                            Log.w("FikonPedido", "Especificadores indisponíveis", e);
                        }
                        if (mListMidias == null) {
                            mListMidias = new ArrayList<>();
                        }
                        if (mListArquitetos == null) {
                            mListArquitetos = new ArrayList<>();
                        }
                    } else {
                        mListMidias = helper.GetMidias();
                        mListArquitetos = helperLogin.GetArquitetos();
                        mList = helperLogin.getListaCondicoes(Integer.parseInt(usuarioLogado.get("codfil")));
                    }


                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            for (Midias m : mListMidias) {
                                listaMidias.add(m.getDescricao());
                            }

                            //listaArq.add("Retirar Especificador");
                            for (Arquiteto a : mListArquitetos) {

                                listaArq.add(a.getNomearq());


                            }

                            for (CondPagamento c : mList) {
                                listaCond.add(c.toString());


                            }

                            if(pedido.getCodArq()>0){
                                edtArquiteto.setText(pedido.getArquiteto());
                            }

                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupPedFechamento.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            }).show();
                        }
                    });
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupPedFechamento.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            }).show();
                        }
                    });
                } finally {
                    dialog.dismiss();


                }
            }


        }.start();


    }

    private void carregaPedido() {



        if (edtFrete.getText().equals("")) {
            edtFrete.setText("0.00");
        }
        pedido.setVlFrete(Double.parseDouble(edtFrete.getText().toString().replace(",",".")));



        if (pedido.getCodvendr() > 0) {
            pedido.setCodfil((pedido.getCodfil()));
            pedido.setCodvendr((pedido.getCodvendr()));
            pedido.setFilial(pedido.getFilial());
            pedido.setConsultor(pedido.getConsultor());
        } else {
            pedido.setCodfil(Integer.parseInt(usuarioLogado.get("codfil")));
            pedido.setCodvendr(Integer.parseInt(usuarioLogado.get("codigo")));
            pedido.setFilial(usuarioLogado.get("filial"));
            pedido.setConsultor(usuarioLogado.get("nome"));
        }

        pedido.setTpped(pedido.getItens().get(0).getTpped());

        if(planoEscolhidoCredz!=null && planoEscolhidoCredz.getDescription().equals("TURBINADO")) {

            Financeiro fin = new Financeiro();

            for (int i = 0; i < FinanceiroAdapter.mList.size(); i++) {

                if (FinanceiroAdapter.mList.get(i).getEvento().equals("CREDZ TURBINADO")) {

                    fin = FinanceiroAdapter.mList.get(i);
                    FinanceiroAdapter.mList.remove(i);

                }

            }

            fin.setCodEve(141);
            fin.setEvento("TURBINADO");
            fin.setQtprc(1);
            fin.setValor(planoEscolhidoCredz.getAmount().getBasePurchase());


        }

        pedido.setFinanceiro(FinanceiroAdapter.mList);

        // Verificar se existe PIX LINK na lista de formas de pagamento
        boolean temPixLink = false;
        for (int i = 0; i < FinanceiroAdapter.mList.size(); i++) {
            if (FinanceiroAdapter.mList.get(i).getCodEve() == 142) {
                temPixLink = true;
                break;
            }
        }

        // PIX será gerado automaticamente no fluxo de salvamento do pedido
        // Removida chamada duplicada para gerarPixAutomatico() aqui

        sincronizarFlagMotorNoPedido();
        boolean pedidoDispensaLiberacaoComercial = pedidoDispensaLiberacaoPorP2OuMotor();
        pedido.setCodStatus(3);

        // Se for fluxo de gerar links, ignora validações de desconto
        if (!ignorarValidacaoDesconto) {
            politicaDesconto = ConsultarPoliticaDesconto();

            if(politicaDesconto == null){
                new androidx.appcompat.app.AlertDialog.Builder(PopupPedFechamento.this)
                        .setTitle("Erro na Politica de Desconto")
                        .setMessage("Erro ao consultar a política de desconto para o pedido. Verifique sua conexão e tente novamente mais tarde.")
                        .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
                return;
            }

            if(politicaDesconto.getId()!=null){

                if(politicaDesconto.getPermitido()){
                    pedido.setCodStatus(3);
                    Toast.makeText(PopupPedFechamento.this, politicaDesconto.getObservacao().replaceAll("</br>","\n"), Toast.LENGTH_LONG).show();
                }
                else{
                    if (!pedidoDispensaLiberacaoComercial) {
                        pedido.setCodStatus(1);

                        new androidx.appcompat.app.AlertDialog.Builder(PopupPedFechamento.this)
                                .setIcon(R.drawable.ic_baseline_warning_30)
                                .setTitle("Pedido" + ( pedido.getNumpedven() > 0 ? "" : " " + (long)pedido.getNumpedven() ) + " Será Salvo\nFalta Liberação Comercial")
                                .setMessage(politicaDesconto.getObservacao().replaceAll("</br>","\n"))
                                .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        atualizaTotais(pedido.getItens());
                                        ConsultaEstqoue(pedido.getItens());
                                        dialog.dismiss();

                                        // Exibir alerta sobre links de pagamento
                                        exibirAlertaLinksPagemento();
                                    }
                                })
                                .create().show();

                        return;
                    }

                }

            }
        }

        if(pedido.getCodStatus()==3) {

            for (int i = 0; i < pedido.getFinanceiro().size(); i++) {

                if (pedido.getFinanceiro().get(i).getFlautcre().equals("S")) {

                    pedido.setCodStatus(2);


                }


                if (pedido.getStatusCredito() == null && pedido.getFinanceiro().get(i).getForma() == 15) {
                    pedido.setCodStatus(2);
                }


            }

            if (pedido.getStatusCredito() != null && pedido.getStatusCredito().equals("TEM SALDO")) {
                pedido.setCodStatus(3);
            } else if (pedido.getStatusCredito() != null && pedido.getStatusCredito().equals("SEM SALDO")) {
                pedido.setCodStatus(2);
            }

            for (int i = 0; i < pedido.getItens().size(); i++) {

                if (pedido.getItens().get(i).getCodFilTransf() > 0) {

                    pedido.setTransferencia(pedido.getItens().get(i).getCodFilTransf());

                }

                // Se não for fluxo de gerar links, aplica validações de desconto por item
                if (!ignorarValidacaoDesconto) {
                    if (politicaDesconto.getId() == null || politicaDesconto.getPercentualDescontoItem() == null) {

                        if (BigDecimal.valueOf(pedido.getItens().get(i).getDpi()).setScale(2, RoundingMode.HALF_UP).doubleValue() > Float.parseFloat(usuarioLogado.get("descontopermitido")) && !itemEhDescontoP2OuMotorPromocional(pedido.getItens().get(i))) {
                            pedido.setCodStatus(1);
                        }

                    }
                }

            }

            // Se não for fluxo de gerar links, aplica validações de margem
            if (!ignorarValidacaoDesconto) {
                if (politicaDesconto.getId() == null || politicaDesconto.getPercentualMargem() == null) {

                    if (pedido.getVlDesconto() > 0 && pedido.getPmargRent() <= Float.parseFloat(usuarioLogado.get("percmargem")) && !pedidoDispensaLiberacaoComercial) {
                        pedido.setCodStatus(1);
                    }

                }
            }

        }

        atualizaTotais(pedido.getItens());

        ConsultaEstqoue(pedido.getItens());

    }

    private void exibirAlertaLinksPagemento() {
        // Exibir alerta sobre links de pagamento SEMPRE que o status for 1
        Log.d("AlertaLinksDebug", "Executando exibirAlertaLinksPagemento()");
        new androidx.appcompat.app.AlertDialog.Builder(PopupPedFechamento.this)
                .setIcon(R.drawable.ic_card_info)
                .setTitle("Pedido necessita Liberação Comercial!")
                .setMessage("Este pedido possui especificações que precisam de aprovação comercial. Os links de pagamento só poderão ser gerados após aprovação do pedido. Aguarde aprovação.")
                .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    private void exibirAlertaLinksPagementoComCallback(final Pedidos pedidoSave) {
        // Exibir alerta sobre links de pagamento e só finalizar após o usuário clicar em "Entendi"
        Log.d("AlertaLinksDebug", "Executando exibirAlertaLinksPagementoComCallback()");
        new androidx.appcompat.app.AlertDialog.Builder(PopupPedFechamento.this)
                .setIcon(R.drawable.ic_card_info)
                .setTitle("Pedido necessita Liberação Comercial!")
                .setMessage("Este pedido possui especificações que precisam de aprovação comercial. Os links de pagamento só poderão ser gerados após aprovação do pedido. Aguarde aprovação.")
                .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // Só finalizar o processo após o usuário clicar em "Entendi"
                        finalizarProcesso(pedidoSave);
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    private void finalizarProcesso(final Pedidos pedidoSave) {
        ProdutoFragments.orcamento = null;
        ConsultaActivity.orcamento = null;
        ProdutoFragments.pedido = null;
        ConsultaActivity.pedido = null;
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pedidos").child(usuarioLogado.get("codigo"));
        myRef.removeValue();
        runOnUiThread(() -> {
            dialog = new ProgressDialog(PopupPedFechamento.this);
            dialog.setMessage("Atualizando pedido finalizado...");
            dialog.setCancelable(false);
            dialog.show();
        });

        Pedidos pedidoBaseClonado = clonarPedido(pedidoSave);
        final Pedidos pedidoBaseAtualizado = pedidoBaseClonado != null ? pedidoBaseClonado : pedidoSave;
        pedidoBaseAtualizado.setCodStatus(10);
        // #region debug-point A:finalizar-processo
        reportDebugEvent("A", "PopupPedFechamento.finalizarProcesso",
                "[DEBUG] snapshot local antes da sincronizacao final",
                buildPedidoDebugSnapshot("finalizarProcesso-local", pedidoBaseAtualizado));
        // #endregion
        Pedidos_LancamentoActivity.pedido = pedidoBaseAtualizado;

        new Thread(() -> {
            Pedidos pedidoAtualizado = sincronizarPedidoFinalizado(pedidoBaseAtualizado);
            if (pedidoAtualizado == null) {
                pedidoAtualizado = pedidoBaseAtualizado;
            }

            pedidoAtualizado.setCodStatus(10);
            final Pedidos pedidoFinalTela = pedidoAtualizado;

            runOnUiThread(() -> {
                Pedidos_LancamentoActivity.pedido = pedidoFinalTela;
                Pedidos_LancamentoActivity.alteracaoFrete = true;

                try {
                    Pedidos_LancamentoActivity.pedido.setIndicadoresVendaPerfeita(pedido.getIndicadoresVendaPerfeita());
                    Pedidos_LancamentoActivity.pedido.setMetasVendaPerfeita(pedido.getMetasVendaPerfeita());
                } catch (Exception ignore) {
                }

                try {
                    if (Pedidos_LancamentoActivity.btn_adcionar != null) {
                        Pedidos_LancamentoActivity.btn_adcionar.setEnabled(false);
                        Pedidos_LancamentoActivity.btn_adcionar.setBackgroundResource(R.drawable.backgroundbuttondisabled);
                    }
                    if (Pedidos_LancamentoActivity.btn_finalizar != null) {
                        Pedidos_LancamentoActivity.btn_finalizar.setEnabled(false);
                        Pedidos_LancamentoActivity.btn_finalizar.setBackgroundResource(R.drawable.backgroundbuttondisabled);
                    }
                } catch (Exception ignore) {
                }

                try {
                    Pedidos_LancamentoActivity.AtualizarLayoutVendaPerfeita();
                } catch (Exception ignore) {
                }

                try {
                    Pedidos_LancamentoActivity.atualizaTotais(Pedidos_LancamentoActivity.pedido.getItens());
                } catch (Exception ignore) {
                }

                try {
                    Pedidos_LancamentoActivity.notificarPedidoAtualizadoAposFinalizacao();
                } catch (Exception ignore) {
                }

                try {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_PEDIDO_RESULTADO, pedidoFinalTela);
                    setResult(RESULT_OK, resultIntent);
                } catch (Exception ignore) {
                }

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                finish();
            });
        }).start();
    }

    private Pedidos sincronizarPedidoFinalizado(Pedidos pedidoBase) {
        Pedidos pedidoAtualizado = clonarPedido(pedidoBase);
        if (pedidoAtualizado == null) {
            pedidoAtualizado = pedidoBase;
        }

        try {
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(br.com.acalfortaleza.fikonmais.service.ApiGemco.BASE_URL)
                    .client(client)
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build();
            br.com.acalfortaleza.fikonmais.service.ApiGemco api = retrofit.create(br.com.acalfortaleza.fikonmais.service.ApiGemco.class);
            String token = "Bearer " + br.com.acalfortaleza.fikonmais.service.ApiGemco.token;

            retrofit2.Response<java.util.List<br.com.acalfortaleza.fikonmais.models.Pedidos>> respPedido =
                    api.GetPedidosConsulta(String.valueOf((long) pedidoBase.getNumpedven()), "CODIGO", token).execute();
            if (respPedido.isSuccessful() && respPedido.body() != null && !respPedido.body().isEmpty() && respPedido.body().get(0) != null) {
                Pedidos pedidoServidor = respPedido.body().get(0);
                recalcularTotalPayloadPedidoApi(pedidoServidor);
                // #region debug-point B:pedido-servidor
                reportDebugEvent("B", "PopupPedFechamento.sincronizarPedidoFinalizado",
                        "[DEBUG] snapshot recebido do servidor antes da mesclagem",
                        buildPedidoDebugSnapshot("sincronizar-servidor", pedidoServidor));
                // #endregion

                if (pedidoServidor.getItens() == null || pedidoServidor.getItens().isEmpty()) {
                    pedidoServidor.setItens(pedidoAtualizado.getItens());
                } else {
                    copiarMarcadoresPromocionaisItens(pedidoServidor, pedidoAtualizado);
                }
                if (pedidoServidor.getCliente() == null) {
                    pedidoServidor.setCliente(pedidoAtualizado.getCliente());
                }
                if (pedidoServidor.getCondPagamento() == null) {
                    pedidoServidor.setCondPagamento(pedidoAtualizado.getCondPagamento());
                }
                if (pedidoServidor.getIndicadoresVendaPerfeita() == null) {
                    pedidoServidor.setIndicadoresVendaPerfeita(pedidoAtualizado.getIndicadoresVendaPerfeita());
                }
                if (pedidoServidor.getMetasVendaPerfeita() == null) {
                    pedidoServidor.setMetasVendaPerfeita(pedidoAtualizado.getMetasVendaPerfeita());
                }
                if (pedidoServidor.getServicosAdicionaisIds() == null || pedidoServidor.getServicosAdicionaisIds().isEmpty()) {
                    pedidoServidor.setServicosAdicionaisIds(pedidoAtualizado.getServicosAdicionaisIds());
                }
                if (pedidoServidor.getServicosAdicionaisNomes() == null || pedidoServidor.getServicosAdicionaisNomes().isEmpty()) {
                    pedidoServidor.setServicosAdicionaisNomes(pedidoAtualizado.getServicosAdicionaisNomes());
                }
                if (pedidoServidor.getVlServicosAdicionais() <= 0) {
                    pedidoServidor.setVlServicosAdicionais(pedidoAtualizado.getVlServicosAdicionais());
                }

                pedidoAtualizado = pedidoServidor;
                recalcularTotalPayloadPedidoApi(pedidoAtualizado);
            }

            retrofit2.Response<br.com.acalfortaleza.fikonmais.service.FreteServicoResponse> respServ =
                    api.ConsultarFreteServicoSelecionado((long) pedidoBase.getNumpedven(), token).execute();
            if (respServ.isSuccessful() && respServ.body() != null) {
                if (respServ.body().getValorTotal() != null) {
                    pedidoAtualizado.setVlServicosAdicionais(respServ.body().getValorTotal().doubleValue());
                }
                if (respServ.body().getIdsSelecionados() != null) {
                    pedidoAtualizado.setServicosAdicionaisIds(respServ.body().getIdsSelecionados());
                }
            }
            recalcularTotalPayloadPedidoApi(pedidoAtualizado);

            retrofit2.Response<java.util.List<br.com.acalfortaleza.fikonmais.models.Financeiro>> respFin =
                    api.GetFinanceiroPedido(pedidoBase.getNumpedven(), pedidoBase.getCodfil(), token).execute();
            if (respFin.isSuccessful() && respFin.body() != null) {
                pedidoAtualizado.setFinanceiro(respFin.body());
            }
            recalcularTotalPayloadPedidoApi(pedidoAtualizado);

            if (pedidoAtualizado.getCondPagamento() == null) {
                if (pedidoAtualizado.getFinanceiro() != null && !pedidoAtualizado.getFinanceiro().isEmpty()) {
                    Financeiro financeiroAtual = pedidoAtualizado.getFinanceiro().get(0);
                    if (financeiroAtual != null) {
                        CondPagamento condPagamentoAtual = new CondPagamento();
                        condPagamentoAtual.setCondpgto(financeiroAtual.getCondpgto());
                        condPagamentoAtual.setDescricao(financeiroAtual.getCondicao());
                        pedidoAtualizado.setCondPagamento(condPagamentoAtual);
                    }
                } else if (pedidoBase.getCondPagamento() != null) {
                    pedidoAtualizado.setCondPagamento(pedidoBase.getCondPagamento());
                }
            }
        } catch (Exception ex) {
            Log.e("FinalizacaoPedidoDebug", "Falha ao sincronizar pedido finalizado antes de voltar para a tela.", ex);
        }

        if (!pedidoTemItensEntrega(pedidoAtualizado)) {
            pedidoAtualizado.setVlFrete(0.0d);
            pedidoAtualizado.setVlServicosAdicionais(0.0d);
            pedidoAtualizado.setServicosAdicionaisIds(new java.util.ArrayList<>());
            pedidoAtualizado.setServicosAdicionaisNomes(new java.util.ArrayList<>());
        }
        recalcularTotalPayloadPedidoApi(pedidoAtualizado);
        // #region debug-point C:pedido-mesclado
        reportDebugEvent("C", "PopupPedFechamento.sincronizarPedidoFinalizado",
                "[DEBUG] snapshot final apos sincronizacao e ajustes locais",
                buildPedidoDebugSnapshot("sincronizar-final", pedidoAtualizado));
        // #endregion
        salvarCacheMarcadoresPromocionais(pedidoAtualizado);
        return pedidoAtualizado;
    }

    private void copiarMarcadoresPromocionaisItens(Pedidos pedidoDestino, Pedidos pedidoOrigem) {
        if (pedidoDestino == null || pedidoDestino.getItens() == null
                || pedidoOrigem == null || pedidoOrigem.getItens() == null) {
            return;
        }

        for (ItemPedidos itemDestino : pedidoDestino.getItens()) {
            ItemPedidos itemOrigem = localizarItemCorrespondente(pedidoOrigem.getItens(), itemDestino);
            if (itemDestino == null || itemOrigem == null) {
                continue;
            }

            itemDestino.setIsMotor(itemOrigem.getIsMotor());
            itemDestino.setCodigoPromo(itemOrigem.getCodigoPromo());
            itemDestino.setNomePromo(itemOrigem.getNomePromo());
            itemDestino.setMensagemPromo(itemOrigem.getMensagemPromo());
            itemDestino.setVldescPromo(itemOrigem.getVldescPromo());

            if (itemOrigem.getPrecoPrimario() > 0d) {
                itemDestino.setPrecoPrimario(itemOrigem.getPrecoPrimario());
            }

            itemDestino.setPrecoUnit(itemOrigem.getPrecoUnit());
            itemDestino.setVlDescItem(itemOrigem.getVlDescItem());
            itemDestino.setDpi(itemOrigem.getDpi());

            if (isItemPromocional(itemOrigem)) {
                // Manter regras específicas promocionais se houver outras propriedades
            }

            if ((itemDestino.getSituacao() == null || itemDestino.getSituacao().trim().isEmpty())
                    && itemOrigem.getSituacao() != null) {
                itemDestino.setSituacao(itemOrigem.getSituacao());
            }
        }
    }

    private ItemPedidos localizarItemCorrespondente(List<ItemPedidos> itensOrigem, ItemPedidos itemDestino) {
        if (itensOrigem == null || itemDestino == null) {
            return null;
        }

        for (ItemPedidos itemOrigem : itensOrigem) {
            if (itemOrigem == null) {
                continue;
            }

            boolean mesmoCodigo = itemOrigem.getCoditprod() == itemDestino.getCoditprod();
            boolean mesmaQuantidade = Math.abs(itemOrigem.getQtcomp() - itemDestino.getQtcomp()) < 0.0001d;
            boolean mesmoItem = itemOrigem.getItem() > 0 && itemDestino.getItem() > 0 && itemOrigem.getItem() == itemDestino.getItem();

            if (mesmoCodigo && (mesmoItem || mesmaQuantidade)) {
                return itemOrigem;
            }
        }

        return null;
    }

    private void salvarCacheMarcadoresPromocionais(Pedidos pedidoAtual) {
        if (pedidoAtual == null || pedidoAtual.getNumpedven() <= 0 || pedidoAtual.getItens() == null) {
            return;
        }

        Pedidos pedidoCache = normalizarPedidoBaseAvista(pedidoAtual);
        if (pedidoCache == null) {
            pedidoCache = pedidoAtual;
        }

        android.content.SharedPreferences preferences = getApplicationContext().getSharedPreferences("pedidoPromocaoPrazo", android.content.Context.MODE_PRIVATE);
        preferences.edit()
                .putString("itens_" + (long) pedidoAtual.getNumpedven(), new Gson().toJson(pedidoCache.getItens()))
                .apply();
    }

    private boolean pedidoTemItensEntrega(Pedidos pedidoRef) {
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return false;
        }

        for (ItemPedidos item : pedidoRef.getItens()) {
            if (item != null && "E".equalsIgnoreCase(item.getTpped())) {
                return true;
            }
        }

        return false;
    }

    private boolean deveAbrirAgendamentoAntesDaFinalizacao(Pedidos pedidoAtual) {
        if (pedidoAtual == null || pedidoAtual.getItens() == null) {
            return false;
        }

        for (ItemPedidos item : pedidoAtual.getItens()) {
            if (item == null) {
                continue;
            }

            boolean isRetiraPosterior = "P".equals(item.getTpped());
            boolean isPeCicero = item.getCodFilOrig() == 5 || "Pe.Cicero".equalsIgnoreCase(item.getFilOrig());

            if (!isRetiraPosterior || !isPeCicero) {
                continue;
            }

            try {
                if (Integer.parseInt(item.getStatus()) < 6) {
                    return true;
                }
            } catch (Exception ignored) {
                return true;
            }
        }

        return false;
    }

    private void limparAgendamentoPendente() {
        agendamentoPendenteData = null;
        agendamentoPendenteHora = null;
        agendamentoPendenteTipo = null;
        agendamentoPendentePaletizado = null;
        agendamentoPendenteDescricao = null;
    }

    private boolean temAgendamentoPendenteSelecionado() {
        return agendamentoPendenteData != null && !agendamentoPendenteData.trim().isEmpty()
                && agendamentoPendenteHora != null && !agendamentoPendenteHora.trim().isEmpty();
    }

    private void aplicarAgendamentoPendenteSeNecessario(Pedidos pedidoSalvo, String retornoPedido) throws IOException, XmlPullParserException, ParseException {
        if (!temAgendamentoPendenteSelecionado() || pedidoSalvo == null || pedidoSalvo.getCliente() == null) {
            return;
        }

        String numPedido = retornoPedido;
        try {
            numPedido = retornoPedido.split("\\.")[0].trim();
        } catch (Exception ignore) {
        }

        double pesoAgendamento = pedidoSalvo.getPeso() > 0 ? pedidoSalvo.getPeso() : calcularPesoAgendamento(pedidoSalvo.getItens());
        String retornoAgendamento = helper.SetAgendamentoRetira(
                numPedido,
                String.valueOf(pedidoSalvo.getCliente().getCodcli()),
                agendamentoPendenteData,
                agendamentoPendenteHora,
                String.valueOf(pesoAgendamento),
                "",
                agendamentoPendenteTipo,
                pedidoSalvo.getCodfil(),
                pedidoSalvo.getCodvendr(),
                "AGENDADO",
                usuarioLogado.get("nome"),
                pedidoSalvo.getCliente(),
                agendamentoPendenteDescricao,
                agendamentoPendentePaletizado == null ? "N" : agendamentoPendentePaletizado
        );

        Log.d("AgendamentoFinalizacaoDebug", "SetAgendamentoRetira retornou: " + retornoAgendamento + " | pedido=" + numPedido + " | descricao=" + agendamentoPendenteDescricao);

        if ("AGENDADO".equalsIgnoreCase(retornoAgendamento)) {
            pedidoSalvo.setAgendamento("Agendado: " + agendamentoPendenteDescricao);
        }

        limparAgendamentoPendente();
    }

    private double calcularPesoAgendamento(List<ItemPedidos> itens) {
        double peso = 0.0d;
        if (itens == null) {
            return peso;
        }

        for (ItemPedidos item : itens) {
            if (item == null) {
                continue;
            }
            if (item.getCodFilOrig() == 5 && "P".equals(item.getTpped())) {
                peso += (item.getPeso() * item.getQtcomp());
            }
        }

        return peso;
    }

    private boolean isUsuarioGerente() {
        if (usuarioLogado == null) {
            return false;
        }
        String isGerente = usuarioLogado.get("isgerente");
        return isGerente != null
                && ("SIM".equalsIgnoreCase(isGerente.trim())
                || "S".equalsIgnoreCase(isGerente.trim())
                || "TRUE".equalsIgnoreCase(isGerente.trim()));
    }

    private boolean temServicoEspecialDescontoFreteSelecionado() {
        java.util.List<Integer> idsSelecionados = br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed;
        if ((idsSelecionados == null || idsSelecionados.isEmpty()) && pedido != null) {
            idsSelecionados = pedido.getServicosAdicionaisIds();
        }
        if (idsSelecionados == null) {
            return false;
        }
        for (Integer id : idsSelecionados) {
            if (id != null && id == ID_SERVICO_DESCONTO_FRETE_GERENTE) {
                return true;
            }
        }
        return false;
    }

    private boolean podeAplicarDescontoFreteEspecial() {
        return isUsuarioGerente() && temServicoEspecialDescontoFreteSelecionado();
    }

    private double getPercentualMaximoDescontoFrete() {
        double percentualBase = 0.0d;
        try {
            percentualBase = Double.parseDouble(usuarioLogado.get("descontofrete"));
        } catch (Exception ignore) {
        }
        if (podeAplicarDescontoFreteEspecial()) {
            return Math.max(percentualBase, 20.0d);
        }
        return percentualBase;
    }

    private void atualizarPermissaoEdicaoFrete() {
        if (!isCalculoFreteConcluido || edtFrete == null) {
            if (edtFrete != null) edtFrete.setEnabled(false);
            if (edtServicosFrete != null) edtServicosFrete.setEnabled(false);
            return;
        }
        boolean possuiEntrega = pedidoTemItensEntrega(pedido);
        edtFrete.setEnabled(possuiEntrega && getPercentualMaximoDescontoFrete() > 0.0d);
        if (edtServicosFrete != null) {
            edtServicosFrete.setEnabled(possuiEntrega && (switchRetira == null || !switchRetira.isChecked()));
        }
    }

    private void sincronizarRegraDescontoFreteEspecial() {
        boolean especialAtivoAgora = podeAplicarDescontoFreteEspecial();
        atualizarPermissaoEdicaoFrete();

        if (descontoFreteEspecialAtivo && !especialAtivoAgora) {
            try {
                double freteAtual = edtFrete != null && edtFrete.getText() != null && !edtFrete.getText().toString().trim().isEmpty()
                        ? Double.parseDouble(edtFrete.getText().toString().replace(",", "."))
                        : freteOrig;
                if (freteOrig > 0.0d && freteAtual < freteOrig) {
                    edtFrete.setText(df.format(freteOrig));
                    if (pedido != null) {
                        pedido.setVlFrete(freteOrig);
                        if (pedido.getItens() != null) {
                            atualizaTotais(pedido.getItens());
                        }
                    }
                }
            } catch (Exception ignore) {
            }
        }

        descontoFreteEspecialAtivo = especialAtivoAgora;
    }

    private String obterDescricaoCondicaoAtual() {
        try {
            if (pedido != null && pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
                Financeiro financeiro = pedido.getFinanceiro().get(0);
                if (financeiro != null && financeiro.getCondicao() != null && !financeiro.getCondicao().trim().isEmpty()) {
                    return financeiro.getCondicao();
                }
            }
        } catch (Exception ignore) {
        }

        try {
            if (edtCondpgto != null && edtCondpgto.getText() != null) {
                return edtCondpgto.getText().toString();
            }
        } catch (Exception ignore) {
        }

        return "";
    }

    private void sincronizarCondicaoAtualDoPedido() {
        try {
            if (pedido != null && pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
                Financeiro financeiro = pedido.getFinanceiro().get(0);
                if (financeiro != null) {
                    if (financeiro.getCondpgto() != null && !financeiro.getCondpgto().trim().isEmpty()) {
                        condicao_selecionada = financeiro.getCondpgto().trim();
                    }
                    if (financeiro.getCondicao() != null && !financeiro.getCondicao().trim().isEmpty()) {
                        edtCondpgto.setText(financeiro.getCondicao().trim());
                        return;
                    }
                }
            }
        } catch (Exception ignore) {
        }

        try {
            if (pedido != null && pedido.getCondPagamento() != null) {
                if (pedido.getCondPagamento().getCondpgto() != null && !pedido.getCondPagamento().getCondpgto().trim().isEmpty()) {
                    condicao_selecionada = pedido.getCondPagamento().getCondpgto().trim();
                }
                if (pedido.getCondPagamento().getDescricao() != null && !pedido.getCondPagamento().getDescricao().trim().isEmpty()) {
                    edtCondpgto.setText(pedido.getCondPagamento().getDescricao().trim());
                }
            }
        } catch (Exception ignore) {
        }
    }

    private boolean avisoDescontoAVistaExibido = false;

    private void tentarExibirAvisoDescontoAVistaAoAbrir() {
        // Ajustes por Preco2/P2 removidos.
    }

    private String montarListaItensDescontoAVista(Pedidos pedidoRef) {
        return "";
    }

    private void tentarExibirAvisoPromocionalAPrazoAoAbrir() {
        tentarExibirAvisoPromocionalAPrazoAoAbrir(null);
    }

    private void tentarExibirAvisoPromocionalAPrazoAoAbrir(Runnable onOkAction) {
        if (avisoPromocionalPrazoExibidoAoAbrir || pedido == null) {
            if (onOkAction != null) onOkAction.run();
            return;
        }

        if (!isCondicaoAPrazo(condicao_selecionada, obterDescricaoCondicaoAtual(), pedido)) {
            if (onOkAction != null) onOkAction.run();
            return;
        }

        String itensPromocionais = montarListaItensPromocionais(pedido);
        if (itensPromocionais.isEmpty()) {
            if (onOkAction != null) onOkAction.run();
            return;
        }

        avisoPromocionalPrazoExibidoAoAbrir = true;
        new AlertDialog.Builder(PopupPedFechamento.this)
                .setTitle("Atenção")
                .setMessage("Os itens promocionados terão reajuste por conta da condição A PRAZO.\n\n" + itensPromocionais)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    if (onOkAction != null) {
                        onOkAction.run();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private BigDecimal calcularTotalItemConsiderandoPromocaoPrazo(ItemPedidos item) {
        String condicaoCodigoAtual = "N".equals(condicao_selecionada) ? "" : condicao_selecionada;
        String condicaoDescricaoAtual = obterDescricaoCondicaoAtual();

        if ((condicaoCodigoAtual == null || condicaoCodigoAtual.trim().isEmpty())
                && (condicaoDescricaoAtual == null || condicaoDescricaoAtual.trim().isEmpty())) {
            return BigDecimal.valueOf(item.getPrecoUnit())
                    .multiply(BigDecimal.valueOf(item.getQtcomp()))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        return calcularTotalItemConsiderandoPromocaoPrazo(item, condicaoCodigoAtual, condicaoDescricaoAtual);
    }

    private BigDecimal calcularTotalItemConsiderandoPromocaoPrazo(ItemPedidos item, String condicaoCodigo, String condicaoDescricao) {
        BigDecimal precoLiquidoUnitario = calcularPrecoUnitarioLiquidoParaCondicao(item, condicaoCodigo, condicaoDescricao);
        return precoLiquidoUnitario.multiply(BigDecimal.valueOf(item.getQtcomp())).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularPrecoUnitarioLiquidoPromocionalPrazo(ItemPedidos item) {
        BigDecimal precoLiquidoVista = getPrecoUnitarioBrutoBaseVista(item)
                .subtract(getDescontoVistaOriginalPromocional(item))
                .max(BigDecimal.ZERO);
        return precoLiquidoVista.compareTo(BigDecimal.ZERO) > 0
                ? precoLiquidoVista.divide(BigDecimal.valueOf(0.95d), 6, RoundingMode.HALF_EVEN)
                : BigDecimal.ZERO.setScale(6, RoundingMode.HALF_EVEN);
    }


    private void normalizarConsistenciaPrecoDescItemPromocional(ItemPedidos item) {
        if (item == null) {
            return;
        }

        if (isMotorPromocional(item.getIsMotor())) {
            if (item.getPrecoPrimario() <= 0d && item.getPrecoUnit() > 0d) {
                item.setPrecoPrimario(
                        BigDecimal.valueOf(item.getPrecoUnit())
                                .setScale(2, RoundingMode.HALF_EVEN)
                                .doubleValue()
                );
            }
            BigDecimal precoBase = BigDecimal.valueOf(item.getPrecoPrimario() > 0d ? item.getPrecoPrimario() : item.getPrecoUnit())
                    .setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal desconto = BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem()))
                    .setScale(6, RoundingMode.HALF_EVEN);
            if (precoBase.compareTo(BigDecimal.ZERO) > 0) {
                item.setDpi(desconto.multiply(BigDecimal.valueOf(100.0d))
                        .divide(precoBase, 6, RoundingMode.HALF_EVEN)
                        .doubleValue());
            } else {
                item.setDpi(0.0d);
            }
            return; // para isMotor, nao roda mais nada.
        }

        if (!isItemPromocional(item)) {
            return;
        }
        BigDecimal pu = BigDecimal.valueOf(item.getPrecoUnit()).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal pp = BigDecimal.valueOf(item.getPrecoPrimario()).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal desc = BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem())).setScale(6, RoundingMode.HALF_EVEN);
        BigDecimal TOL = new BigDecimal("0.02");

        // Se PrecoPrimario <= 0 mas PU > 0, inicializa PP = PU (antes de qualquer coisa)
        if (pp.compareTo(BigDecimal.ZERO) <= 0 && pu.compareTo(BigDecimal.ZERO) > 0) {
            pp = pu;
            item.setPrecoPrimario(pp.doubleValue());
        }

        BigDecimal liqPorPU = pu.subtract(desc).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal liqPorPP = pp.subtract(desc).setScale(2, RoundingMode.HALF_EVEN);

        // Regra 1: Iguala liquidos se PP != PU
        if (pp.compareTo(pu) != 0 && liqPorPU.subtract(liqPorPP).abs().compareTo(TOL) > 0) {
            BigDecimal novoVlDesc = pp.subtract(liqPorPU).max(BigDecimal.ZERO);
            item.setVlDescItem(novoVlDesc.setScale(6, RoundingMode.HALF_EVEN).doubleValue());
            desc = novoVlDesc;
        }

        boolean temMarcaFikon = (item.getCodigoPromo() != null && !item.getCodigoPromo().trim().isEmpty())
                || (item.getNomePromo() != null && !item.getNomePromo().trim().isEmpty())
                || (item.getMensagemPromo() != null && !item.getMensagemPromo().trim().isEmpty())
                || "S".equalsIgnoreCase(item.getIsMotor());
        BigDecimal liqAgora = pp.subtract(desc).setScale(2, RoundingMode.HALF_EVEN);

        // Regra 2: PU == PP mas Desc > 0 (desconto aplicado em cima de preco ja promocional!)
        // Ex: PP=79.90, PU=79.90, Desc=4.00 → liquido 75.90 errado. Zeramos ou limitamos Desc.
        if (temMarcaFikon && !isMotorPromocional(item.getIsMotor()) && pu.compareTo(pp) == 0 && desc.compareTo(BigDecimal.ZERO) > 0) {
            if (item.getVldescPromo() > 0.0d) {
                item.setVlDescItem(0.0d);
            } else {
                BigDecimal precoPromocionalMinimo = pu.min(liqAgora.add(desc));
                BigDecimal descMaxPermitido = pp.subtract(precoPromocionalMinimo).max(BigDecimal.ZERO);
                if (desc.compareTo(descMaxPermitido) > 0) {
                    item.setVlDescItem(descMaxPermitido.setScale(6, RoundingMode.HALF_EVEN).doubleValue());
                }
            }
        }

        // Regra 3: Ajustar DPI (percentual) coerente com VlDescItem novo
        if (pp.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal vlDescNovo = BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem()));
            double novoDpi = vlDescNovo.multiply(BigDecimal.valueOf(100.0d))
                    .divide(pp, 6, RoundingMode.HALF_EVEN)
                    .doubleValue();
            item.setDpi(novoDpi);
        }
    }

    private BigDecimal calcularPrecoUnitarioBrutoPromocionalPrazo(ItemPedidos item) {
        BigDecimal precoBaseVista = getPrecoUnitarioBrutoBaseVista(item);
        if (isMotorPromocional(item != null ? item.getIsMotor() : null)) {
            return precoBaseVista.setScale(6, RoundingMode.HALF_EVEN);
        }
        return calcularPrecoUnitarioLiquidoPromocionalPrazo(item)
                .add(getDescontoVistaOriginalPromocional(item))
                .setScale(6, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calcularPrecoUnitarioLiquidoParaCondicao(ItemPedidos item, String condicaoCodigo, String condicaoDescricao) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_EVEN);
        }

        if (isCondicaoAPrazo(condicaoCodigo, condicaoDescricao) && itemTemReajustePromocionalPrazo(item)) {
            return calcularPrecoUnitarioLiquidoPromocionalPrazo(item);
        }

        BigDecimal precoBaseVista = getPrecoUnitarioBrutoBaseVista(item);
        BigDecimal desconto = itemTemReajustePromocionalPrazo(item)
                ? getDescontoVistaOriginalPromocional(item)
                : BigDecimal.valueOf(item.getVlDescItem());
        return precoBaseVista.subtract(desconto).max(BigDecimal.ZERO).setScale(6, RoundingMode.HALF_EVEN);
    }

    private Pedidos prepararPedidoParaEnvioComPrecoPromocional(Pedidos pedidoOriginal) {
        if (pedidoOriginal == null) {
            return null;
        }


        if (pedidoOriginal.getItens() != null) {
            for (ItemPedidos itemOriginalAntesClone : pedidoOriginal.getItens()) {
                normalizarConsistenciaPrecoDescItemPromocional(itemOriginalAntesClone);
            }
        }

        Pedidos pedidoEnvio = new Gson().fromJson(new Gson().toJson(pedidoOriginal), Pedidos.class);
        if (pedidoEnvio == null || pedidoEnvio.getItens() == null) {
            return pedidoOriginal;
        }

        boolean condicaoAPrazo = isCondicaoAtualAPrazo(pedidoOriginal);
        java.util.List<ItemPedidos> itensOriginais = pedidoOriginal.getItens();
        // Rodar consistencia TAMBEM no clone (depois de deserializar Gson)
        for (ItemPedidos itemClone : pedidoEnvio.getItens()) {
            normalizarConsistenciaPrecoDescItemPromocional(itemClone);
        }
        for (int iItem = 0; iItem < pedidoEnvio.getItens().size(); iItem++) {
            ItemPedidos item = pedidoEnvio.getItens().get(iItem);
            if (item == null) {
                continue;
            }
            ItemPedidos itemOriginal = (itensOriginais != null && iItem < itensOriginais.size())
                    ? itensOriginais.get(iItem)
                    : null;

            BigDecimal precoPrimarioBase;
            if (item.getPrecoPrimario() > 0.0d) {
                precoPrimarioBase = BigDecimal.valueOf(item.getPrecoPrimario());
            } else if (itemOriginal != null && itemOriginal.getPrecoPrimario() > 0.0d) {
                precoPrimarioBase = BigDecimal.valueOf(itemOriginal.getPrecoPrimario());
            } else {
                precoPrimarioBase = getPrecoUnitarioBrutoBaseVista(item);
            }
            precoPrimarioBase = precoPrimarioBase.setScale(2, RoundingMode.HALF_UP);

            if (item.getPrecoPrimario() <= 0.0d) {
                item.setPrecoPrimario(precoPrimarioBase.doubleValue());
            }
            if (itemOriginal != null && itemOriginal.getPrecoPrimario() <= 0.0d) {
                itemOriginal.setPrecoPrimario(precoPrimarioBase.doubleValue());
            }

            BigDecimal precoUnitarioEnvio = getPrecoUnitarioItemEnvioApi(item, condicaoAPrazo);
            BigDecimal descontoUnitarioEnvio = getDescontoUnitarioEnvioApi(item, condicaoAPrazo);
            BigDecimal precoUnitarioEnvioDuasCasas = precoUnitarioEnvio.setScale(2, RoundingMode.HALF_UP);
            BigDecimal descontoUnitarioEnvioPayload = ajustarDescontoUnitarioEnvioParaTotalAlvo(
                    item,
                    precoUnitarioEnvioDuasCasas,
                    descontoUnitarioEnvio,
                    condicaoAPrazo
            );
            BigDecimal precoUnitarioEnvioPayload = ajustarPrecoUnitarioEnvioParaTotalAlvo(
                    item,
                    precoUnitarioEnvioDuasCasas,
                    descontoUnitarioEnvioPayload,
                    condicaoAPrazo
            );

            item.setPrecoUnit(precoUnitarioEnvioDuasCasas.doubleValue());
            // PrecoPrimario (base vista) imutavel: NÃO atualiza mais aqui depois de inicializado
            item.setVlDescItem(descontoUnitarioEnvioPayload.doubleValue());
            if (precoPrimarioBase.compareTo(BigDecimal.ZERO) > 0) {
                double novoDpi = truncarDpiParaEnvio(
                        descontoUnitarioEnvioPayload
                                .multiply(BigDecimal.valueOf(100.0d))
                                .divide(precoPrimarioBase, 6, RoundingMode.HALF_EVEN)
                );
                item.setDpi(novoDpi);
            }


            if (itemOriginal != null) {
                itemOriginal.setPrecoUnit(precoUnitarioEnvioDuasCasas.doubleValue());
                itemOriginal.setVlDescItem(descontoUnitarioEnvioPayload.doubleValue());
                if (precoPrimarioBase.compareTo(BigDecimal.ZERO) > 0) {
                    double novoDpi = truncarDpiParaEnvio(
                            descontoUnitarioEnvioPayload
                                    .multiply(BigDecimal.valueOf(100.0d))
                                    .divide(precoPrimarioBase, 6, RoundingMode.HALF_EVEN)
                    );
                    itemOriginal.setDpi(novoDpi);
                }
                // IMUTABILIDADE PrecoPrimario novamente: SÓ seta se PrecoPrimario <= 0
                if (itemOriginal.getPrecoPrimario() <= 0.0d) {
                    itemOriginal.setPrecoPrimario(precoPrimarioBase.doubleValue());
                }
            }
        }

        return pedidoEnvio;
    }

    private BigDecimal ajustarPrecoUnitarioEnvioParaTotalAlvo(ItemPedidos item,
                                                              BigDecimal precoUnitarioEnvioDuasCasas,
                                                              BigDecimal descontoUnitarioEnvioPayload,
                                                              boolean condicaoAPrazo) {
        if (item == null) {
            return precoUnitarioEnvioDuasCasas.setScale(6, RoundingMode.HALF_UP);
        }

        BigDecimal qtde = BigDecimal.valueOf(Math.max(0.0d, item.getQtcomp())).setScale(6, RoundingMode.HALF_UP);
        if (qtde.compareTo(BigDecimal.ZERO) <= 0) {
            return precoUnitarioEnvioDuasCasas.setScale(6, RoundingMode.HALF_UP);
        }

        BigDecimal descontoPayload = descontoUnitarioEnvioPayload == null
                ? BigDecimal.ZERO
                : descontoUnitarioEnvioPayload.setScale(6, RoundingMode.HALF_UP);
        BigDecimal totalLiquidoAlvo = calcularValorLiquidoTotalAlvoItemEnvioApi(item, condicaoAPrazo)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal precoPayload = totalLiquidoAlvo
                .divide(qtde, 6, RoundingMode.HALF_UP)
                .add(descontoPayload)
                .max(BigDecimal.ZERO)
                .setScale(6, RoundingMode.HALF_UP);

        if (precoPayload.compareTo(BigDecimal.ZERO) <= 0) {
            return precoUnitarioEnvioDuasCasas.setScale(6, RoundingMode.HALF_UP);
        }

        return precoPayload;
    }

    private BigDecimal ajustarDescontoUnitarioEnvioParaTotalAlvo(ItemPedidos item,
                                                                 BigDecimal precoUnitarioEnvioDuasCasas,
                                                                 BigDecimal descontoUnitarioEnvio,
                                                                 boolean condicaoAPrazo) {
        BigDecimal descontoBase = descontoUnitarioEnvio == null
                ? BigDecimal.ZERO
                : descontoUnitarioEnvio.setScale(6, RoundingMode.HALF_UP);

        if (item == null) {
            return descontoBase;
        }

        BigDecimal qtde = BigDecimal.valueOf(Math.max(0.0d, item.getQtcomp())).setScale(6, RoundingMode.HALF_UP);
        if (qtde.compareTo(BigDecimal.ZERO) <= 0) {
            return descontoBase;
        }

        BigDecimal totalLiquidoAlvo = calcularValorLiquidoTotalAlvoItemEnvioApi(item, condicaoAPrazo)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalLiquidoComPayloadAtual = precoUnitarioEnvioDuasCasas
                .subtract(descontoBase)
                .max(BigDecimal.ZERO)
                .multiply(qtde)
                .setScale(2, RoundingMode.HALF_UP);

        if (totalLiquidoComPayloadAtual.compareTo(totalLiquidoAlvo) == 0) {
            return descontoBase;
        }

        BigDecimal liquidoUnitarioAlvo = totalLiquidoAlvo.divide(qtde, 6, RoundingMode.HALF_UP);
        return precoUnitarioEnvioDuasCasas
                .subtract(liquidoUnitarioAlvo)
                .max(BigDecimal.ZERO)
                .setScale(6, RoundingMode.HALF_UP);
    }

    private double truncarDpiParaEnvio(BigDecimal dpiCalculado) {
        if (dpiCalculado == null) {
            return 0.0d;
        }

        return dpiCalculado
                .setScale(2, RoundingMode.DOWN)
                .doubleValue();
    }

    private Pedidos prepararPedidoCompativelParaSalvarNaApi(Pedidos pedidoOriginal) {
        Pedidos pedidoApi = clonarPedido(pedidoOriginal);
        if (pedidoApi == null) {
            return null;
        }
        if (pedidoApi.getItens() == null) {
            pedidoApi.setItens(new java.util.ArrayList<ItemPedidos>());
        }


        final RoundingMode RM = RoundingMode.HALF_UP;
        final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RM);

        BigDecimal subtotalItensComAcrescimoPrazo = ZERO;
        BigDecimal totalProdutosBase = ZERO;
        BigDecimal totalProdutosComAcrescimo = ZERO;
        StringBuilder itensComAcrescimoDetalhe = new StringBuilder();
        StringBuilder itensDetalhadoDebug = new StringBuilder();
        int qtdItensComAcrescimo = 0;
        int _debugIdxItem = -1;
        for (ItemPedidos item : pedidoApi.getItens()) {
            _debugIdxItem++;
            if (item == null) {
                continue;
            }

            BigDecimal qtde = BigDecimal.valueOf(item.getQtcomp());
            if (qtde.compareTo(BigDecimal.ZERO) <= 0) {
                qtde = BigDecimal.ONE;
            }

            BigDecimal precoUnitAtual = BigDecimal.valueOf(Math.max(0.0d, item.getPrecoUnit()));
            BigDecimal descontoUnitAtual = ajustarDescontoUnitarioCompativelBackend(item, precoUnitAtual);
            item.setVlDescItem(descontoUnitAtual.doubleValue());
            if (precoUnitAtual.compareTo(BigDecimal.ZERO) > 0) {
                item.setDpi(
                        descontoUnitAtual
                                .multiply(BigDecimal.valueOf(100.0d))
                                .divide(precoUnitAtual, 6, RoundingMode.HALF_UP)
                                .doubleValue()
                );
            }
            BigDecimal precoBaseItem = item.getPrecoPrimario() > 0.0d
                    ? BigDecimal.valueOf(item.getPrecoPrimario())
                    : precoUnitAtual;

            BigDecimal liquidoUnit = precoUnitAtual.subtract(descontoUnitAtual).max(BigDecimal.ZERO);
            BigDecimal liquidoTotalItemBruto = liquidoUnit.multiply(qtde);
            // ===== HALF_UP POR ITEM (REGRA NOVA OPCAO 2, arredondamento comercial) =====
            BigDecimal liquidoTotalItem = liquidoTotalItemBruto.setScale(2, RM);
            BigDecimal baseTotalItem = precoBaseItem.multiply(qtde).setScale(2, RM);
            BigDecimal diffItemVsBase = liquidoTotalItem.subtract(baseTotalItem).setScale(2, RM);

            totalProdutosBase = totalProdutosBase.add(baseTotalItem);
            totalProdutosComAcrescimo = totalProdutosComAcrescimo.add(
                    precoUnitAtual.multiply(qtde).setScale(2, RM)
            );
            subtotalItensComAcrescimoPrazo = subtotalItensComAcrescimoPrazo.add(liquidoTotalItem);

            if (itensDetalhadoDebug.length() > 0) itensDetalhadoDebug.append(" || ");
            itensDetalhadoDebug.append("[i").append(_debugIdxItem)
                    .append("|cod=").append(item.getCoditprod())
                    .append("|sit=").append(item.getSituacao())
                    .append("|motor=").append(item.getIsMotor())
                    .append("|qt=").append(qtde.setScale(2, RM))
                    .append("|PU=").append(precoUnitAtual.setScale(6, RM))
                    .append("|PPri=").append(precoBaseItem.setScale(6, RM))
                    .append("|Desc=").append(descontoUnitAtual.setScale(6, RM))
                    .append("|LiqBruto=").append(liquidoTotalItemBruto.setScale(6, RM))
                    .append("|LiqTotHALF_UP=").append(liquidoTotalItem.setScale(2, RM))
                    .append("|BaseTot=").append(baseTotalItem.setScale(2, RM))
                    .append("|DiffvsBase=").append(diffItemVsBase.setScale(2, RM))
                    .append("]");

            if (precoUnitAtual.compareTo(precoBaseItem) > 0) {
                qtdItensComAcrescimo++;
                if (itensComAcrescimoDetalhe.length() > 0) itensComAcrescimoDetalhe.append(" | ");
                itensComAcrescimoDetalhe.append("cod=").append(item.getCoditprod())
                        .append(", base=").append(precoBaseItem.setScale(4, RM))
                        .append(", atual=").append(precoUnitAtual.setScale(6, RM))
                        .append(", qtde=").append(qtde.setScale(1, RM))
                        .append(", LiqHALF_UPitem=").append(liquidoTotalItem.setScale(2, RM));
            }
        }

        if (pedidoApi.getVlDescPromo() > 0.0d) {
            subtotalItensComAcrescimoPrazo = subtotalItensComAcrescimoPrazo.subtract(
                    BigDecimal.valueOf(pedidoApi.getVlDescPromo()).setScale(2, RM)
            ).max(ZERO);
        }

        BigDecimal freteBaseCobrado = BigDecimal.valueOf(Math.max(0.0d, pedidoApi.getVlFrete())).setScale(2, RM);
        BigDecimal servicosCobrado = BigDecimal.valueOf(Math.max(0.0d, pedidoApi.getVlServicosAdicionais())).setScale(2, RM);
        BigDecimal freteTotalParaApi = freteBaseCobrado.add(servicosCobrado).setScale(2, RM);

        BigDecimal vlTotalObjetoAntes = BigDecimal.valueOf(Math.max(0.0d, pedidoApi.getVlTotal())).setScale(6, RM);
        // ===== REFERENCIA SOBERANA: subtotalItensLiquido = soma HALF_UP por item =====
        BigDecimal subtotalItensLiquido = subtotalItensComAcrescimoPrazo.setScale(2, RM);
        BigDecimal vlTotalEsperadoDepois = subtotalItensLiquido.add(freteTotalParaApi).setScale(2, RM);

        pedidoApi.setVlTotal(subtotalItensLiquido.doubleValue());
        pedidoApi.setVlFrete(freteTotalParaApi.doubleValue());
        pedidoApi.setVlServicosAdicionais(0.0d);
        BigDecimal descontoItens = totalProdutosComAcrescimo.subtract(subtotalItensLiquido).setScale(2, RM);
        if (descontoItens.compareTo(ZERO) < 0) descontoItens = ZERO;
        pedidoApi.setVlDesconto(descontoItens.doubleValue());

        Log.d("FreteCompatDebug",
                "Ajuste compatibilidade savePedido (HALF_UP)"
                        + " | pedido=" + pedidoApi.getNumpedven()
                        + " | vlTotalCompletoApp(ObjetoAntes)=" + vlTotalObjetoAntes.setScale(2, RM)
                        + " | totalProdutosBase=" + totalProdutosBase.setScale(6, RM)
                        + " | totalProdutosComAcrescimo(Motor095)=" + totalProdutosComAcrescimo.setScale(8, RM)
                        + " | subtotalItensLiquido(subtotalAPI)=" + subtotalItensLiquido
                        + " | vlFreteEnviado=" + freteTotalParaApi
                        + " | vlFreteBase=" + freteBaseCobrado
                        + " | vlServicosInfo=" + servicosCobrado
                        + " | vlTotalEsperadoDepois(subtotal+frete+serv)=" + vlTotalEsperadoDepois
                        + " | diffVlTotalObjeto_vs_subtotalFrete=" + vlTotalObjetoAntes.setScale(2, RM).subtract(vlTotalEsperadoDepois).setScale(4, RM)
                        + " | DETAIL_ITENS=[" + itensDetalhadoDebug.toString() + "]");


        try {
            StringBuilder finDetalhe = new StringBuilder();

            // --- (0) SOMA TODAS AS PARCELAS ANTES (para usar no calculo vlTotalEsperadoDepois) ---
            java.util.List<Financeiro> fins = pedidoApi.getFinanceiro();
            if (fins == null) fins = new java.util.ArrayList<>();
            int qtdParcelasReais = fins.size();
            BigDecimal somaFinanceiroOriginal = ZERO;
            StringBuilder finOrigDetalhe = new StringBuilder();
            for (int i = 0; i < fins.size(); i++) {
                Financeiro fin = fins.get(i);
                if (fin == null) continue;
                BigDecimal vl = BigDecimal.valueOf(Math.max(0.0d, fin.getValor())).setScale(2, RM);
                somaFinanceiroOriginal = somaFinanceiroOriginal.add(vl);
                if (finOrigDetalhe.length() > 0) finOrigDetalhe.append(" ; ");
                finOrigDetalhe.append("[f").append(i)
                        .append(":forma=").append(fin.getForma())
                        .append(",CodEve=").append(fin.getCodEve())
                        .append(",evt=").append(fin.getEvento())
                        .append(",conv=").append(fin.getConveniada())
                        .append(",naut=").append(fin.getNumaut())
                        .append(",port=").append(fin.getPortador())
                        .append(",tax=").append(fin.getTaxadm())
                        .append(",qtp=").append(fin.getQtprc())
                        .append(",nprc=").append(fin.getNumprc())
                        .append(",v=").append(vl)
                        .append(",dtvcto=").append(fin.getDtvcto())
                        .append("]");
            }

            BigDecimal TOLERANCIA_CENTAVOS = new BigDecimal("0.50").setScale(2, RM);
            BigDecimal subtotalFLOORitem = vlTotalEsperadoDepois.setScale(2, RM);
            BigDecimal diferencaTetoVsParcelasANTESdoMIN = subtotalFLOORitem.subtract(somaFinanceiroOriginal).setScale(2, RM);
            BigDecimal diferencaAbsoluta = diferencaTetoVsParcelasANTESdoMIN.abs();
            boolean priorizarSomaItensNoFinanceiro = deveUsarTotalRecalculadoDosItens(pedidoApi);
            boolean existeAcrescimoFinanceiro =
                    !priorizarSomaItensNoFinanceiro
                            && (somaFinanceiroOriginal.compareTo(subtotalFLOORitem) > 0)
                            && somaFinanceiroOriginal.subtract(subtotalFLOORitem).compareTo(TOLERANCIA_CENTAVOS) > 0;
            BigDecimal tetoDefinitivo;
            if (existeAcrescimoFinanceiro) {

                tetoDefinitivo = somaFinanceiroOriginal.setScale(2, RM);
                BigDecimal valorAcrescimo = somaFinanceiroOriginal.subtract(subtotalFLOORitem);
                finDetalhe.append("ACRESCIMO_FINANCEIRO_PRESERVADO: subtotal_itens+frete=").append(subtotalFLOORitem)
                        .append(" vs somaParcelas=").append(somaFinanceiroOriginal)
                        .append(" | acrescimo=").append(valorAcrescimo)
                        .append(" > 0.50cent → TETO = somaParcelas (mantem juros/encargos). ")
                        .append("NAO reduzir parcela | ");
            } else if (priorizarSomaItensNoFinanceiro && somaFinanceiroOriginal.compareTo(subtotalFLOORitem) != 0) {
                tetoDefinitivo = subtotalFLOORitem;
                finDetalhe.append("TOTAL_ITENS_SOBERANO: subtotal_item=").append(subtotalFLOORitem)
                        .append(" vs somaParcelas=").append(somaFinanceiroOriginal)
                        .append(" -> usando soma dos itens para cabecalho/financeiro. | ");
            } else if (diferencaAbsoluta.compareTo(ZERO.setScale(2, RM)) > 0
                    && diferencaAbsoluta.compareTo(TOLERANCIA_CENTAVOS) <= 0) {

                tetoDefinitivo = subtotalFLOORitem;
                finDetalhe.append("TOLERANCIA_50CENTAVOS: subtotal_item=").append(subtotalFLOORitem)
                        .append(" vs somaParcelas=").append(somaFinanceiroOriginal)
                        .append(" |dif|=").append(diferencaAbsoluta)
                        .append(" <= 0.50 → FORCAR igualar ao subtotal+frete (subtotal_item, ajustar ultima parcela PARA CIMA). | ");
            } else {
                // ===== CASO 3: dif > 0.50cent e SEM acréscimo (parcelas < sub+frete) =====
                tetoDefinitivo = subtotalFLOORitem.min(somaFinanceiroOriginal);
                if (tetoDefinitivo.compareTo(subtotalFLOORitem) != 0) {
                    finDetalhe.append("TETO_MIN_PARCELAS(dif>50cent): subtotal_item=").append(subtotalFLOORitem)
                            .append(" > somaParcelas=").append(somaFinanceiroOriginal)
                            .append(" -> USANDO ").append(tetoDefinitivo).append(" (parcela prevalece) | ");
                }
            }
            if (tetoDefinitivo.compareTo(ZERO) < 0) tetoDefinitivo = ZERO;
            vlTotalEsperadoDepois = tetoDefinitivo;

            // --- (1) vlPrc e vlEntrada: ALINHAR com vlTotalEsperadoDepois (TETO) ---
            BigDecimal vlPrcOriginal = BigDecimal.valueOf(Math.max(0.0d, pedidoApi.getVlPrc())).setScale(2, RM);
            BigDecimal vlEntradaOriginal = BigDecimal.valueOf(Math.max(0.0d, pedidoApi.getVlEntrada())).setScale(2, RM);
            BigDecimal vlTotalEsperadoBD = vlTotalEsperadoDepois.setScale(2, RM);

            BigDecimal vlPrcFinal;
            if (existeAcrescimoFinanceiro) {
                // ===== CASO ACRÉSCIMO FINANCEIRO: VlPrc DEVE bater soma parcelas! =====
                // Não podemos aplicar min(vlPrc, teto) quando teto É IGUAL somaParcelas.
                // Garantir VlPrc = somaParcelas = vlTotalEsperadoBD = 8113.02.
                vlPrcFinal = vlTotalEsperadoBD;
                if (vlPrcFinal.compareTo(vlPrcOriginal) != 0) {
                    pedidoApi.setVlPrc(vlPrcFinal.doubleValue());
                    finDetalhe.append("VlPrc_ALINHADO_ACRESCIMO_FINANCEIRO: antigo=").append(vlPrcOriginal)
                            .append(" -> novo=").append(vlPrcFinal)
                            .append(" (IGUAL somaParcelas). NAO reduzir juros. | ");
                } else {
                    finDetalhe.append("VlPrc_PRESERVADO_ACRESCIMO=").append(vlPrcFinal).append(" | ");
                }
            } else {
                // ===== CASO NORMAL (sem acréscimo financeiro) =====
                vlPrcFinal = vlPrcOriginal.min(vlTotalEsperadoBD);
                if (somaFinanceiroOriginal.compareTo(ZERO) > 0) {
                    BigDecimal diffPrcParcelas = vlPrcFinal.subtract(somaFinanceiroOriginal).abs();
                    if (diffPrcParcelas.compareTo(new BigDecimal("0.05")) <= 0
                            && somaFinanceiroOriginal.compareTo(vlTotalEsperadoBD) <= 0) {
                        vlPrcFinal = somaFinanceiroOriginal.setScale(2, RM);
                    }
                }
                if (vlPrcOriginal.compareTo(ZERO) == 0) {
                    vlPrcFinal = vlTotalEsperadoBD;
                    pedidoApi.setVlPrc(vlPrcFinal.doubleValue());
                    finDetalhe.append("VlPrc_FALLBACK_ZERO: ").append(vlPrcFinal).append(" | ");
                } else if (vlPrcFinal.compareTo(vlPrcOriginal) != 0) {
                    pedidoApi.setVlPrc(vlPrcFinal.doubleValue());
                    finDetalhe.append("VlPrc_TETO_ITEM: antigo=").append(vlPrcOriginal)
                            .append(" -> max_permitido_soma_itens=").append(vlPrcFinal).append(" | ");
                } else {
                    finDetalhe.append("VlPrc_PRESERVADO=").append(vlPrcFinal).append(" | ");
                }
            }
            finDetalhe.append("VlEntrada_PRESERVADO=").append(vlEntradaOriginal).append(" | ");

            // --- (3) Ajustar a ULTIMA parcela (REGRAS DIFERENCIADAS por CASO) ---
            //     diferenca = vlTotalEsperadoDepois - somaFinanceiroOriginal
            //     CASO A (existeAcrescimoFinanceiro): NUNCA ALTERAR PARCELAS (mantem inalterado).
            //     CASO B (TOLERANCIA 50 centavos):
            //        - diferenca < 0 → soma parcelas MAIOR que teto (erro centavos) → REDUZIR última parcela.
            //        - 0 < diferenca ≤ 0.50 → soma faltou centavos → AUMENTAR última parcela.
            //     CASO C (outros): NÃO ajusta.
            BigDecimal diferenca = vlTotalEsperadoDepois.subtract(somaFinanceiroOriginal).setScale(2, RM);
            boolean somaParcelasExcedeuTeto = diferenca.compareTo(BigDecimal.ZERO.setScale(2, RM)) < 0;
            boolean somaParcelasFaltouAte50Cent = diferenca.compareTo(BigDecimal.ZERO.setScale(2, RM)) > 0
                    && diferenca.compareTo(TOLERANCIA_CENTAVOS) <= 0;
            boolean ajustouUltimaParcela = false;
            BigDecimal vlUltimoOriginal = ZERO;
            BigDecimal vlUltimoNovo = ZERO;

            if (existeAcrescimoFinanceiro) {

                if (diferenca.abs().compareTo(TOLERANCIA_CENTAVOS) > 0) {
                    // Se diferenca > 0.50cent e já teto = somaParcelas, loga apenas.
                    finDetalhe.append("ACRESCIMO_FINANCEIRO: PARCELAS PRESERVADAS SEM AJUSTE. ")
                            .append("somaParcelas=").append(somaFinanceiroOriginal)
                            .append(", vlTotalEsperadoDepois(teto)=").append(vlTotalEsperadoDepois)
                            .append(", difIgnorada=").append(diferenca)
                            .append(" | MOTIVO: diferenca = juros/encargos financeiros, NAO centavos arredondamento. ");
                } else {
                    finDetalhe.append("SEM_AJUSTE: somaFinanceiro bate perfeitamente com vlTotalEsperadoDepois");
                }
            } else if ((somaParcelasExcedeuTeto || somaParcelasFaltouAte50Cent) && qtdParcelasReais >= 1) {
                int idxUltima = qtdParcelasReais - 1;
                Financeiro finUltima = fins.get(idxUltima);
                if (finUltima != null) {
                    vlUltimoOriginal = BigDecimal.valueOf(Math.max(0.0d, finUltima.getValor())).setScale(2, RM);
                    vlUltimoNovo = vlUltimoOriginal.add(diferenca).max(ZERO).setScale(2, RM);
                    finUltima.setValor(vlUltimoNovo.doubleValue());
                    somaFinanceiroOriginal = somaFinanceiroOriginal.add(diferenca).setScale(2, RM);
                    ajustouUltimaParcela = true;
                    if (somaParcelasExcedeuTeto) {
                        finDetalhe.append("AJUSTE_REDUCAO_ULTIMA_PARCELA: idx=").append(idxUltima)
                                .append(", antigo=").append(vlUltimoOriginal)
                                .append(", reducao=").append(diferenca.abs())
                                .append(", novo=").append(vlUltimoNovo).append(" | ")
                                .append("MOTIVO: soma parcelas excedeu teto soma_itens | ")
                                .append("MANTIDOS_OUTROS_CAMPOS: forma,CodEve,evt,conv,naut,port,tax,qtprc,nprc,dtvcto,flautcre");
                    } else {
                        finDetalhe.append("AJUSTE_ACRESCIMO_ULTIMA_PARCELA_CENTAVOS: idx=").append(idxUltima)
                                .append(", antigo=").append(vlUltimoOriginal)
                                .append(", acrescimoDif=").append(diferenca)
                                .append(", novo=").append(vlUltimoNovo).append(" | ")
                                .append("MOTIVO: (somaParcelas faltou <=0.50cent) IGUALAR itens+frete+serv = vlTotalEsperadoDepois. | ")
                                .append("MANTIDOS_OUTROS_CAMPOS: forma,CodEve,evt,conv,naut,port,tax,qtprc,nprc,dtvcto,flautcre");
                    }
                }
            } else if (diferenca.compareTo(BigDecimal.ZERO.setScale(2, RM)) == 0) {
                finDetalhe.append("SEM_AJUSTE: somaFinanceiro bate perfeitamente com vlTotalEsperadoDepois (teto soma_itens)");
            } else {
                finDetalhe.append("SEM_AJUSTE: soma parcelas (").append(somaFinanceiroOriginal).append(") ")
                        .append("<= teto soma_itens (").append(vlTotalEsperadoDepois).append("). ")
                        .append("DIF=(").append(diferenca).append(") > 0.50 centavos → NAO ajusta para CIMA (parcela prevalece). | ")
                        .append("— parcela nao pode valer mais que a soma da conferencia");
            }

            Log.d("FreteCompatDebug",
                    "Sincronia Financeiro (Preservacao Absoluta + TETO soma itens)"
                            + " | pedido=" + pedidoApi.getNumpedven()
                            + " | vlTotalEsperadoDepois(TETO_SOMA_ITENS)=" + vlTotalEsperadoDepois
                            + " | somaFinanceiroOriginal(antes)=" + somaFinanceiroOriginal.setScale(2, RM).subtract(ajustouUltimaParcela ? diferenca : ZERO).setScale(2, RM)
                            + " | somaFinanceiroAposAjuste=" + somaFinanceiroOriginal.setScale(2, RM)
                            + " | qtdParcelasReais(fins.size)=" + qtdParcelasReais
                            + " | Qtprc_do_Objeto_NAO_USAMOS=" + pedidoApi.getQtprc()
                            + " | diferenca_TETOmenosSomaParcelas=" + diferenca
                            + " | itensComAcrescimo=" + qtdItensComAcrescimo
                            + " | detalheAjustes=[" + finDetalhe.toString() + "]"
                            + " | ORIGINAIS_PRESERVADOS=[" + finOrigDetalhe.toString() + "]"
                            + " | detalheItensAcrescimo=[" + itensComAcrescimoDetalhe.toString() + "]");

        } catch (Exception ex) {
            Log.e("FreteCompatDebug", "Erro ao sincronizar financeiro (preservacao absoluta): " + ex.getMessage(), ex);
        }


        try {
            BigDecimal vlFreteFinal = BigDecimal.valueOf(Math.max(0.0d, pedidoApi.getVlFrete())).setScale(2, RM);
            BigDecimal vlServFinal = BigDecimal.valueOf(Math.max(0.0d, pedidoApi.getVlServicosAdicionais())).setScale(2, RM);
            BigDecimal vlDescPromoCab = BigDecimal.valueOf(Math.max(0.0d, pedidoApi.getVlDescPromo())).setScale(2, RM);

            BigDecimal subtotalItensAjustadoTeto = vlTotalEsperadoDepois.setScale(2, RM)
                    .subtract(vlFreteFinal.setScale(2, RM))
                    .subtract(vlServFinal.setScale(2, RM))
                    .max(ZERO.setScale(2, RM));

            pedidoApi.setVlTotal(subtotalItensAjustadoTeto.doubleValue());
            pedidoApi.setVlPrc(vlTotalEsperadoDepois.setScale(2, RM).doubleValue());
            if (pedidoApi.getVlEntrada() <= 0.0d
                    || BigDecimal.valueOf(pedidoApi.getVlEntrada()).setScale(2, RM).compareTo(vlTotalEsperadoDepois.setScale(2, RM)) != 0) {
                pedidoApi.setVlEntrada(vlTotalEsperadoDepois.setScale(2, RM).doubleValue());
            }

            BigDecimal descontoCabCalc = totalProdutosComAcrescimo.setScale(2, RM)
                    .subtract(subtotalItensAjustadoTeto.setScale(2, RM))
                    .subtract(vlDescPromoCab.setScale(2, RM))
                    .max(ZERO.setScale(2, RM));
            pedidoApi.setVlDesconto(descontoCabCalc.setScale(2, RM).doubleValue());

            Log.d("FreteCompatDebug",
                    "Sincronia Cabecalho (Coerencia Total/Desc) | pedido=" + pedidoApi.getNumpedven()
                            + " | VlTotal_APENAS_Itens_Antigo_Original_SP_Grava_Com_Frete_Depois=" + (vlTotalEsperadoDepois.setScale(2, RM).toPlainString())
                            + " | VlTotal_APENAS_Itens_Cabecalho_JSON=" + BigDecimal.valueOf(pedidoApi.getVlTotal()).setScale(2, RM)
                            + " | VlPrc_Cabecalho_JSON(Total_Pedido)= " + BigDecimal.valueOf(pedidoApi.getVlPrc()).setScale(2, RM)
                            + " | VlEntrada_Cabecalho_JSON=" + BigDecimal.valueOf(pedidoApi.getVlEntrada()).setScale(2, RM)
                            + " | VlDesconto_Apenas_Itens_Cabecalho=" + BigDecimal.valueOf(pedidoApi.getVlDesconto()).setScale(2, RM)
                            + " | VlFrete_Separado=" + vlFreteFinal
                            + " | VlServicos_Separado=" + vlServFinal);
        } catch (Exception ex) {
            Log.e("FreteCompatDebug", "Erro ao sincronizar cabeçalho vlTotal/vlDesconto: " + ex.getMessage(), ex);
        }

        try {
            if (deveUsarTotalRecalculadoDosItens(pedidoApi)) {
                alinharCabecalhoComItensPayloadCompativelBackend(pedidoApi);
            }
        } catch (Exception ex) {
            Log.e("FreteCompatDebug", "Erro ao alinhar cabeçalho com totais compatíveis do backend: " + ex.getMessage(), ex);
        }

        return pedidoApi;
    }

    private BigDecimal ajustarDescontoUnitarioCompativelBackend(ItemPedidos item, BigDecimal precoUnitAtual) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        }

        BigDecimal descontoAtual = BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem())).setScale(6, RoundingMode.HALF_UP);
        BigDecimal qtde = BigDecimal.valueOf(Math.max(0.0d, item.getQtcomp())).setScale(6, RoundingMode.HALF_UP);

        if (qtde.compareTo(BigDecimal.ZERO) <= 0 || descontoAtual.compareTo(BigDecimal.ZERO) <= 0) {
            return descontoAtual;
        }

        BigDecimal totalLiquidoAlvo = precoUnitAtual
                .subtract(descontoAtual)
                .max(BigDecimal.ZERO)
                .multiply(qtde)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalBrutoCompativel = precoUnitAtual
                .multiply(qtde)
                .setScale(2, RoundingMode.DOWN);

        BigDecimal descontoTotalCompativel = totalBrutoCompativel
                .subtract(totalLiquidoAlvo)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        return descontoTotalCompativel
                .divide(qtde, 6, RoundingMode.HALF_UP)
                .max(BigDecimal.ZERO)
                .setScale(6, RoundingMode.HALF_UP);
    }

    private void alinharCabecalhoComItensPayloadCompativelBackend(Pedidos pedidoApi) {
        if (pedidoApi == null) {
            return;
        }

        BigDecimal subtotalItensBackend = calcularSubtotalItensLiquidoApiCompativelBackend(pedidoApi);
        BigDecimal descontoTotalBackend = calcularDescontoTotalItensApiCompativelBackend(pedidoApi);
        BigDecimal freteTotal = BigDecimal.valueOf(Math.max(0.0d, pedidoApi.getVlFrete())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal servicosTotal = BigDecimal.valueOf(Math.max(0.0d, pedidoApi.getVlServicosAdicionais())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalPedidoBackend = subtotalItensBackend.add(freteTotal).add(servicosTotal).setScale(2, RoundingMode.HALF_UP);

        pedidoApi.setVlTotal(subtotalItensBackend.doubleValue());
        pedidoApi.setVlPrc(totalPedidoBackend.doubleValue());
        pedidoApi.setVlDesconto(descontoTotalBackend.doubleValue());

        if (pedidoApi.getVlEntrada() <= 0.0d
                || BigDecimal.valueOf(pedidoApi.getVlEntrada()).setScale(2, RoundingMode.HALF_UP).compareTo(totalPedidoBackend) != 0) {
            pedidoApi.setVlEntrada(totalPedidoBackend.doubleValue());
        }

        if (pedidoApi.getFinanceiro() != null && !pedidoApi.getFinanceiro().isEmpty()) {
            BigDecimal somaFinanceiro = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            Financeiro ultimaParcela = null;

            for (Financeiro financeiro : pedidoApi.getFinanceiro()) {
                if (financeiro == null) {
                    continue;
                }

                BigDecimal valorParcela = BigDecimal.valueOf(Math.max(0.0d, financeiro.getValor())).setScale(2, RoundingMode.HALF_UP);
                somaFinanceiro = somaFinanceiro.add(valorParcela).setScale(2, RoundingMode.HALF_UP);
                ultimaParcela = financeiro;
            }

            if (ultimaParcela != null) {
                BigDecimal diferenca = totalPedidoBackend.subtract(somaFinanceiro).setScale(2, RoundingMode.HALF_UP);
                if (diferenca.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal valorAtualUltima = BigDecimal.valueOf(Math.max(0.0d, ultimaParcela.getValor())).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal novoValorUltima = valorAtualUltima.add(diferenca).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
                    ultimaParcela.setValor(novoValorUltima.doubleValue());
                }
            }
        }

        Log.d("FreteCompatDebug",
                "Alinhamento final com itens compatíveis backend"
                        + " | pedido=" + pedidoApi.getNumpedven()
                        + " | subtotalItensBackend=" + subtotalItensBackend
                        + " | descontoTotalBackend=" + descontoTotalBackend
                        + " | frete=" + freteTotal
                        + " | servicos=" + servicosTotal
                        + " | totalPedidoBackend=" + totalPedidoBackend);
    }

    private BigDecimal calcularSubtotalItensLiquidoApiCompativelBackend(Pedidos pedidoRef) {
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal subtotalItens = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (ItemPedidos item : pedidoRef.getItens()) {
            if (item == null) {
                continue;
            }
            subtotalItens = subtotalItens.add(calcularValorLiquidoItemPayloadCompativelBackend(item)).setScale(2, RoundingMode.HALF_UP);
        }

        if (pedidoRef.getVlDescPromo() > 0.0d) {
            subtotalItens = subtotalItens.subtract(BigDecimal.valueOf(pedidoRef.getVlDescPromo()).setScale(2, RoundingMode.HALF_UP));
        }

        return subtotalItens.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularDescontoTotalItensApiCompativelBackend(Pedidos pedidoRef) {
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal descontoTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (ItemPedidos item : pedidoRef.getItens()) {
            if (item == null) {
                continue;
            }
            descontoTotal = descontoTotal.add(calcularValorDescontoItemPayloadCompativelBackend(item)).setScale(2, RoundingMode.HALF_UP);
        }

        if (pedidoRef.getVlDescPromo() > 0.0d) {
            descontoTotal = descontoTotal.add(BigDecimal.valueOf(pedidoRef.getVlDescPromo()).setScale(2, RoundingMode.HALF_UP));
        }

        return descontoTotal.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularValorLiquidoItemPayloadCompativelBackend(ItemPedidos item) {
        return calcularValorBrutoItemPayloadCompativelBackend(item)
                .subtract(calcularValorDescontoItemPayloadCompativelBackend(item))
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularValorBrutoItemPayloadCompativelBackend(ItemPedidos item) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.valueOf(Math.max(0.0d, item.getPrecoUnit()))
                .multiply(BigDecimal.valueOf(Math.max(0.0d, item.getQtcomp())))
                .setScale(2, RoundingMode.DOWN);
    }

    private BigDecimal calcularValorDescontoItemPayloadCompativelBackend(ItemPedidos item) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem()))
                .multiply(BigDecimal.valueOf(Math.max(0.0d, item.getQtcomp())))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularSubtotalItensLiquidoApi(Pedidos pedidoRef) {
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal subtotalItens = BigDecimal.ZERO;

        for (ItemPedidos item : pedidoRef.getItens()) {
            if (item == null) {
                continue;
            }
            subtotalItens = subtotalItens.add(calcularValorLiquidoItemPayload(item));
        }

        if (pedidoRef.getVlDescPromo() > 0) {
            subtotalItens = subtotalItens.subtract(BigDecimal.valueOf(pedidoRef.getVlDescPromo()));
        }

        return subtotalItens.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularDescontoTotalItensApi(Pedidos pedidoRef) {
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalDesconto = BigDecimal.ZERO;
        for (ItemPedidos item : pedidoRef.getItens()) {
            if (item == null) {
                continue;
            }

            totalDesconto = totalDesconto.add(
                    BigDecimal.valueOf(item.getVlDescItem())
                            .multiply(BigDecimal.valueOf(item.getQtcomp()))
                            .setScale(2, RoundingMode.HALF_UP)
            );
        }

        if (pedidoRef.getVlDescPromo() > 0) {
            totalDesconto = totalDesconto.add(BigDecimal.valueOf(pedidoRef.getVlDescPromo()));
        }

        return totalDesconto.setScale(2, RoundingMode.HALF_UP);
    }

    private void recalcularTotalPayloadPedidoApi(Pedidos pedidoRef) {
        if (pedidoRef == null) {
            return;
        }

        BigDecimal subtotalItens = calcularSubtotalItensLiquidoApi(pedidoRef);
        BigDecimal descontoTotal = calcularDescontoTotalItensApi(pedidoRef);
        BigDecimal freteTotal = BigDecimal.valueOf(Math.max(0.0d, pedidoRef.getVlFrete())).setScale(2, RoundingMode.HALF_UP);
        BigDecimal servicosTotal = BigDecimal.valueOf(Math.max(0.0d, pedidoRef.getVlServicosAdicionais())).setScale(2, RoundingMode.HALF_UP);
        pedidoRef.setVlTotal(subtotalItens.add(freteTotal).add(servicosTotal).setScale(2, RoundingMode.HALF_UP).doubleValue());
        pedidoRef.setVlDesconto(descontoTotal.doubleValue());
        recalcularMargensPayloadPedidoApi(pedidoRef);
    }

    private void recalcularMargensPayloadPedidoApi(Pedidos pedidoRef) {
        if (pedidoRef == null || pedidoRef.getItens() == null || pedidoRef.getItens().isEmpty()) {
            if (pedidoRef != null) {
                pedidoRef.setPmargRent(0.0d);
            }
            return;
        }

        BigDecimal valorTotalMargem = BigDecimal.ZERO;
        BigDecimal valorCusto = BigDecimal.ZERO;
        BigDecimal valorIcms = BigDecimal.ZERO;
        double percentualEncargos = 0.0d;
        boolean percentualEncargosDefinido = false;

        for (ItemPedidos item : pedidoRef.getItens()) {
            if (item == null) {
                continue;
            }

            double margemItem = calcularMargemItemPayload(item);
            item.setPmargRent(margemItem);

            BigDecimal valorLiquidoItem = calcularValorLiquidoItemPayload(item);
            valorTotalMargem = valorTotalMargem.add(valorLiquidoItem);
            valorCusto = valorCusto.add(
                    BigDecimal.valueOf(item.getCusto())
                            .multiply(BigDecimal.valueOf(item.getQtcomp()))
            );
            valorIcms = valorIcms.add(calcularValorIcmsItemPayload(item, valorLiquidoItem));

            if (!percentualEncargosDefinido && item.getEncargos() > 0) {
                percentualEncargos = item.getEncargos();
                percentualEncargosDefinido = true;
            }
        }

        if (pedidoRef.getVlDescPromo() > 0) {
            valorTotalMargem = valorTotalMargem.subtract(BigDecimal.valueOf(pedidoRef.getVlDescPromo()));
        }

        if (valorTotalMargem.compareTo(BigDecimal.ZERO) <= 0) {
            pedidoRef.setPmargRent(0.0d);
            return;
        }

        double valorEncargos = (valorTotalMargem.doubleValue() * percentualEncargos) / 100d;
        double margemPedido = ((valorTotalMargem.doubleValue()
                - valorEncargos
                - valorIcms.doubleValue()
                - valorCusto.doubleValue()) / valorTotalMargem.doubleValue()) * 100d;

        pedidoRef.setPmargRent(margemPedido);
    }

    private double calcularMargemItemPayload(ItemPedidos item) {
        if (item == null || "SV".equals(item.getSituacao())) {
            return 0.0d;
        }

        BigDecimal valorLiquidoItem = calcularValorLiquidoItemPayload(item);
        if (valorLiquidoItem.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0d;
        }

        double valorEncargos = (valorLiquidoItem.doubleValue() * item.getEncargos()) / 100d;
        double valorIcms = calcularValorIcmsItemPayload(item, valorLiquidoItem).doubleValue();
        double valorCusto = item.getCusto() * item.getQtcomp();

        return ((valorLiquidoItem.doubleValue() - valorEncargos - valorIcms - valorCusto)
                / valorLiquidoItem.doubleValue()) * 100d;
    }

    private BigDecimal calcularValorLiquidoItemPayload(ItemPedidos item) {
        if (item == null) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(item.getPrecoUnit())
                .subtract(BigDecimal.valueOf(item.getVlDescItem()))
                .max(BigDecimal.ZERO)
                .multiply(BigDecimal.valueOf(item.getQtcomp()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularValorIcmsItemPayload(ItemPedidos item, BigDecimal valorLiquidoItem) {
        if (item == null || valorLiquidoItem == null || valorLiquidoItem.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (item.getIcmsRed() == 0) {
            return valorLiquidoItem
                    .multiply(BigDecimal.valueOf(item.getIcms()))
                    .divide(BigDecimal.valueOf(100d), 6, RoundingMode.HALF_EVEN);
        }

        BigDecimal baseReduzida = valorLiquidoItem.subtract(
                valorLiquidoItem
                        .multiply(BigDecimal.valueOf(item.getIcmsRed()))
                        .divide(BigDecimal.valueOf(100d), 6, RoundingMode.HALF_EVEN)
        );

        return baseReduzida
                .multiply(BigDecimal.valueOf(item.getIcms()))
                .divide(BigDecimal.valueOf(100d), 6, RoundingMode.HALF_EVEN);
    }

    private boolean isCondicaoAtualAPrazo(Pedidos pedidoRef) {
        String codigo = condicao_selecionada;
        String descricao = obterDescricaoCondicaoAtual();
        if ((codigo == null || codigo.trim().isEmpty() || "N".equalsIgnoreCase(codigo))
                && pedidoRef != null) {
            if (pedidoRef.getCondPagamento() != null) {
                codigo = pedidoRef.getCondPagamento().getCondpgto();
                descricao = pedidoRef.getCondPagamento().getDescricao();
            }
            if ((codigo == null || codigo.trim().isEmpty() || "N".equalsIgnoreCase(codigo))
                    && pedidoRef.getFinanceiro() != null
                    && !pedidoRef.getFinanceiro().isEmpty()
                    && pedidoRef.getFinanceiro().get(0) != null) {
                codigo = pedidoRef.getFinanceiro().get(0).getCondpgto();
                descricao = pedidoRef.getFinanceiro().get(0).getCondicao();
            }
        }
        return isCondicaoAPrazo(codigo, descricao, pedidoRef);
    }

    private boolean isPixLinkPedidoSelecionado(Pedidos pedidoRef) {
        if (pedidoRef != null && pedidoRef.getFinanceiro() != null && !pedidoRef.getFinanceiro().isEmpty()) {
            for (Financeiro fin : pedidoRef.getFinanceiro()) {
                if (fin != null && "87".equals(fin.getCondpgto()) && (fin.getForma() == 142 || fin.getCodEve() == 142)) {
                    return true;
                }
            }
        }

        return isPixLinkPgtoLinkSelecionado();
    }

    private Pedidos clonarPedido(Pedidos pedidoBase) {
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

    private Pedidos normalizarPedidoBaseAvista(Pedidos pedidoBase) {
        Pedidos pedidoNormalizado = clonarPedido(pedidoBase);
        if (pedidoNormalizado == null || pedidoNormalizado.getItens() == null) {
            return pedidoNormalizado;
        }

        for (ItemPedidos item : pedidoNormalizado.getItens()) {
            if (item == null) {
                continue;
            }
            item.setPrecoUnit(
                    getPrecoUnitarioBrutoBaseVista(item)
                            .setScale(6, RoundingMode.HALF_EVEN)
                            .doubleValue()
            );
        }

        return pedidoNormalizado;
    }

    private BigDecimal obterSubtotalOriginalPrazoCarregado(Pedidos pedidoPrazoOriginal) {
        if (pedidoPrazoOriginal == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal totalOriginal = BigDecimal.valueOf(Math.max(0.0d, pedidoPrazoOriginal.getVlTotal()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal freteOriginal = BigDecimal.valueOf(Math.max(0.0d, pedidoPrazoOriginal.getVlFrete()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal servicosOriginais = BigDecimal.valueOf(Math.max(0.0d, pedidoPrazoOriginal.getVlServicosAdicionais()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotalItensCalculado = calcularSubtotalItensOriginalPrazo(pedidoPrazoOriginal);

        BigDecimal subtotalSemFreteEServicos = totalOriginal.subtract(freteOriginal).subtract(servicosOriginais)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotalSemFrete = totalOriginal.subtract(freteOriginal)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotalSemAbatimento = totalOriginal.setScale(2, RoundingMode.HALF_UP);

        if (subtotalSemFreteEServicos.compareTo(BigDecimal.ZERO) < 0) {
            subtotalSemFreteEServicos = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (subtotalSemFrete.compareTo(BigDecimal.ZERO) < 0) {
            subtotalSemFrete = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }


        BigDecimal fatorDeDiferencaMaximaPermitida = new BigDecimal("0.10"); // 10% de tolerancia para /0.95 de motor
        BigDecimal subtotalEsperadoViaCampos = subtotalSemFreteEServicos;
        BigDecimal diferencaAbsoluta = subtotalEsperadoViaCampos.subtract(subtotalItensCalculado).abs();
        boolean baseEstaContaminada = false;
        String motivoDescontaminacao = "";

        // Calcula a diferença percentual em relacao ao menor valor para evitar div/0
        BigDecimal referenciaParaPercentual = subtotalItensCalculado.compareTo(BigDecimal.ZERO) > 0
                ? subtotalItensCalculado
                : BigDecimal.ONE;
        BigDecimal diferencaPercentual = diferencaAbsoluta.divide(referenciaParaPercentual, 4, RoundingMode.HALF_EVEN);

        if (diferencaPercentual.compareTo(fatorDeDiferencaMaximaPermitida) > 0) {
            // Inconsistencia detectada! O VlTotal do pedido nao bate com VlItens + VlFrete + VlServicos.
            // Isso significa que o objeto veio contaminado (ex: frete novo digitado atualizou VlTotal
            // mas a base clonada nao teve VlFrete atualizado correspondentemente).
            baseEstaContaminada = true;
            motivoDescontaminacao = "INCONSISTENCIA detectada: |subtotalSemFreteEServicos(" + subtotalSemFreteEServicos
                    + ") - subtotalItensCalculado(" + subtotalItensCalculado + ")| = " + diferencaAbsoluta
                    + " (" + diferencaPercentual.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_EVEN) + "%)"
                    + " > 10% → usando soma item-a-item como verdade absoluta.";
        }

        BigDecimal subtotalOriginal;
        String estrategiaEscolhida;

        if (baseEstaContaminada) {
            // Usa o somatorio item-a-item (sempre confiavel, pois recalcula preco x qtde item por item)
            subtotalOriginal = subtotalItensCalculado;
            estrategiaEscolhida = "DESCONTAMINACAO_item-a-item | " + motivoDescontaminacao;
        } else {
            // Disputa normal entre as duas estrategias que realmente deduzem frete
            subtotalOriginal = subtotalSemFreteEServicos;
            estrategiaEscolhida = "total-frete-servicos";
            BigDecimal menorDiferenca = subtotalSemFreteEServicos.subtract(subtotalItensCalculado).abs();

            BigDecimal diferencaSemFrete = subtotalSemFrete.subtract(subtotalItensCalculado).abs();
            if (diferencaSemFrete.compareTo(menorDiferenca) < 0) {
                subtotalOriginal = subtotalSemFrete;
                estrategiaEscolhida = "total-frete";
                menorDiferenca = diferencaSemFrete;
            }
        }
        // --- estrategia "total-sem-abatimento" REMOVIDA (corrige duplicacao de frete) ---

        Log.d("FreteCompatDebug",
                "Subtotal original prazo detectado"
                        + " | pedido=" + pedidoPrazoOriginal.getNumpedven()
                        + " | totalOriginal=" + totalOriginal
                        + " | freteOriginal=" + freteOriginal
                        + " | servicosOriginais=" + servicosOriginais
                        + " | subtotalItensCalculado=" + subtotalItensCalculado
                        + " | subtotalSemFreteEServicos=" + subtotalSemFreteEServicos
                        + " | subtotalSemFrete=" + subtotalSemFrete
                        + " | subtotalSemAbatimento=" + subtotalSemAbatimento
                        + " | estrategia=" + estrategiaEscolhida
                        + " | subtotalEscolhido=" + subtotalOriginal);

        return subtotalOriginal;
    }

    private String montarObservacaoPoliticaDesconto() {
        if (usuarioLogado == null) {
            return "";
        }

        String cargo = usuarioLogado.get("cargo") != null ? usuarioLogado.get("cargo").trim() : "";
        boolean isGerenteUsuario = "S".equalsIgnoreCase(usuarioLogado.get("isgerente"))
                || "SIM".equalsIgnoreCase(usuarioLogado.get("isgerente"));

        if (cargo.isEmpty()) {
            return isGerenteUsuario ? "GERENTE" : "";
        }

        return isGerenteUsuario ? "GERENTE|CARGO:" + cargo : "CARGO:" + cargo;
    }

    private boolean isGerenteUsuarioLogado() {
        String isGerente = usuarioLogado.get("isgerente");
        return isGerente != null
                && ("SIM".equalsIgnoreCase(isGerente.trim())
                || "S".equalsIgnoreCase(isGerente.trim())
                || "TRUE".equalsIgnoreCase(isGerente.trim()));
    }

    private BigDecimal calcularSubtotalItensOriginalPrazo(Pedidos pedidoPrazoOriginal) {
        if (pedidoPrazoOriginal == null || pedidoPrazoOriginal.getItens() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        String codigoCondicao = possuiTexto(condicaoOriginalCodigo)
                ? condicaoOriginalCodigo
                : obterCodigoCondicaoOriginalPedido(pedidoPrazoOriginal);
        String descricaoCondicao = possuiTexto(condicaoOriginalDescricao)
                ? condicaoOriginalDescricao
                : obterDescricaoCondicaoOriginalPedido(pedidoPrazoOriginal);

        BigDecimal subtotalItens = BigDecimal.ZERO;
        for (ItemPedidos item : pedidoPrazoOriginal.getItens()) {
            if (item == null) {
                continue;
            }
            subtotalItens = subtotalItens.add(
                    calcularTotalItemConsiderandoPromocaoPrazo(item, codigoCondicao, descricaoCondicao)
            );
        }

        return subtotalItens.setScale(2, RoundingMode.HALF_UP);
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

    private void logParametrosPedidoFinalizacao(String etapa, Pedidos pedidoEnvio) {
        final String tag = "PedidoEnvioDebug";

        if (pedidoEnvio == null) {
            Log.d(tag, etapa + " pedidoEnvio=null");
            return;
        }

        Log.d(tag, "===== INICIO PARAMETROS FINALIZACAO PEDIDO [" + etapa + "] =====");
        Log.d(tag,
                "cabecalho="
                        + " numpedven=" + pedidoEnvio.getNumpedven()
                        + " codfil=" + pedidoEnvio.getCodfil()
                        + " codvendr=" + pedidoEnvio.getCodvendr()
                        + " codcli=" + (pedidoEnvio.getCliente() != null ? pedidoEnvio.getCliente().getCodcli() : 0)
                        + " tpnota=" + (pedidoEnvio.getTpnota() != null ? pedidoEnvio.getTpnota().getCodTpNota() : 0)
                        + " formaPagamento=" + (pedidoEnvio.getFormaPagamento() != null ? pedidoEnvio.getFormaPagamento().getForma() : "null")
                        + " formaPagamentoDesc=" + (pedidoEnvio.getFormaPagamento() != null ? pedidoEnvio.getFormaPagamento().getDescricao() : "null")
                        + " condicaoSelecionada=" + condicao_selecionada
                        + " descricaoCondicao=" + obterDescricaoCondicaoAtual()
                        + " condpgtoPedido=" + (pedidoEnvio.getCondPagamento() != null ? pedidoEnvio.getCondPagamento().getCondpgto() : "null")
                        + " descCondpgtoPedido=" + (pedidoEnvio.getCondPagamento() != null ? pedidoEnvio.getCondPagamento().getDescricao() : "null")
                        + " codMidia=" + pedidoEnvio.getCodMidia()
                        + " codStatus=" + pedidoEnvio.getCodStatus()
                        + " endfat=" + pedidoEnvio.getEndfat()
                        + " endcob=" + pedidoEnvio.getEndcob()
                        + " endent=" + pedidoEnvio.getEndent()
                        + " codmodal=" + pedidoEnvio.getCodmodal()
                        + " tppedPedido=" + pedidoEnvio.getTpped()
                        + " transferencia=" + pedidoEnvio.getTransferencia()
                        + " vlTotal=" + pedidoEnvio.getVlTotal()
                        + " vlFrete=" + pedidoEnvio.getVlFrete()
                        + " vlServicos=" + pedidoEnvio.getVlServicosAdicionais()
                        + " vlDesconto=" + pedidoEnvio.getVlDesconto()
                        + " pmargRent=" + pedidoEnvio.getPmargRent()
                        + " vlEntrada=" + pedidoEnvio.getVlEntrada()
                        + " vlPrc=" + pedidoEnvio.getVlPrc()
                        + " qtprc=" + pedidoEnvio.getQtprc()
                        + " obs=" + pedidoEnvio.getObs()
                        + " obsAdicional=" + pedidoEnvio.getObsAdicional()
                        + " isMotor=" + pedidoEnvio.getIsMotor()
                        + " pixLinkSelecionado=" + isPixLinkPedidoSelecionado(pedidoEnvio)
        );

        if (pedidoEnvio.getItens() != null) {
            for (int i = 0; i < pedidoEnvio.getItens().size(); i++) {
                ItemPedidos item = pedidoEnvio.getItens().get(i);
                if (item == null) {
                    Log.d(tag, "item[" + i + "]=null");
                    continue;
                }

                Log.d(tag,
                        "item[" + i + "]="
                                + " codprod=" + item.getCoditprod()
                                + " codbarra=" + item.getCodbarras()
                                + " descricao=" + item.getDescricao()
                                + " qtcomp=" + item.getQtcomp()
                                + " precoUnit=" + item.getPrecoUnit()
                                + " vlDescItem=" + item.getVlDescItem()
                                + " pmargRent=" + item.getPmargRent()
                                + " dpi=" + item.getDpi()
                                + " vlFrete=" + item.getVlFrete()
                                + " tpped=" + item.getTpped()
                                + " situacao=" + item.getSituacao()
                                + " codFilOrig=" + item.getCodFilOrig()
                                + " codFilTransf=" + item.getCodFilTransf()
                                + " encomenda=" + item.getEncomenda()
                                + " isMotor=" + item.getIsMotor()
                                + " codMotivo=" + item.getCodMotivo()
                                + " motivoDesc=" + item.getMotivoDesc()
                );
            }
        } else {
            Log.d(tag, "itens=null");
        }

        if (pedidoEnvio.getFinanceiro() != null) {
            for (int i = 0; i < pedidoEnvio.getFinanceiro().size(); i++) {
                Financeiro fin = pedidoEnvio.getFinanceiro().get(i);
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
                                + " evento=" + fin.getEvento()
                                + " flautcre=" + fin.getFlautcre()
                                + " dtvcto=" + fin.getDtvcto()
                );
            }
        } else {
            Log.d(tag, "financeiro=null");
        }

        logTextoLongo(tag, "pedido_json[" + etapa + "]=", new Gson().toJson(pedidoEnvio));
        Log.d(tag, "===== FIM PARAMETROS FINALIZACAO PEDIDO [" + etapa + "] =====");
    }

    private boolean isItemPromocional(ItemPedidos item) {
        if (item == null) return false;
        return isItemPromocionalPorSituacao(item)
                || temDescontoMotorPromocional(item)
                || isMotorPromocional(item.getIsMotor());
    }

    private boolean itemTemReajustePromocionalPrazo(ItemPedidos item) {
        if (item == null) {
            return false;
        }
        return isItemPromocionalPorSituacao(item) || isMotorPromocional(item.getIsMotor());
    }

    private boolean deveUsarTotalRecalculadoDosItens(Pedidos pedidoRef) {
        if (pedidoAlteradoLocalmente) {
            return true;
        }
        if (pedidoRef == null || pedidoRef.getItens() == null || pedidoRef.getItens().isEmpty()) {
            return false;
        }
        for (ItemPedidos item : pedidoRef.getItens()) {
            if (itemTemReajustePromocionalPrazo(item) || isMotorPromocional(item != null ? item.getIsMotor() : null)) {
                return true;
            }
        }
        return false;
    }

    private JSONObject buildPedidoDebugSnapshot(String fase, Pedidos pedidoRef) {
        JSONObject data = new JSONObject();
        try {
            data.put("fase", fase);
            data.put("numpedven", pedidoRef != null ? pedidoRef.getNumpedven() : 0);
            data.put("vlTotal", pedidoRef != null ? pedidoRef.getVlTotal() : 0);
            data.put("vlFrete", pedidoRef != null ? pedidoRef.getVlFrete() : 0);
            data.put("vlServicos", pedidoRef != null ? pedidoRef.getVlServicosAdicionais() : 0);
            data.put("itens", pedidoRef != null && pedidoRef.getItens() != null ? pedidoRef.getItens().size() : 0);

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

    private void reportDebugEvent(String hypothesisId, String location, String msg, JSONObject data) {
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

    private boolean isItemPromocionalPorSituacao(ItemPedidos item) {
        return item != null && item.getSituacao() != null && "PR".equalsIgnoreCase(item.getSituacao().trim());
    }

    private boolean temDescontoMotorPromocional(ItemPedidos item) {
        if (item == null || !isMotorPromocional(item.getIsMotor())) {
            return false;
        }

        return item.getVlDescItem() > 0
                || item.getVldescPromo() > 0
                || possuiTexto(item.getCodigoPromo())
                || possuiTexto(item.getNomePromo());
    }

    private boolean isMotorPromocional(String valor) {
        return possuiTexto(valor) && ("S".equalsIgnoreCase(valor.trim()) || "SIM".equalsIgnoreCase(valor.trim()));
    }

    private boolean itemEhDescontoP2OuMotorPromocional(ItemPedidos item) {
        return item != null && isMotorPromocional(item.getIsMotor());
    }

    private boolean pedidoDispensaLiberacaoPorP2OuMotor() {
        if (pedido == null || pedido.getItens() == null) {
            return isMotorPromocional(pedido != null ? pedido.getIsMotor() : null);
        }
        if (isMotorPromocional(pedido.getIsMotor())) return true;
        for (ItemPedidos item : pedido.getItens()) {
            if (itemEhDescontoP2OuMotorPromocional(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean pedidoPossuiItemMotorPromocional() {
        if (pedido == null || pedido.getItens() == null) {
            return false;
        }

        for (ItemPedidos item : pedido.getItens()) {
            if (item != null && isMotorPromocional(item.getIsMotor())) {
                return true;
            }
        }

        return isMotorPromocional(pedido.getIsMotor());
    }

    private void sincronizarFlagMotorNoPedido() {
        if (pedido != null && pedidoPossuiItemMotorPromocional()) {
            pedido.setIsMotor("S");
        }
    }

    private boolean possuiTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private boolean itemTemDescontoManualDiferenteDeP2(ItemPedidos item, BigDecimal precoBaseVista, BigDecimal preco2) {
        return false;
    }

    private BigDecimal getPrecoUnitarioBrutoParaCondicao(ItemPedidos item, String condicaoCodigo, String condicaoDescricao) {
        if (itemTemReajustePromocionalPrazo(item) && isCondicaoAPrazo(condicaoCodigo, condicaoDescricao)) {
            return calcularPrecoUnitarioBrutoPromocionalPrazo(item);
        }
        return getPrecoUnitarioBrutoBaseVista(item);
    }

    private BigDecimal getPrecoUnitarioEnvioApi(ItemPedidos item) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }

        return getPrecoUnitarioBrutoBaseVista(item).setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal getPrecoUnitarioItemEnvioApi(ItemPedidos item, boolean condicaoAPrazo) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_EVEN);
        }
        if (condicaoAPrazo && itemTemReajustePromocionalPrazo(item)) {
            return calcularPrecoUnitarioBrutoPromocionalPrazo(item).setScale(6, RoundingMode.HALF_EVEN);
        }
        return getPrecoUnitarioBrutoBaseVista(item).setScale(6, RoundingMode.HALF_EVEN);
    }

    private BigDecimal getDescontoUnitarioEnvioApi(ItemPedidos item, boolean condicaoAPrazo) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_EVEN);
        }
        if (itemTemReajustePromocionalPrazo(item)) {
            if (condicaoAPrazo && isMotorPromocional(item.getIsMotor())) {
                return calcularDescontoUnitarioPromocionalPrazoParaEnvioApi(item);
            }
            return getDescontoVistaOriginalPromocional(item).setScale(6, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem())).setScale(6, RoundingMode.HALF_EVEN);
    }

    private boolean deveEnviarAcrescimoPrazoNoPrecoUnitarioItem(ItemPedidos item, boolean condicaoAPrazo) {
        if (!condicaoAPrazo || item == null) {
            return false;
        }

        return isMotorPromocional(item.getIsMotor());
    }

    private BigDecimal calcularDescontoUnitarioPromocionalPrazoParaEnvioApi(ItemPedidos item) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_EVEN);
        }

        BigDecimal precoBaseVista = getPrecoUnitarioBrutoBaseVista(item).setScale(6, RoundingMode.HALF_EVEN);
        BigDecimal liquidoPrazo = calcularPrecoUnitarioLiquidoPromocionalPrazo(item);
        return precoBaseVista.subtract(liquidoPrazo)
                .max(BigDecimal.ZERO)
                .setScale(6, RoundingMode.HALF_EVEN);
    }

    private BigDecimal getDescontoVistaOriginalPromocional(ItemPedidos item) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_EVEN);
        }

        BigDecimal descontoAtual = BigDecimal.valueOf(Math.max(0.0d, item.getVlDescItem())).setScale(6, RoundingMode.HALF_EVEN);
        if (!isMotorPromocional(item.getIsMotor())) {
            return descontoAtual;
        }

        BigDecimal descontoPromocionalOriginal = BigDecimal.valueOf(Math.max(0.0d, item.getVldescPromo())).setScale(6, RoundingMode.HALF_EVEN);
        if (descontoPromocionalOriginal.compareTo(BigDecimal.ZERO) <= 0 && descontoAtual.compareTo(BigDecimal.ZERO) > 0) {
            item.setVldescPromo(descontoAtual.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
            descontoPromocionalOriginal = descontoAtual;
        }
        return descontoPromocionalOriginal.compareTo(BigDecimal.ZERO) > 0 ? descontoPromocionalOriginal : descontoAtual;
    }

    private BigDecimal calcularValorLiquidoTotalAlvoItemEnvioApi(ItemPedidos item, boolean condicaoAPrazo) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }


        BigDecimal precoUnitAtual;
        BigDecimal descontoUnitAtual;
        if (condicaoAPrazo && isMotorPromocional(item.getIsMotor())) {
            precoUnitAtual = getPrecoUnitarioBrutoBaseVista(item).setScale(6, RoundingMode.HALF_EVEN);
            descontoUnitAtual = calcularDescontoUnitarioPromocionalPrazoParaEnvioApi(item);
        } else {
            precoUnitAtual = BigDecimal.valueOf(item.getPrecoUnit()).setScale(6, RoundingMode.HALF_EVEN);
            descontoUnitAtual = BigDecimal.valueOf(item.getVlDescItem()).setScale(6, RoundingMode.HALF_EVEN);
        }
        BigDecimal qtde = BigDecimal.valueOf(item.getQtcomp()).setScale(6, RoundingMode.HALF_EVEN);

        return precoUnitAtual
                .subtract(descontoUnitAtual)
                .max(BigDecimal.ZERO)
                .multiply(qtde)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal getPrecoUnitarioBrutoBaseVista(ItemPedidos item) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_EVEN);
        }

        if (item.getPrecoPrimario() > 0d) {
            BigDecimal pp = BigDecimal.valueOf(item.getPrecoPrimario()).setScale(2, RoundingMode.HALF_EVEN);
            return pp.setScale(6, RoundingMode.HALF_EVEN);
        }
        BigDecimal precoAtual = BigDecimal.valueOf(item.getPrecoUnit());
        if (item.getPrecoPrimario() <= 0d) {
            item.setPrecoPrimario(precoAtual.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
        }
        return precoAtual.setScale(6, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calcularPrecoUnitarioBrutoPromocionalPrazo(BigDecimal precoBrutoBaseVista, ItemPedidos item) {
        if (itemTemReajustePromocionalPrazo(item) && isMotorPromocional(item != null ? item.getIsMotor() : null)) {
            return precoBrutoBaseVista.setScale(6, RoundingMode.HALF_EVEN);
        }
        return calcularPrecoUnitarioLiquidoPromocionalPrazo(item)
                .add(getDescontoVistaOriginalPromocional(item))
                .setScale(6, RoundingMode.HALF_EVEN);
    }

    private boolean isMesmoItem(ItemPedidos original, ItemPedidos candidato, int indiceOriginal, int indiceCandidato) {
        if (original == null || candidato == null) return false;
        if (indiceOriginal != indiceCandidato) return false;
        return original.getCoditprod() == candidato.getCoditprod();
    }

    private void copiarDescontosLocaisItens(Pedidos pedidoFonte, Pedidos pedidoDestino) {
        if (pedidoFonte == null || pedidoDestino == null
                || pedidoFonte.getItens() == null || pedidoDestino.getItens() == null) {
            return;
        }

        List<ItemPedidos> itensOrigem = pedidoFonte.getItens();
        List<ItemPedidos> itensDestino = pedidoDestino.getItens();

        for (int i = 0; i < itensOrigem.size(); i++) {
            ItemPedidos origem = itensOrigem.get(i);
            if (origem == null) continue;

            for (int j = 0; j < itensDestino.size(); j++) {
                ItemPedidos destino = itensDestino.get(j);
                if (destino == null) continue;
                if (!isMesmoItem(origem, destino, i, j)) continue;

                if (origem.getPrecoPrimario() > 0d) {
                    destino.setPrecoPrimario(origem.getPrecoPrimario());
                }
                if (origem.getVlDescItem() > 0d) {
                    destino.setVlDescItem(origem.getVlDescItem());
                }
                if (origem.getDpi() > 0d) {
                    destino.setDpi(origem.getDpi());
                }
                if (origem.getCodMotivo() != null && !origem.getCodMotivo().trim().isEmpty()) {
                    destino.setCodMotivo(origem.getCodMotivo());
                }
                if (origem.getMotivoDesc() != null && !origem.getMotivoDesc().trim().isEmpty()) {
                    destino.setMotivoDesc(origem.getMotivoDesc());
                }
                break;
            }
        }
    }

    private void aplicarPrecoDaCondicaoNosItens(Pedidos pedidoRef, String condicaoCodigo, String condicaoDescricao) {
    }

    // Helper para usar precoBaseVista FIXO (forçado), sem depender de getPrecoUnitarioBrutoBaseVista
    // que poderia variar. Chamado por aplicarPrecoDaCondicaoNosItens agora.
    private BigDecimal getPrecoUnitarioBrutoParaCondicaoFixado(ItemPedidos item, String condicaoCodigo, String condicaoDescricao, BigDecimal precoBaseVistaFixado) {
        if (itemTemReajustePromocionalPrazo(item) && isCondicaoAPrazo(condicaoCodigo, condicaoDescricao)) {
            if (isMotorPromocional(item.getIsMotor())) {
                return precoBaseVistaFixado;
            }
            return calcularPrecoUnitarioLiquidoPromocionalPrazo(item)
                    .add(getDescontoVistaOriginalPromocional(item))
                    .setScale(6, RoundingMode.HALF_EVEN);
        }
        return precoBaseVistaFixado;
    }

    private void aplicarVlDescItemDpiEMotivoPreco2NosItens(Pedidos pedidoRef, String condicaoCodigo, String condicaoDescricao) {
        // Ajustes automáticos por Preco2/P2 removidos.
    }

    private BigDecimal reverterPrecoUnitarioBrutoPromocionalPrazo(BigDecimal precoBrutoAtual, ItemPedidos item) {
        return precoBrutoAtual.setScale(6, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calcularTotalPedidoPorBasePromocional(Pedidos pedidoRef, boolean converterPromocionalPrazoParaVista) {
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalItensBruto = BigDecimal.ZERO;
        BigDecimal totalDesconto = BigDecimal.ZERO;

        for (ItemPedidos item : pedidoRef.getItens()) {
            if (item == null) {
                continue;
            }

            BigDecimal precoUnitario = BigDecimal.valueOf(item.getPrecoUnit());

            totalItensBruto = totalItensBruto.add(precoUnitario.multiply(BigDecimal.valueOf(item.getQtcomp())));
            totalDesconto = totalDesconto.add(BigDecimal.valueOf(item.getVlDescItem()).multiply(BigDecimal.valueOf(item.getQtcomp())));
        }

        if (pedidoRef.getVlDescPromo() > 0) {
            totalDesconto = totalDesconto.add(BigDecimal.valueOf(pedidoRef.getVlDescPromo()));
        }

        BigDecimal frete = BigDecimal.valueOf(Math.max(0.0d, pedidoRef.getVlFrete()));
        BigDecimal servicos = BigDecimal.valueOf(Math.max(0.0d, pedidoRef.getVlServicosAdicionais()));
        return totalItensBruto.subtract(totalDesconto).add(frete).add(servicos).setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal obterValorReferenciaPedido(Pedidos pedidoRef) {
        if (pedidoRef == null) {
            return BigDecimal.ZERO;
        }

        try {
            if (pedidoRef.getFinanceiro() != null && !pedidoRef.getFinanceiro().isEmpty()) {
                Financeiro financeiro = pedidoRef.getFinanceiro().get(0);
                if (financeiro != null && financeiro.getValor() > 0.0d) {
                    return BigDecimal.valueOf(financeiro.getValor()).setScale(2, RoundingMode.HALF_EVEN);
                }
            }
        } catch (Exception ignore) {
        }

        return BigDecimal.valueOf(Math.max(0.0d, pedidoRef.getVlTotal())).setScale(2, RoundingMode.HALF_EVEN);
    }

    private boolean pedidoPixLinkTemItensComBasePrazo(Pedidos pedidoRef) {
        return false;
    }

    private boolean isPedidoOriginalAPrazo() {
        if (pedido == null) {
            return false;
        }

        try {
            if (pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
                Financeiro financeiro = pedido.getFinanceiro().get(0);
                if (financeiro != null && isCondicaoAPrazo(financeiro.getCondpgto(), financeiro.getCondicao(), financeiro)) {
                    return true;
                }
            }
        } catch (Exception ignore) {
        }

        try {
            if (pedido.getCondPagamento() != null) {
                return isCondicaoAPrazo(
                        pedido.getCondPagamento().getCondpgto(),
                        pedido.getCondPagamento().getDescricao(),
                        pedido
                );
            }
        } catch (Exception ignore) {
        }

        return false;
    }

    private boolean pedidoTemPrecoPromocionalJaComAcrescimoPrazo(Pedidos pedidoRef) {
        return false;
    }

    private BigDecimal calcularTotalItensComparacao(Pedidos pedidoRef, boolean aplicarRegraPromocional) {
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }

        BigDecimal totalItens = BigDecimal.ZERO;

        for (ItemPedidos item : pedidoRef.getItens()) {
            if (item == null) {
                continue;
            }

            totalItens = totalItens.add(calcularValorLiquidoItemPayload(item));
        }

        BigDecimal frete = BigDecimal.valueOf(Math.max(0.0d, pedidoRef.getVlFrete()));
        BigDecimal servicos = BigDecimal.valueOf(Math.max(0.0d, pedidoRef.getVlServicosAdicionais()));
        BigDecimal descontoPromo = BigDecimal.valueOf(Math.max(0.0d, pedidoRef.getVlDescPromo()));
        return totalItens.subtract(descontoPromo).add(frete).add(servicos).setScale(2, RoundingMode.HALF_EVEN);
    }

    private void logDiagnosticoPromocaoAPrazo(String origem, Pedidos pedidoRef, String condicaoCodigo, String condicaoDescricao) {
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            Log.d("PromoAPrazoDebug", origem + " | pedido nulo ou sem itens");
            return;
        }

        boolean condicaoAPrazo = isCondicaoAPrazo(condicaoCodigo, condicaoDescricao, pedidoRef);
        BigDecimal totalAtualItens = BigDecimal.ZERO;
        BigDecimal totalEsperadoItens = BigDecimal.ZERO;
        BigDecimal totalAtualPromocional = BigDecimal.ZERO;
        BigDecimal totalEsperadoPromocional = BigDecimal.ZERO;
        int qtdItensPromocionais = 0;
        StringBuilder itensPromocionais = new StringBuilder();

        for (ItemPedidos item : pedidoRef.getItens()) {
            BigDecimal valorLiquidoItem = calcularValorLiquidoItemPayload(item);
            BigDecimal valorEsperadoItem = valorLiquidoItem;
            BigDecimal valorMotor095Item = valorLiquidoItem;
            boolean itemPromocional = isItemPromocional(item);

            if (itemPromocional) {
                qtdItensPromocionais++;
                totalAtualPromocional = totalAtualPromocional.add(valorLiquidoItem);

                if (condicaoAPrazo) {
                    valorMotor095Item = calcularTotalItemConsiderandoPromocaoPrazo(item, condicaoCodigo, condicaoDescricao)
                            .setScale(2, RoundingMode.HALF_EVEN);
                    valorEsperadoItem = valorMotor095Item;
                }

                totalEsperadoPromocional = totalEsperadoPromocional.add(valorEsperadoItem);
                itensPromocionais.append("[cod=").append(item.getCoditprod())
                        .append(", qtd=").append(item.getQtcomp())
                        .append(", sit=").append(item.getSituacao())
                        .append(", desc=").append(BigDecimal.valueOf(item.getVlDescItem()).setScale(3, RoundingMode.HALF_EVEN))
                        .append(", atual=").append(valorLiquidoItem.setScale(2, RoundingMode.HALF_EVEN))
                        .append(", motor095=").append(valorMotor095Item.setScale(2, RoundingMode.HALF_EVEN))
                        .append(", esperado=").append(valorEsperadoItem.setScale(2, RoundingMode.HALF_EVEN))
                        .append("] ");
            }

            totalAtualItens = totalAtualItens.add(valorLiquidoItem);
            totalEsperadoItens = totalEsperadoItens.add(valorEsperadoItem);
        }

        Log.d("PromoAPrazoDebug",
                origem
                        + " | pedido=" + pedidoRef.getNumpedven()
                        + " | condCodigo=" + condicaoCodigo
                        + " | condDesc=" + condicaoDescricao
                        + " | isAPrazo=" + condicaoAPrazo
                        + " | regraMotor095Ativa=" + condicaoAPrazo
                        + " | itensPR=" + qtdItensPromocionais
                        + " | totalItensAtual=" + totalAtualItens.setScale(2, RoundingMode.HALF_EVEN)
                        + " | totalItensEsperado=" + totalEsperadoItens.setScale(2, RoundingMode.HALF_EVEN)
                        + " | totalPRAtual=" + totalAtualPromocional.setScale(2, RoundingMode.HALF_EVEN)
                        + " | totalPREsperado=" + totalEsperadoPromocional.setScale(2, RoundingMode.HALF_EVEN)
                        + " | vlTotalPedido=" + BigDecimal.valueOf(pedidoRef.getVlTotal()).setScale(2, RoundingMode.HALF_EVEN)
        );

        if (qtdItensPromocionais > 0) {
            Log.d("PromoAPrazoDebug", origem + " | detalhes_itens_PR=" + itensPromocionais);
        }
    }

    private boolean isCondicaoAPrazo(String condicaoCodigo, String condicaoDescricao) {
        if (FikonVendasClient.ativo()) {
            // As regras de preço "a prazo" (Preco2, condição "01") são do sistema antigo.
            // Na Fikon o preço por condição é calculado e validado pelo ERP.
            return false;
        }
        if (condicaoDescricao != null && condicaoDescricao.toUpperCase().contains("A PRAZO")) {
            return true;
        }

        if (condicaoCodigo != null && !condicaoCodigo.trim().isEmpty()) {
            return !"01".equals(condicaoCodigo.trim());
        }

        return false;
    }

    private boolean isCondicaoAPrazo(String condicaoCodigo, String condicaoDescricao, Pedidos pedidoRef) {
        if ("87".equals(condicaoCodigo) && isPixLinkPedidoSelecionado(pedidoRef)) {
            return false;
        }

        return isCondicaoAPrazo(condicaoCodigo, condicaoDescricao);
    }

    private boolean isCondicaoAPrazo(String condicaoCodigo, String condicaoDescricao, Financeiro financeiroRef) {
        if (financeiroRef != null && "87".equals(financeiroRef.getCondpgto())
                && (financeiroRef.getForma() == 142 || financeiroRef.getCodEve() == 142)) {
            return false;
        }

        return isCondicaoAPrazo(condicaoCodigo, condicaoDescricao);
    }

    private void abrirPopupFormaPagamentoComAviso(Pedidos pedidoParaUsar, Pedidos pedidoAvistaRef, double valorTotalAtualizado, String condicaoCodigo, String condicaoDescricao) {
        if (FikonVendasClient.ativo()) {
            // A forma de pagamento já é a condição Fikon escolhida; falta só o nº de parcelas.
            FikonPagamentoDialog.mostrar(this, pedidoParaUsar, valorTotalAtualizado, condicaoCodigo,
                    new FikonPagamentoDialog.Callback() {
                        @Override
                        public void onConfirmado(List<Financeiro> financeiro) {
                            FinanceiroAdapter.mList = financeiro;
                            if (pedidoParaUsar != null) {
                                pedidoParaUsar.setFinanceiro(financeiro);
                            }
                            if (pedido != null) {
                                pedido.setFinanceiro(financeiro);
                            }
                            layout_condpgto.setError(null);
                        }
                    });
            return;
        }
        Runnable abrirPopup = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PopupPedFechamento.this, PopupFormaPgto.class);
                intent.putExtra("pedido", pedidoParaUsar);
                intent.putExtra("pedidoAvista", pedidoAvistaRef);
                intent.putExtra("valorTotalAtualizado", valorTotalAtualizado);
                startActivity(intent);
                PopupFormaPgto.retornoCredito = null;
            }
        };

        if ("87".equals(condicaoCodigo)) {
            abrirPopup.run();
            return;
        }

        if (!isCondicaoAPrazo(condicaoCodigo, condicaoDescricao) || pedidoParaUsar == null || pedidoParaUsar.getItens() == null) {
            abrirPopup.run();
            return;
        }

        abrirPopup.run();
    }

    private String montarListaItensPromocionais(Pedidos pedidoRef) {
        return montarListaItensPromocionais(pedidoRef, false);
    }

    private String montarListaItensPromocionais(Pedidos pedidoRef, boolean apenasNovosLocais) {
        if (pedidoRef == null || pedidoRef.getItens() == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (ItemPedidos item : pedidoRef.getItens()) {
            if (item == null || !itemTemReajustePromocionalPrazo(item)) {
                continue;
            }
            if (apenasNovosLocais && item.getNumpedven() > 0) {
                continue;
            }

            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append("- ")
                    .append(item.getCoditprod())
                    .append(" - ")
                    .append(item.getDescricao());
        }
        return sb.toString();
    }

    private void salvarPedido(final Pedidos pedidoSave) {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Salvando...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {
            @Override
            public void run() {

                try {
                    logDiagnosticoPromocaoAPrazo(
                            "antes_helper_SetPedidos",
                            pedidoSave,
                            condicao_selecionada,
                            obterDescricaoCondicaoAtual()
                    );

                    Pedidos pedidoEnvio = new Gson().fromJson(new Gson().toJson(pedidoSave), Pedidos.class);
                    logParametrosPedidoFinalizacao("pedidoSave_popup", pedidoSave);
                    logParametrosPedidoFinalizacao("pedidoEnvio_clonado", pedidoEnvio);
                    logDiagnosticoPromocaoAPrazo(
                            "antes_helper_SetPedidos_envio",
                            pedidoEnvio,
                            condicao_selecionada,
                            obterDescricaoCondicaoAtual()
                    );

                    // Garantir que o pedidoSave contenha os IDs/total vindos do cache estático
                    try {
                        if (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed != null && !br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed.isEmpty()) {
                            pedidoEnvio.setServicosAdicionaisIds(new java.util.ArrayList<>(br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed));
                            android.util.Log.d("FreteDebug", "salvarPedido: aplicando ids do cache estatico antes de SetPedidos: " + br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed);
                        }
                        if ((pedidoEnvio.getVlServicosAdicionais() == 0.0d || Double.isNaN(pedidoEnvio.getVlServicosAdicionais())) && br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed > 0) {
                            pedidoEnvio.setVlServicosAdicionais(br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed);
                            android.util.Log.d("FreteDebug", "salvarPedido: aplicando servMemPed do cache estatico antes de SetPedidos: " + br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed);
                        }
                    } catch (Exception ex) {
                        android.util.Log.e("FreteDebug", "Erro ao aplicar valores do cache estatico em salvarPedido", ex);
                    }

                    android.util.Log.d("FreteDebug", "Chamando helper.SetPedidos com pedidoSave.numpedven=" + pedidoEnvio.getNumpedven() + ", ids=" + pedidoEnvio.getServicosAdicionaisIds() + ", vlServicos=" + pedidoEnvio.getVlServicosAdicionais());

                    java.util.List<Integer> idsServicosParaSalvar = null;
                    double totalServicosParaSalvar = 0.0d;

                    try {
                        double freteBaseInput = 0.0;
                        double servicosInput = 0.0;
                        try {
                            if (edtFrete != null && edtFrete.getText() != null) {
                                freteBaseInput = Double.parseDouble(edtFrete.getText().toString().replace(",","."));
                            }
                            if (edtServicosFrete != null && edtServicosFrete.getText() != null) {
                                servicosInput = Double.parseDouble(edtServicosFrete.getText().toString().replace(",","."));
                            }
                        } catch (Exception e) {
                            android.util.Log.w("FreteDebug", "Falha ao ler inputs de frete/servicos. Usando valores do pedido.");
                            freteBaseInput = pedidoEnvio.getVlFrete();
                            servicosInput = pedidoEnvio.getVlServicosAdicionais();
                        }

                        // Garante que o objeto a ser enviado contenha os dois componentes
                        pedidoEnvio.setVlServicosAdicionais(servicosInput);
                        pedidoEnvio.setVlFrete(freteBaseInput);
                        totalServicosParaSalvar = servicosInput;

                        java.util.List<Integer> idsAtual = null;
                        try {
                            if (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed != null) {
                                idsAtual = new java.util.ArrayList<>(br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed);
                            }
                        } catch (Exception ignore) {
                        }
                        if (idsAtual == null || idsAtual.isEmpty()) {
                            idsAtual = pedidoEnvio.getServicosAdicionaisIds();
                        }
                        if (idsAtual == null) {
                            idsAtual = new java.util.ArrayList<>();
                        }

                        try {
                            if (servicosInput <= 0.0d) {
                                idsAtual = new java.util.ArrayList<>();
                                pedidoEnvio.setServicosAdicionaisIds(idsAtual);
                            } else {
                                pedidoEnvio.setServicosAdicionaisIds(new java.util.ArrayList<>(idsAtual));
                            }
                        } catch (Exception ignore) {}

                        idsServicosParaSalvar = new java.util.ArrayList<>(idsAtual);

                        android.util.Log.d("FreteDebug", "Validando envio de frete: baseInput=" + freteBaseInput + ", servicosInput=" + servicosInput);
                    } catch (Exception ignore) {}

                    Pedidos pedidoEnvioPromocional = prepararPedidoParaEnvioComPrecoPromocional(pedidoEnvio);
                    logParametrosPedidoFinalizacao("pedidoEnvio_promocional", pedidoEnvioPromocional);
                    recalcularTotalPayloadPedidoApi(pedidoEnvioPromocional);
                    Pedidos pedidoEnvioApi = prepararPedidoCompativelParaSalvarNaApi(pedidoEnvioPromocional);
                    logParametrosPedidoFinalizacao("pedidoEnvio_api_final", pedidoEnvioApi);
                    retorno = helper.SetPedidos(pedidoEnvioApi);
                    android.util.Log.d("FreteDebug", "helper.SetPedidos retornou: " + retorno);
                    if (retorno != null && !retorno.contains("Pedido não cadastrado!") && !retorno.equals("N")) {
                        long numParsed = 0L;
                        try {
                            String numStr = retorno.split("\\.")[0].trim();
                            numParsed = Long.parseLong(numStr);
                            pedidoSave.setNumpedven(numParsed);
                        } catch (Exception ignore) {
                        }

                        try {
                            java.util.List<Integer> ids = idsServicosParaSalvar;
                            double totalServ = totalServicosParaSalvar;

                            if (totalServ <= 0.0d) {
                                ids = new java.util.ArrayList<>();
                                totalServ = 0.0d;
                            } else if (ids == null || ids.isEmpty()) {
                                try {
                                    if (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed != null && !br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed.isEmpty()) {
                                        ids = new java.util.ArrayList<>(br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed);
                                    }
                                } catch (Exception ignore) {
                                }
                            }

                            if (numParsed > 0) {
                                okhttp3.OkHttpClient clientLoc = new okhttp3.OkHttpClient.Builder()
                                        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                                        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS).build();
                                retrofit2.Retrofit retrofitLoc = new retrofit2.Retrofit.Builder()
                                        .baseUrl(br.com.acalfortaleza.fikonmais.service.ApiGemco.BASE_URL)
                                        .client(clientLoc)
                                        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                                        .build();
                                br.com.acalfortaleza.fikonmais.service.ApiGemco api = retrofitLoc.create(br.com.acalfortaleza.fikonmais.service.ApiGemco.class);
                                br.com.acalfortaleza.fikonmais.model.FreteServicoSelecionadoRequest req = new br.com.acalfortaleza.fikonmais.model.FreteServicoSelecionadoRequest();
                                if (ids == null) ids = new java.util.ArrayList<>();
                                if (Double.isNaN(totalServ)) totalServ = 0.0d;
                                req.setPedido(numParsed);
                                req.setIdsSelecionados(ids);
                                req.setValorTotal(totalServ);
                                String token = "Bearer " + br.com.acalfortaleza.fikonmais.service.ApiGemco.token;
                                retrofit2.Response<String> respSalvar = api.SalvarFreteServicoSelecionado(req, token).execute();
                                if (respSalvar.isSuccessful()) {
                                    br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisAlterados = false;
                                    try {
                                        br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed = totalServ;
                                        br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed = ids;
                                    } catch (Exception ignore) {
                                    }
                                }
                            }
                        } catch (Exception ignore) {
                        }
                        erroRegistroCupom = sincronizarRegistroCupomPedido(pedidoSave);
                        aplicarAgendamentoPendenteSeNecessario(pedidoSave, retorno);
                    }

                } catch (NetworkOnMainThreadException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            isSaving = false;
                            try { btn_finalizar.setEnabled(true); } catch (Exception ignore) {}

                            if (erroRegistroCupom != null && possuiDescontoMotivoCupom(pedidoSave)) {
                                exibirFalhaRegistroCupom(pedidoSave, retorno);
                            } else if (!retorno.contains("Pedido não cadastrado!") && !retorno.equals("N")) {

                                exibirPedidoRealizado(pedidoSave, retorno);

                            } else {

                                Toast.makeText(PopupPedFechamento.this,
                                        FikonVendasClient.ativo() && retorno != null ? retorno : "pedido não cadastrado",
                                        Toast.LENGTH_LONG).show();
                                for(int i = 0 ; i < pedido.getItens().size(); i++){
                                    pedido.getItens().get(i).setIsalteracao("N");
                                }

                                if(retorno!=null && retorno.contains("Erro ao gerar links de pagamento")){

                                    new AlertDialog.Builder(PopupPedFechamento.this)
                                            .setTitle("Pedido não cadastrado")
                                            .setMessage(retorno)
                                            .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).create().show();

                                }

                            }


                        }
                    });
                }
            }


        }.start();



    }

    private String registrarCupomPedido(Pedidos pedidoSave) {
        if (pedidoSave == null || pedidoSave.getNumpedven() <= 0 || identificadorReservaCupom <= 0) {
            return "Pedido ou identificador da reserva do cupom inválido.";
        }
        try {
            br.com.acalfortaleza.fikonmais.models.CupomPedidoRequest request =
                    new br.com.acalfortaleza.fikonmais.models.CupomPedidoRequest();
            request.numpedven = (long) pedidoSave.getNumpedven();
            request.codcli = pedidoSave.getCliente() == null ? 0 : pedidoSave.getCliente().getCodcli();
            request.statusPedido = pedidoSave.getCodStatus();
            request.idReserva = identificadorReservaCupom;
            request.codigoCupom = codigoCupom;

            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(br.com.acalfortaleza.fikonmais.service.ApiGemco.BASE_URL)
                    .client(client)
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build();
            br.com.acalfortaleza.fikonmais.service.ApiGemco api =
                    retrofit.create(br.com.acalfortaleza.fikonmais.service.ApiGemco.class);
            retrofit2.Response<String> response = api.SalvarCupomPedido(
                    request,
                    "Bearer " + br.com.acalfortaleza.fikonmais.service.ApiGemco.token
            ).execute();
            if (!response.isSuccessful()) {
                return "A API retornou HTTP " + response.code() + " ao registrar o cupom.";
            }
            return null;
        } catch (Exception exception) {
            Log.e("CupomPedido", "Falha ao registrar cupom no pedido " + pedidoSave.getNumpedven(), exception);
            return "Não foi possível registrar o cupom: " + exception.getMessage();
        }
    }

    private String sincronizarRegistroCupomPedido(Pedidos pedidoSave) {
        if (pedidoSave == null || pedidoSave.getNumpedven() <= 0) {
            return "Número do pedido inválido para sincronizar o cupom.";
        }

        if (possuiDescontoMotivoCupom(pedidoSave)) {
            if (codigoCupom == null || codigoCupom.trim().isEmpty()) {
                return null;
            }
            if (identificadorReservaCupom <= 0) {
                return "Identificador da reserva do cupom inválido.";
            }
            return registrarCupomPedido(pedidoSave);
        }

        try {
            okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build();
            retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(br.com.acalfortaleza.fikonmais.service.ApiGemco.BASE_URL)
                    .client(client)
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .build();
            br.com.acalfortaleza.fikonmais.service.ApiGemco api =
                    retrofit.create(br.com.acalfortaleza.fikonmais.service.ApiGemco.class);
            retrofit2.Response<String> response = api.ExcluirCupomPedido(
                    (long) pedidoSave.getNumpedven(),
                    "Bearer " + br.com.acalfortaleza.fikonmais.service.ApiGemco.token
            ).execute();
            if (!response.isSuccessful()) {
                return "A API retornou HTTP " + response.code() + " ao remover o cupom do pedido.";
            }
            return null;
        } catch (Exception exception) {
            Log.e("CupomPedido", "Falha ao remover cupom do pedido " + pedidoSave.getNumpedven(), exception);
            return "Não foi possível remover o cupom do pedido: " + exception.getMessage();
        }
    }

    private boolean possuiDescontoMotivoCupom(Pedidos pedidoAtual) {
        if (pedidoAtual == null || pedidoAtual.getItens() == null) {
            return false;
        }

        for (ItemPedidos item : pedidoAtual.getItens()) {
            if (item != null && "CD".equalsIgnoreCase(item.getCodMotivo())) {
                return true;
            }
        }
        return false;
    }

    private void exibirFalhaRegistroCupom(Pedidos pedidoSave, String retornoPedido) {
        String numeroPedido = String.valueOf((long) pedidoSave.getNumpedven());
        new AlertDialog.Builder(PopupPedFechamento.this)
                .setTitle("Pedido salvo; registro do cupom pendente")
                .setMessage("O pedido " + numeroPedido + " foi salvo, mas não foi possível atualizar o registro do cupom. "
                        + erroRegistroCupom)
                .setCancelable(false)
                .setPositiveButton("Tentar novamente", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    ProgressDialog progresso = new ProgressDialog(PopupPedFechamento.this);
                    progresso.setMessage("Atualizando registro do cupom...");
                    progresso.setCancelable(false);
                    progresso.show();
                    new Thread(() -> {
                        String erro = sincronizarRegistroCupomPedido(pedidoSave);
                        runOnUiThread(() -> {
                            if (progresso.isShowing()) {
                                progresso.dismiss();
                            }
                            erroRegistroCupom = erro;
                            if (erro == null) {
                                exibirPedidoRealizado(pedidoSave, retornoPedido);
                            } else {
                                exibirFalhaRegistroCupom(pedidoSave, retornoPedido);
                            }
                        });
                    }).start();
                })
                .setNegativeButton("Fechar", (dialogInterface, which) -> dialogInterface.dismiss())
                .show();
    }

    private void exibirPedidoRealizado(Pedidos pedidoSave, String retornoPedido) {
        new AlertDialog.Builder(PopupPedFechamento.this)
                .setTitle("Realizado :)")
                .setMessage("Seu lançamento foi realizado com sucesso! Pedido número: " + retornoPedido
                        + (FikonVendasClient.ativo() && FikonVendasClient.ultimaPendencia() != null
                                ? "\n\n" + FikonVendasClient.ultimaPendencia() : ""))
                .setCancelable(false)
                .setPositiveButton("Ok", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    try {
                        pedidoSave.setNumpedven(Long.parseLong(retornoPedido.split("\\.")[0].trim()));
                    } catch (Exception exception) {
                        Log.w("CupomPedido", "Não foi possível converter o número do pedido salvo.", exception);
                    }
                    Log.d("AlertaLinksDebug", "Status do pedido: " + pedidoSave.getCodStatus());
                    if (pedidoSave.getCodStatus() == 1) {
                        exibirAlertaLinksPagementoComCallback(pedidoSave);
                    } else {
                        finalizarProcesso(pedidoSave);
                    }
                })
                .show();
    }


    private void gerarPix(final Pedidos pedidoSave) {

        // Criar e exibir ProgressDialog simples na UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new ProgressDialog(PopupPedFechamento.this);
                dialog.setMessage("Aguardando Requisição...");
                dialog.setCancelable(false);
                dialog.show();
            }
        });

        new Thread() {
            @Override
            public void run() {

                try {

                    DecimalFormat inteiro = new DecimalFormat("#");

                    PixRequest pixRequest = new PixRequest();
                    pixRequest.setValue(BigDecimal.valueOf(pedidoSave.getVlTotal()));
                    pixRequest.setMessage("Pagamento Pedido Acal Mais #" + inteiro.format(pedidoSave.getNumpedven()) + " - Consultor: "+ pedidoSave.getConsultor() + " - Valor Total R$" +df.format(pedidoSave.getVlTotal()) );
                    pixRequest.setPartner_id(1);
                    pixRequest.setUsuario(pedidoSave.getConsultor());
                    pixRequest.setUsuario_id(pedidoSave.getCodvendr());
                    pixRequest.setCaptura_id(9);
                    // Buscar na Api Email - pelo codfil
                    pixRequest.setUser_email("luanpontes@acalhomecenter.com.br");

                    // Buscar Token para passar como paramento e gerar PIX com WhatsApp
                    String numpedven = String.valueOf(pedidoSave.getNumpedven());
                    String codcli = String.valueOf(pedidoSave.getCliente().getCodcli());
                    String telefone = pedidoSave.getCliente().getCelular() != null ? pedidoSave.getCliente().getCelular() :
                            (pedidoSave.getCliente().getFone() != null ? pedidoSave.getCliente().getFone() : "");

                    // Verificar se PIX já foi pago antes de gerar
                    FixPayHelper fixPayHelper = new FixPayHelper();
                    fixPayHelper.verificarPixAntesDeGerar(PopupPedFechamento.this, numpedven, null, new FixPayHelper.VerificacaoPixCallback() {
                        @Override
                        public void onPodeGerar() {
                            // PIX pode ser gerado, continuar com a geração
                            try {
                                pixResponse = helperFixpay.GerarPixComWhatsApp(PopupPedFechamento.this, pixRequest, pedidoSave.getCodfil(), numpedven, codcli, telefone, null);

                                // Fechar o dialog na UI thread
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                });

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (pixResponse!= null && pixResponse.getData().getTxid()!= null && pixResponse.getData().getPixCopiaECola()!= null  ) {
                                            //Salvar dados do PIX no banco antes de salvar o pedido
                                            salvarPixNoBanco(pixResponse.getData(), pedidoSave);
                                            //Salvar pedido
                                            salvarPedido(pedidoSave);
                                        } else {
                                            Toast.makeText(PopupPedFechamento.this, "pedido não cadastrado", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                        Toast.makeText(PopupPedFechamento.this, "Erro ao gerar PIX: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onJaPago(String mensagem) {
                            // PIX já foi pago, exibir popup de alerta
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Fechar o dialog de loading
                                    if (dialog != null && dialog.isShowing()) {
                                        dialog.dismiss();
                                    }

                                    // Exibir popup de alerta
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PopupPedFechamento.this);
                                    builder.setTitle("PIX já pago");
                                    builder.setMessage("Pagamento já efetuado para este pedido!");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            // Retornar para a tela de consulta de pedido
                                            finish();
                                        }
                                    });
                                    builder.setCancelable(false);
                                    builder.show();
                                }
                            });
                        }

                        @Override
                        public void onErro(String mensagemErro) {
                            // Erro na verificação, permitir geração do PIX
                            try {
                                pixResponse = helperFixpay.GerarPixComWhatsApp(PopupPedFechamento.this, pixRequest, pedidoSave.getCodfil(), numpedven, codcli, telefone, null);

                                // Fechar o dialog na UI thread
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                });

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (pixResponse!= null && pixResponse.getData().getTxid()!= null && pixResponse.getData().getPixCopiaECola()!= null  ) {
                                            //Salvar dados do PIX no banco antes de salvar o pedido
                                            salvarPixNoBanco(pixResponse.getData(), pedidoSave);
                                            //Salvar pedido
                                            salvarPedido(pedidoSave);
                                        } else {
                                            Toast.makeText(PopupPedFechamento.this, "pedido não cadastrado", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                        Toast.makeText(PopupPedFechamento.this, "Erro ao gerar PIX: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Toast.makeText(PopupPedFechamento.this, "Erro ao verificar PIX: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }.start();
    }



    public void  ConsultaEstqoue( List<ItemPedidos> i) {

        ConsultaEstoqueAsync consulta = new ConsultaEstoqueAsync(this,i,this) ;
        consulta.execute();

    }


    private static double ultimoValorServicosAtualizado = 0.0;

    protected void onResume() {
        if (aguardandoRetornoPopupServicos) {
            atualizarCampoServicosFreteAposPopup();
            aguardandoRetornoPopupServicos = false;
        }
        sincronizarRegraDescontoFreteEspecial();
        sincronizarTotalPopupFechamentoAposFormaPagamento();
        ultimoValorServicosAtualizado = br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed;
        super.onResume();
    }

    private void sincronizarTotalPopupFechamentoAposFormaPagamento() {
        if (txtTotal == null || pedido == null || FinanceiroAdapter.mList == null || FinanceiroAdapter.mList.isEmpty()) {
            return;
        }

        BigDecimal totalFinanceiro = BigDecimal.ZERO;
        boolean pixLinkPgtoLinkSelecionado = false;
        for (Financeiro fin : FinanceiroAdapter.mList) {
            totalFinanceiro = totalFinanceiro.add(BigDecimal.valueOf(fin.getValor()));
            if ("87".equals(fin.getCondpgto()) && (fin.getForma() == 142 || fin.getCodEve() == 142)) {
                pixLinkPgtoLinkSelecionado = true;
            }
        }

        if (!pixLinkPgtoLinkSelecionado) {
            return;
        }

        pedido.setFinanceiro(FinanceiroAdapter.mList);
        double totalFinanceiroFormatado = totalFinanceiro.setScale(2, RoundingMode.HALF_UP).doubleValue();
        pedido.setVlTotal(totalFinanceiroFormatado);
        txtTotal.setText("Total: " + NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(totalFinanceiroFormatado));
    }

    private boolean isPixLinkPgtoLinkSelecionado() {
        if (FinanceiroAdapter.mList == null || FinanceiroAdapter.mList.isEmpty()) {
            return false;
        }

        for (Financeiro fin : FinanceiroAdapter.mList) {
            if ("87".equals(fin.getCondpgto()) && (fin.getForma() == 142 || fin.getCodEve() == 142)) {
                return true;
            }
        }

        return false;
    }

    public static void atualizarTotalExibido(Pedidos pedidoAtualizado) {
        if (instanciaAtual == null || instanciaAtual.txtTotal == null || pedidoAtualizado == null) {
            return;
        }

        pedido = pedidoAtualizado;
        instanciaAtual.txtTotal.setText(
                "Total: " + NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(pedidoAtualizado.getVlTotal())
        );
    }

    private void atualizarCampoServicosFreteAposPopup() {
        if (edtServicosFrete == null) return;
        java.util.List<Integer> idsSel = br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed;
        boolean semSelecao = (idsSel == null || idsSel.isEmpty());
        double valorServicos = semSelecao ? 0.0 : Math.max(0.0, br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed);
        final DecimalFormat df = new DecimalFormat("0.00");
        liberarPreservacaoTotalCarregado();
        edtServicosFrete.setText(df.format(valorServicos));
        android.util.Log.d("FreteDebug", "Campo de serviços atualizado após popup: " + df.format(valorServicos));
        if (pedido != null) {
            try { pedido.setVlServicosAdicionais(valorServicos); } catch (Exception ignore) {}
            try { pedido.setServicosAdicionaisIds(semSelecao ? new java.util.ArrayList<>() : new java.util.ArrayList<>(idsSel)); } catch (Exception ignore) {}
        }
        if (pedido != null && pedido.getItens() != null) {
            atualizaTotais(pedido.getItens());
        }
    }


    public void atualizaTotais(List<ItemPedidos> listaItens) {
        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        final DecimalFormat df = new DecimalFormat("0.00");

        BigDecimal valorTotalItens = new BigDecimal("0.00");
        BigDecimal valorDescontoTotal = new BigDecimal("0.00");
        BigDecimal valorFreteBase = new BigDecimal("0.00");
        BigDecimal valorServicos = new BigDecimal("0.00");

        // Pega os valores dos campos de texto, que são a fonte da verdade nesta tela
        try {
            if (!edtFrete.getText().toString().isEmpty()) {
                valorFreteBase = new BigDecimal(edtFrete.getText().toString().replace(",", "."));
            }
            if (!edtServicosFrete.getText().toString().isEmpty()) {
                valorServicos = new BigDecimal(edtServicosFrete.getText().toString().replace(",", "."));
            }
        } catch (NumberFormatException e) {
            Log.e("FreteDebug", "Erro ao converter valores de frete/serviço para número.");
        }

        BigDecimal valorTotalMarg = new BigDecimal("0.00");
        BigDecimal valorCusto = new BigDecimal("0.00");
        BigDecimal valorIcms = new BigDecimal("0.00");
        double valorEncargos = 0.00d;
        double valorMargem = 0.00d;

        boolean pixLinkPgtoLinkSelecionado = isPixLinkPgtoLinkSelecionado();
        for (ItemPedidos item : listaItens) {
            BigDecimal totalItem = pixLinkPgtoLinkSelecionado
                    ? calcularTotalItemConsiderandoPromocaoPrazo(item, "01", "A VISTA")
                    : calcularTotalItemConsiderandoPromocaoPrazo(item);
            // ===== HALF_UP POR ITEM (OPCAO 2 USUARIO: arredondamento comercial) =====
            totalItem = totalItem.setScale(2, RoundingMode.HALF_UP);
            valorTotalItens = valorTotalItens.add(totalItem);

            BigDecimal descontoItem = BigDecimal.valueOf(item.getVlDescItem()).multiply(BigDecimal.valueOf(item.getQtcomp()));
            descontoItem = descontoItem.setScale(2, RoundingMode.HALF_UP);
            valorDescontoTotal = valorDescontoTotal.add(descontoItem);

            // Cálculos de margem
            valorTotalMarg = valorTotalMarg.add(totalItem);
            valorCusto = valorCusto.add(BigDecimal.valueOf(item.getCusto() * item.getQtcomp()));

            if (item.getIcmsRed() == 0) {
                valorIcms = valorIcms.add(BigDecimal.valueOf((((item.getPrecoUnit() - item.getVlDescItem()) * item.getQtcomp()) * item.getIcms()) / 100));
            } else {
                valorIcms = valorIcms.add(BigDecimal.valueOf(((((item.getPrecoUnit() - item.getVlDescItem()) * item.getQtcomp()) - (((item.getPrecoUnit() - item.getVlDescItem()) * item.getQtcomp()) * item.getIcmsRed()) / 100) * item.getIcms()) / 100));
            }
        }

        // Adiciona desconto de promoção se houver
        if (pedido.getVlDescPromo() > 0) {
            valorDescontoTotal = valorDescontoTotal.add(BigDecimal.valueOf(pedido.getVlDescPromo()));
        }

        // Ajusta precisão final (somente confirma 2 casas; já foi truncado por item acima)
        valorTotalItens = valorTotalItens.setScale(2, RoundingMode.HALF_UP);
        valorDescontoTotal = valorDescontoTotal.setScale(2, RoundingMode.HALF_UP);

        // --- CÁLCULO FINAL CORRIGIDO ---
        BigDecimal subTotal = valorTotalItens;
        BigDecimal totalGeral = subTotal.add(valorFreteBase).add(valorServicos).setScale(2, RoundingMode.HALF_UP);
        double totalParaExibicao = getTotalParaExibicao(totalGeral);

        // Mantém o total do pedido alinhado com o valor exibido e com o valor usado nas integrações.
        pedido.setVlTotal(totalParaExibicao);
        pedido.setVlFrete(valorFreteBase.add(valorServicos).doubleValue()); // VlFrete representa o frete total (base + serviços)
        pedido.setVlServicosAdicionais(valorServicos.doubleValue()); // Garante que o valor dos serviços está no objeto
        pedido.setVlDesconto(valorDescontoTotal.doubleValue());

        // Atualiza a lista de parcelas financeiras caso o total tenha mudado
        if (FinanceiroAdapter.mList != null && !FinanceiroAdapter.mList.isEmpty()) {
            BigDecimal somaAtual = BigDecimal.ZERO;
            for (Financeiro fin : FinanceiroAdapter.mList) {
                somaAtual = somaAtual.add(BigDecimal.valueOf(fin.getValor()));
            }
            BigDecimal novoTotal = BigDecimal.valueOf(totalParaExibicao).setScale(2, RoundingMode.HALF_UP);
            if (somaAtual.setScale(2, RoundingMode.HALF_UP).compareTo(novoTotal) != 0) {
                if (FinanceiroAdapter.mList.size() == 1) {
                    FinanceiroAdapter.mList.get(0).setValor(totalParaExibicao);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    FinanceiroAdapter.mList.clear();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    Toast.makeText(getApplicationContext(), "O valor total mudou. Por favor, informe a forma de pagamento novamente.", Toast.LENGTH_LONG).show();
                }
            }
        }

        // Atualiza o TextView do total
        txtTotal.setText("Total: " + real.format(totalParaExibicao));
        Log.d("FreteDebug", "Total Atualizado: ItensLiquidos(" + valorTotalItens + ") + Frete(" + valorFreteBase + ") + Servicos(" + valorServicos + ") = " + totalGeral + " | descontoInformativo=" + valorDescontoTotal + " | totalParaExibicao=" + totalParaExibicao + " | preservarTotalCarregado=" + preservarTotalCarregado);

        // Cálculos de margem...
        if (listaItens.isEmpty() || valorTotalMarg.doubleValue() == 0) {
            valorMargem = 0.00d;
        } else {
            valorEncargos = (valorTotalMarg.doubleValue() * listaItens.get(0).getEncargos()) / 100;
            valorMargem = ((valorTotalMarg.doubleValue() - valorEncargos - valorIcms.doubleValue() - valorCusto.doubleValue()) / (valorTotalMarg.doubleValue())) * 100;
        }

        // Atualiza UI de margem
        pedido.setPmargRent(valorMargem);
        if (valorMargem >= 36) txtMargem.setTextColor(Color.parseColor("#00bfa5"));
        else if (valorMargem >= 34) txtMargem.setTextColor(Color.parseColor("#C8671E"));
        else txtMargem.setTextColor(Color.parseColor("#A61553"));
        txtMargem.setVisibility(INVISIBLE); // Mantendo comportamento original

        // Atualiza Venda Perfeita
        pedido.setIndicadoresVendaPerfeita(PedidosHelper.ObterIndicadoresVendaPerfeita(pedido, valorFreteBase.doubleValue() + valorServicos.doubleValue()));
        AtualizarLayoutVendaPerfeita();
    }




    public Boolean validarCondicao() {


        if (condicao_selecionada.equals("N")) {

            layout_condpgto.setError("Informe a condição de pagamento");
            return false;

        } else {
            layout_condpgto.setError(null);
            return true;
        }
    }

    public Boolean validarForma() {
        if (FinanceiroAdapter.mList == null || FinanceiroAdapter.mList.size() == 0) {
            layout_condpgto.setError("Informe a forma de pagamento");
            return false;
        } else {
            BigDecimal totalPago = new BigDecimal("0.00");
            for (int i = 0; i < FinanceiroAdapter.mList.size(); i++) {
                totalPago = totalPago.add(BigDecimal.valueOf(FinanceiroAdapter.mList.get(i).getValor()));
            }

            // --- INÍCIO DA CORREÇÃO FINAL ---

            // 1. Pega o valor do frete e serviços direto da tela.
            BigDecimal valorFreteAtual = new BigDecimal(edtFrete.getText().toString().replace(",", "."));
            BigDecimal valorServicosAtual = new BigDecimal(edtServicosFrete.getText().toString().replace(",", "."));

            // 2. RECALCULA o subtotal dos itens a partir da lista de itens (A FONTE DA VERDADE)
            BigDecimal valorTotalItens = BigDecimal.ZERO;

            boolean pixLinkPgtoLinkSelecionado = isPixLinkPgtoLinkSelecionado();
            if (pedido != null && pedido.getItens() != null) {
                for (ItemPedidos item : pedido.getItens()) {
                    BigDecimal totalItem = pixLinkPgtoLinkSelecionado
                            ? calcularTotalItemConsiderandoPromocaoPrazo(item, "01", "A VISTA")
                            : calcularTotalItemConsiderandoPromocaoPrazo(item);
                    valorTotalItens = valorTotalItens.add(totalItem);
                }
            }
            BigDecimal subTotalItensCorreto = valorTotalItens.setScale(2, RoundingMode.HALF_UP);

            BigDecimal totalCorretoPedido = subTotalItensCorreto.add(valorFreteAtual).add(valorServicosAtual).setScale(2, RoundingMode.HALF_UP);

            Log.d("FinalizarPedidoDebug", "Validando Pagamento: Total Pago=" + totalPago
                    + ", Total Esperado=" + totalCorretoPedido
                    + ", SubtotalRecalculado=" + subTotalItensCorreto
                    + ", CondicaoAtual=" + condicao_selecionada);

            // 4. Compara o total pago com o total correto (com tolerância de até 2 centavos para evitar bloqueios de arredondamento).
            BigDecimal diferencaPagamento = totalCorretoPedido.subtract(totalPago).abs().setScale(2, RoundingMode.HALF_UP);
            BigDecimal toleranciaCentavos = BigDecimal.valueOf(0.02d).setScale(2, RoundingMode.HALF_UP);
            if (diferencaPagamento.compareTo(toleranciaCentavos) > 0) {
                layout_condpgto.setError("Valor da forma ("+totalPago+") diferente do pedido ("+totalCorretoPedido+")!");
                Toast.makeText(getApplicationContext(), "Valor da forma de pagamento diferente do total do pedido!", Toast.LENGTH_LONG).show();
                return false;
            } else {
                layout_condpgto.setError(null);
                return true;
            }
        }
    }

    public Boolean validarLiberacao() {


        if (FinanceiroAdapter.mList == null || FinanceiroAdapter.mList.size() > 0) {

            String condicao ="";
            int numprc = 0 ;
            String forma = "";
            BigDecimal valor = new BigDecimal("0.00");

            for(int i = 0 ; i < FinanceiroAdapter.mList.size() ; i++ ){

                valor = valor.add(BigDecimal.valueOf(FinanceiroAdapter.mList.get(i).getValor()));






            }


            return true;
        }

        return true;
    }




    public Boolean validarFase() {


        if (fase_selecionada.equals("N")) {

            layout_midia.setError("Informe a mídia de marketing");
            return false;

        } else {
            layout_midia.setError(null);
            return true;
        }
    }


    public Boolean validarDescontoFrete() {
        if (freteOrig <= 0.0d) {
            layout_frete.setError(null);
            return true;
        }

        Double perccalculado = 0.00d;
        double percentualMaximo = getPercentualMaximoDescontoFrete();

        perccalculado = ((freteOrig - Double.parseDouble(edtFrete.getText().toString().replace(",", ".")))/freteOrig)*100;

        if (perccalculado > percentualMaximo) {

            layout_frete.setError("Desconto do frete superior a "+df.format(percentualMaximo)+"%");
            edtFrete.setText(String.valueOf(df.format(freteOrig)));
            return false;

        } else {
            layout_frete.setError(null);
            return true;
        }
    }


    public Boolean validarAcrescimoFrete() {

        Double perccalculado = 0.00d;

        perccalculado = ((Double.parseDouble(edtFrete.getText().toString()))/freteOrig)*100;

        if (perccalculado > 130 ) {

            layout_frete.setError("Acrescimo do frete superior a 30%");
            edtFrete.setText(String.valueOf(df.format(freteOrig)));
            return false;

        } else {
            layout_frete.setError(null);
            return true;
        }
    }


    @Override
    public void depoisDaConsulta(List<ItemPedidos> retornoEstoque) {
        liberado="";
        final List<ItemPedidos> newLista = new  ArrayList<>();

        if (retornoEstoque == null || retornoEstoque.isEmpty()) {
            liberado = "N";
            new androidx.appcompat.app.AlertDialog.Builder(PopupPedFechamento.this)
                    .setTitle("Erro ao consultar estoque")
                    .setMessage("Não foi possível validar o estoque dos itens para fechar o pedido. Verifique sua conexão e tente novamente.")
                    .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            return;
        }

        for(final ItemPedidos i: retornoEstoque) {

            if (i != null
                    && i.getEstoqueValidacao() != null
                    && "S".equals(i.getEstoqueValidacao().getTemestoque())
                    && "N".equals(i.getEncomenda())) {
                if ("N".equals(i.getFllotevencto())) {
                    if (!liberado.equals("N")) {
                        liberado = "S";
                    }
                }
                if ("S".equals(i.getFllotevencto())) {
                    if ("S".equals(i.getEstoqueValidacao().getTemumlote())) {

                        if (!liberado.equals("N")) {
                            liberado = "S";
                        }
                    } else if ("N".equals(i.getEstoqueValidacao().getTemumlote()) && "S".equals(i.getEstoqueValidacao().getTemmaisdeumlote())) {
                        liberado = "N";

                    } else {
                        liberado = "N";

                    }
                }
            } else if ("E".equals(i != null ? i.getEncomenda() : null) || "SV".equals(i != null ? i.getSituacao() : null)) {
                if (!liberado.equals("N")) {
                    newLista.add(i);
                    liberado = "S";
                }
            }else {

                liberado = "N";

            }


        }

        for( int i = 0; i < retornoEstoque.size(); i++) {
            final int position = i;
            ItemPedidos item = retornoEstoque.get(i);
            if (item != null
                    && item.getEstoqueValidacao() != null
                    && "S".equals(item.getEstoqueValidacao().getTemestoque())
                    && "N".equals(item.getEncomenda())) {
                if ("N".equals(item.getFllotevencto())) {
                    if (!liberado.equals("N")) {
                        liberado = "S";
                    }
                }
                if ("S".equals(item.getFllotevencto())) {
                    if ("S".equals(item.getEstoqueValidacao().getTemumlote())) {
                        Lotevencto lote = new Lotevencto();
                        lote.setDescricao(item.getEstoqueValidacao().getLote());
                        lote.setQtde((float) item.getQtcomp());
                        List<Lotevencto> mlist = new ArrayList<>();
                        mlist.add(lote);
                        pedido.getItens().get(i).setLotevencto(mlist);
                        if (!liberado.equals("N")) {
                            liberado = "S";
                        }
                    } else if ("N".equals(item.getEstoqueValidacao().getTemumlote()) && "S".equals(item.getEstoqueValidacao().getTemmaisdeumlote())) {
                        liberado = "N";
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PopupPedFechamento.this);
                        builder.setTitle("Atenção :|");
                        builder.setMessage("O Item: " + item.getCoditprod() + " - " + item.getDescricao() + ". Não possui quantidade suficiente somente em um lote, deseja realizar o pedido com mais de um lote? ");
                        builder
                                .setCancelable(false)
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        pedido.getItens().get(position).setMultiplosLote("S");
                                        ConsultaEstqoue(pedido.getItens());


                                    }

                                });

                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                liberado = "N";

                            }
                        });


                        builder.create().show();


                    } else {
                        liberado = "N";
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PopupPedFechamento.this);
                        builder.setTitle("Atenção :|");
                        builder.setMessage("O Item: " + item.getCoditprod() + " - " + item.getDescricao() + ". Não possui quantidade suficiente de lote para atender o pedido! ");
                        builder
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        liberado = "N";

                                    }

                                });

                        builder.create().show();


                    }
                }
            } else if ("E".equals(item != null ? item.getEncomenda() : null) || "SV".equals(item != null ? item.getSituacao() : null)) {
                if (!liberado.equals("N")) {
                    liberado = "S";
                }
            }else {

                liberado = "N";
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PopupPedFechamento.this);
                builder.setTitle("Atenção :|");
                builder.setMessage("O Item: " + (item != null ? item.getCoditprod() : 0) + " - " + (item != null ? item.getDescricao() : "") + ". Não possui quantidade suficiente para atender o pedido! ");
                builder
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                liberado = "N";

                            }

                        });
                /*builder.setNegativeButton("Encomendar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        if(!pedido.getItens().get(position).getSituacao().equals("PR") && !pedido.getItens().get(position).getSituacao().equals("IN") ) {
                            pedido.getItens().get(position).setEncomenda("E");

                            ConsultaEstqoue(pedido.getItens());
                        }else{
                            Toast.makeText(PopupPedFechamento.this, "Item possui situação que não pode ser encomendada!" , Toast.LENGTH_LONG).show();
                        }
                    }
                });*/

                builder.create().show();
            }


        }

        if (liberado.equals("S")) {
            aplicarStatusLiberacaoComercialPorParcelamentoCartao();

            // Verificar se é condição de pagamento 87 com status 1 (Pendente de Aprovação)
            if(pedido.getFinanceiro().get(0).getCondpgto().equals("87") && pedido.getCodStatus() == 1){
                // Adicionar flag para não gerar links
                String obsAtual = pedido.getObsAdicional() != null ? pedido.getObsAdicional() : "";
                if(!obsAtual.contains("[NAO_GERAR_LINKS]")){
                    pedido.setObsAdicional(obsAtual + "[NAO_GERAR_LINKS]");
                }
                // Salvar pedido sem gerar PIX
                salvarPedido(pedido);
            } else if(pedido.getFinanceiro().get(0).getCondpgto().equals("87")){
                if(pedido.getFinanceiro().size()==1 && pedido.getFinanceiro().get(0).getForma()==142 ){

                    // Exibir loading antes de gerar PIX
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final ProgressDialog progressDialog = new ProgressDialog(PopupPedFechamento.this);
                            progressDialog.setTitle("Aguardando Requisição...");
                            progressDialog.setMessage("Por favor aguarde");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final boolean[] pixJaPagoFlag = {false}; // Flag para controlar se PIX já foi pago

                                    try {
                                        gerarPixAutomatico();

                                    } catch (PixJaPagoException e) {
                                        // Exceção específica para PIX já pago - interromper fluxo completamente
                                        pixJaPagoFlag[0] = true;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                                LogHelper.writePixLog(PopupPedFechamento.this, "PIX_JA_PAGO_FLUXO_INTERROMPIDO", "Fluxo completamente interrompido - PIX já pago");
                                                // Não executar mais nada - o dialog já foi exibido no gerarPixAutomatico
                                            }
                                        });
                                        return; // Sair completamente
                                    } catch (Exception e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();

                                                // Verificar se a exceção é devido a PIX já pago
                                                String errorMessage = e.getMessage();
                                                if (errorMessage != null &&
                                                        (errorMessage.contains("PIX já foi pago") ||
                                                                errorMessage.contains("Pagamento já efetuado") ||
                                                                errorMessage.contains("CONCLUIDA"))) {
                                                    // PIX já foi pago - não executar salvarPedido
                                                    LogHelper.writePixLog(PopupPedFechamento.this, "PIX_JA_PAGO_CATCH", "Exceção de PIX já pago capturada - não executando salvarPedido");
                                                    return; // Sair sem executar salvarPedido
                                                }

                                                Toast.makeText(PopupPedFechamento.this, "Erro ao gerar PIX: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        return; // Sair em caso de erro
                                    }

                                    // Se chegou até aqui, PIX foi gerado com sucesso - continuar fluxo
                                    if (!pixJaPagoFlag[0]) {
                                        // Exibir loading para envio do PIX para WhatsApp
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();

                                                final ProgressDialog whatsappDialog = new ProgressDialog(PopupPedFechamento.this);
                                                whatsappDialog.setTitle("Enviando PIX para Whatsapp do Cliente...");
                                                whatsappDialog.setMessage("Por favor aguarde");
                                                whatsappDialog.setCancelable(false);
                                                whatsappDialog.show();

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            // Simular tempo de envio para WhatsApp
                                                            Thread.sleep(1500);

                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    whatsappDialog.dismiss();
                                                                    salvarPedido(pedido);
                                                                }
                                                            });
                                                        } catch (Exception e) {
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    whatsappDialog.dismiss();
                                                                    salvarPedido(pedido);
                                                                }
                                                            });
                                                        }
                                                    }
                                                }).start();
                                            }
                                        });
                                    }
                                }
                            }).start();
                        }
                    });

                } else {
                    salvarPedido(pedido);
                }


            }else{

                salvarPedido(pedido);
            }


        }


    }


    public void AtualizarLayoutVendaPerfeita(){

        try{

            PedidosHelper.AtualizarLayoutVendaPerfeita(
                    pedido.getIndicadoresVendaPerfeita(), pedido.getMetasVendaPerfeita(),
                    VendaPerfeitaConversao, VendaPerfeitaSKU, VendaPerfeitaTicket,
                    VendaPerfeitaDesconto, VendaPerfeitaEntrega
            );

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public PoliticaDesconto ConsultarPoliticaDesconto(){

        ProgressDialog progressDialog = new ProgressDialog(PopupPedFechamento.this);

        try{

            progressDialog.setTitle("Consultando Politica de Desconto");
            progressDialog.setMessage("Por favor aguarde");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Gson gson = new Gson();
            Pedidos pedidoPolitica = gson.fromJson(gson.toJson(pedido), Pedidos.class);

            pedidoPolitica.setCodvendr(Integer.parseInt(usuarioLogado.get("codigo")));
            pedidoPolitica.setConsultor(usuarioLogado.get("cpf"));
            pedidoPolitica.setGerenteUsuario(isGerenteUsuarioLogado());
            pedidoPolitica.setObs(montarObservacaoPoliticaDesconto());
            pedidoPolitica.setFinanceiro(FinanceiroAdapter.mList);

            CountDownLatch count = new CountDownLatch(1);

            final PoliticaDesconto[] politica = {new PoliticaDesconto()};

            new Thread(new Runnable() {
                @Override
                public void run() {

                    politica[0] = helper.ConsultarPoliticaDesconto(pedidoPolitica);
                    count.countDown();

                }
            }).start();

            count.await();

            progressDialog.dismiss();
            return politica[0];

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        progressDialog.dismiss();
        return null;

    }

    private Pedidos obterPedidoAtivoParaPix() {
        if (pedido != null) {
            return pedido;
        }

        if (Pedidos_LancamentoActivity.pedido != null) {
            return Pedidos_LancamentoActivity.pedido;
        }

        return null;
    }

    private BigDecimal obterValorTotalParaPix(Pedidos pedidoPix) {
        if (pedidoPix != null && pedidoPix.getFinanceiro() != null && !pedidoPix.getFinanceiro().isEmpty()) {
            BigDecimal totalFinanceiro = BigDecimal.ZERO;

            for (Financeiro financeiro : pedidoPix.getFinanceiro()) {
                if (financeiro != null) {
                    totalFinanceiro = totalFinanceiro.add(BigDecimal.valueOf(financeiro.getValor()));
                }
            }

            if (totalFinanceiro.compareTo(BigDecimal.ZERO) > 0) {
                return totalFinanceiro;
            }
        }

        if (pedidoPix != null) {
            return BigDecimal.valueOf(pedidoPix.getVlTotal());
        }

        return BigDecimal.ZERO;
    }

    private void gerarPixAutomatico() throws Exception, PixJaPagoException {
        LogHelper.writePixLog(this, "INICIO_GERACAO_PIX_AUTO", "Iniciando processo de geração automática de PIX");

        final CountDownLatch latch = new CountDownLatch(1);
        final Exception[] exception = {null};
        final boolean[] pixJaPago = {false}; // Flag para controlar se PIX já foi pago

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("GerarPixAutomatico", "Iniciando geração automática de PIX");
                    LogHelper.writePixLog(PopupPedFechamento.this, "THREAD_INICIADA_AUTO", "Thread de geração PIX automática iniciada");

                    // Obter dados do usuário logado
                    HashMap<String, String> usuarioLogado = helperLogin.GetUsuarioSalvo();
                    String codfil = usuarioLogado.get("codfil");
                    String nomeUsuario = usuarioLogado.get("nome");
                    String codUser = usuarioLogado.get("coduser");

                    Log.d("GerarPixAutomatico", "Dados do usuário - codfil: " + codfil + ", nome: " + nomeUsuario + ", codUser: " + codUser);
                    LogHelper.writePixLog(PopupPedFechamento.this, "DADOS_USUARIO_AUTO", "codfil: " + codfil + ", nome: " + nomeUsuario + ", codUser: " + codUser);

                    // Obter email da filial baseado no codfil
                    String userEmail = obterEmailPorCodfil(Integer.parseInt(codfil));
                    Log.d("GerarPixAutomatico", "Email da filial: " + userEmail);
                    LogHelper.writePixLog(PopupPedFechamento.this, "EMAIL_FILIAL_AUTO", "Email obtido para codfil " + codfil + ": " + userEmail);

                    Pedidos pedidoPix = obterPedidoAtivoParaPix();
                    if (pedidoPix == null) {
                        throw new IllegalStateException("Pedido ativo não encontrado para geração do PIX");
                    }

                    BigDecimal valorPix = obterValorTotalParaPix(pedidoPix);

                    // Dados do pedido
                    LogHelper.writePixLog(PopupPedFechamento.this, "DADOS_PEDIDO_AUTO", "Valor total: " + valorPix + ", Número pedido: " + pedidoPix.getNumpedven());

                    // Criar PixRequest
                    PixRequest pixRequest = new PixRequest();
                    pixRequest.setValue(valorPix);
                    pixRequest.setMessage("PIX ACAL - Pedido: " + (long) pedidoPix.getNumpedven());
                    pixRequest.setCaptura_id(9);
                    pixRequest.setUsuario(nomeUsuario);
                    pixRequest.setUsuario_id(Integer.parseInt(codUser));
                    pixRequest.setUser_email(userEmail);
                    pixRequest.setPartner_id(1);

                    Log.d("GerarPixAutomatico", "PixRequest criado - valor: " + valorPix + ", codfil: " + codfil);
                    LogHelper.writePixRequest(PopupPedFechamento.this, "PixRequest{" +
                            "value=" + pixRequest.getValue() +
                            ", message='" + pixRequest.getMessage() + '\'' +
                            ", captura_id=" + pixRequest.getCaptura_id() +
                            ", usuario='" + pixRequest.getUsuario() + '\'' +
                            ", usuario_id=" + pixRequest.getUsuario_id() +
                            ", user_email='" + pixRequest.getUser_email() + '\'' +
                            ", partner_id=" + pixRequest.getPartner_id() +
                            "}");

                    // Gerar PIX com verificação automática
                    FixPayHelper fixPayHelper = new FixPayHelper();
                    Log.d("GerarPixAutomatico", "Gerando PIX com verificação automática...");
                    LogHelper.writePixLog(PopupPedFechamento.this, "GERANDO_PIX_COM_VERIFICACAO_AUTO", "Iniciando geração de PIX com verificação para pedido: " + pedidoPix.getNumpedven());

                    final PixResponse[] response = {null};
                    final CountDownLatch pixLatch = new CountDownLatch(1);

                    // Callback para verificação de PIX já pago
                    FixPayHelper.VerificacaoPixCallback verificacaoCallback = new FixPayHelper.VerificacaoPixCallback() {
                        @Override
                        public void onPodeGerar() {
                            // Este callback não será usado pois a verificação é feita internamente no GerarPix
                            pixLatch.countDown();
                        }

                        @Override
                        public void onJaPago(String mensagem) {
                            Log.d("GerarPixAutomatico", "PIX já foi pago: " + mensagem);
                            LogHelper.writePixLog(PopupPedFechamento.this, "PIX_JA_PAGO_AUTO", "PIX já foi pago: " + mensagem);

                            pixJaPago[0] = true; // Marcar que PIX já foi pago

                            PopupPedFechamento.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(PopupPedFechamento.this)
                                            .setTitle("PIX já pago")
                                            .setMessage("Pagamento já efetuado para este pedido!")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish();
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
                                }
                            });
                            pixLatch.countDown();
                        }

                        @Override
                        public void onErro(String erro) {
                            Log.e("GerarPixAutomatico", "Erro na verificação: " + erro);
                            LogHelper.writePixError(PopupPedFechamento.this, "ERRO_VERIFICACAO_AUTO: " + erro);
                            exception[0] = new Exception("Erro na verificação: " + erro);
                            pixLatch.countDown();
                        }
                    };

                    try {
                        Log.d("GerarPixAutomatico", "Chamando FixPayHelper.GerarPix com verificação...");
                        LogHelper.writePixLog(PopupPedFechamento.this, "CHAMANDO_FIXPAY_HELPER_AUTO", "Iniciando chamada para FixPayHelper.GerarPix com verificação e codfil: " + codfil);

                        response[0] = fixPayHelper.GerarPix(PopupPedFechamento.this, pixRequest, Integer.parseInt(codfil), true, null);
                    } catch (Exception e) {
                        Log.e("GerarPixAutomatico", "Erro ao gerar PIX: " + e.getMessage(), e);
                        LogHelper.writePixError(PopupPedFechamento.this, "ERRO_GERAR_PIX_AUTO: " + e.getMessage());
                        exception[0] = e;
                    } finally {
                        pixLatch.countDown();
                    }

                    // Aguardar conclusão
                    pixLatch.await();

                    Log.d("GerarPixAutomatico", "Resposta recebida: " + (response[0] != null ? "não nula" : "nula"));
                    LogHelper.writePixLog(PopupPedFechamento.this, "RESPOSTA_RECEBIDA_AUTO", "Response: " + (response[0] != null ? "não nula" : "nula"));

                    if (response[0] != null) {
                        // LOG DETALHADO: TODOS os campos do PixResponse
                        Log.d("PixResponse_COMPLETO_AUTO", "=== TODOS OS CAMPOS DO PIXRESPONSE AUTOMÁTICO ===");
                        Log.d("PixResponse_COMPLETO_AUTO", "payment_date: " + response[0].getPayment_date());

                        Log.d("GerarPixAutomatico", "Response.getData(): " + (response[0].getData() != null ? "não nulo" : "nulo"));
                        LogHelper.writePixResponse(PopupPedFechamento.this, "Response.getData(): " + (response[0].getData() != null ? "não nulo" : "nulo"));

                        if (response[0].getData() != null) {
                            PixResponse.Data data = response[0].getData();

                            // LOG DETALHADO: TODOS os campos do Data
                            Log.d("PixResponse_DATA_AUTO", "=== TODOS OS CAMPOS DO DATA AUTOMÁTICO ===");
                            Log.d("PixResponse_DATA_AUTO", "txid: " + data.getTxid());
                            Log.d("PixResponse_DATA_AUTO", "revisao: " + data.getRevisao());
                            Log.d("PixResponse_DATA_AUTO", "status: " + data.getStatus());
                            Log.d("PixResponse_DATA_AUTO", "pixCopiaECola: " + data.getPixCopiaECola());
                            Log.d("PixResponse_DATA_AUTO", "location: " + data.getLocation());
                            Log.d("PixResponse_DATA_AUTO", "chave: " + data.getChave());
                            Log.d("PixResponse_DATA_AUTO", "solicitacaoPagador: " + data.getSolicitacaoPagador());
                            Log.d("PixResponse_DATA_AUTO", "pix: " + data.getPix());

                            // LOG DETALHADO: Campos do Calendario
                            if (data.getCalendario() != null) {
                                Log.d("PixResponse_CALENDARIO_AUTO", "=== CAMPOS DO CALENDARIO AUTOMÁTICO ===");
                                Log.d("PixResponse_CALENDARIO_AUTO", "criacao: " + data.getCalendario().getCriacao());
                                Log.d("PixResponse_CALENDARIO_AUTO", "expiracao: " + data.getCalendario().getExpiracao());
                            } else {
                                Log.d("PixResponse_CALENDARIO_AUTO", "Calendario é nulo");
                            }

                            // LOG DETALHADO: Campos do Devedor
                            if (data.getDevedor() != null) {
                                Log.d("PixResponse_DEVEDOR_AUTO", "=== CAMPOS DO DEVEDOR AUTOMÁTICO ===");
                                Log.d("PixResponse_DEVEDOR_AUTO", "cnpj: " + data.getDevedor().getCnpj());
                                Log.d("PixResponse_DEVEDOR_AUTO", "nome: " + data.getDevedor().getNome());
                            } else {
                                Log.d("PixResponse_DEVEDOR_AUTO", "Devedor é nulo");
                            }

                            // LOG DETALHADO: Campos do Loc
                            if (data.getLoc() != null) {
                                Log.d("PixResponse_LOC_AUTO", "=== CAMPOS DO LOC AUTOMÁTICO ===");
                                Log.d("PixResponse_LOC_AUTO", "id: " + data.getLoc().getId());
                                Log.d("PixResponse_LOC_AUTO", "location: " + data.getLoc().getLocation());
                                Log.d("PixResponse_LOC_AUTO", "tipoCob: " + data.getLoc().getTipoCob());
                                Log.d("PixResponse_LOC_AUTO", "criacao: " + data.getLoc().getCriacao());
                            } else {
                                Log.d("PixResponse_LOC_AUTO", "Loc é nulo");
                            }

                            // LOG DETALHADO: Campos do Valor
                            if (data.getValor() != null) {
                                Log.d("PixResponse_VALOR_AUTO", "=== CAMPOS DO VALOR AUTOMÁTICO ===");
                                Log.d("PixResponse_VALOR_AUTO", "original: " + data.getValor().getOriginal());
                                Log.d("PixResponse_VALOR_AUTO", "modalidadeAlteracao: " + data.getValor().getModalidadeAlteracao());
                            } else {
                                Log.d("PixResponse_VALOR_AUTO", "Valor é nulo");
                            }

                            Log.d("GerarPixAutomatico", "PixCopiaECola: " + (response[0].getData().getPixCopiaECola() != null ? response[0].getData().getPixCopiaECola() : "nulo"));
                            LogHelper.writePixResponse(PopupPedFechamento.this, "PixCopiaECola: " + (response[0].getData().getPixCopiaECola() != null ? response[0].getData().getPixCopiaECola() : "nulo"));
                        }
                    } else {
                        Log.d("PixResponse_COMPLETO_AUTO", "=== RESPONSE É COMPLETAMENTE NULO AUTOMÁTICO ===");
                        LogHelper.writePixError(PopupPedFechamento.this, "Response é nula - possível problema na comunicação com a API (automático)");
                    }

                    if (response[0] != null && response[0].getData() != null && response[0].getData().getPixCopiaECola() != null) {
                        Log.d("GerarPixAutomatico", "PIX gerado com sucesso automaticamente!");
                        LogHelper.writePixLog(PopupPedFechamento.this, "PIX_SUCESSO_AUTO", "PIX gerado com sucesso automaticamente! Código: " + response[0].getData().getPixCopiaECola());
                        LogHelper.writePixLog(PopupPedFechamento.this, "LOG_PATH_AUTO", "Arquivo de log salvo em: " + LogHelper.getLogPath(PopupPedFechamento.this));

                        // Salvar PIX no banco de dados
                        Log.d("GerarPixAutomatico", "Chamando salvarPixNoBanco para salvar dados do PIX");
                        LogHelper.writePixLog(PopupPedFechamento.this, "CHAMANDO_SALVAR_PIX_AUTO", "Iniciando salvamento do PIX no banco de dados automaticamente");
                        salvarPixNoBanco(response[0].getData(), pedidoPix);

                        PopupPedFechamento.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PopupPedFechamento.this, "PIX gerado com sucesso!", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Log.e("GerarPixAutomatico", "Erro: resposta inválida ou PIX não gerado automaticamente");

                        // Verificar se response é null devido a PIX já pago
                        if (response[0] == null) {
                            pixJaPago[0] = true; // Marcar que PIX já foi pago

                            // Exibir dialog informando que o PIX já foi pago
                            PopupPedFechamento.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(PopupPedFechamento.this)
                                            .setTitle("Ops! Algum problema ocorreu")
                                            .setMessage("Tente refazer esta ação novamente!")
                                            .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    finish(); // Fechar a tela atual
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
                                }
                            });
                            LogHelper.writePixLog(PopupPedFechamento.this, "PIX_JA_PAGO_DIALOG", "Dialog de PIX já pago exibido para o usuário");
                            return; // Sair do método sem lançar exceção
                        }

                        String errorMessage = "Erro ao gerar PIX automaticamente. ";
                        String logError = "";
                        if (response[0].getData() == null) {
                            errorMessage += "Dados não retornados.";
                            logError = "Response.getData() é nulo";
                        } else if (response[0].getData().getPixCopiaECola() == null) {
                            errorMessage += "PIX não gerado.";
                            logError = "PixCopiaECola é nulo";
                        }
                        LogHelper.writePixError(PopupPedFechamento.this, "ERRO_FINAL_AUTO: " + logError);
                        LogHelper.writePixLog(PopupPedFechamento.this, "LOG_PATH_AUTO", "Arquivo de log salvo em: " + LogHelper.getLogPath(PopupPedFechamento.this));
                        exception[0] = new Exception(errorMessage);
                    }

                } catch (Exception e) {
                    Log.e("GerarPixAutomatico", "Exceção capturada: " + e.getMessage(), e);
                    LogHelper.writePixError(PopupPedFechamento.this, "EXCECAO_CAPTURADA_AUTO: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    LogHelper.writePixError(PopupPedFechamento.this, "STACK_TRACE_AUTO: " + android.util.Log.getStackTraceString(e));
                    LogHelper.writePixLog(PopupPedFechamento.this, "LOG_PATH_AUTO", "Arquivo de log salvo em: " + LogHelper.getLogPath(PopupPedFechamento.this));
                    exception[0] = e;
                } finally {
                    latch.countDown();
                }
            }
        }).start();

        // Aguardar a conclusão da thread
        latch.await();

        // Se PIX já foi pago, lançar exceção específica para interromper fluxo
        if (pixJaPago[0]) {
            LogHelper.writePixLog(this, "PIX_JA_PAGO_FLUXO_INTERROMPIDO", "Fluxo interrompido devido a PIX já pago");
            throw new PixJaPagoException("PIX já foi pago para este pedido");
        }

        // Se houve exceção, relançar
        if (exception[0] != null) {
            throw exception[0];
        }
    }

    private void aplicarStatusLiberacaoComercialPorParcelamentoCartao() {
        if (pedido == null || pedido.getFinanceiro() == null || pedido.getFinanceiro().isEmpty()) {
            return;
        }

        for (Financeiro financeiro : pedido.getFinanceiro()) {
            if (exigeLiberacaoComercialPorParcelamentoCartao(financeiro)) {
                pedido.setCodStatus(1);
                return;
            }
        }
    }

    private boolean exigeLiberacaoComercialPorParcelamentoCartao(Financeiro financeiro) {
        if (financeiro == null || financeiro.getEvento() == null) {
            return false;
        }

        String evento = financeiro.getEvento().trim().toUpperCase(Locale.ROOT);
        boolean formaCartao = "CARTAO".equals(evento) || "CARTAO MAN".equals(evento) || "CARTAO RET".equals(evento);
        int parcelas = financeiro.getQtprc();

        return formaCartao && parcelas >= 4 && parcelas <= 10;
    }

    private String obterEmailPorCodfil(int codfil) {
        // Mesma lógica do LinkCartaoRepositorio.cs
        switch (codfil) {
            case 1:
                return "financeiro@acalfortaleza.com.br";
            case 2:
                return "financeiro2@acalfortaleza.com.br";
            case 3:
                return "financeiro3@acalfortaleza.com.br";
            case 4:
                return "financeiro4@acalfortaleza.com.br";
            case 5:
                return "financeiro5@acalfortaleza.com.br";
            case 6:
                return "financeiro6@acalfortaleza.com.br";
            case 7:
                return "financeiro7@acalfortaleza.com.br";
            case 8:
                return "financeiro8@acalfortaleza.com.br";
            case 9:
                return "financeiro9@acalfortaleza.com.br";
            case 10:
                return "financeiro10@acalfortaleza.com.br";
            case 11:
                return "financeiro11@acalfortaleza.com.br";
            case 12:
                return "financeiro12@acalfortaleza.com.br";
            case 13:
                return "financeiro13@acalfortaleza.com.br";
            case 14:
                return "financeiro14@acalfortaleza.com.br";
            case 15:
                return "financeiro15@acalfortaleza.com.br";
            case 16:
                return "financeiro16@acalfortaleza.com.br";
            case 17:
                return "financeiro17@acalfortaleza.com.br";
            case 18:
                return "financeiro18@acalfortaleza.com.br";
            case 19:
                return "financeiro19@acalfortaleza.com.br";
            case 20:
                return "financeiro20@acalfortaleza.com.br";
            case 21:
                return "financeiro21@acalfortaleza.com.br";
            case 22:
                return "financeiro22@acalfortaleza.com.br";
            case 23:
                return "financeiro23@acalfortaleza.com.br";
            case 24:
                return "financeiro24@acalfortaleza.com.br";
            case 25:
                return "financeiro25@acalfortaleza.com.br";
            case 26:
                return "financeiro26@acalfortaleza.com.br";
            case 27:
                return "financeiro27@acalfortaleza.com.br";
            case 28:
                return "financeiro28@acalfortaleza.com.br";
            case 29:
                return "financeiro29@acalfortaleza.com.br";
            case 30:
                return "financeiro30@acalfortaleza.com.br";
            default:
                return "financeiro@acalfortaleza.com.br";
        }
    }

    private void salvarPixNoBanco(PixResponse.Data pixData, Pedidos pedido) {
        try {
            Log.d("SalvarPixNoBanco", "Iniciando salvamento do PIX no banco de dados");

            // Obter o número do pedido do retorno da API (se disponível) ou do pedido atual
            long numpedvenLong = 0;
            if (retorno != null && !retorno.isEmpty() && !retorno.contains("Pedido não cadastrado!") && !retorno.equals("N")) {
                try {
                    numpedvenLong = Long.parseLong(retorno); // retorno contém o número do pedido salvo
                    Log.d("SalvarPixNoBanco", "Usando numpedven do retorno da API: " + numpedvenLong);
                } catch (NumberFormatException e) {
                    Log.e("SalvarPixNoBanco", "ERRO: Retorno da API não é um número válido: " + retorno);
                    LogHelper.writePixError(PopupPedFechamento.this, "ERRO_RETORNO_INVALIDO - retorno: " + retorno + ", erro: " + e.getMessage());
                    return;
                }
            } else if (pedido.getNumpedven() > 0) {
                // Validar se o valor double está dentro do range válido para long
                double numpedvenDouble = pedido.getNumpedven();
                if (numpedvenDouble > Long.MAX_VALUE || numpedvenDouble < Long.MIN_VALUE) {
                    Log.e("SalvarPixNoBanco", "ERRO: Valor numpedven fora do range válido para long: " + numpedvenDouble);
                    LogHelper.writePixError(PopupPedFechamento.this, "ERRO_NUMPEDVEN_OVERFLOW - Valor: " + numpedvenDouble);
                    return;
                }
                // Converter double para long evitando notação científica
                numpedvenLong = (long) numpedvenDouble;
                Log.d("SalvarPixNoBanco", "Usando numpedven do objeto pedido: " + numpedvenLong + " (valor original double: " + numpedvenDouble + ")");
            } else {
                Log.e("SalvarPixNoBanco", "ERRO: Não foi possível obter um numpedven válido!");
                LogHelper.writePixError(PopupPedFechamento.this, "ERRO_NUMPEDVEN_NAO_ENCONTRADO - retorno: " + retorno + ", pedido.numpedven: " + pedido.getNumpedven());
                return;
            }

            // Criar o request com os dados do PIX
            SalvarPixRequest request = new SalvarPixRequest();
            request.setTxid(pixData.getTxid());
            request.setPixCopiaECola(pixData.getPixCopiaECola());
            request.setValor(pixData.getValor() != null ? Double.parseDouble(pixData.getValor().getOriginal()) : 0.0);
            request.setNumpedven(numpedvenLong);
            request.setCodcli(pedido.getCliente().getCodcli());
            request.setCodfil(pedido.getCodfil());
            // Verificar se o status está definido, caso contrário usar 'ATIVO' como padrão
            String status = (pixData.getStatus() != null && !pixData.getStatus().isEmpty()) ? pixData.getStatus() : "ATIVO";
            request.setStatus(status);

            // Logs detalhados para verificar os dados e tipos
            Log.d("SalvarPixNoBanco", "=== DADOS PARA SALVAR PIX ===");
            Log.d("SalvarPixNoBanco", "Txid: " + request.getTxid() + " (tipo: String)");
            Log.d("SalvarPixNoBanco", "PixCopiaECola: " + request.getPixCopiaECola() + " (tipo: String)");
            Log.d("SalvarPixNoBanco", "Valor: " + request.getValor() + " (tipo: double)");
            Log.d("SalvarPixNoBanco", "Numpedven: " + request.getNumpedven() + " (tipo: long)");
            Log.d("SalvarPixNoBanco", "Codcli: " + request.getCodcli() + " (tipo: int)");
            Log.d("SalvarPixNoBanco", "Codfil: " + request.getCodfil() + " (tipo: int)");
            Log.d("SalvarPixNoBanco", "Status: " + request.getStatus() + " (tipo: String)");
            Log.d("SalvarPixNoBanco", "Status original do pixData: " + pixData.getStatus());
            Log.d("SalvarPixNoBanco", "==============================");

            // Log adicional para verificar os dados do pedido
            LogHelper.writePixLog(PopupPedFechamento.this, "DADOS_SALVAMENTO_PIX",
                    "Txid: " + request.getTxid() +
                            ", Valor: " + request.getValor() +
                            ", Numpedven: " + request.getNumpedven() +
                            ", Codcli: " + request.getCodcli() +
                            ", Codfil: " + request.getCodfil() +
                            ", Status: " + request.getStatus());

            // Verificar se numpedven é válido antes de enviar
            if (numpedvenLong <= 0) {
                Log.e("SalvarPixNoBanco", "ERRO: Numpedven é zero ou negativo! Valor: " + numpedvenLong);
                LogHelper.writePixError(PopupPedFechamento.this, "ERRO_NUMPEDVEN_INVALIDO - Numpedven: " + numpedvenLong);
                return;
            }

            // Obter token de autenticação
            String token = "Bearer " + usuarioLogado.get("token");

            // Criar retrofit e fazer a chamada para a API
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS).build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiGemco.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiGemco apiService = retrofit.create(ApiGemco.class);
            Call<String> call = apiService.salvarPixGerado(request, token);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        Log.d("SalvarPixNoBanco", "PIX salvo com sucesso no banco de dados: " + response.body());
                        LogHelper.writePixLog(PopupPedFechamento.this, "PIX_SALVO_BANCO", "PIX salvo com sucesso no banco - Response: " + response.body());
                    } else {
                        Log.e("SalvarPixNoBanco", "Erro ao salvar PIX no banco - Código: " + response.code() + ", Mensagem: " + response.message());
                        LogHelper.writePixError(PopupPedFechamento.this, "ERRO_SALVAR_PIX_BANCO - Erro HTTP " + response.code() + ": " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("SalvarPixNoBanco", "Falha na comunicação ao salvar PIX no banco: " + t.getMessage(), t);
                    LogHelper.writePixError(PopupPedFechamento.this, "FALHA_SALVAR_PIX_BANCO - Erro de comunicação: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e("SalvarPixNoBanco", "Exceção ao tentar salvar PIX no banco: " + e.getMessage(), e);
            LogHelper.writePixError(PopupPedFechamento.this, "EXCECAO_SALVAR_PIX_BANCO - Exceção: " + e.getMessage());
        }
    }



}


        
