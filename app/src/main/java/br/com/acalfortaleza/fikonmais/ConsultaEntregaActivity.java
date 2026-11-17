package br.com.acalfortaleza.fikonmais;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;

import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import br.com.acalfortaleza.fikonmais.fragments.EntregasCgccpfFragments;
import br.com.acalfortaleza.fikonmais.fragments.EntregasFragments;
import br.com.acalfortaleza.fikonmais.fragments.EntregasPedidoFragments;

;


public class ConsultaEntregaActivity extends BaseAcalActivity implements SearchView.OnQueryTextListener {


    private Bundle bundle;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.consulta_entrega_status_bar_bg);
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
        setContentView(R.layout.activity_consulta_entrega);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.consulta_entrega_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Entregas");
        getSupportActionBar().setElevation(0);

        bundle = new Bundle();

        if(getIntent().getStringExtra("numpedido") == null || getIntent().getStringExtra("numpedido").equals("") ){
            EntregasFragments entrega = new EntregasFragments();
            FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
            transacao.replace(R.id.frag_container_entregas, entrega);
            transacao.commit();

        }else if(getIntent().getStringExtra("numpedido") != null && !getIntent().getStringExtra("numpedido").equals("") ){
            bundle.putString("pedido", getIntent().getStringExtra("numpedido"));
            EntregasPedidoFragments pedidoFg = new EntregasPedidoFragments();
            pedidoFg.setArguments(bundle);
            FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
            transacao.replace(R.id.frag_container_entregas, pedidoFg);
            transacao.commit();

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchnoorder, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Consultar...");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                EntregasFragments entrega = new EntregasFragments();
                FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
                transacao.replace(R.id.frag_container_entregas, entrega);
                transacao.commit();

                return true;
            }
        });



        return true;
    }



    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        //noinspection SimplifiableIfStatement
              if (id == android.R.id.home) {


                  finish();
                  return true;
              }


        return false;
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

       hideSoftKeyboard();
        try {

        Double numero = Double.parseDouble(query.trim());

        if (query.trim().length() < 11 && numero > 0) {
            bundle.putString("pedido", query);
            EntregasPedidoFragments pedidoFg = new EntregasPedidoFragments();
            pedidoFg.setArguments(bundle);
            FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
            transacao.replace(R.id.frag_container_entregas, pedidoFg);
            transacao.commit();
            return true;
        }else
        {

            bundle.putString("cgccpf", query);
            EntregasCgccpfFragments cgccpfFg = new EntregasCgccpfFragments();
            cgccpfFg.setArguments(bundle);
            FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
            transacao.replace(R.id.frag_container_entregas, cgccpfFg);
            transacao.commit();
            return true;

        }
        } catch (NumberFormatException ex) {

            Toast.makeText(ConsultaEntregaActivity.this,"Para consultar informe o nº do pedido ou Cgccpf do cliente!",Toast.LENGTH_SHORT).show();
        }


        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return true;
    }


    @Override
    public void onBackPressed() {
        Intent itent = new Intent(ConsultaEntregaActivity.this, MainActivity.class);
        startActivity(itent);
        finishAffinity();
    }
}
