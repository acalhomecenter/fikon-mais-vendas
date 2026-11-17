package br.com.acalfortaleza.fikonmais;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import br.com.acalfortaleza.fikonmais.fragments.EstoqueFragments;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;

public class ConsultaEstoqueActivity extends BaseAcalActivity {

    private String codigo = "";
    private ProgressDialog dialog;
    private ProdutoHelper helper;
    private List<ProdutoGemco> retornoGemco;
    private ProdutoGemco produtoGemco;
    private Handler handler;
    private Bundle bundle;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper helperLogin;

    EstoqueFragments estoqueFragments;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.consulta_estoque_status_bar_bg);
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
        setContentView(R.layout.activity_consulta_estoque);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.consulta_estoque_toolbar);
        setSupportActionBar(toolbar);

        codigo = getIntent().getStringExtra("codigo");

        helper = new ProdutoHelper();

        handler = new Handler();

        bundle = new Bundle();

        helperLogin = new LoginHelper(this);
        usuarioLogado = helperLogin.GetUsuarioSalvo();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Estoque do Item "+ codigo);
        getSupportActionBar().setElevation(0);

        ConsultarProduto(codigo,"consulta");




    }




    public void ConsultarProduto(final String codigo, final String tipo) {

        dialog = new ProgressDialog(ConsultaEstoqueActivity.this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();


        new Thread() {

            public void run() {

                produtoGemco=null;

                try {



                        retornoGemco= helper.ConsultarProdutoEstoque(codigo, usuarioLogado.get("codfil").toString());




                    if (retornoGemco == null || retornoGemco.size()==0) {
                        produtoGemco = null;


                    } else {
                        for (ProdutoGemco p : retornoGemco) {
                            produtoGemco = p;
                            getSupportFragmentManager().beginTransaction().replace(R.id.estoque_frame, new EstoqueFragments(produtoGemco)).commit();

                        }




                    }

                    if (produtoGemco == null || retornoGemco.size()==0) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ConsultaEstoqueActivity.this, "Não foi encontrado nenhum produto com o código: " + codigo, Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {


                        handler.post(new Runnable() {
                            @Override
                            public void run() {




                            }
                        });


                    }


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ConsultaEstoqueActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(ConsultaEstoqueActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

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



        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {


            finish();
            return true;
        }


        return false;
    }


    @Override
    public void onBackPressed() {

        finish();
    }
}
