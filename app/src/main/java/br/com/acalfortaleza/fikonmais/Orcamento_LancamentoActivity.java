package br.com.acalfortaleza.fikonmais;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.HardwareRenderer;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;

import br.com.acalfortaleza.fikonmais.adapters.ItemOrcamentoAdapter;
import br.com.acalfortaleza.fikonmais.applications.AzurePDFOrcamentos;
import br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener;
import br.com.acalfortaleza.fikonmais.applications.TemplatePDFOrcPJ;
import br.com.acalfortaleza.fikonmais.applications.TemplatePDFOrcamento;
import br.com.acalfortaleza.fikonmais.dao.OrcamentoAlteradoDao;
import br.com.acalfortaleza.fikonmais.fragments.ProdutoFragments;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.modal.ModalItens;
import br.com.acalfortaleza.fikonmais.models.AmbienteProduto;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.DadosBancarios;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.NomeAmbiente;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.PrecoConc;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento;
import br.com.acalfortaleza.fikonmais.popup.PopupAlterarContato;
import br.com.acalfortaleza.fikonmais.popup.PopupCodigoMotor;
import br.com.acalfortaleza.fikonmais.popup.PopupEnderecoEntrega;
import br.com.acalfortaleza.fikonmais.popup.PopupItemOrc;
import br.com.acalfortaleza.fikonmais.popup.PopupItemOrcTodos;
import br.com.acalfortaleza.fikonmais.popup.PopupOrcCustomPF;
import br.com.acalfortaleza.fikonmais.popup.PopupVerificaPromoFikon;

public class Orcamento_LancamentoActivity extends BaseAcalActivity implements ModalItens.ActionListener {

    private static final int REQUEST_VERIFICA_PROMO_FIKON = 1101;

    public static Button btn_finalizar;
    public static Button btn_adcionar;
    private static ItemOrcamentoAdapter adapter;
    private List<ItemOrcamento> mList;
    private RecyclerView mRecycleView;
    public static Orcamento orcamento;
    private static boolean preservarValoresCarregados = false;
    private static boolean aplicarRegraPromocionalValoresCarregados = false;
    private static double vlTotalCarregado = 0.0d;
    private static double vlFreteCarregado = 0.0d;
    private static ImageView img_cliente;
    private CardView cardCliente;
    private CardView cardtotais;
    private static TextView txtNome;
    private static TextView txtEmail;
    private static TextView txtCgccpf;
    private static TextView txtFone;
    private static TextView txtTotal;
    private static TextView txtQtdItens;
    private static TextView txtPeso;
    private static TextView txtMargem;
    private static TextView txtServEntrega;
    public static TextView txtNumOrc;
    private int index;
    private boolean alteracao;
    private static HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private ProgressDialog dialog;
    private Handler handler;
    private String retorno;
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    final DecimalFormat df = new DecimalFormat("0.00");
    final DecimalFormat dfInteiro = new DecimalFormat("0");
    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
    private OrcamentoHelper helper;

    public static TextView txtFrete;
    private ProdutoHelper produtoHelper;
    private ItemOrcamento itemOrcamento;
    private static DatabaseReference myRef;
    private static FirebaseDatabase database;
    private static boolean isGerente;
    private static ImageView marg_indicador;

    private AlertDialog alertaDialog;

    private List<PrecoConc> mListPreco = new ArrayList<>();

    private OrcamentoAlteradoDao orcamentoAlteradoDao;
    private AzurePDFOrcamentos azurePDFOrcamentos = new AzurePDFOrcamentos();
    public static List<NomeAmbiente> listNomesAmbientes;
    private HashMap<Long, String> mapTotaisAmbientes;
    public static boolean ambientesAlterados;
    public static boolean aberturaPallet;
    public static String resumoPedido;

