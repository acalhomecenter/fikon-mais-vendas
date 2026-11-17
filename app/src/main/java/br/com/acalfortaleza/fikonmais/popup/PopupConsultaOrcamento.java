package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.ConsultaFaturamentoActivity;
import br.com.acalfortaleza.fikonmais.ConsultaFaturamentoLinhaActivity;
import br.com.acalfortaleza.fikonmais.OrcamentoActivity;
import br.com.acalfortaleza.fikonmais.PedidosActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.applications.Mask;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Arquiteto;
import br.com.acalfortaleza.fikonmais.models.CondPagamento;
import br.com.acalfortaleza.fikonmais.models.Midias;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupConsultaOrcamento extends Activity {

    private Button btn_consultar;
    private TextInputLayout layout_datainicial;
    private TextInputLayout layout_datafinal;
    private TextInputEditText edtdatainicial;
    private TextInputEditText edtdatafinal;
    private TextInputEditText edtArquiteto;

    private ArrayList<String> listaArq = new ArrayList<>();
    private Calendar myCalendar = Calendar.getInstance();

    private SpinnerDialog spinnerDialog;

    private List<Arquiteto> mListArquitetos;

    private LoginHelper helperLogin;
    private ProgressDialog dialog;
    private Handler handler;
    private String tela;

    private int Codarq = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_periodo);

        tela = getIntent().getStringExtra("tela");


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .4));


        layout_datainicial = findViewById(R.id.layout_orc_edtDataInicial);
        layout_datafinal = findViewById(R.id.layout_orc_edtDataFinal);
        edtdatainicial = findViewById(R.id.orc_edtDataInicial);
        edtdatafinal = findViewById(R.id.orc_edtDatafinal);
        edtArquiteto = findViewById(R.id.pop_cli_edtEspecificador);
        btn_consultar = findViewById(R.id.orc_btn_Consultar);

        helperLogin = new LoginHelper(this);
        handler = new Handler();


        if(!tela.equals("orcamento") && !tela.equals("pedido") ){

            edtArquiteto.setVisibility(View.INVISIBLE);
        }else{

            edtArquiteto.setVisibility(View.VISIBLE);
        }

        final DatePickerDialog.OnDateSetListener dateinicial = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                edtdatainicial.setText("");
                updatelabel(edtdatainicial);
            }

        };


        final DatePickerDialog.OnDateSetListener datefinal = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                edtdatafinal.setText("");
                updatelabel(edtdatafinal);
            }

        };


        edtdatainicial.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {

                    new DatePickerDialog(PopupConsultaOrcamento.this, dateinicial, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                }
            }
        });

        edtArquiteto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtArquiteto.getRight() - edtArquiteto.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        edtdatafinal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {

                    new DatePickerDialog(PopupConsultaOrcamento.this, datefinal, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                }
            }
        });


        spinnerDialog = new SpinnerDialog(PopupConsultaOrcamento.this, listaArq, "Selecione um Arquiteto");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                if(item.equals("Retirar Especificador")){
                    edtArquiteto.setText("");
                    Codarq = 0;

                }else{
                    edtArquiteto.setText(item);

                    Codarq= mListArquitetos.get(position).getCodarq();
                }

            }
        });

        edtdatainicial.addTextChangedListener(Mask.insert("##/##/####", edtdatainicial));

        edtdatafinal.addTextChangedListener(Mask.insert("##/##/####", edtdatafinal));

        btn_consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validarDataIni() || !validarDataFim() || !validarData() || !validarValor()){

                }else{

                    hideSoftKeyboard();

                    Date dateinicial = null;
                    Date datefinal = null;


                    DateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                    DateFormat entrada = new SimpleDateFormat("dd/MM/yyyy");

                    try {
                        dateinicial = entrada.parse(edtdatainicial.getText().toString());
                        datefinal = entrada.parse(edtdatafinal.getText().toString());


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(tela.equals("orcamento")){
                        OrcamentoActivity.periodoIni = formato.format(dateinicial);
                        OrcamentoActivity.periodoFim = formato.format(datefinal);
                        OrcamentoActivity.Codarq = Codarq;
                    }else if(tela.equals("pedido")){
                        PedidosActivity.periodoIni = formato.format(dateinicial);
                        PedidosActivity.periodoFim = formato.format(datefinal);
                        PedidosActivity.codarq = Codarq;
                        PedidosActivity._isAlteracao = true;
                    }else{
                        ConsultaFaturamentoActivity.periodoIni = formato.format(dateinicial);
                        ConsultaFaturamentoActivity.periodoFim = formato.format(datefinal);
                        ConsultaFaturamentoActivity.deveConsultarAoRetornar = true;

                    }


                    finish();

                }
            }
        });

        ConsultarCombobox();


    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public boolean validarDataIni() {

        String data = edtdatainicial.getText().toString().trim();

        if (data.isEmpty()) {
            layout_datainicial.setErrorEnabled(true);
            layout_datainicial.setError("Informe a data");

            return false;

        } else {
            layout_datainicial.setErrorEnabled(false);
            return true;
        }

    }

    public void ConsultarCombobox() {

        dialog = new ProgressDialog(PopupConsultaOrcamento.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {



                    mListArquitetos = helperLogin.GetArquitetos();



                    handler.post(new Runnable() {
                        @Override
                        public void run() {




                            //listaArq.add("Retirar Especificador");
                            for (Arquiteto a : mListArquitetos) {

                                listaArq.add(a.getNomearq());


                            }



                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupConsultaOrcamento.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupConsultaOrcamento.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    public boolean validarValor() {

        int dataini = edtdatainicial.getText().toString().length();
        int datafim = edtdatafinal.getText().toString().length();

        if (dataini < 10) {
            layout_datainicial.setErrorEnabled(true);
            layout_datainicial.setError("Data inválida");
            edtdatainicial.setText(null);
            edtdatainicial.requestFocus();
            return false;
        }

        if (datafim < 10) {
            layout_datafinal.setErrorEnabled(true);
            layout_datafinal.setError("Data inválida");
            edtdatafinal.setText(null);
            edtdatafinal.requestFocus();
            return false;
        }
        layout_datafinal.setErrorEnabled(false);
        layout_datainicial.setErrorEnabled(false);
        return true;


    }

    public boolean validarDataFim() {


        String datafim = edtdatafinal.getText().toString().trim();


        if (datafim.isEmpty()) {
            layout_datafinal.setErrorEnabled(true);
            layout_datafinal.setError("Informe a data");

            return false;

        } else {
            layout_datafinal.setErrorEnabled(false);
            return true;

        }
    }

    public boolean validarData() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
        String dataini = edtdatainicial.getText().toString().trim();
        String datafim = edtdatafinal.getText().toString().trim();

        Date d1 = null;
        try {
            d1 = sdf.parse(dataini);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date d2 = null;
        try {
            d2 = sdf.parse(datafim);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (d2.before(d1)) {
            layout_datafinal.setErrorEnabled(true);
            layout_datafinal.setError("Data final menor que a inicial!");

            return false;

        } else {
            layout_datafinal.setErrorEnabled(false);
            return true;

        }


    }


    private void updatelabel(TextInputEditText editext) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));

        editext.setText(sdf.format(myCalendar.getTime()));
    }


}
