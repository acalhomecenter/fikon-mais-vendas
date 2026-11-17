package br.com.acalfortaleza.fikonmais;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.adapters.ViewPagerAdapter;
import br.com.acalfortaleza.fikonmais.fragments.CaracteristicasFragments;
import br.com.acalfortaleza.fikonmais.fragments.EstoqueFragments;
import br.com.acalfortaleza.fikonmais.fragments.ProdutoFragments;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Consultor;
import br.com.acalfortaleza.fikonmais.models.Estoque;
import br.com.acalfortaleza.fikonmais.models.ImagemCiashop;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;

public class GeralProdutoActivity extends BaseAcalActivity  {


    private ViewPager viewPager;
    private TabLayout tabLayout;
    ViewPagerAdapter adapter;
    private Bundle bundle;
    private ProdutoGemco produto;
    private String caracteristicas;
    private String descricao;
    private Consultor consultor;
    private LoginHelper helperLogin;
    private HashMap<String,String> usuario;
    private Orcamento orcamento;
    private Pedidos pedido;
    private static HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;




             ProdutoFragments produtoFragments;
             EstoqueFragments estoqueFragments;
             CaracteristicasFragments caracteristicasFragments;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.geral_produto_status_bar_bg);
        if (statusBarBg == null) {
            return;
        }

        ViewGroup.LayoutParams params = statusBarBg.getLayoutParams();
        params.height = getStatusBarHeight();
        statusBarBg.setLayoutParams(params);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(false);
        setContentView(R.layout.activity_geral_produto);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.geral_produto_toolbar);
        setSupportActionBar(toolbar);

        helperLogin = new LoginHelper(GeralProdutoActivity.this);
        usuario = helperLogin.GetUsuarioSalvo();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Produto");
        getSupportActionBar().setElevation(0);

        loginHelper = new LoginHelper(GeralProdutoActivity.this);

        usuarioLogado = loginHelper.GetUsuarioSalvo();

        bundle = new Bundle();

        bundle.putInt("codfil", Integer.parseInt(usuario.get("codfil")));


        if(getIntent().getSerializableExtra("produtogemco")!=null){

            produto = (ProdutoGemco)getIntent().getSerializableExtra("produtogemco");
            bundle.putSerializable("produtogemco",produto);
            if(produto.Caracteristicas!=null)
            caracteristicas = produto.Caracteristicas;
            bundle.putString("caracteristicas",caracteristicas);
        }


        if(getIntent().getSerializableExtra("orcamento")!=null){

            orcamento = (Orcamento) getIntent().getSerializableExtra("orcamento");
            bundle.putSerializable("orcamento",orcamento);
        }

        if(getIntent().getSerializableExtra("pedido")!=null){

            pedido = (Pedidos) getIntent().getSerializableExtra("pedido");
            bundle.putSerializable("pedido",pedido);
        }

        if(getIntent().getStringExtra("intentCaller")!=null){
            bundle.putString("intentCaller", getIntent().getStringExtra("intentCaller"));
        }


        viewPager = findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(3);




        tabLayout = findViewById(R.id.tabs);



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(),false);
                switch (tab.getPosition()){
                    case 0 :
                        getSupportActionBar().setTitle("Produto");
                        break;
                    case 1 :
                        getSupportActionBar().setTitle("Estoque");
                        break;
                    case 2 :
                        getSupportActionBar().setTitle("Características");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       getMenuInflater().inflate(R.menu.menu_geral_produto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (ProdutoFragments.orcamento != null && ProdutoFragments.pedido == null) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Ops :|");
                builder.setMessage("Você possui um orçamento iniciado, se sair irá perder, deseja sair?");
                builder
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                ProdutoFragments.orcamento = null;
                                ConsultaActivity.orcamento = null;
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("orcamentos").child(usuarioLogado.get("codigo"));
                                myRef.removeValue();
                                Intent itent = new Intent(GeralProdutoActivity.this, MainActivity.class);
                                startActivity(itent);
                                finishAffinity();

                            }

                        });


                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }  if (ProdutoFragments.orcamento == null && ProdutoFragments.pedido != null) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Ops :|");
                builder.setMessage("Você possui um pedido iniciado, se sair irá perder, deseja sair?");
                builder
                        .setCancelable(false)
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                ProdutoFragments.pedido = null;
                                ConsultaActivity.pedido = null;
                                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("pedidos").child(usuarioLogado.get("codigo"));
                                myRef.removeValue();
                                Intent itent = new Intent(GeralProdutoActivity.this, MainActivity.class);
                                startActivity(itent);
                                finishAffinity();

                            }

                        });


                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }else {
                Intent itent = new Intent(GeralProdutoActivity.this, MainActivity.class);
                startActivity(itent);
                finishAffinity();
                return true;
            }
            return true;
        }

        if (id == android.R.id.home) {

            if(ProdutoFragments.orcamento!=null && ProdutoFragments.pedido == null) {
                ConsultaActivity.orcamento = ProdutoFragments.orcamento;

            }

            if(ProdutoFragments.orcamento == null && ProdutoFragments.pedido != null) {
                ConsultaActivity.pedido = ProdutoFragments.pedido;

            }


            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void setupViewPager(ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        produtoFragments=new ProdutoFragments();
        estoqueFragments=new EstoqueFragments();
        caracteristicasFragments = new CaracteristicasFragments();
        produtoFragments.setArguments(bundle);
        estoqueFragments.setArguments(bundle);
        caracteristicasFragments.setArguments(bundle);
        adapter.addFragment(produtoFragments,"");
        adapter.addFragment(estoqueFragments,"");
        adapter.addFragment(caracteristicasFragments,"");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {

        if(ProdutoFragments.orcamento!=null && ProdutoFragments.pedido == null) {
            ConsultaActivity.orcamento = ProdutoFragments.orcamento;

        }

        if(ProdutoFragments.orcamento == null && ProdutoFragments.pedido != null) {
            ConsultaActivity.pedido = ProdutoFragments.pedido;

        }

        finish();
    }



}



