package br.com.acalfortaleza.fikonmais.fragments;

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

import br.com.acalfortaleza.fikonmais.R;

public class ProjecaoFrafments extends Fragment {

    private View layout;
    private int pStatus;
    private Handler handler;
    private TextView tv;
    private TextView meta;
    private TextView faturado;
    private TextView fatAnterior;
    private TextView margem;
    private Double metaConsultor;
    private Double vendaAnterior;
    private Double faturamentoConsultor;
    private String percentualMeta;
    private String percentualMargem;
    private Drawable drawable;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!= null){

            metaConsultor = getArguments().getDouble("meta");
            vendaAnterior = getArguments().getDouble("vendaAnterior");
            faturamentoConsultor = getArguments().getDouble("vendaProjetada");
            percentualMeta = getArguments().getString("projecao");
            percentualMargem = getArguments().getString("margemAnterior");


        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.frame_projecao,
                container, false);

        handler=new Handler();

        NumberFormat real = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        tv = layout.findViewById(R.id.tv);
        meta = layout.findViewById(R.id.frm_meta);
        meta.setText(real.format(metaConsultor));
        fatAnterior = layout.findViewById(R.id.frm_faturamento);
        fatAnterior.setText(real.format(vendaAnterior));
        faturado = layout.findViewById(R.id.frm_projetado);
        faturado.setText(real.format(faturamentoConsultor));
        margem = layout.findViewById(R.id.frm_margem);
        margem.setText(percentualMargem+"%");
        if(Double.parseDouble(percentualMargem)<30){
            margem.setTextColor(Color.parseColor("#B91A1F"));
        }

        if(Double.parseDouble(percentualMargem)>30 && Double.parseDouble(percentualMargem)<32  ){
            margem.setTextColor(Color.parseColor("#DC962D"));
        }

        if(Double.parseDouble(percentualMargem)>32){
            margem.setTextColor(Color.parseColor("#4F8651"));
        }




        pStatus=0;
        Resources res = getResources();
        if(Double.parseDouble(percentualMeta)>100){
            drawable = res.getDrawable(R.drawable.linearbatida);
        }else
        {
            drawable = res.getDrawable(R.drawable.linear);
        }

        final ProgressBar mProgress =  layout.findViewById(R.id.circularProgressbar);
        mProgress.setProgress(0);   // Main Progress
        mProgress.setSecondaryProgress(100); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);

        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (pStatus <= (Double.parseDouble(percentualMeta)-1)) {
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
