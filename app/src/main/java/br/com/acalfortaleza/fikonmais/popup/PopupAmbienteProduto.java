package br.com.acalfortaleza.fikonmais.popup;

import static com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_OUTLINE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.itextpdf.text.pdf.parser.Line;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.AmbienteProdutoAdapter;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.models.AmbienteProduto;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.NomeAmbiente;

public class PopupAmbienteProduto extends Activity {

    //private Context appContext;

    private ImageButton btnFechar;
    private ImageButton btnAdicionar;
    private ImageButton btnDeletar;
    private ImageButton btnEditar;
    private ImageButton btnSalvar;
    private RecyclerView recyclerView;
    private TextView txtDescricaoProduto;
    private TextView txtQuantidadeTotal;
    private TextView txtSomatorioAmbientes;

    private Handler handler;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;

    private AmbienteProdutoAdapter adapter;

    private ItemOrcamento itemOrcamento;
    private double numorc;
    private double somatorioAmbientes;
    public List<AmbienteProduto> listAmbientesProduto;
    public List<NomeAmbiente> listNomesAmbientes;
    private List<String> descricaoAmbientes;
    private List<Long> idAmbientes;
    private int indiceListView;
    private int indiceItemOrcamento;

    private EditText edtBusca;
    private ListView listViewBusca;
    private LinearLayout layoutBusca;
    private Dialog dialogBusca;

