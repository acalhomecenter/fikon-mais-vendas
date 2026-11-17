package br.com.acalfortaleza.fikonmais.modal;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import br.com.acalfortaleza.fikonmais.Orcamento_LancamentoActivity;
import br.com.acalfortaleza.fikonmais.R;

public class ModalItens extends BottomSheetDialogFragment {

    private ActionListener mActionListener;
    private Boolean aberturaPallet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.modal_orcamento_item, container, false);

        Button btn_desconto = v.findViewById(R.id.mod_item_editar);
        Button btn_editar_todos = v.findViewById(R.id.mod_item_editarTodos);
        Button btn_estoque = v.findViewById(R.id.mod_item_estoque);
        Button btn_pesquisa= v.findViewById(R.id.mod_item_pesquisa);
        Button btn_excluir = v.findViewById(R.id.mod_item_excluir);

        Switch switchAberturaPallet = v.findViewById(R.id.ModalOrcamentoItem_SwitchAberturaPallet);
        if(aberturaPallet != null) {
            if (aberturaPallet) {
                switchAberturaPallet.setChecked(true);
            } else {
                switchAberturaPallet.setChecked(false);
            }
        }
        else{
            switchAberturaPallet.setClickable(false);
            switchAberturaPallet.setHeight(0);
            switchAberturaPallet.setWidth(0);
            switchAberturaPallet.setVisibility(View.GONE);
        }


        btn_desconto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    mActionListener.onButtonClick(R.id.mod_item_editar);
                }
                dismiss();
            }
        });

        btn_editar_todos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    mActionListener.onButtonClick(R.id.mod_item_editarTodos);
                }
                dismiss();
            }
        });

        btn_estoque.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    mActionListener.onButtonClick(R.id.mod_item_estoque);
                }
                dismiss();
            }
        });


        btn_pesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    mActionListener.onButtonClick(R.id.mod_item_pesquisa);
                }
                dismiss();
            }
        });


        btn_excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionListener != null) {
                    mActionListener.onButtonClick(R.id.mod_item_excluir);
                }
                dismiss();
            }
        });

        switchAberturaPallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    Orcamento_LancamentoActivity.aberturaPallet = switchAberturaPallet.isChecked();
                    mActionListener.onButtonClick(R.id.ModalOrcamentoItem_SwitchAberturaPallet);
                }
                dismiss();
            }
        });


        return v;
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onButtonClick(int id);
    }

    public void setAberturaPallet(boolean aberturaPallet1){
        aberturaPallet = aberturaPallet1;
    }

}
