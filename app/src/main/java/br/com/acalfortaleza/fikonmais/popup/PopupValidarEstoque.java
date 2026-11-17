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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.models.BeneficioPromocao;
import br.com.acalfortaleza.fikonmais.models.Estoque;
import br.com.acalfortaleza.fikonmais.models.Lotes;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.models.TpNotas;
import br.com.acalfortaleza.fikonmais.models.ValidacaoEstoque;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupValidarEstoque extends Activity {



    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    String resultado = "";
    private SpinnerDialog spinnerDialog;
    private TextInputEditText edtQtde;
    private TextInputEditText edtLote;
    private Button btn_Validar;
    private ProdutoHelper helper;
    private ProgressDialog dialog;
    private Handler handler;
    private ArrayList<String> listaLote = new ArrayList<>();
    private Estoque estoque;
    private ProdutoGemco produto;
    private Button btn_fechar;
    private TextInputLayout layout_qtde;
    private AlertDialog alertaDialog;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper helperLogin;
    private TextView txtFilialEstoque;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_validacao_estoque);


        estoque = (Estoque) getIntent().getSerializableExtra("estoque");
        produto = (ProdutoGemco) getIntent().getSerializableExtra("produto");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .5));

        helper = new ProdutoHelper();

        helperLogin = new LoginHelper(this);

        usuarioLogado = helperLogin.GetUsuarioSalvo();

        handler = new Handler();
        edtQtde = findViewById(R.id.pop_val_edtQtde);
        edtLote = findViewById(R.id.pop_val_edtLote);
        btn_Validar = findViewById(R.id.pop_val_btnValidar);
        btn_fechar = findViewById(R.id.pop_val_btnFechar);
        layout_qtde = findViewById(R.id.pop_val_layoutQtde);
        txtFilialEstoque = findViewById(R.id.pop_val_txtFilialEscolhida);

        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        final DecimalFormat df = new DecimalFormat("0.00",symbols);
        if (produto!= null && produto.Qtemb == 0) {
            edtQtde.setText("1.00");
            produto.Qtemb = 1.00;

        } else {
            edtQtde.setText(df.format(produto.Qtemb));
        }
        edtQtde.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    edtQtde.setText("");
                }
                return false;
            }
        });




        for (Lotes l : estoque.Lotes) {

               listaLote.add(l.Lote);

        }


        spinnerDialog = new SpinnerDialog(PopupValidarEstoque.this, listaLote, "Informe o Lote");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtLote.setText(item);
                edtQtde.setText((estoque.Lotes.get(position).Disponivel));


            }
        });


        edtLote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtLote.getRight() - edtLote.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });



        btn_Validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validarQtde() || !validarConversao()) {



                }else{
                    InsertValidacao();
                }

            }
        });


        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });


        txtFilialEstoque.setText(estoque.Filial);


    }



    public void InsertValidacao(){

        dialog = new ProgressDialog(PopupValidarEstoque.this);
        dialog.setTitle("Solicitando Validação...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(){


            public void run() {

                try {



                    resultado =   helper.InsertValidacao(estoque.CodFil, Integer.parseInt(usuarioLogado.get("codfil")), Integer.parseInt(usuarioLogado.get("codigo")),produto.Codigo,Double.parseDouble(edtQtde.getText().toString()),edtLote.getText().toString());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            if(resultado.equals("REALIZADO")){
                                Toast.makeText(PopupValidarEstoque.this, "Solicitação enviada com sucesso!", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(PopupValidarEstoque.this, "Erro no envio da solicitação, Tente novamente!", Toast.LENGTH_SHORT).show();
                            }



                        }
                    });

                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupValidarEstoque.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupValidarEstoque.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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








    public boolean validarQtde() {
        String qtd = edtQtde.getText().toString().trim();
        if (qtd.isEmpty() || Double.parseDouble(qtd)==0) {
            layout_qtde.setErrorEnabled(true);
            layout_qtde.setError("Informe a quantidade");
            return false;

        } else {
            layout_qtde.setErrorEnabled(false);
            return true;
        }




    }

    public boolean validarConversao(){
        int qtdArredondado =0;
        String qtdDigitada ="0";
        String qtdCalculada = "0";
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        final DecimalFormat df = new DecimalFormat("0.00",symbols);


        float divisao = (float) (Double.parseDouble(edtQtde.getText().toString())/produto.Qtemb);
        qtdArredondado = (int) Math.ceil(divisao);
        qtdDigitada = df.format(Double.parseDouble(edtQtde.getText().toString()));
        qtdCalculada = df.format(produto.Qtemb*qtdArredondado);
        if(!produto.Ncm.equals("32041700") && !produto.Ncm.equals("32064990")) {
            if (!qtdDigitada.equals(qtdCalculada)) {
                layout_qtde.setErrorEnabled(true);
                if (produto.Qtemb == 1) {
                    layout_qtde.setError("Produto vendido em unidade, qtd alterada para: " + (df.format(produto.Qtemb * qtdArredondado)));
                } else {
                    layout_qtde.setError("Produto vendido em caixa, qtd alterada para: " + (df.format(produto.Qtemb * qtdArredondado)));
                }
                edtQtde.setText(df.format(produto.Qtemb * qtdArredondado));
                return false;

            } else {
                layout_qtde.setEnabled(false);
                return true;
            }
        }else{
            layout_qtde.setEnabled(false);
            return true;
        }

    }

}