    private EditText txtAmbiente;
    private EditText edtQuantidade;
    private LinearLayout layoutAddEdit;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_ambiente_produto);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getWindow().setLayout((int) (displayMetrics.widthPixels * .9), (int) (displayMetrics.heightPixels * .9));

        handler = new Handler();

        btnFechar = findViewById(R.id.popup_ambienteProduto_btnFechar);
        btnAdicionar = findViewById(R.id.popup_ambienteProduto_btnAdicionar);
        btnDeletar = findViewById(R.id.popup_ambienteProduto_btnDeletar);
        btnEditar = findViewById(R.id.popup_ambienteProduto_btnEditar);
        btnSalvar = findViewById(R.id.popup_ambienteProduto_btnSalvar);

        recyclerView = findViewById(R.id.popup_ambienteProduto_recyclerAmbientes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PopupAmbienteProduto.this));

        txtDescricaoProduto = findViewById(R.id.popup_ambienteProduto_txtDescricaoProduto);
        txtQuantidadeTotal = findViewById(R.id.popup_ambienteProduto_txtQuantidadeTotal);
        txtSomatorioAmbientes = findViewById(R.id.popup_ambienteProduto_txtSomatorioAmbientes);

        itemOrcamento = (ItemOrcamento) getIntent().getSerializableExtra("itemOrcamento");
        numorc = getIntent().getDoubleExtra("numorc",0);
        indiceItemOrcamento = getIntent().getIntExtra("indiceItemOrcamento",0);
        txtDescricaoProduto.setText(itemOrcamento.getDescricao());
        txtQuantidadeTotal.setText("Quantidade Total: " + itemOrcamento.getQtcomp());

        idAmbientes = new ArrayList<>();
        descricaoAmbientes = new ArrayList<>();

        listNomesAmbientes = Orcamento_LancamentoActivity.listNomesAmbientes;

        for (NomeAmbiente nomeAmbiente: listNomesAmbientes) {

            idAmbientes.add(nomeAmbiente.getId());
            descricaoAmbientes.add(nomeAmbiente.getDescricao());

        }

        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarAmbiente();
            }
        });

        btnDeletar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletarAmbiente();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarAmbiente();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarAmbientes();
            }
        });

        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fecharTela();
            }
        });

        obterAmbientesProduto();

    }

    private void fecharDialogs(){

        try {
            if(progressDialog!=null && progressDialog.isShowing()){
                progressDialog.dismiss();
            }
        }
        catch (Exception ex){}

        try {
            if(alertDialog!=null && alertDialog.isShowing()){
                alertDialog.dismiss();
            }
        }
        catch (Exception ex){}

    }

    private void obterAmbientesProduto(){

        try {

            listAmbientesProduto = new ArrayList<>();

            if(Orcamento_LancamentoActivity.orcamento.getItens()!=null && Orcamento_LancamentoActivity.orcamento.getItens().size()>0){

                for(ItemOrcamento item: Orcamento_LancamentoActivity.orcamento.getItens()){

                    if(
                        item.getId()==itemOrcamento.getId()
                        && item.getCoditprod()==itemOrcamento.getCoditprod()
                        && item.getListAmbientesProdutos()!=null
                        && item.getListAmbientesProdutos().size()>0
                    ){

                        for (AmbienteProduto ambienteProduto: item.getListAmbientesProdutos()) {

                            ambienteProduto.setId(UUID.randomUUID().toString() + UUID.randomUUID().toString());
                            listAmbientesProduto.add(ambienteProduto);

                        }

                    }

                }

            }

            popularRecyclerView(null, 0);

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void popularRecyclerView(String tipo, int posicao){

        try {

            if(adapter==null && listAmbientesProduto!=null && listAmbientesProduto.size()>0){
                adapter = new AmbienteProdutoAdapter(
                    PopupAmbienteProduto.this,
                        listNomesAmbientes,
                        listAmbientesProduto
                    );
            }

            if(adapter!=null && recyclerView.getAdapter()==null){
                recyclerView.setAdapter(adapter);
                adapter.notifyItemRangeChanged(0,listAmbientesProduto.size());
            }

            if(adapter!=null){
                adapter.desmarcarSelecionados();
            }

            if(tipo!=null){

                switch (tipo){

                    case "adicionar":
                        adapter.notifyItemInserted(posicao);
                        adapter.notifyItemRangeChanged(0,listAmbientesProduto.size());
                        break;

                    case "deletar":
                        adapter.notifyItemRemoved(posicao);
                        adapter.notifyItemRangeChanged(0,listAmbientesProduto.size());
                        break;

                    case "editar":
                        adapter.notifyItemChanged(posicao);
                        adapter.notifyItemRangeChanged(0,listAmbientesProduto.size());
                        break;

                }

            }

            if(listAmbientesProduto.size()>0){
                recyclerView.scrollToPosition(listAmbientesProduto.size()-1);
            }
            else{
                recyclerView.scrollToPosition(RecyclerView.NO_POSITION);
            }

            calcularSomatorio();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void criarModalAddEdit(){

        indiceListView = -1;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        layoutBusca = new LinearLayout(this);
        layoutBusca.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) (displayMetrics.widthPixels * .8),
                (int) (displayMetrics.heightPixels * .6)
        );
        layoutBusca.setLayoutParams(layoutParams);

        edtBusca = new EditText(this);
        edtBusca.setHint("Buscar Ambiente");
        edtBusca.setHeight(150);

        listViewBusca = new ListView(this);
        ArrayAdapter<String> adapterBusca = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, descricaoAmbientes);
        listViewBusca.setAdapter(adapterBusca);

        layoutBusca.addView(edtBusca);
        layoutBusca.addView(listViewBusca);

        dialogBusca = new Dialog(PopupAmbienteProduto.this);

        //--------------------------------------------------------------------------------------

        layoutAddEdit = new LinearLayout(this);
        layoutAddEdit.setOrientation(LinearLayout.VERTICAL);
        layoutParams = new LinearLayout.LayoutParams(
                (int) (displayMetrics.widthPixels * .9),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutAddEdit.setLayoutParams(layoutParams);
        layoutAddEdit.setPadding(20,10,20,10);

        txtAmbiente = new EditText(this);
        txtAmbiente.setHint("Escolha um Ambiente");
        txtAmbiente.setTextSize(18);
        txtAmbiente.setHeight(150);
        txtAmbiente.setFocusable(false);

        edtQuantidade = new EditText(this);
        edtQuantidade.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        edtQuantidade.setHint("Quantidade");
        edtQuantidade.setHeight(150);
        edtQuantidade.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edtQuantidade.setKeyListener(DigitsKeyListener.getInstance("0123456789."));

        layoutAddEdit.addView(txtAmbiente);
        layoutAddEdit.addView(edtQuantidade);

        txtAmbiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBusca.setCancelable(false);
                dialogBusca.setContentView(layoutBusca);
                dialogBusca.getWindow().setLayout(900,900);
                dialogBusca.show();
            }
        });

        edtBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterBusca.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        listViewBusca.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                indiceListView = descricaoAmbientes.indexOf(adapterBusca.getItem(position));
                txtAmbiente.setText(descricaoAmbientes.get(indiceListView));
                dialogBusca.dismiss();
            }
        });

    }

    private void calcularSomatorio(){

        try{

            somatorioAmbientes = 0;

            if(listAmbientesProduto!=null && listAmbientesProduto.size()>0) {

                for (AmbienteProduto ambienteProduto : listAmbientesProduto) {

                    somatorioAmbientes += ambienteProduto.getQuantidade();

                }

            }

            double quantidadeRestante = itemOrcamento.getQtcomp() - somatorioAmbientes;

            txtSomatorioAmbientes.setText("Somatório\nAmbientes:\n" + somatorioAmbientes);
            txtQuantidadeTotal.setText("Quantidade Total: " + itemOrcamento.getQtcomp() + " | " + "Restante: " + quantidadeRestante);

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void adicionarAmbiente(){

        try{

            criarModalAddEdit();

            Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,0);

            alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Adicionar Ambiente")
                    .setView(layoutAddEdit)
                    .setCancelable(false)
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            edtQuantidade.onEditorAction(EditorInfo.IME_ACTION_DONE);
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            edtQuantidade.onEditorAction(EditorInfo.IME_ACTION_DONE);

                            if(txtAmbiente.getText().toString().trim().equals("")){

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast.setText("Informe o ambiente");
                                        toast.show();
                                        alertDialog.show();
                                    }
                                });

                                return;

                            }

                            if(
                                edtQuantidade.getText().toString().trim().equals("") ||
                                Double.parseDouble(edtQuantidade.getText().toString().replace(",","."))==0
                            ){

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast.setText("Informe a quantidade");
                                        toast.show();
                                        alertDialog.show();
                                    }
                                });

                                return;

                            }

                            for (AmbienteProduto ambienteProduto: listAmbientesProduto) {

                                if(ambienteProduto.getIdAmbiente()==idAmbientes.get(indiceListView)){

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            toast.setText("Ambiente " + descricaoAmbientes.get(indiceListView) + " já foi adicionado anteriormente. Escolha outro.");
                                            toast.show();
                                            alertDialog.show();
                                        }
                                    });

                                    return;

                                }

                            }

                            Double quantidadeAmbiente = Double.parseDouble(edtQuantidade.getText().toString().replace(",","."));
                            if((somatorioAmbientes + quantidadeAmbiente) > itemOrcamento.getQtcomp()){

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        toast.setText("Somatório dos ambientes ultrapassa a quantidade total");
                                        toast.show();
                                        alertDialog.show();
                                    }
                                });

                                return;

                            }

                            if(!validarConversao()){
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertDialog.show();
                                    }
                                });
                                return;
                            }

                            AmbienteProduto ambienteProduto = new AmbienteProduto();
                            ambienteProduto.setId(UUID.randomUUID().toString() + UUID.randomUUID().toString());
                            ambienteProduto.setNumorc(numorc);
                            ambienteProduto.setCoditprod(itemOrcamento.getCoditprod());
                            ambienteProduto.setIdAmbiente( idAmbientes.get(indiceListView) );
                            ambienteProduto.setQuantidade( Double.parseDouble(edtQuantidade.getText().toString().replace(",",".")) );
                            ambienteProduto.setIdItemOrcamento(itemOrcamento.getId());
                            ambienteProduto.setQtdItemOrcamento(itemOrcamento.getQtcomp());

                            listAmbientesProduto.add(ambienteProduto);
                            System.out.println("lista: " + new Gson().toJson(listAmbientesProduto));

                            popularRecyclerView("adicionar", listAmbientesProduto.size()-1);
                            atualizarAmbientesAlterados(true);

                            dialog.dismiss();

                        }
                    })
                    .create();
            alertDialog.show();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void deletarAmbiente(){

        try {

            if(adapter!=null && adapter.idSelecionado!=null){

                if(listAmbientesProduto!=null && listAmbientesProduto.size()>0){

                    int posicao = RecyclerView.NO_POSITION;

                    for (AmbienteProduto ambienteProduto: listAmbientesProduto) {

                        if(ambienteProduto.getId() == adapter.idSelecionado){
                            posicao = listAmbientesProduto.indexOf(ambienteProduto);
                            listAmbientesProduto.remove(ambienteProduto);
                        }

                    }

                    if(posicao!=RecyclerView.NO_POSITION){
                        popularRecyclerView("deletar", posicao);
                        atualizarAmbientesAlterados(true);
                    }

                }

            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void editarAmbiente(){

        try{

            if(adapter!=null && adapter.idSelecionado!=null){

                int posicaoLista = -1;
                long idAmbienteAtual = -1;

                criarModalAddEdit();

                for (AmbienteProduto ambienteProduto: listAmbientesProduto) {

                    if(ambienteProduto.getId()==adapter.idSelecionado){

                        posicaoLista = listAmbientesProduto.indexOf(ambienteProduto);
                        idAmbienteAtual = ambienteProduto.getIdAmbiente();

                        for (NomeAmbiente nomeAmbiente: listNomesAmbientes) {

                            if(ambienteProduto.getIdAmbiente()==nomeAmbiente.getId()){

                                txtAmbiente.setText(nomeAmbiente.getDescricao());
                                edtQuantidade.setText(String.valueOf(ambienteProduto.getQuantidade()));
                                indiceListView = listNomesAmbientes.indexOf(nomeAmbiente);
                                break;

                            }

                        }

                        break;

                    }

                }

                Toast toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,0);

                final int finalPosicaoLista = posicaoLista;
                final long finalIdAmbienteAtual = idAmbienteAtual;

                alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Editar Ambiente")
                        .setView(layoutAddEdit)
                        .setCancelable(false)
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                edtQuantidade.onEditorAction(EditorInfo.IME_ACTION_DONE);
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Atualizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                edtQuantidade.onEditorAction(EditorInfo.IME_ACTION_DONE);

                                if(txtAmbiente.getText().toString().trim().equals("")){

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            toast.setText("Informe o ambiente");
                                            toast.show();
                                            alertDialog.show();
                                        }
                                    });

                                    return;

                                }

                                if(
                                    edtQuantidade.getText().toString().trim().equals("") ||
                                    Double.parseDouble(edtQuantidade.getText().toString().replace(",","."))==0
                                ){

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            toast.setText("Informe a quantidade");
                                            toast.show();
                                            alertDialog.show();
                                        }
                                    });

                                    return;

                                }

                                for (AmbienteProduto ambienteProduto: listAmbientesProduto) {

                                    if(
                                        ambienteProduto.getIdAmbiente() == idAmbientes.get(indiceListView)
                                        && ambienteProduto.getIdAmbiente() != finalIdAmbienteAtual
                                    ){

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                toast.setText("Ambiente " + descricaoAmbientes.get(indiceListView) + " já foi adicionado");
                                                toast.show();
                                                alertDialog.show();
                                            }
                                        });

                                        return;

                                    }

                                }

                                Double quantidadeAmbiente = Double.parseDouble(edtQuantidade.getText().toString().replace(",","."));
                                if((somatorioAmbientes - listAmbientesProduto.get(finalPosicaoLista).getQuantidade() + quantidadeAmbiente) > itemOrcamento.getQtcomp()){

                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            toast.setText("Somatório dos ambientes ultrapassa a quantidade total");
                                            toast.show();
                                            alertDialog.show();
                                        }
                                    });

                                    return;

                                }

                                if(!validarConversao()){
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.show();
                                        }
                                    });
                                    return;
                                }

                                listAmbientesProduto.get(finalPosicaoLista).setIdAmbiente(idAmbientes.get(indiceListView));
                                listAmbientesProduto.get(finalPosicaoLista).setQuantidade(quantidadeAmbiente);
                                System.out.println("lista: " + new Gson().toJson(listAmbientesProduto));

                                popularRecyclerView("editar", finalPosicaoLista);
                                atualizarAmbientesAlterados(true);

                                dialog.dismiss();

                            }
                        })
                        .create();

                alertDialog.show();

            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private void fecharTela(){
        finish();
    }

    private void salvarAmbientes(){

        try {

            fecharDialogs();

            if(somatorioAmbientes>itemOrcamento.getQtcomp()){

                alertDialog = new AlertDialog.Builder(this)
                        .setTitle("Quantidades erradas")
                        .setMessage("O somatório dos ambientes deve ser igual ou inferior à quantidade total")
                        .setCancelable(false)
                        .setPositiveButton("Entendi", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();

                return;

            }

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Salvando Ambientes");
            progressDialog.setCancelable(false);
            progressDialog.show();


            handler.post(new Runnable() {
                @Override
                public void run() {

                    PopupItemOrc.itemOrcamento.setListAmbientesProdutos(listAmbientesProduto);
                    //itemOrcamento.setListAmbientesProdutos(listAmbientesProduto);
                    Orcamento_LancamentoActivity.atualizaAmbientesProdutos(listAmbientesProduto, indiceItemOrcamento);
                    progressDialog.dismiss();
                    finish();

                }
            });


        }
        catch (Exception ex){
            ex.printStackTrace();
        }


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

        Toast toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL,0,0);

        if(!itemOrcamento.getNcm().equals("32041700") && !itemOrcamento.getNcm().equals("32064990")) {
            if (!qtdDigitada.equals(qtdCalculada)) {
                if (itemOrcamento.getQtemb() == 1) {
                    toast.setText("Produto vendido em unidade, qtd alterada para: " + (df.format(itemOrcamento.getQtemb() * qtdArredondado)));
                    toast.show();
                } else {
                    toast.setText("Produto vendido em caixa, qtd alterada para: " + (df.format(itemOrcamento.getQtemb() * qtdArredondado)));
                }
                edtQuantidade.setText(df.format(itemOrcamento.getQtemb() * qtdArredondado));
                return false;

            } else {
                return true;
            }
        }else{
            return true;
        }

    }

    public void atualizarAmbientesAlterados(boolean alterado){

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("ambientesAlterados", Context.MODE_PRIVATE);
        String chave = String.valueOf( (long)numorc );
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(chave, alterado);
        editor.apply();

        Orcamento_LancamentoActivity.ambientesAlterados = alterado;

    }


}
