package br.com.acalfortaleza.fikonmais.fragments;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.PontosAdapter;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Campanha;
import br.com.acalfortaleza.fikonmais.service.ApiFluig;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

public class PontosFrafments extends Fragment {

    private View layout;
    private TextView txtPontos;
    private TextView txtRank;
    private TextView txtNome;
    private TextView txtVendaLiquida;
    private LinearLayout layoutPosicao;
    private CircleImageView profile;
    private List<Campanha> listPontosConsultor;
    private RecyclerView recyclerView;
    private PontosAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            listPontosConsultor = (List<Campanha>) getArguments().getSerializable("pontosConsultor");
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_pontos,
                container, false);

        txtPontos = layout.findViewById(R.id.camp_txtPontos);
        txtRank = layout.findViewById(R.id.camp_txtrank);
        txtNome = layout.findViewById(R.id.fragmentPontos_nomeConsultor);
        txtVendaLiquida = layout.findViewById(R.id.FragmentPontos_VendaLiquida);
        layoutPosicao = layout.findViewById(R.id.fragmentPontos_layoutPosicao);

        recyclerView = layout.findViewById(R.id.adap_camp_listaCampanha);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        txtPontos.setText(FormatadorDecimal(listPontosConsultor.get(0).getRank().getPontos()));
        txtRank.setText(listPontosConsultor.get(0).getRank().getPosicao().toString());
        txtNome.setText(listPontosConsultor.get(0).getRank().getNome());
        txtVendaLiquida.setText(
            "R$ " + FormatadorDecimal(listPontosConsultor.get(0).getRank().getVendaLiquidaTotal())
        );

        switch (listPontosConsultor.get(0).getRank().getPosicao()){

            case 1:
                layoutPosicao.setBackgroundColor(Color.parseColor("#ffd700"));
                break;

            case 2:
                layoutPosicao.setBackgroundColor(Color.parseColor("#c0c0c0"));
                break;

            case 3:
                layoutPosicao.setBackgroundColor(Color.parseColor("#cd7f32"));
                break;

            default:
                layoutPosicao.setBackgroundColor(Color.parseColor("#03a9f4"));
                break;

        }

        adapter = new PontosAdapter(getContext(), listPontosConsultor);
        recyclerView.setAdapter(adapter);

        return layout;

    }

    private String FormatadorDecimal(Double valor){

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        return decimalFormat.format(valor);

    }

    /*
    public void CarregarImagem() {

        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(ApiFluig.KEY, ApiFluig.SECRET);
        consumer.setTokenWithSecret(ApiFluig.TOKEN, ApiFluig.TOKENSECRET);

        OkHttpClient clientFluig = new OkHttpClient.Builder()
                .addInterceptor(new SigningInterceptor(consumer))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();

        Picasso picasso = new Picasso.Builder(getActivity())
                .downloader(new OkHttp3Downloader(clientFluig))
                .build();

        String urlProfile = "http://fluig.acalhomecenter.com.br/social/api/rest/social/image/profile/" + usuarioLogado.get("login") + "/MEDIUM_PICTURE";

        picasso.load(urlProfile)
                .error(R.drawable.participante)
                .fit()
                .centerCrop()
                .into(profile);

    }

     */


}
