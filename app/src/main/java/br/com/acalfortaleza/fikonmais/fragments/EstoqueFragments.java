package br.com.acalfortaleza.fikonmais.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.SugestoesActivity;
import br.com.acalfortaleza.fikonmais.adapters.EstoqueAdapter;
import br.com.acalfortaleza.fikonmais.models.Estoque;
import br.com.acalfortaleza.fikonmais.models.Lotes;
import br.com.acalfortaleza.fikonmais.models.Pontas;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.popup.PopupTpNota;
import br.com.acalfortaleza.fikonmais.popup.PopupValidarEstoque;

public class EstoqueFragments extends Fragment {

    private Estoque estoque;

    private List<Estoque> estoques;

    private View layout;

    private ListView listaEstoques;

    private ProdutoGemco produto;

    private EstoqueAdapter adapter;

    private List<Lotes> lotes;

    private List<Pontas> pontas;

    private AlertDialog alertaDialog;

    private Context context;

    private Button btnFaltaEstoque;

    public EstoqueFragments(){

    }

    @SuppressLint("ValidFragment")
    public EstoqueFragments(ProdutoGemco produtoGemco) {
       this.produto = produtoGemco;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      if(getArguments()!=null){

          produto = (ProdutoGemco) getArguments().getSerializable("produtogemco");
      }



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



            layout = inflater.inflate(R.layout.fragment_estoque,
                    container, false);
            configurarInsetInferior(layout);
            listaEstoques = layout.findViewById(R.id.listaEstoques);

            btnFaltaEstoque = layout.findViewById(R.id.FragmentEstoque_BtnFaltaEstoque);

            if (produto != null && produto.CodigoBarra != null
                    && !produto.CodigoBarra.isEmpty()
                    && produto.CodigoBarra.get(0) != null
                    && produto.CodigoBarra.get(0).Estoque != null) {
                estoques = produto.CodigoBarra.get(0).Estoque;
                adapter = new EstoqueAdapter(getActivity(),estoques);
                listaEstoques.setAdapter(adapter);
            }




            listaEstoques.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            this.listaEstoques.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    estoque = (Estoque) parent.getItemAtPosition(position);
                    lotes = estoque.Lotes;
                    if (lotes == null || lotes.isEmpty()) {
                        lotes = new java.util.ArrayList<>();
                        Lotes semLote = new Lotes();
                        semLote.Lote = "";
                        semLote.Disponivel = "0";
                        semLote.Reserva = "0";
                        semLote.Bloqueado = "0";
                        lotes.add(semLote);
                    }
                    pontas = estoque.Pontas;

                    final DecimalFormat df = new DecimalFormat("0.00");




                        if ((lotes.get(0).Lote != null && lotes.get(0).Lote.length() > 1
                                && Double.parseDouble(estoque.Disponivel) > 0)
                                && (estoque.Pontas != null) && (estoque.Pontas.toString().length() > 2)) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);


                            builder.setMessage("Escolha uma opção");

                            builder

