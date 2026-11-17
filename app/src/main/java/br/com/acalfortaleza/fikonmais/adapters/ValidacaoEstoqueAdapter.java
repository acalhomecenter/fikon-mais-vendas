package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnItemClickListenerHack;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnLongClickListenerHack;
import br.com.acalfortaleza.fikonmais.models.Devolucao;
import br.com.acalfortaleza.fikonmais.models.ValidacaoEstoque;

public class ValidacaoEstoqueAdapter extends RecyclerView.Adapter<ValidacaoEstoqueAdapter.ViewHolder>  {

    public static List<ValidacaoEstoque> mList;
    public static List<ValidacaoEstoque> mListFull;
    final DecimalFormat df = new DecimalFormat("0.00");
    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
    private Context context;
    private String color;
    private RecyclerViewOnItemClickListenerHack mRecyclerViewOnItemClickListenerHack;
    private RecyclerViewOnLongClickListenerHack mRecyclerViewOnLongClickListenerHack;


    public ValidacaoEstoqueAdapter(Context context, List<ValidacaoEstoque> validacao) {

        this.context = context;
        this.mList = validacao;
        this.mListFull = new ArrayList<>(validacao);

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_validacao, parent, false);
        ValidacaoEstoqueAdapter.ViewHolder holder = new ValidacaoEstoqueAdapter.ViewHolder(view);


        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ValidacaoEstoqueAdapter.ViewHolder holder, final int position) {

        ValidacaoEstoque validacao = mList.get(position);
        holder.status.setText("STATUS: "+validacao.getStatus());
        holder.tempo.setText("TEMPO: "+ validacao.getTempo());
        holder.produto.setText(validacao.getItem()+" - "+validacao.getProduto()+" "+validacao.getLotevencto());
        holder.qtdsolicitada.setText("QTDE SOLICITADA: "+df.format(validacao.getQtdSolicitada()));
        holder.qtdverificada.setText("QTDE VERIFICADA: "+df.format(validacao.getQtdVerificada()));
        holder.responsavel.setText("RESPONSÁVEL: "+ validacao.getResponsavel());
        holder.obs.setText("OBS: "+ validacao.getObs());


    }



    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void addListitem(ValidacaoEstoque v, int position){
        mList.add(v);
        notifyItemInserted(position);

    }

    public void setRecyclerViewOnItemClickListenerHack(RecyclerViewOnItemClickListenerHack r) {
        mRecyclerViewOnItemClickListenerHack = r;
    }


    public void setRecyclerViewOnLongClickListenerHack(RecyclerViewOnLongClickListenerHack r) {
        mRecyclerViewOnLongClickListenerHack = r;
    }




    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView tempo;
        private TextView status;
        private TextView produto;
        private TextView qtdsolicitada;
        private TextView qtdverificada;
        private TextView responsavel;
        private TextView obs;





        public ViewHolder(View itemView) {
            super(itemView);

            tempo = itemView.findViewById(R.id.adap_val_tempo);
            status = itemView.findViewById(R.id.adap_val_status);
            produto = itemView.findViewById(R.id.adap_val_produto);
            qtdsolicitada = itemView.findViewById(R.id.adap_val_qtdsolicitada);
            qtdverificada = itemView.findViewById(R.id.adap_val_qtdVerificada);
            responsavel = itemView.findViewById(R.id.adap_val_responsavel);
            obs = itemView.findViewById(R.id.adap_val_obs);


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
