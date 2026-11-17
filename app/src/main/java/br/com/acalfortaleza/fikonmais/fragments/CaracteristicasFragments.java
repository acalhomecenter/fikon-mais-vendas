package br.com.acalfortaleza.fikonmais.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import br.com.acalfortaleza.fikonmais.R;

public class CaracteristicasFragments extends Fragment {

    public CaracteristicasFragments(){

    }

    private String caracteristicas;
    private View layout;
    private WebView Wbv;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       if(getArguments()!= null){

           caracteristicas = getArguments().getString("caracteristicas");

       }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


            layout = inflater.inflate(R.layout.fragment_caracteristicas,
                    container, false);


            Wbv = layout.findViewById(R.id.WvCaracteristicas);
            Wbv.getSettings().setJavaScriptEnabled(true);
            Wbv.getSettings().setUseWideViewPort(true);
           // Wbv.getSettings().setLoadWithOverviewMode(true);
            Wbv.getSettings().setSupportZoom(true);
            Wbv.getSettings().setDisplayZoomControls(true);
            Wbv.getSettings().setBuiltInZoomControls(true);
            Wbv.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

            if(caracteristicas==null){

            }else{
                Wbv.loadData(caracteristicas, "text/html",null);
            }


            return layout;

        }




    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
           }



}
