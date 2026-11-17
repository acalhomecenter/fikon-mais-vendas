package br.com.acalfortaleza.fikonmais;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;
import android.graphics.Color;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.adapters.OrcamentoAdapter;
import br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener;
import br.com.acalfortaleza.fikonmais.dao.OrcamentoAlteradoDao;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.modal.ModalBottomSheetOrc;
import br.com.acalfortaleza.fikonmais.model.FreteServicoResposta;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.popup.PopupConsultaOrcamento;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class OrcamentoActivity extends BaseAcalActivity implements SearchView.OnQueryTextListener,ModalBottomSheetOrc.ActionListener {

    private FloatingActionButton btn_flutuante;
    private FancyButton btn_dia;
    private FancyButton btn_mes;
    private FancyButton btn_periodo;
    private OrcamentoHelper helper;
    private ProgressDialog dialog;
    private List<Orcamento> mList;

    private List<Orcamento> OrcamentoIndividual;
    private Handler handler;
    private OrcamentoAdapter adapter;
    private RecyclerView mRecycleView;
    private TextView txtpendente;
    private TextView txtvencido;
    private TextView txtconcluido;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private SimpleDateFormat formataData;
    private Date dateinicial;
    private Date datefinal;
    public static String periodoIni="";
    public static String periodoFim="";
    private String _valorConsultado;

    public static Boolean _isAlteracao = false;
    private Orcamento orcamento;
    private AlertDialog alertaDialog;
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    final DecimalFormat df = new DecimalFormat("0.00");
    final DecimalFormat dfInteiro = new DecimalFormat("0");
    private LinearLayout ind_pend;
    private LinearLayout ind_venc;
    private LinearLayout ind_conc;
    private Boolean _isClikedAz = false;
    private Boolean _isClikedValor = false;
    private PedidosHelper pedidosHelper;
    private String retornoCarrinho="";
    public static int Codarq;

    private OrcamentoAlteradoDao orcamentoAlteradoDao;


    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.orc_status_bar_bg);
        if (statusBarBg == null) {
            return;
        }

        ViewGroup.LayoutParams params = statusBarBg.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBarBg.setLayoutParams(params);
    }

    private void ajustarBotaoRodape() {
        if (btn_flutuante == null) {
            return;
        }

        final int margemBase = dpToPx(20);
        final ViewGroup.MarginLayoutParams paramsBase = (ViewGroup.MarginLayoutParams) btn_flutuante.getLayoutParams();
        final int margemEndBase = paramsBase.rightMargin;
        final int margemBottomBase = paramsBase.bottomMargin;

        ViewCompat.setOnApplyWindowInsetsListener(btn_flutuante, (view, insets) -> {
            androidx.core.graphics.Insets navigationInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.rightMargin = margemEndBase + navigationInsets.right;
            params.bottomMargin = margemBottomBase + margemBase + navigationInsets.bottom;
            view.setLayoutParams(params);
            return insets;
        });
        ViewCompat.requestApplyInsets(btn_flutuante);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_orcamento);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.orc_toolbar);
        setSupportActionBar(toolbar);

        helper = new OrcamentoHelper();
        handler = new Handler();
        loginHelper = new LoginHelper(this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();
        pedidosHelper = new PedidosHelper();

        orcamentoAlteradoDao = new OrcamentoAlteradoDao(getApplicationContext());

        _valorConsultado = getIntent().getStringExtra("cgccpf");
        _isAlteracao = getIntent().getBooleanExtra("isalteracao",false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Consulta de orçamento");
        getSupportActionBar().setElevation(0);



        formataData = new SimpleDateFormat("yyyy-MM-dd");

        btn_flutuante = findViewById(R.id.orc_btn_flutuante);
        btn_dia = findViewById(R.id.orc_btn_dia);
        btn_mes = findViewById(R.id.orc_btn_mes);
        btn_periodo = findViewById(R.id.orc_btn_periodo);
        txtpendente = findViewById(R.id.orc_txtPendentes);
        txtconcluido = findViewById(R.id.orc_txtConcluidos);
        txtvencido = findViewById(R.id.orc_txtVencidos);
        mRecycleView = findViewById(R.id.orc_lista_orcamento);
        ind_pend = findViewById(R.id.orc_ind_pend);
        ind_venc = findViewById(R.id.orc_ind_venc);
        ind_conc = findViewById(R.id.orc_ind_conc);
        ajustarBotaoRodape();
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecycleView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ModalBottomSheetOrc modalBottomSheet = new ModalBottomSheetOrc();
                        modalBottomSheet.setActionListener(OrcamentoActivity.this);
                        modalBottomSheet.show(getSupportFragmentManager(), "modalMenu");
                        orcamento = mList.get(position);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );



        btn_flutuante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrcamentoActivity.this, ConsultaActivity.class);
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


                    ConsultarOrcamentos(formataData.format(dateinicial),formataData.format(datefinal),0 );


            }
        });

        btn_mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_dia.setBackgroundColor(getResources().getColor(R.color.icons));
                btn_dia.setTextColor(getResources().getColor(R.color.primary));
                btn_mes.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                btn_mes.setTextColor(getResources().getColor(R.color.icons));
                btn_periodo.setBackgroundColor(getResources().getColor(R.color.icons));
                btn_periodo.setTextColor(getResources().getColor(R.color.primary));
                formataData = new SimpleDateFormat("yyyy-MM-dd");

                Date data = new Date();

                datefinal = new Date();

                Calendar c = Calendar.getInstance();
                c.setTime(data);
                c.add(Calendar.MONTH, -1);
                c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

                dateinicial = new Date(c.getTime().getTime());

                ConsultarOrcamentos(formataData.format(dateinicial),formataData.format(datefinal),0 );
            }
        });

        btn_periodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_dia.setBackgroundColor(getResources().getColor(R.color.icons));
                btn_dia.setTextColor(getResources().getColor(R.color.primary));
                btn_mes.setBackgroundColor(getResources().getColor(R.color.icons));
                btn_mes.setTextColor(getResources().getColor(R.color.primary));
                btn_periodo.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                btn_periodo.setTextColor(getResources().getColor(R.color.icons));
                Intent intent = new Intent(OrcamentoActivity.this, PopupConsultaOrcamento.class);
                intent.putExtra("tela", "orcamento");
                startActivity(intent);
            }
        });


        if (adapter == null) {
            txtvencido.setText("0");
            txtpendente.setText("0");
            txtconcluido.setText("0");
        } else {
            txtvencido.setText(String.valueOf(adapter.QtdVencido()));
            txtpendente.setText(String.valueOf(adapter.QtdPendente()));
            txtconcluido.setText(String.valueOf(adapter.QtdConcluido()));
        }


        formataData = new SimpleDateFormat("yyyy-MM-dd");

        dateinicial = new Date();
        datefinal = new Date();


        //ConsultarOrcamentos(formataData.format(dateinicial),formataData.format(datefinal) );


        ind_pend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("PENDENTE");
            }
        });


        ind_venc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("VENCIDO");
            }
        });


        ind_conc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("CONCLUIDO");
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

                    ConsultarOrcamentoConsulta(_valorConsultado, "CODIGO","S","LISTA");
                    _isAlteracao = false;


                }

            } catch (NumberFormatException ex) {

                ConsultarOrcamentoConsulta(_valorConsultado, "TEXTO","S","LISTA");
                _isAlteracao = false;


            }

        }else if (!periodoIni.equals("") && !periodoFim.equals("")   ) {
            ConsultarOrcamentos(periodoIni, periodoFim,Codarq);
            _isAlteracao = false;


        }

        /*if(!periodoIni.equals("") && !periodoFim.equals(""))

            ConsultarOrcamentos(periodoIni,periodoFim );
        periodoFim = "";
        periodoIni = "";*/

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
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("orcamentos").child(usuarioLogado.get("codigo"));
            myRef.removeValue();
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

                ConsultarOrcamentoConsulta(query,"CODIGO","S","LISTA");


                return true;
            }

        } catch (NumberFormatException ex) {

            ConsultarOrcamentoConsulta(query,"TEXTO","S","LISTA");

        }


        return false;
    }


    public void ConsultarOrcamentos(final String dtinicial, final String dtfinal, final int codarq) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {


                    mList = helper.GetOrcamentos(dtinicial, dtfinal, Integer.parseInt(usuarioLogado.get("codigo")), Integer.parseInt(usuarioLogado.get("codfil")), codarq);

                    if (mList == null || mList.toString().equals("[]")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(OrcamentoActivity.this, "Não foram encontrados orçamentos, para o período informado!", Toast.LENGTH_SHORT).show();
                                mList = new ArrayList<>();
                                adapter = new OrcamentoAdapter(OrcamentoActivity.this, mList);
                                mRecycleView.setAdapter(adapter);
                                txtvencido.setText("0");
                                txtpendente.setText("0");
                                txtconcluido.setText("0");
                            }
                        });
                    } else {
                        // Enriquece cada orçamento com valor de frete/serviço ANTES de criar o adapter
                        enriquecerOrcamentosComFrete(mList);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {


                                adapter = new OrcamentoAdapter(OrcamentoActivity.this, mList);
                                mRecycleView.setAdapter(adapter);

                                if (adapter == null) {
                                    txtvencido.setText("0");
                                    txtpendente.setText("0");
                                    txtconcluido.setText("0");
                                } else {
                                    txtvencido.setText(String.valueOf(adapter.QtdVencido()));
                                    txtpendente.setText(String.valueOf(adapter.QtdPendente()));
                                    txtconcluido.setText(String.valueOf(adapter.QtdConcluido()));
                                }
                                dialog.dismiss();

                            }
                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OrcamentoActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(OrcamentoActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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

    public void ConsultarOrcamentoConsulta(final String consulta, final String tipo, final String lista, final String local ) {

        dialog = new ProgressDialog(this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {
                    if(lista.equals("S")){
                        mList = helper.GetOrcamentoConsulta(consulta, tipo);
                    }else{
                        OrcamentoIndividual = helper.GetOrcamentoConsultaIndividual(consulta, tipo);
                    }


                    if (mList == null || mList.size()==0 && lista.equals("S")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(OrcamentoActivity.this, "Não foram encontrados orçamentos, para o parametro informado!", Toast.LENGTH_SHORT).show();
                                mList = new ArrayList<>();
                                adapter = new OrcamentoAdapter(OrcamentoActivity.this, mList);
                                mRecycleView.setAdapter(adapter);
                                txtvencido.setText("0");
                                txtpendente.setText("0");
                                txtconcluido.setText("0");
                            }
                        });
                    } else {

                        // Enriquece cada orçamento com valor de frete/serviço ANTES de criar o adapter
                        if(lista.equals("S")) {
                            enriquecerOrcamentosComFrete(mList);
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                               if(lista.equals("S")){


                                adapter = new OrcamentoAdapter(OrcamentoActivity.this, mList);
                                mRecycleView.setAdapter(adapter);

                                if (adapter == null) {
                                    txtvencido.setText("0");
                                    txtpendente.setText("0");
                                    txtconcluido.setText("0");
                                } else {
                                    txtvencido.setText(String.valueOf(adapter.QtdVencido()));
                                    txtpendente.setText(String.valueOf(adapter.QtdPendente()));
                                    txtconcluido.setText(String.valueOf(adapter.QtdConcluido()));
                                }
                                dialog.dismiss();

                               }else {
                                   if (local.equals("EDITAR")) {


                                       switch (orcamento.getStatus()) {

                                           case "Concluido":
                                               Toast.makeText(OrcamentoActivity.this, "Ops :( Orçamento já concluído, não pode ser alterado!", Toast.LENGTH_SHORT).show();
                                               break;

                                           case "Vencido":
                                               android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OrcamentoActivity.this);
                                               builder.setTitle("Atenção :|");
                                               builder.setMessage("Esta orçamento já está vencido, se prosseguir com a alteração pode haver diferenças no preço. O que deseja fazer ?");
                                               builder
                                                       .setCancelable(false)
                                                       .setPositiveButton("Alterar", new DialogInterface.OnClickListener() {

                                                           public void onClick(DialogInterface dialog, int which) {
                                                               dialog.dismiss();

                                                               orcamentoAlteradoDao.inserir(dfInteiro.format(orcamento.getNumorc()), OrcamentoIndividual.get(0), false);

                                                               Intent intent = new Intent(OrcamentoActivity.this, Orcamento_LancamentoActivity.class);
                                                               intent.putExtra("orcamento", OrcamentoIndividual.get(0));
                                                               intent.putExtra("alteracao", true);
                                                               startActivity(intent);


                                                           }

                                                       });

                                               builder.setNegativeButton("Sair", new DialogInterface.OnClickListener() {

                                                   public void onClick(DialogInterface dialog, int which) {
                                                       dialog.dismiss();
                                                   }
                                               });


                                               builder.create().show();
                                               break;

                                           case "Pendente":

                                               orcamentoAlteradoDao.inserir(dfInteiro.format(orcamento.getNumorc()), OrcamentoIndividual.get(0), false);

                                               Intent intent = new Intent(OrcamentoActivity.this, Orcamento_LancamentoActivity.class);
                                               intent.putExtra("orcamento", OrcamentoIndividual.get(0));
                                               intent.putExtra("alteracao", true);
                                               startActivity(intent);
                                               break;

                                           default:
                                               break;


                                       }

                                   }else if(local.equals("PEDIDO")){

                                       String tipoProduto = "";

                                       for (int i = 0; i < OrcamentoIndividual.get(0).getItens().size(); i++) {

                                           if (OrcamentoIndividual.get(0).getItens().get(i).getNcm().equals("99999999")) {
                                               tipoProduto = "S";
                                           }

                                       }


                                       if (tipoProduto.equals("")) {


                                               if (OrcamentoIndividual.get(0).getCliente().getCodcli() > 0) {
                                               if (OrcamentoIndividual.get(0).getCliente().getSitcred().equals("Bloqueia Venda")) {
                                                   Toast.makeText(OrcamentoActivity.this, "Cadastro do Cliente está com Situaçao: Bloqueia Venda!", Toast.LENGTH_LONG).show();
                                               } else {
                                                   new Thread(){
                                                       public void run(){
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
                                                               int numOrc = (int) OrcamentoIndividual.get(0).getNumorc();
                                                               retrofit2.Response<br.com.acalfortaleza.fikonmais.model.FreteServicoResposta> resp = api.GetFreteServicoOrcamento(token, numOrc).execute();
                                                               java.util.List<Integer> idsResp = new java.util.ArrayList<>();
                                                               double valorServ = 0.0d;
                                                               if (resp.isSuccessful() && resp.body()!=null){
                                                                   valorServ = resp.body().getValorTotal();
                                                                   if (resp.body().getIdsSelecionados()!=null) idsResp = resp.body().getIdsSelecionados();
                                                                   try {
                                                                       br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.put(numOrc, valorServ);
                                                                       br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.put(numOrc, idsResp);
                                                                   } catch (Exception ignore) {}
                                                                   OrcamentoIndividual.get(0).setVlServicosAdicionais(valorServ);
                                                               }
                                                               final java.util.List<Integer> finalIds = idsResp;
                                                               runOnUiThread(() -> {
                                                                   pedidosHelper.GerarPedido(OrcamentoActivity.this, OrcamentoIndividual.get(0), Integer.parseInt(usuarioLogado.get("prazoentrega")), usuarioLogado.get("filialorig"));
                                                                   DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("orcamentos").child(usuarioLogado.get("codigo"));
                                                                   myRef.removeValue();
                                                               });
                                                           } catch (Exception e) {
                                                               runOnUiThread(() -> {
                                                                   pedidosHelper.GerarPedido(OrcamentoActivity.this, OrcamentoIndividual.get(0), Integer.parseInt(usuarioLogado.get("prazoentrega")), usuarioLogado.get("filialorig"));
                                                                   DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("orcamentos").child(usuarioLogado.get("codigo"));
                                                                   myRef.removeValue();
                                                               });
                                                           }
                                                       }
                                                   }.start();
                                               }

                                           } else {
                                               android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(OrcamentoActivity.this);
                                               builder.setTitle("Atenção :|");
                                               builder.setMessage("Cliente não finalizou o cadastro, deseja finalizar?");
                                               builder
                                                       .setCancelable(false)
                                                       .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                                           public void onClick(DialogInterface dialog, int which) {
                                                               dialog.dismiss();

                                                               Intent intent = new Intent(OrcamentoActivity.this, ClientesActivity.class);
                                                               intent.putExtra("cgccpf", OrcamentoIndividual.get(0).getCliente().getCgccpf());
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

                                       } else {

                                           Toast.makeText(OrcamentoActivity.this, "Pedido não pode ser gerado, orçamento possui produtos não cadastrados!", Toast.LENGTH_LONG).show();

                                       }

                                   }
                               }

                            }
                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OrcamentoActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(OrcamentoActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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
    public void onButtonClick(int id) {

        switch (id) {
            case R.id.mod_orc_ligar:
                if (!orcamento.getCliente().getCelular().equals("")) {


                    if (ActivityCompat.checkSelfPermission((this),
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions((this),
                                new String[]{Manifest.permission.CALL_PHONE}, 1);

                    } else {
                        Intent intentLigar = new Intent(Intent.ACTION_CALL);
                        intentLigar.setData(Uri.parse("tel:" + orcamento.getCliente().getDddCelular() + orcamento.getCliente().getCelular()));
                        startActivity(intentLigar);

                    }

                } else {
                    PerguntarEdicao(orcamento.getCliente().getCgccpf());
                }
                break;

            case R.id.mod_orc_wpp:
                if (!orcamento.getCliente().getCelular().equals("")) {


                    Uri uri = Uri.parse("https://wa.me/" + "55" + orcamento.getCliente().getDddCelular() + orcamento.getCliente().getCelular());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);


                } else {

                    PerguntarEdicao(orcamento.getCliente().getCgccpf());
                }
                break;

            case R.id.mod_orc_editar:

                ConsultarOrcamentoConsulta(dfInteiro.format(orcamento.getNumorc()), "CODIO", "N","EDITAR");



                break;

            case R.id.mod_orc_info:

                String resultado = "";
                String especificador = "";
                String carrinho = "";

                double totalPeso = 0.00d;
                for (ItemOrcamento i : orcamento.getItens()) {
                    totalPeso = totalPeso + (i.getPeso() * i.getQtcomp());
                }


                if (orcamento.getArquiteto() != null && !orcamento.getArquiteto().equals("")) {


                    especificador = "<b>" + "Especificador: " + "</b>" + orcamento.getArquiteto();
                }


                if (orcamento.getLink_Carrinho() != null && !orcamento.getLink_Carrinho().equals("")) {


                    especificador = "<b>" + "Link do Carrinho: " + "</b>" + orcamento.getLink_Carrinho();
                }


                double freteServ = Math.max(0.0, orcamento.getVlServicosAdicionais());
                double freteTotal = Math.max(0.0, orcamento.getVlFrete()) + freteServ;
                resultado = resultado + ("<b>" + "Frete: " + "</b>" + real.format(freteTotal) + "<br />" +
                        "<b>" + "Desconto: " + "</b>" + real.format(orcamento.getVlDesconto()) + " <br />" +
                        "<b>" + "Peso: " + "</b>" + df.format(totalPeso) + "kg" + "" + " <br />" +
                        "<b>" + "Qtd. de Itens: " + "</b>" + String.valueOf(orcamento.getItens().size()) + "" + " <br />" +
                        "<b>" + "Orçamento Gemco: " + "</b>" + dfInteiro.format(orcamento.getNumorcGemco()) + "" + " <br />" +
                        "<b>" + "Pedido: " + "</b>" + dfInteiro.format(orcamento.getNumpedven()) + "" + " <br />" +
                        "<b>" + "Status: " + "</b>" + (orcamento.getStatus()) + "" + " <br />" +
                        " <br />" +
                        " <br />" +
                        carrinho + "" + "<br />" +
                        "<br />" +
                        especificador + "" + "<br />" +
                        "<br />");


                AlertDialog.Builder alerta = new AlertDialog.Builder(this);
                alerta.setIcon(R.drawable.ic_card_info);
                alerta.setCancelable(false);
                alerta.setTitle("Orçamento nº " + dfInteiro.format(orcamento.getNumorc()));
                alerta.setMessage(Html.fromHtml(resultado));
                alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                if (orcamento.getLink_Carrinho() != null && !orcamento.getLink_Carrinho().equals("")) {


                    alerta.setNegativeButton("Copiar link", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClipboardManager clipboard = (ClipboardManager)
                                    getSystemService(OrcamentoActivity.CLIPBOARD_SERVICE);

                            ClipData clip = ClipData.newPlainText("simple text", orcamento.getLink_Carrinho());
                            clipboard.setPrimaryClip(clip);

                            Toast.makeText(OrcamentoActivity.this, "Link Copiado!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    });
                }

                alertaDialog = alerta.create();
                alertaDialog.show();
                break;

            case R.id.mod_orc_gerarPedido:
                //Toast.makeText(OrcamentoActivity.this,"Essa opção será disponibilizada em breve!", Toast.LENGTH_SHORT).show();
                //
                if(helper.NecessarioCodigoEncomenda(orcamento)){

                    String validacao = helper.ValidarCodigoEncomenda(orcamento, OrcamentoActivity.this);
                    if(!Objects.equals(validacao, "validado")){
                      break;
                    }

                }

                ConsultarOrcamentoConsulta(dfInteiro.format(orcamento.getNumorc()), "CODIO", "N","PEDIDO");
                break;

            case R.id.mod_orc_gerarCarrinho:
                //Toast.makeText(OrcamentoActivity.this,"Gerando Carrinho!", Toast.LENGTH_SHORT).show();

                String tipoProduto2="";

                for (int i = 0 ; i < orcamento.getItens().size(); i++){

                    if(orcamento.getItens().get(i).getNcm().equals("99999999")){
                        tipoProduto2="S";
                    }

                }

                if(tipoProduto2.equals("")){
                    PerguntarEdicaoGerarCarrinho();
                    break;
                }else{
                    Toast.makeText(this, "Carrinho não pode ser gerado, orçamento possui produtos não cadastrados!", Toast.LENGTH_LONG).show();
                    break;
                }


            case R.id.mod_orc_codigo_encomenda:

                ProgressDialog progressDialog = new ProgressDialog(OrcamentoActivity.this);
                progressDialog.setTitle("Enviando");
                progressDialog.setMessage("Por favor aguarde");
                progressDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String enviar = helper.EnviarCodigoEncomenda(orcamento);

                        if(enviar!=null){

                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(OrcamentoActivity.this);
                                    builder.setCancelable(false);
                                    if(enviar.contains("Código enviado para o whatsapp")){
                                        builder.setTitle("Código enviado");
                                    }
                                    else{
                                        builder.setTitle("Erro ao enviar");
                                    }

                                    builder.setMessage(enviar);
                                    builder.setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            progressDialog.dismiss();
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.show();

                                }
                            });

                        }
                    }
                }).start();

                break;


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

                        Intent intent = new Intent(OrcamentoActivity.this, ClientesActivity.class);
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


    public void PerguntarEdicaoGerarCarrinho(){


        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Atenção :|");
        builder.setMessage("Deseja gerar um carrinho de compra no site?");
        builder
                .setCancelable(false)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();


                           Gerarcarrinho(orcamento);


                    }

                });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builder.create().show();


    }




    public void Gerarcarrinho(Orcamento orcamento) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Gerando Carrinho...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {


                    retornoCarrinho = helper.GerarCarrinho(orcamento);


                }catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OrcamentoActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(OrcamentoActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {
                    if (dialog.isShowing()) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                PackageManager pm = getApplicationContext().getPackageManager();

                                if (!orcamento.getCliente().getCelular().equals("")) {


                                        try{
                                            String url = "https://api.whatsapp.com/send?phone=" + "+55" + orcamento.getCliente().getDddCelular() + orcamento.getCliente().getCelular()+"&text=" + Uri.encode(retornoCarrinho);
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setPackage("com.whatsapp");
                                            i.setData(Uri.parse(url));
                                            startActivity(i);
                                        }catch (Exception e){
                                            try {
                                                String url = "https://api.whatsapp.com/send?phone=" + "+55" + orcamento.getCliente().getDddCelular() + orcamento.getCliente().getCelular() + "&text=" + Uri.encode(retornoCarrinho);
                                                Intent i = new Intent(Intent.ACTION_VIEW);
                                                i.setPackage("com.whatsapp.w4b");
                                                i.setData(Uri.parse(url));
                                                startActivity(i);
                                            }catch (Exception e2){

                                                Toast.makeText(getApplicationContext(), "Carrinho foi gerado, mas não pode ser enviado por wpp Aplicativo não esta instalado!",Toast.LENGTH_LONG).show();
                                            }
                                        }






                                    dialog.dismiss();
                                }else{
                                    ClipboardManager clipboard = (ClipboardManager)
                                            getSystemService(OrcamentoActivity.CLIPBOARD_SERVICE);

                                    ClipData clip = ClipData.newPlainText("simple text", retornoCarrinho);
                                    clipboard.setPrimaryClip(clip);

                                    Toast.makeText(OrcamentoActivity.this,"Link Copiado!",Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();

                            }
                        });


                    }
                }

            }

        }.start();
    }


    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {

        finish();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("orcamentos").child(usuarioLogado.get("codigo"));
        myRef.removeValue();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /**
     * Enriquece cada orçamento com o valor de frete/serviço obtido via GET no endpoint OrcSelecionado.
     * Soma o ValorTotal retornado ao VlTotal do orçamento para cada item na lista.
     * Este método executa de forma síncrona, bloqueando até que todos os orçamentos sejam enriquecidos.
     *
     * @param orcamentos Lista de orçamentos a ser enriquecida
     */
    private void enriquecerOrcamentosComFrete(List<Orcamento> orcamentos) {
        if (orcamentos == null || orcamentos.isEmpty()) {
            android.util.Log.d("FreteOrcList", "enriquecerOrcamentosComFrete: Lista vazia, nada a fazer.");
            return;
        }

        android.util.Log.d("FreteOrcList", "-> Iniciando enriquecimento de " + orcamentos.size() + " orçamentos com valores de frete");

        try {
            // Configura o cliente Retrofit uma única vez para reutilizar
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiGemco.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiGemco api = retrofit.create(ApiGemco.class);
            String token = "Bearer " + ApiGemco.token;

            // Itera sobre cada orçamento
            for (int i = 0; i < orcamentos.size(); i++) {
                Orcamento orc = orcamentos.get(i);
                int numOrc = (int) orc.getNumorc();

                android.util.Log.d("FreteOrcList", "---> Consultando frete para orçamento: " + numOrc);

                try {
                    // Executa a chamada GET
                    retrofit2.Response<FreteServicoResposta> response = api.GetFreteServicoOrcamento(token, numOrc).execute();

                    if (response.isSuccessful() && response.body() != null) {
                        FreteServicoResposta servicoResposta = response.body();
                        double valorFreteServico = servicoResposta.getValorTotal();

                        // Não somamos diretamente em VlTotal para evitar duplicação
                        // Guardamos o valor dos serviços no campo específico e no cache global
                        orc.setVlServicosAdicionais(valorFreteServico);
                        try {
                            br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.put(numOrc, valorFreteServico);
                            // também salva ids selecionados se houver
                            if (servicoResposta.getIdsSelecionados() != null) {
                                br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.put(numOrc, servicoResposta.getIdsSelecionados());
                            }
                        } catch (Exception ex) {
                            // ignore cache put problems
                        }

                        double vlTotalAnterior = orc.getVlTotal();
                        double vlTotalNovo = vlTotalAnterior + valorFreteServico; // apenas para log

                        android.util.Log.d("FreteOrcList", "----> Orçamento " + numOrc + ": VlTotal anterior=" + vlTotalAnterior + ", ValorFrete=" + valorFreteServico + ", VlTotal (exibido)=" + vlTotalNovo);

                    } else {
                        android.util.Log.w("FreteOrcList", "----> GET falhou para orçamento " + numOrc + ". Code: " + response.code());
                    }

                } catch (Exception e) {
                    android.util.Log.e("FreteOrcList", "----> Erro ao consultar frete para orçamento " + numOrc, e);
                }
            }

            android.util.Log.d("FreteOrcList", "-> Enriquecimento concluído!");

        } catch (Exception e) {
            android.util.Log.e("FreteOrcList", "Erro ao enriquecerOrcamentosComFrete: ", e);
        }
    }
}
