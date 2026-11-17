package br.com.acalfortaleza.fikonmais;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.os.Bundle;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.SugestoesHelper;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.models.Sugestoes;
import fr.ganfra.materialspinner.MaterialSpinner;

public class SugestoesActivity extends BaseAcalActivity {

    private CheckBox ckSugestao;
    private CheckBox ckPerda;
    private MaterialSpinner lista_motivos;
    private TextInputLayout layoutDescricao;
    private TextInputEditText edtDescricao;
    private Button btEnviarSugestao;
    private ArrayList<String> mList;
    private android.os.Handler handler = new android.os.Handler();
    private String motivoSelecionado = "Selecione";
    private String tipoSelecionado;
    private ProgressDialog dialog;
    private String retorno;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper helperLogin;
    private SugestoesHelper helper;
    private ProdutoGemco ProdutoFalta;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.sugestoes_status_bar_bg);
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
        setContentView(R.layout.activity_sugestoes);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.sugestoes_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Sugestões e problemas");
        getSupportActionBar().setElevation(0);

        helper = new SugestoesHelper();
        helperLogin = new LoginHelper(this);
        usuarioLogado = helperLogin.GetUsuarioSalvo();

        ckSugestao = findViewById(R.id.rSugestao);
        ckPerda = findViewById(R.id.rPerda);
        layoutDescricao = findViewById(R.id.sugestao_layoutDescricao);
        edtDescricao = findViewById(R.id.sugestao_edtDescricao);
        btEnviarSugestao = findViewById(R.id.btEnviarSugestao);
        ProdutoFalta = (ProdutoGemco) getIntent().getSerializableExtra("FaltaProduto");


        ckSugestao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ckSugestao.isChecked()){

                    ckPerda.setChecked(false);
                    layoutDescricao.setHint("Sua sugestão é?");
                    ConsultarCombobox("SUGESTAO");
                }

            }
        });

        ckPerda = findViewById(R.id.rPerda);

        ckPerda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ckPerda.isChecked()){

                    ckSugestao.setChecked(false);
                    layoutDescricao.setHint("Descreva o motivo");
                    ConsultarCombobox("PERDA");
                }

            }
        });


        lista_motivos =  findViewById(R.id.lista_motivos);

        lista_motivos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                if(i>= 0){

                   motivoSelecionado = mList.get(i);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        btEnviarSugestao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!validarMotivo() || !validarSugestao() ){
                    return;
                }else{
                    SalvarSugestao(CarregaSugestao());
                }




            }
        });

        ConsultarCombobox("PERDA");



    }


    public boolean validarMotivo(){
        String filial = "Selecione";
      if(!motivoSelecionado.equals("Selecione")) {
           filial = motivoSelecionado.toString().trim();
      }

        if(filial.equals("Selecione")){

            lista_motivos.setError("selecione uma opção");
            return false;

        }else {

            lista_motivos.setError(null);
            return true;
        }
    }

    public boolean validarSugestao() {

        String descricao = layoutDescricao.getEditText().getText().toString().trim();

        if (descricao.isEmpty()) {
            if(ckPerda.isChecked()){
                layoutDescricao.setError("Descreva o motivo da perda!");
            }else {
                layoutDescricao.setError("Descreva a sua sugestão!");
            }

            return false;

        } else {

            layoutDescricao.setError(null);
            return true;
        }

    }

    public void ConsultarCombobox (final String tipo){

        dialog = new ProgressDialog(SugestoesActivity.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(){


            public void run() {

                try {


                    mList = helper.GetMotivos(tipo);


                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            final ArrayAdapter adapter = new ArrayAdapter(SugestoesActivity.this,R.layout.support_simple_spinner_dropdown_item, mList);
                            lista_motivos.setAdapter(adapter);

                            if(ProdutoFalta!=null){

                                ckPerda.setChecked(true);
                                ckSugestao.setChecked(false);
                                lista_motivos.setSelection(adapter.getPosition("FALTA DE ESTOQUE LOJA") + 1);
                                edtDescricao.setText( ProdutoFalta.Codigo + " - " + ProdutoFalta.Descricao );
                                motivoSelecionado = "FALTA DE ESTOQUE LOJA";

                                ckPerda.setClickable(false);
                                ckPerda.setEnabled(false);

                                ckSugestao.setClickable(false);
                                ckSugestao.setEnabled(false);


                            }

                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(SugestoesActivity.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(SugestoesActivity.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


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



    public Sugestoes CarregaSugestao(){

        Sugestoes sugestao = new Sugestoes();


        tipoSelecionado = "";

        if(ckSugestao.isChecked()){

            tipoSelecionado = ckSugestao.getText().toString();

        }

        if(ckPerda.isChecked()){

            tipoSelecionado = ckPerda.getText().toString();

        }

        sugestao.setTipo(tipoSelecionado);
        sugestao.setMotivo(motivoSelecionado);
        sugestao.setDescricao(edtDescricao.getText().toString());
        sugestao.setCodfil(usuarioLogado.get("codfil"));
        sugestao.setCodvendr(usuarioLogado.get("codigo"));


        return sugestao;


    }


    public void SalvarSugestao(final Sugestoes sugestao){
        dialog = new ProgressDialog(SugestoesActivity.this);
        dialog.setMessage("Enviando...");
        dialog.setCancelable(true);
        dialog.show();
        new Thread() {


            public void run() {


                try {


                    retorno = helper.SetSugestao(sugestao);


                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();

                    if(retorno!=null && retorno.equals("Inserido com sucesso!")){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(SugestoesActivity.this).setTitle("Confirmação").setMessage("Seu formulário foi enviado com sucesso!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(SugestoesActivity.this, MainActivity.class));
                                        finishAffinity();
                                    }
                                }).show();

                            }
                        });

                    }else{

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(SugestoesActivity.this).setTitle("Erro").setMessage("Ocorreu um erro no envio do seu formulário, tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).show();
                            }
                        });
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


        }


        return super.onOptionsItemSelected(item);
    }
}
