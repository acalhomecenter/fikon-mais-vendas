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
import br.com.acalfortaleza.fikonmais.models.FreteServico;

public class FreteServicoInfoAdapter extends RecyclerView.Adapter<FreteServicoInfoAdapter.ViewHolder> {
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private Context context;
    private List<FreteServico> mList;

    public FreteServicoInfoAdapter(Context context, List<FreteServico> mList){
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public FreteServicoInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_fretes_info, parent,false);
        return new FreteServicoInfoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FreteServicoInfoAdapter.ViewHolder holder, int position) {
        FreteServico item = mList.get(position);
        String tipoCalc = item.getTipoCalculo() != null ? item.getTipoCalculo() : "";
        String tipoDesc = tipoCalc.equalsIgnoreCase("P") ? "PEDIDO" : (tipoCalc.equalsIgnoreCase("T") ? "PESO" : tipoCalc);
        holder.txtNome.setText("Serviço Frete: " + (item.getServicoFrete() != null ? item.getServicoFrete() : ""));
        holder.txtTipo.setText("Tipo Cálculo: " + tipoDesc);
        holder.txtLimite.setText("Peso Máximo: " + item.getLimitePeso());
        holder.txtObs.setText("Obs: " + (item.getObs() != null ? item.getObs() : ""));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setData(List<FreteServico> data){
        this.mList = data;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        TextView txtNome;
        TextView txtTipo;
        TextView txtLimite;
        TextView txtObs;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNome = itemView.findViewById(R.id.adap_frete_info_nome);
            txtTipo = itemView.findViewById(R.id.adap_frete_info_tipo);
            txtLimite = itemView.findViewById(R.id.adap_frete_info_limite);
            txtObs = itemView.findViewById(R.id.adap_frete_info_obs);
        }

        @Override
        public void onClick(View view) {
        }
    }
}
