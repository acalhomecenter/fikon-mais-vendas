package br.com.acalfortaleza.fikonmais.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Environment;
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

import com.itextpdf.text.Image;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import br.com.acalfortaleza.fikonmais.interfaces.RecyclerViewOnLongClickListenerHack;
import br.com.acalfortaleza.fikonmais.models.Financeiro;
import br.com.acalfortaleza.fikonmais.models.ImagemCiashop;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Lotevencto;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;



public class ItemPedidoAdapter extends RecyclerView.Adapter<ItemPedidoAdapter.ViewHolder>{
    private static final String DEBUG_SERVER_URL = "http://10.1.5.112:7777/event";
    private static final String DEBUG_SESSION_ID = "saved-order-total";
    private static final String DEBUG_RUN_ID = "pre-fix";
    private static final String DEBUG_LOGCAT_TAG = "SavedOrderTotalDebug";

    private Context context;
    public  static List<ItemPedidos> mList;
    private ItemPedidos itemPedido;
    private AlertDialog alertaDialog;
    private RecyclerViewOnLongClickListenerHack mRecyclerViewOnLongClickListenerHack;
    private Image image;
    public static ArrayList<Image> listaImagens;
    private ProdutoHelper helper;
    private List<ImagemCiashop> imagens;
    private Picasso picasso;
    private Handler handler;
    private static HashMap<Integer, String> imageCache;
    private static java.util.HashSet<Integer> vtexFetched;
    private static java.util.HashSet<Integer> vtexFetching;
    private static HashMap<Integer, ProdutoGemco> comissaoCache;
    private static java.util.HashSet<Integer> comissaoFetching;
    private static java.util.HashSet<Integer> comissaoSemResultado;
    private List<Target> picassoTargets;
    DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isGerente;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;




    public ItemPedidoAdapter(Context context, List<ItemPedidos> mList, boolean isGerente) {
        this.context = context;
        this.mList = mList;
        this.isGerente = isGerente;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.adapter_item_ped, parent,false);
        ViewHolder holder = new ViewHolder(view);
        loginHelper = new LoginHelper(context);
        usuarioLogado = loginHelper.GetUsuarioSalvo();
        helper = new ProdutoHelper();
        handler = new Handler();
        picasso = new Picasso.Builder(context)
                .build();
        if (imageCache == null) {
            imageCache = new HashMap<>();
        }
        if (vtexFetched == null) {
            vtexFetched = new java.util.HashSet<>();
        }
        if (vtexFetching == null) {
            vtexFetching = new java.util.HashSet<>();
        }
        if (comissaoCache == null) {
            comissaoCache = new HashMap<>();
        }
        if (comissaoFetching == null) {
            comissaoFetching = new java.util.HashSet<>();
        }
        if (comissaoSemResultado == null) {
            comissaoSemResultado = new java.util.HashSet<>();
        }
        preloadDiskCache();
        picassoTargets = new ArrayList<>();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        itemPedido =  mList.get(position);

        String lote = "";

        if(itemPedido.getLoteSelecionado() != null && !itemPedido.getLoteSelecionado().equals("")  ){
            lote = lote + itemPedido.getLoteSelecionado() +" / ";
        }else{
            if(itemPedido.getLotevencto() != null && itemPedido.getLotevencto().size()>0){


                for (Lotevencto l: itemPedido.getLotevencto()){
                    if(!l.getDescricao().equals("")){
                        lote = lote + l.getDescricao() +" / ";
                    }

                }

            }

        }

