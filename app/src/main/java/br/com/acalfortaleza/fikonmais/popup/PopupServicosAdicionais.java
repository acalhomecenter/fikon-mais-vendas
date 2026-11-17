package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.FreteServicoAdapter;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.FreteServico;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PopupServicosAdicionais extends Activity {

    private RecyclerView mRecyclerView;
    private Button btnOk;
    private TextView txtValor;
    private FreteServicoAdapter adapter;
    private List<FreteServico> mList = new ArrayList<>();
    private java.util.Set<Integer> selecionados = new java.util.HashSet<>();
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private HashMap<String, String> usuarioLogado;
    private int numOrc = 0;
    private double pesoTotalPedido = 0.0;
    private java.util.Set<Integer> itemsDesabilitados = new java.util.HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_servicos_adicionais);

        if (getIntent().hasExtra("numOrc")) {
            numOrc = getIntent().getIntExtra("numOrc", 0);
        }

        // Recebe o peso total do pedido
        if (getIntent().hasExtra("pesoTotal")) {
            pesoTotalPedido = getIntent().getDoubleExtra("pesoTotal", 0.0);
            android.util.Log.d("FreteServAdd", "Peso total do pedido: " + pesoTotalPedido + " KG");
        }

        // --- INÍCIO DA ALTERAÇÃO ---
        // Verifica se a tela foi aberta a partir do Orçamento ou do Pedido
        final boolean isOrcamento = getIntent().getBooleanExtra("isOrcamento", false);

        List<Integer> idsPreSelecionados;

        if (isOrcamento) {
            // Se for ORÇAMENTO, busca os IDs do cache do Orçamento.
            idsPreSelecionados = PopupOrcFechamento.idsServicosFretePorOrcamento.getOrDefault(numOrc, new ArrayList<>());
        } else {
            // Se for PEDIDO, busca os IDs do cache estático do Pedido.
            idsPreSelecionados = br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed;
            android.util.Log.d("FreteDebug", "PopupServicos (Pedido) usando IDs em cache: " + idsPreSelecionados);
        }

        if (idsPreSelecionados != null) {
            // Popula o 'Set' de selecionados com os IDs corretos (do Orçamento ou do Pedido).
            selecionados.addAll(idsPreSelecionados);
        }

        LoginHelper loginHelper = new LoginHelper(PopupServicosAdicionais.this);
        usuarioLogado = loginHelper.GetUsuarioSalvo();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .7));

        mRecyclerView = findViewById(R.id.pop_serv_lista);
        btnOk = findViewById(R.id.pop_serv_btn_ok);
        txtValor = findViewById(R.id.pop_serv_valor);
        Button btnHelp = findViewById(R.id.pop_serv_btn_help);

        btnOk.setEnabled(true);
        btnOk.setBackgroundResource(R.drawable.backgroudbuttom);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(PopupServicosAdicionais.this));
        adapter = new FreteServicoAdapter(PopupServicosAdicionais.this, mList);
        mRecyclerView.setAdapter(adapter);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Intent i = new android.content.Intent(PopupServicosAdicionais.this, PopupInfoFreteServicos.class);
                startActivity(i);
            }
        });

        mRecyclerView.addOnItemTouchListener(new br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener(this, mRecyclerView, new br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FreteServico item = mList.get(position);

                // Validar se o peso do pedido excede o limite_peso
                if (pesoTotalPedido > item.getLimitePeso() && item.getLimitePeso() > 0) {
                    android.widget.Toast.makeText(PopupServicosAdicionais.this,
                            "SERVIÇO COM PESO MÁXIMO POR PEDIDO DE: " + String.format("%.2f", item.getLimitePeso()) + " KG",
                            android.widget.Toast.LENGTH_LONG).show();
                    itemsDesabilitados.add(item.getIdServico());
                    return; // Não permite seleção
                }

                if(selecionados.contains(item.getIdServico())){
                    selecionados.remove(item.getIdServico());
                } else {
                    selecionados.add(item.getIdServico());
                }
                adapter.setSelectedIds(selecionados);
                atualizarResumo();
                
                // ATUALIZAR EM TEMPO REAL PARA PEDIDO (enquanto o popup está aberto)
                if (!isOrcamento) {
                    atualizarValoresEmTempoReal();
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

         btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double total = 0.0;
                List<Integer> ids = new ArrayList<>();
                List<String> nomes = new ArrayList<>();
                for(FreteServico fs : mList){
                    if(selecionados.contains(fs.getIdServico())){
                        // Calcular valor considerando tipo_calculo
                        double valorCalculado = calcularValorServico(fs);
                        total += valorCalculado;
                        ids.add(fs.getIdServico());
                        nomes.add(fs.getServicoFrete());
                        android.util.Log.d("FreteServAdd", "Serviço " + fs.getIdServico() + ": ValorBase=" + fs.getValorServico() + ", TipoCalculo=" + fs.getTipoCalculo() + ", ValorCalculado=" + valorCalculado);
                    }
                }
                if (isOrcamento) {
                    PopupOrcFechamento.servicosFretePorOrcamento.put(numOrc, total);
                    PopupOrcFechamento.idsServicosFretePorOrcamento.put(numOrc, ids);
                } else {
                    br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed = total;
                    if (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido != null) {
                        br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setVlServicosAdicionais(total);
                        br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setServicosAdicionaisIds(ids);
                        // também armazena os nomes para evitar nova chamada à API
                        br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setServicosAdicionaisNomes(nomes);
                    boolean houveSelecaoOk = (!ids.isEmpty() && total > 0);
                    br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisAlterados = houveSelecaoOk;
                    br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisLastModified = houveSelecaoOk ? System.currentTimeMillis() : 0;
                        android.util.Log.d("FreteServicoCarry", "ServMemPed atualizado no Pedido: " + total);
                        br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.marcarTotalComoRecalculavel();
                        br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.atualizaTotais(br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.getItens());
                    }
                    android.content.SharedPreferences sp = getSharedPreferences("frete_pedido", android.content.Context.MODE_PRIVATE);
                    sp.edit().putString("servicos_total", Double.toString(total)).apply();
                }
                finish();
            }
        });

        carregarServicos();
    }

    private void carregarServicos(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiGemco api = retrofit.create(ApiGemco.class);
        String token = "Bearer " + ApiGemco.token;
        Call<List<FreteServico>> call = api.GetFreteServico(token);
        call.enqueue(new Callback<List<FreteServico>>() {
            @Override
            public void onResponse(Call<List<FreteServico>> call, Response<List<FreteServico>> response) {
                if(response.isSuccessful() && response.body()!=null){
                    java.util.List<FreteServico> data = response.body();
                    java.util.List<FreteServico> filtrados = new java.util.ArrayList<>();
                    for(FreteServico fs : data){
                        if (fs.getStatus() != null && fs.getStatus().equals("1")){
                            filtrados.add(fs);
                        }
                    }
                    mList = filtrados;
                    adapter.setData(filtrados);
                    adapter.setSelectedIds(selecionados);
                    // Atualiza o texto do rodapé (valor e nomes dos serviços)
                    atualizarResumo();
                }
            }

            @Override
            public void onFailure(Call<List<FreteServico>> call, Throwable t) {
            }
        });
    }

    private void atualizarResumo(){
        double total = 0.0;
        java.util.List<String> nomes = new java.util.ArrayList<>();
        for(FreteServico fs : mList){
            if(selecionados.contains(fs.getIdServico())){
                double valorCalculado = calcularValorServico(fs);
                total += valorCalculado;
                nomes.add(fs.getServicoFrete());
            }
        }
        String lista = nomes.isEmpty() ? "" : " - " + android.text.TextUtils.join(", ", nomes);
        txtValor.setText("Valor: " + real.format(total) + lista);
        btnOk.setEnabled(true);
        btnOk.setBackgroundResource(R.drawable.backgroudbuttom);
    }

    /**
     * Calcula o valor do serviço conforme tipo_calculo:
     * "P" (Padrão): valor fixo
     * "T" (Tonelada): valor mínimo até 1000 KG, depois proporcional por tonelada
     *   - De 0 até 1000 KG: valor_servico (mínimo)
     *   - Acima de 1000 KG: valor_servico * (peso / 1000)
     */
    private double calcularValorServico(FreteServico servico) {
        if (servico == null || pesoTotalPedido <= 0) {
            return servico == null ? 0.0 : servico.getValorServico();
        }

        String tipoCalculo = servico.getTipoCalculo();

        if (tipoCalculo != null && tipoCalculo.equalsIgnoreCase("T")) {
            // Tipo T: valor mínimo até 1000 KG, depois proporcional
            double valorBase = servico.getValorServico();
            double valorCalculado;

            if (pesoTotalPedido <= 1000.0) {
                // Até 1000 KG: valor fixo (mínimo)
                valorCalculado = valorBase;
                android.util.Log.d("FreteServAdd", "Cálculo (tipo=T, peso<=1000): peso=" + pesoTotalPedido + " KG, valor_base=" + valorBase + " (MÍNIMO)");
            } else {
                // Acima de 1000 KG: proporcional
                valorCalculado = (pesoTotalPedido / 1000.0) * valorBase;
                android.util.Log.d("FreteServAdd", "Cálculo proporcional (tipo=T, peso>1000): peso=" + pesoTotalPedido + " KG, valor_base=" + valorBase + ", valor_proporcional=" + valorCalculado);
            }

            return valorCalculado;
        } else {
            // Tipo "P" ou qualquer outro: valor fixo
            return servico.getValorServico();
        }
    }
    
    private void atualizarValoresEmTempoReal() {
        // Calcula o total e os IDs selecionados em tempo real
        double total = 0.0;
        List<Integer> ids = new ArrayList<>();
        for(FreteServico fs : mList){
            if(selecionados.contains(fs.getIdServico())){
                double valorCalculado = calcularValorServico(fs);
                total += valorCalculado;
                ids.add(fs.getIdServico());
            }
        }
        
        br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servMemPed = total;
        br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.idsServMemPed = ids;
        boolean houveSelecao = (ids != null && !ids.isEmpty() && total > 0);
        br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisAlterados = houveSelecao;
        br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.servicosAdicionaisLastModified = houveSelecao ? System.currentTimeMillis() : 0;
        
        // Atualiza o objeto pedido também
        if (br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido != null) {
            br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setVlServicosAdicionais(total);
            br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity.pedido.setServicosAdicionaisIds(ids);
            android.util.Log.d("FreteServicoCarry", "Valores atualizados em tempo real. Serviços: " + total + ", IDs: " + ids);
        }
    }
}
