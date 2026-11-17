package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.fragments.ClientesFragments;
import br.com.acalfortaleza.fikonmais.helper.ClienteHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.models.Estoque;
import br.com.acalfortaleza.fikonmais.models.Lotes;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupConsultaCliente extends Activity {

    private SpinnerDialog spinnerDialog;
    private CheckBox checkBoxPf;
    private CheckBox checkBoxPj;
    private TextInputEditText edtSituacao;
    private Button btn_Consultarr;
    private ClienteHelper helper;
    private ProgressDialog dialog;
    private Handler handler;
    private ArrayList<String> listaSituacao = new ArrayList<>();
    private Button btn_fechar;
    private TextInputLayout layout_situacao;
    private AlertDialog alertaDialog;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper helperLogin;
    private String situacao = "N";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_consulta_cliente);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .4));

        helper = new ClienteHelper(this);

        helperLogin = new LoginHelper(this);

        usuarioLogado = helperLogin.GetUsuarioSalvo();

        handler = new Handler();

        checkBoxPf =findViewById(R.id.pop_cli_checkPf);
        checkBoxPj =findViewById(R.id.pop_cli_checkpj);
        edtSituacao = findViewById(R.id.pop_cli_edtSituacao);
        btn_Consultarr = findViewById(R.id.pop_cli_btnConsultar);
        btn_fechar = findViewById(R.id.pop_val_btnFechar);
        layout_situacao = findViewById(R.id.pop_cli_layoutSituacao);

        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });




            listaSituacao.add("ATIVO");
            listaSituacao.add("INATIVO");
            listaSituacao.add("PRECHURN");






        spinnerDialog = new SpinnerDialog(PopupConsultaCliente.this, listaSituacao, "Informe a situação");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtSituacao.setText(item);

                situacao = item;




            }
        });


        edtSituacao.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtSituacao.getRight() - edtSituacao.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });



        btn_Consultarr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(!validarSituacao()){

               }else{
                   ClientesFragments.situacao = edtSituacao.getText().toString();
                   if(checkBoxPf.isChecked()) {
                       ClientesFragments.natureza = "F";
                       ClientesFragments.situacao = situacao;

                   }
                   if(checkBoxPj.isChecked()){
                       ClientesFragments.natureza = "J";
                       ClientesFragments.situacao = situacao;
                   }
                   finish();
               }



            }
        });


        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });






    }

    public  boolean validarSituacao(){

        if (situacao.equals("N")) {

            layout_situacao.setError("Informe a situação do cliente");
            return false;

        } else {
            layout_situacao.setError(null);
            return true;
        }
    }


}
