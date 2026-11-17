package br.com.acalfortaleza.fikonmais.helper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.acalfortaleza.fikonmais.models.Sugestoes;
import br.com.acalfortaleza.fikonmais.service.ApiGemco;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SugestoesHelper {



    private Retrofit retrofit;
    private ArrayList<String> listaMotivo;
    private OkHttpClient client;


    public SugestoesHelper(){

        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(ApiGemco.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }


    public ArrayList<String> GetMotivos(String tipo ) throws IOException, XmlPullParserException, ParseException {

        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<ArrayList<String>> request= service.GetMotivos( tipo,"Bearer " + service.token);

        listaMotivo = request.execute().body();

        return listaMotivo;

    }



    public String SetSugestao(Sugestoes sugestao) throws IOException, XmlPullParserException, ParseException {


        final ApiGemco service = retrofit.create(ApiGemco.class);

        final Call<String> post = service.saveSugestao( sugestao, "Bearer " + service.token);

        return post.execute().body();

    }

}
