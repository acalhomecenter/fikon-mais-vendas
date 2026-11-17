package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.Campanha;

public class PontosAdapter extends RecyclerView.Adapter<PontosAdapter.ViewHolder> {

    private Context context;
    private Campanha campanha;
    private List<Campanha> listCampanha;


    public PontosAdapter (Context context, List<Campanha> listCampanha){

        this.context = context;
        this.listCampanha = listCampanha;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_pontos_individuais, parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;

    }

    private String FormatadorDecimal(Double valor){

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(valor);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {

        campanha = listCampanha.get(i);

        holder.txtMes.setText(campanha.getMes());

        if(campanha.getIndicadores()!=null && campanha.getIndicadores().size()>0) {
            IndicadorCampanhaAdapter indicadorCampanhaAdapter = new IndicadorCampanhaAdapter(context, campanha.getIndicadores());
            holder.recyclerIndicadores.setLayoutManager(new LinearLayoutManager(context));
            holder.recyclerIndicadores.setAdapter(indicadorCampanhaAdapter);
        }

        holder.txtSomatorioPontos.setText(FormatadorDecimal(campanha.getSomatorioPontos()) + " Milhas");
        holder.txtPercentualFaturamento.setText(FormatadorDecimal(campanha.getPercentualFaturamento()) + "%");
        holder.txtTotalPontos.setText(FormatadorDecimal(campanha.getPontuacaoMes()) + " Milhas");

    }


    @Override
    public int getItemCount() {

        if(listCampanha == null){
            return 0;
        }

        return listCampanha.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder  {

        TextView txtMes;
        RecyclerView recyclerIndicadores;
        TextView txtSomatorioPontos;
        TextView txtPercentualFaturamento;
        TextView txtTotalPontos;

        public ViewHolder(View itemView) {

            super(itemView);

            txtMes = itemView.findViewById(R.id.adap_indv_mes);
            recyclerIndicadores = itemView.findViewById(R.id.adapterPontosIndividuais_recyclerView);
            txtSomatorioPontos = itemView.findViewById(R.id.adapterPontosIndividuais_somatorioPontos);
            txtPercentualFaturamento = itemView.findViewById(R.id.adapterPontosIndividuais_percentualFaturamento);
            txtTotalPontos = itemView.findViewById(R.id.adap_indv_ptTotal);

        }

    }

}
