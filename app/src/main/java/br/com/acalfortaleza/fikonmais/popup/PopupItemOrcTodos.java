package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.Filial;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.Lotevencto;
import br.com.acalfortaleza.fikonmais.models.MotivoDesconto;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupItemOrcTodos extends Activity {

    private Orcamento orcamento;
    private ItemOrcamento itemOrcamento;
    private TextView txtProduto;
    private TextInputEditText edtDesconto;

    private TextInputEditText edtMotivo;
    private TextInputLayout layout_desconto;

     private TextInputLayout layout_Motivo;
    private PedidosHelper helper;
    private ProgressDialog dialog;
    private Handler handler;
    private String motivo_selecionado = "N";
    private List<MotivoDesconto> mListMotivo;
    private Button btn_fechar;

    private Button btn_alterar;
    private double valorOriginal;
    double quantidade ;
    double percDesc =0.0000d;
    double percDescAlt = 0.0000d;
    private BigDecimal valordodesconto = new BigDecimal("0.00");
    private double margem;
    private  DecimalFormat df;
    private DecimalFormatSymbols symbols;
    private DecimalFormat dfdesconto;
    private  NumberFormat real;
    private SpinnerDialog spinnerDialogMotivo;
    private ArrayList<String> listaMotivo = new ArrayList<>();
    private boolean descPerc;
    private boolean descValor;
    private int position;
    private String codMotivo;
    private String descriMotivo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_itemorc_todos);

        orcamento = (Orcamento) getIntent().getSerializableExtra("orcamento");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .4));


        helper = new PedidosHelper();
        handler = new Handler();

        edtDesconto = findViewById(R.id.pop_orc_edtDesc);
        edtMotivo = findViewById(R.id.pop_orc_edtMotivo);
        btn_alterar = findViewById(R.id.pop_orc_btn_Alterar);
        layout_desconto = findViewById(R.id.pop_orc_layoutDescTodos);
        layout_Motivo = findViewById(R.id.pop_orc_layoutMotivotodos);
        btn_fechar = findViewById(R.id.pop_form_btnFechar2);

        real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        df = new DecimalFormat("0.00", symbols);
        dfdesconto = new DecimalFormat("0.0000", symbols);


        edtDesconto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(edtDesconto.getText().toString().equals("")){
                        edtDesconto.setText("0");

                    }
                    if(Double.parseDouble(edtDesconto.getText().toString())==0){
                        edtDesconto.setText("");
                        descPerc=true;
                        descValor=false;

                    }else{

                    }


                }
                return false;
            }
        });


        edtDesconto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent event) {

                if (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || i == EditorInfo.IME_ACTION_DONE) {
                    if (!validarDesconto()  ) {

                    } else {
                        if (Float.parseFloat(edtDesconto.getText().toString()) >0) {

                            edtMotivo.setEnabled(true);
                            layout_Motivo.setEnabled(true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                            }
                        }else{
                            edtMotivo.setEnabled(false);
                            layout_Motivo.setEnabled(false);
                            edtMotivo.setText("");
                            codMotivo = "";
                            descriMotivo = "";
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
                            }
                        }
                        return false;
                    }




                }
                return  false;
            }
        });




        ConsultarCombobox();

        spinnerDialogMotivo = new SpinnerDialog(PopupItemOrcTodos.this, listaMotivo, "selecione o motivo do desconto");
        spinnerDialogMotivo.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtMotivo.setText(item);
                codMotivo = mListMotivo.get(position).getCodMotivo();
                descriMotivo = mListMotivo.get(position).getDescricao();
                motivo_selecionado = "S";

            }
        });

        edtMotivo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtMotivo.getRight() - edtMotivo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (edtMotivo.isEnabled()) {
                            spinnerDialogMotivo.showSpinerDialog();
                        }


                    }
                }
                return false;
            }
        });



        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        btn_alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !validarDesconto() || !validarMotivo()) {


                } else {
                    int itensPromocaoIgnorados = 0;

                    for (int p = 0 ; p < orcamento.getItens().size(); p++){

                        itemOrcamento = orcamento.getItens().get(p);

                        BigDecimal vldesc = new BigDecimal("0.00");
                        BigDecimal qtd = new BigDecimal("0.00");
                        BigDecimal valor = new BigDecimal("0.00");

                        if(edtDesconto.getText().toString().equals("")){
                            percDesc = 0.00d;
                        }else{
                            percDesc = Double.parseDouble(edtDesconto.getText().toString());
                        }


                        BigDecimal totalProd = new BigDecimal("0.00");
                        BigDecimal totalDesc = new BigDecimal("0.00");
                        BigDecimal percCalc = new BigDecimal("0.00");
                        BigDecimal vldescCalculado = new BigDecimal("0.00");
                        BigDecimal vlProduto = new BigDecimal("0.00");

                        totalProd = BigDecimal.valueOf(itemOrcamento.getPrecoUnit()).multiply(BigDecimal.valueOf(itemOrcamento.getQtcomp()));



                        valordodesconto= BigDecimal.valueOf(itemOrcamento.getPrecoUnit()).multiply(BigDecimal.valueOf(percDesc).divide(new BigDecimal("100")));

                        vldescCalculado = totalProd.multiply(BigDecimal.valueOf(percDesc)).divide(new BigDecimal("100"));

                        vldescCalculado = vldescCalculado.setScale(2, RoundingMode.FLOOR);

                        percCalc = vldescCalculado.divide(totalProd, MathContext.DECIMAL64).multiply(new BigDecimal("100.00"));

                        percCalc = percCalc.setScale(2,RoundingMode.UP);

                        if(percCalc.doubleValue() < percDesc ){
                            totalDesc = valordodesconto.multiply(BigDecimal.valueOf(itemOrcamento.getQtcomp()));
                            totalDesc = totalDesc.add(new BigDecimal("0.01"));
                        }else{
                            totalDesc = valordodesconto.multiply(BigDecimal.valueOf(itemOrcamento.getQtcomp()));
                        }



                        vlProduto = totalProd.subtract(totalDesc).divide(BigDecimal.valueOf(itemOrcamento.getQtcomp()), MathContext.DECIMAL64);

                        totalDesc = totalDesc.setScale(2,RoundingMode.FLOOR);
                        totalProd = totalProd.setScale(2,RoundingMode.FLOOR);
                        vlProduto = vlProduto.setScale(2,RoundingMode.FLOOR);

                        valor = valor.add(BigDecimal.valueOf(totalDesc.doubleValue()));
                        qtd = qtd.add(BigDecimal.valueOf(itemOrcamento.getQtcomp()));
                        vldesc = valor.divide(qtd, MathContext.DECIMAL64);

                        if (isItemComDescontoPromocionalBloqueado(itemOrcamento)) {
                            itensPromocaoIgnorados++;
                        } else if(itemOrcamento.getBloqueioDesc() != null && itemOrcamento.getBloqueioDesc().equals("S")){
                            itemOrcamento.setDpi(0);
                            itemOrcamento.setVlDescItem(0);
                            itemOrcamento.setCodMotivo(null);
                            itemOrcamento.setMotivoDesc(null);
                        }else{
                            itemOrcamento.setDpi(percDesc);
                            itemOrcamento.setVlDescItem(vldesc.doubleValue());
                            itemOrcamento.setCodMotivo(codMotivo);
                            itemOrcamento.setMotivoDesc(descriMotivo);
                        }

                        Orcamento_LancamentoActivity.atualizaItem(itemOrcamento,p);
                    }

                    if (itensPromocaoIgnorados > 0) {
                        Toast.makeText(PopupItemOrcTodos.this, "Itens promocionais vindos do motor não receberam desconto.", Toast.LENGTH_SHORT).show();
                    }


                    finish();


                }


            }
        });


    }

    private boolean isItemPromocao(ItemOrcamento item) {
        return item != null && "PR".equals(item.getSituacao());
    }

    private boolean isItemComDescontoPromocionalBloqueado(ItemOrcamento item) {
        return temDescontoMotorPromocional(item);
    }

    private boolean temDescontoMotorPromocional(ItemOrcamento item) {
        if (item == null || !isMotorPromocional(item.getIsMotor())) {
            return false;
        }

        return item.getVlDescItem() > 0
                || item.getVldescPromo() > 0
                || possuiTexto(item.getCodigoPromo())
                || possuiTexto(item.getNomePromo());
    }

    private boolean isMotorPromocional(String valor) {
        return possuiTexto(valor) && ("S".equalsIgnoreCase(valor.trim()) || "SIM".equalsIgnoreCase(valor.trim()));
    }

    private boolean possuiTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    public void ConsultarCombobox() {

        dialog = new ProgressDialog(PopupItemOrcTodos.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {



                    mListMotivo = helper.GetMotivoDesc();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            //Descomentar apos COVID-19

                            if (mListMotivo != null && mListMotivo.size() > 0) {
                                for (MotivoDesconto m : mListMotivo) {
                                    listaMotivo.add(m.getDescricao());
                                }
                            }





                        }



                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupItemOrcTodos.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupItemOrcTodos.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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



    public boolean validarDesconto() {
        String desconto = edtDesconto.getText().toString().trim();
        if (desconto.isEmpty() ) {
            if(percDescAlt>0){
                edtDesconto.setText(dfdesconto.format(percDescAlt));
            }else{
                layout_desconto.setErrorEnabled(true);
                layout_desconto.setError("Informe o % de desconto");
            }

            return false;

        } else {
            layout_desconto.setErrorEnabled(false);
            return true;
        }


    }











    public boolean validarMotivo() {
        if (Float.parseFloat(edtDesconto.getText().toString()) > 0 ) {
            if (edtMotivo.getText().toString().equals("")) {
                layout_Motivo.setErrorEnabled(true);
                layout_Motivo.setError("Informe o motivo do desconto");
                return false;
            } else {
                layout_Motivo.setErrorEnabled(false);
                return true;
            }
        }
        layout_Motivo.setErrorEnabled(false);
        return true;
    }





}
