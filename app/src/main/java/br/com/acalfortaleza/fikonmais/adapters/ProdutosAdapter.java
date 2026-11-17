package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnItemClickListenerHack;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnLongClickListenerHack;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.models.ProdutoVtex;

public class ProdutosAdapter extends RecyclerView.Adapter<ProdutosAdapter.ViewHolder> implements Filterable {

    private Context context;
    public static List<ProdutoGemco> mList;
    public static List<ProdutoGemco> mListFull;
    private String color;
    private RecyclerViewOnItemClickListenerHack mRecyclerViewOnItemClickListenerHack;
    private RecyclerViewOnLongClickListenerHack mRecyclerViewOnLongClickListenerHack;
    private Picasso picasso;
    final DecimalFormat df = new DecimalFormat("0.00");
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;


    public ProdutosAdapter(Context context, List<ProdutoGemco> produtos) {

        this.context = context;
        this.mList = produtos;
        this.mListFull = new ArrayList<>(produtos);

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_produto, parent, false);
        ViewHolder holder = new ViewHolder(view);

        loginHelper = new LoginHelper(context);
        usuarioLogado = loginHelper.GetUsuarioSalvo();

        picasso = new Picasso.Builder(context)
                .build();

        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final ProdutoGemco produto = mList.get(position);



            color = "#757575";

            if (produto.Situacao != null && produto.Situacao.equals("EN")) {

                color = "#0188CE";
            }

            if (produto.Situacao != null && produto.Situacao.equals("IN")) {

                color = "#CF832F";
            }

            if (produto.Situacao != null && produto.Situacao.equals("PR")) {

                color = "#579A67";
            }


            if (produto.Situacao != null && produto.Situacao.equals("LC")) {

               color = "#ff00c0";
             }


            boolean temComissaoIncentivada = produto.PercentualComissao > 0
                    || (produto.Incentivado != null && produto.Incentivado.trim().equalsIgnoreCase("S"));
            boolean temComissaoAcelerada = produto.PercIncentivo > 0;

            if (temComissaoIncentivada) {
                holder.imgSelecao.setVisibility(View.VISIBLE);
                holder.imgSelecao.setImageResource(R.drawable.ic_img_selecao);
            } else if (produto.BloqueioDesc != null && produto.BloqueioDesc.equals("S")) {
                holder.imgSelecao.setVisibility(View.VISIBLE);
                holder.imgSelecao.setImageResource(R.drawable.desconto_produto);
            } else {
                holder.imgSelecao.setVisibility(View.GONE);
            }

            holder.imgAcelerado.setVisibility(temComissaoAcelerada ? View.VISIBLE : View.GONE);
            if (temComissaoAcelerada) {
                holder.imgAcelerado.setImageResource(R.drawable.ic_img_acelerado);
            }



        holder.codigo.setText(String.valueOf(produto.Codigo));
            holder.codigo.setTextColor(Color.parseColor(color));


            holder.descricao.setText((produto.Descricao));
            holder.descricao.setTextColor(Color.parseColor(color));


            holder.linha.setText("LINHA: " + produto.Linha);
            holder.linha.setTextColor(Color.parseColor(color));

            holder.fornecedor.setText("FORNECEDOR: " + produto.Fornecedor);
            holder.fornecedor.setTextColor(Color.parseColor(color));

            holder.comprador.setText("COMPRADOR: " + produto.Comprador);
            holder.comprador.setTextColor(Color.parseColor(color));

            NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            holder.preco.setText(real.format(produto.Preco));
            holder.preco.setTextColor(Color.parseColor(color));

        holder.precoDe.setText("");
        holder.precoDe.setVisibility(View.GONE);


            holder.un.setText(produto.Unidade);
            holder.un.setTextColor(Color.parseColor(color));



                String urlImagem = produto.Imagem;
                if (produto.Imagens != null) {
                    for (ProdutoVtex.Images imagem : produto.Imagens) {
                        if (imagem != null && imagem.Principal
                                && imagem.ImageUrl != null && !imagem.ImageUrl.trim().isEmpty()) {
                            urlImagem = imagem.ImageUrl;
                            break;
                        }
                    }
                }

                if (urlImagem == null || urlImagem.trim().isEmpty()) {
                    holder.imagem.setImageResource(R.drawable.semimagem);
                }else {
                    picasso.load(urlImagem)
                            .fit()
                            .error(R.drawable.semimagem)
                            .centerInside()
                            .into(holder.imagem);
                }


        double vlEncargos;
        double vlIcms;
        double margem;
        double custo;
        double vlTotal;



        vlTotal = (produto.Preco);

        vlEncargos = (vlTotal*produto.Encargos)/100;
        if(produto.IcmsRed==0) {
            vlIcms = (vlTotal * produto.Icms) / 100;
        }else{
            vlIcms = ((vlTotal- (vlTotal* produto.IcmsRed) / 100)
                    * produto.Icms) / 100;
        }
        custo = produto.Custo;

        //calculo anterior
        //margem = ((vlTotal - vlEncargos - vlIcms - custo) / (vlTotal - vlEncargos - vlIcms)) * 100;