         if(itemPedido.getBloqueioDesc() != null && itemPedido.getBloqueioDesc().equals("S")){
             holder.img_desconto.setVisibility(View.VISIBLE);
         }else{
             holder.img_desconto.setVisibility(View.INVISIBLE);
         }

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        final DecimalFormat df = new DecimalFormat("0.00");
        final DecimalFormat dfdesconto = new DecimalFormat("0.00000");
        double descontoUnitarioExibicao = getValorDescontoUnitarioExibicao(itemPedido);
        double descontoPercentualExibicao = getPercentualDescontoExibicao(itemPedido);
        holder.txt_Codigo.setText(String.valueOf(itemPedido.getCoditprod()));
        holder.txt_Descricao.setText(itemPedido.getDescricao());
        holder.txt_Barcode.setText(itemPedido.getCodbarras());
        holder.txt_Preco.setText(real.format(getPrecoUnitExibicao(itemPedido)));
        holder.txt_Qtd.setText(df.format(itemPedido.getQtcomp()));
        bindComissao(holder, itemPedido, position);
        if(descontoUnitarioExibicao > 0){
            holder.txt_PercDesc.setTextColor(Color.parseColor("#FF4081"));
            holder.txt_Vldesc.setTextColor(Color.parseColor("#FF4081"));
        }else{
            holder.txt_PercDesc.setTextColor(Color.parseColor("#757575"));
            holder.txt_Vldesc.setTextColor(Color.parseColor("#757575"));
        }
        String textoPercentual = "% Desc: "+dfdesconto.format(descontoPercentualExibicao)+" "+itemPedido.getMotivoDesc();
        String codigoCupom = itemPedido.getCodigoCupom();
        if ("CD".equalsIgnoreCase(itemPedido.getCodMotivo())
                && codigoCupom != null
                && !codigoCupom.trim().isEmpty()) {
            SpannableStringBuilder textoFormatado = new SpannableStringBuilder(textoPercentual);
            int inicioCodigoCupom = textoFormatado.length();
            textoFormatado.append(" / ").append(codigoCupom.trim());
            textoFormatado.setSpan(
                    new ForegroundColorSpan(Color.parseColor("#1976D2")),
                    inicioCodigoCupom,
                    textoFormatado.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.txt_PercDesc.setText(textoFormatado);
        } else {
            holder.txt_PercDesc.setText(textoPercentual);
        }
        holder.txt_Vldesc.setText("Vl Desc: "+real.format(descontoUnitarioExibicao * itemPedido.getQtcomp()));
        if (position == 0 || itemPedido.getVlDescItem() > 0 || itemPedido.getPrecoPrimario() > 0) {
            // #region debug-point G:adapter-bind
            reportDebugEvent("G", "ItemPedidoAdapter.onBindViewHolder",
                    "[DEBUG] item exibido no adapter apos refresh",
                    buildItemDebugSnapshot(position, itemPedido, getPrecoUnitExibicao(itemPedido), descontoUnitarioExibicao));
            // #endregion
        }
        String tipoCodigo = itemPedido.getTpped();
        String tipoDescricao = "";
        if (tipoCodigo != null) {
            switch (tipoCodigo) {
                case "E":
                    tipoDescricao = "Entrega";
                    break;
                case "P":
                    tipoDescricao = "Retira Posterior";
                    break;
                case "R":
                    tipoDescricao = "Retira";
                    break;
                default:
                    tipoDescricao = itemPedido.getTppedDesc() != null ? itemPedido.getTppedDesc() : ""; // Fallback
                    break;
            }
        }
        holder.txt_tpped.setText("Tipo: " + tipoDescricao);
        if(lote.equals("")){
            holder.txt_filorig.setText("Saída: "+itemPedido.getFilOrig());
        }else{
            holder.txt_filorig.setText("Saída: "+itemPedido.getFilOrig()+" / Lote: "+lote);
        }

        if(itemPedido.getCodFilTransf()>0){
          holder.txt_filtransf.setText("Transf.: "+itemPedido.getFilTransf() +" / Ped.: "+itemPedido.getNumpedtransf());
        }else{
            holder.txt_filtransf.setText("");
        }
        try {
            Date date = entrada.parse(itemPedido.getDtentrega());
            holder.txt_dtentrega.setText(("Entrega: " + formato.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }





        switch (itemPedido.getStatus()){

            case "6" :
                holder.status.setBackgroundColor(context.getResources().getColor(R.color.primary_dark));
                break;

            case "5" :
                holder.status.setBackgroundColor(Color.parseColor("#006400"));
                break;

            case "8" :
                holder.status.setBackgroundColor(Color.parseColor("#ff8c00"));
                break;

            case "3" :
                holder.status.setBackgroundColor(context.getResources().getColor(R.color.roxo));
                break;

            case "1" :
                holder.status.setBackgroundColor(context.getResources().getColor(R.color.secondary_text));
                break;

            case "2" :
                holder.status.setBackgroundColor(context.getResources().getColor(R.color.secondary_text));
                break;

        }



        final int prodCode = itemPedido.getCoditprod();
        boolean pedidoSalvo = itemPedido.getNumpedven() > 0;

        String itemRef = itemPedido.getImagem();
        if (itemRef != null && !itemRef.equals("")) {
            android.util.Log.d("PED_SCREEN_IMG", "COD " + prodCode + " exibindo ref " + itemRef);
            if (itemRef.startsWith("http")) {
                picasso.load(itemRef)
                        .fit()
                        .error(R.drawable.semimagem)
                        .centerInside()
                        .into(holder.imagem);
            } else {
                File f = new File(itemRef);
                if (f.exists()) {
                    picasso.load(f)
                            .fit()
                            .error(R.drawable.semimagem)
                            .centerInside()
                            .into(holder.imagem);
                } else {
                    holder.imagem.setImageResource(R.drawable.semimagem);
                }
            }
            ensureImageCached(prodCode, itemRef, itemPedido);
        } else if (pedidoSalvo && !vtexFetched.contains(prodCode)) {
            if (!vtexFetching.contains(prodCode)) {
                vtexFetching.add(prodCode);
                holder.imagem.setImageResource(R.drawable.semimagem);
                final ItemPedidos boundItem = itemPedido;
                new Thread(() -> {
                    try {
                        String codFilPreco = usuarioLogado != null ? usuarioLogado.get("codfil") : "";
                        List<ProdutoGemco> produtos = helper.ConsultarProdutoEstoque(String.valueOf(prodCode), codFilPreco);
                        if (produtos != null && !produtos.isEmpty()) {
                            ProdutoGemco p = produtos.get(0);
                            String url = p.Imagem != null ? p.Imagem : "";
                            final String finalUrl = url;
                            if (finalUrl != null && !finalUrl.equals("")) {
                                android.util.Log.d("PED_DB_IMG", "COD " + prodCode + " URL (DB) " + finalUrl);
                                imageCache.put(prodCode, finalUrl);
                                boundItem.setImagem(finalUrl);
                                handler.post(() -> picasso.load(finalUrl)
                                        .fit()
                                        .error(R.drawable.semimagem)
                                        .centerInside()
                                        .into(holder.imagem));
                                ensureImageCached(prodCode, finalUrl, boundItem);
                                vtexFetched.add(prodCode);
                            }
                        }
                    } catch (Exception ignored) {
                        handler.post(() -> holder.imagem.setImageResource(R.drawable.semimagem));
                    } finally {
                        vtexFetching.remove(prodCode);
                    }
                }).start();
            }
        } else {
            String cachedRef = imageCache.get(prodCode);
            if (cachedRef != null && !cachedRef.equals("")) {
                android.util.Log.d("PED_SCREEN_IMG", "COD " + prodCode + " exibindo ref do cache " + cachedRef);
                if (cachedRef.startsWith("http")) {
                    picasso.load(cachedRef)
                            .fit()
                            .error(R.drawable.semimagem)
                            .centerInside()
                            .into(holder.imagem);
                } else {
                    File f = new File(cachedRef);
                    if (f.exists()) {
                        picasso.load(f)
                                .fit()
                                .error(R.drawable.semimagem)
                                .centerInside()
                                .into(holder.imagem);
                    }
                }
                ensureImageCached(prodCode, cachedRef, itemPedido);
            } else {
                File cachedFile = new File(getCachedImagePath(prodCode));
                if (cachedFile.exists() && cachedFile.length() > 0) {
                    String path = cachedFile.getAbsolutePath();
                    android.util.Log.d("PED_SCREEN_IMG", "COD " + prodCode + " exibindo ref de disco " + path);
                    imageCache.put(prodCode, path);
                    picasso.load(cachedFile)
                            .fit()
                            .error(R.drawable.semimagem)
                            .centerInside()
                            .into(holder.imagem);
                } else {
                    holder.imagem.setImageResource(R.drawable.semimagem);
                    final ItemPedidos boundItem = itemPedido;
                    new Thread(() -> {
                        try {
                            String codFilPreco = usuarioLogado != null ? usuarioLogado.get("codfil") : "";
                            List<ProdutoGemco> produtos = helper.ConsultarProdutoPorCodigo(String.valueOf(prodCode), codFilPreco);
                            if (produtos != null && !produtos.isEmpty()) {
                                ProdutoGemco p = produtos.get(0);
                                String url = "";
                                if (p.Imagens != null && !p.Imagens.isEmpty() && p.Imagens.get(0) != null && p.Imagens.get(0).ImageUrl != null && !p.Imagens.get(0).ImageUrl.equals("")) {
                                    url = p.Imagens.get(0).ImageUrl;
                                } else if (p.Imagem != null && !p.Imagem.equals("")) {
                                    url = p.Imagem;
                                }
                                final String finalUrl = url;
                                if (finalUrl != null && !finalUrl.equals("")) {
                                    android.util.Log.d("PED_VTEX_IMG", "COD " + prodCode + " URL (VTEX) " + finalUrl);
                                    imageCache.put(prodCode, finalUrl);
                                    boundItem.setImagem(finalUrl);
                                    handler.post(() -> picasso.load(finalUrl)
                                            .fit()
                                            .error(R.drawable.semimagem)
                                            .centerInside()
                                            .into(holder.imagem));
                                    ensureImageCached(prodCode, finalUrl, boundItem);
                                }
                            }
                        } catch (Exception ignored) {
                            handler.post(() -> holder.imagem.setImageResource(R.drawable.semimagem));
                        }
                    }).start();
                }
            }
        }


        if(itemPedido.getPrecoConc() != null && itemPedido.getPrecoConc().size()>0){
            holder.img_concorrente.setVisibility(View.VISIBLE);
        }else{
            holder.img_concorrente.setVisibility(View.INVISIBLE);
        }



        double vlEncargos;
        double vlIcms;
        double margem;
        double custo;
        BigDecimal vlTotal = getValorLiquidoTotalExibicao(itemPedido);
        holder.txt_Vltotal.setText(real.format(vlTotal.doubleValue()));

        vlEncargos = (vlTotal.doubleValue()*itemPedido.getEncargos())/100;
        if(itemPedido.getIcmsRed()==0) {
            vlIcms = (vlTotal.doubleValue() * itemPedido.getIcms()) / 100;
        }else{
            vlIcms = ((vlTotal.doubleValue()- (vlTotal.doubleValue()* itemPedido.getIcmsRed()) / 100)
                    * itemPedido.getIcms()) / 100;
        }
        custo = itemPedido.getCusto()*itemPedido.getQtcomp();

        //calculo anterior
        //margem = ((vlTotal - vlEncargos - vlIcms - custo) / (vlTotal - vlEncargos - vlIcms)) * 100;

        if( itemPedido.getSituacao().equals("SV")){
            margem = 0.00d;
        }else{
            margem = ((vlTotal.doubleValue() - vlEncargos - vlIcms - custo) / (vlTotal.doubleValue())) * 100;
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


        if(isGerente){
            holder.txt_margem.setVisibility(View.VISIBLE);
            holder.txt_margem.setText(df.format(margem)+"%");
        }else{
            holder.txt_margem.setVisibility(View.INVISIBLE);
            holder.txt_margem.setText(df.format(margem)+"%");
        }

        if(itemPedido.getEncomenda().equals("E")){
            holder.img_encomenda.setVisibility(View.VISIBLE);
        }else{
            holder.img_encomenda.setVisibility(View.INVISIBLE);
        }

        if(itemPedido.getSituacao() != null){


        switch (itemPedido.getSituacao()) {
            case "PR":
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.promocao);

                holder.img_situacao.setImageBitmap(bitmap);

                break;

            case "IN":
                Bitmap bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.inativo);

                holder.img_situacao.setImageBitmap(bitmap2);
                break;

            case "EN":
                Bitmap bitmap3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.encomenda);

                holder.img_situacao.setImageBitmap(bitmap3);
                break;

            case "LC":
                Bitmap bitmap4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.lojaconceito);

                holder.img_situacao.setImageBitmap(bitmap4);
                break;

            default:  holder.img_situacao.setImageBitmap(null);

        }

        }else{
            holder.img_situacao.setImageBitmap(null);
        }

