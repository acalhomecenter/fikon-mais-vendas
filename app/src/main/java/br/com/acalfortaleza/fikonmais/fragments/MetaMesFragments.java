package br.com.acalfortaleza.fikonmais.fragments;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.acalfortaleza.fikonmais.DevolucoesActivity;
import br.com.acalfortaleza.fikonmais.R;

public class MetaMesFragments extends Fragment {

    public MetaMesFragments() {


    }

    private View layout;
    private int pStatus;
    private Handler handler;
    private TextView tv;
    private TextView meta;
    private TextView faturado;
    private TextView percentual;
    private TextView margem;
    private TextView desconto;
    private TextView devolucao;
    private TextView ticket;
    private TextView vlDevolucao;
    private TextView vlDesconto;
    private TextView atdpf;
    private TextView atdpj;
    private TextView atdtotal;
    private TextView vlfrete;
    private TextView freteOrig;
    private TextView percFrete;
    private TextView pedidosRetira;
    private TextView pedidoAgendados;
    private TextView percAgendados;
    private Double metaConsultor;
    private Double faturamentoConsultor;
    private Double ticketMes;
    private String percentualMeta;
    private Double valorDevolucao;
    private Double valorDesconto;
    private String percentualDev;
    private String percentualDesc;
    private String percentualMargem;
    private double valorFrete;
    private double valorFreteorig;
    private int qtdePedidosRetira;
    private int qtdePedidosAgendados;
    private String percentualFrete;
    private String percentualAgendamento;
    private Drawable drawable;
    private int qtdpf;
    private int qtdpj;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            metaConsultor = getArguments().getDouble("metaMes");
            faturamentoConsultor = getArguments().getDouble("faturadoMes");
            valorDevolucao = getArguments().getDouble("devolucao");
            valorDesconto = getArguments().getDouble("desconto");
            percentualMeta = getArguments().getString("percentMes");
            percentualDev = getArguments().getString("percentDevolucao");
            percentualDesc = getArguments().getString("percentDesconto");
            percentualMargem = getArguments().getString("margemMes");
            ticketMes = getArguments().getDouble("ticketMes");
            qtdpf = getArguments().getInt("atdpf");
            qtdpj = getArguments().getInt("atdpj");
            qtdePedidosRetira = getArguments().getInt("pedidosRetira");
            qtdePedidosAgendados = getArguments().getInt("pedidosAgendados");
            valorFrete = getArguments().getDouble("vlfrete");
            valorFreteorig = getArguments().getDouble("freteorig");
            percentualFrete = getArguments().getString("percentualFrete");
            percentualAgendamento = getArguments().getString("percentualAgendamento");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.frame_meta_mes,
                container, false);

        handler = new Handler();

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        tv = layout.findViewById(R.id.tv);
        meta = layout.findViewById(R.id.frm_meta);
        meta.setText(real.format(metaConsultor));
        faturado = layout.findViewById(R.id.frm_faturamento);
        faturado.setText(real.format(faturamentoConsultor));
        margem = layout.findViewById(R.id.frm_margem);
        margem.setText(percentualMargem+ "%");

        if(Double.parseDouble(percentualMargem)<33.5){
          margem.setTextColor(Color.parseColor("#B91A1F"));
        }

        if(Double.parseDouble(percentualMargem)>=33.5 && Double.parseDouble(percentualMargem)<34.5  ){
            margem.setTextColor(Color.parseColor("#DC962D"));
        }

        if(Double.parseDouble(percentualMargem)>=34.5){
            margem.setTextColor(Color.parseColor("#4F8651"));
        }


        vlDevolucao = layout.findViewById(R.id.frm_valorDev);
        vlDevolucao.setText(real.format(valorDevolucao));
        vlDevolucao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DevolucoesActivity.class);
                startActivity(intent);
            }
        });
        vlDesconto = layout.findViewById(R.id.frm_valorDesc);
        vlDesconto.setText(real.format(valorDesconto));
        devolucao = layout.findViewById(R.id.frm_perc_dev);
        devolucao.setText(percentualDev + "%");
        if(Double.parseDouble(percentualDev)<3){
            devolucao.setTextColor(Color.parseColor("#4F8651"));
        }else{

            devolucao.setTextColor(Color.parseColor("#B91A1F"));
        }

        desconto = layout.findViewById(R.id.frm_perc_desc);
        desconto.setText(percentualDesc + "%");
        if(Double.parseDouble(percentualDesc)<3){
            desconto.setTextColor(Color.parseColor("#4F8651"));
        }else{

            desconto.setTextColor(Color.parseColor("#B91A1F"));
        }
        ticket = layout.findViewById(R.id.frm_ticket);
        ticket.setText(real.format(ticketMes));
        atdpf = layout.findViewById(R.id.frm_atdpf);
        atdpf.setText(String.valueOf(qtdpf));
        atdpj = layout.findViewById(R.id.frm_atdpj);
        atdpj.setText(String.valueOf(qtdpj));
        atdtotal = layout.findViewById(R.id.frm_totalatd);
        atdtotal.setText(String.valueOf(qtdpf+qtdpj));

        vlfrete = layout.findViewById(R.id.frm_vlfrete);
        freteOrig = layout.findViewById(R.id.frm_freteorig);
        percFrete = layout.findViewById(R.id.frm_percfrete);
        pedidosRetira = layout.findViewById(R.id.frm_pedidosRetira);
        pedidoAgendados = layout.findViewById(R.id.frm_pedidosAgendados);
        percAgendados = layout.findViewById(R.id.frm_percAgendado);


        vlfrete.setText(real.format(valorFrete));
        freteOrig.setText(real.format(valorFreteorig));
        pedidosRetira.setText(String.valueOf(qtdePedidosRetira));
        pedidoAgendados.setText(String.valueOf(qtdePedidosAgendados));
        percFrete.setText(percentualFrete + "%");
        percAgendados.setText(percentualAgendamento + "%");


        pStatus = 0;
        Resources res = getResources();
        if (Double.parseDouble(percentualMeta) > 100) {
            drawable = res.getDrawable(R.drawable.circularmetabatida);
        } else {
            drawable = res.getDrawable(R.drawable.circular);
        }

        final ProgressBar mProgress = layout.findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (pStatus <= (Double.parseDouble(percentualMeta) - 1)) {
                    pStatus += 1;

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mProgress.setProgress(pStatus);
                            tv.setText(pStatus + "%");

                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        // Just to display the progress slowly
                        Thread.sleep(8); //thread will take approx 1.5 seconds to finish
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return layout;
    }
}