        margem = ((vlTotal - vlEncargos - vlIcms - custo) / (vlTotal)) * 100;
        holder.margem.setText(df.format(margem)+"%");

        TextView comissaoLabel = holder.comissaoLabel;
        TextView comissaoVal = holder.comissaoVal;
        String textoComissao = montarTextoComissao(produto);
        boolean temComissao = !textoComissao.isEmpty();
        comissaoVal.setText(textoComissao);
        comissaoVal.setTextColor(Color.parseColor("#00bfa5"));
        comissaoLabel.setVisibility(View.GONE);
        comissaoVal.setVisibility(temComissao ? View.VISIBLE : View.GONE);

        if(usuarioLogado.get("isgerente").equals("SIM") && !usuarioLogado.get("codfil").equals("501")){
            holder.margem.setVisibility(View.VISIBLE);
        }else{
            holder.margem.setVisibility(View.INVISIBLE);
        }

        if (margem >= 32.7) {
            holder.margem.setTextColor(Color.parseColor("#00bfa5"));
        }
        if (margem >= 31.7 && margem < 32.7) {
            holder.margem.setTextColor(Color.parseColor("#C8671E"));
        }

        if (margem < 31.7) {
            holder.margem.setTextColor(Color.parseColor("#A61553"));
        }




    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).Codigo;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void addListitem(ProdutoGemco p, int position){
        mList.add(p);
        notifyItemInserted(position);

    }

    public void setRecyclerViewOnItemClickListenerHack(RecyclerViewOnItemClickListenerHack r) {
        mRecyclerViewOnItemClickListenerHack = r;
    }


    public void setRecyclerViewOnLongClickListenerHack(RecyclerViewOnLongClickListenerHack r) {
        mRecyclerViewOnLongClickListenerHack = r;
    }

    @Override
    public Filter getFilter() {
        return pedidosFilter;
    }

    private Filter pedidosFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ProdutoGemco> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(mListFull);

            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(ProdutoGemco p : mListFull){
                    if (p.Descricao.toLowerCase().contains(filterPattern)){
                        filteredList.add(p);
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


    public void Order_Az() {
        Collections.sort(mList, ProdutoGemco.ORDER_AZ);
        notifyDataSetChanged();
    }

    public void Order_Za() {
        Collections.sort(mList, ProdutoGemco.ORDER_ZA);
        notifyDataSetChanged();
    }

    public void Order_Valor() {
        Collections.sort(mList, ProdutoGemco.ORDER_VALOR);
        notifyDataSetChanged();
    }

    public void Order_ValorDesc() {
        Collections.sort(mList, ProdutoGemco.ORDER_VALORDESC);
        notifyDataSetChanged();
    }

    private String montarTextoComissao(ProdutoGemco produto) {
        if (produto == null) {
            return "";
        }

        double comissao = produto.PercentualComissao > 0 ? produto.PercentualComissao : 0.0;
        double premiacao = produto.PercIncentivo > 0 ? produto.PercIncentivo : 0.0;
        boolean filial3 = usuarioLogado != null && "3".equals(usuarioLogado.get("codfil"));

        if (filial3) {
            return comissao > 0 ? "Comissão: " + df.format(comissao) + "%" : "";
        }

        if (comissao <= 0 && premiacao <= 0) {
            return "";
        }

        double total = comissao + premiacao;
        return "Comissão: " + df.format(comissao) + "%\n"

                + "Premiação Acelerados: " + df.format(premiacao) + "%\n"
                + "_______________________\n"
                + "Total:     " + df.format(total) + "%";
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView codigo;
        private TextView descricao;
        private TextView linha;
        private TextView fornecedor;
        private TextView comprador;
        private TextView precoDe;
        private TextView preco;
        private TextView un;
        private ImageView imagem;
        private ImageView imgSelecao;
        private ImageView imgAcelerado;
        private TextView margem;
        private TextView comissaoLabel;
        private TextView comissaoVal;

        public ViewHolder(View itemView) {
            super(itemView);

            codigo = itemView.findViewById(R.id.adp_pro_codigo);
            descricao = itemView.findViewById(R.id.adp_pro_descricao);
            linha = itemView.findViewById(R.id.adp_pro_linha);
            fornecedor = itemView.findViewById(R.id.adp_pro_fornecedor);
            comprador = itemView.findViewById(R.id.adp_pro_comprador);
            precoDe = itemView.findViewById(R.id.adp_pro_preco_de);
            preco = itemView.findViewById(R.id.adp_pro_preco);
            un = itemView.findViewById(R.id.adp_pro_un);
            imagem = itemView.findViewById(R.id.adp_pro_imagem);
            imgSelecao = itemView.findViewById(R.id.adp_pro_imgSelecao);
            imgAcelerado = itemView.findViewById(R.id.adp_pro_imgAcelerado);
            margem = itemView.findViewById(R.id.adp_pro_margem);
            comissaoLabel = itemView.findViewById(R.id.adp_pro_comissao_label);
            comissaoVal = itemView.findViewById(R.id.adp_pro_comissao_val);

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
