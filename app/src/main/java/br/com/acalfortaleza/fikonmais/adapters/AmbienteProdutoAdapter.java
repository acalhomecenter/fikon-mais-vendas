package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.AmbienteProduto;
import br.com.acalfortaleza.fikonmais.models.NomeAmbiente;

public class AmbienteProdutoAdapter extends RecyclerView.Adapter<AmbienteProdutoAdapter.ViewHolder>{

    private Context context;
    private List<NomeAmbiente> listNomesAmbientes;
    private List<AmbienteProduto> listAmbientesProduto;
    private int posicaoSelecionada;
    public String idSelecionado;

    public AmbienteProdutoAdapter(Context context, List<NomeAmbiente> listNomesAmbientes , List<AmbienteProduto> listAmbientesProduto){

        this.context = context;
        this.listNomesAmbientes = listNomesAmbientes;
        this.listAmbientesProduto = listAmbientesProduto;
        this.posicaoSelecionada = RecyclerView.NO_POSITION;
        this.idSelecionado = null;

    }

    @NonNull
    @Override
    public AmbienteProdutoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_ambiente_produto, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull AmbienteProdutoAdapter.ViewHolder holder, int position) {

        AmbienteProduto ambienteProduto = listAmbientesProduto.get(position);

        holder.txtID.setText(String.valueOf(ambienteProduto.getId()));

        for (NomeAmbiente nomeAmbiente: listNomesAmbientes) {

            if(nomeAmbiente.getId() == ambienteProduto.getIdAmbiente()){

                holder.txtAmbiente.setText(nomeAmbiente.getDescricao());

            }

        }

        holder.txtQuantidade.setText(String.valueOf(ambienteProduto.getQuantidade()));

        if(posicaoSelecionada==position){
            idSelecionado = ambienteProduto.getId();
            holder.itemView.setBackgroundColor(Color.parseColor("#A3E3FF"));
        }
        else{
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {

        return listAmbientesProduto.size();

    }

    public void desmarcarSelecionados(){

        posicaoSelecionada = RecyclerView.NO_POSITION;
        idSelecionado = null;

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView txtID;
        private TextView txtAmbiente;
        private TextView txtQuantidade;

        public ViewHolder(View view){

            super(view);
            txtID = view.findViewById(R.id.adapterAmbienteProduto_txtViewId);
            txtAmbiente = view.findViewById(R.id.adapterAmbienteProduto_txtViewAmbiente);
            txtQuantidade = view.findViewById(R.id.adapterAmbienteProduto_txtViewQuantidade);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view){

            if(getAdapterPosition()==RecyclerView.NO_POSITION){return;}

            notifyItemChanged(posicaoSelecionada);
            posicaoSelecionada = getAdapterPosition();
            notifyItemChanged(posicaoSelecionada);

        }

    }

}
