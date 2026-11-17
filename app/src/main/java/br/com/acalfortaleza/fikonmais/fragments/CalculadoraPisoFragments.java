package br.com.acalfortaleza.fikonmais.fragments;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.acalfortaleza.fikonmais.R;
import mehdi.sakout.fancybuttons.FancyButton;

public class CalculadoraPisoFragments extends Fragment{

    private TextInputEditText edt_comprimento;
    private TextInputEditText edt_largura;
    private TextInputEditText edt_area ;
    private TextInputLayout layout_comprimento;
    private TextInputLayout layout_largura;
    private TextInputLayout layout_area ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_piso,
                container, false);

        FancyButton btn_calcular = layout.findViewById(R.id.btn_calcular_piso);
        edt_comprimento = layout.findViewById(R.id.edtComprimento_Piso);
        edt_largura = layout.findViewById(R.id.edtLargura_piso);
        edt_area = layout.findViewById(R.id.edtMetro_Piso);
        layout_comprimento = layout.findViewById(R.id.layoutComprimentoPiso);
        layout_largura = layout.findViewById(R.id.layoutLarguraPiso);
        layout_area = layout.findViewById(R.id.layoutMetroPiso);


        btn_calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(!validarTodosVazio() || !validarComprimentoVazio() || !validarLarguraVazio()){


             }else{

             }
            }
        });


        return layout;

    }

    public boolean validarTodosVazio() {



        if (edt_comprimento.getText().toString().equals("") && edt_largura.getText().toString().equals("") && edt_area.getText().toString().equals("")) {

            layout_area.setError("Informe a área total, ou comprimento e largura.");
            return false;

            }else{

                 layout_area.setError(null);
                 return true;
             }


         }

    public boolean validarComprimentoVazio() {



        if (edt_comprimento.getText().toString().equals("") && edt_largura.getText().toString().length()>0 && edt_area.getText().toString().equals("")) {

            layout_comprimento.setError("Informe o comprimento.");
            return false;

        }else{

            layout_comprimento.setError(null);
            return true;
        }


    }


    public boolean validarLarguraVazio() {



        if (edt_comprimento.getText().toString().length()>0 && edt_largura.getText().toString().equals("") && edt_area.getText().toString().equals("")) {

            layout_largura.setError("Informe a largura.");
            return false;

        }else{

            layout_largura.setError(null);
            return true;
        }


    }


}
