package br.com.acalfortaleza.fikonmais;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.adapters.FaturamentoLinhaAdapter;
import br.com.acalfortaleza.fikonmais.adapters.OrcamentoAdapter;
import br.com.acalfortaleza.fikonmais.dao.OrcamentoAlteradoDao;
import br.com.acalfortaleza.fikonmais.helper.FaturamentoHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.FaturamentoLinha;
import br.com.acalfortaleza.fikonmais.models.Orcamento;

public class ConsultaFaturamentoLinhaActivity extends BaseAcalActivity {

    private TextView txtAtingido;
    private TextView txtNaoAtingido;
    private TextView txtSemMeta;
    private TextView txtPremio;
    private FaturamentoHelper helper;
    private ProgressDialog dialog;
    private List<FaturamentoLinha> mList;
    private Handler handler;
    private FaturamentoLinhaAdapter adapter;
    private RecyclerView mRecycleView;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;

    private static String periodoIni="";
    private static String periodoFim="";
    private AlertDialog alertaDialog;
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    final DecimalFormat df = new DecimalFormat("0.00");
    final DecimalFormat dfInteiro = new DecimalFormat("0");
    private LinearLayout ind_atingiu;
    private LinearLayout ind_naoatingiu;
    private LinearLayout ind_semmeta;

    private double percMetaLoja;
    private double percMetaConsultor;
    private double vlPremio;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.fatl_status_bar_bg);
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
        setContentView(R.layout.activity_consulta_faturamento_linha);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.fatl_toolbar);
        setSupportActionBar(toolbar);

        helper = new FaturamentoHelper();
        handler = new Handler();
        loginHelper = new LoginHelper(this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();

        periodoIni = ConsultaFaturamentoActivity.periodoIni;
        periodoFim = ConsultaFaturamentoActivity.periodoFim;

        percMetaLoja = getIntent().getDoubleExtra("metaLoja",0.00d);
        percMetaConsultor = getIntent().getDoubleExtra("metaConsultor",0.00d);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Venda por Linha");
        getSupportActionBar().setElevation(0);

        txtAtingido = findViewById(R.id.fatl_txtatingido);
        txtNaoAtingido = findViewById(R.id.fatl_txtnaoatingido);
        txtSemMeta = findViewById(R.id.fatl_txtSemmeta);
        txtPremio = findViewById(R.id.fatl_valor_premio);
        mRecycleView = findViewById(R.id.fat_lista_linhas);
        ind_atingiu = findViewById(R.id.fat_ind_atingido);
        ind_naoatingiu = findViewById(R.id.fat_ind_naoatingido);
        ind_semmeta = findViewById(R.id.fat_ind_semmeta);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));

         txtPremio.setVisibility(View.INVISIBLE);

        if (adapter == null) {
            txtAtingido.setText("0");
            txtNaoAtingido.setText("0");
            txtSemMeta.setText("0");
            txtPremio.setText("Premiação: R$ 0,00");
        } else {
            txtAtingido.setText(String.valueOf(adapter.QtdAtingido()));
            txtNaoAtingido.setText(String.valueOf(adapter.QtdNaoAtingido()));
            txtSemMeta.setText(String.valueOf(adapter.QtdeSemMeta()));
        }



        ind_atingiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("ATINGIDO");
            }
        });


        ind_naoatingiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("NÃO ATINGIU");
            }
        });


        ind_semmeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.getFilter().filter("S/META");
            }
        });


        ConsultarFaturamento(periodoIni,periodoFim);

    }

    public void ConsultarFaturamento(final String dtinicial, final String dtfinal) {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {


                    mList = helper.GetFaturamentoLinha(dtinicial, dtfinal, Integer.parseInt(usuarioLogado.get("codigo")));

                    if (mList == null || mList.toString().equals("[]")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ConsultaFaturamentoLinhaActivity.this, "Não foram encontrados faturamentos, para o período informado!", Toast.LENGTH_SHORT).show();
                                mList = new ArrayList<>();
                                adapter = new FaturamentoLinhaAdapter(ConsultaFaturamentoLinhaActivity.this, mList);
                                mRecycleView.setAdapter(adapter);
                                txtAtingido.setText("0");
                                txtNaoAtingido.setText("0");
                                txtSemMeta.setText("0");
                            }
                        });
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {


                                adapter = new FaturamentoLinhaAdapter(ConsultaFaturamentoLinhaActivity.this, mList);
                                mRecycleView.setAdapter(adapter);

                                if (adapter == null) {
                                    txtAtingido.setText("0");
                                    txtNaoAtingido.setText("0");
                                    txtSemMeta.setText("0");
                                    txtPremio.setText("Premiação: R$ 0,00");
                                } else {
                                    int qtdeAtingido = adapter.QtdAtingido();
                                    txtAtingido.setText(String.valueOf(qtdeAtingido));
                                    txtNaoAtingido.setText(String.valueOf(adapter.QtdNaoAtingido()));
                                    txtSemMeta.setText(String.valueOf(adapter.QtdeSemMeta()));
                                    if(qtdeAtingido>= 16 && qtdeAtingido< 19 ){
                                        if(percMetaConsultor>=100){
                                            vlPremio = 50.00;
                                        }

                                        if(percMetaLoja  >=100){
                                            vlPremio = 100;
                                        }

                                        txtPremio.setText("Premiação: R$"+df.format(vlPremio));
                                    }else if(qtdeAtingido>= 19 && qtdeAtingido< 23 ){
                                        if(percMetaConsultor>=100){
                                            vlPremio = 75;
                                        }

                                        if(percMetaLoja  >=100){
                                            vlPremio = 150;
                                        }
                                        txtPremio.setText("Premiação: R$"+df.format(vlPremio));
                                    }else if(qtdeAtingido>= 23  ){
                                        if(percMetaConsultor>=100){
                                            vlPremio = 125;
                                        }

                                        if(percMetaLoja  >=100){
                                            vlPremio = 250;
                                        }
                                        txtPremio.setText("Premiação:"+df.format(vlPremio));
                                    }
                                }
                                dialog.dismiss();

                            }
                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ConsultaFaturamentoLinhaActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(ConsultaFaturamentoLinhaActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == android.R.id.home) {


            finish();


        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {


        finish();

    }

}