                                    .setPositiveButton("Ponta de estoque", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            String resultado = "";
                                            NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                                            for (Pontas p : pontas) {

                                                resultado = resultado + ("<b>" + "Lote: " + "</b>" + p.Lote + "" + " <br />" +
                                                        "<b>" + "Disponivel: " + "</b>" + df.format(Double.parseDouble(p.Disponivel)) + "" + " <br />" +"<b>" + "De: " + "</b>" + real.format(Double.parseDouble(p.De))+ "<br />" +
                                                        "<b>" + "Por:" + "<b />"+
                                                        "<br />" +
                                                        "<b>" + "Desconto à vista: " + "</b>" + p.Desconto_AVISTA + "<br />" +
                                                        "<b>" + "Valor à vista: " + "</b>" + real.format(Double.parseDouble(p.Valor_AVISTA)) + " <br />" +
                                                        "<br />" +
                                                        "<b>" + "Desconto a prazo: " + "</b>" + p.Desconto_APRAZO + "<br />" +
                                                        "<b>" + "Valor a prazo: " + "</b>" + real.format(Double.parseDouble(p.Valor_APRAZO)) + " <br />" +
                                                        "<br />" +


                                                        "<br />");
                                            }


                                            AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                                            alerta.setIcon(R.drawable.ic_ponta);
                                            alerta.setTitle("Pontas de Estoque");
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

                            builder

                                    .setNegativeButton("Lote de piso", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    String resultado = "";

                                    for (Lotes l : lotes) {

                                        resultado = resultado + ("<b>" + "Lote: " + "</b>" + l.Lote + "<br />" +
                                                "<b>" + "Disponível: " + "</b>" + df.format(Double.parseDouble(l.Disponivel)) + " <br />" +
                                                "<b>" + "Reserva: " + "</b>" + df.format(Double.parseDouble(l.Reserva)) + "" + " <br />" +
                                                "<b>" + "Bloqueado: " + "</b>" + df.format(Double.parseDouble(l.Bloqueado)) + "<br />" +
                                                "<br />");
                                    }


                                    AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                                    alerta.setIcon(R.drawable.ic_lote);
                                    alerta.setCancelable(false);
                                    alerta.setTitle("Lotes");
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

                            builder.create().show();

                        } else {


                            if (lotes.get(0).Lote == null || lotes.get(0).Lote.equals("")
                                    || Double.parseDouble(estoque.Disponivel)==0) {

                            } else {
                                String resultado = "";

                                for (Lotes l : lotes) {

                                    resultado = resultado + ("<b>" + "Lote: " + "</b>" + l.Lote + "<br />" +
                                            "<b>" + "Disponível: " + "</b>" + df.format(Double.parseDouble(l.Disponivel)) + " <br />" +
                                            "<b>" + "Reserva: " + "</b>" + df.format(Double.parseDouble(l.Reserva)) + "" + " <br />" +
                                            "<b>" + "Bloqueado: " + "</b>" + df.format(Double.parseDouble(l.Bloqueado)) + "<br />" +
                                            "<br />");
                                }


                                AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                                alerta.setIcon(R.drawable.ic_lote);
                                alerta.setCancelable(false);
                                alerta.setTitle("Lotes");
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
                            if (estoque.Pontas != null) {
                                if (pontas.toString().equals("[]")|| Double.parseDouble(estoque.Disponivel)==0) {

                                } else {
                                    String resultado = "";
                                    NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                                    for (Pontas p : pontas) {

                                        resultado = resultado + ("<b>" + "Lote: " + "</b>" + p.Lote + "" + " <br />" +
                                                "<b>" + "Disponivel: " + "</b>" + df.format(Double.parseDouble(p.Disponivel)) + "" + " <br />" +"<b>" + "De: " + "</b>" + real.format(Double.parseDouble(p.De))+ "<br />" +
                                                "<b>" + "Por:" + "<b />"+
                                                "<br />" +
                                                "<b>" + "Desconto à vista: " + "</b>" + p.Desconto_AVISTA + "<br />" +
                                                "<b>" + "Valor à vista: " + "</b>" + real.format(Double.parseDouble(p.Valor_AVISTA)) + " <br />" +
                                                "<br />" +
                                                "<b>" + "Desconto a prazo: " + "</b>" + p.Desconto_APRAZO + "<br />" +
                                                "<b>" + "Valor a prazo: " + "</b>" + real.format(Double.parseDouble(p.Valor_APRAZO)) + " <br />" +
                                                "<br />" +


                                                "<br />");
                                    }


                                    AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                                    alerta.setIcon(R.drawable.ic_ponta);
                                    alerta.setCancelable(false);
                                    alerta.setTitle("Pontas de Estoque");
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

                            }

                        }
                    }


            });


            this.listaEstoques.setOnItemLongClickListener(
                    new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent,
                                                       View view, int position, long id) {
                            estoque = (Estoque) parent.getItemAtPosition(position);

                            if (estoque.EstoqueFikon) {
                                Toast.makeText(context,
                                        "A validação deste estoque não está disponível na API Fikon.",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            }

                            if(estoque.CodFil==8){
                                estoque.CodFil = 5;
                                estoque.Filial = "PE.CICERO";
                            }

                            Intent intent = new Intent(context, PopupValidarEstoque.class);
                            intent.putExtra("estoque",estoque);
                            intent.putExtra("produto",produto);
                            startActivity(intent);

                            estoque = (Estoque) parent.getItemAtPosition(position);

                            return false;
                        }
                    });

            registerForContextMenu(listaEstoques);


            this.btnFaltaEstoque.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, SugestoesActivity.class);
                    intent.putExtra("FaltaProduto", produto);
                    startActivity(intent);

                }
            });


            return layout;
    }


    private void configurarInsetInferior(View root) {
        if (root == null) {
            return;
        }

        final int paddingLeftBase = root.getPaddingLeft();
        final int paddingTopBase = root.getPaddingTop();
        final int paddingRightBase = root.getPaddingRight();
        final int paddingBottomBase = root.getPaddingBottom();
        final float density = root.getResources().getDisplayMetrics().density;
        final int espacoExtra = Math.round(20 * density);

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            androidx.core.graphics.Insets navigationInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            view.setPadding(
                    paddingLeftBase,
                    paddingTopBase,
                    paddingRightBase,
                    paddingBottomBase + navigationInsets.bottom + espacoExtra
            );
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
    }


    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }


}
