package br.com.acalfortaleza.fikonmais.popup;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import br.com.acalfortaleza.fikonmais.R;
import br.com.acalfortaleza.fikonmais.adapters.FinanceiroAdapter;
import br.com.acalfortaleza.fikonmais.models.Financeiro;

public class PopupFinanceiro extends Activity {

    private List<Financeiro> mList;
    private HashMap<String,Financeiro> mListItens;
    private TextView txt_condicao;
    private ExpandableListView mListView;
    private Button btnFechar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_financeiro);

        mList = (List<Financeiro>) getIntent().getSerializableExtra("financeiro");

        mListItens = new HashMap<String,Financeiro>();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .5));


        txt_condicao = findViewById(R.id.pop_fin_condicao);
        mListView = findViewById(R.id.pop_fin_list);
        btnFechar = findViewById(R.id.pop_fin_btnFechar);


        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if(mList !=null && mList.size()>0){

            for(int i = 0; i < mList.size(); i++){

                mListItens.put(mList.get(i).toString(),mList.get(i));

            }

            txt_condicao.setText(mList.get(0).getCondpgto()+" - "+mList.get(0).getCondicao());
            FinanceiroAdapter adapter = new FinanceiroAdapter(this,mList,mListItens);
            mListView.setAdapter(adapter);

        }



    }

}
