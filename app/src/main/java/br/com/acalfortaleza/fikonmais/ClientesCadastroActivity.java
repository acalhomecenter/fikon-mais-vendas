package br.com.acalfortaleza.fikonmais;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;

import com.google.android.material.tabs.TabLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import br.com.acalfortaleza.fikonmais.adapters.ViewPagerAdapter;
import br.com.acalfortaleza.fikonmais.fragments.ClientesComplementoFragments;
import br.com.acalfortaleza.fikonmais.fragments.ClientesEnderecosFragments;
import br.com.acalfortaleza.fikonmais.fragments.ClientesGeralFragments;
import br.com.acalfortaleza.fikonmais.models.Cliente;

public class

ClientesCadastroActivity extends BaseAcalActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    ViewPagerAdapter adapter;
    private Bundle bundle;
    private Cliente cliente;
    private String cgccpf;
    private Boolean isOrcamento;

    private ClientesGeralFragments fragmentsclientes;
    private ClientesComplementoFragments fragmentsComplemento;
    private ClientesEnderecosFragments fragmentsEnderecos;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.clientes_cadastro_status_bar_bg);
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
        setContentView(R.layout.activity_clientes_cadastro);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.clientes_cadastro_toolbar);
        setSupportActionBar(toolbar);


        bundle = new Bundle();

        if(getIntent().getSerializableExtra("cliente")!=null){

            cliente = (Cliente) getIntent().getSerializableExtra("cliente");
            bundle.putSerializable("cliente",  cliente);
        }

        if(getIntent().getStringExtra("cgccpf")!=null){
            cgccpf = getIntent().getStringExtra("cgccpf");
            bundle.putString("cgccpf", cgccpf);
        }


            isOrcamento = getIntent().getBooleanExtra("orcamento",false);
            bundle.putBoolean("isOrcamento", isOrcamento);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Cliente");
        getSupportActionBar().setElevation(0);


        viewPager = findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(3);


        tabLayout = findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(),false);
                switch (tab.getPosition()){
                    case 0 :
                        getSupportActionBar().setTitle("Dados do Cliente");
                        break;
                    case 1 :
                        getSupportActionBar().setTitle("Informações Complementares");
                        break;
                    case 2 :
                        getSupportActionBar().setTitle("Endereços");
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Você realmente deseja sair do cadastro de Cliente?");
            builder
                    .setCancelable(false)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            ClientesEnderecosFragments.limparEnderecoNoFormulario();
                            finish();

                        }

                    });

            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });

            builder.create().show();

            return  true;

           }





        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    private void setupViewPager(ViewPager viewPager)
    {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentsclientes=new ClientesGeralFragments();
        fragmentsComplemento=new ClientesComplementoFragments();
        fragmentsEnderecos=new ClientesEnderecosFragments();
        fragmentsclientes.setArguments(bundle);
        fragmentsComplemento.setArguments(bundle);
        fragmentsEnderecos.setArguments(bundle);
        adapter.addFragment(fragmentsclientes,"");
        adapter.addFragment(fragmentsComplemento,"");
        adapter.addFragment(fragmentsEnderecos,"");
        viewPager.setAdapter(adapter);
    }

}
