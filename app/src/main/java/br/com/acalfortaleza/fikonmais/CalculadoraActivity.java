package br.com.acalfortaleza.fikonmais;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.os.Bundle;
import android.text.Html;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import br.com.acalfortaleza.fikonmais.fragments.CalculadoraPisoFragments;
import mehdi.sakout.fancybuttons.FancyButton;

public class CalculadoraActivity extends BaseAcalActivity {

    private FancyButton btnCalcular;
    private AlertDialog alertaDialog;

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.calculadora_status_bar_bg);
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
        setContentView(R.layout.activity_calculadora);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.calculadora_toolbar);
        setSupportActionBar(toolbar);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frag_calculadora, new CalculadoraPisoFragments());
        ft.commit();

        getSupportActionBar().setTitle("Calculadora de piso");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0);

        Switch sw_piso = findViewById(R.id.sw_piso);
        final Switch sw_revestimento = findViewById(R.id.sw_revestimento);

        sw_piso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frag_calculadora, new CalculadoraPisoFragments());
                ft.commit();
                sw_revestimento.setChecked(false);
            }
        });






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.calculadora, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent itent = new Intent(CalculadoraActivity.this, MainActivity.class);
                //itent.putExtra("consultorC",  getIntent().getParcelableExtra("consultor"));
                //itent.putExtra("usuarioC",  getIntent().getParcelableExtra("usuario"));
                startActivity(itent);
                finishAffinity();
                break;

            case R.id.sobreCalculadora:

                AlertDialog.Builder alerta =new AlertDialog.Builder(CalculadoraActivity.this);
                alerta.setIcon(R.drawable.ic_training);
                alerta.setTitle("MODO DE CALCULAR ");
                alerta.setMessage(Html.fromHtml("<b>"+"Pisos:"+"</b> <br />" +
                        "Cálculo da Área do Piso = comprimento x largura = área (m²).<br />" +
                        "Exemplo: em uma sala com 3,00m de comprimento por 4,00m de largura, temos: 3,00x4,00 = 12,00m²<br />" +
                        "<br />" +
                        "<b>"+"Revestimentos:"+"</b> <br />" +
                        "Cálculo da Área do Revestimento = soma do comprimento de todas as paredes x altura = área (m²).<br />" +
                        "Exemplo: em uma cozinha com 3,00m de comprimento por 4,00m de largura e 2,60m de altura,<br />" +
                        " temos: (3,00+4,00+3,00+4,00) = 14,00mx2,60m = 36,40m²."));
                alerta.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

                alertaDialog = alerta.create();
                alertaDialog.show();

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent itent = new Intent(CalculadoraActivity.this, MainActivity.class);
        //itent.putExtra("consultorC",  getIntent().getParcelableExtra("consultor"));
        //itent.putExtra("usuarioC",  getIntent().getParcelableExtra("usuario"));
        startActivity(itent);
        finishAffinity();
    }

}
