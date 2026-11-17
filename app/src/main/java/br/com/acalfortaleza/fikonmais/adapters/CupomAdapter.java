package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import br.com.acalfortaleza.fikonmais.models.Cupom;
import br.com.acalfortaleza.fikonmais.models.Devolucao;

public class CupomAdapter extends RecyclerView.Adapter<CupomAdapter.ViewHolder> implements Filterable {

    private Context context;
    public static List<Cupom> mList;
    public static List<Cupom> mListFull;
    private String color;
    private RecyclerViewOnItemClickListenerHack mRecyclerViewOnItemClickListenerHack;
    private RecyclerViewOnLongClickListenerHack mRecyclerViewOnLongClickListenerHack;
    private NumberFormat real;
    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");

    public CupomAdapter(Context context, List<Cupom> cupons) {

        this.context = context;
        this.mList = cupons;
        this.mListFull = new ArrayList<>(cupons);

    }



    @Override
    public CupomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_cupom, parent, false);
        CupomAdapter.ViewHolder holder = new CupomAdapter.ViewHolder(view);
        real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final CupomAdapter.ViewHolder holder, final int position) {

        Cupom cupom = mList.get(position);
        holder.numnota.setText("Nota: "+cupom.Numnota);
        holder.cliente.setText(cupom.Cliente);
        holder.pdv.setText(cupom.Pdv);
        holder.valor.setText("Valor: "+real.format(cupom.Valor));
        try {
            Date date = entrada.parse(cupom.Data);
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


    public void addListitem(Cupom c, int position){
        mList.add(c);
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
            List<Cupom> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(mListFull);

            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Cupom c : mListFull){
                    if (c.Cliente.toLowerCase().contains(filterPattern)){
                        filteredList.add(c);
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
        Collections.sort(mList, Cupom.ORDER_AZ);
        notifyDataSetChanged();
    }

    public void Order_Za() {
        Collections.sort(mList, Cupom.ORDER_ZA);
        notifyDataSetChanged();
    }

    public void Order_Valor() {
        Collections.sort(mList, Cupom.ORDER_VALOR);
        notifyDataSetChanged();
    }

    public void Order_ValorDesc() {
        Collections.sort(mList, Cupom.ORDER_VALORDESC);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView numnota;
        private TextView cliente;
        private TextView data;
        private TextView pdv;
        private TextView valor;



        public ViewHolder(View itemView) {
            super(itemView);

            numnota = itemView.findViewById(R.id.adap_cup_nota);
            cliente = itemView.findViewById(R.id.adap_cup_cilente);
            data = itemView.findViewById(R.id.adap_cup_data);
            pdv = itemView.findViewById(R.id.adap_cup_pdv);
            valor = itemView.findViewById(R.id.adap_cup_valor);


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
