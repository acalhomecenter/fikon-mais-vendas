package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.Filial;
import br.com.acalfortaleza.fikonmais.models.MotivoDesconto;
import br.com.acalfortaleza.fikonmais.models.OrcCustomField;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupOrcCustomPF  extends Activity {


    private TextInputEditText edtEstadoVida;
    private SpinnerDialog spinnerDialog;
    private Button btn_fechar;
    private ArrayList<String> listaEstadoVida = new ArrayList<>();
    private ProgressDialog dialog;
    private Handler handler;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_orccustom_pf);

        handler = new Handler();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .6));

        edtEstadoVida = findViewById(R.id.pop_orccustom_edtEstadoVida);
        btn_fechar = findViewById(R.id.pop_orccustom_btnFechar);

        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        ConsultarCombobox();

        spinnerDialog = new SpinnerDialog(PopupOrcCustomPF.this, listaEstadoVida, "selecione o estado de vida");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtEstadoVida.setText(item);
                OrcCustomField custom = new OrcCustomField();

                switch (item){
                    case "Casando":
                        custom.setId(185812);
                        custom.setValue("187166");
                        break;
                    case "Morando Sozinho":
                        custom.setId(185812);
                        custom.setValue("187167");
                        break;
                    case "Nova Casa/Apto":
                        custom.setId(185812);
                        custom.setValue("187168");
                        break;
                    case "Nova Loja":
                        custom.setId(185812);
                        custom.setValue("187169");
                        break;
                    case "Novo escritório":
                        custom.setId(185812);
                        custom.setValue("187170");
                        break;
                    case "Reforma Casa/Apto":
                        custom.setId(185812);
                        custom.setValue("187171");
                        break;
                }



                List<OrcCustomField> fields = new ArrayList<>();

                fields.add(custom);

                Orcamento_LancamentoActivity.orcamento.setOrcCustomFields(fields);



            }
        });


        edtEstadoVida.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtEstadoVida.getRight() - edtEstadoVida.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });


    }


    public void ConsultarCombobox() {

        dialog = new ProgressDialog(PopupOrcCustomPF.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {



                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            //Descomentar apos COVID-19

                            listaEstadoVida.add("Casando");
                            listaEstadoVida.add("Morando Sozinho");
                            listaEstadoVida.add("Nova Casa/Apto");
                            listaEstadoVida.add("Nova Loja");
                            listaEstadoVida.add("Novo escritório");
                            listaEstadoVida.add("Reforma Casa/Apto");

                            if(Orcamento_LancamentoActivity.orcamento.getOrcCustomFields() != null) {


                                for (int i = 0; i < Orcamento_LancamentoActivity.orcamento.getOrcCustomFields().size(); i++) {

                                    if (Orcamento_LancamentoActivity.orcamento.getOrcCustomFields().get(i).getId() == 185812) {

                                        switch (Orcamento_LancamentoActivity.orcamento.getOrcCustomFields().get(i).getValue()) {
                                            case "187166":
                                                edtEstadoVida.setText("Casando");
                                                break;
                                            case "187167":
                                                edtEstadoVida.setText("Morando Sozinho");
                                                break;
                                            case "187168":
                                                edtEstadoVida.setText("Nova Casa/Apto");
                                                break;
                                            case "187169":
                                                edtEstadoVida.setText("Nova Loja");
                                                break;
                                            case "187170":
                                                edtEstadoVida.setText("Novo escritório");
                                                break;
                                            case "187171":
                                                edtEstadoVida.setText("Reforma Casa/Apto");
                                                break;
                                        }
                                    }

                                }

                            }


                        }
                    });




                } finally {
                    dialog.dismiss();


                }
            }


        }.start();


    }


}
