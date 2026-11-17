package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.TpNotas;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupSenhaAcesso extends Activity {

    private SpinnerDialog spinnerDialog;
    private TextInputEditText edtSenha;
    private Button btn_entrar;
    private LoginHelper helper;
    private ProgressDialog dialog;
    private Handler handler;
    private String retorno;
    private Button btn_fechar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_senhaacesso);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .3));


        helper = new LoginHelper(this);

        handler = new Handler();
        edtSenha = findViewById(R.id.pop_senha_edtSenha);
        btn_entrar = findViewById(R.id.pop_senha_btn_entrar);
        btn_fechar = findViewById(R.id.pop_senha_btnFechar);


        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               VerificarSenha();
            }
        });


    }


    public void VerificarSenha (){

        dialog = new ProgressDialog(PopupSenhaAcesso.this);
        dialog.setTitle("verificando...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(){


            public void run() {

                try {


                    retorno =   helper.ConsultarSenha(edtSenha.getText().toString());


                    handler.post(new Runnable() {
                        @Override
                        public void run() {



                            if(retorno.equals("LIBERADO")){

                                Intent intent = new Intent(PopupSenhaAcesso.this,PopupRegistrarCelular.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(PopupSenhaAcesso.this,"Senha invalida!",Toast.LENGTH_SHORT).show();
                                finish();
                            }


                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupSenhaAcesso.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupSenhaAcesso.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

}
