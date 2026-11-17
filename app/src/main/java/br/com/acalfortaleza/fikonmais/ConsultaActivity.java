package br.com.acalfortaleza.fikonmais;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.adapters.ProdutosAdapter;
import br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener;
import br.com.acalfortaleza.fikonmais.helper.ConsultaHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.model.FreteServicoResposta;
import br.com.acalfortaleza.fikonmais.models.Estoque;
import br.com.acalfortaleza.fikonmais.models.ImagemCiashop;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.popup.PopupConsultaProduto;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConsultaActivity extends BaseAcalActivity implements SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {


    private String result = "";
    private TextInputLayout layoutEdtcodigo;
    private AlertDialog alertaDialog;
    public static EditText edtCodigo;
    private ProgressDialog dialog;
    private static RecyclerView mRecyclerView;
    private ProdutoGemco produto;
    private ConsultaHelper helper;
    private static ProdutosAdapter adapter;
    private Handler handler;
    public static List<ProdutoGemco> lista = new ArrayList<>();
    private List<ProdutoGemco> retornoGemco;
    private Bundle bundle;
    private ProdutoHelper helperproduto;
    private String caracteristicas;
    private List<Estoque> estoques;
    private List<ImagemCiashop> imagens;
    private ProdutoGemco produtoGemco;
    private ImageView img_notificacao;
    public static FloatingActionButton floatConsultaAvancada;
    private TextView qtd_carrinho;
    private ImageView carrinho;
    public static Orcamento orcamento ;
    public static Pedidos pedido ;
    private Boolean _isClikedAz = false;
    private Boolean _isClikedValor = false;
    public static Boolean _isConsultaTexto = false;
    private static HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    public static Context context;
    private Button btCamera;
    public static Boolean isOrcameno = false;
    public static Boolean isPedido = false;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.consulta_status_bar_bg);
        if (statusBarBg == null) {
            return;
        }

        ViewGroup.LayoutParams params = statusBarBg.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBarBg.setLayoutParams(params);
    }

    private void ajustarBotaoRodape() {
        if (floatConsultaAvancada == null) {
            return;
        }

        final int margemBase = dpToPx(20);
        final ViewGroup.MarginLayoutParams paramsBase = (ViewGroup.MarginLayoutParams) floatConsultaAvancada.getLayoutParams();
        final int margemEndBase = paramsBase.rightMargin;
        final int margemBottomBase = paramsBase.bottomMargin;

        ViewCompat.setOnApplyWindowInsetsListener(floatConsultaAvancada, (view, insets) -> {
            androidx.core.graphics.Insets navigationInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.rightMargin = margemEndBase + navigationInsets.right;
            params.bottomMargin = margemBottomBase + margemBase + navigationInsets.bottom;
            view.setLayoutParams(params);
            return insets;
        });
        ViewCompat.requestApplyInsets(floatConsultaAvancada);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }


    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {

                } else {
                    ConsultarProduto(result.getContents().toString(), "camera");

                }
            });


    public static void carregarLista() {

        adapter = new ProdutosAdapter(context, lista);
        mRecyclerView.setAdapter(adapter);


    }

    @Override
    protected void onResume() {
        super.onResume();

        qtd_carrinho.setText("");
        img_notificacao.setVisibility(View.INVISIBLE);

        if (orcamento != null && pedido == null) {
            List<ItemOrcamento> itens = orcamento.getItens();

            qtd_carrinho.setText(String.valueOf(itens.size()));

            img_notificacao.setVisibility(View.VISIBLE);


        } else if (orcamento == null && pedido != null) {
            List<ItemPedidos> itens = pedido.getItens();

            qtd_carrinho.setText(String.valueOf(itens.size()));

            img_notificacao.setVisibility(View.VISIBLE);


        } else {
            qtd_carrinho.setText("");
            img_notificacao.setVisibility(View.INVISIBLE);
        }

        edtCodigo.setText("");
        btCamera.setClickable(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
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

        //noinspection SimplifiableIfStatement
        if (adapter != null) {
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
        }


        if (id == android.R.id.home) {

            if (orcamento != null && pedido == null) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Ops :|");
                builder.setMessage("Você possui um orçamento iniciado, se sair irá perder, deseja sair?");
                builder
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                orcamento = null;
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("orcamentos").child(usuarioLogado.get("codigo"));
                                myRef.removeValue();
                                Intent itent = new Intent(ConsultaActivity.this, MainActivity.class);
                                startActivity(itent);
                                finishAffinity();

                            }

                        });


                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            } else if (orcamento == null && pedido != null){
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Ops :|");
                builder.setMessage("Você possui um pedido iniciado, se sair irá perder, deseja sair?");
                builder
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                pedido = null;
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pedidos").child(usuarioLogado.get("codigo"));
                                myRef.removeValue();
                                Intent itent = new Intent(ConsultaActivity.this, MainActivity.class);
                                startActivity(itent);
                                finishAffinity();

                            }

                        });


                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();

            } else {
                orcamento = null;
                pedido = null;
                Intent itent = new Intent(ConsultaActivity.this, MainActivity.class);
                startActivity(itent);
                finishAffinity();
                return true;
            }


        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_consulta);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.consulta_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Consulta de produto");
        getSupportActionBar().setElevation(0);


        bundle = new Bundle();

        helper = new ConsultaHelper();
        handler = new Handler();

        helperproduto = new ProdutoHelper(ConsultaActivity.this);

        loginHelper = new LoginHelper(ConsultaActivity.this);

        usuarioLogado = loginHelper.GetUsuarioSalvo();

        lista = new ArrayList<>();

        adapter = new ProdutosAdapter(ConsultaActivity.this, lista);

        context = ConsultaActivity.this;

        layoutEdtcodigo = findViewById(R.id.layoutEdtCodigo);
        edtCodigo = findViewById(R.id.edtCodigo);

        img_notificacao = findViewById(R.id.consulta_notificacao);
        qtd_carrinho = findViewById(R.id.consulta_txtNotificacao);
        floatConsultaAvancada = findViewById(R.id.floatConsultaAvancada);
        ajustarBotaoRodape();
        mRecyclerView = findViewById(R.id.listaCosnsultaPorTexto);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ConsultaActivity.this));
        mRecyclerView.setAdapter(adapter);

        carrinho = findViewById(R.id.consulta_carrinho);

        carrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orcamento != null && pedido == null) {
                    Intent intent = new Intent(ConsultaActivity.this, Orcamento_LancamentoActivity.class);
                    intent.putExtra("orcamento", orcamento);
                    startActivity(intent);
                }

                if (orcamento == null && pedido != null) {
                    Intent intent = new Intent(ConsultaActivity.this, Pedidos_LancamentoActivity.class);
                    intent.putExtra("pedido", pedido);
                    startActivity(intent);
                }
            }
        });

        floatConsultaAvancada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatConsultaAvancada.setClickable(false);
                Intent intent = new Intent(ConsultaActivity.this, PopupConsultaProduto.class);
                startActivity(intent);

            }
        });

        registerForContextMenu(mRecyclerView);

        btCamera = findViewById(R.id.btCamera);


        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtCodigo.setText("");
                btCamera.setClickable(false);

                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.EAN_13);
                options.setPrompt("Sacanei o código de barras");
                options.setCameraId(0);  // Use a specific camera of the device
                options.setBeepEnabled(true);
                options.setBarcodeImageEnabled(true);
                barcodeLauncher.launch(options);

            }
        });



        edtCodigo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || actionId == EditorInfo.IME_ACTION_DONE) {

                    if (edtCodigo.getText().length() > 0) {
                        final String codigoDigitado = edtCodigo.getText().toString().trim();

                        if (isNumericCode(codigoDigitado)) {
                            ConsultarProduto(codigoDigitado, "consulta");
                        } else {
                            if (!ValidarDescricao()) {

                            } else {
                                hideSoftKeyboard();
                                dialog = new ProgressDialog(ConsultaActivity.this);
                                dialog.setMessage("Consultando...");
                                dialog.setCancelable(false);
                                dialog.show();
                                new Thread() {

                                    public void run() {

                                        try {

                                            lista = helperproduto.ConsultarProdutoPorCodigo(
                                                    edtCodigo.getText().toString(), "TEXTO",
                                                    usuarioLogado.get("chaveEstabelecimento"));
                                            _isConsultaTexto = true;

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {

                                                    if(lista!=null && lista.size()>0){
                                                        carregarLista();

                                                    }


                                                }
                                            });


                                        } catch (UnknownHostException e) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(ConsultaActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                                                    Toast.makeText(ConsultaActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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


                        }

                    }
                }


                return false;
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if(lista !=null && lista.size()>0){
                            produto = lista.get(position);
                            ConsultarProdutoListView(produto);
                        }


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {


                    }
                })
        );

        // Carrega valor de frete/serviços do orçamento cedo, assim que a tela carrega
        carregarValorFreteServicoOrcamento();

    }

    // Launch
    public void onButtonClick(View view) {
        barcodeLauncher.launch(new ScanOptions());
    }

    private void startScan() {



        /*final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(ConsultaActivity.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withTrackerColor(R.color.primary)
                .withText("Scanning...")
                .withCenterTracker()
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {

                    @Override
                    public void onResult(Barcode barcode) {
                        result ="";
                        result = (barcode.rawValue);

                        if (!result.equals("") && ValidarBarcode() ) {
                            ConsultarProduto(result, "camera");
                            barcode.rawValue = "";

                        }


                    }




                }).build();



        materialBarcodeScanner.startScan();*/





    }




    public boolean ValidarBarcode() {

        String barcode = result;

        if (barcode.equals("") || barcode.length() > 14) {
            layoutEdtcodigo.setError("Código de barras inválido tente novamente");
            return false;
        } else {
            layoutEdtcodigo.setError(null);
            return true;
        }

    }


    public boolean ValidarDescricao() {

        String texto = edtCodigo.getText().toString();

        if (texto.length() < 5) {
            layoutEdtcodigo.setError("Digite no mínimo 5 letras");
            return false;
        } else {
            layoutEdtcodigo.setError(null);
            return true;
        }

    }

    public void ConsultarProduto(final String codigo, final String tipo) {

        dialog = new ProgressDialog(ConsultaActivity.this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                retornoGemco=null;
                produtoGemco=null;

                try {
                    String estabelecimento = usuarioLogado.get("chaveEstabelecimento");
                    if (estabelecimento == null || estabelecimento.trim().isEmpty()
                            || "0".equals(estabelecimento.trim())) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ConsultaActivity.this,
                                        "Chave do estabelecimento indisponível. Entre novamente no app.",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }

                    if (isNumericCode(codigo) && codigo.length() <= 7) {

                        retornoGemco = helperproduto.ConsultarProdutoPorCodigo(
                                codigo, "CODIGO", estabelecimento);

                    } else if (isNumericCode(codigo) && codigo.length() > 7) {
                        retornoGemco = helperproduto.ConsultarProdutoPorCodigo(
                                codigo, "BARCODE", estabelecimento);

                    }

                    if (retornoGemco == null || retornoGemco.size()==0) {
                        produtoGemco = null;


                    } else {
                        for (ProdutoGemco p : retornoGemco) {
                            produtoGemco = p;


                        }

                        if (produtoGemco.CodigoBarra != null
                                && !produtoGemco.CodigoBarra.isEmpty()
                                && produtoGemco.CodigoBarra.get(0) != null) {
                            estoques = produtoGemco.CodigoBarra.get(0).Estoque;
                        } else {
                            estoques = new ArrayList<>();
                        }


                    }

                    if (produtoGemco == null || retornoGemco.size()==0) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ConsultaActivity.this, "Não foi encontrado nenhum produto com o código: " + codigo, Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {


                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(ConsultaActivity.this, GeralProdutoActivity.class);
                                if (orcamento != null && pedido == null) {
                                    intent.putExtra("orcamento", (Serializable) orcamento);
                                }
                                if (orcamento == null && pedido != null) {
                                    intent.putExtra("pedido", (Serializable) pedido);
                                }

                                intent.putExtra("produtogemco", produtoGemco);
                                intent.putExtra("intentCaller", ConsultaActivity.class.toString());


                                startActivity(intent);
                                if(!tipo.equals("list")) {
                                    if (_isConsultaTexto) {
                                        adapter.mList.clear();
                                        adapter.mListFull.clear();
                                        _isConsultaTexto = false;
                                    }

                                    adapter.mList.add(produtoGemco);
                                    adapter.mListFull.add(produtoGemco);
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        });


                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ConsultaActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(ConsultaActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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

    private boolean isNumericCode(String value) {
        return value != null && value.matches("[0-9]+");
    }


    public void ConsultarProdutoListView(final ProdutoGemco produto) {


        ConsultarProduto(String.valueOf(produto.Codigo),"list");


    }


    @Override
    public void onBackPressed() {
        if (orcamento != null && pedido == null) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Ops :|");
            builder.setMessage("Você possui um orçamento iniciado, se sair irá perder, deseja sair?");
            builder
                    .setCancelable(false)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            orcamento = null;
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("orcamentos").child(usuarioLogado.get("codigo"));
                            myRef.removeValue();
                            Intent itent = new Intent(ConsultaActivity.this, MainActivity.class);
                            startActivity(itent);
                            finishAffinity();

                        }

                    });


            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        } else if (orcamento == null && pedido != null){
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Ops :|");
            builder.setMessage("Você possui um pedido iniciado, se sair irá perder, deseja sair?");
            builder
                    .setCancelable(false)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            pedido = null;
                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pedidos").child(usuarioLogado.get("codigo"));
                            myRef.removeValue();
                            Intent itent = new Intent(ConsultaActivity.this, MainActivity.class);
                            startActivity(itent);
                            finishAffinity();

                        }

                    });


            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();

        } else {
            orcamento = null;
            pedido = null;
            Intent itent = new Intent(ConsultaActivity.this, MainActivity.class);
            startActivity(itent);
            finishAffinity();

        }

    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public boolean onQueryTextSubmit(String s) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (adapter != null) {
            adapter.getFilter().filter(s);

        }
        return false;
    }

    /**
     * Carrega o valor de frete/serviços selecionados para o orçamento via GET no endpoint OrcSelecionado.
     * Deve ser chamado cedo, logo após onCreate, para que o ValorTotal seja somado ao total do orçamento
     * antes de outras operações que possam sobrescrever os valores.
     */
    private void carregarValorFreteServicoOrcamento() {
        // Verifica se há orçamento ativo
        if (orcamento == null) {
            android.util.Log.d("FreteOrc", "carregarValorFreteServicoOrcamento: Sem orçamento ativo, ignorando.");
            return;
        }

        android.util.Log.d("FreteOrc", "carregarValorFreteServicoOrcamento: Iniciando carregamento para orçamento " + (int) orcamento.getNumorc());

        new Thread(() -> {
            try {
                // 1. Configura o cliente Retrofit
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

                // 2. Executa a chamada GET para OrcSelecionado
                android.util.Log.d("FreteOrc", "-> Chamando GET api/v1/FreteServico/OrcSelecionado para orcamento: " + (int) orcamento.getNumorc());
                retrofit2.Response<FreteServicoResposta> response = api.GetFreteServicoOrcamento(token, (int) orcamento.getNumorc()).execute();

                if (response.isSuccessful() && response.body() != null) {
                    FreteServicoResposta servicoResposta = response.body();
                    android.util.Log.d("FreteOrc", "--> Resposta da API: SUCESSO");

                    // 3. Extrai o ValorTotal
                    double valorFreteServico = servicoResposta.getValorTotal();
                    android.util.Log.d("FreteOrc", "---> ValorTotal retornado da API: " + valorFreteServico);

                    // 4. NÃO somamos diretamente em VlTotal aqui para evitar duplicação.
                    // Guardamos o valor do frete no campo específico e no cache global
                    orcamento.setVlFreteSelecionado(valorFreteServico);
                    try {
                        br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.put((int) orcamento.getNumorc(), valorFreteServico);
                        if (servicoResposta.getIdsSelecionados() != null) {
                            br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.idsServicosFretePorOrcamento.put((int) orcamento.getNumorc(), servicoResposta.getIdsSelecionados());
                        }
                    } catch (Exception ex) {
                        // ignore cache put problems
                    }

                    android.util.Log.d("FreteOrc", "----> Orcamento atualizado (frete em cache) com sucesso!");

                } else {
                    android.util.Log.w("FreteOrc", "--> Resposta da API: ERRO ou vazio. Code: " + response.code());
                }

            } catch (Exception e) {
                android.util.Log.e("FreteOrc", "Erro ao carregarValorFreteServicoOrcamento: ", e);
            }
        }).start();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
