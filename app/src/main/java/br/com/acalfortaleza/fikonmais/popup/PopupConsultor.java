package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.ConsultaActivity;
import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.fragments.ProdutoFragments;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.TpNotas;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupConsultor extends Activity {

    private SpinnerDialog spinnerDialog;
    private TextInputEditText edtConsultor;
    private Button btn_iniciar;
    private OrcamentoHelper helper;
    private List<Consultor> mList;
    private ProgressDialog dialog;
    private ArrayList<String> listaConsultor = new ArrayList<>();
    private Handler handler;
    private List<String> retorno;
    private Orcamento orcamento;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private Button btn_fechar;
    private TextInputLayout layout_consultor;
    private String consultor_selecionado = "N";
    private String numorc = "";
    private HashMap<String, String> usuarioLogado;
    private LoginHelper helperLogin;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_consultor);

        orcamento = (Orcamento) getIntent().getSerializableExtra("orcamento");
        numorc = getIntent().getStringExtra("numorc");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("orcamentos");

        helperLogin = new LoginHelper(this);

        usuarioLogado = helperLogin.GetUsuarioSalvo();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .3));

        helper = new OrcamentoHelper();

        handler = new Handler();
        edtConsultor = findViewById(R.id.pop_ped_edtConsultor);
        btn_iniciar = findViewById(R.id.pop_ped_btn_iniciarr);
        btn_fechar = findViewById(R.id.pop_orc_consultor_btnFechar);
        layout_consultor = findViewById(R.id.pop_ped_layoutConsultor);

        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        ConsultarCombobox();




        spinnerDialog = new SpinnerDialog(PopupConsultor.this, listaConsultor, "Informe o consultor");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtConsultor.setText(item);
                PopupOrcFechamento.orcamento.setCodvendr(mList.get(position).CodigoConsultor);
                PopupOrcFechamento.orcamento.setCodfil(Integer.parseInt(usuarioLogado.get("codfil")));
                PopupOrcFechamento.orcamento.setFilial(usuarioLogado.get("filialorig"));
                PopupOrcFechamento.orcamento.setConsultor(item);


                consultor_selecionado = "S";

            }
        });


        edtConsultor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtConsultor.getRight() - edtConsultor.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        btn_iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validarConsultor()) {

                   PopupOrcFechamento.consultor_selecionado  ="S";
                   finish();

                }

            }
        });





    }


    public void ConsultarCombobox (){

        dialog = new ProgressDialog(PopupConsultor.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(){


            public void run() {

                try {


                    mList =  helper.GetConsultor();


                    handler.post(new Runnable() {
                        @Override
                        public void run() {



                            if(mList != null && mList.size()>0){

                                for (Consultor c : mList) {
                                    listaConsultor.add(c.nomeconsultor.toString());
                                }



                            }


                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupConsultor.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupConsultor.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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



    public Boolean validarConsultor() {


        if (consultor_selecionado.equals("N")) {

            layout_consultor.setError("Selecione uma opção");
            return false;

        } else {
            layout_consultor.setError(null);
            return true;
        }
    }


}

