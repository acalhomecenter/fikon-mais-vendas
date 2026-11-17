package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.VendaPerfeita;

public class PopupVendaPerfeita extends Activity {

    private CheckBox checkBoxCoversao;
    private CheckBox checkBoxSKU;
    private CheckBox checkBoxTicket;
    private CheckBox checkBoxDesconto;
    private CheckBox checkBoxEntrega;
    private ImageButton botaoFechar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_venda_perfeita);

        checkBoxCoversao = findViewById(R.id.PopupVendaPerfeita_CheckboxConversao);
        checkBoxSKU = findViewById(R.id.PopupVendaPerfeita_CheckboxSKU);
        checkBoxTicket = findViewById(R.id.PopupVendaPerfeita_CheckboxTicketMedio);
        checkBoxDesconto = findViewById(R.id.PopupVendaPerfeita_CheckboxDesconto);
        checkBoxEntrega = findViewById(R.id.PopupVendaPerfeita_CheckboxEntrega);
        botaoFechar = findViewById(R.id.PopupVendaPerfeita_BotaoFechar);

        checkBoxCoversao.setClickable(false);
        checkBoxSKU.setClickable(false);
        checkBoxTicket.setClickable(false);
        checkBoxDesconto.setClickable(false);
        checkBoxEntrega.setClickable(false);

        VendaPerfeita metas = (VendaPerfeita) getIntent().getSerializableExtra("metas");
        VendaPerfeita indicadores = (VendaPerfeita) getIntent().getSerializableExtra("indicadores");

        checkBoxCoversao.setChecked(false);
        checkBoxSKU.setChecked(false);
        checkBoxTicket.setChecked(false);
        checkBoxDesconto.setChecked(false);
        checkBoxEntrega.setChecked(false);

        if(metas!=null && indicadores!=null) {

            checkBoxSKU.setText(checkBoxSKU.getText() + "\n" + "Meta: " + metas.SKU);
            checkBoxTicket.setText(checkBoxTicket.getText() + "\n" + "Meta: " + metas.TicketMedio);
            checkBoxDesconto.setText(checkBoxDesconto.getText() + "\n" + "Meta: " + metas.PercentualDesconto + "%");
            checkBoxEntrega.setText(checkBoxEntrega.getText() + "\n" + "Meta: " + metas.PercentualFrete + "%");

            if (indicadores.Conversao == metas.Conversao) {
                checkBoxCoversao.setChecked(true);
            }
            if (indicadores.SKU >= metas.SKU) {
                checkBoxSKU.setChecked(true);
            }
            if (indicadores.TicketMedio >= metas.TicketMedio) {
                checkBoxTicket.setChecked(true);
            }
            if (indicadores.PercentualDesconto <= metas.PercentualDesconto) {
                checkBoxDesconto.setChecked(true);
            }
            if (indicadores.PercentualFrete >= metas.PercentualFrete) {
                checkBoxEntrega.setChecked(true);
            }

        }

        botaoFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent().getExtras().clear();
                finish();
            }
        });

    }

}
