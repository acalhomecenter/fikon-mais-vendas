package br.com.acalfortaleza.fikonmais;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class BaseAcalActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        aplicarInsetVisualLegado();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        aplicarInsetVisualLegado();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        aplicarInsetVisualLegado();
    }

    protected boolean deveAplicarInsetVisualLegado() {
        return true;
    }

    protected int getPaddingTopConteudo(int statusBarHeight, int actionBarHeight) {
        return statusBarHeight + actionBarHeight;
    }

    private void aplicarInsetVisualLegado() {
        if (!deveAplicarInsetVisualLegado()) {
            return;
        }

        final View content = findViewById(android.R.id.content);
        if (!(content instanceof ViewGroup)) {
            return;
        }

        final ViewGroup contentGroup = (ViewGroup) content;
        if (contentGroup.getChildCount() == 0) {
            return;
        }

        final View root = contentGroup.getChildAt(0);
        root.post(() -> {
            int statusBarHeight = getStatusBarHeight();
            int actionBarHeight = getActionBarHeight();

            if (statusBarHeight > 0) {
                garantirFaixaAzulNoTopo(contentGroup, statusBarHeight);
            }

            int paddingTopEsperado = getPaddingTopConteudo(statusBarHeight, actionBarHeight);
            if (paddingTopEsperado <= 0) {
                return;
            }

            root.setPadding(
                    root.getPaddingLeft(),
                    paddingTopEsperado,
                    root.getPaddingRight(),
                    root.getPaddingBottom()
            );
        });
    }

    private void garantirFaixaAzulNoTopo(ViewGroup contentGroup, int statusBarHeight) {
        View faixaTopo = contentGroup.findViewWithTag("legacy_status_bar_bg");
        if (faixaTopo != null) {
            FrameLayout.LayoutParams paramsExistentes = (FrameLayout.LayoutParams) faixaTopo.getLayoutParams();
            paramsExistentes.height = statusBarHeight;
            faixaTopo.setLayoutParams(paramsExistentes);
            return;
        }

        View novaFaixa = new View(this);
        novaFaixa.setTag("legacy_status_bar_bg");
        novaFaixa.setBackgroundColor(getResources().getColor(R.color.primary_dark));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight
        );
        contentGroup.addView(novaFaixa, params);
    }

    protected int getActionBarHeight() {
        TypedValue typedValue = new TypedValue();
        if (!getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            return 0;
        }

        return TypedValue.complexToDimensionPixelSize(
                typedValue.data,
                getResources().getDisplayMetrics()
        );
    }

    protected int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId <= 0) {
            return 0;
        }
        return getResources().getDimensionPixelSize(resourceId);
    }
}
