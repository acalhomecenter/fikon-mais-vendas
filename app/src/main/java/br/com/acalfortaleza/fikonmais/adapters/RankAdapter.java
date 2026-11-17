package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.Rank;
import de.hdodenhof.circleimageview.CircleImageView;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {

    private Context context;
    private  List<Rank> listRank;
    private Rank rank;

    public RankAdapter (Context context, List<Rank> listRank){

        this.context = context;
        this.listRank = listRank;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_rank, parent,false);
        ViewHolder holder = new ViewHolder(view);

        return holder;

    }

    private String FormatadorDecimal(Double valor){

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(valor);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        rank = listRank.get(position);

        switch (rank.getPosicao()){

            case 1:
                holder.layoutPosicao.setBackgroundColor(Color.parseColor("#ffd700"));
                break;

            case 2:
                holder.layoutPosicao.setBackgroundColor(Color.parseColor("#c0c0c0"));
                break;

            case 3:
                holder.layoutPosicao.setBackgroundColor(Color.parseColor("#cd7f32"));
                break;

            default:
                holder.layoutPosicao.setBackgroundColor(Color.parseColor("#03a9f4"));
                break;

        }

        holder.txtRank.setText(rank.getPosicao().toString());
        holder.txtPontos.setText( FormatadorDecimal(rank.getPontos()) );
        holder.txtNome.setText(rank.getNome());
        holder.txtVendaLiquida.setText( "R$ " + FormatadorDecimal(rank.getVendaLiquidaTotal()) );

        /*
        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(ApiFluig.KEY, ApiFluig.SECRET);
        consumer.setTokenWithSecret(ApiFluig.TOKEN, ApiFluig.TOKENSECRET);

        OkHttpClient clientFluig = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();

        Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(clientFluig))
                .build();

        String urlProfile = "http://fluig.acalhomecenter.com.br/social/api/rest/social/image/profile/" + campanha.getLogin() + "/MEDIUM_PICTURE";

        picasso.load(urlProfile)
                .error(R.drawable.participante)
                .fit()
                .centerCrop()
                .into(holder.profile);

         */

    }



    @Override
    public int getItemCount() {

        if(listRank==null){
            return 0;
        }

        return listRank.size();

    }

    class ViewHolder extends RecyclerView.ViewHolder  {

        CircleImageView profile;
        TextView txtRank;
        TextView txtPontos;
        TextView txtNome;
        TextView txtVendaLiquida;
        LinearLayout layoutPosicao;

        public ViewHolder(View itemView) {

            super(itemView);

            txtRank = itemView.findViewById(R.id.adap_camp_txtrank);
            txtPontos = itemView.findViewById(R.id.adap_camp_txtPontos);
            txtNome = itemView.findViewById(R.id.adap_camp_nome);
            txtVendaLiquida = itemView.findViewById(R.id.AdapterRank_VendaLiquida);
            layoutPosicao = itemView.findViewById(R.id.adapterRank_layoutPosicao);

        }


    }

}
