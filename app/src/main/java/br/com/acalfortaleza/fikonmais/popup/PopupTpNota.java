package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.HardwareRenderer;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.ConsultaActivity;
import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.FikonVendasClient;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.TpNotas;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupTpNota extends Activity {

    private SpinnerDialog spinnerDialog;
    private TextInputEditText edtTpnota;
    private Button btn_iniciar;
    private PedidosHelper helper;
    private List<TpNotas>mList;
    private ProgressDialog dialog;
    private ArrayList<String> listaTpnota = new ArrayList<>();
    private Handler handler;
    private List<String> retorno;
    private Pedidos pedido;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private Button btn_fechar;
    private TextInputLayout layout_tpnota;
    private String tpnota_selecionada = "N";
    private String numorc = "";

    private boolean isServico = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_tipodenota);

        pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
        numorc = getIntent().getStringExtra("numorc");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("pedidos");

        isServico = false;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .3));

        helper = new PedidosHelper();

        handler = new Handler();
        edtTpnota = findViewById(R.id.pop_ped_edtTpnota);
        btn_iniciar = findViewById(R.id.pop_ped_btn_iniciarr);
        btn_fechar = findViewById(R.id.pop_ped_tpnota_btnFechar);
        layout_tpnota = findViewById(R.id.pop_ped_layoutTpNota);

        btn_fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        ConsultarCombobox();




        spinnerDialog = new SpinnerDialog(PopupTpNota.this, listaTpnota, "Informe o Tipo de nota");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                edtTpnota.setText(item);
                TpNotas t = new TpNotas();
                t.setCodTpNota(mList.get(position).getCodTpNota());
                t.setDescricao(mList.get(position).getDescricao());
                pedido.setTpnota(t);
                tpnota_selecionada = "S";

            }
        });


        edtTpnota.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtTpnota.getRight() - edtTpnota.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });


        btn_iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validarTpnota()) {

                    InicializarPedido(pedido.getCodvendr(),
                            pedido.getCodfil(),
                            pedido.getTpnota().getCodTpNota(),
                            String.valueOf(pedido.getItens().get(0).getCoditprod())
                                    .substring(0, String.valueOf(pedido.getItens().get(0).getCoditprod()).length() - 1),
                            pedido.getItens().get(0).getQtcomp(),
                            pedido.getItens().get(0).getPrecoUnit());

                }

            }
        });




    }


    public void ConsultarCombobox (){

        dialog = new ProgressDialog(PopupTpNota.this);
        dialog.setTitle("Aguarde...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(){


            public void run() {

                try {


                    mList =   helper.GetTpNotas();


                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            for(int i = 0 ; i < pedido.getItens().size(); i++){
                                if(pedido.getItens().get(i).getSituacao().equals("SV")){

                                    isServico = true;

                                }
                            }


                            if(mList != null && mList.size()>0){

                                List<TpNotas> retorno = new ArrayList<>();

                                for (TpNotas t : mList) {
                                    if(isServico){
                                        if(t.getCodTpNota()==536){
                                            listaTpnota.add(t.toString());
                                            retorno.add(t);

                                        }

                                    }else{
                                        if(t.getCodTpNota()!=536){
                                            listaTpnota.add(t.toString());
                                            retorno.add(t);
                                        }
                                    }



                                }
                                mList = retorno;
                                edtTpnota.setText(retorno.get(0).toString());
                                TpNotas tp = new TpNotas();
                                tp.setCodTpNota(retorno.get(0).getCodTpNota());
                                tp.setDescricao(retorno.get(0).getDescricao());
                                pedido.setTpnota(tp);
                                tpnota_selecionada = "S";


                            }


                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupTpNota.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                            new AlertDialog.Builder(PopupTpNota.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

    public void InicializarPedido (final int Codvendr, final int Codfil, final int Tpnota, final String Coditprod, final double Qtcomp, final double Precounit){

        dialog = new ProgressDialog(PopupTpNota.this);
        dialog.setTitle("Inicializando...");
        dialog.setCancelable(false);
        dialog.show();

        new Thread(){

            public void run() {

                try {

                    if (FikonVendasClient.ativo()) {
                        // Na Fikon o pedido nasce da transformação do orçamento, que já
                        // liga os dois documentos (não há AtualizarOrc).
                        long chavePedido = FikonVendasClient.iniciarPedido(numorc, pedido);
                        retorno = chavePedido > 0
                                ? java.util.Arrays.asList(String.valueOf(chavePedido), "0")
                                : null;
                    } else {
                        retorno =   helper.InicializaPedido( Codvendr, Codfil,  Tpnota,  Coditprod,  Qtcomp,  Precounit);

                        if(retorno!=null) {
                            helper.AtualizarOrc(numorc, retorno.get(0));
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if(pedido != null && retornoPedidoValido(retorno) ){

                                Double numped = parseDoubleSeguro(retorno.get(0));
                                Double numdav = retorno.size() > 1 ? parseDoubleSeguro(retorno.get(1)) : 0d;

                                if (numped == null) {
                                    Log.w("OrcGerarPedidoDebug", "Retorno de inicializacao invalido para numped. retorno=" + retorno);
                                    Toast.makeText(PopupTpNota.this, "Falha ao inicializar o pedido, tente novamente!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (numdav == null) {
                                    Log.w("OrcGerarPedidoDebug", "Retorno com numdav vazio. Seguindo com zero. retorno=" + retorno);
                                    numdav = 0d;
                                }

                                pedido.setNumpedven(numped);
                                pedido.setNumdav(numdav);
                                pedido.setCodStatus(9);
                                ConsultaActivity.pedido = pedido;
                                myRef.child(String.valueOf(pedido.getCodvendr())).setValue(pedido);

                                // --- INÍCIO DA CORREÇÃO ---
                                // 1. Cria o novo Intent para a Pedidos_LancamentoActivity.
                                Intent intent = new Intent(PopupTpNota.this, Pedidos_LancamentoActivity.class);

                                // 2. [A MÁGICA] Copia TODOS os "extras" do Intent que abriu esta tela.
                                // Isso garante que o objeto 'pedido' (já modificado) e a lista 'servicos_adicionais_ids'
                                // sejam repassados para a próxima tela.
                                //intent.putExtras(getIntent());

                                // 3. Sobrescreve o objeto 'pedido' no novo Intent com a versão atualizada
                                //    (que agora tem o Numpedven).
                                intent.putExtra("pedido", pedido);

                                // 4. Adiciona o 'orcamento' como extra para a lógica na Pedidos_LancamentoActivity funcionar.
                                //intent.putExtra("orcamento", true);

                                // 5. Inicia a próxima activity.
                                startActivity(intent);
                                // --- FIM DA CORREÇÃO ---

                                finish();

                            }else{
                                String motivo = FikonVendasClient.ativo() ? FikonVendasClient.ultimoErro() : null;
                                Toast.makeText(PopupTpNota.this,
                                        motivo == null || motivo.trim().isEmpty()
                                                ? "Falha ao inicializar o pedido, tente novamente!"
                                                : "Falha ao inicializar o pedido: " + motivo,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupTpNota.this).setTitle("Falha de Conexão").setMessage("Verifique a sua conexão com a internet e tente novamente!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    finishAffinity();
                                }
                            }).show();
                        }
                    });
                } catch (ParseException | XmlPullParserException | IOException e) {
                    e.printStackTrace();
                    // Adicionei o tratamento de erro que estava faltando
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(PopupTpNota.this).setTitle("Falha de Conexão").setMessage("ocorreu um problema com o WebService, por gentileza informar ao suporte!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finishAffinity();
                                }
                            }).show();
                        }
                    });
                } finally {
                    if (dialog.isShowing()) { // Garante que o dialog só será dispensado se estiver mostrando
                        dialog.dismiss();
                    }
                }
            }

        }.start();


    }


    public Boolean validarTpnota() {


        if (tpnota_selecionada.equals("N")) {

            layout_tpnota.setError("Selecione uma opção");
            return false;

        } else {
            layout_tpnota.setError(null);
            return true;
        }
    }

    private boolean retornoPedidoValido(List<String> retorno) {
        return retorno != null
                && !retorno.isEmpty()
                && retorno.get(0) != null
                && !retorno.get(0).trim().isEmpty()
                && !"Pedido não inicializado!".equalsIgnoreCase(retorno.get(0).trim());
    }

    private Double parseDoubleSeguro(String valor) {
        if (valor == null) {
            return null;
        }

        String valorTratado = valor.trim();
        if (valorTratado.isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(valorTratado);
        } catch (NumberFormatException ex) {
            Log.e("OrcGerarPedidoDebug", "Falha ao converter retorno numerico: " + valorTratado, ex);
            return null;
        }
    }
}
