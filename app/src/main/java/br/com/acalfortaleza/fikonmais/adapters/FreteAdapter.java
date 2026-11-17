package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnItemClickListenerHack;
import br.com.acalfortaleza.fikonmais.models.Fretes;
import br.com.acalfortaleza.fikonmais.models.HorarioRetira;

public class FreteAdapter extends RecyclerView.Adapter<FreteAdapter.ViewHolder> {
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private Context context;
    private List<Fretes> mList;
    private RecyclerViewOnItemClickListenerHack mRecyclerViewOnItemClickListenerHack;
    private Fretes frete;



    public FreteAdapter(Context context, List<Fretes> mList){
        this.context = context;
        this.mList = mList;

    }

    @NonNull
    @Override
    public FreteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_fretes, parent,false);
        FreteAdapter.ViewHolder holder = new FreteAdapter.ViewHolder(view);



        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull FreteAdapter.ViewHolder holder, int position) {

        frete = mList.get(position);


        holder.txtCod.setText(String.valueOf(frete.getCodFrete()));
        holder.txtModalidade.setText(frete.getModalidade());
        holder.txtValor.setText(real.format(frete.getVlfrete()));



    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void addListitem(Fretes c, int position){
        mList.add(c);
        notifyItemInserted(position);

    }

    public void setRecyclerViewOnItemClickListenerHack(RecyclerViewOnItemClickListenerHack r) {
        mRecyclerViewOnItemClickListenerHack = r;
    }


    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        TextView txtCod;
        TextView txtModalidade;
        TextView txtValor;





        public ViewHolder(View itemView) {
            super(itemView);

            txtCod = itemView.findViewById(R.id.adap_frete_cod);
            txtModalidade = itemView.findViewById(R.id.adap_frete_modalidade);
            txtValor = itemView.findViewById(R.id.adap_frete_valor);



        }


        @Override
        public void onClick(View view) {
            if (mRecyclerViewOnItemClickListenerHack != null) {
                mRecyclerViewOnItemClickListenerHack.onItemClickListener(view, getPosition());
            }

        }



    }
}
