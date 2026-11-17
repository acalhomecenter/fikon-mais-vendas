package br.com.acalfortaleza.fikonmais.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.FaturamentoHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.models.FaturamentoLinha;
import br.com.acalfortaleza.fikonmais.models.Orcamento;

public class FaturamentoLinhaAdapter extends RecyclerView.Adapter<FaturamentoLinhaAdapter.ViewHolder> implements Filterable {

    private Context context;
    public static List<FaturamentoLinha> mList;
    public static List<FaturamentoLinha> mListFull;
    private FaturamentoLinha faturamentoLinha;
    private int qtdAtingido = 0;
    private int qtdnaoAtingido = 0;
    private int qtdSemMeta = 0;
    private FaturamentoHelper helper;
    private ProgressDialog dialog;
    private Handler handler;
    private String retorno;
    final NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    final DecimalFormat df = new DecimalFormat("0.00");
    final DecimalFormat dfInteiro = new DecimalFormat("0");
    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private boolean isGerente;


    public FaturamentoLinhaAdapter(Context context, List<FaturamentoLinha> mList) {
        this.context = context;
        this.mList = mList;
        this.mListFull = new ArrayList<>(mList);
    }



    @Override
    public Filter getFilter() {
        return faturamentoFilter;
    }

    @NonNull
    @Override
    public FaturamentoLinhaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        handler = new Handler();
        helper = new FaturamentoHelper();
        loginHelper = new LoginHelper(context);
        usuarioLogado = loginHelper.GetUsuarioSalvo();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_faturamento_linha, parent, false);
        FaturamentoLinhaAdapter.ViewHolder holder = new FaturamentoLinhaAdapter.ViewHolder(view);

        if(usuarioLogado.get("isgerente").equals("SIM")){
            isGerente = true;
        }else{
            isGerente = false;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FaturamentoLinhaAdapter.ViewHolder holder, int position) {

        faturamentoLinha = mList.get(position);

        holder.txt_Codlinha.setText("Cód. Linha: " + (dfInteiro.format(faturamentoLinha.getCodlinha())));
        if (faturamentoLinha.getPercMeta() > 0) {
            holder.txt_percmeta.setText("Percentual: " + df.format(faturamentoLinha.getPercMeta()) + "%");
        } else {
            holder.txt_percmeta.setText("Percentual: 0%");
        }
        holder.txt_Linha.setText(faturamentoLinha.getLinha());

        double valorFalta = faturamentoLinha.getMeta() - faturamentoLinha.getVlLiquida();
        if(valorFalta>0){
            holder.txt_Falta.setText("Falta: " + real.format(valorFalta));
        }else{
            holder.txt_Falta.setText("");
        }

        holder.txt_Meta.setText("Meta: " + real.format(faturamentoLinha.getMeta()));


        holder.txt_Faturamento.setText("Pedido: " + real.format(faturamentoLinha.getVlLiquida()));

        //holder.img_indicador.setImageResource(R.drawable.fundo_bt_flutuante_blue);

        switch (faturamentoLinha.getStatus()) {
            case "ATINGIDO":
                holder.img_indicador.setImageResource(R.drawable.fundo_bt_flutuante);
                break;

            case "NÃO ATINGIU":
                holder.img_indicador.setImageResource(R.drawable.fundo_bt_flutuante_orange);
                break;

            case "S/META":
                holder.img_indicador.setImageResource(R.drawable.fundo_bt_flutuante_red);
                break;
            default:  holder.img_indicador.setImageBitmap(null);

        }




    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public int QtdAtingido() {
        for (FaturamentoLinha f : mList) {
            if (f.getStatus().equals("ATINGIDO")) {
                qtdAtingido = qtdAtingido + 1;
            }
        }
        return qtdAtingido;
    }

    public int QtdNaoAtingido() {
        for (FaturamentoLinha f : mList) {
            if (f.getStatus().equals("NÃO ATINGIU")) {
                qtdnaoAtingido = qtdnaoAtingido + 1;
            }
        }
        return qtdnaoAtingido;
    }

    public int QtdeSemMeta() {
        for (FaturamentoLinha f : mList) {
            if (f.getStatus().equals("S/META")) {
                qtdSemMeta = qtdSemMeta + 1;
            }
        }
        return qtdSemMeta;
    }


    private Filter faturamentoFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FaturamentoLinha> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(mListFull);

            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(FaturamentoLinha f : mListFull){
                    if (f.getStatus().toLowerCase().contains(filterPattern)){
                        filteredList.add(f);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }


        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mList.clear();
            mList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };


        class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_Codlinha;
        TextView txt_Linha;
        TextView txt_Meta;
        TextView txt_Falta;
        TextView txt_Faturamento;
        TextView txt_percmeta;
        ImageView img_indicador;



        public ViewHolder(View itemView) {
            super(itemView);

            txt_Codlinha = itemView.findViewById(R.id.adap_fat_codlinha);
            txt_Linha = itemView.findViewById(R.id.adap_fat_linha);
            txt_Meta = itemView.findViewById(R.id.adap_fat_meta);
            txt_Falta= itemView.findViewById(R.id.adap_fat_valorfalta);
            txt_Faturamento = itemView.findViewById(R.id.adap_fat_venda);
            txt_percmeta = itemView.findViewById(R.id.adap_fat_perc);
            img_indicador = itemView.findViewById(R.id.adap_fat_indicador);





        }


    }

}
