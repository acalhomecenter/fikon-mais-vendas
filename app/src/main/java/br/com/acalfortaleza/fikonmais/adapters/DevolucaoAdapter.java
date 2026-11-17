package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnItemClickListenerHack;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnLongClickListenerHack;
import br.com.acalfortaleza.fikonmais.models.Devolucao;


public class DevolucaoAdapter extends RecyclerView.Adapter<DevolucaoAdapter.ViewHolder> implements Filterable {

    private Context context;
    public static List<Devolucao> mList;
    public static List<Devolucao> mListFull;
    private String color;
    private RecyclerViewOnItemClickListenerHack mRecyclerViewOnItemClickListenerHack;
    private RecyclerViewOnLongClickListenerHack mRecyclerViewOnLongClickListenerHack;
    private NumberFormat real;
    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");

    public DevolucaoAdapter(Context context, List<Devolucao> devolucoes) {

        this.context = context;
        this.mList = devolucoes;
        this.mListFull = new ArrayList<>(devolucoes);

    }



    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_devolucao, parent, false);
        ViewHolder holder = new ViewHolder(view);
        real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        Devolucao devolucao = mList.get(position);
        if(devolucao.Tipo.equals("DEVOL")){
            holder.numnota.setText("Nota: "+devolucao.Numnota);
        }else{
            holder.numnota.setText("Pedido: "+devolucao.Numnota);
        }

        holder.cliente.setText(devolucao.Cliente);
        holder.filial.setText("Filial: "+devolucao.Filial);
        holder.valor.setText("Valor: "+real.format(devolucao.Valor));
        try {
            Date date = entrada.parse(devolucao.Data);
            holder.data.setText(("Data: " + formato.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }



    @Override
    public long getItemId(int position) {
        return Integer.parseInt(mList.get(position).Numnota);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void addListitem(Devolucao d, int position){
        mList.add(d);
        notifyItemInserted(position);

    }

    public void setRecyclerViewOnItemClickListenerHack(RecyclerViewOnItemClickListenerHack r) {
        mRecyclerViewOnItemClickListenerHack = r;
    }


    public void setRecyclerViewOnLongClickListenerHack(RecyclerViewOnLongClickListenerHack r) {
        mRecyclerViewOnLongClickListenerHack = r;
    }

    @Override
    public Filter getFilter() {
        return pedidosFilter;
    }

    private Filter pedidosFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Devolucao> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(mListFull);

            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Devolucao d : mListFull){
                    if (d.Cliente.toLowerCase().contains(filterPattern)){
                        filteredList.add(d);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mList.clear();
            mList.addAll((List) filterResults.values);
            notifyDataSetChanged();

        }
    };


    public void Order_Az() {
        Collections.sort(mList, Devolucao.ORDER_AZ);
        notifyDataSetChanged();
    }

    public void Order_Za() {
        Collections.sort(mList, Devolucao.ORDER_ZA);
        notifyDataSetChanged();
    }

    public void Order_Valor() {
        Collections.sort(mList, Devolucao.ORDER_VALOR);
        notifyDataSetChanged();
    }

    public void Order_ValorDesc() {
        Collections.sort(mList, Devolucao.ORDER_VALORDESC);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView numnota;
        private TextView cliente;
        private TextView data;
        private TextView filial;
        private TextView valor;



        public ViewHolder(View itemView) {
            super(itemView);

            numnota = itemView.findViewById(R.id.adap_dev_nota);
            cliente = itemView.findViewById(R.id.adap_dev_cilente);
            data = itemView.findViewById(R.id.adap_dev_data);
            filial = itemView.findViewById(R.id.adap_dev_filial);
            valor = itemView.findViewById(R.id.adap_dev_valor);


            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (mRecyclerViewOnItemClickListenerHack != null) {
                mRecyclerViewOnItemClickListenerHack.onItemClickListener(view, getPosition());
            }

        }

        public boolean onLongClick(View view) {
            if (mRecyclerViewOnLongClickListenerHack != null) {
                mRecyclerViewOnLongClickListenerHack.onLongClickListener(view, getPosition());
            }

            return false;
        }
    }
}
