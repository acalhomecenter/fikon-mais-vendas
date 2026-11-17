package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.FreteServico;

public class FreteServicoAdapter extends RecyclerView.Adapter<FreteServicoAdapter.ViewHolder> {
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private Context context;
    private List<FreteServico> mList;
    private java.util.Set<Integer> selectedIds = new java.util.HashSet<>();

    public FreteServicoAdapter(Context context, List<FreteServico> mList){
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public FreteServicoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_fretes, parent,false);
        return new FreteServicoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FreteServicoAdapter.ViewHolder holder, int position) {
        FreteServico item = mList.get(position);
        String tip = item.getTipoCalculo() != null ? item.getTipoCalculo() : "";
        if (tip.equalsIgnoreCase("P")) {
            holder.txtCod.setText("P");
            holder.txtCod.setTextColor(Color.parseColor("#00BF63"));
        } else if (tip.equalsIgnoreCase("T")) {
            holder.txtCod.setText("KG");
            holder.txtCod.setTextColor(context.getResources().getColor(R.color.roxo));
        } else {
            holder.txtCod.setText("");
            holder.txtCod.setTextColor(Color.BLACK);
        }
        holder.txtModalidade.setText(item.getServicoFrete());
        holder.txtValor.setText(real.format(item.getValorServico()));

        boolean selected = selectedIds.contains(item.getIdServico());
        holder.itemView.setBackgroundColor(selected ? context.getResources().getColor(R.color.backgroudcolor) : Color.TRANSPARENT);
        holder.txtModalidade.setTextColor(selected ? context.getResources().getColor(R.color.primary) : Color.BLACK);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setData(List<FreteServico> data){
        this.mList = data;
        notifyDataSetChanged();
    }

    public void setSelectedIds(java.util.Set<Integer> ids){
        this.selectedIds = new java.util.HashSet<>(ids);
        notifyDataSetChanged();
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
        }
    }
}

