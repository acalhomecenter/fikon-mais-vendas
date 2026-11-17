package br.com.acalfortaleza.fikonmais.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import br.com.acalfortaleza.fikonmais.models.Credz.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.popup.PopupFormaPgto;

public class CredzPlanAdapter extends RecyclerView.Adapter<CredzPlanAdapter.ViewHolder>{

    private Context context;
    private List<Plan> listPlanos;
    private Pedidos pedido;
    private Orcamento orcamento;

    public CredzPlanAdapter(Context context, List<Plan> listPlanos, Pedidos pedido, Orcamento orcamento){

        this.context = context;
        this.listPlanos = listPlanos;
        this.pedido = pedido;
        this.orcamento = orcamento;

    }

    @NonNull
    @Override
    public CredzPlanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_credz_plan, parent,false);
        CredzPlanAdapter.ViewHolder holder = new CredzPlanAdapter.ViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull CredzPlanAdapter.ViewHolder holder, int position) {

        Plan plan = listPlanos.get(position);

        holder.tipoPagamento.setText(plan.getDescription().toUpperCase());
        holder.quantParcelas.setText(plan.getInstallments()+"x");
        holder.valorParcela.setText(
            NumberFormat.getCurrencyInstance(new Locale("pt","BR")).format(plan.getAmount().getInstallment())
        );
        holder.limiteDisponivel.setText(
            "Limite disponível para este plano: " + NumberFormat.getCurrencyInstance(new Locale("pt","BR")).format(plan.getAmount().getMaxPurchase())
        );

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pedido!=null) {
                    PopupFormaPgto.planoEscolhidoCredz = plan;
                }
                System.out.println("plano escolhido:" + new Gson().toJson(PopupFormaPgto.planoEscolhidoCredz));
                ((Activity) context).finish();

            }
        });

    }

    @Override
    public int getItemCount() {
        return listPlanos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tipoPagamento;
        TextView quantParcelas;
        TextView valorParcela;
        TextView limiteDisponivel;
        CardView cardView;

        public ViewHolder(View itemView){

            super(itemView);
            tipoPagamento = itemView.findViewById(R.id.adapter_credzPlan_tipoPagamento);
            quantParcelas = itemView.findViewById(R.id.adapter_credzPlan_quantParcelas);
            valorParcela = itemView.findViewById(R.id.adapter_credzPlan_valorParcela);
            limiteDisponivel = itemView.findViewById(R.id.adapter_credPlan_limiteDisponivel);
            cardView = itemView.findViewById(R.id.adapter_credzPlan_card);

        }

    }

}
