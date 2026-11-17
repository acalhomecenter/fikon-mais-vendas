package br.com.acalfortaleza.fikonmais;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.graphics.Color;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import br.com.acalfortaleza.fikonmais.adapters.PedidosAdapter;
import br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener;
import br.com.acalfortaleza.fikonmais.fragments.AprazoFragment;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.helper.FixPayHelper;
import br.com.acalfortaleza.fikonmais.models.PixResponse;
import br.com.acalfortaleza.fikonmais.modal.ModalBottomSheetPed;
import br.com.acalfortaleza.fikonmais.modal.ModalBottomSheetPedLong;
import br.com.acalfortaleza.fikonmais.popup.PopupPedFechamento;
import br.com.acalfortaleza.fikonmais.models.Credito;
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Notas;
import br.com.acalfortaleza.fikonmais.models.PedTransferencia;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.PoliticaDesconto;
import br.com.acalfortaleza.fikonmais.popup.PopupConsultaOrcamento;
import br.com.acalfortaleza.fikonmais.popup.PopupFinanceiro;
import br.com.acalfortaleza.fikonmais.popup.PopupObsPedido;
import mehdi.sakout.fancybuttons.FancyButton;

public class PedidosActivity extends BaseAcalActivity implements SearchView.OnQueryTextListener, ModalBottomSheetPed.ActionListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, DialogInterface.OnCancelListener {

    private FloatingActionButton btn_flutuante;
    private FancyButton btn_dia;
    private FancyButton btn_mes;
    private FancyButton btn_periodo;
    private PedidosHelper helper;
    private ProgressDialog dialog;
    private AlertDialog alertaDialog;
    private List<Pedidos> mList;

