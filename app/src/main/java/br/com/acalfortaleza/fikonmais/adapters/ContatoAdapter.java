package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnItemClickListenerHack;
import br.com.acalfortaleza.fikonmais.models.Contato;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;


public class ContatoAdapter extends RecyclerView.Adapter<ContatoAdapter.ViewHolder>  {

    private Context context;
    private List<Contato> mList;
    private RecyclerViewOnItemClickListenerHack mRecyclerViewOnItemClickListenerHack;
    private Contato contato;


    public ContatoAdapter(Context context, List<Contato> mList){
        this.context = context;
        this.mList = mList;

    }

    @NonNull
    @Override
    public ContatoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_contato, parent,false);
        ContatoAdapter.ViewHolder holder = new ContatoAdapter.ViewHolder(view);



        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContatoAdapter.ViewHolder holder, int position) {

        contato = mList.get(position);

        holder.txtNome.setText(contato.getNomeContato());
        holder.txtCpf.setText("CPF: "+contato.getCgccpf());
        holder.txtEmail.setText("EMAIL: "+contato.getEmail());
        holder.txtContato.setText("CONTATO: ("+contato.getDddCelular()+")"+contato.getCelular());


    }

    @Override
    public int getItemCount() {

        if(mList!= null){
            return mList.size();
        }else{
            return 0;
        }

    }


    public void addListitem(Contato c, int position){
        mList.add(c);
        notifyItemInserted(position);

    }


    public void deleteListitem(Contato c, int position){
        mList.remove(position);
        notifyDataSetChanged();

    }


    public void setRecyclerViewOnItemClickListenerHack(RecyclerViewOnItemClickListenerHack r) {
        mRecyclerViewOnItemClickListenerHack = r;
    }


    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        TextView txtNome;
        TextView txtCpf;
        TextView txtEmail;
        TextView txtContato;




        public ViewHolder(View itemView) {
            super(itemView);

            txtNome = itemView.findViewById(R.id.adap_cont_nome);
            txtCpf = itemView.findViewById(R.id.adap_cont_cpf);
            txtEmail = itemView.findViewById(R.id.adap_cont_email);
            txtContato = itemView.findViewById(R.id.adap_cont_cont);




        }


        @Override
        public void onClick(View view) {
            if (mRecyclerViewOnItemClickListenerHack != null) {
                mRecyclerViewOnItemClickListenerHack.onItemClickListener(view, getPosition());
            }

        }



    }
}
