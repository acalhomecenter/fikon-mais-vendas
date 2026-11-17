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

public class ModalBottomSheetPedLong extends BottomSheetDialogFragment {

    private ModalBottomSheetPed.ActionListener mActionListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.modal_pedidos_long, container, false);


        Button btn_ligar = v.findViewById(R.id.mod_ped_ligar);
        Button btn_wpp= v.findViewById(R.id.mod_ped_wpp);
        Button btn_liberar= v.findViewById(R.id.mod_ped_liberar);



        btn_ligar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_ligar);
                }
                dismiss();
            }
        });

        btn_wpp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_wpp);
                }
                dismiss();
            }
        });


        btn_liberar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_liberar);
                }
                dismiss();
            }
        });







        return v;
    }

    public void setActionListener(ModalBottomSheetPed.ActionListener actionListener)
    {
        mActionListener = actionListener;
    }

    public interface ActionListener{
        void onButtonClick(int id);
    }
}
