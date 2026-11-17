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

public class ModalBottomSheetPed extends BottomSheetDialogFragment {

    private ModalBottomSheetPed.ActionListener mActionListener;
    private Button btn_gerarLinks;
    private boolean mostrarGerarLinks = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.modal_pedidos, container, false);


        Button btn_entrega = v.findViewById(R.id.mod_ped_entrega);
        Button btn_alterarentrega= v.findViewById(R.id.mod_ped_alterarentrega);
        Button btn_editar = v.findViewById(R.id.mod_ped_editar);
        Button btn_obs = v.findViewById(R.id.mod_ped_obs);
        Button btn_info = v.findViewById(R.id.mod_ped_info);
        Button btn_financeiro = v.findViewById(R.id.mod_ped_financeiro);
        Button btn_agendar = v.findViewById(R.id.mod_ped_agenda);
        Button btn_linksPagamento = v.findViewById(R.id.mod_ped_linksPagamento);
        btn_gerarLinks = v.findViewById(R.id.mod_ped_gerarLinks);
        
        // Aplicar configuração de visibilidade do botão GERAR LINKS
        btn_gerarLinks.setVisibility(mostrarGerarLinks ? View.VISIBLE : View.GONE);


        btn_entrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_entrega);
                }
                dismiss();
            }
        });

        btn_alterarentrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_alterarentrega);
                }
                dismiss();
            }
        });

        btn_financeiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_financeiro);
                }
                dismiss();
            }
        });



        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_editar);
                }
                dismiss();
            }
        });


        btn_obs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_obs);
                    dismiss();
                }

            }
        });


        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_info);
                    dismiss();
                }

            }
        });


        btn_agendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_agenda);
                    dismiss();
                }

            }
        });

        btn_linksPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_linksPagamento);
                    dismiss();
                }
            }
        });

        btn_gerarLinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mActionListener != null){
                    mActionListener.onButtonClick(R.id.mod_ped_gerarLinks);
                    dismiss();
                }
            }
        });


        return v;
    }

    public void setActionListener(ModalBottomSheetPed.ActionListener actionListener)
    {
        mActionListener = actionListener;
    }

    public void configurarBotaoGerarLinks(boolean mostrar) {
        this.mostrarGerarLinks = mostrar;
        if (btn_gerarLinks != null) {
            btn_gerarLinks.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        }
    }

    public interface ActionListener{
        void onButtonClick(int id);
    }
}
