package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnItemClickListenerHack;
import br.com.acalfortaleza.fikonmais.models.Indicador;

public class IndicadorAdapter extends  RecyclerView.Adapter<IndicadorAdapter.ViewHolder> {

    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    final DecimalFormat df = new DecimalFormat("0.00");
    final DecimalFormat intf = new DecimalFormat("0");
    private Context context;
    private RecyclerViewOnItemClickListenerHack mRecyclerViewOnItemClickListenerHack;
    private List<Indicador> mList;
    private Indicador indicador;


    public IndicadorAdapter(Context context, List<Indicador> mList){
        this.context = context;
        this.mList = mList;

    }



    @NonNull
    @Override
    public IndicadorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_indicador, parent,false);
        IndicadorAdapter.ViewHolder holder = new IndicadorAdapter.ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull IndicadorAdapter.ViewHolder holder, int position) {

        indicador = mList.get(position);


        holder.txtDescricao.setText(indicador.getDescricao());

        switch (indicador.getTipo()){
            case "int":
                holder.txtValor.setText(intf.format(indicador.getValor()));
                break;
            case "valor":
                holder.txtValor.setText(real.format(indicador.getValor()));
                break;
            case "perc":
                holder.txtValor.setText(df.format(indicador.getValor())+"%");
                break;
            case "quantidade":
                holder.txtValor.setText(df.format(indicador.getValor()));
                break;
        }


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addListitem(Indicador i, int position){
        mList.add(i);
        notifyItemInserted(position);

    }

    public void setRecyclerViewOnItemClickListenerHack(RecyclerViewOnItemClickListenerHack r) {
        mRecyclerViewOnItemClickListenerHack = r;
    }


    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        private TextView txtDescricao;
        private TextView txtValor;



        public ViewHolder(View itemView) {
            super(itemView);

            txtDescricao = itemView.findViewById(R.id.adap_ind_descricao);
            txtValor = itemView.findViewById(R.id.adap_ind_valor);



        }


        @Override
        public void onClick(View view) {
            if (mRecyclerViewOnItemClickListenerHack != null) {
                mRecyclerViewOnItemClickListenerHack.onItemClickListener(view, getPosition());
            }

        }



    }

}
