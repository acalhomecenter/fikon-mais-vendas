package br.com.acalfortaleza.fikonmais.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.dao.OrcamentoAlteradoDao;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.models.Orcamento;

public class OrcamentoAdapter extends RecyclerView.Adapter<OrcamentoAdapter.ViewHolder> implements Filterable {

    private Context context;
    public static List<Orcamento> mList;
    public static List<Orcamento> mListFull;
    private Orcamento orcamento;
    private int qtdPendente = 0;
    private int qtdVencido = 0;
    private int qtdConcluido = 0;
    private OrcamentoHelper helper;
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
    private OrcamentoAlteradoDao orcamentoAlteradoDao;


    public OrcamentoAdapter(Context context, List<Orcamento> mList) {
        this.context = context;
        this.mList = mList;
        this.mListFull = new ArrayList<>(mList);
    }


    @NonNull
    @Override
    public OrcamentoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        handler = new Handler();
        helper = new OrcamentoHelper();
        loginHelper = new LoginHelper(context);
        usuarioLogado = loginHelper.GetUsuarioSalvo();
        orcamentoAlteradoDao = new OrcamentoAlteradoDao(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_orcamento, parent, false);
        OrcamentoAdapter.ViewHolder holder = new OrcamentoAdapter.ViewHolder(view);

