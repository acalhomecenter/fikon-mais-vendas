package br.com.acalfortaleza.fikonmais;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;

import br.com.acalfortaleza.fikonmais.fragments.IndicadoresConsultorFragment;
import br.com.acalfortaleza.fikonmais.fragments.MetaDiaFragments;
import br.com.acalfortaleza.fikonmais.fragments.MetaMesFragments;
import br.com.acalfortaleza.fikonmais.fragments.ProjecaoFrafments;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import mehdi.sakout.fancybuttons.FancyButton;

public class IndicadoresActivity extends BaseAcalActivity {

    private HashMap<String, String> usuarioLogado;
    private LoginHelper helperLogin;
    private FancyButton btnAcumulado;
    private FancyButton btnDia;
    private FancyButton btnOntem;
    private Consultor consultor;
    private Bundle bundle;
    private Handler handler;
    private ProgressDialog dialog;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.ind_status_bar_bg);
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
        setContentView(R.layout.activity_indicadores);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.ind_toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Meus Indicadores");
        getSupportActionBar().setElevation(0);


        helperLogin = new LoginHelper(this);
        usuarioLogado = helperLogin.GetUsuarioSalvo();

        handler = new Handler();
        bundle = new Bundle();



        btnAcumulado = findViewById(R.id.btn_acumulado);
        btnAcumulado.setBackgroundColor(getResources().getColor(R.color.primary_dark));
        btnAcumulado.setTextColor(getResources().getColor(R.color.icons));
        btnAcumulado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAcumulado.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                btnAcumulado.setTextColor(getResources().getColor(R.color.icons));
                btnDia.setBackgroundColor(getResources().getColor(R.color.icons));
                btnDia.setTextColor(getResources().getColor(R.color.primary));
                btnOntem.setBackgroundColor(getResources().getColor(R.color.icons));
                btnOntem.setTextColor(getResources().getColor(R.color.primary));

                ConsultarIndicadores(usuarioLogado.get("cpf"), usuarioLogado.get("filial"), usuarioLogado.get("nome"),"acumulado");


            }
        });


        btnDia = findViewById(R.id.btn_dia);
        btnDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAcumulado.setBackgroundColor(getResources().getColor(R.color.icons));
                btnAcumulado.setTextColor(getResources().getColor(R.color.primary));
                btnDia.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                btnDia.setTextColor(getResources().getColor(R.color.icons));
                btnOntem.setBackgroundColor(getResources().getColor(R.color.icons));
                btnOntem.setTextColor(getResources().getColor(R.color.primary));

                ConsultarIndicadores(usuarioLogado.get("cpf"), usuarioLogado.get("filial"), usuarioLogado.get("nome"),"dia");


            }
        });


        btnOntem = findViewById(R.id.btn_ontem);
        btnOntem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAcumulado.setBackgroundColor(getResources().getColor(R.color.icons));
                btnAcumulado.setTextColor(getResources().getColor(R.color.primary));
                btnDia.setBackgroundColor(getResources().getColor(R.color.icons));
                btnDia.setTextColor(getResources().getColor(R.color.primary));
                btnOntem.setBackgroundColor(getResources().getColor(R.color.primary_dark));
                btnOntem.setTextColor(getResources().getColor(R.color.icons));

                ConsultarIndicadores(usuarioLogado.get("cpf"), usuarioLogado.get("filial"), usuarioLogado.get("nome"),"ontem");

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Esta tela está em reformulação, aguarde atualizações futuras.");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        if (id == android.R.id.home) {

            finish();


        }


        return super.onOptionsItemSelected(item);
    }


    public void ConsultarIndicadores(final String cpf, final String filial, final String nome, final String tipo) {

        dialog = new ProgressDialog(IndicadoresActivity.this);
        dialog.setMessage("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {

                    switch (tipo){
                        case "acumulado" :
                            consultor = helperLogin.ConsultarConsultorAcumulado(cpf, filial, nome);
                            break;
                        case "dia" :
                            consultor = helperLogin.ConsultarConsultorDia(cpf, filial, nome);
                            break;
                        case "ontem" :
                            consultor = helperLogin.ConsultarConsultorOntem(cpf, filial, nome);
                            break;
                    }



                } catch (UnknownHostException e) {

                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {

                } finally {
                    dialog.dismiss();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (consultor != null) {
                                bundle.putSerializable("indicadores", consultor);
                                bundle.putString("tipo", tipo);
                                IndicadoresConsultorFragment indicadorfm = new IndicadoresConsultorFragment();
                                indicadorfm.setArguments(bundle);
                                FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
                                transacao.replace(R.id.frameLayout, indicadorfm);
                                transacao.commit();
                            } else {

                            }
                        }
                    });


                }

            }


        }.start();

    }


}
