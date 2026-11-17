package br.com.acalfortaleza.fikonmais.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Fragment;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import br.com.acalfortaleza.fikonmais.MainActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.SplashActivity;
import br.com.acalfortaleza.fikonmais.adapters.FinanceiroAdapter;
import br.com.acalfortaleza.fikonmais.applications.Mask;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.Conveniada;
import br.com.acalfortaleza.fikonmais.models.Credito;
import br.com.acalfortaleza.fikonmais.models.Credz.Plan;
import br.com.acalfortaleza.fikonmais.models.Credz.PlanSimulation;
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.FormaPagamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.popup.PopupFormaPgto;
import br.com.acalfortaleza.fikonmais.popup.PopupPedFechamento;
import br.com.acalfortaleza.fikonmais.popup.PopupPlanSimulationCredz;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class AprazoFragment extends Fragment {

    public View layout;
    public TextInputLayout layout_valor;
    public TextInputLayout layout_vencimento;
    public TextInputLayout layout_parcelas;
    public TextInputLayout layout_documento;
    public TextInputLayout layout_autorizacao;
    public TextInputLayout layout_conveniada;
    public Button btn_add;
    public TextInputEditText edtValor;
    public TextInputEditText edtVencimento;
    public TextInputEditText edtParcelas;
    public TextInputEditText edtAutorizacao;
    public TextInputEditText edtConveniada;
    public Pedidos pedido;
    public FormaPagamento forma;
    private DecimalFormat real;
    private DecimalFormatSymbols symbols;
    private Calendar myCalendar = Calendar.getInstance();
    private ProgressDialog dialog;
    private Handler handler;
    private List<Conveniada> mList;
    private ArrayList<String> listaConveniada = new ArrayList<>();
    private PedidosHelper helper;
    private SpinnerDialog spinnerDialog;
    private String conveniada_selecionada = "N";
    private Conveniada conveniada;
    private int numlan =1;
    public Button btnConsultarPlanos;

    private static String alterarForma ="X";
    private Plan planoEscolhidoCredz;
    private int codigoIntentPlanosCredz;

    private BigDecimal getTotalPedidoSelecionadoAtual() {
        if (PopupFormaPgto.valorTotalPedidoSelecionado >= 0d) {
            return BigDecimal.valueOf(PopupFormaPgto.valorTotalPedidoSelecionado).setScale(2, RoundingMode.HALF_EVEN);
        }
        if (pedido != null) {
            return BigDecimal.valueOf(pedido.getVlTotal()).setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pedido = PopupFormaPgto.pedido;
        forma = PopupFormaPgto.forma;
        handler = new Handler();
        helper = new PedidosHelper();
        planoEscolhidoCredz = PopupFormaPgto.planoEscolhidoCredz;
        codigoIntentPlanosCredz = PopupFormaPgto.codigoIntentPlanosCredz;



    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.frame_forma_aprazo,
                container, false);

        layout_valor = layout.findViewById(R.id.frame_ap_layoutValor);
        layout_vencimento = layout.findViewById(R.id.frame_ap_layoutDtVencimento);
        layout_parcelas = layout.findViewById(R.id.frame_ap_layoutQtdParcelas);
        layout_autorizacao = layout.findViewById(R.id.frame_ap_layoutAutorizacao);
        layout_conveniada = layout.findViewById(R.id.frame_ap_layoutConveniada);
        edtValor = layout.findViewById(R.id.frame_ap_edtvalor);
        edtParcelas = layout.findViewById(R.id.frame_ap_edtQtdPArcelas);
        edtVencimento = layout.findViewById(R.id.frame_ap_edtVencimento);
        edtAutorizacao = layout.findViewById(R.id.frame_ap_edtAutorizacao);
        edtConveniada = layout.findViewById(R.id.frame_ap_edtConveniada);
        btn_add = layout.findViewById(R.id.frame_ap_btn_Add);
        btnConsultarPlanos = layout.findViewById(R.id.frame_ap_btnConsultarPlanos);

        btnConsultarPlanos.setVisibility(View.INVISIBLE);

       if (forma.getCodEve() == 142) {
            edtParcelas.setText("1");
            layout_parcelas.setVisibility(View.GONE);

            // limpar focos para evitar "travamento"
            edtVencimento.clearFocus();
            edtAutorizacao.clearFocus();
            edtConveniada.clearFocus();

            layout_vencimento.setVisibility(View.GONE);
            layout_autorizacao.setVisibility(View.GONE);
            layout_conveniada.setVisibility(View.GONE);
        }

        symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        real = new DecimalFormat("0.00", symbols);

        BigDecimal valor = new BigDecimal("0");

        ConsultarCombobox();

        edtAutorizacao.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                if(event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() || i == EditorInfo.IME_ACTION_DONE) {
                    spinnerDialog.showSpinerDialog();
                }
                return false;
            }
        });

        if(forma.getFlTef().equals("S") ){
            edtAutorizacao.setEnabled(false);

        }else{
            edtAutorizacao.setEnabled(true);
            edtConveniada.setEnabled(true);
        }


        if(forma.getForma().equals("15")){
            edtAutorizacao.setEnabled(false);
            edtConveniada.setEnabled(false);

        }else{
            edtAutorizacao.setEnabled(true);
            edtConveniada.setEnabled(true);
        }

        // Verificar se pedido não é nulo antes de acessar seus métodos
        if (pedido != null) {
            BigDecimal totalCorreto = getTotalPedidoSelecionadoAtual();

            if(PopupFormaPgto.adapter.mList.size()>0) {
                // Se já existem pagamentos na lista, calcula o valor que AINDA FALTA.
                BigDecimal valorJaPago = BigDecimal.ZERO;
                for (Financeiro fin : PopupFormaPgto.adapter.mList) {
                    valorJaPago = valorJaPago.add(BigDecimal.valueOf(fin.getValor()));
                }
                valor = totalCorreto.subtract(valorJaPago);
                edtValor.setText(real.format(valor));
            }else{
                // Se for o PRIMEIRO pagamento, o valor a ser preenchido é o TOTAL do pedido.
                edtValor.setText(real.format(totalCorreto));
            }
        } else {
            // Se pedido for nulo, definir valor padrão
            edtValor.setText(real.format(0.00));
        }

        updatelabel(edtVencimento);

        final DatePickerDialog.OnDateSetListener dateinicial = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth );

                updatelabel(edtVencimento);
            }

        };


        spinnerDialog = new SpinnerDialog(getActivity(), listaConveniada, "Selecione a conveniada!");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtConveniada.setText(item);
                conveniada = mList.get(position);
                conveniada_selecionada = "S";

            }
        });


        if(edtVencimento.isEnabled()) {


            edtVencimento.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (edtVencimento.getRight() - edtVencimento.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {



                            new DatePickerDialog(getActivity(), dateinicial, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();



                        }
                    }
                    return false;
                }
            });

        }



        edtConveniada.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtConveniada.getRight() - edtConveniada.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        edtVencimento.addTextChangedListener(Mask.insert("##/##/####", edtVencimento));


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (alterarForma.equals("X")) {
                    verificarCredito();
                }


                if (alterarForma.equals("S")) {

                } else if (alterarForma.equals("N")) {

                    if(forma.getReseve().equals("CREDZ")){

                        for(int i=0; i<PopupFormaPgto.adapter.mList.size(); i++){

                            if(PopupFormaPgto.adapter.mList.get(i).getEvento().equals(forma.getReseve())){

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        new AlertDialog.Builder(getActivity())
                                            .setTitle("CREDZ já incluída")
                                            .setMessage("Forma de pagamento CREDZ já foi incluída")
                                            .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create().show();

                                    }
                                });
                                return;

                            }

                        }

                    }


                    // Para PIX (código 142), pular validações de campos ocultos
                    boolean validarAuth = (forma.getCodEve() == 142) ? true : ValidarAutorizacao();
                    boolean validarConv = (forma.getCodEve() == 142) ? true : ValidarConveniada();
                    
                    if (!ValidarParcelas() || !validarValor() || !ValidarValorParcela() || !validarAuth || !validarConv) {

                    } else {

                        if (forma.getReseve().equals("CREDZ") && planoEscolhidoCredz != null && planoEscolhidoCredz.getDescription().equals("TURBINADO")) {

                            forma.setReseve("CREDZ TURBINADO");

                        }

                        BigDecimal valorTotal = new BigDecimal(edtValor.getText().toString());
                        BigDecimal qtdParcela = new BigDecimal(edtParcelas.getText().toString());
                        BigDecimal valorParcela;
                        BigDecimal valorCalculado;
                        BigDecimal differ;
                        BigDecimal vlUltimaParcela;

                        valorParcela = valorTotal.divide(qtdParcela, 2, RoundingMode.HALF_UP);
                        valorCalculado = valorParcela.multiply(qtdParcela);
                        differ = valorTotal.subtract(valorCalculado);
                        vlUltimaParcela = valorParcela.add(differ);


                        for (int i = 0; i < Integer.parseInt(edtParcelas.getText().toString()); i++) {

                            Date dataalterada = new Date();
                            Calendar c = Calendar.getInstance();
                            c.setTime(dataalterada);
                            c.add(Calendar.DATE, +(forma.getInterdias() * (i + 1)));
                            dataalterada = c.getTime();
                            SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy");
                            SimpleDateFormat formataSaida = new SimpleDateFormat("yyyy-MM-dd");
                            int contador = i;

                            Financeiro fin = new Financeiro();
                            fin.setCodfilcrc(0);
                            fin.setNumtit("0");
                            fin.setCodorigcrc(0);
                            fin.setCgccpf("0");
                            fin.setDesdtit("");
                            fin.setCodrec("0");
                            fin.setNumlan(PopupFormaPgto.numlan);
                            // Verificar se pedido e financeiro não são nulos
                            if (pedido != null && pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
                                fin.setCondicao(pedido.getFinanceiro().get(0).getCondicao());
                                fin.setCondpgto(pedido.getFinanceiro().get(0).getCondpgto());
                            } else {
                                fin.setCondicao("");
                                fin.setCondpgto("");
                            }
                            fin.setEvento(forma.getReseve());
                            fin.setDtvcto(formataSaida.format(dataalterada));
                            // Para PIX (código 142), usar valores padrão ao invés de acessar campos ocultos
                            if (forma.getCodEve() == 142) {
                                fin.setConveniada("");
                            } else {
                                fin.setConveniada(edtConveniada.getText().toString());
                            }
                            
                            fin.setForma(Integer.parseInt(forma.getForma()));
                            fin.setNumprc(contador + 1);
                            fin.setQtprc(Integer.parseInt(edtParcelas.getText().toString()));
                            if (contador + 1 != Integer.parseInt(edtParcelas.getText().toString())) {
                                fin.setValor(valorParcela.doubleValue());
                            } else {
                                fin.setValor(vlUltimaParcela.doubleValue());
                            }

                            fin.setCodEve(forma.getCodEve());
                            fin.setFlautcre(forma.getFlautcre());
                            
                            // Para PIX (código 142), usar valores padrão ao invés de acessar campos ocultos
                            if (forma.getCodEve() == 142) {
                                fin.setNumaut("0");
                            } else {
                                if (edtAutorizacao.getText().toString().equals("")) {
                                    fin.setNumaut("0");
                                } else {
                                    fin.setNumaut(edtAutorizacao.getText().toString());
                                }
                            }

                            if (forma.getCodEve() == 142) {
                                // Para PIX, não definir conveniada
                            } else {
                                if (!edtConveniada.getText().toString().equals("")) {
                                    fin.setCodcon(conveniada.getCodcon());
                                    fin.setTaxadm(conveniada.getTaxaAdm());
                                }
                            }
                            // Verificar se pedido e financeiro não são nulos
                            if (pedido != null && pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
                                fin.setFlmultneg(pedido.getFinanceiro().get(0).getFlmultneg());
                            } else {
                                fin.setFlmultneg("");
                            }
                            fin.setPortador(forma.getPortador());


                            PopupFormaPgto.adapter.mList.add(fin);

                        }

                        if (pedido != null) {
                            int quantidadeParcelas = Integer.parseInt(edtParcelas.getText().toString());
                            pedido.setQtprc(quantidadeParcelas);
                            pedido.setVlEntrada(0.0d);
                            pedido.setVlPrc(valorParcela.doubleValue());
                        }

                        PopupFormaPgto.numlan = PopupFormaPgto.numlan + 1;


                        if (FinanceiroAdapter.mList != null && FinanceiroAdapter.mList.size() > 0) {
                            for (int i = 0; i < FinanceiroAdapter.mList.size(); i++) {

                                PopupFormaPgto.adapter.mListItens.put(PopupFormaPgto.adapter.mList.get(i).toString(), PopupFormaPgto.adapter.mList.get(i));

                            }

                            PopupFormaPgto.adapter.notifyDataSetChanged();
                            PopupPedFechamento.adapter.notifyDataSetChanged();
                        }

                    }

                }

                BigDecimal valor = new BigDecimal("0.00");


                for (int i = 0; i < PopupFormaPgto.adapter.mList.size(); i++) {
                    valor = valor.add(BigDecimal.valueOf(PopupFormaPgto.adapter.mList.get(i).getValor()));
                }
                // Verificar se pedido não é nulo antes de acessar seus métodos
                if (pedido != null) {
                    // CORRIGIDO: Usar apenas VlTotal, não somar VlFrete
                    BigDecimal totalCorreto = getTotalPedidoSelecionadoAtual();
                    BigDecimal valorRestante = totalCorreto.subtract(valor);
                    edtValor.setText(real.format(valorRestante));

                    if (valorRestante.compareTo(BigDecimal.ZERO) == 0) {
                        PopupFormaPgto.btn_gravar.setEnabled(true);
                        PopupFormaPgto.btn_gravar.setBackgroundResource(R.drawable.backgroudbuttom);
                    }
                } else {
                    edtValor.setText(real.format(0.00));
                }


            }
        });

        btnConsultarPlanos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), PopupPlanSimulationCredz.class);
                intent.putExtra("pedido", pedido);
                intent.putExtra("valor", Float.parseFloat(edtValor.getText().toString()));
                getActivity().startActivityForResult(intent, codigoIntentPlanosCredz);
                // Removido this.finalize() que causava travamento

            }
        });

        if(forma.getReseve().equals("CREDZ")){

            edtValor.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) btnConsultarPlanos.getLayoutParams();
                    layoutParams.setMargins(0,3,0,3);

                    btnConsultarPlanos.setVisibility(View.VISIBLE);
                    btnConsultarPlanos.setLayoutParams(layoutParams);
                    btnConsultarPlanos.setEnabled(true);

                    btn_add.setVisibility(View.INVISIBLE);
                    btn_add.setEnabled(false);

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            if(planoEscolhidoCredz!=null && planoEscolhidoCredz.getPlanId()>0){

                edtValor.setText(String.valueOf(planoEscolhidoCredz.getAmount().getBasePurchase()));
                edtParcelas.setText(String.valueOf(planoEscolhidoCredz.getInstallments()));

                btn_add.setVisibility(View.VISIBLE);
                btn_add.setEnabled(true);

                btnConsultarPlanos.setVisibility(View.INVISIBLE);
                btnConsultarPlanos.setEnabled(false);

            }
            else{

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) btnConsultarPlanos.getLayoutParams();
                layoutParams.setMargins(0,3,0,3);

                btnConsultarPlanos.setVisibility(View.VISIBLE);
                btnConsultarPlanos.setLayoutParams(layoutParams);
                btnConsultarPlanos.setEnabled(true);

                btn_add.setVisibility(View.INVISIBLE);
                btn_add.setEnabled(false);

            }

            edtParcelas.setEnabled(false);
            edtParcelas.setClickable(false);
            edtParcelas.setFocusable(false);

            edtAutorizacao.setText("0");
            edtAutorizacao.setEnabled(false);
            edtAutorizacao.setClickable(false);
            edtAutorizacao.setFocusable(false);

            edtVencimento.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            edtVencimento.setEnabled(false);
            edtVencimento.setClickable(false);
            edtVencimento.setFocusable(false);

            edtConveniada.setText("VISA");
            edtConveniada.setEnabled(false);
            edtConveniada.setClickable(false);
            edtConveniada.setFocusable(false);

        }

        return layout;

    }






    public boolean validarValor(){
        BigDecimal valor = new BigDecimal("0.00");

        if(edtValor.getText().toString().equals("") || Double.parseDouble(edtValor.getText().toString())== 0 ){
            Toast.makeText(getActivity().getApplicationContext(),"Informe o valor da parcela!",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(PopupFormaPgto.adapter.mList.size()>0) {
            for (int i = 0; i < PopupFormaPgto.adapter.mList.size(); i++) {
                valor = valor.add(BigDecimal.valueOf(PopupFormaPgto.adapter.mList.get(i).getValor()));
            }
        }
        BigDecimal valorpedido =  new BigDecimal("0.00");

        // Verificar se pedido não é nulo antes de acessar seus métodos
        if (pedido != null) {
            valorpedido = valorpedido.add(getTotalPedidoSelecionadoAtual());
        } else {
            // Se pedido for nulo, retornar true para evitar bloqueio
            return true;
        }


        if(valorpedido.compareTo(valor.add(BigDecimal.valueOf(Double.parseDouble(edtValor.getText().toString())).setScale(2, RoundingMode.HALF_EVEN))) < 0) {

            Toast.makeText(getActivity().getApplicationContext(),"Valor da parcela maior que o permitido!",Toast.LENGTH_SHORT).show();
            return false;

        }else{
            return true;
        }






    }



    public  void  verificarCredito() {

            alterarForma="X";
        if (Integer.parseInt(forma.getForma()) == 15 || Integer.parseInt(forma.getForma()) == 7 ) {

                    Credito credito;


                    credito = PopupFormaPgto.retornoCredito;


                    if (credito != null && credito.getRetorno().equals("TEM SALDO")) {
                        alterarForma = "N";
                    } else if (credito != null && credito.getRetorno().equals("SEM SALDO")) {




                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());


                                builder.setMessage("Cliente não possui limite de credito para esse pedido! " + "Saldo Disponivel:" + String.valueOf(credito.getSaldo()));

                                builder
                                        .setCancelable(false)
                                        .setPositiveButton("Alterar Forma", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                alterarForma = "X";
                                                return;

                                            }

                                        });

                                builder.setNegativeButton("Continuar", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        alterarForma = "N";
                                        return;

                                    }
                                });

                                builder.create().show();




                    }


                }else

                {
                    alterarForma = "N";
                }



    }



    public void ConsultarCombobox() {

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {

                     mList = helper.GetConveniadas();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            if (mList != null && mList.size() > 0) {

                                for (Conveniada c : mList) {
                                    listaConveniada.add(c.getRescon());
                                }

                                if(planoEscolhidoCredz!=null){

                                    for (Conveniada c:mList) {
                                        if(c.getRescon().equals("VISA")){
                                            conveniada = c;
                                            conveniada_selecionada = "S";
                                        }
                                    }

                                }

                            }

                        }
                    });

                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(getActivity().getApplicationContext()).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(getActivity().getApplicationContext()).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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


    public boolean ValidarParcelas(){

        // Se for PIX (código 142), não valida parcelas pois o campo está oculto
        if(forma.getCodEve() == 142){
            return true;
        }

        if((edtParcelas.getText().toString().equals(""))){
                layout_parcelas.setErrorEnabled(true);
                layout_parcelas.setError("Informe o N° de parcelas!");
                return false;
            }else{
                if(Integer.parseInt(edtParcelas.getText().toString()) == 0 || Integer.parseInt(edtParcelas.getText().toString()) > forma.getParcelaMax() ){
                    layout_parcelas.setErrorEnabled(true);
                    layout_parcelas.setError("N° de parcelas maxima é "+forma.getParcelaMax());
                    return false;
                }else{
                    layout_parcelas.setErrorEnabled(false);
                    return true;
                }

            }

    }

    public boolean ValidarAutorizacao(){
        if(isDepositos()){
            layout_autorizacao.setErrorEnabled(false);
            return true;
        }
        if(forma.getFlTef().equals("N") && forma.getPortador()==21 || forma.getPortador()==43){
            if(edtAutorizacao.getText().toString().equals("")){
                layout_autorizacao.setErrorEnabled(true);
                layout_autorizacao.setError("Informe o N° da autorização!");
                return false;
            }else{
                layout_autorizacao.setErrorEnabled(false);
                return true;
            }
        }else{
            return true;
        }
    }


    public boolean ValidarValorParcela(){
        // Se for PIX (código 142), não valida valor da parcela pois o campo está oculto
        if(forma.getCodEve() == 142){
            return true;
        }
        
        // Se for apenas 1 parcela (à vista), não aplica validação de valor mínimo
        int numeroParcelas = Integer.parseInt(edtParcelas.getText().toString());
        if(numeroParcelas <= 1){
            layout_parcelas.setErrorEnabled(false);
            return true;
        }
        
        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        double vlparcela = (Double.parseDouble(edtValor.getText().toString())/numeroParcelas);
        double parcelanin = forma.getVlminprc();
            if(parcelanin>0 && vlparcela<parcelanin){
                layout_parcelas.setErrorEnabled(true);
                layout_parcelas.setError("O Valor minimo da parcela tem que ser "+real.format(forma.getVlminprc()));
                return false;
            }else{
                layout_parcelas.setErrorEnabled(false);
                return true;
            }

    }


    public boolean ValidarConveniada(){

        if(isDepositos()){
            layout_conveniada.setErrorEnabled(false);
            return true;
        }
        if( forma.getFlTef().equals("N") && forma.getPortador()==21 || forma.getPortador()==43){
            if(edtConveniada.getText().toString().equals("")) {
                layout_conveniada.setErrorEnabled(true);
                layout_conveniada.setError("Informe a conveniada!");
                return false;
            }else{
                layout_conveniada.setErrorEnabled(false);
                return true;
            }
        }else{
            layout_conveniada.setErrorEnabled(false);
            return true;
        }

    }



    private void updatelabel(TextInputEditText editext) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));

        myCalendar.add(myCalendar.DATE,forma.getNrdiasvc1());

        editext.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean isDepositos(){
        if(forma == null){
            return false;
        }
        String desc = forma.getDescricao();
        if(desc == null){
            return false;
        }
        String normalized = Normalizer.normalize(desc, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        normalized = normalized.toUpperCase(Locale.ROOT);
        return normalized.contains("DEPOSITO");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
        // Limpar handlers para evitar memory leaks
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        
        // Fechar ProgressDialog se estiver aberto
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        
        // Limpar referências para evitar vazamentos de contexto
        pedido = null;
        forma = null;
        helper = null;
        mList = null;
        listaConveniada = null;
        conveniada = null;
        planoEscolhidoCredz = null;
        
        // Limpar spinner dialog
        if (spinnerDialog != null) {
            spinnerDialog = null;
        }
    }

}
