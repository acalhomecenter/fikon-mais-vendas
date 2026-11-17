package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.Estoque;

public class EstoqueAdapter extends BaseAdapter {

    private Context context;
    private final List<Estoque> estoques;
    private Estoque estoque;

    public EstoqueAdapter(Context context, List<Estoque> estoques) {

        this.context = context;
        this.estoques = estoques;

    }

    @Override
    public int getCount() {
        return estoques == null ? 0 : estoques.size();
    }

    @Override
    public Object getItem(int position) {
        return estoques.get(position);
    }

    @Override
    public long getItemId(int position) {
        Estoque estoque = estoques.get(position);
        return estoque.EstoqueFikon
                ? estoque.FikonEstabelecimentoChave : estoque.CodFil;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        estoque = estoques.get(position);

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.adapter_estoque, parent, false);
        }


        DecimalFormat df = new DecimalFormat("0.00");
        TextView filial = view.findViewById(R.id.adp_filial);
        filial.setText(estoque.Filial);


        TextView disponivel = view.findViewById(R.id.adp_disponivel);
        disponivel.setText(df.format(parseNumber(estoque.Disponivel)));


        TextView fisico = view.findViewById(R.id.adp_fisico);
        fisico.setText(df.format(parseNumber(estoque.Fisico)));

        TextView reserva = view.findViewById(R.id.adp_reserva);
        reserva.setText(df.format(parseNumber(estoque.Reserva)));

        TextView transito = view.findViewById(R.id.adp_transito);
        transito.setText(df.format(parseNumber(estoque.Transito)));

        TextView futuro = view.findViewById(R.id.adp_futuro);
        futuro.setText(df.format(parseNumber(estoque.Futuro)));

        TextView mostruario = view.findViewById(R.id.adp_mostruario);
        mostruario.setText(df.format(parseNumber(estoque.Mostruario)));

        TextView previsao = view.findViewById(R.id.adp_previsao);
        if (estoque.Previsao == null || estoque.Previsao.equals("0001-01-01T00:00:00")) {

            previsao.setText((""));
        } else {


            DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date date = entrada.parse(estoque.Previsao);
                previsao.setText(("Previsão de chegada " + formato.format(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        ImageView imagemLOTE = view.findViewById(R.id.lote);
        imagemLOTE.setVisibility(View.GONE);
        if (estoque.Lotes != null && !estoque.Lotes.isEmpty()
                && estoque.Lotes.get(0) != null
                && estoque.Lotes.get(0).Lote != null
                && !estoque.Lotes.get(0).Lote.isEmpty()
                && parseNumber(estoque.Disponivel) != 0) {
            imagemLOTE.setImageResource(R.drawable.ic_lote);
            imagemLOTE.setVisibility(View.VISIBLE);
        }


        ImageView imagem = view.findViewById(R.id.ponta);
        imagem.setVisibility(View.GONE);
        if (estoque.Pontas != null) {
            if (estoque.Pontas.isEmpty() || parseNumber(estoque.Disponivel) == 0) {


            } else {

                if (estoque.Pontas.get(0) != null && estoque.Pontas.get(0).Codfil == estoque.CodFil) {

                    imagem.setImageResource(R.drawable.ic_ponta);
                    imagem.setVisibility(View.VISIBLE);

                } else {


                }

            }
        }
        return view;


    }

    private double parseNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0d;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            Log.e("EstoqueAdapter", "Invalid stock quantity: " + value, e);
            return 0d;
        }
    }


}
