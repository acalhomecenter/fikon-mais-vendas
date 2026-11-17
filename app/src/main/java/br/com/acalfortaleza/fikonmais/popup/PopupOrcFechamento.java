package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.ConsultaActivity;
import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.dao.OrcamentoAlteradoDao;
import br.com.acalfortaleza.fikonmais.fragments.ProdutoFragments;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.model.FreteServicoOrcamento;
import br.com.acalfortaleza.fikonmais.model.FreteServicoPost;
import br.com.acalfortaleza.fikonmais.models.Arquiteto;
import br.com.acalfortaleza.fikonmais.models.CondPagamento;
import br.com.acalfortaleza.fikonmais.models.FasesObra;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import br.com.acalfortaleza.fikonmais.model.FreteServicoResposta;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PopupOrcFechamento extends Activity {
    private static final int ID_SERVICO_DESCONTO_FRETE_GERENTE = 101;

    private List<CondPagamento> mList = new ArrayList<>();
    private List<FasesObra> mListFases;
    private List<Arquiteto> mListArquitetos;
    private OrcamentoHelper helper;
    private ProgressDialog dialog;
    private Handler handler;
    private TextInputEditText edtcondpag;
    private TextInputEditText edtArquiteto;
    private TextInputEditText edtFase;
    private TextInputEditText edtFrete;
    private TextInputEditText edtQtdParcelas;
    private TextInputEditText edtEntrada;
    private TextInputLayout layout_qtdParcelas;
    private TextInputLayout layout_entrada;
    private TextInputLayout layout_fase;
    private TextInputLayout layout_condicao;
    private TextInputLayout layout_frete;
    private Button btn_fechar;
    private TextView txtTotal;
    private Button btn_finalizar;
    public static Orcamento orcamento;
    private String condicao_selecionada = "01";
    private String fase_selecionada = "N";
    private String temEntrada = "N";
    private String retorno = "N";
    private HashMap<String, String> usuarioLogado;
    private LoginHelper helperLogin;
    final DecimalFormat df = new DecimalFormat("0.00");
    final DecimalFormat dfInteiro = new DecimalFormat("0");
    private SpinnerDialog spinnerDialog;
    private SpinnerDialog spinnerDialogFase;
    private SpinnerDialog spinnerDialogCondicao;
    private PedidosHelper pedidosHelper;
    private ArrayList<String> listaCondicao = new ArrayList<>();
    private ArrayList<String> listaArq = new ArrayList<>();
    private ArrayList<String> listaFase = new ArrayList<>();
    public static String consultor_selecionado = "N";

    private static String tipoEncomenda="";

    private OrcamentoAlteradoDao orcamentoAlteradoDao;

    private Switch switchRetira;
    public static java.util.Map<Integer, Double> servicosFretePorOrcamento = new java.util.HashMap<>();
    public static java.util.Map<Integer, java.util.List<Integer>> idsServicosFretePorOrcamento = new java.util.HashMap<>();

    private TextInputEditText edtServicosFrete;
    private TextInputLayout layoutServicosFrete;
    private boolean orcamentoCarregadoOriginalmenteAPrazo = false;
    private boolean avisoPromocionalPrazoExibidoAoAbrir = false;
    private double freteOrig = 0.00d;
    private boolean descontoFreteEspecialAtivo = false;
    private Orcamento orcamentoVistaOriginal;
    private Orcamento orcamentoPrazoOriginal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_orcfechamento);


        tipoEncomenda = "";

        orcamentoVistaOriginal = (Orcamento) getIntent().getSerializableExtra("orcamento");
        orcamentoPrazoOriginal = (Orcamento) getIntent().getSerializableExtra("orcamentoAPrazo");
        orcamento = obterOrcamentoInicial();
        orcamentoCarregadoOriginalmenteAPrazo = isOrcamentoOriginalAPrazo();
        if (orcamento != null && orcamento.getCondPagamento() != null && orcamento.getCondPagamento().getCondpgto() != null
                && !orcamento.getCondPagamento().getCondpgto().trim().isEmpty()) {
            condicao_selecionada = orcamento.getCondPagamento().getCondpgto().trim();
        }

        helper = new OrcamentoHelper();
        handler = new Handler();
        helperLogin = new LoginHelper(this);
        pedidosHelper = new PedidosHelper();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .9));


        usuarioLogado = helperLogin.GetUsuarioSalvo();


        orcamentoAlteradoDao = new OrcamentoAlteradoDao(getApplicationContext());


        edtcondpag = findViewById(R.id.pop_orc_spinner);
        edtFase = findViewById(R.id.pop_orc_edtFase);
        edtArquiteto = findViewById(R.id.pop_orc_edtArquiteto);
        edtQtdParcelas = findViewById(R.id.pop_orc_edtQtdparcelas);
        edtEntrada = findViewById(R.id.pop_orc_edtEntrada);
        edtFrete = findViewById(R.id.pop_orc_edtFrete);
        edtServicosFrete = findViewById(R.id.pop_orc_edtServicosFrete);
        btn_finalizar = findViewById(R.id.pop_orc_btn_Finalizar);
        layout_entrada = findViewById(R.id.pop_orc_layoutEntrada);
        layout_qtdParcelas = findViewById(R.id.pop_orc_layoutParcelas);
        layout_condicao = findViewById(R.id.pop_orc_layoutcondicao);
        layout_frete = findViewById(R.id.pop_orc_layoutFrete);
        layoutServicosFrete = findViewById(R.id.pop_orc_layoutServicosFrete);
        layout_fase = findViewById(R.id.pop_orc_layoutFase);
        txtTotal = findViewById(R.id.pop_orc_txtTotal);
        btn_fechar = findViewById(R.id.pop_orc_btnFechar);
        switchRetira = findViewById(R.id.PopupOrcFechamento_SwitchRetira);
        sincronizarCondicaoAtualDoOrcamento();

        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setupSwitchRetiraListener();


        ConsultarCombobox();
        // edtcondpag.post(this::tentarExibirAvisoPromocionalAPrazoAoAbrir);


        spinnerDialogCondicao = new SpinnerDialog(PopupOrcFechamento.this, listaCondicao, "Selecione uma condição de pagamento");
        spinnerDialogCondicao.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtcondpag.setText(item);
                if (position >= 0) {
                    String condicaoCodigoSelecionada = mList.get(position).getCondpgto();
                    String condicaoDescricaoSelecionada = mList.get(position).getDescricao();
                    condicao_selecionada = condicaoCodigoSelecionada;

                    if(condicaoCodigoSelecionada.equals("01")){
                        orcamento = obterOrcamentoVista();
                    }
                    else{
                        orcamento = obterOrcamentoPrazo();
                    }

                    temEntrada = mList.get(position).getTemEntrada();
                    orcamento.setCondPagamento(mList.get(position));
                    logDiagnosticoPromocaoPrazoOrcamento("condicao_selecionada_antes_total", orcamento, condicaoCodigoSelecionada, condicaoDescricaoSelecionada);
                    alteraTotal(orcamento.getItens());
                    logDiagnosticoPromocaoPrazoOrcamento("condicao_selecionada_depois_total", orcamento, condicaoCodigoSelecionada, condicaoDescricaoSelecionada);
                    exibirAvisoReajustePromocionalOrcamento(condicaoCodigoSelecionada, condicaoDescricaoSelecionada, orcamento);

                    if ("S".equalsIgnoreCase(mList.get(position).getTemEntrada())) {
                        edtEntrada.setEnabled(true);
                        edtEntrada.setText("0.00");

                    } else {
                        edtEntrada.setEnabled(false);
                        edtEntrada.setText("0.00");

                    }
                }
                if(item.contains("CARTAO PROPRIO")){

                    Intent intent = new Intent(PopupOrcFechamento.this, PopupPlanSimulationCredz.class);
                    intent.putExtra("orcamento", orcamento);
                    startActivity(intent);

                }
            }
        });



        spinnerDialog = new SpinnerDialog(PopupOrcFechamento.this, listaArq, "Selecione um Arquiteto");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                if(item.equals("Retirar Especificador")){
                    edtArquiteto.setText("");
                    orcamento.setArquiteto("");
                    orcamento.setCodArq(0);
                }else{
                    edtArquiteto.setText(item);
                    orcamento.setArquiteto(item);
                    orcamento.setCodArq(mListArquitetos.get(position).getCodarq());
                }

            }
        });


        spinnerDialogFase = new SpinnerDialog(PopupOrcFechamento.this, listaFase, "Selecione um Item");
        spinnerDialogFase.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtFase.setText(item);
                orcamento.setFaseObra(item);
                fase_selecionada = "S";
            }
        });


        if (orcamento.getArquiteto()!= null && !orcamento.getArquiteto().isEmpty()) {
            edtArquiteto.setText(orcamento.getArquiteto());
        }

        if (orcamento.getFaseObra() != null && !orcamento.getFaseObra().isEmpty()) {
            edtFase.setText(orcamento.getFaseObra());
            fase_selecionada = "S";

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




        edtFase.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtArquiteto.getRight() - edtArquiteto.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialogFase.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        edtcondpag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtcondpag.getRight() - edtcondpag.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialogCondicao.showSpinerDialog();

                    }
                }
                return false;
            }
        });

        // Abrir popup de Serviços de frete pelo input com ícone
        final TextInputEditText edtServicosFreteLocal = findViewById(R.id.pop_orc_edtServicosFrete);
        if (edtServicosFreteLocal != null) {
            edtServicosFreteLocal.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_RIGHT = 2;
                    if (!edtServicosFreteLocal.isEnabled()) {
                        return false;
                    }
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (edtServicosFreteLocal.getRight() - edtServicosFreteLocal.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            Intent intent = new Intent(PopupOrcFechamento.this, PopupServicosAdicionais.class);
                            intent.putExtra("isOrcamento", true);
                            int numOrc = (orcamento != null) ? (int) orcamento.getNumorc() : 0;
                            intent.putExtra("numOrc", numOrc);
                            double pesoOrc = (orcamento != null) ? orcamento.getPeso() : 0.0;
                            intent.putExtra("pesoTotal", pesoOrc);
                            android.util.Log.d("FreteServAdd", "Abrindo PopupServicosAdicionais para Orçamento " + numOrc + " com peso: " + pesoOrc + " KG");
                            startActivity(intent);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }



        edtQtdParcelas.setText("1");
        // Exibe apenas o frete base (VlFreteSelecionado), não o calculado
        // edtFrete.setText(String.valueOf(orcamento.getVlFreteSelecionado() > 0 ? orcamento.getVlFreteSelecionado() : 0.00));
        atualizarValorFrete();
        atualizarPermissaoEdicaoFrete();

        edtEntrada.setText("0.00");
        edtEntrada.setEnabled(false);
        switchRetira.setChecked(false);

        edtFrete.setOnEditorActionListener((textView, actionId, event) -> {
            if ((event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()) || actionId == EditorInfo.IME_ACTION_DONE) {
                if (edtFrete.getText() == null || edtFrete.getText().toString().trim().isEmpty()) {
                    edtFrete.setText("0.00");
                }
                if (validarDescontoFrete()) {
                    alteraTotal(orcamento.getItens());
                }
            }
            return false;
        });

        edtFrete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (edtFrete.getText() != null && !edtFrete.getText().toString().isEmpty()) {
                        if (validarDescontoFrete()) {
                            alteraTotal(orcamento.getItens());
                        }
                    } else {
                        edtFrete.setText("0.00");
                    }

                    if (b) {
                        if (edtFrete.getText().toString().equals("0.00")) {
                            edtFrete.setText("");
                        }
                    }

                }
            }
        });



        btn_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validarCondicao() || !validarParcelas() || !validarEntrada() || !validarParcelaZero() || !validarQtdParcelas() || !validarFase() || !validarDescontoFrete()) {

                } else {

                    if(usuarioLogado.get("cargo").equals("AUXILIAR DE VENDAS CORPORATIVO FX U") || usuarioLogado.get("cargo").equals("AUXILIAR DE VENDAS FX U")){
                        if(consultor_selecionado.equals("N")){
                            Intent intent = new Intent(PopupOrcFechamento.this, PopupConsultor.class);
                            startActivity(intent);
                        }else{
                            carregaOrcamento();
                        }
                    }else{
                        if (Integer.parseInt(usuarioLogado.get("codigo")) == 0) {
                            Toast.makeText(PopupOrcFechamento.this, "Atenção :| apenas consultores podem utilizar essa função!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            carregaOrcamento();
                        }
                    }



                }
            }
        });


        alteraTotal(orcamento.getItens());


    }

    private void setupSwitchRetiraListener() {
        switchRetira.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            double vlfrete = 0.00d;
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                layoutServicosFrete.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                edtServicosFrete.setEnabled(!isChecked);

                if (isChecked) {
                    edtFrete.setText("0.00");
                    edtServicosFrete.setText("0.00");
                    freteOrig = 0.0d;
                    if (layout_frete != null) {
                        layout_frete.setError(null);
                    }
                    int numOrc = (orcamento != null) ? (int) orcamento.getNumorc() : 0;
                    servicosFretePorOrcamento.remove(numOrc);
                } else {
                    // Se há frete selecionado, exibe apenas o valor base do frete (SEM serviços adicionais)
                    atualizarValorFrete();
                }
                sincronizarRegraDescontoFreteEspecial();
                alteraTotal(orcamento.getItens());
            }
        });
    }

    private void atualizarValorFrete() {
        double vlfrete = 0.00d;
        double freteBaseSalvo = getFreteBasePersistido();
        if (freteBaseSalvo > 0) {
            vlfrete = freteBaseSalvo;
        } else if (orcamento.getVlFreteSelecionado() > 0) {
            if (usuarioLogado.get("codfil").equals("501")) {
                if (orcamento.getPeso() < 500) {
                    vlfrete = 50;
                } else {
                    vlfrete = Math.floor(100 * (orcamento.getPeso() / 1000));
                }
            } else {
                if ((orcamento.getPeso() / 1000) > 1.0) {
                    vlfrete = Math.floor(orcamento.getVlFreteSelecionado() * (orcamento.getPeso() / 1000));
                } else {
                    vlfrete = orcamento.getVlFreteSelecionado();
                }
            }
        } else {
            vlfrete = 0.00d;
        }
        edtFrete.setText(String.valueOf(vlfrete));
        freteOrig = vlfrete;
    }

    private double getFreteBasePersistido() {
        if (orcamento == null) {
            return 0.0d;
        }

        if (orcamento.getVlFreteCalculado() > 0.0d) {
            return orcamento.getVlFreteCalculado();
        }

        double servicosPersistidos = getServicosFretePersistidos((int) orcamento.getNumorc());
        if (orcamento.getVlFrete() > 0.0d) {
            double freteBase = orcamento.getVlFrete() - servicosPersistidos;
            if (freteBase > 0.0d || servicosPersistidos == 0.0d) {
                return Math.max(0.0d, freteBase);
            }
        }

        return 0.0d;
    }

    private double getServicosFretePersistidos(int key) {
        Double servicoEmCache = servicosFretePorOrcamento.get(key);
        if (servicoEmCache != null && servicoEmCache > 0.0d) {
            return servicoEmCache;
        }

        if (orcamento != null && orcamento.getVlServicosAdicionais() > 0.0d) {
            if (key > 0) {
                servicosFretePorOrcamento.put(key, orcamento.getVlServicosAdicionais());
            }
            return orcamento.getVlServicosAdicionais();
        }

        return 0.0d;
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
        List<Integer> idsSelecionados = null;
        if (orcamento != null) {
            idsSelecionados = idsServicosFretePorOrcamento.get((int) orcamento.getNumorc());
        }
        if ((idsSelecionados == null || idsSelecionados.isEmpty()) && orcamento != null && orcamento.getNumorc() > 0) {
            idsSelecionados = idsServicosFretePorOrcamento.get((int) orcamento.getNumorc());
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
        if (edtFrete == null) {
            return;
        }
        edtFrete.setEnabled(true);
        edtFrete.setFocusable(true);
        edtFrete.setFocusableInTouchMode(true);
        edtFrete.setClickable(true);
        edtFrete.setLongClickable(true);
    }

    private void sincronizarRegraDescontoFreteEspecial() {
        boolean especialAtivoAgora = podeAplicarDescontoFreteEspecial();
        atualizarPermissaoEdicaoFrete();

        if (descontoFreteEspecialAtivo && !especialAtivoAgora) {
            try {
                double freteAtual = parseFreteInput();
                if (freteOrig > 0.0d && freteAtual < freteOrig) {
                    edtFrete.setText(df.format(freteOrig));
                    if (orcamento != null) {
                        orcamento.setVlFrete(freteOrig);
                        if (orcamento.getItens() != null) {
                            atualizaTotais(orcamento.getItens());
                        }
                    }
                }
            } catch (Exception ignore) {
            }
        }

        descontoFreteEspecialAtivo = especialAtivoAgora;
    }

    private double parseFreteInput() {
        if (edtFrete == null || edtFrete.getText() == null || edtFrete.getText().toString().trim().isEmpty()) {
            return 0.0d;
        }
        return Double.parseDouble(edtFrete.getText().toString().replace(",", "."));
    }

    private double getServicosFreteValue() {
        double value = 0.0;
        try {
            String text = edtServicosFrete.getText().toString();
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            value = format.parse(text).doubleValue();
        } catch (ParseException e) {
            try {
                if(edtServicosFrete.getText() != null) {
                    value = Double.parseDouble(edtServicosFrete.getText().toString().replace(",", "."));
                }
            } catch (NumberFormatException nfe) {
                // ignore
            }
        }
        return value;
    }


    @Override
    protected void onResume() {
        super.onResume();
        sincronizarRegraDescontoFreteEspecial();
        TextInputEditText edtServ = findViewById(R.id.pop_orc_edtServicosFrete);
        if (edtServ != null) {
            int key = (orcamento != null && orcamento.getNumorc() > 0) ? (int) orcamento.getNumorc() : 0;

            if (orcamento != null && orcamento.getNumorc() > 0) {
                fetchFreteServico(key);
            } else {
                updateServicosFreteField(key);
            }
        }
    }

    private void fetchFreteServico(int numOrc) {
        // Verifica se já tem valor em cache local
        if (servicosFretePorOrcamento.containsKey(numOrc)) {
            android.util.Log.d("FreteServico", "Usando valor em cache local para orçamento " + numOrc);
            updateServicosFreteField(numOrc);
            return;
        }

        android.util.Log.d("FreteServico", "Buscando do servidor para orçamento " + numOrc);
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

        // ----- INÍCIO DA CORREÇÃO -----

        // 1. O tipo da variável 'call' foi alterado para o novo objeto de resposta
        Call<FreteServicoResposta> call = api.GetFreteServicoOrcamento(token, numOrc);

        // 2. O tipo do Callback também foi alterado para corresponder
        call.enqueue(new Callback<FreteServicoResposta>() {
            @Override
            public void onResponse(Call<FreteServicoResposta> call, Response<FreteServicoResposta> response) {                if (response.isSuccessful() && response.body() != null) {
                if (response.isSuccessful() && response.body() != null) {
                    // 3. Extraímos o valor de dentro do objeto JSON usando o getter
                    double valorServico = response.body().getValorTotal();
                    List<Integer> idsServico = response.body().getIdsSelecionados();
                    android.util.Log.d("FreteServico", "Valor do servidor (JSON): " + valorServico);
                    android.util.Log.d("FreteDebug", "IDs de serviço recuperados da API: " + idsServico);
                    servicosFretePorOrcamento.put(numOrc, valorServico);
                    if (idsServico != null) {
                        idsServicosFretePorOrcamento.put(numOrc, idsServico);
                    }

                }
                }
                updateServicosFreteField(numOrc);
            }

            @Override
            public void onFailure(Call<FreteServicoResposta> call, Throwable t) {
                Log.e("API_ERROR", "Falha ao buscar serviço de frete", t);
                updateServicosFreteField(numOrc);
            }
        });
    }


    private void updateServicosFreteField(int key) {
        TextInputEditText edtServ = findViewById(R.id.pop_orc_edtServicosFrete);
        double servicosFreteOrc = getServicosFretePersistidos(key);
        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        edtServ.setText(real.format(servicosFreteOrc));
        if (orcamento != null) {
            orcamento.setVlServicosAdicionais(servicosFreteOrc);
        }
        sincronizarRegraDescontoFreteEspecial();
        alteraTotal(orcamento.getItens());
    }

    private Orcamento obterOrcamentoInicial() {
        if (isOrcamentoAPrazo(orcamentoVistaOriginal)) {
            return obterOrcamentoPrazo();
        }
        if (orcamentoVistaOriginal != null) {
            return orcamentoVistaOriginal;
        }
        return obterOrcamentoPrazo();
    }

    private Orcamento obterOrcamentoVista() {
        if (orcamentoVistaOriginal != null) {
            return orcamentoVistaOriginal;
        }
        if (orcamentoPrazoOriginal != null) {
            return orcamentoPrazoOriginal;
        }
        return orcamento;
    }

    private Orcamento obterOrcamentoPrazo() {
        if (orcamentoPrazoOriginal != null) {
            return orcamentoPrazoOriginal;
        }
        if (orcamentoVistaOriginal != null) {
            return orcamentoVistaOriginal;
        }
        return orcamento;
    }

    private boolean isOrcamentoAPrazo(Orcamento orcamentoRef) {
        if (orcamentoRef == null) {
            return false;
        }
        return isCondicaoAPrazo(
                orcamentoRef.getCondPagamento() != null ? orcamentoRef.getCondPagamento().getCondpgto() : null,
                orcamentoRef.getCondPagamento() != null ? orcamentoRef.getCondPagamento().getDescricao() : null
        );
    }

    private void sincronizarCondicaoAtualDoOrcamento() {
        if (orcamento == null || orcamento.getCondPagamento() == null) {
            return;
        }

        try {
            if (orcamento.getCondPagamento().getCondpgto() != null
                    && !orcamento.getCondPagamento().getCondpgto().trim().isEmpty()) {
                condicao_selecionada = orcamento.getCondPagamento().getCondpgto().trim();
            }
        } catch (Exception ignore) {
        }

        try {
            if (orcamento.getCondPagamento().getDescricao() != null
                    && !orcamento.getCondPagamento().getDescricao().trim().isEmpty()) {
                edtcondpag.setText(orcamento.getCondPagamento().getDescricao().trim());
            }
        } catch (Exception ignore) {
        }

        try {
            if (orcamento.getCondPagamento().getTemEntrada() != null
                    && !orcamento.getCondPagamento().getTemEntrada().trim().isEmpty()) {
                temEntrada = orcamento.getCondPagamento().getTemEntrada().trim();
            }
        } catch (Exception ignore) {
        }
    }



    public void ConsultarCombobox() {

        dialog = new ProgressDialog(PopupOrcFechamento.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {


                    mList = helperLogin.getListaCondicoes(Integer.parseInt(usuarioLogado.get("codfil")));
                    mListFases = helperLogin.GetFases();
                    mListArquitetos = helperLogin.GetArquitetos();


                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            if (mList != null && !mList.isEmpty()) {
                                for (CondPagamento c : mList) {
                                    listaCondicao.add(c.toString());
                                }

                            }

                            if (mListFases != null && !mListFases.isEmpty()) {
                                for (FasesObra f : mListFases) {
                                    listaFase.add(f.Descricao);
                                }


                            }


                            if (mListArquitetos != null && !mListArquitetos.isEmpty()) {
                                for (Arquiteto a : mListArquitetos) {
                                    listaArq.add(a.getNomearq());
                                }


                            }


                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupOrcFechamento.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            }).show();
                        }
                    });
                } catch (ParseException | XmlPullParserException | IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupOrcFechamento.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            }).show();
                        }
                    });
                } finally {
                    if(dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }


        }.start();


    }

    private void carregaOrcamento() {

        if (edtFrete.getText() != null && edtFrete.getText().toString().isEmpty()) {
            edtFrete.setText("0.00");
        }

        // Correção: Somar o frete base com os serviços de frete
        double freteBase = 0.0;
        try {
            freteBase = Double.parseDouble(edtFrete.getText().toString().replace(",", "."));
        } catch (NumberFormatException e) {
            Log.e("CarregaOrcamentoDebug", "Erro ao parse freteBase: " + edtFrete.getText().toString());
        }

        double servicosFrete = getServicosFreteValue();
        double freteTotal = freteBase + servicosFrete;

        Log.d("CarregaOrcamentoDebug", "Frete Base: " + freteBase);
        Log.d("CarregaOrcamentoDebug", "Serviços Frete: " + servicosFrete);
        Log.d("CarregaOrcamentoDebug", "Frete Total a ser salvo: " + freteTotal);

        orcamento.setVlFrete(freteTotal);

        if(edtQtdParcelas.getText() != null && edtQtdParcelas.getText().toString().isEmpty()){
            edtQtdParcelas.setText("1");
        }
        orcamento.setQtprc(Integer.parseInt(edtQtdParcelas.getText().toString()));
        if (edtEntrada.getText() != null && edtEntrada.getText().toString().isEmpty()) {
            orcamento.setVlEntrada(0.00);
        } else {
            orcamento.setVlEntrada(Double.parseDouble(edtEntrada.getText().toString()));
        }

        if (orcamento.getCodvendr() > 0) {
            orcamento.setCodvendr((orcamento.getCodvendr()));
            orcamento.setFilial(orcamento.getFilial());
            orcamento.setConsultor(orcamento.getConsultor());
            orcamento.setCodfil(orcamento.getCodfil());
            orcamento.setCodaux(Integer.parseInt(usuarioLogado.get("codigo")));
        } else {
            orcamento.setCodvendr(Integer.parseInt(usuarioLogado.get("codigo")));
            orcamento.setFilial(usuarioLogado.get("filialorig"));
            orcamento.setConsultor(usuarioLogado.get("nome"));
            orcamento.setCodfil(Integer.parseInt(usuarioLogado.get("codfil")));
            orcamento.setCodaux(Integer.parseInt(usuarioLogado.get("codigo")));
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 0);
        orcamento.setDtValidade(dateFormat.format(c.getTime()));

        atualizaTotais(orcamento.getItens());
        consultor_selecionado = "N";
        salvarOrcamento();



    }



    private void salvarOrcamento() {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Salvando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {
                    android.util.Log.d("OrcamentoSalvamento", "=== SALVANDO ORÇAMENTO ===");
                    android.util.Log.d("OrcamentoSalvamento", "Número do orçamento: " + orcamento.getNumorc());
                    android.util.Log.d("OrcamentoSalvamento", "VlTotal (Total de itens): " + orcamento.getVlTotal());
                    android.util.Log.d("OrcamentoSalvamento", "VlFrete: " + orcamento.getVlFrete());
                    android.util.Log.d("OrcamentoSalvamento", "VlDesconto: " + orcamento.getVlDesconto());
                    android.util.Log.d("OrcamentoSalvamento", "VlFreteSelecionado: " + orcamento.getVlFreteSelecionado());
                    android.util.Log.d("OrcamentoSalvamento", "VlFreteCalculado: " + orcamento.getVlFreteCalculado());
                    android.util.Log.d("OrcamentoSalvamento", "VlTotal FINAL que será enviado: " + (orcamento.getVlTotal() + orcamento.getVlFrete()));

                    // Mantem a mesma regra do fechamento de pedido:
                    // para item do motor, o preco base permanece e o efeito de A PRAZO
                    // acontece pela reducao do desconto unitario enviado.
                    Orcamento orcamentoEnvio = prepararOrcamentoParaEnvioComPrecoPromocional(orcamento);
                    android.util.Log.d("OrcamentoSalvamento", "VlTotal FINAL do objeto de envio: " + (orcamentoEnvio.getVlTotal() + orcamentoEnvio.getVlFrete()));
                    
                    boolean itensSalvosComSucesso = false;
                    try {
                        retorno = helper.SetOrcamento(orcamentoEnvio);
                        if (retorno != null && !retorno.equals("Orcamento não cadastrado!")) {
                            List<Orcamento> listOrcamentos = helper.GetOrcamentoConsultaIndividual(retorno, "CODIGO");
                            if (listOrcamentos != null && !listOrcamentos.isEmpty() && listOrcamentos.get(0).getItens() != null && !listOrcamentos.get(0).getItens().isEmpty()) {
                                itensSalvosComSucesso = true;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        itensSalvosComSucesso = false;
                    }

                    if (itensSalvosComSucesso) {
                        if (retorno != null && !retorno.equals("Orcamento não cadastrado!")) {
                            orcamentoAlteradoDao.setItensEmCache(retorno, false);
                        }
                        int oldNumOrc = (orcamento != null) ? (int) orcamento.getNumorc() : 0;
                        if (oldNumOrc > 0) {
                            orcamentoAlteradoDao.setItensEmCache(dfInteiro.format(oldNumOrc), false);
                        }
                    } else {
                        if (retorno != null && !retorno.equals("Orcamento não cadastrado!")) {
                            orcamentoAlteradoDao.setItensEmCache(retorno, true);
                        } else if (orcamento != null && orcamento.getNumorc() > 0) {
                            orcamentoAlteradoDao.setItensEmCache(dfInteiro.format(orcamento.getNumorc()), true);
                        }
                    }

                    if (
                        retorno != null
                        && !retorno.equals("Orcamento não cadastrado!")
                    ) {

                        int newNumOrc = Integer.parseInt(retorno);
                        int oldNumOrc = (orcamento != null) ? (int) orcamento.getNumorc() : 0;

                        if (servicosFretePorOrcamento.containsKey(oldNumOrc)) {
                            Double freteValue = servicosFretePorOrcamento.getOrDefault(oldNumOrc, 0.0);
                            List<Integer> ids = idsServicosFretePorOrcamento.getOrDefault(oldNumOrc, new ArrayList<>());

                            // Mantem o cache local sincronizado com o numero novo do orcamento.
                            // Sem isso, a activity pode consultar a chave nova e perder os servicos
                            // de frete ate uma nova recarga do servidor.
                            servicosFretePorOrcamento.put(newNumOrc, freteValue != null ? freteValue : 0.0d);
                            idsServicosFretePorOrcamento.put(newNumOrc, ids != null ? new ArrayList<>(ids) : new ArrayList<>());
                            if (orcamento != null) {
                                orcamento.setVlServicosAdicionais(freteValue != null ? freteValue : 0.0d);
                            }

                            // Sempre fazer POST quando há entrada no mapa, mesmo que ids esteja vazio (para limpar dados antigos)
                            if (freteValue != null && ids != null) {
                                FreteServicoPost post = new FreteServicoPost();
                                post.setOrcamento(newNumOrc);
                                post.setIdsSelecionados(ids);
                                post.setValorTotal(freteValue);

                                Log.d("FreteServicoPost", "Enviando dados para o orçamento: " + newNumOrc);
                                Log.d("FreteServicoPost", "IDs: " + ids.toString());
                                Log.d("FreteServicoPost", "ValorTotal: " + freteValue);

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
                                Call<String> call = api.PostFreteServicoOrcamento(token, post);
                                call.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.isSuccessful()) {
                                            Log.d("FreteServicoPost", "Sucesso ao salvar serviço de frete. Resposta: " + response.body());
                                        } else {
                                            Log.e("FreteServicoPost", "Erro ao salvar serviço de frete. Código: " + response.code() + " Mensagem: " + response.message());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.e("FreteServicoPost", "Falha na chamada da API de serviço de frete", t);
                                    }
                                });
                            }
                        }

                        List<Orcamento> listOrcamentos = helper.GetOrcamentoConsultaIndividual(retorno,"CODIGO");
                        if(listOrcamentos!=null && !listOrcamentos.isEmpty()){

                            Orcamento_LancamentoActivity.ambientesAlterados = false;
                            orcamento = mesclarOrcamentoRecarregadoComEstadoLocal(listOrcamentos.get(0), orcamentoEnvio);

                            // Cache local de promocao desativado para evitar persistir preco contaminado.

                            SharedPreferences preferences = getApplicationContext().getSharedPreferences("ambientesAlterados", Context.MODE_PRIVATE);
                            String chave = String.valueOf( Long.parseLong(retorno) );
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean(chave, false);
                            editor.apply();

                        }

                    }

                } catch (IOException | XmlPullParserException | ParseException e) {
                    e.printStackTrace();
                } finally {
                    if(dialog.isShowing()){
                       dialog.dismiss();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            if (retorno != null && !retorno.equals("Orcamento não cadastrado!")) {

                                orcamentoAlteradoDao.editarOrcamento( dfInteiro.format(orcamento.getNumorc()), orcamento );

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PopupOrcFechamento.this);
                                builder.setTitle("Realizado :)");
                                builder.setMessage("Seu lançamento foi realizado com sucesso! Orçamento número: " + retorno);
                                builder
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();


                                                orcamento.setNumorc(Integer.parseInt(retorno));
                                                Orcamento_LancamentoActivity.aplicarOrcamentoSalvoCarregado(orcamento);
                                                Orcamento_LancamentoActivity.txtNumOrc.setText("Nº Orc.: " + dfInteiro.format(orcamento.getNumorc()));
                                                Orcamento_LancamentoActivity.btn_finalizar.setText("ALTERAR ORÇAMENTO");
                                                ProdutoFragments.orcamento = null;
                                                ConsultaActivity.orcamento = null;
                                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("orcamentos").child(usuarioLogado.get("codigo"));
                                                myRef.removeValue();

                                                Orcamento_LancamentoActivity.btn_adcionar.setEnabled(false);
                                                Orcamento_LancamentoActivity.btn_adcionar.setBackgroundResource(R.drawable.backgroundbuttondisabled);
                                                Orcamento_LancamentoActivity.btn_finalizar.setEnabled(false);
                                                Orcamento_LancamentoActivity.btn_finalizar.setBackgroundResource(R.drawable.backgroundbuttondisabled);

                                                orcamentoAlteradoDao.editarOrcamento( dfInteiro.format(orcamento.getNumorc()), Orcamento_LancamentoActivity.orcamento );

                                                finish();


                                            }

                                        });

                                builder.setNegativeButton("Gerar Pedido", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        //Toast.makeText(PopupOrcFechamento.this,"Essa opção fica disponivel em breve!",Toast.LENGTH_SHORT).show();

                                        if(tipoEncomenda.isEmpty()) {
                                            if (orcamento.getCliente().getCodcli() > 0) {
                                                if (orcamento.getCliente().getSitcred().equals("Bloqueia Venda")) {
                                                    Toast.makeText(PopupOrcFechamento.this, "Cadastro do Cliente está com Situaçao: Bloqueia Venda!", Toast.LENGTH_LONG).show();
                                                } else {

                                                    orcamento.setNumorc(Integer.parseInt(retorno));
                                                    ProdutoFragments.orcamento = null;
                                                    ConsultaActivity.orcamento = null;
                                                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("orcamentos").child(usuarioLogado.get("codigo"));
                                                    myRef.removeValue();


                                                    Orcamento_LancamentoActivity.btn_adcionar.setEnabled(false);
                                                    Orcamento_LancamentoActivity.btn_adcionar.setBackgroundResource(R.drawable.backgroundbuttondisabled);
                                                    Orcamento_LancamentoActivity.btn_finalizar.setEnabled(false);
                                                    Orcamento_LancamentoActivity.btn_finalizar.setBackgroundResource(R.drawable.backgroundbuttondisabled);

                                                    if(helper.NecessarioCodigoEncomenda(orcamento)){

                                                        String validacao = helper.ValidarCodigoEncomenda(orcamento, Orcamento_LancamentoActivity.btn_adcionar.getContext());
                                                        if(!Objects.equals(validacao, "validado")){
                                                            finish();
                                                            return;
                                                        }

                                                    }

                                                    // --- INÍCIO DA ALTERAÇÃO ---
                                                    // Antes de gerar o pedido, consulta o servidor para obter
                                                    // os IDs e o total de serviços do orçamento recém-salvo.
                                                    int numOrc = (int) orcamento.getNumorc();

                                                    // Executa a consulta em thread separada para evitar NetworkOnMainThreadException
                                                    final android.app.ProgressDialog progress = new android.app.ProgressDialog(PopupOrcFechamento.this);
                                                    progress.setMessage("Buscando serviços do orçamento...");
                                                    progress.setCancelable(false);
                                                    progress.show();

                                                    new Thread(() -> {
                                                        java.util.List<Integer> idsServicos = new java.util.ArrayList<>();
                                                        double valorServTotal = 0.0d;
                                                        Orcamento orcamentoParaPedido = orcamento;
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

                                                            retrofit2.Response<br.com.acalfortaleza.fikonmais.model.FreteServicoResposta> resp = api.GetFreteServicoOrcamento(token, numOrc).execute();
                                                            if (resp.isSuccessful() && resp.body() != null) {
                                                                idsServicos = resp.body().getIdsSelecionados() != null ? resp.body().getIdsSelecionados() : new java.util.ArrayList<>();
                                                                valorServTotal = resp.body().getValorTotal();
                                                                android.util.Log.d("FreteServicoPost", "GET pós-salvamento OK. IDs=" + idsServicos + ", ValorTotal=" + valorServTotal);
                                                                idsServicosFretePorOrcamento.put(numOrc, idsServicos);
                                                                servicosFretePorOrcamento.put(numOrc, valorServTotal);
                                                                orcamento.setVlServicosAdicionais(valorServTotal);
                                                            } else {
                                                                android.util.Log.w("FreteServicoPost", "GET pós-salvamento falhou. Code=" + (resp != null ? resp.code() : -1));
                                                                idsServicos = idsServicosFretePorOrcamento.getOrDefault(numOrc, new java.util.ArrayList<>());
                                                                valorServTotal = servicosFretePorOrcamento.getOrDefault(numOrc, 0.0d);
                                                            }

                                                            try {
                                                                List<Orcamento> orcamentosAtualizados = helper.GetOrcamentoConsultaIndividual(String.valueOf(numOrc), "CODIGO");
                                                                if (orcamentosAtualizados != null && !orcamentosAtualizados.isEmpty()) {
                                                                    orcamentoParaPedido = orcamentosAtualizados.get(0);
                                                                    orcamentoParaPedido.setVlServicosAdicionais(valorServTotal);

                                                                    if (orcamento != null
                                                                            && orcamento.getItens() != null
                                                                            && !orcamento.getItens().isEmpty()
                                                                            && orcamentoParaPedido.getItens() != null
                                                                            && !orcamentoParaPedido.getItens().isEmpty()) {
                                                                        android.util.Log.d("PromoAPrazoOrcToPed",
                                                                                "GerarPedido via PopupOrcFechamento. precoLocal="
                                                                                        + orcamento.getItens().get(0).getPrecoUnit()
                                                                                        + " precoRecarregado="
                                                                                        + orcamentoParaPedido.getItens().get(0).getPrecoUnit()
                                                                                        + " item="
                                                                                        + orcamentoParaPedido.getItens().get(0).getCoditprod());
                                                                    }
                                                                }
                                                            } catch (Exception ex) {
                                                                android.util.Log.e("PromoAPrazoOrcToPed", "Falha ao recarregar orçamento antes de gerar pedido", ex);
                                                            }
                                                        } catch (Exception ex) {
                                                            android.util.Log.e("FreteServicoPost", "Erro ao consultar serviços para orçamento antes de gerar pedido", ex);
                                                            idsServicos = idsServicosFretePorOrcamento.getOrDefault(numOrc, new java.util.ArrayList<>());
                                                            valorServTotal = servicosFretePorOrcamento.getOrDefault(numOrc, 0.0d);
                                                        } finally {
                                                            java.util.List<Integer> finalIds = idsServicos;
                                                            double finalValor = valorServTotal;
                                                            Orcamento finalOrcamentoParaPedido = orcamentoParaPedido;
                                                            handler.post(() -> {
                                                                try {
                                                                    pedidosHelper.GerarPedido(
                                                                            PopupOrcFechamento.this,
                                                                            finalOrcamentoParaPedido,
                                                                            Integer.parseInt(usuarioLogado.get("prazoentrega")),
                                                                            usuarioLogado.get("filialorig")

                                                                    );
                                                                } finally {
                                                                    progress.dismiss();
                                                                }
                                                            });
                                                        }
                                                    }).start();
                                                    // --- FIM DA ALTERAÇÃO ---

                                                    finish();
                                                }
                                            } else {
                                                Toast.makeText(PopupOrcFechamento.this, "Pedido não pode ser gerado!, Cliente não cadastrado.", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            Toast.makeText(PopupOrcFechamento.this, "Pedido não pode ser gerado!, Orçamento possui produtos não cadastrados!.", Toast.LENGTH_SHORT).show();
                                            orcamento.setNumorc(Integer.parseInt(retorno));
                                            ProdutoFragments.orcamento = null;
                                            ConsultaActivity.orcamento = null;
                                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("orcamentos").child(usuarioLogado.get("codigo"));
                                            myRef.removeValue();


                                            Orcamento_LancamentoActivity.btn_adcionar.setEnabled(false);
                                            Orcamento_LancamentoActivity.btn_adcionar.setBackgroundResource(R.drawable.backgroundbuttondisabled);
                                            Orcamento_LancamentoActivity.btn_finalizar.setEnabled(false);
                                            Orcamento_LancamentoActivity.btn_finalizar.setBackgroundResource(R.drawable.backgroundbuttondisabled);

                                            finish();
                                        }

                                    }
                                });


                                builder.create().show();

                            } else {
                                String motivo = br.com.acalfortaleza.fikonmais.helper.FikonVendasClient.ativo()
                                        ? br.com.acalfortaleza.fikonmais.helper.FikonVendasClient.ultimoErro()
                                        : null;
                                Toast.makeText(PopupOrcFechamento.this,
                                        motivo == null || motivo.trim().isEmpty()
                                                ? "Orçamento não cadastrado"
                                                : "Orçamento não cadastrado: " + motivo,
                                        Toast.LENGTH_LONG).show();
                            }


                        }
                    });
                }

            }
        }.start();

    }


    public void atualizaTotais(List<ItemOrcamento> listaItens) {

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        BigDecimal valorTotal = BigDecimal.ZERO;
        double valorFrete = 0.00d;
        double valorDesconto = 0.00;

        if (edtFrete.getText() != null && !edtFrete.getText().toString().isEmpty()) {
            valorFrete = Double.parseDouble(edtFrete.getText().toString());
        }


        for (ItemOrcamento item : listaItens) {

            valorTotal = valorTotal.add(getValorLiquidoItemOrcamento(item));
            valorDesconto = valorDesconto + ((item.getVlDescItem()) * item.getQtcomp());

            if(item.getNcm()!= null && item.getNcm().equals("99999999")){
                tipoEncomenda = "S";
            }

        }
        orcamento.setVlTotal(valorTotal.doubleValue());
        orcamento.setVlFrete(valorFrete);
        orcamento.setVlDesconto(valorDesconto);
        if (valorTotal.compareTo(BigDecimal.ZERO) > 0) {
            orcamento.setDpi((valorDesconto / valorTotal.doubleValue()) * 100);
        } else {
            orcamento.setDpi(0);
        }
        double valorServicos = getServicosFreteValue();

        String temEntradaCondicao = orcamento.getCondPagamento() != null ? orcamento.getCondPagamento().getTemEntrada() : null;
        double valorEntrada = 0.0d;
        if (edtEntrada.getText() != null && !edtEntrada.getText().toString().isEmpty()) {
            valorEntrada = Double.parseDouble(edtEntrada.getText().toString());
        }

        if ("S".equalsIgnoreCase(temEntradaCondicao) && valorEntrada > 0) {
            orcamento.setVlPrc((valorTotal.doubleValue() + valorFrete + valorServicos - valorEntrada) / Integer.parseInt(edtQtdParcelas.getText().toString()) - 1);
        } else if (edtQtdParcelas.getText() != null && !edtQtdParcelas.getText().toString().isEmpty()){
            orcamento.setVlPrc((valorTotal.doubleValue() + valorFrete + valorServicos - valorEntrada) / Integer.parseInt(edtQtdParcelas.getText().toString()));
        }

        txtTotal.setText(String.format("Total: %s", real.format(valorTotal.doubleValue() + valorFrete + valorServicos)));
        logDiagnosticoPromocaoPrazoOrcamento("atualizaTotais", orcamento, condicao_selecionada, getDescricaoCondicaoAtual());


    }

    public void alteraTotal(List<ItemOrcamento> listaItens) {

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        BigDecimal valorTotal = BigDecimal.ZERO;
        double valorFrete = 0.00d;
        double valorServicos;


        if (edtFrete.getText() != null && !edtFrete.getText().toString().isEmpty()) {
            valorFrete = Double.parseDouble(edtFrete.getText().toString().replace(",", "."));
        }

        valorServicos = getServicosFreteValue();


        for (ItemOrcamento item : listaItens) {

            valorTotal = valorTotal.add(getValorLiquidoItemOrcamento(item));


        }

        txtTotal.setText(String.format("Total: %s", real.format(valorTotal.doubleValue() + valorFrete + valorServicos)));
        logDiagnosticoPromocaoPrazoOrcamento("alteraTotal", orcamento, condicao_selecionada, getDescricaoCondicaoAtual());


    }

    private BigDecimal getValorLiquidoItemOrcamento(ItemOrcamento item) {
        if (item == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.FLOOR);
        }

        return getPrecoLiquidoUnitarioParaCondicao(item, condicao_selecionada, getDescricaoCondicaoAtual())
                .multiply(BigDecimal.valueOf(item.getQtcomp()))
                .setScale(2, RoundingMode.FLOOR);
    }

    private double getPrecoUnitarioBrutoEnvio(ItemOrcamento item) {
        if (item == null) {
            return 0.0d;
        }
        return getPrecoUnitarioBrutoParaCondicao(item, condicao_selecionada, getDescricaoCondicaoAtual());
    }

    private Orcamento prepararOrcamentoParaEnvioComPrecoPromocional(Orcamento orcamentoOriginal) {
        if (orcamentoOriginal == null) {
            return null;
        }

        Orcamento orcamentoEnvio = new Gson().fromJson(new Gson().toJson(orcamentoOriginal), Orcamento.class);
        if (orcamentoEnvio == null || orcamentoEnvio.getItens() == null) {
            return orcamentoOriginal;
        }

        for (ItemOrcamento item : orcamentoEnvio.getItens()) {
            double precoBrutoBaseVista = getPrecoUnitarioBrutoEnvio(item);
            item.setPrecoUnit(precoBrutoBaseVista);
            item.setVlDescItem(getDescontoUnitarioEnvio(item, condicao_selecionada, getDescricaoCondicaoAtual()));
        }

        return orcamentoEnvio;
    }

    private double getDescontoUnitarioEnvio(ItemOrcamento item, String condicaoCodigo, String condicaoDescricao) {
        if (item == null) {
            return 0.0d;
        }

        if (!itemTemReajustePromocionalPrazo(item)) {
            return Math.max(0.0d, item.getVlDescItem());
        }

        double precoBrutoBaseVista = getPrecoUnitarioBrutoBaseVista(item);
        double precoLiquidoCondicao = getPrecoLiquidoUnitarioParaCondicao(item, condicaoCodigo, condicaoDescricao).doubleValue();
        return Math.max(0.0d, precoBrutoBaseVista - precoLiquidoCondicao);
    }

    private boolean temDescontoMotorPromocional(ItemOrcamento item) {
        if (item == null) {
            return false;
        }
        boolean isPR = item.getSituacao() != null && "PR".equalsIgnoreCase(item.getSituacao().trim());
        boolean isMotor = isMotorPromocional(item.getIsMotor());
        return isPR || isMotor;
    }

    private boolean itemTemReajustePromocionalPrazo(ItemOrcamento item) {
        return item != null && temDescontoMotorPromocional(item);
    }

    private boolean isMotorPromocional(String valor) {
        return possuiTexto(valor) && ("S".equalsIgnoreCase(valor.trim()) || "SIM".equalsIgnoreCase(valor.trim()));
    }

    private boolean possuiTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private double getPrecoUnitarioBrutoParaCondicao(ItemOrcamento item, String condicaoCodigo, String condicaoDescricao) {
        if (item == null) {
            return 0.0d;
        }

        double precoBrutoBaseVista = getPrecoUnitarioBrutoBaseVista(item);
        if (!itemTemReajustePromocionalPrazo(item) || !isCondicaoAPrazo(condicaoCodigo, condicaoDescricao)) {
            return precoBrutoBaseVista;
        }

        return precoBrutoBaseVista;
    }

    private BigDecimal getPrecoLiquidoUnitarioParaCondicao(ItemOrcamento item, String condicaoCodigo, String condicaoDescricao) {
        if (item == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal precoLiquidoBase = BigDecimal.valueOf(item.getPrecoUnit())
                .subtract(BigDecimal.valueOf(item.getVlDescItem()));
        if (deveAplicarRegraTotalDoAdapter(item, condicaoCodigo, condicaoDescricao)) {
            return precoLiquidoBase.divide(BigDecimal.valueOf(0.95d), 6, RoundingMode.HALF_EVEN);
        }

        return precoLiquidoBase;
    }

    private boolean deveAplicarRegraTotalDoAdapter(ItemOrcamento item, String condicaoCodigo, String condicaoDescricao) {
        return item != null
                && isCondicaoAPrazo(condicaoCodigo, condicaoDescricao)
                && itemTemReajustePromocionalPrazo(item);
    }

    private double getPrecoUnitarioBrutoBaseVista(ItemOrcamento item) {
        if (item == null) {
            return 0.0d;
        }
        return item.getPrecoUnit();
    }

    private double calcularPrecoUnitarioBrutoPromocionalPrazo(double precoBrutoBaseVista, ItemOrcamento item) {
        return ((precoBrutoBaseVista - item.getVlDescItem()) / 0.95d) + item.getVlDescItem();
    }

    private double reverterPrecoUnitarioBrutoPromocionalPrazo(double precoBrutoAtual, ItemOrcamento item) {
        return ((precoBrutoAtual - item.getVlDescItem()) * 0.95d) + item.getVlDescItem();
    }

    private boolean isOrcamentoOriginalAPrazo() {
        return isOrcamentoAPrazo(orcamento);
    }

    private Orcamento mesclarOrcamentoRecarregadoComEstadoLocal(Orcamento orcamentoServidor, Orcamento orcamentoLocal) {
        if (orcamentoServidor == null) {
            return orcamentoLocal;
        }

        if (orcamentoLocal == null) {
            return orcamentoServidor;
        }

        if (orcamentoServidor.getItens() == null || orcamentoServidor.getItens().isEmpty()) {
            orcamentoServidor.setItens(orcamentoLocal.getItens());
        } else {
            copiarMarcadoresPromocionaisItens(orcamentoServidor, orcamentoLocal);
        }

        if (orcamentoServidor.getCliente() == null) {
            orcamentoServidor.setCliente(orcamentoLocal.getCliente());
        }

        boolean servidorPrazo = isCondicaoAPrazo(
                orcamentoServidor.getCondPagamento() != null ? orcamentoServidor.getCondPagamento().getCondpgto() : null,
                orcamentoServidor.getCondPagamento() != null ? orcamentoServidor.getCondPagamento().getDescricao() : null
        );
        boolean localPrazo = isCondicaoAPrazo(
                orcamentoLocal.getCondPagamento() != null ? orcamentoLocal.getCondPagamento().getCondpgto() : null,
                orcamentoLocal.getCondPagamento() != null ? orcamentoLocal.getCondPagamento().getDescricao() : null
        );

        if (!servidorPrazo && localPrazo) {
            orcamentoServidor.setCondPagamento(orcamentoLocal.getCondPagamento());
            orcamentoServidor.setQtprc(orcamentoLocal.getQtprc());
            orcamentoServidor.setVlPrc(orcamentoLocal.getVlPrc());
            orcamentoServidor.setVlEntrada(orcamentoLocal.getVlEntrada());
        } else if (orcamentoServidor.getCondPagamento() == null) {
            orcamentoServidor.setCondPagamento(orcamentoLocal.getCondPagamento());
        }

        if (orcamentoServidor.getVlTotal() <= 0d && orcamentoLocal.getVlTotal() > 0d) {
            orcamentoServidor.setVlTotal(orcamentoLocal.getVlTotal());
        }

        if (orcamentoServidor.getVlDesconto() <= 0d && orcamentoLocal.getVlDesconto() > 0d) {
            orcamentoServidor.setVlDesconto(orcamentoLocal.getVlDesconto());
        }

        return orcamentoServidor;
    }

    private void copiarMarcadoresPromocionaisItens(Orcamento orcamentoDestino, Orcamento orcamentoOrigem) {
        if (orcamentoDestino == null || orcamentoDestino.getItens() == null
                || orcamentoOrigem == null || orcamentoOrigem.getItens() == null) {
            return;
        }

        for (ItemOrcamento itemDestino : orcamentoDestino.getItens()) {
            ItemOrcamento itemOrigem = localizarItemCorrespondente(orcamentoOrigem.getItens(), itemDestino);
            if (itemDestino == null || itemOrigem == null) {
                continue;
            }

            itemDestino.setIsMotor(itemOrigem.getIsMotor());
            itemDestino.setCodigoPromo(itemOrigem.getCodigoPromo());
            itemDestino.setNomePromo(itemOrigem.getNomePromo());
            itemDestino.setMensagemPromo(itemOrigem.getMensagemPromo());
            itemDestino.setVldescPromo(itemOrigem.getVldescPromo());
            itemDestino.setPrecoUnit(itemOrigem.getPrecoUnit());
            itemDestino.setVlDescItem(itemOrigem.getVlDescItem());
            itemDestino.setDpi(itemOrigem.getDpi());

            if ((itemDestino.getSituacao() == null || itemDestino.getSituacao().trim().isEmpty())
                    && itemOrigem.getSituacao() != null) {
                itemDestino.setSituacao(itemOrigem.getSituacao());
            }
        }
    }

    private ItemOrcamento localizarItemCorrespondente(List<ItemOrcamento> itensOrigem, ItemOrcamento itemDestino) {
        if (itensOrigem == null || itemDestino == null) {
            return null;
        }

        for (ItemOrcamento itemOrigem : itensOrigem) {
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

    private void salvarCacheMarcadoresPromocionais(Orcamento orcamentoAtual) {
        if (orcamentoAtual == null || orcamentoAtual.getNumorc() <= 0 || orcamentoAtual.getItens() == null) {
            return;
        }

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("orcamentoPromocaoPrazo", Context.MODE_PRIVATE);
        preferences.edit()
                .putString("itens_" + (long) orcamentoAtual.getNumorc(), new Gson().toJson(orcamentoAtual.getItens()))
                .apply();
    }

    private String getDescricaoCondicaoAtual() {
        try {
            if (orcamento != null && orcamento.getCondPagamento() != null && orcamento.getCondPagamento().getDescricao() != null) {
                return orcamento.getCondPagamento().getDescricao();
            }
        } catch (Exception ignore) {
        }

        try {
            if (edtcondpag != null && edtcondpag.getText() != null) {
                return edtcondpag.getText().toString();
            }
        } catch (Exception ignore) {
        }

        return "";
    }

    private boolean isCondicaoAPrazo(String condicaoCodigo, String condicaoDescricao) {
        if (condicaoDescricao != null) {
            String descricaoNormalizada = condicaoDescricao.toUpperCase().trim();
            if (descricaoNormalizada.contains("A VISTA") || descricaoNormalizada.contains("AVISTA")) {
                return false;
            }
            if (descricaoNormalizada.contains("A PRAZO")) {
                return true;
            }
        }

        if (condicaoCodigo != null && !condicaoCodigo.trim().isEmpty()) {
            if ("N".equalsIgnoreCase(condicaoCodigo.trim())) {
                return false;
            }
            return !"01".equals(condicaoCodigo.trim());
        }

        if (orcamento != null && orcamento.getQtprc() <= 1 && orcamento.getVlEntrada() <= 0d) {
            return false;
        }

        return false;
    }

    private void tentarExibirAvisoPromocionalAPrazoAoAbrir() {
        if (avisoPromocionalPrazoExibidoAoAbrir || orcamento == null) {
            return;
        }

        String condicaoDescricaoAtual = getDescricaoCondicaoAtual();
        if (!isCondicaoAPrazo(condicao_selecionada, condicaoDescricaoAtual)) {
            return;
        }

        String itensPromocionais = montarListaItensPromocionais(orcamento);
        if (itensPromocionais.isEmpty()) {
            return;
        }

        avisoPromocionalPrazoExibidoAoAbrir = true;
        new AlertDialog.Builder(PopupOrcFechamento.this)
                .setTitle("Itens com reajuste")
                .setMessage("Atenção: Os itens promocionados terão reajuste por conta da condição A PRAZO.\n\n" + itensPromocionais)
                .setPositiveButton("OK", null)
                .show();
    }

    private void logDiagnosticoPromocaoPrazoOrcamento(String origem, Orcamento orcamentoRef, String condicaoCodigo, String condicaoDescricao) {
        if (orcamentoRef == null || orcamentoRef.getItens() == null) {
            Log.d("PromoAPrazoOrcDebug", origem + " | orcamento nulo ou sem itens");
            return;
        }

        boolean condicaoAPrazo = isCondicaoAPrazo(condicaoCodigo, condicaoDescricao);
        double totalAtualItens = 0.0d;
        double totalEsperadoItens = 0.0d;
        int qtdItensPromocionais = 0;
        StringBuilder itensPromocionais = new StringBuilder();

        for (ItemOrcamento item : orcamentoRef.getItens()) {
            double valorLiquidoAtual = (item.getPrecoUnit() - item.getVlDescItem()) * item.getQtcomp();
            double valorMotor095 = valorLiquidoAtual;

            if (itemTemReajustePromocionalPrazo(item)) {
                qtdItensPromocionais++;
                if (condicaoAPrazo) {
                    valorMotor095 = ((item.getPrecoUnit() - item.getVlDescItem()) / 0.95d) * item.getQtcomp();
                }
                itensPromocionais.append("[cod=").append(item.getCoditprod())
                        .append(", qtd=").append(item.getQtcomp())
                        .append(", sit=").append(item.getSituacao())
                        .append(", atual=").append(String.format(Locale.US, "%.2f", valorLiquidoAtual))
                        .append(", motor095=").append(String.format(Locale.US, "%.2f", valorMotor095))
                        .append("] ");
            }

            totalAtualItens += valorLiquidoAtual;
            totalEsperadoItens += condicaoAPrazo && itemTemReajustePromocionalPrazo(item) ? valorMotor095 : valorLiquidoAtual;
        }

        Log.d("PromoAPrazoOrcDebug",
                origem
                        + " | orcamento=" + orcamentoRef.getNumorc()
                        + " | condCodigo=" + condicaoCodigo
                        + " | condDesc=" + condicaoDescricao
                        + " | isAPrazo=" + condicaoAPrazo
                        + " | itensMotorReajuste=" + qtdItensPromocionais
                        + " | totalItensAtual=" + String.format(Locale.US, "%.2f", totalAtualItens)
                        + " | totalItensEsperado=" + String.format(Locale.US, "%.2f", totalEsperadoItens)
                        + " | vlTotalOrcamento=" + String.format(Locale.US, "%.2f", orcamentoRef.getVlTotal()));

        if (qtdItensPromocionais > 0) {
            Log.d("PromoAPrazoOrcDebug", origem + " | detalhes_itens_motor=" + itensPromocionais);
        }
    }

    private void exibirAvisoReajustePromocionalOrcamento(String condicaoCodigo, String condicaoDescricao, Orcamento orcamentoRef) {
        if (!isCondicaoAPrazo(condicaoCodigo, condicaoDescricao) || orcamentoRef == null || orcamentoRef.getItens() == null) {
            return;
        }

        String itensPromocionais = montarListaItensPromocionais(orcamentoRef);

        if (itensPromocionais.isEmpty()) {
            return;
        }

        new AlertDialog.Builder(PopupOrcFechamento.this)
                .setTitle("Itens com reajuste")
                .setMessage("Atenção: Os itens promocionados terão reajuste por conta da condição A PRAZO.\n\n" + itensPromocionais)
                .setPositiveButton("OK", null)
                .show();
    }

    private String montarListaItensPromocionais(Orcamento orcamentoRef) {
        if (orcamentoRef == null || orcamentoRef.getItens() == null) {
            return "";
        }

        StringBuilder itensPromocionais = new StringBuilder();
        for (ItemOrcamento item : orcamentoRef.getItens()) {
            if (itemTemReajustePromocionalPrazo(item)) {
                if (itensPromocionais.length() > 0) {
                    itensPromocionais.append("\n");
                }
                itensPromocionais.append("- ")
                        .append(item.getCoditprod())
                        .append(" - ")
                        .append(item.getDescricao());
            }
        }

        return itensPromocionais.toString();
    }



    public Boolean validarParcelas() {

        if (edtQtdParcelas.getText() == null || edtQtdParcelas.getText().toString().isEmpty()) {
            layout_qtdParcelas.setErrorEnabled(true);
            layout_qtdParcelas.setError("Informe a qtd. de parcelas");
            return false;

        } else {
            layout_qtdParcelas.setErrorEnabled(false);
            return true;
        }
    }

    public Boolean validarQtdParcelas() {

        double parcelas = Double.parseDouble(edtQtdParcelas.getText().toString());
        if (parcelas > 10) {
            layout_qtdParcelas.setErrorEnabled(true);
            layout_qtdParcelas.setError("Número maximo de parcelas é 10");
            edtQtdParcelas.setText("10");
            return false;

        } else {
            layout_qtdParcelas.setErrorEnabled(false);
            return true;
        }


    }

    public Boolean validarParcelaZero() {

        double parcelas = Double.parseDouble(edtQtdParcelas.getText().toString());
        if (parcelas == 0) {
            layout_qtdParcelas.setErrorEnabled(true);
            layout_qtdParcelas.setError("Informe o numero de parcelas");
            edtQtdParcelas.setText("");
            return false;

        } else {
            layout_qtdParcelas.setErrorEnabled(false);
            return true;
        }


    }

    public Boolean validarEntrada() {

        if (edtEntrada.getText() != null && edtEntrada.getText().toString().isEmpty() && !temEntrada.equals("S") && !condicao_selecionada.equals("01")) {
            layout_entrada.setErrorEnabled(true);
            layout_entrada.setError("Informe o valor da entrada");
            return false;

        } else {
            layout_entrada.setErrorEnabled(false);
            return true;
        }
    }


    public Boolean validarCondicao() {


        if (condicao_selecionada.equals("N")) {

            layout_condicao.setError("Informe a condição de pagamento");
            return false;

        } else {
            layout_condicao.setError(null);
            return true;
        }
    }


    public Boolean validarFase() {


        if (fase_selecionada.equals("N")) {

            layout_fase.setError("Informe a fase da obra");
            return false;

        } else {
            layout_fase.setError(null);
            return true;
        }
    }

    public Boolean validarDescontoFrete() {
        if (switchRetira != null && switchRetira.isChecked()) {
            if (layout_frete != null) {
                layout_frete.setError(null);
            }
            return true;
        }

        if (freteOrig <= 0.0d) {
            if (layout_frete != null) {
                layout_frete.setError(null);
            }
            return true;
        }

        double percentualMaximo = getPercentualMaximoDescontoFrete();
        double freteAtual = parseFreteInput();
        double percCalculado = ((freteOrig - freteAtual) / freteOrig) * 100;

        if (percCalculado > percentualMaximo) {
            if (layout_frete != null) {
                layout_frete.setError("Desconto do frete superior a " + df.format(percentualMaximo) + "%");
            }
            edtFrete.setText(df.format(freteOrig));
            return false;
        }

        if (layout_frete != null) {
            layout_frete.setError(null);
        }
        return true;
    }

    public Boolean validarAcrescimoFrete() {
        if (freteOrig <= 0.0d) {
            if (layout_frete != null) {
                layout_frete.setError(null);
            }
            return true;
        }

        double percCalculado = (parseFreteInput() / freteOrig) * 100;
        if (percCalculado > 130) {
            if (layout_frete != null) {
                layout_frete.setError("Acrescimo do frete superior a 30%");
            }
            edtFrete.setText(df.format(freteOrig));
            return false;
        }

        if (layout_frete != null) {
            layout_frete.setError(null);
        }
        return true;
    }


}

