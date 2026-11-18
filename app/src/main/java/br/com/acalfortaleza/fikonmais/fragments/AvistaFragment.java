package br.com.acalfortaleza.fikonmais.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
import android.text.TextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.Editable;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.FinanceiroAdapter;
import br.com.acalfortaleza.fikonmais.applications.Mask;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.Conveniada;
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.FormaPagamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.Recibo;
import br.com.acalfortaleza.fikonmais.popup.PopupFormaPgto;
import br.com.acalfortaleza.fikonmais.popup.PopupPedFechamento;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class AvistaFragment extends Fragment {

    public View layout;
    public TextInputLayout layout_valor;
    public TextInputLayout layout_vencimento;
    public TextInputLayout layout_recibo;
    public Button btn_add;
    public TextInputEditText edtValor;
    public TextInputEditText edtVencimento;
    public TextInputEditText edtRecibo;
    public Pedidos pedido;
    public FormaPagamento forma;
    private  DecimalFormat real;
    private DecimalFormatSymbols symbols;
    private Calendar myCalendar = Calendar.getInstance();
    private List<Recibo> mList;
    private ArrayList<String> listaRecibo = new ArrayList<>();
    private PedidosHelper helper;
    private Handler handler;
    private ProgressDialog dialog;
    private SpinnerDialog spinnerDialog;
    private String recibo_selecionado = "N";
    private Recibo recibo;
    private Recibo ultimoReciboAdicionado;
    private int numlan=1;
    private List<Recibo> recibosSelecionados;
    private boolean reciboAdicionado = false;
    private static AvistaFragment instanciaAtual;

    private BigDecimal getTotalPedidoSelecionadoAtual() {
        if (PopupFormaPgto.valorTotalPedidoSelecionado >= 0d) {
            return BigDecimal.valueOf(PopupFormaPgto.valorTotalPedidoSelecionado).setScale(2, RoundingMode.HALF_EVEN);
        }
        return (pedido != null) ? BigDecimal.valueOf(pedido.getVlTotal()).setScale(2, RoundingMode.HALF_EVEN) : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
    }
  

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pedido = PopupPedFechamento.pedido;
        forma = PopupFormaPgto.forma;
        handler = new Handler();
        helper = new PedidosHelper();
        recibosSelecionados = new ArrayList<>();
    }


    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.frame_forma_avista,
                container, false);
        instanciaAtual = this;

        layout_valor = layout.findViewById(R.id.frame_av_layoutValor);
        layout_vencimento = layout.findViewById(R.id.frame_av_layoutDtVencimento);
        layout_recibo = layout.findViewById(R.id.frame_av_layoutRecibo);
        edtValor = layout.findViewById(R.id.frame_av_edtvalor);
        edtRecibo = layout.findViewById(R.id.frame_av_edtRecibo);
        edtVencimento = layout.findViewById(R.id.frame_av_edtVencimento);
        btn_add = layout.findViewById(R.id.frame_av_btn_Add);

        symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        real = new DecimalFormat("0.00", symbols);

        BigDecimal valorRestante;

        // Pega o valor total CORRETO que veio do PopupPedFechamento.
        BigDecimal totalCorreto = getTotalPedidoSelecionadoAtual();

        if (PopupFormaPgto.adapter.mList.size() > 0) {
            // Se já existem pagamentos na lista, calcula o valor que AINDA FALTA.
            BigDecimal valorJaPago = BigDecimal.ZERO;
            for (Financeiro fin : PopupFormaPgto.adapter.mList) {
                valorJaPago = valorJaPago.add(BigDecimal.valueOf(fin.getValor()));
            }
            valorRestante = totalCorreto.subtract(valorJaPago);
        } else {
            // Se for o PRIMEIRO pagamento, o valor a ser preenchido é o TOTAL do pedido.
            valorRestante = totalCorreto;
        }

        edtValor.setText(real.format(valorRestante));
        android.util.Log.d("AvistaFragmentDebug", "Valor total definido no campo: " + real.format(valorRestante));


        if(forma.getCodEve()==29){
            edtRecibo.setEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                edtRecibo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#8A18D1")));
                ConsultarCombobox(String.valueOf(pedido.getCliente().getCodcli()));
                edtValor.setText(real.format(0.00d));
            }

        }else{
            edtRecibo.setEnabled(false);
            edtRecibo.setText("");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                edtRecibo.setCompoundDrawableTintList(ColorStateList.valueOf(Color.parseColor("#757575")));
            }
        }


        // Verificar se é PIX LINK (código 142) para ocultar campo de vencimento
        if (forma.getCodEve() == 142) {
            layout_vencimento.setVisibility(View.INVISIBLE);
            // Definir data atual como valor padrão
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
            edtVencimento.setText(sdf.format(new Date()));
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


        spinnerDialog = new SpinnerDialog(getActivity(), listaRecibo, "Selecione o credito!");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {


            @Override
            public void onClick(String item, int position) {
                // --- INÍCIO DA CORREÇÃO AQUI ---
                BigDecimal valorJaPago = BigDecimal.ZERO;
                for (int i = 0; i < PopupFormaPgto.adapter.mList.size(); i++) {
                    valorJaPago = valorJaPago.add(BigDecimal.valueOf(PopupFormaPgto.adapter.mList.get(i).getValor()));
                }

                BigDecimal totalCorretoDoPedido = (pedido != null) ? BigDecimal.valueOf(pedido.getVlTotal()) : BigDecimal.ZERO;
                totalCorretoDoPedido = totalCorretoDoPedido.setScale(2, RoundingMode.HALF_EVEN);
                BigDecimal valorRestante = totalCorretoDoPedido.subtract(valorJaPago);

                BigDecimal saldoRecibo = BigDecimal.valueOf(mList.get(position).getSldtit());

                // O valor a ser preenchido é o menor entre o saldo do recibo e o valor restante do pedido.
                if (saldoRecibo.compareTo(valorRestante) > 0) {
                    edtValor.setText(real.format(valorRestante));
                } else {
                    edtValor.setText(real.format(saldoRecibo));
                }
                // --- FIM DA CORREÇÃO AQUI ---

                recibo = mList.get(position);
                recibo.setValor(Double.parseDouble(edtValor.getText().toString()));
                recibo.setConsultor(pedido.getConsultor());
                recibo.setCodfilRec(pedido.getCodfil());
                recibo.setNumpedven(String.valueOf(pedido.getNumpedven()));
                boolean existe = false;
                for (Recibo r : recibosSelecionados) {
                    if (r.getNumrecdevol() != null && r.getNumrecdevol().equals(recibo.getNumrecdevol())) {
                        existe = true;
                        r.setValor(recibo.getValor());
                        break;
                    }
                }
                if (!existe) {
                    recibosSelecionados.add(recibo);
                }
                edtRecibo.setText( mList.get(position).Numrecdevol);
                recibo_selecionado = "S";

                if(recibosSelecionados != null && recibosSelecionados.size()>0){
                    PopupPedFechamento.pedido.setRecibos(recibosSelecionados);
                }
                if (listaRecibo != null && listaRecibo.size() > position) {
                    listaRecibo.remove(position);
                }
                if (mList != null && mList.size() > position) {
                    mList.remove(position);
                }
            }

        });


        edtRecibo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtRecibo.getRight() - edtRecibo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });

        edtVencimento.addTextChangedListener(Mask.insert("##/##/####", edtVencimento));

        edtRecibo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    if (recibo != null && !reciboAdicionado) {
                        mList.add(recibo);
                        String texto;
                        if(recibo.getNumnotadev().equals("")){
                            texto = "Codfil: "+recibo.getCodfil()+" - Valor: R$ "+ real.format(recibo.getSldtit())+ " - Recibo: "+recibo.getNumrecdevol();
                        }else{
                            texto = "Codfil: "+recibo.getCodfil()+" - Nf: "+recibo.getNumnotadev()+" - Valor: R$ "+ real.format(recibo.getSldtit())+ " - Recibo: "+recibo.getNumrecdevol();
                        }
                        listaRecibo.add(texto);
                        spinnerDialog = new SpinnerDialog(getActivity(), listaRecibo, "Selecione o credito!");
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        edtRecibo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    if (recibo != null && !reciboAdicionado) {
                        mList.add(recibo);
                        String texto;
                        if(recibo.getNumnotadev().equals("")){
                            texto = "Codfil: "+recibo.getCodfil()+" - Valor: R$ "+ real.format(recibo.getSldtit())+ " - Recibo: "+recibo.getNumrecdevol();
                        }else{
                            texto = "Codfil: "+recibo.getCodfil()+" - Nf: "+recibo.getNumnotadev()+" - Valor: R$ "+ real.format(recibo.getSldtit())+ " - Recibo: "+recibo.getNumrecdevol();
                        }
                        listaRecibo.add(texto);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });




        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validarValor() || !validarRecibo()) {
                    return; // Sai do método se a validação falhar
                }

                // --- Bloco de código original para criar e preencher o objeto 'fin' ---
                Date dataalterada = new Date();
                SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat formataSaida = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    dataalterada = formataData.parse(edtVencimento.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                numlan = (FinanceiroAdapter.mList != null) ? FinanceiroAdapter.mList.size() + 1 : 1;
                Financeiro fin = new Financeiro();

                if (recibo != null && recibo.getSldtit() > 0) {
                    fin.setCodfilcrc(recibo.getCodfil());
                    fin.setNumtit(recibo.getNumtit());
                    fin.setCodorigcrc(recibo.getCodorigcrc());
                    fin.setCgccpf(recibo.getCgccpf());
                    fin.setDesdtit(recibo.getDesdtit());
                    fin.setCodrec(recibo.getNumrecdevol());
                } else {
                    fin.setCodfilcrc(0);
                    fin.setNumtit("0");
                    fin.setCodorigcrc(0);
                    fin.setCgccpf("0");
                    fin.setDesdtit("");
                    fin.setCodrec("0");
                }
                fin.setNumlan(numlan);
                if (pedido != null && pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
                    fin.setCondicao(pedido.getFinanceiro().get(0).getCondicao());
                    fin.setCondpgto(pedido.getFinanceiro().get(0).getCondpgto());
                }
                fin.setEvento(forma.getReseve());
                fin.setDtvcto(formataSaida.format(dataalterada));
                fin.setConveniada("");
                fin.setForma(Integer.parseInt(forma.getForma()));
                fin.setNumprc(PopupFormaPgto.numlan);
                fin.setQtprc(1);
                fin.setValor(Double.parseDouble(edtValor.getText().toString()));
                fin.setCodEve(forma.getCodEve());
                fin.setFlautcre(forma.getFlautcre());
                fin.setNumaut("0");
                fin.setNumdoc("0");
                fin.setCodcon(0);
                fin.setTaxadm(0.00d);
                if (pedido != null && pedido.getFinanceiro() != null && !pedido.getFinanceiro().isEmpty()) {
                    fin.setFlmultneg(pedido.getFinanceiro().get(0).getFlmultneg());
                } else {
                    fin.setFlmultneg("");
                }
                fin.setPortador(forma.getPortador());
                // --- Fim do Bloco de preenchimento do 'fin' ---

                boolean atualizouExistente = false;
                if (forma.getCodEve() == 29 && recibo != null && recibo.getNumrecdevol() != null) {
                    for (int i = 0; i < PopupFormaPgto.adapter.mList.size(); i++) {
                        Financeiro existente = PopupFormaPgto.adapter.mList.get(i);
                        if (existente.getCodEve() == 29 && existente.getCodrec() != null && existente.getCodrec().equals(recibo.getNumrecdevol())) {
                            existente.setValor(Double.parseDouble(edtValor.getText().toString()));
                            existente.setDtvcto(fin.getDtvcto());
                            existente.setNumlan(fin.getNumlan());
                            existente.setNumprc(fin.getNumprc());
                            existente.setForma(fin.getForma());
                            existente.setEvento(fin.getEvento());
                            existente.setCondicao(fin.getCondicao());
                            existente.setCondpgto(fin.getCondpgto());
                            existente.setPortador(fin.getPortador());
                            existente.setCodfilcrc(fin.getCodfilcrc());
                            existente.setNumtit(fin.getNumtit());
                            existente.setCodorigcrc(fin.getCodorigcrc());
                            existente.setCgccpf(fin.getCgccpf());
                            existente.setDesdtit(fin.getDesdtit());
                            existente.setCodrec(fin.getCodrec());
                            atualizouExistente = true;
                            break;
                        }
                    }
                }
                if (!atualizouExistente) {
                    PopupFormaPgto.adapter.mList.add(fin);
                }
                if (pedido != null) {
                    pedido.setVlEntrada(Double.parseDouble(edtValor.getText().toString()));
                }
                PopupFormaPgto.numlan = PopupFormaPgto.numlan + 1;

                if (FinanceiroAdapter.mList != null && FinanceiroAdapter.mList.size() > 0) {
                    for (int i = 0; i < FinanceiroAdapter.mList.size(); i++) {
                        PopupFormaPgto.adapter.mListItens.put(PopupFormaPgto.adapter.mList.get(i).toString(), PopupFormaPgto.adapter.mList.get(i));
                    }
                    PopupFormaPgto.adapter.notifyDataSetChanged();
                    PopupPedFechamento.adapter.notifyDataSetChanged();
                }

                // --- INÍCIO DA CORREÇÃO NO RECALCULO ---
                BigDecimal valorJaPago = new BigDecimal("0");
                for (int i = 0; i < PopupFormaPgto.adapter.mList.size(); i++) {
                    valorJaPago = valorJaPago.add(BigDecimal.valueOf(PopupFormaPgto.adapter.mList.get(i).getValor()));
                }

                // Pega o total CORRETO do pedido, sem somar nada a mais.
                BigDecimal totalCorretoDoPedido = (pedido != null) ? BigDecimal.valueOf(pedido.getVlTotal()) : BigDecimal.ZERO;
                totalCorretoDoPedido = totalCorretoDoPedido.setScale(2, RoundingMode.HALF_EVEN);

                if (forma.getCodEve() == 29) {
                    edtValor.setText(real.format(0.00d)); // Comportamento original para recibo
                } else {
                    // Recalcula o valor restante a partir do total CORRETO.
                    BigDecimal valorRestante = totalCorretoDoPedido.subtract(valorJaPago);
                    edtValor.setText(real.format(valorRestante));
                    android.util.Log.d("AvistaFragmentDebug", "Recalculado valor restante para: " + real.format(valorRestante));
                }

                // Verifica se o valor total foi pago para habilitar o botão de gravar
                // Usamos uma pequena tolerância para evitar problemas de arredondamento com double/float
                if (totalCorretoDoPedido.subtract(valorJaPago).abs().doubleValue() < 0.01) {
                    PopupFormaPgto.btn_gravar.setEnabled(true);
                    PopupFormaPgto.btn_gravar.setBackgroundResource(R.drawable.backgroudbuttom);
                }
                reciboAdicionado = true;
                ultimoReciboAdicionado = recibo;
                edtRecibo.setText("");
                recibo_selecionado = "N";
                recibo = null;
                }

        });

        return layout;

    }


    public boolean validarRecibo(){

        if(forma.getCodEve() == 29 && edtRecibo.getText().toString().equals("")){
            Toast.makeText(getActivity().getApplicationContext(),"Selecione o recibo de devolução!",Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }


    }

    public boolean validarValor(){
        BigDecimal valorDigitado;
        try {
            if(edtValor.getText().toString().isEmpty()) {
                valorDigitado = BigDecimal.ZERO;
            } else {
                valorDigitado = new BigDecimal(edtValor.getText().toString());
            }
        } catch (NumberFormatException e) {
            valorDigitado = BigDecimal.ZERO;
        }

        if(valorDigitado.compareTo(BigDecimal.ZERO) <= 0){
            // Apenas para o caso de recibo é permitido valor 0 se o saldo do recibo for maior que o pedido
            if(forma.getCodEve() != 29) {
                Toast.makeText(getActivity().getApplicationContext(),"Informe o valor da parcela!",Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        BigDecimal valorJaPago = BigDecimal.ZERO;
        if(PopupFormaPgto.adapter.mList.size() > 0) {
            for (Financeiro fin : PopupFormaPgto.adapter.mList) {
                valorJaPago = valorJaPago.add(BigDecimal.valueOf(fin.getValor()));
            }
        }

        // Usa apenas o vlTotal que já está completo
        BigDecimal valorPedidoCorreto = (pedido != null) ? BigDecimal.valueOf(pedido.getVlTotal()) : BigDecimal.ZERO;
        valorPedidoCorreto = valorPedidoCorreto.setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal valorTotalAPagar = valorJaPago.add(valorDigitado.setScale(2, RoundingMode.HALF_EVEN));

        // A soma dos pagamentos não pode ser maior que o total do pedido.
        // Usamos uma pequena tolerância (0.01) para evitar problemas de arredondamento.
        if(valorTotalAPagar.subtract(valorPedidoCorreto).doubleValue() > 0.01) {
            Toast.makeText(getActivity().getApplicationContext(),"Valor da parcela maior que o permitido!",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
    private void updatelabel(TextInputEditText editext) {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));

        myCalendar.add(myCalendar.DATE,forma.getNrdiasvc1());

        editext.setText(sdf.format(myCalendar.getTime()));
    }



    public void ConsultarCombobox( String codcli) {

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread() {


            public void run() {

                try {

                    mList = helper.GetRecibos(codcli);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (mList != null && mList.size() > 0) {
                                for (Recibo r : mList) {
                                    String texto="";
                                    if(r.getNumnotadev().equals("")){
                                        texto = "Codfil: "+r.getCodfil()+" - Valor: R$ "+ real.format(r.getSldtit())+ " - Recibo: "+r.getNumrecdevol();
                                    }else{
                                        texto = "Codfil: "+r.getCodfil()+" - Nf: "+r.getNumnotadev()+" - Valor: R$ "+ real.format(r.getSldtit())+ " - Recibo: "+r.getNumrecdevol();
                                    }

                                    listaRecibo.add(texto);
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

    public static void devolverReciboParaSelecao(Financeiro fin) {
        if (instanciaAtual == null || fin == null) return;
        Recibo r = new Recibo();
        r.setCodfil(fin.getCodfilcrc());
        r.setNumtit(fin.getNumtit());
        r.setCodorigcrc(fin.getCodorigcrc());
        r.setCgccpf(fin.getCgccpf());
        r.setDesdtit(fin.getDesdtit());
        r.setNumrecdevol(fin.getCodrec());
        r.setSldtit(fin.getValor());
        String texto;
        if (r.getNumnotadev() != null && !r.getNumnotadev().equals("")) {
            texto = "Codfil: " + r.getCodfil() + " - Nf: " + r.getNumnotadev() + " - Valor: R$ " + instanciaAtual.real.format(r.getSldtit()) + " - Recibo: " + r.getNumrecdevol();
        } else {
            texto = "Codfil: " + r.getCodfil() + " - Valor: R$ " + instanciaAtual.real.format(r.getSldtit()) + " - Recibo: " + r.getNumrecdevol();
        }
        if (instanciaAtual.mList == null) instanciaAtual.mList = new ArrayList<>();
        instanciaAtual.mList.add(r);
        if (instanciaAtual.listaRecibo == null) instanciaAtual.listaRecibo = new ArrayList<>();
        instanciaAtual.listaRecibo.add(texto);
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
        recibosSelecionados = null;
        mList = null;
        listaRecibo = null;
        recibo = null;

        // Limpar spinner dialog
        if (spinnerDialog != null) {
            spinnerDialog = null;
        }
    }
}
