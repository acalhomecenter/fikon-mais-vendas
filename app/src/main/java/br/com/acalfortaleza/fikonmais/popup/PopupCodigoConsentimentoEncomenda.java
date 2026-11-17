package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.models.CodigoEncomenda;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.Orcamento;

public class PopupCodigoConsentimentoEncomenda extends Activity {

    private EditText txtCodigo;
    private Button btnValidar;
    private Button btnCancelar;
    private Button btnReenviar;
    private Orcamento orcamento;
    private OrcamentoHelper helper = new OrcamentoHelper();
    private Handler handler = new Handler();
    private CodigoEncomenda codigoAnterior;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_codigo_consentimento_encomenda);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getWindow().setLayout((int) (displayMetrics.widthPixels * .9), ConstraintLayout.LayoutParams.WRAP_CONTENT);

        txtCodigo = findViewById(R.id.PopupCodigoEncomenda_TxtCodigo);
        btnValidar = findViewById(R.id.PopupCodigoEncomenda_BtnValidar);
        btnCancelar = findViewById(R.id.PopupCodigoEncomenda_BtnCancelar);
        btnReenviar = findViewById(R.id.PopupCodigoEncomenda_BtnReenviar);

        orcamento = (Orcamento) getIntent().getSerializableExtra("orcamento");
        codigoAnterior = (CodigoEncomenda) getIntent().getSerializableExtra("codigoAnterior");

        btnValidar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtCodigo.getText()!=null && !txtCodigo.getText().toString().trim().equals("")){
                    ValidarCodigo(txtCodigo.getText().toString().toUpperCase());
                }

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnReenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EnviarCodigo();
            }
        });

    }

    public void ValidarCodigo(String codigoDigitado){

        ProgressDialog progressDialog = new ProgressDialog(PopupCodigoConsentimentoEncomenda.this);
        progressDialog.setTitle("Verificando");
        progressDialog.setMessage("Por favor aguarde");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String titulo = "";
                String mensagem = "";
                boolean finalizarActivity = false;

                codigoAnterior.CodigoDigitado = codigoDigitado;
                CodigoEncomenda codigoNovo = helper.SalvarCodigoEncomenda(codigoAnterior);

                if(
                    codigoNovo!=null && codigoNovo.Numorc == (long) orcamento.getNumorc()
                    && codigoNovo.CodigoDigitado!=null && codigoNovo.CodigoGerado!=null
                    && !codigoNovo.CodigoDigitado.trim().equals("") && !codigoNovo.CodigoGerado.trim().equals("")
                ){

                    if(codigoNovo.CodigoDigitado.equals(codigoNovo.CodigoGerado)){

                        titulo = "Código Válido";
                        mensagem = "Código validado com sucesso.";
                        finalizarActivity = true;

                    }
                    else{

                        titulo = "Código Incorreto";
                        mensagem = "Digite o código correto e refaça a validação.";

                    }

                }
                else {

                    titulo = "Erro ao salvar código.";
                    mensagem = "Verifique sua conexão e tente novamente mais tarde.";

                }

                final String titulo2 = titulo;
                final String mensagem2 = mensagem;
                final boolean finalizarActivity2 = finalizarActivity;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new AlertDialog.Builder(PopupCodigoConsentimentoEncomenda.this)
                            .setTitle(titulo2)
                            .setMessage(mensagem2)
                            .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressDialog.dismiss();
                                    if(finalizarActivity2) {
                                        finish();
                                    }
                                    dialogInterface.dismiss();
                                }
                            }).show();
                    }
                });

            }
        }).start();

    }

    public void EnviarCodigo(){

        ProgressDialog progressDialog = new ProgressDialog(PopupCodigoConsentimentoEncomenda.this);
        progressDialog.setTitle("Enviando");
        progressDialog.setMessage("Por favor aguarde");
        progressDialog.setCancelable(false);

        new Thread(new Runnable() {
            @Override
            public void run() {

                String mensagem = helper.EnviarCodigoEncomenda(orcamento);

                if(mensagem!=null){

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            AlertDialog.Builder builder = new AlertDialog.Builder(PopupCodigoConsentimentoEncomenda.this);
                            builder.setTitle("Erro ao enviar código");
                            builder.setMessage(mensagem);

                            if(mensagem.contains("Código enviado para o whatsapp")){
                                builder.setTitle("Código Enviado");
                            }

                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressDialog.dismiss();
                                    dialogInterface.dismiss();
                                }
                            });

                            builder.show();

                        }
                    });

                }

            }
        }).start();

    }

}