    private long mLastClickTime = 0;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.orc_lanc_status_bar_bg);
        if (statusBarBg == null) {
            return;
        }

        ViewGroup.LayoutParams params = statusBarBg.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBarBg.setLayoutParams(params);
    }

    private void ajustarRodapeBotoes() {
        final int margemBase = Math.round(20 * getResources().getDisplayMetrics().density);

        if (btn_finalizar != null) {
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

    public static void atualizaTotais(List<ItemOrcamento> listaItens) {

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        final DecimalFormat df = new DecimalFormat("0.00");

        BigDecimal valorTotal = new BigDecimal("0.00");
        BigDecimal valorTotalMarg = new BigDecimal("0.00");
        BigDecimal valorCusto = new BigDecimal("0.00");
        BigDecimal valorFrete = BigDecimal.valueOf(orcamento.getVlFrete());
        double valorEncargos = 0.00d;
        BigDecimal valorIcms = new BigDecimal("0.00");
        double valorMargem = 0.00d;
        BigDecimal pesoTotal = new BigDecimal("0.00");
        BigDecimal valorIcmRed = new BigDecimal("0.00");
        BigDecimal valorDesconto = new BigDecimal("0.00");
        BigDecimal calcDesconto = new BigDecimal("0.00");

        if(listaItens != null && listaItens.size() > 0){
            txtQtdItens.setText("Qtd. Itens: " + listaItens.size());

            for (ItemOrcamento item : listaItens) {
                try {
                    android.util.Log.d("ORC_LIST_IMG", "Item COD " + item.getCoditprod() + " imagem " + String.valueOf(item.getImagem()));
                } catch (Exception ignored) { }

                double precoUnitarioBrutoExibicao = getPrecoUnitarioBrutoExibicao(item);
                double valorLiquidoExibicao = getValorLiquidoExibicao(item);

                valorTotal = valorTotal.setScale(2, RoundingMode.FLOOR).add(BigDecimal.valueOf(precoUnitarioBrutoExibicao).multiply(BigDecimal.valueOf(item.getQtcomp())));
                valorTotalMarg = valorTotalMarg.add(BigDecimal.valueOf(valorLiquidoExibicao).setScale(2, RoundingMode.DOWN));
                valorCusto = valorCusto.add(BigDecimal.valueOf((item.getCusto()) * item.getQtcomp()));
                valorDesconto = valorDesconto.add(BigDecimal.valueOf((item.getVlDescItem()) * item.getQtcomp()));

                pesoTotal = pesoTotal.add(BigDecimal.valueOf(item.getQtcomp() * item.getPeso()));

                if (item.getIcmsRed() == 0) {
                    valorIcms = valorIcms.add(BigDecimal.valueOf((valorLiquidoExibicao * item.getIcms()) / 100));

                } else {
                    valorIcms = valorIcms.add(BigDecimal.valueOf((((valorLiquidoExibicao) - ((valorLiquidoExibicao) * item.getIcmsRed()) / 100)
                            * item.getIcms()) / 100));

                }


            }
        }else{

        }

        // Popula o campo 'Serv. Entrega' mapeando IDs do cache do popup de orcamento para nomes via API
        try {
            if (orcamento != null) {
                java.util.List<Integer> ids = br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.getOrDefault((int) orcamento.getNumorc(), new java.util.ArrayList<Integer>());
                if (ids != null && !ids.isEmpty()) {
                    okhttp3.OkHttpClient client = new okhttp3.OkHttpClient.Builder().connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS).readTimeout(30, java.util.concurrent.TimeUnit.SECONDS).build();
                    retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                            .baseUrl(br.com.acalfortaleza.fikonmais.service.ApiGemco.BASE_URL)
                            .client(client)
                            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                            .build();
                    br.com.acalfortaleza.fikonmais.service.ApiGemco api = retrofit.create(br.com.acalfortaleza.fikonmais.service.ApiGemco.class);
                    String token = "Bearer " + br.com.acalfortaleza.fikonmais.service.ApiGemco.token;
                    retrofit2.Call<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> call = api.GetFreteServico(token);
                    call.enqueue(new retrofit2.Callback<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>>() {
                        @Override
                        public void onResponse(retrofit2.Call<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> call, retrofit2.Response<java.util.List<br.com.acalfortaleza.fikonmais.models.FreteServico>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                java.util.List<String> nomes = new java.util.ArrayList<>();
                                double valorServicosSelecionados = 0.0d;
                                for (br.com.acalfortaleza.fikonmais.models.FreteServico fs : response.body()) {
                                    if (ids.contains(fs.getIdServico())) {
                                        nomes.add(fs.getServicoFrete());
                                        valorServicosSelecionados += Math.max(0.0d, fs.getValorServico());
                                    }
                                }
                                if (!nomes.isEmpty()) {
                                    txtServEntrega.setText("Serv. Entrega: " + android.text.TextUtils.join(", ", nomes));

                                    double valorAtualServico = 0.0d;
                                    if (orcamento != null) {
                                        valorAtualServico = Math.max(
                                                Math.max(0.0d, orcamento.getVlServicosAdicionais()),
                                                br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.getOrDefault((int) orcamento.getNumorc(), 0.0d)
                                        );
                                    }

                                    if (valorServicosSelecionados > 0.0d && Math.abs(valorAtualServico - valorServicosSelecionados) > 0.009d) {
                                        if (orcamento != null && orcamento.getNumorc() > 0) {
                                            br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.put((int) orcamento.getNumorc(), valorServicosSelecionados);
                                            orcamento.setVlServicosAdicionais(valorServicosSelecionados);
                                        }

                                        if (txtFrete != null && orcamento != null && orcamento.getItens() != null) {
                                            txtFrete.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    atualizaTotais(orcamento.getItens());
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    txtServEntrega.setText("Serv. Entrega: -");
                                }
                            }
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
        } catch (Exception ex) {
            txtServEntrega.setText("Serv. Entrega: -");
        }





        if (listaItens != null && listaItens.size() > 0) {
            if (valorTotalMarg.doubleValue()==0 || valorCusto.doubleValue() == 0) {
                valorEncargos = 0.00d;
                valorMargem = 0.00d;
            } else {

                valorEncargos = ((valorTotalMarg).doubleValue() * listaItens.get(0).getEncargos()) / 100;

                //calculo anterior
                //valorMargem = ((valorTotalMarg - valorEncargos - valorIcms - valorCusto) / (valorTotalMarg - valorEncargos - valorIcms)) * 100;

                valorMargem = ((valorTotalMarg.doubleValue() - valorEncargos - valorIcms.doubleValue() - valorCusto.doubleValue()) / (valorTotalMarg.doubleValue())) * 100;

            }
        } else {
             valorEncargos = 0.00d;
             valorMargem = 0.00d;
        }


        valorTotal = valorTotal.setScale(2, RoundingMode.FLOOR);

        android.util.Log.d("AtualizaTotais", "=== ATUALIZANDO TOTAIS ===");
        android.util.Log.d("AtualizaTotais", "Valor Total (itens): " + valorTotal.doubleValue());
        android.util.Log.d("AtualizaTotais", "Valor Desconto: " + valorDesconto.doubleValue());
        android.util.Log.d("AtualizaTotais", "VlFrete atual: " + orcamento.getVlFrete());
        android.util.Log.d("AtualizaTotais", "VlFreteSelecionado: " + orcamento.getVlFreteSelecionado());

        double displayedFrete = getFreteComServicosResumo();

        // Atualiza o campo de exibição do frete (somente visual)
        try{
            txtFrete.setText("Frete: " + real.format(displayedFrete));
        }catch(Exception ex){
            // se txtFrete não estiver disponível no contexto atual, ignorar
        }

        // Soma o total dos itens (já com desconto) + valor de frete exibido (frete base + serviços em cache)
        java.math.BigDecimal totalItensComDesconto = valorTotal.subtract(valorDesconto);
        java.math.BigDecimal totalComFrete = totalItensComDesconto.add(java.math.BigDecimal.valueOf(displayedFrete));

        double totalParaExibicao = getTotalParaExibicao(totalComFrete.doubleValue(), displayedFrete);
        txtTotal.setText("Total: " + real.format(totalParaExibicao));

        orcamento.setVlTotal(devePreservarValoresCarregados()
            ? vlTotalCarregado
            : totalItensComDesconto.doubleValue());
        orcamento.setPmargRent(valorMargem);
        orcamento.setVlDesconto(valorDesconto.doubleValue());
        orcamento.setPeso(pesoTotal.doubleValue());
        
        // Mantem no objeto o frete base salvo/calculado, separado do valor exibido com servicos.
        orcamento.setVlFreteCalculado(Math.max(0.0d, orcamento.getVlFrete()));


            



        if (valorMargem >= 36) {
            marg_indicador.setImageResource(R.drawable.fundo_bt_flutuante);
            txtMargem.setTextColor(Color.parseColor("#00bfa5"));
        }
        if (valorMargem >= 34 && valorMargem < 36) {
            marg_indicador.setImageResource(R.drawable.fundo_bt_flutuante_orange);
            txtMargem.setTextColor(Color.parseColor("#C8671E"));
        }

        if (valorMargem < 34) {
            marg_indicador.setImageResource(R.drawable.fundo_bt_flutuante_red);
            txtMargem.setTextColor(Color.parseColor("#A61553"));
        }


        txtPeso.setText("Peso Total: " + df.format(pesoTotal) + "kg");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("orcamentos");

        myRef.child(usuarioLogado.get("codigo")).setValue(orcamento);


        if(isGerente){
            txtMargem.setText("Margem: " + df.format(valorMargem) + "%");
            txtMargem.setVisibility(View.VISIBLE);
        }else{
            txtMargem.setText("Margem: " + df.format(valorMargem) + "%");
            txtMargem.setVisibility(View.INVISIBLE);
        }

        CriarResumoPedido();

        /*if(isGerente){
            if (valorMargem > 0) {
                txtMargem.setText("Margem: " + df.format(valorMargem) + "%");
            } else {
                txtMargem.setText("Margem: 0%");
            }
            txtMargem.setVisibility(View.VISIBLE);

        }else{
            if (valorMargem > 0) {
                txtMargem.setText("Margem: ");
            } else {
                txtMargem.setText("Margem: ");
            }
            txtMargem.setVisibility(View.INVISIBLE);

        }*/



    }

    private static double getFreteBaseExibicao(double pesoTotalKg, double servicosCache) {
        if (orcamento == null) {
            return 0.0d;
        }

        if (orcamento.getVlFreteCalculado() > 0.0d) {
            return orcamento.getVlFreteCalculado();
        }

        if (orcamento.getVlFrete() > 0.0d) {
            double freteBaseSalvo = orcamento.getVlFrete() - servicosCache;
            if (freteBaseSalvo > 0.0d || servicosCache == 0.0d) {
                return Math.max(0.0d, freteBaseSalvo);
            }
        }

        if (usuarioLogado.get("codfil").equals("501")) {
            if (pesoTotalKg < 500) {
                return 50.0d;
            }
            return Math.floor(100 * (pesoTotalKg / 1000));
        }

        if (pesoTotalKg < 1000) {
            return orcamento.getVlFreteSelecionado();
        }

        return Math.floor(orcamento.getVlFreteSelecionado() * (pesoTotalKg / 1000));
    }

    private static double getFreteComServicosResumo() {
        if (orcamento == null) {
            return 0.0d;
        }

        double freteServ = Math.max(0.0d, orcamento.getVlServicosAdicionais());
        if (freteServ == 0.0d) {
            try {
                int numOrcKey = (int) orcamento.getNumorc();
                Double servCache = br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.getOrDefault(numOrcKey, 0.0d);
                freteServ = Math.max(0.0d, servCache);
            } catch (Exception ignore) {
                freteServ = Math.max(0.0d, orcamento.getVlServicosAdicionais());
            }
        }

        double freteSalvo = Math.max(0.0d, orcamento.getVlFrete());
        return freteSalvo + freteServ;
    }

    public static void prepararPreservacaoValoresCarregados(Orcamento orcamentoAtual) {
        if (orcamentoAtual != null && orcamentoAtual.getNumorc() > 0) {
            preservarValoresCarregados = true;
            vlTotalCarregado = orcamentoAtual.getVlTotal();
            vlFreteCarregado = Math.max(0.0d, orcamentoAtual.getVlFrete());
            aplicarRegraPromocionalValoresCarregados = deveAplicarRegraPromocionalAosValoresCarregados(orcamentoAtual);
        } else {
            preservarValoresCarregados = false;
            aplicarRegraPromocionalValoresCarregados = false;
            vlTotalCarregado = 0.0d;
            vlFreteCarregado = 0.0d;
        }
    }

    public static void marcarOrcamentoComoRecalculavel() {
        preservarValoresCarregados = false;
        aplicarRegraPromocionalValoresCarregados = false;
        vlFreteCarregado = 0.0d;
    }

    public static boolean devePreservarValoresCarregados() {
        return preservarValoresCarregados && orcamento != null && orcamento.getNumorc() > 0;
    }

    private static double getTotalParaExibicao(double totalCalculado, double frete) {
        if (devePreservarValoresCarregados()) {
            return vlTotalCarregado + frete;
        }
        return totalCalculado;
    }

    public static void carregaDadosCliente(Cliente cliente) {
        orcamento.setCliente(cliente);

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

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("orcamentos");

        myRef.child(usuarioLogado.get("codigo")).setValue(orcamento);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_orcamento_lancamento);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.orc_lanc_toolbar);
        setSupportActionBar(toolbar);

        try {
            // Evita herdar valores de sessão anterior para orçamentos sem número
            br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.remove(0);
            br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.remove(0);
        } catch (Exception ignore) {}

        orcamento = (Orcamento) getIntent().getSerializableExtra("orcamento");
        if (orcamento != null) {
            try {
                android.util.Log.d("ORC_LANC_ACTIVITY", "Orcamento received: " + new Gson().toJson(orcamento));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        orcamentoAlteradoDao = new OrcamentoAlteradoDao(getApplicationContext());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Orçamento");
        if(orcamento != null && orcamento.getNumorc()>0) {
            getSupportActionBar().setSubtitle(dfInteiro.format(orcamento.getNumorc()));
        }
        getSupportActionBar().setElevation(0);


        alteracao = getIntent().getBooleanExtra("alteracao", false);
        helper = new OrcamentoHelper();
        produtoHelper = new ProdutoHelper();
        loginHelper = new LoginHelper(Orcamento_LancamentoActivity.this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();

        if(usuarioLogado.get("isgerente").equals("SIM")){
            isGerente = true;
        }else{
            isGerente = false;
        }

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
        txtQtdItens = findViewById(R.id.ped_lanc_txtQtd);
        txtServEntrega = findViewById(R.id.ped_lanc_txtServEntrega);
        txtTotal = findViewById(R.id.ped_lanc_txtTotalOrc);
        txtMargem = findViewById(R.id.ped_lanc_txtMargem);
        btn_finalizar = findViewById(R.id.ped_lanc_btnSalvar);
        cardCliente = findViewById(R.id.orc_lanc_cardCliente);
        cardtotais = findViewById(R.id.ped_lanc_cardTotal);
        txtNumOrc = findViewById(R.id.ped_lanc_numOrc);
        btn_adcionar = findViewById(R.id.ped_lanc_btnAdcionar);
        marg_indicador = findViewById(R.id.adp_ped_indicador2);
        txtFrete = findViewById(R.id.ActivityOrcamentoLancamento_TxtFrete);
        ajustarRodapeBotoes();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                orcamentoAlteradoDao.inserir(dfInteiro.format(orcamento.getNumorc()), orcamento, false);
                orcamentoAlteradoDao.editarAlterado(dfInteiro.format(orcamento.getNumorc()), true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                orcamentoAlteradoDao.inserir(dfInteiro.format(orcamento.getNumorc()), orcamento, false);
                orcamentoAlteradoDao.editarAlterado(dfInteiro.format(orcamento.getNumorc()), true);
            }

        };

        txtNome.addTextChangedListener(textWatcher);
        txtCgccpf.addTextChangedListener(textWatcher);
        txtEmail.addTextChangedListener(textWatcher);
        txtFone.addTextChangedListener(textWatcher);
        txtPeso.addTextChangedListener(textWatcher);
        txtQtdItens.addTextChangedListener(textWatcher);
        txtTotal.addTextChangedListener(textWatcher);
        txtMargem.addTextChangedListener(textWatcher);
        txtNumOrc.addTextChangedListener(textWatcher);



        btn_adcionar.setEnabled(true);
        btn_adcionar.setBackgroundResource(R.drawable.backgroudbuttom);
        btn_finalizar.setEnabled(true);
        btn_finalizar.setBackgroundResource(R.drawable.backgroudbuttom);



        btn_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                /*if(orcamento.getItens()!=null && orcamento.getItens().size()>0){

                    for (ItemOrcamento item: orcamento.getItens()) {

                        if(item.getSituacao()!=null && item.getSituacao().equals("EN")){

                            if(item.getFluigEncomenda()<=0 || item.getDataPrevisaoEncomenda() ==  null || item.getDataPrevisaoEncomenda().trim().equals("")){

                                new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this)
                                        .setTitle("Informe a previsão de encomenda")
                                        .setMessage("Informe a data de previsão de encomenda para todos os itens do orçamento que possuem situação encomenda.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        }).show();

                                return;

                            }

                        }

                    }

                }*/

                if (adapter != null && adapter.getItemCount() > 0) {
                    if (txtNome.getText().equals("Informe o cliente")) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);
                        builder.setTitle("Ops :|");
                        builder.setMessage("Para finalizar o orçamento, informe o cliente!");
                        builder
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        Intent intent = new Intent(Orcamento_LancamentoActivity.this, ClientesActivity.class);
                                        intent.putExtra("orcamento", true);
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

                        if (orcamento.getCliente().getNatjur().equals("F")) {
                            orcamento.setUrlMotor(usuarioLogado.get("urlmotor"));
                            if(orcamento.getCodfil()==0){
                                orcamento.setCodfil(Integer.parseInt(usuarioLogado.get("codfil")));
                            }
                            // Intent intent = new Intent(Orcamento_LancamentoActivity.this, PopupCodigoMotor.class);
                            // intent.putExtra("orcamento", orcamento);
                            // startActivity(intent);
                            abrirPopupVerificaPromoFikon();
                            return;
                        }


                    }

                    restaurarMarcadoresPromocionaisSalvos(orcamento);

                    if(!txtNome.getText().equals("Informe o cliente") && orcamento.getCliente().getNatjur().equals("J") && orcamento.getCodContato() == null && Integer.parseInt(usuarioLogado.get("codfil"))!= 3
                            && Integer.parseInt(usuarioLogado.get("codfil"))!= 21  && Integer.parseInt(usuarioLogado.get("codfil"))!= 501  ){

                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);
                        builder.setTitle("Atenção ;)");
                        builder.setMessage("O Contato responsável pelo orçamento nao foi informado! ");
                        builder
                                .setCancelable(false)
                                .setPositiveButton("Informar o contato", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        if(orcamento.getCliente().getContatos() != null && orcamento.getCliente().getContatos().size()>0 ){
                                            Intent intent = new Intent(Orcamento_LancamentoActivity.this, PopupAlterarContato.class);
                                            intent.putExtra("contatos", (Serializable) orcamento.getCliente().getContatos());
                                            startActivity(intent);
                                        }else{
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);
                                            builder.setTitle("Ops :|");
                                            builder.setMessage("Empresa não possui contatos cadastrados!");
                                            builder
                                                    .setCancelable(false)
                                                    .setPositiveButton("Cadastrar novo contato", new DialogInterface.OnClickListener() {

                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();

                                                            Intent intent = new Intent(Orcamento_LancamentoActivity.this, ClientesActivity.class);
                                                            intent.putExtra("orcamento", true);
                                                            intent.putExtra("cgccpf", String.valueOf(orcamento.getCliente().getCodcli()));
                                                            startActivity(intent);

                                                        }

                                                    });

                                            /*
                                            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                             */

                                            builder.create().show();

                                        }


                                    }

                                });

                        /*
                        builder.setNegativeButton("Não informar", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                orcamento.setUrlMotor(usuarioLogado.get("urlmotor"));
                                Intent intent = new Intent(Orcamento_LancamentoActivity.this, PopupCodigoMotor.class);
                                intent.putExtra("orcamento", orcamento);
                                startActivity(intent);
                            }
                        });
                        */

                        builder.create().show();

                    }else{
                        if(!txtNome.getText().equals("Informe o cliente") && orcamento.getCliente().getNatjur().equals("J") && Integer.parseInt(usuarioLogado.get("codfil"))==3
                                || Integer.parseInt(usuarioLogado.get("codfil"))==21 ||  Integer.parseInt(usuarioLogado.get("codfil"))==501  ){
                            orcamento.setUrlMotor(usuarioLogado.get("urlmotor"));
                            if(orcamento.getCodfil()==0){
                                orcamento.setCodfil(Integer.parseInt(usuarioLogado.get("codfil")));
                            }
                            // Intent intent = new Intent(Orcamento_LancamentoActivity.this, PopupCodigoMotor.class);
                            // intent.putExtra("orcamento", orcamento);
                            // startActivity(intent);
                            abrirPopupVerificaPromoFikon();
                            return;
                        }else
                             if(!txtNome.getText().equals("Informe o cliente") && orcamento.getCliente().getNatjur().equals("J") && !orcamento.getCodContato().equals("")  ){
                                 orcamento.setUrlMotor(usuarioLogado.get("urlmotor"));
                                 if(orcamento.getCodfil()==0){
                                     orcamento.setCodfil(Integer.parseInt(usuarioLogado.get("codfil")));
                                 }
                                 // Intent intent = new Intent(Orcamento_LancamentoActivity.this, PopupCodigoMotor.class);
                                 // intent.putExtra("orcamento", orcamento);
                                 // startActivity(intent);
                                 abrirPopupVerificaPromoFikon();
                                 return;
                        }
                    }

                } else {

                    Toast.makeText(Orcamento_LancamentoActivity.this, "Informe o produto!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        cardCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orcamento.getCliente() != null) {
                    Intent intent = new Intent(Orcamento_LancamentoActivity.this, ClientesActivity.class);
                    intent.putExtra("cgccpf", String.valueOf(orcamento.getCliente().getCodcli()));
                    intent.putExtra("orcamento", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Orcamento_LancamentoActivity.this, ClientesActivity.class);
                    intent.putExtra("orcamento", true);
                    startActivity(intent);
                }
            }
        });



        if (orcamento != null) {
            final DecimalFormat dfInteiro = new DecimalFormat("0");

            if(orcamento.getItens()!=null && orcamento.getItens().size()>0){
                for (ItemOrcamento item: orcamento.getItens()) {
                    if(item!=null && item.getId()==0){
                        item.setId(new Random().nextLong());
                    }
                    if(item!=null && item.getListAmbientesProdutos()==null){
                        item.setListAmbientesProdutos(new ArrayList<>());
                    }
                    // Para orcamentos ja salvos, preserve os valores vindos da API.
                    // Recalcular o desconto a partir do DPI aqui distorce o preco do item ao reabrir.
                    if(item!=null && item.getDpi()>0 && orcamento.getNumorc() <= 0){

                        item.setVlDescItem(
                            BigDecimal
                                .valueOf( ( item.getDpi() / 100 ) * item.getPrecoUnit() )
                                .setScale(2, RoundingMode.FLOOR)
                                .doubleValue()
                        );

                    }
                }
            }

            if (deveNormalizarAoAbrir(orcamento)) {

                // Cache local de promocao desativado: nao restaurar precos/descontos antigos.
                normalizarPrecoUnitarioItensPromocionaisCarregados(orcamento);
                prepararPreservacaoValoresCarregados(orcamento);
            } else {
                marcarOrcamentoComoRecalculavel();
            }
            mList = orcamento.getItens();
            adapter = new ItemOrcamentoAdapter(this, mList,isGerente);
            mRecycleView.setAdapter(adapter);
            atualizaTotais(orcamento.getItens());

            // SUBSTITUA o bloco da Thread (linhas 705 a 739) por este:
            if (orcamento.getNumorc() > 0) {
                new Thread() {
                    public void run() {
                        try {
                            android.util.Log.d("FreteServicoDebug", "=== Consultando FreteServico (JSON) ao abrir orçamento ===");
                            android.util.Log.d("FreteServicoDebug", "Número do orçamento: " + orcamento.getNumorc());

                            // 1. Construir o cliente Retrofit (seu código já faz isso)
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

                            // 2. Executar a chamada e obter a resposta completa
                            android.util.Log.d("FreteServicoDebug", "Chamando GetFreteServicoOrcamento...");
                            retrofit2.Response<br.com.acalfortaleza.fikonmais.model.FreteServicoResposta> response = api.GetFreteServicoOrcamento(token, (int) orcamento.getNumorc()).execute();

                            double valorServDouble = 0.0;

                            // 3. Tratar a resposta e extrair o ValorTotal
                            if (response.isSuccessful() && response.body() != null) {
                                valorServDouble = response.body().getValorTotal();
                                android.util.Log.d("FreteServicoDebug", "Sucesso! ValorTotal extraído do JSON: " + valorServDouble);
                                List<Integer> idsServico = response.body().getIdsSelecionados();
                                // Adiciona o log com os IDs
                                android.util.Log.d("FreteDebug", "IDs de serviço recuperados da API (Activity): " + idsServico);

                                int numOrc = (int) orcamento.getNumorc();
                                br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.put(numOrc, valorServDouble);
                                orcamento.setVlServicosAdicionais(valorServDouble);
                                if (idsServico != null) {
                                    br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.put(numOrc, idsServico);
                                }
                            } else {
                                android.util.Log.e("FreteServicoDebug", "Erro na API de frete. Código: " + response.code());
                            }

                            if (valorServDouble > 0) {
                                // O resto da sua lógica para armazenar em cache e atualizar a UI
                                int numOrc = (int) orcamento.getNumorc();
                                br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.put(numOrc, valorServDouble);
                                android.util.Log.d("FreteServicoDebug", "Serviço de frete armazenado em memória: " + valorServDouble);

                                // A sua lógica para atualizar a UI já está correta
                                txtFrete.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        atualizaTotais(orcamento.getItens());
                                    }
                                });
                            } else {
                                android.util.Log.d("FreteServicoDebug", "Nenhum serviço de frete retornado (ValorTotal = 0 ou erro na API)");
                            }
                        } catch (Exception ex) {
                            android.util.Log.e("FreteServicoDebug", "Exceção ao consultar FreteServico", ex);
                        }
                    }
                }.start();
            }

            if (orcamento.getNumorc() > 0) {
                txtNumOrc.setText("");
                btn_finalizar.setText("ALTERAR ORÇAMENTO");
            } else {
                txtNumOrc.setText("");
                btn_finalizar.setText("FINALIZAR ORÇAMENTO");
            }
            if (orcamento.getCliente() != null) {

                carregaDadosCliente(orcamento.getCliente());
                orcamentoAlteradoDao.inserir(dfInteiro.format(orcamento.getNumorc()), orcamento, false);
                orcamentoAlteradoDao.editarAlterado(dfInteiro.format(orcamento.getNumorc()), false);

            }


        }

        if (getIntent().getSerializableExtra("cliente") != null) {
            orcamento = new Orcamento();
            orcamento.setCliente((Cliente) getIntent().getSerializableExtra("cliente"));
            orcamento.setItens(new ArrayList<ItemOrcamento>());
            carregaDadosCliente(orcamento.getCliente());
            Intent intent = new Intent(Orcamento_LancamentoActivity.this, ConsultaActivity.class);
            startActivity(intent);
            ConsultaActivity.orcamento = orcamento;
            finish();

        }


        btn_adcionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orcamento == null) {
                    orcamento = new Orcamento();
                    orcamento.setItens(new ArrayList<ItemOrcamento>());
                    Intent intent = new Intent(Orcamento_LancamentoActivity.this, ConsultaActivity.class);
                    startActivity(intent);
                    ConsultaActivity.orcamento = orcamento;
                    finish();

                } else {
                    if (ConsultaActivity.orcamento == null || orcamento.getNumorc() > 0) {
                        orcamento.setItens(ItemOrcamentoAdapter.mList);
                        ConsultaActivity.orcamento = orcamento;
                        if(ProdutoFragments.class != null){
                            ProdutoFragments.orcamento = orcamento;
                        }
                        Intent intent = new Intent(Orcamento_LancamentoActivity.this, ConsultaActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        orcamento.setItens(ItemOrcamentoAdapter.mList);
                        ConsultaActivity.orcamento = orcamento;
                        if(ProdutoFragments.class != null){
                            ProdutoFragments.orcamento = orcamento;
                        }
                        finish();
                    }

                }
            }
        });


        mRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecycleView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ModalItens modalBottomSheet = new ModalItens();
                        modalBottomSheet.setAberturaPallet(mList.get(position).isAberturaPallet());
                        modalBottomSheet.setActionListener(Orcamento_LancamentoActivity.this);
                        modalBottomSheet.show(getSupportFragmentManager(), "modalMenu");
                        itemOrcamento = mList.get(position);
                        index = position;

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {


                    }
                })
        );

        cardtotais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alerta = new AlertDialog.Builder(Orcamento_LancamentoActivity.this);
                alerta.setIcon(R.drawable.ic_card_info);
                alerta.setCancelable(false);
                alerta.setTitle("Orçamento nº " + dfInteiro.format(orcamento.getNumorc()));
                alerta.setMessage(Html.fromHtml(resumoPedido));
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

        ConsultarAmbientes();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            int key = (orcamento != null) ? (int) orcamento.getNumorc() : 0;
            br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.remove(key);
            br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.remove(key);
            // Remove qualquer resíduo temporário
            br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.remove(0);
            br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.remove(0);
        } catch (Exception ignore) {}
    }

    public static void atualizaItem(ItemOrcamento item, int position) {

        adapter.updateListitem(item, position);


    }

    public static void aplicarOrcamentoSalvoCarregado(Orcamento orcamentoAtualizado) {
        if (orcamentoAtualizado == null) {
            return;
        }

        orcamento = orcamentoAtualizado;
        prepararPreservacaoValoresCarregados(orcamentoAtualizado);

        if (adapter != null) {
            adapter.replaceAllItemsPreservandoValoresCarregados(orcamentoAtualizado.getItens());
        }

        atualizaTotais(orcamentoAtualizado.getItens());
    }

    private boolean deveNormalizarAoAbrir(Orcamento orcamentoAtual) {
        if (orcamentoAtual == null || orcamentoAtual.getNumorc() <= 0) {
            return false;
        }

        try {
            DecimalFormat dfInteiro = new DecimalFormat("0");
            return !orcamentoAlteradoDao.orcamentoAlterado(dfInteiro.format(orcamentoAtual.getNumorc()), orcamentoAtual);
        } catch (Exception ignore) {
            return false;
        }
    }

    private void restaurarMarcadoresPromocionaisSalvos(Orcamento orcamentoAtual) {
        if (orcamentoAtual == null || orcamentoAtual.getNumorc() <= 0 || orcamentoAtual.getItens() == null || orcamentoAtual.getItens().isEmpty()) {
            return;
        }

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("orcamentoPromocaoPrazo", Context.MODE_PRIVATE);
        String jsonItens = preferences.getString("itens_" + (long) orcamentoAtual.getNumorc(), null);
        if (jsonItens == null || jsonItens.trim().isEmpty()) {
            return;
        }

        ItemOrcamento[] itensCache = new Gson().fromJson(jsonItens, ItemOrcamento[].class);
        if (itensCache == null || itensCache.length == 0) {
            return;
        }

        for (ItemOrcamento itemAtual : orcamentoAtual.getItens()) {
            ItemOrcamento itemCache = localizarItemCorrespondenteCache(itensCache, itemAtual);
            if (itemAtual == null || itemCache == null) {
                continue;
            }

            itemAtual.setIsMotor(itemCache.getIsMotor());
            itemAtual.setCodigoPromo(itemCache.getCodigoPromo());
            itemAtual.setNomePromo(itemCache.getNomePromo());
            itemAtual.setMensagemPromo(itemCache.getMensagemPromo());
            itemAtual.setVldescPromo(itemCache.getVldescPromo());
            itemAtual.setPrecoUnit(itemCache.getPrecoUnit());
            itemAtual.setVlDescItem(itemCache.getVlDescItem());
            itemAtual.setDpi(itemCache.getDpi());

            if ((itemAtual.getSituacao() == null || itemAtual.getSituacao().trim().isEmpty())
                    && itemCache.getSituacao() != null) {
                itemAtual.setSituacao(itemCache.getSituacao());
            }
        }
    }

    private ItemOrcamento localizarItemCorrespondenteCache(ItemOrcamento[] itensCache, ItemOrcamento itemAtual) {
        if (itensCache == null || itemAtual == null) {
            return null;
        }

        for (ItemOrcamento itemCache : itensCache) {
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

    private void aplicarOrcamentoAtualizadoPosPromocao(Orcamento orcamentoAtualizado) {
        if (orcamentoAtualizado == null) {
            return;
        }

        orcamento = orcamentoAtualizado;
        mList = orcamento.getItens() != null ? orcamento.getItens() : new ArrayList<>();
        adapter = new ItemOrcamentoAdapter(this, mList, isGerente);
        mRecycleView.setAdapter(adapter);
        atualizarComissoesItensSalvosAoEntrar();
        atualizaTotais(mList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizarComissoesItensSalvosAoEntrar();
    }

    private void atualizarComissoesItensSalvosAoEntrar() {
        if (orcamento == null || orcamento.getNumorc() <= 0 || orcamento.getItens() == null || orcamento.getItens().isEmpty()) {
            return;
        }

        final List<ItemOrcamento> itensOrcamento = orcamento.getItens();
        final String codFilPreco = obterCodFilPrecoOrcamento();

        new Thread(() -> {
            try {
                if (produtoHelper == null) {
                    produtoHelper = new ProdutoHelper();
                }

                List<Integer> codigos = new ArrayList<>();
                for (ItemOrcamento item : itensOrcamento) {
                    if (item != null && item.getCoditprod() > 0) {
                        codigos.add(item.getCoditprod());
                    }
                }

                HashMap<Integer, ProdutoGemco> produtosPorCodigo = produtoHelper.ConsultarComissoesProdutosPorCodigo(codigos, codFilPreco);
                boolean alterou = false;

                for (ItemOrcamento item : itensOrcamento) {
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
                Log.e("ComissaoItemDebug", "Falha ao atualizar comissao dos itens do orcamento salvo", e);
            }
        }).start();
    }

    private String obterCodFilPrecoOrcamento() {
        if (orcamento != null && orcamento.getCodfil() > 0) {
            return String.valueOf(orcamento.getCodfil());
        }

        if (usuarioLogado != null && usuarioLogado.get("codfil") != null && !usuarioLogado.get("codfil").trim().isEmpty()) {
            return usuarioLogado.get("codfil");
        }

        return "1";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_VERIFICA_PROMO_FIKON) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Serializable orcamentoAtualizado = data.getSerializableExtra(PopupVerificaPromoFikon.EXTRA_ORCAMENTO_RESULTADO);
                if (orcamentoAtualizado instanceof Orcamento) {
                    aplicarOrcamentoAtualizadoPosPromocao((Orcamento) orcamentoAtualizado);
                }
            }
            abrirPopupFechamentoOrcamento();
        }
    }

    public static void atualizaAmbientesProdutos(List<AmbienteProduto> listAmbientesProdutos, int position){
        adapter.updateAmbientesProdutos(listAmbientesProdutos, position);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.orcamento_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (ItemOrcamentoAdapter.mList != null && orcamento.getNumorc() == 0) {
                orcamento.setItens(ItemOrcamentoAdapter.mList);
                ConsultaActivity.orcamento = orcamento;
                ProdutoFragments.orcamento = orcamento;
                finish();
            } else {
                if (orcamento.getStatus() == null) {
                    finish();

                } else {
                    if (alteracao == false) {
                        orcamento.setItens(ItemOrcamentoAdapter.mList);
                        ConsultaActivity.orcamento = orcamento;
                        ProdutoFragments.orcamento = orcamento;
                        finish();
                    } else {
                        finish();
                    }

                }


            }
            return true;
        }

        /*if(id == R.id.menu_complemento){

            if(!txtNome.getText().equals("Informe o cliente") && orcamento.getCliente() != null) {
                Intent intent = new Intent(Orcamento_LancamentoActivity.this, PopupOrcCustomPF.class);
                startActivity(intent);
            }

        }*/


        if(id == R.id.menu_endereco){

            if(!txtNome.getText().equals("Informe o cliente") && orcamento.getCliente() != null) {
                Intent intent = new Intent(Orcamento_LancamentoActivity.this, PopupEnderecoEntrega.class);
                intent.putExtra("isOrcamento", true);
                intent.putExtra("endereco", (Serializable) orcamento.getCliente().getEnderecos());
                startActivity(intent);
            }

        }


        if (id == R.id.menu_compartilhar) {

            if(orcamentoAlteradoDao.orcamentoAlterado(dfInteiro.format(orcamento.getNumorc()),orcamento)){

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);

                builder.setTitle("Atenção :|");
                builder.setMessage("Orçamento foi alterado. Salve o orçamento antes de compartilhar.");
                builder
                        .setCancelable(true)
                        .setNeutralButton("Entendi", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }

                        });

                builder.create().show();
                return true;

            }

            if(ambientesAlterados){

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);

                builder.setTitle("Atenção :|");
                builder.setMessage("Ambientes foram alterados. Salve o orçamento antes de compartilhar.");
                builder
                        .setCancelable(true)
                        .setNeutralButton("Entendi", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }

                        });

                builder.create().show();
                return true;

            }

            /*if(helper.NecessarioCodigoEncomenda(orcamento)){

                String validacao = helper.ValidarCodigoEncomenda(orcamento, Orcamento_LancamentoActivity.this);
                if(!Objects.equals(validacao, "validado")){
                    return true;
                }

            }*/

            /*if(orcamento.getCliente() != null && orcamento.getCliente().getNatjur().equals("J")) {

                if (orcamento.getNumorc() > 0) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);

                    builder.setTitle("Atenção :|");
                    builder.setMessage("Como você deseja enviar o orçamento?");
                    builder
                            .setCancelable(true)
                            .setPositiveButton("Com preço", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    GerarPDFComPrecoPJ(orcamento);


                                }

                            });

                    builder.setNegativeButton("Sem preço", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            GerarPDFPJ(orcamento);
                        }
                    });


                    builder.create().show();
                    return true;

                } else {
                    Toast.makeText(Orcamento_LancamentoActivity.this, "Orçamento ainda não foi finalizado!", Toast.LENGTH_SHORT).show();
                }

            }else{*/

                if (orcamento.getNumorc() > 0 && orcamento.getCliente() != null && (usuarioLogado.get("isgerente").equals("SIM") || usuarioLogado.get("imprimir").equals("S"))) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);

                    builder.setTitle("Atenção :|");
                    builder.setMessage("Como você deseja enviar o orçamento?");
                    builder
                            .setCancelable(true)
                            .setPositiveButton("Com preço", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    GerarPDFComPreco(orcamento);


                                }

                            });

                    builder.setNegativeButton("Sem preço", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            GerarPDF(orcamento);
                        }
                    });


                    builder.create().show();
                    return true;

                } else {
                    if(usuarioLogado.get("isgerente").equals("NÄO") || usuarioLogado.get("imprimir").equals("N")){

                        Toast.makeText(Orcamento_LancamentoActivity.this, "Procurar a gerência, para gerar a impressão", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Orcamento_LancamentoActivity.this, "Orçamento ainda não foi finalizado!", Toast.LENGTH_SHORT).show();
                    }

                }

            }





        if (id == R.id.menu_integrar) {
            if (orcamento.getNumorc() > 0) {
                if (Integer.parseInt(usuarioLogado.get("codigo")) == 0) {
                    Toast.makeText(Orcamento_LancamentoActivity.this, "Você não pode integrar o orçamento!", Toast.LENGTH_SHORT).show();

                } else {

                    if(helper.NecessarioCodigoEncomenda(orcamento)){

                        String validacao = helper.ValidarCodigoEncomenda(orcamento, Orcamento_LancamentoActivity.this);
                        if(!Objects.equals(validacao, "validado")){
                            return true;
                        }

                    }

                    if (orcamento.getCliente().getCodcli() == 0) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);
                        builder.setTitle("Atenção :|");
                        builder.setMessage("Para integar o orçamento, você deve terminar o cadastro do cliente, e atualizar o orçamento");
                        builder
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(Orcamento_LancamentoActivity.this, ClientesActivity.class);
                                        intent.putExtra("cgccpf", orcamento.getCliente().getCgccpf());
                                        intent.putExtra("orcamento", true);
                                        intent.putExtra("alteracao", true);
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


                        if (orcamento.getNumorcGemco() == 0) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);
                            builder.setTitle("Atenção :|");
                            builder.setMessage("Você deseja integrar o orçamento ao sistema?");
                            builder
                                    .setCancelable(false)
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            IntegraOrcamento(dfInteiro.format(orcamento.getNumorc()));

                                        }

                                    });

                            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });


                            builder.create().show();

                        } else {

                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);
                            builder.setTitle("Atenção :|");
                            builder.setMessage("Já existe integração para esse orçamento, deseja integrar novamente?");
                            builder
                                    .setCancelable(false)
                                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            IntegraOrcamento(dfInteiro.format(orcamento.getNumorc()));

                                        }

                                    });

                            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });


                            builder.create().show();


                        }

                    }


                }

            } else {
                Toast.makeText(Orcamento_LancamentoActivity.this, "Orçamento ainda não foi finalizado!", Toast.LENGTH_SHORT).show();
            }


        }


        if(id == R.id.menu_contato){

            if(orcamento.getCliente() != null && orcamento.getCliente().getNatjur().equals("J") && orcamento.getCliente().getContatos() != null && orcamento.getCliente().getContatos().size() > 0 ){

                Intent intent = new Intent(Orcamento_LancamentoActivity.this, PopupAlterarContato.class);
                intent.putExtra("contatos", (Serializable) orcamento.getCliente().getContatos());
                startActivity(intent);

            }

        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onBackPressed() {
        if (ItemOrcamentoAdapter.mList != null && orcamento.getNumorc() == 0) {
            orcamento.setItens(ItemOrcamentoAdapter.mList);
            ConsultaActivity.orcamento = orcamento;
            ProdutoFragments.orcamento = orcamento;
            finish();
        } else {
            if (orcamento.getStatus() == null) {
                finish();

            } else {
                if (alteracao == false) {
                    orcamento.setItens(ItemOrcamentoAdapter.mList);
                    ConsultaActivity.orcamento = orcamento;
                    ProdutoFragments.orcamento = orcamento;
                    finish();
                } else {
                    finish();
                }

            }
        }
    }


    private void IntegraOrcamento(final String numorc) {

        dialog = new ProgressDialog(Orcamento_LancamentoActivity.this);
        dialog.setMessage("Integrando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    retorno = helper.IntegraOrcamento(numorc);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (!retorno.toString().equals("Orçamento não integrado!")) {
                                Toast.makeText(Orcamento_LancamentoActivity.this, "Orçamento integrado com o número: " + retorno, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(Orcamento_LancamentoActivity.this, retorno, Toast.LENGTH_SHORT).show();
                            }

                        }

                    });
                }

            }
        }.start();

    }



    private void GerarPDF(final Orcamento orcamento) {

        dialog = new ProgressDialog(Orcamento_LancamentoActivity.this);
        dialog.setMessage("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {
                Log.d("OrcamentoDebug", "[GerarPDF] Thread started. Orcamento numorc=" + orcamento.getNumorc() + " VlTotal=" + orcamento.getVlTotal() + " VlFrete=" + orcamento.getVlFrete() + " VlServicosAdicionais=" + orcamento.getVlServicosAdicionais());
                try {


                    final DecimalFormat dfInteiro = new DecimalFormat("0");

                    String[] cabecalho = {"Imagem", "Código Acal", "Produto", "Qtd"};


                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();


                    String cgccpf;
                    String fone;

                    TemplatePDFOrcamento templatePdf = new TemplatePDFOrcamento(Orcamento_LancamentoActivity.this);
                    templatePdf.openDocument(dfInteiro.format(orcamento.getNumorc()));
                    templatePdf.addMetaData("Orçamento", "Acal+", "Acal");

                    if (orcamento.getCliente().getCelular().equals("")) {
                        fone = "Contato:";
                    } else {
                        fone = "Contato: (" + orcamento.getCliente().getDddCelular() + ") " + orcamento.getCliente().getCelular();
                    }

                    boolean layoutFilial150 = templatePdf.isLayoutFilial150();
                    if (layoutFilial150) {
                        templatePdf.addCabecalhoFilial150(dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                    } else switch (orcamento.getCodfil()){
                        case 1 :
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0008-25", "Av. Tristão Gonçalves, 1074 Centro - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 3:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0005-82", "Av. Antonio Sales, 3210 Dionisio Torres - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 7 :
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0009-06", "Av. Washington Soares, 10008 Messejana - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 9:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0011-20", "Av. Godofredo Maciel, 767 Parangaba - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 11:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0012-01", "Rua Coronel Correia, 2273 Parque Soledade - Caucaia - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 15:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0013-92", "Av. Desembargador Moreira, 2211 Dionisio Torres - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 17:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0014-73", "Av. Padre Jose Holanda do Vale, 0 Luzardo Viana - Maracanau - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 19:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0015-54", "Av. Cearenses, 0 Presidente Kennedy - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 21:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0016-35", "Av. Eusebio de Queiroz, 2850 Centro - Eusébio - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        default:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0001-59", "Rua Padre Cicero, 400 Rodolfo Teofilo - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                    }

                    String[] dadosCliente150;
                    if (orcamento.getCliente().getNatjur().equals("F")) {
                        cgccpf = "Cpf: " + orcamento.getCliente().getCgccpf().substring(0, 3) + "." + orcamento.getCliente().getCgccpf().substring(3, 6) + "." + orcamento.getCliente().getCgccpf().substring(6, 9) + "-" + orcamento.getCliente().getCgccpf().substring(9, 11);
                        dadosCliente150 = new String[]{"Nome: " + orcamento.getCliente().getNomcli(), cgccpf,
                                "E-mail: " + orcamento.getCliente().getEmail(), fone};
                    } else {
                        cgccpf = "Cnpj: " + orcamento.getCliente().getCgccpf().substring(0, 2) + "." + orcamento.getCliente().getCgccpf().substring(2, 5) + "." + orcamento.getCliente().getCgccpf().substring(5, 8) + "/" + orcamento.getCliente().getCgccpf().substring(8, 12) + "-" + orcamento.getCliente().getCgccpf().substring(12, 14);
                        dadosCliente150 = new String[]{"Razão social: " + orcamento.getCliente().getNomcli(),
                                "Fantasia: " + orcamento.getCliente().getFantasia(), cgccpf,
                                "E-mail: " + orcamento.getCliente().getEmail(), fone};
                    }

                    final boolean[] valoresNegociaveis = {false};
                    if(usuarioLogado.get("codfil").equals("3")) {

                        CountDownLatch countDownLatch = new CountDownLatch(1);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                android.app.AlertDialog.Builder builderNegociaveis = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);

                                builderNegociaveis.setTitle("Valores Negociáveis");
                                builderNegociaveis.setMessage("Deseja adicionar a mensagem Valores Negociáveis?");

                                builderNegociaveis.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        valoresNegociaveis[0] = true;
                                        countDownLatch.countDown();
                                        dialog.dismiss();
                                    }
                                });

                                builderNegociaveis.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        valoresNegociaveis[0] = false;
                                        countDownLatch.countDown();
                                        dialog.dismiss();
                                    }
                                });

                                builderNegociaveis.create().show();

                            }
                        });

                        countDownLatch.await();

                    }

                    String[] dadosConsultorLegado = new String[]{
                            "Nome: " + orcamento.getConsultor(),
                            "Email: " + usuarioLogado.get("email"),
                            "Fone/WhatsApp: " + usuarioLogado.get("celular")
                    };

                    String[] financeiroFilial150 = new String[0];
                    String[] condicoesFilial150 = new String[0];
                    String[] totaisLegado = new String[0];
                    try {
                        Date dateval = entrada.parse(orcamento.getDtValidade());
                        // Recupera os serviços de frete do mapa estático em PopupOrcFechamento
                        double servicosFrete = PopupOrcFechamento.servicosFretePorOrcamento.getOrDefault((int)orcamento.getNumorc(), 0.0);
                        double freteComServicos = orcamento.getVlFrete() + servicosFrete;
                        boolean entrega = isEntregaOrcamento(orcamento, freteComServicos);
                        double valorItensLiquido = getValorItensLiquidoPdf(orcamento).doubleValue();
                        double valorDesconto = getValorDescontoPdf(orcamento).doubleValue();
                        double valorItensCabecalho = BigDecimal.valueOf(valorItensLiquido)
                                .add(BigDecimal.valueOf(valorDesconto))
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue();
                        financeiroFilial150 = new String[]{
                                "Valor dos itens: " + real.format(valorItensCabecalho),
                                "Valor do desconto: " + real.format(valorDesconto),
                                "Valor total dos itens: " + real.format(valorItensLiquido),
                                "Tipo de frete: " + (entrega ? "CIF" : "FOB"),
                                "Valor do frete: " + (entrega ? real.format(freteComServicos) : "N/A"),
                                "Valor total dos itens com frete: " + real.format(valorItensLiquido + freteComServicos)
                        };
                        condicoesFilial150 = new String[]{
                                orcamento.getCondPagamento() == null ? "" : "Condição de pagamento: " + orcamento.getCondPagamento().getDescricao(),
                                "Parcelamento: " + orcamento.getQtprc() + "x sem juros",
                                "Peso total: " + df.format(orcamento.getPeso()) + "kg",
                                "Validade da proposta: " + formato.format(dateval)
                        };
                        if (!layoutFilial150) {
                            totaisLegado = new String[]{
                                    "Valor dos itens: " + real.format(valorItensCabecalho),
                                    "Valor do desconto: " + real.format(valorDesconto),
                                    "Valor total dos itens: " + real.format(valorItensLiquido),
                                    "Valor do frete: " + real.format(freteComServicos),
                                    "Valor total dos itens com frete: " + real.format(valorItensLiquido + freteComServicos),
                                    "Peso Total: " + df.format(orcamento.getPeso()) + "kg",
                                    "Condição de pagamento:",
                                    orcamento.getCondPagamento() == null ? "" : orcamento.getCondPagamento().getDescricao(),
                                    "EM " + orcamento.getQtprc() + "X S/JUROS",
                                    "Orçamento válido até: " + formato.format(dateval)
                            };
                        }
                    } catch (Exception e) {

                    }

                    if (layoutFilial150) {
                        templatePdf.addResumoFilial150(
                                dadosCliente150,
                                new String[]{"Nome: Pedro Alves", "E-mail: pedroalves@acalhomecenter.com.br", "Fone/WhatsApp: (85) 98899-8470"},
                                new String[]{"Nome: " + orcamento.getConsultor(), "E-mail: " + usuarioLogado.get("email"), "Fone/WhatsApp: " + usuarioLogado.get("celular")},
                                null,
                                financeiroFilial150,
                                condicoesFilial150
                        );
                    } else {
                        templatePdf.addResumoLegado(dadosCliente150, dadosConsultorLegado, totaisLegado, valoresNegociaveis[0]);
                    }

                    HashMap<Long, ArrayList<String[]>> itensAmbiente = getItensSemPrecoAmbiente();
                    if (layoutFilial150) {
                        templatePdf.createTableFilial150(new String[]{"Imagem", "Código Acal", "Produto", "Qtd"}, getItensSemPreco(orcamento));
                    } else if(itensAmbiente!=null && itensAmbiente.size()>0){
                        templatePdf.createTableAmbientesSemPreco(cabecalho, itensAmbiente, mapTotaisAmbientes, listNomesAmbientes);
                    }
                    else{
                        templatePdf.createTableSemPreco(cabecalho, getItensSemPreco(orcamento));
                    }

                    if (layoutFilial150) {
                        templatePdf.addTermoFilial150();
                    } else {
                        templatePdf.addTermo();
                    }

                    templatePdf.closeDocument();

                    File pdfFile = templatePdf.getFile();
                    String urlAnexo = azurePDFOrcamentos.subirArquivoPDF(pdfFile);
                    if(urlAnexo!=null){
                        helper.EnviarAnexoMoskit(urlAnexo, dfInteiro.format(orcamento.getNumorc()), azurePDFOrcamentos.nomeArquivo);
                    }

                    templatePdf.viewPdf((Activity) Orcamento_LancamentoActivity.this);


                } catch (Exception e) {

                } finally {
                    dialog.dismiss();
                }

            }
        }.start();

    }

    private void GerarPDFComPreco(final Orcamento orcamento) {


        dialog = new ProgressDialog(Orcamento_LancamentoActivity.this);
        dialog.setMessage("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {
                Log.d("OrcamentoDebug", "[GerarPDFComPreco] Thread started. Orcamento numorc=" + orcamento.getNumorc() + " VlTotal=" + orcamento.getVlTotal() + " VlFrete=" + orcamento.getVlFrete() + " VlServicosAdicionais=" + orcamento.getVlServicosAdicionais());

                try {


                    final DecimalFormat dfInteiro = new DecimalFormat("0");

                    String[] cabecalho = {"Imagem", "Código Acal", "Produto", "Qtd", "Preço", "Preço Total"};


                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();

                    String cgccpf;
                    String fone;

                    TemplatePDFOrcamento templatePdf = new TemplatePDFOrcamento(Orcamento_LancamentoActivity.this);
                    templatePdf.openDocument(dfInteiro.format(orcamento.getNumorc()));
                    templatePdf.addMetaData("Orçamento", "Acal+", "Acal");

                    if (orcamento.getCliente().getCelular().equals("")) {
                        fone = "Contato:";
                    } else {
                        fone = "Contato: (" + orcamento.getCliente().getDddCelular() + ") " + orcamento.getCliente().getCelular();
                    }

                    boolean layoutFilial150 = templatePdf.isLayoutFilial150();
                    if (layoutFilial150) {
                        templatePdf.addCabecalhoFilial150(dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                    } else switch (orcamento.getCodfil()){
                        case 1 :
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0008-25", "Av. Tristão Gonçalves, 1074 Centro - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 3:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0005-82", "Av. Antonio Sales, 3210 Dionisio Torres - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 7 :
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0009-06", "Av. Washington Soares, 10008 Messejana - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 9:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0011-20", "Av. Godofredo Maciel, 767 Parangaba - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 11:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0012-01", "Rua Coronel Correia, 2273 Parque Soledade - Caucaia - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 15:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0013-92", "Av. Desembargador Moreira, 2211 Dionisio Torres - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 17:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0014-73", "Av. Padre Jose Holanda do Vale, 0 Luzardo Viana - Maracanau - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 19:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0015-54", "Av. Cearenses, 0 Presidente Kennedy - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 21:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0016-35", "Av. Eusebio de Queiroz, 2850 Centro - Eusébio - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        default:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0001-59", "Rua Padre Cicero, 400 Rodolfo Teofilo - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                    }

                    String[] dadosCliente150;
                    if (orcamento.getCliente().getNatjur().equals("F")) {
                        cgccpf = "Cpf: " + orcamento.getCliente().getCgccpf().substring(0, 3) + "." + orcamento.getCliente().getCgccpf().substring(3, 6) + "." + orcamento.getCliente().getCgccpf().substring(6, 9) + "-" + orcamento.getCliente().getCgccpf().substring(9, 11);
                        dadosCliente150 = new String[]{"Nome: " + orcamento.getCliente().getNomcli(), cgccpf,
                                "E-mail: " + orcamento.getCliente().getEmail(), fone};
                    } else {
                        cgccpf = "Cnpj: " + orcamento.getCliente().getCgccpf().substring(0, 2) + "." + orcamento.getCliente().getCgccpf().substring(2, 5) + "." + orcamento.getCliente().getCgccpf().substring(5, 8) + "/" + orcamento.getCliente().getCgccpf().substring(8, 12) + "-" + orcamento.getCliente().getCgccpf().substring(12, 14);
                        dadosCliente150 = new String[]{"Razão social: " + orcamento.getCliente().getNomcli(),
                                "Fantasia: " + orcamento.getCliente().getFantasia(), cgccpf,
                                "E-mail: " + orcamento.getCliente().getEmail(), fone};
                    }

                    final boolean[] valoresNegociaveis = {false};
                    if(usuarioLogado.get("codfil").equals("3")) {

                        CountDownLatch countDownLatch = new CountDownLatch(1);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                android.app.AlertDialog.Builder builderNegociaveis = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);

                                builderNegociaveis.setTitle("Valores Negociáveis");
                                builderNegociaveis.setMessage("Deseja adicionar a mensagem Valores Negociáveis?");

                                builderNegociaveis.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        valoresNegociaveis[0] = true;
                                        countDownLatch.countDown();
                                        dialog.dismiss();
                                    }
                                });

                                builderNegociaveis.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        valoresNegociaveis[0] = false;
                                        countDownLatch.countDown();
                                        dialog.dismiss();
                                    }
                                });

                                builderNegociaveis.create().show();

                            }
                        });

                        countDownLatch.await();

                    }

                    String[] dadosConsultorLegado = new String[]{
                            "Nome: " + orcamento.getConsultor(),
                            "Email: " + usuarioLogado.get("email"),
                            "Fone/WhatsApp: " + usuarioLogado.get("celular")
                    };

                    String[] financeiroFilial150 = new String[0];
                    String[] condicoesFilial150 = new String[0];
                    String[] totaisLegado = new String[0];
                    try {
                        Date dateval = entrada.parse(orcamento.getDtValidade());
                        // Recupera os serviços de frete do mapa estático em PopupOrcFechamento
                        double servicosFrete = PopupOrcFechamento.servicosFretePorOrcamento.getOrDefault((int)orcamento.getNumorc(), 0.0);
                        double freteComServicos = orcamento.getVlFrete() + servicosFrete;
                        boolean entrega = isEntregaOrcamento(orcamento, freteComServicos);
                        double valorItensLiquido = getValorItensLiquidoPdf(orcamento).doubleValue();
                        double valorDesconto = getValorDescontoPdf(orcamento).doubleValue();
                        double valorItensCabecalho = BigDecimal.valueOf(valorItensLiquido)
                                .add(BigDecimal.valueOf(valorDesconto))
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue();
                        financeiroFilial150 = new String[]{
                                "Valor dos itens: " + real.format(valorItensCabecalho),
                                "Valor do desconto: " + real.format(valorDesconto),
                                "Valor total dos itens: " + real.format(valorItensLiquido),
                                "Tipo de frete: " + (entrega ? "CIF" : "FOB"),
                                "Valor do frete: " + (entrega ? real.format(freteComServicos) : "N/A"),
                                "Valor total dos itens com frete: " + real.format(valorItensLiquido + freteComServicos)
                        };
                        condicoesFilial150 = new String[]{
                                orcamento.getCondPagamento() == null ? "" : "Condição de pagamento: " + orcamento.getCondPagamento().getDescricao(),
                                "Parcelamento: " + orcamento.getQtprc() + "x sem juros",
                                "Peso total: " + df.format(orcamento.getPeso()) + "kg",
                                "Validade da proposta: " + formato.format(dateval)
                        };
                        if (!layoutFilial150) {
                            totaisLegado = new String[]{
                                    "Valor dos itens: " + real.format(valorItensCabecalho),
                                    "Valor do desconto: " + real.format(valorDesconto),
                                    "Valor total dos itens: " + real.format(valorItensLiquido),
                                    "Valor do frete: " + real.format(freteComServicos),
                                    "Valor total dos itens com frete: " + real.format(valorItensLiquido + freteComServicos),
                                    "Peso Total: " + df.format(orcamento.getPeso()) + "kg",
                                    "Condição de pagamento:",
                                    orcamento.getCondPagamento() == null ? "" : orcamento.getCondPagamento().getDescricao(),
                                    "EM " + orcamento.getQtprc() + "X S/JUROS",
                                    "Orçamento válido até: " + formato.format(dateval)
                            };
                        }
                    } catch (Exception e) {

                    }

                    if (layoutFilial150) {
                        templatePdf.addResumoFilial150(
                                dadosCliente150,
                                new String[]{"Nome: Pedro Alves", "E-mail: pedroalves@acalhomecenter.com.br", "Fone/WhatsApp: (85) 98899-8470"},
                                new String[]{"Nome: " + orcamento.getConsultor(), "E-mail: " + usuarioLogado.get("email"), "Fone/WhatsApp: " + usuarioLogado.get("celular")},
                                null,
                                financeiroFilial150,
                                condicoesFilial150
                        );
                    } else {
                        templatePdf.addResumoLegado(dadosCliente150, dadosConsultorLegado, totaisLegado, valoresNegociaveis[0]);
                    }

                    HashMap<Long, ArrayList<String[]>> itensAmbiente = getItensComPrecoAmbiente();
                    if (layoutFilial150) {
                        templatePdf.createTableFilial150(new String[]{"Imagem", "Código Acal", "Produto", "Qtd", "Preço Unit.", "Preço Total"}, getItensComPreco(orcamento));
                    } else if(itensAmbiente!=null && itensAmbiente.size()>0){
                        templatePdf.createTableAmbientesComPreco(cabecalho, itensAmbiente, mapTotaisAmbientes, listNomesAmbientes);
                    }
                    else{
                        templatePdf.createTableComPreco(cabecalho, getItensComPreco(orcamento));
                    }

                    if (layoutFilial150) {
                        templatePdf.addTermoFilial150();
                    } else {
                        templatePdf.addTermo();
                    }

                    templatePdf.closeDocument();

                    File pdfFile = templatePdf.getFile();
                    String urlAnexo = azurePDFOrcamentos.subirArquivoPDF(pdfFile);
                    if(urlAnexo!=null){
                        helper.EnviarAnexoMoskit(urlAnexo, dfInteiro.format(orcamento.getNumorc()), azurePDFOrcamentos.nomeArquivo);
                    }

                    templatePdf.viewPdf((Activity) Orcamento_LancamentoActivity.this);


                } catch (Exception e) {

                } finally {
                    dialog.dismiss();
                }

            }
        }.start();

    }

    private boolean isEntregaOrcamento(Orcamento orcamento, double valorFrete) {
        return orcamento != null
                && (orcamento.getVlFreteSelecionado() > 0.0d || valorFrete > 0.0d);
    }

    private ArrayList<String[]> getItensSemPreco(Orcamento orcamento) {

        ArrayList<String[]> rows = new ArrayList<>();

        for (ItemOrcamento item : orcamento.getItens()) {
            rows.add(new String[]{
                item.getImagem(),
                String.valueOf(item.getCoditprod()),
                item.getDescricao(),
                df.format(item.getQtcomp()),
                String.valueOf(item.isAberturaPallet())
            });
        }

        return rows;

    }


    private ArrayList<String[]> getItensComPreco(Orcamento orcamento) {

        ArrayList<String[]> rows = new ArrayList<>();

        for (ItemOrcamento item : orcamento.getItens()) {
            rows.add(new String[]{
                item.getImagem(),
                String.valueOf(item.getCoditprod()),
                item.getDescricao(),
                df.format(item.getQtcomp()), real.format(getPrecoLiquidoUnitarioPdf(item).doubleValue()),
                real.format(getValorLiquidoPdf(item, item.getQtcomp()).doubleValue()),
                String.valueOf(item.isAberturaPallet())
            });
        }

        return rows;

    }


    private HashMap<Long, ArrayList<String[]>> getItensSemPrecoAmbiente(){

        HashMap<Long, ArrayList<String[]>> hashMap = new HashMap<>();
        HashMap<Long, Double> mapTotais = new HashMap<>();
        mapTotaisAmbientes = new HashMap<>();

        try{

            for(ItemOrcamento item: orcamento.getItens()){

                if(item!=null) {

                    double somatorioAmbientes = 0;

                    if (item.getListAmbientesProdutos() != null && item.getListAmbientesProdutos().size() > 0) {

                        for (AmbienteProduto ambienteProduto : item.getListAmbientesProdutos()) {

                            if (
                                item.getId() == ambienteProduto.getIdItemOrcamento()
                                && item.getNumorc() == ambienteProduto.getNumorc()
                                && item.getCoditprod() == ambienteProduto.getCoditprod()
                            ) {

                                long chave = ambienteProduto.getIdAmbiente();

                                if (!hashMap.containsKey(chave)) {
                                    hashMap.put(chave, new ArrayList<>());
                                    mapTotais.put(chave, 0.0);
                                }

                                somatorioAmbientes += ambienteProduto.getQuantidade();

                                hashMap.get(chave).add(
                                    new String[]{
                                        item.getImagem(),
                                        String.valueOf(item.getCoditprod()),
                                        item.getDescricao(),
                                        df.format(ambienteProduto.getQuantidade()),
                                        String.valueOf(item.isAberturaPallet())
                                    }
                                );

                                mapTotais.put(chave,
                                    mapTotais.get(chave) +
                                    getValorLiquidoPdf(item, ambienteProduto.getQuantidade()).doubleValue()
                                );

                            }

                        }

                    }

                    if (item.getQtcomp() > somatorioAmbientes) {

                        long chaveSemAmbiente = 0;

                        if (!hashMap.containsKey(chaveSemAmbiente)) {
                            hashMap.put(chaveSemAmbiente, new ArrayList<>());
                            mapTotais.put(chaveSemAmbiente, 0.0);
                        }

                        hashMap.get(chaveSemAmbiente).add(
                            new String[]{
                                item.getImagem(),
                                String.valueOf(item.getCoditprod()),
                                item.getDescricao(),
                                df.format(item.getQtcomp() - somatorioAmbientes),
                                String.valueOf(item.isAberturaPallet())
                            }
                        );

                        mapTotais.put(chaveSemAmbiente,
                            mapTotais.get(chaveSemAmbiente) +
                            getValorLiquidoPdf(item, item.getQtcomp() - somatorioAmbientes).doubleValue()
                        );

                    }

                }

            }

            if(mapTotais!=null && mapTotais.size()>0){
                for (Map.Entry<Long, Double> entry: mapTotais.entrySet()) {
                    mapTotaisAmbientes.put(entry.getKey(), real.format(entry.getValue()));
                }
            }

            if(hashMap.size()==1 && hashMap.containsKey(0L)){
                return new HashMap<>();
            }

            System.out.println("hashMap: " + new Gson().toJson(hashMap));

            return hashMap;

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;

    }

    private HashMap<Long, ArrayList<String[]>> getItensComPrecoAmbiente(){

        HashMap<Long, ArrayList<String[]>> hashMap = new HashMap<>();
        HashMap<Long, Double> mapTotais = new HashMap<>();
        mapTotaisAmbientes = new HashMap<>();

        try{

            for(ItemOrcamento item: orcamento.getItens()){

                if(item!=null) {

                    double somatorioAmbientes = 0;

                    if(item.getListAmbientesProdutos()!=null && item.getListAmbientesProdutos().size()>0) {

                        for (AmbienteProduto ambienteProduto : item.getListAmbientesProdutos()) {

                            long chave = ambienteProduto.getIdAmbiente();

                            if (!hashMap.containsKey(chave)) {
                                hashMap.put(chave, new ArrayList<>());
                                mapTotais.put(chave, 0.0);
                            }

                            if (
                                item.getId() == ambienteProduto.getIdItemOrcamento()
                                && item.getNumorc() == ambienteProduto.getNumorc()
                                && item.getCoditprod() == ambienteProduto.getCoditprod()
                            ) {

                                somatorioAmbientes += ambienteProduto.getQuantidade();

                                hashMap.get(chave).add(new String[]{
                                    item.getImagem(),
                                    String.valueOf(item.getCoditprod()),
                                    item.getDescricao(),
                                    df.format(ambienteProduto.getQuantidade()),
                                    real.format(getPrecoLiquidoUnitarioPdf(item).doubleValue()),
                                    real.format(getValorLiquidoPdf(item, ambienteProduto.getQuantidade()).doubleValue()),
                                    String.valueOf(item.isAberturaPallet())
                                });

                                mapTotais.put(chave,
                                    mapTotais.get(chave) +
                                    getValorLiquidoPdf(item, ambienteProduto.getQuantidade()).doubleValue()
                                );

                            }

                        }

                    }

                    if (item.getQtcomp() > somatorioAmbientes) {

                        long chaveSemAmbiente = 0;

                        if (!hashMap.containsKey(chaveSemAmbiente)) {
                            hashMap.put(chaveSemAmbiente, new ArrayList<>());
                            mapTotais.put(chaveSemAmbiente, 0.0);
                        }

                        hashMap.get(chaveSemAmbiente).add(new String[]{
                                item.getImagem(),
                                String.valueOf(item.getCoditprod()),
                                item.getDescricao(),
                                df.format(item.getQtcomp() - somatorioAmbientes),
                                real.format(getPrecoLiquidoUnitarioPdf(item).doubleValue()),
                                real.format(getValorLiquidoPdf(item, item.getQtcomp() - somatorioAmbientes).doubleValue()),
                                String.valueOf(item.isAberturaPallet())
                        });

                        mapTotais.put(chaveSemAmbiente,
                            mapTotais.get(chaveSemAmbiente) +
                            getValorLiquidoPdf(item, item.getQtcomp() - somatorioAmbientes).doubleValue()
                        );

                    }

                }

            }

            if(mapTotais!=null && mapTotais.size()>0){
                for (Map.Entry<Long, Double> entry: mapTotais.entrySet()) {
                    mapTotaisAmbientes.put(entry.getKey(), real.format(entry.getValue()));
                }
            }

            if(hashMap.size()==1 && hashMap.containsKey(0L)){
                return new HashMap<>();
            }

            System.out.println("hashMap: " + new Gson().toJson(hashMap));

            return hashMap;

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;

    }


    private void GerarPDFPJ(final Orcamento orcamento) {

        dialog = new ProgressDialog(Orcamento_LancamentoActivity.this);
        dialog.setMessage("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {
                Log.d("OrcamentoDebug", "[GerarPDFPJ] Thread started. Orcamento numorc=" + orcamento.getNumorc() + " VlTotal=" + orcamento.getVlTotal() + " VlFrete=" + orcamento.getVlFrete() + " VlServicosAdicionais=" + orcamento.getVlServicosAdicionais());
                try {


                    final DecimalFormat dfInteiro = new DecimalFormat("0");

                    String[] cabecalho = {"Imagem", "Código Acal", "Produto", "Qtd"};
                    String[] cabecalhoBanco = {"Agência", "Conta", "Banco",};

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();


                    String cgccpf;
                    String fone;

                    TemplatePDFOrcPJ templatePdf = new TemplatePDFOrcPJ(Orcamento_LancamentoActivity.this);
                    templatePdf.openDocument(dfInteiro.format(orcamento.getNumorc()));
                    templatePdf.addMetaData("Orçamento", "Acal+", "Acal");

                    if (orcamento.getCliente().getCelular().equals("")) {
                        fone = "Contato:";
                    } else {
                        fone = "Contato: (" + orcamento.getCliente().getDddCelular() + ") " + orcamento.getCliente().getCelular();
                    }

                    boolean layoutFilial150 = templatePdf.isLayoutFilial150();
                    if (layoutFilial150) {
                        templatePdf.addCabecalhoFilial150(dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                    } else switch (orcamento.getCodfil()){
                        case 1 :
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0008-25", "Av. Tristão Gonçalves, 1074 Centro - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 3:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0005-82", "Av. Antonio Sales, 3210 Dionisio Torres - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 7 :
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0009-06", "Av. Washington Soares, 10008 Messejana - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 9:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0011-20", "Av. Godofredo Maciel, 767 Parangaba - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 11:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0012-01", "Rua Coronel Correia, 2273 Parque Soledade - Caucaia - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 15:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0013-92", "Av. Desembargador Moreira, 2211 Dionisio Torres - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 17:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0014-73", "Av. Padre Jose Holanda do Vale, 0 Luzardo Viana - Maracanau - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 19:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0015-54", "Av. Cearenses, 0 Presidente Kennedy - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 21:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0016-35", "Av. Eusebio de Queiroz, 2850 Centro - Eusébio - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        default:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0001-59", "Rua Padre Cicero, 400 Rodolfo Teofilo - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                    }


                        cgccpf = "Cnpj: " + orcamento.getCliente().getCgccpf().substring(0, 2) + "." + orcamento.getCliente().getCgccpf().substring(2, 5) + "." + orcamento.getCliente().getCgccpf().substring(5, 8) + "/" + orcamento.getCliente().getCgccpf().substring(8, 12) + "-" + orcamento.getCliente().getCgccpf().substring(12, 14);
                    String[] dadosCliente150 = new String[]{"Razão social: " + orcamento.getCliente().getNomcli(),
                            "Fantasia: " + orcamento.getCliente().getFantasia(), cgccpf,
                            "E-mail: " + orcamento.getCliente().getEmail(), fone};


                    final boolean[] valoresNegociaveis = {false};
                    if(usuarioLogado.get("codfil").equals("3")) {

                        CountDownLatch countDownLatch = new CountDownLatch(1);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                android.app.AlertDialog.Builder builderNegociaveis = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);

                                builderNegociaveis.setTitle("Valores Negociáveis");
                                builderNegociaveis.setMessage("Deseja adicionar a mensagem Valores Negociáveis?");

                                builderNegociaveis.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        valoresNegociaveis[0] = true;
                                        countDownLatch.countDown();
                                        dialog.dismiss();
                                    }
                                });

                                builderNegociaveis.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        valoresNegociaveis[0] = false;
                                        countDownLatch.countDown();
                                        dialog.dismiss();
                                    }
                                });

                                builderNegociaveis.create().show();

                            }
                        });

                        countDownLatch.await();

                    }

                    String[] dadosConsultorLegado = new String[]{
                            "Nome: " + orcamento.getConsultor(),
                            "Email: " + usuarioLogado.get("email"),
                            "Fone/WhatsApp: " + usuarioLogado.get("celular")
                    };

                    String[] financeiroFilial150 = new String[0];
                    String[] condicoesFilial150 = new String[0];
                    String[] totaisLegado = new String[0];
                    try {
                        Date dateval = entrada.parse(orcamento.getDtValidade());
                        // Recupera os serviços de frete do mapa estático em PopupOrcFechamento
                        double servicosFrete = PopupOrcFechamento.servicosFretePorOrcamento.getOrDefault((int)orcamento.getNumorc(), 0.0);
                        double freteComServicos = orcamento.getVlFrete() + servicosFrete;
                        boolean entrega = isEntregaOrcamento(orcamento, freteComServicos);
                        double valorItensLiquido = getValorItensLiquidoPdf(orcamento).doubleValue();
                        double valorItens = BigDecimal.valueOf(valorItensLiquido)
                                .add(BigDecimal.valueOf(getValorDescontoPdf(orcamento).doubleValue()))
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue();
                        financeiroFilial150 = new String[]{
                                "Valor dos itens: " + real.format(valorItens),
                                "Valor do desconto: " + real.format(getValorDescontoPdf(orcamento).doubleValue()),
                                "Valor total dos itens: " + real.format(valorItensLiquido),
                                "Tipo de frete: " + (entrega ? "CIF" : "FOB"),
                                "Valor do frete: " + (entrega ? real.format(freteComServicos) : "N/A"),
                                "Valor total dos itens com frete: " + real.format(valorItensLiquido + freteComServicos)
                        };
                        condicoesFilial150 = new String[]{
                                orcamento.getCondPagamento() == null ? "" : "Condição de pagamento: " + orcamento.getCondPagamento().getDescricao(),
                                "Parcelamento: " + orcamento.getQtprc() + "x sem juros",
                                "Peso total: " + df.format(orcamento.getPeso()) + "kg",
                                "Validade da proposta: " + formato.format(dateval)
                        };
                        if (!layoutFilial150) {
                            totaisLegado = new String[]{
                                    "Valor dos itens: " + real.format(valorItens),
                                    "Valor do desconto: " + real.format(getValorDescontoPdf(orcamento).doubleValue()),
                                    "Valor total dos itens: " + real.format(valorItensLiquido),
                                    "+ Valor do frete: " + real.format(freteComServicos),
                                    "Valor total dos itens com frete: " + real.format(valorItensLiquido + freteComServicos),
                                    "Peso total: " + df.format(orcamento.getPeso()) + "kg",
                                    "Condição de pagamento:",
                                    orcamento.getCondPagamento() == null ? "" : orcamento.getCondPagamento().getDescricao(),
                                    "EM " + orcamento.getQtprc() + "X S/JUROS",
                                    "Orçamento válido até: " + formato.format(dateval)
                            };
                        }
                    } catch (Exception e) {

                    }

                    if (layoutFilial150) {
                        templatePdf.addResumoFilial150(
                                dadosCliente150,
                                new String[]{"Nome: Pedro Alves", "E-mail: pedroalves@acalhomecenter.com.br", "Fone/WhatsApp: (85) 98899-8470"},
                                new String[]{"Nome: " + orcamento.getConsultor(), "E-mail: " + usuarioLogado.get("email"), "Fone/WhatsApp: " + usuarioLogado.get("celular")},
                                null,
                                financeiroFilial150,
                                condicoesFilial150
                        );
                    } else {
                        templatePdf.addResumoLegado(dadosCliente150, dadosConsultorLegado, totaisLegado, valoresNegociaveis[0]);
                    }

                    HashMap<Long, ArrayList<String[]>> itensAmbientes = getItensSemPrecoAmbiente();
                    if (layoutFilial150) {
                        templatePdf.createTableFilial150(new String[]{"Imagem", "Código Acal", "Produto", "Qtd"}, getItensSemPrecoPJ(orcamento));
                    } else if(itensAmbientes!=null && itensAmbientes.size()>0){
                        templatePdf.createTableAmbientesSemPreco(cabecalho, itensAmbientes, mapTotaisAmbientes, listNomesAmbientes);
                    }
                    else{
                        templatePdf.createTableSemPreco(cabecalho, getItensSemPrecoPJ(orcamento));
                    }


                    if (!layoutFilial150) {
                        templatePdf.addParagraph("Contas - Araujo Cabral & Alves Ltda");

                        List<DadosBancarios> list = new ArrayList<>();

                        list = helper.GetDadosBancarios();

                        templatePdf.createTableBancos(cabecalhoBanco,getItensBancos(list));
                        templatePdf.addTermo();
                    } else {
                        templatePdf.addTermoFilial150();
                    }

                    templatePdf.closeDocument();

                    File pdfFile = templatePdf.getFile();
                    String urlAnexo = azurePDFOrcamentos.subirArquivoPDF(pdfFile);
                    if(urlAnexo!=null){
                        helper.EnviarAnexoMoskit(urlAnexo, dfInteiro.format(orcamento.getNumorc()), azurePDFOrcamentos.nomeArquivo);
                    }

                    templatePdf.viewPdf((Activity) Orcamento_LancamentoActivity.this);


                } catch (Exception e) {

                } finally {
                    dialog.dismiss();
                }

            }
        }.start();

    }

    private void GerarPDFComPrecoPJ(final Orcamento orcamento) {


        dialog = new ProgressDialog(Orcamento_LancamentoActivity.this);
        dialog.setMessage("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {
                Log.d("OrcamentoDebug", "[GerarPDFComPrecoPJ] Thread started. Orcamento numorc=" + orcamento.getNumorc() + " VlTotal=" + orcamento.getVlTotal() + " VlFrete=" + orcamento.getVlFrete() + " VlServicosAdicionais=" + orcamento.getVlServicosAdicionais());

                try {


                    final DecimalFormat dfInteiro = new DecimalFormat("0");

                    String[] cabecalho = {"Imagem", "Código Acal", "Produto", "Qtd", "Preço", "Preço Total"};
                    String[] cabecalhoBanco = {"Agência", "Conta", "Banco",};

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();

                    String cgccpf;
                    String fone;

                    TemplatePDFOrcPJ templatePdf = new TemplatePDFOrcPJ(Orcamento_LancamentoActivity.this);
                    templatePdf.openDocument(dfInteiro.format(orcamento.getNumorc()));
                    templatePdf.addMetaData("Orçamento", "Acal+", "Acal");

                    if (orcamento.getCliente().getCelular().equals("")) {
                        fone = "Contato:";
                    } else {
                        fone = "Contato: (" + orcamento.getCliente().getDddCelular() + ") " + orcamento.getCliente().getCelular();
                    }

                    boolean layoutFilial150 = templatePdf.isLayoutFilial150();
                    if (layoutFilial150) {
                        templatePdf.addCabecalhoFilial150(dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                    } else switch (orcamento.getCodfil()){
                        case 1 :
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0008-25", "Av. Tristão Gonçalves, 1074 Centro - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 3:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0005-82", "Av. Antonio Sales, 3210 Dionisio Torres - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 7 :
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0009-06", "Av. Washington Soares, 10008 Messejana - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 9:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0011-20", "Av. Godofredo Maciel, 767 Parangaba - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 11:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0012-01", "Rua Coronel Correia, 2273 Parque Soledade - Caucaia - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 15:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0013-92", "Av. Desembargador Moreira, 2211 Dionisio Torres - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 17:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0014-73", "Av. Padre Jose Holanda do Vale, 0 Luzardo Viana - Maracanau - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 19:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0015-54", "Av. Cearenses, 0 Presidente Kennedy - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                        case 21:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0016-35", "Av. Eusebio de Queiroz, 2850 Centro - Eusébio - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;

                        default:
                            templatePdf.addTitulo("ACAL HOME CENTER" , "CNPJ: 07.201.916/0001-59", "Rua Padre Cicero, 400 Rodolfo Teofilo - Fortaleza - CE","Orçamento número: " + dfInteiro.format(orcamento.getNumorc()), dateFormat.format(date));
                            break;
                    }

                        cgccpf = "Cnpj: " + orcamento.getCliente().getCgccpf().substring(0, 2) + "." + orcamento.getCliente().getCgccpf().substring(2, 5) + "." + orcamento.getCliente().getCgccpf().substring(5, 8) + "/" + orcamento.getCliente().getCgccpf().substring(8, 12) + "-" + orcamento.getCliente().getCgccpf().substring(12, 14);
                    String[] dadosCliente150 = new String[]{"Razão social: " + orcamento.getCliente().getNomcli(),
                            "Fantasia: " + orcamento.getCliente().getFantasia(), cgccpf,
                            "E-mail: " + orcamento.getCliente().getEmail(), fone};

                    final boolean[] valoresNegociaveis = {false};
                    if(usuarioLogado.get("codfil").equals("3")) {

                        CountDownLatch countDownLatch = new CountDownLatch(1);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                android.app.AlertDialog.Builder builderNegociaveis = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);

                                builderNegociaveis.setTitle("Valores Negociáveis");
                                builderNegociaveis.setMessage("Deseja adicionar a mensagem Valores Negociáveis?");

                                builderNegociaveis.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        valoresNegociaveis[0] = true;
                                        countDownLatch.countDown();
                                        dialog.dismiss();
                                    }
                                });

                                builderNegociaveis.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        valoresNegociaveis[0] = false;
                                        countDownLatch.countDown();
                                        dialog.dismiss();
                                    }
                                });

                                builderNegociaveis.create().show();

                            }
                        });

                        countDownLatch.await();

                    }

                    String[] dadosConsultorLegado = new String[]{
                            "Nome: " + orcamento.getConsultor(),
                            "Email: " + usuarioLogado.get("email"),
                            "Fone/WhatsApp: " + usuarioLogado.get("celular")
                    };

                    String[] financeiroFilial150 = new String[0];
                    String[] condicoesFilial150 = new String[0];
                    String[] totaisLegado = new String[0];
                    try {
                        Date dateval = entrada.parse(orcamento.getDtValidade());
                        // Recupera os serviços de frete do mapa estático em PopupOrcFechamento
                        double servicosFrete = PopupOrcFechamento.servicosFretePorOrcamento.getOrDefault((int)orcamento.getNumorc(), 0.0);
                        double freteComServicos = orcamento.getVlFrete() + servicosFrete;
                        boolean entrega = isEntregaOrcamento(orcamento, freteComServicos);
                        double valorItensLiquido = getValorItensLiquidoPdf(orcamento).doubleValue();
                        double valorItens = BigDecimal.valueOf(valorItensLiquido)
                                .add(BigDecimal.valueOf(getValorDescontoPdf(orcamento).doubleValue()))
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue();
                        financeiroFilial150 = new String[]{
                                "Valor dos itens: " + real.format(valorItens),
                                "Valor do desconto: " + real.format(getValorDescontoPdf(orcamento).doubleValue()),
                                "Valor total dos itens: " + real.format(valorItensLiquido),
                                "Tipo de frete: " + (entrega ? "CIF" : "FOB"),
                                "Valor do frete: " + (entrega ? real.format(freteComServicos) : "N/A"),
                                "Valor total dos itens com frete: " + real.format(valorItensLiquido + freteComServicos)
                        };
                        condicoesFilial150 = new String[]{
                                orcamento.getCondPagamento() == null ? "" : "Condição de pagamento: " + orcamento.getCondPagamento().getDescricao(),
                                "Parcelamento: " + orcamento.getQtprc() + "x sem juros",
                                "Peso total: " + df.format(orcamento.getPeso()) + "kg",
                                "Validade da proposta: " + formato.format(dateval)
                        };
                        if (!layoutFilial150) {
                            totaisLegado = new String[]{
                                    "Valor dos itens: " + real.format(valorItens),
                                    "Valor do desconto: " + real.format(getValorDescontoPdf(orcamento).doubleValue()),
                                    "Valor total dos itens: " + real.format(valorItensLiquido),
                                    "+ Valor do frete: " + real.format(freteComServicos),
                                    "Valor total dos itens com frete: " + real.format(valorItensLiquido + freteComServicos),
                                    "Peso total: " + df.format(orcamento.getPeso()) + "kg",
                                    "Condição de pagamento:",
                                    orcamento.getCondPagamento() == null ? "" : orcamento.getCondPagamento().getDescricao(),
                                    "EM " + orcamento.getQtprc() + "X S/JUROS",
                                    "Orçamento válido até: " + formato.format(dateval)
                            };
                        }

                    } catch (Exception e) {

                    }

                    if (layoutFilial150) {
                        templatePdf.addResumoFilial150(
                                dadosCliente150,
                                new String[]{"Nome: Pedro Alves", "E-mail: pedroalves@acalhomecenter.com.br", "Fone/WhatsApp: (85) 98899-8470"},
                                new String[]{"Nome: " + orcamento.getConsultor(), "E-mail: " + usuarioLogado.get("email"), "Fone/WhatsApp: " + usuarioLogado.get("celular")},
                                null,
                                financeiroFilial150,
                                condicoesFilial150
                        );
                    } else {
                        templatePdf.addResumoLegado(dadosCliente150, dadosConsultorLegado, totaisLegado, valoresNegociaveis[0]);
                    }

                    HashMap<Long, ArrayList<String[]>> itensAmbiente = getItensComPrecoAmbiente();
                    if (layoutFilial150) {
                        templatePdf.createTableFilial150(new String[]{"Imagem", "Código Acal", "Produto", "Qtd", "Preço Unit.", "Preço Total"}, getItensComPreco(orcamento));
                    } else if(itensAmbiente!=null && itensAmbiente.size()>0){
                        templatePdf.createTableAmbientesComPreco(cabecalho, itensAmbiente, mapTotaisAmbientes, listNomesAmbientes);
                    }
                    else{
                        templatePdf.createTableComPreco(cabecalho, getItensComPreco(orcamento));
                    }

                    if (!layoutFilial150) {
                        templatePdf.addParagraph("Contas - Araujo Cabral & Alves Ltda");

                        List<DadosBancarios> list = new ArrayList<>();

                        list = helper.GetDadosBancarios();

                        templatePdf.createTableBancos(cabecalhoBanco,getItensBancos(list));
                        templatePdf.addTermo();
                    } else {
                        templatePdf.addTermoFilial150();
                    }

                    templatePdf.closeDocument();

                    File pdfFile = templatePdf.getFile();
                    String urlAnexo = azurePDFOrcamentos.subirArquivoPDF(pdfFile);
                    if(urlAnexo!=null){
                        helper.EnviarAnexoMoskit(urlAnexo, dfInteiro.format(orcamento.getNumorc()), azurePDFOrcamentos.nomeArquivo);
                    }

                    templatePdf.viewPdf((Activity) Orcamento_LancamentoActivity.this);


                } catch (Exception e) {

                } finally {
                    dialog.dismiss();
                }

            }
        }.start();

    }

    private ArrayList<String[]> getItensSemPrecoPJ(Orcamento orcamento) {

        ArrayList<String[]> rows = new ArrayList<>();

        for (ItemOrcamento item : orcamento.getItens()) {
            rows.add(new String[]{
                item.getImagem(),
                String.valueOf(item.getCoditprod()),
                item.getDescricao(),
                df.format(item.getQtcomp()),
                String.valueOf(item.isAberturaPallet())
            });
        }

        return rows;

    }


    private ArrayList<String[]> getItensComPrecoPJ(Orcamento orcamento) {

        ArrayList<String[]> rows = new ArrayList<>();

        for (ItemOrcamento item : orcamento.getItens()) {
            rows.add(new String[]{
                item.getImagem(),
                String.valueOf(item.getCoditprod()),
                item.getDescricao(),
                df.format(item.getQtcomp()),
                real.format(getPrecoLiquidoUnitarioPdf(item).doubleValue()),
                real.format(getValorLiquidoPdf(item, item.getQtcomp()).doubleValue()),
                String.valueOf(item.isAberturaPallet())
            });
        }

        return rows;

    }



    private ArrayList<String[]> getItensBancos(List<DadosBancarios> bancos) {

        ArrayList<String[]> rows = new ArrayList<>();

        for (DadosBancarios item : bancos) {
            rows.add(new String[]{item.getAgencia(), item.getConta(), item.getBanco()});
        }

        return rows;

    }



    @Override
    public void onButtonClick(int id) {

        switch (id) {


            case R.id.mod_item_editar:
                Intent intent = new Intent(Orcamento_LancamentoActivity.this, PopupItemOrc.class);
                intent.putExtra("item", itemOrcamento);
                intent.putExtra("position", index);
                startActivity(intent);
                break;


            case R.id.mod_item_editarTodos:
                Intent itodos = new Intent(Orcamento_LancamentoActivity.this, PopupItemOrcTodos.class);
                itodos.putExtra("orcamento", orcamento);
                startActivity(itodos);
                break;

            case R.id.mod_item_estoque:
                Intent i = new Intent(Orcamento_LancamentoActivity.this, ConsultaEstoqueActivity.class);
                i.putExtra("codigo", String.valueOf(itemOrcamento.getCoditprod()));
                startActivity(i);
                break;


            case R.id.mod_item_pesquisa:

                String resultado = "";
                NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

                DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");


                Date date = null;
                if(itemOrcamento.getPrecoConc() != null && itemOrcamento.getPrecoConc().size()>0 && usuarioLogado.get("isgerente").equals("SIM")){



                    for (PrecoConc p : itemOrcamento.getPrecoConc()) {





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






                    AlertDialog.Builder alerta = new AlertDialog.Builder(Orcamento_LancamentoActivity.this);
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
                    Toast.makeText(Orcamento_LancamentoActivity.this, "Não existe pesquisa de preço para esse produto!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ModalOrcamentoItem_SwitchAberturaPallet:
                itemOrcamento.setAberturaPallet(aberturaPallet);
                break;

            case R.id.mod_item_excluir:
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Orcamento_LancamentoActivity.this);

                builder.setTitle("Atenção :|");
                builder.setMessage("Deseja retirar esse item do orçamento?");
                builder
                        .setCancelable(true)
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {

                                adapter.updateAmbientesProdutos(new ArrayList<>(), index);

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

    private void consultaPrecoConc(final String codigo) {

        dialog = new ProgressDialog(Orcamento_LancamentoActivity.this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                    mListPreco = produtoHelper.consultaPrecoConc(codigo);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {



                        }

                    });
                }

            }
        }.start();

    }

    private void abrirPopupFechamentoOrcamento() {
        Orcamento orcamentoVistaBase = criarSnapshotOrcamentoBaseParaPopup();
        Orcamento orcamentoAPrazo = new Gson().fromJson(new Gson().toJson(orcamentoVistaBase), Orcamento.class);
        Intent intent = new Intent(Orcamento_LancamentoActivity.this, PopupOrcFechamento.class);
        intent.putExtra("orcamento", orcamentoVistaBase);
        intent.putExtra("orcamentoAPrazo", orcamentoAPrazo);
        startActivity(intent);
    }

    private Orcamento criarSnapshotOrcamentoBaseParaPopup() {
        Orcamento snapshot = new Gson().fromJson(new Gson().toJson(orcamento), Orcamento.class);
        if (snapshot == null || snapshot.getItens() == null) {
            return snapshot;
        }

        for (ItemOrcamento item : snapshot.getItens()) {
            if (item == null) {
                continue;
            }

            if (isMotorPromocional(item.getIsMotor()) && item.getVldescPromo() > 0d) {
                item.setVlDescItem(item.getVldescPromo());
                if (item.getPrecoUnit() > 0d) {
                    item.setDpi(
                            BigDecimal.valueOf(item.getVldescPromo())
                                    .divide(BigDecimal.valueOf(item.getPrecoUnit()), 6, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100d))
                                    .setScale(6, RoundingMode.HALF_UP)
                                    .doubleValue()
                    );
                } else {
                    item.setDpi(0d);
                }
            }
        }

        return snapshot;
    }

    private void abrirPopupVerificaPromoFikon() {
        Orcamento orcamentoParaPromocao = new Gson().fromJson(new Gson().toJson(orcamento), Orcamento.class);
        Intent intent = new Intent(Orcamento_LancamentoActivity.this, PopupVerificaPromoFikon.class);
        intent.putExtra("orcamento", orcamentoParaPromocao);
        startActivityForResult(intent, REQUEST_VERIFICA_PROMO_FIKON);
    }

    private void ConsultarAmbientes(){

        dialog = new ProgressDialog(Orcamento_LancamentoActivity.this);
        dialog.setMessage("Consultando Ambientes...");
        dialog.setCancelable(false);
        dialog.show();

        try{

            CountDownLatch count = new CountDownLatch(1);

            new Thread(new Runnable() {

                @Override
                public void run() {

                    try {

                        if(produtoHelper==null){
                            produtoHelper = new ProdutoHelper();
                        }

                        listNomesAmbientes = produtoHelper.ConsultarNomesAmbientes();

                        ambientesAlterados = false;
                        if(orcamento.getNumorc()>0){
                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("ambientesAlterados", Context.MODE_PRIVATE);
                            String chave = String.valueOf((long)orcamento.getNumorc());
                            if(preferences.contains(chave)){
                                ambientesAlterados = preferences.getBoolean(chave, false);
                            }
                            else {
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putBoolean(chave, false);
                                editor.apply();
                            }
                        }

                        System.out.println(new Gson().toJson(listNomesAmbientes));
                        System.out.println(new Gson().toJson(orcamento.getItens()));

                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }

                    dialog.dismiss();
                    count.countDown();

                }

            }).start();

            count.await();

        }
        catch (Exception ex2){
            ex2.printStackTrace();
        }

    }

    public static void CriarResumoPedido(){

        String resultado = "";
        String motor="";
        String valordositens="";
        String valordosdesconto="";
        String valordofrete="";
        String totaldositens="";
        String totalcomfrete="";
        String pesoTotal="";
        String percdesc="";
        String vencimento="";
        String condicao="";

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DecimalFormat df = new DecimalFormat("0.00");
        DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");


        if(orcamento.getNumorc()>0) {
            double freteTotalResumo = getFreteComServicosResumo();
            double totalItensLiquidoResumo = getValorItensLiquidoPdf(orcamento).doubleValue();
            double valorDescontoResumo = getValorDescontoPdf(orcamento).doubleValue();
            double valorItensCabecalhoResumo = BigDecimal.valueOf(totalItensLiquidoResumo)
                    .add(BigDecimal.valueOf(valorDescontoResumo))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();


            if (orcamento.getVlFrete() > 0 || orcamento.getVlServicosAdicionais() > 0) {

                // valordositens será calculado após definirmos freteTotal
                valordosdesconto = "Valor do desconto: " + real.format(valorDescontoResumo);
                percdesc = "% do desconto: " + df.format((valorDescontoResumo / (totalItensLiquidoResumo + valorDescontoResumo)) * 100) + "%";
                valordositens = "Valor dos itens: " + real.format(valorItensCabecalhoResumo);
                valordofrete = "Valor do frete: " + real.format(freteTotalResumo);
                totaldositens = "Valor total dos itens: " + real.format(totalItensLiquidoResumo);
                totalcomfrete = "Valor total com frete: " + real.format(totalItensLiquidoResumo + freteTotalResumo);
                pesoTotal = "Peso total: " + df.format(orcamento.getPeso()) + "kg";
                try {
                    Date dateval = entrada.parse(orcamento.getDtValidade());
                    vencimento = "Orçamento válido até: " + formato.format(dateval);
                } catch (Exception e) {
                    vencimento = "";
                }
                condicao = orcamento.getCondPagamento().getDescricao() + " EM " + orcamento.getQtprc() + "X S/JUROS";

                resultado = resultado + ("<b>" + valordositens + "<br />" +
                        "<b>" + valordosdesconto + " <br />" +
                        "<b>" + percdesc + " <br />" +
                        "<b>" + totaldositens + " <br />" +
                        "<b>" + valordofrete + " <br />" +
                        "<b>" + totalcomfrete + " <br />" +
                        "<b>" + pesoTotal + " <br />" +
                        "<b>" + vencimento + " <br />" +
                        "<br />" +
                        condicao + "" + "<br />" +
                        "<br />"
                );

            }

            if (orcamento.getVlFrete() == 0 && orcamento.getVlServicosAdicionais() == 0) {

                valordositens = "Valor dos itens: " + real.format(valorItensCabecalhoResumo);
                valordosdesconto = "Valor do desconto: " + real.format(valorDescontoResumo);
                percdesc = "% do desconto: " + df.format((valorDescontoResumo / (totalItensLiquidoResumo + valorDescontoResumo)) * 100) + "%";
                valordofrete = "Valor do frete: " + real.format(0.0);
                totaldositens = "Valor total dos itens: " + real.format(totalItensLiquidoResumo);
                totalcomfrete = "Valor total com frete: " + real.format(totalItensLiquidoResumo);
                pesoTotal = "Peso total: " + df.format(orcamento.getPeso()) + "kg";
                try {
                    Date dateval = entrada.parse(orcamento.getDtValidade());
                    vencimento = "Orçamento válido até: " + formato.format(dateval);
                } catch (Exception e) {
                    vencimento = "";
                }
                condicao = orcamento.getCondPagamento().getDescricao() + " EM " + orcamento.getQtprc() + "X S/JUROS";

                resultado = resultado + ("<b>" + valordositens + "<br />" +
                        "<b>" + valordosdesconto + " <br />" +
                        "<b>" + percdesc + " <br />" +
                        "<b>" + totaldositens + " <br />" +
                        "<b>" + valordofrete + " <br />" +
                        "<b>" + pesoTotal + " <br />" +
                        "<b>" + vencimento + " <br />" +
                        "<br />" +
                        condicao + "" + "<br />" +
                        "<br />"
                );

            }

        }

        resumoPedido = resultado;

    }


    private static double getPrecoUnitarioBrutoExibicao(ItemOrcamento item) {
        if (item == null) {
            return 0.0d;
        }
        return item.getPrecoUnit();
    }

    private static double getValorLiquidoExibicao(ItemOrcamento item) {
        return getValorLiquidoExibicao(item, item != null ? item.getQtcomp() : 0.0d, orcamento);
    }

    private static double getPrecoLiquidoUnitarioExibicao(ItemOrcamento item, Orcamento orcamentoRef) {
        if (item == null) {
            return 0.0d;
        }
        return item.getPrecoUnit() - item.getVlDescItem();
    }

    private static double getValorLiquidoExibicao(ItemOrcamento item, Orcamento orcamentoRef) {
        return getValorLiquidoExibicao(item, item.getQtcomp(), orcamentoRef);
    }

    private static double getValorLiquidoExibicao(ItemOrcamento item, double quantidade) {
        return getValorLiquidoExibicao(item, quantidade, orcamento);
    }

    private static double getValorLiquidoExibicao(ItemOrcamento item, double quantidade, Orcamento orcamentoRef) {
        if (item == null) {
            return 0.0d;
        }
        return (item.getPrecoUnit() - item.getVlDescItem()) * quantidade;
    }

    private static BigDecimal getPrecoLiquidoUnitarioPdf(ItemOrcamento item, Orcamento orcamentoRef) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }

        return BigDecimal.valueOf(getPrecoLiquidoUnitarioExibicao(item, orcamentoRef))
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal getPrecoLiquidoUnitarioPdf(ItemOrcamento item) {
        return getPrecoLiquidoUnitarioPdf(item, orcamento);
    }

    private static BigDecimal getValorLiquidoPdf(ItemOrcamento item, double quantidade, Orcamento orcamentoRef) {
        // Para os totais do PDF, arredondamos apenas no valor final do item.
        // Isso mantém o total alinhado com o orçamento salvo em casos de promoção a prazo.
        return BigDecimal.valueOf(getValorLiquidoExibicao(item, quantidade, orcamentoRef))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal getValorLiquidoPdf(ItemOrcamento item, double quantidade) {
        return getValorLiquidoPdf(item, quantidade, orcamento);
    }

    private static BigDecimal getValorItensLiquidoPdf(Orcamento orcamentoRef) {
        BigDecimal valorItens = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (orcamentoRef == null || orcamentoRef.getItens() == null) {
            return valorItens;
        }

        for (ItemOrcamento item : orcamentoRef.getItens()) {
            valorItens = valorItens.add(getValorLiquidoPdf(item, item.getQtcomp(), orcamentoRef));
        }

        return valorItens.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal getValorDescontoPdf(Orcamento orcamentoRef) {
        BigDecimal valorDesconto = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (orcamentoRef == null || orcamentoRef.getItens() == null) {
            return valorDesconto;
        }

        for (ItemOrcamento item : orcamentoRef.getItens()) {
            if (item == null) {
                continue;
            }
            valorDesconto = valorDesconto.add(
                    BigDecimal.valueOf(item.getVlDescItem())
                            .multiply(BigDecimal.valueOf(item.getQtcomp()))
                            .setScale(2, RoundingMode.HALF_UP)
            );
        }

        if (valorDesconto.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return valorDesconto.setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal getValorItensCabecalhoPdf(Orcamento orcamentoRef) {
        BigDecimal valorItensLiquido = getValorItensLiquidoPdf(orcamentoRef);
        BigDecimal valorDesconto = getValorDescontoPdf(orcamentoRef);
        // O primeiro campo do PDF deve refletir exatamente:
        // Valor total dos itens + Valor do desconto.
        return valorItensLiquido.add(valorDesconto).setScale(2, RoundingMode.HALF_UP);
    }

    private static boolean deveAplicarRegraPromocionalExibicao(ItemOrcamento item, Orcamento orcamentoRef) {
        return false;
    }

    public static boolean deveAplicarPromocaoPrazoNaExibicao(ItemOrcamento item) {
        return deveAplicarRegraPromocionalExibicao(item, orcamento);
    }

    private static boolean isItemPromocional(ItemOrcamento item) {
        return temDescontoMotorPromocional(item);
    }

    private static boolean temDescontoMotorPromocional(ItemOrcamento item) {
        if (item == null || !isMotorPromocional(item.getIsMotor())) {
            return false;
        }

        return item.getVlDescItem() > 0 || item.getVldescPromo() > 0;
    }

    private static boolean isMotorPromocional(String valor) {
        return possuiTexto(valor) && ("S".equalsIgnoreCase(valor.trim()) || "SIM".equalsIgnoreCase(valor.trim()));
    }

    private static boolean possuiTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private static boolean deveAplicarRegraPromocionalAosValoresCarregados(Orcamento orcamentoAtual) {
        if (orcamentoAtual == null
                || orcamentoAtual.getNumorc() <= 0
                || !isOrcamentoAPrazo(orcamentoAtual)
                || orcamentoAtual.getItens() == null
                || orcamentoAtual.getItens().isEmpty()) {
            return false;
        }

        boolean possuiItemPromocional = false;
        for (ItemOrcamento item : orcamentoAtual.getItens()) {
            if (isItemPromocional(item)) {
                possuiItemPromocional = true;
                break;
            }
        }

        if (!possuiItemPromocional) {
            return false;
        }

        double totalSemRegra = calcularTotalItensComparacao(orcamentoAtual, false);
        double totalComRegra = calcularTotalItensComparacao(orcamentoAtual, true);
        double totalCarregado = orcamentoAtual.getVlTotal();

        return Math.abs(totalCarregado - totalComRegra) + 0.009d < Math.abs(totalCarregado - totalSemRegra);
    }

    private static void normalizarPrecoUnitarioItensPromocionaisCarregados(Orcamento orcamentoAtual) {
        // O preco base do item deve permanecer fixo.
        // O efeito promocional/prazo e representado no desconto, nao em PrecoUnit.
    }

    private static boolean orcamentoTemPrecoPromocionalJaComAcrescimoPrazo(Orcamento orcamentoAtual) {
        if (orcamentoAtual == null
                || orcamentoAtual.getNumorc() <= 0
                || !isOrcamentoAPrazo(orcamentoAtual)
                || orcamentoAtual.getItens() == null
                || orcamentoAtual.getItens().isEmpty()) {
            return false;
        }

        boolean possuiItemPromocional = false;
        for (ItemOrcamento item : orcamentoAtual.getItens()) {
            if (isItemPromocional(item)) {
                possuiItemPromocional = true;
                break;
            }
        }

        if (!possuiItemPromocional) {
            return false;
        }

        double totalSemRegra = calcularTotalItensComparacao(orcamentoAtual, false);
        double totalComRegra = calcularTotalItensComparacao(orcamentoAtual, true);
        double totalCarregado = orcamentoAtual.getVlTotal();

        return Math.abs(totalCarregado - totalSemRegra) + 0.009d < Math.abs(totalCarregado - totalComRegra);
    }

    private static double calcularTotalItensComparacao(Orcamento orcamentoAtual, boolean aplicarRegraPromocional) {
        if (orcamentoAtual == null || orcamentoAtual.getItens() == null) {
            return 0.0d;
        }

        BigDecimal total = BigDecimal.ZERO;

        for (ItemOrcamento item : orcamentoAtual.getItens()) {
            if (item == null) {
                continue;
            }

            double valorLiquido = (item.getPrecoUnit() - item.getVlDescItem()) * item.getQtcomp();
            total = total.add(BigDecimal.valueOf(valorLiquido));
        }

        return total.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    private static double reverterPrecoUnitarioBrutoPromocionalPrazo(double precoBrutoAtual, ItemOrcamento item) {
        if (item == null) {
            return precoBrutoAtual;
        }
        return ((precoBrutoAtual - item.getVlDescItem()) * 0.95d) + item.getVlDescItem();
    }

    private static boolean isOrcamentoAPrazo() {
        try {
            if (orcamento != null && orcamento.getCondPagamento() != null) {
                if (orcamento.getCondPagamento().getDescricao() != null) {
                    String descricaoNormalizada = orcamento.getCondPagamento().getDescricao().toUpperCase().trim();
                    if (descricaoNormalizada.contains("A VISTA") || descricaoNormalizada.contains("AVISTA")) {
                        return false;
                    }
                    if (descricaoNormalizada.contains("A PRAZO")) {
                        return true;
                    }
                }
                if (orcamento.getCondPagamento().getCondpgto() != null
                        && !orcamento.getCondPagamento().getCondpgto().trim().isEmpty()) {
                    return !"01".equals(orcamento.getCondPagamento().getCondpgto().trim());
                }
                if (orcamento.getQtprc() <= 1 && orcamento.getVlEntrada() <= 0d) {
                    return false;
                }
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    private static boolean isOrcamentoAPrazo(Orcamento orcamentoRef) {
        try {
            if (orcamentoRef != null && orcamentoRef.getCondPagamento() != null) {
                if (orcamentoRef.getCondPagamento().getDescricao() != null) {
                    String descricaoNormalizada = orcamentoRef.getCondPagamento().getDescricao().toUpperCase().trim();
                    if (descricaoNormalizada.contains("A VISTA") || descricaoNormalizada.contains("AVISTA")) {
                        return false;
                    }
                    if (descricaoNormalizada.contains("A PRAZO")) {
                        return true;
                    }
                }
                if (orcamentoRef.getCondPagamento().getCondpgto() != null
                        && !orcamentoRef.getCondPagamento().getCondpgto().trim().isEmpty()) {
                    return !"01".equals(orcamentoRef.getCondPagamento().getCondpgto().trim());
                }
                if (orcamentoRef.getQtprc() <= 1 && orcamentoRef.getVlEntrada() <= 0d) {
                    return false;
                }
            }
        } catch (Exception ignore) {
        }
        return false;
    }

}
