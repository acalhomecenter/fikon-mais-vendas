package br.com.acalfortaleza.fikonmais.modal;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import br.com.acalfortaleza.fikonmais.R;

public class ModalBottomSheetOrc extends BottomSheetDialogFragment {

    private ModalBottomSheetOrc.ActionListener mActionListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.modal_orcamentos, container, false);


        Button btn_ligar = v.findViewById(R.id.mod_orc_ligar);
        Button btn_wpp = v.findViewById(R.id.mod_orc_wpp);
        Button btn_editar = v.findViewById(R.id.mod_orc_editar);
        Button btn_info = v.findViewById(R.id.mod_orc_info);
        Button btn_gerar = v.findViewById(R.id.mod_orc_gerarPedido);
        Button btn_gerarCarrinho = v.findViewById(R.id.mod_orc_gerarCarrinho);
        Button btn_enviarCodigoEncomenda = v.findViewById(R.id.mod_orc_codigo_encomenda);


        btn_ligar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_orc_ligar);
                }
                dismiss();
            }
        });

        btn_wpp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_orc_wpp);
                }
                dismiss();
            }
        });

        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_orc_editar);
                }
                dismiss();
            }
        });

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_orc_info);
                    dismiss();
                }

            }
        });

        btn_gerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_orc_gerarPedido);
                }
                dismiss();
            }
        });


        btn_gerarCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_orc_gerarCarrinho);
                }
                dismiss();
            }
        });

        btn_enviarCodigoEncomenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_orc_codigo_encomenda);
                }
                dismiss();
            }
        });

        return v;

    }

    public void setActionListener(ModalBottomSheetOrc.ActionListener actionListener)
    {
        mActionListener = actionListener;
    }

    public interface ActionListener{
        void onButtonClick(int id);
    }
}
