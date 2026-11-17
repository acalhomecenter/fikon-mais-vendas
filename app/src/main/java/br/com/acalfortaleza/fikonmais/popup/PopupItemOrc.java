package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.os.Handler;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.AmbienteProduto;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.MotivoDesconto;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupItemOrc extends Activity {

    public static ItemOrcamento itemOrcamento;
    private TextView txtProduto;
    private TextInputEditText edtDesconto;
    private TextInputEditText edtVlDesconto;
    private TextInputEditText edtValorProduto;
    private TextInputEditText edtQuantidade;
    private TextInputEditText edtMotivo;
    private TextInputEditText edtFluigEncomenda;
    private TextInputEditText edtPrevisaoEncomenda;
    private TextInputLayout layout_desconto;
    private TextInputLayout layout_vldesconto;
    private TextInputLayout layout_vlproduto;
    private TextInputLayout layout_quantidade;
    private TextInputLayout layout_Motivo;
    private PedidosHelper helper;
    private OrcamentoHelper orcamentoHelper;
    private ProgressDialog dialog;
    private Handler handler;
    private String motivo_selecionado = "N";
    private List<MotivoDesconto> mListMotivo;
    private Button btn_fechar;
    private TextView txtTotal;
    private ImageView indicador;
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
    private ImageButton btnAmbientes;
    private TextInputLayout layoutFluigEncomenda;
    private TextInputLayout layoutPrevisaoEncomenda;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_itemorc);

        itemOrcamento = (ItemOrcamento) getIntent().getSerializableExtra("item");
        position = getIntent().getIntExtra("position",0);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .8));


        helper = new PedidosHelper();
        orcamentoHelper = new OrcamentoHelper();
        handler = new Handler();



        txtProduto = findViewById(R.id.pop_orc_produto);
        edtDesconto = findViewById(R.id.pop_orc_edtDesc);
        edtVlDesconto = findViewById(R.id.pop_orc_edtvlDesc);
        edtValorProduto = findViewById(R.id.pop_orc_edtPreco);
        edtQuantidade = findViewById(R.id.pop_orc_edtQuantidade);
        edtMotivo = findViewById(R.id.pop_orc_edtMotivo);
        edtFluigEncomenda = findViewById(R.id.PopupItemOrc_EdtFluigEncomenda);
        edtPrevisaoEncomenda = findViewById(R.id.PopupItemOrc_EdtPrevisaoEncomenda);
        txtTotal = findViewById(R.id.pop_orc_txtTotal);
        indicador = findViewById(R.id.pop_orc_indicador);
        btn_alterar = findViewById(R.id.pop_orc_btn_Alterar);
        layout_desconto = findViewById(R.id.pop_orc_layoutDesc);
        layout_vldesconto = findViewById(R.id.pop_orc_layoutValorDesc);
        layout_vlproduto = findViewById(R.id.pop_orc_layoutPreco);
        layout_quantidade = findViewById(R.id.pop_orc_layoutQuantidade);
        layout_Motivo = findViewById(R.id.pop_orc_layoutMotivo);
        btn_fechar = findViewById(R.id.pop_form_btnFechar2);
        btnAmbientes = findViewById(R.id.popup_itemOrc_btnAmbientes);
        layoutFluigEncomenda = findViewById(R.id.PopupItemOrc_LayoutFluigEncomenda);
        layoutPrevisaoEncomenda = findViewById(R.id.PopupItemOrc_LayoutPrevisaoEncomenda);

        real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        df = new DecimalFormat("0.00", symbols);
        dfdesconto = new DecimalFormat("0.0000", symbols);

        double descontoUnitarioExibicao = getValorDescontoUnitarioExibicao();
        double descontoPercentualExibicao = getPercentualDescontoExibicao();
        double precoLiquidoUnitarioExibicao = getPrecoLiquidoUnitarioExibicao();
        double valorLiquidoTotalExibicao = getValorLiquidoTotalExibicao();

        txtProduto.setText(itemOrcamento.getCoditprod() + " - " + itemOrcamento.getDescricao());
        edtDesconto.setText(dfdesconto.format(descontoPercentualExibicao));
        edtVlDesconto.setText(df.format(descontoUnitarioExibicao * itemOrcamento.getQtcomp()));
        edtValorProduto.setText(real.format(precoLiquidoUnitarioExibicao));
        edtQuantidade.setText(df.format(itemOrcamento.getQtcomp()));
        txtTotal.setText(real.format(valorLiquidoTotalExibicao));
        edtFluigEncomenda.setText(itemOrcamento.getFluigEncomenda() == 0 ? "" : String.valueOf(itemOrcamento.getFluigEncomenda()));
        edtPrevisaoEncomenda.setEnabled(false);
        if (itemOrcamento.getDataPrevisaoEncomenda()!= null && !itemOrcamento.getDataPrevisaoEncomenda().equals("") && !itemOrcamento.getDataPrevisaoEncomenda().equals("0001-01-01T00:00:00") ) {
            DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date date = entrada.parse(itemOrcamento.getDataPrevisaoEncomenda());
                edtPrevisaoEncomenda.setText((formato.format(date)));
                            } catch (ParseException e) {
                e.printStackTrace();
            }


        }else{
            edtPrevisaoEncomenda.setText("");
        }

        valorOriginal = itemOrcamento.getPrecoUnit();
        quantidade = itemOrcamento.getQtcomp();
        percDesc = descontoPercentualExibicao;
        valordodesconto = BigDecimal.valueOf(descontoUnitarioExibicao);

        configurarBloqueioDescontoPromocao();

              edtQuantidade.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    edtQuantidade.setText("");
                }
                return false;
            }
        });

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




        if (itemOrcamento.getPmargRent() >= 36) {

            indicador.setImageResource(R.drawable.fundo_bt_flutuante);

        }
        if (itemOrcamento.getPmargRent() >= 34 && itemOrcamento.getPmargRent() < 36) {

            indicador.setImageResource(R.drawable.fundo_bt_flutuante_orange);
        }


        if (itemOrcamento.getPmargRent() < 34) {

            indicador.setImageResource(R.drawable.fundo_bt_flutuante_red);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            edtPrevisaoEncomenda.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
        }

        if(itemOrcamento.getSituacao()!=null && !itemOrcamento.getSituacao().equals("EN")){
            ViewGroup.LayoutParams paramsFluig = layoutFluigEncomenda.getLayoutParams();
            ViewGroup.LayoutParams paramsPrevisao = layoutPrevisaoEncomenda.getLayoutParams();
            paramsFluig.height = 0;
            paramsFluig.width = 0;
            paramsPrevisao.height = 0;
            paramsPrevisao.width = 0;
            layoutFluigEncomenda.setLayoutParams(paramsFluig);
            layoutPrevisaoEncomenda.setLayoutParams(paramsPrevisao);
            edtFluigEncomenda.setText("");
            edtPrevisaoEncomenda.setText("");
            itemOrcamento.setFluigEncomenda(0);
            itemOrcamento.setDataPrevisaoEncomenda("");
        }

        /*edtDesconto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b ){
                    if(!validarQtd() || !validarDesconto() || !validarDescontoValor() || descValor) {

                    }else{
                        descPerc=true;
                        descValor=false;
                        calculaPercentual();
                    }

                }

            }
        });*/

        /*edtVlDesconto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b ){
                    if(!validarQtd() || !validarVlDesconto() || !validarDescontoValor() || descPerc) {

                    }else{
                        descPerc=false;
                        descValor=true;
                        calculaValorDesconto();
                    }

                }

            }
        });*/


          edtDesconto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
              @Override
              public boolean onEditorAction(TextView v, int i, KeyEvent event) {

                  if (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || i == EditorInfo.IME_ACTION_DONE) {
                      if (!validarQtd() || !validarVlDesconto() || !validarDescontoValor() || !validarDesconto()  ) {

                      } else {
                          if (Float.parseFloat(edtDesconto.getText().toString()) >0) {
                              calculaPercentual();
                              edtMotivo.setEnabled(true);
                              layout_Motivo.setEnabled(true);
                              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                  edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                              }
                          }else{
                              calculaPercentual();
                              edtMotivo.setEnabled(false);
                              layout_Motivo.setEnabled(false);
                              edtMotivo.setText("");
                              itemOrcamento.setCodMotivo("");
                              itemOrcamento.setMotivoDesc("");
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

        edtVlDesconto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent event) {

                if(event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || i == EditorInfo.IME_ACTION_DONE) {
                    if (!validarQtd() || !validarVlDesconto() || !validarDescontoValor() || descPerc) {

                    } else {
                        if (Float.parseFloat(edtVlDesconto.getText().toString()) > 0) {
                            calculaValorDesconto();
                            edtMotivo.setEnabled(true);
                            layout_Motivo.setEnabled(true);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                            }
                        } else {
                            edtMotivo.setEnabled(false);
                            layout_Motivo.setEnabled(false);
                            calculaValorDesconto();
                            edtMotivo.setText("");
                            itemOrcamento.setCodMotivo("");
                            itemOrcamento.setMotivoDesc("");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
                            }
                        }


                    }
                    descPerc=false;
                    descValor=true;
                    return false;

                }


                descPerc=false;
                descValor=true;
                return false;
            }
        });

        edtQuantidade.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b ){
                    if(!validarQtd()|| !validarConversao()) {


                    }else{
                        calculaQuantidade();
                    }

                }

            }
        });

        ConsultarCombobox();

        spinnerDialogMotivo = new SpinnerDialog(PopupItemOrc.this, listaMotivo, "selecione o motivo do desconto");
        spinnerDialogMotivo.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtMotivo.setText(item);
                itemOrcamento.setCodMotivo(mListMotivo.get(position).getCodMotivo());
                itemOrcamento.setMotivoDesc(mListMotivo.get(position).getDescricao());
                motivo_selecionado = "S";

            }
        });

        edtPrevisaoEncomenda.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtPrevisaoEncomenda.getRight() - edtPrevisaoEncomenda.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        if(edtPrevisaoEncomenda.isEnabled()) {
                            DatePickerDialog datePickerDialog =  new DatePickerDialog(PopupItemOrc.this, dateinicial, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH));
                            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() -1000);
                            datePickerDialog.show();

                        }

                    }
                }
                return false;
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
                boolean itemPromocao = isItemComDescontoPromocionalBloqueado();
                if (!validarQtd() || !validarConversao() || !validarQuantidadeAmbientes()
                        || (!itemPromocao && (!validarVlDesconto() || !validarDescontoValor() || !validarDesconto() || !validarMotivo() || !validarBloqueioDesconto()))) {


                } else {

                    BigDecimal vldesc = new BigDecimal("0.00");
                    BigDecimal qtd = new BigDecimal("0.00");
                    BigDecimal valor = new BigDecimal("0.00");

                    valor = valor.add(BigDecimal.valueOf(Double.parseDouble(edtVlDesconto.getText().toString())));
                    qtd = qtd.add(BigDecimal.valueOf(Double.parseDouble(edtQuantidade.getText().toString())));
                    vldesc = valor.divide(qtd, MathContext.DECIMAL64);

                    if (!itemPromocao) {
                        if(itemOrcamento.getBloqueioDesc().equals("S")){
                            itemOrcamento.setDpi(0.00d);
                            itemOrcamento.setVlDescItem(0.00d);
                            itemOrcamento.setCodMotivo("");
                            itemOrcamento.setMotivoDesc("");

                        }else{
                            itemOrcamento.setDpi(Double.parseDouble(edtDesconto.getText().toString()));
                            itemOrcamento.setVlDescItem(vldesc.doubleValue());

                        }
                    }

                    itemOrcamento.setQtcomp(Double.parseDouble(edtQuantidade.getText().toString()));

                     Orcamento_LancamentoActivity.atualizaItem(itemOrcamento,position);
                     finish();


                }


            }
        });


        if(itemOrcamento == null || ((long)Orcamento_LancamentoActivity.orcamento.getNumorc()) == 0){
            btnAmbientes.setEnabled(false);
            btnAmbientes.setVisibility(View.INVISIBLE);
        }
        else{

            btnAmbientes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layout_quantidade.setErrorEnabled(false);

                    if(edtQuantidade.getText()!=null && Double.parseDouble(edtQuantidade.getText().toString()) != itemOrcamento.getQtcomp()) {

                        layout_quantidade.setErrorEnabled(true);
                        layout_quantidade.setError("Quantidade do item alterada. Salve o item antes de editar os ambientes.");

                        return;

                    }


                    if(
                        itemOrcamento!=null && Orcamento_LancamentoActivity.orcamento!=null
                        && Orcamento_LancamentoActivity.orcamento.getNumorc()>0
                    ){

                        Intent intent = new Intent(PopupItemOrc.this, PopupAmbienteProduto.class);
                        intent.putExtra("numorc", Orcamento_LancamentoActivity.orcamento.getNumorc());
                        intent.putExtra("qtcomp", Double.parseDouble(edtQuantidade.getText().toString()));
                        intent.putExtra("indiceItemOrcamento", position);
                        intent.putExtra("itemOrcamento", itemOrcamento);
                        startActivity(intent);

                    }

                }
            });

        }

        edtFluigEncomenda.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE || i == EditorInfo.IME_ACTION_NEXT){

                    if(edtFluigEncomenda.getText()!=null && !edtFluigEncomenda.getText().toString().trim().equals("")){

                        long fluig = Long.parseLong(edtFluigEncomenda.getText().toString());

                        if(fluig>0){
                            consultarPrevisaoEncomenda(fluig);
                            return true;
                        }

                    }

                    edtFluigEncomenda.setText("");
                    edtPrevisaoEncomenda.setText("");
                    itemOrcamento.setFluigEncomenda(0);
                    itemOrcamento.setDataPrevisaoEncomenda("");
                    return false;

                }
                return false;
            }
        });


    }

    final DatePickerDialog.OnDateSetListener dateinicial = new DatePickerDialog.OnDateSetListener() {
        String day ="";
        String month="";
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            if(dayOfMonth<10){
                day = "0"+dayOfMonth;
            }else{
                day = String.valueOf(dayOfMonth);
            }
            if(monthOfYear+1<10){
                month = "0"+(monthOfYear+1);
            }else{
                month = String.valueOf((monthOfYear+1));
            }
            edtPrevisaoEncomenda.setText(day + "/" +month+ "/" + year );


                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat entrada = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date date = entrada.parse(edtPrevisaoEncomenda.getText().toString());

                    itemOrcamento.setDataPrevisaoEncomenda(s.format(date));

                } catch (ParseException e) {
                    e.printStackTrace();
                }



        }

    };




    public void ConsultarCombobox() {

        dialog = new ProgressDialog(PopupItemOrc.this);
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




                                if (itemOrcamento.getCodMotivo() != null && !itemOrcamento.getCodMotivo().equals("")) {
                                    layout_Motivo.setEnabled(true);
                                    edtMotivo.setText(itemOrcamento.getMotivoDesc());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                                    }
                                    motivo_selecionado = "S";
                                }

                                if(itemOrcamento.getMotivoDesc() != null && !itemOrcamento.getMotivoDesc().equals("")){
                                    edtMotivo.setText(itemOrcamento.getMotivoDesc());
                                }

                            }



                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupItemOrc.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupItemOrc.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    public boolean validarQtd() {
        String qtd = edtQuantidade.getText().toString().trim();
        if (qtd.isEmpty() || Double.parseDouble(qtd) == 0) {
            if(quantidade>0){
                edtQuantidade.setText(df.format(quantidade));
            }else{
                layout_quantidade.setErrorEnabled(true);
                layout_quantidade.setError("Informe a quantidade");
            }

            return false;

        } else {
            layout_quantidade.setErrorEnabled(false);
            return true;
        }
    }


    public boolean validarBloqueioDesconto() {
        double vldesconto = Double.parseDouble(edtVlDesconto.getText().toString().trim()) / Double.parseDouble(edtQuantidade.getText().toString());
        if (itemOrcamento.getBloqueioDesc() != null && itemOrcamento.getBloqueioDesc().equals("S") && vldesconto>0) {

            layout_desconto.setErrorEnabled(true);
            layout_desconto.setError("Este produto não permite desconto! ");
            return false;
        }else {
            layout_desconto.setErrorEnabled(false);
            return true;
        }
    }

    private boolean isItemPromocao() {
        return itemOrcamento != null && "PR".equals(itemOrcamento.getSituacao());
    }

    private boolean deveAplicarPromocaoPrazoNaExibicao() {
        return Orcamento_LancamentoActivity.deveAplicarPromocaoPrazoNaExibicao(itemOrcamento);
    }

    private double getPrecoLiquidoUnitarioExibicao() {
        if (itemOrcamento == null) {
            return 0.0d;
        }
        if (deveAplicarPromocaoPrazoNaExibicao()) {
            return (itemOrcamento.getPrecoUnit() - itemOrcamento.getVlDescItem()) / 0.95d;
        }
        return itemOrcamento.getPrecoUnit() - itemOrcamento.getVlDescItem();
    }

    private double getValorLiquidoTotalExibicao() {
        return BigDecimal.valueOf(getPrecoLiquidoUnitarioExibicao())
                .multiply(BigDecimal.valueOf(itemOrcamento != null ? itemOrcamento.getQtcomp() : 0.0d))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private double getValorDescontoUnitarioExibicao() {
        if (itemOrcamento == null) {
            return 0.0d;
        }
        return BigDecimal.valueOf(Math.max(0.0d, itemOrcamento.getPrecoUnit() - getPrecoLiquidoUnitarioExibicao()))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private double getPercentualDescontoExibicao() {
        if (itemOrcamento == null || itemOrcamento.getPrecoUnit() <= 0) {
            return 0.0d;
        }
        return BigDecimal.valueOf((getValorDescontoUnitarioExibicao() / itemOrcamento.getPrecoUnit()) * 100.0d)
                .setScale(4, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private boolean isItemComDescontoPromocionalBloqueado() {
        return temDescontoMotorPromocional(itemOrcamento);
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

    private void configurarBloqueioDescontoPromocao() {
        if (!isItemComDescontoPromocionalBloqueado()) {
            return;
        }

        edtDesconto.setEnabled(false);
        edtVlDesconto.setEnabled(false);
        edtMotivo.setEnabled(false);
        layout_desconto.setEnabled(false);
        layout_vldesconto.setEnabled(false);
        layout_Motivo.setEnabled(false);
        layout_desconto.setErrorEnabled(false);
        layout_vldesconto.setErrorEnabled(false);
        layout_Motivo.setErrorEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
        }
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

    public boolean validarVlDesconto() {
        String vldesconto = edtVlDesconto.getText().toString().trim();
        if (vldesconto.isEmpty() ) {

                layout_vldesconto.setErrorEnabled(true);
                layout_vldesconto.setError("Informe o valor do desconto");

            return false;

        } else {
            layout_vldesconto.setErrorEnabled(false);
            return true;
        }


    }

    public boolean validarDescontoValor() {
        if(!edtQuantidade.getText().equals("")) {
            try{
                double vldesconto = Double.parseDouble(edtVlDesconto.getText().toString().trim()) / Double.parseDouble(edtQuantidade.getText().toString());
            }catch (Exception e){
                layout_vldesconto.setErrorEnabled(true);
                layout_vldesconto.setError("Valor do desconto é inválido ");
                edtVlDesconto.setText("0.00");
                calculaValorDesconto();
                return false;
            }
            double vldesconto = Double.parseDouble(edtVlDesconto.getText().toString().trim()) / Double.parseDouble(edtQuantidade.getText().toString());

            if ((vldesconto) > valorOriginal) {
                layout_vldesconto.setErrorEnabled(true);
                layout_vldesconto.setError("Valor do desconto é inválido ");
                edtVlDesconto.setText("0.00");
                calculaValorDesconto();
                return false;

            } else {
                layout_vldesconto.setErrorEnabled(false);
                return true;
            }
        }
        return false;
    }

    public boolean validarConversao(){
        int qtdArredondado =0;
        String qtdDigitada ="0";
        String qtdCalculada = "0";
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        final DecimalFormat df = new DecimalFormat("0.00",symbols);


        float divisao = (float) (Double.parseDouble(edtQuantidade.getText().toString())/itemOrcamento.getQtemb());
        qtdArredondado = (int) Math.ceil(divisao);
        qtdDigitada = df.format(Double.parseDouble(edtQuantidade.getText().toString()));
        qtdCalculada = df.format(itemOrcamento.getQtemb()*qtdArredondado);

        if(!itemOrcamento.getNcm().equals("32041700") && !itemOrcamento.getNcm().equals("32064990")) {
            if (!qtdDigitada.equals(qtdCalculada)) {
                layout_quantidade.setErrorEnabled(true);
                if (itemOrcamento.getQtemb() == 1) {
                    layout_quantidade.setError("Produto vendido em unidade, qtd alterada para: " + (df.format(itemOrcamento.getQtemb() * qtdArredondado)));
                } else {
                    layout_quantidade.setError("Produto vendido em caixa, qtd alterada para: " + (df.format(itemOrcamento.getQtemb() * qtdArredondado)));
                }
                edtQuantidade.setText(df.format(itemOrcamento.getQtemb() * qtdArredondado));
                return false;

            } else {
                layout_quantidade.setErrorEnabled(false);
                return true;
            }
        }else{
            layout_quantidade.setErrorEnabled(false);
            return true;
        }

    }


    public void calculaMargem(BigDecimal total){

        double vlEncargos;
        double vlIcms;
        double custo;
        double vlTotal;



        vlEncargos = total.multiply(BigDecimal.valueOf(itemOrcamento.getEncargos())).divide(new BigDecimal("100")).doubleValue();
        if(itemOrcamento.getIcmsRed()==0) {
            vlIcms = total.multiply(BigDecimal.valueOf(itemOrcamento.getIcms())).divide(new BigDecimal("100")).doubleValue();
        }else{
            vlIcms = total.subtract(total.multiply(BigDecimal.valueOf(itemOrcamento.getEncargos())).divide(new BigDecimal("100")).
                    multiply(BigDecimal.valueOf(itemOrcamento.getIcms() / 100))).doubleValue();
        }
        custo = itemOrcamento.getCusto()*quantidade;

        margem = ((total.doubleValue() - vlEncargos - vlIcms - custo) / (total.doubleValue())) * 100;


        if(margem>=36){

            indicador.setImageResource(R.drawable.fundo_bt_flutuante);

        }
        if(margem>=34 && margem<36){

            indicador.setImageResource(R.drawable.fundo_bt_flutuante_orange);
        }


        if(margem<34){

            indicador.setImageResource(R.drawable.fundo_bt_flutuante_red);
        }
    }

    public void calculaPercentual(){
        final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');

        final DecimalFormat df = new DecimalFormat("0.00", symbols);
        if(edtQuantidade.getText().toString().equals("")){
            quantidade=itemOrcamento.getQtcomp();
        }else{
            quantidade = Double.parseDouble(edtQuantidade.getText().toString());
        }
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

        totalProd = BigDecimal.valueOf(valorOriginal).multiply(BigDecimal.valueOf(quantidade));



        valordodesconto= BigDecimal.valueOf(valorOriginal).multiply(BigDecimal.valueOf(percDesc).divide(new BigDecimal("100")));

        vldescCalculado = totalProd.multiply(BigDecimal.valueOf(percDesc)).divide(new BigDecimal("100"));

        vldescCalculado = vldescCalculado.setScale(2, RoundingMode.FLOOR);

        percCalc = vldescCalculado.divide(totalProd, MathContext.DECIMAL64).multiply(new BigDecimal("100.00"));

        percCalc = percCalc.setScale(2,RoundingMode.UP);

        if(percCalc.doubleValue() < percDesc ){
            totalDesc = valordodesconto.multiply(BigDecimal.valueOf(quantidade));
            totalDesc = totalDesc.add(new BigDecimal("0.01"));
        }else{
            totalDesc = valordodesconto.multiply(BigDecimal.valueOf(quantidade));
        }



        vlProduto = totalProd.subtract(totalDesc).divide(BigDecimal.valueOf(quantidade), MathContext.DECIMAL64);

        totalDesc = totalDesc.setScale(2,RoundingMode.FLOOR);
        totalProd = totalProd.setScale(2,RoundingMode.FLOOR);
        vlProduto = vlProduto.setScale(2,RoundingMode.FLOOR);

        edtVlDesconto.setText(df.format(totalDesc));
        edtValorProduto.setText(real.format(vlProduto));
        txtTotal.setText(real.format(totalProd.subtract(totalDesc)));
        calculaMargem(totalProd.subtract(totalDesc));

    }


    public boolean validarMotivo() {
        if (Float.parseFloat(edtDesconto.getText().toString()) > 0 || Float.parseFloat(edtVlDesconto.getText().toString()) > 0) {
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


    public void calculaValorDesconto(){
        final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        final DecimalFormat df = new DecimalFormat("0.00", symbols);
        final DecimalFormat dfdesconto = new DecimalFormat("0.0000",symbols);
        quantidade = Double.parseDouble(edtQuantidade.getText().toString());
        percDesc = 0.0000d;
        valordodesconto = BigDecimal.valueOf(Double.parseDouble(edtVlDesconto.getText().toString())).divide(BigDecimal.valueOf(quantidade), MathContext.DECIMAL64);

        percDesc = Double.valueOf((valordodesconto.doubleValue()/valorOriginal)*100).doubleValue();

        edtDesconto.setText(dfdesconto.format(percDesc));
        edtValorProduto.setText(real.format(valorOriginal - (valordodesconto).doubleValue()));
        txtTotal.setText(real.format((valorOriginal - (valordodesconto.doubleValue()))*quantidade));
        calculaMargem(BigDecimal.valueOf(valorOriginal - (valordodesconto.doubleValue())*quantidade));

    }

    public void calculaQuantidade(){
        final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            txtTotal.setText(real.format(valorOriginal  * Double.parseDouble(edtQuantidade.getText().toString())));
            calculaPercentual();



    }

    public boolean validarQuantidadeAmbientes(){

        try {

            double somatorioAmbientes = 0.0;

            if(itemOrcamento.getListAmbientesProdutos()!=null && itemOrcamento.getListAmbientesProdutos().size()>0){

                for (AmbienteProduto ambiente: itemOrcamento.getListAmbientesProdutos()) {
                    somatorioAmbientes += ambiente.getQuantidade();
                }

            }

            if(edtQuantidade.getText()!=null && Double.parseDouble(edtQuantidade.getText().toString()) < somatorioAmbientes ){

                layout_quantidade.setErrorEnabled(true);
                layout_quantidade.setError("Quantidade do item inferior ao somatorio dos ambientes. Ajuste as quantidades dos ambientes do item antes de salvar");
                return false;

            }

            layout_quantidade.setErrorEnabled(false);
            return true;

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        layout_quantidade.setErrorEnabled(true);
        layout_quantidade.setError("Quantidade do item inferior ao somatorio dos ambientes. Ajuste as quantidades dos ambientes do item antes de salvar");
        return false;

    }


    public boolean consultarPrevisaoEncomenda(long fluig){

        try {
            ProgressDialog progressDialog = new ProgressDialog(PopupItemOrc.this);
            progressDialog.setTitle("Consultando");
            progressDialog.setMessage("Por favor aguarde");
            progressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int coditprod = (int) itemOrcamento.getCoditprod();
                    String previsao = orcamentoHelper.ConsultarPrevisaoEncomenda(fluig, coditprod);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            itemOrcamento.setFluigEncomenda(0);
                            itemOrcamento.setDataPrevisaoEncomenda("");
                            edtFluigEncomenda.setText("");
                            edtPrevisaoEncomenda.setText("");

                            if(previsao!=null){

                                if(previsao.trim().equals("")){

                                    new AlertDialog.Builder(PopupItemOrc.this)
                                            .setTitle("Não encontrado")
                                            .setMessage("Não foi encontrada nenhuma data de previsão de encomenda para o fluig " + fluig + "." +
                                                    " Verifique se o número do fluig está correto e se a data de previsão de encomenda está preenchida nesse fluig." +
                                                    " Depois tente consultar novamente.")
                                            .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();

                                }
                                else {

                                    itemOrcamento.setFluigEncomenda(fluig);
                                    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                                    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");

                                    try {
                                        Date date = entrada.parse(previsao);
                                        edtPrevisaoEncomenda.setText((formato.format(date)));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    itemOrcamento.setDataPrevisaoEncomenda(previsao);
                                    edtFluigEncomenda.setText(String.valueOf(fluig));

                                }

                            }
                            else{

                                new AlertDialog.Builder(PopupItemOrc.this)
                                        .setTitle("Erro ao consultar data")
                                        .setMessage("Houve um erro ao consultar a data de previsão da encomenda. Verifique sua conexão e tente novamente.")
                                        .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        }).show();

                            }

                            progressDialog.dismiss();
                        }
                    });

                }
            }).start();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return false;

    }

    public boolean validarPrevisaoEncomenda(){

        try {

            if(itemOrcamento!=null && itemOrcamento.getSituacao().equals("EN")){

                long fluig = itemOrcamento.getFluigEncomenda();
                String data = itemOrcamento.getDataPrevisaoEncomenda();

                if( data!=null && !data.trim().equals("")){

                    layoutPrevisaoEncomenda.setErrorEnabled(false);
                    return true;

                }else{
                    layoutPrevisaoEncomenda.setErrorEnabled(true);
                    layoutPrevisaoEncomenda.setError("Informe a data de previsão da encomenda");
                    return false;
                }

            }else{
                layoutPrevisaoEncomenda.setErrorEnabled(false);
                return true;
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        layoutPrevisaoEncomenda.setErrorEnabled(false);
        return true;

    }

}
