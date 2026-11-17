package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.FinanceiroAdapter;
import br.com.acalfortaleza.fikonmais.fragments.AprazoFragment;
import br.com.acalfortaleza.fikonmais.fragments.AvistaFragment;
import br.com.acalfortaleza.fikonmais.helper.CredzHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.helper.FixPayHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.LogHelper;
import br.com.acalfortaleza.fikonmais.models.Credito;
import br.com.acalfortaleza.fikonmais.models.Credz.Plan;
import br.com.acalfortaleza.fikonmais.models.Credz.PlanSimulation;
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.FormaPagamento;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.TheChecker;
import br.com.acalfortaleza.fikonmais.models.PixRequest;
import br.com.acalfortaleza.fikonmais.models.PixResponse;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupFormaPgto extends Activity {


    private TextInputLayout layout_forma;
    private TextInputEditText edtForma;
    public static Button btn_gravar;
    private FrameLayout mFrame;
    private ExpandableListView mListView;
    private Button btn_fechar;
    private PedidosHelper helper;
    private FixPayHelper fixPayHelper;
    private LoginHelper helperLogin;
    private SpinnerDialog spinnerDialog;
    public static Pedidos pedido;
    private Pedidos pedidoAvista;
    private ProgressDialog dialog;
    private ProgressDialog progressDialogPix;
    private Handler handler;
    private List<FormaPagamento> mList;
    private List<Financeiro> mListFin;
    private ArrayList<String> listaForma = new ArrayList<>();
    public static FinanceiroAdapter adapter;
    private HashMap<String, Financeiro> mListItens;
    public static  FormaPagamento forma;
    public static Credito retornoCredito = null;
    public static int numlan = 1 ;
    public static Plan planoEscolhidoCredz;
    public static int codigoIntentPlanosCredz;
    public static double valorTotalPedidoSelecionado = -1d;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_lanc_formaspgto);

        pedido = (Pedidos) getIntent().getSerializableExtra("pedido") ;
        pedidoAvista = (Pedidos) getIntent().getSerializableExtra("pedidoAvista");
        double valorTotalAtualizado = getIntent().getDoubleExtra("valorTotalAtualizado", -1d);
        if (pedido != null && valorTotalAtualizado >= 0d) {
            pedido.setVlTotal(valorTotalAtualizado);
        }
        if (PopupPedFechamento.pedido != null) {
            pedido = PopupPedFechamento.pedido;
        }
        valorTotalPedidoSelecionado = pedido != null ? pedido.getVlTotal() : valorTotalAtualizado;
        if (pedidoAvista == null) {
            pedidoAvista = pedido;
        }
        if (pedido != null) {
            PopupPedFechamento.pedido = pedido;
        }
        Log.d("PopupFormaPgtoDebug",
                "onCreate | totalIntent=" + valorTotalAtualizado
                        + " | totalPopupFechamento=" + (PopupPedFechamento.pedido != null ? PopupPedFechamento.pedido.getVlTotal() : -1d)
                        + " | totalSelecionadoFinal=" + valorTotalPedidoSelecionado);

        helper = new PedidosHelper();
        fixPayHelper = new FixPayHelper();
        helperLogin = new LoginHelper(this);
        handler = new Handler();
        progressDialogPix = new ProgressDialog(this);

        mListFin = new ArrayList<>();

        mListItens = new HashMap<String,Financeiro>();



        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .9));

        layout_forma = findViewById(R.id.pop_form_layoutForma);
        edtForma = findViewById(R.id.pop_form_edtforma);
        btn_gravar = findViewById(R.id.pop_form_btn_Finalizar);
        btn_fechar = findViewById(R.id.pop_form_btnFechar);
        mListView = findViewById(R.id.pop_form_listaForma);

        adapter = new FinanceiroAdapter(this,mListFin   ,mListItens, true);
        mListView.setAdapter(adapter);

        btn_gravar.setEnabled(false);
        btn_gravar.setBackgroundResource(R.drawable.backgroundbuttondisabled);

        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_gravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(planoEscolhidoCredz!=null){
                    String gravarPlano = GravarPlanIdCredz();
                    if(gravarPlano == null || !gravarPlano.equals("Sucesso")){
                        return;
                    }
                }

                if(retornoCredito!=null){
                    PopupPedFechamento.pedido.setStatusCredito(retornoCredito.Retorno);
                }

                sincronizarPedidoComFinanceiroSelecionado();

                numlan = 1;
                PopupPedFechamento.imgFinanceiro.setVisibility(View.VISIBLE);
                PopupPedFechamento.txtCondicao.setVisibility(View.VISIBLE);
                // Verificar se pedido e financeiro não são nulos antes de acessar
                if (pedido != null && pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
                    PopupPedFechamento.txtCondicao.setText(pedido.getFinanceiro().get(0).getCondpgto() + " - "+pedido.getFinanceiro().get(0).getCondicao());
                }
                finish();

            }
        });


        spinnerDialog = new SpinnerDialog(PopupFormaPgto.this, listaForma, "Selecione a forma de pagamento");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtForma.setText(item);
                forma = mList.get(position);
                Pedidos pedidoCondicaoSelecionada = pedido;
                abrirFormaSelecionadaComAvisoSeNecessario(pedidoCondicaoSelecionada, forma);
            }
        });


        edtForma.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtForma.getRight() - edtForma.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        ConsultarCombobox();



    }


    private void sincronizarPedidoComFinanceiroSelecionado() {
        if (pedido == null || FinanceiroAdapter.mList == null || FinanceiroAdapter.mList.isEmpty()) {
            return;
        }

        pedido.setFinanceiro(FinanceiroAdapter.mList);
        PopupPedFechamento.pedido.setFinanceiro(FinanceiroAdapter.mList);

        if (!isPixLinkPgtoLinkSelecionado(FinanceiroAdapter.mList)) {
            return;
        }

        BigDecimal totalFinanceiro = BigDecimal.ZERO;
        for (Financeiro fin : FinanceiroAdapter.mList) {
            totalFinanceiro = totalFinanceiro.add(BigDecimal.valueOf(fin.getValor()));
        }

        pedido.setVlTotal(totalFinanceiro.doubleValue());
        PopupPedFechamento.pedido.setVlTotal(totalFinanceiro.doubleValue());
    }

    private boolean isPixLinkPgtoLinkSelecionado(List<Financeiro> financeiros) {
        if (financeiros == null || financeiros.isEmpty()) {
            return false;
        }

        for (Financeiro fin : financeiros) {
            if ("87".equals(fin.getCondpgto()) && (fin.getForma() == 142 || fin.getCodEve() == 142)) {
                return true;
            }
        }

        return false;
    }

    private void abrirFormaSelecionadaComAvisoSeNecessario(Pedidos pedidoCondicaoSelecionada, FormaPagamento formaSelecionada) {
        if (!deveExibirAvisoReajustePgtoLink(pedidoCondicaoSelecionada, formaSelecionada)) {
            aplicarFormaSelecionada(pedidoCondicaoSelecionada, formaSelecionada);
            return;
        }

        StringBuilder itensPromocionais = new StringBuilder();
        if (pedidoCondicaoSelecionada != null && pedidoCondicaoSelecionada.getItens() != null) {
            for (ItemPedidos item : pedidoCondicaoSelecionada.getItens()) {
                if (isItemPromocional(item)) {
                    if (itensPromocionais.length() > 0) {
                        itensPromocionais.append("\n");
                    }
                    itensPromocionais.append("- ")
                            .append(item.getCoditprod())
                            .append(" - ")
                            .append(item.getDescricao());
                }
            }
        }

        if (itensPromocionais.length() == 0) {
            aplicarFormaSelecionada(pedidoCondicaoSelecionada, formaSelecionada);
            return;
        }

        new AlertDialog.Builder(PopupFormaPgto.this)
                .setTitle("" +
                        "Itens com reajuste")
                .setMessage("Os itens abaixo terão reajuste de valor para condições de pagamento À PRAZO:\n\n" + itensPromocionais)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aplicarFormaSelecionada(pedidoCondicaoSelecionada, formaSelecionada);
                    }
                })
                .show();
    }

    private void aplicarFormaSelecionada(Pedidos pedidoCondicaoSelecionada, FormaPagamento formaSelecionada) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        boolean isPgtoLinkPixLink = isPgtoLinkPixLink(pedidoCondicaoSelecionada, formaSelecionada);
        boolean pedidoAtualPixLinkAvista = isPedidoPixLinkAvista(pedidoCondicaoSelecionada);
        boolean formaAvistaSelecionada = isFormaAvista(formaSelecionada);

        if (isPgtoLinkPixLink || (pedidoAtualPixLinkAvista && formaAvistaSelecionada)) {
            pedido = pedidoAvista != null ? pedidoAvista : pedidoCondicaoSelecionada;
            PopupPedFechamento.pedido = pedido;
            if (isPgtoLinkPixLink) {
                Toast.makeText(
                        PopupFormaPgto.this,
                        "Preço atualizado para PAGAMENTO A VISTA",
                        Toast.LENGTH_LONG
                ).show();
            }
        } else {
            pedido = pedidoCondicaoSelecionada;
            PopupPedFechamento.pedido = pedidoCondicaoSelecionada;
        }

        valorTotalPedidoSelecionado = pedido != null ? pedido.getVlTotal() : valorTotalPedidoSelecionado;

        PopupPedFechamento.atualizarTotalExibido(pedido);

        if (formaSelecionada.getCodEve() == 1 || formaSelecionada.getCodEve() == 29 || formaSelecionada.getCodEve() == 135) {
            ft.replace(R.id.pop_form_frame, new AvistaFragment());
            ft.commit();
            return;
        }

        if (Integer.parseInt(formaSelecionada.getForma()) == 15 || Integer.parseInt(formaSelecionada.getForma()) == 7) {
            BigDecimal valor = new BigDecimal("0.00");
            if (PopupFormaPgto.adapter.mList.size() > 0) {
                for (int i = 0; i < PopupFormaPgto.adapter.mList.size(); i++) {
                    valor = valor.add(BigDecimal.valueOf(PopupFormaPgto.adapter.mList.get(i).getValor()));
                }
                valor = BigDecimal.valueOf(pedido.getVlTotal()).subtract(valor);
            } else {
                valor = valor.add(BigDecimal.valueOf(pedido.getVlTotal()));
            }

            CosnultarCredito(valor.doubleValue());
        } else if (formaSelecionada.getReseve().equals("CREDZ")) {
            planoEscolhidoCredz = null;
            PopupPedFechamento.planoEscolhidoCredz = null;
            codigoIntentPlanosCredz = new Random().nextInt(Integer.MAX_VALUE);
            ft.replace(R.id.pop_form_frame, new AprazoFragment());
            ft.commit();
        } else {
            ft.replace(R.id.pop_form_frame, new AprazoFragment());
            ft.commit();
        }
    }

    private boolean deveExibirAvisoReajustePgtoLink(Pedidos pedidoSelecionado, FormaPagamento formaSelecionada) {
        if (pedidoSelecionado == null || formaSelecionada == null) {
            return false;
        }

        if (pedidoSelecionado.getFinanceiro() == null || pedidoSelecionado.getFinanceiro().isEmpty()) {
            return false;
        }

        if (isPedidoPixLinkAvista(pedidoSelecionado) && isFormaAvista(formaSelecionada)) {
            return false;
        }

        return "87".equals(pedidoSelecionado.getFinanceiro().get(0).getCondpgto())
                && formaSelecionada.getCodEve() != 142;
    }

    private boolean isItemPromocional(ItemPedidos item) {
        return item != null && item.getSituacao() != null && "PR".equalsIgnoreCase(item.getSituacao().trim());
    }

    private boolean isPgtoLinkPixLink(Pedidos pedidoSelecionado, FormaPagamento formaSelecionada) {
        if (pedidoSelecionado == null || formaSelecionada == null) {
            return false;
        }

        if (pedidoSelecionado.getFinanceiro() == null || pedidoSelecionado.getFinanceiro().isEmpty()) {
            return false;
        }

        return "87".equals(pedidoSelecionado.getFinanceiro().get(0).getCondpgto())
                && formaSelecionada.getCodEve() == 142;
    }

    private boolean isPedidoPixLinkAvista(Pedidos pedidoSelecionado) {
        if (pedidoSelecionado == null || pedidoSelecionado.getFinanceiro() == null || pedidoSelecionado.getFinanceiro().isEmpty()) {
            return false;
        }

        Financeiro financeiroAtual = pedidoSelecionado.getFinanceiro().get(0);
        return financeiroAtual != null
                && "87".equals(financeiroAtual.getCondpgto())
                && (financeiroAtual.getForma() == 142 || financeiroAtual.getCodEve() == 142);
    }

    private boolean isFormaAvista(FormaPagamento formaSelecionada) {
        if (formaSelecionada == null) {
            return false;
        }

        return formaSelecionada.getCodEve() == 1
                || formaSelecionada.getCodEve() == 29
                || formaSelecionada.getCodEve() == 135
                || formaSelecionada.getCodEve() == 142;
    }

    public  void CosnultarCredito(double valor){

        dialog = new ProgressDialog(this);
        dialog.setTitle("Verificando Credito...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {

                    retornoCredito = helper.VerificarCredito(String.valueOf(pedido.getCliente().getCodcli()),valor);


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
                }finally {
                    dialog.dismiss();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.pop_form_frame, new AprazoFragment());
                    ft.commit();
                }
            }


        }.start();



    }


    public void ConsultarCombobox (){

        dialog = new ProgressDialog(PopupFormaPgto.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(){


            public void run() {

                try {

                    // Verificar se pedido e financeiro não são nulos
                    if (pedido == null || pedido.getFinanceiro() == null || pedido.getFinanceiro().isEmpty()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(PopupFormaPgto.this)
                                    .setTitle("Erro")
                                    .setMessage("Dados do pedido não encontrados. Tente novamente.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    }).show();
                            }
                        });
                        return;
                    }

                   String cond = pedido.getFinanceiro().get(0).getCondpgto();
                    mList =   helper.GetFormas(pedido.getCodfil(),cond);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if(mList != null & mList.size()>0) {

                                for (FormaPagamento f : mList) {
                                    listaForma.add(f.getDescricao());
                                }
                            }
                        }
                    });



                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupFormaPgto.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    finish();
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
                            new AlertDialog.Builder(PopupFormaPgto.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==codigoIntentPlanosCredz){

            if(planoEscolhidoCredz!=null && planoEscolhidoCredz.getPlanId()>0) {
                PopupPedFechamento.planoEscolhidoCredz = planoEscolhidoCredz;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.pop_form_frame, new AprazoFragment());
                ft.commit();
            }
            else{
                edtForma.setText("");
                forma = null;
            }

        }

    }

    private String GravarPlanIdCredz(){

        dialog = new ProgressDialog(PopupFormaPgto.this);
        dialog.setTitle("Aguarde...");
        dialog.setMessage("Gravando plano Credz escolhido");
        dialog.show();

        final CountDownLatch countDown = new CountDownLatch(1);
        final String[] gravarPlano = {null};

        try {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    gravarPlano[0] = null;

                    try {

                        CredzHelper credzHelper = new CredzHelper();
                        gravarPlano[0] = credzHelper.GravarPlanId(String.valueOf(planoEscolhidoCredz.getPlanId()), String.valueOf((int) pedido.getNumpedven()));

                    } catch (Exception ex) {
                        Log.e(this.getClass().toString(), "Erro na função GravarPlanIdCredz", ex);
                    }

                    dialog.dismiss();

                    if (gravarPlano[0] == null || !gravarPlano[0].equals("Sucesso")) {

                        new AlertDialog.Builder(PopupFormaPgto.this)
                                .setTitle("Erro ao gravar plano")
                                .setMessage("Não foi possível gravar plano Credz escolhido. " +
                                        "Escolha outra forma de pagamento ou tente mais tarde")
                                .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .create().show();

                    }

                    countDown.countDown();

                }
            }).start();

            countDown.await();
            return gravarPlano[0];

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return null;

    }

    private void GerarPixCopiaECola() {
        // Exibir loading simples imediatamente na UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialogPix.setMessage("Aguardando Requisição...");
                progressDialogPix.setCancelable(false);
                progressDialogPix.show();
            }
        });
        
        // Limpar log anterior e iniciar nova sessão
        LogHelper.clearLog(this);
        LogHelper.writePixLog(this, "INICIO_GERACAO_PIX", "Iniciando processo de geração de PIX");
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("GerarPixCopiaECola", "Iniciando geração de PIX");
                    LogHelper.writePixLog(PopupFormaPgto.this, "THREAD_INICIADA", "Thread de geração PIX iniciada");
                    
                    // Obter dados do usuário logado
                    HashMap<String, String> usuarioLogado = helperLogin.GetUsuarioSalvo();
                    String codfil = usuarioLogado.get("codfil");
                    String nomeUsuario = usuarioLogado.get("nome");
                    String codUser = usuarioLogado.get("coduser");
                    
                    Log.d("GerarPixCopiaECola", "Dados do usuário - codfil: " + codfil + ", nome: " + nomeUsuario + ", codUser: " + codUser);
                    LogHelper.writePixLog(PopupFormaPgto.this, "DADOS_USUARIO", "codfil: " + codfil + ", nome: " + nomeUsuario + ", codUser: " + codUser);
                    
                    // Obter email da filial baseado no codfil
                    String userEmail = obterEmailPorCodfil(Integer.parseInt(codfil));
                    Log.d("GerarPixCopiaECola", "Email da filial: " + userEmail);
                    LogHelper.writePixLog(PopupFormaPgto.this, "EMAIL_FILIAL", "Email obtido para codfil " + codfil + ": " + userEmail);
                    
                    // Dados do pedido
                    LogHelper.writePixLog(PopupFormaPgto.this, "DADOS_PEDIDO", "Valor total: " + pedido.getVlTotal() + ", Número pedido: " + pedido.getNumpedven());
                    
                    // Criar PixRequest
                    PixRequest pixRequest = new PixRequest();
                    pixRequest.setValue(BigDecimal.valueOf(pedido.getVlTotal()));
                    pixRequest.setMessage("PIX ACAL - Pedido: " + (long)pedido.getNumpedven());
                    pixRequest.setCaptura_id(Integer.parseInt(codfil));
                    pixRequest.setUsuario(nomeUsuario);
                    pixRequest.setUsuario_id(Integer.parseInt(codUser));
                    pixRequest.setUser_email(userEmail);
                    pixRequest.setPartner_id(1);
                    
                    Log.d("GerarPixCopiaECola", "PixRequest criado - valor: " + pedido.getVlTotal() + ", codfil: " + codfil);
                    LogHelper.writePixRequest(PopupFormaPgto.this, "PixRequest{" +
                        "value=" + pixRequest.getValue() +
                        ", message='" + pixRequest.getMessage() + '\'' +
                        ", captura_id=" + pixRequest.getCaptura_id() +
                        ", usuario='" + pixRequest.getUsuario() + '\'' +
                        ", usuario_id=" + pixRequest.getUsuario_id() +
                        ", user_email='" + pixRequest.getUser_email() + '\'' +
                        ", partner_id=" + pixRequest.getPartner_id() +
                        "}");
                    
                    // Gerar PIX
                    FixPayHelper fixPayHelper = new FixPayHelper();
                    Log.d("GerarPixCopiaECola", "Chamando FixPayHelper.GerarPix...");
                    LogHelper.writePixLog(PopupFormaPgto.this, "CHAMANDO_FIXPAY_HELPER", "Iniciando chamada para FixPayHelper.GerarPix com codfil: " + codfil);
                    
                    PixResponse response = fixPayHelper.GerarPix(PopupFormaPgto.this, pixRequest, Integer.parseInt(codfil), true, null);
                    
                    Log.d("GerarPixCopiaECola", "Resposta recebida: " + (response != null ? "não nula" : "nula"));
                    LogHelper.writePixLog(PopupFormaPgto.this, "RESPOSTA_RECEBIDA", "Response: " + (response != null ? "não nula" : "nula"));
                    
                    if (response != null) {
                        // LOG DETALHADO: TODOS os campos do PixResponse
                        Log.d("PixResponse_COMPLETO", "=== TODOS OS CAMPOS DO PIXRESPONSE ===");
                        Log.d("PixResponse_COMPLETO", "payment_date: " + response.getPayment_date());
                        
                        Log.d("GerarPixCopiaECola", "Response.getData(): " + (response.getData() != null ? "não nulo" : "nulo"));
                        LogHelper.writePixResponse(PopupFormaPgto.this, "Response.getData(): " + (response.getData() != null ? "não nulo" : "nulo"));
                        
                        if (response.getData() != null) {
                            PixResponse.Data data = response.getData();
                            
                            // LOG DETALHADO: TODOS os campos do Data
                            Log.d("PixResponse_DATA", "=== TODOS OS CAMPOS DO DATA ===");
                            Log.d("PixResponse_DATA", "txid: " + data.getTxid());
                            Log.d("PixResponse_DATA", "revisao: " + data.getRevisao());
                            Log.d("PixResponse_DATA", "status: " + data.getStatus());
                            Log.d("PixResponse_DATA", "pixCopiaECola: " + data.getPixCopiaECola());
                            Log.d("PixResponse_DATA", "location: " + data.getLocation());
                            Log.d("PixResponse_DATA", "chave: " + data.getChave());
                            Log.d("PixResponse_DATA", "solicitacaoPagador: " + data.getSolicitacaoPagador());
                            Log.d("PixResponse_DATA", "pix: " + data.getPix());
                            
                            // LOG DETALHADO: Campos do Calendario
                            if (data.getCalendario() != null) {
                                Log.d("PixResponse_CALENDARIO", "=== CAMPOS DO CALENDARIO ===");
                                Log.d("PixResponse_CALENDARIO", "criacao: " + data.getCalendario().getCriacao());
                                Log.d("PixResponse_CALENDARIO", "expiracao: " + data.getCalendario().getExpiracao());
                            } else {
                                Log.d("PixResponse_CALENDARIO", "Calendario é nulo");
                            }
                            
                            // LOG DETALHADO: Campos do Devedor
                            if (data.getDevedor() != null) {
                                Log.d("PixResponse_DEVEDOR", "=== CAMPOS DO DEVEDOR ===");
                                Log.d("PixResponse_DEVEDOR", "cnpj: " + data.getDevedor().getCnpj());
                                Log.d("PixResponse_DEVEDOR", "nome: " + data.getDevedor().getNome());
                            } else {
                                Log.d("PixResponse_DEVEDOR", "Devedor é nulo");
                            }
                            
                            // LOG DETALHADO: Campos do Loc
                            if (data.getLoc() != null) {
                                Log.d("PixResponse_LOC", "=== CAMPOS DO LOC ===");
                                Log.d("PixResponse_LOC", "id: " + data.getLoc().getId());
                                Log.d("PixResponse_LOC", "location: " + data.getLoc().getLocation());
                                Log.d("PixResponse_LOC", "tipoCob: " + data.getLoc().getTipoCob());
                                Log.d("PixResponse_LOC", "criacao: " + data.getLoc().getCriacao());
                            } else {
                                Log.d("PixResponse_LOC", "Loc é nulo");
                            }
                            
                            // LOG DETALHADO: Campos do Valor
                            if (data.getValor() != null) {
                                Log.d("PixResponse_VALOR", "=== CAMPOS DO VALOR ===");
                                Log.d("PixResponse_VALOR", "original: " + data.getValor().getOriginal());
                                Log.d("PixResponse_VALOR", "modalidadeAlteracao: " + data.getValor().getModalidadeAlteracao());
                            } else {
                                Log.d("PixResponse_VALOR", "Valor é nulo");
                            }
                            
                            Log.d("GerarPixCopiaECola", "PixCopiaECola: " + (response.getData().getPixCopiaECola() != null ? response.getData().getPixCopiaECola() : "nulo"));
                            LogHelper.writePixResponse(PopupFormaPgto.this, "PixCopiaECola: " + (response.getData().getPixCopiaECola() != null ? response.getData().getPixCopiaECola() : "nulo"));
                        }
                    } else {
                        Log.d("PixResponse_COMPLETO", "=== RESPONSE É COMPLETAMENTE NULO ===");
                        LogHelper.writePixError(PopupFormaPgto.this, "Response é nula - possível problema na comunicação com a API");
                    }
                    
                    PopupFormaPgto.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialogPix.dismiss();
                            if (response != null && response.getData() != null && response.getData().getPixCopiaECola() != null) {
                                Log.d("GerarPixCopiaECola", "PIX gerado com sucesso!");
                                LogHelper.writePixLog(PopupFormaPgto.this, "PIX_SUCESSO", "PIX gerado com sucesso! Código: " + response.getData().getPixCopiaECola());
                                LogHelper.writePixLog(PopupFormaPgto.this, "LOG_PATH", "Arquivo de log salvo em: " + LogHelper.getLogPath(PopupFormaPgto.this));
                                Toast.makeText(PopupFormaPgto.this, "PIX gerado com sucesso!", Toast.LENGTH_LONG).show();
                                // Aqui você pode adicionar lógica para mostrar o código PIX ou link
                            } else {
                                Log.e("GerarPixCopiaECola", "Erro: resposta inválida ou PIX não gerado");
                                String errorMsg = "Erro ao gerar PIX. ";
                                String logError = "";
                                if (response == null) {
                                    errorMsg += "Resposta nula da API.";
                                    logError = "Response é completamente nula";
                                } else if (response.getData() == null) {
                                    errorMsg += "Dados não retornados.";
                                    logError = "Response.getData() é nulo";
                                } else if (response.getData().getPixCopiaECola() == null) {
                                    errorMsg += "PIX não gerado.";
                                    logError = "PixCopiaECola é nulo";
                                }
                                LogHelper.writePixError(PopupFormaPgto.this, "ERRO_FINAL: " + logError);
                                LogHelper.writePixLog(PopupFormaPgto.this, "LOG_PATH", "Arquivo de log salvo em: " + LogHelper.getLogPath(PopupFormaPgto.this));
                                Toast.makeText(PopupFormaPgto.this, errorMsg + " Tente novamente.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    
                } catch (Exception e) {
                    Log.e("GerarPixCopiaECola", "Exceção capturada: " + e.getMessage(), e);
                    LogHelper.writePixError(PopupFormaPgto.this, "EXCECAO_CAPTURADA: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    LogHelper.writePixError(PopupFormaPgto.this, "STACK_TRACE: " + android.util.Log.getStackTraceString(e));
                    LogHelper.writePixLog(PopupFormaPgto.this, "LOG_PATH", "Arquivo de log salvo em: " + LogHelper.getLogPath(PopupFormaPgto.this));
                    
                    PopupFormaPgto.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialogPix.dismiss();
                            Toast.makeText(PopupFormaPgto.this, "Erro ao gerar PIX: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Limpar handlers para evitar memory leaks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        
        // Fechar ProgressDialog se estiver aberto
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        
        // Fechar ProgressDialog do PIX se estiver aberto
        if (progressDialogPix != null && progressDialogPix.isShowing()) {
            progressDialogPix.dismiss();
            progressDialogPix = null;
        }
        
        // Limpar referências estáticas para evitar vazamentos de contexto
        btn_gravar = null;
        pedido = null;
        adapter = null;
        forma = null;
        retornoCredito = null;
        planoEscolhidoCredz = null;
        
        // Limpar spinner dialog
        if (spinnerDialog != null) {
            spinnerDialog = null;
        }
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

}
