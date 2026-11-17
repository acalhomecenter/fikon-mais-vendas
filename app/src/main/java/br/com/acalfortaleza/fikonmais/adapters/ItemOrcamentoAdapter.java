package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnLongClickListenerHack;
import br.com.acalfortaleza.fikonmais.modal.ModalItens;
import br.com.acalfortaleza.fikonmais.models.AmbienteProduto;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;

public class ItemOrcamentoAdapter extends RecyclerView.Adapter<ItemOrcamentoAdapter.ViewHolder> implements ModalItens.ActionListener {

    private Context context;
    public  static List<ItemOrcamento> mList;
    private ItemOrcamento itemOrcamento;
    private AlertDialog alertaDialog;
    private RecyclerViewOnLongClickListenerHack mRecyclerViewOnLongClickListenerHack;
    private Image image;
    public static ArrayList<Image> listaImagens;
    private int index;
    private boolean isGerente;

    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;
    private ProdutoHelper helper;
    private android.os.Handler handler;
    private static HashMap<Integer, ProdutoGemco> comissaoCache;
    private static java.util.HashSet<Integer> comissaoFetching;
    private static java.util.HashSet<Integer> comissaoSemResultado;




    public ItemOrcamentoAdapter(Context context, List<ItemOrcamento> mList, boolean isGerente) {
        this.context = context;
        this.mList = mList;
        this.isGerente = isGerente;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_item_orc, parent,false);
        ViewHolder holder = new ViewHolder(view);
        listaImagens = new ArrayList<Image>();
        loginHelper = new LoginHelper(context);
        usuarioLogado = loginHelper.GetUsuarioSalvo();
        helper = new ProdutoHelper();
        handler = new android.os.Handler();
        if (comissaoCache == null) {
            comissaoCache = new HashMap<>();
        }
        if (comissaoFetching == null) {
            comissaoFetching = new java.util.HashSet<>();
        }
        if (comissaoSemResultado == null) {
            comissaoSemResultado = new java.util.HashSet<>();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        itemOrcamento =  mList.get(position);

        if(itemOrcamento.getBloqueioDesc() != null && itemOrcamento.getBloqueioDesc().equals("S")){
            holder.img_desconto.setVisibility(View.VISIBLE);
        }else{
            holder.img_desconto.setVisibility(View.INVISIBLE);
        }

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        final DecimalFormat df = new DecimalFormat("0.00");
        final DecimalFormat dfdesconto = new DecimalFormat("0.00000");
        double descontoUnitarioExibicao = getValorDescontoUnitarioExibicao(itemOrcamento);
        double descontoPercentualExibicao = getPercentualDescontoExibicao(itemOrcamento);
        holder.txt_Codigo.setText(String.valueOf(itemOrcamento.getCoditprod()));
        holder.txt_Descricao.setText(itemOrcamento.getDescricao());
        holder.txt_Barcode.setText(itemOrcamento.getCodbarras());
        holder.txt_Preco.setText("Preço: "+real.format(getPrecoUnitExibicao(itemOrcamento)));
        holder.txt_Qtd.setText(df.format(itemOrcamento.getQtcomp()));
        bindComissao(holder, itemOrcamento, position);
        if(descontoUnitarioExibicao > 0){
            holder.txt_PercDesc.setTextColor(Color.parseColor("#FF4081"));
            holder.txt_Vldesc.setTextColor(Color.parseColor("#FF4081"));
        }else{
            holder.txt_PercDesc.setTextColor(Color.parseColor("#757575"));
            holder.txt_Vldesc.setTextColor(Color.parseColor("#757575"));
        }

        holder.txt_PercDesc.setText("% Desc: "+dfdesconto.format(descontoPercentualExibicao)+" "+itemOrcamento.getMotivoDesc());
        holder.txt_Vldesc.setText("Vl Desc: "+real.format(descontoUnitarioExibicao * itemOrcamento.getQtcomp()));


        holder.txt_Vltotal.setText("Total: "+real.format(getValorLiquidoTotalExibicao(itemOrcamento)));

        if(itemOrcamento.getImagem()!=null && !itemOrcamento.getImagem().equals("")) {
            String ref = itemOrcamento.getImagem();
            Log.d("ORC_SCREEN_IMG", "COD " + itemOrcamento.getCoditprod() + " exibindo ref " + ref);
            if (ref.startsWith("http")) {
                Picasso.get().load(ref).placeholder(R.drawable.semimagem)
                        .fit()
                        .centerInside()
                        .into(holder.imagem, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError(Exception e) {
                                holder.imagem.setImageResource(R.drawable.semimagem);
                            }
                        });
            } else {
                File f = new File(ref);
                if (f.exists()) {
                    Picasso.get().load(f).placeholder(R.drawable.semimagem)
                            .fit()
                            .centerInside()
                            .into(holder.imagem, new Callback() {
                                @Override
                                public void onSuccess() {
                                }
                                @Override
                                public void onError(Exception e) {
                                    holder.imagem.setImageResource(R.drawable.semimagem);
                                }
                            });
                } else {
                    holder.imagem.setImageResource(R.drawable.semimagem);
                }
            }
        }else
        {
            holder.imagem.setImageResource(R.drawable.semimagem);
        }

