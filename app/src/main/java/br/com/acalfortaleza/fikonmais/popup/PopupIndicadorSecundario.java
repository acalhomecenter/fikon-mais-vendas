package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.IndicadorAdapter;
import br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener;
import br.com.acalfortaleza.fikonmais.models.Categoria;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.models.Indicador;

public class PopupIndicadorSecundario extends Activity {

     private Button btnFechar;
     private TextView titulo;
     private IndicadorAdapter adapter;
     private List<Indicador> mList;
     private RecyclerView mRecyclerView;
     private Consultor consultor;
     private String tipo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_indicadorsecundario);

        consultor = (Consultor) getIntent().getSerializableExtra("consultor");
        tipo = getIntent().getStringExtra("tipo");

        mList = new ArrayList<>();


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .9), (int) (height * .5));

        btnFechar = findViewById(R.id.pop_ind_btnFechar);
        titulo = findViewById(R.id.pop_ind_titulo);
        mRecyclerView = findViewById(R.id.pop_ind_listaIndicador);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(PopupIndicadorSecundario.this));


        adapter = new IndicadorAdapter(this,mList);
        mRecyclerView.setAdapter(adapter);


        switch(tipo){
            case "margem":

                titulo.setText("Indicadores Margem");

                Indicador ind = new Indicador();
                ind.setDescricao("% Desconto");
                ind.setValor(Double.parseDouble(consultor.PercDesconto));
                ind.setTipo("perc");
                mList.add(ind);


                for(Categoria c : consultor.Categorias){
                    Indicador ind1 = new Indicador();
                    ind1.setDescricao("% Desc. "+c.getDescricao());
                    ind1.setValor(c.getPercDesc());
                    ind1.setTipo("perc");
                    mList.add(ind1);

                }

                adapter = new IndicadorAdapter(this,mList);
                mRecyclerView.setAdapter(adapter);
                break;
            case "faturamento" :

                titulo.setText("Indicadores Faturamento");

                Indicador indf = new Indicador();
                indf.setDescricao("% Devolução");
                indf.setValor(Double.parseDouble(consultor.PercentDevolucao));
                indf.setTipo("perc");
                mList.add(indf);

                Indicador indf1 = new Indicador();
                indf1.setDescricao("Valor Devolução");
                indf1.setValor((consultor.Devolucao));
                indf1.setTipo("valor");
                mList.add(indf1);

                Indicador indf2 = new Indicador();
                indf2.setDescricao("Valor Frete");
                indf2.setValor((consultor.Frete));
                indf2.setTipo("valor");
                mList.add(indf2);

                Indicador indf3 = new Indicador();
                indf3.setDescricao("Valor Desconto");
                indf3.setValor((consultor.Desconto));
                indf3.setTipo("valor");
                mList.add(indf3);


                adapter = new IndicadorAdapter(this,mList);
                mRecyclerView.setAdapter(adapter);
                break;

            case "ticket":
                titulo.setText("Indicadores Ticket");

                Indicador indt = new Indicador();
                indt.setDescricao("M2 por Atendimento");
                indt.setValor((consultor.QuantidadeMes));
                indt.setTipo("quantidade");
                mList.add(indt);

                Indicador indt1 = new Indicador();
                indt1.setDescricao("SKU por Vendas");
                indt1.setValor((consultor.ItensVendas));
                indt1.setTipo("int");
                mList.add(indt1);

                break;

            case "atendimento":
                titulo.setText("Indicadores Atendimentos");

                Indicador inda = new Indicador();
                inda.setDescricao("Pedidos Retira Deposito");
                inda.setValor((consultor.qtdeRetira));
                inda.setTipo("quantidade");
                mList.add(inda);

                Indicador inda1 = new Indicador();
                inda1.setDescricao("Pedidos Retira agendados");
                inda1.setValor((consultor.qtdeAgendado));
                inda1.setTipo("quantidade");
                mList.add(inda1);


                Indicador inda2 = new Indicador();
                inda2.setDescricao("% de pedidos agendados");
                inda2.setValor(Double.parseDouble(consultor.percAgendado));
                inda2.setTipo("perc");
                mList.add(inda2);

                Indicador inda3 = new Indicador();
                inda3.setDescricao("Valor do Frete tabelado ");
                inda3.setValor((consultor.freteOrig));
                inda3.setTipo("valor");
                mList.add(inda3);

                Indicador inda4 = new Indicador();
                inda4.setDescricao("Valor do Frete cobrado");
                inda4.setValor((consultor.vlFrete));
                inda4.setTipo("valor");
                mList.add(inda4);

                Indicador inda5 = new Indicador();
                inda5.setDescricao("% do frete cobrado");
                inda5.setValor(Double.parseDouble(consultor.percFrete));
                inda5.setTipo("perc");
                mList.add(inda5);


                break;


        }





        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

}
