package br.com.acalfortaleza.fikonmais;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.app.AlertDialog;
import android.widget.TextView; // Re-adicionado
import android.util.Log;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.graphics.Color;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;

import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.models.Metas.BonusArrecadeFrete;
import br.com.acalfortaleza.fikonmais.models.Metas.BonusLinhasPositivadas;
import br.com.acalfortaleza.fikonmais.models.Metas.BonusVendaAvista;
import br.com.acalfortaleza.fikonmais.models.Metas.ComissaoVariavelProduto;
import br.com.acalfortaleza.fikonmais.models.Metas.MetasConsultor;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MetasActivity extends BaseAcalActivity {

    private EditText edtDataIni, edtDataFim;
    private Button btnBuscar, btnProximo, btnAnterior;
    private Button btnFaturamentoLinha;
    private ImageView btnDetalhesMeta;
    private TableLayout tabelaMetas;
    private ViewFlipper viewFlipper;
    
    // Objeto para armazenar detalhes da meta (vindo da API)
    private ComissaoVariavelProduto detalhesMetaAtual;

    // Loading da Matriz
    private android.widget.LinearLayout layoutLoadingMatriz;
    private android.widget.HorizontalScrollView scrollMatriz;

    // Loading da Tabela Principal
    private android.widget.LinearLayout layoutLoadingTabela;
    private android.widget.HorizontalScrollView scrollTabela;
    
    // Totais do Cabeçalho
    private TextView txtTotalVendaLiquida, txtTotalMargem;
    private TextView txtTotalComissao, txtTotalPremiacao, txtTotalPremiacaoAcelerados;

    // Campos da tabela
    private TextView txtMetaAvista, txtRealizadoAvista, txtGanhoAvista;
    private TextView txtMetaLinhas, txtRealizadoLinhas, txtGanhoLinhas;
    private TextView txtMetaSku, txtRealizadoSku, txtGanhoSku;
    private TextView txtMetaFrete, txtRealizadoFrete, txtGanhoFrete;
    private TextView txtMetaFaturamento, txtRealizadoFaturamento, txtGanhoFaturamento;
    private TextView txtInfoGatilho;
    
    // Matriz 3x3
    private TextView cell_34_200k, cell_34_250k, cell_34_300k;
    private TextView cell_33_200k, cell_33_250k, cell_33_300k;
    private TextView cell_32_200k, cell_32_250k, cell_32_300k;
    private TextView lblRow34, lblRow33, lblRow32;
    private TextView footer200k, footer250k, footer300k;
    private View viewDimVendaDia;
    private LinearLayout painelVendaDia;
    private LinearLayout etiquetaVendaDia;
    private LinearLayout layoutLoadingVendaDiaOverlay;
    private TextView txtVendaLiquidaDiaOverlay;
    private TextView txtStatusVendaDiaOverlay;
    private TextView txtDataVendaDiaOverlay;
    private ImageView imgEtiquetaVendaDia;
    private ScrollView metasScroll;

    // Variável global para armazenar a Venda Líquida e controlar regras de ganho
    private double vendaLiquidaGlobal = 0;
    
    // Variáveis para somar a premiação total
    private double premioAvista = 0;
    private double premioLinhas = 0;
    private double premioSku = 0;
    private double premioFrete = 0;
    private double premioMatriz = 0;
    
    // Regra: Mínimo de 120 mil em Venda Líquida para ganhar bônus (R$ 200,00)
    private static final double META_MINIMA_VENDA_LIQUIDA = 120000.0;
    private static final double VALOR_BONUS_PADRAO = 200.00;
    private static final int META_LINHAS_POSITIVADAS = 14;
    private static final int META_LINHAS_POSITIVADAS_FILIAL_900 = 10;
    private static final double GATILHO_FRETE_VENDIDO = 3000.0;
    private static final double GATILHO_FRETE_VENDIDO_FILIAL_900 = 2000.0;

    // Variáveis para armazenar as metas vindas da API
    private double metaAvistaVal = 45.0; // Padrão caso falhe
    private int metaLinhasVal = META_LINHAS_POSITIVADAS;
    private double metaSkuVal = 3.0;
    private double metaFreteVal = 80.0;
    private boolean metaCarregada = false;

    private Calendar calendarIni = Calendar.getInstance();
    private Calendar calendarFim = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    // "Padrao americano" para a API: yyyy-MM-dd
    private SimpleDateFormat sdfApi = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private boolean overlayVendaDiaAberto = false;
    private boolean vendaDiaCarregada = false;
    private float overlayClosedTranslationX;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.metas_status_bar_bg);
        if (statusBarBg == null) {
            return;
        }

        ViewGroup.LayoutParams params = statusBarBg.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBarBg.setLayoutParams(params);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_metas);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.metas_toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Metas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        inicializarComponentes();
        configurarEventos();
        definirDatasPadrao();
    }

    private void inicializarComponentes() {
        metasScroll = findViewById(R.id.metas_scroll);
        edtDataIni = findViewById(R.id.edtDataIni);
        edtDataFim = findViewById(R.id.edtDataFim);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnProximo = findViewById(R.id.btnProximo);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnFaturamentoLinha = findViewById(R.id.btnFaturamentoLinha);
        btnDetalhesMeta = findViewById(R.id.btnDetalhesMeta);
        tabelaMetas = findViewById(R.id.tabelaMetas);
        viewFlipper = findViewById(R.id.viewFlipper);
        
        layoutLoadingMatriz = findViewById(R.id.layoutLoadingMatriz);
        scrollMatriz = findViewById(R.id.scrollMatriz);
        
        layoutLoadingTabela = findViewById(R.id.layoutLoadingTabela);
        scrollTabela = findViewById(R.id.scrollTabela);
        
        txtTotalVendaLiquida = findViewById(R.id.txtTotalVendaLiquida);
        txtTotalMargem = findViewById(R.id.txtTotalMargem);
        txtTotalComissao = findViewById(R.id.txtTotalComissao);
        txtTotalPremiacao = findViewById(R.id.txtTotalPremiacao);
        txtTotalPremiacaoAcelerados = findViewById(R.id.txtTotalPremiacaoAcelerados);

        txtMetaAvista = findViewById(R.id.txtMetaAvista);
        txtRealizadoAvista = findViewById(R.id.txtRealizadoAvista);
        txtGanhoAvista = findViewById(R.id.txtGanhoAvista);
        
        txtMetaLinhas = findViewById(R.id.txtMetaLinhas);
        txtRealizadoLinhas = findViewById(R.id.txtRealizadoLinhas);
        txtGanhoLinhas = findViewById(R.id.txtGanhoLinhas);
        
        txtMetaSku = findViewById(R.id.txtMetaSku);
        txtRealizadoSku = findViewById(R.id.txtRealizadoSku);
        txtGanhoSku = findViewById(R.id.txtGanhoSku);
        
        txtMetaFrete = findViewById(R.id.txtMetaFrete);
        txtRealizadoFrete = findViewById(R.id.txtRealizadoFrete);
        txtGanhoFrete = findViewById(R.id.txtGanhoFrete);
        
        txtMetaFaturamento = findViewById(R.id.txtMetaFaturamento);
        txtRealizadoFaturamento = findViewById(R.id.txtRealizadoFaturamento);
        txtGanhoFaturamento = findViewById(R.id.txtGanhoFaturamento);
        txtInfoGatilho = findViewById(R.id.txtInfoGatilho);
        
        // Mapear células da matriz
        cell_34_200k = findViewById(R.id.cell_34_200k);
        cell_34_250k = findViewById(R.id.cell_34_250k);
        cell_34_300k = findViewById(R.id.cell_34_300k);
        
        cell_33_200k = findViewById(R.id.cell_33_200k);
        cell_33_250k = findViewById(R.id.cell_33_250k);
        cell_33_300k = findViewById(R.id.cell_33_300k);
        
        cell_32_200k = findViewById(R.id.cell_32_200k);
        cell_32_250k = findViewById(R.id.cell_32_250k);
        cell_32_300k = findViewById(R.id.cell_32_300k);
        lblRow34 = findViewById(R.id.lbl_row_34);
        lblRow33 = findViewById(R.id.lbl_row_33);
        lblRow32 = findViewById(R.id.lbl_row_32);
        footer200k = findViewById(R.id.footer_200k);
        footer250k = findViewById(R.id.footer_250k);
        footer300k = findViewById(R.id.footer_300k);
        configurarInsetInferiorScroll();
        // Overlay lateral "HOJE" desativado por ora.
        // Mantemos as views no layout para facilitar a reativacao futura.
//        viewDimVendaDia = findViewById(R.id.viewDimVendaDia);
//        painelVendaDia = findViewById(R.id.painelVendaDia);
//        etiquetaVendaDia = findViewById(R.id.etiquetaVendaDia);
//        layoutLoadingVendaDiaOverlay = findViewById(R.id.layoutLoadingVendaDiaOverlay);
//        txtVendaLiquidaDiaOverlay = findViewById(R.id.txtVendaLiquidaDiaOverlay);
//        txtStatusVendaDiaOverlay = findViewById(R.id.txtStatusVendaDiaOverlay);
//        txtDataVendaDiaOverlay = findViewById(R.id.txtDataVendaDiaOverlay);
//        imgEtiquetaVendaDia = findViewById(R.id.imgEtiquetaVendaDia);
//
//        txtDataVendaDiaOverlay.setText("Hoje, " + sdf.format(Calendar.getInstance().getTime()));
//
//        painelVendaDia.post(() -> {
//            int larguraEtiqueta = etiquetaVendaDia.getWidth() > 0 ? etiquetaVendaDia.getWidth() : dpToPxInt(22);
//            overlayClosedTranslationX = -(painelVendaDia.getWidth() - larguraEtiqueta);
//            painelVendaDia.setTranslationX(overlayClosedTranslationX);
//        });
    }

    private void configurarInsetInferiorScroll() {
        if (metasScroll == null) {
            return;
        }

        final int paddingBottomBase = metasScroll.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(metasScroll, (view, insets) -> {
            androidx.core.graphics.Insets navigationInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    paddingBottomBase + navigationInsets.bottom + dpToPx(24)
            );
            return insets;
        });
        ViewCompat.requestApplyInsets(metasScroll);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void configurarEventos() {
        edtDataIni.setOnClickListener(v -> mostrarDatePicker(true));
        edtDataFim.setOnClickListener(v -> mostrarDatePicker(false));
        btnBuscar.setOnClickListener(v -> buscarMetas());
        btnDetalhesMeta.setOnClickListener(v -> mostrarDetalhesMeta());
        
        btnProximo.setOnClickListener(v -> {
            viewFlipper.setInAnimation(this, R.anim.slide_in_right);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_left);
            viewFlipper.showNext();
        });

        btnAnterior.setOnClickListener(v -> {
            viewFlipper.setInAnimation(this, R.anim.slide_in_left);
            viewFlipper.setOutAnimation(this, R.anim.slide_out_right);
            viewFlipper.showPrevious();
        });
        
        btnFaturamentoLinha.setOnClickListener(v -> {
            br.com.acalfortaleza.fikonmais.ConsultaFaturamentoActivity.periodoIni = sdfApi.format(calendarIni.getTime());
            br.com.acalfortaleza.fikonmais.ConsultaFaturamentoActivity.periodoFim = sdfApi.format(calendarFim.getTime());
            Intent intent = new Intent(MetasActivity.this, br.com.acalfortaleza.fikonmais.ConsultaFaturamentoLinhaActivity.class);
            startActivity(intent);
        });

        // Overlay lateral "HOJE" desativado por ora.
