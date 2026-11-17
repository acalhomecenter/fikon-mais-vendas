package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.Pedidos_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.models.Contato;
import br.com.acalfortaleza.fikonmais.models.Enderecos;
import br.com.acalfortaleza.fikonmais.models.Orcamento;
import br.com.acalfortaleza.fikonmais.models.Pedidos;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PopupAlterarContato extends Activity {

    private TextInputLayout layout_contato;
    private TextInputEditText edtContato;
    private Button btnOk;
    private SpinnerDialog spinnerDialog;
    private List<Contato> mList;
    private ArrayList<String> listaContatos;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_escolhe_contato);

        mList = (List<Contato>) getIntent().getSerializableExtra("contatos");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .3));


        layout_contato = findViewById(R.id.pop_cont_layoucontato);
        edtContato = findViewById(R.id.pop_cont_edtContato);
        btnOk = findViewById(R.id.pop_cont_btn_Alterar);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        listaContatos = new ArrayList<>();
        if(mList != null && mList.size()>0) {
            for (Contato e : mList) {
                listaContatos.add(e.toString());
            }
        }


        if(Orcamento_LancamentoActivity.orcamento.getCodContato() != null && !Orcamento_LancamentoActivity.orcamento.getCodContato().equals("")){

            edtContato.setText(Orcamento_LancamentoActivity.orcamento.getCliente().getContatos().get(Integer.parseInt(Orcamento_LancamentoActivity.orcamento.getCodContato())).toString());
        }



        spinnerDialog = new SpinnerDialog(PopupAlterarContato.this, listaContatos, "Selecione o contato ");
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {

                edtContato.setText(item);
                Orcamento_LancamentoActivity.orcamento.setCodContato(String.valueOf(position));


            }
        });


        edtContato.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtContato.getRight() - edtContato.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        spinnerDialog.showSpinerDialog();

                    }
                }
                return false;
            }
        });

    }
}
