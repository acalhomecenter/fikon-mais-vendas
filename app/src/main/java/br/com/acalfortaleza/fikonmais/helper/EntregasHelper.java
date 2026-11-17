package br.com.acalfortaleza.fikonmais.helper;

import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;


import br.com.acalfortaleza.fikonmais.models.Entregas;

import br.com.acalfortaleza.fikonmais.models.ProdutoGemco;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EntregasHelper {

    private Retrofit retrofit;
    private List<Entregas> listaEntragasDia;
    private List<Entregas> listaEntragasMes;
    private OkHttpClient client;

    public  EntregasHelper(){

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public List<Entregas> ConsultarEntregasDia(int codigo) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<Entregas>> requestEntregas = service.GetEntregasDia( codigo, "Bearer " + service.token);

        listaEntragasDia = requestEntregas.execute().body();


        return listaEntragasDia;

    }



    public List<Entregas> ConsultarEntregasMes(int codigo) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<Entregas>> requestEntregas = service.GetEntregasMes( codigo, "Bearer " + service.token);

        listaEntragasMes = requestEntregas.execute().body();


        return listaEntragasMes;

    }



    public List<Entregas> ConsultarEntregasCgccpf(Double cgccpf) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);



        final Call<List<Entregas>> requestEntregas = service.GetEntregasCgccpf( cgccpf, "Bearer " + service.token);

        listaEntragasMes = requestEntregas.execute().body();


        return listaEntragasMes;

    }



    public List<Entregas> ConsultarEntregasPedido(int pedido) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);


        final Call<List<Entregas>> requestEntregas = service.GetEntregasPedido( pedido, "Bearer " + service.token);

        listaEntragasMes = requestEntregas.execute().body();


        return listaEntragasMes;

    }


}
