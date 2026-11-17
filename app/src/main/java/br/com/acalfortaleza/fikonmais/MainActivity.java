package br.com.acalfortaleza.fikonmais;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.dao.UsuarioDao;
import br.com.acalfortaleza.fikonmais.fragments.MetaDiaFragments;
import br.com.acalfortaleza.fikonmais.fragments.MetaMesFragments;
import br.com.acalfortaleza.fikonmais.fragments.ProjecaoFrafments;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.MainHelper;
import br.com.acalfortaleza.fikonmais.models.Cliente;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.popup.PopupSenhaAcesso;
import br.com.acalfortaleza.fikonmais.service.ApiFluig;
import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;
import okhttp3.OkHttpClient;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {



    private CircleImageView profile;
    private CardView btOrcamento;
    private CardView btconsultarclientes;
    private CardView btconsutaproduto;
    private CardView btcnpj;
    private CardView btconsultarentrega;
    private CardView btIndicadores;
    private CardView btFaturamento;
    private MainHelper helper;
    private LoginHelper helperLogin;
    private UsuarioDao dao;
    private Handler handler;
    private HashMap<String, String> usuarioLogado;
    private Consultor consultor;
    private FancyButton btnMes;
    private FancyButton btnDia;
    private FancyButton btnProjecao;
    private boolean backPressedOnce = false;
    private Handler backPressedHandler = new Handler();
    private static final int BACK_PRESSED_DELAY = 2000;
    private Bundle bundle;
    private static DatabaseReference myRef;
    private static DatabaseReference myRefPed;
    private static DatabaseReference myRefCli;
    private static FirebaseDatabase database;
    private List<Orcamento> orcamentoSalvo = new ArrayList<>();
    private List<Pedidos> pedidoSalvo = new ArrayList<>();
    private List<Cliente> clienteSalvo = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        helperLogin = new LoginHelper(MainActivity.this);

        usuarioLogado = helperLogin.GetUsuarioSalvo();
        helperLogin.logDadosUsuario();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("orcamentos");
        myRefPed = database.getReference("pedidos");
        myRefCli= database.getReference("clientes");



        //ConsultarIndicadoresMes(usuarioLogado.get("cpf"), usuarioLogado.get("filial"), usuarioLogado.get("nome"));


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView txtVersionName = findViewById(R.id.txtVersionName);
        txtVersionName.setText("v" + BuildConfig.VERSION_NAME);

        FloatingActionButton fab = findViewById(R.id.floatCadastrar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SugestoesActivity.class);
                startActivity(intent);

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
        View header = navigationView.getHeaderView(0);

        checaPermissoes();


        dao = new UsuarioDao(this);
        handler = new Handler();
        bundle = new Bundle();

        helper = new MainHelper(header);


        profile = header.findViewById(R.id.nav_profile);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent.Builder customTabsIntentBuilder = new CustomTabsIntent.Builder(null);
                CustomTabsIntent customTabsIntent = customTabsIntentBuilder.build();
                customTabsIntentBuilder.setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.primary));
                customTabsIntentBuilder.setStartAnimations(MainActivity.this, android.R.anim.fade_in, android.R.anim.fade_out);
                customTabsIntentBuilder.setExitAnimations(MainActivity.this, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                customTabsIntentBuilder.setCloseButtonIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_back));
                Uri uri = Uri.parse("http://fluig.acalhomecenter.com.br/portal/p/01/home");
                customTabsIntent.launchUrl(MainActivity.this, uri);
            }
        });


        TextView nomeconsultor = header.findViewById(R.id.nav_consultor);
        String nomeUsuario = usuarioLogado.get("usuario");
        if (nomeUsuario == null || nomeUsuario.trim().isEmpty()) {
            nomeUsuario = usuarioLogado.get("nome");
        }
        nomeconsultor.setText(nomeUsuario);

        TextView filialconsultor = header.findViewById(R.id.nav_filial);
        String codigoEstabelecimento = usuarioLogado.get("codigoEstabelecimento");
        if (codigoEstabelecimento == null || codigoEstabelecimento.trim().isEmpty()) {
            codigoEstabelecimento = usuarioLogado.get("filialorig");
        }
        filialconsultor.setText("FILIAL: " + codigoEstabelecimento);

        TextView codconsultor = header.findViewById(R.id.nav_codconsultor);
        codconsultor.setText("CÓDIGO: " + String.valueOf(usuarioLogado.get("codigo")));

        Switch swDelivery = header.findViewById(R.id.nav_sw_delivery);





        swDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swDelivery.isChecked()){
                    helperLogin.AlterarUsuarioDelivery(true,0,"");
                    usuarioLogado = helperLogin.GetUsuarioSalvo();
                    filialconsultor.setText("FILIAL: " + usuarioLogado.get("filialorig"));

                }else{
                    helperLogin.AlterarUsuarioDelivery(false, Integer.parseInt(usuarioLogado.get("codfilant")),usuarioLogado.get("filialorigant"));
                    usuarioLogado = helperLogin.GetUsuarioSalvo();
                    filialconsultor.setText("FILIAL: " + usuarioLogado.get("filialorig"));
                }
            }
        });




        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);


       /* btnMes = findViewById(R.id.btn_metames);
        btnMes.setBackgroundColor(getResources().getColor(R.color.primary_dark));
        btnMes.setTextColor(getResources().getColor(R.color.icons));
        btnMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMes.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                btnMes.setTextColor(getResources().getColor(R.color.icons));
                btnDia.setBackgroundColor(getResources().getColor(R.color.icons));
                btnDia.setTextColor(getResources().getColor(R.color.primary));
                btnProjecao.setBackgroundColor(getResources().getColor(R.color.icons));
                btnProjecao.setTextColor(getResources().getColor(R.color.primary));
                ConsultarIndicadores(usuarioLogado.get("cpf"), usuarioLogado.get("filial"), usuarioLogado.get("nome"));
                if (consultor != null) {
                    bundle.putDouble("metaMes", consultor.Meta);
                    bundle.putDouble("faturadoMes", consultor.VendaLiquida);
                    bundle.putString("percentMes", consultor.PercentualMeta);
                    bundle.putDouble("devolucao", consultor.Devolucao);
                    bundle.putDouble("desconto", consultor.Desconto);
                    bundle.putString("percentDesconto", consultor.PercDesconto);
                    bundle.putString("percentDevolucao", consultor.PercentDevolucao);
                    bundle.putString("margemMes", consultor.MargemMes);
                    bundle.putDouble("ticketMes", consultor.TicketMes);
                    bundle.putInt("atdpf", consultor.Atdpf);
                    bundle.putInt("atdpj", consultor.Atdpj);
                    bundle.putString("percentualFrete", consultor.percFrete);
                    bundle.putString("percentualAgendamento", consultor.percAgendado);
                    bundle.putInt("pedidosRetira", consultor.qtdeRetira);
                    bundle.putInt("pedidosAgendados", consultor.qtdeAgendado);
                    bundle.putDouble("vlfrete", consultor.vlFrete);
                    bundle.putDouble("freteorig", consultor.freteOrig);


                    MetaMesFragments metafm = new MetaMesFragments();
                    metafm.setArguments(bundle);
                    FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
                    transacao.replace(R.id.frameLayout, metafm);
                    transacao.commit();
                } else {

                }
            }
        });*/


         /*btnDia = findViewById(R.id.btn_metadia);
        btnDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMes.setBackgroundColor(getResources().getColor(R.color.icons));
                btnMes.setTextColor(getResources().getColor(R.color.primary));
                btnDia.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                btnDia.setTextColor(getResources().getColor(R.color.icons));
                btnProjecao.setBackgroundColor(getResources().getColor(R.color.icons));
                btnProjecao.setTextColor(getResources().getColor(R.color.primary));
                ConsultarIndicadores(usuarioLogado.get("cpf"), usuarioLogado.get("filial"), usuarioLogado.get("nome"));
                if (consultor != null) {
                    bundle.putDouble("metaDia", consultor.MetaDoDia);
                    bundle.putDouble("faturadoDia", consultor.VendaDoDia);
                    bundle.putString("percentDia", consultor.PercentualMetaDia);
                    bundle.putString("margemDia", consultor.MargemDia);
                    bundle.putDouble("ticketDia", consultor.TicketDia);
                    MetaDiaFragments metadiafm = new MetaDiaFragments();
                    metadiafm.setArguments(bundle);
                    FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
                    transacao.replace(R.id.frameLayout, metadiafm);
                    transacao.commit();
                } else {

                }
            }
        });*/

        /*btnProjecao = findViewById(R.id.btn_projecao);
        btnProjecao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMes.setBackgroundColor(getResources().getColor(R.color.icons));
                btnMes.setTextColor(getResources().getColor(R.color.primary));
                btnDia.setBackgroundColor(getResources().getColor(R.color.icons));
                btnDia.setTextColor(getResources().getColor(R.color.primary));
                btnProjecao.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                btnProjecao.setTextColor(getResources().getColor(R.color.icons));
                ConsultarIndicadores(usuarioLogado.get("cpf"), usuarioLogado.get("filial"), usuarioLogado.get("nome"));
                if (consultor != null) {
                    bundle.putDouble("meta", consultor.Meta);
                    bundle.putDouble("vendaAnterior", consultor.VendaDiaAnterior);
                    bundle.putDouble("vendaProjetada", consultor.ProjecaoEmValor);
                    bundle.putDouble("projecao", consultor.Projecao);
                    bundle.putString("margemAnterior", consultor.MargemDiaAnterior);
                    ProjecaoFrafments projecaofm = new ProjecaoFrafments();
                    projecaofm.setArguments(bundle);
                    FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
                    transacao.replace(R.id.frameLayout, projecaofm);
                    transacao.commit();
                } else {

                }
            }
        });*/


        btOrcamento = findViewById(R.id.btn_orcamento);
        btOrcamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itent = new Intent(MainActivity.this, OrcamentoActivity.class);
                startActivity(itent);
            }
        });


        btconsultarclientes = findViewById(R.id.btn_clientes);
        btconsultarclientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itent = new Intent(MainActivity.this, ClientesActivity.class);
                startActivity(itent);

            }
        });


        btconsutaproduto = findViewById(R.id.btn_consultar);
        btconsutaproduto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent itent = new Intent(MainActivity.this, ConsultaActivity.class);
                startActivity(itent);


            }
        });


        btcnpj = findViewById(R.id.btn_cnpj);
        btcnpj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PedidosActivity.class);
                startActivity(intent);


            }
        });


        btconsultarentrega = findViewById(R.id.btn_entregas);
        btconsultarentrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent itent = new Intent(MainActivity.this, ConsultaEntregaActivity.class);
                startActivity(itent);

            }
        });

        btIndicadores = findViewById(R.id.btn_Indicadores);
        btIndicadores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usuarioLogado.get("isgerente").toString().equals("SIM")){
                    Intent itent = new Intent(MainActivity.this, IndicadoresGestorActivity.class);
                    startActivity(itent);
                }else{
                    Intent itent = new Intent(MainActivity.this, IndicadoresActivity.class);
                    startActivity(itent);
                }


            }
        });

        btFaturamento = findViewById(R.id.btn_Faturamento);
        btFaturamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(MainActivity.this).isFinishing()) {

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Cupons / Devoluções :)");
                    builder.setMessage("O que deseja ver?");
                    builder
                            .setCancelable(false)
                            .setPositiveButton("Cupons", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    Intent intent = new Intent(MainActivity.this, CupomActivity.class);
                                    startActivity(intent);

                                }

                            });


                    builder.setNegativeButton("Devoluções", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(MainActivity.this, DevolucoesActivity.class);
                            startActivity(intent);

                        }
                    });

                    builder.create().show();

                }

            }
        });

             if(!usuarioLogado.get("codigo").equals("0")){
                 VerificaOrcamento();
                 VerificaPedido();
                 VerificaCliente();
             }



    }




    private void VerificaOrcamento() {


        final Query  query = myRef.orderByKey().equalTo(usuarioLogado.get("codigo"));


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        Orcamento o = snap.getValue(Orcamento.class);
                        orcamentoSalvo.add(o);
                        myRef = null;


                    }
                }

                if (orcamentoSalvo != null && orcamentoSalvo.size() > 0) {
                    if (!(MainActivity.this).isFinishing()) {

                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Ops :|");
                        builder.setMessage("Você possui um orçamento que não foi finalizado, deseja continuar?");
                        builder
                                .setCancelable(false)
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        Intent intent = new Intent(MainActivity.this, Orcamento_LancamentoActivity.class);
                                        intent.putExtra("orcamento", orcamentoSalvo.get(0));
                                        startActivity(intent);

                                    }

                                });


                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {


                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("orcamentos").child(usuarioLogado.get("codigo"));
                                myRef.removeValue();
                                orcamentoSalvo=null;
                                myRef = null;
                                dialog.cancel();
                            }
                        });

                        builder.create().show();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void VerificaPedido() {

        DateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");




        final Query  query = myRefPed.orderByKey().equalTo(usuarioLogado.get("codigo"));


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                         Pedidos p = snap.getValue(Pedidos.class);
                        pedidoSalvo.add(p);
                        myRefPed = null;


                    }
                }



                if (pedidoSalvo != null && pedidoSalvo.size() > 0 ) {
                    if (!(MainActivity.this).isFinishing()) {

                        try {
                            String datapedido;
                            Date hoje = new Date();
                            String dtp = pedidoSalvo.get(0).getDtpedido();
                            if (pedidoSalvo.get(0).getCodStatus() == 9) {
                                datapedido = formataData.format(hoje);
                            } else if (dtp == null || dtp.length() == 0) {
                                datapedido = formataData.format(hoje);
                            } else {
                                Date data = entrada.parse(dtp);
                                datapedido = formataData.format(data);
                            }


                            String datahoje;


                            datahoje = formataData.format(hoje);

                            if(datapedido.equals(datahoje)) {

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Ops :|");
                                builder.setMessage("Você possui um pedido que não foi finalizado, deseja continuar?");
                                builder
                                        .setCancelable(false)
                                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                                Intent intent = new Intent(MainActivity.this, Pedidos_LancamentoActivity.class);
                                                intent.putExtra("pedido", pedidoSalvo.get(0));
                                                startActivity(intent);

                                            }

                                        });


                                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {


                                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pedidos").child(usuarioLogado.get("codigo"));
                                        myRef.removeValue();
                                        pedidoSalvo = null;
                                        myRefPed = null;
                                        dialog.cancel();
                                    }
                                });

                                builder.create().show();

                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void VerificaCliente() {


        final Query  query = myRefCli.orderByKey().equalTo(usuarioLogado.get("codigo"));


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        Cliente c = snap.getValue(Cliente.class);
                        clienteSalvo.add(c);
                        myRefPed = null;


                    }
                }

                if (clienteSalvo != null && clienteSalvo.size() > 0) {
                    if (!(MainActivity.this).isFinishing()) {

                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Ops :|");
                        builder.setMessage("Você possui um cadastro que não foi finalizado, deseja continuar?");
                        builder
                                .setCancelable(false)
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        Intent intent = new Intent(MainActivity.this, ClientesCadastroActivity.class);
                                        intent.putExtra("cliente", clienteSalvo.get(0));
                                        startActivity(intent);

                                    }

                                });


                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {


                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("clientes").child(usuarioLogado.get("codigo"));
                                myRef.removeValue();
                                pedidoSalvo=null;
                                myRefPed = null;
                                dialog.cancel();
                            }
                        });

                        builder.create().show();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private final Runnable backPressedTimeoutAction = new Runnable() {
        @Override
        public void run() {
            backPressedOnce = false;
        }
    };


    @SuppressLint("RestrictedApi")
    public void showMenuImagem(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_foto, popup.getMenu());


        MenuPopupHelper menuHelper = new MenuPopupHelper(this, (MenuBuilder) popup.getMenu(), v);
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.END);
        menuHelper.show();


    }


    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:

                break;
            case R.id.galeria:

                break;
            default:
                break;
        }
        return false;
    }

    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            // Back pressionado

            try {

                if (this.backPressedOnce) {

                    // Finaliza a aplicacao

                    finish();

                    return;
                }

                this.backPressedOnce = true;

                Toast.makeText(this, "Pressione novamente para sair",
                        Toast.LENGTH_SHORT).show();

                backPressedHandler.postDelayed(backPressedTimeoutAction, BACK_PRESSED_DELAY);

            } catch (Exception e) {

            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ConsultarIndicadores(usuarioLogado.get("cpf"), usuarioLogado.get("filial"), usuarioLogado.get("nome"));
        CarregarImagem();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu_orcamento) {
            Intent itent = new Intent(MainActivity.this, OrcamentoActivity.class);
            startActivity(itent);

        } else if (id == R.id.nav_menu_clientes) {
            Intent itent = new Intent(MainActivity.this, ClientesActivity.class);
            startActivity(itent);

        } else if (id == R.id.nav_menu_cnpj) {
            Intent intent = new Intent(MainActivity.this, PedidosActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_menu_entregas) {
            Intent itent = new Intent(MainActivity.this, ConsultaEntregaActivity.class);
            startActivity(itent);

        } else if (id == R.id.nav_menu_consultar) {
            Intent itent = new Intent(MainActivity.this, ConsultaActivity.class);
            startActivity(itent);

        } else if (id == R.id.nav_menu_encomendas) {
            Intent itent = new Intent(MainActivity.this, EncomendasActivity.class);
            startActivity(itent);

        } else if (id == R.id.nav_menu_calculadora) {
            Toast.makeText(MainActivity.this,
                    "Essa opção será disponibilizada nas proximas versões!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_menu_vendas) {
                    Intent itent = new Intent(MainActivity.this, CampanhaActivity.class);
                    startActivity(itent);
        } else if (id == R.id.nav_menu_faq) {
            Intent intent = new Intent(MainActivity.this, SugestoesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_menu_registrar) {
            Intent intent = new Intent(MainActivity.this, PopupSenhaAcesso.class);
            startActivity(intent);
        } else if (id == R.id.nav_menu_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);


            builder.setMessage("Você realmente deseja fazer o logoff?");

            builder
                    .setCancelable(false)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dao.limparBanco();
                            dao.close();
                            finish();
                            startActivity(new Intent(MainActivity.this, SplashActivity.class));

                        }

                    });

            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();

        } else if (id == R.id.nav_menu_sair) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);


            builder.setMessage("Você realmente deseja sair do app?");

            builder
                    .setCancelable(false)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            finish();


                        }

                    });

            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();


        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.maisopcoes) {
            return true;
        }

        if (id == R.id.campanha) {
            Intent itent = new Intent(MainActivity.this, CampanhaActivity.class);
            startActivity(itent);
        }

        if (id == R.id.vendas) {
            String codFilial = usuarioLogado.get("codfil");
            if (codFilial != null && codFilial.equals("3")) {
                Intent itent = new Intent(MainActivity.this, ConsultaFaturamentoActivity.class);
                startActivity(itent);
            } else {
                Intent itent = new Intent(MainActivity.this, MetasActivity.class);
                startActivity(itent);
            }
        }

        if (id == R.id.clientes) {
            Intent itent = new Intent(MainActivity.this, ClientesCadastroActivity.class);
            startActivity(itent);


            return true;
        }

        if (id == R.id.validacao) {
            Intent itent = new Intent(MainActivity.this, ValidacaoEstoqueActivity.class);
            startActivity(itent);
        }


        if (id == R.id.Logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);


            builder.setMessage("Você realmente deseja fazer o logoff?");

            builder
                    .setCancelable(false)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            dao.limparBanco();
                            dao.close();
                            finish();
                            startActivity(new Intent(MainActivity.this, SplashActivity.class));

                        }

                    });

            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();


            return true;
        }

        if (id == R.id.Sair) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);


            builder.setMessage("Você realmente deseja sair do app?");

            builder
                    .setCancelable(false)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            finish();


                        }

                    });

            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();


            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void CarregarImagem() {


        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(ApiFluig.KEY, ApiFluig.SECRET);
        consumer.setTokenWithSecret(ApiFluig.TOKEN, ApiFluig.TOKENSECRET);

        OkHttpClient clientFluig = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();

        Picasso picasso = new Picasso.Builder(MainActivity.this)
                .downloader(new OkHttp3Downloader(clientFluig))
                .build();

        String urlProfile = "http://fluig.acalhomecenter.com.br/social/api/rest/social/image/profile/" + usuarioLogado.get("login") + "/MEDIUM_PICTURE";


        picasso.load(urlProfile)
                .fit()
                .centerCrop()
                .into(profile);

    }


    private void checaPermissoes() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
        }

    }
    //Retirado na versao para nova Activivity de indicadores
   /* public void ConsultarIndicadores(final String cpf, final String filial, final String nome) {

        new Thread() {


            public void run() {

                try {

                    consultor = helperLogin.ConsultarConsultor(cpf, filial, nome);


                } catch (UnknownHostException e) {

                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {

                } finally {


                }

            }


        }.start();

    }*/

    //Retirado na versao para nova Activivity de indicadores
   /* public void ConsultarIndicadoresMes(final String cpf, final String filial, final String nome) {

        new Thread() {


            public void run() {

                try {

                    consultor = helperLogin.ConsultarConsultor(cpf, filial, nome);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (consultor != null) {
                                bundle.putDouble("metaMes", consultor.Meta);
                                bundle.putDouble("faturadoMes", consultor.VendaLiquida);
                                bundle.putString("percentMes", consultor.PercentualMeta);
                                bundle.putDouble("devolucao", consultor.Devolucao);
                                bundle.putDouble("desconto", consultor.Desconto);
                                bundle.putString("percentDesconto", consultor.PercDesconto);
                                bundle.putString("percentDevolucao", consultor.PercentDevolucao);
                                bundle.putString("margemMes", consultor.MargemMes);
                                bundle.putDouble("ticketMes", consultor.TicketMes);
                                bundle.putInt("atdpf", consultor.Atdpf);
                                bundle.putInt("atdpj", consultor.Atdpj);
                                bundle.putString("percentualFrete", consultor.percFrete);
                                bundle.putString("percentualAgendamento", consultor.percAgendado);
                                bundle.putInt("pedidosRetira", consultor.qtdeRetira);
                                bundle.putInt("pedidosAgendados", consultor.qtdeAgendado);
                                bundle.putDouble("vlfrete", consultor.vlFrete);
                                bundle.putDouble("freteorig", consultor.freteOrig);
                                MetaMesFragments metafm = new MetaMesFragments();
                                metafm.setArguments(bundle);
                                FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
                                transacao.replace(R.id.frameLayout, metafm);
                                transacao.commitAllowingStateLoss();
                            }
                        }
                    });

                } catch (UnknownHostException e) {

                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {

                } finally {


                }

            }


        }.start();

    }*/


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