        double pesoTotal = BigDecimal.valueOf(itemPedido.getPeso() * itemPedido.getQtcomp()).setScale(2, RoundingMode.FLOOR).doubleValue();
        holder.txt_pesoTotal.setText(String.valueOf(pesoTotal).replace(".",",").concat(" Kg"));

        if (itemPedido.getUpsellTipo() != null) {
            String tipo = itemPedido.getUpsellTipo();
            holder.strip.setVisibility(View.VISIBLE);
            if ("UPSELL".equals(tipo)) {
                holder.strip.setBackgroundColor(Color.parseColor("#FFD600"));
                holder.stripStar.setVisibility(View.GONE);
                holder.stripLabel.setVisibility(View.VISIBLE);
                holder.stripLabel.setText("SUGERIDO");
                holder.stripLabel.setTextColor(Color.parseColor("#000000"));
            } else if ("PRINCIPAL".equals(tipo)) {
                holder.strip.setBackgroundColor(Color.parseColor("#00C853"));
                holder.stripStar.setVisibility(View.VISIBLE);
                holder.stripLabel.setVisibility(View.GONE);
            } else {
                holder.strip.setVisibility(View.GONE);
                holder.stripStar.setVisibility(View.GONE);
                holder.stripLabel.setVisibility(View.GONE);
            }
        } else {
            holder.strip.setVisibility(View.GONE);
            holder.stripStar.setVisibility(View.GONE);
            holder.stripLabel.setVisibility(View.GONE);
        }
    }

    private File getCacheFolder() {
        File folder = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "PedidoImgCache");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    private String getCachedImagePath(int cod) {
        return new File(getCacheFolder(), "prod_" + cod + ".png").getAbsolutePath();
    }

    private void preloadDiskCache() {
        File folder = getCacheFolder();
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File f : files) {
            String name = f.getName();
            if (name.startsWith("prod_") && name.endsWith(".png") && f.length() > 0) {
                try {
                    String num = name.substring(5, name.length() - 4);
                    int cod = Integer.parseInt(num);
                    imageCache.put(cod, f.getAbsolutePath());
                } catch (Exception ignored) { }
            }
        }
    }

    private void ensureImageCached(final int cod, final String url, final ItemPedidos item) {
        if (url == null || url.equals("")) return;
        File file = new File(getCachedImagePath(cod));
        if (file.exists() && file.length() > 0) {
            return;
        }
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception ignored) { }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        picassoTargets.add(target);
        picasso.load(url).into(target);
    }

    @Override
    public long getItemId(int position) {
        return (long) mList.get(position).getNumpedven();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private double getPrecoUnitExibicao(ItemPedidos itemPedido) {
        return Pedidos_LancamentoActivity.getPrecoPrimarioItemExibicao(itemPedido);
    }

    private BigDecimal getValorLiquidoTotalExibicao(ItemPedidos itemPedido) {
        return getPrecoLiquidoUnitarioExibicao(itemPedido)
                .multiply(BigDecimal.valueOf(itemPedido.getQtcomp()))
                .setScale(2, RoundingMode.FLOOR);
    }

    private BigDecimal getPrecoLiquidoUnitarioExibicao(ItemPedidos itemPedido) {
        BigDecimal precoLiquido = BigDecimal.valueOf(getPrecoUnitExibicao(itemPedido))
                .subtract(BigDecimal.valueOf(itemPedido.getVlDescItem()));

        return precoLiquido;
    }

    private double getValorDescontoUnitarioExibicao(ItemPedidos itemPedido) {
        return BigDecimal.valueOf(
                        Math.max(
                                0.0d,
                                getPrecoUnitExibicao(itemPedido) - getPrecoLiquidoUnitarioExibicao(itemPedido).doubleValue()
                        )
                )
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private double getPercentualDescontoExibicao(ItemPedidos itemPedido) {
        double precoBase = getPrecoUnitExibicao(itemPedido);
        if (precoBase <= 0) {
            return 0.0d;
        }

        return BigDecimal.valueOf((getValorDescontoUnitarioExibicao(itemPedido) / precoBase) * 100.0d)
                .setScale(5, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private String montarTextoComissao(ItemPedidos itemPedido) {
        if (itemPedido == null) {
            return "";
        }

        DecimalFormat df = new DecimalFormat("0.00");
        double comissao = itemPedido.getPercentualComissao() > 0 ? itemPedido.getPercentualComissao() : 0.0;
        double premiacao = itemPedido.getPercIncentivo() > 0 ? itemPedido.getPercIncentivo() : 0.0;
        boolean filial3 = usuarioLogado != null && "3".equals(usuarioLogado.get("codfil"));

        if (filial3) {
            return "";
        }

        if (comissao <= 0 && premiacao <= 0) {
            return "";
        }

        double total = comissao + premiacao;
        return "C: " + df.format(comissao) + "%  "
                + "P: " + df.format(premiacao) + "%\n"
                + "Total: " + df.format(total) + "%";
    }

    private CharSequence montarTextoComissaoFormatado(ItemPedidos itemPedido) {
        String textoComissao = montarTextoComissao(itemPedido);
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

    private void bindComissao(ViewHolder holder, ItemPedidos itemPedido, int position) {
        aplicarComissaoCache(itemPedido);

        String textoComissao = montarTextoComissao(itemPedido);
        if (!textoComissao.isEmpty()) {
            holder.txt_Comissao.setVisibility(View.VISIBLE);
            holder.txt_Comissao.setTextColor(Color.parseColor("#00bfa5"));
            holder.txt_Comissao.setText(montarTextoComissaoFormatado(itemPedido));
            return;
        }

        int coditprod = itemPedido != null ? itemPedido.getCoditprod() : 0;
        if (coditprod <= 0 || comissaoSemResultado.contains(coditprod)) {
            holder.txt_Comissao.setVisibility(View.GONE);
            holder.txt_Comissao.setText("");
            return;
        }

        holder.txt_Comissao.setVisibility(View.VISIBLE);
        holder.txt_Comissao.setTextColor(Color.parseColor("#757575"));
        holder.txt_Comissao.setText("Carregando...");
        carregarComissaoAsync(itemPedido, position);
    }

    private void aplicarComissaoCache(ItemPedidos itemPedido) {
        if (itemPedido == null || itemPedido.getCoditprod() <= 0) {
            return;
        }

        ProdutoGemco produtoCache = comissaoCache.get(itemPedido.getCoditprod());
        if (produtoCache == null) {
            return;
        }

        itemPedido.setPercentualComissao(produtoCache.PercentualComissao);
        itemPedido.setPercIncentivo(produtoCache.PercIncentivo);
    }

    private void carregarComissaoAsync(ItemPedidos itemPedido, int position) {
        if (itemPedido == null) {
            return;
        }

        final int coditprod = itemPedido.getCoditprod();
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
                    itemPedido.setPercentualComissao(produto.PercentualComissao);
                    itemPedido.setPercIncentivo(produto.PercIncentivo);

                    if (montarTextoComissao(itemPedido).isEmpty()) {
                        comissaoSemResultado.add(coditprod);
                    }
                } else {
                    comissaoSemResultado.add(coditprod);
                }
            } catch (Exception e) {
                android.util.Log.e("ComissaoItemDebug", "Falha ao consultar comissao do item pedido " + coditprod, e);
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
    public void addListitem(ItemPedidos o, int position){
        mList.add(o);
        notifyItemInserted(position);
        // #region debug-point D:adapter-add-item
        reportDebugEvent("D", "ItemPedidoAdapter.addListitem",
                "[DEBUG] item adicionado ao adapter do pedido",
                buildItemDebugSnapshot(position, o, getPrecoUnitExibicao(o), getValorDescontoUnitarioExibicao(o)));
        // #endregion
        Pedidos_LancamentoActivity.marcarTotalComoRecalculavel();
        Pedidos_LancamentoActivity.atualizaTotais(mList);
    }

    public void updateListitem(ItemPedidos o, int position){
        mList.set(position,o);
        notifyItemChanged(position);
        // #region debug-point E:adapter-update-item
        reportDebugEvent("E", "ItemPedidoAdapter.updateListitem",
                "[DEBUG] item atualizado no adapter do pedido",
                buildItemDebugSnapshot(position, o, getPrecoUnitExibicao(o), getValorDescontoUnitarioExibicao(o)));
        // #endregion
        Pedidos_LancamentoActivity.marcarTotalComoRecalculavel();
        Pedidos_LancamentoActivity.atualizaTotais(mList);
    }

    public void setRecyclerViewOnLongClickListenerHack(RecyclerViewOnLongClickListenerHack r){
        mRecyclerViewOnLongClickListenerHack = r;
    }

    public void removeListItem(int position){
        ItemPedidos removido = position >= 0 && position < mList.size() ? mList.get(position) : null;
        mList.remove(position);
        notifyDataSetChanged();
        // #region debug-point F:adapter-remove-item
        reportDebugEvent("F", "ItemPedidoAdapter.removeListItem",
                "[DEBUG] item removido do adapter do pedido",
                buildItemDebugSnapshot(position, removido, removido != null ? getPrecoUnitExibicao(removido) : 0d,
                        removido != null ? getValorDescontoUnitarioExibicao(removido) : 0d));
        // #endregion
        Pedidos_LancamentoActivity.marcarTotalComoRecalculavel();
        Pedidos_LancamentoActivity.atualizaTotais(mList);


    }

    public void prefetchAll(){
        if (helper == null) helper = new ProdutoHelper();
        if (loginHelper == null) loginHelper = new LoginHelper(context);
        if (usuarioLogado == null) usuarioLogado = loginHelper.GetUsuarioSalvo();
        new Thread(() -> {
            try {
                String codFilPreco = usuarioLogado != null ? usuarioLogado.get("codfil") : "";
                for (int i = 0; i < mList.size(); i++) {
                    ItemPedidos it = mList.get(i);
                    int prodCode = it.getCoditprod();
                    boolean pedidoSalvo = it.getNumpedven() > 0;
                    if (imageCache.containsKey(prodCode)) {
                        final int idx = i;
                        handler.post(() -> notifyItemChanged(idx));
                        continue;
                    }
                    File cachedFile = new File(getCachedImagePath(prodCode));
                    if (cachedFile.exists() && cachedFile.length() > 0) {
                        String path = cachedFile.getAbsolutePath();
                        imageCache.put(prodCode, path);
                        it.setImagem(path);
                        final int idx = i;
                        handler.post(() -> notifyItemChanged(idx));
                        continue;
                    }
                    if (it.getImagem() != null && !it.getImagem().equals("")) {
                        imageCache.put(prodCode, it.getImagem());
                        ensureImageCached(prodCode, it.getImagem(), it);
                        final int idx = i;
                        handler.post(() -> notifyItemChanged(idx));
                        continue;
                    }
                    if (vtexFetched.contains(prodCode) || vtexFetching.contains(prodCode)) continue;
                    vtexFetching.add(prodCode);
                    try {
                        List<ProdutoGemco> produtos = pedidoSalvo
                                ? helper.ConsultarProdutoEstoque(String.valueOf(prodCode), codFilPreco)
                                : helper.ConsultarProdutoPorCodigo(String.valueOf(prodCode), codFilPreco);
                        if (produtos != null && !produtos.isEmpty()) {
                            ProdutoGemco p = produtos.get(0);
                            String url = "";
                            if (!pedidoSalvo && p.Imagens != null && !p.Imagens.isEmpty() && p.Imagens.get(0) != null && p.Imagens.get(0).ImageUrl != null && !p.Imagens.get(0).ImageUrl.equals("")) {
                                url = p.Imagens.get(0).ImageUrl;
                            } else if (p.Imagem != null && !p.Imagem.equals("")) {
                                url = p.Imagem;
                            }
                            if (url != null && !url.equals("")) {
                                imageCache.put(prodCode, url);
                                it.setImagem(url);
                                ensureImageCached(prodCode, url, it);
                                vtexFetched.add(prodCode);
                                final int idx = i;
                                handler.post(() -> notifyItemChanged(idx));
                            }
                        }
                    } catch (Exception ignored) {
                    } finally {
                        vtexFetching.remove(prodCode);
                    }
                }
            } catch (Exception ignored) { }
        }).start();
    }

    private JSONObject buildItemDebugSnapshot(int position, ItemPedidos item, double precoExibicao, double descontoUnitarioExibicao) {
        JSONObject data = new JSONObject();
        try {
            data.put("position", position);
            data.put("listaItens", mList != null ? mList.size() : -1);
            data.put("codprod", item != null ? item.getCoditprod() : 0);
            data.put("numpedven", item != null ? item.getNumpedven() : 0);
            data.put("precoExibicao", precoExibicao);
            data.put("precoUnit", item != null ? item.getPrecoUnit() : 0);
            data.put("precoPrimario", item != null ? item.getPrecoPrimario() : 0);
            data.put("vlDescItem", item != null ? item.getVlDescItem() : 0);
            data.put("descontoUnitarioExibicao", descontoUnitarioExibicao);
            data.put("qt", item != null ? item.getQtcomp() : 0);
            data.put("dpi", item != null ? item.getDpi() : 0);
            data.put("isMotor", item != null ? item.getIsMotor() : "");
            data.put("situacao", item != null ? item.getSituacao() : "");
        } catch (Exception ignore) {
        }
        return data;
    }

    private void reportDebugEvent(String hypothesisId, String location, String msg, JSONObject data) {
        try {
            android.util.Log.d(DEBUG_LOGCAT_TAG, hypothesisId + " | " + location + " | " + msg + " | data=" + (data != null ? data.toString() : "{}"));
        } catch (Exception ignore) {
        }
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                JSONObject payload = new JSONObject();
                payload.put("sessionId", DEBUG_SESSION_ID);
                payload.put("runId", DEBUG_RUN_ID);
                payload.put("hypothesisId", hypothesisId);
                payload.put("location", location);
                payload.put("msg", msg);
                payload.put("data", data != null ? data : new JSONObject());
                payload.put("ts", System.currentTimeMillis());

                URL url = new URL(DEBUG_SERVER_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                byte[] body = payload.toString().getBytes("UTF-8");
                connection.setFixedLengthStreamingMode(body.length);
                OutputStream os = connection.getOutputStream();
                os.write(body);
                os.flush();
                os.close();
                connection.getResponseCode();
            } catch (Exception ignore) {
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
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
        ImageView img_encomenda;
        TextView txt_Vldesc;
        TextView txt_Vltotal;
        TextView txt_margem;
        TextView txt_tpped;
        TextView txt_filorig;
        TextView txt_filtransf;
        TextView txt_dtentrega;
        View status;
        ImageView img_situacao;

        ImageView img_concorrente;
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
            img_encomenda = itemView.findViewById(R.id.adp_ped_indicador3);
            status = itemView.findViewById(R.id.adp_ped_status);
            txt_margem = itemView.findViewById(R.id.adp_orc_txtmargem);
            txt_tpped = itemView.findViewById(R.id.adp_ped_tipoPed);
            txt_filorig = itemView.findViewById(R.id.adp_ped_filorig);
            txt_filtransf = itemView.findViewById(R.id.adp_ped_filtransf);
            txt_dtentrega = itemView.findViewById(R.id.adp_ped_dtentrega);
            img_situacao = itemView.findViewById(R.id.etiqueta);
            img_concorrente = itemView.findViewById(R.id.imgConcorrente);
            img_desconto = itemView.findViewById(R.id.adapter_ped_desconto);
            txt_pesoTotal = itemView.findViewById(R.id.AdapterItemPedido_PesoTotal);
            strip = itemView.findViewById(R.id.adapter_ped_strip);
            stripStar = itemView.findViewById(R.id.adapter_ped_stripStar);
            stripLabel = itemView.findViewById(R.id.adapter_ped_stripLabel);

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
