package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Objects;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.IndicadorCampanha;

public class IndicadorCampanhaAdapter extends RecyclerView.Adapter<IndicadorCampanhaAdapter.ViewHolder>{

    private Context context;
    private List<IndicadorCampanha> listIndicadores;
    private IndicadorCampanha indicador;

    public IndicadorCampanhaAdapter(Context context, List<IndicadorCampanha> listIndicadores){

        this.context = context;
        this.listIndicadores = listIndicadores;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.adapter_indicador_campanha, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    private String FormatadorDecimal(Double valor){

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(valor);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        indicador = listIndicadores.get(position);

        if(indicador!=null && indicador.getNome()!=null){

            holder.txtIndicador.setText(indicador.getNome().replaceAll("_"," "));
            holder.txtValorAtingido.setText(FormatadorDecimal(indicador.getValorAtingido()));

            if(Objects.equals(indicador.getNome(), "DESCONTO_COMERCIAL")){
                holder.txtMeta.setText("Meta <= " + FormatadorDecimal(indicador.getMeta()));
            }
            else {
                holder.txtMeta.setText("Meta >= " + FormatadorDecimal(indicador.getMeta()));
            }

            if(indicador.getTipo().contains("percentual")){
                holder.txtValorAtingido.setText( holder.txtValorAtingido.getText() + "%" );
                holder.txtMeta.setText( holder.txtMeta.getText() + "%" );
            }

            holder.txtPontos.setText(FormatadorDecimal(indicador.getPontos()) + " Milhas");

            if(position % 2 == 0){
                holder.layoutIndicador.setBackgroundColor(Color.parseColor("#C5C7C9"));
            }

            if(Objects.equals(indicador.getNome(), "DESCONTO_COMERCIAL")){
                if (indicador.getValorAtingido() <= indicador.getMeta() && indicador.getMeta() > 0) {
                    holder.txtValorAtingido.setTextColor(Color.parseColor("#2CAA2A"));
                } else {
                    holder.txtValorAtingido.setTextColor(Color.parseColor("#B91818"));
                }
            }
            else {
                if (indicador.getValorAtingido() >= indicador.getMeta() && indicador.getMeta() > 0) {
                    holder.txtValorAtingido.setTextColor(Color.parseColor("#2CAA2A"));
                } else {
                    holder.txtValorAtingido.setTextColor(Color.parseColor("#B91818"));
                }
            }

        }


    }

    @Override
    public int getItemCount() {

        if(listIndicadores == null){
            return 0;
        }

        return listIndicadores.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtIndicador;
        TextView txtValorAtingido;
        TextView txtMeta;
        TextView txtPontos;
        LinearLayout layoutIndicador;

        public ViewHolder(View itemView){

            super(itemView);

            txtIndicador = itemView.findViewById(R.id.adapterIndicadorCampanha_Indicador);
            txtValorAtingido = itemView.findViewById(R.id.adapterIndicadorCampanha_ValorAtingido);
            txtMeta = itemView.findViewById(R.id.adapterIndicadorCampanha_Meta);
            txtPontos = itemView.findViewById(R.id.adapterIndicadorCampanha_Pontos);
            layoutIndicador = itemView.findViewById(R.id.adapterIndicadorCampanha_layoutIndicador);

        }

    }

}