//        viewDimVendaDia.setOnClickListener(v -> fecharOverlayVendaDia());
//        View.OnTouchListener overlayDragListener = new View.OnTouchListener() {
//            private float downRawX;
//            private float startTranslationX;
//            private boolean arrastou;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getActionMasked()) {
//                    case MotionEvent.ACTION_DOWN:
//                        // Garante que o dim exista enquanto o usuário "puxa" a etiqueta/painel
//                        if (viewDimVendaDia.getVisibility() != View.VISIBLE) {
//                            viewDimVendaDia.setVisibility(View.VISIBLE);
//                        }
//                        downRawX = event.getRawX();
//                        startTranslationX = painelVendaDia.getTranslationX();
//                        arrastou = false;
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        float delta = event.getRawX() - downRawX;
//                        if (Math.abs(delta) > dpToPx(6)) {
//                            arrastou = true;
//                        }
//                        float destino = startTranslationX + delta;
//                        destino = Math.max(overlayClosedTranslationX, Math.min(0f, destino));
//                        painelVendaDia.setTranslationX(destino);
//                        atualizarDimensaoOverlay(destino);
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        if (!arrastou) {
//                            alternarOverlayVendaDia();
//                            return true;
//                        }
//                        float metade = overlayClosedTranslationX / 2f;
//                        if (painelVendaDia.getTranslationX() > metade) {
//                            abrirOverlayVendaDia();
//                        } else {
//                            fecharOverlayVendaDia();
//                        }
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        };
//
//        // Permite "puxar" tanto pelo painel quanto pela etiqueta (a etiqueta é clickable e absorve touch)
//        painelVendaDia.setOnTouchListener(overlayDragListener);
//        etiquetaVendaDia.setOnTouchListener(overlayDragListener);
    }

    private void definirDatasPadrao() {
        Calendar agora = Calendar.getInstance();
        calendarIni.set(Calendar.YEAR, agora.get(Calendar.YEAR));
        calendarIni.set(Calendar.MONTH, agora.get(Calendar.MONTH));
        calendarIni.set(Calendar.DAY_OF_MONTH, 1);
        edtDataIni.setText(sdf.format(calendarIni.getTime()));
    }

    private void mostrarDatePicker(boolean isInicial) {
        Calendar cal = isInicial ? calendarIni : calendarFim;
        
        DatePickerDialog dpd = DatePickerDialog.newInstance(
            (view, year, monthOfYear, dayOfMonth) -> {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, monthOfYear);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                if (isInicial) {
                    edtDataIni.setText(sdf.format(cal.getTime()));
                } else {
                    edtDataFim.setText(sdf.format(cal.getTime()));
                }
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        );
        
        Calendar min = Calendar.getInstance();
        min.set(Calendar.YEAR, 2026);
        min.set(Calendar.MONTH, Calendar.JANUARY);
        min.set(Calendar.DAY_OF_MONTH, 1);
        dpd.setMinDate(min);
        
        if (isInicial) {
            // Habilitar apenas o dia 1 de cada mês (Regra de Negócio)
            // Gerar dias permitidos (ex: últimos 5 anos e próximos 5 anos)
            Calendar[] allowedDays = getDiasPermitidos();
            dpd.setSelectableDays(allowedDays);
            dpd.setTitle("Selecione o dia 01");
        }
        
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }

    private Calendar[] getDiasPermitidos() {
        List<Calendar> daysList = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2026);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        for (int i = 0; i < 120; i++) {
            Calendar c = (Calendar) cal.clone();
            daysList.add(c);
            cal.add(Calendar.MONTH, 1);
        }
        return daysList.toArray(new Calendar[0]);
    }

    private void mostrarDetalhesMeta() {
        if (detalhesMetaAtual == null) {
            Toast.makeText(this, "Realize uma consulta primeiro.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalhes da Meta");

        // Layout personalizado para o popup
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_detalhes_meta, null);
        
        TextView txtVendas = view.findViewById(R.id.txtVendas);
        TextView txtDevolucoes = view.findViewById(R.id.txtDevolucoes);
        TextView txtVendaLiquida = view.findViewById(R.id.txtVendaLiquida);
        TextView txtDiasUteis = view.findViewById(R.id.txtDiasUteis);
        
        // Formatar valores
        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
        String vendasStr = real.format(detalhesMetaAtual.Vendas != null ? detalhesMetaAtual.Vendas : 0.0);
        String devolucoesStr = real.format(detalhesMetaAtual.Devolucoes != null ? detalhesMetaAtual.Devolucoes : 0.0);
        String vendaLiquidaStr = real.format(detalhesMetaAtual.VendaLiquida != null ? detalhesMetaAtual.VendaLiquida : 0.0);
        
        txtVendas.setText("Vendas: " + vendasStr);
        txtDevolucoes.setText("Devoluções: " + devolucoesStr);
        txtVendaLiquida.setText("Venda Líquida: " + vendaLiquidaStr);
        txtDiasUteis.setText("Dias úteis no período: " + (detalhesMetaAtual.DiasUteis != null ? detalhesMetaAtual.DiasUteis : 0));
        
        builder.setView(view);
        builder.setPositiveButton("Fechar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void carregarMetasConsultor(String token) {
        ApiGemco api = getApiService();
        Call<MetasConsultor> call = api.GetMetasConsultor(token);
        call.enqueue(new Callback<MetasConsultor>() {
            @Override
            public void onResponse(Call<MetasConsultor> call, Response<MetasConsultor> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MetasConsultor metas = response.body();
                    
                    metaAvistaVal = metas.PagAvista;
                    metaLinhasVal = getMetaLinhasPositivadas();
                    metaSkuVal = metas.SkuPorVenda;
                    metaFreteVal = metas.FreteVendido;
                    metaCarregada = true;

                    // Regras específicas para filial 900
                    if (isFilial900()) {
                        metaAvistaVal = 50.0;
                        metaLinhasVal = getMetaLinhasPositivadas();
                        metaSkuVal = 2.2;
                        metaFreteVal = 80.0;
                        aplicarTextosBaseMatrizFilial(true);
                    } else {
                        // Meta SKU geral: 3,00
                        metaSkuVal = 3.0;
                        aplicarTextosBaseMatrizFilial(false);
                    }

                    // Atualiza a UI
                    txtMetaAvista.setText(String.format(Locale.getDefault(), "%.0f%%", metaAvistaVal));
                    txtMetaLinhas.setText(String.valueOf(metaLinhasVal));
                    txtMetaSku.setText(String.format(new Locale("pt","BR"), "%.2f", metaSkuVal));
                    if (isFilial900()) {
                        NumberFormat realFmt = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                        txtMetaFrete.setText(String.format(Locale.getDefault(), "%.0f%% | %s", metaFreteVal, realFmt.format(getGatilhoFreteVendido())));
                    } else {
                        NumberFormat realFmt = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                        txtMetaFrete.setText(String.format(Locale.getDefault(), "%.0f%% | %s", metaFreteVal, realFmt.format(getGatilhoFreteVendido())));
                    }
                    
                } else {
                    definirMetaAguardando();
                }
            }

            @Override
            public void onFailure(Call<MetasConsultor> call, Throwable t) {
                definirMetaAguardando();
            }
        });
    }

    private void definirMetaAguardando() {
        metaCarregada = false;
        String aguardando = "Aguardando Meta";
        txtMetaAvista.setText(aguardando);
        txtMetaLinhas.setText(aguardando);
        txtMetaSku.setText(aguardando);
        txtMetaFrete.setText(aguardando);
    }

    private void buscarMetas() {
        if (edtDataIni.getText().toString().isEmpty() || edtDataFim.getText().toString().isEmpty()) {
            Toast.makeText(this, "Selecione o período completo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ajuste: Pegar do SharedPreferences via LoginHelper
        LoginHelper loginHelper = new LoginHelper(this);
        String codConsultorStr = loginHelper.GetUsuarioSalvo().get("codigo");
        
        Log.d("METAS_DEBUG", "Consultor recuperado da sessao: " + codConsultorStr);

        if (codConsultorStr == null || codConsultorStr.equals("0")) {
            Toast.makeText(this, "Erro ao identificar consultor logado", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int codConsultor = Integer.parseInt(codConsultorStr);
        String dataIniApi = sdfApi.format(calendarIni.getTime());
        String dataFimApi = sdfApi.format(calendarFim.getTime());
        String token = "Bearer " + ApiGemco.token; // Token estático definido na ApiGemco
        
        Log.d("METAS_DEBUG", "Iniciando busca. DataIni: " + dataIniApi + ", DataFim: " + dataFimApi + ", Consultor: " + codConsultor);

        // Resetar Venda Líquida Global antes de buscar
        vendaLiquidaGlobal = 0;
        
        // Resetar prêmios
        premioAvista = 0;
        premioLinhas = 0;
        premioSku = 0;
        premioFrete = 0;
        premioMatriz = 0;
        
        // Exibir Loading da Tabela Principal e Esconder Scroll
        tabelaMetas.setVisibility(View.GONE); // Garantir que a tabela esteja oculta
        scrollTabela.setVisibility(View.GONE);
        layoutLoadingTabela.setVisibility(View.VISIBLE);

        // Carregar as metas antes de processar os cálculos
        carregarMetasConsultor(token);

        // Primeiro carregar ComissaoVariavel para obter a Venda Liquida
        // As outras chamadas dependem desse valor para calcular o ganho, então idealmente deveriam esperar.
        // Como o Retrofit é assincrono, vamos chamar ComissaoVariavel PRIMEIRO e dentro do sucesso dela chamar as outras.
        carregarComissaoVariavel(dataIniApi, dataFimApi, codConsultor, token);
    }

    private void alternarOverlayVendaDia() {
        if (overlayVendaDiaAberto) {
            fecharOverlayVendaDia();
        } else {
            abrirOverlayVendaDia();
        }
    }

    private void abrirOverlayVendaDia() {
        overlayVendaDiaAberto = true;
        viewDimVendaDia.setVisibility(View.VISIBLE);
        animarOverlayVendaDia(0f);
        imgEtiquetaVendaDia.setImageResource(R.drawable.ic__arrow_left);
        if (!vendaDiaCarregada) {
            carregarVendaLiquidaDiaAtual();
        }
    }

    private void fecharOverlayVendaDia() {
        overlayVendaDiaAberto = false;
        animarOverlayVendaDia(overlayClosedTranslationX);
        imgEtiquetaVendaDia.setImageResource(R.drawable.ic_arrow_right);
    }

    private void animarOverlayVendaDia(float destino) {
        painelVendaDia.animate()
                .translationX(destino)
                .setDuration(220)
                .withEndAction(() -> {
                    if (!overlayVendaDiaAberto && painelVendaDia.getTranslationX() <= overlayClosedTranslationX + 1f) {
                        viewDimVendaDia.setVisibility(View.GONE);
                    }
                    atualizarDimensaoOverlay(painelVendaDia.getTranslationX());
                })
                .start();
    }

    private void atualizarDimensaoOverlay(float translationX) {
        if (painelVendaDia.getWidth() <= 0) {
            return;
        }

        float abertura = 1f - (Math.abs(translationX) / Math.abs(overlayClosedTranslationX));
        abertura = Math.max(0f, Math.min(1f, abertura));
        viewDimVendaDia.setAlpha(abertura * 0.55f);
    }

    private void carregarVendaLiquidaDiaAtual() {
        LoginHelper loginHelper = new LoginHelper(this);
        java.util.HashMap<String, String> usuarioLogado = loginHelper.GetUsuarioSalvo();
        String cpf = usuarioLogado.get("cpf");
        String filial = usuarioLogado.get("filial");
        String codConsultorStr = usuarioLogado.get("codigo");
        String token = "Bearer " + ApiGemco.token;

        if (cpf == null || filial == null || codConsultorStr == null || codConsultorStr.trim().equals("0")) {
            txtStatusVendaDiaOverlay.setText("Nao foi possivel identificar o consultor logado.");
            return;
        }

        layoutLoadingVendaDiaOverlay.setVisibility(View.VISIBLE);
        txtStatusVendaDiaOverlay.setText("Buscando venda liquida do dia atual...");

        ApiGemco api = getApiService();
        int codConsultor = Integer.parseInt(codConsultorStr.trim());

        // IMPORTANTE: para o overlay HOJE, usar o endpoint de Consultores/Acumulado (Cpf + Filial),
        // pois ele ja retorna a VendaLiquida no modelo Consultor.
        Log.d("METAS_DEBUG", "Overlay HOJE - Consultores/Acumulado | Cpf: " + cpf + " | Filial: " + filial + " | Consultor: " + codConsultor);
        Call<List<Consultor>> call = api.GetConsultorAcumulado(cpf, filial, token);
        Log.d("METAS_DEBUG", "Overlay HOJE - URL: " + call.request().url());

        call.enqueue(new Callback<List<Consultor>>() {
            @Override
            public void onResponse(Call<List<Consultor>> call, Response<List<Consultor>> response) {
                layoutLoadingVendaDiaOverlay.setVisibility(View.GONE);
                Log.d("METAS_DEBUG", "Overlay HOJE - HTTP " + response.code() + " | success=" + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    String json = new Gson().toJson(response.body());
                    Log.d("METAS_DEBUG", "Overlay HOJE - Response JSON: " + limitarLog(json));

                    Consultor consultor = response.body().get(0);
                    double vendaLiquidaDia = consultor.VendaLiquida != null ? consultor.VendaLiquida : 0.0;
                    txtVendaLiquidaDiaOverlay.setText(formatarMoeda(vendaLiquidaDia));
                    txtStatusVendaDiaOverlay.setText("Venda Liquida carregada pelo Consultores/Acumulado.");
                    vendaDiaCarregada = true;
                } else {
                    txtVendaLiquidaDiaOverlay.setText("R$ 0,00");
                    txtStatusVendaDiaOverlay.setText("Nao foi possivel carregar a venda liquida do dia.");
                    Log.d("METAS_DEBUG", "Overlay HOJE - Response Code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("METAS_DEBUG", "Overlay HOJE - ErrorBody: " + response.errorBody().string());
                        } catch (Exception e) {}
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Consultor>> call, Throwable t) {
                layoutLoadingVendaDiaOverlay.setVisibility(View.GONE);
                txtVendaLiquidaDiaOverlay.setText("R$ 0,00");
                txtStatusVendaDiaOverlay.setText("Erro ao consultar a venda liquida do dia.");
                Log.e("METAS_DEBUG", "Overlay HOJE - onFailure: " + t.getMessage(), t);
            }
        });
    }

    private String limitarLog(String value) {
        if (value == null) return "null";
        // Evita estourar limite de linha do logcat.
        int max = 3500;
        return value.length() <= max ? value : value.substring(0, max) + "...(truncado)";
    }

    private ApiGemco getApiService() {
        OkHttpClient client = new OkHttpClient.Builder()
               .connectTimeout(120, TimeUnit.SECONDS)
               .readTimeout(120, TimeUnit.SECONDS)
               .build();
               
        Retrofit retrofit = new Retrofit.Builder()
               .baseUrl(ApiGemco.BASE_URL)
               .client(client)
               .addConverterFactory(GsonConverterFactory.create())
               .build();
               
        return retrofit.create(ApiGemco.class);
    }

    private String formatarMoeda(double valor) {
        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return real.format(valor);
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private int dpToPxInt(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void carregarBonusVendaAvista(String dtIni, String dtFim, int consultor, String token) {
        ApiGemco api = getApiService();
        Log.d("METAS_DEBUG", "Chamando GetBonusVendaAvista... URL base: " + ApiGemco.BASE_URL);
        Call<List<BonusVendaAvista>> call = api.GetBonusVendaAvista(dtIni, dtFim, consultor, token);
        call.enqueue(new Callback<List<BonusVendaAvista>>() {
            @Override
            public void onResponse(Call<List<BonusVendaAvista>> call, Response<List<BonusVendaAvista>> response) {
                Log.d("METAS_DEBUG", "GetBonusVendaAvista Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    BonusVendaAvista dados = response.body().get(0);
                    Log.d("METAS_DEBUG", "GetBonusVendaAvista Sucesso: Perc=" + dados.Perc + ", Bonus=" + dados.VlBonus);
                    
                    // Coluna Meta: Valor Fixo (ex: 45% conforme layout original ou deixar vazio se não tivermos a meta)
                    // Coluna Realizado: Valor vindo da API (dados.Perc)
                    // Coluna Ganho: Valor vindo da API (dados.VlBonus)
                    
                    // txtMetaAvista.setText("45%"); // Removido, agora atualizado pelo carregarMetasConsultor
                    txtRealizadoAvista.setText(String.format(Locale.getDefault(), "%.2f%%", dados.Perc));
                    
                    // Ganho deve refletir exatamente o VlBonus retornado pela API
                    double valorGanho = dados.VlBonus != null ? dados.VlBonus : 0.0;
                    // Gatilho mínimo de venda para acesso às premiações
                    if (vendaLiquidaGlobal < getGatilhoMinVenda()) {
                        valorGanho = 0.0;
                    }
                    premioAvista = valorGanho;
                    atualizarTotalPremiacao();
                    NumberFormat realFmt = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                    txtGanhoAvista.setText(realFmt.format(valorGanho));
                    atualizarInfoGatilho();
                 } else {
                    Log.d("METAS_DEBUG", "GetBonusVendaAvista Vazio ou Falha. Body null? " + (response.body() == null));
                    if (response.errorBody() != null) {
                        try {
                            Log.e("METAS_DEBUG", "ErrorBody: " + response.errorBody().string());
                        } catch (Exception e) {}
                    }
                    // txtMetaAvista.setText("45%");
                    txtRealizadoAvista.setText("-");
                    txtGanhoAvista.setText("R$ 0,00");
                }
            }
            @Override
            public void onFailure(Call<List<BonusVendaAvista>> call, Throwable t) {
                Log.e("METAS_DEBUG", "GetBonusVendaAvista Falha: " + t.getMessage(), t);
                // txtMetaAvista.setText("45%");
                txtRealizadoAvista.setText("Erro");
            }
        });
    }

    private void carregarBonusLinhasPositivadas(String dtIni, String dtFim, int consultor, String token) {
        ApiGemco api = getApiService();
        Call<List<BonusLinhasPositivadas>> call = api.GetBonusLinhasPositivadas(dtIni, dtFim, consultor, token);
        call.enqueue(new Callback<List<BonusLinhasPositivadas>>() {
            @Override
            public void onResponse(Call<List<BonusLinhasPositivadas>> call, Response<List<BonusLinhasPositivadas>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    BonusLinhasPositivadas dados = response.body().get(0);
                    
                    // txtMetaLinhas.setText("14"); // Fixo
                    txtRealizadoLinhas.setText(String.valueOf(dados.QtdLinhas));
                    
                    // Ganho deve refletir exatamente o VlBonus retornado pela API
                    double valorGanho = dados.VlBonus != null ? dados.VlBonus : 0.0;
                    // Gatilho mínimo de venda para acesso às premiações
                    if (vendaLiquidaGlobal < getGatilhoMinVenda()) {
                        valorGanho = 0.0;
                    }
                    premioLinhas = valorGanho;
                    atualizarTotalPremiacao();
                    NumberFormat realFmt = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                    txtGanhoLinhas.setText(realFmt.format(valorGanho));
                    atualizarInfoGatilho();
                 } else {
                    // txtMetaLinhas.setText("14");
                    txtRealizadoLinhas.setText("-");
                    txtGanhoLinhas.setText("R$ 0,00");
                }
            }
            @Override
            public void onFailure(Call<List<BonusLinhasPositivadas>> call, Throwable t) {
                // txtMetaLinhas.setText("14");
                txtRealizadoLinhas.setText("Erro");
            }
        });
    }

    private void carregarBonusArrecadeFrete(String dtIni, String dtFim, int consultor, String token) {
        ApiGemco api = getApiService();
        Call<List<BonusArrecadeFrete>> call = api.GetBonusArrecadeFrete(dtIni, dtFim, consultor, token);
        call.enqueue(new Callback<List<BonusArrecadeFrete>>() {
            @Override
            public void onResponse(Call<List<BonusArrecadeFrete>> call, Response<List<BonusArrecadeFrete>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    BonusArrecadeFrete dados = response.body().get(0);
                    
                    //  atualizado por carregarMetasConsultor
                    NumberFormat realFmt = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                    double perc = dados.PercArrecadado != null ? dados.PercArrecadado : 0.0;
                    double valor = dados.Arrecadado != null ? dados.Arrecadado : 0.0;
                    txtRealizadoFrete.setText(String.format(Locale.getDefault(), "%.2f%% | %s", perc, realFmt.format(valor)));
                    
                    // Ganho deve refletir exatamente o VlBonus retornado pela API
                    double valorGanho = dados.VlBonus != null ? dados.VlBonus : 0.0;
                    // Gatilhos: venda mínima e frete arrecadado mínimo conforme meta atual.
                    if (vendaLiquidaGlobal < getGatilhoMinVenda()) {
                        valorGanho = 0.0;
                    }
                    double arrecadado = dados.Arrecadado != null ? dados.Arrecadado : 0.0;
                    if (arrecadado < getGatilhoFreteVendido()) {
                        valorGanho = 0.0;
                    }
                    premioFrete = valorGanho;
                    atualizarTotalPremiacao();
                    txtGanhoFrete.setText(realFmt.format(valorGanho));
                    atualizarInfoGatilho();
                 } else {

                    txtRealizadoFrete.setText("-");
                    txtGanhoFrete.setText("R$ 0,00");
                }
            }
            @Override
            public void onFailure(Call<List<BonusArrecadeFrete>> call, Throwable t) {

                txtRealizadoFrete.setText("Erro");
            }
        });
    }

    private void carregarComissaoVariavel(String dtIni, String dtFim, int consultor, String token) {
        // OBS: ComissaoVariavelProduto precisa de codigo. Filial não é necessário.
        
        ApiGemco api = getApiService();
        // Passando string vazia "" para filial
        Log.d("METAS_DEBUG", "Iniciando carregarComissaoVariavel... Consultor: " + consultor);
        
        // Exibir Loading e Esconder Tabela
        layoutLoadingMatriz.setVisibility(View.VISIBLE);
        scrollMatriz.setVisibility(View.GONE);
        
        Call<List<ComissaoVariavelProduto>> call = api.GetComissaoVariavel(dtIni, dtFim, String.valueOf(consultor), "", token);
        call.enqueue(new Callback<List<ComissaoVariavelProduto>>() {
            @Override
            public void onResponse(Call<List<ComissaoVariavelProduto>> call, Response<List<ComissaoVariavelProduto>> response) {
                // Esconder Loading e Exibir Tabela
                layoutLoadingMatriz.setVisibility(View.GONE);
                scrollMatriz.setVisibility(View.VISIBLE);

                Log.d("METAS_DEBUG", "GetComissaoVariavel Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Resetar cores da matriz
                    resetarCoresMatriz();

                    // Pegar o primeiro item (assumindo que retorna 1 registro consolidado por consultor)
                    ComissaoVariavelProduto item = response.body().get(0);
                    detalhesMetaAtual = item; // Guardar para o popup de detalhes
                    
                    double vendaLiquida = item.VendaLiquida != null ? item.VendaLiquida : 0;
                    double margem = item.Margem != null ? item.Margem : 0;
                    double premioIncentivo = item.PremioIncentivo != null ? item.PremioIncentivo : 0;
                    double totalReceber = 0; // Calculo manual se necessário ou pegar de outro campo
                    if (item.ComissaoNova != null) totalReceber += item.ComissaoNova;
                    // if (item.Dsr != null) totalReceber += item.Dsr; // DSR removido conforme solicitação
                    
                    Log.d("METAS_DEBUG", "ComissaoVariavel: Venda=" + vendaLiquida + ", Margem=" + margem + ", PremioIncentivo=" + premioIncentivo);

                    // Atualizar variável global
                    vendaLiquidaGlobal = vendaLiquida;

                    // Cálculo da Média de SKUs (Skus / Atendimentos)
                    double mediaSku = 0.0;
                    if (item.Skus != null && item.Atendimentos != null && item.Atendimentos > 0) {
                        mediaSku = (double) item.Skus / item.Atendimentos;
                    }
                    
                    // Atualizar linha de SKU na tabela principal
                    // Regra: Meta SKU (>= Meta) E Venda Líquida >= 120k
                    // txtMetaSku.setText("4"); // Removido, atualizado por carregarMetasConsultor
                    txtRealizadoSku.setText(String.format(Locale.getDefault(), "%.2f", mediaSku));
                    double ganhoSku = 0.0;
                    if (metaCarregada && mediaSku >= metaSkuVal && vendaLiquidaGlobal >= getGatilhoMinVenda()) {
                        ganhoSku = VALOR_BONUS_PADRAO;
                    }
                    premioSku = ganhoSku;
                    atualizarTotalPremiacao();
                    NumberFormat realFmt = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
                    txtGanhoSku.setText(realFmt.format(ganhoSku));
                    atualizarInfoGatilho();

                    // Carregar os outros indicadores agora que temos a Venda Líquida
                    carregarBonusVendaAvista(dtIni, dtFim, consultor, token);
                    carregarBonusLinhasPositivadas(dtIni, dtFim, consultor, token);
                    carregarBonusArrecadeFrete(dtIni, dtFim, consultor, token);
                    
                    // Remover loading da tabela e exibir
                    layoutLoadingTabela.setVisibility(View.GONE);
                    scrollTabela.setVisibility(View.VISIBLE);
                    tabelaMetas.setVisibility(View.VISIBLE);
                    btnFaturamentoLinha.setVisibility(View.VISIBLE);

                    // Lógica para pintar a célula
                    pintarCelulaMatriz(vendaLiquida, margem);
                    
                    // Animar Totais
                    animarValorMonetario(txtTotalVendaLiquida, 0, (float) vendaLiquida);
                    animarValorMonetario(txtTotalComissao, 0, (float) totalReceber); // Comissão é o valor da procedure
                    animarValorPercentual(txtTotalMargem, 0, (float) margem);
                    animarValorMonetario(txtTotalPremiacaoAcelerados, 0, (float) premioIncentivo);
                    
                    txtMetaFaturamento.setText("-");
                    txtRealizadoFaturamento.setText(String.format(Locale.getDefault(), "V: %s | M: %.2f%%", realFmt.format(vendaLiquida), margem)); 
                    // txtGanhoFaturamento será atualizado dentro do pintarCelulaMatriz com o valor correto da matriz
                } else {
                    Log.d("METAS_DEBUG", "GetComissaoVariavel Falhou ou Vazio. Body null? " + (response.body() == null));
                    if (response.errorBody() != null) {
                        try {
                            Log.e("METAS_DEBUG", "GetComissaoVariavel ErrorBody: " + response.errorBody().string());
                        } catch (Exception e) {}
                    }
                    resetarCoresMatriz();
                    txtMetaFaturamento.setText("-");
                    txtRealizadoFaturamento.setText("-");
                    txtGanhoFaturamento.setText("R$ 0,00");
                    
                    txtTotalVendaLiquida.setText("R$ 0,00");
                    txtTotalComissao.setText("R$ 0,00");
                    txtTotalMargem.setText("0,00%");
                    txtTotalPremiacao.setText("R$ 0,00");
                    txtTotalPremiacaoAcelerados.setText("R$ 0,00");
                    atualizarInfoGatilho();
                }
            }
            @Override
            public void onFailure(Call<List<ComissaoVariavelProduto>> call, Throwable t) {
                // Esconder Loading e Exibir Tabela (mesmo vazia ou erro)
                layoutLoadingMatriz.setVisibility(View.GONE);
                scrollMatriz.setVisibility(View.VISIBLE);

                Log.e("METAS_DEBUG", "GetComissaoVariavel onFailure: " + t.getMessage(), t);
                txtMetaFaturamento.setText("-");
                txtRealizadoFaturamento.setText("Erro");
                
                txtTotalVendaLiquida.setText("Erro");
                txtTotalComissao.setText("Erro");
                txtTotalMargem.setText("Erro");
                txtTotalPremiacao.setText("Erro");
                txtTotalPremiacaoAcelerados.setText("Erro");
                
                // Remover loading da tabela e exibir erro
                layoutLoadingTabela.setVisibility(View.GONE);
                scrollTabela.setVisibility(View.VISIBLE);
                tabelaMetas.setVisibility(View.VISIBLE);
            }
        });
    }

    private void animarValorMonetario(final TextView textView, float start, float end) {
        final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(3000);
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            textView.setText(real.format(value));
        });
        animator.start();
    }

    private void animarValorPercentual(final TextView textView, float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(3000); // 3 segundos
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            textView.setText(String.format(Locale.getDefault(), "%.2f%%", value));
        });
        animator.start();
    }

    private void resetarCoresMatriz() {
        int corPadrao = android.graphics.Color.parseColor("#F7F4FC");
        cell_34_200k.setBackgroundColor(corPadrao);
        cell_34_250k.setBackgroundColor(corPadrao);
        cell_34_300k.setBackgroundColor(corPadrao);
        cell_33_200k.setBackgroundColor(corPadrao);
        cell_33_250k.setBackgroundColor(corPadrao);
        cell_33_300k.setBackgroundColor(corPadrao);
        cell_32_200k.setBackgroundColor(corPadrao);
        cell_32_250k.setBackgroundColor(corPadrao);
        cell_32_300k.setBackgroundColor(corPadrao);
    }

    private void pintarCelulaMatriz(double venda, double margem) {
        // Log para debug
        Log.d("METAS_DEBUG", "--------------------------------------------------");
        Log.d("METAS_DEBUG", "Pintando Matriz: Venda=" + venda + ", Margem=" + margem);

        // Cor Verde Sólida para teste
        int corDestaque = android.graphics.Color.GREEN; 
        
        // Valores de Ganho da Matriz
        double[][] valoresMatriz;
        if (isFilial900()) {
            // Filial 900: linhas 33%, 32%, 31% (mapeadas nas linhas 34/33/32 da UI)
            valoresMatriz = new double[][]{
                {100.0, 150.0, 300.0}, // Linha 0 (Margem >= 33%) -> UI 34%
                {50.0,  100.0, 150.0}, // Linha 1 (Margem >= 32%) -> UI 33%
                {0.0,   50.0,  100.0}  // Linha 2 (Margem >= 31%) -> UI 32%
            };
            // Atualiza textos das células para refletir os valores da filial 900
            setCellText(cell_34_200k, "R$ 100,00");
            setCellText(cell_34_250k, "R$ 150,00");
            setCellText(cell_34_300k, "R$ 300,00");
            setCellText(cell_33_200k, "R$ 50,00");
            setCellText(cell_33_250k, "R$ 100,00");
            setCellText(cell_33_300k, "R$ 150,00");
            setCellText(cell_32_200k, "R$ 0,00");
            setCellText(cell_32_250k, "R$ 50,00");
            setCellText(cell_32_300k, "R$ 100,00");
            // Atualiza rótulos de linhas e rodapé
            setCellText(lblRow34, "33%");
            setCellText(lblRow33, "32%");
            setCellText(lblRow32, "31%");
            setCellText(footer200k, "R$ 100.000,00");
            setCellText(footer250k, "R$ 120.000,00");
            setCellText(footer300k, "R$ 140.000,00");
        } else {
            valoresMatriz = new double[][]{
                {200.0, 400.0, 800.0}, // Linha 0 (Margem >= 35%)
                {100.0, 300.0, 400.0}, // Linha 1 (Margem >= 34%)
                {0.0,   200.0, 300.0}  // Linha 2 (Margem >= 33%)
            };
            // Reset textos padrão
            setCellText(cell_34_200k, "R$ 200,00");
            setCellText(cell_34_250k, "R$ 400,00");
            setCellText(cell_34_300k, "R$ 800,00");
            setCellText(cell_33_200k, "R$ 100,00");
            setCellText(cell_33_250k, "R$ 300,00");
            setCellText(cell_33_300k, "R$ 400,00");
            setCellText(cell_32_200k, "R$ 0,00");
            setCellText(cell_32_250k, "R$ 200,00");
            setCellText(cell_32_300k, "R$ 300,00");
            setCellText(lblRow34, "35%");
            setCellText(lblRow33, "34%");
            setCellText(lblRow32, "33%");
            setCellText(footer200k, "R$ 170.000,00");
            setCellText(footer250k, "R$ 250.000,00");
            setCellText(footer300k, "R$ 300.000,00");
        }
        
        double ganhoMatrizCalculado = 0.0;

        // Determinar Coluna (Venda Líquida)
        int col = -1;
        if (isFilial900()) {
            if (venda >= 140000) {
                col = 2;
                Log.d("METAS_DEBUG", "Coluna 2 (>= 140k)");
            } else if (venda >= 120000) {
                col = 1;
                Log.d("METAS_DEBUG", "Coluna 1 (>= 120k)");
            } else if (venda >= 100000) {
                col = 0;
                Log.d("METAS_DEBUG", "Coluna 0 (>= 100k)");
            } else {
                Log.d("METAS_DEBUG", "Nenhuma Coluna (< 100k)");
            }
        } else {
            if (venda >= 300000) {
                col = 2;
                Log.d("METAS_DEBUG", "Coluna 2 (>= 300k)");
            }
            else if (venda >= 250000) {
                col = 1;
                Log.d("METAS_DEBUG", "Coluna 1 (>= 250k)");
            }
            else if (venda >= 170000) {
                col = 0;
                Log.d("METAS_DEBUG", "Coluna 0 (>= 170k)");
            } else {
                Log.d("METAS_DEBUG", "Nenhuma Coluna (< 170k)");
            }
        }

        // Determinar Linha (Margem)
        int row = -1;
        if (isFilial900()) {
            if (margem >= 33) {
                row = 0;
                Log.d("METAS_DEBUG", "Linha 0 (>= 33%) [UI 34%]");
            }
            else if (margem >= 32) {
                row = 1;
                Log.d("METAS_DEBUG", "Linha 1 (>= 32%) [UI 33%]");
            }
            else if (margem >= 31) {
                row = 2;
                Log.d("METAS_DEBUG", "Linha 2 (>= 31%) [UI 32%]");
            } else {
                Log.d("METAS_DEBUG", "Nenhuma Linha (< 31%)");
            }
        } else {
            if (margem >= 35) {
                row = 0;
                Log.d("METAS_DEBUG", "Linha 0 (>= 35%)");
            }
            else if (margem >= 34) {
                row = 1;
                Log.d("METAS_DEBUG", "Linha 1 (>= 34%)");
            }
            else if (margem >= 33) {
                row = 2;
                Log.d("METAS_DEBUG", "Linha 2 (>= 33%)");
            } else {
                Log.d("METAS_DEBUG", "Nenhuma Linha (< 33%)");
            }
        }

        if (col != -1 && row != -1) {
            TextView target = null;
            
            // Obter o valor do ganho da célula identificada
            ganhoMatrizCalculado = valoresMatriz[row][col];
            
            // Mapeamento [row][col]
            if (row == 0) { // 35%
                if (col == 0) target = cell_34_200k;
                else if (col == 1) target = cell_34_250k;
                else if (col == 2) target = cell_34_300k;
            } else if (row == 1) { // 34%
                if (col == 0) target = cell_33_200k;
                else if (col == 1) target = cell_33_250k;
                else if (col == 2) target = cell_33_300k;
            } else if (row == 2) { // 33%
                if (col == 0) target = cell_32_200k;
                else if (col == 1) target = cell_32_250k;
                else if (col == 2) target = cell_32_300k;
            }

            if (target != null) {
                Log.d("METAS_DEBUG", "Pintando Célula ID: " + target.getId());
                target.setBackgroundColor(corDestaque);
            } else {
                Log.e("METAS_DEBUG", "Target TextView é NULL!");
            }
        } else {
            Log.d("METAS_DEBUG", "Fora do range da matriz.");
        }
        
        // Atualizar o texto de Ganho na Tabela Principal (Faturamento vs Margem)
        // Regra: Ganho = Valor da Célula SE VendaLiquida >= 120k (mas a propria matriz ja exige >= 200k, entao ok)
        // Se a venda for < 200k, col é -1, ganho 0. Se venda >= 200k (que é > 120k), ganho conforme matriz.
        // Porem, se o consultor tiver venda 150k, ele nao entra na matriz (min 200k), entao ganho 0.
        // A regra de 120k é o MÍNIMO GERAL, mas a matriz exige mais.
        
        NumberFormat realFmt = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
        txtGanhoFaturamento.setText(realFmt.format(ganhoMatrizCalculado));
        
        premioMatriz = ganhoMatrizCalculado;
        atualizarTotalPremiacao();
        atualizarInfoGatilho();
    }

    // Helper para definir textos das células de forma segura
    private void setCellText(TextView cell, String text) {
        if (cell != null) {
            cell.setText(text);
        }
    }

    // Verifica se o consultor logado está na filial 900
    private boolean isFilial900() {
        try {
            LoginHelper loginHelper = new LoginHelper(this);
            String codfilStr = loginHelper.GetUsuarioSalvo().get("codfil");
            return codfilStr != null && codfilStr.equals("900");
        } catch (Exception e) {
            return false;
        }
    }

    // Obtém o gatilho mínimo de venda líquida para acessar premiações
    private double getGatilhoMinVenda() {
        return isFilial900() ? 90000.0 : META_MINIMA_VENDA_LIQUIDA;
    }

    private int getMetaLinhasPositivadas() {
        return isFilial900() ? META_LINHAS_POSITIVADAS_FILIAL_900 : META_LINHAS_POSITIVADAS;
    }

    private double getGatilhoFreteVendido() {
        return isFilial900() ? GATILHO_FRETE_VENDIDO_FILIAL_900 : GATILHO_FRETE_VENDIDO;
    }

    private void atualizarInfoGatilho() {
        boolean nenhumPremio = premioAvista == 0.0 && premioLinhas == 0.0 && premioSku == 0.0 && premioFrete == 0.0 && premioMatriz == 0.0;
        if (nenhumPremio) {
            NumberFormat realFmt = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));
            String valor = isFilial900() ? realFmt.format(90000.0) : realFmt.format(META_MINIMA_VENDA_LIQUIDA);
            String msg = "Valor Líquido mínimo de " + valor + " para se enquadrar nas bonificações.";
            txtInfoGatilho.setText(msg);
            txtInfoGatilho.setVisibility(View.VISIBLE);
        } else {
            txtInfoGatilho.setVisibility(View.GONE);
        }
    }

    // Ajusta rótulos e valores base da matriz conforme a filial,
    // garantindo que a UI não apresente os padrões enquanto os dados carregam
    private void aplicarTextosBaseMatrizFilial(boolean filial900) {
        if (filial900) {
            setCellText(lblRow34, "33%");
            setCellText(lblRow33, "32%");
            setCellText(lblRow32, "31%");
            setCellText(footer200k, "R$ 100.000,00");
            setCellText(footer250k, "R$ 120.000,00");
            setCellText(footer300k, "R$ 140.000,00");
            setCellText(cell_34_200k, "R$ 100,00");
            setCellText(cell_34_250k, "R$ 150,00");
            setCellText(cell_34_300k, "R$ 300,00");
            setCellText(cell_33_200k, "R$ 50,00");
            setCellText(cell_33_250k, "R$ 100,00");
            setCellText(cell_33_300k, "R$ 150,00");
            setCellText(cell_32_200k, "R$ 0,00");
            setCellText(cell_32_250k, "R$ 50,00");
            setCellText(cell_32_300k, "R$ 100,00");
        } else {
            setCellText(lblRow34, "35%");
            setCellText(lblRow33, "34%");
            setCellText(lblRow32, "33%");
            setCellText(footer200k, "R$ 170.000,00");
            setCellText(footer250k, "R$ 250.000,00");
            setCellText(footer300k, "R$ 300.000,00");
            setCellText(cell_34_200k, "R$ 200,00");
            setCellText(cell_34_250k, "R$ 400,00");
            setCellText(cell_34_300k, "R$ 800,00");
            setCellText(cell_33_200k, "R$ 100,00");
            setCellText(cell_33_250k, "R$ 300,00");
            setCellText(cell_33_300k, "R$ 400,00");
            setCellText(cell_32_200k, "R$ 0,00");
            setCellText(cell_32_250k, "R$ 200,00");
            setCellText(cell_32_300k, "R$ 300,00");
        }
    }

    private void atualizarTotalPremiacao() {
        double total = premioAvista + premioLinhas + premioSku + premioFrete + premioMatriz;
        animarValorMonetario(txtTotalPremiacao, 0, (float) total);
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
