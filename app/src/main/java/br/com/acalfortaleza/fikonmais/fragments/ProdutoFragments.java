package br.com.acalfortaleza.fikonmais.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.io.File;
import java.io.FileOutputStream;

import br.com.acalfortaleza.fikonmais.ConsultaActivity;
import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.SlideAdapter;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.helper.OrcamentoHelper;
import br.com.acalfortaleza.fikonmais.helper.PedidosHelper;
import br.com.acalfortaleza.fikonmais.helper.ProdutoHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import br.com.acalfortaleza.fikonmais.models.CodigoBarra;
import br.com.acalfortaleza.fikonmais.models.Estoque;
import br.com.acalfortaleza.fikonmais.models.ImagemCiashop;
import br.com.acalfortaleza.fikonmais.models.ItemOrcamento;
import br.com.acalfortaleza.fikonmais.models.ItemPedidos;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.PrecoConc;
import br.com.acalfortaleza.fikonmais.models.PromocaoPrecoProduto;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.models.ProdutoVtex;
import br.com.acalfortaleza.fikonmais.models.SugestaoProdutoVtex;
import br.com.acalfortaleza.fikonmais.popup.PopupSugestaoProduto;

public class ProdutoFragments extends Fragment {

    public ProdutoFragments() {

    }


    private View layout;
    private TextView descricaoProduto;
    private TextView preco;
    private TextView codigoItem;
    private ViewPager slider;
    private Context context;
    private ImageView etiqueta;
    private ProdutoGemco produto;
    private String urlImagem;
    private String descricao;
    private List<ProdutoVtex.Images> imagens;
    private TextView unidade;
    private TextView validade;
    private int codfil;
    private String oferta;
    private Button btIncluirOrc;
    private Button btIncluirPed;
    private TextInputLayout layoutQtd;
    private TextInputEditText edtQtd;
    private ImageView imgBarcode;
    private ImageView imgConcorrente;
    private AlertDialog alertaDialog;
    private String resultado = "";
    private ProdutoVtex.Images imagem;
    private ImageView img_notificacao;
    private ImageView imgSelecao;
    private ImageView imgAcelerado;
    private ImageButton btnToggleEtiqueta;
    private boolean etiquetaVisivel = true;
    private TextView qtd_carrinho;
    public static Orcamento orcamento;
    public static Pedidos pedido;
    private List<ItemOrcamento> lista_itensOrc;
    private List<ItemPedidos> lista_itensPed;
    private OrcamentoHelper orcamentoHelper;
    private PedidosHelper pedidosHelper;
    private ProdutoHelper produtoHelper;
    private Picasso picasso;
    private List<Target> picassoTargets = new ArrayList<>();
    private ImageView carrinho;
    private ViewPager viewPager;
    private SlideAdapter slideAdapter;
    private TextView txtPromocao;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private HashMap<String, String> usuarioLogado;
    private LoginHelper helperLogin;
    private boolean isServico = false;
    private ProgressDialog progressDialog;
    private String IntentCaller;
    private String codFilPreco;
    private TextView txtAualizacaoPreco;
    private TextView TxtPrecoPor;
    private TextView txtPrecoDe;
    private TextView txtTipoPreco;
    private TextView txtEconomize;
    private LinearLayout layoutPromocaoInfo;
    private TextView txtNomePromocao;
    private TextView txtValidadePromocao;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (getArguments() != null) {

            produto = (ProdutoGemco) getArguments().getSerializable("produtogemco");
            descricao = produto.Descricao;
            if(descricao!=null) {
                descricao = descricao.replaceAll("'", " ");

            }
            imagens = produto.Imagens;
            codfil = getArguments().getInt("codfil");
            orcamento = (Orcamento) getArguments().getSerializable("orcamento");
            pedido = (Pedidos) getArguments().getSerializable("pedido");
            IntentCaller = getArguments().getString("intentCaller");
            helperLogin = new LoginHelper(getContext());
            usuarioLogado = helperLogin.GetUsuarioSalvo();
            codFilPreco = usuarioLogado.get("codfil").toString();
        }

        orcamentoHelper = new OrcamentoHelper();
        pedidosHelper = new PedidosHelper();
        picasso = new Picasso.Builder(getContext()).build();

        if(imagens != null && !imagens.isEmpty()){
            slideAdapter = new SlideAdapter(getActivity(),imagens);
        }else{
            imagem = new ProdutoVtex.Images();

            if(produto.Imagem != null && !produto.Imagem.equals("")){
                imagem.ImageUrl = produto.Imagem;
            }else{
                imagem.ImageUrl = "Sem foto";
            }


            imagens = new ArrayList<>();
            imagens.add(imagem);
            slideAdapter = new SlideAdapter(getActivity(),imagens);
        }


    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        layout = inflater.inflate(R.layout.fragment_produto,
                container, false);

