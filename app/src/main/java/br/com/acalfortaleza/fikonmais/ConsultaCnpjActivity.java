package br.com.acalfortaleza.fikonmais;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

public class ConsultaCnpjActivity extends BaseAcalActivity {

    @Override
    protected boolean deveAplicarInsetVisualLegado() {
        return false;
    }

    private void ajustarFaixaStatusBar() {
        View statusBarBg = findViewById(R.id.consulta_cnpj_status_bar_bg);
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
        setContentView(R.layout.activity_consulta_cnpj);
        ajustarFaixaStatusBar();
        Toolbar toolbar = findViewById(R.id.consulta_cnpj_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Consulta CNPJ");



    }
}
