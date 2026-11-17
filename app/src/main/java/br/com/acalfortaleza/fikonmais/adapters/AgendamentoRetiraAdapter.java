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
import br.com.acalfortaleza.fikonmais.models.HorarioRetira;

public class AgendamentoRetiraAdapter extends RecyclerView.Adapter<AgendamentoRetiraAdapter.ViewHolder>  {



    private Context context;
    private List<HorarioRetira> mList;
    private RecyclerViewOnItemClickListenerHack mRecyclerViewOnItemClickListenerHack;
    private HorarioRetira horario;


    public AgendamentoRetiraAdapter(Context context, List<HorarioRetira> mList){
        this.context = context;
        this.mList = mList;

    }

    @NonNull
    @Override
    public AgendamentoRetiraAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_horario_agendamento, parent,false);
        AgendamentoRetiraAdapter.ViewHolder holder = new AgendamentoRetiraAdapter.ViewHolder(view);



        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull AgendamentoRetiraAdapter.ViewHolder holder, int position) {

        horario = mList.get(position);

        holder.txtHora.setText(horario.getHorario());
        holder.txtVagas.setText(horario.getVagas());
        
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void addListitem(HorarioRetira c, int position){
        mList.add(c);
        notifyItemInserted(position);

    }

    public void setRecyclerViewOnItemClickListenerHack(RecyclerViewOnItemClickListenerHack r) {
        mRecyclerViewOnItemClickListenerHack = r;
    }


    class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        TextView txtHora;
        TextView txtVagas;





        public ViewHolder(View itemView) {
            super(itemView);

            txtHora = itemView.findViewById(R.id.adap_agen_horario);
            txtVagas = itemView.findViewById(R.id.adap_agen_vagas);




        }


        @Override
        public void onClick(View view) {
            if (mRecyclerViewOnItemClickListenerHack != null) {
                mRecyclerViewOnItemClickListenerHack.onItemClickListener(view, getPosition());
            }

        }



    }
}