        descricaoProduto = layout.findViewById(R.id.descricao);
        codigoItem = layout.findViewById(R.id.codigoItem);
        preco = layout.findViewById(R.id.preco);
        slider = layout.findViewById(R.id.slider);
        etiqueta = layout.findViewById(R.id.etiqueta);
        unidade = layout.findViewById(R.id.unidade);
        btIncluirOrc = layout.findViewById(R.id.bt_incluirOrc);
        btIncluirPed = layout.findViewById(R.id.bt_incluirPed);
        layoutQtd = layout.findViewById(R.id.layoutEdtQtd);
        edtQtd = layout.findViewById(R.id.edtQtd);
        imgBarcode = layout.findViewById(R.id.imgBarcode);
        imgConcorrente = layout.findViewById(R.id.imgConcorrente);
        img_notificacao = layout.findViewById(R.id.prod_notificacao);
        qtd_carrinho = layout.findViewById(R.id.prod_txtNotificacao);
        carrinho = layout.findViewById(R.id.prod_carrinho);
        txtPromocao = layout.findViewById(R.id.prod_txtpromocao);
        imgSelecao = layout.findViewById(R.id.imgSelecao);
        imgAcelerado = layout.findViewById(R.id.imgAcelerado);
        btnToggleEtiqueta = layout.findViewById(R.id.btnToggleEtiqueta);
        btnToggleEtiqueta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etiquetaVisivel = !etiquetaVisivel;
                etiqueta.setVisibility(etiquetaVisivel ? View.VISIBLE : View.GONE);
                btnToggleEtiqueta.setImageResource(etiquetaVisivel
                        ? R.drawable.ic_visibility
                        : R.drawable.ic_visibility_off);
                btnToggleEtiqueta.setContentDescription(etiquetaVisivel
                        ? "Ocultar situação do produto"
                        : "Mostrar situação do produto");
            }
        });
        txtAualizacaoPreco = layout.findViewById(R.id.FragmentProduto_txtAtualizacaoPreco);
        TxtPrecoPor = layout.findViewById(R.id.FragmentProduto_TxtPrecoPor);
        txtPrecoDe = layout.findViewById(R.id.FragmentProduto_TxtPrecoDe);
        txtTipoPreco = layout.findViewById(R.id.FragmentProduto_TxtTipoPreco);
        txtTipoPreco.setText("");
        txtTipoPreco.setVisibility(View.GONE);
        txtEconomize = layout.findViewById(R.id.FragmentProduto_TxtEconomize);
        layoutPromocaoInfo = layout.findViewById(R.id.FragmentProduto_LayoutPromocaoInfo);
        txtNomePromocao = layout.findViewById(R.id.FragmentProduto_TxtNomePromocao);
        txtValidadePromocao = layout.findViewById(R.id.FragmentProduto_TxtValidadePromocao);
        txtPromocao.setText("");
        txtPromocao.setVisibility(View.GONE);
        TxtPrecoPor.setText("");
        TxtPrecoPor.setVisibility(View.GONE);
        txtPrecoDe.setText("");
        txtPrecoDe.setVisibility(View.GONE);
        txtEconomize.setText("");
        txtEconomize.setVisibility(View.GONE);
        layoutPromocaoInfo.setVisibility(View.GONE);
        txtNomePromocao.setText("");
        txtValidadePromocao.setText("");

        btIncluirPed.setEnabled(false);
        btIncluirPed.setBackgroundResource(R.drawable.backgroundbuttondisabled);


        if (orcamento != null && pedido==null) {
            lista_itensOrc = orcamento.getItens();
            btIncluirPed.setEnabled(false);
            btIncluirPed.setBackgroundResource(R.drawable.backgroundbuttondisabled);

        }

        if (orcamento == null && pedido != null) {
            lista_itensPed = pedido.getItens();
            btIncluirOrc.setEnabled(false);
            btIncluirOrc.setBackgroundResource(R.drawable.backgroundbuttondisabled);
            btIncluirPed.setEnabled(true);
            btIncluirPed.setBackgroundResource(R.drawable.backgroudbuttom);

        }


        carrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orcamento != null && pedido == null) {
                    Intent intent = new Intent(context, Orcamento_LancamentoActivity.class);
                    intent.putExtra("orcamento", orcamento);
                    startActivity(intent);
                }


                if (orcamento == null && pedido != null) {
                    Intent intent = new Intent(context, Pedidos_LancamentoActivity.class);
                    intent.putExtra("pedido", pedido);
                    startActivity(intent);
                }

            }
        });
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        final DecimalFormat df = new DecimalFormat("0.00",symbols);
        if (produto!= null && produto.Qtemb == 0) {
            edtQtd.setText("1.00");
            produto.Qtemb = 1.00;

        } else {
            edtQtd.setText(df.format(produto.Qtemb));
        }

        edtQtd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    edtQtd.setText("");
                }
                return false;
            }
        });


        if (lista_itensOrc != null && lista_itensPed == null) {

            for(int i = 0 ; i < lista_itensOrc.size(); i++){
                if(lista_itensOrc.get(i).getSituacao().equals("SV")){
                    isServico = true;
                }else{
                    isServico = false;
                }
            }
            qtd_carrinho.setText(String.valueOf(lista_itensOrc.size()));
            img_notificacao.setVisibility(View.VISIBLE);

        } if (lista_itensOrc == null && lista_itensPed != null) {

            for(int i = 0 ; i < lista_itensPed.size(); i++){
                if(lista_itensPed.get(i).getSituacao().equals("SV")){
                    isServico = true;
                }else{
                    isServico = false;
                }
            }
            qtd_carrinho.setText(String.valueOf(lista_itensPed.size()));
            img_notificacao.setVisibility(View.VISIBLE);

        } else {
            qtd_carrinho.setText("");
            img_notificacao.setVisibility(View.INVISIBLE);
        }

        imgBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultado = "";
                for (CodigoBarra b : produto.CodigoBarra) {

                    if (b.CodEmbal == 0) {


                        resultado = resultado + ("<b>" + "EAN UN: " + "</b>" + b.CodBarra + "<br />");


                    }

                }
                resultado = resultado + ("<br />");

                for (CodigoBarra b : produto.CodigoBarra) {

                    if (b.CodEmbal == 1) {


                        resultado = resultado + ("<b>" + "EAN CX: " + "</b>" + b.CodBarra + "<br />");


                    }

                }

                AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                alerta.setIcon(R.drawable.ic_nav_barcode);
                alerta.setCancelable(false);
                alerta.setTitle("Código de barras");
                alerta.setMessage(Html.fromHtml(resultado));
                alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                alertaDialog = alerta.create();
                alertaDialog.show();


            }
        });


        if(produto.PrecoConc != null && produto.PrecoConc.size()>0 && usuarioLogado.get("isgerente").equals("SIM")){
            imgConcorrente.setVisibility(View.VISIBLE);
        }else{
            imgConcorrente.setVisibility(View.INVISIBLE);
        }


        imgConcorrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultado = "";
                NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

                DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                DateFormat entrada = new SimpleDateFormat("yyyy-MM-dd");


                Date date = null;


                for (PrecoConc p : produto.PrecoConc) {



                    try {
                        date = entrada.parse(p.DataPesquisa);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }

                        resultado = resultado + "<b>" + "Concorrente: "+ "</b>" + p.Concorrente + " <br />" +
                                "<b>" + "Preço: "+ "</b>" + real.format(p.Preco )+ "" + " <br />" +
                                "<b>" + "Data: "+ "</b>"  + formato.format(date) + "" + " <br />" +
                                "<br />";

                    }







                AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                alerta.setIcon(R.drawable.ic_nav_cnpj);
                alerta.setCancelable(false);
                alerta.setTitle("Preço dos Concorrentes");
                alerta.setMessage(Html.fromHtml(resultado));
                alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                alertaDialog = alerta.create();
                alertaDialog.show();


            }
        });




        btIncluirOrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validarQtd() || !validarConversao() || !validarServico()) {

                } else {

                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("orcamentos");

                    if (produto.Imagens != null
                            && slider.getCurrentItem() < produto.Imagens.size()
                            && produto.Imagens.get(slider.getCurrentItem()) != null
                            && produto.Imagens.get(slider.getCurrentItem()).ImageUrl != null
                            && !produto.Imagens.get(slider.getCurrentItem()).ImageUrl.equals("")) {
                        urlImagem = produto.Imagens.get(slider.getCurrentItem()).ImageUrl;
                    } else if (produto.Imagem != null && !produto.Imagem.equals("")) {
                        urlImagem = produto.Imagem;
                    } else {
                        urlImagem = "";
                    }
                    Log.d("ORC_CAROUSEL_IMG", "Index " + slider.getCurrentItem() + " URL " + String.valueOf(urlImagem));



                    if (orcamento != null) {

                        double qtd = Double.parseDouble(edtQtd.getText().toString());

                        if(descricao==null){
                            descricao=produto.Descricao;
                        }


                        if(produto.Upsell != null && produto.Upsell.size()>0){
                            ItemOrcamento itemOrcamento = orcamentoHelper.setItemOrcamento(qtd, produto, descricao, urlImagem);
                            itemOrcamento.setUpsellTipo("PRINCIPAL");
                            lista_itensOrc.add(itemOrcamento);
                            cacheOrcImage(produto.Codigo, urlImagem, itemOrcamento);
                            /* Inclusao automatica de upsell temporariamente desativada.
                            for(int i = 0 ; i < produto.Upsell.size(); i++ ){
                                br.com.acalfortaleza.fikonmais.models.ProdutoGemco up = produto.Upsell.get(i);
                                boolean existe = false;
                                for (ItemOrcamento io : lista_itensOrc) {
                                    if (io.getCoditprod() == up.Codigo) { existe = true; break; }
                                }
                                if (!existe) {
                                    ItemOrcamento upsell = orcamentoHelper.setItemOrcamento(1, up, up.Descricao, up.Imagem);
                                    upsell.setUpsellTipo("UPSELL");
                                    lista_itensOrc.add(upsell);
                                    cacheOrcImage(up.Codigo, up.Imagem, upsell);
                                }
                            }
                            */
                            new Thread(() -> {
                                try {
                                    if (urlImagem != null && !urlImagem.equals("") && urlImagem.startsWith("http")) {
                                        produtoHelper.AtualizarImagemVtex(produto.Codigo, urlImagem);
                                        android.util.Log.d("ORC_URL_UPDATE", "Atualizada IMAGEM para COD " + produto.Codigo + " com URL do carrossel: " + urlImagem);
                                    }
                                    for (int i = 0 ; i < produto.Upsell.size(); i++ ) {
                                        String upUrl = produto.Upsell.get(i).Imagem;
                                        if (upUrl != null && !upUrl.equals("") && upUrl.startsWith("http")) {
                                            produtoHelper.AtualizarImagemVtex(produto.Upsell.get(i).Codigo, upUrl);
                                            android.util.Log.d("ORC_URL_UPDATE", "Atualizada IMAGEM para COD " + produto.Upsell.get(i).Codigo + " (upsell) URL: " + upUrl);
                                        }
                                    }
                                } catch (Exception ignored) { }
                            }).start();
                        }else{
                            ItemOrcamento itemOrcamento = orcamentoHelper.setItemOrcamento(qtd, produto, descricao, urlImagem);
                            lista_itensOrc.add(itemOrcamento);
                            cacheOrcImage(produto.Codigo, urlImagem, itemOrcamento);
                            new Thread(() -> {
                                try {
                                    if (urlImagem != null && !urlImagem.equals("") && urlImagem.startsWith("http")) {
                                        produtoHelper.AtualizarImagemVtex(produto.Codigo, urlImagem);
                                        android.util.Log.d("ORC_URL_UPDATE", "Atualizada IMAGEM para COD " + produto.Codigo + " com URL do carrossel: " + urlImagem);
                                    }
                                } catch (Exception ignored) { }
                            }).start();
                        }

                        qtd_carrinho.setText(String.valueOf(lista_itensOrc.size()));
                        img_notificacao.setVisibility(View.VISIBLE);
                        orcamento.setItens(lista_itensOrc);
                        Toast.makeText(context, "Produto inserido no carrinho!", Toast.LENGTH_SHORT).show();
                        ConsultaActivity.orcamento = orcamento;
                        myRef.child(usuarioLogado.get("codigo")).setValue(orcamento);

                        /*if(IntentCaller!=null && IntentCaller.equals(ConsultaActivity.class.toString())){
                            consultarSugestoesProduto();
                        }
                        else if(IntentCaller!=null && IntentCaller.equals(PopupSugestaoProduto.class.toString())){
                            Intent resultIntent = new Intent();
                            if(orcamento!=null){
                                resultIntent.putExtra("orcamento",(Serializable) orcamento);
                            }
                            if(pedido!=null){
                                resultIntent.putExtra("pedido",(Serializable) pedido);
                            }
                            getActivity().setResult(Activity.RESULT_OK, resultIntent);
                            getActivity().finish();
                        }
                        else {
                            getActivity().finish();
                        }*/
                        getActivity().finish();


                    } else {

                        double qtd = Double.parseDouble(edtQtd.getText().toString());

                        if(descricao==null){
                            descricao=produto.Descricao;
                        }
                        orcamento = new Orcamento();
                        lista_itensOrc = new ArrayList<>();

                        if(produto.Upsell != null && produto.Upsell.size()>0){
                            ItemOrcamento itemOrcamento = orcamentoHelper.setItemOrcamento(qtd, produto, descricao, urlImagem);
                            itemOrcamento.setUpsellTipo("PRINCIPAL");
                            lista_itensOrc.add(itemOrcamento);
                            cacheOrcImage(produto.Codigo, urlImagem, itemOrcamento);
                            /* Inclusao automatica de upsell temporariamente desativada.
                            for(int i = 0 ; i < produto.Upsell.size(); i++ ){
                                br.com.acalfortaleza.fikonmais.models.ProdutoGemco up = produto.Upsell.get(i);
                                boolean existe = false;
                                for (ItemOrcamento io : lista_itensOrc) {
                                    if (io.getCoditprod() == up.Codigo) { existe = true; break; }
                                }
                                if (!existe) {
                                    ItemOrcamento upsell = orcamentoHelper.setItemOrcamento(1, up, up.Descricao, up.Imagem);
                                    upsell.setUpsellTipo("UPSELL");
                                    lista_itensOrc.add(upsell);
                                    cacheOrcImage(up.Codigo, up.Imagem, upsell);
                                }
                            }
                            */
                            new Thread(() -> {
                                try {
                                    if (urlImagem != null && !urlImagem.equals("") && urlImagem.startsWith("http")) {
                                        produtoHelper.AtualizarImagemVtex(produto.Codigo, urlImagem);
                                        android.util.Log.d("ORC_URL_UPDATE", "Atualizada IMAGEM para COD " + produto.Codigo + " com URL do carrossel: " + urlImagem);
                                    }
                                    for (int i = 0 ; i < produto.Upsell.size(); i++ ) {
                                        String upUrl = produto.Upsell.get(i).Imagem;
                                        if (upUrl != null && !upUrl.equals("") && upUrl.startsWith("http")) {
                                            produtoHelper.AtualizarImagemVtex(produto.Upsell.get(i).Codigo, upUrl);
                                            android.util.Log.d("ORC_URL_UPDATE", "Atualizada IMAGEM para COD " + produto.Upsell.get(i).Codigo + " (upsell) URL: " + upUrl);
                                        }
                                    }
                                } catch (Exception ignored) { }
                            }).start();
                        }else{
                            ItemOrcamento itemOrcamento = orcamentoHelper.setItemOrcamento(qtd, produto, descricao, urlImagem);
                            lista_itensOrc.add(itemOrcamento);
                            cacheOrcImage(produto.Codigo, urlImagem, itemOrcamento);
                            new Thread(() -> {
                                try {
                                    if (urlImagem != null && !urlImagem.equals("") && urlImagem.startsWith("http")) {
                                        produtoHelper.AtualizarImagemVtex(produto.Codigo, urlImagem);
                                        android.util.Log.d("ORC_URL_UPDATE", "Atualizada IMAGEM para COD " + produto.Codigo + " com URL do carrossel: " + urlImagem);
                                    }
                                } catch (Exception ignored) { }
                            }).start();
                        }



                        qtd_carrinho.setText(String.valueOf(lista_itensOrc.size()));
                        img_notificacao.setVisibility(View.VISIBLE);
                        orcamento.setItens(lista_itensOrc);
                        Toast.makeText(context, "Produto inserido no carrinho!", Toast.LENGTH_SHORT).show();
                        ConsultaActivity.orcamento = orcamento;
                        myRef.child(usuarioLogado.get("codigo")).setValue(orcamento);

                        /*if(IntentCaller!=null && IntentCaller.equals(ConsultaActivity.class.toString())){
                            consultarSugestoesProduto();
                        }
                        else if(IntentCaller!=null && IntentCaller.equals(PopupSugestaoProduto.class.toString())){
                            Intent resultIntent = new Intent();
                            if(orcamento!=null){
                                resultIntent.putExtra("orcamento",(Serializable) orcamento);
                            }
                            if(pedido!=null){
                                resultIntent.putExtra("pedido",(Serializable) pedido);
                            }
                            getActivity().setResult(Activity.RESULT_OK, resultIntent);
                            getActivity().finish();
                        }
                        else {
                            getActivity().finish();
                        }*/
                        getActivity().finish();



                    }

                }

            }
        });


        btIncluirPed.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {

                if (!validarQtd() || !validarConversao() || !validarServicoPed() || !validarTipo()) {

                } else {

                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference("pedidos");

                    if (produto.Imagens != null
                            && slider.getCurrentItem() < produto.Imagens.size()
                            && produto.Imagens.get(slider.getCurrentItem()) != null
                            && produto.Imagens.get(slider.getCurrentItem()).ImageUrl != null
                            && !produto.Imagens.get(slider.getCurrentItem()).ImageUrl.equals("")) {
                        urlImagem = produto.Imagens.get(slider.getCurrentItem()).ImageUrl;
                    } else if (produto.Imagem != null && !produto.Imagem.equals("")) {
                        urlImagem = produto.Imagem;
                    } else {
                        urlImagem = "";
                    }
                    Log.d("PED_CAROUSEL_IMG", "Index " + slider.getCurrentItem() + " URL " + String.valueOf(urlImagem));


                    if (pedido != null && pedido.getNumpedven() > 0) {

                        double qtd = Double.parseDouble(edtQtd.getText().toString());

                        if (descricao == null) {
                            descricao = produto.Descricao;
                        }


                        if(produto.Upsell != null && produto.Upsell.size()>0){
                            ItemPedidos itemPedidos = pedidosHelper.IncluirItem(qtd, produto, descricao, urlImagem, 0, usuarioLogado.get("filialorig"),Integer.parseInt(usuarioLogado.get("codfil")));
                            itemPedidos.setUpsellTipo("PRINCIPAL");
                            lista_itensPed.add(itemPedidos);
                            /* Inclusao automatica de upsell temporariamente desativada.
                            for(int i = 0 ; i < produto.Upsell.size(); i++ ){
                                br.com.acalfortaleza.fikonmais.models.ProdutoGemco up = produto.Upsell.get(i);
                                boolean existe = false;
                                for (ItemPedidos ip : lista_itensPed) {
                                    if (ip.getCoditprod() == up.Codigo) { existe = true; break; }
                                }
                                if (!existe) {
                                    ItemPedidos upsell = pedidosHelper.IncluirItem(qtd, up, up.Descricao, up.Imagem, 0, usuarioLogado.get("filialorig"),Integer.parseInt(usuarioLogado.get("codfil")));
                                    upsell.setUpsellTipo("UPSELL");
                                    lista_itensPed.add(upsell);
                                }
                            }
                            */
                        }else{
                            ItemPedidos itemPedidos = pedidosHelper.IncluirItem(qtd, produto, descricao, urlImagem, 0, usuarioLogado.get("filialorig"),Integer.parseInt(usuarioLogado.get("codfil")));
                            itemPedidos.setUpsellTipo(null);
                            lista_itensPed.add(itemPedidos);
                        }



                        qtd_carrinho.setText(String.valueOf(lista_itensPed.size()));
                        img_notificacao.setVisibility(View.VISIBLE);
                        pedido.setItens(lista_itensPed);
                        ConsultaActivity.pedido = pedido;
                        myRef.child(usuarioLogado.get("codigo")).setValue(pedido);
                        Toast.makeText(context, "Produto inserido no carrinho!", Toast.LENGTH_SHORT).show();

                        /*if(IntentCaller!=null && IntentCaller.equals(ConsultaActivity.class.toString())){
                            consultarSugestoesProduto();
                        }
                        else if(IntentCaller!=null && IntentCaller.equals(PopupSugestaoProduto.class.toString())){
                            Intent resultIntent = new Intent();
                            if(orcamento!=null){
                                resultIntent.putExtra("orcamento",(Serializable) orcamento);
                            }
                            if(pedido!=null){
                                resultIntent.putExtra("pedido",(Serializable) pedido);
                            }
                            getActivity().setResult(Activity.RESULT_OK, resultIntent);
                            getActivity().finish();
                        }
                        else {
                            getActivity().finish();
                        }*/
                        getActivity().finish();

                    }

                }


            }
        });

        if (produto != null) {

            NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            preco.setText(real.format(produto.Preco));
            aplicarPrecoPromocionalCarregado();
            consultarPrecoDePromocaoSeNecessario();
            unidade.setText(produto.Unidade);
            if(descricao == null){
                descricaoProduto.setText(produto.Descricao);
            }else{
                descricaoProduto.setText(descricao);
            }

            aplicarIndicadoresComissaoProduto();

            codigoItem.setText("(" + produto.Codigo + ")");

            Date dtAtualizacaoPreco = null;
            if(produto.AtualizacaoPreco!=null){

                try {

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    dtAtualizacaoPreco = dateFormat.parse(produto.AtualizacaoPreco);

                    DateFormat ano = new SimpleDateFormat("yyyy");

                    if(dtAtualizacaoPreco!=null && ano.format(dtAtualizacaoPreco).equals("0001")){
                        dtAtualizacaoPreco = null;
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }

            }

            if(dtAtualizacaoPreco!=null){

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dataAtualizacao = "Última Atualização de Preço: " + dateFormat.format(dtAtualizacaoPreco);

                txtAualizacaoPreco.setText(dataAtualizacao);
                ajustarPosicaoInformacoesPreco(false);

            }
            else {

                txtAualizacaoPreco.setText("");
                txtAualizacaoPreco.setVisibility(View.GONE);

            }


            slider.setAdapter(slideAdapter);

            TxtPrecoPor.setText("");
            TxtPrecoPor.setVisibility(View.GONE);

            switch (produto.Situacao) {
                case "PR":
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.promocao);

                    etiqueta.setImageBitmap(bitmap);
                    double precoPor = produto.PrecoAtualPromocional > 0
                            ? produto.PrecoAtualPromocional
                            : produto.Preco;
                    BigDecimal precoPrazo = getPrecoPrazoPromocional(precoPor);
                    TxtPrecoPor.setText("Preço à prazo: " + real.format(precoPrazo.doubleValue()));
                    TxtPrecoPor.setVisibility(View.VISIBLE);
                    ajustarPosicaoInformacoesPreco(true);

                    break;

                case "IN":
                    Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.inativo);

                    etiqueta.setImageBitmap(bitmap2);
                    break;

                case "EN":
                    Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.encomenda);

                    etiqueta.setImageBitmap(bitmap3);
                    break;

                case "LC":
                    Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.lojaconceito);

                    etiqueta.setImageBitmap(bitmap4);
                    break;

            }

            if (!"PR".equals(produto.Situacao)) {
                if (txtPrecoDe.getVisibility() != View.VISIBLE) {
                    txtPrecoDe.setVisibility(View.GONE);
                    txtEconomize.setVisibility(View.GONE);
                }
                TxtPrecoPor.setVisibility(View.GONE);
                ajustarPosicaoInformacoesPreco(false);
            }

        }

        return layout;

    }

    private BigDecimal getPrecoPrazoPromocional(double precoBase) {
        return BigDecimal.valueOf(precoBase)
                .divide(BigDecimal.valueOf(0.95d), 6, RoundingMode.HALF_EVEN)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private void aplicarIndicadoresComissaoProduto() {
        if (produto == null) {
            if (imgSelecao != null) {
                imgSelecao.setVisibility(View.GONE);
            }
            if (imgAcelerado != null) {
                imgAcelerado.setVisibility(View.GONE);
            }
            return;
        }

        boolean temComissaoIncentivada = produto.PercentualComissao > 0
                || (produto.Incentivado != null && produto.Incentivado.trim().equalsIgnoreCase("S"));
        boolean temComissaoAcelerada = produto.PercIncentivo > 0;

        Log.d("IconeComissaoDebug", "ProdutoFragment | codigo=" + produto.Codigo
                + " | percentualComissao=" + produto.PercentualComissao
                + " | percIncentivo=" + produto.PercIncentivo
                + " | incentivado=" + produto.Incentivado
                + " | temComissaoIncentivada=" + temComissaoIncentivada
                + " | temComissaoAcelerada=" + temComissaoAcelerada);

        if (imgSelecao != null) {
            imgSelecao.setVisibility(View.GONE);
        }
        if (imgAcelerado != null) {
            imgAcelerado.setVisibility(View.GONE);
        }

        if (temComissaoIncentivada) {
            imgSelecao.setVisibility(View.VISIBLE);
            imgSelecao.setImageResource(R.drawable.ic_img_selecao);
        } else if (produto.BloqueioDesc != null && produto.BloqueioDesc.equals("S")) {
            imgSelecao.setVisibility(View.VISIBLE);
            imgSelecao.setImageResource(R.drawable.desconto_produto);
        } else {
            imgSelecao.setVisibility(View.GONE);
        }

        if (imgAcelerado != null && temComissaoAcelerada) {
            imgAcelerado.setVisibility(View.VISIBLE);
            imgAcelerado.setImageResource(R.drawable.ic_img_acelerado);
        }
    }

    private void consultarPrecoDePromocaoSeNecessario() {
        if (produto == null || produto.Codigo <= 0) {
            return;
        }

        int codigoItemProd = produto.Codigo;
        int codFil = 1;
        try {
            if (codFilPreco != null && !codFilPreco.trim().isEmpty()) {
                codFil = Integer.parseInt(codFilPreco.trim());
            } else if (usuarioLogado != null && usuarioLogado.get("codfil") != null) {
                codFil = Integer.parseInt(usuarioLogado.get("codfil").trim());
            }
        } catch (Exception ignore) {}

        final int finalCodigo = codigoItemProd;
        final int finalCodFil = codFil;

        if (produtoHelper == null) {
            produtoHelper = new ProdutoHelper();
        }

        new Thread(() -> {
            try {
                ItemPedidos itemTemp = new ItemPedidos();
                itemTemp.setCoditprod(finalCodigo);
                itemTemp.setPrecoUnit(produto.Preco);
                itemTemp.setQtcomp(1.0);
                itemTemp.setItem(1);

                List<ItemPedidos> listaTemp = new ArrayList<>();
                listaTemp.add(itemTemp);

                List<PromocaoPrecoProduto> promocoes = produtoHelper.consultarPromocoesFikonPedido(listaTemp, String.valueOf(finalCodFil));
                if (promocoes != null && !promocoes.isEmpty()) {
                    for (PromocaoPrecoProduto promocao : promocoes) {
                        if (promocao != null && promocao.precoAtual > 0) {
                            produto.PrecoAtualPromocional = promocao.precoAtual;
                            produto.PromocaoFikon = true;
                            if (promocao.precoAnterior > 0) {
                                produto.PrecoAnteriorPromocional = promocao.precoAnterior;
                            } else if (produto.PrecoOriginalConsulta <= 0) {
                                produto.PrecoOriginalConsulta = produto.Preco;
                            }
                            if (promocao.nomePromocao != null) {
                                produto.NomePromocao = promocao.nomePromocao;
                            }
                            if (promocao.vigencia != null) {
                                produto.VigenciaPromocao = promocao.vigencia;
                            }
                            break;
                        }
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            aplicarPrecoPromocionalCarregado();
                        });
                    }
                }
            } catch (Exception e) {
                Log.e("PrecoDeDebug", "Erro ao consultar preço promocional Fikon para produto no Fragment via Pedido", e);
            }
        }).start();
    }

    private void aplicarPrecoPromocionalCarregado() {
        if (produto == null || preco == null || txtPrecoDe == null || txtEconomize == null) {
            return;
        }

        double precoAtual = produto.PrecoAtualPromocional > 0
                ? produto.PrecoAtualPromocional
                : produto.Preco;
        double precoDe = produto.PrecoAnteriorPromocional > 0
                ? produto.PrecoAnteriorPromocional
                : (produto.PrecoOriginalConsulta > 0
                    ? produto.PrecoOriginalConsulta
                    : produto.Preco);

        if (precoAtual <= 0 || precoDe <= 0 || precoAtual >= precoDe) {
            if (produto.PrecoAtualPromocional > 0 && produto.Preco > produto.PrecoAtualPromocional) {
                precoAtual = produto.PrecoAtualPromocional;
                precoDe = produto.Preco;
            } else {
                txtTipoPreco.setText("");
                txtTipoPreco.setVisibility(View.GONE);
                txtPrecoDe.setText("");
                txtPrecoDe.setVisibility(View.GONE);
                txtEconomize.setText("");
                txtEconomize.setVisibility(View.GONE);
                layoutPromocaoInfo.setVisibility(View.GONE);
                return;
            }
        }

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        BigDecimal economia = BigDecimal.valueOf(precoDe).subtract(BigDecimal.valueOf(precoAtual));
        preco.setText(real.format(precoAtual));
        txtTipoPreco.setText(produto.PromocaoFikon ? "À vista" : "");
        txtTipoPreco.setVisibility(produto.PromocaoFikon ? View.VISIBLE : View.GONE);
        txtPrecoDe.setText("De: " + real.format(precoDe));
        txtPrecoDe.setPaintFlags(txtPrecoDe.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        txtPrecoDe.setVisibility(View.VISIBLE);
        txtEconomize.setText("Economize " + real.format(economia)
                + " (" + montarTextoPercentualDesconto(precoDe, precoAtual) + ")");
        txtEconomize.setVisibility(View.VISIBLE);

        String nomePromocao = produto.NomePromocao == null ? "" : produto.NomePromocao.trim();
        String vigenciaPromocao = formatarVigenciaPromocao(produto.VigenciaPromocao);
        txtNomePromocao.setText(nomePromocao);
        txtValidadePromocao.setText(vigenciaPromocao);
        layoutPromocaoInfo.setVisibility(
                nomePromocao.isEmpty() && vigenciaPromocao.isEmpty() ? View.GONE : View.VISIBLE
        );
    }

    private String formatarVigenciaPromocao(String vigencia) {
        if (vigencia == null || vigencia.trim().isEmpty()) {
            return "";
        }

        String data = vigencia.trim();
        try {
            if (data.length() >= 10) {
                Date dataVigencia = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(data.substring(0, 10));
                if (dataVigencia != null) {
                    data = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR")).format(dataVigencia);
                }
            }
        } catch (ParseException ignored) {
        }
        return "Válido até: " + data;
    }

    private String montarTextoPercentualDesconto(double precoDe, double precoAtual) {
        if (precoDe <= 0 || precoAtual <= 0 || precoAtual >= precoDe) {
            return "";
        }

        BigDecimal valorDesconto = BigDecimal.valueOf(precoDe)
                .subtract(BigDecimal.valueOf(precoAtual));
        BigDecimal percentualDesconto = valorDesconto
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(precoDe), 4, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);

        return percentualDesconto.toPlainString().replace('.', ',') + "%";
    }

    private int getCoditprodBaseCincoDigitos(int codigoProduto) {
        String codigoTexto = String.valueOf(codigoProduto);
        if (codigoTexto.length() <= 5) {
            try {
                return Integer.parseInt(codigoTexto);
            } catch (Exception ignore) {
                return 0;
            }
        }

        String codigoBase = codigoTexto.substring(0, 5);
        try {
            return Integer.parseInt(codigoBase);
        } catch (Exception ignore) {
            return 0;
        }
    }

    private void ajustarPosicaoInformacoesPreco(boolean mostrarPrecoPrazo) {
        boolean temAtualizacaoPreco = txtAualizacaoPreco.getText() != null
                && !txtAualizacaoPreco.getText().toString().trim().isEmpty();
        txtAualizacaoPreco.setVisibility(temAtualizacaoPreco ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (orcamento != null && pedido == null) {
            lista_itensOrc = orcamento.getItens();
            qtd_carrinho.setText(String.valueOf(lista_itensOrc.size()));
            img_notificacao.setVisibility(View.VISIBLE);
        }

        if (orcamento == null && pedido != null) {
            lista_itensPed = pedido.getItens();
            qtd_carrinho.setText(String.valueOf(lista_itensPed.size()));
            img_notificacao.setVisibility(View.VISIBLE);
        }
    }

    private File getOrcCacheFolder() {
        File folder = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "OrcamentoImgCache");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    private String getOrcCachedImagePath(int cod, String url) {
        String suffix;
        try {
            int h = (url == null) ? 0 : url.hashCode();
            suffix = String.valueOf(Math.abs(h));
        } catch (Exception e) {
            suffix = "0";
        }
        return new File(getOrcCacheFolder(), "orc_" + cod + "_" + suffix + ".png").getAbsolutePath();
    }

    private String getOrcUrlSidecarPath(int cod, String url) {
        String suffix;
        try {
            int h = (url == null) ? 0 : url.hashCode();
            suffix = String.valueOf(Math.abs(h));
        } catch (Exception e) {
            suffix = "0";
        }
        return new File(getOrcCacheFolder(), "orc_" + cod + "_" + suffix + ".url").getAbsolutePath();
    }

    private void cacheOrcImage(final int cod, final String url, final ItemOrcamento item) {
        if (url == null || url.equals("")) return;
        File file = new File(getOrcCachedImagePath(cod, url));
        File sidecar = new File(getOrcUrlSidecarPath(cod, url));
        if (file.exists() && file.length() > 0) {
            try {
                if (!sidecar.exists() || sidecar.length() == 0) {
                    FileOutputStream outUrl = new FileOutputStream(sidecar);
                    outUrl.write(url.getBytes());
                    outUrl.flush();
                    outUrl.close();
                }
            } catch (Exception ignored) { }
            item.setImagem(file.getAbsolutePath());
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
                    try {
                        FileOutputStream outUrl = new FileOutputStream(sidecar);
                        outUrl.write(url.getBytes());
                        outUrl.flush();
                        outUrl.close();
                    } catch (Exception ignored) { }
                    item.setImagem(file.getAbsolutePath());
                    Log.d("ORC_IMG_CACHE_SAVE", "COD " + cod + " cached " + file.getAbsolutePath() + " from " + url);
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

    public boolean validarQtd() {
        String qtd = edtQtd.getText().toString().trim();
        if (qtd.isEmpty() || Double.parseDouble(qtd)==0) {
            layoutQtd.setErrorEnabled(true);
            layoutQtd.setError("Informe a quantidade");
            return false;

        } else {
            layoutQtd.setErrorEnabled(false);
            return true;
        }




    }


    public boolean validarTipo() {
        String tipo = produto.Linha;
        if (tipo.equals("Encomenda")) {
            layoutQtd.setErrorEnabled(true);
            layoutQtd.setError("Tipo de produto não pode ser adcionado ao pedido!");
            return false;

        } else {
            layoutQtd.setErrorEnabled(false);
            return true;
        }




    }

    public boolean validarServico() {
        String situacao = produto.Situacao;
        if(lista_itensOrc==null && situacao.equals("SV") ){
            isServico = true;


        }

        if (situacao.equals("SV") && !isServico ) {
            layoutQtd.setErrorEnabled(true);
            layoutQtd.setError("Serviço não pode ser adcionado ao carrinho de produtos!");
            return false;

        } else if(!situacao.equals("SV") && isServico ) {
            layoutQtd.setErrorEnabled(true);
            layoutQtd.setError("Produto não pode ser adcionado ao carrinho de serviço!");
            return false;
        }
        else {
            layoutQtd.setErrorEnabled(false);
            return true;
        }




    }

    public boolean validarConversao(){
        int qtdArredondado =0;
        String qtdDigitada ="0";
        String qtdCalculada = "0";
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        final DecimalFormat df = new DecimalFormat("0.00",symbols);


            float divisao = (float) (Double.parseDouble(edtQtd.getText().toString())/produto.Qtemb);
            qtdArredondado = (int) Math.ceil(divisao);
            qtdDigitada = df.format(Double.parseDouble(edtQtd.getText().toString()));
            qtdCalculada = df.format(produto.Qtemb*qtdArredondado);
        if(!produto.Ncm.equals("32041700") && !produto.Ncm.equals("32064990")) {
            if (!qtdDigitada.equals(qtdCalculada)) {
                layoutQtd.setErrorEnabled(true);
                if (produto.Qtemb == 1) {
                    layoutQtd.setError("Produto vendido em unidade, qtd alterada para: " + (df.format(produto.Qtemb * qtdArredondado)));
                } else {
                    layoutQtd.setError("Produto vendido em caixa, qtd alterada para: " + (df.format(produto.Qtemb * qtdArredondado)));
                }
                edtQtd.setText(df.format(produto.Qtemb * qtdArredondado));
                return false;

            } else {
                layoutQtd.setEnabled(false);
                return true;
            }
        }else{
            layoutQtd.setEnabled(false);
            return true;
        }

    }


    public boolean validarServicoPed() {
        String situacao = produto.Situacao;
        isServico = false;
        if(lista_itensPed==null && situacao.equals("SV") ){
            isServico = true;
        }

        if(lista_itensPed !=null  ){

            for(int i = 0 ; i < lista_itensPed.size(); i ++){

                if(lista_itensPed.get(i).getSituacao().equals("SV") ) {
                    isServico = true;
                }



            }

        }

        if(pedido.getTpnota().getCodTpNota()==536){
            isServico = true;
        }

        if (situacao.equals("SV") && !isServico ) {
            layoutQtd.setErrorEnabled(true);
            layoutQtd.setError("Serviço não pode ser adcionado ao carrinho de produtos!");
            return false;

        } else if(!situacao.equals("SV") && isServico ) {
            layoutQtd.setErrorEnabled(true);
            layoutQtd.setError("Produto não pode ser adcionado ao carrinho de serviço!");
            return false;
        }
        else {
            layoutQtd.setErrorEnabled(false);
            return true;
        }




    }


    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void consultarSugestoesProduto(){

        if(alertaDialog!=null && alertaDialog.isShowing()){
            alertaDialog.dismiss();
        }

        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Consultando Sugestões de Produto");
        progressDialog.setMessage("Por favor aguarde");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        try{

                            produtoHelper = new ProdutoHelper();

                            List<ProdutoGemco> listSugestoesFinal = new ArrayList<>();

                            if(produto!=null && produto.Skuid!=null){

                                List<SugestaoProdutoVtex> listSugestoes = produtoHelper.ConsultarSugestoesProdutoVtex(produto.Skuid);

                                if(listSugestoes!=null && !listSugestoes.isEmpty()) {

                                    CountDownLatch countDownLatch = new CountDownLatch(listSugestoes.size());

                                    for (SugestaoProdutoVtex sugestaoProduto : listSugestoes) {

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                try {

                                                    ProdutoHelper produtoHelper2 = new ProdutoHelper();

                                                    List<ProdutoGemco> listProduto = produtoHelper2.ConsultarProdutoPorCodigo(sugestaoProduto.productReferenceCode, usuarioLogado.get("codfil").toString());

                                                    if (listProduto != null && !listProduto.isEmpty()) {

                                                        ProdutoGemco produtoGemco = listProduto.get(listProduto.size()-1);

                                                        if (produtoGemco != null && validarEstoque(produtoGemco)) {
                                                            listSugestoesFinal.add(produtoGemco);
                                                        }

                                                    }

                                                } catch (Exception ex2) {}

                                                countDownLatch.countDown();

                                            }
                                        }).start();

                                    }

                                    countDownLatch.await();

                                }

                            }

                            if(listSugestoesFinal!=null && !listSugestoesFinal.isEmpty()){

                                Intent intent = new Intent(getActivity(), PopupSugestaoProduto.class);
                                intent.putExtra("ListSugestoesProduto",(Serializable) listSugestoesFinal);
                                if (orcamento != null && pedido == null) {
                                    intent.putExtra("orcamento", (Serializable) orcamento);
                                }
                                if (orcamento == null && pedido != null) {
                                    intent.putExtra("pedido", (Serializable) pedido);
                                }

                                intent.putExtra("intentCaller", ProdutoFragments.class.toString());
                                if(progressDialog!=null && progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                startActivity(intent);

                            }

                        }catch (Exception ex){
                            System.out.println(ex.toString());
                        }

                        if(progressDialog!=null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }

                        getActivity().finish();

                    }
                }
        ).start();

    }

    public boolean validarEstoque(ProdutoGemco produtoGemco){

        try {

            if(produtoGemco!=null &&
               produtoGemco.CodigoBarra!=null && !produtoGemco.CodigoBarra.isEmpty() &&
               produtoGemco.Preco>0
            ){

                if(produtoGemco.CodigoBarra.get(0).Estoque!=null && !produtoGemco.CodigoBarra.get(0).Estoque.isEmpty()){

                    for (Estoque estoque: produtoGemco.CodigoBarra.get(0).Estoque) {

                        float fisico = Float.parseFloat(estoque.Fisico);
                        float reserva = Float.parseFloat(estoque.Reserva);
                        int filial = estoque.CodFil;

                        if(filial>0 && filial!=8 && filial!=150 && filial!=13 && filial!=900 && filial!=500){

                            if(fisico-reserva>0){
                                return true;
                            }

                        }

                    }

                }

            }

        }catch (Exception exception){
            exception.printStackTrace();
        }

        return false;

    }

    public Boolean validarSituacaoConceito(){

        try{

            if(
                produto!=null
                && produto.Situacao!=null
                && produto.Situacao.equals("LC")
                && usuarioLogado.get("codfil") != null
            ){

                if(usuarioLogado.get("codfil").equals("3") || usuarioLogado.get("codfil").equals("21") || usuarioLogado.get("codfil").equals("501")){
                    return true;
                }
                else{
                    btIncluirOrc.setEnabled(false);
                    btIncluirPed.setEnabled(false);
                    layoutQtd.setErrorEnabled(true);
                    layoutQtd.setError("Produto com situação LC. Só pode ser vendido pela Loja Conceito.");
                    return false;
                }

            }

        }
        catch (Exception exception){
            exception.printStackTrace();
        }

        return null;

    }


}