        if(usuarioLogado.get("isgerente").equals("SIM")){
            isGerente = true;
        }else{
            isGerente = false;
        }

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final OrcamentoAdapter.ViewHolder holder, final int position) {


        orcamento = mList.get(position);



        holder.txt_Numorc.setText("Nº Orc.: " + (dfInteiro.format(orcamento.getNumorc())));
        
        if (orcamentoAlteradoDao == null) {
            orcamentoAlteradoDao = new OrcamentoAlteradoDao(context);
        }
        if (orcamento != null && orcamento.getNumorc() > 0) {
            boolean emCache = orcamentoAlteradoDao.possuiItensEmCache(dfInteiro.format(orcamento.getNumorc()));
            if (emCache) {
                holder.img_cache_sync.setVisibility(View.VISIBLE);
            } else {
                holder.img_cache_sync.setVisibility(View.GONE);
            }
        } else {
            holder.img_cache_sync.setVisibility(View.GONE);
        }
        holder.txt_Cliente.setText(orcamento.getCliente().getNomcli());
        holder.txt_consultor.setText("Consultor: " + orcamento.getConsultor());

        if (orcamento.getCliente().getNatjur().equals("F")) {
            holder.img_cliente.setImageResource(R.drawable.ic_pessoaf);
            holder.txt_Cpf.setText("Cpf: " + orcamento.getCliente().getCgccpf().substring(0, 3) + "." + orcamento.getCliente().getCgccpf().substring(3, 6) + "." + orcamento.getCliente().getCgccpf().substring(6, 9) + "-" + orcamento.getCliente().getCgccpf().substring(9, 11));
        } else {

            holder.img_cliente.setImageResource(R.drawable.ic_pessoaj);
            holder.txt_Cpf.setText("Cnpj: " + orcamento.getCliente().getCgccpf().substring(0, 2) + "." + orcamento.getCliente().getCgccpf().substring(2, 5) + "." + orcamento.getCliente().getCgccpf().substring(5, 8) + "/" + orcamento.getCliente().getCgccpf().substring(8, 12) + "-" + orcamento.getCliente().getCgccpf().substring(12, 14));
        }

        if (orcamento.getCliente().getSexo().equals("F")) {
            holder.img_cliente.setImageResource(R.drawable.ic_pessoam);
        }


        if(orcamento.getLink_Carrinho() !=  null && !orcamento.getLink_Carrinho().equals("")){
            holder.img_carrinho.setVisibility(View.VISIBLE);
        }else{
            holder.img_carrinho.setVisibility(View.INVISIBLE);
        }


        // Calcula o frete exibido: frete base (VlFrete ou VlFreteCalculado) + serviços em cache
        double servicosCache = 0.0d;
        try {
            if (orcamento != null && orcamento.getNumorc() > 0) {
                int numOrc = (int) orcamento.getNumorc();
                servicosCache = br.com.acalfortaleza.fikonmais.popup.PopupOrcFechamento.servicosFretePorOrcamento.getOrDefault(numOrc, 0.0d);
            }
        } catch (Exception ex) {
            servicosCache = 0.0d;
        }

        double displayedFrete;
        if (orcamento.getVlFrete() > 0.00d && orcamento.getVlFreteCalculado() == 0) {
            displayedFrete = orcamento.getVlFrete() + servicosCache;
        } else {
            displayedFrete = orcamento.getVlFreteCalculado() + servicosCache;
        }

        holder.txt_Total.setText("Total: " + real.format(orcamento.getVlTotal() + displayedFrete));


        if (orcamento.getNumorcGemco() > 0 && orcamento.getNumpedven() > 0) {
            holder.img_indicador.setImageResource(R.drawable.fundo_bt_flutuante_blue);
        }


        if (orcamento.getStatus().equals("Pendente")) {

            holder.img_indicador.setImageResource(R.drawable.ic_indicador_pendentes);

        }

        if (orcamento.getStatus().equals("Vencido")) {

            holder.img_indicador.setImageResource(R.drawable.ic_indicador_vencidos);

        }

        if (orcamento.getStatus().equals("Concluido")) {

            holder.img_indicador.setImageResource(R.drawable.ic_indicador_concluidos);

        }


        if (orcamento.getPmargRent() > 0) {
            holder.txt_margem.setText("Margem: " + df.format(orcamento.getPmargRent()) + "%");
        } else {
            holder.txt_margem.setText("Margem: 0%");
        }

        if (orcamento.getPmargRent() >= 32.7) {
            holder.txt_margem.setTextColor(Color.parseColor("#00bfa5"));
        }
        if (orcamento.getPmargRent() >= 31.7 && orcamento.getPmargRent() < 32.7) {
            holder.txt_margem.setTextColor(Color.parseColor("#C8671E"));
        }

        if (orcamento.getPmargRent() < 31.7) {
            holder.txt_margem.setTextColor(Color.parseColor("#A61553"));
        }


        try {
            Date date = entrada.parse(orcamento.getDtValidade());
            holder.txt_validade.setText(("Válidade " + formato.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(orcamento.getCodArq()>0){
            holder.img_espec.setVisibility(View.VISIBLE);
        }else{
            holder.img_espec.setVisibility(View.INVISIBLE);
        }

        if(usuarioLogado.get("isgerente").equals("SIM") && !usuarioLogado.get("codfil").equals("501")){
            holder.txt_margem.setVisibility(View.VISIBLE);
        }else{
            holder.txt_margem.setVisibility(View.INVISIBLE);
        }

    }



    @Override
    public long getItemId(int position) {
        return (long) mList.get(position).getNumorc();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public int QtdPendente() {
        for (Orcamento o : mList) {
            if (o.getStatus().equals("Pendente")) {
                qtdPendente = qtdPendente + 1;
            }
        }
        return qtdPendente;
    }

    public int QtdConcluido() {
        for (Orcamento o : mList) {
            if (o.getStatus().equals("Concluido")) {
                qtdConcluido = qtdConcluido + 1;
            }
        }
        return qtdConcluido;
    }

    public int QtdVencido() {
        for (Orcamento o : mList) {
            if (o.getStatus().equals("Vencido")) {
                qtdVencido = qtdVencido + 1;
            }
        }
        return qtdVencido;
    }

    @Override
    public Filter getFilter() {
        return orcamentoFilter;
    }

    private Filter orcamentoFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Orcamento> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(mListFull);

            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Orcamento o : mListFull){
                    if (o.getStatus().toLowerCase().contains(filterPattern)){
                        filteredList.add(o);
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

    public void Order_Az(){
        Collections.sort(mList, Orcamento.ORDER_AZ);
        notifyDataSetChanged();
    }

    public void Order_Za(){
        Collections.sort(mList, Orcamento.ORDER_ZA);
        notifyDataSetChanged();
    }

    public void Order_Valor(){
        Collections.sort(mList, Orcamento.ORDER_VALOR);
        notifyDataSetChanged();
    }

    public void Order_ValorDesc(){
        Collections.sort(mList, Orcamento.ORDER_VALORDESC);
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_Numorc;
        TextView txt_validade;
        TextView txt_margem;
        TextView txt_Cliente;
        TextView txt_Total;
        TextView txt_Cpf;
        ImageView img_cliente;
        ImageView img_indicador;
        TextView txt_consultor;
        ImageView img_carrinho;
        ImageView img_espec;
        ImageView img_cache_sync;


        public ViewHolder(View itemView) {
            super(itemView);

            txt_Numorc = itemView.findViewById(R.id.orc_adp_numorc);
            txt_validade = itemView.findViewById(R.id.orc_adp_txtvalidade);
            txt_Cliente = itemView.findViewById(R.id.orc_adp_txtcliente);
            txt_Total = itemView.findViewById(R.id.orc_adp_txttotal);
            txt_Cpf = itemView.findViewById(R.id.orc_adp_txtcpf);
            img_indicador = itemView.findViewById(R.id.orc_adp_indicador);
            txt_margem = itemView.findViewById(R.id.orc_adp_txtmargem);
            img_cliente = itemView.findViewById(R.id.orc_adp_imgCliente);
            txt_consultor = itemView.findViewById(R.id.orc_adp_txtconsultor);
            img_carrinho = itemView.findViewById(R.id.orc_adp_imgCarrinho);
            img_espec = itemView.findViewById(R.id.orc_adp_indicadorEsp);
            img_cache_sync = itemView.findViewById(R.id.orc_adp_imgCacheSync);




        }


    }
}


