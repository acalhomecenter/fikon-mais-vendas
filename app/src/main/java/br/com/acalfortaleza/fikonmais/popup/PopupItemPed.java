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
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.Filial;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Lotevencto;
import br.com.acalfortaleza.fikonmais.models.MotivoDesconto;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupItemPed extends Activity {
    private static final String COD_MOTIVO_CUPOM = "CD";

    private ItemPedidos itemPedido;
    private Pedidos pedido;
    private TextView txtProduto;
    private TextInputEditText edtDesconto;
    private TextInputEditText edtVlDesconto;
    private TextInputEditText edtValorProduto;
    private TextInputEditText edtQuantidade;
    private TextInputEditText edtTipoPed;
    private TextInputEditText edtFilSaida;
    private TextInputEditText edtFilTransf;
    private TextInputEditText edtMotivo;
    private TextInputEditText edtLote;
    private TextInputLayout layout_lote;
    private TextInputLayout layout_desconto;
    private TextInputLayout layout_vldesconto;
    private TextInputLayout layout_vlproduto;
    private TextInputLayout layout_quantidade;
    private TextInputLayout layout_FilSaida;
    private TextInputLayout layout_FilTransf;
    private TextInputLayout layout_Motivo;
    private TextInputLayout layout_TipoPed;
    private Button btn_fechar;
    private TextView txtTotal;
    private ImageView indicador;
    private Button btn_alterar;
    private double valorOriginal;
    private double descontoOriginalItem;
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
    private final ArrayList<MotivoDesconto> motivosDescontoSelecionaveis = new ArrayList<>();
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
    private SpinnerDialog spinnerDialogLote;
    private Calendar myCalendar = Calendar.getInstance();
    private TextInputEditText edtDataEntrega;
    private TextInputLayout layout_dataentrega;
    private Switch encomendaSwitch;
    private String DataEntrega;
    private boolean enderecoPopupAberto = false;
    private boolean fromFinalize = false;
    private boolean skipFreteOnInitialOpen = false;
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
        final ProgressDialog pd = new ProgressDialog(PopupItemPed.this);
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
                                    
                                    android.util.Log.d("PopupItemPed", "Flags de entrega atualizadas via API para validação.");
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
                            Toast.makeText(PopupItemPed.this, "Não foi possível verificar a disponibilidade online. Usando dados locais.", Toast.LENGTH_SHORT).show();
                            
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
        setContentView(R.layout.popup_itemped);

        itemPedido = (ItemPedidos) getIntent().getSerializableExtra("item");
        if (getIntent().hasExtra("pedido")) {
            pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
        } else {
            pedido = Pedidos_LancamentoActivity.pedido;
        }
        fromFinalize = getIntent().getBooleanExtra("fromFinalize", false);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .9));

        helper = new PedidosHelper();
        handler = new Handler();
        helperLogin = new LoginHelper(this);
        usuarioLogado = helperLogin.GetUsuarioSalvo();

        txtProduto = findViewById(R.id.pop_itped_produto);
        edtDesconto = findViewById(R.id.pop_itped_edtDesc);
        edtVlDesconto = findViewById(R.id.pop_itped_edtvlDesc);
        edtValorProduto = findViewById(R.id.pop_itped_edtPreco);
        edtQuantidade = findViewById(R.id.pop_itped_edtQuantidade);
        edtFilSaida = findViewById(R.id.pop_itped_edtFilSaida);
        edtFilTransf = findViewById(R.id.pop_itped_edtFilTransf);
        edtDataEntrega = findViewById(R.id.pop_itped_edtDataEntrega);
        edtMotivo = findViewById(R.id.pop_itped_edtMotivo);
        edtTipoPed = findViewById(R.id.pop_itped_edtTipoPed);
        txtTotal = findViewById(R.id.pop_itped_txtTotal);
        indicador = findViewById(R.id.pop_itped_indicador);
        btn_alterar = findViewById(R.id.pop_itped_btn_Alterar);
        layout_desconto = findViewById(R.id.pop_itped_layoutDesc);
        layout_vldesconto = findViewById(R.id.pop_itped_layoutValorDesc);
        layout_vlproduto = findViewById(R.id.pop_itped_layoutPreco);
        layout_quantidade = findViewById(R.id.pop_itped_layoutQuantidade);
        layout_FilSaida = findViewById(R.id.pop_itped_layoutFilSaida);
        layout_FilTransf = findViewById(R.id.pop_itped_layoutFilTrans);
        layout_Motivo = findViewById(R.id.pop_itped_layoutMotivo);
        layout_dataentrega = findViewById(R.id.pop_itped_layoutedtDataEntrega);
        layout_lote = findViewById(R.id.pop_itped_layoutlote);
        layout_TipoPed = findViewById(R.id.pop_itped_layoutTipoPed);
        edtLote = findViewById(R.id.pop_itped_edtLote);
        encomendaSwitch = findViewById(R.id.pop_itped_encomenda);
        btn_fechar = findViewById(R.id.pop_itped_btnFechar);

        //Retirar esse parte apos o COVID-19
        edtFilTransf.setEnabled(false);


        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        if(itemPedido.getFllotevencto().equals("S")){
            edtLote.setEnabled(true);
            if(itemPedido.getLoteSelecionado() != null && !itemPedido.getLoteSelecionado().equals("")){
                edtLote.setText(itemPedido.getLoteSelecionado());
            }

            if(itemPedido.getLotevencto() != null && itemPedido.getLotevencto().size()>0 && !itemPedido.getLotevencto().get(0).getDescricao().equals("")){
                edtLote.setText(itemPedido.getLotevencto().get(0).getDescricao());
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                edtLote.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
            }
        }else{
            edtLote.setEnabled(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                edtLote.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
            }
        }

        real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        df = new DecimalFormat("0.00", symbols);
        dfdesconto = new DecimalFormat("0.0000", symbols);

        txtProduto.setText(itemPedido.getCoditprod() + " - " + itemPedido.getDescricao());
        edtDesconto.setText(dfdesconto.format(itemPedido.getDpi()));

        edtValorProduto.setText(real.format(getValorLiquidoExibicao(itemPedido.getPrecoUnit() - itemPedido.getVlDescItem())));
        edtQuantidade.setText(df.format(itemPedido.getQtcomp()));

        BigDecimal vlproduto = new BigDecimal("0.00");
        BigDecimal vldesconto = new BigDecimal("0.00");

        vlproduto = (BigDecimal.valueOf(itemPedido.getPrecoUnit()).multiply(BigDecimal.valueOf(itemPedido.getQtcomp())));

        vldesconto = (BigDecimal.valueOf(itemPedido.getVlDescItem()).multiply(BigDecimal.valueOf(itemPedido.getQtcomp())));


        vlproduto = vlproduto.setScale(2,RoundingMode.FLOOR);

        vldesconto = vldesconto.setScale(3,RoundingMode.UP);

        edtVlDesconto.setText(df.format(vldesconto));

        txtTotal.setText(real.format(getValorTotalExibicao(vlproduto.subtract(vldesconto).doubleValue())));
        valorOriginal = itemPedido.getPrecoUnit();
        quantidade = itemPedido.getQtcomp();
        percDesc = itemPedido.getDpi();
        valordodesconto = BigDecimal.valueOf(itemPedido.getVlDescItem());
        descontoOriginalItem = itemPedido.getVlDescItem();

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

        /*edtDesconto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean f) {

                if(!f){
                    if(edtDesconto.getText().toString().isEmpty()) {
                        if (!validarQtd() || !validarDesconto() || !validarDescontoValor()) {

                        } else {

                        }
                    }
                }
            }
        });*/


        if (itemPedido.getPmargRent() >= 36) {

            indicador.setImageResource(R.drawable.fundo_bt_flutuante);

        }
        if (itemPedido.getPmargRent() >= 34 && itemPedido.getPmargRent() < 36) {

            indicador.setImageResource(R.drawable.fundo_bt_flutuante_orange);
        }


        if (itemPedido.getPmargRent() < 34) {

            indicador.setImageResource(R.drawable.fundo_bt_flutuante_red);
        }

        edtDesconto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent event) {
                if(event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || i == EditorInfo.IME_ACTION_DONE) {
                    if (!validarQtd() || !validarDesconto() || !validarDescontoValor() || !validarDesconto() || !validarBloqueioDesconto() ) {

                    } else {
                        if (Float.parseFloat(edtDesconto.getText().toString()) >0) {
                            calculaPercentual();
                            edtMotivo.setEnabled(true);
                            layout_Motivo.setEnabled(true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                            }
                        } else {
                            calculaPercentual();
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

        edtVlDesconto.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int i, KeyEvent event) {


                if(event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || i == EditorInfo.IME_ACTION_DONE) {




                    if (!validarQtd() || !validarVlDesconto() || !validarDescontoValor() || descPerc ) {

                    } else {

                        BigDecimal vlParaCalc = new BigDecimal("0.00");
                        BigDecimal vldescontoCampo = new BigDecimal("0.00");

                        vldescontoCampo = BigDecimal.valueOf(Double.parseDouble(edtVlDesconto.getText().toString()));
                        vldescontoCampo = vldescontoCampo.setScale(2,RoundingMode.FLOOR);
                        vlParaCalc = valordodesconto.multiply(BigDecimal.valueOf(Double.parseDouble(edtQuantidade.getText().toString())));
                        vlParaCalc = vlParaCalc.setScale(2, RoundingMode.FLOOR);

                        if(vlParaCalc.doubleValue() != vldescontoCampo.doubleValue()){
                            descPerc=false;
                            descValor=true;
                        }


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
                            itemPedido.setCodMotivo("");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
                            }
                        }

                    }
                    descPerc=false;
                    descValor=true;
                    return false;

                }



                return false;
            }
        });




        edtQuantidade.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!validarQtd() || !validarConversao()) {


                    } else {
                        calculaQuantidade();


                    }

                }

            }
        });


        if(!itemPedido.getSituacao().equals("EN") )
        {
            encomendaSwitch.setEnabled(false);


        }else{
            encomendaSwitch.setEnabled(true);
        }


        if(itemPedido.getEncomenda().equals("E")){
            encomendaSwitch.setChecked(true);
        }else{
            encomendaSwitch.setChecked(false);
        }

        encomendaSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(encomendaSwitch.isChecked()){
                    edtFilTransf.setEnabled(false);
                    edtFilTransf.setText("");
                    itemPedido.setCodFilTransf(0);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));

                    }


                }else{


                        edtFilTransf.setText("");
                        itemPedido.setCodFilTransf(0);
                        if(edtTipoPed.getText().toString().equals("Retira")){
                            edtFilTransf.setEnabled(false);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
                            }
                        }

                        if(edtTipoPed.getText().toString().equals("Retira Posterior")){
                            edtFilTransf.setEnabled(true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                            }
                        }

                        if(edtTipoPed.getText().toString().equals("Entrega")){
                            edtFilTransf.setEnabled(true);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                            }
                        }




                }
            }
        });

        btn_alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean itemPromocao = isItemComDescontoPromocionalBloqueado();
                if (!validarQtd() || !validarConversao() || !validarDataentrega() || !validarValorEntrega()
                        || (!itemPromocao && (!validarVlDesconto() || !validarDescontoValor() || !validarMotivo() || !validarBloqueioDesconto()))) {


                } else {

                    if (encomendaSwitch.isChecked() ) {
                        itemPedido.setEncomenda("E");
                    } else {
                        itemPedido.setEncomenda("N");
                    }


                    itemPedido.setDtentrega(DataEntrega);
                    if (!itemPromocao) {
                        if(itemPedido.getBloqueioDesc().equals("S")){
                            itemPedido.setVlDescItem(0.00d);
                            itemPedido.setDpi(0.00d);
                            itemPedido.setMotivoDesc("");
                            edtMotivo.setText("");
                            itemPedido.setCodMotivo("");
                            itemPedido.setMotivoDesc("");
                        }else{
                            itemPedido.setVlDescItem(valordodesconto.doubleValue());
                            itemPedido.setDpi(percDesc);
                            if (COD_MOTIVO_CUPOM.equalsIgnoreCase(itemPedido.getCodMotivo())
                                    && BigDecimal.valueOf(itemPedido.getVlDescItem()).setScale(6, RoundingMode.HALF_UP)
                                    .compareTo(BigDecimal.valueOf(descontoOriginalItem).setScale(6, RoundingMode.HALF_UP)) != 0) {
                                itemPedido.setCodMotivo("");
                                itemPedido.setMotivoDesc("");
                                edtMotivo.setText("");
                            }
                        }
                    }

                    itemPedido.setQtcomp(Double.parseDouble(edtQuantidade.getText().toString()));
                    if ("Entrega".equals(edtTipoPed.getText().toString())) {
                        boolean temFreteSelecionado = br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.getVlFreteSelecionado() > 0.0d;
                        boolean temServicosAdicionais = (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.getVlServicosAdicionais() > 0.0d)
                                || (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.getServicosAdicionaisIds() != null
                                    && !br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.getServicosAdicionaisIds().isEmpty());
                        if (temFreteSelecionado || temServicosAdicionais) {
                            itemPedido.setTpped("E");
                            itemPedido.setTppedDesc("Entrega");
                        }
                    }
                    Pedidos_LancamentoActivity.atualizaItem(itemPedido, getIntent().getIntExtra("position", 0));
                    if (pedido == null) {
                        pedido = Pedidos_LancamentoActivity.pedido;
                    }
                    if (pedido != null) {
                        pedido.setItens(ItemPedidoAdapter.mList);
                    }
                    //itemPedido.setDtentrega(DataEntrega);
                    finish();


                }


            }
        });



        ConsultarCombobox();



        spinnerDialogTipo = new SpinnerDialog(PopupItemPed.this, listaTipo, "Tipo de Pedido");
        spinnerDialogTipo.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtTipoPed.setText(item);
                itemPedido.setTppedDesc(item);
                validaCombobox(item);


            }
        });

        spinnerDialog = new SpinnerDialog(PopupItemPed.this, listaFilial, "selecione a filial de saída");
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

        spinnerDialogMotivo = new SpinnerDialog(PopupItemPed.this, listaMotivo, "selecione o motivo do desconto");
        spinnerDialogMotivo.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                MotivoDesconto motivo = motivosDescontoSelecionaveis.get(position);
                if (COD_MOTIVO_CUPOM.equalsIgnoreCase(motivo.getCodMotivo())) {
                    new AlertDialog.Builder(PopupItemPed.this)
                            .setMessage("O motivo CUPOM DESCONTO só pode ser aplicado por meio de um cupom.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                    return;
                }
                edtMotivo.setText(item);
                itemPedido.setCodMotivo(motivo.getCodMotivo());
                itemPedido.setMotivoDesc(motivo.getDescricao());
                motivo_selecionado = "S";

            }
        });


        spinnerDialogTransf = new SpinnerDialog(PopupItemPed.this, listaFilial, "selecione a filial de transferência");
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



        spinnerDialogLote= new SpinnerDialog(PopupItemPed.this, listaLote, "selecione o lote do Produto");
        spinnerDialogLote.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                if(mListLote.get(position).getQtde() >= Double.parseDouble(edtQuantidade.getText().toString()) ){
                    edtLote.setText(mListLote.get(position).getDescricao());
                    itemPedido.setLoteSelecionado(mListLote.get(position).getDescricao());
                    lote_selecionado = "S";
                }else{
                    itemPedido.setLoteSelecionado("");
                    lote_selecionado = "N";
                    edtLote.setText("");
                    Toast.makeText(PopupItemPed.this,"Quantidade do lote selecionado inferior ao solicitado!",Toast.LENGTH_SHORT).show();
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
                             DatePickerDialog datePickerDialog =  new DatePickerDialog(PopupItemPed.this, dateinicial, myCalendar
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



        edtLote.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtLote.getRight() - edtLote.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (edtLote.isEnabled()) {
                            spinnerDialogLote.showSpinerDialog();

                        }


                    }
                }
                return false;
            }
        });


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

                br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setVlServicosAdicionais(0.0d);
                br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed = 0.0d;
                br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setVlFrete(0.0d);
                android.content.SharedPreferences spR = getSharedPreferences("frete_pedido", android.content.Context.MODE_PRIVATE);
                android.content.SharedPreferences.Editor edR = spR.edit();
                edR.remove("servicos_total_sem_pedido");
                edR.remove("servicos_total");
                edR.apply();
                br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.marcarTotalComoRecalculavel();
                br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.atualizaTotais(br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.getItens());

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

                br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setVlServicosAdicionais(0.0d);
                br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed = 0.0d;
                br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setVlFrete(0.0d);
                android.content.SharedPreferences spRP = getSharedPreferences("frete_pedido", android.content.Context.MODE_PRIVATE);
                android.content.SharedPreferences.Editor edRP = spRP.edit();
                edRP.remove("servicos_total_sem_pedido");
                edRP.remove("servicos_total");
                edRP.apply();
                br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.marcarTotalComoRecalculavel();
                br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.atualizaTotais(br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.getItens());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    edtDataEntrega.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                    edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                    edtFilSaida.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                }


                if(encomendaSwitch.isChecked()){

                    edtFilTransf.setEnabled(false);
                    edtFilTransf.setText("");
                    itemPedido.setCodFilTransf(0);

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
                edtDataEntrega.setEnabled(true);
                edtFilTransf.setEnabled(true);
                edtFilSaida.setEnabled(true);
                edtFilSaida.setText("Pe.Cicero");
                itemPedido.setFilOrig("Pe.Cicero");
                itemPedido.setCodFilOrig(5);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    edtDataEntrega.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                    edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                    edtFilSaida.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                }

                if(encomendaSwitch.isChecked()) {

                    edtFilTransf.setEnabled(false);
                    edtFilTransf.setText("");
                    itemPedido.setCodFilTransf(0);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));

                    }

                }
                if (pedido != null && !enderecoPopupAberto && !skipFreteOnInitialOpen) {
                    Intent intent = new Intent(PopupItemPed.this, PopupEnderecoEntrega.class);
                    // Passa a lista de endereços do cliente contido no objeto 'pedido'
                    intent.putExtra("endereco", (Serializable) pedido.getCliente().getEnderecos());
                    // Adiciona a linha que faltava: passa o objeto 'pedido' completo.
                    intent.putExtra("pedido", pedido);
                    intent.putExtra("isOrcamento", false); // Garante que o fluxo seja de Pedido
                    enderecoPopupAberto = true;
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


    public void ConsultarCombobox() {
        dialog = new ProgressDialog(PopupItemPed.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {
            public void run() {
                try {
                    mListSaida = helper.GetFiliais();
                    mListMotivo = helper.GetMotivoDesc();

                    if (itemPedido.getFllotevencto().equals("S")) {
                        int filorig;

                        if (itemPedido.getCodFilTransf() > 0) {
                            filorig = itemPedido.getCodFilTransf();
                        } else {
                            filorig = itemPedido.getCodFilOrig();
                        }

                        mListLote = helper.GetLoteventoProduto(String.valueOf(itemPedido.getCoditprod()), String.valueOf(filorig), Double.parseDouble(edtQuantidade.getText().toString()));
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            listaTipo.add("Retira");
                            listaTipo.add("Retira Posterior");
                            listaTipo.add("Entrega");

                            if (itemPedido != null && itemPedido.getTpped() != null && !itemPedido.getTpped().isEmpty()) {
                                String tipoCodigo = itemPedido.getTpped();
                                String tipoDescricao = "";

                                switch (tipoCodigo) {
                                    case "E":
                                        tipoDescricao = "Entrega";
                                        break;
                                    case "P":
                                        tipoDescricao = "Retira Posterior";
                                        break;
                                    case "R":
                                        tipoDescricao = "Retira";
                                        break;
                                    default:
                                        if (itemPedido.getTppedDesc() != null && !itemPedido.getTppedDesc().isEmpty()) {
                                            tipoDescricao = itemPedido.getTppedDesc();
                                        }
                                        break;
                                }

                                if (!tipoDescricao.isEmpty()) {
                                    android.util.Log.d("PopupItemPedDebug", "Item chegou com Tpped='" + tipoCodigo + "'. Configurando popup para: " + tipoDescricao);
                                    if ("Entrega".equals(tipoDescricao)) {
                                        skipFreteOnInitialOpen = true;
                                    }
                                    edtTipoPed.setText(tipoDescricao);
                                    validaCombobox(tipoDescricao);
                                    skipFreteOnInitialOpen = false;
                                }

                            } else if (itemPedido != null && itemPedido.getTppedDesc() != null && !itemPedido.getTppedDesc().equals("")) {
                                android.util.Log.d("PopupItemPedDebug", "Tpped nulo, usando TppedDesc como fallback: " + itemPedido.getTppedDesc());
                                if ("Entrega".equals(itemPedido.getTppedDesc())) {
                                    skipFreteOnInitialOpen = true;
                                }
                                edtTipoPed.setText(itemPedido.getTppedDesc());
                                validaCombobox(itemPedido.getTppedDesc());
                                skipFreteOnInitialOpen = false;
                            }

                            if (mListMotivo != null && mListMotivo.size() > 0) {
                                listaMotivo.clear();
                                motivosDescontoSelecionaveis.clear();
                                for (MotivoDesconto m : mListMotivo) {
                                    if (m != null && !COD_MOTIVO_CUPOM.equalsIgnoreCase(m.getCodMotivo())) {
                                        motivosDescontoSelecionaveis.add(m);
                                        listaMotivo.add(m.getDescricao());
                                    }
                                }
                            }

                            if (mListLote != null && mListLote.size() > 0) {
                                for (Lotevencto l : mListLote) {
                                    listaLote.add("Lote: " + l.getDescricao() + " Qtde: " + l.getQtde());
                                }
                            }

                            if (mListSaida != null && mListSaida.size() > 0) {
                                for (Filial f : mListSaida) {
                                    listaFilial.add(f.getFantasia());
                                }

                                if (itemPedido.getDtentrega() != null && !itemPedido.getDtentrega().equals("")) {
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
                                        edtMotivo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                                    }
                                    motivo_selecionado = "S";
                                }

                                if (itemPedido.getMotivoDesc() != null && !itemPedido.getMotivoDesc().equals("")) {
                                    edtMotivo.setText(itemPedido.getMotivoDesc());
                                }
                            }
                        }
                    });

                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupItemPed.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupItemPed.this).setTitle("Falha de Conexão").setMessage("Ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                        }
                    });
                } finally {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        }.start();
    }




    public boolean validarQtd() {
        String qtd = edtQuantidade.getText().toString().trim();
        if (qtd.isEmpty() || Double.parseDouble(qtd) == 0) {
            if (quantidade > 0) {
                edtQuantidade.setText(df.format(quantidade));
            } else {
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
        if (itemPedido.getBloqueioDesc() != null && itemPedido.getBloqueioDesc().equals("S") && vldesconto>0) {

            layout_desconto.setErrorEnabled(true);
            layout_desconto.setError("O desconto desse produto so pode ser realizado via motor de promoções! ");
            return false;
        }else {
            layout_desconto.setErrorEnabled(false);
            return true;
        }
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

    public boolean validarVlDesconto() {
        String vldesconto = edtVlDesconto.getText().toString().trim();
        if (vldesconto.isEmpty()) {

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

    public boolean validarValorEntrega(){
        // Permitir tipo 'E' quando há serviços adicionais válidos (ids ou valor)
        boolean temFreteSelecionado = Pedidos_LancamentoActivity.pedido.getVlFreteSelecionado() > 0.0d;
        boolean temServicosAdicionais = (Pedidos_LancamentoActivity.pedido.getVlServicosAdicionais() > 0.0d)
                || (Pedidos_LancamentoActivity.pedido.getServicosAdicionaisIds() != null && !Pedidos_LancamentoActivity.pedido.getServicosAdicionaisIds().isEmpty());

        if (!temFreteSelecionado && !temServicosAdicionais && itemPedido.getTpped().equals("E")){
            layout_TipoPed.setErrorEnabled(true);
            layout_TipoPed.setError("Informe o endereço de entrega!");
            itemPedido.setTpped("P");
            itemPedido.setTppedDesc("Retira Posterior");
            edtTipoPed.setText("Retira Posterior");
            edtDataEntrega.setEnabled(true);
            edtFilSaida.setEnabled(true);
            edtFilTransf.setEnabled(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                edtDataEntrega.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                edtFilTransf.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
                edtFilSaida.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#1C8BF1")));
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

            edtDataEntrega.setText(dayp+"/"+monthp+"/"+cp.get(cp.YEAR));
            DataEntrega = (cp.get(cp.YEAR)+"-"+monthp+"-"+dayp);


            if(validarDataentrega()){
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat entrada = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date date = entrada.parse(edtDataEntrega.getText().toString());

                    itemPedido.setDtentrega(s.format(date));
                    DataEntrega = s.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            return  false;
        }else{
            layout_TipoPed.setErrorEnabled(false);
            layout_TipoPed.setError("");
            return true;
        }

    }
    public boolean validarConversao() {
        int qtdArredondado = 0;
        String qtdDigitada = "0";
        String qtdCalculada = "0";
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        final DecimalFormat df = new DecimalFormat("0.00", symbols);


        float divisao = (float) (Double.parseDouble(edtQuantidade.getText().toString()) / itemPedido.getQtemb());
        qtdArredondado = (int) Math.ceil(divisao);
        qtdDigitada = df.format(Double.parseDouble(edtQuantidade.getText().toString()));
        qtdCalculada = df.format(itemPedido.getQtemb() * qtdArredondado);

        if (!itemPedido.getNcm().equals("32041700") && !itemPedido.getNcm().equals("32064990") ) {
            if (!qtdDigitada.equals(qtdCalculada)) {
                layout_quantidade.setErrorEnabled(true);
                if (itemPedido.getQtemb() == 1) {
                    layout_quantidade.setError("Produto vendido em unidade, qtd alterada para: " + (df.format(itemPedido.getQtemb() * qtdArredondado)));
                } else {
                    layout_quantidade.setError("Produto vendido em caixa, qtd alterada para: " + (df.format(itemPedido.getQtemb() * qtdArredondado)));
                }
                edtQuantidade.setText(df.format(itemPedido.getQtemb() * qtdArredondado));
                return false;

            } else {
                layout_quantidade.setErrorEnabled(false);
                return true;
            }
        } else {
            layout_quantidade.setErrorEnabled(false);
            return true;
        }

    }


    public void calculaMargem(BigDecimal total) {

        double vlEncargos;
        double vlIcms;
        double custo;
        double vlTotal;

        //vlTotal = (BigDecimal.valueOf(valorOriginal).subtract(valordodesconto).multiply(BigDecimal.valueOf(quantidade))).doubleValue() ;

        vlEncargos = total.multiply(BigDecimal.valueOf(itemPedido.getEncargos())).divide(new BigDecimal("100")).doubleValue();
        if (itemPedido.getIcmsRed() == 0) {
            vlIcms = total.multiply(BigDecimal.valueOf(itemPedido.getIcms())).divide(new BigDecimal("100")).doubleValue();
        } else {
            vlIcms = total.subtract(total.multiply(BigDecimal.valueOf(itemPedido.getEncargos())).divide(new BigDecimal("100")).
                    multiply(BigDecimal.valueOf(itemPedido.getIcms() / 100))).doubleValue();
        }
        custo = itemPedido.getCusto() * quantidade;

        margem = ((total.doubleValue() - vlEncargos - vlIcms - custo) / (total.doubleValue())) * 100;


        if (margem >= 34.5) {

            indicador.setImageResource(R.drawable.fundo_bt_flutuante);

        }
        if (margem >= 33.5 && margem < 34.5) {

            indicador.setImageResource(R.drawable.fundo_bt_flutuante_orange);
        }


        if (margem < 33.5) {

            indicador.setImageResource(R.drawable.fundo_bt_flutuante_red);
        }
    }

    public void calculaPercentual() {
        final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');

        final DecimalFormat df = new DecimalFormat("0.00", symbols);
        if (edtQuantidade.getText().toString().equals("")) {
            quantidade = itemPedido.getQtcomp();
        } else {
            quantidade = Double.parseDouble(edtQuantidade.getText().toString());
        }
        if (edtDesconto.getText().toString().equals("")) {
            percDesc = 0.0000d;
        } else {
            percDesc = Double.parseDouble(edtDesconto.getText().toString());
        }

        valordodesconto = new BigDecimal("0.00");

        BigDecimal totalProd = new BigDecimal("0.00");
        BigDecimal totalDesc = new BigDecimal("0.00");
        BigDecimal percCalc = new BigDecimal("0.00");
        BigDecimal vldescCalculado =new BigDecimal("0.00");


        totalProd = BigDecimal.valueOf(valorOriginal).multiply(BigDecimal.valueOf(quantidade));

        valordodesconto = BigDecimal.valueOf(valorOriginal).multiply(BigDecimal.valueOf(percDesc).divide(new BigDecimal("100")));

        vldescCalculado = totalProd.multiply(BigDecimal.valueOf(percDesc)).divide(new BigDecimal("100"));

        vldescCalculado = vldescCalculado.setScale(2,RoundingMode.FLOOR);

        percCalc = vldescCalculado.divide(totalProd, MathContext.DECIMAL64).multiply(new BigDecimal("100.00"));

        percCalc = percCalc.setScale(2,RoundingMode.HALF_UP);

        if(percCalc.doubleValue() < percDesc ){
            totalDesc = valordodesconto.multiply(BigDecimal.valueOf(quantidade));
            totalDesc = totalDesc.add(new BigDecimal("0.01"));
        }else{
            totalDesc = valordodesconto.multiply(BigDecimal.valueOf(quantidade));
        }



        totalDesc = totalDesc.setScale(2,RoundingMode.FLOOR);
        totalProd = totalProd.setScale(2,RoundingMode.FLOOR);


        edtVlDesconto.setText(df.format(totalDesc));
        edtValorProduto.setText(real.format(getValorLiquidoExibicao(BigDecimal.valueOf(valorOriginal).subtract(valordodesconto).doubleValue())));
        txtTotal.setText(real.format(getValorTotalExibicao(totalProd.subtract(totalDesc).doubleValue())));
        calculaMargem(totalProd.subtract(totalDesc));



            valordodesconto = totalDesc.divide(BigDecimal.valueOf(quantidade),MathContext.DECIMAL64);


        }



    public void calculaValorDesconto() {
        final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        BigDecimal total = new BigDecimal("0.00");
        BigDecimal desc = new BigDecimal("0.00");
        final DecimalFormat df = new DecimalFormat("0.00", symbols);
        final DecimalFormat dfdesconto = new DecimalFormat("0.0000", symbols);
        quantidade = Double.parseDouble(edtQuantidade.getText().toString());
        percDesc = 0.00000d;
        valordodesconto =  BigDecimal.valueOf(Double.parseDouble(edtVlDesconto.getText().toString())).divide(BigDecimal.valueOf(quantidade), MathContext.DECIMAL64);
        percDesc = valordodesconto.divide(BigDecimal.valueOf(valorOriginal), MathContext.DECIMAL64).multiply(new BigDecimal("100.00")).doubleValue();

        BigDecimal totalProd = new BigDecimal("0.00");
        BigDecimal totalDesc = new BigDecimal("0.00");



        totalDesc = valordodesconto.multiply(BigDecimal.valueOf(quantidade));
        totalProd = BigDecimal.valueOf(valorOriginal).multiply(BigDecimal.valueOf(quantidade));

        totalDesc = totalDesc.setScale(2,RoundingMode.FLOOR);
        totalProd = totalProd.setScale(2,RoundingMode.FLOOR);


        edtDesconto.setText(dfdesconto.format(percDesc));
        edtValorProduto.setText(real.format(getValorLiquidoExibicao(BigDecimal.valueOf(valorOriginal).subtract(valordodesconto).doubleValue())));
        txtTotal.setText(real.format(getValorTotalExibicao(totalProd.subtract(totalDesc).doubleValue())));
        calculaMargem(totalProd.subtract(totalDesc));

    }

    public void calculaQuantidade() {
        final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        txtTotal.setText(real.format(getValorTotalExibicao(BigDecimal.valueOf(valorOriginal).multiply(BigDecimal.valueOf(Double.parseDouble(edtQuantidade.getText().toString()))).doubleValue())));
        calculaPercentual();



    }


    private boolean isItemPromocao() {
        return itemPedido != null && "PR".equals(itemPedido.getSituacao());
    }

    private boolean isItemComDescontoPromocionalBloqueado() {
        return temDescontoMotorPromocional(itemPedido);
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

    private boolean isPedidoAPrazo() {
        try {
            if (pedido != null) {
                if (pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
                    Financeiro financeiro = pedido.getFinanceiro().get(0);
                    if (financeiro != null && financeiro.getCondicao() != null) {
                        return financeiro.getCondicao().toUpperCase().contains("A PRAZO");
                    }
                }

                if (pedido.getCondPagamento() != null && pedido.getCondPagamento().getDescricao() != null) {
                    return pedido.getCondPagamento().getDescricao().toUpperCase().contains("A PRAZO");
                }
            }
        } catch (Exception ignore) {
        }

        return false;
    }

    private double getValorLiquidoExibicao(double valorLiquidoBase) {
        if (isPedidoAPrazo() && isItemPromocional()) {
            return valorLiquidoBase / 0.95d;
        }
        return valorLiquidoBase;
    }

    private double getValorTotalExibicao(double valorTotalBase) {
        if (isPedidoAPrazo() && isItemPromocional()) {
            return valorTotalBase / 0.95d;
        }
        return valorTotalBase;
    }

    private boolean isItemPromocional() {
        return itemPedido != null && itemPedido.getSituacao() != null && "PR".equalsIgnoreCase(itemPedido.getSituacao().trim());
    }

    public boolean validarMotivo() {
        if(edtDesconto.getText().toString().equals("")){

            layout_Motivo.setErrorEnabled(false);
            return true;

        }

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


    

    public boolean validarDataentrega() {

        String tipoUi = edtTipoPed.getText() != null ? edtTipoPed.getText().toString() : "";
        boolean isEntrega = "Entrega".equals(tipoUi) || "E".equals(itemPedido.getTpped());
        if(isEntrega){


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
                        Toast.makeText(PopupItemPed.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(PopupItemPed.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(PopupItemPed.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(PopupItemPed.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(PopupItemPed.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(PopupItemPed.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(PopupItemPed.this,"Não existe rota de entrega para a localidade do cliente, na data informada! Selecione outra data.", Toast.LENGTH_LONG).show();
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
