package br.com.acalfortaleza.fikonmais;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import br.com.acalfortaleza.fikonmais.fragments.ClientesFragments;

public class ClientesActivity extends BaseAcalActivity implements SearchView.OnQueryTextListener {

    private Bundle bundle;
    private Button btn_alterar;
    private String parametroConsultado = "";
    public static String alterado = "";
    public boolean orcamento = false;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.clientes_status_bar_bg);
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
        setContentView(R.layout.activity_clientes);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.clientes_toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Nossos Clientes");
        getSupportActionBar().setElevation(0);

        bundle = new Bundle();



        bundle.putBoolean("orcamento", getIntent().getBooleanExtra("orcamento", false));
        bundle.putBoolean("pedido", getIntent().getBooleanExtra("pedido", false));
        ClientesFragments frm = new ClientesFragments();
        frm.setArguments(bundle);
        FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
        transacao.replace(R.id.frag_container_clientes, frm);
        transacao.commit();


    }

    @Override
    protected void onResume() {
        super.onResume();


        if(orcamento){
            finish();
        }else {

            if (getIntent().getStringExtra("cgccpf") != null) {
                CarregarCliente(getIntent().getStringExtra("cgccpf"), "nome");
            }

            if (!parametroConsultado.equals("") && !alterado.equals("")) {

                try {

                    Double numero = Double.parseDouble(parametroConsultado.trim());

                    if ( numero > 0) {

                        CarregarCliente(parametroConsultado, "nome");


                    }


                } catch (NumberFormatException ex) {

                    if (parametroConsultado.trim().length() >= 5) {

                        CarregarCliente("0", parametroConsultado);

                    }
                }
            }
        }
    }


    private void CarregarCliente(String cgccpf, String nome) {
        bundle.putString("cgccpf", cgccpf);
        bundle.putString("nome", nome);
        bundle.putBoolean("orcamento", getIntent().getBooleanExtra("orcamento", false));
        bundle.putBoolean("pedido", getIntent().getBooleanExtra("pedido", false));
        ClientesFragments clienteFg = new ClientesFragments();
        clienteFg.setArguments(bundle);
        FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
        transacao.replace(R.id.frag_container_clientes, clienteFg);
        transacao.commit();
        alterado = "";

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchnoorder, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Código/Cpf/Cnpj, Nome ou Fantasia");
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(false);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                /*ClientesFragments frm = new ClientesFragments();
                FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
                transacao.replace(R.id.frag_container_clientes, frm);
                transacao.commit();*/

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


        if (id == android.R.id.home) {

            if (getIntent().getBooleanExtra("orcamento", false)) {
                finish();
            } else {

                finish();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
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

            if ( numero > 0) {
                parametroConsultado = query.trim();
                bundle.putString("cgccpf", query);
                bundle.putString("nome", "nome");
                bundle.putBoolean("orcamento", getIntent().getBooleanExtra("orcamento", false));
                ClientesFragments clienteFg = new ClientesFragments();
                clienteFg.setArguments(bundle);
                FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
                transacao.replace(R.id.frag_container_clientes, clienteFg);
                transacao.commit();
                numero=0.00d;
                return true;
            }else{
                Toast.makeText(ClientesActivity.this, "Para consultar por cpf informe o cpf válido!", Toast.LENGTH_SHORT).show();
            }


        } catch (NumberFormatException ex) {

            if (query.trim().length() >= 5) {
                parametroConsultado = query.trim();
                bundle.putString("nome", query);
                bundle.putString("cgccpf", "0");
                bundle.putBoolean("orcamento", getIntent().getBooleanExtra("orcamento", false));
                ClientesFragments clienteFg = new ClientesFragments();
                clienteFg.setArguments(bundle);
                FragmentTransaction transacao = getSupportFragmentManager().beginTransaction();
                transacao.replace(R.id.frag_container_clientes, clienteFg);
                transacao.commit();
                return true;
            } else {
                Toast.makeText(ClientesActivity.this, "Para consultar por nome informe mais de 4 letras!", Toast.LENGTH_SHORT).show();
            }


        }


        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return true;
    }


    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("orcamento", false)) {
            finish();
        } else {

            finish();
        }

    }


}
