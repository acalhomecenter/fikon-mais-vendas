package br.com.acalfortaleza.fikonmais;

import android.app.ProgressDialog;
import android.os.Handler;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.os.Bundle;
import android.graphics.Color;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.adapters.VazioFragment;
import br.com.acalfortaleza.fikonmais.adapters.ViewPagerAdapter;
import br.com.acalfortaleza.fikonmais.fragments.PontosFrafments;
import br.com.acalfortaleza.fikonmais.fragments.RankFragments;
import br.com.acalfortaleza.fikonmais.fragments.RegulamentoFragments;
import br.com.acalfortaleza.fikonmais.helper.CampanhaHelper;
import br.com.acalfortaleza.fikonmais.helper.LoginHelper;
import br.com.acalfortaleza.fikonmais.models.Campanha;
import br.com.acalfortaleza.fikonmais.models.Rank;
import br.com.acalfortaleza.fikonmais.models.Regulamento;

public class CampanhaActivity extends BaseAcalActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    ViewPagerAdapter adapter;
    private ProgressDialog dialog;
    private List<Rank> listRank;
    private List<Campanha> listPontosConsultor;
    private List<Regulamento> listRegulamento;
    private CampanhaHelper helper;
    private Bundle bundle;
    private Handler handler;
    private static HashMap<String, String> usuarioLogado;
    private LoginHelper loginHelper;

    PontosFrafments pontosFragments;
    RankFragments rankFragments;
    RegulamentoFragments regulamentoFragments;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.campanha_status_bar_bg);
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
        setContentView(R.layout.activity_campanha);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.campanha_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Meus pontos");
        getSupportActionBar().setElevation(0);

        helper = new CampanhaHelper();
        loginHelper = new LoginHelper(this);
        handler = new Handler();
        bundle = new Bundle();

        usuarioLogado = loginHelper.GetUsuarioSalvo();

        viewPager = findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(3);

        tabLayout = findViewById(R.id.tabs);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(),false);
                switch (tab.getPosition()){
                    case 0 :
                        getSupportActionBar().setTitle("Minhas Milhas");
                        break;
                    case 1 :
                        getSupportActionBar().setTitle("Rank");
                        break;
                    case 2 :
                        getSupportActionBar().setTitle("Regulamento");
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

        ConsultarCampanha();

    }



    private void setupViewPager(ViewPager viewPager)
    {

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pontosFragments=new PontosFrafments();
        rankFragments=new RankFragments();
        regulamentoFragments=new RegulamentoFragments();
        pontosFragments.setArguments(bundle);
        rankFragments.setArguments(bundle);
        regulamentoFragments.setArguments(bundle);

        if(listPontosConsultor != null && listPontosConsultor.size()>0){
            adapter.addFragment(pontosFragments,"");
        }else{
            VazioFragment vazio = new VazioFragment();
            adapter.addFragment(vazio,"");
        }

        adapter.addFragment(rankFragments,"");
        adapter.addFragment(regulamentoFragments,"");
        viewPager.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


    public void ConsultarCampanha (){

        dialog = new ProgressDialog(CampanhaActivity.this);
        dialog.setMessage("Consultando...");
        dialog.setCancelable(false);
        dialog.show();
        new Thread() {

            public void run() {

                try {

                    int codvendr = 0;
                    int codfil = Integer.parseInt(usuarioLogado.get("codfil"));

                    if(usuarioLogado.get("isgerente")!=null && usuarioLogado.get("isgerente").toUpperCase().contains("S")){
                        codvendr = 999999;
                    }
                    else{
                        codvendr = Integer.parseInt(usuarioLogado.get("codigo"));
                    }

                    listPontosConsultor = helper.ConsultaPontosDetalhado(codvendr, codfil);
                    listRank = helper.ConsultaRank(codvendr, codfil);
                    listRegulamento = helper.ConsultaRegulamento();
                    String dataAtualizacao = helper.ConsultaAtualizacao(codvendr);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if(dataAtualizacao!=null){
                                getSupportActionBar().setSubtitle("Última Atualização: " + dataAtualizacao);
                            }

                            if(listRank!= null && listRank.size()>0){

                                bundle.putSerializable("rank", (Serializable) listRank);
                                bundle.putSerializable("regulamento", (Serializable) listRegulamento);

                                if (listPontosConsultor != null && listPontosConsultor.size() > 0) {
                                    bundle.putSerializable("pontosConsultor", (Serializable) listPontosConsultor);
                                }

                                setupViewPager(viewPager);

                            }

                        }

                    });


                } catch (UnknownHostException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(CampanhaActivity.this, "Verifique a sua conexão com a internet e tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(CampanhaActivity.this, "Ocorreu uma falha na consulta, por gentileza tente novamente!", Toast.LENGTH_SHORT).show();

                        }
                    });
                } finally {

                    if (dialog.isShowing()) {

                        dialog.dismiss();

                    }

                }

            }


        }.start();


    }


}




