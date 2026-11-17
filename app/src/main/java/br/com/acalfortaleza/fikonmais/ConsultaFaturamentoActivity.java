package br.com.acalfortaleza.fikonmais;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.google.gson.Gson;

import br.com.acalfortaleza.fikonmais.helper.FaturamentoHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Faturamento;
import br.com.acalfortaleza.fikonmais.popup.PopupConsultaOrcamento;

public class ConsultaFaturamentoActivity extends BaseAcalActivity {

    private FloatingActionButton btn_consultar;
    private TextView txt_titulo;
    private TextView txt_Venda;
    private TextView txt_Devolucao;
    private TextView txt_Margem;
    private TextView txt_Promocao;
    private TextView txt_Normais;
    private TextView txt_Inativos;
    private TextView txt_Comissao;
    private TextView txt_Premiacao;
    private TextView txt_Total;
    private TextView txt_Periodo;
    private TextView txt_PercMeta;
    private TextView txt_PercMetaLoja;
    private TextView txt_MetaMargemP1;
    private ProgressDialog dialog;
    private AlertDialog alertaDialog;
    private Faturamento faturamento;
    private Handler handler;
    private FaturamentoHelper helper;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    final DecimalFormat df = new DecimalFormat("0.00");
    public static String periodoIni = "";
    public static String periodoFim = "";
    public static boolean deveConsultarAoRetornar = false;
    private volatile boolean consultaCancelada = false;
    private double percMetaLoja;
    private double percMetaConsultor;
    private Button btn_fatLinha;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.fat_status_bar_bg);
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
        setContentView(R.layout.activity_consulta_faturamento);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.fat_toolbar);
        setSupportActionBar(toolbar);

        helper = new FaturamentoHelper();
        handler = new Handler();
        loginHelper = new LoginHelper(this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();

        df.setRoundingMode(RoundingMode.DOWN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Consulta de faturamento");
        getSupportActionBar().setElevation(0);


        txt_titulo = findViewById(R.id.fat_titulofat);
        txt_Venda = findViewById(R.id.fat_txtFaturamento);
        txt_Devolucao = findViewById(R.id.fat_txtDevolucao);
        txt_Margem = findViewById(R.id.fat_txtMargem);
        btn_consultar = findViewById(R.id.fat_btn_flutuante);
        txt_Periodo = findViewById(R.id.fat_txtPeriodo);
        txt_Promocao = findViewById(R.id.fat_txtpromocao);
        txt_Inativos = findViewById(R.id.fat_txtInativo);
        txt_Normais = findViewById(R.id.fat_txtNormais);
        txt_Comissao = findViewById(R.id.fat_txtcomissao);
        txt_Premiacao = findViewById(R.id.fat_txtPremiacao);
        txt_Total = findViewById(R.id.fat_txtTotal);
        txt_PercMeta = findViewById(R.id.fat_txtPercMeta);
        txt_PercMetaLoja = findViewById(R.id.fat_txtPercMetaLoja);
        txt_MetaMargemP1 = findViewById(R.id.fat_txtMetaMargemP1);
        btn_fatLinha = findViewById(R.id.fat_consulta_btnLinha);

        btn_fatLinha.setVisibility(View.INVISIBLE);

        btn_fatLinha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConsultaFaturamentoActivity.this, ConsultaFaturamentoLinhaActivity.class);
                intent.putExtra("metaLoja",percMetaLoja);
                intent.putExtra("metaConsultor", percMetaConsultor);
                startActivity(intent);
                finish();
            }
        });


        btn_consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConsultaFaturamentoActivity.this, PopupConsultaOrcamento.class);
                intent.putExtra("tela", "faturamento");
                startActivity(intent);
            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deveConsultarAoRetornar && !periodoIni.equals("") && !periodoFim.equals("")) {
            deveConsultarAoRetornar = false;
            ConsultarFaturamento(periodoIni, periodoFim);
        }
    }

    public void ConsultarFaturamento(final String dtinicial, final String dtfinal) {
        consultaCancelada = false;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Parar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                consultaCancelada = true;
                helper.cancelarConsultaFaturamentoAtual();
                dialogInterface.dismiss();
                Toast.makeText(ConsultaFaturamentoActivity.this, "Consulta cancelada.", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();


        new Thread() {

            public void run() {

                try {
                    final int codigoConsultor = Integer.parseInt(usuarioLogado.get("codigo"));
                    final String filialOrig = usuarioLogado.get("filialorig");
                    final String filialConsulta = "";

                    Log.d("FaturamentoTelaDebug",
                            "ConsultarFaturamento"
                                    + " | dtinicial=" + dtinicial
                                    + " | dtfinal=" + dtfinal
                                    + " | codigoConsultor=" + codigoConsultor
                                    + " | filialOrigSessao=" + filialOrig
                                    + " | filialEnviada=" + filialConsulta);

                    faturamento = helper.GetFaturamento(dtinicial, dtfinal, codigoConsultor, filialConsulta);

                    if (consultaCancelada) {
                        Log.d("FaturamentoTelaDebug", "Consulta cancelada antes de atualizar a tela.");
                        return;
                    }

                    Log.d("FaturamentoTelaDebug", "retorno_faturamento=" + new Gson().toJson(faturamento));

                    if (faturamento == null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (consultaCancelada) {
                                    return;
                                }
                                Toast.makeText(ConsultaFaturamentoActivity.this, "Não foram encontrados valores, para o período informado!", Toast.LENGTH_SHORT).show();


                            }
                        });
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (consultaCancelada) {
                                    return;
                                }
                                CarregaCard(faturamento);
                            }

                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (consultaCancelada) {
                                return;
                            }
                            Toast.makeText(ConsultaFaturamentoActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            if (consultaCancelada) {
                                return;
                            }
                            Toast.makeText(ConsultaFaturamentoActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    });
                }
            }

        }.start();

    }

    @Override
    protected void onDestroy() {
        helper.cancelarConsultaFaturamentoAtual();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDestroy();
    }


    public void CarregaCard(Faturamento faturamento){




        txt_Venda.setText(real.format(faturamento.VendaLiquida));

        txt_Devolucao.setText(real.format(faturamento.Devolucoes));

        txt_Margem.setText(df.format(faturamento.Margem*100)+"%");

        txt_Promocao.setText(real.format(faturamento.VlPromocao));

        txt_Inativos.setText(real.format(faturamento.VlInativo));

        txt_Normais.setText(real.format(faturamento.VlNormal));

        txt_Comissao.setText(real.format(faturamento.Comissao));

        txt_Premiacao.setText(real.format(faturamento.Premiacao));

        txt_Total.setText(real.format(faturamento.Comissao+faturamento.Premiacao));

        txt_titulo.setText("Faturamento categoria: "+ faturamento.Categoria);

        if(faturamento.MetaLoja>0){
            percMetaLoja = (faturamento.FatLoja/faturamento.MetaLoja)*100;
                    txt_PercMetaLoja.setText(df.format(percMetaLoja)+"%");

        }else{
            txt_PercMetaLoja.setText("0.00%");
        }

        if(faturamento.MetaConsultor>0){
            percMetaConsultor =  (faturamento.VendaLiquida/faturamento.MetaConsultor)*100;
            txt_PercMeta.setText(df.format(percMetaConsultor)+"%");
            if(faturamento.VendaLiquida/faturamento.MetaConsultor>1){
                txt_Venda.setTextColor(Color.parseColor("#00bfa5"));
            }else{
                txt_Venda.setTextColor(Color.parseColor("#A61553"));
            }
        }else{
            txt_PercMeta.setText("0.00%");
            txt_Venda.setTextColor(Color.parseColor("#A61553"));
        }

        if (faturamento.Margem*100 >= faturamento.MetaMargemP2) {
            txt_Margem.setTextColor(Color.parseColor("#00bfa5"));
        }
        if (faturamento.Margem*100 >= faturamento.MetaMargemP1 && faturamento.Margem*100< faturamento.MetaMargemP2) {
            txt_Margem.setTextColor(Color.parseColor("#C8671E"));
        }

        if (faturamento.Margem*100 < faturamento.MetaMargemP1) {
            txt_Margem.setTextColor(Color.parseColor("#A61553"));
        }


        if(faturamento.MetaMargemP1>0){
            txt_MetaMargemP1.setText(df.format(faturamento.MetaMargemP1)+"%");
        }else{
            txt_MetaMargemP1.setText("0.00%");
        }



        Date dateinicial = null;
        Date datefinal = null;


        DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");



        try{
            dateinicial = entrada.parse(periodoIni);
            datefinal = entrada.parse(periodoFim);
            txt_Periodo.setText("Período da consulta "+formato.format(dateinicial)+ " até "+formato.format(datefinal));
        }catch (Exception e){

        }

       btn_fatLinha.setVisibility(View.VISIBLE);


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
