package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.AgendamentoRetiraActivity;
import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.AgendamentoRetiraAdapter;
import br.com.acalfortaleza.fikonmais.adapters.FreteAdapter;
import br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Enderecos;
import br.com.acalfortaleza.fikonmais.models.Fretes;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupEnderecoEntrega extends Activity {


    private TextInputLayout layout_endereco;
    private TextInputEditText edtEndereco;
    private Button btnOk;
    private static HashMap<String, String> usuarioLogado;
    private SpinnerDialog spinnerDialog;
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private Button btModalidade;
    private List<Enderecos> mList;
    private SpinnerDialog spinnerDialogFrete;
    private double freteSelecionado;
    private int codmodal;
    private ArrayList<String>  listaEnderecos;
    private List<Fretes> mListFrete = new ArrayList<>();
    private FreteAdapter adapter;
    private RecyclerView mRecyclerView;
    private ArrayList<String>  dadosFrete;
    private LoginHelper loginHelper;
    private int Codfil = 0;
    private int pos = 0;
    private TextView txtVlfrete;

    private Boolean isOrcamento = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_endereco_entrega);


        mList = (List<Enderecos>) getIntent().getSerializableExtra("endereco");

        isOrcamento = getIntent().getBooleanExtra("isOrcamento",false);

        loginHelper = new LoginHelper(PopupEnderecoEntrega.this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();

        Codfil = Integer.parseInt(usuarioLogado.get("codfil"));

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .9), (int) (height * .7));


        layout_endereco = findViewById(R.id.pop_end_layoutendereco);
        edtEndereco = findViewById(R.id.pop_end_edtEndereco);

        txtVlfrete = findViewById(R.id.pop_end_VlFrete);
        btnOk = findViewById(R.id.pop_end_btn_Alterar);
        mRecyclerView = findViewById(R.id.pop_end_listaFrete);
        txtVlfrete.setVisibility(View.INVISIBLE);


        btnOk.setEnabled(false);
        btnOk.setBackgroundResource(R.drawable.backgroundbuttondisabled);



        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(PopupEnderecoEntrega.this));
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        if(Codfil == 900 || Codfil == 11) {

                            freteSelecionado = mListFrete.get(position).getVlfrete();
                            codmodal = mListFrete.get(position).getCodFrete();
                            txtVlfrete.setVisibility(View.VISIBLE);
                            if(mListFrete.get(position).getModalidade().trim().equals("RODOVIARIO")){
                                txtVlfrete.setText("FRETE: " + real.format(freteSelecionado));
                            }else{
                                txtVlfrete.setText("VALOR POR TONELADA: " + real.format(freteSelecionado));
                            }

                            btnOk.setEnabled(true);
                            btnOk.setBackgroundResource(R.drawable.backgroudbuttom);
                        }else if(Codfil != 900 && Codfil != 11 && !mListFrete.get(position).getModalidade().trim().equals("RODOVIARIO")){
                            Toast.makeText(PopupEnderecoEntrega.this,"Escolha a opção de frete RODOVIARIO!",Toast.LENGTH_SHORT).show();
                        }else{
                            freteSelecionado = mListFrete.get(position).getVlfrete();
                            codmodal = mListFrete.get(position).getCodFrete();
                            txtVlfrete.setVisibility(View.VISIBLE);
                            txtVlfrete.setText("VALOR POR TONELADA: " + real.format(freteSelecionado));
                            btnOk.setEnabled(true);
                            btnOk.setBackgroundResource(R.drawable.backgroudbuttom);
                        }



                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );



        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOrcamento){
                    Orcamento_LancamentoActivity.orcamento.setVlFreteSelecionado(freteSelecionado);
                    Orcamento_LancamentoActivity.atualizaTotais(Orcamento_LancamentoActivity.orcamento.getItens());
                }else{
                    //Pedidos_LancamentoActivity.pedido.setVlFrete(mList.get(pos).getFretes().getFrete);
                    Pedidos_LancamentoActivity.pedido.setVlFreteSelecionado(freteSelecionado);
                    Pedidos_LancamentoActivity.pedido.setCodmodal(codmodal);
                }


                finish();

            }
        });


        if(isOrcamento){

        }else{


            edtEndereco.setText(mList.get(Pedidos_LancamentoActivity.pedido.getEndent()).toString());

            if(mList.get(Pedidos_LancamentoActivity.pedido.getEndent()).getFretes() != null && mList.get(Pedidos_LancamentoActivity.pedido.getEndent()).getFretes().size()>0 ){
                mListFrete = mList.get(Pedidos_LancamentoActivity.pedido.getEndent()).getFretes();
                adapter = new FreteAdapter(PopupEnderecoEntrega.this,mListFrete);
                mRecyclerView.setAdapter(adapter);
            }

        }


        //Pedidos_LancamentoActivity.pedido.setVlFreteSelecionado(0.00d);





        listaEnderecos = new ArrayList<>();
        if(mList != null && mList.size()>0) {
            for (Enderecos e : mList) {
                listaEnderecos.add(e.toString());
            }
        }


        spinnerDialog = new SpinnerDialog(PopupEnderecoEntrega.this, listaEnderecos, "Selecione o endereço ");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {

                if(isOrcamento){
                    edtEndereco.setText(item);

                    //mudar essa linha de zero para o valor do frete
                    //Pedidos_LancamentoActivity.pedido.setVlFreteSelecionado(mList.get(position).getVlfrete());
                    Orcamento_LancamentoActivity.orcamento.setVlFreteSelecionado(0.00d);
                    if(mList.get(position).getFretes() !=null && mList.get(position).getFretes().size()>0 ){
                        mListFrete = mList.get(position).getFretes();
                        adapter = new FreteAdapter(PopupEnderecoEntrega.this, mListFrete);
                        mRecyclerView.setAdapter(adapter);
                    }else{
                        btnOk.setEnabled(false);
                        btnOk.setBackgroundResource(R.drawable.backgroundbuttondisabled);
                        freteSelecionado = 0.00d;
                        codmodal= 0;
                        txtVlfrete.setVisibility(View.INVISIBLE);
                        txtVlfrete.setText("FRETE: " + real.format(freteSelecionado));
                        mListFrete = new ArrayList<>();
                        adapter = new FreteAdapter(PopupEnderecoEntrega.this, mListFrete);
                        mRecyclerView.setAdapter(adapter);
                        Toast.makeText(PopupEnderecoEntrega.this,"Endereço não tem rota cadastrada, solicite o cadastro na Expedição!",Toast.LENGTH_LONG).show();
                    }
                }else{


                    edtEndereco.setText(item);
                    Pedidos_LancamentoActivity.pedido.setPraca(mList.get(position).getPraca());
                    Pedidos_LancamentoActivity.pedido.setEndent(mList.get(position).getCodend());
                    Pedidos_LancamentoActivity.pedido.setEndfat(mList.get(position).getCodend());
                    Pedidos_LancamentoActivity.pedido.setEndcob(0);
                    //mudar essa linha de zero para o valor do frete
                    //Pedidos_LancamentoActivity.pedido.setVlFreteSelecionado(mList.get(position).getVlfrete());
                    Pedidos_LancamentoActivity.pedido.setVlFreteSelecionado(0.00d);
                    if(mList.get(position).getFretes() !=null && mList.get(position).getFretes().size()>0 ){
                        mListFrete = mList.get(position).getFretes();
                        adapter = new FreteAdapter(PopupEnderecoEntrega.this, mListFrete);
                        mRecyclerView.setAdapter(adapter);
                    }else{
                        btnOk.setEnabled(false);
                        btnOk.setBackgroundResource(R.drawable.backgroundbuttondisabled);
                        freteSelecionado = 0.00d;
                        codmodal= 0;
                        txtVlfrete.setVisibility(View.INVISIBLE);
                        txtVlfrete.setText("FRETE: " + real.format(freteSelecionado));
                        mListFrete = new ArrayList<>();
                        adapter = new FreteAdapter(PopupEnderecoEntrega.this, mListFrete);
                        mRecyclerView.setAdapter(adapter);
                        Toast.makeText(PopupEnderecoEntrega.this,"Endereço não tem rota cadastrada, solicite o cadastro na Expedição!",Toast.LENGTH_LONG).show();
                    }

                }


            }
        });






        edtEndereco.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtEndereco.getRight() - edtEndereco.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });




    }
}