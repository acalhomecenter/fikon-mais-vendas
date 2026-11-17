package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.BeneficioPromocao;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Notas;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.PedTransferencia;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupCodigoMotor extends Activity {

    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    String resultado = "";
    private SpinnerDialog spinnerDialog;
    private TextInputEditText edtCodigo;
    private Button btn_iniciar;
    private PedidosHelper helper;
    private OrcamentoHelper orcamentoHelper;
    private ProgressDialog dialog;
    private Handler handler;
    private List<String> retorno;
    private List<BeneficioPromocao>  mList;
    private Pedidos pedidoOrig;
    private Pedidos pedidoAPrazo;
    private Button btn_fechar;
    private TextInputLayout layout_codmotor;
    private AlertDialog alertaDialog;
    private Orcamento orcamentoOrig;
    private Orcamento orcamentoAPrazo;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_codigomotor);

        Bundle extras = getIntent().getExtras();

        if(extras.containsKey("pedido")) {
            pedidoOrig = (Pedidos) extras.getSerializable("pedido");
            pedidoAPrazo = new Gson().fromJson(new Gson().toJson(pedidoOrig), Pedidos.class);
        }
        else if(extras.containsKey("orcamento")){
            orcamentoOrig = (Orcamento) extras.getSerializable("orcamento");
            orcamentoAPrazo = new Gson().fromJson(new Gson().toJson(orcamentoOrig), Orcamento.class);
        }


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .3));

        helper = new PedidosHelper();
        orcamentoHelper = new OrcamentoHelper();

        handler = new Handler();
        edtCodigo = findViewById(R.id.pop_ped_motor_edtCodigo);
        btn_iniciar = findViewById(R.id.pop_ped_motor_btn_iniciarr);
        btn_fechar = findViewById(R.id.pop_ped_motor_btnFechar);
        layout_codmotor = findViewById(R.id.pop_ped_Motor_layoutCodigo);

        if(pedidoOrig!=null) {

            btn_fechar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PopupCodigoMotor.this, PopupPedFechamento.class);
                    intent.putExtra("pedido", pedidoOrig);
                    intent.putExtra("pedidoAPrazo", pedidoAPrazo);
                    startActivity(intent);
                    finish();

                }
            });

            btn_iniciar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    if(validarCodigo()) {

                        ConsultarPromocao();

                    }

                }
            });

        }
        else if(orcamentoOrig!=null){

            btn_fechar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PopupCodigoMotor.this, PopupOrcFechamento.class);
                    intent.putExtra("orcamento", orcamentoOrig);
                    intent.putExtra("orcamentoAPrazo", orcamentoAPrazo);
                    startActivity(intent);
                    finish();

                }
            });

            btn_iniciar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(validarCodigoOrcamento()) {

                        ConsultarPromocaoOrcamento();

                    }

                }
            });

        }


    }

    public boolean  validarCodigo() {
        String temDesconto = "N";
        for (int i = 0; i < pedidoOrig.getItens().size(); i++) {


            if (pedidoOrig.getItens().get(i).getVlDescItem() > 0) {
                temDesconto = "S";
            }


        }
        if (temDesconto.equals("S") && !edtCodigo.getText().toString().equals("")) {

            layout_codmotor.setError("Retire o desconto dos itens, para utilizar código no motor");
            edtCodigo.setText("");
            temDesconto = "N";
            return false;

        } else {
            layout_codmotor.setError(null);
            temDesconto = "N";
            return true;
        }
    }



        public boolean  validarCodigoOrcamento(){
         String temDesconto = "N";
        for(int i = 0; i < orcamentoOrig.getItens().size(); i++){


            if(orcamentoOrig.getItens().get(i).getVlDescItem()>0){
                temDesconto = "S";
            }


        }


        if(temDesconto.equals("S") && !edtCodigo.getText().toString().equals("")){

            layout_codmotor.setError("Retire o desconto dos itens, para utilizar código no motor");
            edtCodigo.setText("");
            temDesconto="N";
            return false;

        } else {
            layout_codmotor.setError(null);
            temDesconto="N";
            return true;
        }





    }


    public void ConsultarPromocao(){

        dialog = new ProgressDialog(PopupCodigoMotor.this);
        dialog.setTitle("Verificando Promoções...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(){


            public void run() {

                try {


                    mList = helper.CalcularPromocao(edtCodigo.getText().toString().toUpperCase(),  pedidoOrig);


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupCodigoMotor.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupCodigoMotor.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            }).show();
                        }
                    });
                } finally {



                    String promocoes = "";

                    if(mList != null && mList.size()>0){



                        for(int i = 0 ; i< mList.size(); i++){

                            if(mList.get(i).getDescontoValorTotal() >0){
                                promocoes = promocoes + "Promoção: "+ mList.get(i).getNomePromocao() + " <br />" +
                                            "Valor : "+ real.format(mList.get(i).getDescontoValorTotal()) + " <br />" +
                                        "<br />";

                                pedidoOrig.setVlDescPromo(mList.get(i).getDescontoValorTotal());
                                pedidoOrig.setNomepromo(mList.get(i).getNomePromocao());
                                pedidoOrig.setMensagemPromo(mList.get(i).getMensagemPromocional().toString());
                                pedidoOrig.setCodigoPromo(edtCodigo.getText().toString().toUpperCase());
                                pedidoOrig.setIsMotor("S");

                                pedidoAPrazo.setVlDescPromo(mList.get(i).getDescontoValorTotal());
                                pedidoAPrazo.setNomepromo(mList.get(i).getNomePromocao());
                                pedidoAPrazo.setMensagemPromo(mList.get(i).getMensagemPromocional().toString());
                                pedidoAPrazo.setCodigoPromo(edtCodigo.getText().toString().toUpperCase());
                                pedidoAPrazo.setIsMotor("S");

                            }else{


                                for(int p = 0 ; p < mList.get(i).getProdutos().size(); p++) {

                                    ItemPedidos item = new ItemPedidos();
                                    item = pedidoOrig.getItens().get(mList.get(i).getProdutos().get(p).getPosicaoItem() - 1);

                                    item.setCodigoPromo(edtCodigo.getText().toString().toUpperCase());
                                    item.setNomePromo(mList.get(i).getNomePromocao());
                                    item.setVldescPromo(mList.get(i).getProdutos().get(p).getDesconto());
                                    item.setMensagemPromo(mList.get(i).getMensagemPromocional().toString());
                                    item.setIsMotor("S");
                                    item.setCodMotivo("");
                                    item.setMotivoDesc("");

                                    ItemPedidos itemAPrazo = pedidoAPrazo.getItens().get(mList.get(i).getProdutos().get(p).getPosicaoItem() - 1);
                                    itemAPrazo.setCodigoPromo(edtCodigo.getText().toString().toUpperCase());
                                    itemAPrazo.setNomePromo(mList.get(i).getNomePromocao());
                                    itemAPrazo.setVldescPromo(mList.get(i).getProdutos().get(p).getDesconto());
                                    itemAPrazo.setMensagemPromo(mList.get(i).getMensagemPromocional().toString());
                                    itemAPrazo.setIsMotor("S");
                                    itemAPrazo.setCodMotivo("");
                                    itemAPrazo.setMotivoDesc("");
;
                                    promocoes = promocoes + "Promoção: " + mList.get(i).getNomePromocao() + " <br />" +
                                            "Produto : " + String.valueOf(item.getCoditprod()) + " - " + item.getDescricao() + " <br />" +
                                            "Valor : " + real.format(mList.get(i).getProdutos().get(p).getDesconto()) + " <br />" +
                                            "<br />";

                                    calculaValorDesconto(item, mList.get(i).getProdutos().get(p).getDesconto(), mList.get(i).getProdutos().get(p).getPosicaoItem() - 1, false);
                                    calculaValorDesconto(itemAPrazo, mList.get(i).getProdutos().get(p).getDesconto(), mList.get(i).getProdutos().get(p).getPosicaoItem() - 1, true);

                                }
                            }


                        }





                        resultado = resultado + (promocoes + "" + " <br />" );


                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder alerta = new AlertDialog.Builder(PopupCodigoMotor.this);
                                alerta.setIcon(R.drawable.ic_card_info);
                                alerta.setCancelable(false);
                                alerta.setTitle("Promoções contempladas");
                                alerta.setMessage(Html.fromHtml(resultado));
                                alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        Intent intent = new Intent(PopupCodigoMotor.this, PopupPedFechamento.class);
                                        intent.putExtra("pedido", pedidoOrig);
                                        intent.putExtra("pedidoAPrazo", pedidoAPrazo);
                                        startActivity(intent);

                                    }
                                });


                                alertaDialog = alerta.create();
                                alertaDialog.show();

                            }
                        });

                        dialog.dismiss();



                    }else{
                        finish();
                        Intent intent = new Intent(PopupCodigoMotor.this, PopupPedFechamento.class);
                        intent.putExtra("pedido", pedidoOrig);
                        intent.putExtra("pedidoAPrazo", pedidoAPrazo);
                        startActivity(intent);

                    }




                }
            }


        }.start();

    }


    public void ConsultarPromocaoOrcamento(){

        dialog = new ProgressDialog(PopupCodigoMotor.this);
        dialog.setTitle("Verificando Promoções...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(){


            public void run() {

                try {

                    if(orcamentoOrig.getCodfil()==0){

                    }

                    mList = orcamentoHelper.CalcularPromocao(edtCodigo.getText().toString().toUpperCase(),  orcamentoOrig);


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupCodigoMotor.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupCodigoMotor.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            }).show();
                        }
                    });
                } finally {



                    String promocoes = "";

                    if(mList != null && mList.size()>0){



                        for(int i = 0 ; i< mList.size(); i++){

                            if(mList.get(i).getDescontoValorTotal() >0){
                                promocoes = promocoes + "Promoção: "+ mList.get(i).getNomePromocao() + " <br />" +
                                        "Valor : "+ real.format(mList.get(i).getDescontoValorTotal()) + " <br />" +
                                        "<br />";

                                orcamentoOrig.setVlDesconto(  mList.get(i).getDescontoValorTotal() );
                                orcamentoAPrazo.setVlDesconto( mList.get(i).getDescontoValorTotal() );

                                //pedidoOrig.setVlDescPromo(mList.get(i).getDescontoValorTotal());
                                //pedidoOrig.setNomepromo(mList.get(i).getNomePromocao());
                                //pedidoOrig.setMensagemPromo(mList.get(i).getMensagemPromocional().toString());
                                //pedidoOrig.setCodigoPromo(edtCodigo.getText().toString().toUpperCase());
                                //pedidoOrig.setIsMotor("S");

                            }else{


                                for(int p = 0 ; p < mList.get(i).getProdutos().size(); p++) {

                                    ItemOrcamento item = new ItemOrcamento();
                                    item = orcamentoOrig.getItens().get(mList.get(i).getProdutos().get(p).getPosicaoItem() - 1);

                                    ItemOrcamento itemAPrazo = new ItemOrcamento();
                                    itemAPrazo = orcamentoAPrazo.getItens().get(mList.get(i).getProdutos().get(p).getPosicaoItem() - 1);

                                    promocoes = promocoes + "Promoção: " + mList.get(i).getNomePromocao() + " <br />" +
                                            "Produto : " + String.valueOf(item.getCoditprod()) + " - " + item.getDescricao() + " <br />" +
                                            "Valor : " + real.format(mList.get(i).getProdutos().get(p).getDesconto()) + " <br />" +
                                            "<br />";

                                    calculaValorDescontoOrcamento(item, mList.get(i).getProdutos().get(p).getDesconto(), mList.get(i).getProdutos().get(p).getPosicaoItem() - 1, false);
                                    calculaValorDescontoOrcamento(itemAPrazo, mList.get(i).getProdutos().get(p).getDesconto(), mList.get(i).getProdutos().get(p).getPosicaoItem() - 1, true);

                                }
                            }


                        }





                        resultado = resultado + (promocoes + "" + " <br />" );


                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder alerta = new AlertDialog.Builder(PopupCodigoMotor.this);
                                alerta.setIcon(R.drawable.ic_card_info);
                                alerta.setCancelable(false);
                                alerta.setTitle("Promoções contempladas");
                                alerta.setMessage(Html.fromHtml(resultado));
                                alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                        Intent intent = new Intent(PopupCodigoMotor.this, PopupOrcFechamento.class);
                                        intent.putExtra("orcamento", orcamentoOrig);
                                        intent.putExtra("orcamentoAPrazo", orcamentoAPrazo);
                                        startActivity(intent);

                                    }
                                });


                                alertaDialog = alerta.create();
                                alertaDialog.show();

                            }
                        });

                        dialog.dismiss();



                    }else{

                        finish();
                        Intent intent = new Intent(PopupCodigoMotor.this, PopupOrcFechamento.class);
                        intent.putExtra("orcamento", orcamentoOrig);
                        intent.putExtra("orcamentoAPrazo", orcamentoAPrazo);
                        startActivity(intent);

                    }




                }
            }


        }.start();

    }


    public void calculaMargem(BigDecimal total , ItemPedidos itemPedido, int position, boolean aPrazo) {

        double vlEncargos;
        double vlIcms;
        double custo;
        double vlTotal;
        double margem;

        //vlTotal = (BigDecimal.valueOf(valorOriginal).subtract(valordodesconto).multiply(BigDecimal.valueOf(quantidade))).doubleValue() ;
        if(itemPedido.getSituacao().equals("IN")){
            vlEncargos =0.00d ;
            if (itemPedido.getIcmsRed() == 0) {
                vlIcms = 0.00d;
            } else {
                vlIcms = 0.00d;
            }
            custo = 0.00d;
        }else{
            vlEncargos = total.multiply(BigDecimal.valueOf(itemPedido.getEncargos())).divide(new BigDecimal("100")).doubleValue();
            if (itemPedido.getIcmsRed() == 0) {
                vlIcms = total.multiply(BigDecimal.valueOf(itemPedido.getIcms())).divide(new BigDecimal("100")).doubleValue();
            } else {
                vlIcms = total.subtract(total.multiply(BigDecimal.valueOf(itemPedido.getEncargos())).divide(new BigDecimal("100")).
                        multiply(BigDecimal.valueOf(itemPedido.getIcms() / 100))).doubleValue();
            }
            custo = itemPedido.getCusto() * itemPedido.getQtcomp();
        }


        margem = ((total.doubleValue() - vlEncargos - vlIcms - custo) / (total.doubleValue())) * 100;

        itemPedido.setPmargRent(margem);
        itemPedido.setCodigoPromo(edtCodigo.getText().toString().toUpperCase());

        if(aPrazo){

            List<ItemPedidos> itens = new ArrayList<>();
            itens = pedidoAPrazo.getItens();
            itens.set(position, itemPedido);

            pedidoAPrazo.setItens(itens);
            pedidoAPrazo.setIsMotor("S");

        }
        else {

            List<ItemPedidos> itens = new ArrayList<>();
            itens = pedidoOrig.getItens();
            itens.set(position, itemPedido);

            pedidoOrig.setItens(itens);
            pedidoOrig.setIsMotor("S");

        }



    }


    public void calculaMargemOrcamento(BigDecimal total , ItemOrcamento itemOrcamento, int position, boolean aPrazo) {

        double vlEncargos;
        double vlIcms;
        double custo;
        double vlTotal;
        double margem;

        //vlTotal = (BigDecimal.valueOf(valorOriginal).subtract(valordodesconto).multiply(BigDecimal.valueOf(quantidade))).doubleValue() ;
        if(itemOrcamento.getSituacao().equals("IN")){
            vlEncargos =0.00d ;
            if (itemOrcamento.getIcmsRed() == 0) {
                vlIcms = 0.00d;
            } else {
                vlIcms = 0.00d;
            }
            custo = 0.00d;
        }else{
            vlEncargos = total.multiply(BigDecimal.valueOf(itemOrcamento.getEncargos())).divide(new BigDecimal("100")).doubleValue();
            if (itemOrcamento.getIcmsRed() == 0) {
                vlIcms = total.multiply(BigDecimal.valueOf(itemOrcamento.getIcms())).divide(new BigDecimal("100")).doubleValue();
            } else {
                vlIcms = total.subtract(total.multiply(BigDecimal.valueOf(itemOrcamento.getEncargos())).divide(new BigDecimal("100")).
                        multiply(BigDecimal.valueOf(itemOrcamento.getIcms() / 100))).doubleValue();
            }
            custo = itemOrcamento.getCusto() * itemOrcamento.getQtcomp();
        }


        margem = ((total.doubleValue() - vlEncargos - vlIcms - custo) / (total.doubleValue())) * 100;
        itemOrcamento.setPmargRent(margem);

        if(aPrazo){

            List<ItemOrcamento> itens = new ArrayList<>();
            itens = orcamentoAPrazo.getItens();
            itens.set(position, itemOrcamento);
            orcamentoAPrazo.setItens(itens);

        }
        else {

            List<ItemOrcamento> itens = new ArrayList<>();
            itens = orcamentoOrig.getItens();
            itens.set(position, itemOrcamento);
            orcamentoOrig.setItens(itens);

        }


    }


    public void calculaValorDesconto(ItemPedidos item, double valorDescMotor, int position, boolean aPrazo) {

        final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        BigDecimal total = new BigDecimal("0.00");
        BigDecimal desc = new BigDecimal("0.00");
        BigDecimal valordodesconto = new BigDecimal("0.00");
        final DecimalFormat df = new DecimalFormat("0.00", symbols);
        final DecimalFormat dfdesconto = new DecimalFormat("0.0000", symbols);
        double quantidade = (item.getQtcomp());
        double percDesc = 0.00000d;

        valordodesconto =  BigDecimal.valueOf(valorDescMotor).divide(BigDecimal.valueOf(quantidade), MathContext.DECIMAL64);
        if (aPrazo){
            double valorItemAPrazo = ( item.getPrecoUnit() - valordodesconto.doubleValue() ) / 0.95;
            valordodesconto = BigDecimal.valueOf( item.getPrecoUnit() - valorItemAPrazo );
        }
        percDesc = valordodesconto.divide(BigDecimal.valueOf(item.getPrecoUnit()), MathContext.DECIMAL64).multiply(new BigDecimal("100.00")).doubleValue();

        BigDecimal totalProd = new BigDecimal("0.00");
        BigDecimal totalDesc = new BigDecimal("0.00");



        totalDesc = valordodesconto.multiply(BigDecimal.valueOf(quantidade));
        totalProd = BigDecimal.valueOf(item.getPrecoUnit()).multiply(BigDecimal.valueOf(quantidade));

        totalDesc = totalDesc.setScale(2,RoundingMode.FLOOR);
        totalProd = totalProd.setScale(2,RoundingMode.FLOOR);

        item.setDpi(percDesc);
        item.setVlDescItem(valordodesconto.setScale(3, RoundingMode.UP).doubleValue());


        calculaMargem(totalProd.subtract(totalDesc), item, position, aPrazo);

    }


    public void calculaValorDescontoOrcamento(ItemOrcamento item, double valorDescMotor, int position, boolean aPrazo) {

        final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        BigDecimal total = new BigDecimal("0.00");
        BigDecimal desc = new BigDecimal("0.00");
        BigDecimal valordodesconto = new BigDecimal("0.00");
        final DecimalFormat df = new DecimalFormat("0.00", symbols);
        final DecimalFormat dfdesconto = new DecimalFormat("0.0000", symbols);
        double quantidade = (item.getQtcomp());
        double percDesc = 0.00000d;

        valordodesconto =  BigDecimal.valueOf(valorDescMotor).divide(BigDecimal.valueOf(quantidade), MathContext.DECIMAL64);
        if (aPrazo){
            double valorItemAPrazo = ( item.getPrecoUnit() - valordodesconto.doubleValue() ) / 0.95;
            valordodesconto = BigDecimal.valueOf( item.getPrecoUnit() - valorItemAPrazo );
        }
        percDesc = valordodesconto.divide(BigDecimal.valueOf(item.getPrecoUnit()), MathContext.DECIMAL64).multiply(new BigDecimal("100.00")).doubleValue();

        BigDecimal totalProd = new BigDecimal("0.00");
        BigDecimal totalDesc = new BigDecimal("0.00");



        totalDesc = valordodesconto.multiply(BigDecimal.valueOf(quantidade));
        totalProd = BigDecimal.valueOf(item.getPrecoUnit()).multiply(BigDecimal.valueOf(quantidade));

        totalDesc = totalDesc.setScale(2,RoundingMode.FLOOR);
        totalProd = totalProd.setScale(2,RoundingMode.FLOOR);


        item.setDpi(percDesc);
        item.setVlDescItem(valordodesconto.doubleValue());
        item.setCodMotivo("");
        item.setMotivoDesc("");


        calculaMargemOrcamento(totalProd.subtract(totalDesc), item, position, aPrazo);

    }


}
