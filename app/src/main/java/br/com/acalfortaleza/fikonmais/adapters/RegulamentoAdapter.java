package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.fragments.ImageDialogFragment;
import br.com.acalfortaleza.fikonmais.models.Regulamento;

public class RegulamentoAdapter extends RecyclerView.Adapter<RegulamentoAdapter.ViewHolder> {


    private Context context;
    private List<Regulamento> mList;
    private Regulamento regulamento;
    private AlertDialog alertaDialog;
    private Bundle bundle;


    public RegulamentoAdapter (Context context, List<Regulamento> mList){

        this.context = context;
        this.mList = mList;
         bundle = new Bundle();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_regulamento, parent,false);
        ViewHolder holder = new ViewHolder(view);



        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        regulamento = mList.get(i);

        Picasso.get().load(regulamento.getUrl_Img()).into(holder.img1);

        holder.img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putSerializable("img_url", mList.get(i).Url_Img);
                ImageDialogFragment fragment = new ImageDialogFragment();
                fragment.setArguments(bundle);
                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                fragment.show(transaction, ImageDialogFragment.KEY);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder  {


        ImageView img1;


        public ViewHolder(View itemView) {
            super(itemView);

            img1 = itemView.findViewById(R.id.adap_reg_img1);





        }



    }

}
