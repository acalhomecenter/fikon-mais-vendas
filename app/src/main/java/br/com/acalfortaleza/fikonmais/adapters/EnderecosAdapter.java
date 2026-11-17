package br.com.acalfortaleza.fikonmais.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.fragments.ClientesEnderecosFragments;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnClickListenerHack;
import br.com.acalfortaleza.fikonmais.models.Enderecos;

public class EnderecosAdapter extends RecyclerView.Adapter<EnderecosAdapter.ViewHolder> {

    private Context context;
    private final List<Enderecos> mList;
    private Enderecos endereco;
    private AlertDialog alertaDialog;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;


    public EnderecosAdapter(Context context, List<Enderecos> enderecos) {

        this.context = context;
        this.mList = enderecos;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_popup_enderecos, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        endereco =  mList.get(position);


        holder.endereco.setText(endereco.getCodlograd() + " " + endereco.getEnder()+", "+ endereco.getNumero()+" "+ endereco.getBairro() + ", " + endereco.getCidade() + " " + endereco.getUf()+ " - "+ endereco.getCep());

        if (!endereco.getComplemento().equals("")) {

            holder.complemento.setText("Complemento: " + (endereco.getComplemento()));
        }
        switch (endereco.getTpender()){
            case    "E":
                holder.tpender.setText("Tipo de endereço: Entrega");
                break;
            case    "R":
                holder.tpender.setText("Tipo de endereço: Residencial");
                break;

            case    "C":
                holder.tpender.setText("Tipo de endereço: Comercial");
                break;
                default:
                    holder.tpender.setText("Tipo de endereço: "+ endereco.getTpender());
                    break;
        }



        holder.imgMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentMapa = new Intent(Intent.ACTION_VIEW);
                intentMapa.setData(Uri.parse("geo:0,0?q=" + Uri.encode(mList.get(position).getCodlograd()+ " "+mList.get(position).getEnder()+", "+mList.get(position).getNumero()
                        + " " + mList.get(position).getBairro() + ", " + mList.get(position).getCidade() + " " + mList.get(position).getUf())));
                context.startActivity(intentMapa);

            }
        });


    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getCodend();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addListitem(Enderecos e, int position){
        mList.add(e);
        notifyItemInserted(position);
    }

    public void updateListitem(Enderecos e, int position){
        mList.set(position,e);
        ClientesEnderecosFragments.mList.set(position,e);
        notifyItemChanged(position);
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r){
        mRecyclerViewOnClickListenerHack = r;
    }

    public void removeListItem(int position){
        mList.remove(position);
        notifyItemRemoved(position);

    }




    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tpender;
        TextView endereco;
        TextView complemento;
        ImageView imgMapa;


        public ViewHolder(View itemView) {
            super(itemView);

            endereco = itemView.findViewById(R.id.adp_popup_endereco);
            complemento = itemView.findViewById(R.id.adp_popup_complemento);
            imgMapa = itemView.findViewById(R.id.adp_popup_imgMapa);
            tpender = itemView.findViewById(R.id.adp_popup_tpender);
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            if(mRecyclerViewOnClickListenerHack!= null){
                mRecyclerViewOnClickListenerHack.onClickListener(view,getPosition());
            }

        }
    }

}


