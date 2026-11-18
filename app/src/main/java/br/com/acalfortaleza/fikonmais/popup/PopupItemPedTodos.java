package br.com.acalfortaleza.fikonmais.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.ItemPedidoAdapter;
import br.com.acalfortaleza.fikonmais.applications.Mask;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.Enderecos;
import br.com.acalfortaleza.fikonmais.models.Filial;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Lotevencto;
import br.com.acalfortaleza.fikonmais.models.MotivoDesconto;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupItemPedTodos extends Activity {

    private ItemPedidos itemPedido;

    private ItemPedidos novoItem;

    private Pedidos pedido;

    private TextInputEditText edtDesconto;

    private TextInputEditText edtTipoPed;
    private TextInputEditText edtFilSaida;
    private TextInputEditText edtFilTransf;
    private TextInputEditText edtMotivo;
    private TextInputLayout layout_desconto;

    private TextInputLayout layout_FilSaida;
    private TextInputLayout layout_FilTransf;
    private TextInputLayout layout_Motivo;
    private TextInputLayout layout_TipoPed;
    private Button btn_fechar;

    private Button btn_alterar;

    double quantidade;
    double percDesc = 0.0000d;
    double percDescAlt = 0.0000d;
    BigDecimal valordodesconto = new BigDecimal("0.00");
    private double margem;
    private DecimalFormat df;
    private DecimalFormatSymbols symbols;
    private DecimalFormat dfdesconto;
    private NumberFormat real;
    private ProgressDialog dialog;
    private Handler handler;
    private PedidosHelper helper;
    private List<Filial> mListSaida;
    private List<Filial> mListTransf;
    private List<Lotevencto> mListLote;
    private List<MotivoDesconto> mListMotivo;
    private ArrayList<String> listaMotivo = new ArrayList<>();
    private ArrayList<String> listaFilial = new ArrayList<>();
    private ArrayList<String> listaTipo = new ArrayList<>();
    private ArrayList<String> listaLote = new ArrayList<>();
    private HashMap<String, String> usuarioLogado;
    private LoginHelper helperLogin;
    private String filialsaida_selecionada = "N";
    private String filialtransf_selecionada = "N";
    private String motivo_selecionado = "N";
    private String lote_selecionado = "N";
    private Boolean descPerc =false;
    private Boolean descValor=false;
    private SpinnerDialog spinnerDialog;
    private SpinnerDialog spinnerDialogMotivo;
    private SpinnerDialog spinnerDialogTransf;
    private SpinnerDialog spinnerDialogTipo;
    private Calendar myCalendar = Calendar.getInstance();
    private TextInputEditText edtDataEntrega;
    private TextInputLayout layout_dataentrega;
    private Switch encomendaSwitch;
    private Switch descontoSwitch;
    private Switch tipopedSwitch;
    private Switch saidaSwitch;
    private Switch transfeSwitch;
    private String DataEntrega;




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

            // Implementação Option 1: Validação Just-in-Time via API
            String dataFormatada = day + "/" +month+ "/" + year;
            validarDataEntregaComApi(dataFormatada);
        }

    };

    // Método para validar data com dados atualizados da API
    private void validarDataEntregaComApi(final String dataFormatada) {
        final ProgressDialog pd = new ProgressDialog(PopupItemPedTodos.this);
        pd.setMessage("Verificando disponibilidade na data...");
        pd.setCancelable(false);
        pd.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String cpfCliente = pedido.getCliente().getCgccpf();
                    int codFilial = Integer.parseInt(usuarioLogado.get("codfil"));
                    String cpfConsultor = usuarioLogado.containsKey("cpf") ? usuarioLogado.get("cpf") : "";

                    if (cpfConsultor == null || cpfConsultor.isEmpty()) {
                        if (usuarioLogado.containsKey("login")) cpfConsultor = usuarioLogado.get("login");
                    }

                    // Busca dados atualizados do cliente
                    final br.com.acalfortaleza.fikonmais.models.Cliente clienteAtualizado = helper.GetClientePedidos(cpfCliente, codFilial, cpfConsultor);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pd.isShowing()) pd.dismiss();

                            if (clienteAtualizado != null && clienteAtualizado.getEnderecos() != null) {
                                int indexEndereco = br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.getEndent();

                                // Atualiza as flags do endereço selecionado no objeto estático global
                                if (indexEndereco >= 0 && indexEndereco < clienteAtualizado.getEnderecos().size()) {
                                    Enderecos enderecoAtualizado = clienteAtualizado.getEnderecos().get(indexEndereco);
                                    Enderecos enderecoAntigo = br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.getCliente().getEnderecos().get(indexEndereco);

                                    enderecoAntigo.setFldomingo(enderecoAtualizado.getFldomingo());
                                    enderecoAntigo.setFlsegunda(enderecoAtualizado.getFlsegunda());
                                    enderecoAntigo.setFlterca(enderecoAtualizado.getFlterca());
                                    enderecoAntigo.setFlquarta(enderecoAtualizado.getFlquarta());
                                    enderecoAntigo.setFlquinta(enderecoAtualizado.getFlquinta());
                                    enderecoAntigo.setFlsexta(enderecoAtualizado.getFlsexta());
                                    enderecoAntigo.setFlsabado(enderecoAtualizado.getFlsabado());

                                    android.util.Log.d("PopupItemPedTodos", "Flags de entrega atualizadas via API para validação.");
                                }
                            }

                            // Define o texto e chama a validação original (agora com dados atualizados)
                            edtDataEntrega.setText(dataFormatada);

                            if (validarDataentrega()) {
                                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                                DateFormat entrada = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    Date date = entrada.parse(edtDataEntrega.getText().toString());
                                    itemPedido.setDtentrega(s.format(date));
                                    DataEntrega = s.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // Se inválido, limpa o campo (a mensagem de erro é exibida pelo validarDataentrega)
                                edtDataEntrega.setText("");
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (pd.isShowing()) pd.dismiss();
                            Toast.makeText(PopupItemPedTodos.this, "Não foi possível verificar a disponibilidade online. Usando dados locais.", Toast.LENGTH_SHORT).show();

                            // Fallback: valida com dados locais
                            edtDataEntrega.setText(dataFormatada);
                            if (validarDataentrega()) {
                                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                                DateFormat entrada = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    Date date = entrada.parse(edtDataEntrega.getText().toString());
                                    itemPedido.setDtentrega(s.format(date));
                                    DataEntrega = s.format(date);
                                } catch (ParseException e2) {
                                    e2.printStackTrace();
                                }
                            } else {
                                edtDataEntrega.setText("");
                            }
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_itemped_todos);

        pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
        itemPedido = (ItemPedidos) getIntent().getSerializableExtra("item");


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .8));

        helper = new PedidosHelper();
        handler = new Handler();
        helperLogin = new LoginHelper(this);
        usuarioLogado = helperLogin.GetUsuarioSalvo();


        edtDesconto = findViewById(R.id.pop_itped_edtDesc);

        edtFilSaida = findViewById(R.id.pop_itped_edtFilSaida);
        edtFilTransf = findViewById(R.id.pop_itped_edtFilTransf);
        edtDataEntrega = findViewById(R.id.pop_itped_edtDataEntrega);
        edtMotivo = findViewById(R.id.pop_itped_edtMotivo);
        edtTipoPed = findViewById(R.id.pop_itped_edtTipoPed);
        btn_alterar = findViewById(R.id.pop_itped_btn_Alterar);
        layout_desconto = findViewById(R.id.pop_itped_layoutDesc);
        layout_FilSaida = findViewById(R.id.pop_itped_layoutFilSaida);
        layout_FilTransf = findViewById(R.id.pop_itped_layoutFilTrans);
        layout_Motivo = findViewById(R.id.pop_itped_layoutMotivo);
        layout_dataentrega = findViewById(R.id.pop_itped_layoutedtDataEntrega);
        layout_TipoPed = findViewById(R.id.pop_itped_layoutTipoPed);
        encomendaSwitch = findViewById(R.id.pop_itped_encomenda);
        descontoSwitch = findViewById(R.id.pop_itped_desconto);
        tipopedSwitch = findViewById(R.id.pop_itped_tipoped);
        saidaSwitch = findViewById(R.id.pop_itped_filsaida);
        transfeSwitch = findViewById(R.id.pop_itped_transferencia);
        btn_fechar = findViewById(R.id.pop_itped_btnFechar);

        //Retirar esse parte apos o COVID-19
        edtFilTransf.setEnabled(false);


        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });




        real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        df = new DecimalFormat("0.00", symbols);
        dfdesconto = new DecimalFormat("0.0000", symbols);


        edtDesconto.setText(dfdesconto.format(itemPedido.getDpi()));



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
                if(event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || i == EditorInfo.IME_ACTION_DONE) {
                    if ( !validarDesconto()   ) {

                    } else {
                        if (Float.parseFloat(edtDesconto.getText().toString()) >0) {

                            edtMotivo.setEnabled(true);
                            layout_Motivo.setEnabled(true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                            }
                        } else {

                            edtMotivo.setEnabled(false);
                            layout_Motivo.setEnabled(false);
                            edtMotivo.setText("");
                            itemPedido.setCodMotivo("");
                            itemPedido.setMotivoDesc("");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
                            }
                        }

                    }
                    descPerc=true;
                    descValor=false;
                    return false;
                }

                descPerc=true;
                descValor=false;
                return false;
            }
        });




        encomendaSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(encomendaSwitch.isChecked()){
                    edtFilTransf.setEnabled(false);
                    edtFilTransf.setText("");
                    itemPedido.setCodFilTransf(0);
                    transfeSwitch.setChecked(true);
                    transfeSwitch.setEnabled(false);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));


                    }


                }else{


                    edtFilTransf.setText("");
                    itemPedido.setCodFilTransf(0);
                    transfeSwitch.setEnabled(true);
                    if(edtTipoPed.getText().toString().equals("Retira")){
                        edtFilTransf.setEnabled(false);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
                        }
                    }

                    if(edtTipoPed.getText().toString().equals("Retira Posterior")){
                        edtFilTransf.setEnabled(true);
                        transfeSwitch.setEnabled(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                        }
                    }

                    if(edtTipoPed.getText().toString().equals("Entrega")){
                        edtFilTransf.setEnabled(true);
                        transfeSwitch.setEnabled(true);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                        }
                    }




                }
            }
        });

        btn_alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tipopedSwitch.isChecked()) {
                    if (!validarDataentrega()) {
                        return;
                    }
                }

                if (descontoSwitch.isChecked()) {
                    if (!validarDesconto() || !validarMotivo()) {
                        Toast.makeText(PopupItemPedTodos.this, "Informe o motivo do desconto", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                for(int p = 0; p < pedido.getItens().size(); p++){
                        boolean aplicarDescontoItem = false;


                        novoItem = pedido.getItens().get(p);



                        if (encomendaSwitch.isChecked()) {
                            if (!novoItem.getSituacao().equals("EN")) {
                                novoItem.setEncomenda("N");
                            } else {
                                novoItem.setEncomenda("E");
                            }
                        }


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

                        totalProd = BigDecimal.valueOf(novoItem.getPrecoUnit()).multiply(BigDecimal.valueOf(novoItem.getQtcomp()));



                        valordodesconto= BigDecimal.valueOf(novoItem.getPrecoUnit()).multiply(BigDecimal.valueOf(percDesc).divide(new BigDecimal("100")));

                        vldescCalculado = totalProd.multiply(BigDecimal.valueOf(percDesc)).divide(new BigDecimal("100"));

                        vldescCalculado = vldescCalculado.setScale(2, RoundingMode.FLOOR);

                        percCalc = vldescCalculado.divide(totalProd, MathContext.DECIMAL64).multiply(new BigDecimal("100.00"));

                        percCalc = percCalc.setScale(2,RoundingMode.UP);

                        if(percCalc.doubleValue() < percDesc ){
                            totalDesc = valordodesconto.multiply(BigDecimal.valueOf(novoItem.getQtcomp()));
                            totalDesc = totalDesc.add(new BigDecimal("0.01"));
                        }else{
                            totalDesc = valordodesconto.multiply(BigDecimal.valueOf(novoItem.getQtcomp()));
                        }



                        vlProduto = totalProd.subtract(totalDesc).divide(BigDecimal.valueOf(novoItem.getQtcomp()), MathContext.DECIMAL64);

                        totalDesc = totalDesc.setScale(2,RoundingMode.FLOOR);
                        totalProd = totalProd.setScale(2,RoundingMode.FLOOR);
                        vlProduto = vlProduto.setScale(2,RoundingMode.FLOOR);

                        valor = valor.add(BigDecimal.valueOf(totalDesc.doubleValue()));
                        qtd = qtd.add(BigDecimal.valueOf(novoItem.getQtcomp()));
                        vldesc = valor.divide(qtd, MathContext.DECIMAL64);


                        if(descontoSwitch.isChecked() && validarDesconto() && validarMotivo()){
                            aplicarDescontoItem = !isItemComDescontoPromocionalBloqueado(novoItem);
                            if(aplicarDescontoItem && novoItem.getBloqueioDesc() != null && novoItem.getBloqueioDesc().equals("S")){
                                novoItem.setDpi(0);
                                novoItem.setVlDescItem(0);
                                novoItem.setCodMotivo(null);
                                novoItem.setMotivoDesc(null);
                            }else if(aplicarDescontoItem){
                                novoItem.setDpi(percDesc);
                                novoItem.setVlDescItem(valordodesconto.doubleValue());
                                novoItem.setCodMotivo(itemPedido.getCodMotivo());
                                novoItem.setMotivoDesc(itemPedido.getMotivoDesc());
                            }

                        }
                        if(tipopedSwitch.isChecked()) {
                            String tipoTxt = edtTipoPed.getText() != null ? edtTipoPed.getText().toString() : "";
                            String tppedCode = itemPedido.getTpped();
                            if (tipoTxt != null && !tipoTxt.equals("")) {
                                if (tipoTxt.equals("Entrega")) tppedCode = "E";
                                else if (tipoTxt.equals("Retira Posterior")) tppedCode = "P";
                                else if (tipoTxt.equals("Retira")) tppedCode = "R";
                            }
                            novoItem.setTpped(tppedCode);
                            novoItem.setTppedDesc(tipoTxt);
                            String dataTxt = edtDataEntrega.getText() != null ? edtDataEntrega.getText().toString() : "";
                            if (dataTxt.length() == 10) {
                                try {
                                    DateFormat entradaFmt = new SimpleDateFormat("dd/MM/yyyy");
                                    DateFormat saidaFmt = new SimpleDateFormat("yyyy-MM-dd");
                                    Date d = entradaFmt.parse(dataTxt);
                                    DataEntrega = saidaFmt.format(d);
                                } catch (ParseException ignore) {
                                }
                            }
                            novoItem.setDtentrega(DataEntrega);
                        }

                        if(saidaSwitch.isChecked()){

                            novoItem.setCodFilOrig(itemPedido.getCodFilOrig());
                            novoItem.setFilOrig(itemPedido.getFilOrig());
                        }

                        if(transfeSwitch.isChecked()){
                            novoItem.setCodFilTransf(itemPedido.getCodFilTransf());
                            novoItem.setFilTransf(itemPedido.getFilTransf());
                        }



                        Pedidos_LancamentoActivity.atualizaItem(novoItem,p);
                        //itemPedido.setDtentrega(DataEntrega);
                    }

                    if (pedido == null) {
                        pedido = Pedidos_LancamentoActivity.pedido;
                    }
                    if (pedido != null) {
                        pedido.setItens(ItemPedidoAdapter.mList);
                    }

                    if (descontoSwitch.isChecked() && temItemPromocaoComDescontoIgnorado()) {
                        Toast.makeText(PopupItemPedTodos.this, "Itens promocionais vindos do motor não receberam desconto.", Toast.LENGTH_SHORT).show();
                    }

                    finish();
                }
        });



        ConsultarCombobox();



        spinnerDialogTipo = new SpinnerDialog(PopupItemPedTodos.this, listaTipo, "Tipo de Pedido");
        spinnerDialogTipo.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtTipoPed.setText(item);
                itemPedido.setTppedDesc(item);
                validaCombobox(item);


            }
        });

        spinnerDialog = new SpinnerDialog(PopupItemPedTodos.this, listaFilial, "selecione a filial de saída");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                if (validarFilial(item)) {
                    edtFilSaida.setText(item);
                    itemPedido.setFilOrig(mListSaida.get(position).getFantasia());
                    itemPedido.setCodFilOrig(mListSaida.get(position).getCodfil());
                    itemPedido.setIsalteracao("N");
                    filialsaida_selecionada = "S";
                }

            }
        });

        spinnerDialogMotivo = new SpinnerDialog(PopupItemPedTodos.this, listaMotivo, "selecione o motivo do desconto");
        spinnerDialogMotivo.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtMotivo.setText(item);
                itemPedido.setCodMotivo(mListMotivo.get(position).getCodMotivo());
                itemPedido.setMotivoDesc(mListMotivo.get(position).getDescricao());
                motivo_selecionado = "S";

            }
        });


        spinnerDialogTransf = new SpinnerDialog(PopupItemPedTodos.this, listaFilial, "selecione a filial de transferência");
        spinnerDialogTransf.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                if (validarTransferencia(item)) {
                    edtFilTransf.setText(item);
                    itemPedido.setFilTransf(mListTransf.get(position).getFantasia());
                    itemPedido.setCodFilTransf(mListTransf.get(position).getCodfil());
                    itemPedido.setIsalteracao("N");
                }


            }
        });




        if (edtFilSaida.isEnabled()) {
            edtFilSaida.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (edtFilSaida.getRight() - edtFilSaida.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                            spinnerDialog.showSpinerDialog();

                        }
                    }
                    return false;
                }
            });
        }


        edtTipoPed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtTipoPed.getRight() - edtTipoPed.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialogTipo.showSpinerDialog();

                    }
                }
                return false;
            }
        });

        edtFilTransf.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtFilTransf.getRight() - edtFilTransf.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if(edtFilTransf.isEnabled()){
                            spinnerDialogTransf.showSpinerDialog();
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


        if (edtDataEntrega.isEnabled()) {

            edtDataEntrega.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (edtDataEntrega.getRight() - edtDataEntrega.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                            if(edtDataEntrega.isEnabled()) {
                                DatePickerDialog datePickerDialog =  new DatePickerDialog(PopupItemPedTodos.this, dateinicial, myCalendar
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

        }





    }

    public void validaCombobox(String tpped){

        switch (tpped) {
            case "Retira":
                itemPedido.setTpped("R");
                edtFilSaida.setText(usuarioLogado.get("filialorig"));
                itemPedido.setCodFilOrig(Integer.parseInt(usuarioLogado.get("codfil")));
                edtDataEntrega.setEnabled(false);
                edtFilSaida.setEnabled(false);
                edtFilTransf.setEnabled(false);
                edtFilTransf.setText("");
                itemPedido.setCodFilTransf(0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    edtDataEntrega.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
                    edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
                    edtFilSaida.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
                }


                String dayr = "";
                String monthr = "";

                Date dater = new Date();
                Calendar cr = Calendar.getInstance();
                cr.setTime(dater);
                dater = cr.getTime();

                if ((cr.get(cr.DAY_OF_MONTH)) < 10) {
                    dayr = "0" + (cr.get(cr.DAY_OF_MONTH));
                } else {
                    dayr = String.valueOf((cr.get(cr.DAY_OF_MONTH)));
                }

                if ((cr.get(cr.MONTH) + 1) < 10) {
                    monthr = "0" + (cr.get(cr.MONTH) + 1);
                } else {
                    monthr = String.valueOf((cr.get(cr.MONTH) + 1));
                }

                String dataFormatadaRetira = dayr + "/" + monthr + "/" + cr.get(cr.YEAR);
                validarDataEntregaComApi(dataFormatadaRetira);


                break;
            case "Retira Posterior":
                itemPedido.setTpped("P");
                edtDataEntrega.setEnabled(true);
                edtFilSaida.setEnabled(true);
                edtFilTransf.setEnabled(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    edtDataEntrega.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                    edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                    edtFilSaida.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                }


                if(encomendaSwitch.isChecked()){

                    edtFilTransf.setEnabled(false);
                    edtFilTransf.setText("");
                    //itemPedido.setCodFilTransf(0);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
                    }

                }


                String dayp = "";
                String monthp = "";

                Date datep = new Date();
                Calendar cp = Calendar.getInstance();
                cp.setTime(datep);
                datep = cp.getTime();

                if ((cp.get(cp.DAY_OF_MONTH)) < 10) {
                    dayp = "0" + (cp.get(cp.DAY_OF_MONTH));
                } else {
                    dayp = String.valueOf((cp.get(cp.DAY_OF_MONTH)));
                }

                if ((cp.get(cp.MONTH) + 1) < 10) {
                    monthp = "0" + (cp.get(cp.MONTH) + 1);
                } else {
                    monthp = String.valueOf((cp.get(cp.MONTH) + 1));
                }

                String dataFormatadaPosterior = dayp + "/" + monthp + "/" + cp.get(cp.YEAR);
                validarDataEntregaComApi(dataFormatadaPosterior);


                break;


            case "Entrega":
                itemPedido.setTpped("E");
                edtDataEntrega.setEnabled(true);
                edtFilTransf.setEnabled(true);
                edtFilSaida.setEnabled(true);
                edtFilSaida.setText("Pe.Cicero");
                itemPedido.setFilOrig("Pe.Cicero");
                itemPedido.setCodFilOrig(5);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    edtDataEntrega.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                    edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                    edtFilSaida.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                }

                if(encomendaSwitch.isChecked()) {

                    edtFilTransf.setEnabled(false);
                    edtFilTransf.setText("");
                    itemPedido.setCodFilTransf(0);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));

                    }

                }
                if (Pedidos_LancamentoActivity.vlfrete == 0 && Pedidos_LancamentoActivity.pedido.getVlFrete() == 0) {

                    Intent intent = new Intent(PopupItemPedTodos.this, PopupEnderecoEntrega.class);
                    intent.putExtra("endereco", (Serializable) Pedidos_LancamentoActivity.pedido.getCliente().getEnderecos());
                    startActivity(intent);

                    String day = "";
                    String month = "";

                    Date date = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    c.add(Calendar.DATE, 2);
                    date = c.getTime();

                    if ((c.get(c.DAY_OF_MONTH)) < 10) {
                        day = "0" + (c.get(c.DAY_OF_MONTH));
                    } else {
                        day = String.valueOf((c.get(c.DAY_OF_MONTH)));
                    }

                    if ((c.get(c.MONTH) + 1) < 10) {
                        month = "0" + (c.get(c.MONTH) + 1);
                    } else {
                        month = String.valueOf((c.get(c.MONTH) + 1));
                    }

                    String dataFormatadaEntrega = day + "/" + month + "/" + c.get(c.YEAR);
                    validarDataEntregaComApi(dataFormatadaEntrega);

                }










                break;
        }




    }

    private boolean isItemPromocao(ItemPedidos item) {
        return item != null && "PR".equals(item.getSituacao());
    }

    private boolean isItemComDescontoPromocionalBloqueado(ItemPedidos item) {
        return temDescontoMotorPromocional(item);
    }

    private boolean temDescontoMotorPromocional(ItemPedidos item) {
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

    private boolean temItemPromocaoComDescontoIgnorado() {
        if (pedido == null || pedido.getItens() == null) {
            return false;
        }

        for (ItemPedidos item : pedido.getItens()) {
            if (isItemComDescontoPromocionalBloqueado(item)) {
                return true;
            }
        }

        return false;
    }


    public void ConsultarCombobox() {

        dialog = new ProgressDialog(PopupItemPedTodos.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);

        new Thread() {


            public void run() {

                try {


                    mListSaida = helper.GetFiliais();
                    mListMotivo = helper.GetMotivoDesc();




                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            //Descomentar apos COVID-19

                            listaTipo.add("Retira");
                            listaTipo.add("Retira Posterior");
                            listaTipo.add("Entrega");


                            if (!itemPedido.getTppedDesc().equals("")) {
                                edtTipoPed.setText(itemPedido.getTppedDesc());
                                validaCombobox(itemPedido.getTppedDesc());
                            }

                            if(itemPedido.getTpped().equals("R")){
                                edtFilSaida.setEnabled(false);
                                edtFilTransf.setEnabled(false);
                                edtDataEntrega.setEnabled(false);
                            }

                            if (mListMotivo != null && mListMotivo.size() > 0) {
                                for (MotivoDesconto m : mListMotivo) {
                                    listaMotivo.add(m.getDescricao());
                                }
                            }




                            if (mListSaida != null && mListSaida.size() > 0) {
                                for (Filial f : mListSaida) {
                                    listaFilial.add(f.getFantasia());
                                }


                                if (!itemPedido.getDtentrega().equals("")) {
                                    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                                    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        Date date = entrada.parse(itemPedido.getDtentrega());
                                        String dataFormatadaExistente = formato.format(date);
                                        validarDataEntregaComApi(dataFormatadaExistente);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }


                                edtFilSaida.setText(itemPedido.getFilOrig());
                                mListTransf = mListSaida;
                                filialsaida_selecionada = "S";

                                if (itemPedido.getCodFilTransf() > 0) {
                                    edtFilTransf.setText(itemPedido.getFilTransf());
                                    filialtransf_selecionada = "S";
                                }

                                if (itemPedido.getCodMotivo() != null && !itemPedido.getCodMotivo().equals("")) {
                                    layout_Motivo.setEnabled(true);
                                    edtMotivo.setText(itemPedido.getMotivoDesc());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                                    }
                                    motivo_selecionado = "S";
                                }

                                if(itemPedido.getMotivoDesc() != null && !itemPedido.getMotivoDesc().equals("")){
                                    edtMotivo.setText(itemPedido.getMotivoDesc());
                                }

                            }


                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupItemPedTodos.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupItemPedTodos.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
        if (desconto.isEmpty()) {
            if (percDescAlt > 0) {
                edtDesconto.setText(dfdesconto.format(percDescAlt));
            } else {
                layout_desconto.setErrorEnabled(true);
                layout_desconto.setError("Informe o % de desconto");
            }

            return false;

        } else {
            layout_desconto.setErrorEnabled(false);
            return true;
        }


    }


    public boolean validarTransferencia(String filial) {
        String filialdesaida = edtFilSaida.getText().toString().trim();
        if (filial.trim().equals(filialdesaida)) {

            layout_FilTransf.setErrorEnabled(true);
            layout_FilTransf.setError("Filial de transferência não pode ser igual a de saída");

            return false;

        } else {
            layout_FilTransf.setErrorEnabled(false);
            return true;
        }


    }

    public boolean validarFilial(String filial) {
        String filialtransf= edtFilTransf.getText().toString().trim();
        if (filial.trim().equals(filialtransf)) {

            layout_FilSaida.setErrorEnabled(true);
            layout_FilSaida.setError("Filial de saída não pode ser igual a de transferência");

            return false;

        } else {
            layout_FilSaida.setErrorEnabled(false);
            return true;
        }


    }

 

    public boolean validarMotivo() {
        if (edtDesconto.getText().toString().equals("")) {
            layout_Motivo.setErrorEnabled(false);
            return true;
        }

        if (Float.parseFloat(edtDesconto.getText().toString()) > 0) {
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


    public boolean validarValorEntrega(){
        if(Pedidos_LancamentoActivity.pedido.getVlFreteSelecionado()==0 && itemPedido.getTpped().equals("E")){
            layout_TipoPed.setErrorEnabled(true);
            layout_TipoPed.setError("Informe o endereço de entrega!");
            itemPedido.setTpped("P");
            itemPedido.setTppedDesc("Retira Posterior");
            edtTipoPed.setText("Retira Posterior");
            edtDataEntrega.setEnabled(true);
            edtFilSaida.setEnabled(true);
            edtFilTransf.setEnabled(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                edtDataEntrega.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                edtFilSaida.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
            }

            String dayp ="";
            String monthp ="";

            Date datep = new Date();
            Calendar cp = Calendar.getInstance();
            cp.setTime(datep);
            datep = cp.getTime();

            if((cp.get(cp.DAY_OF_MONTH))<10){
                dayp = "0"+(cp.get(cp.DAY_OF_MONTH));
            }else{
                dayp = String.valueOf((cp.get(cp.DAY_OF_MONTH)));
            }

            if((cp.get(cp.MONTH)+1)<10){
                monthp = "0"+(cp.get(cp.MONTH)+1);
            }else{
                monthp = String.valueOf((cp.get(cp.MONTH)+1));
            }

            String dataFormatadaReclass = dayp + "/" + monthp + "/" + cp.get(cp.YEAR);
            validarDataEntregaComApi(dataFormatadaReclass);

            return  false;
        }else{
            layout_TipoPed.setErrorEnabled(false);
            layout_TipoPed.setError("");
            return true;
        }

    }

    public boolean validarDataentrega() {


        if(edtTipoPed.getText().toString().equals("Entrega")){


            Enderecos endereco = new Enderecos();
            endereco = Pedidos_LancamentoActivity.pedido.getCliente().getEnderecos().get(Pedidos_LancamentoActivity.pedido.getEndent());


            GregorianCalendar gc = new GregorianCalendar();
            try {
                gc.setTime(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(edtDataEntrega.getText().toString()));
                switch (gc.get(Calendar.DAY_OF_WEEK)) {
                    case Calendar.SUNDAY:
                        if(endereco.getFldomingo().equals("N")){
                            edtDataEntrega.setText("");
                            itemPedido.setDtentrega("");
                            layout_dataentrega.setErrorEnabled(true);
                            layout_dataentrega.setError("Data inválida");
                            Toast.makeText(PopupItemPedTodos.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
                            return false;

                        }else{
                            layout_dataentrega.setErrorEnabled(false);
                            layout_dataentrega.setError("");
                            return true;

                        }
                    case Calendar.MONDAY:
                        if(endereco.getFlsegunda().equals("N")){
                            edtDataEntrega.setText("");
                            itemPedido.setDtentrega("");
                            layout_dataentrega.setErrorEnabled(true);
                            layout_dataentrega.setError("Data inválida");
                            Toast.makeText(PopupItemPedTodos.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
                            return false;

                        }else{
                            layout_dataentrega.setErrorEnabled(false);
                            layout_dataentrega.setError("");
                            return true;

                        }
                    case Calendar.TUESDAY:
                        if(endereco.getFlterca().equals("N")){
                            edtDataEntrega.setText("");
                            itemPedido.setDtentrega("");
                            layout_dataentrega.setErrorEnabled(true);
                            layout_dataentrega.setError("Data inválida");
                            Toast.makeText(PopupItemPedTodos.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
                            return false;

                        }else{
                            layout_dataentrega.setErrorEnabled(false);
                            layout_dataentrega.setError("");
                            return true;

                        }
                    case Calendar.WEDNESDAY:
                        if(endereco.getFlquarta().equals("N")){
                            edtDataEntrega.setText("");
                            itemPedido.setDtentrega("");
                            layout_dataentrega.setErrorEnabled(true);
                            layout_dataentrega.setError("Data inválida");
                            Toast.makeText(PopupItemPedTodos.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
                            return false;

                        }else{
                            layout_dataentrega.setErrorEnabled(false);
                            layout_dataentrega.setError("");
                            return true;

                        }
                    case Calendar.THURSDAY:
                        if(endereco.getFlquinta().equals("N")){
                            edtDataEntrega.setText("");
                            itemPedido.setDtentrega("");
                            layout_dataentrega.setErrorEnabled(true);
                            layout_dataentrega.setError("Data inválida");
                            Toast.makeText(PopupItemPedTodos.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
                            return false;

                        }else{
                            layout_dataentrega.setErrorEnabled(false);
                            layout_dataentrega.setError("");
                            return true;

                        }


                    case Calendar.FRIDAY:
                        if(endereco.getFlsexta().equals("N")){
                            edtDataEntrega.setText("");
                            itemPedido.setDtentrega("");
                            layout_dataentrega.setErrorEnabled(true);
                            layout_dataentrega.setError("Data inválida");
                            Toast.makeText(PopupItemPedTodos.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
                            return false;

                        }else{
                            layout_dataentrega.setErrorEnabled(false);
                            layout_dataentrega.setError("");
                            return true;

                        }


                    case Calendar.SATURDAY:
                        if(endereco.getFlsabado().equals("N")){
                            edtDataEntrega.setText("");
                            itemPedido.setDtentrega("");
                            layout_dataentrega.setErrorEnabled(true);
                            layout_dataentrega.setError("Data inválida");
                            Toast.makeText(PopupItemPedTodos.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
                            return false;
                        }else{
                            layout_dataentrega.setErrorEnabled(false);
                            layout_dataentrega.setError("");
                            return true;
                        }


                }
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }


        }
        return true;
    }

}