    private List<Pedidos> pedidoIndividual;
    private Handler handler;
    public PedidosAdapter adapter;
    private RecyclerView mRecycleView;
    private TextView txtliberado;
    private TextView txtparcial;
    private TextView txtconcluido;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private SimpleDateFormat formataData;
    private Date dateinicial;
    private Date datefinal;
    public static String periodoIni = "";
    public static String periodoFim = "";
    public static int codarq = 0;
    private Pedidos pedido;
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    final DecimalFormat df = new DecimalFormat("0.00");
    final DecimalFormat dfInteiro = new DecimalFormat("0");
    private LinearLayout ind_lib;
    private LinearLayout ind_parc;
    private LinearLayout ind_fat;
    private TextView ind_flib;
    private TextView ind_pcaixa;
    private Boolean _isClikedAz = false;
    private Boolean _isClikedValor = false;
    private String _valorConsultado;
    public static Boolean _isAlteracao = false;
    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
    private Date date;
    private int year, month, day, hour, minute;
    private String retornoLiberacao = "";
    private String temcredi="";
    private String libComercial = "";
    public static Credito retornoCredito;






    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_pedidos);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.ped_toolbar);
        setSupportActionBar(toolbar);


        helper = new PedidosHelper();
        
        // Teste temporário para verificar ConsultarLinksComFallback
        //helper.testarConsultaLinks();
        
        handler = new Handler();
        loginHelper = new LoginHelper(this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();

        _valorConsultado = getIntent().getStringExtra("cgccpf");
        _isAlteracao = getIntent().getBooleanExtra("isalteracao",false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Consulta de Pedido");
        getSupportActionBar().setElevation(0);

        formataData = new SimpleDateFormat("yyyy-MM-dd");

        btn_flutuante = findViewById(R.id.ped_btn_flutuante);
        btn_dia = findViewById(R.id.ped_btn_dia);
        btn_mes = findViewById(R.id.ped_btn_mes);
        btn_periodo = findViewById(R.id.ped_btn_periodo);
        txtliberado = findViewById(R.id.ped_txtLibCxa);
        txtconcluido = findViewById(R.id.ped_txtConcluidos);
        txtparcial = findViewById(R.id.ped_txtParcial);
        mRecycleView = findViewById(R.id.ped_lista_pedido);
        ind_lib = findViewById(R.id.ped_ind_lib);
        ind_parc = findViewById(R.id.ped_ind_parc);
        ind_fat = findViewById(R.id.ped_ind_fat);
        ind_flib = findViewById(R.id.ped_ind_flib);
        ind_pcaixa= findViewById(R.id.ped_ind_pcaixa);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecycleView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ModalBottomSheetPed modalBottomSheet = new ModalBottomSheetPed();
                        modalBottomSheet.setActionListener(PedidosActivity.this);
                        pedido = mList.get(position);
                        
                        // Configurar visibilidade do botão "GERAR LINKS DE PAGAMENTO"
                        // Mostrar se status = 3 E condição de pagamento = "87"
                        
                        boolean statusCorreto = pedido.getCodStatus() == 3;
                        boolean condicaoCorreta = pedido.getCondPagamento() != null && 
                             "87".equals(pedido.getCondPagamento().getCondpgto());
                        boolean mostrarGerarLinks = statusCorreto && condicaoCorreta;
                        
                        modalBottomSheet.configurarBotaoGerarLinks(mostrarGerarLinks);
                        
                        modalBottomSheet.show(getSupportFragmentManager(), "modalMenu");

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        ModalBottomSheetPedLong modalBottomSheetLong = new ModalBottomSheetPedLong();
                        modalBottomSheetLong.setActionListener(PedidosActivity.this);
                        modalBottomSheetLong.show(getSupportFragmentManager(), "modalMenu");
                        pedido = mList.get(position);
                    }
                })
        );


        btn_flutuante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PedidosActivity.this, ConsultaActivity.class);
                startActivity(intent);

            }
        });

        btn_dia.setBackgroundColor(getResources().getColor(R.color.primary_dark));
        btn_dia.setTextColor(getResources().getColor(R.color.icons));
        btn_dia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_dia.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                btn_dia.setTextColor(getResources().getColor(R.color.icons));
                btn_mes.setBackgroundColor(getResources().getColor(R.color.icons));
                btn_mes.setTextColor(getResources().getColor(R.color.primary));
                btn_periodo.setBackgroundColor(getResources().getColor(R.color.icons));
                btn_periodo.setTextColor(getResources().getColor(R.color.primary));

                formataData = new SimpleDateFormat("yyyy-MM-dd");

                dateinicial = new Date();
                datefinal = new Date();

                periodoIni = formataData.format(dateinicial);
                periodoFim = formataData.format(datefinal);


                ConsultarPedidos(formataData.format(dateinicial), formataData.format(datefinal),0);


            }
        });

        btn_mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_dia.setBackgroundColor(ContextCompat.getColor(PedidosActivity.this, R.color.icons));
                btn_dia.setTextColor(ContextCompat.getColor(PedidosActivity.this, R.color.primary));
                btn_mes.setBackgroundColor(ContextCompat.getColor(PedidosActivity.this,R.color.primary_dark));
                btn_mes.setTextColor(ContextCompat.getColor(PedidosActivity.this,R.color.icons));
                btn_periodo.setBackgroundColor(ContextCompat.getColor(PedidosActivity.this, R.color.icons));
                btn_periodo.setTextColor(ContextCompat.getColor(PedidosActivity.this, R.color.primary));
                formataData = new SimpleDateFormat("yyyy-MM-dd");

                Date data = new Date();

                datefinal = new Date();

                Calendar c = Calendar.getInstance();
                c.setTime(data);
                c.add(Calendar.MONTH, -1);
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

                dateinicial = new Date(c.getTime().getTime());

                periodoIni = formataData.format(dateinicial);
                periodoFim = formataData.format(datefinal);

                ConsultarPedidos(formataData.format(dateinicial), formataData.format(datefinal),0);
            }
        });

        btn_periodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_dia.setBackgroundColor(ContextCompat.getColor(PedidosActivity.this,R.color.icons));
                btn_dia.setTextColor(ContextCompat.getColor(PedidosActivity.this,R.color.primary));
                btn_mes.setBackgroundColor(ContextCompat.getColor(PedidosActivity.this,R.color.icons));
                btn_mes.setTextColor(ContextCompat.getColor(PedidosActivity.this,R.color.primary));
                btn_periodo.setBackgroundColor(ContextCompat.getColor(PedidosActivity.this,R.color.primary_dark));
                btn_periodo.setTextColor(ContextCompat.getColor(PedidosActivity.this,R.color.icons));

                Intent intent = new Intent(PedidosActivity.this, PopupConsultaOrcamento.class);
                intent.putExtra("tela", "pedido");
                startActivity(intent);
            }
        });


        if (adapter == null) {
            txtliberado.setText("0");
            txtparcial.setText("0");
            txtconcluido.setText("0");
        } else {
            txtliberado.setText(String.valueOf(adapter.QtdLiberado()));
            txtparcial.setText(String.valueOf(adapter.QtdParcial()));
            txtconcluido.setText(String.valueOf(adapter.QtdConcluido()));
        }


        formataData = new SimpleDateFormat("yyyy-MM-dd");

        dateinicial = new Date();
        datefinal = new Date();
        periodoIni = formataData.format(dateinicial);
        periodoFim = formataData.format(datefinal);


        ind_lib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("5");
            }
        });


        ind_parc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("8");
            }
        });


        ind_fat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("6");
            }
        });


        ind_flib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("1");
            }
        });


        ind_pcaixa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("3");
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(_valorConsultado!=null && _isAlteracao){

            try {

                Double numero = Double.parseDouble(_valorConsultado.trim());

                if (numero > 0) {

                    ConsultarPedidosConsulta(_valorConsultado, "CODIGO","S");
                    _isAlteracao = false;

                }

            } catch (NumberFormatException ex) {

                ConsultarPedidosConsulta(_valorConsultado, "TEXTO","S");
                _isAlteracao = false;

            }

        }else if (!periodoIni.equals("") && !periodoFim.equals("") && _isAlteracao) {
            ConsultarPedidos(periodoIni, periodoFim,codarq);

            _isAlteracao = false;

        }


    }


    @SuppressLint("RestrictedApi")
    public void showMenuImagem(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_pedido_long, popup.getMenu());


        MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) popup.getMenu(), v);
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.END);
        menuHelper.show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);


        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Consultar...");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {


                return true;
            }
        });


        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_order_az) {

            if (_isClikedAz) {
                adapter.Order_Za();
                _isClikedAz = false;
            } else {
                adapter.Order_Az();
                _isClikedAz = true;
            }

        }

        if (id == R.id.menu_order_preco) {

            if (_isClikedValor) {
                adapter.Order_ValorDesc();
                _isClikedValor = false;
            } else {
                adapter.Order_Valor();
                _isClikedValor = true;
            }
        }


        if (id == android.R.id.home) {

            finish();
            return true;
        }

        return false;
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        _valorConsultado = query;
        hideSoftKeyboard();
        try {

            Double numero = Double.parseDouble(query.trim());

            if (numero > 0) {

                ConsultarPedidosConsulta(query, "CODIGO","S");


                return true;
            }

        } catch (NumberFormatException ex) {

            ConsultarPedidosConsulta(query, "TEXTO","S");

        }


        return false;
    }

    public void ConsultarPedidos(final String dtinicial, final String dtfinal, final int codarq) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Consultando pedidos..."); // This line is unchanged
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {
                    int codvendr ;
                    if(usuarioLogado.get("isgerente").equals("SIM")){
                        codvendr = 9999;
                    }else{
                        codvendr = Integer.parseInt(usuarioLogado.get("codigo"));
                    }
                    mList = helper.GetPedidosList(dtinicial, dtfinal, codvendr, Integer.parseInt(usuarioLogado.get("codfil")), codarq, "DATA", "");
                    mList = helper.GetPedidosList(dtinicial, dtfinal, codvendr, Integer.parseInt(usuarioLogado.get("codfil")), codarq, "DATA", "");

                    // Enriquecer a lista com valores de frete/serviços retornados pela API
                    try {
                        enriquecerPedidosComFrete(mList);
                    } catch (Exception e) {
                        android.util.Log.e("FretePedList", "Erro ao enriquecer pedidos com frete: ", e);
                    }
                    if (mList == null || mList.size()==0) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PedidosActivity.this, "Não foram encontrados pedidos, para o período informado!", Toast.LENGTH_SHORT).show();
                                mList = new ArrayList<>();
                                adapter = new PedidosAdapter(PedidosActivity.this, mList);
                                mRecycleView.setAdapter(adapter);
                                CarregaPainel();
                            }
                        });
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {


                                adapter = new PedidosAdapter(PedidosActivity.this, mList);
                                mRecycleView.setAdapter(adapter);
                                CarregaPainel();
                                dialog.dismiss();

                            }
                        });
                    }

                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PedidosActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(PedidosActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();

                    }


                }
            }

        }.start();

    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.ped_status_bar_bg);
        if (statusBarBg == null) {
            return;
        }

        ViewGroup.LayoutParams params = statusBarBg.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBarBg.setLayoutParams(params);
    }





    private void CarregaPainel(){

        if (adapter == null || mList == null || mList.size()==0 ) {
            txtliberado.setText("0");
            txtparcial.setText("0");
            txtconcluido.setText("0");
            ind_pcaixa.setText("");
            ind_flib.setText("");
        } else {
            txtliberado.setText(String.valueOf(adapter.QtdLiberado()));
            txtparcial.setText(String.valueOf(adapter.QtdParcial()));
            txtconcluido.setText(String.valueOf(adapter.QtdConcluido()));
            ind_pcaixa.setText("Falta passar no cxa: "+String.valueOf(adapter.QtdFaltaCaixa()));
            ind_flib.setText("Falta liberação: "+String.valueOf(adapter.QtdFaltaLib()));
        }

    }

    /**
     * Enriquecer lista de pedidos com valor de frete/serviços retornado pela API.
     * Não soma diretamente em VlTotal (para evitar duplicações): armazena em pedido.vlFreteSelecionado
     * e coloca o valor no cache `PopupPedFechamento.servicosFretePorPedido` para uso pelo adapter.
     */
    private void enriquecerPedidosComFrete(List<Pedidos> pedidos) {
        if (pedidos == null || pedidos.isEmpty()) return;

        android.util.Log.d("FretePedList", "-> Iniciando enriquecimento de " + pedidos.size() + " pedidos com valores de frete");

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

            for (Pedidos ped : pedidos) {
                try {
                    long numPed = (long) ped.getNumpedven();
                    android.util.Log.d("FretePedList", "---> Consultando frete para pedido: " + numPed);

                    retrofit2.Response<br.com.acalfortaleza.fikonmais.service.FreteServicoResponse> response = api.ConsultarFreteServicoSelecionado(numPed, token).execute();

                    if (response.isSuccessful() && response.body() != null) {
                        br.com.acalfortaleza.fikonmais.service.FreteServicoResponse resp = response.body();
                        double valorServico = 0.0d;
                        if (resp.getValorTotal() != null) valorServico = resp.getValorTotal().doubleValue();

                        // Guarda no campo específico e no cache global
                        ped.setVlFreteSelecionado(valorServico);
                        try {
                            int key = (int) ped.getNumpedven();
                            br.com.acalfortaleza.fikonmais.popup.PopupPedFechamento.servicosFretePorPedido.put(key, valorServico);
                            if (resp.getIdsSelecionados() != null) {
                                br.com.acalfortaleza.fikonmais.popup.PopupPedFechamento.idsServicosFretePorPedido.put(key, resp.getIdsSelecionados());
                            }
                        } catch (Exception ex) {
                            // ignore
                        }

                        double vlTotalAnterior = ped.getVlTotal();
                        android.util.Log.d("FretePedList", "----> Pedido " + numPed + ": VlTotal anterior=" + vlTotalAnterior + ", ValorServicos=" + valorServico);
                    } else {
                        android.util.Log.w("FretePedList", "----> GET falhou para pedido " + ped.getNumpedven() + ". Code: " + response.code());
                    }

                } catch (Exception e) {
                    android.util.Log.e("FretePedList", "Erro ao consultar frete para pedido " + ped.getNumpedven(), e);
                }
            }

            android.util.Log.d("FretePedList", "-> Enriquecimento concluído!");

        } catch (Exception e) {
            android.util.Log.e("FretePedList", "Erro no processo de enriquecimento: ", e);
        }
    }

    public void ConsultarPedidosConsulta(final String consulta, final String tipo, final String Islist) {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Consultando pedidos...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {
                    if(Islist.equals("S")){
                        mList = helper.GetPedidosList("2025-01-01", "2025-05-01", 0, Integer.parseInt(usuarioLogado.get("codfil")), 0,consulta, tipo);
                    }else{
                        mList = helper.GetPedidosConsulta(consulta, tipo);
                    }


                    if (mList == null || mList.size()==0) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PedidosActivity.this, "Não foram encontrados pedidos, para o parametro informado!", Toast.LENGTH_SHORT).show();
                                mList = new ArrayList<>();
                                adapter = new PedidosAdapter(PedidosActivity.this, mList);
                                mRecycleView.setAdapter(adapter);
                                CarregaPainel();
                            }
                        });
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {


                                adapter = new PedidosAdapter(PedidosActivity.this, mList);
                                mRecycleView.setAdapter(adapter);
                                CarregaPainel();
                                dialog.dismiss();

                            }
                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PedidosActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(PedidosActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }


                }
            }

        }.start();

    }


    public void ConsultarPedidoIndividual(final String consulta, final String tipo) {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {

                        pedidoIndividual = helper.GetPedidosConsulta(consulta, tipo);



                    if (pedidoIndividual == null || pedidoIndividual.size()==0) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(PedidosActivity.this, "Não foram encontrados pedidos, para o parametro informado!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                dialog.dismiss();
                                registrarResumoPedidoDebug("PedidosActivity_antes_de_abrir_lancamento", pedidoIndividual.get(0));
                                Intent intentAlter = new Intent(PedidosActivity.this, Pedidos_LancamentoActivity.class);
                                intentAlter.putExtra("pedido", pedidoIndividual.get(0));
                                intentAlter.putExtra("alteracao", true);
                                startActivity(intentAlter);

                            }
                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PedidosActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(PedidosActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }


                }
            }

        }.start();

    }




    @Override
    public void onBackPressed() {

        finish();
    }

    private void registrarResumoPedidoDebug(String origem, Pedidos pedido) {
        if (pedido == null) {
            android.util.Log.d("PEDIDO_VALORES_DEBUG", origem + " | pedido=null");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(origem)
                .append(" | numpedven=").append((long) pedido.getNumpedven())
                .append(" | vlTotal=").append(pedido.getVlTotal())
                .append(" | vlFrete=").append(pedido.getVlFrete())
                .append(" | vlServicos=").append(pedido.getVlServicosAdicionais())
                .append(" | vlDesconto=").append(pedido.getVlDesconto())
                .append(" | peso=").append(pedido.getPeso())
                .append(" | pesoEntrega=").append(pedido.getPesoEntrega())
                .append(" | itens=").append(pedido.getItens() != null ? pedido.getItens().size() : 0);

        if (pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty() && pedido.getFinanceiro().get(0) != null) {
            sb.append(" | condpgto=").append(pedido.getFinanceiro().get(0).getCondpgto())
                    .append(" | condicao=").append(pedido.getFinanceiro().get(0).getCondicao())
                    .append(" | forma=").append(pedido.getFinanceiro().get(0).getForma());
        }

        android.util.Log.d("PEDIDO_VALORES_DEBUG", sb.toString());

        if (pedido.getItens() == null) {
            return;
        }

        for (int i = 0; i < pedido.getItens().size(); i++) {
            ItemPedidos item = pedido.getItens().get(i);
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
                            + " vlDescItem=" + item.getVlDescItem()
                            + " vlFreteItem=" + item.getVlFrete()
                            + " situacao=" + item.getSituacao()
                            + " isMotor=" + item.getIsMotor()
                            + " tpped=" + item.getTpped()
            );
        }
    }



    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onButtonClick(int id) {

        switch (id) {
            case R.id.mod_ped_ligar:
                if (!pedido.getCliente().getCelular().equals("")) {


                    if (ActivityCompat.checkSelfPermission((this),
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((this),
                                new String[]{Manifest.permission.CALL_PHONE}, 1);

                    } else {
                        Intent intentLigar = new Intent(Intent.ACTION_CALL);
                        intentLigar.setData(Uri.parse("tel:" + pedido.getCliente().getDddCelular() + pedido.getCliente().getCelular()));
                        startActivity(intentLigar);

                    }

                } else {
                    PerguntarEdicao(pedido.getCliente().getCgccpf());
                }
                break;

            case R.id.mod_ped_wpp:
                if (!pedido.getCliente().getCelular().equals("")) {


                    Uri uri = Uri.parse("https://wa.me/" + "55" + pedido.getCliente().getDddCelular() + pedido.getCliente().getCelular());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                } else {

                    PerguntarEdicao(pedido.getCliente().getCgccpf());
                }
                break;

            case R.id.mod_ped_liberar:
                if(usuarioLogado.get("isgerente").equals("SIM")){

                    PoliticaDesconto politicaDesconto = ConsultarPoliticaDesconto();
                    if(pedido.getFinanceiro().get(0).getCondpgto().equals("61")){
                        politicaDesconto = new PoliticaDesconto();
                        politicaDesconto.setId(null);
                    }

                    if(politicaDesconto == null){
                        new AlertDialog.Builder(PedidosActivity.this)
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
                            libComercial = "S";
                            Toast.makeText(PedidosActivity.this, politicaDesconto.getObservacao().replaceAll("</br>","\n"), Toast.LENGTH_SHORT);
                        }
                        else{
                            libComercial = "N";
                            new AlertDialog.Builder(PedidosActivity.this)
                                .setTitle("Pedido Não Liberado")
                                .setMessage(politicaDesconto.getObservacao().replaceAll("</br>","\n"))
                                .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create().show();
                            return;
                        }

                    }

                    if (politicaDesconto.getId() == null || politicaDesconto.getPercentualMargem() == null) {

                        if(pedido.getVlDesconto() > 0 && pedido.getPmargRent() < Float.parseFloat(usuarioLogado.get("percmargem"))) {
                            libComercial = "N";
                        }

                        for (int i = 0; i < pedido.getFinanceiro().size(); i++) {

                            if (pedido.getFinanceiro().get(i).getFlautcre() != null && pedido.getFinanceiro().get(i).getFlautcre().equals("S") || pedido.getFinanceiro().get(i).getForma()==15) {
                                libComercial = "S";



                            }
                        }

                    }

                    if(politicaDesconto.getId() == null || politicaDesconto.getPercentualDescontoItem() == null) {
                        for (int i = 0; i < pedido.getItens().size(); i++) {

                            if (BigDecimal.valueOf(pedido.getItens().get(i).getDpi()).setScale(2, RoundingMode.FLOOR).doubleValue() > Double.parseDouble(usuarioLogado.get("descontopermitido")) && pedido.getItens().get(i).getIsMotor() == null) {
                                libComercial = "N";
                            }

                        }
                    }

                    if(pedido.getFinanceiro().get(0).getCondpgto().equals("61")){
                        libComercial = "S";
                    }



                    if(libComercial.equals("N")){
                        Toast.makeText(PedidosActivity.this, "Desconto é maior ou margem menor que o permitido liberar!", Toast.LENGTH_SHORT).show();
                        libComercial="";
                    }else {


                        if (pedido.getCodStatus() <= 2) {


                            temcredi = "";
                            for (int i = 0; i < pedido.getFinanceiro().size(); i++) {

                                if (pedido.getFinanceiro().get(i).getFlautcre() != null && pedido.getFinanceiro().get(i).getFlautcre().equals("S") || pedido.getFinanceiro().get(i).getForma()==15) {
                                    temcredi = "S";


                                }
                            }


                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PedidosActivity.this);
                            builder.setTitle("Liberação Comercial :)");
                            builder.setMessage("Deseja efetuar a Liberação Comercial?");
                            builder
                                    .setCancelable(false)
                                    .setPositiveButton("Liberar", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();


                                            LiberacaoComercial(String.valueOf(pedido.getNumpedven()), Integer.parseInt(usuarioLogado.get("coduser")), Integer.parseInt(usuarioLogado.get("codfil")), temcredi);


                                        }

                                    });

                            builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });


                            builder.create().show();

                        } else {
                            Toast.makeText(PedidosActivity.this, "Pedido não necessita de liberação!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {


                }
                break;

            case R.id.mod_ped_editar:


                    ConsultarPedidoIndividual(dfInteiro.format(pedido.getNumpedven()), "CODIGO");









                break;

            case R.id.mod_ped_obs:


                    Intent intent = new Intent(PedidosActivity.this, PopupObsPedido.class);
                    intent.putExtra("numpedido", dfInteiro.format(pedido.getNumpedven()));
                    intent.putExtra("obs", pedido.getObs());
                    intent.putExtra("status",pedido.getCodStatus());
                    startActivity(intent);

                break;

            case R.id.mod_ped_financeiro:


                    Intent fin = new Intent(PedidosActivity.this, PopupFinanceiro.class);
                    fin.putExtra("financeiro", (Serializable) pedido.getFinanceiro());
                    startActivity(fin);

                break;

            case R.id.mod_ped_entrega:

                if (pedido.getVlFrete()>0 ) {
                    Intent fre = new Intent(PedidosActivity.this, ConsultaEntregaActivity.class);
                    fre.putExtra("numpedido", dfInteiro.format(pedido.getNumpedven()));
                    startActivity(fre);
                } else {
                    Toast.makeText(this, "Pedido não possui entrega!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.mod_ped_alterarentrega:

                Toast.makeText(this, "Essa opção estara disponivel em breve!", Toast.LENGTH_SHORT).show();

                /*if (pedido.getVlFrete()>0 && pedido.getCodStatus() != 6) {
                    Intent intent = new Intent(PedidosActivity.this, PopupAlterarEntrega.class);
                    intent.putExtra("pedido", (pedido));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Pedido já faturado ou não possui entrega!", Toast.LENGTH_SHORT).show();
                }*/
                break;

            case R.id.mod_ped_agenda:

                //Toast.makeText(this, "Essa opção estara disponivel em breve!", Toast.LENGTH_SHORT).show();

                if (pedido.getCodStatus() <= 5 || pedido.getCodStatus() == 8 ) {
                    if(validarAgendamento(pedido.getItens()).equals("S")){
                        Intent intentA = new Intent(PedidosActivity.this, AgendamentoRetiraActivity.class);
                        intentA.putExtra("pedido", (pedido));
                        startActivity(intentA);
                    }else{
                        Toast.makeText(this, "Pedido não possui retirada para realizar agendamento!", Toast.LENGTH_SHORT).show();
                    }




                } else {
                    Toast.makeText(this, "Pedido já faturado ou já possui agendamento!", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.mod_ped_info:

                CosnultarInformacoesPedido( pedido.getNumpedven(),   pedido.getCodfil() );


                break;

            case R.id.mod_ped_gerarLinks:

                if(pedido.getCodStatus() == 3){
                    Intent intentGerarLinks = new Intent(PedidosActivity.this, PopupPedFechamento.class);
                    intentGerarLinks.putExtra("statusLiberado", true);
                    intentGerarLinks.putExtra("pedido", pedido);
                    intentGerarLinks.putExtra("ignorarValidacaoDesconto", true);
                    startActivity(intentGerarLinks);
                }
                else{
                    Toast.makeText(
                        PedidosActivity.this,
                        "Pedido deve estar liberado para gerar links de pagamento",
                        Toast.LENGTH_LONG
                    ).show();
                }

                break;

            case R.id.mod_ped_linksPagamento:

                // Verifica se o pedido tem status "Falta liberação" (códigos 1 ou 2)
                if(pedido.getCodStatus() == 1 || pedido.getCodStatus() == 2){
                    
                    Toast.makeText(
                        PedidosActivity.this,
                        "Aguardando Liberação do Pedido",
                        Toast.LENGTH_LONG
                    ).show();
                    
                }
                else if(pedido.getCondPagamento().getCondpgto()!=null && pedido.getCondPagamento().getCondpgto().equals("87")){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String consultaLinksTemp = helper.ConsultarLinksComFallback((long)pedido.getNumpedven());
                            final String[] titulo = {null};
                            final boolean[] pagamentoJaEfetuado = {false};
                            final String[] consultaLinks = {consultaLinksTemp};

                            if(consultaLinksTemp.equals("PAGAMENTO_JA_EFETUADO")){
                                titulo[0] = "Status do Pagamento";
                                consultaLinks[0] = "PAGAMENTO DESTE PEDIDO JÁ EFETUADO";
                                pagamentoJaEfetuado[0] = true;
                            }
                            else if(consultaLinksTemp.contains("Links de pagamento do pedido")){
                                titulo[0] = "Links de Pagamento";
                                consultaLinks[0] = consultaLinksTemp;
                            }
                            else{
                                titulo[0] = "Erro ao consultar" + "\n" + "links de pagamento";
                                consultaLinks[0] = consultaLinksTemp;
                            }

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    android.app.AlertDialog.Builder alertLinks = new android.app.AlertDialog.Builder(PedidosActivity.this);
                                    alertLinks.setTitle(titulo[0]);
                                    
                                    // Se o pagamento já foi efetuado, exibe a mensagem em verde
                                    if(pagamentoJaEfetuado[0]){
                                        // Cria um TextView personalizado para a mensagem verde
                                        TextView messageView = new TextView(PedidosActivity.this);
                                        messageView.setText(consultaLinks[0]);
                                        messageView.setTextColor(android.graphics.Color.GREEN);
                                        messageView.setTextSize(16);
                                        messageView.setPadding(50, 30, 50, 30);
                                        messageView.setGravity(android.view.Gravity.CENTER);
                                        alertLinks.setView(messageView);
                                    }
                                    else{
                                        alertLinks.setMessage(consultaLinks[0]);
                                    }
                                    
                                    alertLinks.setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    // Só adiciona os botões de copiar e reenviar se NÃO for pagamento já efetuado
                                    if(titulo[0].equals("Links de Pagamento") && !pagamentoJaEfetuado[0]){
                                        final String linksParaCopiar = consultaLinks[0];

                                        alertLinks.setNeutralButton("Copiar Links", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                                ClipData clip = ClipData.newPlainText("Links copiados", linksParaCopiar);
                                                clipboard.setPrimaryClip(clip);

                                                Toast.makeText(PedidosActivity.this, "Links copiados", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        alertLinks.setNegativeButton("Reenviar Links", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                ReenviarLinksWhatsapp();
                                            }
                                        });

                                    }

                                    alertLinks.create().show();

                                }

                            });

                        }
                    }).start();

                }
                else {

                    Toast.makeText(
                        PedidosActivity.this,
                        "Para consultar os links de pagamento o pedido deve possuir a condição de pagamento PGTO LINK",
                        Toast.LENGTH_LONG
                    ).show();

                }


        }

    }


    private void initDateTimeData(){
        if(year == 0){
            Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        }
    }

    public void PerguntarEdicao(final String cgccpf){


        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Atenção :|");
        builder.setMessage("Cliente não possui celular cadastrado, deseja alterar?");
        builder
                .setCancelable(false)
                .setPositiveButton("Alterar", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Intent intent = new Intent(PedidosActivity.this, ClientesActivity.class);
                        intent.putExtra("cgccpf", cgccpf);
                        startActivity(intent);


                    }

                });

        builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.create().show();


    }


    public double getPeso(List<ItemPedidos> itens) {
        double peso = 0.00;

        for (ItemPedidos p : itens) {
            peso = peso + (p.getPeso()*p.getQtcomp());

        }
        return peso;
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

            vlbruto = vlbruto + (p.getPrecoUnit()*p.getQtcomp());

        }
        return (desconto/vlbruto)*100;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        year = month = day = hour = minute = 0;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        Calendar tDefault = Calendar.getInstance();
        tDefault.set(this.year,month,day,hour,minute);

        this.year = year;
        month = monthOfYear;
        day = dayOfMonth;


        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                tDefault.get(Calendar.HOUR_OF_DAY),
                tDefault.get(Calendar.MINUTE),
                true

        );

        timePickerDialog.setOnCancelListener(this);
        timePickerDialog.show(getSupportFragmentManager(), "timePickerDialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        if( hourOfDay < 8 || hourOfDay > 17){
            onDateSet(null,year, month,day);
            Toast.makeText(this,"Somente entre 8h e 17h",Toast.LENGTH_SHORT).show();
            return;
        }

        hour = hourOfDay;
        this.minute = minute;

        String dataagendada = "";

        dataagendada = (day < 10 ? "0"+day : day)+"/"+
                (month+1 < 10 ? "0"+(month+1) : month+1 )+"/"+
                year+" às "+
                (hour < 10 ? "0"+hour : hour)+"h"+
                (this.minute < 10 ? "0"+this.minute : this.minute);


        Toast.makeText(this,dataagendada,Toast.LENGTH_SHORT).show();



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


    public void LiberacaoComercial(final String numpedven, final int codlib, int codfil , String temcredi) {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Liberando...");
        dialog.setCancelable(false);
        dialog.show();



        new Thread() {

            @SuppressLint("SuspiciousIndentation")
            public void run() {

                try {

                    retornoLiberacao = helper.LiberacaoComercial(numpedven, codlib, codfil,temcredi);


                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if(retornoLiberacao.equals("LIBERADO")){



                                    if(temcredi.equals("S")){

                                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PedidosActivity.this);
                                        builder.setTitle("Liberação de Credito :)");
                                        builder.setMessage("Pedido precisa de Liberação de credito, deseja efetuar a liberação?");
                                        builder
                                                .setCancelable(false)
                                                .setPositiveButton("Liberar", new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();

                                                        double valor = 0.00d;

                                                        for(int i = 0; i < pedido.getFinanceiro().size(); i++){

                                                            if(pedido.getFinanceiro().get(i).getForma()==15){
                                                                valor = valor + pedido.getFinanceiro().get(i).getValor();
                                                            }

                                                        }


                                                        CosnultarCredito(0 ,numpedven, codlib, codfil);





                                                    }

                                                });

                                        builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });


                                        builder.create().show();

                                    }else {
                                        // Atualizar status do pedido para liberado
                                        pedido.setCodStatus(3);
                                        Toast.makeText(PedidosActivity.this, "Pedido Liberado!", Toast.LENGTH_SHORT).show();
                                    }

                                }else {
                                    Toast.makeText(PedidosActivity.this, "Falha na Liberação Comercial!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });



                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PedidosActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(PedidosActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }


                }
            }

        }.start();

    }


    public void LiberacaoCredito(final String numpedven, final int codlib, int codfil , String liberador, String codcli) {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Liberando...");
        dialog.setCancelable(false);
        dialog.show();



        new Thread() {

            public void run() {

                try {


                    if(retornoCredito.getRetorno().equals("TEM SALDO")){
                        retornoLiberacao = helper.LiberacaoCredito(numpedven, codlib, codfil,liberador,codcli);

                    }





                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if(retornoCredito.getRetorno().equals("TEM SALDO")){
                                if (retornoLiberacao.equals("LIBERADO")) {

                                    // Atualizar status do pedido para liberado
                                    pedido.setCodStatus(3);
                                    Toast.makeText(PedidosActivity.this, "Pedido Liberado!", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(PedidosActivity.this, "Falha na Liberação de Credito!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PedidosActivity.this);


                                builder.setMessage("Cliente não possui limite de credito para esse pedido! " + "Saldo Disponivel: " + String.valueOf(retornoCredito.getSaldo()));

                                builder
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                return;

                                            }

                                        });

                                builder.create().show();

                            }


                        }

                    });



                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PedidosActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(PedidosActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }


                }
            }

        }.start();

    }


    public  void CosnultarCredito(double valor,final String numpedven, final int codlib, int codfil ){

        dialog = new ProgressDialog(this);
        dialog.setTitle("Verificando Credito...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {

                    retornoCredito = helper.VerificarCredito(String.valueOf(pedido.getCliente().getCodcli()),valor);


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            LiberacaoCredito(numpedven, codlib, codfil,usuarioLogado.get("nome"),String.valueOf(pedido.getCliente().getCodcli()));

                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(getApplicationContext()).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(getApplicationContext()).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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



    public  void CosnultarInformacoesPedido(final double numpedven,  int codfil ){

        dialog = new ProgressDialog(this);
        dialog.setTitle("Carregando...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {

                    pedido.setNotas(helper.GetNotasPedidos(numpedven));
                    pedido.setPedTransferencias(helper.GetTransfPedidos(numpedven));
                    if (pedido.getVlFrete() > 0  ) {
                        pedido.setCliente(helper.GetClientePedidos(pedido.getCliente().getCgccpf(),0,"0"));
                    }




                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            String resultado = "";
                            String notas = "";
                            String transferencias="";
                            String especificador="";
                            String motor="";
                            String localentrega="";
                            String data="";

                            DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                            DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");





                            if (pedido.getNotas() != null && pedido.getNotas().size()>0) {

                                for (Notas n : pedido.getNotas()) {
                                    try {
                                        Date date = entrada.parse(n.Data);
                                        data = "Data: " + formato.format(date) + " <br />";
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (!n.NotaDevol.equals("")) {
                                        notas = notas + "Numnota: " + n.Numnota + " <br />" +
                                                data +
                                                "Serie: " + n.Serie + "" + " <br />" +
                                                "Filial: " + n.Filial + "" + " <br />" +
                                                "Status Nf: " + n.Status + "" + " <br />" +
                                                "Nota devol.: " + n.NotaDevol + "" + " <br />" +
                                                "<br />";
                                    } else {
                                        notas = notas + "Numnota: " + n.Numnota + " <br />" +
                                                data +
                                                "Serie: " + n.Serie + "" + " <br />" +
                                                "Filial: " + n.Filial + "" + " <br />" +
                                                "Status Nf: " + n.Status + "" + " <br />" +
                                                "<br />";
                                    }
                                }
                            }


                            if (pedido.getPedTransferencias() != null && pedido.getPedTransferencias().size()>0) {
                                for (PedTransferencia p : pedido.getPedTransferencias()) {
                                    if (p.Numnota.equals("")) {
                                        transferencias = transferencias + "Num. Ped. Transf: " + p.Numpedtransf + " <br />" +
                                                "Filial: " + p.Filial + "" + " <br />" +
                                                "Status Nf: " + p.Status + "" + " <br />" +
                                                "<br />";
                                    } else {
                                        transferencias = transferencias + "Num. Ped. Transf: " + p.Numpedtransf + " <br />" +
                                                "Filial: " + p.Filial + "" + " <br />" +
                                                "Numnota: " + p.Numnota + "" + " <br />" +
                                                "Status Nf: " + p.Status + "" + " <br />" +
                                                "<br />";
                                    }
                                }
                            }



                            if (pedido.getArquiteto() != null && !pedido.getArquiteto().equals("")) {


                                especificador = "<b>" +  "Especificador: " + "</b>"+ pedido.getArquiteto();
                            }


                            if (pedido.getVlFrete() > 0  ) {


                                java.util.List<br.com.acalfortaleza.fikonmais.models.Enderecos> ends =
                                        (pedido.getCliente() != null) ? pedido.getCliente().getEnderecos() : null;
                                if (ends != null && ends.size() > 0) {
                                    int idx = pedido.getEndent();
                                    if (idx < 0 || idx >= ends.size()) {
                                        int found = -1;
                                        try {
                                            for (int i = 0; i < ends.size(); i++) {
                                                if (ends.get(i).getCodend() == pedido.getEndent()) {
                                                    found = i;
                                                    break;
                                                }
                                            }
                                        } catch (Exception ignore) {}
                                        idx = (found != -1) ? found : 0;
                                    }
                                    localentrega = "<b>" +  "Local entrega: " + "</b>"+ ends.get(idx);
                                }
                            }


                            if (pedido.getTipoMotor() != null && !pedido.getTipoMotor().equals("")) {


                                if(pedido.getTipoMotor().equals("T")){
                                    motor = "<b>" +  "Motor de Promoção: " + "</b>"+ "Desconto no Total";
                                }else{
                                    motor = "<b>" +  "Motor de Promoção: " + "</b>"+ "Desconto nos Itens";
                                }

                            }









                            double servicosCache = 0.0d;
                            try {
                                int numPedInfo = (int) pedido.getNumpedven();
                                servicosCache = br.com.acalfortaleza.fikonmais.popup.PopupPedFechamento.servicosFretePorPedido.getOrDefault(numPedInfo, 0.0d);
                            } catch (Exception ignore) {}
                            double freteBaseInfo = Math.max(0.0, pedido.getVlFrete());
                            double valorTotalInfo = Math.max(0.0, pedido.getVlTotal());

                            resultado = resultado + ("<b>" + "Frete: " + "</b>" + real.format(freteBaseInfo) + "<br />" +
                                    "<b>" + "Valor total do pedido: " + "</b>" + real.format(valorTotalInfo) + "<br />" +
                                    "<b>" + "% do frete: " + "</b>" + df.format(getPercFretePrevisto(pedido.getItens(),pedido.getVlFreteSelecionado(), freteBaseInfo)) + "%" + "" + " <br />" +
                                    "<b>" + "Peso: " + "</b>" + df.format(getPeso(pedido.getItens())) + "kg" + "" + " <br />" +
                                    "<b>" + "Desconto: " + "</b>" + real.format(getDesconto(pedido.getItens())) + " <br />" +
                                    "<b>" + "% Desc.: " + "</b>" + df.format(getDescontoPerc(pedido.getItens()))+"%" + " <br />" +
                                    "<b>" + "Qtd. de Itens: " + "</b>" + String.valueOf(pedido.getItens().size()) + "" + " <br />" +
                                    "<b>" + "Status pedido: " + "</b>" + (pedido.getStatus()) + "" + " <br />" +
                                    "<b>" + "Consultor: " + "</b>" + (pedido.getCodvendr()) +" - "+pedido.getConsultor() + "" + " <br />" +
                                    "<b>" + "Filial: " + "</b>" + (pedido.getFilial()) + "" + " <br />" +
                                    "<b>" + "Cond. Pagamento: " + "</b>" + (pedido.getFinanceiro().get(0).getCondicao()) + "" + " <br />" +
                                    "<br />" +

                                    "<b>" + "Notas fiscais: " + "</b>" +
                                    "<br />" +
                                    notas + "" + " <br />" +
                                    "<b>" + "Pedidos de transferência: " + "</b>" +
                                    "<br />" +
                                    transferencias + "" + " <br />" +
                                    "<br />"+
                                    especificador + ""+ "<br />"+
                                    "<br />"+
                                    localentrega + ""+ "<br />"+
                                    "<br />"+
                                    motor + ""+ "<br />"+
                                    "<br />"
                            );


                            AlertDialog.Builder alerta = new AlertDialog.Builder(PedidosActivity.this);
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


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(getApplicationContext()).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(getApplicationContext()).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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



    public PoliticaDesconto ConsultarPoliticaDesconto(){

        ProgressDialog progressDialog = new ProgressDialog(PedidosActivity.this);

        try{

            progressDialog.setTitle("Consultando Politica de Desconto");
            progressDialog.setMessage("Por favor aguarde");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Gson gson = new Gson();
            Pedidos pedidoPolitica = gson.fromJson(gson.toJson(pedido), Pedidos.class);

            pedidoPolitica.setCodvendr(Integer.parseInt(usuarioLogado.get("codigo")));
            pedidoPolitica.setConsultor(usuarioLogado.get("cpf"));
            pedidoPolitica.setObs(usuarioLogado.get("isgerente").equals("S") || usuarioLogado.get("isgerente").equals("SIM") ? "GERENTE" : "");

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


    public String EnviarLinksPagamento(){

        try {
            // Log de monitoramento do uso da função
            Log.d("PedidosActivity", "EnviarLinksPagamento chamada - Pedido: " + pedido.getNumpedven() + ", Condição: " + pedido.getCondPagamento().getCondpgto());

            ProgressDialog progressDialogEnvio = new ProgressDialog(PedidosActivity.this);
            progressDialogEnvio.setTitle("Enviando");
            progressDialogEnvio.setMessage("Por favor aguarde");
            progressDialogEnvio.setCancelable(false);
            progressDialogEnvio.show();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    String[] envio = {null};
                    String[] titulo = {"Erro ao enviar links"};
                    boolean isPix = false;

                    try {
                        // Verificar se há links PIX consultando os links do pedido
                        String consultaLinks = helper.ConsultarLinksComFallback((long)pedido.getNumpedven());
                        Log.d("PedidosActivity", "Consulta de links retornou: " + consultaLinks);
                        
                        // Detectar se é um pedido PIX baseado no conteúdo dos links
                        if (consultaLinks != null && consultaLinks.contains("PIX")) {
                            isPix = true;
                            Log.d("PedidosActivity", "Pedido PIX detectado - usando GerarPixComWhatsApp");
                            
                            // Para PIX, usar o mesmo método da finalização que funciona
                            // Obter telefone do cliente
                            String telefone = "";
                            if (pedido.getCliente() != null && pedido.getCliente().getCelular() != null) {
                                String ddd = pedido.getCliente().getDddCelular();
                                String celular = pedido.getCliente().getCelular();
                                telefone = (ddd != null ? ddd : "") + celular;
                            }
                            
                            // Usar o método EnviarLinksPagamentoPix que já funciona
                            try {
                                envio[0] = helper.EnviarLinksPagamentoPix(pedido);
                                Log.d("PedidosActivity", "Resultado EnviarLinksPagamentoPix: " + envio[0]);
                            } catch (Exception e) {
                                Log.e("PedidosActivity", "Erro ao usar EnviarLinksPagamentoPix: " + e.getMessage(), e);
                                envio[0] = "Erro ao processar PIX: " + e.getMessage();
                            }
                        } else {
                            Log.d("PedidosActivity", "Pedido não-PIX detectado - usando envio padrão");
                            
                            // Para cartão, usar EnviarWhatsappLinks que realmente envia para WhatsApp
                            envio[0] = helper.EnviarWhatsappLinks(pedido);
                        }
                        
                        Log.d("PedidosActivity", "Resultado do envio (isPix=" + isPix + "): " + envio[0]);
                        
                    } catch (Exception e) {
                        Log.e("PedidosActivity", "Erro durante envio de links: " + e.getMessage(), e);
                        envio[0] = "Erro ao processar envio: " + e.getMessage();
                    }

                    if(envio[0]==null) {
                        envio[0] = "Erro ao enviar links de pagamento";
                    }
                    else if(envio[0].contains("Links de pagamento enviados para o whatsapp") || 
                            envio[0].contains("Links de pagamento pix enviados para o whatsapp")){
                        titulo[0] = "Links enviado com sucesso";
                        Log.d("PedidosActivity", "Envio bem-sucedido para WhatsApp - isPix: " + isPix);
                    }
                    else if(envio[0].contains("Não foram encontrados recursos http")) {
                        Log.e("PedidosActivity", "Erro HTTP 404 - Endpoint não encontrado. isPix: " + isPix + ", Resposta: " + envio[0]);
                        envio[0] = "Erro: Serviço de envio temporariamente indisponível. Tente novamente mais tarde.";
                    }
                    else if(envio[0].contains("\"Message\":\"Ocorreu um erro.\"") || envio[0].contains("Ocorreu um erro")) {
                        Log.e("PedidosActivity", "Erro da API - isPix: " + isPix + ", Resposta: " + envio[0]);
                        envio[0] = "Erro no servidor: Não foi possível enviar o PIX via WhatsApp. Verifique se o número do cliente está correto e tente novamente.";
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialogEnvio.dismiss();

                            new android.app.AlertDialog.Builder(PedidosActivity.this)
                                    .setTitle(titulo[0])
                                    .setMessage(envio[0])
                                    .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        }
                    });


                }
            }).start();

        }
        catch (Exception ex){
            Log.e("PedidosActivity", "Exceção em EnviarLinksPagamento: " + ex.getMessage(), ex);
            ex.printStackTrace();
        }

        return "Erro ao enviar links de pagamento";

    }

    public String EnviarLinksPagamentoPix(){

        try {

            ProgressDialog progressDialogEnvio = new ProgressDialog(PedidosActivity.this);
            progressDialogEnvio.setTitle("Enviando");
            progressDialogEnvio.setMessage("Por favor aguarde");
            progressDialogEnvio.setCancelable(false);
            progressDialogEnvio.show();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    String[] envio = {helper.ReenviarPixWhatsapp(pedido)};
                    String[] titulo = {"Erro ao enviar links"};

                    if(envio[0]==null) {
                        envio[0] = "Erro ao enviar links de pagamento";
                    }
                    else if(envio[0].contains("Links de pagamento pix enviados para o whatsapp")){
                        titulo[0] = "Links enviado com sucesso";
                    }
                    else if(envio[0].contains("\"Message\":\"Ocorreu um erro.\"") || envio[0].contains("Ocorreu um erro")) {
                        Log.e("PedidosActivity", "Erro da API no EnviarLinksPagamentoPix - Resposta: " + envio[0]);
                        envio[0] = "Erro no servidor: Não foi possível enviar o PIX via WhatsApp. Verifique se o número do cliente está correto e tente novamente.";
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialogEnvio.dismiss();

                            new android.app.AlertDialog.Builder(PedidosActivity.this)
                                    .setTitle(titulo[0])
                                    .setMessage(envio[0])
                                    .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        }
                    });


                }
            }).start();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return "Erro ao enviar links de pagamento";

    }

    public String ReenviarLinksWhatsapp(){

        try {
            Log.d("PedidosActivity", "ReenviarLinksWhatsapp chamada - Pedido: " + pedido.getNumpedven());

            ProgressDialog progressDialogReenvio = new ProgressDialog(PedidosActivity.this);
            progressDialogReenvio.setTitle("Reenviando Links");
            progressDialogReenvio.setMessage("Por favor aguarde");
            progressDialogReenvio.setCancelable(false);
            progressDialogReenvio.show();

            new Thread(new Runnable() {
                @Override
                public void run() {

                    String[] envio = {null};
                    String[] titulo = {"Links reenviados com sucesso!"};

                    try {
                        // Usar o método unificado que detecta automaticamente PIX ou cartão
                        envio[0] = helper.ReenviarLinksWhatsapp(pedido);
                        Log.d("PedidosActivity", "Resultado do reenvio: " + envio[0]);
                        
                    } catch (Exception e) {
                        Log.e("PedidosActivity", "Erro durante reenvio de links: " + e.getMessage(), e);
                        envio[0] = "Erro ao processar reenvio: " + e.getMessage();
                    }

                    if(envio[0]==null) {
                        envio[0] = "Erro ao reenviar links de pagamento";
                    }
                    else if(envio[0].contains("Links de pagamento enviados para o whatsapp") || 
                            envio[0].contains("PIX enviado com sucesso para o WhatsApp") ||
                            envio[0].contains("Mensagem enviada com sucesso")){
                        titulo[0] = "Links reenviados com sucesso!";
                        Log.d("PedidosActivity", "Reenvio bem-sucedido para WhatsApp");
                    }
                    else if(envio[0].contains("Não foram encontrados recursos http")) {
                        Log.e("PedidosActivity", "Erro HTTP 404 - Endpoint não encontrado. Resposta: " + envio[0]);
                        envio[0] = "Erro: Serviço de reenvio temporariamente indisponível. Tente novamente mais tarde.";
                    }
                    else if(envio[0].contains("\"Message\":\"Ocorreu um erro.\"") || envio[0].contains("Ocorreu um erro")) {
                        Log.e("PedidosActivity", "Erro da API - Resposta: " + envio[0]);
                        envio[0] = "Erro no servidor: Não foi possível reenviar os links via WhatsApp. Verifique se o número do cliente está correto e tente novamente.";
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialogReenvio.dismiss();

                            new android.app.AlertDialog.Builder(PedidosActivity.this)
                                    .setTitle(titulo[0])
                                    .setMessage(envio[0])
                                    .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    }).show();
                        }
                    });


                }
            }).start();

        }
        catch (Exception ex){
            Log.e("PedidosActivity", "Exceção em ReenviarLinksWhatsapp: " + ex.getMessage(), ex);
            ex.printStackTrace();
        }

        return "Erro ao reenviar links de pagamento";

    }


}
