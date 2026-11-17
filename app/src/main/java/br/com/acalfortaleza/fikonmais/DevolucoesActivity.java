package br.com.acalfortaleza.fikonmais;

import android.app.ProgressDialog;
import android.os.Handler;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.os.Bundle;
import android.graphics.Color;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.adapters.DevolucaoAdapter;
import br.com.acalfortaleza.fikonmais.helper.DevolucaoHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Devolucao;

public class DevolucoesActivity extends BaseAcalActivity {

    private Handler handler;
    private DevolucaoHelper helper;
    private List<Devolucao> mList;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private ProgressDialog dialog;
    private RecyclerView mRecyclerView;
    private DevolucaoAdapter adapter;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.devolucoes_status_bar_bg);
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
        setContentView(R.layout.activity_devolucoes);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.devolucoes_toolbar);
        setSupportActionBar(toolbar);

        helper = new DevolucaoHelper();
        handler = new Handler();
        loginHelper = new LoginHelper(this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Devoluções");
        getSupportActionBar().setElevation(0);

        mRecyclerView = findViewById(R.id.dev_listaDev);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(DevolucoesActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Integer.parseInt(usuarioLogado.get("codigo")) > 0) {
            ConsultarDevolucoes();
        }
    }

    public void ConsultarDevolucoes() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {


                    mList = helper.GetDevolucoes(Integer.parseInt(usuarioLogado.get("codigo")));

                    if (mList == null || mList.toString().equals("[]")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DevolucoesActivity.this, "Não foram encontradas devoluções, para o período informado!", Toast.LENGTH_SHORT).show();


                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new DevolucaoAdapter(DevolucoesActivity.this, mList);
                                mRecyclerView.setAdapter(adapter);
                            }
                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DevolucoesActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(DevolucoesActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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