         double vlEncargos;
         double vlIcms;
         double margem;
         double custo;
         double vlTotal;

                vlTotal = getValorLiquidoTotalExibicao(itemOrcamento).doubleValue();

                vlEncargos = (vlTotal*itemOrcamento.getEncargos())/100;
            if(itemOrcamento.getIcmsRed()==0) {
                vlIcms = (vlTotal * itemOrcamento.getIcms()) / 100;
            }else{
                vlIcms = ((vlTotal- (vlTotal* itemOrcamento.getIcmsRed()) / 100)
                         * itemOrcamento.getIcms()) / 100;
            }
            custo = itemOrcamento.getCusto()*itemOrcamento.getQtcomp();

               //calculo anterior
               //margem = ((vlTotal - vlEncargos - vlIcms - custo) / (vlTotal - vlEncargos - vlIcms)) * 100;

             if( itemOrcamento.getSituacao().equals("SV") ){
                 margem = 0.00d;
             }else{
                 margem = ((vlTotal - vlEncargos - vlIcms - custo) / (vlTotal)) * 100;
             }

               mList.get(position).setPmargRent(margem);



          if(margem>=36){

              holder.img_indicador.setImageResource(R.drawable.fundo_bt_flutuante);



          }
          if(margem>=34 && margem<36){

              holder.img_indicador.setImageResource(R.drawable.fundo_bt_flutuante_orange);
          }

        if(margem<34){

            holder.img_indicador.setImageResource(R.drawable.fundo_bt_flutuante_red);
        }


        if(isGerente && !usuarioLogado.get("codfil").equals("501")){
            holder.txt_margem.setVisibility(View.VISIBLE);
            holder.txt_margem.setText(df.format(margem)+"%");
        }else{
            holder.txt_margem.setVisibility(View.INVISIBLE);
            holder.txt_margem.setText(df.format(margem)+"%");
        }


        Bitmap bitmap = ((BitmapDrawable) holder.imagem.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        try {
            image = Image.getInstance(stream.toByteArray());
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            listaImagens.add(image);
        }


        if(itemOrcamento.getPrecoConc() != null && itemOrcamento.getPrecoConc().size()>0){
            holder.img_concorrente.setVisibility(View.VISIBLE);
        }else{
            holder.img_concorrente.setVisibility(View.INVISIBLE);
        }



        if(itemOrcamento.getSituacao() != null){


        switch (itemOrcamento.getSituacao()) {
            case "PR":
                Bitmap bitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.promocao);

                holder.img_situacao.setImageBitmap(bitmap3);

                break;

            case "IN":
                Bitmap bitmap4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.inativo);

                holder.img_situacao.setImageBitmap(bitmap4);
                break;

            case "EN":
                Bitmap bitmap5 = BitmapFactory.decodeResource(context.getResources(), R.drawable.encomenda);

                holder.img_situacao.setImageBitmap(bitmap5);
                break;


            case "LC":
                Bitmap bitmap6 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lojaconceito);

