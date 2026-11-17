package br.com.acalfortaleza.fikonmais;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import br.com.acalfortaleza.fikonmais.adapters.ValidacaoEstoqueAdapter;
import br.com.acalfortaleza.fikonmais.helper.DevolucaoHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.models.Devolucao;
import br.com.acalfortaleza.fikonmais.models.ValidacaoEstoque;

public class ValidacaoEstoqueActivity extends BaseAcalActivity {

    private Handler handler;
    private ProdutoHelper helper;
    private List<ValidacaoEstoque> mList;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private ProgressDialog dialog;
    private RecyclerView mRecyclerView;
    private ValidacaoEstoqueAdapter adapter;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.validacao_estoque_status_bar_bg);
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
        setContentView(R.layout.activity_validacao_estoque);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.validacao_estoque_toolbar);
        setSupportActionBar(toolbar);

        helper = new ProdutoHelper();
        handler = new Handler();
        loginHelper = new LoginHelper(this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Validações de Estoque");
        getSupportActionBar().setElevation(0);

        mRecyclerView = findViewById(R.id.val_listaValidacao);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ValidacaoEstoqueActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Integer.parseInt(usuarioLogado.get("codigo")) > 0) {
            ConsultarValidacoes();
        }
    }

    public void ConsultarValidacoes() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                try {


                    mList = helper.ConsultarValidacoes(Integer.parseInt(usuarioLogado.get("codigo")));

                    if (mList == null || mList.toString().equals("[]")) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ValidacaoEstoqueActivity.this, "Não foram encontradas validações, para o período informado!", Toast.LENGTH_SHORT).show();


                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter = new ValidacaoEstoqueAdapter(ValidacaoEstoqueActivity.this, mList);
                                mRecyclerView.setAdapter(adapter);
                            }
                        });
                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ValidacaoEstoqueActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(ValidacaoEstoqueActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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
