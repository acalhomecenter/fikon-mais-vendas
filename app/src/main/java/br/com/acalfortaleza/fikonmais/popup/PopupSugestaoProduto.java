package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.acalfortaleza.fikonmais.ConsultaActivity;
import br.com.acalfortaleza.fikonmais.GeralProdutoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.PedidosAdapter;
import br.com.acalfortaleza.fikonmais.adapters.ProdutosAdapter;
import br.com.acalfortaleza.fikonmais.adapters.SugestoesProdutoAdapter;
import br.com.acalfortaleza.fikonmais.applications.RecyclerItemClickListener;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.models.SugestaoProdutoVtex;

public class PopupSugestaoProduto extends Activity {

    private List<ProdutoGemco> listProdutos;
    private RecyclerView recyclerView;
    private SugestoesProdutoAdapter produtosAdapter;
    private Button btnFechar;
    ProdutoGemco produtoGemco;
    AlertDialog.Builder alertBuilder;
    private static Pedidos pedido;
    private static Orcamento orcamento;
    private static int REQUEST_CODE = new Random().nextInt(Integer.MAX_VALUE);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_sugestao_produto);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .9));

        pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
        orcamento = (Orcamento) getIntent().getSerializableExtra("orcamento");

        recyclerView = findViewById(R.id.pop_sugestao_produto_recycler);

        btnFechar = findViewById(R.id.pop_sugestao_produto_btn_fechar);

        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listProdutos = (List<ProdutoGemco>) getIntent().getSerializableExtra("ListSugestoesProduto");

        produtosAdapter = new SugestoesProdutoAdapter(this, listProdutos);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(PopupSugestaoProduto.this));
        recyclerView.setAdapter(produtosAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                produtoGemco = listProdutos.get(position);

                if(produtoGemco!=null){
                    Intent intent = new Intent(PopupSugestaoProduto.this, GeralProdutoActivity.class);
                    if (orcamento != null && pedido == null) {
                        intent.putExtra("orcamento", (Serializable) orcamento);
                    }
                    if (orcamento == null && pedido != null) {
                        intent.putExtra("pedido", (Serializable) pedido);
                    }

                    intent.putExtra("produtogemco", produtoGemco);
                    intent.putExtra("intentCaller", PopupSugestaoProduto.class.toString());

                    startActivityForResult(intent,REQUEST_CODE);

                }

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

        }));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE){
            if(resultCode==Activity.RESULT_OK){

                if(data.getSerializableExtra("orcamento")!=null){
                    orcamento = (Orcamento) data.getSerializableExtra("orcamento");
                }

                if(data.getSerializableExtra("pedido")!=null){
                    pedido = (Pedidos) data.getSerializableExtra("pedido");
                }

            }
        }

    }

}