                holder.img_situacao.setImageBitmap(bitmap6);
                break;
            default:  holder.img_situacao.setImageBitmap(null);

        }

        }else{
            holder.img_situacao.setImageBitmap(null);
        }

        if(itemOrcamento!=null && itemOrcamento.getListAmbientesProdutos()!=null && itemOrcamento.getListAmbientesProdutos().size()>0){
            holder.img_ambientes.setVisibility(View.VISIBLE);
        }
        else {
            holder.img_ambientes.setVisibility(View.INVISIBLE);
        }

        double pesoTotal = BigDecimal.valueOf(itemOrcamento.getPeso() * itemOrcamento.getQtcomp()).setScale(2, RoundingMode.FLOOR).doubleValue();
        holder.txt_pesoTotal.setText(String.valueOf(pesoTotal).replace(".",",").concat(" Kg"));

        String tipo = itemOrcamento.getUpsellTipo();
        if ("UPSELL".equals(tipo)) {
            holder.strip.setVisibility(View.VISIBLE);
            holder.strip.setBackgroundColor(Color.parseColor("#FFD600"));
            holder.stripStar.setVisibility(View.GONE);
            holder.stripLabel.setVisibility(View.VISIBLE);
            holder.stripLabel.setText("SUGERIDO");
            holder.stripLabel.setTextColor(Color.parseColor("#000000"));
        } else if ("PRINCIPAL".equals(tipo)) {
            holder.strip.setVisibility(View.VISIBLE);
            holder.strip.setBackgroundColor(Color.parseColor("#00C853"));
            holder.stripStar.setVisibility(View.VISIBLE);
            holder.stripLabel.setVisibility(View.GONE);
        } else {
            holder.strip.setVisibility(View.GONE);
            holder.stripStar.setVisibility(View.GONE);
            holder.stripLabel.setVisibility(View.GONE);
        }

    }
    @Override
    public long getItemId(int position) {
        return (long) mList.get(position).getNumorc();
    }

    @Override
    public int getItemCount() {
        return (mList != null) ? mList.size() : 0;
    }

    private double getPrecoUnitExibicao(ItemOrcamento itemOrcamento) {
        // No orcamento, a condicao A PRAZO deve impactar apenas o total do item.
        // Inflar o preco unitario quebra a leitura do desconto/promocao.
        return itemOrcamento.getPrecoUnit();
    }

    private BigDecimal getPrecoLiquidoUnitarioExibicao(ItemOrcamento itemOrcamento) {
        return BigDecimal.valueOf(getPrecoUnitExibicao(itemOrcamento))
                .subtract(BigDecimal.valueOf(itemOrcamento.getVlDescItem()));
    }

    private double getValorDescontoUnitarioExibicao(ItemOrcamento itemOrcamento) {
        double desconto = getPrecoUnitExibicao(itemOrcamento) - getPrecoLiquidoUnitarioExibicao(itemOrcamento).doubleValue();
        return BigDecimal.valueOf(Math.max(0.0d, desconto))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private double getPercentualDescontoExibicao(ItemOrcamento itemOrcamento) {
        double precoBase = getPrecoUnitExibicao(itemOrcamento);
        if (precoBase <= 0) {
            return 0.0d;
        }

        double percentual = (getValorDescontoUnitarioExibicao(itemOrcamento) / precoBase) * 100.0d;
        return BigDecimal.valueOf(percentual)
                .setScale(5, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private String montarTextoComissao(ItemOrcamento itemOrcamento) {
        if (itemOrcamento == null) {
            return "";
        }

        DecimalFormat df = new DecimalFormat("0.00");
        double comissao = itemOrcamento.getPercentualComissao() > 0 ? itemOrcamento.getPercentualComissao() : 0.0;
        double premiacao = itemOrcamento.getPercIncentivo() > 0 ? itemOrcamento.getPercIncentivo() : 0.0;
        boolean filial3 = usuarioLogado != null && "3".equals(usuarioLogado.get("codfil"));

        if (filial3) {
            return comissao > 0 ? "Comissão: " + df.format(comissao) + "%" : "";
        }

        if (comissao <= 0 && premiacao <= 0) {
            return "";
        }

        double total = comissao + premiacao;
      return "C: " + df.format(comissao) + "%  "
                + "P: " + df.format(premiacao) + "%\n"
                + "Total: " + df.format(total) + "%";
    }

    private CharSequence montarTextoComissaoFormatado(ItemOrcamento itemOrcamento) {
        String textoComissao = montarTextoComissao(itemOrcamento);
        if (textoComissao.isEmpty()) {
            return "";
        }

        if (usuarioLogado != null && "3".equals(usuarioLogado.get("codfil"))) {
            return textoComissao;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(textoComissao);

        int inicioPremiacao = textoComissao.indexOf("P:");
        int fimPremiacao = textoComissao.indexOf('\n');
        if (inicioPremiacao >= 0) {
            if (fimPremiacao < 0 || fimPremiacao <= inicioPremiacao) {
                fimPremiacao = textoComissao.length();
            }
            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#1976D2")), inicioPremiacao, fimPremiacao, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        int inicioTotal = textoComissao.indexOf("Total:");
        if (inicioTotal >= 0) {
            int fimTotal = textoComissao.length();
            builder.setSpan(new ForegroundColorSpan(Color.parseColor("#00bfa5")), inicioTotal, fimTotal, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new StyleSpan(Typeface.BOLD), inicioTotal, fimTotal, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return builder;
    }

    private void bindComissao(ViewHolder holder, ItemOrcamento itemOrcamento, int position) {
        aplicarComissaoCache(itemOrcamento);

        String textoComissao = montarTextoComissao(itemOrcamento);
        if (!textoComissao.isEmpty()) {
            holder.txt_Comissao.setVisibility(View.VISIBLE);
            holder.txt_Comissao.setTextColor(Color.parseColor("#00bfa5"));
            holder.txt_Comissao.setText(montarTextoComissaoFormatado(itemOrcamento));
            return;
        }

        int coditprod = itemOrcamento != null ? itemOrcamento.getCoditprod() : 0;
        if (coditprod <= 0 || comissaoSemResultado.contains(coditprod)) {
            holder.txt_Comissao.setVisibility(View.GONE);
            holder.txt_Comissao.setText("");
            return;
        }

        holder.txt_Comissao.setVisibility(View.VISIBLE);
        holder.txt_Comissao.setTextColor(Color.parseColor("#757575"));
        holder.txt_Comissao.setText("Carregando...");
        carregarComissaoAsync(itemOrcamento, position);
    }

    private void aplicarComissaoCache(ItemOrcamento itemOrcamento) {
        if (itemOrcamento == null || itemOrcamento.getCoditprod() <= 0) {
            return;
        }

        ProdutoGemco produtoCache = comissaoCache.get(itemOrcamento.getCoditprod());
        if (produtoCache == null) {
            return;
        }

        itemOrcamento.setPercentualComissao(produtoCache.PercentualComissao);
        itemOrcamento.setPercIncentivo(produtoCache.PercIncentivo);
    }

    private void carregarComissaoAsync(ItemOrcamento itemOrcamento, int position) {
        if (itemOrcamento == null) {
            return;
        }

        final int coditprod = itemOrcamento.getCoditprod();
        if (coditprod <= 0 || comissaoFetching.contains(coditprod) || comissaoSemResultado.contains(coditprod)) {
            return;
        }

        comissaoFetching.add(coditprod);
        new Thread(() -> {
            try {
                List<ProdutoGemco> produtos = helper.ConsultarProdutoPorCodigo(String.valueOf(coditprod), obterCodFilPreco());
                ProdutoGemco produto = null;
                if (produtos != null) {
                    for (ProdutoGemco p : produtos) {
                        if (p != null && p.Codigo == coditprod) {
                            produto = p;
                            break;
                        }
                    }
                    if (produto == null && !produtos.isEmpty()) {
                        produto = produtos.get(0);
                    }
                }

                if (produto != null) {
                    comissaoCache.put(coditprod, produto);
                    itemOrcamento.setPercentualComissao(produto.PercentualComissao);
                    itemOrcamento.setPercIncentivo(produto.PercIncentivo);

                    if (montarTextoComissao(itemOrcamento).isEmpty()) {
                        comissaoSemResultado.add(coditprod);
                    }
                } else {
                    comissaoSemResultado.add(coditprod);
                }
            } catch (Exception e) {
                Log.e("ComissaoItemDebug", "Falha ao consultar comissao do item orcamento " + coditprod, e);
                comissaoSemResultado.add(coditprod);
            } finally {
                comissaoFetching.remove(coditprod);
                handler.post(() -> {
                    if (position >= 0 && position < mList.size()) {
                        notifyItemChanged(position);
                    } else {
                        notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private String obterCodFilPreco() {
        if (usuarioLogado != null && usuarioLogado.get("codfil") != null && !usuarioLogado.get("codfil").trim().isEmpty()) {
            return usuarioLogado.get("codfil");
        }

        return "1";
    }

    private BigDecimal getValorLiquidoTotalExibicao(ItemOrcamento itemOrcamento) {
        return getPrecoLiquidoUnitarioExibicao(itemOrcamento)
                .multiply(BigDecimal.valueOf(itemOrcamento.getQtcomp()))
                .setScale(2, RoundingMode.FLOOR);
    }

    public ItemOrcamento getItemOrcamento(int position){
        return mList.get(position);
    }

    public void addListitem(ItemOrcamento o, int position){
        mList.add(o);
        notifyItemInserted(position);
        Orcamento_LancamentoActivity.marcarOrcamentoComoRecalculavel();
        Orcamento_LancamentoActivity.atualizaTotais(mList);
    }

    public void updateListitem(ItemOrcamento o, int position){
        mList.set(position,o);
        notifyItemChanged(position);
        Orcamento_LancamentoActivity.marcarOrcamentoComoRecalculavel();
        Orcamento_LancamentoActivity.atualizaTotais(mList);
    }

    public void replaceAllItemsPreservandoValoresCarregados(List<ItemOrcamento> itensAtualizados) {
        mList.clear();
        if (itensAtualizados != null) {
            mList.addAll(itensAtualizados);
        }
        notifyDataSetChanged();
    }

    public void setRecyclerViewOnLongClickListenerHack(RecyclerViewOnLongClickListenerHack r){
        mRecyclerViewOnLongClickListenerHack = r;
    }

    public void removeListItem(int position){
        mList.remove(position);
        notifyDataSetChanged();
        Orcamento_LancamentoActivity.marcarOrcamentoComoRecalculavel();
        Orcamento_LancamentoActivity.atualizaTotais(mList);


    }

    public void updateAmbientesProdutos(List<AmbienteProduto> listAmbientesProdutos, int position){
        mList.get(position).setListAmbientesProdutos(listAmbientesProdutos);
    }

    @Override
    public void onButtonClick(int id) {

    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView txt_Codigo;
        TextView txt_Descricao;
        TextView txt_Preco;
        ImageView imagem;
        TextView txt_Qtd;
        TextView txt_Comissao;
        TextView txt_Barcode;
        TextView txt_PercDesc;
        ImageView img_indicador;
        TextView txt_Vldesc;
        TextView txt_Vltotal;
        TextView txt_margem;
        ImageView img_situacao;

        ImageView img_concorrente;
        ImageView img_ambientes;

        ImageView img_desconto;
        TextView txt_pesoTotal;
        View strip;
        ImageView stripStar;
        TextView stripLabel;





        public ViewHolder(View itemView) {
            super(itemView);

            txt_Codigo = itemView.findViewById(R.id.adp_ped_txtCodigo);
            txt_Descricao = itemView.findViewById(R.id.adp_ped_txtDescricao);
            imagem = itemView.findViewById(R.id.adp_ped_imagem);
            txt_Preco = itemView.findViewById(R.id.adp_ped_preco);
            txt_Qtd = itemView.findViewById(R.id.adp_ped_txtQtd);
            txt_Comissao = itemView.findViewById(R.id.adp_ped_txtComissao);
            txt_Barcode = itemView.findViewById(R.id.adp_ped_txtBarcode);
            txt_PercDesc = itemView.findViewById(R.id.adp_ped_percDesc);
            txt_Vltotal = itemView.findViewById(R.id.adp_ped_vlTotal);
            txt_Vldesc = itemView.findViewById(R.id.adp_orc_vlDesc);
            img_indicador = itemView.findViewById(R.id.adp_ped_indicador);
            txt_margem = itemView.findViewById(R.id.adp_orc_txtmargem);
            img_situacao = itemView.findViewById(R.id.etiqueta);
            img_concorrente = itemView.findViewById(R.id.imgConcorrente);
            img_ambientes = itemView.findViewById(R.id.adapter_itemOrc_imgAmbientes);
            img_desconto = itemView.findViewById(R.id.adapter_itemOrc_desconto);
            txt_pesoTotal = itemView.findViewById(R.id.AdapterItemOrcamento_PesoTotal);
            strip = itemView.findViewById(R.id.adapter_itemOrc_strip);
            stripStar = itemView.findViewById(R.id.adapter_itemOrc_stripStar);
            stripLabel = itemView.findViewById(R.id.adapter_itemOrc_stripLabel);

            itemView.setOnLongClickListener(this);

        }


        @Override
        public boolean onLongClick(View view) {
            if (mRecyclerViewOnLongClickListenerHack != null) {
                mRecyclerViewOnLongClickListenerHack.onLongClickListener(view, getPosition());

            }
            return false;
        }
    }
}
